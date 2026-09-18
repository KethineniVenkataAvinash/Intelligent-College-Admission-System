package com.college.admission.model;

import com.college.admission.enums.ExamSession;
import com.college.admission.enums.ExamStatus;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "exam_allocations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_exam_allocation_application_exam",
                        columnNames = {
                                "application_number",
                                "exam_date",
                                "session"
                        }
                ),
                @UniqueConstraint(
                        name = "uk_exam_allocation_physical_seat",
                        columnNames = {
                                "centre_code",
                                "room_number",
                                "seat_number",
                                "exam_date",
                                "session"
                        }
                )
        }
)
public class ExamAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allocation_id")
    private Long allocationId;

    @Column(name = "application_number", nullable = false, length = 30)
    private String applicationNumber;

    @Column(name = "student_name", nullable = false, length = 150)
    private String studentName;

    @Column(name = "centre_code", nullable = false, length = 20)
    private String centreCode;

    @Column(name = "centre_name", nullable = false, length = 150)
    private String centreName;

    @Column(name = "room_number", nullable = false, length = 30)
    private String roomNumber;

    @Column(name = "seat_number", nullable = false, length = 30)
    private String seatNumber;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "session", nullable = false, length = 20)
    private ExamSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExamStatus status;

    /*
     * Required by JPA.
     */
    protected ExamAllocation() {
    }

    /*
     * Existing constructor preserved.
     * ExamService can continue using this constructor.
     */
    public ExamAllocation(
            String applicationNumber,
            String studentName,
            String centreCode,
            String centreName,
            String roomNumber,
            String seatNumber,
            LocalDate examDate,
            ExamSession session,
            ExamStatus status
    ) {

        if (applicationNumber == null || applicationNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Application number cannot be null or blank."
            );
        }

        if (studentName == null || studentName.isBlank()) {
            throw new IllegalArgumentException(
                    "Student name cannot be null or blank."
            );
        }

        if (centreCode == null || centreCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Centre code cannot be null or blank."
            );
        }

        if (centreName == null || centreName.isBlank()) {
            throw new IllegalArgumentException(
                    "Centre name cannot be null or blank."
            );
        }

        if (roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Room number cannot be null or blank."
            );
        }

        if (seatNumber == null || seatNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Seat number cannot be null or blank."
            );
        }

        if (examDate == null) {
            throw new IllegalArgumentException(
                    "Exam date cannot be null."
            );
        }

        if (session == null) {
            throw new IllegalArgumentException(
                    "Exam session cannot be null."
            );
        }

        if (status == null) {
            throw new IllegalArgumentException(
                    "Exam status cannot be null."
            );
        }

        this.applicationNumber = applicationNumber;
        this.studentName = studentName;
        this.centreCode = centreCode;
        this.centreName = centreName;
        this.roomNumber = roomNumber;
        this.seatNumber = seatNumber;
        this.examDate = examDate;
        this.session = session;
        this.status = status;
    }

    // ---------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------

    public Long getAllocationId() {
        return allocationId;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCentreCode() {
        return centreCode;
    }

    public String getCentreName() {
        return centreName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public ExamSession getSession() {
        return session;
    }

    public ExamStatus getStatus() {
        return status;
    }

    // ---------------------------------------------------------
    // STATUS MANAGEMENT
    // ---------------------------------------------------------

    public void setStatus(ExamStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Exam status cannot be null."
            );
        }

        this.status = status;
    }

    public void markAllocated() {
        this.status = ExamStatus.ALLOCATED;
    }

    public void markCompleted() {
        this.status = ExamStatus.COMPLETED;
    }

    public void cancel() {
        this.status = ExamStatus.CANCELLED;
    }

    // ---------------------------------------------------------
    // DISPLAY
    // ---------------------------------------------------------

    public void displayAllocation() {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("           EXAM SEAT ALLOCATION");
        System.out.println("==============================================");

        System.out.println("Allocation ID     : " + allocationId);
        System.out.println("Application No.   : " + applicationNumber);
        System.out.println("Student Name      : " + studentName);
        System.out.println("Centre Code       : " + centreCode);
        System.out.println("Centre Name       : " + centreName);
        System.out.println("Room Number       : " + roomNumber);
        System.out.println("Seat Number       : " + seatNumber);
        System.out.println("Exam Date         : " + examDate);
        System.out.println("Session           : " + session);
        System.out.println("Status            : " + status);

        System.out.println("==============================================");
    }

    // ---------------------------------------------------------
    // TOSTRING
    // ---------------------------------------------------------

    @Override
    public String toString() {

        return "ExamAllocation{" +
                "allocationId=" + allocationId +
                ", applicationNumber='" +
                applicationNumber + '\'' +
                ", studentName='" +
                studentName + '\'' +
                ", centreCode='" +
                centreCode + '\'' +
                ", centreName='" +
                centreName + '\'' +
                ", roomNumber='" +
                roomNumber + '\'' +
                ", seatNumber='" +
                seatNumber + '\'' +
                ", examDate=" +
                examDate +
                ", session=" +
                session +
                ", status=" +
                status +
                '}';
    }
}