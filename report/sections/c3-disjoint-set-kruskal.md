# C3 — Disjoint Set & Kruskal

**Owner:** Ayim Obed Boateng
**Slot:** C3
**Structure:** Disjoint Set / Union-Find
**Algorithm:** Kruskal's Minimum Spanning Tree

## What it does

The C3 Disjoint Set implementation maintains groups of connected hospital-network locations. Each location is identified using a String ID such as `AMBULANCE-BAY`, `OPD`, or a generated location ID such as `EXT-C01`.

Because Java collection classes such as `HashMap` are prohibited for assessed core logic, the implementation is array-backed. It maintains an `ids[]` array containing the location identifiers, a `parent[]` array representing the parent forest, and a `rank[]` array used to keep the trees shallow.

Each new location begins as its own singleton set using `makeSet()`. The `find()` operation returns the representative or root of a set and applies path compression so that later searches follow shorter parent paths. The `union()` operation combines two different sets using union by rank, attaching the lower-rank tree below the higher-rank tree. `connected()` determines whether two locations already belong to the same set, while `setCount()` records how many separate components remain.

This structure is paired directly with Kruskal's Minimum Spanning Tree algorithm. Kruskal receives the shared hospital `Graph`, obtains all vertices and edges, and orders the edges by effective traversal cost:

`effectiveCost = travelTimeSecs × roadConditionWeight`

It then considers edges from lowest cost upward. Before accepting an edge, Kruskal asks the Disjoint Set whether its two endpoints are already connected. If they are connected, accepting the edge would create a cycle and the edge is skipped. Otherwise, the edge is selected and the two endpoint sets are united.

For a connected graph containing `V` vertices, the resulting minimum spanning tree contains `V - 1` edges.

## Complexity

The underlying Union-Find parent-forest operations use path compression and union by rank. With direct element indexing, these operations have amortised complexity close to `O(α(V))`, where `α` is the inverse Ackermann function.

The current project implementation, however, receives String location IDs and uses a custom linear array search to translate each String ID into its internal array index because built-in hash maps are prohibited. Therefore, a public `find()`, `connected()`, or `union()` operation also includes an `O(V)` ID lookup. The parent-tree component remains highly efficient, but the current full public operation is dominated by this linear lookup.

Kruskal currently uses a custom insertion sort for its edge ordering. Consequently, sorting `E` edges has worst-case complexity `O(E²)`. The standard textbook Kruskal algorithm can achieve `O(E log E)` when an `O(E log E)` sorting algorithm and near-constant Disjoint Set access are available.

This difference is important when interpreting the project's empirical results. A possible future optimisation would be to integrate a permitted custom hash-table index for location IDs and an `O(E log E)` custom sorting implementation.

## Trace walkthrough

A demonstration trace was generated directly from the C3 test code using four hospital-network locations:

* `AMBULANCE-BAY`
* `OPD`
* `LAB`
* `PHARMACY`

Kruskal selected the following edges in increasing effective-cost order:

| Step | From          | To       | Effective Cost |
| ---- | ------------- | -------- | -------------: |
| 1    | LAB           | PHARMACY |            5.0 |
| 2    | OPD           | LAB      |            8.0 |
| 3    | AMBULANCE-BAY | OPD      |           10.0 |

The resulting total MST cost was:

`5.0 + 8.0 + 10.0 = 23.0`

Three edges were selected for four vertices, satisfying the MST requirement of `V - 1 = 3`.

This trace currently demonstrates that the trace mechanism works correctly. The final evidence version will be rerun against the team's full hospital dataset once the database/road seed data is available.

## Tests and why

The C3 slot currently includes tests for both the data structure and algorithm.

### Disjoint Set tests

The normal test creates several locations, unions their sets, and verifies that locations expected to be connected share the same representative.

The boundary test creates a Disjoint Set containing only one element and verifies that the element is its own root and that the number of sets is one.

Invalid-input testing verifies that searching for an unknown ID throws `IllegalArgumentException`. An additional test confirms that attempting to create the same ID twice is rejected.

A trace test verifies that `findWithTrace()` generates human-readable traversal evidence while returning the same representative as the normal `find()` operation.

### Kruskal tests

The normal test constructs a weighted graph and checks that Kruskal selects the correct number of MST edges and produces the expected minimum total cost.

The boundary test uses a graph containing only one node and verifies that the resulting MST contains zero edges with total cost zero.

The invalid-input test confirms that passing a null graph to Kruskal throws `IllegalArgumentException`.

An additional trace test prints the selected MST edges and effective costs for evidence and demonstration purposes.

All current tests successfully completed through the Maven test lifecycle in IntelliJ.

## Correctness idea

Kruskal maintains the invariant that the selected edges form a forest with no cycles.

Before an edge `(u, v)` is added, the Disjoint Set checks whether `u` and `v` already have the same representative. If they do, there is already a path between them, so adding the edge would create a cycle and it is rejected.

If their representatives differ, the edge joins two previously separate components and therefore cannot create a cycle. The components are then combined using `union()`.

By examining the edges from lowest to highest cost and accepting only edges that connect separate components, Kruskal progressively builds a minimum-cost spanning tree.

## One thing it handles badly

The current implementation deliberately avoids `HashMap` and other banned built-in structures, but this means mapping a String location ID to its array index requires linear search.

This creates additional `O(V)` lookup work around the otherwise very efficient Union-Find operations. The custom insertion sort inside Kruskal also scales poorly for large edge sets compared with an `O(E log E)` sorting algorithm.

These choices preserve the project's custom-implementation constraint and correctness, but they provide useful opportunities for the efficiency experiments and theory-versus-observed-performance discussion.
