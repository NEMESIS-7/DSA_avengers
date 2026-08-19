# C1 — Greedy Assignment Pseudocode and Input/Output Definition

## 1. Input

The algorithm receives:

* `cost` — a two-dimensional integer matrix where `cost[porter][job]` represents the cost of assigning a particular porter to a particular job.
* `urgency` — an integer array where `urgency[job]` represents the urgency level of each job.

The number of urgency values must equal the number of jobs.

## 2. Output

The algorithm returns:

* `assignment` — an array where `assignment[porter]` contains the job assigned to that porter. A value of `-1` means that the porter was not assigned a job.
* `totalCost` — the sum of the costs of all assignments made by the algorithm.

## 3. Pseudocode

```text
GREEDY-ASSIGNMENT(cost, urgency)

    IF cost is null OR urgency is null
        report invalid input
    END IF

    porters ← number of rows in cost

    IF porters = 0
        RETURN empty assignment and total cost 0
    END IF

    jobs ← number of columns in cost

    IF number of urgency values ≠ jobs
        report invalid input
    END IF

    FOR each porter
        IF the cost row is invalid
            report invalid input
        END IF
    END FOR

    assignment ← array of size porters
    Set every assignment value to -1

    porterAvailable ← boolean array of size porters
    Set every porterAvailable value to TRUE

    jobProcessed ← boolean array of size jobs
    Set every jobProcessed value to FALSE

    totalCost ← 0

    FOR count ← 0 TO jobs - 1

        selectedJob ← -1
        highestUrgency ← smallest possible integer

        FOR each job

            IF jobProcessed[job] = FALSE
               AND urgency[job] > highestUrgency

                highestUrgency ← urgency[job]
                selectedJob ← job

            END IF

        END FOR

        IF selectedJob = -1
            BREAK
        END IF

        jobProcessed[selectedJob] ← TRUE

        cheapestPorter ← -1
        cheapestCost ← largest possible integer

        FOR each porter

            IF porterAvailable[porter] = TRUE
               AND cost[porter][selectedJob] < cheapestCost

                cheapestCost ← cost[porter][selectedJob]
                cheapestPorter ← porter

            END IF

        END FOR

        IF cheapestPorter ≠ -1

            assignment[cheapestPorter] ← selectedJob
            porterAvailable[cheapestPorter] ← FALSE
            totalCost ← totalCost + cheapestCost

        END IF

    END FOR

    RETURN assignment and totalCost

END GREEDY-ASSIGNMENT
```

## 4. Greedy Decision Rule

For every selected job, the algorithm chooses the available porter with the smallest cost.

The decision can be summarized as:

```text
Select the highest-urgency unprocessed job
        ↓
Examine all available porters
        ↓
Choose the porter with minimum cost
        ↓
Assign the porter to the job
        ↓
Mark the porter unavailable
        ↓
Continue with the next highest-urgency job
```

## 5. Complexity

Let:

* `P` = number of porters
* `J` = number of jobs

Finding the highest-urgency unprocessed job requires `O(J)` work for each job.

Finding the cheapest available porter requires `O(P)` work for each job.

Therefore, the overall running time is:

```text
O(J × (J + P))
```

For the common square case where `P = J = n`:

```text
O(n²)
```

The algorithm uses arrays for its tracking structures, so the additional space used is:

```text
O(P + J)
```

## 6. Relationship to Brute Force

The greedy algorithm does not examine every possible porter-to-job assignment.

Instead, it makes one locally cheapest decision for each job.

The brute-force assignment algorithm examines all `n!` possible assignments and therefore guarantees the minimum total cost for small inputs.

The C1 counterexample demonstrates that the greedy solution can be worse than the brute-force optimum.
