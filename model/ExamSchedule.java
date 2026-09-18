package com.college.admission.model;

import com.college.admission.enums.ExamSession;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "exam_schedules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_exam_schedule",
                        columnNames = {
                                "exam_code",
                                "exam_date",
                                "session"
                        }
                )
        }
)
public class ExamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "exam_code",
            nullable = false
    )
    private Exam exam;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "session",
            nullable = false,
            length = 20
    )
    private ExamSession session;

    /*
     * Required by JPA.
     */
    protected ExamSchedule() {
    }

    /*
     * Main constructor.
     */
    public ExamSchedule(
            Exam exam,
            LocalDate examDate,
            ExamSession session
    ) {

        validateExam(exam);
        validateExamDate(examDate);
        validateSession(session);

        this.exam = exam;
        this.examDate = examDate;
        this.session = session;
    }

    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------

    private void validateExam(Exam exam) {

        if (exam == null) {
            throw new IllegalArgumentException(
                    "Exam cannot be null."
            );
        }
    }

    private void validateExamDate(LocalDate examDate) {

        if (examDate == null) {
            throw new IllegalArgumentException(
                    "Exam date cannot be null."
            );
        }
    }

    private void validateSession(ExamSession session) {

        if (session == null) {
            throw new IllegalArgumentException(
                    "Exam session cannot be null."
            );
        }
    }

    // ---------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------

    public Long getScheduleId() {
        return scheduleId;
    }

    public Exam getExam() {
        return exam;
    }

    public String getExamCode() {

        return exam != null
                ? exam.getExamCode()
                : null;
    }

    public String getExamName() {

        return exam != null
                ? exam.getExamName()
                : null;
    }

    public String getExamDescription() {

        return exam != null
                ? exam.getDescription()
                : null;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public ExamSession getSession() {
        return session;
    }

    // ---------------------------------------------------------
    // DISPLAY
    // ---------------------------------------------------------

    public void displaySchedule() {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                  EXAMINATION SCHEDULE"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Schedule ID  : "
                        + scheduleId
        );

        System.out.println(
                "Exam Code    : "
                        + getExamCode()
        );

        System.out.println(
                "Exam Name    : "
                        + getExamName()
        );

        System.out.println(
                "Description  : "
                        + getExamDescription()
        );

        System.out.println(
                "Exam Date    : "
                        + examDate
        );

        System.out.println(
                "Session      : "
                        + session
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

        return "ExamSchedule{" +
                "scheduleId=" + scheduleId +
                ", exam=" + exam +
                ", examDate=" + examDate +
                ", session=" + session +
                '}';
    }
}