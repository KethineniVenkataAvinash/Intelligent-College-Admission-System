package com.college.admission.service;

import com.college.admission.jpa.PaymentJpaRepository;
import com.college.admission.model.Course;
import com.college.admission.model.Payment;
import com.college.admission.model.Scholarship;
import com.college.admission.model.SeatAllocation;
import com.college.admission.model.Student;
import com.college.admission.security.ServiceAuthorization;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

/**
 * Handles student fee payment operations.
 *
 * Payment records are persisted in MySQL using JPA.
 *
 * Flow:
 *
 * Student
 *    ↓
 * Rank
 *    ↓
 * Seat Allocation
 *    ↓
 * Scholarship
 *    ↓
 * Payment
 *    ↓
 * MySQL
 */
public class PaymentService {

    private final PaymentJpaRepository paymentRepository;

    public PaymentService() {

        this.paymentRepository =
                new PaymentJpaRepository();
    }

    // =========================================================
    // TRANSACTION ID GENERATION
    // =========================================================

    private String generateTransactionId() {

        return "TXN2026"
                + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .toUpperCase();
    }

    // =========================================================
    // CREATE / GET PAYMENT
    // =========================================================

    /**
     * Creates the initial payment record.
     *
     * If a payment already exists, the existing record is returned.
     *
     * Zero-payable scholarships are automatically marked PAID.
     */
    public Payment createPayment(
            Student student,
            SeatAllocation seatAllocation,
            Course course,
            Scholarship scholarship
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (student.getRank() == null) {

            throw new IllegalStateException(
                    "Student rank has not been generated yet."
            );
        }

        if (seatAllocation == null) {

            throw new IllegalStateException(
                    "Admission seat allocation is required."
            );
        }

        if (course == null) {

            throw new IllegalArgumentException(
                    "Course cannot be null."
            );
        }

        if (scholarship == null) {

            throw new IllegalStateException(
                    "Scholarship details must be calculated "
                            + "before payment."
            );
        }

        String applicationNumber =
                student.getApplicationNumber();

        // -----------------------------------------------------
        // Validate application number
        // -----------------------------------------------------

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalStateException(
                    "Student application number is missing."
            );
        }

        // -----------------------------------------------------
        // Validate allocation belongs to student
        // -----------------------------------------------------

        if (!applicationNumber.equals(
                seatAllocation.getApplicationNumber())) {

            throw new IllegalArgumentException(
                    "Seat allocation does not belong "
                            + "to the given student."
            );
        }

        // -----------------------------------------------------
        // Validate course
        // -----------------------------------------------------

        if (!course.getCourseCode().equals(
                seatAllocation.getCourseCode())) {

            throw new IllegalArgumentException(
                    "Course does not match the allocated course."
            );
        }

        // -----------------------------------------------------
        // Validate scholarship belongs to student
        // -----------------------------------------------------

        if (!applicationNumber.equals(
                scholarship.getApplicationNumber())) {

            throw new IllegalArgumentException(
                    "Scholarship does not belong "
                            + "to the given student."
            );
        }

        // -----------------------------------------------------
        // Validate scholarship course
        // -----------------------------------------------------

        if (!course.getCourseCode().equals(
                scholarship.getCourseCode())) {

            throw new IllegalArgumentException(
                    "Scholarship course does not match "
                            + "the allocated course."
            );
        }

        // =====================================================
        // CHECK EXISTING PAYMENT IN DATABASE
        // =====================================================

        Payment existingPayment =
                paymentRepository.findByApplicationNumber(
                        applicationNumber
                );

        if (existingPayment != null) {

            /*
             * Existing payment records may have been created
             * before the zero-payable PAID rule was introduced.
             *
             * Synchronize such records here.
             */
            if (existingPayment.getNetPayableAmount().compareTo(BigDecimal.ZERO) <= 0
                    && existingPayment.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0
                    && !"PAID".equals(
                            existingPayment.getPaymentStatus())) {

                paymentRepository.updatePaymentStatus(
                        existingPayment.getTransactionId(),
                        "PAID"
                );

                /*
                 * Reload the entity so the returned object
                 * contains the updated database value.
                 */
                existingPayment =
                        paymentRepository.findByApplicationNumber(
                                applicationNumber
                        );
            }

            return existingPayment;
        }

        // =====================================================
        // GENERATE TRANSACTION ID
        // =====================================================

        String transactionId =
                generateTransactionId();

        // =====================================================
        // CREATE PAYMENT
        // =====================================================

        Payment payment =
                new Payment(
                        transactionId,
                        applicationNumber,
                        student.getName(),
                        course.getAnnualFee(),
                        scholarship.getScholarshipAmount(),
                        scholarship.getNetPayableAmount(),
                        BigDecimal.ZERO,
                        "NOT_SELECTED"
                );

        // =====================================================
        // PERSIST PAYMENT
        // =====================================================

        return paymentRepository.save(
                payment
        );
    }

    // =========================================================
    // MAKE PAYMENT
    // =========================================================

    /**
     * Makes a payment against an existing payment record.
     */
    public void makePayment(
            Student student,
            BigDecimal amount,
            String paymentMode
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero."
            );
        }

        if (paymentMode == null
                || paymentMode.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment mode is required."
            );
        }

        String applicationNumber =
                student.getApplicationNumber();

        // -----------------------------------------------------
        // GET PAYMENT FROM DATABASE
        // -----------------------------------------------------

        Payment payment =
                paymentRepository.findByApplicationNumber(
                        applicationNumber
                );

        if (payment == null) {

            throw new IllegalStateException(
                    "Payment record has not been created yet."
            );
        }

        // -----------------------------------------------------
        // PREVENT PAYMENT FOR ZERO-PAYABLE ACCOUNT
        // -----------------------------------------------------

        if (payment.getNetPayableAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalStateException(
                    "No payment is required. "
                            + "The net payable amount is ₹0.00."
            );
        }

        // -----------------------------------------------------
        // CALCULATE REMAINING AMOUNT
        // -----------------------------------------------------

        BigDecimal remainingAmount =
                payment.getNetPayableAmount()
                        .subtract(payment.getAmountPaid());

        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalStateException(
                    "Payment is already fully paid."
            );
        }

        // -----------------------------------------------------
        // PREVENT OVERPAYMENT
        // -----------------------------------------------------

        if (amount.compareTo(remainingAmount) > 0) {

            throw new IllegalArgumentException(
                    String.format(
                            "Payment exceeds remaining amount. "
                                    + "Remaining amount: ₹%.2f",
                            remainingAmount
                    )
            );
        }

        // -----------------------------------------------------
        // UPDATE PAYMENT
        // -----------------------------------------------------

        payment.makePayment(amount);

        payment.setPaymentMode(
                paymentMode.trim().toUpperCase()
        );

        // -----------------------------------------------------
        // PERSIST UPDATED PAYMENT
        // -----------------------------------------------------

        paymentRepository.update(
                payment
        );
    }

    // =========================================================
    // GET PAYMENT
    // =========================================================

    public Payment getPayment(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }

        return paymentRepository
                .findByApplicationNumber(
                        applicationNumber.trim()
                );
    }

    // =========================================================
    // CHECK PAYMENT
    // =========================================================

    public boolean hasPayment(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return false;
        }

        return paymentRepository
                .existsByApplicationNumber(
                        applicationNumber.trim()
                );
    }

    // =========================================================
    // DISPLAY STUDENT PAYMENT
    // =========================================================

    public void displayStudentPayment(
            Student student
    ) {

        ServiceAuthorization.requireAuthenticated();

        if (student == null) {

            System.out.println(
                    "Student not found."
            );

            return;
        }

        Payment payment =
                getPayment(
                        student.getApplicationNumber()
                );

        System.out.println();

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "                     PAYMENT DETAILS"
        );

        System.out.println(
                "================================================================"
        );

        if (payment == null) {

            System.out.println(
                    "No payment record found."
            );

            System.out.println(
                    "================================================================"
            );

            return;
        }

        /*
         * Synchronize zero-payable payment records
         * that may contain an old UNPAID status.
         */
        if (payment.getNetPayableAmount().compareTo(BigDecimal.ZERO) <= 0
                && payment.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0
                && !"PAID".equals(
                        payment.getPaymentStatus())) {

            paymentRepository.updatePaymentStatus(
                    payment.getTransactionId(),
                    "PAID"
            );

            payment =
                    paymentRepository.findByApplicationNumber(
                            student.getApplicationNumber()
                    );
        }

        payment.displayPaymentDetails();

        System.out.println(
                "================================================================"
        );
    }

    // =========================================================
    // DISPLAY ALL PAYMENTS
    // =========================================================

    public void displayAllPayments() {

        ServiceAuthorization.requireAdmin();

        List<Payment> payments =
                paymentRepository.findAll();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                         FINANCE REPORT"
        );

        System.out.println(
                "=========================================================================="
        );

        if (payments.isEmpty()) {

            System.out.println(
                    "No payment records found."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        System.out.printf(
                "%-20s %-22s %-12s %-15s %-15s%n",
                "Application No.",
                "Student",
                "Net Payable",
                "Paid",
                "Status"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (Payment payment : payments) {

            System.out.printf(
                    "%-20s %-22s ₹%-11.2f ₹%-13.2f %-15s%n",
                    payment.getApplicationNumber(),
                    payment.getStudentName(),
                    payment.getNetPayableAmount(),
                    payment.getAmountPaid(),
                    payment.getPaymentStatus()
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // =========================================================
    // GET ALL PAYMENTS
    // =========================================================

    public List<Payment> getAllPayments() {

        ServiceAuthorization.requireAdmin();

        return paymentRepository.findAll();
    }

    // =========================================================
    // PAYMENT COUNT
    // =========================================================

    public long getPaymentCount() {

        return paymentRepository.count();
    }

    // =========================================================
    // PAID PAYMENTS
    // =========================================================

    public List<Payment> getPaidPayments() {

        ServiceAuthorization.requireAdmin();

        return paymentRepository.findByStatus(
                "PAID"
        );
    }

    // =========================================================
    // PENDING PAYMENTS
    // =========================================================

    public List<Payment> getPendingPayments() {

        ServiceAuthorization.requireAdmin();

        return paymentRepository.findByStatuses(
                List.of(
                        "UNPAID",
                        "PARTIALLY_PAID"
                )
        );
    }

    // =========================================================
    // TOTAL COLLECTION
    // =========================================================

    public BigDecimal getTotalAmountCollected() {

        ServiceAuthorization.requireAdmin();

        return paymentRepository
                .getTotalAmountPaid();
    }

    // =========================================================
    // TOTAL OUTSTANDING
    // =========================================================

    public BigDecimal getTotalOutstandingAmount() {

        ServiceAuthorization.requireAdmin();

        return paymentRepository
                .getTotalOutstandingAmount();
    }

    // =========================================================
    // DELETE PAYMENT
    // =========================================================

    public void deletePayment(
            String applicationNumber
    ) {

        ServiceAuthorization.requireAdmin();

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        paymentRepository
                .deleteByApplicationNumber(
                        applicationNumber.trim()
                );
    }

    // =========================================================
    // REPOSITORY ACCESS
    // =========================================================

    public PaymentJpaRepository
    getPaymentRepository() {

        ServiceAuthorization.requireAdmin();

        return paymentRepository;
    }
}