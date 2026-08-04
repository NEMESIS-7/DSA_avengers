package gsoo.structures.b3_btree;

/**
 * B3 — evidence generator.
 *
 * Running this class produces the two pieces of evidence B3 owes:
 *   1. A search trace (the path taken through the tree to find/miss a key)
 *   2. A before/after snapshot around a node split
 *
 * This uses small representative sample data (mimicking request IDs from
 * the request/runs tables) since the real seeded dataset isn't loaded
 * yet. Once A3 ships the real CSVs / DB, swap the sample inserts below
 * for real requestId values pulled from the dataset — nothing else in
 * this class needs to change.
 *
 * How to run (from the project root, after `mvn compile`):
 *   java -cp target/classes gsoo.structures.b3_btree.BTreeTraceDemo
 */
public class BTreeTraceDemo {

    public static void main(String[] args) {
        // t = 2 -> each node holds at most 3 keys before splitting.
        // Small on purpose so a split is easy to trigger and easy to read.
        BTree<Integer, String> index = new BTree<>(2);

        System.out.println("=== B3 — B-tree evidence run ===\n");

        System.out.println("--- Step 1: insert requestIds one at a time, tree BEFORE the split ---");
        int[] requestIds = {105, 220, 340, 410, 512, 630, 745, 800};
        // Insert everything except the last one, then print — this is the
        // "before" snapshot. The last leaf is already at max capacity
        // (3 keys) here, one insert away from being forced to split.
        for (int i = 0; i < requestIds.length - 1; i++) {
            index.insert(requestIds[i], "Request-" + requestIds[i]);
        }
        index.printTree();

        System.out.println("\n--- Step 2: insert one more requestId — this triggers a node split ---");
        int triggerKey = requestIds[requestIds.length - 1];
        System.out.println("Inserting requestId " + triggerKey + " ...");
        index.insert(triggerKey, "Request-" + triggerKey);

        System.out.println("\n--- Step 3: tree AFTER the split ---");
        index.printTree();

        System.out.println("\n--- Step 4: search trace for a key that EXISTS ---");
        StringBuilder foundTrace = new StringBuilder();
        String foundValue = index.searchWithTrace(410, foundTrace);
        System.out.print(foundTrace);
        System.out.println("Result: " + foundValue);

        System.out.println("\n--- Step 5: search trace for a key that does NOT exist ---");
        StringBuilder missTrace = new StringBuilder();
        String missValue = index.searchWithTrace(999, missTrace);
        System.out.print(missTrace);
        System.out.println("Result: " + missValue);

        System.out.println("\n=== End of evidence run ===");
    }
}
