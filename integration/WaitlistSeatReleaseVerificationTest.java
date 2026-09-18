package com.college.admission.integration;

import com.college.admission.jpa.EntityManagerFactoryProvider;
import com.college.admission.model.Application;
import com.college.admission.model.SeatAllocation;
import com.college.admission.service.SeatReleaseService;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Scanner;

public class WaitlistSeatReleaseVerificationTest {

    private static final Scanner scanner =
            new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println();
        System.out.println(
                "=============================================================="
        );
        System.out.println(
                "       WAITLIST + SEAT RELEASE VERIFICATION"
        );
        System.out.println(
                "=============================================================="
        );

        try {

            showCurrentState();

            System.out.println();
            System.out.println(
                    "--------------------------------------------------------------"
            );

            String applicationNumber =
                    readApplicationNumber();

            if (applicationNumber == null) {
                return;
            }

            /*
             * ------------------------------------------------------
             * STEP 1: Verify allocation before release
             * ------------------------------------------------------
             */

            SeatAllocation allocationBefore =
                    findAllocation(applicationNumber);

            if (allocationBefore == null) {

                System.out.println();
                System.out.println(
                        "ERROR: No seat allocation found for:"
                );

                System.out.println(
                        "Application : "
                                + applicationNumber
                );

                System.out.println();
                System.out.println(
                        "Please enter an application number "
                                + "which currently has an allocated seat."
                );

                return;
            }

            System.out.println();
            System.out.println(
                    "ALLOCATED STUDENT FOUND"
            );

            System.out.println(
                    "Application : "
                            + allocationBefore
                            .getApplicationNumber()
            );

            System.out.println(
                    "Student     : "
                            + allocationBefore
                            .getStudentName()
            );

            System.out.println(
                    "Course      : "
                            + allocationBefore
                            .getCourseCode()
            );

            System.out.println(
                    "Seat        : "
                            + allocationBefore
                            .getSeatNumber()
            );

            /*
             * ------------------------------------------------------
             * STEP 2: Find current first waitlisted student
             * ------------------------------------------------------
             */

            Application firstWaitlisted =
                    findFirstWaitlistedApplication();

            if (firstWaitlisted != null) {

                System.out.println();
                System.out.println(
                        "FIRST WAITLISTED STUDENT"
                );

                System.out.println(
                        "Application : "
                                + firstWaitlisted
                                .getApplicationNumber()
                );

                if (firstWaitlisted.getStudent() != null) {

                    System.out.println(
                            "Student     : "
                                    + firstWaitlisted
                                    .getStudent()
                                    .getName()
                    );
                }

                System.out.println(
                        "Position    : "
                                + firstWaitlisted
                                .getWaitlistPosition()
                );

            } else {

                System.out.println();
                System.out.println(
                        "No waitlisted student currently exists."
                );

                System.out.println(
                        "Seat release will still be verified."
                );
            }

            /*
             * ------------------------------------------------------
             * STEP 3: Release the seat
             * ------------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "RELEASING SEAT..."
            );

            System.out.println(
                    "Application : "
                            + applicationNumber
            );

            System.out.println(
                    "Seat        : "
                            + allocationBefore
                            .getSeatNumber()
            );

            SeatReleaseService
                    seatReleaseService =
                    new SeatReleaseService();

            seatReleaseService.releaseSeat(
                    applicationNumber
            );

            System.out.println();
            System.out.println(
                    "Seat release operation completed."
            );

            /*
             * ------------------------------------------------------
             * STEP 4: Verify released allocation
             * ------------------------------------------------------
             */

            SeatAllocation allocationAfter =
                    findAllocation(applicationNumber);

            if (allocationAfter == null) {

                printSuccess(
                        "Allocation was removed from the database."
                );

            } else {

                printFailure(
                        "Allocation still exists after seat release."
                );
            }

            /*
             * ------------------------------------------------------
             * STEP 5: Verify waitlist promotion
             * ------------------------------------------------------
             */

            if (firstWaitlisted != null) {

                String waitlistedApplicationNumber =
                        firstWaitlisted
                                .getApplicationNumber();

                SeatAllocation promotedAllocation =
                        findAllocation(
                                waitlistedApplicationNumber
                        );

                System.out.println();
                System.out.println(
                        "WAITLIST PROMOTION CHECK"
                );

                if (promotedAllocation != null) {

                    printSuccess(
                            "First waitlisted student received a seat."
                    );

                    System.out.println(
                            "Application : "
                                    + promotedAllocation
                                    .getApplicationNumber()
                    );

                    System.out.println(
                            "Student     : "
                                    + promotedAllocation
                                    .getStudentName()
                    );

                    System.out.println(
                            "Course      : "
                                    + promotedAllocation
                                    .getCourseCode()
                    );

                    System.out.println(
                            "Seat        : "
                                    + promotedAllocation
                                    .getSeatNumber()
                    );

                } else {

                    printFailure(
                            "First waitlisted student was not promoted."
                    );
                }

            } else {

                System.out.println();
                System.out.println(
                        "WAITLIST PROMOTION CHECK"
                );

                System.out.println(
                        "SKIPPED: No waitlisted student existed "
                                + "before release."
                );
            }

            /*
             * ------------------------------------------------------
             * STEP 6: Display final database state
             * ------------------------------------------------------
             */

            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "              FINAL DATABASE STATE"
            );

            System.out.println(
                    "=============================================================="
            );

            showCurrentState();

            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "       WAITLIST + SEAT RELEASE TEST COMPLETED"
            );

            System.out.println(
                    "=============================================================="
            );

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "              VERIFICATION FAILED"
            );

            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "Reason: "
                            + e.getMessage()
            );

            e.printStackTrace();

        } finally {

            EntityManagerFactoryProvider.close();

            scanner.close();
        }
    }

    /*
     * ============================================================
     * DISPLAY CURRENT DATABASE STATE
     * ============================================================
     */

    private static void showCurrentState() {

        EntityManager entityManager =
                EntityManagerFactoryProvider
                        .createEntityManager();

        try {

            Long allocationCount =
                    entityManager.createQuery(
                            """
                            SELECT COUNT(sa)
                            FROM SeatAllocation sa
                            """,
                            Long.class
                    )
                    .getSingleResult();

            Long waitlistCount =
                    entityManager.createQuery(
                            """
                            SELECT COUNT(a)
                            FROM Application a
                            WHERE a.waitlistPosition IS NOT NULL
                            """,
                            Long.class
                    )
                    .getSingleResult();

            Long availableSeatCount =
                    entityManager.createQuery(
                            """
                            SELECT COUNT(s)
                            FROM Seat s
                            WHERE s.status = com.college.admission.enums.SeatStatus.AVAILABLE
                            """,
                            Long.class
                    )
                    .getSingleResult();

            System.out.println();
            System.out.println(
                    "Current Allocations : "
                            + allocationCount
            );

            System.out.println(
                    "Current Waitlist    : "
                            + waitlistCount
            );

            System.out.println(
                    "Available Seats     : "
                            + availableSeatCount
            );

        } finally {

            entityManager.close();
        }
    }

    /*
     * ============================================================
     * FIND ALLOCATION
     * ============================================================
     */

    private static SeatAllocation findAllocation(
            String applicationNumber
    ) {

        EntityManager entityManager =
                EntityManagerFactoryProvider
                        .createEntityManager();

        try {

            List<SeatAllocation> allocations =
                    entityManager.createQuery(
                            """
                            SELECT sa
                            FROM SeatAllocation sa
                            WHERE sa.application.applicationNumber
                                  = :applicationNumber
                            """,
                            SeatAllocation.class
                    )
                    .setParameter(
                            "applicationNumber",
                            applicationNumber
                    )
                    .setMaxResults(1)
                    .getResultList();

            if (allocations.isEmpty()) {
                return null;
            }

            return allocations.get(0);

        } finally {

            entityManager.close();
        }
    }

    /*
     * ============================================================
     * FIND FIRST WAITLISTED APPLICATION
     * ============================================================
     */

    private static Application
    findFirstWaitlistedApplication() {

        EntityManager entityManager =
                EntityManagerFactoryProvider
                        .createEntityManager();

        try {

            List<Application> applications =
                    entityManager.createQuery(
                            """
                            SELECT a
                            FROM Application a
                            WHERE a.waitlistPosition IS NOT NULL
                            ORDER BY a.waitlistPosition ASC
                            """,
                            Application.class
                    )
                    .setMaxResults(1)
                    .getResultList();

            if (applications.isEmpty()) {
                return null;
            }

            return applications.get(0);

        } finally {

            entityManager.close();
        }
    }

    /*
     * ============================================================
     * READ APPLICATION NUMBER
     * ============================================================
     */

    private static String readApplicationNumber() {

        System.out.println();
        System.out.println(
                "Enter an application number whose seat "
                        + "should be released."
        );

        System.out.print(
                "Application Number: "
        );

        String applicationNumber =
                scanner.nextLine();

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            System.out.println();
            System.out.println(
                    "Application number cannot be empty."
            );

            return null;
        }

        return applicationNumber.trim();
    }

    /*
     * ============================================================
     * SUCCESS
     * ============================================================
     */

    private static void printSuccess(
            String message
    ) {

        System.out.println();
        System.out.println(
                "[PASS] " + message
        );
    }

    /*
     * ============================================================
     * FAILURE
     * ============================================================
     */

    private static void printFailure(
            String message
    ) {

        System.out.println();
        System.out.println(
                "[FAIL] " + message
        );
    }
}