package gsoo.app;

/**
 * All tunable parameters live here, per the assignment brief's
 * "nothing hardcoded" rule — the examiner can be shown this class as
 * the single place a priority rule, hash table size, etc. would change.
 *
 * STUB: S is a placeholder. The real value is the sum of the last three
 * digits of every team member's index number (team-charter.md §2.7),
 * collected once all 15 numbers are in (README open item #3, owned by A1).
 * Every derived constant below is a placeholder until S is replaced.
 */
public final class Config {

    private Config() {
    }

    public static final int S = 536;

    public static final int URGENCY_WEIGHT = 1 + (S % 5);
    public static final int HASH_TABLE_SIZE = smallestPrimeAtLeast(1000 + (S % 500));
    public static final double ROUTE_PENALTY = 1.0 + ((S % 20) / 10.0);
    public static final long RANDOM_SEED = S;
    public static final int SHIFT_BUDGET_MINUTES = 240 + (S % 120);

    /**
     * Flood-prone / infection-control-restricted roads are the ones whose
     * road_condition_weight already exceeds the 1.0 "good/flat" baseline.
     * C2's Dijkstra charges ROUTE_PENALTY on top of those (README §2.4) —
     * anything with weight >= this threshold counts.
     */
    public static final double FLOOD_PRONE_WEIGHT_THRESHOLD = 1.5;

    /**
     * Effective edge cost = travelTime x roadConditionWeight (README §2.2),
     * defined once here so no algorithm ever recomputes it differently.
     * Flood-prone roads (weight >= FLOOD_PRONE_WEIGHT_THRESHOLD) additionally
     * take ROUTE_PENALTY as a multiplier — the index-derived tunable C2 uses.
     */
    public static double effectiveEdgeCost(double travelTimeSecs, double roadConditionWeight) {
        double base = travelTimeSecs * roadConditionWeight;
        if (roadConditionWeight >= FLOOD_PRONE_WEIGHT_THRESHOLD) {
            return base * ROUTE_PENALTY;
        }
        return base;
    }

    /** priority = urgencyWeight x urgency + slackFactor / max(1, minutesUntilDeadline) */
    public static double dispatchPriority(int urgency, double slackFactor, long minutesUntilDeadline) {
        return URGENCY_WEIGHT * urgency + slackFactor / Math.max(1, minutesUntilDeadline);
    }

    private static int smallestPrimeAtLeast(int n) {
        int candidate = Math.max(n, 2);
        while (!isPrime(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; (long) i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
