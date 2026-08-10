package gsoo.algorithms.a1_binary_search;

import gsoo.structures.a1_dynamic_array.DynamicArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evidence generator for A1: loads the real request IDs straight out of the
 * committed seed data (sql/seed/service_requests_seed.sql — 300 real
 * REQ-000x IDs, no live DB required so this is reproducible by anyone who
 * checks out the repo), puts them in the dynamic array, and runs a traced
 * binary search. Output feeds docs/evidence/a1-trace-tables.md directly —
 * the numbers there are copied from a real run of this class, not typed by hand.
 */
public class BinarySearchTraceDemo {

    private static final String SEED_FILE = "sql/seed/service_requests_seed.sql";
    private static final Pattern REQUEST_ID_PATTERN = Pattern.compile("REQ-\\d{4}");

    public static void main(String[] args) throws IOException {
        DynamicArray<String> requestIds = loadRequestIdsFromSeed(SEED_FILE);
        System.out.println("Loaded " + requestIds.size() + " request IDs from " + SEED_FILE + " (expect 300)");

        insertionSort(requestIds);

        String[] sorted = new String[requestIds.size()];
        for (int i = 0; i < requestIds.size(); i++) {
            sorted[i] = requestIds.get(i);
        }
        System.out.println("First: " + sorted[0] + "  Last: " + sorted[sorted.length - 1]);

        BinarySearch<String> search = new BinarySearch<>();

        System.out.println("\n=== Trace 1: present target REQ-0187 ===");
        StringBuilder trace1 = new StringBuilder();
        int found = search.binarySearchWithTrace(0, sorted.length - 1, sorted, "REQ-0187", trace1);
        System.out.print(trace1);
        System.out.println("Result: index " + found);

        System.out.println("\n=== Trace 2: absent target REQ-9999 ===");
        StringBuilder trace2 = new StringBuilder();
        int notFound = search.binarySearchWithTrace(0, sorted.length - 1, sorted, "REQ-9999", trace2);
        System.out.print(trace2);
        System.out.println("Result: " + notFound + " (not present)");
    }

    private static DynamicArray<String> loadRequestIdsFromSeed(String path) throws IOException {
        DynamicArray<String> ids = new DynamicArray<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = REQUEST_ID_PATTERN.matcher(line);
                if (m.find()) {
                    ids.add(m.group());
                }
            }
        }
        return ids;
    }

    // Defensive: the seed file happens to already be in order, but binary search's
    // precondition is "sorted," so this demo doesn't assume that silently.
    private static void insertionSort(DynamicArray<String> arr) {
        for (int i = 1; i < arr.size(); i++) {
            String key = arr.get(i);
            int j = i - 1;
            while (j >= 0 && arr.get(j).compareTo(key) > 0) {
                arr.set(j + 1, arr.get(j));
                j--;
            }
            arr.set(j + 1, key);
        }
    }
}
