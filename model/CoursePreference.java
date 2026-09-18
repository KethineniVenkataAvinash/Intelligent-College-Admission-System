package com.college.admission.model;

import jakarta.persistence.*;

@Entity
@Table(name = "course_preferences")
public class CoursePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preference_id")
    private Long preferenceId;

    @Column(name = "preference_number", nullable = false)
    private int preferenceNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "course_code",
            nullable = false
    )
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "application_number",
            nullable = false
    )
    private Application application;

    protected CoursePreference() {
    }

    public CoursePreference(
            int preferenceNumber,
            Course course
    ) {

        if (preferenceNumber <= 0) {
            throw new IllegalArgumentException(
                    "Preference number must be greater than zero."
            );
        }

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course cannot be null."
            );
        }

        this.preferenceNumber = preferenceNumber;
        this.course = course;
    }

    public Long getPreferenceId() {
        return preferenceId;
    }

    public int getPreferenceNumber() {
        return preferenceNumber;
    }

    public Course getCourse() {
        return course;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {

        if (application == null) {
            throw new IllegalArgumentException(
                    "Application cannot be null."
            );
        }

        this.application = application;
    }

    @Override
    public String toString() {

        return preferenceNumber
                + ". "
                + course.getCourseCode()
                + " - "
                + course.getCourseName();
    }
}