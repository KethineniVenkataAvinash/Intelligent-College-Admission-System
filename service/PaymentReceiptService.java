package com.college.admission.service;

import java.math.BigDecimal;

import com.college.admission.jpa.PaymentReceiptJpaRepository;
import com.college.admission.model.Course;
import com.college.admission.model.Payment;
import com.college.admission.model.PaymentReceipt;
import com.college.admission.model.SeatAllocation;
import com.college.admission.model.Student;
import com.college.admission.security.ServiceAuthorization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PaymentReceiptService {

    private static final String RECEIPT_DIRECTORY =
            "payment_receipts";

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm:ss"
            );

    private final PaymentReceiptJpaRepository receiptRepository;

    public PaymentReceiptService() {

        receiptRepository =
                new PaymentReceiptJpaRepository();

        createReceiptDirectory();
    }

    // =========================================================
    // CREATE RECEIPT DIRECTORY
    // =========================================================

    private void createReceiptDirectory() {

        File directory =
                new File(RECEIPT_DIRECTORY);

        if (!directory.exists()) {

            if (!directory.mkdirs()) {

                throw new IllegalStateException(
                        "Unable to create payment receipt directory."
                );
            }
        }
    }

    // =========================================================
    // CREATE RECEIPT
    // =========================================================

    /**
     * Creates a payment receipt from the current
     * persisted payment information.
     *
     * One receipt is created for one transaction.
     */
    public PaymentReceipt createReceipt(
            Student student,
            SeatAllocation seatAllocation,
            Course course,
            Payment payment
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student cannot be null."
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

        if (payment == null) {
            throw new IllegalStateException(
                    "Payment record is required."
            );
        }

        String applicationNumber =
                student.getApplicationNumber();

        // -----------------------------------------------------
        // Validate allocation
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
                    "Course does not match "
                            + "the allocated course."
            );
        }

        // -----------------------------------------------------
        // Validate payment
        // -----------------------------------------------------

        if (!applicationNumber.equals(
                payment.getApplicationNumber())) {

            throw new IllegalArgumentException(
                    "Payment does not belong "
                            + "to the given student."
            );
        }

        // -----------------------------------------------------
        // Check existing receipt in database
        // -----------------------------------------------------

        PaymentReceipt existingReceipt =
                receiptRepository.findByTransactionId(
                        payment.getTransactionId()
                );

        if (existingReceipt != null) {

            return existingReceipt;
        }

        // -----------------------------------------------------
        // Create receipt
        // -----------------------------------------------------

        PaymentReceipt receipt =
                new PaymentReceipt(
                        payment.getTransactionId(),
                        payment.getApplicationNumber(),
                        payment.getStudentName(),

                        course.getCourseCode(),
                        course.getCourseName(),

                        payment.getTotalFee(),
                        payment.getScholarshipAmount(),
                        payment.getNetPayableAmount(),

                        payment.getAmountPaid(),
                        payment.getPaymentMode(),
                        payment.getPaymentStatus()
                );

        // -----------------------------------------------------
        // Save receipt in MySQL
        // -----------------------------------------------------

        return receiptRepository.save(
                receipt
        );
    }

    // =========================================================
    // GENERATE RECEIPT FILE
    // =========================================================

    /**
     * Generates the payment receipt as a text file
     * using BufferedWriter and FileWriter.
     */
    public String generateReceiptFile(
            PaymentReceipt receipt
    ) {

        if (receipt == null) {

            throw new IllegalArgumentException(
                    "Payment receipt cannot be null."
            );
        }

        String fileName =
                RECEIPT_DIRECTORY
                        + File.separator
                        + receipt.getTransactionId()
                        + "_Payment_Receipt.txt";

        try (
                BufferedWriter writer =
                        new BufferedWriter(
                                new FileWriter(fileName)
                        )
        ) {

            writer.write(
                    "============================================================"
            );
            writer.newLine();

            writer.write(
                    "                 COLLEGE ADMISSION SYSTEM"
            );
            writer.newLine();

            writer.write(
                    "                     PAYMENT RECEIPT"
            );
            writer.newLine();

            writer.write(
                    "============================================================"
            );
            writer.newLine();

            writer.newLine();

            // -------------------------------------------------
            // TRANSACTION DETAILS
            // -------------------------------------------------

            writer.write(
                    "TRANSACTION DETAILS"
            );
            writer.newLine();

            writer.write(
                    "------------------------------------------------------------"
            );
            writer.newLine();

            writer.write(
                    "Receipt ID         : "
                            + receipt.getReceiptId()
            );
            writer.newLine();

            writer.write(
                    "Transaction ID     : "
                            + receipt.getTransactionId()
            );
            writer.newLine();

            writer.write(
                    "Receipt Date       : "
                            + receipt.getReceiptDate()
                                    .format(dateTimeFormatter)
            );
            writer.newLine();

            writer.write(
                    "Payment Mode       : "
                            + receipt.getPaymentMode()
            );
            writer.newLine();

            writer.write(
                    "Payment Status     : "
                            + receipt.getPaymentStatus()
            );
            writer.newLine();

            writer.newLine();

            // -------------------------------------------------
            // STUDENT DETAILS
            // -------------------------------------------------

            writer.write(
                    "STUDENT DETAILS"
            );
            writer.newLine();

            writer.write(
                    "------------------------------------------------------------"
            );
            writer.newLine();

            writer.write(
                    "Student Name       : "
                            + receipt.getStudentName()
            );
            writer.newLine();

            writer.write(
                    "Application Number : "
                            + receipt.getApplicationNumber()
            );
            writer.newLine();

            writer.newLine();

            // -------------------------------------------------
            // COURSE DETAILS
            // -------------------------------------------------

            writer.write(
                    "COURSE DETAILS"
            );
            writer.newLine();

            writer.write(
                    "------------------------------------------------------------"
            );
            writer.newLine();

            writer.write(
                    "Course Code        : "
                            + receipt.getCourseCode()
            );
            writer.newLine();

            writer.write(
                    "Course Name        : "
                            + receipt.getCourseName()
            );
            writer.newLine();

            writer.newLine();

            // -------------------------------------------------
            // FEE DETAILS
            // -------------------------------------------------

            writer.write(
                    "FEE DETAILS"
            );
            writer.newLine();

            writer.write(
                    "------------------------------------------------------------"
            );
            writer.newLine();

            writer.write(
                    String.format(
                            "Course Fee         : ₹%.2f",
                            receipt.getCourseFee()
                    )
            );
            writer.newLine();

            writer.write(
                    String.format(
                            "Scholarship Amount : ₹%.2f",
                            receipt.getScholarshipAmount()
                    )
            );
            writer.newLine();

            writer.write(
                    String.format(
                            "Net Payable        : ₹%.2f",
                            receipt.getNetPayableAmount()
                    )
            );
            writer.newLine();

            writer.write(
                    String.format(
                            "Amount Paid        : ₹%.2f",
                            receipt.getAmountPaid()
                    )
            );
            writer.newLine();

            BigDecimal remainingAmount =
                    receipt.getNetPayableAmount()
                            .subtract(receipt.getAmountPaid())
                            .max(BigDecimal.ZERO);

            writer.write(
                    String.format(
                            "Remaining Amount   : ₹%.2f",
                            remainingAmount
                    )
            );
            writer.newLine();

            writer.newLine();

            writer.write(
                    "============================================================"
            );
            writer.newLine();

            writer.write(
                    "                    THANK YOU"
            );
            writer.newLine();

            writer.write(
                    "============================================================"
            );
            writer.newLine();

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to generate payment receipt file.",
                    e
            );
        }

        return fileName;
    }

    // =========================================================
    // READ RECEIPT FILE
    // =========================================================

    /**
     * Reads the generated receipt using
     * BufferedReader and FileReader.
     */
    public void readReceiptFile(
            String fileName
    ) {

        if (fileName == null
                || fileName.isBlank()) {

            throw new IllegalArgumentException(
                    "Receipt file name cannot be empty."
            );
        }

        File file =
                new File(fileName);

        if (!file.exists()) {

            throw new IllegalArgumentException(
                    "Payment receipt file does not exist: "
                            + fileName
            );
        }

        System.out.println();

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                 PAYMENT RECEIPT FILE"
        );

        System.out.println(
                "============================================================"
        );

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(file)
                        )
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                System.out.println(line);
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to read payment receipt file.",
                    e
            );
        }

        System.out.println(
                "============================================================"
        );
    }

    // =========================================================
    // DISPLAY RECEIPT
    // =========================================================

    /**
     * Displays the receipt directly in the console.
     */
    public void displayReceipt(
            PaymentReceipt receipt
    ) {

        if (receipt == null) {

            System.out.println(
                    "Payment receipt is not available."
            );

            return;
        }

        System.out.println();

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                 COLLEGE ADMISSION SYSTEM"
        );

        System.out.println(
                "                     PAYMENT RECEIPT"
        );

        System.out.println(
                "============================================================"
        );

        System.out.println();

        System.out.println(
                "Receipt ID         : "
                        + receipt.getReceiptId()
        );

        System.out.println(
                "Transaction ID     : "
                        + receipt.getTransactionId()
        );

        System.out.println(
                "Receipt Date       : "
                        + receipt.getReceiptDate()
                                .format(dateTimeFormatter)
        );

        System.out.println(
                "Payment Mode       : "
                        + receipt.getPaymentMode()
        );

        System.out.println(
                "Payment Status     : "
                        + receipt.getPaymentStatus()
        );

        System.out.println();

        System.out.println(
                "Student Name       : "
                        + receipt.getStudentName()
        );

        System.out.println(
                "Application Number : "
                        + receipt.getApplicationNumber()
        );

        System.out.println();

        System.out.println(
                "Course Code        : "
                        + receipt.getCourseCode()
        );

        System.out.println(
                "Course Name        : "
                        + receipt.getCourseName()
        );

        System.out.println();

        System.out.printf(
                "Course Fee         : ₹%.2f%n",
                receipt.getCourseFee()
        );

        System.out.printf(
                "Scholarship Amount : ₹%.2f%n",
                receipt.getScholarshipAmount()
        );

        System.out.printf(
                "Net Payable        : ₹%.2f%n",
                receipt.getNetPayableAmount()
        );

        System.out.printf(
                "Amount Paid        : ₹%.2f%n",
                receipt.getAmountPaid()
        );

        BigDecimal remainingAmount =
                receipt.getNetPayableAmount()
                        .subtract(receipt.getAmountPaid())
                        .max(BigDecimal.ZERO);

        System.out.printf(
                "Remaining Amount   : ₹%.2f%n",
                remainingAmount
        );

        System.out.println();

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                    THANK YOU"
        );

        System.out.println(
                "============================================================"
        );
    }

    // =========================================================
    // GET RECEIPT
    // =========================================================

    /**
     * Gets a receipt from MySQL using transaction ID.
     */
    public PaymentReceipt getReceipt(
            String transactionId
    ) {

        ServiceAuthorization.requireAuthenticated();

        if (transactionId == null
                || transactionId.isBlank()) {

            return null;
        }

        PaymentReceipt receipt =
                receiptRepository.findByTransactionId(
                transactionId.trim()
        );

        if (receipt != null) {
            ServiceAuthorization.requireApplicationOwnership(
                    receipt.getApplicationNumber()
            );
        }

        return receipt;
    }

    // =========================================================
    // GET RECEIPT BY APPLICATION NUMBER
    // =========================================================

    /**
     * Gets the latest receipt for an application.
     */
    public PaymentReceipt getReceiptByApplicationNumber(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }

        return receiptRepository
                .findByApplicationNumber(
                        applicationNumber.trim()
                );
    }

    // =========================================================
    // CHECK RECEIPT
    // =========================================================

    public boolean hasReceipt(
            String transactionId
    ) {

        ServiceAuthorization.requireAuthenticated();

        if (transactionId == null
                || transactionId.isBlank()) {

            return false;
        }

        PaymentReceipt receipt =
                receiptRepository.findByTransactionId(
                        transactionId.trim()
                );

        if (receipt == null) {
            return false;
        }

        ServiceAuthorization.requireApplicationOwnership(
                receipt.getApplicationNumber()
        );
        return true;
    }

    // =========================================================
    // DELETE RECEIPT
    // =========================================================

    public void deleteReceipt(
            String transactionId
    ) {

        ServiceAuthorization.requireAdmin();

        if (transactionId == null
                || transactionId.isBlank()) {

            throw new IllegalArgumentException(
                    "Transaction ID cannot be empty."
            );
        }

        receiptRepository.deleteByTransactionId(
                transactionId.trim()
        );
    }

    // =========================================================
    // RECEIPT COUNT
    // =========================================================

    public long getReceiptCount() {

        ServiceAuthorization.requireAdmin();

        return receiptRepository.count();
    }

    // =========================================================
    // REPOSITORY ACCESS
    // =========================================================

    public PaymentReceiptJpaRepository
    getReceiptRepository() {

        return receiptRepository;
    }
}