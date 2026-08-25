package gsoo.algorithms.c5_dp_knapsack;

import gsoo.app.Config;
import gsoo.db.ServiceRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;

public final class KnapsackTraceDemo {

    private KnapsackTraceDemo() {
    }

    public static void main(String[] args) throws IOException {
        Path csvPath = Path.of(
                "experiments",
                "csv",
                "service_requests_template.csv"
        );

        ServiceRequest[] requests = loadRequests(csvPath);
        HospitalKnapsackPlanner planner =
                new HospitalKnapsackPlanner();

        Knapsack.Item[] items =
                planner.buildPendingItems(requests);

        Knapsack.Result result = new Knapsack().solve(
                items,
                Config.SHIFT_BUDGET_MINUTES
        );

        System.out.println(
                "=== DP KNAPSACK REAL-DATASET TRACE ==="
        );
        System.out.println("Requests loaded: " + requests.length);
        System.out.println("Pending candidates: " + items.length);
        System.out.println(
                "Shift capacity: "
                        + Config.SHIFT_BUDGET_MINUTES
                        + " minutes"
        );
        System.out.println(
                "Value rule: urgency x URGENCY_WEIGHT ("
                        + Config.URGENCY_WEIGHT + ")"
        );
        System.out.println(
                "Time-cost rule: recorded SLA window "
                        + "(deadline - submitted time)"
        );

        printSelectedTableColumns(items, result);
        printReconstruction(result);
    }

    private static void printSelectedTableColumns(
            Knapsack.Item[] items,
            Knapsack.Result result
    ) {
        int[] possibleColumns = {
            0,
            60,
            120,
            180,
            240,
            300,
            result.capacityMinutes
        };

        int[] columns = new int[possibleColumns.length];
        int columnCount = 0;

        for (int candidate : possibleColumns) {
            if (candidate > result.capacityMinutes) {
                continue;
            }

            boolean duplicate = false;
            for (int i = 0; i < columnCount; i++) {
                if (columns[i] == candidate) {
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate) {
                columns[columnCount] = candidate;
                columnCount++;
            }
        }

        System.out.println();
        System.out.println("Selected columns from the generated DP table:");
        System.out.print("Request | Minutes | Value");

        for (int i = 0; i < columnCount; i++) {
            System.out.print(" | C=" + columns[i]);
        }
        System.out.println();

        for (int i = 0; i < items.length; i++) {
            Knapsack.Item item = items[i];

            System.out.print(
                    item.requestId + " | "
                            + item.requiredMinutes + " | "
                            + item.priorityValue
            );

            for (int j = 0; j < columnCount; j++) {
                System.out.print(
                        " | " + result.table[i + 1][columns[j]]
                );
            }
            System.out.println();
        }
    }

    private static void printReconstruction(
            Knapsack.Result result
    ) {
        System.out.println();
        System.out.println("Reconstructed optimal request set:");

        for (int i = 0;
             i < result.selectedItems.length;
             i++) {
            Knapsack.Item item = result.selectedItems[i];

            System.out.println(
                    (i + 1) + ". " + item.requestId
                            + " | minutes=" + item.requiredMinutes
                            + " | value=" + item.priorityValue
            );
        }

        System.out.println(
                "Selected requests: "
                        + result.selectedItems.length
        );
        System.out.println(
                "Total minutes used: " + result.totalMinutes
        );
        System.out.println(
                "Remaining minutes: "
                        + result.remainingMinutes()
        );
        System.out.println(
                "Maximum total value: " + result.totalValue
        );
    }

    private static ServiceRequest[] loadRequests(
            Path csvPath
    ) throws IOException {
        ServiceRequest[] requests = new ServiceRequest[16];
        int count = 0;

        try (BufferedReader reader =
                     Files.newBufferedReader(csvPath)) {

            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",", -1);

                if (fields.length < 10) {
                    throw new IllegalArgumentException(
                            "Invalid service request CSV row: " + line
                    );
                }

                if (count == requests.length) {
                    ServiceRequest[] larger =
                            new ServiceRequest[requests.length * 2];

                    for (int i = 0; i < count; i++) {
                        larger[i] = requests[i];
                    }
                    requests = larger;
                }

                String assignedResource =
                        fields[8].isEmpty() ? null : fields[8];
                String patientRef =
                        fields[9].isEmpty() ? null : fields[9];

                requests[count] = new ServiceRequest(
                        fields[0],
                        fields[1],
                        patientRef,
                        fields[2],
                        fields[3],
                        Integer.parseInt(fields[4]),
                        fields[5],
                        parseTimestamp(fields[6]),
                        parseTimestamp(fields[7]),
                        assignedResource
                );
                count++;
            }
        }

        ServiceRequest[] exact = new ServiceRequest[count];
        for (int i = 0; i < count; i++) {
            exact[i] = requests[i];
        }

        return exact;
    }

    private static Timestamp parseTimestamp(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        return Timestamp.valueOf(value.replace('T', ' '));
    }
}
