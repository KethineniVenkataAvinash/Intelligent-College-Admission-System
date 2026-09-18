package com.college.admission.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "application_number", nullable = false)
    private String applicationNumber;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "total_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalFee;

    @Column(name = "scholarship_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal scholarshipAmount;

    @Column(name = "net_payable_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal netPayableAmount;

    @Column(name = "amount_paid", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "remaining_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "payment_mode", nullable = false)
    private String paymentMode;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;


    /**
     * Required by JPA.
     */
    protected Payment() {
    }


    /**
     * Constructor used by the application.
     */
    public Payment(
            String transactionId,
            String applicationNumber,
            String studentName,
            BigDecimal totalFee,
            BigDecimal scholarshipAmount,
            BigDecimal netPayableAmount,
            BigDecimal amountPaid,
            String paymentMode
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

        if (totalFee == null
                || totalFee.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Total fee cannot be negative."
            );
        }

        if (scholarshipAmount == null
                || scholarshipAmount.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Scholarship amount cannot be negative."
            );
        }

        if (scholarshipAmount.compareTo(totalFee) > 0) {

            throw new IllegalArgumentException(
                    "Scholarship amount cannot exceed total fee."
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

        if (amountPaid.compareTo(netPayableAmount) > 0) {

            throw new IllegalArgumentException(
                    "Amount paid cannot exceed the net payable amount."
            );
        }

        if (paymentMode == null
                || paymentMode.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment mode cannot be empty."
            );
        }


        this.transactionId = transactionId;

        this.applicationNumber =
                applicationNumber;

        this.studentName =
                studentName;

        this.totalFee =
                totalFee;

        this.scholarshipAmount =
                scholarshipAmount;

        this.netPayableAmount =
                netPayableAmount;

        this.amountPaid =
                amountPaid;

        this.remainingAmount =
                netPayableAmount.subtract(amountPaid);

        this.paymentMode =
                paymentMode;

        this.paymentDate =
                LocalDateTime.now();

        updatePaymentStatus();
    }


    /**
     * Updates payment status based on
     * payable amount and amount paid.
     *
     * Rules:
     *
     * Net Payable = 0
     *      -> PAID
     *
     * Amount Paid = 0
     *      -> UNPAID
     *
     * Amount Paid < Net Payable
     *      -> PARTIALLY_PAID
     *
     * Amount Paid >= Net Payable
     *      -> PAID
     */
    private void updatePaymentStatus() {

        if (netPayableAmount.compareTo(BigDecimal.ZERO) <= 0) {

            paymentStatus = "PAID";

        } else if (amountPaid.compareTo(BigDecimal.ZERO) <= 0) {

            paymentStatus = "UNPAID";

        } else if (amountPaid.compareTo(netPayableAmount) < 0) {

            paymentStatus = "PARTIALLY_PAID";

        } else {

            paymentStatus = "PAID";
        }
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


    public BigDecimal getTotalFee() {

        return totalFee;
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


    public BigDecimal getRemainingAmount() {

        return remainingAmount;
    }


    public String getPaymentMode() {

        return paymentMode;
    }


    public LocalDateTime getPaymentDate() {

        return paymentDate;
    }


    public String getPaymentStatus() {

        return paymentStatus;
    }


    public void setPaymentMode(
            String paymentMode
    ) {

        if (paymentMode == null
                || paymentMode.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment mode cannot be empty."
            );
        }

        this.paymentMode =
                paymentMode;
    }


    /**
     * Makes a payment against the
     * remaining payable amount.
     */
    public void makePayment(
            BigDecimal amount
    ) {

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero."
            );
        }

        if (amount.compareTo(remainingAmount) > 0) {

            throw new IllegalArgumentException(
                    "Payment amount cannot exceed the remaining amount."
            );
        }

        amountPaid = amountPaid.add(amount);

        remainingAmount =
                netPayableAmount.subtract(amountPaid);

        updatePaymentStatus();

        paymentDate =
                LocalDateTime.now();
    }


    public boolean isFullyPaid() {

        return "PAID".equals(paymentStatus);
    }


    /**
     * Displays payment information.
     */
    public void displayPaymentDetails() {

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

        System.out.println(
                "Transaction ID     : "
                        + transactionId
        );

        System.out.println(
                "Student Name       : "
                        + studentName
        );

        System.out.println(
                "Application Number : "
                        + applicationNumber
        );

        System.out.printf(
                "Total Fee          : ₹%.2f%n",
                totalFee
        );

        System.out.printf(
                "Scholarship Amount : ₹%.2f%n",
                scholarshipAmount
        );

        System.out.printf(
                "Net Payable        : ₹%.2f%n",
                netPayableAmount
        );

        System.out.printf(
                "Amount Paid        : ₹%.2f%n",
                amountPaid
        );

        System.out.printf(
                "Remaining Amount   : ₹%.2f%n",
                remainingAmount
        );

        System.out.println(
                "Payment Mode       : "
                        + paymentMode
        );

        System.out.println(
                "Payment Date       : "
                        + paymentDate
        );

        System.out.println(
                "Payment Status     : "
                        + paymentStatus
        );

        System.out.println(
                "================================================================"
        );
    }


    @Override
    public String toString() {

        return "Payment{" +

                "transactionId='" +
                transactionId + '\'' +

                ", applicationNumber='" +
                applicationNumber + '\'' +

                ", studentName='" +
                studentName + '\'' +

                ", totalFee=" +
                totalFee +

                ", scholarshipAmount=" +
                scholarshipAmount +

                ", netPayableAmount=" +
                netPayableAmount +

                ", amountPaid=" +
                amountPaid +

                ", remainingAmount=" +
                remainingAmount +

                ", paymentMode='" +
                paymentMode + '\'' +

                ", paymentDate=" +
                paymentDate +

                ", paymentStatus='" +
                paymentStatus + '\'' +

                '}';
    }
}