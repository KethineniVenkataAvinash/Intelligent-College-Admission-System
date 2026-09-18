package com.college.admission.service;

import com.college.admission.model.Student;
import com.college.admission.util.AdmissionReportToolkit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Application report facade. Ranking and allocation reporting use the same
 * stream-safe utility as other admission exports.
 */
public class AdmissionReportService {

    private final AdmissionReportToolkit toolkit =
            AdmissionReportToolkit.getInstance();

    public String createRankReport(List<Student> students) {
        List<String> rows = new ArrayList<>();
        for (Student student : students) {
            rows.add(new AdmissionReportToolkit.CsvRow(
                    student.getApplicationNumber(),
                    student.getName()).toString());
        }
        return toolkit.renderWithCallback(
                rows, new AdmissionReportToolkit.CsvRenderer());
    }

    public void exportRankReport(List<Student> students, Path target)
            throws IOException {
        toolkit.writeText(target, createRankReport(students));
    }
}
