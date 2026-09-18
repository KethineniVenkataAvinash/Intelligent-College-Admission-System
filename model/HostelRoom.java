package com.college.admission.model;

import java.util.ArrayList;
import java.util.List;

public class HostelRoom {

    private final String roomNumber;
    private final String roomType;
    private final int capacity;

    private final List<String> allocatedApplications;

    public HostelRoom(
            String roomNumber,
            String roomType,
            int capacity
    ) {

        if (roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Room number cannot be empty."
            );
        }

        if (roomType == null || roomType.isBlank()) {
            throw new IllegalArgumentException(
                    "Room type cannot be empty."
            );
        }

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Room capacity must be greater than zero."
            );
        }

        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity;

        this.allocatedApplications =
                new ArrayList<>();
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<String> getAllocatedApplications() {
        return allocatedApplications;
    }

    public int getOccupiedBeds() {
        return allocatedApplications.size();
    }

    public int getAvailableBeds() {
        return capacity - getOccupiedBeds();
    }

    public boolean hasAvailableBed() {
        return getAvailableBeds() > 0;
    }

    public boolean isFull() {
        return getOccupiedBeds() >= capacity;
    }

    public void allocateBed(
            String applicationNumber
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        if (allocatedApplications.contains(
                applicationNumber
        )) {

            throw new IllegalStateException(
                    "Student is already allocated to this room."
            );
        }

        if (isFull()) {

            throw new IllegalStateException(
                    "Room " + roomNumber
                            + " is already full."
            );
        }

        allocatedApplications.add(
                applicationNumber
        );
    }

    public void releaseBed(
            String applicationNumber
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        if (!allocatedApplications.remove(
                applicationNumber
        )) {

            throw new IllegalStateException(
                    "Student is not allocated to room "
                            + roomNumber
            );
        }
    }

    public boolean hasStudent(
            String applicationNumber
    ) {

        return allocatedApplications.contains(
                applicationNumber
        );
    }

    public void displayRoom() {

        System.out.println();

        System.out.println(
                "------------------------------------------------------------"
        );

        System.out.println(
                "Room Number       : "
                        + roomNumber
        );

        System.out.println(
                "Room Type         : "
                        + roomType
        );

        System.out.println(
                "Total Beds        : "
                        + capacity
        );

        System.out.println(
                "Occupied Beds     : "
                        + getOccupiedBeds()
        );

        System.out.println(
                "Available Beds    : "
                        + getAvailableBeds()
        );

        System.out.println(
                "Status             : "
                        + (isFull()
                        ? "FULL"
                        : "AVAILABLE")
        );

        System.out.println(
                "------------------------------------------------------------"
        );
    }

    @Override
    public String toString() {

        return "HostelRoom{" +
                "roomNumber='" +
                roomNumber + '\'' +
                ", roomType='" +
                roomType + '\'' +
                ", capacity=" +
                capacity +
                ", occupiedBeds=" +
                getOccupiedBeds() +
                ", availableBeds=" +
                getAvailableBeds() +
                '}';
    }
}