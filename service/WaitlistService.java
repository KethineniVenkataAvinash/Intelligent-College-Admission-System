package com.college.admission.service;

import com.college.admission.jpa.WaitlistRepository;
import com.college.admission.model.Application;

import java.util.List;

public class WaitlistService {

    private final WaitlistRepository waitlistRepository;

    public WaitlistService() {

        waitlistRepository =
                new WaitlistRepository();
    }

    /**
     * Displays the persistent waitlist.
     */
    public void displayWaitlist() {

        List<Application> waitlisted =
                waitlistRepository
                        .findAllWaitlisted();

        System.out.println();

        System.out.println(
                "======================================"
        );

        System.out.println(
                "         PERSISTENT WAITLIST"
        );

        System.out.println(
                "======================================"
        );

        if (waitlisted == null
                || waitlisted.isEmpty()) {

            System.out.println(
                    "No students are currently waitlisted."
            );

            System.out.println(
                    "======================================"
            );

            return;
        }

        int displayPosition = 1;

        for (Application application :
                waitlisted) {

            if (application == null
                    || application.getStudent() == null) {

                continue;
            }

            System.out.println();

            System.out.println(
                    "Waitlist Position : "
                            + displayPosition
            );

            System.out.println(
                    "Database Position  : "
                            + application
                                    .getWaitlistPosition()
            );

            System.out.println(
                    "Rank               : "
                            + application
                                    .getStudent()
                                    .getRank()
            );

            System.out.println(
                    "Student Name       : "
                            + application
                                    .getStudent()
                                    .getName()
            );

            System.out.println(
                    "Application No.    : "
                            + application
                                    .getApplicationNumber()
            );

            System.out.println(
                    "Category           : "
                            + application
                                    .getStudent()
                                    .getCategory()
            );

            System.out.println(
                    "Percentage         : "
                            + application
                                    .getStudent()
                                    .getPercentage()
            );

            System.out.println(
                    "Status             : "
                            + application
                                    .getCounsellingStatus()
            );

            System.out.println(
                    "--------------------------------------"
            );

            displayPosition++;
        }

        System.out.println();

        System.out.println(
                "Total Waitlisted : "
                        + waitlisted.size()
        );

        System.out.println(
                "======================================"
        );
    }

    /**
     * Returns the first waitlisted application.
     */
    public Application getNextWaitlistedStudent() {

        return waitlistRepository
                .findFirstWaitlisted();
    }

    /**
     * Returns total waitlisted students.
     */
    public long getWaitlistCount() {

        return waitlistRepository
                .countWaitlisted();
    }

    /**
     * Removes a student from waitlist.
     */
    public void removeFromWaitlist(
            String applicationNumber
    ) {

        waitlistRepository.removeFromWaitlist(
                applicationNumber
        );

        System.out.println(
                "Student removed from waitlist."
        );
    }
}