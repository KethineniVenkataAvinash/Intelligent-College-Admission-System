package com.college.admission.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(
        name = "payment_receipts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_receipt_transaction",
                        columnNames = "transaction_id"
                )
        }
)
public class PaymentReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    @Column(
            name = "transaction_id",
            nullable = false,
            length = 30
    )
    private String transactionId;

    @Column(
            name = "application_number",
            nullable = false,
            length = 30
    )
    private String applicationNumber;

    @Column(
            name = "student_name",
            nullable = false,
            length = 150
    )
    private String studentName;

    @Column(
            name = "course_code",
            nullable = false,
            length = 20
    )
    private String courseCode;

    @Column(
            name = "course_name",
            nullable = false,
            length = 150
    )
    private String courseName;

    @Column(
            name = "course_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal courseFee;

    @Column(
            name = "scholarship_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal scholarshipAmount;

    @Column(
            name = "net_payable_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal netPayableAmount;

    @Column(
            name = "amount_paid",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amountPaid;

    @Column(
            name = "payment_mode",
            nullable = false,
            length = 30
    )
    private String paymentMode;

    @Column(
            name = "payment_status",
            nullable = false,
            length = 30
    )
    private String paymentStatus;

    @Column(
            name = "receipt_date",
            nullable = false
    )
    private LocalDateTime receiptDate;

    protected PaymentReceipt() {
    }

    public PaymentReceipt(
            String transactionId,
            String applicationNumber,
            String studentName,
            String courseCode,
            String courseName,
            BigDecimal courseFee,
            BigDecimal scholarshipAmount,
            BigDecimal netPayableAmount,
            BigDecimal amountPaid,
            String paymentMode,
            String paymentStatus
    ) {

        if (transactionId == null
                || transactionId.isBlank()) {

            throw new IllegalArgumentException(
                    "Transaction ID cannot be empty."
            );
        }

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        if (studentName == null
                || studentName.isBlank()) {

            throw new IllegalArgumentException(
                    "Student name cannot be empty."
            );
        }

        if (courseCode == null
                || courseCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Course code cannot be empty."
            );
        }

        if (courseName == null
                || courseName.isBlank()) {

            throw new IllegalArgumentException(
                    "Course name cannot be empty."
            );
        }

        if (courseFee == null
                || courseFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Course fee cannot be negative."
            );
        }

        if (scholarshipAmount == null
                || scholarshipAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Scholarship amount cannot be negative."
            );
        }

        if (netPayableAmount == null
                || netPayableAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Net payable amount cannot be negative."
            );
        }

        if (amountPaid == null
                || amountPaid.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Amount paid cannot be negative."
            );
        }

        if (paymentMode == null
                || paymentMode.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment mode cannot be empty."
            );
        }

        if (paymentStatus == null
                || paymentStatus.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment status cannot be empty."
            );
        }

        this.transactionId = transactionId;
        this.applicationNumber = applicationNumber;
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseFee = courseFee;
        this.scholarshipAmount = scholarshipAmount;
        this.netPayableAmount = netPayableAmount;
        this.amountPaid = amountPaid;
        this.paymentMode = paymentMode;
        this.paymentStatus = paymentStatus;
        this.receiptDate = LocalDateTime.now();
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public Long getReceiptId() {
        return receiptId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public BigDecimal getCourseFee() {
        return courseFee;
    }

    public BigDecimal getScholarshipAmount() {
        return scholarshipAmount;
    }

    public BigDecimal getNetPayableAmount() {
        return netPayableAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getReceiptDate() {
        return receiptDate;
    }

    // =========================================================
    // DISPLAY
    // =========================================================

    public void displayReceipt() {

        System.out.println();
        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                    PAYMENT RECEIPT"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Receipt ID          : " + receiptId
        );

        System.out.println(
                "Transaction ID      : " + transactionId
        );

        System.out.println(
                "Application Number  : " + applicationNumber
        );

        System.out.println(
                "Student Name        : " + studentName
        );

        System.out.println(
                "Course Code         : " + courseCode
        );

        System.out.println(
                "Course Name         : " + courseName
        );

        System.out.printf(
                "Course Fee          : ₹%.2f%n",
                courseFee
        );

        System.out.printf(
                "Scholarship Amount  : ₹%.2f%n",
                scholarshipAmount
        );

        System.out.printf(
                "Net Payable         : ₹%.2f%n",
                netPayableAmount
        );

        System.out.printf(
                "Amount Paid         : ₹%.2f%n",
                amountPaid
        );

        System.out.println(
                "Payment Mode        : " + paymentMode
        );

        System.out.println(
                "Payment Status      : " + paymentStatus
        );

        System.out.println(
                "Receipt Date        : " + receiptDate
        );

        System.out.println(
                "=============================================================="
        );
    }

    @Override
    public String toString() {

        return "PaymentReceipt{" +
                "receiptId=" + receiptId +
                ", transactionId='" + transactionId + '\'' +
                ", applicationNumber='" + applicationNumber + '\'' +
                ", studentName='" + studentName + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseFee=" + courseFee +
                ", scholarshipAmount=" + scholarshipAmount +
                ", netPayableAmount=" + netPayableAmount +
                ", amountPaid=" + amountPaid +
                ", paymentMode='" + paymentMode + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", receiptDate=" + receiptDate +
                '}';
    }
}