package com.college.admission.service;

import com.college.admission.jpa.CourseJpaRepository;
import com.college.admission.model.Course;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

public class CourseService {

    private final List<Course> courses;
    private final CourseJpaRepository courseRepository;

    public CourseService() {

        courses = new ArrayList<>();
        courseRepository = new CourseJpaRepository();

        initializeCourses();
    }

    /**
     * Initializes the default courses.
     *
     * If courses already exist in MySQL, they are loaded from
     * the database.
     *
     * If the database is empty, the default 8 courses are created
     * and saved to MySQL.
     */
    private void initializeCourses() {

        List<Course> databaseCourses =
                courseRepository.findAll();

        if (databaseCourses != null
                && !databaseCourses.isEmpty()) {

            courses.addAll(databaseCourses);

            return;
        }

        createDefaultCourses();

        for (Course course : courses) {

            try {

                courseRepository.save(course);

            } catch (RuntimeException e) {

                System.out.println(
                        "Unable to save course "
                                + course.getCourseCode()
                                + ": "
                                + e.getMessage()
                );
            }
        }
    }

    /**
     * Creates the default B.Tech courses.
     */
    private void createDefaultCourses() {

        courses.add(
                new Course(
                        "CSE",
                        "Computer Science Engineering",
                        50,
                        1000,
                        BigDecimal.valueOf(150000)
                )
        );

        courses.add(
                new Course(
                        "CSM",
                        "CSE - Artificial Intelligence and Machine Learning",
                        50,
                        1500,
                        BigDecimal.valueOf(150000)
                )
        );

        courses.add(
                new Course(
                        "ECE",
                        "Electronics and Communication Engineering",
                        50,
                        3500,
                        BigDecimal.valueOf(130000)
                )
        );

        courses.add(
                new Course(
                        "EEE",
                        "Electrical and Electronics Engineering",
                        50,
                        7500,
                        BigDecimal.valueOf(120000)
                )
        );

        courses.add(
                new Course(
                        "CSC",
                        "CSE - Cyber Security",
                        50,
                        15000,
                        BigDecimal.valueOf(145000)
                )
        );

        courses.add(
                new Course(
                        "IT",
                        "Information Technology",
                        50,
                        17500,
                        BigDecimal.valueOf(140000)
                )
        );

        courses.add(
                new Course(
                        "ME",
                        "Mechanical Engineering",
                        50,
                        20000,
                        BigDecimal.valueOf(110000)
                )
        );

        courses.add(
                new Course(
                        "CE",
                        "Civil Engineering",
                        50,
                        25000,
                        BigDecimal.valueOf(110000)
                )
        );
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Course findCourse(String courseCode) {

        if (courseCode == null) {
            return null;
        }

        for (Course course : courses) {

            if (course.getCourseCode()
                    .equalsIgnoreCase(courseCode)) {

                return course;
            }
        }

        return null;
    }

    public void displayCourses() {

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("                    AVAILABLE COURSES");
        System.out.println("==============================================================");

        System.out.printf(
                "%-8s %-45s %-10s %-12s%n",
                "Code",
                "Course",
                "Seats",
                "Annual Fee"
        );

        System.out.println(
                "--------------------------------------------------------------"
        );

        for (Course course : courses) {

            System.out.printf(
                    "%-8s %-45s %-10d ₹%-11.2f%n",
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getAvailableSeats(),
                    course.getAnnualFee()
            );
        }

        System.out.println(
                "=============================================================="
        );
    }
}