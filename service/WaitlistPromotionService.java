package com.college.admission.service;

import com.college.admission.security.ServiceAuthorization;
import com.college.admission.model.Application;
import com.college.admission.model.SeatAllocation;

public class WaitlistPromotionService {

    private final WaitlistService waitlistService;

    private final SeatAllocationTransactionalService
            allocationService;

    public WaitlistPromotionService() {

        waitlistService =
                new WaitlistService();

        allocationService =
                new SeatAllocationTransactionalService();
    }

    /**
     * Attempts to promote the highest-priority
     * waitlisted student.
     */
    public boolean promoteNextStudent() {

        ServiceAuthorization.requireAdmin();

        Application application =
                waitlistService
                        .getNextWaitlistedStudent();

        if (application == null) {

            System.out.println();

            System.out.println(
                    "No students are currently waitlisted."
            );

            return false;
        }

        String applicationNumber =
                application.getApplicationNumber();

        try {

            SeatAllocation allocation =
                    allocationService
                            .promoteWaitlistedStudent(
                                    applicationNumber
                            );

            if (allocation == null) {

                System.out.println();

                System.out.println(
                        "The next waitlisted student "
                                + "still has no suitable seat."
                );

                return false;
            }

            System.out.println();

            System.out.println(
                    "Waitlisted student promoted successfully."
            );

            return true;

        } catch (Exception e) {

            System.out.println();

            System.out.println(
                    "Waitlist promotion failed."
            );

            System.out.println(
                    "Reason: "
                            + e.getMessage()
            );

            return false;
        }
    }
}