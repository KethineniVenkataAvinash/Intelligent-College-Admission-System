package com.college.admission.model;

import java.util.ArrayList;
import java.util.List;

public class BusRoute {

    private final String routeCode;
    private final String routeName;
    private final String startingPoint;
    private final String destination;
    private final List<String> stops;

    public BusRoute(
            String routeCode,
            String routeName,
            String startingPoint,
            String destination
    ) {
        if (routeCode == null || routeCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Route code cannot be empty."
            );
        }

        if (routeName == null || routeName.isBlank()) {
            throw new IllegalArgumentException(
                    "Route name cannot be empty."
            );
        }

        if (startingPoint == null || startingPoint.isBlank()) {
            throw new IllegalArgumentException(
                    "Starting point cannot be empty."
            );
        }

        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException(
                    "Destination cannot be empty."
            );
        }

        this.routeCode = routeCode;
        this.routeName = routeName;
        this.startingPoint = startingPoint;
        this.destination = destination;
        this.stops = new ArrayList<>();
    }

    public String getRouteCode() {
        return routeCode;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public String getDestination() {
        return destination;
    }

    public List<String> getStops() {
        return stops;
    }

    public void addStop(String stop) {

        if (stop == null || stop.isBlank()) {
            throw new IllegalArgumentException(
                    "Bus stop cannot be empty."
            );
        }

        stops.add(stop);
    }

    public int getNumberOfStops() {
        return stops.size();
    }

    public void displayRoute() {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                     BUS ROUTE"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Route Code       : " + routeCode
        );

        System.out.println(
                "Route Name       : " + routeName
        );

        System.out.println(
                "Starting Point   : " + startingPoint
        );

        System.out.println(
                "Destination      : " + destination
        );

        System.out.println(
                "Number of Stops  : " + getNumberOfStops()
        );

        System.out.println();

        System.out.println("Stops:");

        if (stops.isEmpty()) {

            System.out.println(
                    "No intermediate stops configured."
            );

        } else {

            int position = 1;

            for (String stop : stops) {

                System.out.println(
                        "  " + position + ". " + stop
                );

                position++;
            }
        }

        System.out.println(
                "=============================================================="
        );
    }

    @Override
    public String toString() {

        return "BusRoute{" +
                "routeCode='" + routeCode + '\'' +
                ", routeName='" + routeName + '\'' +
                ", startingPoint='" + startingPoint + '\'' +
                ", destination='" + destination + '\'' +
                ", stops=" + stops +
                '}';
    }
}