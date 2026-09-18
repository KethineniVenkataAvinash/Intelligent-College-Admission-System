package com.college.admission.model;

import com.college.admission.enums.ExamStatus;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "exam_registrations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_exam_schedule",
                        columnNames = {
                                "application_number",
                                "schedule_id"
                        }
                )
        }
)
public class ExamRegistration {

    @Id
    @Column(
            name = "registration_id",
            length = 30,
            nullable = false
    )
    private String registrationId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "application_number",
            referencedColumnName = "application_number",
            nullable = false
    )
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "schedule_id",
            nullable = false
    )
    private ExamSchedule examSchedule;

    @Column(
            name = "registration_date",
            nullable = false
    )
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private ExamStatus status;

    /*
     * Required by JPA.
     */
    protected ExamRegistration() {
    }

    /*
     * Main constructor.
     */
    public ExamRegistration(
            String registrationId,
            Student student,
            ExamSchedule examSchedule
    ) {

        validateRegistrationId(registrationId);
        validateStudent(student);
        validateExamSchedule(examSchedule);

        this.registrationId = registrationId.trim().toUpperCase();
        this.student = student;
        this.examSchedule = examSchedule;
        this.registrationDate = LocalDateTime.now();
        this.status = ExamStatus.SCHEDULED;
    }

    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------

    private void validateRegistrationId(String registrationId) {

        if (registrationId == null
                || registrationId.isBlank()) {

            throw new IllegalArgumentException(
                    "Registration ID cannot be empty."
            );
        }

        if (registrationId.trim().length() > 30) {

            throw new IllegalArgumentException(
                    "Registration ID cannot exceed 30 characters."
            );
        }
    }

    private void validateStudent(Student student) {

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }
    }

    private void validateExamSchedule(
            ExamSchedule examSchedule
    ) {

        if (examSchedule == null) {

            throw new IllegalArgumentException(
                    "Exam schedule cannot be null."
            );
        }
    }

    // ---------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------

    public String getRegistrationId() {
        return registrationId;
    }

    public Student getStudent() {
        return student;
    }

    public ExamSchedule getExamSchedule() {
        return examSchedule;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public ExamStatus getStatus() {
        return status;
    }

    // ---------------------------------------------------------
    // CONVENIENCE GETTERS
    // ---------------------------------------------------------

    public String getApplicationNumber() {

        return student != null
                ? student.getApplicationNumber()
                : null;
    }

    public String getStudentName() {

        return student != null
                ? student.getName()
                : null;
    }

    public String getExamCode() {

        return examSchedule != null
                ? examSchedule.getExamCode()
                : null;
    }

    public String getExamName() {

        return examSchedule != null
                ? examSchedule.getExamName()
                : null;
    }

    public Long getScheduleId() {

        return examSchedule != null
                ? examSchedule.getScheduleId()
                : null;
    }

    // ---------------------------------------------------------
    // STATUS
    // ---------------------------------------------------------

    public void setStatus(ExamStatus status) {

        if (status == null) {

            throw new IllegalArgumentException(
                    "Exam status cannot be null."
            );
        }

        this.status = status;
    }

    // ---------------------------------------------------------
    // STATUS OPERATIONS
    // ---------------------------------------------------------

    public void markScheduled() {

        this.status = ExamStatus.SCHEDULED;
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

    public void displayRegistration() {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                   EXAM REGISTRATION"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Registration ID    : "
                        + registrationId
        );

        System.out.println(
                "Application Number : "
                        + getApplicationNumber()
        );

        System.out.println(
                "Student Name       : "
                        + getStudentName()
        );

        System.out.println(
                "Exam Code          : "
                        + getExamCode()
        );

        System.out.println(
                "Exam Name          : "
                        + getExamName()
        );

        System.out.println(
                "Schedule ID        : "
                        + getScheduleId()
        );

        System.out.println(
                "Exam Date          : "
                        + (
                        examSchedule != null
                                ? examSchedule.getExamDate()
                                : null
                )
        );

        System.out.println(
                "Session            : "
                        + (
                        examSchedule != null
                                ? examSchedule.getSession()
                                : null
                )
        );

        System.out.println(
                "Registration Date  : "
                        + registrationDate
        );

        System.out.println(
                "Status             : "
                        + status
        );

        System.out.println(
                "=============================================================="
        );
    }

    // ---------------------------------------------------------
    // TO STRING
    // ---------------------------------------------------------

    @Override
    public String toString() {

        return "ExamRegistration{" +
                "registrationId='" + registrationId + '\'' +
                ", applicationNumber='" +
                getApplicationNumber() + '\'' +
                ", studentName='" +
                getStudentName() + '\'' +
                ", examCode='" +
                getExamCode() + '\'' +
                ", examName='" +
                getExamName() + '\'' +
                ", scheduleId=" +
                getScheduleId() +
                ", registrationDate=" +
                registrationDate +
                ", status=" +
                status +
                '}';
    }
}