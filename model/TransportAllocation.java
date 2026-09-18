package com.college.admission.model;

public class TransportAllocation {

    private final String applicationNumber;
    private final String studentName;

    private final String routeCode;
    private final String routeName;

    private final String busNumber;
    private final String busRegistrationNumber;

    private final String boardingPoint;
    private final int busSeatNumber;

    public TransportAllocation(
            String applicationNumber,
            String studentName,
            String routeCode,
            String routeName,
            String busNumber,
            String busRegistrationNumber,
            String boardingPoint,
            int busSeatNumber
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

        if (routeCode == null
                || routeCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Route code cannot be empty."
            );
        }

        if (routeName == null
                || routeName.isBlank()) {

            throw new IllegalArgumentException(
                    "Route name cannot be empty."
            );
        }

        if (busNumber == null
                || busNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Bus number cannot be empty."
            );
        }

        if (busRegistrationNumber == null
                || busRegistrationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Bus registration number cannot be empty."
            );
        }

        if (boardingPoint == null
                || boardingPoint.isBlank()) {

            throw new IllegalArgumentException(
                    "Boarding point cannot be empty."
            );
        }

        if (busSeatNumber <= 0) {

            throw new IllegalArgumentException(
                    "Bus seat number must be greater than zero."
            );
        }

        this.applicationNumber = applicationNumber;
        this.studentName = studentName;
        this.routeCode = routeCode;
        this.routeName = routeName;
        this.busNumber = busNumber;
        this.busRegistrationNumber = busRegistrationNumber;
        this.boardingPoint = boardingPoint;
        this.busSeatNumber = busSeatNumber;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getBusRegistrationNumber() {
        return busRegistrationNumber;
    }

    public String getBoardingPoint() {
        return boardingPoint;
    }

    public int getBusSeatNumber() {
        return busSeatNumber;
    }

    public void displayAllocation() {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                 TRANSPORT ALLOCATION"
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

        System.out.println();

        System.out.println(
                "Route Code         : "
                        + routeCode
        );

        System.out.println(
                "Route Name         : "
                        + routeName
        );

        System.out.println(
                "Boarding Point     : "
                        + boardingPoint
        );

        System.out.println();

        System.out.println(
                "Bus Number         : "
                        + busNumber
        );

        System.out.println(
                "Bus Registration   : "
                        + busRegistrationNumber
        );

        System.out.println(
                "Bus Seat Number    : "
                        + busSeatNumber
        );

        System.out.println(
                "=============================================================="
        );
    }

    @Override
    public String toString() {

        return "TransportAllocation{" +
                "applicationNumber='" + applicationNumber + '\'' +
                ", studentName='" + studentName + '\'' +
                ", routeCode='" + routeCode + '\'' +
                ", routeName='" + routeName + '\'' +
                ", busNumber='" + busNumber + '\'' +
                ", busRegistrationNumber='" + busRegistrationNumber + '\'' +
                ", boardingPoint='" + boardingPoint + '\'' +
                ", busSeatNumber=" + busSeatNumber +
                '}';
    }
}