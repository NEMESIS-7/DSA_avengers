# C1 — Greedy Assignment Counterexample

## Purpose

This counterexample demonstrates that the greedy assignment algorithm does not
always produce the globally optimal porter-to-job assignment.

The greedy algorithm processes jobs according to urgency and assigns each job
to the cheapest currently available porter. Once a porter is assigned, that
porter cannot be reused.

## Input

### Cost matrix

| Porter | Job 0 | Job 1 |
|---|---:|---:|
| Porter 0 | 1 | 2 |
| Porter 1 | 2 | 100 |

### Job urgency

| Job | Urgency |
|---|---:|
| Job 0 | 10 |
| Job 1 | 5 |

## Greedy Result

Job 0 has the highest urgency, so the greedy algorithm processes it first.

For Job 0:

- Porter 0 costs 1
- Porter 1 costs 2
- Greedy chooses Porter 0

The remaining assignment is therefore:

- Porter 0 → Job 0 = cost 1
- Porter 1 → Job 1 = cost 100

### Greedy total cost

**1 + 100 = 101**

## Brute Force Result

Brute force checks every possible assignment.

One possible assignment is:

- Porter 0 → Job 0 = cost 1
- Porter 1 → Job 1 = cost 100

Total = 101

The other assignment is:

- Porter 0 → Job 1 = cost 2
- Porter 1 → Job 0 = cost 2

Total = **4**

Therefore, the globally optimal assignment is:

- Porter 0 → Job 1
- Porter 1 → Job 0

### Optimal total cost

**2 + 2 = 4**

## Comparison

| Algorithm | Total Cost |
|---|---:|
| Greedy | 101 |
| Brute Force | 4 |

The greedy algorithm produces a cost of **101**, while brute force finds the
optimal cost of **4**.

Therefore:

**Greedy is not guaranteed to produce the globally optimal assignment.**

## Conclusion

This counterexample provides evidence for the limitation of the C1 greedy
algorithm. The greedy choice of the cheapest porter for the highest-urgency
job can consume a porter that is needed for a much more expensive assignment.

The result confirms that greedy makes a locally optimal choice, but that
choice can lead to a globally poor solution.
## Correctness Idea — Greedy Assignment

### Greedy Strategy

The greedy assignment algorithm processes jobs from highest urgency to lowest urgency.

For each selected job, the algorithm chooses the cheapest porter who is still available. Once a porter is assigned to a job, that porter is no longer available for subsequent jobs.

The algorithm therefore makes the best local choice at each step:

> For the current highest-urgency job, assign the cheapest available porter.

### Correctness Idea

The algorithm correctly follows the defined greedy strategy because:

1. Every job is processed according to its urgency.
2. For each job, only available porters are considered.
3. The porter with the lowest assignment cost is selected.
4. An assigned porter is marked unavailable, preventing the same porter from being assigned twice.
5. The cost of every successful assignment is added to the total cost.
6. The final result records which job was assigned to each porter.

Thus, the algorithm correctly implements the intended greedy rule.

### Important Limitation

Correct implementation of the greedy strategy does **not** mean that the result is always globally optimal.

A locally cheapest choice for an early, high-urgency job can use a porter who would have been much more valuable for another job. This can cause the final total cost to be higher than the optimal solution.

The brute-force algorithm checks every possible porter-to-job permutation and can therefore provide the optimal assignment for small inputs.

The C1 counterexample demonstrates this limitation:

* Greedy total cost: **101**
* Brute-force optimal total cost: **4**

Therefore:

**Greedy is correct with respect to its strategy, but it is not guaranteed to produce the globally minimum-cost assignment.**

### Complexity

For `p` porters and `j` jobs, the greedy algorithm uses nested searches to:

* find the next highest-urgency job;
* find the cheapest available porter.

For the square assignment case where `p = j = n`, the implementation runs in approximately **O(n²)** time.

The brute-force solution, in contrast, examines all `n!` possible assignments and therefore has factorial growth.

This difference explains why the greedy approach is much faster, while brute force provides an optimal solution for small inputs.

### Evidence

The accompanying test demonstrates both normal greedy behaviour and the failure case where greedy produces a worse solution than brute force.

The counterexample test produced:

```text
Greedy total cost: 101
Brute force total cost: 4
Counterexample confirmed: Greedy is worse than Brute Force.
```

This evidence supports the claim that the greedy algorithm is not guaranteed to find the globally optimal assignment.
