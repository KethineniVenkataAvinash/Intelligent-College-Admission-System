package com.college.admission.service;

import com.college.admission.enums.ApplicationStatus;
import com.college.admission.enums.CounsellingStatus;
import com.college.admission.jpa.ApplicationJpaRepository;
import com.college.admission.jpa.EntityManagerFactoryProvider;
import com.college.admission.jpa.SeatAllocationJpaRepository;
import com.college.admission.jpa.SeatJpaRepository;
import com.college.admission.model.Application;
import com.college.admission.model.Seat;
import com.college.admission.model.SeatAllocation;
import com.college.admission.model.Student;

import java.util.List;

public class SeatAllocationRollbackTest {

    public static void main(String[] args) {

        ApplicationJpaRepository applicationRepository =
                new ApplicationJpaRepository();

        SeatAllocationJpaRepository allocationRepository =
                new SeatAllocationJpaRepository();

        SeatJpaRepository seatRepository =
                new SeatJpaRepository();

        try {

            System.out.println();
            System.out.println(
                    "=============================================================="
            );
            System.out.println(
                    "          SEAT ALLOCATION ROLLBACK TEST"
            );
            System.out.println(
                    "=============================================================="
            );

            /*
             * Find an eligible student who is registered
             * for counselling and has no allocation.
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
                                        application
                                                .getApplicationNumber()
                                );

                if (existing == null) {

                    testStudent = student;
                    break;
                }
            }

            if (testStudent == null) {

                System.out.println();
                System.out.println(
                        "No suitable student found."
                );

                System.out.println(
                        "Required:"
                                + " eligible + ranked + "
                                + "counselling registered + "
                                + "no existing allocation."
                );

                return;
            }

            String applicationNumber =
                    testStudent.getApplicationNumber();

            System.out.println();
            System.out.println(
                    "Test Student : "
                            + testStudent.getName()
            );

            System.out.println(
                    "Application  : "
                            + applicationNumber
            );

            /*
             * --------------------------------------------------
             * Record the initial state.
             * --------------------------------------------------
             */

            SeatAllocation initialAllocation =
                    allocationRepository
                            .findByApplicationNumber(
                                    applicationNumber
                            );

            System.out.println();
            System.out.println(
                    "Initial allocation : "
                            + (initialAllocation == null
                            ? "NONE"
                            : initialAllocation
                            .getSeatNumber())
            );

            /*
             * --------------------------------------------------
             * IMPORTANT
             *
             * We need to force a failure AFTER an allocation
             * has started.
             *
             * The transaction service itself currently commits
             * a successful allocation, so this test demonstrates
             * rollback using a direct JPA transaction.
             * --------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "--------------------------------------------------------------"
            );
            System.out.println(
                    "TEST: FORCED ROLLBACK"
            );
            System.out.println(
                    "--------------------------------------------------------------"
            );

            jakarta.persistence.EntityManager entityManager =
                    EntityManagerFactoryProvider
                            .getEntityManagerFactory()
                            .createEntityManager();

            jakarta.persistence.EntityTransaction transaction =
                    entityManager.getTransaction();

            Seat testSeat = null;

            try {

                transaction.begin();

                /*
                 * Find an available seat belonging to one of
                 * the student's preferences.
                 */
                List<com.college.admission.model.CoursePreference>
                        preferences =
                        testStudent
                                .getApplication()
                                .getPreferences();

                if (preferences.isEmpty()) {

                    throw new IllegalStateException(
                            "Student has no course preferences."
                    );
                }

                String courseCode =
                        preferences
                                .get(0)
                                .getCourse()
                                .getCourseCode();

                /*
                 * Find an available seat and lock it.
                 */
                List<Seat> seats =
                        entityManager.createQuery(
                                """
                                SELECT s
                                FROM Seat s
                                WHERE s.course.courseCode =
                                      :courseCode
                                  AND s.category =
                                      :category
                                  AND s.status =
                                      com.college.admission.enums.SeatStatus.AVAILABLE
                                ORDER BY s.seatNumber
                                """,
                                Seat.class
                        )
                        .setParameter(
                                "courseCode",
                                courseCode
                        )
                        .setParameter(
                                "category",
                                testStudent.getCategory()
                        )
                        .setMaxResults(1)
                        .setLockMode(
                                jakarta.persistence.LockModeType
                                        .PESSIMISTIC_WRITE
                        )
                        .getResultList();

                if (seats.isEmpty()) {

                    throw new IllegalStateException(
                            "No available seat found for rollback test."
                    );
                }

                testSeat = seats.get(0);

                String originalSeatStatus =
                        testSeat.getStatus().name();

                String originalApplicationNumber =
                        testSeat.getApplicationNumber();

                /*
                 * Temporarily allocate the seat.
                 */
                testSeat.allocate(
                        applicationNumber
                );

                System.out.println(
                        "Seat temporarily allocated : "
                                + testSeat.getSeatNumber()
                );

                System.out.println(
                        "Original seat status        : "
                                + originalSeatStatus
                );

                /*
                 * Force an error.
                 *
                 * This exception occurs before COMMIT.
                 */
                throw new RuntimeException(
                        "INTENTIONAL FAILURE FOR ROLLBACK TEST"
                );

            } catch (RuntimeException e) {

                /*
                 * Roll back the transaction.
                 */
                if (transaction.isActive()) {

                    transaction.rollback();
                }

                System.out.println();
                System.out.println(
                        "Intentional exception generated."
                );

                System.out.println(
                        "Transaction : ROLLED BACK"
                );

            } finally {

                entityManager.close();
            }

            /*
             * --------------------------------------------------
             * VERIFY SEAT AFTER ROLLBACK
             * --------------------------------------------------
             */

            if (testSeat != null) {

                Seat databaseSeat =
                        seatRepository
                                .findBySeatNumber(
                                        testSeat
                                                .getSeatNumber()
                                );

                if (databaseSeat != null
                        && databaseSeat
                        .isAvailable()
                        && databaseSeat
                        .getApplicationNumber()
                        == null) {

                    System.out.println();
                    System.out.println(
                            "Seat rollback verification: PASSED"
                    );

                } else {

                    System.out.println();
                    System.out.println(
                            "Seat rollback verification: FAILED"
                    );
                }
            }

            /*
             * --------------------------------------------------
             * VERIFY NO ALLOCATION WAS CREATED
             * --------------------------------------------------
             */

            SeatAllocation allocationAfterRollback =
                    allocationRepository
                            .findByApplicationNumber(
                                    applicationNumber
                            );

            if (allocationAfterRollback == null) {

                System.out.println(
                        "Allocation rollback verification: PASSED"
                );

            } else {

                System.out.println(
                        "Allocation rollback verification: FAILED"
                );

                System.out.println(
                        "Unexpected allocation: "
                                + allocationAfterRollback
                                .getSeatNumber()
                );
            }

            /*
             * --------------------------------------------------
             * VERIFY APPLICATION WAS NOT CHANGED
             * --------------------------------------------------
             */

            Application applicationAfterRollback =
                    applicationRepository
                            .findByApplicationNumber(
                                    applicationNumber
                            );

            if (applicationAfterRollback != null
                    && applicationAfterRollback
                    .getStatus()
                    == ApplicationStatus.ELIGIBLE
                    && applicationAfterRollback
                    .getCounsellingStatus()
                    == CounsellingStatus.REGISTERED) {

                System.out.println(
                        "Application rollback verification: PASSED"
                );

            } else {

                System.out.println(
                        "Application rollback verification: FAILED"
                );
            }

            /*
             * --------------------------------------------------
             * FINAL RESULT
             * --------------------------------------------------
             */

            boolean allocationRemoved =
                    allocationAfterRollback == null;

            boolean applicationRestored =
                    applicationAfterRollback != null
                            && applicationAfterRollback
                            .getStatus()
                            == ApplicationStatus.ELIGIBLE
                            && applicationAfterRollback
                            .getCounsellingStatus()
                            == CounsellingStatus.REGISTERED;

            boolean seatRestored =
                    testSeat == null
                            || (
                            seatRepository
                                    .findBySeatNumber(
                                            testSeat
                                                    .getSeatNumber()
                                    ) != null
                                    && seatRepository
                                    .findBySeatNumber(
                                            testSeat
                                                    .getSeatNumber()
                                    )
                                    .isAvailable()
                                    && seatRepository
                                    .findBySeatNumber(
                                            testSeat
                                                    .getSeatNumber()
                                    )
                                    .getApplicationNumber()
                                    == null
                    );

            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            if (allocationRemoved
                    && applicationRestored
                    && seatRestored) {

                System.out.println(
                        "ROLLBACK TEST: PASSED"
                );

            } else {

                System.out.println(
                        "ROLLBACK TEST: FAILED"
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