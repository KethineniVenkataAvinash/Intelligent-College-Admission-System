package com.college.admission.service;

import com.college.admission.enums.ApplicationStatus;
import com.college.admission.enums.CounsellingStatus;
import com.college.admission.enums.SeatStatus;
import com.college.admission.jpa.EntityManagerFactoryProvider;
import com.college.admission.model.Application;
import com.college.admission.model.Course;
import com.college.admission.model.CoursePreference;
import com.college.admission.model.Seat;
import com.college.admission.model.SeatAllocation;
import com.college.admission.model.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SeatAllocationTransactionalService {

    /**
     * Normal allocation.
     *
     * Student must be registered for counselling.
     */
    public SeatAllocation allocate(
            String applicationNumber
    ) {

        return allocateInternal(
                applicationNumber,
                false
        );
    }

    /**
     * Allocation used when promoting a
     * waitlisted student.
     */
    public SeatAllocation promoteWaitlistedStudent(
            String applicationNumber
    ) {

        return allocateInternal(
                applicationNumber,
                true
        );
    }

    /**
     * Common transactional allocation logic.
     *
     * waitlistPromotion = true means the student
     * is currently WAITLISTED and is allowed to
     * re-enter the allocation process.
     */
    private SeatAllocation allocateInternal(
            String applicationNumber,
            boolean waitlistPromotion
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        String normalizedApplicationNumber =
                applicationNumber.trim();

        EntityManager entityManager =
                EntityManagerFactoryProvider
                        .getEntityManagerFactory()
                        .createEntityManager();

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {

            transaction.begin();

            /*
             * Lock application first.
             *
             * This prevents two allocation operations
             * from processing the same application
             * simultaneously.
             */
            Application application =
                    entityManager.find(
                            Application.class,
                            normalizedApplicationNumber,
                            LockModeType.PESSIMISTIC_WRITE
                    );

            if (application == null) {

                throw new IllegalStateException(
                        "Application not found: "
                                + normalizedApplicationNumber
                );
            }

            Student student =
                    application.getStudent();

            if (student == null) {

                throw new IllegalStateException(
                        "Student not found for application: "
                                + normalizedApplicationNumber
                );
            }

            /*
             * Prevent duplicate allocation.
             */
            List<SeatAllocation> existingAllocations =
                    entityManager.createQuery(
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
                    .getResultList();

            if (!existingAllocations.isEmpty()) {

                transaction.commit();

                return existingAllocations.get(0);
            }

            /*
             * Application must be eligible.
             */
            if (application.getStatus()
                    != ApplicationStatus.ELIGIBLE) {

                throw new IllegalStateException(
                        "Application is not eligible."
                );
            }

            /*
             * Normal allocation requires REGISTERED.
             *
             * Promotion allows WAITLISTED.
             */
            CounsellingStatus counsellingStatus =
                    application.getCounsellingStatus();

            if (!waitlistPromotion) {

                if (counsellingStatus
                        != CounsellingStatus.REGISTERED) {

                    throw new IllegalStateException(
                            "Student is not registered "
                                    + "for counselling."
                    );
                }

            } else {

                if (counsellingStatus
                        != CounsellingStatus.WAITLISTED) {

                    throw new IllegalStateException(
                            "Student is not currently waitlisted."
                    );
                }

                /*
                 * Move the student back into the
                 * allocation state.
                 */
                application.setCounsellingStatus(
                        CounsellingStatus.REGISTERED
                );

                application.setWaitlistPosition(
                        null
                );
            }

            if (student.getRank() == null) {

                throw new IllegalStateException(
                        "Student rank has not been generated."
                );
            }

            if (student.getCategory() == null) {

                throw new IllegalStateException(
                        "Student category is not available."
                );
            }

            /*
             * Copy preferences so that we can sort them
             * without changing the managed collection.
             */
            List<CoursePreference> preferences =
                    new ArrayList<>(
                            application.getPreferences()
                    );

            preferences.sort(
                    Comparator.comparingInt(
                            CoursePreference
                                    ::getPreferenceNumber
                    )
            );

            /*
             * Try each course preference.
             */
            for (CoursePreference preference :
                    preferences) {

                if (preference == null) {
                    continue;
                }

                CoursePreference currentPreference =
                        preference;

                Course requestedCourse =
                        currentPreference.getCourse();

                if (requestedCourse == null) {
                    continue;
                }

                String courseCode =
                        requestedCourse.getCourseCode();

                if (courseCode == null
                        || courseCode.isBlank()) {

                    continue;
                }

                /*
                 * Lock the course row.
                 *
                 * This protects availableSeats
                 * from concurrent modifications.
                 */
                Course course =
                        entityManager.find(
                                Course.class,
                                courseCode,
                                LockModeType.PESSIMISTIC_WRITE
                        );

                if (course == null) {
                    continue;
                }

                /*
                 * Check course-level capacity.
                 */
                if (!course.hasAvailableSeat()) {
                    continue;
                }

                /*
                 * Find one available seat matching
                 * the student's category.
                 *
                 * The selected row is pessimistically locked.
                 */
                List<Seat> availableSeats =
                        entityManager.createQuery(
                                """
                                SELECT s
                                FROM Seat s
                                WHERE s.course.courseCode =
                                      :courseCode
                                  AND s.category = :category
                                  AND s.status = :status
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
                                student.getCategory()
                        )
                        .setParameter(
                                "status",
                                SeatStatus.AVAILABLE
                        )
                        .setMaxResults(1)
                        .setLockMode(
                                LockModeType.PESSIMISTIC_WRITE
                        )
                        .getResultList();

                /*
                 * No seat for this preference.
                 *
                 * Continue to the next preference.
                 */
                if (availableSeats.isEmpty()) {
                    continue;
                }

                Seat seat =
                        availableSeats.get(0);

                /*
                 * Allocate physical seat.
                 */
                seat.allocate(
                        normalizedApplicationNumber
                );

                /*
                 * Decrease course capacity.
                 */
                course.allocateSeat();

                /*
                 * Create permanent allocation record.
                 */
                SeatAllocation allocation =
                        new SeatAllocation(
                                application,
                                seat,
                                currentPreference
                                        .getPreferenceNumber()
                        );

                entityManager.persist(
                        allocation
                );

                /*
                 * Update application.
                 */
                application.setStatus(
                        ApplicationStatus.ALLOTTED
                );

                application.setCounsellingStatus(
                        CounsellingStatus.ALLOTTED
                );

                application.setWaitlistPosition(
                        null
                );

                /*
                 * Everything is committed together.
                 */
                transaction.commit();

                displaySuccessfulAllocation(
                        student,
                        application,
                        course,
                        seat,
                        currentPreference,
                        waitlistPromotion
                );

                return allocation;
            }

            /*
             * No suitable seat exists.
             *
             * For a normal allocation:
             *
             * REGISTERED → WAITLISTED
             *
             * For a promotion attempt:
             *
             * WAITLISTED → WAITLISTED
             */
            if (waitlistPromotion) {

                application.setCounsellingStatus(
                        CounsellingStatus.WAITLISTED
                );

            } else {

                application.setCounsellingStatus(
                        CounsellingStatus.WAITLISTED
                );
            }

            /*
             * Position is assigned by WaitlistService
             * before/after this operation.
             *
             * For the atomic operation we calculate
             * the next position inside this transaction.
             */
            Integer position =
                    findNextWaitlistPosition(
                            entityManager
                    );

            application.setWaitlistPosition(
                    position
            );

            transaction.commit();

            System.out.println();

            System.out.println(
                    "No suitable seat available."
            );

            System.out.println(
                    "Application No. : "
                            + normalizedApplicationNumber
            );

            System.out.println(
                    "Student         : "
                            + student.getName()
            );

            System.out.println(
                    "Rank            : "
                            + student.getRank()
            );

            System.out.println(
                    "Waitlist Position: "
                            + position
            );

            return null;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;

        } finally {

            entityManager.close();
        }
    }

    /**
     * Finds the next waitlist position.
     */
    private int findNextWaitlistPosition(
            EntityManager entityManager
    ) {

        Integer maximumPosition =
                entityManager.createQuery(
                        """
                        SELECT MAX(a.waitlistPosition)
                        FROM Application a
                        WHERE a.counsellingStatus =
                              :status
                        """,
                        Integer.class
                )
                .setParameter(
                        "status",
                        CounsellingStatus.WAITLISTED
                )
                .getSingleResult();

        if (maximumPosition == null) {
            return 1;
        }

        return maximumPosition + 1;
    }

    /**
     * Displays successful allocation details.
     */
    private void displaySuccessfulAllocation(
            Student student,
            Application application,
            Course course,
            Seat seat,
            CoursePreference preference,
            boolean promotion
    ) {

        System.out.println();

        System.out.println(
                "======================================"
        );

        if (promotion) {

            System.out.println(
                    "       WAITLIST PROMOTION SUCCESS"
            );

        } else {

            System.out.println(
                    "        SEAT ALLOCATION SUCCESS"
            );
        }

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Student Name      : "
                        + student.getName()
        );

        System.out.println(
                "Application No.   : "
                        + application
                                .getApplicationNumber()
        );

        System.out.println(
                "Rank              : "
                        + student.getRank()
        );

        System.out.println(
                "Category          : "
                        + student.getCategory()
        );

        System.out.println(
                "Course            : "
                        + course.getCourseName()
        );

        System.out.println(
                "Course Code       : "
                        + course.getCourseCode()
        );

        System.out.println(
                "Preference        : "
                        + preference
                                .getPreferenceNumber()
        );

        System.out.println(
                "Seat Number       : "
                        + seat.getSeatNumber()
        );

        System.out.println(
                "Remaining Seats   : "
                        + course.getAvailableSeats()
        );

        System.out.println(
                "Allocation Thread : "
                        + Thread.currentThread()
                                .getName()
        );

        System.out.println(
                "======================================"
        );
    }
}