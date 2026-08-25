package gsoo.algorithms.a2_linear_search;

import gsoo.structures.a2_linked_list.LinkedList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evidence generator for A2: loads the real request IDs straight out of the
 * committed seed data (sql/seed/service_requests_seed.sql — 300 real
 * REQ-000x IDs, no live DB required, matching the convention
 * BinarySearchTraceDemo (A1) already uses), puts them in the A2 LinkedList
 * to get an ordinary array out via the iterator, and runs a traced linear
 * search. Output feeds docs/evidence/a2-trace-tables.md directly.
 *
 * Deliberately searches the SAME target ids BinarySearchTraceDemo does
 * (REQ-0187 present, REQ-9999 absent) — that's what makes the "linear vs
 * binary search" comparison (evidence ledger, owned jointly by A1 and A2) a
 * fair one: identical input, identical target, only the algorithm differs.
 */
public class LinearSearchTraceDemo {

    private static final String SEED_FILE = "sql/seed/service_requests_seed.sql";
    private static final Pattern REQUEST_ID_PATTERN = Pattern.compile("REQ-\\d{4}");

    public static void main(String[] args) throws IOException {
        LinkedList<String> requestIds = loadRequestIdsFromSeed(SEED_FILE);
        System.out.println("Loaded " + requestIds.size() + " request IDs from " + SEED_FILE
                + " into the A2 linked list (expect 300).");

        String[] ids = new String[requestIds.size()];
        int i = 0;
        for (String id : requestIds) {
            ids[i++] = id;
        }
        System.out.println("First: " + ids[0] + "  Last: " + ids[ids.length - 1]);

        LinearSearch<String> search = new LinearSearch<>();

        System.out.println("\n=== Trace 1: present target REQ-0187 (same id A1's demo uses) ===");
        StringBuilder trace1 = new StringBuilder();
        int found = search.searchWithTrace(ids, "REQ-0187", trace1);
        System.out.print(trace1);
        System.out.println("Result: index " + found + " — took " + (found + 1) + " comparisons.");

        System.out.println("\n=== Trace 2: absent target REQ-9999 ===");
        StringBuilder trace2 = new StringBuilder();
        int notFound = search.searchWithTrace(ids, "REQ-9999", trace2);
        System.out.println("(Full step-by-step trace omitted here for length — " + ids.length
                + " comparisons made, one per real request ID, all misses.)");
        System.out.println("Result: " + notFound + " (not present, checked all " + ids.length + " entries)");
    }

    private static LinkedList<String> loadRequestIdsFromSeed(String path) throws IOException {
        LinkedList<String> ids = new LinkedList<>();
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
}
