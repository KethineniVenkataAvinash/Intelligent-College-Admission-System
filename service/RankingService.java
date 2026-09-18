package com.college.admission.service;

import com.college.admission.jpa.StudentJpaRepository;
import com.college.admission.model.Student;
import com.college.admission.annotation.AdminOnly;
import com.college.admission.security.ServiceAuthorization;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class RankingService {

    private final StudentJpaRepository studentRepository;

    public RankingService() {

        this.studentRepository =
                new StudentJpaRepository();
    }

    /*
     * ============================================================
     * GENERATE RANKS
     * ============================================================
     */
    @AdminOnly
    public List<Student> generateRanks() {

        ServiceAuthorization.requireAdmin();

        /*
         * Get eligible students directly from MySQL.
         */
        List<Student> eligibleStudents =
                studentRepository
                        .findAllEligibleStudents();

        /*
         * Sort by:
         *
         * 1. Percentage - descending
         * 2. Application Number - ascending
         *
         * Application number is used as the tie-breaker.
         */
        eligibleStudents.sort(
                Comparator
                        .comparing(
                                Student::getPercentage,
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                Student::getApplicationNumber
                        )
        );

        /*
         * Generate sequential ranks.
         */
        int rank = 1;

        for (Student student : eligibleStudents) {

            student.setRank(rank);

            /*
             * Persist rank_number to MySQL.
             */
            studentRepository.updateRank(
                    student.getApplicationNumber(),
                    rank
            );

            rank++;
        }

        return eligibleStudents;
    }

    /*
     * ============================================================
     * DISPLAY RANK LIST
     * ============================================================
     */
    @AdminOnly
    public void displayRankList() {

        ServiceAuthorization.requireAdmin();

        List<Student> rankedStudents =
                generateRanks();

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                       RANK LIST"
        );

        System.out.println(
                "=============================================================="
        );

        if (rankedStudents.isEmpty()) {

            System.out.println(
                    "No eligible students available for ranking."
            );

            return;
        }

        System.out.printf(
                "%-8s %-25s %-20s %-12s%n",
                "Rank",
                "Student Name",
                "Application No.",
                "Percentage"
        );

        System.out.println(
                "--------------------------------------------------------------"
        );

        for (Student student : rankedStudents) {

            System.out.printf(
                    "%-8d %-25s %-20s %-12.2f%n",
                    student.getRank(),
                    student.getName(),
                    student.getApplicationNumber(),
                    student.getPercentage()
            );
        }

        System.out.println(
                "=============================================================="
        );
    }

    /*
     * ============================================================
     * FIND STUDENT BY RANK
     * ============================================================
     */
    @AdminOnly
    public Student findStudentByRank(int rank) {

        ServiceAuthorization.requireAdmin();

        if (rank <= 0) {

            return null;
        }

        List<Student> rankedStudents =
                generateRanks();

        for (Student student : rankedStudents) {

            if (student.getRank() != null
                    && student.getRank() == rank) {

                return student;
            }
        }

        return null;
    }

    /**
     * Exports an already generated rank list without changing persistence.
     * The report facade demonstrates the application's character-stream
     * reporting path and can be used by an administrative export action.
     */
    @AdminOnly
    public void exportRankList(Path target) throws IOException {
        ServiceAuthorization.requireAdmin();
        new AdmissionReportService().exportRankReport(
                studentRepository.findAllEligibleStudents(),
                target
        );
    }
}