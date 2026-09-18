package com.college.admission.model;

import com.college.admission.enums.Gender;

import java.util.ArrayList;
import java.util.List;

public class Hostel {

    private final String hostelCode;
    private final String hostelName;
    private final Gender gender;
    private final String blockName;

    private final List<HostelRoom> rooms;

    public Hostel(
            String hostelCode,
            String hostelName,
            Gender gender,
            String blockName
    ) {

        if (hostelCode == null || hostelCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Hostel code cannot be empty."
            );
        }

        if (hostelName == null || hostelName.isBlank()) {
            throw new IllegalArgumentException(
                    "Hostel name cannot be empty."
            );
        }

        if (gender == null) {
            throw new IllegalArgumentException(
                    "Hostel gender cannot be null."
            );
        }

        if (blockName == null || blockName.isBlank()) {
            throw new IllegalArgumentException(
                    "Block name cannot be empty."
            );
        }

        this.hostelCode = hostelCode;
        this.hostelName = hostelName;
        this.gender = gender;
        this.blockName = blockName;

        this.rooms = new ArrayList<>();
    }

    public String getHostelCode() {
        return hostelCode;
    }

    public String getHostelName() {
        return hostelName;
    }

    public Gender getGender() {
        return gender;
    }

    public String getBlockName() {
        return blockName;
    }

    public List<HostelRoom> getRooms() {
        return rooms;
    }

    public void addRoom(HostelRoom room) {

        if (room == null) {
            throw new IllegalArgumentException(
                    "Hostel room cannot be null."
            );
        }

        rooms.add(room);
    }

    public int getTotalCapacity() {

        int capacity = 0;

        for (HostelRoom room : rooms) {
            capacity += room.getCapacity();
        }

        return capacity;
    }

    public int getOccupiedCapacity() {

        int occupied = 0;

        for (HostelRoom room : rooms) {
            occupied += room.getOccupiedBeds();
        }

        return occupied;
    }

    public int getAvailableCapacity() {

        return getTotalCapacity()
                - getOccupiedCapacity();
    }

    public boolean hasAvailableBed() {

        return getAvailableCapacity() > 0;
    }

    public void displayHostel() {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Hostel Code       : "
                        + hostelCode
        );

        System.out.println(
                "Hostel Name       : "
                        + hostelName
        );

        System.out.println(
                "Gender            : "
                        + gender
        );

        System.out.println(
                "Block             : "
                        + blockName
        );

        System.out.println(
                "Total Capacity    : "
                        + getTotalCapacity()
        );

        System.out.println(
                "Occupied Beds     : "
                        + getOccupiedCapacity()
        );

        System.out.println(
                "Available Beds    : "
                        + getAvailableCapacity()
        );

        System.out.println(
                "=============================================================="
        );
    }

    @Override
    public String toString() {

        return "Hostel{" +
                "hostelCode='" +
                hostelCode + '\'' +
                ", hostelName='" +
                hostelName + '\'' +
                ", gender=" +
                gender +
                ", blockName='" +
                blockName + '\'' +
                ", totalCapacity=" +
                getTotalCapacity() +
                ", occupiedBeds=" +
                getOccupiedCapacity() +
                ", availableBeds=" +
                getAvailableCapacity() +
                '}';
    }
}