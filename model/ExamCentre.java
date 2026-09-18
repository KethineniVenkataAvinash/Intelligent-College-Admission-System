package com.college.admission.model;

import java.util.ArrayList;
import java.util.List;

public class ExamCentre {

    private final String centreCode;
    private final String centreName;
    private final String address;
    private final int totalCapacity;

    private final List<ExamRoom> rooms;

    public ExamCentre(
            String centreCode,
            String centreName,
            String address,
            int totalCapacity
    ) {

        if (centreCode == null || centreCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Centre code cannot be empty."
            );
        }

        if (centreName == null || centreName.isBlank()) {
            throw new IllegalArgumentException(
                    "Centre name cannot be empty."
            );
        }

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException(
                    "Centre address cannot be empty."
            );
        }

        if (totalCapacity <= 0) {
            throw new IllegalArgumentException(
                    "Centre capacity must be greater than zero."
            );
        }

        this.centreCode = centreCode;
        this.centreName = centreName;
        this.address = address;
        this.totalCapacity = totalCapacity;

        this.rooms = new ArrayList<>();
    }

    public String getCentreCode() {
        return centreCode;
    }

    public String getCentreName() {
        return centreName;
    }

    public String getAddress() {
        return address;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public List<ExamRoom> getRooms() {
        return rooms;
    }

    public void addRoom(ExamRoom room) {

        if (room == null) {
            throw new IllegalArgumentException(
                    "Room cannot be null."
            );
        }

        rooms.add(room);
    }

    public int getTotalRoomCapacity() {

        int capacity = 0;

        for (ExamRoom room : rooms) {
            capacity += room.getCapacity();
        }

        return capacity;
    }

    public int getAvailableSeats() {

        int available = 0;

        for (ExamRoom room : rooms) {
            available += room.getAvailableSeats();
        }

        return available;
    }

    public boolean hasAvailableSeat() {
        return getAvailableSeats() > 0;
    }

    public void displayCentre() {

        System.out.println();
        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Exam Centre Code : " + centreCode
        );

        System.out.println(
                "Exam Centre Name : " + centreName
        );

        System.out.println(
                "Address          : " + address
        );

        System.out.println(
                "Total Capacity   : " + totalCapacity
        );

        System.out.println(
                "Room Capacity    : " + getTotalRoomCapacity()
        );

        System.out.println(
                "Available Seats  : " + getAvailableSeats()
        );

        System.out.println(
                "=============================================================="
        );
    }
}