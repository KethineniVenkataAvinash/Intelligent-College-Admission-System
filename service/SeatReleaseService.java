package com.college.admission.service;

import com.college.admission.security.ServiceAuthorization;
import com.college.admission.enums.ApplicationStatus;
import com.college.admission.enums.CounsellingStatus;
import com.college.admission.jpa.EntityManagerFactoryProvider;
import com.college.admission.model.Application;
import com.college.admission.model.Course;
import com.college.admission.model.Seat;
import com.college.admission.model.SeatAllocation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

public class SeatReleaseService {

    private final WaitlistPromotionService
            waitlistPromotionService;

    public SeatReleaseService() {

        waitlistPromotionService =
                new WaitlistPromotionService();
    }

    /**
     * Releases an allocated seat.
     *
     * After successful release, the system attempts
     * to promote the next waitlisted student.
     */
    public void releaseSeat(
            String applicationNumber
    ) {

        ServiceAuthorization.requireAdmin();

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        String normalizedApplicationNumber =
                applicationNumber.trim();

        EntityManager em =
                EntityManagerFactoryProvider
                        .getEntityManagerFactory()
                        .createEntityManager();

        EntityTransaction transaction =
                em.getTransaction();

        try {

            transaction.begin();

            /*
             * Lock application.
             */
            Application application =
                    em.find(
                            Application.class,
                            normalizedApplicationNumber,
                            LockModeType.PESSIMISTIC_WRITE
                    );

            if (application == null) {

                throw new IllegalStateException(
                        "Application not found."
                );
            }

            /*
             * Find allocation.
             */
            SeatAllocation allocation =
                    em.createQuery(
                            """
                            SELECT sa
                            FROM SeatAllocation sa
                            WHERE sa.application.applicationNumber =
                                  :applicationNumber
                            """,
                            SeatAllocation.class
                    )
                    .setParameter(
                            "applicationNumber",
                            normalizedApplicationNumber
                    )
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (allocation == null) {

                throw new IllegalStateException(
                        "No seat allocation found."
                );
            }

            /*
             * Lock seat.
             */
            String seatNumber =
                    allocation
                            .getSeat()
                            .getSeatNumber();

            Seat seat =
                    em.find(
                            Seat.class,
                            seatNumber,
                            LockModeType.PESSIMISTIC_WRITE
                    );

            if (seat == null) {

                throw new IllegalStateException(
                        "Allocated seat not found."
                );
            }

            /*
             * Lock course.
             */
            String courseCode =
                    seat.getCourse()
                            .getCourseCode();

            Course course =
                    em.find(
                            Course.class,
                            courseCode,
                            LockModeType.PESSIMISTIC_WRITE
                    );

            if (course == null) {

                throw new IllegalStateException(
                        "Course not found."
                );
            }

            /*
             * Release physical seat.
             */
            seat.release();

            /*
             * Restore course capacity.
             */
            course.releaseSeat();

            /*
             * Remove allocation.
             */
            em.remove(allocation);

            /*
             * Reset application.
             */
            application.setStatus(
                    ApplicationStatus.ELIGIBLE
            );

            application.setCounsellingStatus(
                    CounsellingStatus.REGISTERED
            );

            application.setWaitlistPosition(
                    null
            );

            /*
             * Commit release.
             */
            transaction.commit();

            System.out.println();

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "           SEAT RELEASED"
            );

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "Application No. : "
                            + normalizedApplicationNumber
            );

            System.out.println(
                    "Seat Number     : "
                            + seatNumber
            );

            System.out.println(
                    "Course          : "
                            + courseCode
            );

            System.out.println(
                    "Available Seats : "
                            + course.getAvailableSeats()
            );

            System.out.println(
                    "======================================"
            );

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;

        } finally {

            em.close();
        }

        /*
         * Try promotion only after the release
         * transaction has successfully committed.
         */
        promoteNextStudent();
    }

    /**
     * Attempts to promote the next waitlisted student.
     */
    private void promoteNextStudent() {

        try {

            waitlistPromotionService
                    .promoteNextStudent();

        } catch (Exception e) {

            /*
             * Seat release itself has already succeeded.
             * Promotion failure must not undo a completed
             * release transaction.
             */
            System.out.println();

            System.out.println(
                    "Automatic waitlist promotion "
                            + "could not be completed."
            );

            System.out.println(
                    "Reason: "
                            + e.getMessage()
            );
        }
    }
}