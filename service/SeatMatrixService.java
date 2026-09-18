package com.college.admission.service;

import com.college.admission.enums.Category;
import com.college.admission.jpa.SeatJpaRepository;
import com.college.admission.model.Course;
import com.college.admission.security.ServiceAuthorization;


import java.util.HashMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SeatMatrixService {

        public static class CategorySeatMatrix {

                private final String courseCode;
                private final int totalSeats;
                private final Map<Category, Integer> availableSeats;

                public CategorySeatMatrix(String courseCode, int totalSeats) {
                        this.courseCode = courseCode;
                        this.totalSeats = Math.max(0, totalSeats);
                        this.availableSeats = new EnumMap<>(Category.class);

                        for (Category category : Category.values()) {
                                availableSeats.put(category, this.totalSeats);
                        }
                }

                public String getCourseCode() {
                        return courseCode;
                }

                public void setAvailableCategorySeats(
                                Category category,
                                int seats
                ) {
                        if (category != null) {
                                availableSeats.put(category, Math.max(0, seats));
                        }
                }

                public boolean hasAvailableSeat(Category category) {
                        return category != null
                                        && availableSeats.getOrDefault(category, 0) > 0;
                }

                public void allocateSeat(Category category) {
                        if (hasAvailableSeat(category)) {
                                availableSeats.put(
                                                category,
                                                availableSeats.get(category) - 1
                                );
                        }
                }

                public void releaseSeat(Category category) {
                        if (category != null) {
                                availableSeats.put(
                                                category,
                                                Math.min(
                                                                totalSeats,
                                                                availableSeats.getOrDefault(category, 0) + 1
                                                )
                                );
                        }
                }

                public void displayMatrix() {
                        for (Category category : Category.values()) {
                                System.out.println(
                                                category + " : "
                                                                + availableSeats.getOrDefault(category, 0)
                                );
                        }
                }
        }

    private final Map<String, CategorySeatMatrix> seatMatrices;

    private final SeatJpaRepository seatJpaRepository;

    public SeatMatrixService() {

        seatMatrices =
                new HashMap<>();

        seatJpaRepository =
                new SeatJpaRepository();
    }

    /**
     * Initializes the in-memory seat matrices
     * for all available courses.
     */
    public void initializeSeatMatrices(
            List<Course> courses
    ) {

        if (courses == null
                || courses.isEmpty()) {

            throw new IllegalArgumentException(
                    "Courses cannot be empty."
            );
        }

        seatMatrices.clear();

        for (Course course : courses) {

            if (course == null) {
                continue;
            }

            String courseCode =
                    course.getCourseCode();

            if (courseCode == null
                    || courseCode.isBlank()) {

                continue;
            }

            CategorySeatMatrix matrix =
                    new CategorySeatMatrix(
                            courseCode,
                            course.getTotalSeats()
                    );

            seatMatrices.put(
                    courseCode.trim().toUpperCase(),
                    matrix
            );
        }
    }

    /**
     * Refreshes category-wise available seat counts
     * directly from the MySQL database.
     *
     * The database is the source of truth.
     */
    private void refreshAvailabilityFromDatabase() {

        if (seatMatrices.isEmpty()) {
            return;
        }

        for (CategorySeatMatrix matrix :
                seatMatrices.values()) {

            if (matrix == null) {
                continue;
            }

            String courseCode =
                    matrix.getCourseCode();

            if (courseCode == null
                    || courseCode.isBlank()) {

                continue;
            }

            /*
             * Refresh every category separately.
             *
             * SeatJpaRepository already provides:
             *
             * findAvailableSeats(
             *     courseCode,
             *     category
             * )
             *
             * Therefore we simply count the
             * returned available seats.
             */
            for (Category category :
                    Category.values()) {

                int availableSeats =
                        seatJpaRepository
                                .findAvailableSeats(
                                        courseCode,
                                        category
                                )
                                .size();

                matrix.setAvailableCategorySeats(
                        category,
                        availableSeats
                );
            }
        }
    }

    /**
     * Returns the matrix for a specific course.
     */
    public CategorySeatMatrix getSeatMatrix(
            String courseCode
    ) {

        if (courseCode == null
                || courseCode.isBlank()) {

            return null;
        }

        return seatMatrices.get(
                courseCode.trim().toUpperCase()
        );
    }

    /**
     * Checks whether a category has
     * an available seat.
     */
    public boolean hasAvailableSeat(
            String courseCode,
            Category category
    ) {

        if (category == null) {
            return false;
        }

        refreshAvailabilityFromDatabase();

        CategorySeatMatrix matrix =
                getSeatMatrix(courseCode);

        if (matrix == null) {
            return false;
        }

        return matrix.hasAvailableSeat(
                category
        );
    }

    /**
     * Allocates one seat in the in-memory matrix.
     *
     * Physical database allocation is performed
     * separately by the transactional allocation service.
     */
    public void allocateSeat(
            String courseCode,
            Category category
    ) {

        ServiceAuthorization.requireAdmin();

        if (category == null) {
            return;
        }

        CategorySeatMatrix matrix =
                getSeatMatrix(courseCode);

        if (matrix == null) {

            throw new IllegalArgumentException(
                    "Seat matrix not found for course: "
                            + courseCode
            );
        }

        matrix.allocateSeat(category);
    }

    /**
     * Releases one seat in the in-memory matrix.
     */
    public void releaseSeat(
            String courseCode,
            Category category
    ) {

        ServiceAuthorization.requireAdmin();

        if (category == null) {
            return;
        }

        CategorySeatMatrix matrix =
                getSeatMatrix(courseCode);

        if (matrix == null) {
            return;
        }

        matrix.releaseSeat(category);
    }

    /**
     * Displays all seat matrices.
     *
     * Availability is refreshed from MySQL
     * before displaying.
     */
    public void displayAllSeatMatrices() {

        ServiceAuthorization.requireAdmin();

        if (seatMatrices.isEmpty()) {

            System.out.println(
                    "Seat matrices have not been initialized."
            );

            return;
        }

        /*
         * IMPORTANT:
         *
         * Always refresh from MySQL before displaying.
         *
         * This ensures that the matrix reflects
         * actual physical seat availability.
         */
        refreshAvailabilityFromDatabase();

        System.out.println();

        System.out.println(
                "################################################################"
        );

        System.out.println(
                "                 COLLEGE SEAT MATRICES"
        );

        System.out.println(
                "################################################################"
        );

        for (CategorySeatMatrix matrix :
                seatMatrices.values()) {

            if (matrix == null) {
                continue;
            }

            System.out.println();

            System.out.println(
                    "Course Code : "
                            + matrix.getCourseCode()
            );

            matrix.displayMatrix();
        }

        System.out.println(
                "################################################################"
        );
    }
}