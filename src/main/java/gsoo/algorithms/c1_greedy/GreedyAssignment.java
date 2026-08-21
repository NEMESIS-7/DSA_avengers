package gsoo.algorithms.c1_greedy;

/**
 * Slot C1 (Antwi Prince Walker) — Greedy assignment, paired with the C1 Set
 * above (visited/locked-down departments during the assignment walk, same
 * pattern as Stack<->DFS and Heap<->Dijkstra elsewhere in the project).
 *
 * Frozen contract, set by A1 (Architect). Method body is a stub for C1 to
 * implement.
 *
 * Deliberately shaped like BruteForceAssignment (B3) — same porter-to-job
 * cost-matrix domain, same Result pattern — since the required "greedy
 * failure case" counterexample (evidence ledger, owned by C1) is easiest to
 * construct when both algorithms take the exact same input and you can show
 * greedy landing on a worse total cost than brute force on the same matrix.
 */
public class GreedyAssignment {

    /** Result of solving: which job each porter got (or -1, unassigned), and the total cost. */
    public static class Result {
        public final int[] assignment; // assignment[porterIndex] = jobIndex, or -1 if unassigned
        public final int totalCost;

        public Result(int[] assignment, int totalCost) {
            this.assignment = assignment;
            this.totalCost = totalCost;
        }
    }

    /**
     * Greedily assigns porters to jobs: processes jobs in descending urgency
     * order, and for each picks the cheapest still-available porter. Unlike
     * BruteForceAssignment, this does not try every permutation and is not
     * guaranteed optimal — that's the whole point of the counterexample.
     */
    public Result solve(int[][] cost, int[] urgency) {
        throw new UnsupportedOperationException("C1: implement me");
    }
}
