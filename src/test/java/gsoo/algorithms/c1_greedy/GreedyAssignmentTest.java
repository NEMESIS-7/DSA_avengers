package gsoo.algorithms.c1_greedy;

import gsoo.algorithms.b3_brute_force.BruteForceAssignment;

public class GreedyAssignmentTest {

    public static void main(String[] args) {

        // ==========================================
        // TEST 1 - Normal Case
        // ==========================================

        System.out.println("TEST 1 - Normal Case");

        GreedyAssignment greedy = new GreedyAssignment();

        int[][] cost = {
            {10, 2, 8},
            {6, 7, 3},
            {4, 9, 5}
        };

        int[] urgency = {
            5, 10, 7
        };

        GreedyAssignment.Result result =
                greedy.solve(cost, urgency);

        System.out.println("Total cost: " + result.totalCost);

        System.out.print("Assignments: ");

        for (int porter = 0;
             porter < result.assignment.length;
             porter++) {

            System.out.print(
                    "Porter " + porter
                    + " -> Job " + result.assignment[porter]
            );

            if (porter < result.assignment.length - 1) {
                System.out.print(", ");
            }
        }

        System.out.println();
        System.out.println("PASS");
        System.out.println();


        // ==========================================
        // TEST 2 - Greedy Counterexample
        // ==========================================

        System.out.println("TEST 2 - Greedy Counterexample");

        /*
         * Cost matrix:
         *
         *             Job 0   Job 1
         * Porter 0      1       2
         * Porter 1      2      100
         *
         * Job 0 has higher urgency.
         *
         * Greedy chooses:
         *
         * Job 0 -> Porter 0 = 1
         * Job 1 -> Porter 1 = 100
         *
         * Greedy total = 101
         *
         * Brute force finds:
         *
         * Job 0 -> Porter 1 = 2
         * Job 1 -> Porter 0 = 2
         *
         * Optimal total = 4
         */

        int[][] counterexampleCost = {
            {1, 2},
            {2, 100}
        };

        int[] counterexampleUrgency = {
            10, 5
        };

        GreedyAssignment.Result greedyResult =
                greedy.solve(
                        counterexampleCost,
                        counterexampleUrgency
                );

        BruteForceAssignment bruteForce =
                new BruteForceAssignment();

        BruteForceAssignment.Result bruteResult =
                bruteForce.solve(counterexampleCost);

        System.out.println(
                "Greedy total cost: "
                + greedyResult.totalCost
        );

        System.out.println(
                "Brute force total cost: "
                + bruteResult.totalCost
        );

        if (greedyResult.totalCost > bruteResult.totalCost) {

            System.out.println("PASS");
            System.out.println(
                    "Counterexample confirmed: "
                    + "Greedy is worse than Brute Force."
            );

        } else {

            System.out.println("FAIL");
        }

        System.out.println();
        System.out.println(
                "C1 Greedy Assignment tests completed."
        );
    }
}