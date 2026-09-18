package com.college.admission.model;

public class Bus {

    private final String busNumber;
    private final String registrationNumber;
    private final String routeCode;
    private final int capacity;

    private int occupiedSeats;

    public Bus(
            String busNumber,
            String registrationNumber,
            String routeCode,
            int capacity
    ) {
        if (busNumber == null || busNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Bus number cannot be empty."
            );
        }

        if (registrationNumber == null
                || registrationNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Registration number cannot be empty."
            );
        }

        if (routeCode == null || routeCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Route code cannot be empty."
            );
        }

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Bus capacity must be greater than zero."
            );
        }

        this.busNumber = busNumber;
        this.registrationNumber = registrationNumber;
        this.routeCode = routeCode;
        this.capacity = capacity;
        this.occupiedSeats = 0;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupiedSeats() {
        return occupiedSeats;
    }

    public int getAvailableSeats() {
        return capacity - occupiedSeats;
    }

    public boolean hasAvailableSeat() {
        return occupiedSeats < capacity;
    }

    public boolean isFull() {
        return occupiedSeats >= capacity;
    }

    public synchronized void allocateSeat() {

        if (!hasAvailableSeat()) {
            throw new IllegalStateException(
                    "Bus " + busNumber + " is already full."
            );
        }

        occupiedSeats++;
    }

    public synchronized void releaseSeat() {

        if (occupiedSeats <= 0) {
            throw new IllegalStateException(
                    "No occupied seats available to release."
            );
        }

        occupiedSeats--;
    }

    public void displayBus() {

        System.out.println();

        System.out.println(
                "------------------------------------------------------------"
        );

        System.out.println(
                "Bus Number        : " + busNumber
        );

        System.out.println(
                "Registration No.  : " + registrationNumber
        );

        System.out.println(
                "Route Code        : " + routeCode
        );

        System.out.println(
                "Total Capacity    : " + capacity
        );

        System.out.println(
                "Occupied Seats    : " + occupiedSeats
        );

        System.out.println(
                "Available Seats   : " + getAvailableSeats()
        );

        System.out.println(
                "Status            : "
                        + (isFull() ? "FULL" : "AVAILABLE")
        );

        System.out.println(
                "------------------------------------------------------------"
        );
    }

    @Override
    public String toString() {

        return "Bus{" +
                "busNumber='" + busNumber + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", routeCode='" + routeCode + '\'' +
                ", capacity=" + capacity +
                ", occupiedSeats=" + occupiedSeats +
                ", availableSeats=" + getAvailableSeats() +
                '}';
    }
}