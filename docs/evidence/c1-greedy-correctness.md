# C1 — Greedy Assignment Correctness Proof Sketch

## 1. Algorithm Idea

The C1 greedy algorithm assigns porters to jobs using the following strategy:

1. Process jobs from highest urgency to lowest urgency.
2. For the current job, examine all porters who are still available.
3. Select the available porter with the lowest assignment cost.
4. Assign that porter to the job.
5. Mark the porter as unavailable.
6. Continue until all jobs have been processed or no porter remains available.

The algorithm therefore makes the cheapest available choice for each job at the time that job is processed.

## 2. Correctness Idea

The algorithm correctly implements the required greedy strategy because, for every selected job, it considers only porters who have not already been assigned.

Among those available porters, it keeps track of the smallest assignment cost. The porter with that smallest cost is selected.

After the assignment, that porter is marked unavailable. Therefore, a porter cannot be assigned to more than one job.

This means that every assignment produced by the algorithm satisfies the one-porter-to-one-job restriction.

## 3. Why the Algorithm Follows the Greedy Rule

Suppose the current job is `j`.

Let the available porters be:

```text
P = {p1, p2, ..., pk}
```

The algorithm compares:

```text
cost[p1][j], cost[p2][j], ..., cost[pk][j]
```

and selects the porter `p` with the minimum cost.

Therefore:

```text
cost[p][j] <= cost[pi][j]
```

for every available porter `pi`.

Thus, at every step, the algorithm makes the cheapest possible local choice for the current job.

## 4. Limitation of the Greedy Approach

Although the algorithm always makes the cheapest local choice, this does not guarantee the globally minimum total assignment cost.

A porter selected because they are cheapest for the current high-urgency job may be the only inexpensive choice for another job.

Using that porter too early can force the later job to use a very expensive porter.

Therefore, a locally optimal decision can lead to a globally non-optimal solution.

This is why the C1 implementation is deliberately described as a greedy algorithm rather than an exact optimization algorithm.

## 5. Counterexample Evidence

The C1 test includes a counterexample comparing the greedy solution with the brute-force solution.

The observed result was:

```text
Greedy total cost: 101
Brute force total cost: 4
```

The brute-force algorithm checks every possible assignment and finds the globally minimum cost of `4`.

The greedy algorithm finds a valid assignment, but its total cost is `101`.

Therefore:

```text
101 > 4
```

This demonstrates that the greedy strategy can produce a valid but non-optimal assignment.

## 6. Conclusion

The C1 greedy algorithm is correct with respect to its defined greedy strategy:

* It processes jobs by urgency.
* It chooses the cheapest available porter.
* It prevents a porter from being assigned more than once.
* It calculates the total cost of the assignments.

However, the algorithm is not guaranteed to find the globally optimal assignment.

The counterexample provides concrete evidence of this limitation, while the brute-force algorithm provides the optimal solution for the same small input.

Therefore, the evidence supports the following conclusion:

**The greedy algorithm correctly implements the greedy assignment strategy, but the strategy itself is not guaranteed to produce a globally optimal assignment.**
