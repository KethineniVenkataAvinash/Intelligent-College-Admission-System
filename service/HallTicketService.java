package com.college.admission.service;

import com.college.admission.jpa.HallTicketJpaRepository;
import com.college.admission.model.ExamAllocation;
import com.college.admission.model.HallTicket;
import com.college.admission.model.SeatAllocation;
import com.college.admission.model.Student;
import com.college.admission.security.ServiceAuthorization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HallTicketService {

    // =========================================================
    // CONSTANTS
    // =========================================================

    private static final String HALL_TICKET_DIRECTORY =
            "hall_tickets";

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");


    // =========================================================
    // REPOSITORY
    // =========================================================

    private final HallTicketJpaRepository hallTicketRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public HallTicketService() {

        hallTicketRepository =
                new HallTicketJpaRepository();

        createHallTicketDirectory();
    }


    // =========================================================
    // DIRECTORY MANAGEMENT
    // =========================================================

    private void createHallTicketDirectory() {

        File directory =
                new File(HALL_TICKET_DIRECTORY);

        if (!directory.exists()) {

            if (!directory.mkdirs()) {

                throw new IllegalStateException(
                        "Unable to create hall ticket directory."
                );
            }
        }
    }


    // =========================================================
    // CREATE HALL TICKET
    // =========================================================

    /**
     * Creates and persists a hall ticket.
     *
     * The hall ticket is generated from:
     *
     * Student
     *      +
     * Admission Seat Allocation
     *      +
     * Examination Allocation
     *
     * The generated HallTicket is then stored in MySQL.
     */
    public HallTicket createHallTicket(
            Student student,
            SeatAllocation seatAllocation,
            ExamAllocation examAllocation
    ) {

        ServiceAuthorization.requireStudentOrAdminOwnership(student);

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (seatAllocation == null) {

            throw new IllegalStateException(
                    "Admission seat allocation is required "
                            + "before generating the hall ticket."
            );
        }

        if (examAllocation == null) {

            throw new IllegalStateException(
                    "Exam allocation is required "
                            + "before generating the hall ticket."
            );
        }


        /*
         * Make sure the exam allocation belongs
         * to the same student.
         */
        if (!student.getApplicationNumber()
                .equals(
                        examAllocation.getApplicationNumber()
                )) {

            throw new IllegalStateException(
                    "Exam allocation does not belong "
                            + "to the selected student."
            );
        }


        /*
         * Check whether this exact hall ticket
         * already exists in the database.
         */
        HallTicket existing =
                hallTicketRepository.findHallTicket(
                        student.getApplicationNumber(),
                        examAllocation.getExamDate(),
                        examAllocation.getSession()
                );


        if (existing != null) {

            return existing;
        }


        /*
         * Create HallTicket.
         */
        HallTicket hallTicket =
                new HallTicket(
                        student.getApplicationNumber(),
                        student.getName(),
                        student.getCategory().toString(),
                        student.getRank(),

                        seatAllocation.getCourseCode(),
                        seatAllocation.getCourseName(),
                        seatAllocation.getSeatNumber(),

                        examAllocation.getCentreCode(),
                        examAllocation.getCentreName(),
                        examAllocation.getRoomNumber(),
                        examAllocation.getSeatNumber(),

                        examAllocation.getExamDate(),
                        examAllocation.getSession()
                );


        /*
         * Persist to MySQL.
         */
        hallTicketRepository.save(
                hallTicket
        );


        return hallTicket;
    }


    // =========================================================
    // GENERATE HALL TICKET FILE
    // =========================================================

    /**
     * Generates a physical .txt hall ticket.
     *
     * Uses Java Character I/O:
     *
     * BufferedWriter
     * FileWriter
     */
    public String generateHallTicketFile(
            HallTicket hallTicket
    ) {

        if (hallTicket != null) {
            ServiceAuthorization.requireApplicationOwnership(
                    hallTicket.getApplicationNumber()
            );
        }

        if (hallTicket == null) {

            throw new IllegalArgumentException(
                    "Hall ticket cannot be null."
            );
        }


        String fileName =
                HALL_TICKET_DIRECTORY
                        + File.separator
                        + hallTicket.getApplicationNumber()
                        + "_Hall_Ticket.txt";


        try (
                BufferedWriter writer =
                        new BufferedWriter(
                                new FileWriter(fileName)
                        )
        ) {

            writer.write(
                    "============================================================"
            );
            writer.newLine();

            writer.write(
                    "                 COLLEGE ADMISSION SYSTEM"
            );
            writer.newLine();

            writer.write(
                    "                     HALL TICKET"
            );
            writer.newLine();

            writer.write(
                    "============================================================"
            );
            writer.newLine();

            writer.newLine();


            // -------------------------------------------------
            // STUDENT DETAILS
            // -------------------------------------------------

            writer.write(
                    "STUDENT DETAILS"
            );
            writer.newLine();

            writer.write(
                    "------------------------------------------------------------"
            );
            writer.newLine();

            writer.write(
                    "Student Name       : "
                            + hallTicket.getStudentName()
            );
            writer.newLine();

            writer.write(
                    "Application Number : "
                            + hallTicket.getApplicationNumber()
            );
            writer.newLine();

            writer.write(
                    "Rank               : "
                            + formatRank(
                                    hallTicket.getRank()
                            )
            );
            writer.newLine();

            writer.write(
                    "Category           : "
                            + hallTicket.getCategory()
            );
            writer.newLine();

            writer.newLine();


            // -------------------------------------------------
            // ADMISSION DETAILS
            // -------------------------------------------------

            writer.write(
                    "ADMISSION DETAILS"
            );
            writer.newLine();

            writer.write(
                    "------------------------------------------------------------"
            );
            writer.newLine();

            writer.write(
                    "Course Code        : "
                            + hallTicket.getCourseCode()
            );
            writer.newLine();

            writer.write(
                    "Course Name        : "
                            + hallTicket.getCourseName()
            );
            writer.newLine();

            writer.write(
                    "Admission Seat     : "
                            + formatValue(
                                    hallTicket.getAdmissionSeatNumber()
                            )
            );
            writer.newLine();

            writer.newLine();


            // -------------------------------------------------
            // EXAMINATION DETAILS
            // -------------------------------------------------

            writer.write(
                    "EXAMINATION DETAILS"
            );
            writer.newLine();

            writer.write(
                    "------------------------------------------------------------"
            );
            writer.newLine();

            writer.write(
                    "Exam Centre Code   : "
                            + hallTicket.getExamCentreCode()
            );
            writer.newLine();

            writer.write(
                    "Exam Centre Name   : "
                            + hallTicket.getExamCentreName()
            );
            writer.newLine();

            writer.write(
                    "Room Number        : "
                            + hallTicket.getRoomNumber()
            );
            writer.newLine();

            writer.write(
                    "Exam Seat          : "
                            + hallTicket.getExamSeatNumber()
            );
            writer.newLine();

            writer.write(
                    "Exam Date          : "
                            + hallTicket.getExamDate()
                                    .format(dateFormatter)
            );
            writer.newLine();

            writer.write(
                    "Session            : "
                            + hallTicket.getSession()
            );
            writer.newLine();

            writer.newLine();


            // -------------------------------------------------
            // IMPORTANT INSTRUCTIONS
            // -------------------------------------------------

            writer.write(
                    "IMPORTANT INSTRUCTIONS"
            );
            writer.newLine();

            writer.write(
                    "------------------------------------------------------------"
            );
            writer.newLine();

            writer.write(
                    "1. Carry this hall ticket to the examination centre."
            );
            writer.newLine();

            writer.write(
                    "2. Reach the examination centre before the reporting time."
            );
            writer.newLine();

            writer.write(
                    "3. Carry a valid identity document."
            );
            writer.newLine();

            writer.write(
                    "4. Follow all examination rules and instructions."
            );
            writer.newLine();

            writer.newLine();


            writer.write(
                    "============================================================"
            );
            writer.newLine();

            writer.write(
                    "                  END OF HALL TICKET"
            );
            writer.newLine();

            writer.write(
                    "============================================================"
            );
            writer.newLine();


        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to generate hall ticket file.",
                    e
            );
        }


        return fileName;
    }


    // =========================================================
    // DISPLAY HALL TICKET
    // =========================================================

    public void displayHallTicket(
            HallTicket hallTicket
    ) {

        if (hallTicket != null) {
            ServiceAuthorization.requireApplicationOwnership(
                    hallTicket.getApplicationNumber()
            );
        }

        if (hallTicket == null) {

            System.out.println(
                    "Hall ticket is not available."
            );

            return;
        }


        System.out.println();

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                 COLLEGE ADMISSION SYSTEM"
        );

        System.out.println(
                "                     HALL TICKET"
        );

        System.out.println(
                "============================================================"
        );

        System.out.println();


        System.out.println(
                "HALL TICKET ID      : "
                        + formatValue(
                                hallTicket.getHallTicketId()
                        )
        );


        System.out.println();

        System.out.println(
                "STUDENT DETAILS"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        System.out.println(
                "Student Name       : "
                        + hallTicket.getStudentName()
        );

        System.out.println(
                "Application Number : "
                        + hallTicket.getApplicationNumber()
        );

        System.out.println(
                "Rank               : "
                        + formatRank(
                                hallTicket.getRank()
                        )
        );

        System.out.println(
                "Category           : "
                        + hallTicket.getCategory()
        );

        System.out.println();


        System.out.println(
                "ADMISSION DETAILS"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        System.out.println(
                "Course Code        : "
                        + hallTicket.getCourseCode()
        );

        System.out.println(
                "Course Name        : "
                        + hallTicket.getCourseName()
        );

        System.out.println(
                "Admission Seat     : "
                        + formatValue(
                                hallTicket.getAdmissionSeatNumber()
                        )
        );

        System.out.println();


        System.out.println(
                "EXAMINATION DETAILS"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        System.out.println(
                "Exam Centre Code   : "
                        + hallTicket.getExamCentreCode()
        );

        System.out.println(
                "Exam Centre Name   : "
                        + hallTicket.getExamCentreName()
        );

        System.out.println(
                "Room Number        : "
                        + hallTicket.getRoomNumber()
        );

        System.out.println(
                "Exam Seat          : "
                        + hallTicket.getExamSeatNumber()
        );

        System.out.println(
                "Exam Date          : "
                        + hallTicket.getExamDate()
                                .format(dateFormatter)
        );

        System.out.println(
                "Session            : "
                        + hallTicket.getSession()
        );

        System.out.println();


        System.out.println(
                "IMPORTANT INSTRUCTIONS"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        System.out.println(
                "1. Carry this hall ticket to the examination centre."
        );

        System.out.println(
                "2. Reach the examination centre before the reporting time."
        );

        System.out.println(
                "3. Carry a valid identity document."
        );

        System.out.println(
                "4. Follow all examination rules and instructions."
        );

        System.out.println();

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                  END OF HALL TICKET"
        );

        System.out.println(
                "============================================================"
        );
    }


    // =========================================================
    // READ HALL TICKET FILE
    // =========================================================

    /**
     * Reads a generated hall ticket using:
     *
     * BufferedReader
     * FileReader
     */
    public void readHallTicketFile(
            String fileName
    ) {

        if (fileName == null
                || fileName.isBlank()) {

            throw new IllegalArgumentException(
                    "Hall ticket file name cannot be empty."
            );
        }


        File file =
                new File(fileName);


        if (!file.exists()) {

            throw new IllegalArgumentException(
                    "Hall ticket file does not exist: "
                            + fileName
            );
        }


        if (!file.isFile()) {

            throw new IllegalArgumentException(
                    "Specified path is not a hall ticket file: "
                            + fileName
            );
        }


        System.out.println();

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "                READING HALL TICKET FILE"
        );

        System.out.println(
                "============================================================"
        );


        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(file)
                        )
        ) {

            String line;

            while (
                    (line = reader.readLine())
                            != null
            ) {

                System.out.println(line);
            }


        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to read hall ticket file.",
                    e
            );
        }


        System.out.println(
                "============================================================"
        );
    }


    // =========================================================
    // DATABASE LOOKUP METHODS
    // =========================================================

    /**
     * Find a student's hall ticket.
     */
    public HallTicket getHallTicket(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        return hallTicketRepository
                .findByApplicationNumber(
                        applicationNumber
                );
    }


    /**
     * Find all hall tickets belonging to a student.
     */
    public List<HallTicket> getHallTickets(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        return hallTicketRepository
                .findAllByApplicationNumber(
                        applicationNumber
                );
    }


    /**
     * Find an exact hall ticket.
     */
    public HallTicket getHallTicket(
            String applicationNumber,
            java.time.LocalDate examDate,
            com.college.admission.enums.ExamSession session
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        return hallTicketRepository.findHallTicket(
                applicationNumber,
                examDate,
                session
        );
    }


    /**
     * Return every hall ticket.
     */
    public List<HallTicket> getAllHallTickets() {

        ServiceAuthorization.requireAdmin();

        return hallTicketRepository.findAll();
    }


    // =========================================================
    // ADMIN DISPLAY
    // =========================================================

    /**
     * Display all hall tickets stored in MySQL.
     */
    public void displayAllHallTickets() {

        ServiceAuthorization.requireAdmin();

        List<HallTicket> hallTickets =
                hallTicketRepository.findAll();


        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                         HALL TICKETS"
        );

        System.out.println(
                "=========================================================================="
        );


        if (hallTickets.isEmpty()) {

            System.out.println(
                    "No hall tickets have been generated."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }


        System.out.printf(
                "%-20s %-25s %-12s %-12s %-12s %-15s%n",
                "Application",
                "Student",
                "Course",
                "Centre",
                "Room",
                "Exam Date"
        );


        System.out.println(
                "--------------------------------------------------------------------------"
        );


        for (HallTicket hallTicket :
                hallTickets) {

            System.out.printf(
                    "%-20s %-25s %-12s %-12s %-12s %-15s%n",
                    hallTicket.getApplicationNumber(),
                    hallTicket.getStudentName(),
                    hallTicket.getCourseCode(),
                    hallTicket.getExamCentreCode(),
                    hallTicket.getRoomNumber(),
                    hallTicket.getExamDate()
                            .format(dateFormatter)
            );
        }


        System.out.println(
                "=========================================================================="
        );
    }


    // =========================================================
    // DELETE HALL TICKET
    // =========================================================

    public void deleteHallTicket(
            Long hallTicketId
    ) {

        ServiceAuthorization.requireAdmin();

        hallTicketRepository.delete(
                hallTicketId
        );
    }


    // =========================================================
    // COUNT
    // =========================================================

    public long getHallTicketCount() {

        ServiceAuthorization.requireAdmin();

        return hallTicketRepository.count();
    }


    // =========================================================
    // REPOSITORY ACCESS
    // =========================================================

    public HallTicketJpaRepository
    getHallTicketRepository() {

        return hallTicketRepository;
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private String formatRank(
            Integer rank
    ) {

        if (rank == null) {

            return "Not Generated";
        }

        return String.valueOf(rank);
    }


    private String formatValue(
            Object value
    ) {

        if (value == null) {

            return "N/A";
        }

        String text =
                String.valueOf(value);

        if (text.isBlank()) {

            return "N/A";
        }

        return text;
    }
}