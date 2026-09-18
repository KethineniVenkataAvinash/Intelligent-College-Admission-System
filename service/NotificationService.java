package com.college.admission.service;

import com.college.admission.jpa.NotificationJpaRepository;
import com.college.admission.model.Notification;
import com.college.admission.security.ServiceAuthorization;
import com.college.admission.model.Student;

import java.util.List;
import java.math.BigDecimal;

public class NotificationService {

    private final NotificationJpaRepository notificationRepository;

    public NotificationService() {
        this.notificationRepository =
                new NotificationJpaRepository();
    }

    // =========================================================
    // CREATE NOTIFICATION
    // =========================================================

    /**
     * Creates and stores a notification for a student.
     */
    public Notification createNotification(
            Student student,
            String title,
            String message,
            String notificationType
    ) {

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (student.getApplicationNumber() == null
                || student.getApplicationNumber().isBlank()) {

            throw new IllegalStateException(
                    "Student application number is required."
            );
        }

        Notification notification =
                new Notification(
                        student.getApplicationNumber(),
                        student.getName(),
                        title,
                        message,
                        notificationType
                );

        return notificationRepository.save(
                notification
        );
    }

    // =========================================================
    // CREATE NOTIFICATION USING APPLICATION NUMBER
    // =========================================================

    /**
     * Creates a notification using an application number
     * and student name.
     */
    public Notification createNotification(
            String applicationNumber,
            String studentName,
            String title,
            String message,
            String notificationType
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        if (studentName == null
                || studentName.isBlank()) {

            throw new IllegalArgumentException(
                    "Student name cannot be empty."
            );
        }

        Notification notification =
                new Notification(
                        applicationNumber,
                        studentName,
                        title,
                        message,
                        notificationType
                );

        return notificationRepository.save(
                notification
        );
    }

    // =========================================================
    // APPLICATION SUBMITTED
    // =========================================================

    public Notification notifyApplicationSubmitted(
            Student student
    ) {

        return createNotification(
                student,
                "Application Submitted",
                "Your college admission application has "
                        + "been successfully submitted.",
                "APPLICATION_SUBMITTED"
        );
    }

    // =========================================================
    // APPLICATION VERIFIED
    // =========================================================

    public Notification notifyApplicationVerified(
            Student student
    ) {

        return createNotification(
                student,
                "Application Verified",
                "Your admission application has been "
                        + "verified successfully.",
                "APPLICATION_VERIFIED"
        );
    }

    // =========================================================
    // APPLICATION REJECTED
    // =========================================================

    public Notification notifyApplicationRejected(
            Student student,
            String reason
    ) {

        String rejectionReason =
                reason == null || reason.isBlank()
                        ? "No reason provided."
                        : reason.trim();

        return createNotification(
                student,
                "Application Rejected",
                "Your admission application has been "
                        + "rejected. Reason: "
                        + rejectionReason,
                "APPLICATION_REJECTED"
        );
    }

    // =========================================================
    // RANK GENERATED
    // =========================================================

    public Notification notifyRankGenerated(
            Student student
    ) {

        if (student.getRank() == null) {
            throw new IllegalStateException(
                    "Student rank has not been generated yet."
            );
        }

        return createNotification(
                student,
                "Rank Generated",
                "Your admission rank has been generated. "
                        + "Your rank is "
                        + student.getRank()
                        + ".",
                "RANK_GENERATED"
        );
    }

    // =========================================================
    // SEAT ALLOTTED
    // =========================================================

    public Notification notifySeatAllotted(
            Student student,
            String courseName,
            String seatNumber
    ) {

        return createNotification(
                student,
                "Seat Allotted",
                "Congratulations! You have been allotted "
                        + "a seat in "
                        + courseName
                        + ". Seat Number: "
                        + seatNumber
                        + ".",
                "SEAT_ALLOTTED"
        );
    }

    // =========================================================
    // WAITLISTED
    // =========================================================

    public Notification notifyWaitlisted(
            Student student,
            int waitlistPosition
    ) {

        if (waitlistPosition <= 0) {
            throw new IllegalArgumentException(
                    "Waitlist position must be greater than zero."
            );
        }

        return createNotification(
                student,
                "Seat Allocation Waitlisted",
                "You have been placed on the admission "
                        + "waitlist. Current position: "
                        + waitlistPosition
                        + ".",
                "WAITLISTED"
        );
    }

    // =========================================================
    // SEAT UPGRADED
    // =========================================================

    public Notification notifySeatUpgraded(
            Student student,
            String courseName,
            String seatNumber
    ) {

        return createNotification(
                student,
                "Seat Upgraded",
                "Your admission seat has been upgraded "
                        + "to "
                        + courseName
                        + ". New seat number: "
                        + seatNumber
                        + ".",
                "SEAT_UPGRADED"
        );
    }

    // =========================================================
    // SCHOLARSHIP GENERATED
    // =========================================================

    public Notification notifyScholarshipGenerated(
            Student student,
            double scholarshipPercentage,
            BigDecimal scholarshipAmount
    ) {

        return createNotification(
                student,
                "Scholarship Generated",
                String.format(
                        "Your scholarship has been generated. "
                                + "Scholarship: %.2f%% "
                                + "(₹%s).",
                        scholarshipPercentage,
                        scholarshipAmount
                ),
                "SCHOLARSHIP_GENERATED"
        );
    }

    // =========================================================
    // PAYMENT CREATED
    // =========================================================

    public Notification notifyPaymentCreated(
            Student student,
            BigDecimal amount
    ) {

        return createNotification(
                student,
                "Payment Created",
                String.format(
                        "Your admission payment record has "
                                + "been created. Net payable amount: "
                                + "₹%s.",
                        amount
                ),
                "PAYMENT_CREATED"
        );
    }

    // =========================================================
    // PAYMENT COMPLETED
    // =========================================================

    public Notification notifyPaymentCompleted(
            Student student,
            BigDecimal amount
    ) {

        return createNotification(
                student,
                "Payment Completed",
                String.format(
                        "Your payment of ₹%s has been "
                                + "successfully recorded.",
                        amount
                ),
                "PAYMENT_COMPLETED"
        );
    }

    // =========================================================
    // EXAM SCHEDULED
    // =========================================================

    public Notification notifyExamScheduled(
            Student student,
            String examName,
            String examDate,
            String session
    ) {

        return createNotification(
                student,
                "Exam Scheduled",
                "Your "
                        + examName
                        + " examination is scheduled on "
                        + examDate
                        + " during the "
                        + session
                        + " session.",
                "EXAM_SCHEDULED"
        );
    }

    // =========================================================
    // HALL TICKET GENERATED
    // =========================================================

    public Notification notifyHallTicketGenerated(
            Student student
    ) {

        return createNotification(
                student,
                "Hall Ticket Generated",
                "Your examination hall ticket has been "
                        + "generated successfully. Please "
                        + "check your hall ticket details.",
                "HALL_TICKET_GENERATED"
        );
    }

    // =========================================================
    // HOSTEL ALLOTTED
    // =========================================================

    public Notification notifyHostelAllotted(
            Student student,
            String hostelName,
            String roomNumber
    ) {

        return createNotification(
                student,
                "Hostel Allotted",
                "Your hostel accommodation has been "
                        + "allotted. Hostel: "
                        + hostelName
                        + ", Room: "
                        + roomNumber
                        + ".",
                "HOSTEL_ALLOTTED"
        );
    }

    // =========================================================
    // TRANSPORT ALLOTTED
    // =========================================================

    public Notification notifyTransportAllotted(
            Student student,
            String routeName,
            String busNumber
    ) {

        return createNotification(
                student,
                "Transport Allotted",
                "Your college transport has been "
                        + "allotted. Route: "
                        + routeName
                        + ", Bus: "
                        + busNumber
                        + ".",
                "TRANSPORT_ALLOTTED"
        );
    }

    // =========================================================
    // GET STUDENT NOTIFICATIONS
    // =========================================================

    /**
     * Returns all notifications for a student.
     */
    public List<Notification> getStudentNotifications(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {
            return List.of();
        }

        return notificationRepository
                .findByApplicationNumber(
                        student.getApplicationNumber()
                );
    }

    // =========================================================
    // GET BY APPLICATION NUMBER
    // =========================================================

    public List<Notification> getNotifications(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return List.of();
        }

        return notificationRepository
                .findByApplicationNumber(
                        applicationNumber.trim()
                );
    }

    // =========================================================
    // GET UNREAD NOTIFICATIONS
    // =========================================================

    public List<Notification> getUnreadNotifications(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {
            return List.of();
        }

        return notificationRepository
                .findUnreadByApplicationNumber(
                        student.getApplicationNumber()
                );
    }

    // =========================================================
    // DISPLAY STUDENT NOTIFICATIONS
    // =========================================================

    public void displayStudentNotifications(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            System.out.println(
                    "Student not found."
            );

            return;
        }

        List<Notification> notifications =
                getStudentNotifications(student);

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                         NOTIFICATIONS"
        );

        System.out.println(
                "=========================================================================="
        );

        if (notifications.isEmpty()) {

            System.out.println(
                    "No notifications found."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        for (Notification notification :
                notifications) {

            notification.displayNotification();
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // =========================================================
    // DISPLAY UNREAD NOTIFICATIONS
    // =========================================================

    public void displayUnreadNotifications(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            System.out.println(
                    "Student not found."
            );

            return;
        }

        List<Notification> notifications =
                getUnreadNotifications(student);

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                     UNREAD NOTIFICATIONS"
        );

        System.out.println(
                "=========================================================================="
        );

        if (notifications.isEmpty()) {

            System.out.println(
                    "You have no unread notifications."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        for (Notification notification :
                notifications) {

            notification.displayNotification();
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // =========================================================
    // MARK NOTIFICATION AS READ
    // =========================================================

    public void markAsRead(
            Long notificationId
    ) {

        ServiceAuthorization.requireAdmin();

        notificationRepository.markAsRead(
                notificationId
        );
    }

    // =========================================================
    // MARK NOTIFICATION AS UNREAD
    // =========================================================

    public void markAsUnread(
            Long notificationId
    ) {

        ServiceAuthorization.requireAdmin();

        notificationRepository.markAsUnread(
                notificationId
        );
    }

    // =========================================================
    // MARK ALL AS READ
    // =========================================================

    public void markAllAsRead(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        notificationRepository.markAllAsRead(
                student.getApplicationNumber()
        );
    }

    // =========================================================
    // UNREAD COUNT
    // =========================================================

    public long getUnreadCount(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {
            return 0;
        }

        return notificationRepository
                .countUnreadByApplicationNumber(
                        student.getApplicationNumber()
                );
    }

    // =========================================================
    // TOTAL NOTIFICATION COUNT
    // =========================================================

    public long getNotificationCount() {

        ServiceAuthorization.requireAdmin();

        return notificationRepository.count();
    }

    // =========================================================
    // ADMIN - DISPLAY ALL
    // =========================================================

    public void displayAllNotifications() {

        ServiceAuthorization.requireAdmin();

        List<Notification> notifications =
                notificationRepository.findAll();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                    ALL NOTIFICATIONS"
        );

        System.out.println(
                "=========================================================================="
        );

        if (notifications.isEmpty()) {

            System.out.println(
                    "No notifications found."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        for (Notification notification :
                notifications) {

            notification.displayNotification();
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // =========================================================
    // ADMIN - DISPLAY UNREAD
    // =========================================================

    public void displayAllUnreadNotifications() {

        ServiceAuthorization.requireAdmin();

        List<Notification> notifications =
                notificationRepository.findAllUnread();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                  ALL UNREAD NOTIFICATIONS"
        );

        System.out.println(
                "=========================================================================="
        );

        if (notifications.isEmpty()) {

            System.out.println(
                    "No unread notifications."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        for (Notification notification :
                notifications) {

            notification.displayNotification();
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // =========================================================
    // FIND BY TYPE
    // =========================================================

    public List<Notification> getNotificationsByType(
            String notificationType
    ) {

        ServiceAuthorization.requireAdmin();

        return notificationRepository.findByType(
                notificationType
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void deleteNotification(
            Long notificationId
    ) {

        ServiceAuthorization.requireAdmin();

        notificationRepository.delete(
                notificationId
        );
    }

    // =========================================================
    // DELETE ALL FOR STUDENT
    // =========================================================

    public void deleteStudentNotifications(
            Student student
    ) {

        ServiceAuthorization.requireAdmin();

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        notificationRepository
                .deleteByApplicationNumber(
                        student.getApplicationNumber()
                );
    }

    // =========================================================
    // REPOSITORY ACCESS
    // =========================================================

    public NotificationJpaRepository
    getNotificationRepository() {

        ServiceAuthorization.requireAdmin();

        return notificationRepository;
    }
}