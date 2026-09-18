package com.college.admission.model;

import com.college.admission.enums.ApplicationStatus;
import com.college.admission.enums.CounsellingStatus;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @Column(name = "application_number", length = 30)
    private String applicationNumber;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            unique = true
    )
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "counselling_status", nullable = false)
    private CounsellingStatus counsellingStatus;

    /*
     * Course preferences will be mapped later.
     *
     * For now, this remains a normal Java collection.
     */
    @OneToMany(
        mappedBy = "application",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    ) 
    private List<CoursePreference> preferences =
            new ArrayList<>();

    @Column(name = "waitlist_position")
    private Integer waitlistPosition;
    
    public Integer getWaitlistPosition() {
         return waitlistPosition;
    }

    public void setWaitlistPosition(Integer waitlistPosition) {
            this.waitlistPosition = waitlistPosition;
    }
    /*
     * Required by JPA
     */
    protected Application() {
    }


    /*
     * Existing application constructor
     */
    public Application(
            String applicationNumber,
            Student student
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        this.applicationNumber = applicationNumber;
        this.student = student;

        this.status =
                ApplicationStatus.DRAFT;

        this.counsellingStatus =
                CounsellingStatus.NOT_STARTED;
    }


    public String getApplicationNumber() {
        return applicationNumber;
    }


    public Student getStudent() {
        return student;
    }


    public ApplicationStatus getStatus() {
        return status;
    }


    public List<CoursePreference> getPreferences() {
        return preferences;
    }


    public void addPreference(
            CoursePreference preference
    ) {

        if (preference == null) {

            throw new IllegalArgumentException(
                    "Course preference cannot be null."
            );
        }
        preference.setApplication(this);

        preferences.add(preference);
    }


    public void submit() {

        if (preferences.isEmpty()) {

            throw new IllegalStateException(
                    "At least one course preference is required."
            );
        }

        status =
                ApplicationStatus.SUBMITTED;
    }


    public void verify() {

        if (status != ApplicationStatus.SUBMITTED) {

            throw new IllegalStateException(
                    "Application must be submitted before verification."
            );
        }

        status =
                ApplicationStatus.VERIFIED;
    }


    public void setStatus(
            ApplicationStatus status
    ) {

        if (status == null) {

            throw new IllegalArgumentException(
                    "Application status cannot be null."
            );
        }

        this.status = status;
    }


    public CounsellingStatus getCounsellingStatus() {
        return counsellingStatus;
    }


    public void setCounsellingStatus(
            CounsellingStatus counsellingStatus
    ) {

        if (counsellingStatus == null) {

            throw new IllegalArgumentException(
                    "Counselling status cannot be null."
            );
        }

        this.counsellingStatus =
                counsellingStatus;
    }
}