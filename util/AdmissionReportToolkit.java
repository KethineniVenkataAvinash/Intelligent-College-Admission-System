package com.college.admission.util;

import com.college.admission.annotation.AcademicExample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * Shared reporting and diagnostics support used by admission services.
 *
 * <p>This is deliberately application-facing rather than a collection of
 * disconnected classroom programs: reports need tabular data, byte and
 * character export, ordering, reflection diagnostics, and safe background
 * generation.</p>
 */
@AcademicExample("CSE2006 reporting utility")
public final class AdmissionReportToolkit {

    private AdmissionReportToolkit() {
    }

    public enum ReportFormat {
        TEXT("text/plain"),
        CSV("text/csv");

        private final String mediaType;

        ReportFormat(String mediaType) {
            this.mediaType = mediaType;
        }

        @Override
        public String toString() {
            return mediaType;
        }
    }

    public interface ReportRenderer {
        String render(List<String> rows);
    }

    public abstract static class AbstractReportRenderer
            implements ReportRenderer {
        protected final ReportFormat format;

        protected AbstractReportRenderer(ReportFormat format) {
            this.format = format;
        }

        protected String header(String title) {
            return title + " (" + format + ")";
        }
    }

    public static final class CsvRenderer extends AbstractReportRenderer {
        public CsvRenderer() {
            super(ReportFormat.CSV);
        }

        @Override
        public String render(List<String> rows) {
            StringBuilder output = new StringBuilder(header("Admissions"));
            for (String row : rows) {
                output.append(System.lineSeparator()).append(row);
            }
            return output.toString();
        }
    }

    /** Static nested value type for one report row. */
    public static final class CsvRow {
        private final String applicationNumber;
        private final String studentName;

        public CsvRow(String applicationNumber, String studentName) {
            this.applicationNumber = applicationNumber;
            this.studentName = studentName;
        }

        @Override
        public String toString() {
            return applicationNumber + "," + studentName;
        }
    }

    /** Non-static inner session retains the report's title and format. */
    public final class ReportSession {
        private final String title;

        private ReportSession(String title) {
            this.title = title;
        }

        public String title() {
            return title + " [" + ReportFormat.CSV + "]";
        }
    }

    private static final class Holder {
        private static final AdmissionReportToolkit INSTANCE =
                new AdmissionReportToolkit();
    }

    /** Singleton shared by report services without global mutable state. */
    public static AdmissionReportToolkit getInstance() {
        return Holder.INSTANCE;
    }

    public ReportSession openSession(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Report title is required.");
        }
        return new ReportSession(title.trim());
    }

    @AcademicExample("static method overloading")
    public static String formatSeatSummary(String[] seats) {
        return formatSeatSummary(Arrays.asList(seats));
    }

    @AcademicExample("static method overloading")
    public static String formatSeatSummary(List<String> seats) {
        StringBuilder summary = new StringBuilder("Seats: ");
        for (String seat : seats) {
            if (seat != null && !seat.isBlank()) {
                summary.append(seat.trim()).append(' ');
            }
        }
        return summary.toString().trim();
    }

    /** Iterative factorial used for bounded report statistics. */
    @AcademicExample("looping factorial")
    public static long factorial(int value) {
        if (value < 0 || value > 20) {
            throw new IllegalArgumentException("Factorial must be between 0 and 20.");
        }
        long result = 1;
        for (int index = 2; index <= value; index++) {
            result *= index;
        }
        return result;
    }

    /** Recursive count of nested preference groups. */
    public static int countPreferences(List<List<String>> groups) {
        if (groups == null || groups.isEmpty()) {
            return 0;
        }
        return countPreferences(groups, 0);
    }

    private static int countPreferences(List<List<String>> groups, int index) {
        if (index == groups.size()) {
            return 0;
        }
        List<String> group = groups.get(index);
        return (group == null ? 0 : group.size())
                + countPreferences(groups, index + 1);
    }

    /** One-dimensional seat labels for a report. */
    public static String[] seatLabels(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Seat count cannot be negative.");
        }
        String[] labels = new String[count];
        for (int index = 0; index < labels.length; index++) {
            labels[index] = "SEAT-" + (index + 1);
        }
        return labels;
    }

    /** Two-dimensional physical exam seating layout. */
    public static int[][] examLayout(int rows, int columns) {
        if (rows < 0 || columns < 0) {
            throw new IllegalArgumentException("Layout dimensions cannot be negative.");
        }
        int[][] layout = new int[rows][columns];
        int seat = 1;
        for (int[] row : layout) {
            for (int column = 0; column < row.length; column++) {
                row[column] = seat++;
            }
        }
        return layout;
    }

    /** Jagged preference matrix because courses have different preference counts. */
    public static String[][] preferenceMatrix(List<List<String>> preferences) {
        if (preferences == null) {
            throw new IllegalArgumentException("Preferences cannot be null.");
        }
        String[][] matrix = new String[preferences.size()][];
        for (int row = 0; row < preferences.size(); row++) {
            List<String> preference = preferences.get(row);
            matrix[row] = preference == null
                    ? new String[0]
                    : preference.toArray(new String[0]);
        }
        return matrix;
    }

    /**
     * Uses legacy Vector and Stack where report generation needs a
     * thread-safe row buffer and last-in-first-out section ordering.
     */
    public String renderSections(Map<String, List<String>> sections) {
        Vector<String> rows = new Vector<>();
        Stack<String> pendingSections = new Stack<>();
        pendingSections.addAll(sections.keySet());
        while (!pendingSections.empty()) {
            String section = pendingSections.pop();
            rows.add(section + ": " + formatSeatSummary(sections.get(section)));
        }
        return new CsvRenderer().render(rows);
    }

    public String renderWithCallback(List<String> rows, ReportRenderer renderer) {
        if (renderer instanceof AbstractReportRenderer abstractRenderer) {
            return abstractRenderer.render(rows);
        }
        return renderer.render(rows);
    }

    public String renderDefault(List<String> rows) {
        ReportRenderer renderer = new ReportRenderer() {
            @Override
            public String render(List<String> values) {
                StringBuffer output = new StringBuffer("Admissions");
                for (String value : values) {
                    output.append(System.lineSeparator()).append(value);
                }
                return output.toString();
            }
        };
        return renderWithCallback(rows, renderer);
    }

    public void writeText(Path target, String content) throws IOException {
        try (Writer writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            bufferedWriter.write(content);
        }
    }

    public String readText(Path source) throws IOException {
        try (Reader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();
        }
    }

    public byte[] roundTripBytes(byte[] content) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (OutputStream stream = output) {
            stream.write(content);
        }
        try (InputStream stream = new ByteArrayInputStream(output.toByteArray())) {
            return stream.readAllBytes();
        } catch (IOException e) {
            throw new IOException("Unable to read report bytes.", e);
        }
    }

    public Set<String> inspectAcademicMethods(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        Set<String> methods = new java.util.LinkedHashSet<>();
        for (Method method : type.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AcademicExample.class)) {
                methods.add(method.getName());
            }
        }
        for (Field field : type.getDeclaredFields()) {
            if (field.getType().isEnum()) {
                methods.add(field.getName());
            }
        }
        return Collections.unmodifiableSet(methods);
    }

    public List<String> collectionSnapshot(List<String> values) {
        List<String> snapshot = new ArrayList<>(values);
        return Collections.unmodifiableList(snapshot);
    }

    public static final class ReportGenerationThread extends Thread {
        private final AdmissionReportToolkit toolkit;
        private final Path target;
        private final String content;
        private volatile IOException failure;

        public ReportGenerationThread(
                AdmissionReportToolkit toolkit, Path target, String content) {
            super("admission-report-writer");
            this.toolkit = toolkit;
            this.target = target;
            this.content = content;
        }

        @Override
        public void run() {
            try {
                toolkit.writeText(target, content);
                Thread.sleep(1);
            } catch (IOException e) {
                failure = e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                failure = new IOException("Report generation interrupted.", e);
            }
        }

        public void rethrowFailure() throws IOException {
            if (failure != null) {
                throw failure;
            }
        }
    }

    public static final class AllocationAuditTask implements Runnable {
        private final List<String> events;
        private final CountDownLatch completed;

        public AllocationAuditTask(List<String> events, CountDownLatch completed) {
            this.events = events;
            this.completed = completed;
        }

        @Override
        public void run() {
            synchronized (events) {
                events.add("allocation-audit-complete");
            }
            completed.countDown();
        }
    }
}
