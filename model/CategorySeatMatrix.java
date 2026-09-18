package com.college.admission.model;

import com.college.admission.enums.Category;

import java.util.EnumMap;
import java.util.Map;

public class CategorySeatMatrix {

    private final String courseCode;
    private final int totalSeats;

    private final Map<Category, Integer> categorySeats;
    private final Map<Category, Integer> availableCategorySeats;

    public CategorySeatMatrix(
            String courseCode,
            int totalSeats
    ) {
        this.courseCode = courseCode;
        this.totalSeats = totalSeats;

        categorySeats =
                new EnumMap<>(Category.class);

        availableCategorySeats =
                new EnumMap<>(Category.class);

        initializeCategories(totalSeats);
    }

    /**
     * Initializes category-wise seat distribution.
     *
     * Distribution:
     *
     * GENERAL = 50%
     * OBC     = 27%
     * SC      = 15%
     * ST      = 4%
     * EWS     = remaining seats
     *
     * Example for 50 seats:
     *
     * GENERAL = 25
     * OBC     = 13
     * SC      = 7
     * ST      = 2
     * EWS     = 3
     *
     * Total = 50
     */
    private void initializeCategories(
            int totalSeats
    ) {

        if (totalSeats < 0) {

            throw new IllegalArgumentException(
                    "Total seats cannot be negative."
            );
        }

        /*
         * Calculate reserved category seats.
         */
        int generalSeats =
                (int) Math.floor(
                        totalSeats * 0.50
                );

        int obcSeats =
                (int) Math.floor(
                        totalSeats * 0.27
                );

        int scSeats =
                (int) Math.floor(
                        totalSeats * 0.15
                );

        int stSeats =
                (int) Math.floor(
                        totalSeats * 0.04
                );

        /*
         * Calculate already assigned seats.
         */
        int assignedSeats =
                generalSeats
                        + obcSeats
                        + scSeats
                        + stSeats;

        /*
         * EWS receives all remaining seats.
         *
         * This guarantees that:
         *
         * GENERAL + OBC + SC + ST + EWS
         *
         * always equals totalSeats.
         */
        int ewsSeats =
                totalSeats - assignedSeats;

        /*
         * Store total category seats.
         */
        categorySeats.put(
                Category.GENERAL,
                generalSeats
        );

        categorySeats.put(
                Category.OBC,
                obcSeats
        );

        categorySeats.put(
                Category.SC,
                scSeats
        );

        categorySeats.put(
                Category.ST,
                stSeats
        );

        categorySeats.put(
                Category.EWS,
                ewsSeats
        );

        /*
         * Initially all category seats are available.
         */
        for (Category category :
                Category.values()) {

            availableCategorySeats.put(
                    category,
                    categorySeats.getOrDefault(
                            category,
                            0
                    )
            );
        }
    }

    /**
     * Returns the course code.
     */
    public String getCourseCode() {

        return courseCode;
    }

    /**
     * Returns total seats in the course.
     */
    public int getTotalSeats() {

        return totalSeats;
    }

    /**
     * Returns total seats assigned to
     * the specified category.
     */
    public int getTotalCategorySeats(
            Category category
    ) {

        return categorySeats.getOrDefault(
                category,
                0
        );
    }

    /**
     * Returns currently available seats
     * for the specified category.
     */
    public int getAvailableCategorySeats(
            Category category
    ) {

        return availableCategorySeats.getOrDefault(
                category,
                0
        );
    }

    /**
     * Updates the available seat count.
     *
     * This method is used by SeatMatrixService
     * to synchronize the in-memory matrix with
     * the actual MySQL seat availability.
     */
    public void setAvailableCategorySeats(
            Category category,
            int availableSeats
    ) {

        if (category == null) {
            return;
        }

        int totalSeatsForCategory =
                getTotalCategorySeats(category);

        /*
         * Prevent invalid values.
         *
         * Available seats can never be:
         *
         * less than 0
         * greater than total category seats
         */
        int safeAvailableSeats =
                Math.max(
                        0,
                        Math.min(
                                availableSeats,
                                totalSeatsForCategory
                        )
                );

        availableCategorySeats.put(
                category,
                safeAvailableSeats
        );
    }

    /**
     * Checks whether at least one seat is
     * available for the specified category.
     */
    public boolean hasAvailableSeat(
            Category category
    ) {

        return getAvailableCategorySeats(
                category
        ) > 0;
    }

    /**
     * Allocates one seat from the specified
     * category in the in-memory matrix.
     */
    public void allocateSeat(
            Category category
    ) {

        int available =
                getAvailableCategorySeats(
                        category
                );

        if (available <= 0) {

            throw new IllegalStateException(
                    "No " + category
                            + " seats available for course "
                            + courseCode
            );
        }

        availableCategorySeats.put(
                category,
                available - 1
        );
    }

    /**
     * Releases one seat back to the specified
     * category.
     */
    public void releaseSeat(
            Category category
    ) {

        int available =
                getAvailableCategorySeats(
                        category
                );

        int total =
                getTotalCategorySeats(
                        category
                );

        /*
         * Prevent available seats from becoming
         * greater than the original capacity.
         */
        if (available < total) {

            availableCategorySeats.put(
                    category,
                    available + 1
            );
        }
    }

    /**
     * Displays the category-wise seat matrix.
     */
    public void displayMatrix() {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                 CATEGORY SEAT MATRIX"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.printf(
                "%-12s %-12s %-15s%n",
                "Category",
                "Total Seats",
                "Available"
        );

        System.out.println(
                "--------------------------------------------------------------"
        );

        for (Category category :
                Category.values()) {

            System.out.printf(
                    "%-12s %-12d %-15d%n",
                    category,
                    getTotalCategorySeats(category),
                    getAvailableCategorySeats(category)
            );
        }

        System.out.println(
                "=============================================================="
        );
    }
}