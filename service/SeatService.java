package com.college.admission.service;

import com.college.admission.enums.Category;
import com.college.admission.model.Course;
import com.college.admission.model.Seat;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatService {

    private final Map<String, List<Seat>> seatsByCourse;

    public SeatService() {
        seatsByCourse = new HashMap<>();
    }

    /**
     * Creates individual seats for every course.
     */
    public void initializeSeats(List<Course> courses) {

        if (courses == null || courses.isEmpty()) {
            throw new IllegalArgumentException(
                    "Courses are required to initialize seats."
            );
        }

        seatsByCourse.clear();

        for (Course course : courses) {

            if (course == null) {
                continue;
            }

            List<Seat> seats = new ArrayList<>();

            createCategorySeats(
                    seats,
                    course,
                    Category.GENERAL,
                    (int) (course.getTotalSeats() * 0.50)
            );

            createCategorySeats(
                    seats,
                    course,
                    Category.OBC,
                    (int) (course.getTotalSeats() * 0.27)
            );

            createCategorySeats(
                    seats,
                    course,
                    Category.SC,
                    (int) (course.getTotalSeats() * 0.15)
            );

            createCategorySeats(
                    seats,
                    course,
                    Category.ST,
                    (int) (course.getTotalSeats() * 0.04)
            );

            /*
             * EWS receives the remaining seats so that
             * the total generated seats exactly matches
             * the course capacity.
             */
            int alreadyCreated = seats.size();

            int ewsSeats =
                    course.getTotalSeats() - alreadyCreated;

            createCategorySeats(
                    seats,
                    course,
                    Category.EWS,
                    ewsSeats
            );

            seatsByCourse.put(
                    course.getCourseCode().toUpperCase(),
                    seats
            );
        }
    }

    /**
     * Creates seats for a particular course and category.
     */
    private void createCategorySeats(
            List<Seat> seats,
            Course course,
            Category category,
            int count
    ) {

        for (int i = 1; i <= count; i++) {

            String seatNumber =
                    course.getCourseCode().toUpperCase()
                            + "-"
                            + category.name()
                            + "-"
                            + String.format("%03d", i);

            seats.add(
                    new Seat(
                            seatNumber,
                            course,
                            category
                    )
            );
        }
    }

    /**
     * Finds and allocates the first available seat
     * belonging to the requested course and category.
     */
    public Seat allocateSeat(
            String courseCode,
            Category category,
            String applicationNumber
    ) {

        if (courseCode == null || courseCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Course code is required."
            );
        }

        if (category == null) {
            throw new IllegalArgumentException(
                    "Category is required."
            );
        }

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number is required."
            );
        }

        List<Seat> seats =
                seatsByCourse.get(
                        courseCode.toUpperCase()
                );

        if (seats == null) {
            throw new IllegalArgumentException(
                    "No seats found for course: "
                            + courseCode
            );
        }

        for (Seat seat : seats) {

            if (seat.getCategory() == category
                    && seat.isAvailable()) {

                seat.allocate(applicationNumber);

                return seat;
            }
        }

        return null;
    }

    /**
     * Finds a seat by its seat number.
     */
    public Seat findSeat(String seatNumber) {

        if (seatNumber == null || seatNumber.isBlank()) {
            return null;
        }

        for (List<Seat> seats :
                seatsByCourse.values()) {

            for (Seat seat : seats) {

                if (seat.getSeatNumber()
                        .equalsIgnoreCase(seatNumber)) {

                    return seat;
                }
            }
        }

        return null;
    }

    /**
     * Returns all seats belonging to a course.
     */
    public List<Seat> getSeatsForCourse(
            String courseCode
    ) {

        if (courseCode == null || courseCode.isBlank()) {
            return new ArrayList<>();
        }

        return seatsByCourse.getOrDefault(
                courseCode.toUpperCase(),
                new ArrayList<>()
        );
    }

    /**
     * Returns the number of available seats
     * for a particular course and category.
     */
    public int getAvailableSeatCount(
            String courseCode,
            Category category
    ) {

        if (category == null) {
            return 0;
        }

        int count = 0;

        for (Seat seat :
                getSeatsForCourse(courseCode)) {

            if (seat.getCategory() == category
                    && seat.isAvailable()) {

                count++;
            }
        }

        return count;
    }

    /**
     * Displays all seats of a particular course.
     */
    public void displayCourseSeats(
            String courseCode
    ) {

        List<Seat> seats =
                getSeatsForCourse(courseCode);

        System.out.println();
        System.out.println(
                "=============================================================="
        );
        System.out.println(
                "                    COURSE SEATS"
        );
        System.out.println(
                "=============================================================="
        );

        if (seats.isEmpty()) {

            System.out.println(
                    "No seats found for course: "
                            + courseCode
            );

            return;
        }

        for (Seat seat : seats) {
            System.out.println(seat);
        }

        System.out.println(
                "=============================================================="
        );
    }

    /**
     * Displays available seat counts
     * category-wise for every course.
     */
    public void displaySeatSummary() {

        System.out.println();
        System.out.println(
                "================================================================"
        );
        System.out.println(
                "                    SEAT SUMMARY"
        );
        System.out.println(
                "================================================================"
        );

        for (Map.Entry<String, List<Seat>> entry :
                seatsByCourse.entrySet()) {

            String courseCode = entry.getKey();

            Map<Category, Integer> available =
                    new EnumMap<>(Category.class);

            for (Category category :
                    Category.values()) {

                available.put(
                        category,
                        getAvailableSeatCount(
                                courseCode,
                                category
                        )
                );
            }

            System.out.println();
            System.out.println(
                    "Course: " + courseCode
            );

            for (Category category :
                    Category.values()) {

                System.out.println(
                        "  "
                                + category
                                + " Available: "
                                + available.get(category)
                );
            }
        }

        System.out.println(
                "================================================================"
        );
    }
}