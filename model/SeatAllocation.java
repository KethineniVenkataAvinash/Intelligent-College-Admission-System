package com.college.admission.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seat_allocations")
public class SeatAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allocation_id")
    private Long allocationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "application_number",
            nullable = false
    )
    private Application application;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "seat_number",
            nullable = false
    )
    private Seat seat;

    @Column(name = "preference_number", nullable = false)
    private int preferenceNumber;

    protected SeatAllocation() {
    }

    public SeatAllocation(
            Application application,
            Seat seat,
            int preferenceNumber
    ) {

        if (application == null) {
            throw new IllegalArgumentException(
                    "Application cannot be null."
            );
        }

        if (seat == null) {
            throw new IllegalArgumentException(
                    "Seat cannot be null."
            );
        }

        if (preferenceNumber <= 0) {
            throw new IllegalArgumentException(
                    "Preference number must be greater than zero."
            );
        }

        this.application = application;
        this.seat = seat;
        this.preferenceNumber = preferenceNumber;
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public Application getApplication() {
        return application;
    }

    public Seat getSeat() {
        return seat;
    }

    public int getPreferenceNumber() {
        return preferenceNumber;
    }

    /*
     * Convenience methods.
     * These keep the rest of your existing
     * allocation code easy to read.
     */

    public String getApplicationNumber() {
        return application.getApplicationNumber();
    }

    public String getStudentName() {
        return application.getStudent().getName();
    }

    public String getCourseCode() {
        return seat.getCourse().getCourseCode();
    }

    public String getCourseName() {
        return seat.getCourse().getCourseName();
    }

    public String getSeatNumber() {
        return seat.getSeatNumber();
    }

    @Override
    public String toString() {

        return "SeatAllocation{" +
                "allocationId=" + allocationId +
                ", applicationNumber='" +
                getApplicationNumber() + '\'' +
                ", studentName='" +
                getStudentName() + '\'' +
                ", courseCode='" +
                getCourseCode() + '\'' +
                ", courseName='" +
                getCourseName() + '\'' +
                ", preferenceNumber=" +
                preferenceNumber +
                ", seatNumber='" +
                getSeatNumber() + '\'' +
                '}';
    }
}