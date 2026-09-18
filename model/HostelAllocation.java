package com.college.admission.model;

public class HostelAllocation {

    private final String applicationNumber;
    private final String studentName;

    private final String hostelCode;
    private final String hostelName;
    private final String blockName;

    private final String roomNumber;
    private final String roomType;
    private final int bedNumber;

    public HostelAllocation(
            String applicationNumber,
            String studentName,
            String hostelCode,
            String hostelName,
            String blockName,
            String roomNumber,
            String roomType,
            int bedNumber
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        if (studentName == null
                || studentName.isBlank()) {

            throw new IllegalArgumentException(
                    "Student name cannot be empty."
            );
        }

        if (hostelCode == null
                || hostelCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Hostel code cannot be empty."
            );
        }

        if (hostelName == null
                || hostelName.isBlank()) {

            throw new IllegalArgumentException(
                    "Hostel name cannot be empty."
            );
        }

        if (blockName == null
                || blockName.isBlank()) {

            throw new IllegalArgumentException(
                    "Block name cannot be empty."
            );
        }

        if (roomNumber == null
                || roomNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Room number cannot be empty."
            );
        }

        if (roomType == null
                || roomType.isBlank()) {

            throw new IllegalArgumentException(
                    "Room type cannot be empty."
            );
        }

        if (bedNumber <= 0) {

            throw new IllegalArgumentException(
                    "Bed number must be greater than zero."
            );
        }

        this.applicationNumber = applicationNumber;
        this.studentName = studentName;

        this.hostelCode = hostelCode;
        this.hostelName = hostelName;
        this.blockName = blockName;

        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.bedNumber = bedNumber;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getHostelCode() {
        return hostelCode;
    }

    public String getHostelName() {
        return hostelName;
    }

    public String getBlockName() {
        return blockName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getBedNumber() {
        return bedNumber;
    }

    public void displayAllocation() {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                  HOSTEL ALLOCATION"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Student Name       : "
                        + studentName
        );

        System.out.println(
                "Application Number : "
                        + applicationNumber
        );

        System.out.println(
                "Hostel Code        : "
                        + hostelCode
        );

        System.out.println(
                "Hostel Name        : "
                        + hostelName
        );

        System.out.println(
                "Block              : "
                        + blockName
        );

        System.out.println(
                "Room Number        : "
                        + roomNumber
        );

        System.out.println(
                "Room Type          : "
                        + roomType
        );

        System.out.println(
                "Bed Number         : "
                        + bedNumber
        );

        System.out.println(
                "=============================================================="
        );
    }

    @Override
    public String toString() {

        return "HostelAllocation{" +
                "applicationNumber='" +
                applicationNumber + '\'' +
                ", studentName='" +
                studentName + '\'' +
                ", hostelCode='" +
                hostelCode + '\'' +
                ", hostelName='" +
                hostelName + '\'' +
                ", blockName='" +
                blockName + '\'' +
                ", roomNumber='" +
                roomNumber + '\'' +
                ", roomType='" +
                roomType + '\'' +
                ", bedNumber=" +
                bedNumber +
                '}';
    }
}