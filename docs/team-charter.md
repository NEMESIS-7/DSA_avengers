# Ghana Smart Service Operations Optimizer
## Team Charter — Roles, Ownership & Working Agreement

**Course:** DCIT 204/308 — Joint DSA Semester Project
**Team size:** 15
**Duration:** 4 weeks
**Status:** Draft v1 — to be confirmed at Week 1 kickoff

---

## 0. How to use this document

Find your slot ID in Section 4. That row is your contract for the next four weeks. Read Section 5 (your spine role, if you have one), Section 6 (Definition of Done — this is what "finished" means, and it is not "the code runs"), and Section 9 (what you must be able to defend orally).

Everything in this document exists because of one line in the assignment brief:

> *Every member must defend one data structure and one algorithm during the oral demonstration.*

Nobody can hide. Nobody can be carried. The allocation below is built so that each person owns exactly one structure and one algorithm, and so that the two things you own explain each other.

---

## 1. Non-negotiables (straight from the brief)

These are not team preferences. Breaking any of them costs marks directly.

1. **No Java built-ins for assessed logic.** `HashMap`, `TreeMap`, `PriorityQueue`, `Stack`, `ArrayDeque` and equivalents are banned in core structures and algorithms. They are allowed only for file I/O, JDBC, printing, plotting export and test scaffolding.
2. **Every structure needs three tests minimum:** normal case, boundary case, invalid input case. 15 people × 3 = 45 tests, clearing the 40-test floor.
3. **Nothing is hardcoded.** The examiner may ask us to change a priority rule, add a location, or resize the hash table *live*. Every tunable value comes from `Config` and is reachable from the console menu.
4. **At least three algorithm parameters derive from member index numbers.** Documented formula, no magic numbers.
5. **Evidence is part of the work, not a write-up phase.** Trace tables, proof sketches, counterexamples and benchmark CSVs are generated from our real dataset, not typed into Word at the end.
6. **The database is part of the running system.** CSV seeds the DB; the program reads from and writes to the DB at runtime, including `algorithm_runs` and `audit_events`.
7. **Weekly dev-log entry per person. Sign-in at every meeting.** Both are explicitly required and both are trivially easy to lose.

---

## 2. Domain model — District hospital operations

### 2.1 The system in one sentence

*A district hospital receives service requests — patient transfers, specimen runs, drug deliveries, and referrals arriving from CHPS compounds and health centres — and must decide what to handle next, who handles it, and by what route, under limited porters, ambulances, shift time and degraded roads.*

Every structure and algorithm in Section 4 exists to answer some part of that sentence. If a member cannot say which part of it their slot serves, they are not ready to defend it.

### 2.2 Graph topology — two layers, one join

The dataset schema is a **routing** schema: weighted edges, and requests with both a source and a destination. A hospital is a single point on a map, so we model two layers:

| Layer | Nodes | Edges | Requests it serves |
|---|---|---|---|
| **External** — district health network | communities, CHPS compounds, health centres, polyclinic, regional referral hospital | real roads, seasonally degraded | referrals in and out, ambulance dispatch |
| **Internal** — hospital topology | wards, theatres, labs, pharmacy, stores, OPD | corridors, stairwells, lift links | porter jobs, specimen runs, drug and supply delivery |

**The two layers join at exactly one node: the ambulance bay** (`type = 'gateway'`). A referral route ends there; an internal job begins there.

> **Strict rule:** do not add any other edge between layers. More than one join makes cross-layer shortest paths physically meaningless and the examiner will find it.

The same `Graph`, Dijkstra, Prim and Kruskal code runs on both layers. The layer is a property of the data, not of the code.

### 2.3 Locations — budget to exactly 50

| Layer | Group | Count |
|---|---|---|
| External | Communities / catchment settlements | 8 |
| External | CHPS compounds | 6 |
| External | Health centres | 3 |
| External | Polyclinic | 1 |
| External | Regional referral hospital | 1 |
| **Join** | **District hospital gate / ambulance bay** | **1** |
| Internal | OPD reception, records, triage bay | 3 |
| Internal | Consulting rooms A–D | 4 |
| Internal | Emergency / A&E | 1 |
| Internal | Wards — male, female, paediatric, maternity, lying-in | 5 |
| Internal | Theatre 1, Theatre 2, recovery | 3 |
| Internal | Laboratory, imaging/X-ray, ultrasound | 3 |
| Internal | Pharmacy, drug store | 2 |
| Internal | Blood bank | 1 |
| Internal | Central sterile supply (CSSD) | 1 |
| Internal | Mortuary | 1 |
| Internal | Kitchen, laundry | 2 |
| Internal | Biomedical workshop, general stores | 2 |
| Internal | Admin block, staff room | 2 |
| | **Total** | **50** |

`area` distinguishes sub-district or hospital block. `type` distinguishes the node class and carries the layer.

### 2.4 Roads / edges — 100

Roughly 35 external road segments, 65 internal corridor segments.

**Units are uniform across both layers** — `distance` in metres, `travelTime` in seconds. Mixing kilometres and metres across layers will silently corrupt every Dijkstra result.

`roadConditionWeight` is a **traversal cost multiplier**, defined once and documented:

| External | Weight | Internal | Weight |
|---|---|---|---|
| Paved, good condition | 1.0 | Flat corridor | 1.0 |
| Laterite / untarred | 1.4 | Congested at OPD peak | 1.3 |
| Seasonal, flood-prone | 1.8 | Stair-only link | 1.6 |
| Impassable in heavy rains | 2.5 | Lift-dependent | 2.0 |
| | | Infection-control restricted | 3.0 |

**Effective edge cost = `travelTime` × `roadConditionWeight`.** This is what Dijkstra minimises. Write it in `Config`, state it in the report, and never compute it two different ways in two different classes.

### 2.5 Service requests — 300

Every request has a real source and a real destination.

| Category | Source | Destination | Typical urgency |
|---|---|---|---|
| `REFERRAL_IN` | CHPS / health centre | A&E or OPD | 3–5 |
| `REFERRAL_OUT` | district hospital | regional hospital | 4–5 |
| `PATIENT_TRANSFER` | ward | theatre / imaging | 2–5 |
| `SPECIMEN` | ward / OPD | laboratory | 2–4 |
| `DRUG_DELIVERY` | pharmacy | ward | 1–4 |
| `BLOOD` | blood bank | theatre / ward | 4–5 |
| `STERILE_SUPPLY` | CSSD | theatre | 2–3 |
| `EQUIPMENT` | biomedical workshop | ward | 1–3 |
| `MEALS` | kitchen | wards | 1 |
| `LINEN` | laundry | wards | 1 |
| `MAINTENANCE` | requester location | target location | 1–3 |
| `MORTUARY_TRANSFER` | ward | mortuary | 2 |

**Urgency:** 1 (routine) to 5 (life-threatening).
**Status:** `PENDING` → `ASSIGNED` → `IN_TRANSIT` → `COMPLETED`, plus `CANCELLED`.
**Deadline:** `timeSubmitted` + category SLA, tightened by urgency.

Suggested mix: ~120 internal patient/equipment transport, ~60 specimen and drug, ~50 referrals, ~70 support services.

### 2.6 Resources — 30

| Type | Count | Home location | Capacity |
|---|---|---|---|
| Porter / orderly | 12 | OPD, wards | 1 job |
| Wheelchair | 4 | OPD, A&E | 1 patient |
| Trolley | 4 | wards | 1 patient |
| Ambulance | 3 | ambulance bay | 1–2 patients |
| Lab runner | 3 | laboratory | *n* specimens |
| Biomedical technician | 2 | workshop | 1 job |
| Pharmacy dispatch rider | 2 | pharmacy | *n* packages |

`availabilityStatus`: `AVAILABLE`, `ASSIGNED`, `OFF_SHIFT`, `MAINTENANCE`.

### 2.7 Index-number-derived parameters

The brief requires at least three. We define five, all in `Config`, all reproducible from a documented formula. Let **S** = the sum of the last three digits of every member's index number.

| Parameter | Formula | Used by |
|---|---|---|
| `urgencyWeight` | `1 + (S mod 5)` | C2 priority score, C1 greedy |
| `hashTableSize` | smallest prime ≥ `1000 + (S mod 500)` | B4 hash table, load-factor experiment |
| `routePenalty` | `1.0 + ((S mod 20) / 10)` | extra multiplier on flood-prone edges, C2 Dijkstra |
| `randomSeed` | `S` | dataset generation, experiment shuffles |
| `shiftBudgetMinutes` | `240 + (S mod 120)` | C5 knapsack capacity |

Deriving the knapsack capacity from **S** makes our DP result unreproducible by any other team — which is precisely what the anti-plagiarism controls are testing for.

**Dispatch priority score** (single definition, `Config`-driven):
`priority = urgencyWeight × urgency + slackFactor ÷ max(1, minutesUntilDeadline)`

### 2.8 What each slot owns, in hospital terms

| ID | Structure — its job here | Algorithm — the decision it makes |
|---|---|---|
| A1 | Dynamic array — loaded request table, resized on import | Binary search — lookup by `requestId` on the sorted array |
| A2 | Linked list — per-request event timeline | Linear search — category/status scan with no index; the baseline |
| A3 | Stack — `audit_events`, undo a wrong dispatch | DFS — departments still reachable when a corridor is closed |
| A4 | Queue + circular queue — OPD walk-in line; wrapping porter roster | BFS — fewest-corridor route; reachability within *k* hops |
| A5 | Deque — triage: critical to front, routine to rear | Insertion sort — requests arrive near-sorted by time |
| B1 | BST — index on deadline; inorder gives the worklist | Quicksort — by composite priority; worst case on sorted arrivals |
| B2 | AVL — the same index, balanced | Merge sort — urgency then arrival; **stability = fairness** |
| B3 | B-tree — page index over the growing runs/request tables | Brute force — all porter-to-job assignments for small *n* |
| B4 | Hash table — patient and department lookup | Selection sort — the deliberate worst performer |
| B5 | Map — category→cost, urgency→weight, range queries | Efficiency analysis, primitive op counts, asymptotic summary |
| C1 | Set — visited nodes, departments under lockdown | Greedy — nearest available porter to highest urgency |
| C2 | Heap — the dispatch queue itself | Dijkstra — fastest route by `travelTime × roadConditionWeight` |
| C3 | Disjoint set — catchments still connected when a road floods | Kruskal — minimum-cost sample and cold-chain loop |
| C4 | Graph, adjacency list — sparse district network | Prim — same MST, compared against Kruskal |
| C5 | Graph, adjacency matrix — dense internal corridor layout | DP knapsack — which requests one shift can serve |

Pairings are deliberate: B1's degenerate BST on sorted inserts and B1's quicksort worst case on sorted input are the same lesson from two directions; B2's merge-sort stability is a fairness argument about patients, not a textbook footnote.

### 2.9 Explicitly out of scope

Diagnoses, prescriptions, clinical notes, NHIS or claims processing, billing, payroll, and any UI beyond the required console menu. These add schema weight and earn zero marks. The brief states plainly that this is not a UI-design project and that the marks sit in structures, algorithms, evidence and analysis.

### 2.10 Data provenance

Place names are real and locally sourced. **Every patient and request identifier is synthetic** (`RQ-00001` style) — no real patient records enter this system in any form. The provenance note required by the brief must say exactly this.

---

## 3. Team structure

Three pods. Each pod has a lead. The Architect talks to three pod leads, not to fifteen people.

| Pod | Domain | Lead | Members |
|---|---|---|---|
| **A** | Linear structures & elementary search | A2 | A1–A5 |
| **B** | Trees, hashing & indexing | B2 | B1–B5 |
| **C** | Graph, priority & optimisation | C2 | C1–C5 |

**Pod lead duties:** run the pod standup, review pod code before it goes to the Architect, know the status of all five members daily, escalate blockers within 24 hours. A pod lead who reports "all good" for a member who has pushed nothing that week has failed at the job.

---

## 4. Master allocation

Each row: one owner, one data structure, one algorithm, one spine role (where applicable), one backup.

### Pod A — Linear Structures & Elementary Search

| ID | Owner | Data structure | Algorithm | Spine role | Backs up |
|---|---|---|---|---|---|
| **A1** | Fenuku Reynolds Elikem | Dynamic array / array-backed list | Binary search | Architect & Integrator, Pod A Lead | A2 |
| **A2** | Mensah Constance Awura Adwoa | Linked list + iterator | Linear search |  | A3 |
| **A3** | Kena-Bonti Gabriel | Stack | Depth-first search | Data & Database Owner | A4 |
| **A4** | Amoaku Emefa Deotormenyo | Queue + circular queue | Breadth-first search | Test & Harness Owner | A5 |
| **A5** | Dzebu Daniel Selorm Yaw | Deque | Insertion sort | — | A1 |

### Pod B — Trees, Hashing & Indexing

| ID | Owner | Data structure | Algorithm | Spine role | Backs up |
|---|---|---|---|---|---|
| **B1** | Tenkorang Roland Yeboah | Binary search tree | Quicksort | — | B2 |
| **B2** | Ansah Joel Eugene | Balanced tree (AVL) | Merge sort | Pod B Lead | B3 |
| **B3** | Gyankomah Samuel Offei-Dei | B-tree (page / index simulation) | Brute-force exhaustive search | — | B4 |
| **B4** | Ohemeng Yvonne Darkoa | Hash table | Selection sort | — | B5 |
| **B5** | Oti-Antwi Fremponmaa | Map (BST-backed) | Efficiency analysis & asymptotic study | Efficiency Lab Owner | B1 |

### Pod C — Graph, Priority & Optimisation

| ID | Owner | Data structure | Algorithm | Spine role | Backs up |
|---|---|---|---|---|---|
| **C1** | Antwi Prince Walker | Set (hash-backed) | Greedy assignment + counterexample | Report Editor & Evidence Registrar | C2 |
| **C2** | Nutsua Bless Yesutor | Heap / priority queue | Dijkstra | Pod C Lead | C3 |
| **C3** | Ayim Obed Boateng | Disjoint set | Kruskal | — | C4 |
| **C4** | Botwe Michael | Graph — adjacency list | Prim | — | C5 |
| **C5** | Jarawura Williams Koyiri | Graph — adjacency matrix | Dynamic programming (knapsack) | — | C1 |

### Why the pairings are what they are

Stack↔DFS, Queue↔BFS, Disjoint set↔Kruskal, Heap↔Dijkstra, Adjacency matrix↔DP: in each case the structure *is* the mechanism the algorithm runs on. You will prepare one explanation, not two, and it will be a stronger explanation. Heavy structures (B-tree, hash table) carry light algorithms; light structures (Set, Map) carry heavier analytical work. Spine roles get structures that are quick to implement, because their overhead is real work.

### Backup rule

Your backup is the person on the row below you in your pod; the last person in each pod backs up the first. **Your backup starts reading your code in Week 1, not Week 3.** If you go silent for 48 hours past a checkpoint with nothing pushed, your backup implements your slot — and **you still defend it orally.** That is not a threat invented by the team; the brief requires you personally to explain it to the examiner.

### Cross-pod dependency — the one that can stall five people at once

A3 (DFS), A4 (BFS), C2 (Dijkstra), C3 (Kruskal) and C4 (Prim) all consume the graph API owned by C4/C5, and Dijkstra additionally consumes C2's heap. **The graph and heap interfaces are frozen by end of Week 1 or five people stall simultaneously in Week 3.** These two interfaces are the Architect's first priority.

---

## 5. Spine roles

Layered on top of a structure and an algorithm. These are not exemptions from implementing.

**A1 — Architect & Integrator.** Owns `Config`, the index-number parameter derivation formula, all 15 interface contracts, and merge authority. Builds the dynamic array first and publicly, as the reference implementation everyone copies for style, test shape and trace hooks. Arbitrates dependency conflicts. *Does not implement other people's structures* — if A1 writes more than two of the fifteen, the project has collapsed into one person's capacity and fourteen people are defending code they didn't write.

**A3 — Data & Database Owner.** Builds the dataset (50 locations, 100 roads, 300 service requests, 30 resources) with real local place names and realistic constraints. Writes the provenance/evidence note explaining how the data was constructed from local knowledge without exposing personal data. Owns `schema.sql`, the JDBC loader, `audit_events`, and the data dictionary.

**A4 — Test & Harness Owner.** Builds the unit-test template every member fills in, tracks the running test count against the 40 floor, and builds the benchmark runner (timing, memory, input-size sweep, writes to `algorithm_runs`, exports CSV). **This harness ships in Week 1.** It feeds 15 rubric marks and it makes our evidence regenerate itself whenever a structure changes.

**B5 — Efficiency Lab Owner.** Owns all six required experiments end to end: design, execution discipline (three runs each, one machine, machine spec recorded once), plots, and the written interpretation of any gap between theoretical complexity and observed runtime. Also owns primitive-operation counting for two algorithms and the Big-O / Big-Θ / Big-Ω summary. Runs on A4's harness; A4 builds the infrastructure, B5 designs and runs the science.

**C1 — Report Editor & Evidence Registrar.** Creates the 12-section report skeleton in Week 1 and holds final editorial say. Maintains the evidence ledger (Section 7) and chases missing artifacts. Owns the dev log, the sign-in sheets, the AI-assistance prompt record, and the demo video. **Has authority to block a merge for missing evidence.**

---

## 6. Definition of Done

A slot is done when **all five** of these exist. Pod leads check before review; the Architect will not merge an incomplete slot.

- [ ] **Implementation** against the frozen interface, no banned built-ins
- [ ] **Three tests** — normal, boundary, invalid input — passing
- [ ] **Trace output** generated by the code from our real dataset, not hand-written
- [ ] **Report section drafted** into the shared skeleton (diagram + explanation)
- [ ] **Dev-log entry** for the week: what was done, what broke, what was decided

"It works on my machine" is not one of the five.

---

## 7. Evidence ledger — named owners

The brief sets hard minimums. Unowned evidence is uncreated evidence.

**Trace tables (6 required)**

| Trace | Owner |
|---|---|
| Binary search | A1 |
| Insertion sort | A5 |
| Merge sort *and* quicksort | B2, B1 |
| Dijkstra (distance + predecessor table) | C2 |
| Kruskal / Prim (MST edge list + total cost) | C3, C4 |
| Dynamic programming (table + reconstruction) | C5 |

**Proof sketches (3 required)**

| Proof | Owner |
|---|---|
| Loop invariant — binary search | A1 |
| Induction / recursion — merge sort | B2 |
| Correctness idea — greedy assignment | C1 |

**Counterexamples (2 required)**

| Counterexample | Owner |
|---|---|
| Greedy failure case | C1 |
| Binary search on unsorted input (precondition violation) | A1 |

**Performance experiments (6 required)** — coordinated by B5

| Experiment | Contributing owners |
|---|---|
| Linear vs binary search | A1, A2 |
| Selection / insertion / merge / quicksort | A5, B1, B2, B4 |
| Hash table load factor vs collisions | B4 |
| BST vs balanced tree (height & search time) | B1, B2 |
| Heap priority dispatch | C2 |
| BFS / DFS / Dijkstra / MST runtime | C3, C4, C5 |

**Shared deliverables**

- Pseudocode and input/output definitions for the five major operations — A1 with C1
- Structure diagrams — each owner, collated by C1
- Console menu exposing all live-modifiable parameters — A1 with A4

---

## 8. Timeline

**Internal code freeze is end of Week 3.** Week 4 is report, video and rehearsal. Treat Week 3 as the deadline in every conversation.

### Week 1 — Foundations (M1, M2)
Context and organisation locked. Dataset built and loaded. `schema.sql` complete. `Config` with index-derived parameters. **All 15 interfaces frozen — graph and heap first.** Test template, benchmark harness skeleton, report skeleton. A1's dynamic array shipped as the reference implementation.

*Week 1 is the whole project. If interfaces are not frozen by Sunday, nothing downstream is parallel and we lose the four-week structure.*

### Week 2 — Structure library (M2, M3)
All 15 structures merged, each with three tests and trace hooks. Search and sort algorithms implemented. First integration run: load from DB into every structure.

### Week 3 — Algorithms, integration, experiments (M4, M5, M6)
Graph algorithms, greedy, DP. Full DB read/write path. All six experiments run three times each on one machine. CSVs exported, graphs plotted, interpretations written. **Code freeze Sunday.**

### Week 4 — Report and defense (M7)
Report assembled and edited. Screenshots and run logs. Demo video (5–8 min: DB load, core algorithms, tests, graphs). **Mock oral panel — every member defends their structure and algorithm to the rest of the team before the real one.**

---

## 9. Individual accountability

At the oral defense you will be asked to explain, and possibly modify live, **your** structure and **your** algorithm. Prepare:

1. What it does, in plain language, in under a minute
2. Its complexity — best, average, worst — and why
3. Your trace table, walked through step by step
4. Your three test cases and why those three
5. One thing it handles badly, and what you'd use instead

If your backup wrote your code, you still stand up and answer these five. Plan accordingly.

---

## 10. Ceremonies

| When | What | Who |
|---|---|---|
| Daily | Pod standup — 15 min, async in the group chat is fine | Pod members → Pod lead |
| Daily | Pod leads report status to Architect | A2, B2, C2 → A1 |
| Twice weekly | Merge window — pod lead reviews, Architect merges | All |
| Weekly | All-hands, **with sign-in sheet** | All |
| Weekly | Dev-log entry: done / broken / decided | Every member individually |

---

## 11. Open items for kickoff

1. **Named facility** — the context is settled (district hospital operations, Section 2). Still open: *which* district hospital, and who sources the real place names for the 8 communities, 6 CHPS compounds and 3 health centres in its catchment. Owner: A3.
2. **Names against slot IDs** — A1 is the Architect and is already assigned. The remaining 14 slots need names before any code is written.
3. **Confirm S** — the five parameters and their formulas are defined in Section 2.7, but **S** cannot be computed until all 15 index numbers are collected. Owner: A1. Blocks the dataset generator.
4. **Benchmark machine** — one machine for all experiments, specification recorded in Week 1. Owner: B5.
5. **Layer join integrity** — one person owns a test asserting that exactly one edge crosses between the external and internal layers. Owner: A3, with C4.