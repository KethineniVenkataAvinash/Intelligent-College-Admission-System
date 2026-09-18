package com.college.admission.model;

public class ExamSeat {

    private final String seatNumber;
    private final int row;
    private final int column;

    private boolean available;
    private String applicationNumber;

    public ExamSeat(
            String seatNumber,
            int row,
            int column
    ) {

        this.seatNumber = seatNumber;
        this.row = row;
        this.column = column;

        this.available = true;
        this.applicationNumber = null;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void allocate(
            String applicationNumber
    ) {

        if (!available) {
            throw new IllegalStateException(
                    "Exam seat " + seatNumber
                            + " is already occupied."
            );
        }

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number is required."
            );
        }

        this.applicationNumber =
                applicationNumber;

        this.available = false;
    }

    public void release() {

        this.applicationNumber = null;
        this.available = true;
    }

    @Override
    public String toString() {

        return seatNumber
                + " | Row: "
                + row
                + " | Column: "
                + column
                + " | Status: "
                + (available
                ? "AVAILABLE"
                : "ALLOCATED");
    }
}