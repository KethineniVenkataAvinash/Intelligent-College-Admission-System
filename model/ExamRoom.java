package com.college.admission.model;

import com.college.admission.enums.Category;

public class ExamRoom {

    private final String roomNumber;
    private final int rows;
    private final int columns;

    /*
     * 2-D array representing physical exam seats.
     *
     * Example:
     *
     * [Seat][Seat][Seat][Seat]
     * [Seat][Seat][Seat][Seat]
     * [Seat][Seat][Seat][Seat]
     */
    private final ExamSeat[][] seats;

    public ExamRoom(
            String roomNumber,
            int rows,
            int columns
    ) {

        if (roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Room number cannot be empty."
            );
        }

        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException(
                    "Rows and columns must be greater than zero."
            );
        }

        this.roomNumber = roomNumber;
        this.rows = rows;
        this.columns = columns;

        this.seats = new ExamSeat[rows][columns];

        initializeSeats();
    }

    private void initializeSeats() {

        int seatCounter = 1;

        for (int row = 0; row < rows; row++) {

            for (int column = 0; column < columns; column++) {

                String seatNumber =
                        roomNumber
                                + "-"
                                + String.format(
                                        "%03d",
                                        seatCounter
                                );

                seats[row][column] =
                        new ExamSeat(
                                seatNumber,
                                row,
                                column
                        );

                seatCounter++;
            }
        }
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getCapacity() {
        return rows * columns;
    }

    public ExamSeat[][] getSeats() {
        return seats;
    }

    public int getAvailableSeats() {

        int count = 0;

        for (int row = 0; row < rows; row++) {

            for (int column = 0; column < columns; column++) {

                if (seats[row][column].isAvailable()) {
                    count++;
                }
            }
        }

        return count;
    }

    public ExamSeat findAvailableSeat() {

        for (int row = 0; row < rows; row++) {

            for (int column = 0; column < columns; column++) {

                if (seats[row][column].isAvailable()) {
                    return seats[row][column];
                }
            }
        }

        return null;
    }

    public void displayRoomLayout() {

        System.out.println();
        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Room Number : " + roomNumber
        );

        System.out.println(
                "Rows        : " + rows
        );

        System.out.println(
                "Columns     : " + columns
        );

        System.out.println(
                "Capacity    : " + getCapacity()
        );

        System.out.println(
                "Available   : " + getAvailableSeats()
        );

        System.out.println();
        System.out.println("Seat Layout:");

        for (int row = 0; row < rows; row++) {

            for (int column = 0; column < columns; column++) {

                ExamSeat seat = seats[row][column];

                System.out.printf(
                        "%-15s",
                        seat.getSeatNumber()
                );
            }

            System.out.println();
        }

        System.out.println(
                "=============================================================="
        );
    }
}