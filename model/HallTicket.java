package com.college.admission.model;

import com.college.admission.enums.ExamSession;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "hall_tickets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hall_ticket_application_exam",
                        columnNames = {
                                "application_number",
                                "exam_date",
                                "session"
                        }
                )
        }
)
public class HallTicket {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hall_ticket_id")
    private Long hallTicketId;


    // =========================================================
    // STUDENT / ADMISSION DETAILS
    // =========================================================

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
            name = "category",
            nullable = false,
            length = 20
    )
    private String category;

    @Column(name = "rank_number")
    private Integer rank;


    // =========================================================
    // COURSE / ADMISSION SEAT DETAILS
    // =========================================================

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
            name = "admission_seat_number",
            length = 30
    )
    private String admissionSeatNumber;


    // =========================================================
    // EXAM CENTRE DETAILS
    // =========================================================

    @Column(
            name = "exam_centre_code",
            nullable = false,
            length = 20
    )
    private String examCentreCode;

    @Column(
            name = "exam_centre_name",
            nullable = false,
            length = 150
    )
    private String examCentreName;

    @Column(
            name = "room_number",
            nullable = false,
            length = 30
    )
    private String roomNumber;

    @Column(
            name = "exam_seat_number",
            nullable = false,
            length = 30
    )
    private String examSeatNumber;


    // =========================================================
    // EXAM DETAILS
    // =========================================================

    @Column(
            name = "exam_date",
            nullable = false
    )
    private LocalDate examDate;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "session",
            nullable = false,
            length = 20
    )
    private ExamSession session;


    // =========================================================
    // JPA CONSTRUCTOR
    // =========================================================

    /**
     * Required by JPA/Hibernate.
     */
    protected HallTicket() {
    }


    // =========================================================
    // APPLICATION CONSTRUCTOR
    // =========================================================

    /**
     * Existing constructor preserved.
     *
     * Existing HallTicketService/Main.java code can
     * continue using this constructor.
     */
    public HallTicket(
            String applicationNumber,
            String studentName,
            String category,
            Integer rank,
            String courseCode,
            String courseName,
            String admissionSeatNumber,
            String examCentreCode,
            String examCentreName,
            String roomNumber,
            String examSeatNumber,
            LocalDate examDate,
            ExamSession session
    ) {

        validate(
                applicationNumber,
                studentName,
                category,
                courseCode,
                courseName,
                examCentreCode,
                examCentreName,
                roomNumber,
                examSeatNumber,
                examDate,
                session
        );

        this.applicationNumber = applicationNumber.trim();
        this.studentName = studentName.trim();
        this.category = category.trim();
        this.rank = rank;

        this.courseCode = courseCode.trim();
        this.courseName = courseName.trim();
        this.admissionSeatNumber =
                admissionSeatNumber == null
                        ? null
                        : admissionSeatNumber.trim();

        this.examCentreCode =
                examCentreCode.trim();

        this.examCentreName =
                examCentreName.trim();

        this.roomNumber =
                roomNumber.trim();

        this.examSeatNumber =
                examSeatNumber.trim();

        this.examDate = examDate;
        this.session = session;
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    private void validate(
            String applicationNumber,
            String studentName,
            String category,
            String courseCode,
            String courseName,
            String examCentreCode,
            String examCentreName,
            String roomNumber,
            String examSeatNumber,
            LocalDate examDate,
            ExamSession session
    ) {

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

        if (category == null
                || category.isBlank()) {

            throw new IllegalArgumentException(
                    "Category cannot be empty."
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

        if (examCentreCode == null
                || examCentreCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Exam centre code cannot be empty."
            );
        }

        if (examCentreName == null
                || examCentreName.isBlank()) {

            throw new IllegalArgumentException(
                    "Exam centre name cannot be empty."
            );
        }

        if (roomNumber == null
                || roomNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Room number cannot be empty."
            );
        }

        if (examSeatNumber == null
                || examSeatNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Exam seat number cannot be empty."
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
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getHallTicketId() {
        return hallTicketId;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCategory() {
        return category;
    }

    public Integer getRank() {
        return rank;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getAdmissionSeatNumber() {
        return admissionSeatNumber;
    }

    public String getExamCentreCode() {
        return examCentreCode;
    }

    public String getExamCentreName() {
        return examCentreName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getExamSeatNumber() {
        return examSeatNumber;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public ExamSession getSession() {
        return session;
    }


    // =========================================================
    // DISPLAY
    // =========================================================

    public void displayHallTicket() {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                       HALL TICKET"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Hall Ticket ID      : " + hallTicketId
        );

        System.out.println(
                "Application Number  : " + applicationNumber
        );

        System.out.println(
                "Student Name        : " + studentName
        );

        System.out.println(
                "Category            : " + category
        );

        System.out.println(
                "Rank                : " + rank
        );

        System.out.println(
                "--------------------------------------------------------------"
        );

        System.out.println(
                "Course Code         : " + courseCode
        );

        System.out.println(
                "Course Name         : " + courseName
        );

        System.out.println(
                "Admission Seat No.  : " + admissionSeatNumber
        );

        System.out.println(
                "--------------------------------------------------------------"
        );

        System.out.println(
                "Exam Centre Code    : " + examCentreCode
        );

        System.out.println(
                "Exam Centre Name    : " + examCentreName
        );

        System.out.println(
                "Room Number         : " + roomNumber
        );

        System.out.println(
                "Exam Seat Number    : " + examSeatNumber
        );

        System.out.println(
                "Exam Date           : " + examDate
        );

        System.out.println(
                "Session             : " + session
        );

        System.out.println(
                "=============================================================="
        );
    }


    // =========================================================
    // TOSTRING
    // =========================================================

    @Override
    public String toString() {

        return "HallTicket{" +
                "hallTicketId=" + hallTicketId +
                ", applicationNumber='" +
                applicationNumber + '\'' +
                ", studentName='" +
                studentName + '\'' +
                ", category='" +
                category + '\'' +
                ", rank=" + rank +
                ", courseCode='" +
                courseCode + '\'' +
                ", courseName='" +
                courseName + '\'' +
                ", admissionSeatNumber='" +
                admissionSeatNumber + '\'' +
                ", examCentreCode='" +
                examCentreCode + '\'' +
                ", examCentreName='" +
                examCentreName + '\'' +
                ", roomNumber='" +
                roomNumber + '\'' +
                ", examSeatNumber='" +
                examSeatNumber + '\'' +
                ", examDate=" +
                examDate +
                ", session=" +
                session +
                '}';
    }
}