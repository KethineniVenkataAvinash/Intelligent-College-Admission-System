package com.college.admission.service;

import com.college.admission.enums.ApplicationStatus;
import com.college.admission.enums.CounsellingStatus;
import com.college.admission.jpa.ApplicationJpaRepository;
import com.college.admission.jpa.EntityManagerFactoryProvider;
import com.college.admission.jpa.SeatAllocationJpaRepository;
import com.college.admission.model.Application;
import com.college.admission.model.SeatAllocation;
import com.college.admission.model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for concurrent seat allocation.
 */
public class ConcurrentAllocationTest {

    public static void main(String[] args) {

        ApplicationJpaRepository applicationRepository =
                new ApplicationJpaRepository();

        SeatAllocationJpaRepository allocationRepository =
                new SeatAllocationJpaRepository();

        CourseService courseService =
                new CourseService();

        ConcurrentAllocationService
                concurrentAllocationService =
                new ConcurrentAllocationService();

        try {

            System.out.println();
            System.out.println(
                    "=============================================================="
            );
            System.out.println(
                    "             CONCURRENT ALLOCATION TEST"
            );
            System.out.println(
                    "=============================================================="
            );

            /*
             * Demonstrate Runnable.
             */
            System.out.println();
            System.out.println(
                    "Testing Runnable..."
            );

            concurrentAllocationService
                    .demonstrateRunnable();

            /*
             * Get applications from database.
             */
            List<Application> applications =
                    applicationRepository.findAll();

            if (applications.isEmpty()) {

                System.out.println();
                System.out.println(
                        "No applications found in database."
                );

                System.out.println(
                        "Create applications first."
                );

                return;
            }

            /*
             * Convert applications into students.
             *
             * Only valid applications are selected.
             */
            List<Student> students =
                    new ArrayList<>();

            for (Application application :
                    applications) {

                if (application == null) {
                    continue;
                }

                Student student =
                        application.getStudent();

                if (student == null) {
                    continue;
                }

                /*
                 * Only students who are:
                 *
                 * ELIGIBLE
                 * + have rank
                 * + REGISTERED for counselling
                 */
                if (application.getStatus()
                        == ApplicationStatus.ELIGIBLE
                        && student.getRank() != null
                        && application
                        .getCounsellingStatus()
                        == CounsellingStatus.REGISTERED) {

                    /*
                     * Skip students who already have
                     * an allocation.
                     */
                    SeatAllocation existing =
                            allocationRepository
                                    .findByApplicationNumber(
                                            application
                                                    .getApplicationNumber()
                                    );

                    if (existing == null) {

                        students.add(student);
                    }
                }

                /*
                 * Limit the concurrency demonstration
                 * to 10 students.
                 */
                if (students.size() >= 10) {
                    break;
                }
            }

            if (students.isEmpty()) {

                System.out.println();
                System.out.println(
                        "No eligible unallocated students found."
                );

                System.out.println(
                        "Register students for counselling "
                                + "before running this test."
                );

                return;
            }

            /*
             * Display students selected for the test.
             */
            System.out.println();
            System.out.println(
                    "Students selected for concurrent allocation:"
            );

            for (Student student : students) {

                System.out.println(
                        "Application: "
                                + student
                                .getApplicationNumber()
                                + " | Name: "
                                + student.getName()
                                + " | Rank: "
                                + student.getRank()
                );
            }

            /*
             * Start concurrent allocation.
             */
            System.out.println();
            System.out.println(
                    "Starting concurrent allocation..."
            );

            long startTime =
                    System.currentTimeMillis();

            concurrentAllocationService
                    .allocateConcurrently(
                            students,
                            courseService
                    );

            long endTime =
                    System.currentTimeMillis();

            /*
             * Execution time.
             */
            System.out.println();
            System.out.println(
                    "Execution time: "
                            + (endTime - startTime)
                            + " ms"
            );

            /*
             * Verify results from database.
             */
            System.out.println();
            System.out.println(
                    "=============================================================="
            );
            System.out.println(
                    "             DATABASE VERIFICATION"
            );
            System.out.println(
                    "=============================================================="
            );

            int allocatedCount = 0;

            for (Student student :
                    students) {

                SeatAllocation allocation =
                        allocationRepository
                                .findByApplicationNumber(
                                        student
                                                .getApplicationNumber()
                                );

                if (allocation != null) {

                    allocatedCount++;

                    System.out.println(
                            "Application: "
                                    + student
                                    .getApplicationNumber()
                                    + " | Seat: "
                                    + allocation
                                    .getSeatNumber()
                                    + " | Course: "
                                    + allocation
                                    .getCourseCode()
                    );
                }
            }

            System.out.println();
            System.out.println(
                    "Total successful allocations: "
                            + allocatedCount
            );

            /*
             * Final test result.
             */
            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            if (allocatedCount > 0) {

                System.out.println(
                        "CONCURRENT ALLOCATION TEST: PASSED"
                );

            } else {

                System.out.println(
                        "CONCURRENT ALLOCATION TEST: "
                                + "NO ALLOCATIONS CREATED"
                );
            }

            System.out.println(
                    "=============================================================="
            );

        } finally {

            EntityManagerFactoryProvider.close();
        }
    }
}