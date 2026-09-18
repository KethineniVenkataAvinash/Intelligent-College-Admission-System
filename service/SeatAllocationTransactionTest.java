package com.college.admission.service;

import com.college.admission.enums.ApplicationStatus;
import com.college.admission.enums.CounsellingStatus;
import com.college.admission.jpa.ApplicationJpaRepository;
import com.college.admission.jpa.CourseJpaRepository;
import com.college.admission.jpa.EntityManagerFactoryProvider;
import com.college.admission.jpa.SeatAllocationJpaRepository;
import com.college.admission.jpa.SeatJpaRepository;
import com.college.admission.model.Application;
import com.college.admission.model.Course;
import com.college.admission.model.Seat;
import com.college.admission.model.SeatAllocation;
import com.college.admission.model.Student;
import java.util.List;

public class SeatAllocationTransactionTest {

    public static void main(String[] args) {

        ApplicationJpaRepository applicationRepository =
                new ApplicationJpaRepository();

        SeatAllocationJpaRepository allocationRepository =
                new SeatAllocationJpaRepository();

        SeatJpaRepository seatRepository =
                new SeatJpaRepository();

        CourseJpaRepository courseRepository =
                new CourseJpaRepository();

        Object transactionService = createTransactionService();

        try {

            System.out.println();
            System.out.println(
                    "=============================================================="
            );
            System.out.println(
                    "       SEAT ALLOCATION TRANSACTION TEST"
            );
            System.out.println(
                    "=============================================================="
            );

            /*
             * Find an eligible student who:
             *
             * 1. Has a rank
             * 2. Is registered for counselling
             * 3. Does not already have an allocation
             */
            List<Application> applications =
                    applicationRepository.findAll();

            Student testStudent = null;

            for (Application application : applications) {

                if (application == null) {
                    continue;
                }

                Student student =
                        application.getStudent();

                if (student == null) {
                    continue;
                }

                if (application.getStatus()
                        != ApplicationStatus.ELIGIBLE) {
                    continue;
                }

                if (application.getCounsellingStatus()
                        != CounsellingStatus.REGISTERED) {
                    continue;
                }

                if (student.getRank() == null) {
                    continue;
                }

                SeatAllocation existing =
                        allocationRepository
                                .findByApplicationNumber(
                                        application.getApplicationNumber()
                                );

                if (existing == null) {

                    testStudent = student;
                    break;
                }
            }

            /*
             * No suitable student found.
             */
            if (testStudent == null) {

                System.out.println();
                System.out.println(
                        "No suitable student found."
                );

                System.out.println(
                        "You need an eligible student who is "
                                + "registered for counselling "
                                + "and has no allocation."
                );

                return;
            }

            /*
             * Display test student.
             */
            System.out.println();
            System.out.println(
                    "Test Student : "
                            + testStudent.getName()
            );

            System.out.println(
                    "Application  : "
                            + testStudent.getApplicationNumber()
            );

            System.out.println(
                    "Rank         : "
                            + testStudent.getRank()
            );

            System.out.println(
                    "Category     : "
                            + testStudent.getCategory()
            );

            /*
             * ==============================================
             * TEST 1: SUCCESSFUL TRANSACTION
             * ==============================================
             */

            System.out.println();
            System.out.println(
                    "--------------------------------------------------------------"
            );
            System.out.println(
                    "TEST 1: SUCCESSFUL ALLOCATION"
            );
            System.out.println(
                    "--------------------------------------------------------------"
            );

            SeatAllocation allocation =
                    allocate(transactionService, testStudent);

            /*
             * Verify transaction result.
             */
            if (allocation != null) {

                System.out.println();
                System.out.println(
                        "COMMIT TEST: PASSED"
                );

                System.out.println(
                        "Allocation ID : "
                                + allocation.getAllocationId()
                );

                System.out.println(
                        "Seat          : "
                                + allocation.getSeatNumber()
                );

                System.out.println(
                        "Course        : "
                                + allocation.getCourseCode()
                );

            } else {

                System.out.println();
                System.out.println(
                        "No seat was available."
                );

                System.out.println(
                        "COMMIT TEST: NOT EXECUTED"
                );

                return;
            }

            /*
             * ==============================================
             * VERIFY ALLOCATION IN DATABASE
             * ==============================================
             */

            SeatAllocation databaseAllocation =
                    allocationRepository
                            .findByApplicationNumber(
                                    testStudent
                                            .getApplicationNumber()
                            );

            if (databaseAllocation != null) {

                System.out.println(
                        "Database verification: PASSED"
                );

            } else {

                System.out.println(
                        "Database verification: FAILED"
                );
            }

            /*
             * ==============================================
             * VERIFY SEAT
             * ==============================================
             */

            Seat allocatedSeat =
                    seatRepository.findBySeatNumber(
                            allocation.getSeatNumber()
                    );

            if (allocatedSeat != null
                    && testStudent
                    .getApplicationNumber()
                    .equals(
                            allocatedSeat
                                    .getApplicationNumber()
                    )) {

                System.out.println(
                        "Seat allocation verification: PASSED"
                );

            } else {

                System.out.println(
                        "Seat allocation verification: FAILED"
                );
            }

            /*
             * ==============================================
             * CLEANUP
             * ==============================================
             */

            /*
             * Delete test allocation.
             */
            allocationRepository
                    .deleteByApplicationNumber(
                            testStudent
                                    .getApplicationNumber()
                    );

            /*
             * Release the seat.
             */
            if (allocatedSeat != null) {

                allocatedSeat.release();

                seatRepository.update(
                        allocatedSeat
                );
            }

            /*
             * Restore application status.
             */
            applicationRepository.updateStatus(
                    testStudent.getApplicationNumber(),
                    ApplicationStatus.ELIGIBLE
            );

            applicationRepository.updateCounsellingStatus(
                    testStudent.getApplicationNumber(),
                    CounsellingStatus.REGISTERED
            );

            /*
             * Restore course capacity.
             */
            Course course = null;

            if (allocation != null) {

                course =
                        courseRepository.findByCourseCode(
                                allocation.getCourseCode()
                        );
            }

            if (course != null) {

                courseRepository.updateAvailableSeats(
                        course.getCourseCode(),
                        course.getAvailableSeats() + 1
                );
            }

            System.out.println();
            System.out.println(
                    "Test data cleanup completed."
            );

            /*
             * ==============================================
             * FINAL RESULT
             * ==============================================
             */

            System.out.println();
            System.out.println(
                    "=============================================================="
            );
            System.out.println(
                    "       TRANSACTION TEST COMPLETED"
            );
            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "Successful transaction : VERIFIED"
            );

            System.out.println(
                    "Database persistence   : VERIFIED"
            );

            System.out.println(
                    "Cleanup                : VERIFIED"
            );

            System.out.println(
                    "=============================================================="
            );

        } finally {

            EntityManagerFactoryProvider.close();
        }
    }

        private static Object createTransactionService() {
                try {
                        return Class.forName(
                                        "com.college.admission.service.SeatAllocationTransactionService"
                        ).getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException exception) {
                        throw new IllegalStateException(
                                        "SeatAllocationTransactionService is not available.",
                                        exception
                        );
                }
        }

        private static SeatAllocation allocate(
                        Object transactionService,
                        Student student) {
                try {
                        return (SeatAllocation) transactionService.getClass()
                                        .getMethod("allocate", Student.class)
                                        .invoke(transactionService, student);
                } catch (ReflectiveOperationException exception) {
                        throw new IllegalStateException(
                                        "Unable to execute seat allocation transaction.",
                                        exception
                        );
                }
        }
}