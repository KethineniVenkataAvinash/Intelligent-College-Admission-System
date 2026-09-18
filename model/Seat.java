package com.college.admission.model;

import com.college.admission.enums.Category;
import com.college.admission.enums.SeatStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @Column(name = "seat_number", length = 30)
    private String seatNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "course_code",
            nullable = false
    )
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SeatStatus status;

    @Column(name = "application_number", length = 30)
    private String applicationNumber;

    /*
     * Required by JPA.
     */
    protected Seat() {
    }

    public Seat(
            String seatNumber,
            Course course,
            Category category
    ) {

        if (seatNumber == null || seatNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Seat number cannot be empty."
            );
        }

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course cannot be null."
            );
        }

        if (category == null) {
            throw new IllegalArgumentException(
                    "Category cannot be null."
            );
        }

        this.seatNumber = seatNumber;
        this.course = course;
        this.category = category;
        this.status = SeatStatus.AVAILABLE;
        this.applicationNumber = null;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public Course getCourse() {
        return course;
    }

    public String getCourseCode() {
        return course.getCourseCode();
    }

    public Category getCategory() {
        return category;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public void allocate(String applicationNumber) {

        if (!isAvailable()) {
            throw new IllegalStateException(
                    "Seat " + seatNumber
                            + " is not available."
            );
        }

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number is required."
            );
        }

        this.applicationNumber = applicationNumber;
        this.status = SeatStatus.ALLOTTED;
    }

    public void release() {

        this.applicationNumber = null;
        this.status = SeatStatus.AVAILABLE;
    }

    @Override
    public String toString() {

        return seatNumber
                + " | Course: "
                + course.getCourseCode()
                + " | Category: "
                + category
                + " | Status: "
                + status;
    }
}