package gsoo.algorithms.b3_brute_force;

/**
 * B3 — Brute force: porter-to-job assignment, small n.
 *
 * Given a square cost matrix where cost[i][j] is the cost of assigning
 * porter i to job j, this tries EVERY possible way to pair porters to
 * jobs one-to-one (every permutation), and keeps whichever pairing has
 * the lowest total cost. This is the "no shortcuts, check everything"
 * approach — correct by definition, but its cost grows as n! (factorial),
 * which is exactly why it only works for small n. See
 * BruteForceTraceDemo for a concrete before/after look at that blow-up.
 *
 * Cost here mirrors the same idea used elsewhere in the project
 * (distance/time-based edge cost) — in the real system this would be
 * built from travelTime x roadConditionWeight between a porter's
 * location and a job's pickup location, sourced via Config once the
 * graph API (C4/C5) is available. For now it's a plain cost matrix so
 * this class can be developed and tested independently of that.
 *
 * Constraint compliance: no java.util.TreeMap, HashMap, PriorityQueue,
 * Stack, or ArrayDeque used anywhere in this class.
 */
public class BruteForceAssignment {

    /** Result of solving: which job each porter got, the total cost, and
     *  how many full permutations were actually tried (evidence of the
     *  combinatorial cost — this number is exactly n!). */
    public static class Result {
        public final int[] assignment; // assignment[porterIndex] = jobIndex
        public final int totalCost;
        public final long permutationsTried;

        Result(int[] assignment, int totalCost, long permutationsTried) {
            this.assignment = assignment;
            this.totalCost = totalCost;
            this.permutationsTried = permutationsTried;
        }
    }

    private int[] bestAssignment;
    private int bestCost;
    private long permutationCount;

    /**
     * Solves the assignment problem by brute force.
     * cost must be a non-null, non-empty, square matrix
     * (same number of porters as jobs).
     */
    public Result solve(int[][] cost) {
        if (cost == null) {
            throw new NullPointerException("cost matrix cannot be null");
        }
        int n = cost.length;
        if (n == 0) {
            throw new IllegalArgumentException("cost matrix cannot be empty");
        }
        for (int[] row : cost) {
            if (row == null || row.length != n) {
                throw new IllegalArgumentException(
                        "cost matrix must be square: " + n + " porters requires " + n + " jobs per row");
            }
        }

        int[] jobs = new int[n];
        for (int i = 0; i < n; i++) {
            jobs[i] = i;
        }

        bestAssignment = null;
        bestCost = Integer.MAX_VALUE;
        permutationCount = 0;

        permute(cost, jobs, 0);

        return new Result(bestAssignment, bestCost, permutationCount);
    }

    /**
     * Generates every permutation of job indices (every possible
     * porter-to-job pairing) via recursive swapping, scoring each
     * complete pairing as it's produced. This is the brute-force core:
     * no pruning, no shortcuts — every arrangement gets checked.
     */
    private void permute(int[][] cost, int[] jobs, int k) {
        int n = jobs.length;
        if (k == n) {
            permutationCount++;
            int total = 0;
            for (int porter = 0; porter < n; porter++) {
                total += cost[porter][jobs[porter]];
            }
            if (total < bestCost) {
                bestCost = total;
                bestAssignment = jobs.clone();
            }
            return;
        }
        for (int i = k; i < n; i++) {
            swap(jobs, k, i);
            permute(cost, jobs, k + 1);
            swap(jobs, k, i); // backtrack
        }
    }

    private void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
