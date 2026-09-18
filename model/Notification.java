package com.college.admission.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(
                        name = "idx_notification_application",
                        columnList = "application_number"
                ),
                @Index(
                        name = "idx_notification_created",
                        columnList = "created_at"
                )
        }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(
            name = "application_number",
            nullable = false,
            length = 30
    )
    private String applicationNumber;

    @Column(
            name = "student_name",
            nullable = false,
            length = 150
    )
    private String studentName;

    @Column(
            name = "title",
            nullable = false,
            length = 200
    )
    private String title;

    @Column(
            name = "message",
            nullable = false,
            length = 1000
    )
    private String message;

    @Column(
            name = "notification_type",
            nullable = false,
            length = 50
    )
    private String notificationType;

    @Column(
            name = "is_read",
            nullable = false
    )
    private boolean read;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    protected Notification() {
    }

    public Notification(
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

        if (title == null
                || title.isBlank()) {

            throw new IllegalArgumentException(
                    "Notification title cannot be empty."
            );
        }

        if (message == null
                || message.isBlank()) {

            throw new IllegalArgumentException(
                    "Notification message cannot be empty."
            );
        }

        if (notificationType == null
                || notificationType.isBlank()) {

            throw new IllegalArgumentException(
                    "Notification type cannot be empty."
            );
        }

        this.applicationNumber =
                applicationNumber.trim();

        this.studentName =
                studentName.trim();

        this.title =
                title.trim();

        this.message =
                message.trim();

        this.notificationType =
                notificationType.trim().toUpperCase();

        this.read = false;

        this.createdAt =
                LocalDateTime.now();
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public Long getNotificationId() {
        return notificationId;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // =========================================================
    // READ / UNREAD
    // =========================================================

    public void markAsRead() {
        this.read = true;
    }

    public void markAsUnread() {
        this.read = false;
    }

    // =========================================================
    // DISPLAY
    // =========================================================

    public void displayNotification() {

        System.out.println();

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                       NOTIFICATION"
        );

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "Notification ID    : "
                        + notificationId
        );

        System.out.println(
                "Application Number : "
                        + applicationNumber
        );

        System.out.println(
                "Student Name       : "
                        + studentName
        );

        System.out.println(
                "Type               : "
                        + notificationType
        );

        System.out.println(
                "Title              : "
                        + title
        );

        System.out.println(
                "Message            : "
                        + message
        );

        System.out.println(
                "Status             : "
                        + (read ? "READ" : "UNREAD")
        );

        System.out.println(
                "Created At         : "
                        + createdAt
        );

        System.out.println(
                "============================================================"
        );
    }

    @Override
    public String toString() {

        return "Notification{" +
                "notificationId=" + notificationId +
                ", applicationNumber='" +
                applicationNumber + '\'' +
                ", studentName='" +
                studentName + '\'' +
                ", title='" +
                title + '\'' +
                ", message='" +
                message + '\'' +
                ", notificationType='" +
                notificationType + '\'' +
                ", read=" + read +
                ", createdAt=" + createdAt +
                '}';
    }
}