package com.college.admission.integration;

import com.college.admission.jpa.EntityManagerFactoryProvider;
import com.college.admission.jpa.HostelAllocationJpaEntity;
import com.college.admission.jpa.HostelJpaRepository;
import com.college.admission.model.HostelAllocation;
import com.college.admission.model.Student;
import com.college.admission.enums.Category;
import com.college.admission.enums.Gender;
import com.college.admission.service.HostelAllocationTransactionalService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HostelConcurrencyVerificationTest {

    private static final int STUDENT_COUNT = 10;

    private static final String HOSTEL_CODE = "BH01";

    private static final String ROOM_TYPE = "TRIPLE";

    public static void main(String[] args) {

        System.out.println();
        System.out.println(
                "=============================================================="
        );
        System.out.println(
                "        HOSTEL CONCURRENCY VERIFICATION TEST"
        );
        System.out.println(
                "=============================================================="
        );

        try {

            HostelJpaRepository repository =
                    new HostelJpaRepository();

            System.out.println();
            System.out.println(
                    "INITIAL DATABASE STATE"
            );

            System.out.println(
                    "Hostels      : "
                            + repository.countHostels()
            );

            System.out.println(
                    "Rooms        : "
                            + repository.countRooms()
            );

            System.out.println(
                    "Allocations  : "
                            + repository.countAllocations()
            );

            /*
             * ------------------------------------------------------
             * Create independent test students.
             * ------------------------------------------------------
             */

            List<Student> students =
                    createTestStudents();

            System.out.println();
            System.out.println(
                    "Created test students : "
                            + students.size()
            );

            /*
             * ------------------------------------------------------
             * Create thread pool.
             * ------------------------------------------------------
             */

            ExecutorService executor =
                    Executors.newFixedThreadPool(
                            STUDENT_COUNT
                    );

            List<Callable<AllocationResult>> tasks =
                    new ArrayList<>();

            for (Student student : students) {

                tasks.add(() -> {

                    HostelAllocationTransactionalService
                            service =
                            new HostelAllocationTransactionalService();

                    try {

                        HostelAllocation allocation =
                                service.allocateHostel(
                                        student,
                                        HOSTEL_CODE,
                                        ROOM_TYPE
                                );

                        return AllocationResult.success(
                                student,
                                allocation
                        );

                    } catch (Exception e) {

                        return AllocationResult.failure(
                                student,
                                e
                        );
                    }
                });
            }

            /*
             * ------------------------------------------------------
             * Start concurrent allocation.
             * ------------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "Starting "
                            + STUDENT_COUNT
                            + " concurrent hostel allocation requests..."
            );

            long startTime =
                    System.currentTimeMillis();

            List<Future<AllocationResult>> futures =
                    executor.invokeAll(tasks);

            long endTime =
                    System.currentTimeMillis();

            executor.shutdown();

            /*
             * ------------------------------------------------------
             * Collect results.
             * ------------------------------------------------------
             */

            List<AllocationResult> successfulResults =
                    new ArrayList<>();

            List<AllocationResult> failedResults =
                    new ArrayList<>();

            for (Future<AllocationResult> future :
                    futures) {

                try {

                    AllocationResult result =
                            future.get();

                    if (result.isSuccess()) {

                        successfulResults.add(result);

                    } else {

                        failedResults.add(result);
                    }

                } catch (ExecutionException e) {

                    System.out.println(
                            "Unexpected task error: "
                                    + e.getCause()
                    );
                }
            }

            /*
             * ------------------------------------------------------
             * Display results.
             * ------------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "                   ALLOCATION RESULTS"
            );

            System.out.println(
                    "=============================================================="
            );

            for (AllocationResult result :
                    successfulResults) {

                HostelAllocation allocation =
                        result.getAllocation();

                System.out.println(
                        "[SUCCESS] "
                                + result.getStudent()
                                .getApplicationNumber()
                                + " -> "
                                + allocation.getRoomNumber()
                                + " / BED "
                                + allocation.getBedNumber()
                );
            }

            for (AllocationResult result :
                    failedResults) {

                System.out.println(
                        "[EXPECTED/FAILED] "
                                + result.getStudent()
                                .getApplicationNumber()
                                + " -> "
                                + result.getException()
                                .getMessage()
                );
            }

            /*
             * ------------------------------------------------------
             * Verification 1:
             * No duplicate beds.
             * ------------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "CHECK 1: DUPLICATE BED VERIFICATION"
            );

            Set<String> uniqueBeds =
                    new HashSet<>();

            boolean duplicateFound = false;

            for (AllocationResult result :
                    successfulResults) {

                HostelAllocation allocation =
                        result.getAllocation();

                String bedKey =
                        allocation.getHostelCode()
                                + "|"
                                + allocation.getRoomNumber()
                                + "|"
                                + allocation.getBedNumber();

                if (!uniqueBeds.add(bedKey)) {

                    duplicateFound = true;

                    System.out.println(
                            "[FAIL] Duplicate bed detected: "
                                    + bedKey
                    );
                }
            }

            if (!duplicateFound) {

                System.out.println(
                        "[PASS] No duplicate beds detected."
                );
            }

            /*
             * ------------------------------------------------------
             * Verification 2:
             * No duplicate student allocation.
             * ------------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "CHECK 2: DUPLICATE STUDENT VERIFICATION"
            );

            Set<String> uniqueApplications =
                    new HashSet<>();

            boolean duplicateStudent =
                    false;

            for (AllocationResult result :
                    successfulResults) {

                String applicationNumber =
                        result.getStudent()
                                .getApplicationNumber();

                if (!uniqueApplications.add(
                        applicationNumber
                )) {

                    duplicateStudent = true;

                    System.out.println(
                            "[FAIL] Student allocated twice: "
                                    + applicationNumber
                    );
                }
            }

            if (!duplicateStudent) {

                System.out.println(
                        "[PASS] No student received multiple allocations."
                );
            }

            /*
             * ------------------------------------------------------
             * Verification 3:
             * Database count.
             * ------------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "CHECK 3: DATABASE ALLOCATION COUNT"
            );

            long databaseAllocationCount =
                    repository.countAllocations();

            System.out.println(
                    "Successful requests : "
                            + successfulResults.size()
            );

            System.out.println(
                    "Database allocations : "
                            + databaseAllocationCount
            );

            /*
             * ------------------------------------------------------
             * Verification 4:
             * Verify each successful allocation exists in DB.
             * ------------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "CHECK 4: DATABASE PERSISTENCE"
            );

            boolean persistenceCorrect = true;

            for (AllocationResult result :
                    successfulResults) {

                String applicationNumber =
                        result.getStudent()
                                .getApplicationNumber();

                HostelAllocationJpaEntity entity =
                        repository
                                .findAllocationByApplication(
                                        applicationNumber
                                );

                if (entity == null) {

                    persistenceCorrect = false;

                    System.out.println(
                            "[FAIL] Allocation missing from DB: "
                                    + applicationNumber
                    );
                }
            }

            if (persistenceCorrect) {

                System.out.println(
                        "[PASS] All successful allocations "
                                + "exist in the database."
                );
            }

            /*
             * ------------------------------------------------------
             * Verification 5:
             * Capacity.
             * ------------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "CHECK 5: HOSTEL CAPACITY"
            );

            List<HostelAllocationJpaEntity>
                    allocations =
                    repository.findAllAllocations();

            long bh01TripleBeds =
                    allocations.stream()
                            .filter(
                                    allocation ->
                                            allocation
                                                    .getHostelCode()
                                                    .equalsIgnoreCase(
                                                            HOSTEL_CODE
                                                    )
                            )
                            .filter(
                                    allocation ->
                                            allocation
                                                    .getRoomType()
                                                    .equalsIgnoreCase(
                                                            ROOM_TYPE
                                                    )
                            )
                            .count();

            /*
             * BH01 has:
             *
             * A-203 -> TRIPLE
             * A-204 -> TRIPLE
             *
             * Total capacity = 6 beds.
             */

            int expectedMaximumCapacity = 6;

            System.out.println(
                    "BH01 TRIPLE allocations : "
                            + bh01TripleBeds
            );

            System.out.println(
                    "Maximum expected capacity : "
                            + expectedMaximumCapacity
            );

            if (bh01TripleBeds <= expectedMaximumCapacity) {

                System.out.println(
                        "[PASS] Hostel capacity was not exceeded."
                );

            } else {

                System.out.println(
                        "[FAIL] Hostel capacity was exceeded."
                );
            }

            /*
             * ------------------------------------------------------
             * Summary
             * ------------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "                  TEST SUMMARY"
            );

            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "Concurrent requests : "
                            + STUDENT_COUNT
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

            boolean overallPass =
                    !duplicateFound
                            && !duplicateStudent
                            && persistenceCorrect
                            && bh01TripleBeds
                            <= expectedMaximumCapacity;

            System.out.println();

            if (overallPass) {

                System.out.println(
                        "=============================================================="
                );

                System.out.println(
                        "       HOSTEL CONCURRENCY TEST PASSED"
                );

                System.out.println(
                        "=============================================================="
                );

            } else {

                System.out.println(
                        "=============================================================="
                );

                System.out.println(
                        "       HOSTEL CONCURRENCY TEST FAILED"
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
                    "       HOSTEL CONCURRENCY TEST ERROR"
            );

            System.out.println(
                    "=============================================================="
            );

            e.printStackTrace();

        } finally {

            EntityManagerFactoryProvider.close();
        }
    }

    // ============================================================
    // CREATE TEST STUDENTS
    // ============================================================

    private static List<Student> createTestStudents() {

        List<Student> students =
                new ArrayList<>();

        for (int i = 1;
             i <= STUDENT_COUNT;
             i++) {

            Student student =
                    new Student(
                            null,
                            "Hostel Test Student " + i,
                            "hosteltest"
                                    + i
                                    + "@example.com",
                            "90000000"
                                    + String.format(
                                            "%02d",
                                            i
                                    ),
                            "HOSTELTEST"
                                    + String.format(
                                            "%02d",
                                            i
                                    ),
                            LocalDate.of(
                                    2005,
                                    1,
                                    1
                            ),
                            Gender.MALE,
                            Category.GENERAL,
                            "Test Address",
                            80.0 + i
                    );

            student.setRank(i);

            students.add(student);
        }

        return students;
    }

    // ============================================================
    // RESULT CLASS
    // ============================================================

    private static class AllocationResult {

        private final Student student;

        private final HostelAllocation allocation;

        private final Exception exception;

        private final boolean success;

        private AllocationResult(
                Student student,
                HostelAllocation allocation,
                Exception exception,
                boolean success
        ) {

            this.student = student;
            this.allocation = allocation;
            this.exception = exception;
            this.success = success;
        }

        public static AllocationResult success(
                Student student,
                HostelAllocation allocation
        ) {

            return new AllocationResult(
                    student,
                    allocation,
                    null,
                    true
            );
        }

        public static AllocationResult failure(
                Student student,
                Exception exception
        ) {

            return new AllocationResult(
                    student,
                    null,
                    exception,
                    false
            );
        }

        public Student getStudent() {
            return student;
        }

        public HostelAllocation getAllocation() {
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