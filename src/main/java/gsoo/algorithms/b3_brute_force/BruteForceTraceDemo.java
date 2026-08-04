package gsoo.algorithms.b3_brute_force;

import java.util.Random;

/**
 * B3 — evidence generator for the brute-force algorithm.
 *
 * Produces two pieces of evidence:
 *   1. A solved small porter-to-job assignment (concrete, readable result)
 *   2. A timing table showing permutations tried and elapsed time as n
 *      grows, which is the concrete proof of "why brute force doesn't
 *      scale" the Definition of Done asks for.
 *
 * How to run (from the project root, after `mvn compile`):
 *   java -cp target/classes gsoo.algorithms.b3_brute_force.BruteForceTraceDemo
 */
public class BruteForceTraceDemo {

    public static void main(String[] args) {
        System.out.println("=== B3 - Brute force evidence run ===\n");

        System.out.println("--- Part 1: small solved example (4 porters, 4 jobs) ---");
        int[][] sampleCost = {
                {12, 7, 9, 15},
                {8, 14, 6, 11},
                {13, 5, 10, 9},
                {6, 12, 8, 7}
        };
        BruteForceAssignment solver = new BruteForceAssignment();
        BruteForceAssignment.Result result = solver.solve(sampleCost);

        for (int porter = 0; porter < result.assignment.length; porter++) {
            System.out.println("Porter " + porter + " -> Job " + result.assignment[porter]
                    + " (cost " + sampleCost[porter][result.assignment[porter]] + ")");
        }
        System.out.println("Total cost: " + result.totalCost);
        System.out.println("Permutations tried: " + result.permutationsTried + " (that's 4!)");

        System.out.println("\n--- Part 2: how it grows as n increases ---");
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
                cost[i][j] = 1 + rng.nextInt(20); // cost between 1 and 20
            }
        }
        return cost;
    }
}
