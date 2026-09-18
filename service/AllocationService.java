package com.college.admission.service;

import com.college.admission.enums.ApplicationStatus;
import com.college.admission.enums.CounsellingStatus;
import com.college.admission.jpa.SeatAllocationJpaRepository;
import com.college.admission.jpa.StudentJpaRepository;
import com.college.admission.model.Application;
import com.college.admission.model.SeatAllocation;
import com.college.admission.model.Student;
import com.college.admission.security.ServiceAuthorization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AllocationService {

    private final StudentJpaRepository studentJpaRepository;

    private final SeatAllocationJpaRepository
            seatAllocationJpaRepository;

    private final SeatAllocationTransactionalService
            transactionalService;

    /*
     * Stores students who could not receive a seat
     * during the current allocation run.
     */
    private final List<Student> waitlistedStudents;

    private final WaitlistService waitlistService;

    /**
     * Constructor.
     */
    public AllocationService() {

        studentJpaRepository =
                new StudentJpaRepository();

        seatAllocationJpaRepository =
                new SeatAllocationJpaRepository();

        transactionalService =
                new SeatAllocationTransactionalService();

        waitlistService =
                new WaitlistService();

        waitlistedStudents =
                new ArrayList<>();
    }

    /**
     * Performs complete seat allocation.
     *
     * Allocation order:
     *
     * 1. Load eligible students.
     * 2. Select counselling candidates.
     * 3. Registered students are normally allocated.
     * 4. Previously waitlisted students are promoted/retried.
     * 5. Students are sorted by rank.
     * 6. Course preferences are checked in order.
     * 7. Successful allocations are stored in MySQL.
     * 8. Students without seats remain waitlisted.
     */
    public void allocateSeats() {

        ServiceAuthorization.requireAdmin();

        /*
         * Clear runtime waitlist from previous execution.
         */
        waitlistedStudents.clear();

        /*
         * Load all eligible students from database.
         */
        List<Student> students =
                studentJpaRepository
                        .findAllEligibleStudents();

        if (students == null
                || students.isEmpty()) {

            System.out.println();
            System.out.println(
                    "======================================"
            );
            System.out.println(
                    "          SEAT ALLOCATION"
            );
            System.out.println(
                    "======================================"
            );
            System.out.println(
                    "No eligible students found."
            );
            System.out.println(
                    "======================================"
            );

            return;
        }

        /*
         * Create candidate list.
         */
        List<Student> candidates =
                new ArrayList<>();

        /*
         * Select students who:
         *
         * - have an application
         * - are ELIGIBLE
         * - are either REGISTERED or WAITLISTED
         * - have a generated rank
         */
        for (Student student : students) {

            if (student == null) {
                continue;
            }

            Application application =
                    student.getApplication();

            if (application == null) {
                continue;
            }

            /*
             * Application must be eligible.
             */
            if (application.getStatus()
                    != ApplicationStatus.ELIGIBLE) {

                continue;
            }

            /*
             * Both states are valid candidates:
             *
             * REGISTERED:
             *     First allocation attempt.
             *
             * WAITLISTED:
             *     Retry / promotion attempt.
             */
            CounsellingStatus counsellingStatus =
                    application.getCounsellingStatus();

            if (counsellingStatus
                    != CounsellingStatus.REGISTERED
                    && counsellingStatus
                    != CounsellingStatus.WAITLISTED) {

                continue;
            }

            /*
             * Rank must already exist.
             */
            if (student.getRank() == null) {
                continue;
            }

            candidates.add(student);
        }

        /*
         * Sort candidates by rank.
         *
         * Lower rank number = higher priority.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                Student::getRank
                        )
                        .thenComparing(
                                Student::getApplicationNumber
                        )
        );

        /*
         * Display allocation header.
         */
        System.out.println();
        System.out.println(
                "======================================"
        );
        System.out.println(
                "        STARTING SEAT ALLOCATION"
        );
        System.out.println(
                "======================================"
        );

        System.out.println(
                "Eligible students       : "
                        + students.size()
        );

        System.out.println(
                "Counselling candidates  : "
                        + candidates.size()
        );

        System.out.println(
                "Allocation order        : Rank"
        );

        System.out.println(
                "======================================"
        );

        int allocatedCount = 0;
        int waitlistedCount = 0;
        int failedCount = 0;

        /*
         * Process each candidate according to rank.
         */
        for (Student student : candidates) {

            String applicationNumber =
                    student.getApplicationNumber();

            try {

                Application application =
                        student.getApplication();

                CounsellingStatus counsellingStatus =
                        application.getCounsellingStatus();

                boolean waitlistPromotion =
                        counsellingStatus
                                == CounsellingStatus.WAITLISTED;

                /*
                 * Use the correct transactional method.
                 *
                 * REGISTERED:
                 *     Normal allocation.
                 *
                 * WAITLISTED:
                 *     Retry allocation / promotion.
                 */
                SeatAllocation allocation;

                if (waitlistPromotion) {

                    System.out.println();
                    System.out.println(
                            "Retrying waitlisted student..."
                    );

                    System.out.println(
                            "Student       : "
                                    + student.getName()
                    );

                    System.out.println(
                            "Application   : "
                                    + applicationNumber
                    );

                    System.out.println(
                            "Rank          : "
                                    + student.getRank()
                    );

                    allocation =
                            transactionalService
                                    .promoteWaitlistedStudent(
                                            applicationNumber
                                    );

                } else {

                    allocation =
                            transactionalService.allocate(
                                    applicationNumber
                            );
                }

                /*
                 * Allocation successful.
                 */
                if (allocation != null) {

                    allocatedCount++;

                    System.out.println();

                    System.out.println(
                            "Rank "
                                    + student.getRank()
                                    + " allocated successfully."
                    );

                } else {

                    /*
                     * Student could not get any
                     * preferred course.
                     */
                    waitlistedStudents.add(student);

                    waitlistedCount++;

                    System.out.println();

                    System.out.println(
                            "Student remains waitlisted."
                    );

                    System.out.println(
                            "Student       : "
                                    + student.getName()
                    );

                    System.out.println(
                            "Application   : "
                                    + applicationNumber
                    );

                    System.out.println(
                            "Rank          : "
                                    + student.getRank()
                    );

                    System.out.println(
                            "Status        : WAITLISTED"
                    );
                }

            } catch (Exception e) {

                /*
                 * One student's failure must not
                 * terminate the entire allocation run.
                 */
                failedCount++;

                System.out.println();

                System.out.println(
                        "Allocation failed."
                );

                System.out.println(
                        "Student       : "
                                + student.getName()
                );

                System.out.println(
                        "Application   : "
                                + applicationNumber
                );

                System.out.println(
                        "Rank          : "
                                + student.getRank()
                );

                System.out.println(
                        "Reason        : "
                                + e.getMessage()
                );

                System.out.println(
                        "The next student will be processed."
                );
            }
        }

        /*
         * Final allocation summary.
         */
        System.out.println();
        System.out.println(
                "======================================"
        );
        System.out.println(
                "      ALLOCATION PROCESS COMPLETE"
        );
        System.out.println(
                "======================================"
        );

        System.out.println(
                "Candidates processed : "
                        + candidates.size()
        );

        System.out.println(
                "Seats allocated      : "
                        + allocatedCount
        );

        System.out.println(
                "Students waitlisted  : "
                        + waitlistedCount
        );

        System.out.println(
                "Allocation failures  : "
                        + failedCount
        );

        System.out.println(
                "======================================"
        );
    }

    /**
     * Returns students who could not receive
     * a seat during the current allocation run.
     */
    public List<Student> getWaitlistedStudents() {

        return List.copyOf(
                waitlistedStudents
        );
    }

    /**
     * Displays every seat allocation stored
     * in the database.
     */
    public void displayAllAllocations() {

        ServiceAuthorization.requireAdmin();

        List<SeatAllocation> allocations =
                seatAllocationJpaRepository
                        .findAll();

        System.out.println();
        System.out.println(
                "======================================"
        );
        System.out.println(
                "          ALLOCATED SEATS"
        );
        System.out.println(
                "======================================"
        );

        if (allocations == null
                || allocations.isEmpty()) {

            System.out.println(
                    "No seats have been allocated."
            );

            System.out.println(
                    "======================================"
            );

            return;
        }

        for (SeatAllocation allocation :
                allocations) {

            if (allocation == null) {
                continue;
            }

            System.out.println();

            System.out.println(
                    "Student Name      : "
                            + allocation.getStudentName()
            );

            System.out.println(
                    "Application No.   : "
                            + allocation
                                    .getApplicationNumber()
            );

            System.out.println(
                    "Course            : "
                            + allocation.getCourseName()
            );

            System.out.println(
                    "Course Code       : "
                            + allocation.getCourseCode()
            );

            System.out.println(
                    "Preference        : "
                            + allocation
                                    .getPreferenceNumber()
            );

            System.out.println(
                    "Seat Number       : "
                            + allocation.getSeatNumber()
            );

            System.out.println(
                    "--------------------------------------"
            );
        }

        System.out.println(
                "Total allocations  : "
                        + allocations.size()
        );

        System.out.println(
                "======================================"
        );
    }

    /**
     * Displays the seat allocation of one student.
     */
    public void displayStudentAllocation(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            System.out.println(
                    "Application number cannot be empty."
            );

            return;
        }

        String normalizedApplicationNumber =
                applicationNumber.trim();

        SeatAllocation allocation =
                seatAllocationJpaRepository
                        .findByApplicationNumber(
                                normalizedApplicationNumber
                        );

        System.out.println();
        System.out.println(
                "======================================"
        );
        System.out.println(
                "       STUDENT SEAT ALLOCATION"
        );
        System.out.println(
                "======================================"
        );

        if (allocation == null) {

            System.out.println(
                    "No seat allocation found."
            );

            System.out.println(
                    "Application No. : "
                            + normalizedApplicationNumber
            );

            System.out.println(
                    "======================================"
            );

            return;
        }

        System.out.println(
                "Student Name    : "
                        + allocation.getStudentName()
        );

        System.out.println(
                "Application No. : "
                        + allocation
                                .getApplicationNumber()
        );

        System.out.println(
                "Course          : "
                        + allocation.getCourseName()
        );

        System.out.println(
                "Course Code     : "
                        + allocation.getCourseCode()
        );

        System.out.println(
                "Preference No.  : "
                        + allocation
                                .getPreferenceNumber()
        );

        System.out.println(
                "Seat Number     : "
                        + allocation.getSeatNumber()
        );

        System.out.println(
                "======================================"
        );
    }

    /**
     * Checks whether a student already has
     * a seat allocation.
     */
    public boolean hasAllocation(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return false;
        }

        SeatAllocation allocation =
                seatAllocationJpaRepository
                        .findByApplicationNumber(
                                applicationNumber.trim()
                        );

        return allocation != null;
    }

    /**
     * Returns a student's allocation.
     */
    public SeatAllocation getAllocation(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }

        return seatAllocationJpaRepository
                .findByApplicationNumber(
                        applicationNumber.trim()
                );
    }

    /**
     * Displays the persistent waitlist.
     */
    public void displayWaitlist() {

        ServiceAuthorization.requireAdmin();

        WaitlistService waitlistService =
                new WaitlistService();

        waitlistService.displayWaitlist();
    }
}