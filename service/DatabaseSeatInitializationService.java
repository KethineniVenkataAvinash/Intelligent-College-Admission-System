package com.college.admission.service;

import com.college.admission.enums.Category;
import com.college.admission.jpa.CourseJpaRepository;
import com.college.admission.jpa.SeatJpaRepository;
import com.college.admission.model.Course;
import com.college.admission.model.Seat;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSeatInitializationService {

    private final CourseJpaRepository courseJpaRepository;
    private final SeatJpaRepository seatJpaRepository;

    public DatabaseSeatInitializationService() {

        courseJpaRepository =
                new CourseJpaRepository();

        seatJpaRepository =
                new SeatJpaRepository();
    }

    /**
     * Creates seats in MySQL for all courses.
     *
     * Existing seats are not recreated.
     */
    public void initializeSeats() {

        List<Course> courses =
                courseJpaRepository.findAll();

        if (courses == null || courses.isEmpty()) {

            System.out.println(
                    "No courses found. Cannot initialize seats."
            );

            return;
        }

        int createdSeats = 0;

        for (Course course : courses) {

            if (course == null) {
                continue;
            }

            List<Seat> existingSeats =
                    seatJpaRepository.findByCourseCode(
                            course.getCourseCode()
                    );

            if (existingSeats != null
                    && !existingSeats.isEmpty()) {

                System.out.println(
                        "Seats already exist for course: "
                                + course.getCourseCode()
                );

                continue;
            }

            List<Seat> seats =
                    generateSeats(course);

            if (!seats.isEmpty()) {

                seatJpaRepository.saveAll(seats);

                createdSeats += seats.size();

                System.out.println(
                        seats.size()
                                + " seats created for "
                                + course.getCourseCode()
                );
            }
        }

        System.out.println();
        System.out.println(
                "Database seat initialization completed."
        );

        System.out.println(
                "Total seats created: "
                        + createdSeats
        );
    }

    /**
     * Generates category-wise seats.
     *
     * Distribution:
     *
     * GENERAL = 50%
     * OBC     = 27%
     * SC      = 15%
     * ST      = 4%
     * EWS     = remaining seats
     */
    private List<Seat> generateSeats(
            Course course
    ) {

        int totalSeats =
                course.getTotalSeats();

        int generalSeats =
                (int) Math.floor(totalSeats * 0.50);

        int obcSeats =
                (int) Math.floor(totalSeats * 0.27);

        int scSeats =
                (int) Math.floor(totalSeats * 0.15);

        int stSeats =
                (int) Math.floor(totalSeats * 0.04);

        int assigned =
                generalSeats
                        + obcSeats
                        + scSeats
                        + stSeats;

        int ewsSeats =
                totalSeats - assigned;

        List<Seat> seats =
                new ArrayList<>(totalSeats);

        addSeats(
                seats,
                course,
                Category.GENERAL,
                generalSeats
        );

        addSeats(
                seats,
                course,
                Category.OBC,
                obcSeats
        );

        addSeats(
                seats,
                course,
                Category.SC,
                scSeats
        );

        addSeats(
                seats,
                course,
                Category.ST,
                stSeats
        );

        addSeats(
                seats,
                course,
                Category.EWS,
                ewsSeats
        );

        return seats;
    }

    /**
     * Creates sequential seat numbers.
     */
    private void addSeats(
            List<Seat> seats,
            Course course,
            Category category,
            int count
    ) {

        String courseCode =
                course.getCourseCode();

        for (int i = 1; i <= count; i++) {

            String seatNumber =
                    String.format(
                            "%s-%s-%03d",
                            courseCode,
                            category.name(),
                            i
                    );

            seats.add(
                    new Seat(
                            seatNumber,
                            course,
                            category
                    )
            );
        }
    }
}