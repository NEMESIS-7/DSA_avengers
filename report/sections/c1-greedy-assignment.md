# C1 — Greedy Assignment

**Owner:** Antwi Prince Walker  
**Slot:** C1  
**Structure:** Array-based Assignment Tracking  
**Algorithm:** Greedy Assignment

## What it does

The Greedy Assignment algorithm assigns available porters to jobs based on job urgency and assignment cost.

The greedy strategy is:

1. Select the unprocessed job with the highest urgency.
2. Find the cheapest available porter for that job.
3. Assign the porter to the job.
4. Continue until all jobs have been processed or no porter remains available.

This approach makes the best local decision at each step. However, it does not always guarantee the globally optimal assignment.

## Implementation details

The algorithm uses:

- `assignment[]` to store which job each porter receives.
- `porterAvailable[]` to track available porters.
- `jobProcessed[]` to track processed jobs.

The implementation avoids banned Java built-in structures and uses arrays for the core logic.

## Complexity Analysis

Let:

- J = number of jobs
- P = number of porters

Finding the highest urgency job requires scanning all jobs:

`O(J)`

Finding the cheapest available porter requires scanning all porters:

`O(P)`

Since this is repeated for every job:

Time complexity:

`O(J × (J + P))`

Space complexity:

`O(P + J)`

## Trace Walkthrough

Example:

Cost matrix:

| | Job 0 | Job 1 | Job 2 |
|-|-|-|-|
| Porter 0 | 10 | 2 | 8 |
| Porter 1 | 6 | 7 | 3 |
| Porter 2 | 4 | 9 | 5 |

Urgency:

- Job 0 = 5
- Job 1 = 10
- Job 2 = 7

Step 1:

Highest urgency job:

Job 1

Cheapest available porter:

Porter 0 with cost 2

Assignment:

Porter 0 → Job 1

Step 2:

Highest remaining urgency job:

Job 2

Cheapest available porter:

Porter 1 with cost 3

Assignment:

Porter 1 → Job 2

The process continues until all possible assignments are completed.

## Greedy Counterexample

The algorithm can fail to find the optimal solution.

Cost matrix:

| | Job 0 | Job 1 |
|-|-|-|
| Porter 0 | 1 | 2 |
| Porter 1 | 2 | 100 |

Urgency:

- Job 0 = 10
- Job 1 = 5

Greedy decision:

Job 0 → Porter 0 = 1

Job 1 → Porter 1 = 100

Total greedy cost:

`101`

Optimal solution:

Job 0 → Porter 1 = 2

Job 1 → Porter 0 = 2

Total optimal cost:

`4`

Therefore, greedy produces a worse solution in this case.

## Testing

The C1 implementation includes:

### Normal Test
Checks that the algorithm correctly assigns jobs under normal conditions.

### Boundary Test
Checks the case with one porter and one job.

### Invalid Input Tests
Checks that:

- Null cost matrix is rejected.
- Incorrect urgency size is rejected.

### Counterexample Test
Compares greedy output against brute force to prove that greedy is not always optimal.

All tests passed successfully.

## Evidence Location

Implementation:

`src/main/java/gsoo/algorithms/c1_greedy/GreedyAssignment.java`

Tests:

`src/test/java/gsoo/algorithms/c1_greedy/GreedyAssignmentTest.java`