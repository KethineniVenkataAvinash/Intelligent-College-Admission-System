package com.college.admission.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.OneToMany;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @Column(name = "course_code", length = 20)
    private String courseCode;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @Column(name = "cutoff_rank", nullable = false)
    private int cutoffRank;

    @Column(name = "annual_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal annualFee;

    @OneToMany(
        mappedBy = "course",
        fetch = FetchType.LAZY
    )
    private List<CoursePreference> preferences =
    new ArrayList<>();

    public List<CoursePreference> getPreferences() {
    return preferences;
    }

    /*
     * Required by JPA.
     */
    protected Course() {
    }

    public Course(
            String courseCode,
            String courseName,
            int totalSeats,
            int cutoffRank,
            BigDecimal annualFee
    ) {

        if (courseCode == null || courseCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Course code cannot be empty."
            );
        }

        if (courseName == null || courseName.isBlank()) {
            throw new IllegalArgumentException(
                    "Course name cannot be empty."
            );
        }

        if (totalSeats <= 0) {
            throw new IllegalArgumentException(
                    "Total seats must be greater than zero."
            );
        }

        if (cutoffRank <= 0) {
            throw new IllegalArgumentException(
                    "Cutoff rank must be greater than zero."
            );
        }

        if (annualFee == null
                || annualFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Annual fee cannot be negative."
            );
        }

        this.courseCode = courseCode;
        this.courseName = courseName;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.cutoffRank = cutoffRank;
        this.annualFee = annualFee;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public int getCutoffRank() {
        return cutoffRank;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public boolean hasAvailableSeat() {
        return availableSeats > 0;
    }

    public void allocateSeat() {

        if (availableSeats <= 0) {
            throw new IllegalStateException(
                    "No seats available for " + courseName
            );
        }

        availableSeats--;
    }

    public void releaseSeat() {

        if (availableSeats < totalSeats) {
            availableSeats++;
        }
    }
}