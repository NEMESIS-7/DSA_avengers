
package gsoo.algorithms.c1_greedy;

/**
 * Slot C1 (Antwi Prince Walker) - Greedy Assignment.
 *
 * Greedy strategy:
 * 1. Process jobs from highest urgency to lowest urgency.
 * 2. For each job, choose the cheapest available porter.
 * 3. Each porter can handle at most one job.
 *
 * This algorithm is greedy and is not guaranteed to find the globally
 * optimal assignment.
 */
public class GreedyAssignment {

    /**
     * Result of the greedy assignment.
     *
     * assignment[porterIndex] = jobIndex
     * -1 means the porter was not assigned a job.
     */
    public static class Result {

        public final int[] assignment;
        public final int totalCost;

        public Result(int[] assignment, int totalCost) {
            this.assignment = assignment;
            this.totalCost = totalCost;
        }
    }

    /**
     * Greedily assigns porters to jobs.
     *
     * @param cost cost[porter][job] represents the cost of assigning
     *             a particular porter to a particular job
     * @param urgency urgency[job] represents the urgency of each job
     * @return the greedy assignment and its total cost
     */
    public Result solve(int[][] cost, int[] urgency) {

        // Check for invalid input.
        if (cost == null || urgency == null) {
            throw new IllegalArgumentException(
                    "Cost matrix and urgency cannot be null");
        }

        // Number of porters.
        int porters = cost.length;

        // If there are no porters, there is nothing to assign.
        if (porters == 0) {
            return new Result(new int[0], 0);
        }

        // Check that the first row exists.
        if (cost[0] == null) {
            throw new IllegalArgumentException(
                    "Invalid cost matrix");
        }

        // Number of jobs.
        int jobs = cost[0].length;

        // There must be one urgency value for every job.
        if (urgency.length != jobs) {
            throw new IllegalArgumentException(
                    "Urgency length must match number of jobs");
        }

        // Make sure every row has the same number of jobs.
        for (int porter = 0; porter < porters; porter++) {

            if (cost[porter] == null
                    || cost[porter].length != jobs) {

                throw new IllegalArgumentException(
                        "Invalid cost matrix");
            }
        }

        /*
         * assignment[porter] tells us which job the porter receives.
         *
         * -1 means that the porter was not assigned a job.
         */
        int[] assignment = new int[porters];

        for (int porter = 0; porter < porters; porter++) {
            assignment[porter] = -1;
        }

        /*
         * Tracks whether each porter is still available.
         *
         * true  = available
         * false = already assigned
         */
        boolean[] porterAvailable = new boolean[porters];

        for (int porter = 0; porter < porters; porter++) {
            porterAvailable[porter] = true;
        }

        /*
         * Tracks which jobs have already been processed.
         */
        boolean[] jobProcessed = new boolean[jobs];

        // Stores the total cost of all assignments.
        int totalCost = 0;

        /*
         * Process jobs one by one.
         */
        for (int count = 0; count < jobs; count++) {

            /*
             * Find the unprocessed job with the highest urgency.
             */
            int selectedJob = -1;
            int highestUrgency = Integer.MIN_VALUE;

            for (int job = 0; job < jobs; job++) {

                if (!jobProcessed[job]
                        && urgency[job] > highestUrgency) {

                    highestUrgency = urgency[job];
                    selectedJob = job;
                }
            }

            /*
             * If no job was found, stop.
             */
            if (selectedJob == -1) {
                break;
            }

            // Mark this job as processed.
            jobProcessed[selectedJob] = true;

            /*
             * Find the cheapest available porter for this job.
             */
            int cheapestPorter = -1;
            int cheapestCost = Integer.MAX_VALUE;

            for (int porter = 0; porter < porters; porter++) {

                if (porterAvailable[porter]
                        && cost[porter][selectedJob] < cheapestCost) {

                    // IMPORTANT: update the cheapest cost.
                    cheapestCost = cost[porter][selectedJob];

                    // Remember which porter is cheapest.
                    cheapestPorter = porter;
                }
            }

            /*
             * Assign the cheapest porter if one is available.
             */
            if (cheapestPorter != -1) {

                // Store the job assigned to this porter.
                assignment[cheapestPorter] = selectedJob;

                // This porter cannot receive another job.
                porterAvailable[cheapestPorter] = false;

                // Add the assignment cost to the total.
                totalCost += cheapestCost;
            }
        }

        // Return the final assignment and total cost.
        return new Result(assignment, totalCost);
    }
}