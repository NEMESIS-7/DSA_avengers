package gsoo.algorithms.b3_brute_force;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for B3 — Brute force porter-to-job assignment.
 * Covers the three required cases: normal, boundary, invalid input.
 */
public class BruteForceAssignmentTest {

    // ---------- 1. Normal case ----------
    // A 3x3 cost matrix with a known, hand-verifiable optimal assignment.
    // Porter 0 -> Job 2 (cost 2), Porter 1 -> Job 0 (cost 2), Porter 2 -> Job 1 (cost 2)
    // Total = 6, and no other pairing beats it (checked by hand below).

    @Test
    void solve_normalCase_findsMinimumCostAssignment() {
        int[][] cost = {
                {9, 2, 7},
                {6, 4, 3},
                {5, 8, 1}
        };
        // By hand: best pairing is (0,1)=2 + (1,0)=6 + (2,2)=1 = 9
        // vs (0,1)=2 + (1,2)=3 + (2,0)=5 = 10
        // vs (0,2)=7 + (1,0)=6 + (2,1)=8 = 21 ... etc.
        // The true minimum across all 6 permutations for this matrix is 9.
        BruteForceAssignment solver = new BruteForceAssignment();
        BruteForceAssignment.Result result = solver.solve(cost);

        assertEquals(9, result.totalCost);
        assertEquals(6, result.permutationsTried, "3! = 6 permutations must all be tried");
    }

    // ---------- 2. Boundary case ----------
    // Smallest valid input: a single porter, single job. Trivial, but
    // must not crash or mishandle the n=1 edge case.

    @Test
    void solve_boundaryCase_singlePorterSingleJob() {
        int[][] cost = { {42} };

        BruteForceAssignment solver = new BruteForceAssignment();
        BruteForceAssignment.Result result = solver.solve(cost);

        assertEquals(42, result.totalCost);
        assertEquals(0, result.assignment[0]);
        assertEquals(1, result.permutationsTried, "1! = 1 permutation for a single porter/job");
    }

    // ---------- 3. Invalid input case ----------
    // Two forms of invalid: a non-square matrix (unequal porters vs
    // jobs, which the assignment problem as defined here doesn't
    // support), and a null matrix.

    @Test
    void solve_nonSquareMatrix_throwsIllegalArgumentException() {
        int[][] cost = {
                {1, 2, 3},
                {4, 5, 6}
        }; // 2 porters, 3 jobs -- not a valid one-to-one assignment

        BruteForceAssignment solver = new BruteForceAssignment();
        assertThrows(IllegalArgumentException.class, () -> solver.solve(cost));
    }

    @Test
    void solve_nullMatrix_throwsNullPointerException() {
        BruteForceAssignment solver = new BruteForceAssignment();
        assertThrows(NullPointerException.class, () -> solver.solve(null));
    }
}
