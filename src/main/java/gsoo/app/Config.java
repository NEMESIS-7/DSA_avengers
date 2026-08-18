package gsoo.app;

/**
 * All tunable parameters live here, per the assignment brief's
 * "nothing hardcoded" rule — the examiner can be shown this class as
 * the single place a priority rule, hash table size, etc. would change.
 *
 * S is the sum of the last three digits of every team member's index number
 * (team-charter.md §2.7), computed from the confirmed roster of all 15 index
 * numbers. A3 independently recomputed this and flagged a mismatch against
 * an earlier 536 figure that had been circulating in gsoo.app.Config and
 * elsewhere — 6316 is the value that actually matches the roster (it also
 * matches the value already used to generate the real patient_ref seed
 * data), so that's what's authoritative here.
 */
public final class Config {

    private Config() {
    }

    public static final int S = 6316;

    public static final int URGENCY_WEIGHT = 1 + (S % 5);
    public static final int HASH_TABLE_SIZE = smallestPrimeAtLeast(1000 + (S % 500));
    public static final double ROUTE_PENALTY = 1.0 + ((S % 20) / 10.0);
    public static final long RANDOM_SEED = S;
    public static final int SHIFT_BUDGET_MINUTES = 240 + (S % 120);

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
