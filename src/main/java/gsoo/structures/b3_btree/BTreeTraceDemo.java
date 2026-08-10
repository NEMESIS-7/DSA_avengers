package gsoo.structures.b3_btree;

/**
 * B3 - evidence generator (REAL DATA VERSION).
 *
 * Updated 06/08/2026 once A3 (Gabriel) shipped the real dataset CSVs.
 * Uses genuine requestId values from service_requests_template-1.csv
 * instead of sample integers. Note the key type changed from Integer
 * to String, since real requestIds are formatted like "REQ-0001" -
 * nothing else in BTree.java needed to change for this swap, since it
 * was built generic from the start.
 *
 * How to run (from the project root, after `mvn compile`):
 *   java -cp target/classes gsoo.structures.b3_btree.BTreeTraceDemo
 */
public class BTreeTraceDemo {

    public static void main(String[] args) {
        // t = 2 -> each node holds at most 3 keys before splitting.
        BTree<String, String> index = new BTree<>(2);

        System.out.println("=== B3 - B-tree evidence run (real data) ===\n");

        System.out.println("--- Step 1: insert real requestIds one at a time, tree BEFORE the split ---");
        // Real requestIds, straight from service_requests_template-1.csv (rows 1-8)
        String[] requestIds = {
                "REQ-0001", "REQ-0002", "REQ-0003", "REQ-0004",
                "REQ-0005", "REQ-0006", "REQ-0007", "REQ-0008"
        };
        for (int i = 0; i < requestIds.length - 1; i++) {
            index.insert(requestIds[i], "Request " + requestIds[i]);
        }
        index.printTree();

        System.out.println("\n--- Step 2: insert one more requestId - this triggers a node split ---");
        String triggerKey = requestIds[requestIds.length - 1];
        System.out.println("Inserting requestId " + triggerKey + " ...");
        index.insert(triggerKey, "Request " + triggerKey);

        System.out.println("\n--- Step 3: tree AFTER the split ---");
        index.printTree();

        System.out.println("\n--- Step 4: search trace for a requestId that EXISTS ---");
        StringBuilder foundTrace = new StringBuilder();
        String foundValue = index.searchWithTrace("REQ-0004", foundTrace);
        System.out.print(foundTrace);
        System.out.println("Result: " + foundValue);

        System.out.println("\n--- Step 5: search trace for a requestId that does NOT exist ---");
        StringBuilder missTrace = new StringBuilder();
        String missValue = index.searchWithTrace("REQ-9999", missTrace);
        System.out.print(missTrace);
        System.out.println("Result: " + missValue);

        System.out.println("\n=== End of evidence run ===");
    }
}
