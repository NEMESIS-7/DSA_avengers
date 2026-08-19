package gsoo.algorithms.c1_greedy;

import gsoo.algorithms.b3_brute_force.BruteForceAssignment;

public class GreedyAssignmentTest {

    public static void main(String[] args) {

        GreedyAssignment greedy = new GreedyAssignment();

        // ==========================================
        // TEST 1 - Normal Case
        // ==========================================

        System.out.println("TEST 1 - Normal Case");

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


        // ==========================================
        // TEST 3 - Boundary Case
        // ==========================================

        System.out.println("TEST 3 - Boundary Case");

        /*
         * Boundary case:
         * One porter and one job.
         *
         * The only porter should be assigned
         * to the only available job.
         */

        int[][] boundaryCost = {
            {5}
        };

        int[] boundaryUrgency = {
            10
        };

        GreedyAssignment.Result boundaryResult =
                greedy.solve(
                        boundaryCost,
                        boundaryUrgency
                );

        System.out.println(
                "Boundary total cost: "
                + boundaryResult.totalCost
        );

        System.out.println(
                "Boundary assignment: Porter 0 -> Job "
                + boundaryResult.assignment[0]
        );

        if (boundaryResult.totalCost == 5
                && boundaryResult.assignment[0] == 0) {

            System.out.println("PASS");

        } else {

            System.out.println("FAIL");
        }

        System.out.println();


        // ==========================================
        // TEST 4 - Invalid Input
        // ==========================================

        System.out.println("TEST 4 - Invalid Input");

        /*
         * Invalid case 1:
         * Null cost matrix.
         */

        try {

            greedy.solve(
                    null,
                    new int[]{10}
            );

            System.out.println(
                    "FAIL - Null cost matrix was accepted."
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "PASS - Null cost matrix rejected."
            );
        }


        /*
         * Invalid case 2:
         * Number of urgency values does not match
         * the number of jobs.
         */

        try {

            greedy.solve(
                    new int[][]{{5}},
                    new int[]{10, 20}
            );

            System.out.println(
                    "FAIL - Mismatched urgency length was accepted."
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "PASS - Mismatched urgency length rejected."
            );
        }

        System.out.println();


        // ==========================================
        // ALL TESTS COMPLETED
        // ==========================================

        System.out.println(
                "C1 Greedy Assignment tests completed."
        );
    }
}