package com.college.admission.integration;

import com.college.admission.enums.Gender;
import com.college.admission.jpa.BusJpaEntity;
import com.college.admission.jpa.BusRouteJpaEntity;
import com.college.admission.jpa.EntityManagerFactoryProvider;
import com.college.admission.jpa.TransportAllocationJpaEntity;
import com.college.admission.jpa.TransportJpaRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TransportConcurrencyVerificationTest {

    private static final int REQUEST_COUNT = 10;

    public static void main(String[] args) {

        System.out.println();
        System.out.println(
                "=============================================================="
        );
        System.out.println(
                "       TRANSPORT CONCURRENCY VERIFICATION TEST"
        );
        System.out.println(
                "=============================================================="
        );

        TransportJpaRepository repository =
                new TransportJpaRepository();

        ExecutorService executor = null;

        try {

            /*
             * ======================================================
             * STEP 1: DATABASE STATE
             * ======================================================
             */

            System.out.println();
            System.out.println(
                    "INITIAL DATABASE STATE"
            );

            System.out.println(
                    "Routes       : "
                            + repository.countRoutes()
            );

            System.out.println(
                    "Buses        : "
                            + repository.countBuses()
            );

            System.out.println(
                    "Allocations  : "
                            + repository.countAllocations()
            );

            /*
             * ======================================================
             * STEP 2: FIND A ROUTE WITH AVAILABLE CAPACITY
             * ======================================================
             */

            BusRouteJpaEntity selectedRoute =
                    findRouteWithCapacity(
                            repository
                    );

            if (selectedRoute == null) {

                System.out.println();
                System.out.println(
                        "[ERROR] No route has enough available "
                                + "capacity for the concurrency test."
                );

                return;
            }

            String routeCode =
                    selectedRoute.getRouteCode();

            String boardingPoint =
                    findBoardingPoint(
                            selectedRoute
                    );

            if (boardingPoint == null) {

                System.out.println();
                System.out.println(
                        "[ERROR] Selected route has no boarding points."
                );

                return;
            }

            List<BusJpaEntity> buses =
                    repository.findBusesByRoute(
                            routeCode
                    );

            int totalCapacity = 0;

            for (BusJpaEntity bus : buses) {
                totalCapacity += bus.getCapacity();
            }

            System.out.println();
            System.out.println(
                    "SELECTED ROUTE"
            );

            System.out.println(
                    "Route Code       : "
                            + routeCode
            );

            System.out.println(
                    "Route Name       : "
                            + selectedRoute.getRouteName()
            );

            System.out.println(
                    "Boarding Point   : "
                            + boardingPoint
            );

            System.out.println(
                    "Number of Buses  : "
                            + buses.size()
            );

            System.out.println(
                    "Total Capacity   : "
                            + totalCapacity
            );

            /*
             * ======================================================
             * STEP 3: CREATE UNIQUE APPLICATION NUMBERS
             * ======================================================
             */

            List<String> applicationNumbers =
                    new ArrayList<>();

            for (int i = 1;
                 i <= REQUEST_COUNT;
                 i++) {

                applicationNumbers.add(
                        "TRANSPORTTEST"
                                + System.currentTimeMillis()
                                + "_"
                                + i
                );
            }

            /*
             * ======================================================
             * STEP 4: CREATE THREAD POOL
             * ======================================================
             */

            executor =
                    Executors.newFixedThreadPool(
                            REQUEST_COUNT
                    );

            List<Callable<TransportResult>> tasks =
                    new ArrayList<>();

            for (int i = 0;
                 i < REQUEST_COUNT;
                 i++) {

                final int index = i;

                tasks.add(() -> {

                    TransportJpaRepository
                            threadRepository =
                            new TransportJpaRepository();

                    String applicationNumber =
                            applicationNumbers.get(index);

                    String studentName =
                            "Transport Test Student "
                                    + (index + 1);

                    try {

                        TransportAllocationJpaEntity
                                allocation =
                                threadRepository
                                        .allocateSeatTransactionally(
                                                applicationNumber,
                                                studentName,
                                                routeCode,
                                                boardingPoint
                                        );

                        return TransportResult.success(
                                applicationNumber,
                                studentName,
                                allocation
                        );

                    } catch (Exception e) {

                        return TransportResult.failure(
                                applicationNumber,
                                studentName,
                                e
                        );
                    }
                });
            }

            /*
             * ======================================================
             * STEP 5: START CONCURRENT REQUESTS
             * ======================================================
             */

            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "STARTING "
                            + REQUEST_COUNT
                            + " CONCURRENT TRANSPORT REQUESTS"
            );

            System.out.println(
                    "=============================================================="
            );

            long startTime =
                    System.currentTimeMillis();

            List<Future<TransportResult>> futures =
                    executor.invokeAll(tasks);

            long endTime =
                    System.currentTimeMillis();

            /*
             * ======================================================
             * STEP 6: COLLECT RESULTS
             * ======================================================
             */

            List<TransportResult> successfulResults =
                    new ArrayList<>();

            List<TransportResult> failedResults =
                    new ArrayList<>();

            for (Future<TransportResult> future :
                    futures) {

                try {

                    TransportResult result =
                            future.get();

                    if (result.isSuccess()) {

                        successfulResults.add(result);

                    } else {

                        failedResults.add(result);
                    }

                } catch (ExecutionException e) {

                    System.out.println(
                            "[ERROR] Worker thread failed: "
                                    + e.getCause()
                    );
                }
            }

            /*
             * ======================================================
             * STEP 7: DISPLAY SUCCESSFUL ALLOCATIONS
             * ======================================================
             */

            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "             SUCCESSFUL TRANSPORT ALLOCATIONS"
            );

            System.out.println(
                    "=============================================================="
            );

            for (TransportResult result :
                    successfulResults) {

                TransportAllocationJpaEntity allocation =
                        result.getAllocation();

                System.out.println(
                        "[SUCCESS] "
                                + result.getApplicationNumber()
                                + " -> Bus "
                                + allocation.getBusNumber()
                                + " / Seat "
                                + allocation.getBusSeatNumber()
                );
            }

            /*
             * ======================================================
             * STEP 8: DISPLAY FAILED REQUESTS
             * ======================================================
             */

            if (!failedResults.isEmpty()) {

                System.out.println();
                System.out.println(
                        "FAILED REQUESTS"
                );

                for (TransportResult result :
                        failedResults) {

                    System.out.println(
                            "[EXPECTED/FAILED] "
                                    + result
                                    .getApplicationNumber()
                                    + " -> "
                                    + result
                                    .getException()
                                    .getMessage()
                    );
                }
            }

            /*
             * ======================================================
             * CHECK 1: DUPLICATE BUS SEATS
             * ======================================================
             */

            System.out.println();
            System.out.println(
                    "CHECK 1: DUPLICATE BUS SEATS"
            );

            Set<String> uniqueBusSeats =
                    new HashSet<>();

            boolean duplicateSeatFound =
                    false;

            for (TransportResult result :
                    successfulResults) {

                TransportAllocationJpaEntity allocation =
                        result.getAllocation();

                String seatKey =
                        allocation.getBusNumber()
                                + "|"
                                + allocation.getBusSeatNumber();

                if (!uniqueBusSeats.add(seatKey)) {

                    duplicateSeatFound = true;

                    System.out.println(
                            "[FAIL] Duplicate bus seat: "
                                    + seatKey
                    );
                }
            }

            if (!duplicateSeatFound) {

                System.out.println(
                        "[PASS] No duplicate bus seats detected."
                );
            }

            /*
             * ======================================================
             * CHECK 2: DUPLICATE STUDENTS
             * ======================================================
             */

            System.out.println();
            System.out.println(
                    "CHECK 2: DUPLICATE STUDENT ALLOCATIONS"
            );

            Set<String> uniqueApplications =
                    new HashSet<>();

            boolean duplicateApplication =
                    false;

            for (TransportResult result :
                    successfulResults) {

                if (!uniqueApplications.add(
                        result.getApplicationNumber()
                )) {

                    duplicateApplication = true;

                    System.out.println(
                            "[FAIL] Student received "
                                    + "multiple allocations: "
                                    + result
                                    .getApplicationNumber()
                    );
                }
            }

            if (!duplicateApplication) {

                System.out.println(
                        "[PASS] No student received multiple allocations."
                );
            }

            /*
             * ======================================================
             * CHECK 3: DATABASE PERSISTENCE
             * ======================================================
             */

            System.out.println();
            System.out.println(
                    "CHECK 3: DATABASE PERSISTENCE"
            );

            boolean persistenceCorrect =
                    true;

            for (TransportResult result :
                    successfulResults) {

                TransportAllocationJpaEntity
                        databaseAllocation =
                        repository
                                .findAllocationByApplication(
                                        result
                                                .getApplicationNumber()
                                );

                if (databaseAllocation == null) {

                    persistenceCorrect = false;

                    System.out.println(
                            "[FAIL] Allocation missing from DB: "
                                    + result
                                    .getApplicationNumber()
                    );
                }
            }

            if (persistenceCorrect) {

                System.out.println(
                        "[PASS] All successful allocations "
                                + "are persisted in MySQL."
                );
            }

            /*
             * ======================================================
             * CHECK 4: BUS CAPACITY
             * ======================================================
             */

            System.out.println();
            System.out.println(
                    "CHECK 4: BUS CAPACITY"
            );

            boolean capacityCorrect =
                    true;

            for (BusJpaEntity bus :
                    buses) {

                long occupied =
                        repository
                                .countAllocationsByBus(
                                        bus.getBusNumber()
                                );

                System.out.println(
                        "Bus "
                                + bus.getBusNumber()
                                + " : "
                                + occupied
                                + " / "
                                + bus.getCapacity()
                );

                if (occupied >
                        bus.getCapacity()) {

                    capacityCorrect = false;

                    System.out.println(
                            "[FAIL] Capacity exceeded for bus "
                                    + bus.getBusNumber()
                    );
                }
            }

            if (capacityCorrect) {

                System.out.println(
                        "[PASS] No bus capacity was exceeded."
                );
            }

            /*
             * ======================================================
             * CHECK 5: DATABASE UNIQUE CONSTRAINT
             * ======================================================
             */

            System.out.println();
            System.out.println(
                    "CHECK 5: DATABASE ALLOCATION COUNT"
            );

            long databaseCount =
                    repository.countAllocations();

            System.out.println(
                    "Successful test requests : "
                            + successfulResults.size()
            );

            System.out.println(
                    "Total DB allocations     : "
                            + databaseCount
            );

            /*
             * ======================================================
             * FINAL RESULT
             * ======================================================
             */

            boolean overallPass =
                    !duplicateSeatFound
                            && !duplicateApplication
                            && persistenceCorrect
                            && capacityCorrect;

            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "                    TEST SUMMARY"
            );

            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "Concurrent requests : "
                            + REQUEST_COUNT
            );

            System.out.println(
                    "Successful          : "
                            + successfulResults.size()
            );

            System.out.println(
                    "Failed              : "
                            + failedResults.size()
            );

            System.out.println(
                    "Execution time      : "
                            + (endTime - startTime)
                            + " ms"
            );

            System.out.println();

            if (overallPass) {

                System.out.println(
                        "=============================================================="
                );

                System.out.println(
                        "       TRANSPORT CONCURRENCY TEST PASSED"
                );

                System.out.println(
                        "=============================================================="
                );

            } else {

                System.out.println(
                        "=============================================================="
                );

                System.out.println(
                        "       TRANSPORT CONCURRENCY TEST FAILED"
                );

                System.out.println(
                        "=============================================================="
                );
            }

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "       TRANSPORT TEST ERROR"
            );

            System.out.println(
                    "=============================================================="
            );

            e.printStackTrace();

        } finally {

            if (executor != null
                    && !executor.isShutdown()) {

                executor.shutdown();
            }

            EntityManagerFactoryProvider.close();
        }
    }

    // ============================================================
    // FIND ROUTE WITH AVAILABLE CAPACITY
    // ============================================================

    private static BusRouteJpaEntity
    findRouteWithCapacity(
            TransportJpaRepository repository
    ) {

        List<BusRouteJpaEntity> routes =
                repository.findAllRoutes();

        for (BusRouteJpaEntity route :
                routes) {

            List<BusJpaEntity> buses =
                    repository.findBusesByRoute(
                            route.getRouteCode()
                    );

            int availableCapacity = 0;

            for (BusJpaEntity bus :
                    buses) {

                long occupied =
                        repository
                                .countAllocationsByBus(
                                        bus.getBusNumber()
                                );

                availableCapacity +=
                        bus.getCapacity()
                                - (int) occupied;
            }

            if (availableCapacity >=
                    REQUEST_COUNT) {

                return route;
            }
        }

        /*
         * If no route has ten free seats,
         * return the first route so the test
         * can still demonstrate capacity handling.
         */
        return routes.isEmpty()
                ? null
                : routes.get(0);
    }

    // ============================================================
    // FIND BOARDING POINT
    // ============================================================

    private static String findBoardingPoint(
            BusRouteJpaEntity route
    ) {

        if (route.getStops() == null
                || route.getStops().isEmpty()) {

            return null;
        }

        return route.getStops().get(0);
    }

    // ============================================================
    // RESULT CLASS
    // ============================================================

    private static class TransportResult {

        private final String applicationNumber;

        private final String studentName;

        private final TransportAllocationJpaEntity allocation;

        private final Exception exception;

        private final boolean success;

        private TransportResult(
                String applicationNumber,
                String studentName,
                TransportAllocationJpaEntity allocation,
                Exception exception,
                boolean success
        ) {

            this.applicationNumber =
                    applicationNumber;

            this.studentName =
                    studentName;

            this.allocation =
                    allocation;

            this.exception =
                    exception;

            this.success =
                    success;
        }

        public static TransportResult success(
                String applicationNumber,
                String studentName,
                TransportAllocationJpaEntity allocation
        ) {

            return new TransportResult(
                    applicationNumber,
                    studentName,
                    allocation,
                    null,
                    true
            );
        }

        public static TransportResult failure(
                String applicationNumber,
                String studentName,
                Exception exception
        ) {

            return new TransportResult(
                    applicationNumber,
                    studentName,
                    null,
                    exception,
                    false
            );
        }

        public String getApplicationNumber() {
            return applicationNumber;
        }

        public String getStudentName() {
            return studentName;
        }

        public TransportAllocationJpaEntity
        getAllocation() {

            return allocation;
        }

        public Exception getException() {
            return exception;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}