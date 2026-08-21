# C5 — Adjacency Matrix and Dynamic-Programming Knapsack

**Owner:** Jarawura Williams Koyiri  
**Slot:** C5

## Adjacency matrix structure

The C5 adjacency matrix represents the dense internal hospital corridor
network. Nodes are stored in a manually resized array and edges are stored in
a two-dimensional `Edge[][]` matrix. If an edge from node `i` to node `j`
exists, the corresponding matrix cell contains the shared `Graph.Edge`;
otherwise it contains `null`.

The class implements the same `Graph` interface as C4's adjacency-list graph.
Consequently, DFS, BFS, Dijkstra, Kruskal and Prim can consume either
representation without changing their algorithm code. The console menu proves
this by running the shared Prim implementation against the internal
matrix-backed graph.

The implementation uses arrays only and manually doubles both the node array
and matrix when capacity is reached. It supports directed and undirected
edges, closed-road metadata, outgoing neighbours and direction-independent
physical connections.

An adjacency matrix uses `O(V²)` storage. Once numeric node positions are
known, an edge cell can be checked in `O(1)`. The public String-ID methods in
this project first perform a manual `O(V)` ID lookup because built-in maps are
prohibited. Enumerating neighbours scans a complete row and therefore takes
`O(V)`, while enumerating every edge takes `O(V²)`.

## Hospital knapsack problem

The algorithm decides which pending service requests can be planned within one
hospital shift. Each request is treated as a 0/1 item: it is either selected
once or not selected.

| Knapsack term | Hospital meaning |
|---|---|
| Item | One pending service request |
| Weight | Request planning minutes |
| Value | Urgency-derived priority value |
| Capacity | `Config.SHIFT_BUDGET_MINUTES` |
| Result | Optimal requests for the shift |

For the confirmed team index-number sum `S = 6316`, the capacity is:

`240 + (6316 mod 120) = 316 minutes`

The current schema does not contain an estimated-service-duration field. To
avoid silently inventing category times, `HospitalKnapsackPlanner` uses the
real recorded SLA window (`deadlineAt - submittedAt`) as a planning-time
proxy. Priority value is `urgency × Config.URGENCY_WEIGHT`. This mapping is
kept outside `Knapsack`, so the core DP does not need to change if a true
duration field is added later.

## Dynamic-programming method

The algorithm creates a table with `n + 1` rows and `capacity + 1` columns.
`table[i][m]` stores the maximum value achievable using the first `i` requests
and at most `m` minutes.

For each cell, the algorithm compares:

1. Excluding the current request: `table[i - 1][m]`.
2. Including it, when it fits: `value + table[i - 1][m - minutes]`.

The larger value is stored. Once the table is complete, reconstruction starts
from its bottom-right cell. If the current cell differs from the one directly
above, the current item was used. The algorithm records it and subtracts its
minutes before continuing upward.

The DP stage takes `O(nC)` time and `O(nC)` memory, where `n` is the request
count and `C` is the integer minute capacity. Best, average and worst cases are
the same because the implementation fills every table cell. Input validation
also checks duplicate request IDs with a manual nested scan, adding `O(n²)`
validation in the complete public method.

## Tests

`KnapsackTest` contains:

- A normal test showing that requests A and B produce the optimal reconstructed
  value of 7 within capacity 5.
- A boundary test for zero capacity and an empty item array.
- An invalid-input test for null input, negative capacity, invalid items and
  duplicate request IDs.
- An integration test confirming that only pending `ServiceRequest` records
  are converted and that `Config` supplies the value multiplier and capacity.

The C5 adjacency-matrix tests separately cover normal directed/undirected
behaviour, resizing beyond the initial capacity and invalid operations.

## Real-dataset result

The generated trace loaded 300 real synthetic service requests, including 38
pending candidates. With a 316-minute capacity, the optimum selected five
60-minute urgency-five requests:

`REQ-0005, REQ-0008, REQ-0010, REQ-0014, REQ-0043`

They use 300 minutes, leave 16 minutes and give total priority value 50. The
full selected-column DP table and reconstruction evidence are stored in
`docs/evidence/c5-dp-knapsack-trace.md`.

## Limitation

Knapsack is pseudo-polynomial: a very large numeric capacity creates a large
table even when there are few requests. The full-table approach is useful here
because reconstruction evidence is required, but a space-optimised one-row
version would use less memory when reconstruction is unnecessary.

The SLA-window weight is also a documented proxy, not a true measured service
duration. A production version should add an estimated-duration field to the
schema and use historical completion data to improve that estimate.
