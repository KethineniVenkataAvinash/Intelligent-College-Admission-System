package com.college.admission.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @Column(name = "exam_code", length = 20, nullable = false)
    private String examCode;

    @Column(name = "exam_name", length = 150, nullable = false)
    private String examName;

    @Column(name = "description", length = 500, nullable = false)
    private String description;

    /*
     * Required by JPA.
     */
    protected Exam() {
    }

    /*
     * Main constructor.
     */
    public Exam(
            String examCode,
            String examName,
            String description
    ) {

        validateExamCode(examCode);
        validateExamName(examName);
        validateDescription(description);

        this.examCode = examCode.trim().toUpperCase();
        this.examName = examName.trim();
        this.description = description.trim();
    }

    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------

    private void validateExamCode(String examCode) {

        if (examCode == null || examCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Exam code cannot be empty."
            );
        }

        if (examCode.trim().length() > 20) {
            throw new IllegalArgumentException(
                    "Exam code cannot exceed 20 characters."
            );
        }
    }

    private void validateExamName(String examName) {

        if (examName == null || examName.isBlank()) {
            throw new IllegalArgumentException(
                    "Exam name cannot be empty."
            );
        }

        if (examName.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Exam name cannot exceed 150 characters."
            );
        }
    }

    private void validateDescription(String description) {

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Exam description cannot be empty."
            );
        }

        if (description.trim().length() > 500) {
            throw new IllegalArgumentException(
                    "Exam description cannot exceed 500 characters."
            );
        }
    }

    // ---------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------

    public String getExamCode() {
        return examCode;
    }

    public String getExamName() {
        return examName;
    }

    public String getDescription() {
        return description;
    }

    // ---------------------------------------------------------
    // DISPLAY
    // ---------------------------------------------------------

    public void displayExam() {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                       EXAM DETAILS"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Exam Code    : " + examCode
        );

        System.out.println(
                "Exam Name    : " + examName
        );

        System.out.println(
                "Description  : " + description
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

        return "Exam{" +
                "examCode='" + examCode + '\'' +
                ", examName='" + examName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}