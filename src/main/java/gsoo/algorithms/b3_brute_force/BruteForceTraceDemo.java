package gsoo.algorithms.b3_brute_force;

import java.util.Random;

/**
 * B3 - evidence generator for the brute-force algorithm (REAL DATA VERSION).
 *
 * Updated 06/08/2026 once A3 (Gabriel) shipped the real dataset CSVs.
 * Part 1 now uses genuine porter IDs (resources_template-1.csv, type =
 * porter, is_available = 1) and genuine pending requestIds
 * (service_requests_template-1.csv, status = PENDING) as labels.
 *
 * Honest limitation: real cost (travelTime x roadConditionWeight
 * between a porter's home location and a job's source location)
 * requires the graph/routing API, which is owned by C2/C4 and not yet
 * available. Costs below remain representative placeholders until
 * that integration lands -- only the porter/job IDENTITIES are real
 * for now, not yet the distances between them. This will be updated
 * again once the graph API is frozen and usable.
 *
 * Part 2 (the growth/timing table) is an abstract scalability
 * demonstration, independent of any specific real IDs, so it is
 * unchanged.
 *
 * How to run (from the project root, after `mvn compile`):
 *   java -cp target/classes gsoo.algorithms.b3_brute_force.BruteForceTraceDemo
 */
public class BruteForceTraceDemo {

    public static void main(String[] args) {
        System.out.println("=== B3 - Brute force evidence run (real porter/job IDs) ===\n");

        System.out.println("--- Part 1: small solved example (4 real porters, 4 real pending jobs) ---");
        // Real, available porters from resources_template-1.csv
        String[] porters = {"RES-P01", "RES-P02", "RES-P03", "RES-P04"};
        // Real PENDING requests from service_requests_template-1.csv
        String[] jobs = {"REQ-0005", "REQ-0008", "REQ-0010", "REQ-0012"};

        // NOTE: cost values are still representative placeholders --
        // real cost needs the graph API (not yet available). Porter
        // and job IDENTITIES are real; the distances between them
        // are not yet.
        int[][] sampleCost = {
                {12, 7, 9, 15},
                {8, 14, 6, 11},
                {13, 5, 10, 9},
                {6, 12, 8, 7}
        };

        BruteForceAssignment solver = new BruteForceAssignment();
        BruteForceAssignment.Result result = solver.solve(sampleCost);

        for (int p = 0; p < result.assignment.length; p++) {
            int j = result.assignment[p];
            System.out.println("Porter " + porters[p] + " -> Job " + jobs[j]
                    + " (cost " + sampleCost[p][j] + ")");
        }
        System.out.println("Total cost: " + result.totalCost);
        System.out.println("Permutations tried: " + result.permutationsTried + " (that's 4!)");

        System.out.println("\n--- Part 2: how it grows as n increases (abstract scalability test) ---");
        System.out.printf("%-4s %-18s %-15s%n", "n", "permutations (n!)", "time (ms)");

        Random rng = new Random(42); // fixed seed -> reproducible sample data
        for (int n = 1; n <= 10; n++) {
            int[][] cost = generateCostMatrix(n, rng);

            long start = System.nanoTime();
            BruteForceAssignment.Result r = new BruteForceAssignment().solve(cost);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            System.out.printf("%-4d %-18d %-15d%n", n, r.permutationsTried, elapsedMs);
        }

        System.out.println("\nNotice how the permutation count (n!) and the time both");
        System.out.println("explode past roughly n=9-10 -- that's the concrete evidence");
        System.out.println("that this approach only works for small n, as required.");

        System.out.println("\n=== End of evidence run ===");
    }

    /** Builds a reproducible random cost matrix of the given size. */
    private static int[][] generateCostMatrix(int n, Random rng) {
        int[][] cost = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cost[i][j] = 1 + rng.nextInt(20);
            }
        }
        return cost;
    }
}
