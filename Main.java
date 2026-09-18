package com.college.admission;

import com.college.admission.enums.Category;
import com.college.admission.enums.Gender;
import com.college.admission.enums.Role;
import com.college.admission.model.Application;
import com.college.admission.model.Course;
import com.college.admission.model.Student;
import com.college.admission.service.StudentAuthenticationService;
import com.college.admission.service.StudentRegistrationService;
import com.college.admission.util.InputUtil;
import com.college.admission.service.ApplicationService;
import com.college.admission.service.CourseService;
import com.college.admission.service.ApplicationVerificationService;
import com.college.admission.service.RankingService;
import com.college.admission.service.CounsellingService;
import com.college.admission.service.AllocationService;
import com.college.admission.service.SeatMatrixService;
import com.college.admission.service.SeatReleaseService;
import com.college.admission.service.SeatService;
import com.college.admission.model.Exam;
import com.college.admission.model.ExamRegistration;
import com.college.admission.model.ExamRoom;
import com.college.admission.model.ExamSchedule;
import com.college.admission.enums.ExamSession;
import com.college.admission.service.ExamService;
import com.college.admission.model.ExamAllocation;
import com.college.admission.model.ExamCentre;
import com.college.admission.model.HallTicket;
import com.college.admission.model.Hostel;
import com.college.admission.model.SeatAllocation;
import com.college.admission.service.HallTicketService;
import com.college.admission.model.Scholarship;
import com.college.admission.service.ScholarshipService;
import com.college.admission.model.Payment;
import com.college.admission.service.PaymentService;
import com.college.admission.model.PaymentReceipt;
import com.college.admission.service.PaymentReceiptService;
import com.college.admission.model.HostelAllocation;
import com.college.admission.service.HostelService;
import com.college.admission.model.Bus;
import com.college.admission.model.BusRoute;
import com.college.admission.model.TransportAllocation;
import com.college.admission.service.TransportService;
import com.college.admission.service.WaitlistPromotionService;
import com.college.admission.service.WaitlistService;
import com.college.admission.service.DatabaseSeatInitializationService;
import com.college.admission.model.Notification;
import com.college.admission.service.NotificationService;

import com.college.admission.exception.AuthorizationException;
import com.college.admission.security.AdminAuthenticationService;
import com.college.admission.security.AuthorizationService;
import com.college.admission.security.SecurityContext;
import com.college.admission.security.SecurityManager;
import com.college.admission.security.SecuritySession;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final List<Student> students = new ArrayList<>();

    private static final StudentRegistrationService registrationService =
            new StudentRegistrationService();

    private static final StudentAuthenticationService authenticationService =
            new StudentAuthenticationService();

    private static final AdminAuthenticationService adminAuthenticationService =
            new AdminAuthenticationService();

    private static final SecurityManager securityManager =
            SecurityManager.getInstance();

    private static final AuthorizationService authorizationService =
            securityManager.getAuthorizationService();
    private static final ApplicationService applicationService =
            new ApplicationService();
    private static final CourseService courseService =
            new CourseService();
    private static final ApplicationVerificationService verificationService =
            new ApplicationVerificationService();
    private static final RankingService rankingService =
            new RankingService();
    private static final CounsellingService counsellingService =
            new CounsellingService();
    private static final AllocationService allocationService =
            new AllocationService();
    private static final SeatMatrixService seatMatrixService =
            new SeatMatrixService();
    private static final SeatService seatService =
            new SeatService();
    private static final ExamService examService =
            new ExamService();
    private static final HallTicketService hallTicketService =
            new HallTicketService();
    private static final ScholarshipService scholarshipService =
            new ScholarshipService();
    private static final PaymentService paymentService =
            new PaymentService();
    private static final PaymentReceiptService paymentReceiptService =
            new PaymentReceiptService();
    private static final HostelService hostelService =
            new HostelService();
    private static final TransportService transportService =
            new TransportService();

    private static final NotificationService notificationService =
        new NotificationService();

    private static final DatabaseSeatInitializationService databaseSeatInitializationService =
            new DatabaseSeatInitializationService();
    private static final WaitlistService
        waitlistService =
                new WaitlistService();
    private static final SeatReleaseService
        seatReleaseService =
                new SeatReleaseService();

    private static final WaitlistPromotionService
        waitlistPromotionService =
                new WaitlistPromotionService(); 
               

    public static void main(String[] args) {

    /*
     * ============================================================
     * SYSTEM INITIALIZATION
     * ============================================================
     *
     * 1. Load/create courses in MySQL.
     * 2. Create physical seat records in MySQL.
     * 3. Initialize the in-memory category seat matrices.
     *
     * This must happen BEFORE the main menu starts.
     */

    try {

        // Load courses from MySQL or create the default courses.
        java.lang.reflect.Method initializeCourses =
                CourseService.class.getDeclaredMethod("initializeCourses");
        initializeCourses.setAccessible(true);
        initializeCourses.invoke(courseService);

        // Create database seat records if they do not already exist.
        databaseSeatInitializationService.initializeSeats();

        // Initialize the in-memory seat matrix used by the
        // category-wise seat allocation/display functionality.
        seatMatrixService.initializeSeatMatrices(
                courseService.getCourses()
        );

        System.out.println();
        System.out.println("======================================");
        System.out.println("       SYSTEM INITIALIZATION");
        System.out.println("======================================");
        System.out.println("Courses       : READY");
        System.out.println("Database Seats: READY");
        System.out.println("Seat Matrices : READY");
        System.out.println("======================================");

    } catch (Exception e) {

        System.out.println();
        System.out.println("======================================");
        System.out.println("   SYSTEM INITIALIZATION FAILED");
        System.out.println("======================================");
        System.out.println(
                "Reason: " + e.getMessage()
        );
        System.out.println("======================================");

        /*
         * Do not start the application with an incomplete
         * seat-allocation configuration.
         */
        return;
    }

        while (true) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("   INTELLIGENT COLLEGE ADMISSION");
            System.out.println("     & SEAT ALLOCATION SYSTEM");
            System.out.println("======================================");
            System.out.println("1. Student");
            System.out.println("2. Admin");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    studentMenu();
                    break;

                case "2":
                    adminMenu();
                    break;

                case "3":
                    System.out.println("Thank you for using the system.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void studentMenu() {

        while (true) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("          STUDENT MENU");
            System.out.println("======================================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    registerStudent();
                    break;

                case "2":
                    loginStudent();
                    break;

                case "3":
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
     private static void registerStudent() {

    System.out.println();
    System.out.println("========== STUDENT REGISTRATION ==========");

    String name =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Name: "
            );

    String email =
            InputUtil.readEmail(scanner);

    String phone =
            InputUtil.readPhoneNumber(scanner);

    LocalDate dateOfBirth =
            LocalDate.parse(
                    InputUtil.readDateOfBirth(scanner)
            );

    Gender gender =
            InputUtil.readGender(scanner);

    Category category =
            InputUtil.readCategory(scanner);

    String address =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Address: "
            );

    double percentage =
            InputUtil.readPercentage(scanner);

    String password;

    while (true) {

        password =
                InputUtil.readRequiredString(
                        scanner,
                        "Create Password: "
                );

        String confirmPassword =
                InputUtil.readRequiredString(
                        scanner,
                        "Confirm Password: "
                );

        if (password.equals(confirmPassword)) {
            break;
        }

        System.out.println(
                "Passwords do not match. Please try again."
        );
    }

    try {

        Student student =
                registrationService.registerStudent(
                        name,
                        email,
                        phone,
                        dateOfBirth,
                        gender,
                        category,
                        address,
                        percentage,
                        password
                );

        /*
         * Keep the student in memory for the
         * current console session.
         */
        students.add(student);

        System.out.println();
        System.out.println("======================================");
        System.out.println("     REGISTRATION SUCCESSFUL");
        System.out.println("======================================");

        System.out.println(
                "Student ID         : "
                        + student.getUserId()
        );

        System.out.println(
                "Application Number : "
                        + student.getApplicationNumber()
        );

        System.out.println(
                "Name               : "
                        + student.getName()
        );

        System.out.println(
                "Email              : "
                        + student.getEmail()
        );

        System.out.println(
                "Please remember your Application Number "
                        + "and Password."
        );

    } catch (Exception e) {

        System.out.println();
        System.out.println(
                "======================================"
        );

        System.out.println(
                "       REGISTRATION FAILED"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );
    }
}    private static void loginStudent() {

        System.out.println();
        System.out.println("========== STUDENT LOGIN ==========");

        String applicationNumber =
                InputUtil.readRequiredString(
                        scanner,
                        "Enter Application Number: "
                );

        String password =
                InputUtil.readRequiredString(
                        scanner,
                        "Enter Password: "
                );

        try {

            Student student =
                    authenticationService.login(
                            applicationNumber,
                            password
                    );

            /*
             * StudentAuthenticationService is responsible for
             * creating the STUDENT SecuritySession.
             */
            authorizationService.requireRole(Role.STUDENT);

            SecuritySession session =
                    authorizationService.getCurrentSession();

            if (!session.getUsername().equalsIgnoreCase(
                    student.getApplicationNumber())) {

                authenticationService.logout(student);

                throw new AuthorizationException(
                        "Student security session does not match the logged-in account."
                );
            }

            /*
             * Keep the existing in-memory compatibility list.
             * Persistent data remains stored in the database.
             */
            boolean alreadyLoaded = false;

            for (Student existingStudent : students) {

                if (existingStudent != null
                        && existingStudent.getApplicationNumber()
                                .equalsIgnoreCase(
                                        student.getApplicationNumber())) {

                    alreadyLoaded = true;
                    break;
                }
            }

            if (!alreadyLoaded) {
                students.add(student);
            }

            System.out.println();
            System.out.println("======================================");
            System.out.println("       STUDENT LOGIN SUCCESSFUL");
            System.out.println("======================================");
            System.out.println(
                    "Welcome, " + student.getName() + "!"
            );
            System.out.println(
                    "Application Number : "
                            + student.getApplicationNumber()
            );
            System.out.println(
                    "Role               : "
                            + session.getRole()
            );
            System.out.println(
                    "Session            : ACTIVE"
            );
            System.out.println("======================================");

            studentDashboard(student);

        } catch (Exception e) {

            if (SecurityContext.isAuthenticated()) {
                SecurityContext.clear();
            }

            System.out.println();
            System.out.println(
                    "Login failed: " + e.getMessage()
            );
        }
    }    private static void studentDashboard(Student student) {

        if (!requireStudentSession(student)) {
            return;
        }

        while (
                student.isAuthenticated()
                        && SecurityContext.isAuthenticated()
        ) {

            if (!requireStudentSession(student)) {
                return;
            }

            student.displayDashboard();

            System.out.println();
            System.out.println("1. View Profile");
            System.out.println("2. View Application");
            System.out.println("3. View Rank");
            System.out.println("4. Course Preferences");
            System.out.println("5. Submit Application");
            System.out.println("6. Register for Counselling");
            System.out.println("7. Seat Allotment");
            System.out.println("8. Scholarship");
            System.out.println("9. Hostel");
            System.out.println("10. Transport");
            System.out.println("11. Exam Management");
            System.out.println("12. Generate Hall Ticket");
            System.out.println("13. Finance & Payment");
            System.out.println("14. Notifications");
            System.out.println("15. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    student.displayDashboard();
                    break;

                case "2":
                    viewApplication(student);
                    break;

                case "3":
                    viewStudentRank(student);
                    break;

                case "4":
                    manageCoursePreferences(student);
                    break;

                case "5":
                    submitApplication(student);
                    break;

                case "6":
                    registerForCounselling(student);
                    break;

                case "7":
                    allocationService.displayStudentAllocation(
                            student.getApplicationNumber()
                    );
                    break;

                case "8":
                    viewScholarship(student);
                    break;

                case "9":
                    manageHostel(student);
                    break;

                case "10":
                    manageTransport(student);
                    break;

                case "11":
                    manageStudentExam(student);
                    break;

                case "12":
                    generateHallTicket(student);
                    break;

                case "13":
                    managePayment(student);
                    break;
                case "14":
                        viewStudentNotifications(student);
                        break;    

                case "15":
                    authenticationService.logout(student);
                    System.out.println();
                    System.out.println(
                            "Student logged out successfully."
                    );
                    break;

                default:
                    System.out.println(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    private static void viewApplication(Student student) {

        if (!requireStudentSession(student)) {
            return;
        }


    try {

        if (student.getApplication() == null) {

    Application application =
            applicationService.createApplication(
                    student
            );

    System.out.println();

    System.out.println(
            "Application is ready."
    );

    System.out.println(
            "Application Number : "
                    + application.getApplicationNumber()
    );
}

        applicationService.displayApplication(student);

    } catch (Exception e) {

        System.out.println();
        System.out.println(
                "Unable to view application: "
                        + e.getMessage()
        );
    }
    }
    private static void manageCoursePreferences(Student student) {

        if (!requireStudentSession(student)) {
            return;
        }


    try {

        if (student.getApplication() == null) {

            applicationService.createApplication(student);

            System.out.println();
            System.out.println(
                    "Application created successfully."
            );
        }

        if (!student.getApplication()
                .getPreferences().isEmpty()) {

            applicationService.displayCoursePreferences(student);

            System.out.println();
            System.out.println(
                    "Course preferences have already been added."
            );

            return;
        }

        courseService.displayCourses();

        System.out.println();
        System.out.println(
                "Enter course codes in your preferred order."
        );
        System.out.println(
                "Enter DONE when you have finished."
        );

        int preferenceNumber = 1;

        while (true) {

            System.out.print(
                    "Preference " +
                            preferenceNumber +
                            ": "
            );

            String courseCode =
                    scanner.nextLine().trim();

            if (courseCode.equalsIgnoreCase("DONE")) {

                if (preferenceNumber == 1) {

                    System.out.println(
                            "Please select at least one course."
                    );

                    continue;
                }

                break;
            }

            Course course =
                    courseService.findCourse(courseCode);

            if (course == null) {

                System.out.println(
                        "Invalid course code. Please try again."
                );

                continue;
            }

            try {

                applicationService.addCoursePreference(
                        student,
                        course.getCourseCode(),
                        preferenceNumber
                );

                System.out.println(
                        "Added: " +
                                course.getCourseName()
                );

                preferenceNumber++;

            } catch (Exception e) {

                System.out.println(
                        "Unable to add preference: " +
                                e.getMessage()
                );
            }
        }

        System.out.println();
        System.out.println(
                "Course preferences saved successfully."
        );

        applicationService.displayCoursePreferences(student);

    } catch (Exception e) {

        System.out.println();
        System.out.println(
                "Unable to manage course preferences: "
                        + e.getMessage()
        );
    }
    }
    private static void submitApplication(Student student) {

        if (!requireStudentSession(student)) {
            return;
        }


    try {

        if (student.getApplication() == null) {

            System.out.println();
            System.out.println(
                    "No application has been created yet."
            );

            System.out.println(
                    "Please add your course preferences first."
            );

            return;
        }

        if (student.getApplication()
                .getPreferences().isEmpty()) {

            System.out.println();
            System.out.println(
                    "Cannot submit application."
            );

            System.out.println(
                    "Please select at least one course preference."
            );

            return;
        }

        System.out.println();
        System.out.println("======================================");
        System.out.println("        APPLICATION SUBMISSION");
        System.out.println("======================================");

        applicationService.displayApplication(student);

        System.out.println();
        System.out.print(
                "Are you sure you want to submit? (YES/NO): "
        );

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("YES")) {

            System.out.println(
                    "Application submission cancelled."
            );

            return;
        }

        applicationService.submitApplication(student);

/*
 * Create student notification after successful submission.
 */
try {

    createNotificationOnce(
            student,
            "APPLICATION_SUBMITTED",
            () -> notificationService.notifyApplicationSubmitted(
                    student
            )
    );

} catch (Exception notificationException) {

    System.out.println(
            "Warning: Application notification could not be created."
    );

    System.out.println(
            "Reason: "
                    + notificationException.getMessage()
    );
}

        System.out.println();
        System.out.println("======================================");
        System.out.println("    APPLICATION SUBMITTED SUCCESSFULLY");
        System.out.println("======================================");

        System.out.println(
                "Application Number : "
                        + student.getApplicationNumber()
        );

        System.out.println(
                "Status             : "
                        + student.getApplication().getStatus()
        );

    } catch (Exception e) {

        System.out.println();
        System.out.println(
                "Unable to submit application: "
                        + e.getMessage()
        );
    }
    }    private static void adminDashboard() {

        if (!requireAdminAccess()) {
            return;
        }

        while (true) {

            if (!requireAdminAccess()) {
                return;
            }

            System.out.println();
            System.out.println("======================================");
            System.out.println("          ADMIN DASHBOARD");
            System.out.println("======================================");
            System.out.println("1. View Submitted Applications");
            System.out.println("2. Verify Application");
            System.out.println("3. Check Eligibility");
            System.out.println("4. Generate Rank List");
            System.out.println("5. View Counselling Students");
            System.out.println("6. Allocate Seats");
            System.out.println("7. View Seat Allocation");
            System.out.println("8. View Waitlist");
            System.out.println("9. View Seat Matrix");
            System.out.println("10. Create Exam Schedules");
            System.out.println("11. View Exam Schedules");
            System.out.println("12. View Exam Centres");
            System.out.println("13. View Exam Allocations");
            System.out.println("14. View Hostel Allocations");
            System.out.println("15. View Hostel Occupancy");
            System.out.println("16. Allocate Hostel by Rank");
            System.out.println("17. View Bus Routes");
            System.out.println("18. View Buses");
            System.out.println("19. View Transport Allocations");
            System.out.println("20. View Route-wise Occupancy");
            System.out.println("21. View Bus-wise Occupancy");
            System.out.println("22. Allocate Transport by Rank");
            System.out.println("24. Exam Management");
            System.out.println("25. Seat Management");
            System.out.println("27. Notification Management");
            System.out.println("26. Logout");
            System.out.print("Enter choice: ");

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    verificationService
                            .displaySubmittedApplications();
                    break;

                case "2":
                    verifyApplication();
                    break;

                case "3":
                    checkEligibility();
                    break;

                case "4":
                    rankingService.displayRankList();

                    /*
                     * Notify loaded students whose rank has been
                     * generated. The helper prevents duplicates.
                     */
                    for (Student student : students) {

                        if (student == null
                                || student.getRank() == null) {
                            continue;
                        }

                        createNotificationOnce(
                                student,
                                "RANK_GENERATED",
                                () -> notificationService
                                        .notifyRankGenerated(student)
                        );
                    }

                    break;

                case "5":
                    counsellingService
                            .displayCounsellingStudents();
                    break;

                case "6":
                    allocateSeats();
                    break;

                case "7":
                    allocationService
                            .displayAllAllocations();
                    break;

                case "8":
                    allocationService
                            .displayWaitlist();
                    break;

                case "9":
                    seatMatrixService
                            .displayAllSeatMatrices();
                    break;

                case "10":
                    createExamSchedule();
                    break;

                case "11":
                    examService.displayExamSchedules();
                    break;

                case "12":
                    examService.displayExamCentres();
                    break;

                case "13":
                    examService.displayAllExamAllocations();
                    break;

                case "14":
                    hostelService.displayAllAllocations();
                    break;

                case "15":
                    hostelService.displayHostelOccupancy();
                    break;

                case "16":
                    allocateHostelByRank();
                    break;

                case "17":
                    transportService.displayRoutes();
                    break;

                case "18":
                    transportService.displayBuses();
                    break;

                case "19":
                    transportService.displayAllAllocations();
                    break;

                case "20":
                    transportService.displayRouteOccupancy();
                    break;

                case "21":
                    transportService.displayBusOccupancy();
                    break;

                case "22":
                    allocateTransportByRank();
                    break;

                case "24":
                    manageAdminExams();
                    break;

                case "25":
                    seatManagementMenu();
                    break;

                case "27":
                    adminNotificationManagement();
                    break;

                case "26":
                    adminAuthenticationService.logout();

                    System.out.println();
                    System.out.println(
                            "Admin logged out successfully."
                    );

                    return;

                default:
                    System.out.println(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }


    private static void seatManagementMenu() {

        if (!requireAdminAccess()) {
            return;
        }


    while (true) {

        System.out.println();
        System.out.println("======================================");
        System.out.println("          SEAT MANAGEMENT");
        System.out.println("======================================");

        System.out.println("1. Allocate Seats");
        System.out.println("2. View All Allocations");
        System.out.println("3. View Waitlist");
        System.out.println("4. Release Student Seat");
        System.out.println("5. Promote Waitlisted Student");
        System.out.println("6. View Seat Availability");
        System.out.println("0. Back");

        System.out.println("======================================");

        System.out.print("Enter your choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {

            case "1":
                allocateSeats();
                break;

            case "2":
                viewAllAllocations();
                break;

            case "3":
                viewWaitlist();
                break;

            case "4":
                releaseStudentSeat();
                break;

            case "5":
                promoteWaitlistedStudent();
                break;

            case "6":
                viewSeatAvailability();
                break;

            case "0":
                return;

            default:
                System.out.println(
                        "Invalid choice. Please try again."
                );
        }
    }
}
private static void allocateSeats() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();
    System.out.println("======================================");
    System.out.println("          SEAT ALLOCATION");
    System.out.println("======================================");

    try {

        allocationService.allocateSeats();

        for (Student student : students) {

            if (student == null) {
                continue;
            }

            try {

                SeatAllocation allocation =
                        allocationService.getAllocation(
                                student.getApplicationNumber()
                        );

                if (allocation != null) {
                    notifySeatAllocation(
                            student,
                            allocation
                    );
                }

            } catch (Exception notificationException) {

                System.out.println(
                        "Warning: Seat allocation notification could not be created for "
                                + student.getApplicationNumber()
                );

                System.out.println(
                        "Reason: "
                                + notificationException.getMessage()
                );
            }
        }

    } catch (Exception e) {

        System.out.println();
        System.out.println(
                "Seat allocation failed."
        );

        System.out.println(
                "Reason: " + e.getMessage()
        );
    }
}

private static void viewAllAllocations() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();
    System.out.println("======================================");
    System.out.println("        ALL SEAT ALLOCATIONS");
    System.out.println("======================================");

    try {

        allocationService.displayAllAllocations();

    } catch (Exception e) {

        System.out.println(
                "Unable to display allocations."
        );

        System.out.println(
                "Reason: " + e.getMessage()
        );
    }
}

private static void viewWaitlist() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();
    System.out.println("======================================");
    System.out.println("             WAITLIST");
    System.out.println("======================================");

    try {

        waitlistService.displayWaitlist();

    } catch (Exception e) {

        System.out.println(
                "Unable to display waitlist."
        );

        System.out.println(
                "Reason: " + e.getMessage()
        );
    }
}

private static void releaseStudentSeat() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();
    System.out.println("======================================");
    System.out.println("          RELEASE STUDENT SEAT");
    System.out.println("======================================");

    System.out.print(
            "Enter Application Number: "
    );

    String applicationNumber =
            scanner.nextLine().trim();

    if (applicationNumber.isBlank()) {

        System.out.println(
                "Application number cannot be empty."
        );

        return;
    }

    try {

        seatReleaseService.releaseSeat(
                applicationNumber
        );

    } catch (Exception e) {

        System.out.println();

        System.out.println(
                "Seat release failed."
        );

        System.out.println(
                "Reason: " + e.getMessage()
        );
    }
}

private static void promoteWaitlistedStudent() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();
    System.out.println("======================================");
    System.out.println("       WAITLIST PROMOTION");
    System.out.println("======================================");

    try {

        boolean promoted =
                waitlistPromotionService
                        .promoteNextStudent();

        if (!promoted) {

            System.out.println();

            System.out.println(
                    "No student was promoted."
            );
        }

    } catch (Exception e) {

        System.out.println();

        System.out.println(
                "Waitlist promotion failed."
        );

        System.out.println(
                "Reason: " + e.getMessage()
        );
    }
}

private static void viewSeatAvailability() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();
    System.out.println("======================================");
    System.out.println("        SEAT AVAILABILITY");
    System.out.println("======================================");

    try {

        com.college.admission.jpa.SeatJpaRepository
                seatRepository =
                new com.college.admission.jpa
                        .SeatJpaRepository();

        java.util.List<
                com.college.admission.model.Course
        > courses =
                new com.college.admission.jpa
                        .CourseJpaRepository()
                        .findAll();

        if (courses == null
                || courses.isEmpty()) {

            System.out.println(
                    "No courses found."
            );

            return;
        }

        for (com.college.admission.model.Course course :
                courses) {

            long available =
                    seatRepository
                            .countAvailableSeats(
                                    course.getCourseCode()
                            );

            System.out.println();

            System.out.println(
                    "Course       : "
                            + course.getCourseCode()
            );

            System.out.println(
                    "Course Name   : "
                            + course.getCourseName()
            );

            System.out.println(
                    "Total Seats  : "
                            + course.getTotalSeats()
            );

            System.out.println(
                    "Available    : "
                            + available
            );

            System.out.println(
                    "Allocated    : "
                            + (course.getTotalSeats()
                            - available)
            );

            System.out.println(
                    "--------------------------------------"
            );
        }

    } catch (Exception e) {

        System.out.println();

        System.out.println(
                "Unable to retrieve seat availability."
        );

        System.out.println(
                "Reason: " + e.getMessage()
        );
    }
}

    private static void verifyApplication() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();
    System.out.println("========== VERIFY APPLICATION ==========");

    String applicationNumber =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Application Number: "
            );

    Student student =
            verificationService.findSubmittedApplication(
                    applicationNumber
            );

    if (student == null) {

        System.out.println();
        System.out.println(
                "Submitted application not found."
        );

        return;
    }

    System.out.println();
    System.out.println("Student Name    : "
            + student.getName());

    System.out.println("Application No. : "
            + student.getApplicationNumber());

    System.out.println("Category        : "
            + student.getCategory());

    System.out.println("Percentage      : "
            + student.getPercentage());

    System.out.println();
    System.out.print(
            "Verify this application? (YES/NO): "
    );

    String confirmation =
            scanner.nextLine().trim();

    if (!confirmation.equalsIgnoreCase("YES")) {

        System.out.println(
                "Application verification cancelled."
        );

        return;
    }

    try {

        verificationService.verifyApplication(
        student
);

/*
 * Notify the student after successful verification.
 */
try {

    createNotificationOnce(
            student,
            "APPLICATION_VERIFIED",
            () -> notificationService.notifyApplicationVerified(
                    student
            )
    );

} catch (Exception notificationException) {

    System.out.println(
            "Warning: Verification notification could not be created."
    );

    System.out.println(
            "Reason: "
                    + notificationException.getMessage()
    );
}

        System.out.println();
        System.out.println(
                "Application verified successfully."
        );

        System.out.println(
                "Status : "
                        + student.getApplication().getStatus()
        );

    } catch (Exception e) {

        System.out.println();
        System.out.println(
                "Verification failed: "
                        + e.getMessage()
        );
    }
    }

    private static void checkEligibility() {

    if (!requireAdminAccess()) {
        return;
    }

    System.out.println();
    System.out.println(
            "========== ELIGIBILITY CHECK =========="
    );

    String applicationNumber =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Application Number: "
            );

    try {

        /*
         * Find the application directly from MySQL.
         *
         * Do NOT use findSubmittedApplication() here because
         * the application has already been VERIFIED.
         */
        Application application =
                verificationService.findApplicationByNumber(
                        applicationNumber
                );

        if (application == null) {

            System.out.println();
            System.out.println(
                    "Application not found."
            );

            return;
        }

        /*
         * Get the student associated with the application.
         */
        Student student =
                application.getStudent();

        if (student == null) {

            System.out.println();
            System.out.println(
                    "Student not found for this application."
            );

            return;
        }

        /*
         * Application must be VERIFIED before
         * eligibility can be checked.
         */
        if (application.getStatus()
                != com.college.admission.enums.ApplicationStatus.VERIFIED) {

            System.out.println();
            System.out.println(
                    "Eligibility can only be checked for "
                            + "VERIFIED applications."
            );

            System.out.println(
                    "Current Status : "
                            + application.getStatus()
            );

            return;
        }

        /*
         * Update eligibility status.
         *
         * The service checks the student's percentage.
         * Current rule: minimum 50%.
         */
        verificationService.updateEligibilityStatus(
                student
        );

        System.out.println();
        System.out.println(
                "======================================"
        );
        System.out.println(
                "     ELIGIBILITY CHECK COMPLETED"
        );
        System.out.println(
                "======================================"
        );

        System.out.println(
                "Student Name : "
                        + student.getName()
        );

        System.out.println(
                "Application  : "
                        + application.getApplicationNumber()
        );

        System.out.println(
                "Percentage   : "
                        + student.getPercentage()
        );

        System.out.println(
                "Final Status : "
                        + application.getStatus()
        );

        System.out.println(
                "======================================"
        );

    } catch (Exception e) {

        System.out.println();
        System.out.println(
                "Eligibility check failed: "
                        + e.getMessage()
        );
    }
}

    private static void viewStudentRank(Student student) {

        if (!requireStudentSession(student)) {
            return;
        }


    System.out.println();
    System.out.println("======================================");
    System.out.println("           STUDENT RANK");
    System.out.println("======================================");

    if (student.getRank() == null) {

        System.out.println(
                "Rank has not been generated yet."
        );

        return;
    }

    System.out.println(
            "Student Name : " + student.getName()
    );

    System.out.println(
            "Application No. : "
                    + student.getApplicationNumber()
    );

    System.out.println(
            "Percentage : "
                    + student.getPercentage()
    );

    System.out.println(
            "Rank : "
                    + student.getRank()
    );
    }
    private static void registerForCounselling(
        Student student
) {

        if (!requireStudentSession(student)) {
            return;
        }


    try {

        counsellingService.registerForCounselling(
                student
        );

        System.out.println();
        System.out.println("======================================");
        System.out.println("   COUNSELLING REGISTRATION SUCCESS");
        System.out.println("======================================");

        System.out.println(
                "Application Number : "
                        + student.getApplicationNumber()
        );

        System.out.println(
                "Rank               : "
                        + student.getRank()
        );

        System.out.println(
                "Status             : "
                        + student.getApplication()
                        .getCounsellingStatus()
        );

    } catch (Exception e) {

        System.out.println();
        System.out.println(
                "Counselling registration failed: "
                        + e.getMessage()
        );
    }
    }

    private static void viewExamDetails(Student student) {

        if (!requireStudentSession(student)) {
            return;
        }


    System.out.println();
    System.out.println(
            "=========================================="
    );
    System.out.println(
            "             EXAM DETAILS"
    );
    System.out.println(
            "=========================================="
    );

    /*
     * A student must have a submitted/processed
     * application before receiving exam allocation.
     */
    if (student.getApplication() == null) {

        System.out.println(
                "Application has not been created."
        );

        return;
    }

    ExamSchedule schedule =
            examService.getLatestSchedule();

    if (schedule == null) {

        System.out.println(
                "No examination schedule has been published yet."
        );

        return;
    }

    /*
     * Allocate only if the student does not already
     * have an examination seat.
     */
    if (!examService.hasExamAllocation(
            student.getApplicationNumber()
    )) {

        try {

            examService.allocateExam(
                    student,
                    schedule
            );

            System.out.println(
                    "Exam centre and seat allocated successfully."
            );

        } catch (IllegalStateException e) {

            System.out.println(
                    "Exam allocation failed: "
                            + e.getMessage()
            );

            return;
        }
    }

    /*
     * Display the student's complete examination details.
     */
    examService.displayStudentExamAllocation(
            student
    );
    }
    private static void createExamSchedule() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();
    System.out.println(
            "=========================================="
    );
    System.out.println(
            "         CREATE EXAM SCHEDULE"
    );
    System.out.println(
            "=========================================="
    );

    String examCode =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Exam Code: "
            );

    String examName =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Exam Name: "
            );

    LocalDate examDate;

    while (true) {

        String dateInput =
                InputUtil.readRequiredString(
                        scanner,
                        "Enter Exam Date (DD/MM/YYYY): "
                );

        try {

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "dd/MM/yyyy"
                    );

            examDate =
                    LocalDate.parse(
                            dateInput,
                            formatter
                    );

            if (examDate.isBefore(
                    LocalDate.now()
            )) {

                System.out.println(
                        "Exam date cannot be in the past."
                );

                continue;
            }

            break;

        } catch (DateTimeParseException e) {

            System.out.println(
                    "Invalid date. Please use DD/MM/YYYY."
            );
        }
    }

    ExamSession session;

    while (true) {

        System.out.println();
        System.out.println(
                "Select Exam Session:"
        );

        System.out.println(
                "1. Morning"
        );

        System.out.println(
                "2. Afternoon"
        );

        System.out.print(
                "Enter choice: "
        );

        String choice =
                scanner.nextLine().trim();

        switch (choice) {

            case "1":
                session = ExamSession.MORNING;
                break;

            case "2":
                session = ExamSession.AFTERNOON;
                break;

            default:
                System.out.println(
                        "Invalid choice. Please select 1 or 2."
                );

                continue;
        }

        break;
    }

    try {

        examService.createExam(
                examCode,
                examName,
                "Admission entrance examination for"
                + examName
        );

        examService.createExamSchedule(
                examCode,
                examDate,
                session
        );

        createNotificationForAllLoadedStudents(
                "EXAM_SCHEDULED",
                examName,
                examDate.format(
                        DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy"
                        )
                ),
                session.toString()
        );

        System.out.println();
        System.out.println(
                "Exam schedule created successfully."
        );

        System.out.println(
                "Exam Code : " + examCode
        );

        System.out.println(
                "Exam Name : " + examName
        );

        System.out.println(
                "Date      : "
                        + examDate.format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy"
                                )
                        )
        );

        System.out.println(
                "Session   : " + session
        );

    } catch (IllegalArgumentException e) {

        System.out.println();
        System.out.println(
                "Unable to create exam schedule."
        );

        System.out.println(
                "Reason: " + e.getMessage()
        );
    }
    }
    private static void generateHallTicket(
        Student student
) {

        if (!requireStudentSession(student)) {
            return;
        }


    System.out.println();
    System.out.println(
            "============================================================"
    );
    System.out.println(
            "                HALL TICKET GENERATION"
    );
    System.out.println(
            "============================================================"
    );

    if (student == null) {
        System.out.println(
                "Student information is not available."
        );
        return;
    }

    /*
     * Step 1:
     * Check whether the student has an admission seat.
     */

    SeatAllocation seatAllocation =
            allocationService.getAllocation(
                    student.getApplicationNumber()
            );

    if (seatAllocation == null) {

        System.out.println();
        System.out.println(
                "Hall ticket cannot be generated."
        );

        System.out.println(
                "Reason: Admission seat has not been allotted yet."
        );

        System.out.println(
                "Please complete counselling and seat allocation first."
        );

        return;
    }

    /*
     * Step 2:
     * Check whether the student has an examination allocation.
     */

    ExamAllocation examAllocation =
            examService.getExamAllocation(
                    student.getApplicationNumber()
            );

    if (examAllocation == null) {

        System.out.println();
        System.out.println(
                "Hall ticket cannot be generated."
        );

        System.out.println(
                "Reason: Examination centre and seat have not "
                        + "been allocated yet."
        );

        System.out.println(
                "Please check your Exam Details first."
        );

        return;
    }

    /*
     * Step 3:
     * Create the HallTicket object.
     */

    try {

        HallTicket hallTicket =
                hallTicketService.createHallTicket(
                        student,
                        seatAllocation,
                        examAllocation
                );

        createNotificationOnce(
                student,
                "HALL_TICKET_GENERATED",
                () -> notificationService.notifyHallTicketGenerated(
                        student
                )
        );

        /*
         * Step 4:
         * Generate the text file using Java Writer.
         */

        String fileName =
                hallTicketService.generateHallTicketFile(
                        hallTicket
                );

        /*
         * Step 5:
         * Display the hall ticket in the console.
         */

        hallTicketService.displayHallTicket(
                hallTicket
        );

        System.out.println();

        System.out.println(
                "Hall ticket generated successfully."
        );

        System.out.println(
                "File Location : "
                        + fileName
        );

        /*
         * Step 6:
         * Read the generated file using Java Reader.
         */

        System.out.println();
        System.out.println(
                "Do you want to read the generated hall ticket file?"
        );

        System.out.println(
                "1. Yes"
        );

        System.out.println(
                "2. No"
        );

        System.out.print(
                "Enter choice: "
        );

        String choice =
                scanner.nextLine().trim();

        if (choice.equals("1")) {

            hallTicketService.readHallTicketFile(
                    fileName
            );
        }

    } catch (IllegalStateException e) {

        System.out.println();
        System.out.println(
                "Unable to generate hall ticket."
        );

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );
    }
    }
    private static void viewScholarship(
        Student student
) {

        if (!requireStudentSession(student)) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                  SCHOLARSHIP MANAGEMENT"
    );

    System.out.println(
            "=============================================================="
    );

    if (student == null) {

        System.out.println(
                "Student information is not available."
        );

        return;
    }

    /*
     * Step 1:
     * Check whether the student has a rank.
     */

    if (student.getRank() == null) {

        System.out.println();

        System.out.println(
                "Scholarship cannot be calculated yet."
        );

        System.out.println(
                "Reason: Student rank has not been generated."
        );

        return;
    }

    /*
     * Step 2:
     * Get the student's actual admission seat allocation.
     */

    SeatAllocation seatAllocation =
            allocationService.getAllocation(
                    student.getApplicationNumber()
            );

    if (seatAllocation == null) {

        System.out.println();

        System.out.println(
                "Scholarship cannot be calculated yet."
        );

        System.out.println(
                "Reason: Admission seat has not been allotted."
        );

        System.out.println(
                "Complete counselling and seat allocation first."
        );

        return;
    }

    /*
     * Step 3:
     * Find the actual allocated course.
     */

    Course course =
            courseService.findCourse(
                    seatAllocation.getCourseCode()
            );

    if (course == null) {

        System.out.println();

        System.out.println(
                "Unable to determine the allocated course."
        );

        return;
    }

    /*
     * Step 4:
     * Calculate scholarship.
     */

    try {

        Scholarship scholarship =
                scholarshipService.calculateScholarship(
                        student,
                        seatAllocation,
                        course
                );

        /*
         * Step 5:
         * Display scholarship details.
         */

        scholarship.displayScholarship();

        createNotificationOnce(
                student,
                "SCHOLARSHIP_GENERATED",
                () -> notificationService.notifyScholarshipGenerated(
                        student,
                        scholarship.getScholarshipPercentage(),
                        scholarship.getScholarshipAmount()
                )
        );

    } catch (IllegalArgumentException
             | IllegalStateException e) {

        System.out.println();

        System.out.println(
                "Unable to calculate scholarship."
        );

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );
    }
    }
    private static void managePayment(
        Student student
) {

        if (!requireStudentSession(student)) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                  FINANCE & PAYMENT"
    );

    System.out.println(
            "=============================================================="
    );

    if (student == null) {

        System.out.println(
                "Student information is not available."
        );

        return;
    }

    /*
     * Step 1:
     * Check whether rank has been generated.
     */

    if (student.getRank() == null) {

        System.out.println(
                "Payment cannot be processed yet."
        );

        System.out.println(
                "Reason: Student rank has not been generated."
        );

        return;
    }

    /*
     * Step 2:
     * Get actual admission seat allocation.
     */

    SeatAllocation seatAllocation =
            allocationService.getAllocation(
                    student.getApplicationNumber()
            );

    if (seatAllocation == null) {

        System.out.println(
                "Payment cannot be processed yet."
        );

        System.out.println(
                "Reason: Admission seat has not been allotted."
        );

        return;
    }

    /*
     * Step 3:
     * Get actual allocated course.
     */

    Course course =
            courseService.findCourse(
                    seatAllocation.getCourseCode()
            );

    if (course == null) {

        System.out.println(
                "Unable to determine allocated course."
        );

        return;
    }

    /*
     * Step 4:
     * Get or calculate scholarship.
     */

    Scholarship scholarship =
            scholarshipService.getScholarship(
                    student.getApplicationNumber()
            );

    boolean scholarshipWasCreated = false;

    if (scholarship == null) {

        try {

            scholarship =
                    scholarshipService.calculateScholarship(
                            student,
                            seatAllocation,
                            course
                    );

            scholarshipWasCreated = true;

        } catch (IllegalArgumentException
                 | IllegalStateException e) {

            System.out.println(
                    "Unable to calculate scholarship."
            );

            System.out.println(
                    "Reason: "
                            + e.getMessage()
            );

            return;
        }
    }

    /*
     * Step 5:
     * Create payment record if it doesn't already exist.
     */

    Payment payment;

    boolean paymentWasCreated =
            !paymentService.hasPayment(
                    student.getApplicationNumber()
            );

    try {

        payment =
                paymentService.createPayment(
                        student,
                        seatAllocation,
                        course,
                        scholarship
                );

        if (paymentWasCreated) {

            createNotificationOnce(
                    student,
                    "PAYMENT_CREATED",
                    () -> notificationService.notifyPaymentCreated(
                            student,
                            payment.getNetPayableAmount()
                    )
            );
        }

    } catch (IllegalArgumentException
             | IllegalStateException e) {

        System.out.println(
                "Unable to create payment record."
        );

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );

        return;
    }

    /*
     * Step 6:
     * Display current payment information.
     */

    payment.displayPaymentDetails();

    /*
     * If the student has already paid the complete
     * amount, there is nothing more to pay.
     */

    if (payment.isFullyPaid()) {

        System.out.println();
        System.out.println(
                "Your fee has already been paid in full."
        );

        return;
    }

    /*
     * Step 7:
     * Ask whether the student wants to make a payment.
     */

    System.out.println();

    System.out.println(
            "1. Make Payment"
    );

    System.out.println(
            "2. Back"
    );

    System.out.print(
            "Enter choice: "
    );

    String choice =
            scanner.nextLine().trim();

    if (!choice.equals("1")) {

        return;
    }

    /*
     * Step 8:
     * Read payment amount.
     */

    BigDecimal amount;

    while (true) {

        System.out.print(
                "Enter Payment Amount: ₹"
        );

        String amountInput =
                scanner.nextLine().trim();

        try {

            amount =
                    new BigDecimal(
                            amountInput
                    );

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {

                System.out.println(
                        "Payment amount must be greater than zero."
                );

                continue;
            }

            if (amount.compareTo(
                    payment.getRemainingAmount()) > 0) {

                System.out.printf(
                        "Amount cannot exceed remaining amount "
                                + "of ₹%.2f%n",
                        payment.getRemainingAmount()
                );

                continue;
            }

            break;

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid amount."
            );
        }
    }

    /*
     * Step 9:
     * Select payment mode.
     */

    String paymentMode;

    while (true) {

        System.out.println();

        System.out.println(
                "Select Payment Mode:"
        );

        System.out.println(
                "1. UPI"
        );

        System.out.println(
                "2. Debit Card"
        );

        System.out.println(
                "3. Credit Card"
        );

        System.out.println(
                "4. Net Banking"
        );

        System.out.println(
                "5. Cash"
        );

        System.out.print(
                "Enter choice: "
        );

        String modeChoice =
                scanner.nextLine().trim();

        switch (modeChoice) {

            case "1":
                paymentMode = "UPI";
                break;

            case "2":
                paymentMode = "DEBIT_CARD";
                break;

            case "3":
                paymentMode = "CREDIT_CARD";
                break;

            case "4":
                paymentMode = "NET_BANKING";
                break;

            case "5":
                paymentMode = "CASH";
                break;

            default:

                System.out.println(
                        "Invalid payment mode."
                );

                continue;
        }

        break;
    }

    /*
     * Step 10:
     * Process the simulated payment.
     */

    try {

        paymentService.makePayment(
        student,
        amount,
        paymentMode
);

        Payment updatedPayment =
                paymentService.getPayment(
                        student.getApplicationNumber()
                );

        if (updatedPayment != null
                && "PAID".equalsIgnoreCase(
                        updatedPayment.getPaymentStatus()
                )) {

            createNotificationOnce(
                    student,
                    "PAYMENT_COMPLETED",
                    () -> notificationService.notifyPaymentCompleted(
                            student,
                            updatedPayment.getAmountPaid()
                    )
            );
        }

System.out.println();

System.out.println(
        "Payment processed successfully."
);

System.out.println(
        "Transaction ID : "
                + payment.getTransactionId()
);

System.out.printf(
        "Amount Paid    : ₹%.2f%n",
        payment.getAmountPaid()
);

System.out.printf(
        "Remaining      : ₹%.2f%n",
        payment.getRemainingAmount()
);

System.out.println(
        "Status         : "
                + payment.getPaymentStatus()
);

/*
 * Generate payment receipt.
 */

try {

    PaymentReceipt receipt =
            paymentReceiptService.createReceipt(
                    student,
                    seatAllocation,
                    course,
                    payment
            );

    paymentReceiptService.displayReceipt(
            receipt
    );

    String receiptFile =
            paymentReceiptService.generateReceiptFile(
                    receipt
            );

    System.out.println();

    System.out.println(
            "Payment receipt generated successfully."
    );

    System.out.println(
            "Receipt Location : "
                    + receiptFile
    );

    /*
     * Give the student the option to read
     * the generated receipt using BufferedReader.
     */

    System.out.println();

    System.out.println(
            "Do you want to read the generated receipt file?"
    );

    System.out.println(
            "1. Yes"
    );

    System.out.println(
            "2. No"
    );

    System.out.print(
            "Enter choice: "
    );

    String receiptChoice =
            scanner.nextLine().trim();

    if (receiptChoice.equals("1")) {

        paymentReceiptService.readReceiptFile(
                receiptFile
        );
    }

} catch (IllegalArgumentException
         | IllegalStateException e) {

    System.out.println();

    System.out.println(
            "Payment receipt generation failed."
    );

    System.out.println(
            "Reason: "
                    + e.getMessage()
    );
}

    } catch (IllegalArgumentException
             | IllegalStateException e) {

        System.out.println();

        System.out.println(
                "Payment failed."
        );

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );
    }
    }

    private static void manageHostel(
        Student student
) {

        if (!requireStudentSession(student)) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                  HOSTEL MANAGEMENT"
    );

    System.out.println(
            "=============================================================="
    );

    if (student == null) {

        System.out.println(
                "Student information is not available."
        );

        return;
    }

    /*
     * Step 1:
     * Student must have a rank.
     */

    if (student.getRank() == null) {

        System.out.println();

        System.out.println(
                "Hostel allocation cannot be processed yet."
        );

        System.out.println(
                "Reason: Student rank has not been generated."
        );

        return;
    }

    /*
     * Step 2:
     * Check whether hostel accommodation
     * has already been allocated.
     */

    HostelAllocation existingAllocation =
            hostelService.getAllocation(
                    student.getApplicationNumber()
            );

    if (existingAllocation != null) {

        System.out.println();

        System.out.println(
                "Hostel accommodation has already been allocated."
        );

        hostelService.displayStudentAllocation(
                student
        );

        return;
    }

    /*
     * Step 3:
     * Display hostels available for the student's gender.
     */

    hostelService.displayAvailableHostels(
            student.getGender()
    );

    /*
     * Step 4:
     * Ask the student for room type.
     */

    String roomType;

    while (true) {

        System.out.println();

        System.out.println(
                "Select Preferred Room Type:"
        );

        System.out.println(
                "1. Single Room"
        );

        System.out.println(
                "2. Double Room"
        );

        System.out.println(
                "3. Triple Room"
        );

        System.out.println(
                "4. Back"
        );

        System.out.print(
                "Enter choice: "
        );

        String choice =
                scanner.nextLine().trim();

        switch (choice) {

            case "1":
                roomType = "SINGLE";
                break;

            case "2":
                roomType = "DOUBLE";
                break;

            case "3":
                roomType = "TRIPLE";
                break;

            case "4":
                return;

            default:

                System.out.println(
                        "Invalid choice. Please select 1-4."
                );

                continue;
        }

        break;
    }

    /*
     * Step 5:
     * Display rooms of the selected room type
     * before allocation.
     */

    System.out.println();

    System.out.println(
            "Available "
                    + roomType
                    + " rooms:"
    );

    boolean availableRoomFound = false;

    for (Hostel hostel :
            hostelService.getHostels()) {

        if (hostel.getGender()
                != student.getGender()) {

            continue;
        }

        for (var room :
                hostel.getRooms()) {

            if (room.getRoomType()
                    .equalsIgnoreCase(roomType)
                    && room.hasAvailableBed()) {

                availableRoomFound = true;

                System.out.println(
                        "Hostel: "
                                + hostel.getHostelCode()
                                + " | Block: "
                                + hostel.getBlockName()
                                + " | Room: "
                                + room.getRoomNumber()
                                + " | Type: "
                                + room.getRoomType()
                                + " | Available Beds: "
                                + room.getAvailableBeds()
                );
            }
        }
    }

    if (!availableRoomFound) {

        System.out.println();

        System.out.println(
                "No "
                        + roomType
                        + " rooms are currently available."
        );

        return;
    }

    /*
     * Step 6:
     * Automatically allocate hostel, room and bed.
     */

    try {

        HostelAllocation allocation =
                hostelService.allocateHostel(
                        student,
                        roomType
                );

        createNotificationOnce(
                student,
                "HOSTEL_ALLOTTED",
                () -> notificationService.notifyHostelAllotted(
                        student,
                        allocation.getHostelName(),
                        allocation.getRoomNumber()
                )
        );

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "             HOSTEL ALLOCATED SUCCESSFULLY"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Hostel Code  : "
                        + allocation.getHostelCode()
        );

        System.out.println(
                "Hostel Name  : "
                        + allocation.getHostelName()
        );

        System.out.println(
                "Block        : "
                        + allocation.getBlockName()
        );

        System.out.println(
                "Room Number  : "
                        + allocation.getRoomNumber()
        );

        System.out.println(
                "Room Type    : "
                        + allocation.getRoomType()
        );

        System.out.println(
                "Bed Number   : "
                        + allocation.getBedNumber()
        );

        System.out.println(
                "=============================================================="
        );

    } catch (IllegalArgumentException
             | IllegalStateException e) {

        System.out.println();

        System.out.println(
                "Hostel allocation failed."
        );

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );
    }
    }

    private static void allocateHostelByRank() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "             HOSTEL ALLOCATION BY RANK"
    );

    System.out.println(
            "=============================================================="
    );

    String roomType;

    while (true) {

        System.out.println();

        System.out.println("Select Room Type:");

        System.out.println("1. Single Room");
        System.out.println("2. Double Room");
        System.out.println("3. Triple Room");
        System.out.println("4. Back");

        System.out.print("Enter choice: ");

        String choice =
                scanner.nextLine().trim();

        switch (choice) {

            case "1":
                roomType = "SINGLE";
                break;

            case "2":
                roomType = "DOUBLE";
                break;

            case "3":
                roomType = "TRIPLE";
                break;

            case "4":
                return;

            default:
                System.out.println(
                        "Invalid choice. Please select 1-4."
                );
                continue;
        }

        break;
    }

    System.out.println();

    System.out.println(
            "Starting hostel allocation according to student rank..."
    );

    System.out.println();

    hostelService.allocateHostelByRank(
            students,
            roomType
    );

    System.out.println();

    System.out.println(
            "Hostel allocation process completed."
    );

    System.out.println(
            "=============================================================="
    );
    }
    private static void manageTransport(
        Student student
) {

        if (!requireStudentSession(student)) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                  TRANSPORT MANAGEMENT"
    );

    System.out.println(
            "=============================================================="
    );

    if (student == null) {

        System.out.println(
                "Student information is not available."
        );

        return;
    }

    /*
     * Check whether transport has already
     * been allocated.
     */

    TransportAllocation existingAllocation =
            transportService.getAllocation(
                    student.getApplicationNumber()
            );

    if (existingAllocation != null) {

        System.out.println();

        System.out.println(
                "Transport has already been allocated."
        );

        transportService.displayStudentAllocation(
                student
        );

        return;
    }

    /*
     * Display available routes.
     */

    transportService.displayRoutes();

    String routeCode;

    while (true) {

        System.out.println();

        System.out.print(
                "Enter Route Code: "
        );

        routeCode =
                scanner.nextLine()
                        .trim()
                        .toUpperCase();

        BusRoute route =
                transportService.findRoute(
                        routeCode
                );

        if (route == null) {

            System.out.println(
                    "Invalid route code. Please try again."
            );

            continue;
        }

        break;
    }

    /*
     * Display selected route details.
     */

    BusRoute selectedRoute =
            transportService.findRoute(
                    routeCode
            );

    selectedRoute.displayRoute();

    /*
     * Display available buses.
     */

    transportService.displayAvailableBuses(
            routeCode
    );

    /*
     * Select boarding point.
     */

    String boardingPoint;

    while (true) {

        System.out.println();

        System.out.println(
                "Available Boarding Points:"
        );

        List<String> stops =
                selectedRoute.getStops();

        for (int i = 0; i < stops.size(); i++) {

            System.out.println(
                    (i + 1)
                            + ". "
                            + stops.get(i)
            );
        }

        System.out.print(
                "Enter boarding point number: "
        );

        String input =
                scanner.nextLine().trim();

        try {

            int choice =
                    Integer.parseInt(input);

            if (choice < 1
                    || choice > stops.size()) {

                System.out.println(
                        "Invalid boarding point."
                );

                continue;
            }

            boardingPoint =
                    stops.get(choice - 1);

            break;

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid number."
            );
        }
    }

    /*
     * Allocate transport automatically.
     */

    try {

        TransportAllocation allocation =
                transportService.allocateTransport(
                        student,
                        routeCode,
                        boardingPoint
                );

        createNotificationOnce(
                student,
                "TRANSPORT_ALLOTTED",
                () -> notificationService.notifyTransportAllotted(
                        student,
                        allocation.getRouteName(),
                        allocation.getBusNumber()
                )
        );

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "          TRANSPORT ALLOCATED SUCCESSFULLY"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Route Code        : "
                        + allocation.getRouteCode()
        );

        System.out.println(
                "Route Name        : "
                        + allocation.getRouteName()
        );

        System.out.println(
                "Boarding Point    : "
                        + allocation.getBoardingPoint()
        );

        System.out.println(
                "Bus Number        : "
                        + allocation.getBusNumber()
        );

        System.out.println(
                "Bus Registration  : "
                        + allocation.getBusRegistrationNumber()
        );

        System.out.println(
                "Bus Seat Number   : "
                        + allocation.getBusSeatNumber()
        );

        System.out.println(
                "=============================================================="
        );

    } catch (IllegalArgumentException
             | IllegalStateException e) {

        System.out.println();

        System.out.println(
                "Transport allocation failed."
        );

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );
    }
    }

    private static void allocateTransportByRank() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "           TRANSPORT ALLOCATION BY RANK"
    );

    System.out.println(
            "=============================================================="
    );

    transportService.displayRoutes();

    String routeCode;

    while (true) {

        System.out.println();

        System.out.print(
                "Enter Route Code: "
        );

        routeCode =
                scanner.nextLine()
                        .trim()
                        .toUpperCase();

        if (transportService.findRoute(
                routeCode
        ) == null) {

            System.out.println(
                    "Invalid route code."
            );

            continue;
        }

        break;
    }

    System.out.println();

    System.out.println(
            "Starting transport allocation according to rank..."
    );

    transportService.allocateTransportByRank(
            students,
            routeCode
    );

    System.out.println();

    System.out.println(
            "Transport allocation process completed."
    );

    System.out.println(
            "=============================================================="
    );
    } 


    private static void manageStudentExam(
        Student student
) {

        if (!requireStudentSession(student)) {
            return;
        }


    while (true) {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                    EXAM MANAGEMENT"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "1. View Available Exams"
        );

        System.out.println(
                "2. Register for Exam"
        );

        System.out.println(
                "3. View My Exam Registration"
        );

        System.out.println(
                "4. View Exam Schedules"
        );

        System.out.println(
                "5. View Exam Allocation"
        );

        System.out.println(
                "6. Back"
        );

        System.out.println();

        System.out.print(
                "Enter choice: "
        );

        String choice =
                scanner.nextLine().trim();

        switch (choice) {

            case "1":

                examService.displayExams();

                break;

            case "2":

                registerStudentForExam(
                        student
                );

                break;

            case "3":

                viewStudentExamRegistration(
                        student
                );

                break;

            case "4":

                examService.displayExamSchedules();

                break;

            case "5":

                examService.displayStudentExamAllocation(
                        student
                );

                break;

            case "6":

                return;

            default:

                System.out.println();

                System.out.println(
                        "Invalid choice. Please select 1-6."
                );
        }
    }
    }

    private static void registerStudentForExam(
        Student student
) {

        if (!requireStudentSession(student)) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                   EXAM REGISTRATION"
    );

    System.out.println(
            "=============================================================="
    );

    if (student == null) {

        System.out.println(
                "Student information is not available."
        );

        return;
    }

    if (examService.getExams().isEmpty()) {

        System.out.println();

        System.out.println(
                "No examinations are currently available."
        );

        System.out.println(
                "Please wait for the administrator to create an exam."
        );

        return;
    }

    /*
     * Display all available exams.
     */

    examService.displayExams();

    String examCode;

    while (true) {

        System.out.println();

        System.out.print(
                "Enter Exam Code: "
        );

        examCode =
                scanner.nextLine()
                        .trim()
                        .toUpperCase();

        Exam exam =
                examService.findExam(
                        examCode
                );

        if (exam == null) {

            System.out.println();

            System.out.println(
                    "Invalid exam code. Please try again."
            );

            continue;
        }

        break;
    }

    /*
     * Check whether the student has
     * already registered.
     */

    if (examService.isRegisteredForExam(
            student,
            examCode
    )) {

        System.out.println();

        System.out.println(
                "You are already registered for this examination."
        );

        examService.displayStudentExamRegistration(
                student,
                examCode
        );

        return;
    }

    /*
     * Check whether the exam has at least
     * one schedule.
     */

    List<ExamSchedule> schedules =
            examService.getSchedulesForExam(
                    examCode
            );

    if (schedules.isEmpty()) {

        System.out.println();

        System.out.println(
                "This examination does not have a schedule yet."
        );

        System.out.println(
                "Registration cannot be completed."
        );

        return;
    }

    try {

        ExamRegistration registration =
                examService.registerStudentForExam(
                        student,
                        examCode
                );

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "          EXAM REGISTERED SUCCESSFULLY"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Registration ID : "
                        + registration.getRegistrationId()
        );

        System.out.println(
                "Exam Code       : "
                        + registration.getExamCode()
        );

        System.out.println(
                "Exam Name       : "
                        + registration.getExamName()
        );

        System.out.println(
                "Student Name    : "
                        + registration.getStudentName()
        );

        System.out.println(
                "Status          : "
                        + registration.getStatus()
        );

        System.out.println(
                "=============================================================="
        );

    } catch (IllegalArgumentException e) {

        System.out.println();

        System.out.println(
                "Exam registration failed."
        );

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );
    }
    }

    private static void viewStudentExamRegistration(
        Student student
) {

        if (!requireStudentSession(student)) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "               MY EXAM REGISTRATIONS"
    );

    System.out.println(
            "=============================================================="
    );

    if (student == null) {

        System.out.println(
                "Student information is not available."
        );

        return;
    }

    boolean found = false;

    for (Exam exam :
            examService.getExams()) {

        ExamRegistration registration =
                examService.getExamRegistration(
                        student,
                        exam.getExamCode()
                );

        if (registration != null) {

            found = true;

            registration.displayRegistration();
        }
    }

    if (!found) {

        System.out.println(
                "You have not registered for any examination."
        );
    }

    System.out.println(
            "=============================================================="
    );
    }

    private static void manageAdminExams() {

        if (!requireAdminAccess()) {
            return;
        }


    while (true) {

        System.out.println();

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "                  ADMIN EXAM MANAGEMENT"
        );

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "1. Create Exam"
        );

        System.out.println(
                "2. View Exams"
        );

        System.out.println(
                "3. Create Exam Schedule"
        );

        System.out.println(
                "4. View Exam Schedules"
        );

        System.out.println(
                "5. View Exam Registrations"
        );

        System.out.println(
                "6. Allocate Exam Seats"
        );

        System.out.println(
                "7. View Exam Allocations"
        );

        System.out.println(
                "8. View Exam Centres"
        );

        System.out.println(
                "9. View Room / Seat Layout"
        );

        System.out.println(
                "10. Back"
        );

        System.out.println();

        System.out.print(
                "Enter choice: "
        );

        String choice =
                scanner.nextLine().trim();

        switch (choice) {

            case "1":
                createExamFromAdmin();
                break;

            case "2":
                examService.displayExams();
                break;

            case "3":
                createExamScheduleFromAdmin();
                break;

            case "4":
                examService.displayExamSchedules();
                break;

            case "5":
                examService.displayAllExamRegistrations();
                break;

            case "6":
                allocateExamSeatsFromAdmin();
                break;

            case "7":
                examService.displayAllExamAllocations();
                break;

            case "8":
                examService.displayExamCentres();
                break;

            case "9":
                displayExamRoomLayoutFromAdmin();
                break;

            case "10":
                return;

            default:
                System.out.println();
                System.out.println(
                        "Invalid choice. Please select 1-10."
                );
        }
    }
    }

    private static void createExamFromAdmin() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                       CREATE EXAM"
    );

    System.out.println(
            "=============================================================="
    );

    String examCode =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Exam Code: "
            ).toUpperCase();

    String examName =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Exam Name: "
            );

    String description =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Exam Description: "
            );

    try {

        examService.createExam(
                examCode,
                examName,
                description
        );

        System.out.println();

        System.out.println(
                "Exam created successfully."
        );

        System.out.println(
                "Exam Code : " + examCode
        );

    } catch (IllegalArgumentException e) {

        System.out.println();

        System.out.println(
                "Exam creation failed."
        );

        System.out.println(
                "Reason: " + e.getMessage()
        );
    }
    }

    private static void createExamScheduleFromAdmin() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                  CREATE EXAM SCHEDULE"
    );

    System.out.println(
            "=============================================================="
    );

    if (examService.getExams().isEmpty()) {

        System.out.println(
                "No exams exist."
        );

        System.out.println(
                "Create an exam first."
        );

        return;
    }

    examService.displayExams();

    String examCode =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Exam Code: "
            ).toUpperCase();

    LocalDate examDate;

    while (true) {

        System.out.print(
                "Enter Exam Date (DD/MM/YYYY): "
        );

        String dateInput =
                scanner.nextLine().trim();

        try {

            examDate =
                    LocalDate.parse(
                            dateInput,
                            DateTimeFormatter.ofPattern(
                                    "dd/MM/yyyy"
                            )
                    );

            break;

        } catch (DateTimeParseException e) {

            System.out.println(
                    "Invalid date. Use DD/MM/YYYY."
            );
        }
    }

    ExamSession session;

    while (true) {

        System.out.println();

        System.out.println(
                "Select Exam Session:"
        );

        System.out.println(
                "1. Morning"
        );

        System.out.println(
                "2. Afternoon"
        );

        System.out.print(
                "Enter choice: "
        );

        String choice =
                scanner.nextLine().trim();

        if (choice.equals("1")) {

            session = ExamSession.MORNING;
            break;

        } else if (choice.equals("2")) {

            session = ExamSession.AFTERNOON;
            break;

        } else {

            System.out.println(
                    "Invalid choice. Select 1 or 2."
            );
        }
    }

    try {

        examService.createExamSchedule(
                examCode,
                examDate,
                session
        );

        Exam scheduledExam =
                examService.findExam(
                        examCode
                );

        if (scheduledExam != null) {

            createNotificationForAllLoadedStudents(
                    "EXAM_SCHEDULED",
                    scheduledExam.getExamName(),
                    examDate.format(
                            DateTimeFormatter.ofPattern(
                                    "dd/MM/yyyy"
                            )
                    ),
                    session.toString()
            );
        }

        System.out.println();

        System.out.println(
                "Exam schedule created successfully."
        );

        System.out.println(
                "Exam Code : " + examCode
        );

        System.out.println(
                "Date      : "
                        + examDate.format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy"
                                )
                        )
        );

        System.out.println(
                "Session   : " + session
        );

    } catch (IllegalArgumentException e) {

        System.out.println();

        System.out.println(
                "Schedule creation failed."
        );

        System.out.println(
                "Reason: " + e.getMessage()
        );
    }
    }

    private static void allocateExamSeatsFromAdmin() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                 EXAM SEAT ALLOCATION"
    );

    System.out.println(
            "=============================================================="
    );

    if (examService.getExamSchedules().isEmpty()) {

        System.out.println(
                "No exam schedules are available."
        );

        System.out.println(
                "Create an exam schedule first."
        );

        return;
    }

    examService.displayExamSchedules();

    String examCode =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Exam Code: "
            ).toUpperCase();

    LocalDate examDate;

    while (true) {

        System.out.print(
                "Enter Exam Date (DD/MM/YYYY): "
        );

        String input =
                scanner.nextLine().trim();

        try {

            examDate =
                    LocalDate.parse(
                            input,
                            DateTimeFormatter.ofPattern(
                                    "dd/MM/yyyy"
                            )
                    );

            break;

        } catch (DateTimeParseException e) {

            System.out.println(
                    "Invalid date. Use DD/MM/YYYY."
            );
        }
    }

    ExamSession session;

    while (true) {

        System.out.println();

        System.out.println(
                "Select Session:"
        );

        System.out.println(
                "1. Morning"
        );

        System.out.println(
                "2. Afternoon"
        );

        System.out.print(
                "Enter choice: "
        );

        String choice =
                scanner.nextLine().trim();

        if (choice.equals("1")) {

            session = ExamSession.MORNING;
            break;

        } else if (choice.equals("2")) {

            session = ExamSession.AFTERNOON;
            break;

        } else {

            System.out.println(
                    "Invalid choice."
            );
        }
    }

    ExamSchedule schedule =
            examService.findSchedule(
                    examCode,
                    examDate,
                    session
            );

    if (schedule == null) {

        System.out.println();

        System.out.println(
                "The specified exam schedule does not exist."
        );

        return;
    }

    /*
     * Get all registered students.
     */

    List<Student> registeredStudents =
            new ArrayList<>();

    for (Student student : students) {

        if (student == null) {
            continue;
        }

        if (examService.isRegisteredForExam(
                student,
                examCode
        )) {

            registeredStudents.add(student);
        }
    }

    if (registeredStudents.isEmpty()) {

        System.out.println();

        System.out.println(
                "No students are registered for this examination."
        );

        return;
    }

    /*
     * Allocate in rank order where rank exists.
     */

    registeredStudents.sort(
            (student1, student2) -> {

                if (student1.getRank() == null
                        && student2.getRank() == null) {

                    return student1
                            .getApplicationNumber()
                            .compareTo(
                                    student2
                                            .getApplicationNumber()
                            );
                }

                if (student1.getRank() == null) {
                    return 1;
                }

                if (student2.getRank() == null) {
                    return -1;
                }

                return Integer.compare(
                        student1.getRank(),
                        student2.getRank()
                );
            }
    );

    int allocatedCount = 0;

    for (Student student :
            registeredStudents) {

        try {

            ExamAllocation allocation =
                    examService.allocateExam(
                            student,
                            schedule
                    );

            allocatedCount++;

            System.out.println();

            System.out.println(
                    "Exam seat allocated:"
            );

            System.out.println(
                    "Student : "
                            + student.getName()
            );

            System.out.println(
                    "Rank    : "
                            + student.getRank()
            );

            System.out.println(
                    "Centre  : "
                            + allocation.getCentreCode()
            );

            System.out.println(
                    "Room    : "
                            + allocation.getRoomNumber()
            );

            System.out.println(
                    "Seat    : "
                            + allocation.getSeatNumber()
            );

        } catch (IllegalStateException e) {

            System.out.println();

            System.out.println(
                    "Unable to allocate seat for "
                            + student.getName()
            );

            System.out.println(
                    "Reason: "
                            + e.getMessage()
            );
        }
    }

    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                 ALLOCATION COMPLETED"
    );

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "Exam Code       : "
                    + examCode
    );

    System.out.println(
            "Exam Date       : "
                    + examDate.format(
                            DateTimeFormatter.ofPattern(
                                    "dd/MM/yyyy"
                            )
                    )
    );

    System.out.println(
            "Session         : "
                    + session
    );

    System.out.println(
            "Registered      : "
                    + registeredStudents.size()
    );

    System.out.println(
            "Allocated       : "
                    + allocatedCount
    );

    System.out.println(
            "=============================================================="
    );
    }
    
    private static void displayExamRoomLayoutFromAdmin() {

        if (!requireAdminAccess()) {
            return;
        }


    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                 EXAM ROOM / SEAT LAYOUT"
    );

    System.out.println(
            "=============================================================="
    );

    List<ExamCentre> centres =
            examService.getExamCentres();

    if (centres.isEmpty()) {

        System.out.println(
                "No examination centres available."
        );

        return;
    }

    for (int i = 0; i < centres.size(); i++) {

        ExamCentre centre =
                centres.get(i);

        System.out.println();

        System.out.println(
                (i + 1)
                        + ". "
                        + centre.getCentreCode()
                        + " - "
                        + centre.getCentreName()
        );
    }

    System.out.println();

    System.out.print(
            "Select Centre: "
    );

    String input =
            scanner.nextLine().trim();

    try {

        int choice =
                Integer.parseInt(input);

        if (choice < 1
                || choice > centres.size()) {

            System.out.println(
                    "Invalid centre selection."
            );

            return;
        }

        ExamCentre selectedCentre =
                centres.get(choice - 1);

        System.out.println();

        System.out.println(
                "Centre: "
                        + selectedCentre.getCentreName()
        );

        List<ExamRoom> rooms =
                selectedCentre.getRooms();

        if (rooms.isEmpty()) {

            System.out.println(
                    "No rooms configured for this centre."
            );

            return;
        }

        for (ExamRoom room : rooms) {

            room.displayRoomLayout();
        }

    } catch (NumberFormatException e) {

        System.out.println(
                "Please enter a valid number."
        );
    }
    }

    private static void initializeDatabaseSeats() {

        if (!requireAdminAccess()) {
            return;
        }


    try {

        databaseSeatInitializationService
                .initializeSeats();

    } catch (Exception e) {

        System.out.println();
        System.out.println(
                "Database seat initialization failed."
        );

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );

        System.out.println(
                "Please verify MySQL/JPA configuration."
        );
    }
    }private static void studentExamRegistrationMenu(
        Student student
) {

        if (!requireStudentSession(student)) {
            return;
        }


    while (true) {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                 EXAM REGISTRATION"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "1. View Available Exams"
        );

        System.out.println(
                "2. Register for an Exam"
        );

        System.out.println(
                "3. View My Exam Registrations"
        );

        System.out.println(
                "4. View My Exam Allocation"
        );

        System.out.println(
                "0. Back"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.print(
                "Enter your choice: "
        );

        String choice =
                scanner.nextLine().trim();

        try {

            switch (choice) {

                case "1":
                    examService.displayExams();
                    break;

                case "2":
                    registerStudentForExam(student);
                    break;

                case "3":
                                        viewStudentExamRegistration(student);
                    break;

                case "4":
                    examService
                            .displayStudentExamAllocation(student);
                    break;

                case "0":
                    return;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }

        } catch (Exception e) {

            System.out.println();

            System.out.println(
                    "Operation failed: "
                            + e.getMessage()
            );
        }
    }
}

    private static void adminMenu() {

        while (true) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("            ADMIN MENU");
            System.out.println("======================================");
            System.out.println("1. Register Admin");
            System.out.println("2. Login");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    registerAdmin();
                    break;
                
                case "2":
                        loginAdmin();
                        break;    

                case "3":
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

    }    
    private static void registerAdmin() {

    System.out.println();
    System.out.println("======================================");
    System.out.println("          ADMIN REGISTRATION");
    System.out.println("======================================");

    String employeeId =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Employee ID: "
            );

    String name =
            InputUtil.readRequiredString(
                    scanner,
                    "Enter Admin Name: "
            );

    String email =
            InputUtil.readEmail(scanner);

    String phoneNumber =
            InputUtil.readPhoneNumber(scanner);

    String password;

    while (true) {

        password =
                InputUtil.readRequiredString(
                        scanner,
                        "Create Password: "
                );

        String confirmPassword =
                InputUtil.readRequiredString(
                        scanner,
                        "Confirm Password: "
                );

        if (!password.equals(confirmPassword)) {

            System.out.println();

            System.out.println(
                    "Passwords do not match. Please try again."
            );

            continue;
        }

        break;
    }

    try {

        /*
         * AdminAuthenticationService is responsible for:
         *
         * 1. Validating registration data
         * 2. Checking duplicate Employee ID
         * 3. Checking duplicate Email
         * 4. Hashing the password
         * 5. Saving the admin to MySQL
         */
        com.college.admission.jpa.AdminJpaEntity admin =
                adminAuthenticationService.registerAdmin(
                        employeeId,
                        name,
                        email,
                        phoneNumber,
                        password
                );

        System.out.println();
        System.out.println("======================================");
        System.out.println("     ADMIN REGISTRATION SUCCESSFUL");
        System.out.println("======================================");

        System.out.println(
                "Admin ID    : "
                        + admin.getAdminId()
        );

        System.out.println(
                "Employee ID : "
                        + admin.getEmployeeId()
        );

        System.out.println(
                "Name        : "
                        + admin.getName()
        );

        System.out.println(
                "Email       : "
                        + admin.getEmail()
        );

        System.out.println(
                "Role        : "
                        + admin.getRole()
        );

        System.out.println("======================================");

        System.out.println();
        System.out.println(
                "You can now use your Employee ID and "
                        + "password to login."
        );

    } catch (Exception e) {

        System.out.println();
        System.out.println("======================================");
        System.out.println("      ADMIN REGISTRATION FAILED");
        System.out.println("======================================");

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );

        System.out.println("======================================");
    }
}


private static void viewStudentNotifications(
        Student student
) {

    if (!requireStudentSession(student)) {
        return;
    }

    System.out.println();

    System.out.println(
            "=============================================================="
    );

    System.out.println(
            "                    NOTIFICATIONS"
    );

    System.out.println(
            "=============================================================="
    );

    if (student == null) {

        System.out.println(
                "Student information is not available."
        );

        return;
    }

    try {

        long unreadCount =
                notificationService.getUnreadCount(
                        student
                );

        long totalCount =
                notificationService
                        .getStudentNotifications(student)
                        .size();

        System.out.println(
                "Total Notifications : "
                        + totalCount
        );

        System.out.println(
                "Unread Notifications: "
                        + unreadCount
        );

        System.out.println(
                "=============================================================="
        );

        if (totalCount == 0) {

            System.out.println();

            System.out.println(
                    "You currently have no notifications."
            );

            System.out.println(
                    "=============================================================="
            );

            return;
        }

        notificationService.displayStudentNotifications(
                student
        );

        System.out.println();

        if (unreadCount > 0) {

            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "1. Mark All Notifications as Read"
            );

            System.out.println(
                    "2. Back"
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine().trim();

            if (choice.equals("1")) {

                notificationService.markAllAsRead(
                        student
                );

                System.out.println();

                System.out.println(
                        "All notifications marked as read."
                );
            }

        } else {

            System.out.println();

            System.out.println(
                    "All notifications have been read."
            );
        }

    } catch (Exception e) {

        System.out.println();

        System.out.println(
                "Unable to display notifications."
        );

        System.out.println(
                "Reason: "
                        + e.getMessage()
        );
    }

    System.out.println();

    System.out.println(
            "=============================================================="
    );
}
    

    
    
    

    private static void adminNotificationManagement() {

        if (!requireAdminAccess()) {
            return;
        }

        while (true) {

            System.out.println();

            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "                  NOTIFICATION MANAGEMENT"
            );

            System.out.println(
                    "=============================================================="
            );

            System.out.println(
                    "1. View All Notifications"
            );

            System.out.println(
                    "2. View All Unread Notifications"
            );

            System.out.println(
                    "3. View Student Notifications"
            );

            System.out.println(
                    "4. View Notifications By Type"
            );

            System.out.println(
                    "5. Mark Notification As Read"
            );

            System.out.println(
                    "6. Mark Notification As Unread"
            );

            System.out.println(
                    "7. Delete Notification"
            );

            System.out.println(
                    "8. Delete All Student Notifications"
            );

            System.out.println(
                    "0. Back"
            );

            System.out.println(
                    "=============================================================="
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":

                    notificationService
                            .displayAllNotifications();

                    break;

                case "2":

                    notificationService
                            .displayAllUnreadNotifications();

                    break;

                case "3":

                    System.out.print(
                            "Enter Application Number: "
                    );

                    String applicationNumber =
                            scanner.nextLine().trim();

                    if (applicationNumber.isBlank()) {

                        System.out.println(
                                "Application number cannot be empty."
                        );

                        break;
                    }

                    notificationService
                            .displayStudentNotifications(
                                    findStudentByApplicationNumber(
                                            applicationNumber
                                    )
                            );

                    break;

                case "4":

                    System.out.print(
                            "Enter Notification Type: "
                    );

                    String notificationType =
                            scanner.nextLine().trim();

                    if (notificationType.isBlank()) {

                        System.out.println(
                                "Notification type cannot be empty."
                        );

                        break;
                    }

                    List<Notification> notifications =
                            notificationService
                                    .getNotificationsByType(
                                            notificationType
                                    );

                    if (notifications.isEmpty()) {

                        System.out.println(
                                "No notifications found for type: "
                                        + notificationType
                        );

                    } else {

                        System.out.println();

                        for (Notification notification :
                                notifications) {

                            notification.displayNotification();
                        }
                    }

                    break;

                case "5":

                    markNotificationReadFromAdmin();

                    break;

                case "6":

                    markNotificationUnreadFromAdmin();

                    break;

                case "7":

                    deleteNotificationFromAdmin();

                    break;

                case "8":

                    deleteStudentNotificationsFromAdmin();

                    break;

                case "0":

                    return;

                default:

                    System.out.println(
                            "Invalid choice. Please select 0-8."
                    );
            }
        }
    }

    private static Student findStudentByApplicationNumber(
            String applicationNumber
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }

        for (Student student : students) {

            if (student != null
                    && student.getApplicationNumber()
                            .equalsIgnoreCase(
                                    applicationNumber.trim()
                            )) {

                return student;
            }
        }

        System.out.println();

        System.out.println(
                "Student is not currently loaded in this console session."
        );

        System.out.println(
                "Please login as that student once before using this "
                        + "student-specific display option."
        );

        return null;
    }

    private static void markNotificationReadFromAdmin() {

        System.out.print(
                "Enter Notification ID: "
        );

        String input =
                scanner.nextLine().trim();

        try {

            Long notificationId =
                    Long.parseLong(input);

            notificationService.markAsRead(
                    notificationId
            );

            System.out.println(
                    "Notification marked as read."
            );

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid notification ID."
            );

        } catch (Exception e) {

            System.out.println(
                    "Unable to mark notification as read: "
                            + e.getMessage()
            );
        }
    }

    private static void markNotificationUnreadFromAdmin() {

        System.out.print(
                "Enter Notification ID: "
        );

        String input =
                scanner.nextLine().trim();

        try {

            Long notificationId =
                    Long.parseLong(input);

            notificationService.markAsUnread(
                    notificationId
            );

            System.out.println(
                    "Notification marked as unread."
            );

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid notification ID."
            );

        } catch (Exception e) {

            System.out.println(
                    "Unable to mark notification as unread: "
                            + e.getMessage()
            );
        }
    }

    private static void deleteNotificationFromAdmin() {

        System.out.print(
                "Enter Notification ID: "
        );

        String input =
                scanner.nextLine().trim();

        try {

            Long notificationId =
                    Long.parseLong(input);

            System.out.print(
                    "Delete this notification? (YES/NO): "
            );

            String confirmation =
                    scanner.nextLine().trim();

            if (!confirmation.equalsIgnoreCase("YES")) {

                System.out.println(
                        "Deletion cancelled."
                );

                return;
            }

            notificationService.deleteNotification(
                    notificationId
            );

            System.out.println(
                    "Notification deleted successfully."
            );

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid notification ID."
            );

        } catch (Exception e) {

            System.out.println(
                    "Unable to delete notification: "
                            + e.getMessage()
            );
        }
    }

    private static void deleteStudentNotificationsFromAdmin() {

        System.out.print(
                "Enter Application Number: "
        );

        String applicationNumber =
                scanner.nextLine().trim();

        if (applicationNumber.isBlank()) {

            System.out.println(
                    "Application number cannot be empty."
            );

            return;
        }

        System.out.print(
                "Delete all notifications for "
                        + applicationNumber
                        + "? (YES/NO): "
        );

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("YES")) {

            System.out.println(
                    "Deletion cancelled."
            );

            return;
        }

        try {

            Student student =
                    findStudentByApplicationNumber(
                            applicationNumber
                    );

            if (student == null) {
                return;
            }

            notificationService.deleteStudentNotifications(
                    student
            );

            System.out.println(
                    "Student notifications deleted successfully."
            );

        } catch (Exception e) {

            System.out.println(
                    "Unable to delete student notifications: "
                            + e.getMessage()
            );
        }
    }

    private static void loginAdmin() {

        System.out.println();
        System.out.println("========== ADMIN LOGIN ==========");

        String employeeId =
                InputUtil.readRequiredString(
                        scanner,
                        "Enter Employee ID: "
                );

        String password =
                InputUtil.readRequiredString(
                        scanner,
                        "Enter Password: "
                );

        try {

            SecuritySession session =
                    adminAuthenticationService.login(
                            employeeId,
                            password
                    );

            authorizationService.requireAdmin();

            System.out.println();
            System.out.println("======================================");
            System.out.println("        ADMIN LOGIN SUCCESSFUL");
            System.out.println("======================================");
            System.out.println(
                    "Employee ID : "
                            + session.getUsername()
            );
            System.out.println(
                    "Name        : "
                            + session.getDisplayName()
            );
            System.out.println(
                    "Role        : "
                            + session.getRole()
            );
            System.out.println(
                    "Session     : ACTIVE"
            );
            System.out.println("======================================");

            adminDashboard();

        } catch (Exception e) {

            if (SecurityContext.isAuthenticated()) {
                SecurityContext.clear();
            }

            System.out.println();
            System.out.println(
                    "Admin login failed: " + e.getMessage()
            );
        }
    }



    /*
     * ============================================================
     * NOTIFICATION HELPERS
     * ============================================================
     *
     * Notifications are created only after the corresponding
     * operation succeeds.  The helper below also prevents the
     * same event from being inserted repeatedly when an admin
     * re-opens or re-runs an already completed operation.
     */

    private static void createNotificationOnce(
            Student student,
            String notificationType,
            NotificationCreator creator
    ) {

        if (student == null
                || notificationType == null
                || notificationType.isBlank()
                || creator == null) {
            return;
        }

        try {

            List<Notification> existingNotifications =
                    notificationService.getNotifications(
                            student.getApplicationNumber()
                    );

            for (Notification notification :
                    existingNotifications) {

                if (notification != null
                        && notificationType.equalsIgnoreCase(
                                notification.getNotificationType()
                        )) {

                    return;
                }
            }

            creator.create();

        } catch (Exception e) {

            /*
             * Notification failure must never make a successful
             * admission operation fail.
             */
            System.out.println(
                    "Warning: Notification could not be created for "
                            + student.getApplicationNumber()
            );

            System.out.println(
                    "Reason: " + e.getMessage()
            );
        }
    }

    @FunctionalInterface
    private interface NotificationCreator {
        void create();
    }

    private static void createNotificationForAllLoadedStudents(
            String notificationType,
            String examName,
            String examDate,
            String session
    ) {

        for (Student student : students) {

            if (student == null) {
                continue;
            }

            createNotificationOnce(
                    student,
                    notificationType,
                    () -> notificationService.notifyExamScheduled(
                            student,
                            examName,
                            examDate,
                            session
                    )
            );
        }
    }

    private static void notifySeatAllocation(
            Student student,
            SeatAllocation allocation
    ) {

        if (student == null || allocation == null) {
            return;
        }

        createNotificationOnce(
                student,
                "SEAT_ALLOTTED",
                () -> notificationService.notifySeatAllotted(
                        student,
                        allocation.getCourseCode(),
                        allocation.getSeatNumber()
                )
        );
    }

    private static void notifyWaitlistedStudent(
            Student student,
            int waitlistPosition
    ) {

        if (student == null || waitlistPosition <= 0) {
            return;
        }

        createNotificationOnce(
                student,
                "WAITLISTED",
                () -> notificationService.notifyWaitlisted(
                        student,
                        waitlistPosition
                )
        );
    }

    private static void notifySeatUpgrade(
            Student student,
            SeatAllocation allocation
    ) {

        if (student == null || allocation == null) {
            return;
        }

        String upgradeMessage =
                "Your admission seat has been upgraded "
                        + "to "
                        + allocation.getCourseCode()
                        + ". New seat number: "
                        + allocation.getSeatNumber()
                        + ".";

        createNotificationForEvent(
                student,
                "SEAT_UPGRADED",
                upgradeMessage,
                () -> notificationService.notifySeatUpgraded(
                        student,
                        allocation.getCourseCode(),
                        allocation.getSeatNumber()
                )
        );
    }

    /**
     * Central authorization boundary for administrative console operations.
     */
    private static boolean requireAdminAccess() {

        try {

            authorizationService.requireAdmin();

            return true;

        } catch (AuthorizationException e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("             ACCESS DENIED");
            System.out.println("======================================");
            System.out.println(e.getMessage());
            System.out.println("======================================");

            return false;
        }
    }

    private static void createNotificationForEvent(
            Student student,
            String notificationType,
            String eventMessage,
            NotificationCreator creator
    ) {

        if (student == null
                || notificationType == null
                || notificationType.isBlank()
                || eventMessage == null
                || eventMessage.isBlank()
                || creator == null) {
            return;
        }

        try {

            List<Notification> existingNotifications =
                    notificationService.getNotifications(
                            student.getApplicationNumber()
                    );

            for (Notification notification :
                    existingNotifications) {

                if (notification != null
                        && notificationType.equalsIgnoreCase(
                                notification.getNotificationType()
                        )
                        && eventMessage.equals(
                                notification.getMessage()
                        )) {

                    return;
                }
            }

            creator.create();

        } catch (Exception e) {

            System.out.println(
                    "Warning: Notification could not be created for "
                            + student.getApplicationNumber()
            );

            System.out.println(
                    "Reason: " + e.getMessage()
            );
        }
    }

    /**
     * Ensures that the supplied Student belongs to the currently
     * authenticated STUDENT security session.
     */
    private static boolean requireStudentSession(Student student) {

        try {

            if (student == null) {

                throw new AuthorizationException(
                        "Student information is unavailable."
                );
            }

            authorizationService.requireRole(Role.STUDENT);

            SecuritySession session =
                    authorizationService.getCurrentSession();

            if (!student.getApplicationNumber()
                    .equalsIgnoreCase(
                            session.getUsername()
                    )) {

                throw new AuthorizationException(
                        "You can access only your own student account."
                );
            }

            return true;

        } catch (AuthorizationException e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("             ACCESS DENIED");
            System.out.println("======================================");
            System.out.println(e.getMessage());
            System.out.println("======================================");

            return false;
        }
    }

}    
    