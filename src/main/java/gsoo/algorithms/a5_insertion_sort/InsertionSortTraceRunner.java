package gsoo.algorithms.a5_insertion_sort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;

public class InsertionSortTraceRunner {

    // Lightweight record matching the hospital domain model (README §2.3)
    public static class ServiceRequest {
        public String requestId;
        public String category;
        public int urgency; // 1 (lowest) to 5 (highest/emergency)
        public int travelTimeSeconds;

        public ServiceRequest(String requestId, String category, int urgency, int travelTimeSeconds) {
            this.requestId = requestId;
            this.category = category;
            this.urgency = urgency;
            this.travelTimeSeconds = travelTimeSeconds;
        }

        @Override
        public String toString() {
            return String.format("[%s|U%d|%s]", requestId, urgency, category);
        }
    }

    public static void main(String[] args) {
        // Load requests from CSV or fallback to representative batch from domain model
        ServiceRequest[] requests = loadSampleRequests();

        System.out.println("Generating Insertion Sort trace for " + requests.length + " hospital service requests...");

        // Comparator: Sort primarily by Urgency descending (5 down to 1), then by Travel Time ascending
        Comparator<ServiceRequest> priorityComparator = (a, b) -> {
            if (b.urgency != a.urgency) {
                return Integer.compare(b.urgency, a.urgency); // Higher urgency comes first
            }
            return Integer.compare(a.travelTimeSeconds, b.travelTimeSeconds);
        };

        File reportDir = new File("report");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        File traceFile = new File(reportDir, "a5_insertion_sort_trace.md");

        try (PrintWriter writer = new PrintWriter(new FileWriter(traceFile))) {
            writer.println("# Insertion Sort Trace Table — Hospital Service Request Prioritization");
            writer.println("Generated automatically by Slot A5 (`InsertionSortTraceRunner.java`)\n");
            writer.println("| Pass ($i$) | Key Element Inserted | Comparisons | Shifts | Array State After Pass |");
            writer.println("|---|---|---|---|---|");

            // Initial State (Pass 0)
            writer.printf("| 0 (Initial) | N/A | 0 | 0 | `%s` |\n", arrayToString(requests));

            int totalComparisons = 0;
            int totalShifts = 0;

            for (int i = 1; i < requests.length; i++) {
                ServiceRequest key = requests[i];
                int j = i - 1;
                int passComparisons = 0;
                int passShifts = 0;

                while (j >= 0) {
                    passComparisons++;
                    if (priorityComparator.compare(requests[j], key) > 0) {
                        requests[j + 1] = requests[j];
                        passShifts++;
                        j--;
                    } else {
                        break;
                    }
                }
                requests[j + 1] = key;

                totalComparisons += passComparisons;
                totalShifts += passShifts;

                writer.printf("| %d | `%s` | %d | %d | `%s` |\n",
                        i, key.toString(), passComparisons, passShifts, arrayToString(requests));
            }

            writer.println("\n## Execution Metrics");
            writer.printf("- **Total Elements ($n$):** %d\n", requests.length);
            writer.printf("- **Total Key Comparisons:** %d\n", totalComparisons);
            writer.printf("- **Total Element Shifts:** %d\n", totalShifts);
            writer.printf("- **Theoretical Average Shifts ($n(n-1)/4$):** %.2f\n", (requests.length * (requests.length - 1)) / 4.0);

            System.out.println(" Trace generated successfully at: " + traceFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write trace output: " + e.getMessage());
        }
    }

    private static String arrayToString(ServiceRequest[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i].toString());
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private static ServiceRequest[] loadSampleRequests() {
        // Look for requests CSV in sql/ or resources
        File csv = new File("sql/requests.csv");
        if (csv.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
                // Pre-count lines
                int count = 0;
                String line;
                br.readLine(); // header
                while (br.readLine() != null && count < 15) count++; // 15-item slice for clean trace table

                ServiceRequest[] list = new ServiceRequest[count];
                BufferedReader br2 = new BufferedReader(new FileReader(csv));
                br2.readLine();
                int idx = 0;
                while ((line = br2.readLine()) != null && idx < count) {
                    String[] cols = line.split(",");
                    list[idx++] = new ServiceRequest(cols[0].trim(), cols[1].trim(),
                            Integer.parseInt(cols[2].trim()), Integer.parseInt(cols[3].trim()));
                }
                br2.close();
                return list;
            } catch (Exception ignored) {}
        }

        // Standard 10-request operational slice across hospital categories (§2.3)
        return new ServiceRequest[] {
            new ServiceRequest("REQ-101", "MEALS", 1, 300),
            new ServiceRequest("REQ-102", "REFERRAL_IN", 5, 720),
            new ServiceRequest("REQ-103", "SPECIMEN", 3, 180),
            new ServiceRequest("REQ-104", "BLOOD", 5, 240),
            new ServiceRequest("REQ-105", "LINEN", 2, 450),
            new ServiceRequest("REQ-106", "DRUG_DELIVERY", 4, 150),
            new ServiceRequest("REQ-107", "PATIENT_TRANSFER", 4, 600),
            new ServiceRequest("REQ-108", "MAINTENANCE", 1, 900),
            new ServiceRequest("REQ-109", "STERILE_SUPPLY", 3, 360),
            new ServiceRequest("REQ-110", "MORTUARY_TRANSFER", 2, 500)
        };
    }
}