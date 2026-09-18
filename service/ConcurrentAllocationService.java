package com.college.admission.service;

import com.college.admission.enums.ApplicationStatus;
import com.college.admission.enums.CounsellingStatus;
import com.college.admission.model.Application;
import com.college.admission.model.SeatAllocation;
import com.college.admission.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentAllocationService {

    /*
     * Responsible for the complete database transaction.
     */
    private final SeatAllocationTransactionalService
            transactionalService;

    public ConcurrentAllocationService() {

        transactionalService =
                new SeatAllocationTransactionalService();
    }

    /**
     * Allocates seats concurrently to multiple students.
     *
     * Demonstrates:
     * - ExecutorService
     * - Callable
     * - Future
     * - Multiple threads
     * - Concurrent execution
     */
    public void allocateConcurrently(
            List<Student> students,
            CourseService courseService
    ) {

        if (students == null || students.isEmpty()) {

            System.out.println(
                    "No students available for concurrent allocation."
            );

            return;
        }

        if (courseService == null) {

            throw new IllegalArgumentException(
                    "CourseService cannot be null."
            );
        }

        /*
         * Create a thread pool.
         *
         * We do not create more threads than students.
         */
        int threadCount =
                Math.min(
                        students.size(),
                        Runtime.getRuntime()
                                .availableProcessors()
                );

        threadCount =
                Math.max(threadCount, 1);

        ExecutorService executor =
                Executors.newFixedThreadPool(
                        threadCount
                );

        try {

            List<Callable<String>> tasks =
                    new ArrayList<>();

            /*
             * Create one task for every student.
             */
            for (Student student : students) {

                if (student == null) {
                    continue;
                }

                tasks.add(
                        () -> allocateStudent(student)
                );
            }

            /*
             * Execute all tasks concurrently.
             */
            List<Future<String>> results =
                    executor.invokeAll(tasks);

            /*
             * Display results.
             */
            System.out.println();
            System.out.println(
                    "=============================================================="
            );
            System.out.println(
                    "             CONCURRENT ALLOCATION RESULTS"
            );
            System.out.println(
                    "=============================================================="
            );

            for (Future<String> result :
                    results) {

                try {

                    System.out.println(
                            result.get()
                    );

                } catch (ExecutionException e) {

                    Throwable cause =
                            e.getCause();

                    String message =
                            cause == null
                                    ? "Unknown error"
                                    : cause.getMessage();

                    System.out.println(
                            "Allocation task failed: "
                                    + message
                    );
                }
            }

            System.out.println(
                    "=============================================================="
            );

        } catch (InterruptedException e) {

            /*
             * Restore interrupted status.
             */
            Thread.currentThread().interrupt();

            System.out.println(
                    "Concurrent allocation was interrupted."
            );

        } finally {

            /*
             * Stop accepting new tasks.
             */
            executor.shutdown();
        }
    }

    /**
     * Performs allocation for one student.
     *
     * This method runs inside a worker thread.
     */
    private String allocateStudent(
            Student student
    ) {

        String applicationNumber =
                student.getApplicationNumber();

        String threadName =
                Thread.currentThread()
                        .getName();

        Application application =
                student.getApplication();

        /*
         * ----------------------------------------------
         * VALIDATION
         * ----------------------------------------------
         */

        if (application == null) {

            return threadName
                    + " | "
                    + applicationNumber
                    + " | FAILED: Application not found.";
        }

        /*
         * Check eligibility.
         */
        if (application.getStatus()
                != ApplicationStatus.ELIGIBLE) {

            return threadName
                    + " | "
                    + applicationNumber
                    + " | SKIPPED: Student is not eligible.";
        }

        /*
         * Check rank.
         */
        if (student.getRank() == null) {

            return threadName
                    + " | "
                    + applicationNumber
                    + " | SKIPPED: Rank not generated.";
        }

        /*
         * Check counselling registration.
         */
        if (application.getCounsellingStatus()
                != CounsellingStatus.REGISTERED) {

            return threadName
                    + " | "
                    + applicationNumber
                    + " | SKIPPED: Counselling not registered.";
        }

        /*
         * ----------------------------------------------
         * TRANSACTIONAL ALLOCATION
         * ----------------------------------------------
         *
         * The complete operation is handled by:
         *
         * SeatAllocationTransactionalService
         *
         * It handles:
         *
         * - Application locking
         * - Course locking
         * - Seat locking
         * - Seat allocation
         * - Course capacity
         * - SeatAllocation persistence
         * - Application status
         * - COMMIT
         * - ROLLBACK
         */
        try {

            SeatAllocation allocation =
                    transactionalService.allocate(
                            applicationNumber
                    );

            /*
             * No seat available.
             */
            if (allocation == null) {

                return threadName
                        + " | "
                        + applicationNumber
                        + " | WAITLISTED: No available seat.";
            }

            /*
             * Successful allocation.
             */
            return threadName
                    + " | "
                    + applicationNumber
                    + " | ALLOCATED"
                    + " | Course: "
                    + allocation.getCourseCode()
                    + " | Seat: "
                    + allocation.getSeatNumber();

        } catch (RuntimeException e) {

            /*
             * Transactional service has already
             * performed rollback.
             */
            return threadName
                    + " | "
                    + applicationNumber
                    + " | FAILED"
                    + " | "
                    + e.getMessage();
        }
    }

    /**
     * Demonstrates a simple Runnable thread.
     */
    public void demonstrateRunnable() {

        Runnable task = () -> {

            System.out.println(
                    "Runnable executed by: "
                            + Thread.currentThread()
                            .getName()
            );
        };

        Thread thread =
                new Thread(
                        task,
                        "Admission-Runnable-Thread"
                );

        thread.start();

        try {

            thread.join();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "Runnable demonstration interrupted."
            );
        }
    }
}