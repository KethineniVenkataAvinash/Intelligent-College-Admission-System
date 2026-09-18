package com.college.admission.service;

import com.college.admission.jpa.BusJpaEntity;
import com.college.admission.jpa.BusRouteJpaEntity;
import com.college.admission.jpa.TransportAllocationJpaEntity;
import com.college.admission.jpa.TransportJpaRepository;
import com.college.admission.model.Bus;
import com.college.admission.model.BusRoute;
import com.college.admission.model.Student;
import com.college.admission.security.ServiceAuthorization;
import com.college.admission.model.TransportAllocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransportService {

    private final List<BusRoute> routes;
    private final List<Bus> buses;

    private final Map<String, TransportAllocation> allocations;

    private final TransportJpaRepository transportRepository;

    public TransportService() {

        routes = new ArrayList<>();
        buses = new ArrayList<>();
        allocations = new HashMap<>();

        transportRepository =
                new TransportJpaRepository();

        initializeTransportData();
        loadAllocationsFromDatabase();
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    private void initializeTransportData() {

        if (transportRepository.countRoutes() == 0) {

            initializeRoutesInDatabase();
        }

        if (transportRepository.countBuses() == 0) {

            initializeBusesInDatabase();
        }

        loadRoutesFromDatabase();
        loadBusesFromDatabase();
    }

    // ============================================================
    // ROUTE INITIALIZATION
    // ============================================================

    private void initializeRoutesInDatabase() {

        createRoute(
                "RT01",
                "City Centre Route",
                "City Centre",
                "College Campus",
                List.of(
                        "Railway Station",
                        "Main Market",
                        "Bus Stand",
                        "College Campus"
                )
        );

        createRoute(
                "RT02",
                "North City Route",
                "North City",
                "College Campus",
                List.of(
                        "North Square",
                        "University Road",
                        "Tech Park",
                        "College Campus"
                )
        );

        createRoute(
                "RT03",
                "South City Route",
                "South City",
                "College Campus",
                List.of(
                        "South Square",
                        "Central Hospital",
                        "Main Junction",
                        "College Campus"
                )
        );

        createRoute(
                "RT04",
                "East City Route",
                "East City",
                "College Campus",
                List.of(
                        "East Market",
                        "Industrial Area",
                        "East Junction",
                        "College Campus"
                )
        );

        createRoute(
                "RT05",
                "West City Route",
                "West City",
                "College Campus",
                List.of(
                        "West Market",
                        "West Junction",
                        "Old Town",
                        "College Campus"
                )
        );
    }

    private void createRoute(
            String routeCode,
            String routeName,
            String startingPoint,
            String destination,
            List<String> stops
    ) {

        BusRouteJpaEntity route =
                new BusRouteJpaEntity(
                        routeCode,
                        routeName,
                        startingPoint,
                        destination
                );

        route.setStops(
                new ArrayList<>(stops)
        );

        transportRepository.saveRoute(
                route
        );
    }

    // ============================================================
    // BUS INITIALIZATION
    // ============================================================

    private void initializeBusesInDatabase() {

        List<BusRouteJpaEntity> routeEntities =
                transportRepository.findAllRoutes();

        int busCounter = 1;

        for (BusRouteJpaEntity route :
                routeEntities) {

            for (int i = 1; i <= 2; i++) {

                String busNumber =
                        String.format(
                                "BUS%02d",
                                busCounter
                        );

                String registrationNumber =
                        String.format(
                                "COL-2026-%03d",
                                busCounter
                        );

                BusJpaEntity bus =
                        new BusJpaEntity(
                                busNumber,
                                registrationNumber,
                                route,
                                40
                        );

                transportRepository.saveBus(
                        bus
                );

                busCounter++;
            }
        }
    }

    // ============================================================
    // LOAD ROUTES
    // ============================================================

    private void loadRoutesFromDatabase() {

        routes.clear();

        List<BusRouteJpaEntity> entities =
                transportRepository.findAllRoutes();

        for (BusRouteJpaEntity entity :
                entities) {

            BusRoute route =
                    new BusRoute(
                            entity.getRouteCode(),
                            entity.getRouteName(),
                            entity.getStartingPoint(),
                            entity.getDestination()
                    );

            for (String stop :
                    entity.getStops()) {

                route.addStop(stop);
            }

            routes.add(route);
        }
    }

    // ============================================================
    // LOAD BUSES
    // ============================================================

    private void loadBusesFromDatabase() {

        buses.clear();

        List<BusJpaEntity> entities =
                transportRepository.findAllBuses();

        for (BusJpaEntity entity :
                entities) {

            Bus bus =
                    new Bus(
                            entity.getBusNumber(),
                            entity.getRegistrationNumber(),
                            entity.getRoute()
                                    .getRouteCode(),
                            entity.getCapacity()
                    );

            /*
             * Restore occupied seats from
             * persisted allocations.
             */
            long occupied =
                    transportRepository
                            .countAllocationsByBus(
                                    entity.getBusNumber()
                            );

            for (int i = 0; i < occupied; i++) {

                bus.allocateSeat();
            }

            buses.add(bus);
        }
    }

    // ============================================================
    // LOAD ALLOCATIONS
    // ============================================================

    private void loadAllocationsFromDatabase() {

        allocations.clear();

        List<TransportAllocationJpaEntity>
                entities =
                transportRepository
                        .findAllAllocations();

        for (TransportAllocationJpaEntity entity :
                entities) {

            TransportAllocation allocation =
                    convertToModel(entity);

            allocations.put(
                    allocation.getApplicationNumber(),
                    allocation
            );
        }
    }

    private TransportAllocation convertToModel(
            TransportAllocationJpaEntity entity
    ) {

        return new TransportAllocation(
                entity.getApplicationNumber(),
                entity.getStudentName(),
                entity.getRouteCode(),
                entity.getRouteName(),
                entity.getBusNumber(),
                entity.getBusRegistrationNumber(),
                entity.getBoardingPoint(),
                entity.getBusSeatNumber()
        );
    }

    // ============================================================
    // GETTERS
    // ============================================================

    public List<BusRoute> getRoutes() {
        return routes;
    }

    public List<Bus> getBuses() {
        return buses;
    }

    public Map<String, TransportAllocation>
    getAllocations() {

        ServiceAuthorization.requireAdmin();

        loadAllocationsFromDatabase();

        return allocations;
    }

    // ============================================================
    // FIND ROUTE
    // ============================================================

    public BusRoute findRoute(
            String routeCode
    ) {

        if (routeCode == null
                || routeCode.isBlank()) {

            return null;
        }

        for (BusRoute route :
                routes) {

            if (route.getRouteCode()
                    .equalsIgnoreCase(routeCode)) {

                return route;
            }
        }

        return null;
    }

    // ============================================================
    // FIND BUS
    // ============================================================

    public Bus findBus(
            String busNumber
    ) {

        if (busNumber == null
                || busNumber.isBlank()) {

            return null;
        }

        for (Bus bus :
                buses) {

            if (bus.getBusNumber()
                    .equalsIgnoreCase(busNumber)) {

                return bus;
            }
        }

        return null;
    }

    // ============================================================
    // DISPLAY ROUTES
    // ============================================================

    public void displayRoutes() {

        loadRoutesFromDatabase();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                         BUS ROUTES"
        );

        System.out.println(
                "=========================================================================="
        );

        if (routes.isEmpty()) {

            System.out.println(
                    "No bus routes are configured."
            );

            return;
        }

        System.out.printf(
                "%-10s %-25s %-20s %-20s%n",
                "Code",
                "Route",
                "Starting Point",
                "Destination"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (BusRoute route :
                routes) {

            System.out.printf(
                    "%-10s %-25s %-20s %-20s%n",
                    route.getRouteCode(),
                    route.getRouteName(),
                    route.getStartingPoint(),
                    route.getDestination()
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // ============================================================
    // ROUTE DETAILS
    // ============================================================

    public void displayRouteDetails(
            String routeCode
    ) {

        BusRoute route =
                findRoute(routeCode);

        if (route == null) {

            System.out.println(
                    "Route not found."
            );

            return;
        }

        route.displayRoute();
    }

    // ============================================================
    // DISPLAY BUSES
    // ============================================================

    public void displayBuses() {

        loadRoutesFromDatabase();
        loadBusesFromDatabase();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                           BUS DETAILS"
        );

        System.out.println(
                "=========================================================================="
        );

        if (buses.isEmpty()) {

            System.out.println(
                    "No buses are configured."
            );

            return;
        }

        for (Bus bus :
                buses) {

            bus.displayBus();
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // ============================================================
    // BUSES FOR ROUTE
    // ============================================================

    public List<Bus> getBusesForRoute(
            String routeCode
    ) {

        List<Bus> matchingBuses =
                new ArrayList<>();

        if (routeCode == null
                || routeCode.isBlank()) {

            return matchingBuses;
        }

        for (Bus bus :
                buses) {

            if (bus.getRouteCode()
                    .equalsIgnoreCase(routeCode)) {

                matchingBuses.add(bus);
            }
        }

        return matchingBuses;
    }

    // ============================================================
    // AVAILABLE BUSES
    // ============================================================

    public void displayAvailableBuses(
            String routeCode
    ) {

        loadBusesFromDatabase();

        List<Bus> matchingBuses =
                getBusesForRoute(routeCode);

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                    AVAILABLE BUSES"
        );

        System.out.println(
                "=========================================================================="
        );

        if (matchingBuses.isEmpty()) {

            System.out.println(
                    "No buses are assigned to this route."
            );

            return;
        }

        System.out.printf(
                "%-12s %-20s %-12s %-15s%n",
                "Bus Number",
                "Registration",
                "Capacity",
                "Available"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (Bus bus :
                matchingBuses) {

            if (!bus.hasAvailableSeat()) {
                continue;
            }

            System.out.printf(
                    "%-12s %-20s %-12d %-15d%n",
                    bus.getBusNumber(),
                    bus.getRegistrationNumber(),
                    bus.getCapacity(),
                    bus.getAvailableSeats()
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // ============================================================
    // TRANSPORT ALLOCATION
    // ============================================================

    public TransportAllocation allocateTransport(
            Student student,
            String routeCode,
            String boardingPoint
    ) {

        ServiceAuthorization.requireStudentOrAdminOwnership(student);

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (routeCode == null
                || routeCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Route code cannot be empty."
            );
        }

        if (boardingPoint == null
                || boardingPoint.isBlank()) {
            throw new IllegalArgumentException(
                    "Boarding point cannot be empty."
            );
        }

        TransportAllocationJpaEntity allocationEntity =
                transportRepository
                        .allocateSeatTransactionally(
                                student.getApplicationNumber(),
                                student.getName(),
                                routeCode,
                                boardingPoint
                        );

        TransportAllocation allocation =
                convertToModel(allocationEntity);

        allocations.put(
                student.getApplicationNumber(),
                allocation
        );

        loadBusesFromDatabase();

        return allocation;
    }

    private synchronized TransportAllocation allocateTransportLegacy(
            Student student,
            String routeCode,
            String boardingPoint
    ) {

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (routeCode == null
                || routeCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Route code cannot be empty."
            );
        }

        if (boardingPoint == null
                || boardingPoint.isBlank()) {

            throw new IllegalArgumentException(
                    "Boarding point cannot be empty."
            );
        }

        String applicationNumber =
                student.getApplicationNumber();

        /*
         * Database is checked first.
         */
        TransportAllocationJpaEntity
                existingEntity =
                transportRepository
                        .findAllocationByApplication(
                                applicationNumber
                        );

        if (existingEntity != null) {

            TransportAllocation existing =
                    convertToModel(
                            existingEntity
                    );

            allocations.put(
                    applicationNumber,
                    existing
            );

            return existing;
        }

        BusRoute route =
                findRoute(routeCode);

        if (route == null) {

            throw new IllegalArgumentException(
                    "Invalid bus route: "
                            + routeCode
            );
        }

        /*
         * Validate boarding point.
         */
        boolean validBoardingPoint =
                false;

        for (String stop :
                route.getStops()) {

            if (stop.equalsIgnoreCase(
                    boardingPoint
            )) {

                validBoardingPoint = true;
                break;
            }
        }

        if (!validBoardingPoint) {

            throw new IllegalArgumentException(
                    "Selected boarding point does not belong to this route."
            );
        }

        /*
         * Refresh bus occupancy from DB.
         */
        loadBusesFromDatabase();

        List<Bus> routeBuses =
                getBusesForRoute(routeCode);

        routeBuses.sort(
                Comparator.comparingInt(
                        Bus::getAvailableSeats
                ).reversed()
        );

        for (Bus bus :
                routeBuses) {

            if (!bus.hasAvailableSeat()) {
                continue;
            }

            /*
             * The service itself is synchronized.
             * This prevents concurrent calls from
             * selecting the same bus simultaneously.
             */
            int seatNumber =
                    bus.getOccupiedSeats() + 1;

            BusJpaEntity busEntity =
                    transportRepository
                            .findBusByNumber(
                                    bus.getBusNumber()
                            );

            BusRouteJpaEntity routeEntity =
                    transportRepository
                            .findRouteByCode(
                                    routeCode
                            );

            if (busEntity == null) {

                throw new IllegalStateException(
                        "Bus does not exist in database."
                );
            }

            if (routeEntity == null) {

                throw new IllegalStateException(
                        "Route does not exist in database."
                );
            }

            /*
             * Double-check database occupancy.
             */
            long occupied =
                    transportRepository
                            .countAllocationsByBus(
                                    bus.getBusNumber()
                            );

            if (occupied >= bus.getCapacity()) {

                continue;
            }

            seatNumber =
                    (int) occupied + 1;

            TransportAllocation allocation =
                    new TransportAllocation(
                            applicationNumber,
                            student.getName(),
                            route.getRouteCode(),
                            route.getRouteName(),
                            bus.getBusNumber(),
                            bus.getRegistrationNumber(),
                            boardingPoint,
                            seatNumber
                    );

            TransportAllocationJpaEntity
                    allocationEntity =
                    new TransportAllocationJpaEntity(
                            allocation.getApplicationNumber(),
                            allocation.getStudentName(),
                            routeEntity,
                            busEntity,
                            allocation.getRouteCode(),
                            allocation.getRouteName(),
                            allocation.getBusNumber(),
                            allocation.getBusRegistrationNumber(),
                            allocation.getBoardingPoint(),
                            allocation.getBusSeatNumber()
                    );

            try {

                transportRepository.saveAllocation(
                        allocationEntity
                );

            } catch (RuntimeException e) {

                /*
                 * A database unique constraint on
                 * application + bus/seat protects
                 * persistent consistency.
                 */
                throw e;
            }

            allocations.put(
                    applicationNumber,
                    allocation
            );

            /*
             * Refresh local state.
             */
            loadBusesFromDatabase();

            return allocation;
        }

        throw new IllegalStateException(
                "No transport seat is available on the selected route."
        );
    }

    // ============================================================
    // ALLOCATION LOOKUP
    // ============================================================

    public TransportAllocation getAllocation(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }

        TransportAllocationJpaEntity entity =
                transportRepository
                        .findAllocationByApplication(
                                applicationNumber
                        );

        if (entity != null) {

            TransportAllocation allocation =
                    convertToModel(entity);

            allocations.put(
                    applicationNumber,
                    allocation
            );

            return allocation;
        }

        return allocations.get(
                applicationNumber
        );
    }

    public boolean hasAllocation(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return false;
        }

        return transportRepository
                .allocationExists(
                        applicationNumber
                );
    }

    // ============================================================
    // STUDENT ALLOCATION
    // ============================================================

    public void displayStudentAllocation(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            System.out.println(
                    "Student not found."
            );

            return;
        }

        TransportAllocation allocation =
                getAllocation(
                        student.getApplicationNumber()
                );

        System.out.println();

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "                  TRANSPORT DETAILS"
        );

        System.out.println(
                "================================================================"
        );

        if (allocation == null) {

            System.out.println(
                    "Transport has not been allocated."
            );

            System.out.println(
                    "================================================================"
            );

            return;
        }

        allocation.displayAllocation();
    }

    // ============================================================
    // ALL ALLOCATIONS
    // ============================================================

    public void displayAllAllocations() {

        ServiceAuthorization.requireAdmin();

        loadAllocationsFromDatabase();

        System.out.println();

        System.out.println(
                "============================================================================"
        );

        System.out.println(
                "                       TRANSPORT ALLOCATIONS"
        );

        System.out.println(
                "============================================================================"
        );

        if (allocations.isEmpty()) {

            System.out.println(
                    "No transport allocations found."
            );

            return;
        }

        System.out.printf(
                "%-20s %-20s %-10s %-12s %-12s %-8s%n",
                "Application",
                "Student",
                "Route",
                "Bus",
                "Boarding",
                "Seat"
        );

        System.out.println(
                "----------------------------------------------------------------------------"
        );

        for (TransportAllocation allocation :
                allocations.values()) {

            System.out.printf(
                    "%-20s %-20s %-10s %-12s %-12s %-8d%n",
                    allocation.getApplicationNumber(),
                    allocation.getStudentName(),
                    allocation.getRouteCode(),
                    allocation.getBusNumber(),
                    allocation.getBoardingPoint(),
                    allocation.getBusSeatNumber()
            );
        }

        System.out.println(
                "============================================================================"
        );
    }

    // ============================================================
    // ROUTE OCCUPANCY
    // ============================================================

    public void displayRouteOccupancy() {

        ServiceAuthorization.requireAdmin();

        loadBusesFromDatabase();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                       ROUTE-WISE OCCUPANCY"
        );

        System.out.println(
                "=========================================================================="
        );

        for (BusRoute route :
                routes) {

            int totalCapacity = 0;
            int occupiedSeats = 0;

            for (Bus bus :
                    getBusesForRoute(
                            route.getRouteCode()
                    )) {

                totalCapacity +=
                        bus.getCapacity();

                occupiedSeats +=
                        bus.getOccupiedSeats();
            }

            int availableSeats =
                    totalCapacity - occupiedSeats;

            System.out.println();

            System.out.println(
                    "Route Code       : "
                            + route.getRouteCode()
            );

            System.out.println(
                    "Route Name       : "
                            + route.getRouteName()
            );

            System.out.println(
                    "Total Capacity   : "
                            + totalCapacity
            );

            System.out.println(
                    "Occupied Seats   : "
                            + occupiedSeats
            );

            System.out.println(
                    "Available Seats  : "
                            + availableSeats
            );

            System.out.println(
                    "--------------------------------------------------------------------------"
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // ============================================================
    // BUS OCCUPANCY
    // ============================================================

    public void displayBusOccupancy() {

        ServiceAuthorization.requireAdmin();

        loadBusesFromDatabase();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                         BUS OCCUPANCY"
        );

        System.out.println(
                "=========================================================================="
        );

        for (Bus bus :
                buses) {

            System.out.println();

            System.out.println(
                    "Bus Number       : "
                            + bus.getBusNumber()
            );

            System.out.println(
                    "Registration No. : "
                            + bus.getRegistrationNumber()
            );

            System.out.println(
                    "Route Code       : "
                            + bus.getRouteCode()
            );

            System.out.println(
                    "Capacity         : "
                            + bus.getCapacity()
            );

            System.out.println(
                    "Occupied Seats   : "
                            + bus.getOccupiedSeats()
            );

            System.out.println(
                    "Available Seats  : "
                            + bus.getAvailableSeats()
            );

            System.out.println(
                    "Status           : "
                            + (bus.isFull()
                            ? "FULL"
                            : "AVAILABLE")
            );

            System.out.println(
                    "--------------------------------------------------------------------------"
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // ============================================================
    // RANK-BASED TRANSPORT ALLOCATION
    // ============================================================

    public void allocateTransportByRank(
            List<Student> students,
            String routeCode
    ) {

        ServiceAuthorization.requireAdmin();

        if (students == null
                || students.isEmpty()) {

            System.out.println(
                    "No students available for transport allocation."
            );

            return;
        }

        if (routeCode == null
                || routeCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Route code cannot be empty."
            );
        }

        BusRoute route =
                findRoute(routeCode);

        if (route == null) {

            throw new IllegalArgumentException(
                    "Route not found."
            );
        }

        List<Student> rankedStudents =
                new ArrayList<>();

        for (Student student :
                students) {

            if (student != null
                    && student.getRank() != null) {

                rankedStudents.add(student);
            }
        }

        rankedStudents.sort(
                Comparator.comparingInt(
                        Student::getRank
                )
        );

        for (Student student :
                rankedStudents) {

            if (hasAllocation(
                    student.getApplicationNumber()
            )) {

                continue;
            }

            String boardingPoint =
                    route.getStops().isEmpty()
                            ? route.getStartingPoint()
                            : route.getStops().get(0);

            try {

                TransportAllocation allocation =
                        allocateTransport(
                                student,
                                routeCode,
                                boardingPoint
                        );

                System.out.println();

                System.out.println(
                        "Transport allocated to "
                                + student.getName()
                                + " | Rank: "
                                + student.getRank()
                                + " | Bus: "
                                + allocation.getBusNumber()
                                + " | Seat: "
                                + allocation.getBusSeatNumber()
                );

            } catch (IllegalStateException
                     | IllegalArgumentException e) {

                System.out.println();

                System.out.println(
                        "Transport allocation failed for "
                                + student.getName()
                                + " | Rank: "
                                + student.getRank()
                                + " | Reason: "
                                + e.getMessage()
                );
            }
        }
    }

    // ============================================================
    // REPOSITORY ACCESS
    // ============================================================

    public TransportJpaRepository
    getTransportRepository() {

        ServiceAuthorization.requireAdmin();

        return transportRepository;
    }
}