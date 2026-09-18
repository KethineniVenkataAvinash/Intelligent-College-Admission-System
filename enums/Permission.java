package com.college.admission.enums;

public enum Permission {

    // ============================================================
    // STUDENT PERMISSIONS
    // ============================================================

    VIEW_OWN_PROFILE,

    VIEW_OWN_APPLICATION,

    UPDATE_OWN_APPLICATION,

    VIEW_OWN_RANK,

    VIEW_OWN_ALLOCATION,

    VIEW_OWN_SCHOLARSHIP,

    VIEW_OWN_PAYMENT,

    VIEW_OWN_RECEIPT,

    VIEW_OWN_HALL_TICKET,

    VIEW_OWN_HOSTEL,

    VIEW_OWN_TRANSPORT,

    VIEW_OWN_NOTIFICATIONS,


    // ============================================================
    // ADMIN PERMISSIONS
    // ============================================================

    VIEW_ALL_STUDENTS,

    VIEW_ALL_APPLICATIONS,

    VERIFY_APPLICATION,

    REJECT_APPLICATION,

    GENERATE_RANK,

    ALLOCATE_SEATS,

    RELEASE_SEATS,

    MANAGE_WAITLIST,

    MANAGE_EXAMS,

    ALLOCATE_EXAM_SEATS,

    GENERATE_HALL_TICKETS,

    MANAGE_SCHOLARSHIPS,

    MANAGE_PAYMENTS,

    VIEW_PAYMENT_REPORTS,

    MANAGE_HOSTELS,

    MANAGE_TRANSPORT,

    VIEW_TRANSPORT_REPORTS,

    VIEW_HOSTEL_REPORTS,

    MANAGE_COURSES,

    MANAGE_SYSTEM
}