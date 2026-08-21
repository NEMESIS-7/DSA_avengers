# C3 — Kruskal MST Real-Dataset Trace

**Owner:** Ayim Obed Boateng  
**Slot:** C3  
**Structure:** Disjoint Set / Union-Find  
**Algorithm:** Kruskal's Minimum Spanning Tree

## Dataset Summary

The trace was generated directly from the Ghana Smart Service Operations Optimizer real seed dataset.

- Locations loaded: 50
- Road records: 100
- Unique usable graph edges: 98
- Parallel road records collapsed: 2
- MST edges selected: 49
- Total MST effective cost: 16750.40

Effective edge cost:

`travelTimeSecs × roadConditionWeight`

The graph contains 50 vertices. A spanning tree for 50 connected vertices must contain:

`V - 1 = 50 - 1 = 49 edges`

Kruskal selected exactly 49 edges.

## Generated Trace

```text
=== C3 REAL HOSPITAL DATASET ===
Locations loaded: 50
Road records loaded: 100

=== C3 KRUSKAL REAL-DATASET TRACE ===
Step | From | To | Effective Cost
----------------------------------------
1 | INT-13 | INT-17 | 28.60
2 | INT-22 | INT-14 | 32.00
3 | INT-01 | INT-03 | 34.00
4 | INT-26 | INT-11 | 34.00
5 | INT-01 | INT-04 | 36.40
6 | INT-26 | INT-13 | 41.00
7 | INT-09 | INT-17 | 46.80
8 | INT-27 | INT-17 | 48.10
9 | INT-20 | INT-21 | 49.00
10 | INT-28 | INT-20 | 52.00
11 | INT-14 | INT-15 | 55.00
12 | INT-20 | INT-08 | 57.20
13 | INT-17 | INT-18 | 60.00
14 | INT-25 | INT-10 | 61.00
15 | INT-29 | INT-30 | 62.00
16 | INT-14 | INT-23 | 64.00
17 | INT-25 | INT-26 | 70.00
18 | INT-06 | INT-08 | 72.80
19 | INT-04 | INT-05 | 74.00
20 | INT-21 | INT-13 | 78.00
21 | INT-22 | INT-12 | 84.50
22 | GATE-01 | INT-08 | 89.00
23 | INT-02 | INT-30 | 93.00
24 | INT-03 | INT-08 | 100.00
25 | INT-01 | INT-02 | 103.00
26 | INT-12 | INT-17 | 109.20
27 | INT-18 | INT-19 | 128.00
28 | INT-24 | INT-09 | 141.00
29 | INT-15 | INT-16 | 146.00
30 | INT-06 | INT-07 | 147.00
31 | EXT-C05 | EXT-HC03 | 332.00
32 | EXT-H02 | GATE-01 | 439.00
33 | EXT-C01 | EXT-HC02 | 455.00
34 | EXT-C07 | EXT-HC01 | 457.80
35 | EXT-C06 | EXT-H02 | 523.00
36 | EXT-C04 | EXT-HC01 | 544.60
37 | EXT-C02 | EXT-H01 | 550.00
38 | EXT-C03 | EXT-HC02 | 565.60
39 | EXT-C03 | EXT-H02 | 618.80
40 | EXT-HC01 | EXT-HC02 | 624.40
41 | EXT-H05 | EXT-HC04 | 635.60
42 | EXT-C02 | EXT-HC02 | 693.00
43 | EXT-C07 | EXT-H03 | 708.40
44 | EXT-H04 | EXT-HC03 | 812.00
45 | EXT-H05 | EXT-H06 | 846.00
46 | EXT-C08 | EXT-HC03 | 858.00
47 | EXT-H04 | EXT-H06 | 1020.60
48 | EXT-HC02 | EXT-HC03 | 1170.00
49 | GATE-01 | EXT-R01 | 2700.00
----------------------------------------
MST edges selected: 49
Total MST effective cost: 16750.40


```

## Result

Kruskal successfully produced a minimum spanning tree from the real project dataset.

The graph contained 50 locations and Kruskal selected 49 edges, satisfying the spanning-tree property `V - 1`.

The final total effective MST cost was:

**16750.40**

The test completed successfully with exit code 0.