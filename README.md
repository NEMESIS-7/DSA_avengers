# Ghana Smart Service Operations Optimizer

DCIT 204/308 Joint DSA Semester Project — University of Ghana, Department of Computer Science.

15-person team. 4 weeks. Local context: **district hospital operations** — an external referral network (communities, CHPS compounds, health centres) joined to an internal hospital corridor network at a single gateway node (the ambulance bay).

Project hub (live tracking): Notion — Slot Board, Evidence Tracker, Milestone Timeline.

---

## 1. What we're building

A system that answers, for a district hospital and its catchment:

- Which service request should be handled next — FIFO, urgency, or priority rules?
- What's the fastest route between two locations under weighted, degraded roads?
- Which locations are still reachable from a dispatch point when a corridor or road is closed?
- Which subset of requests can a shift serve under a time/staff budget?
- How do alternative structures and algorithms perform as the dataset grows?
- How does the system persist and reload records for later analysis?

It is **not** a UI project. Marks sit in correct custom data-structure implementation, algorithmic reasoning, empirical analysis, testing, and database integration — see the rubric in §7.

## 2. Domain model

### 2.1 Two layers, one join

| Layer | Nodes | Edges | Requests served |
|---|---|---|---|
| **External** — district health network | communities, CHPS compounds, health centres, polyclinic, regional referral hospital | real roads, seasonally degraded | referrals in/out, ambulance dispatch |
| **Internal** — hospital topology | wards, theatres, labs, pharmacy, stores, OPD | corridors, stairwells, lifts | porter jobs, specimen runs, drug/supply delivery |

The two layers join at **exactly one node**: the ambulance bay (`type = 'gateway'`). No other cross-layer edge is permitted — more than one join makes shortest paths meaningless. A3 and C4 jointly own a test asserting this invariant holds.

### 2.2 Locations (50) and roads (100)

Budgeted: 20 external (8 communities, 6 CHPS, 3 health centres, 1 polyclinic, 1 regional hospital, 1 gateway), 29 internal (OPD, wards, theatres, labs, pharmacy, blood bank, CSSD, mortuary, support services, admin). ~35 external road segments, ~65 internal corridor segments.

Units are uniform across both layers: `distance` in metres, `travelTime` in seconds. `roadConditionWeight` is a traversal-cost multiplier (1.0 for good/flat, up to 2.5–3.0 for flood-prone or infection-control-restricted). **Effective edge cost = `travelTime × roadConditionWeight`** — defined once, in `Config`, never recomputed differently elsewhere.

### 2.3 Service requests (300) and resources (30)

Categories: `REFERRAL_IN`, `REFERRAL_OUT`, `PATIENT_TRANSFER`, `SPECIMEN`, `DRUG_DELIVERY`, `BLOOD`, `STERILE_SUPPLY`, `EQUIPMENT`, `MEALS`, `LINEN`, `MAINTENANCE`, `MORTUARY_TRANSFER`. Urgency 1–5, status `PENDING → ASSIGNED → IN_TRANSIT → COMPLETED` (or `CANCELLED`).

Resources: porters, wheelchairs, trolleys, ambulances, lab runners, biomedical technicians, pharmacy riders.

### 2.4 Index-number-derived parameters

Let **S** = sum of the last three digits of every member's index number (collected at kickoff — blocks the dataset generator).

| Parameter | Formula | Used by |
|---|---|---|
| `urgencyWeight` | `1 + (S mod 5)` | C2 priority score, C1 greedy |
| `hashTableSize` | smallest prime ≥ `1000 + (S mod 500)` | B4, load-factor experiment |
| `routePenalty` | `1.0 + ((S mod 20) / 10)` | multiplier on flood-prone edges, C2 Dijkstra |
| `randomSeed` | `S` | dataset generation, experiment shuffles |
| `shiftBudgetMinutes` | `240 + (S mod 120)` | C5 knapsack capacity |

### 2.5 Explicitly out of scope

Diagnoses, prescriptions, clinical notes, NHIS/claims, billing, payroll, any UI beyond the required console menu. All patient/request identifiers are **synthetic** — no real records in any form.

---

## 3. Team structure

Three pods, five people each, one lead per pod. The Architect talks to three pod leads, not fifteen people.

| Pod | Domain | Lead |
|---|---|---|
| **A** | Linear structures & elementary search | A2 |
| **B** | Trees, hashing & indexing | B2 |
| **C** | Graph, priority & optimisation | C2 |

## 4. Slot allocation

One person, one data structure, one algorithm, paired so each explains the other.

### Pod A — Reynolds (Architect) + Constance

| ID | Owner | Structure | Algorithm | Spine role |
|---|---|---|---|---|
| A1 | Fenuku Reynolds Elikem | Dynamic array | Binary search | **Architect & Integrator** |
| A2 | Mensah Constance Awura Adwoa | Linked list + iterator | Linear search | Pod A Lead |
| A3 | Kena-Bonti Gabriel | Stack | DFS | Data & Database Owner |
| A4 | Amoaku Emefa Deotormenyo | Queue + circular queue | BFS | Test & Harness Owner |
| A5 | Dzebu Daniel Selorm Yaw | Deque | Insertion sort | — |

### Pod B — Joel + Yvonne

| ID | Owner | Structure | Algorithm | Spine role |
|---|---|---|---|---|
| B1 | Tenkorang Roland Yeboah | BST | Quicksort | — |
| B2 | Ansah Joel Eugene | AVL (balanced tree) | Merge sort | Pod B Lead |
| B3 | Gyankomah Samuel Offei-Dei | B-tree | Brute force | — |
| B4 | Ohemeng Yvonne Darkoa | Hash table | Selection sort | — |
| B5 | Oti-Antwi Fremponmaa | Map | Efficiency analysis | Efficiency Lab Owner |

### Pod C — Bless + Williams

| ID | Owner | Structure | Algorithm | Spine role |
|---|---|---|---|---|
| C1 | Antwi Prince Walker | Set | Greedy + counterexample | Report Editor & Evidence Registrar |
| C2 | Nutsua Bless Yesutor | Heap / priority queue | Dijkstra | Pod C Lead |
| C3 | Ayim Obed Boateng | Disjoint set | Kruskal | — |
| C4 | Botwe Michael | Graph, adjacency list | Prim | — |
| C5 | Jarawura Williams Koyiri | Graph, adjacency matrix | DP knapsack | — |

**Backup rule:** your backup is the next row in your pod (last wraps to first). Backups start reading your code in Week 1. Go dark 48 hours past a checkpoint and your backup implements your slot — you still defend it orally, which the brief requires personally of you regardless of who wrote it.

**Cross-pod dependency:** DFS, BFS, Dijkstra, Kruskal, Prim all consume the graph API (C4/C5); Dijkstra also consumes the heap (C2). **These two interfaces freeze end of Week 1** or five people stall in Week 3.

## 5. Spine roles

- **A1 — Architect & Integrator.** Owns `Config`, the parameter derivation formula, all 15 interface contracts, merge authority. Builds the reference dynamic array first, publicly. Does not implement other slots — more than two and the project has collapsed into one person's capacity.
- **A3 — Data & Database Owner.** Dataset construction, provenance note, `schema.sql`, JDBC loader, `audit_events`.
- **A4 — Test & Harness Owner.** Test template, 40-test tracker, benchmark runner (ships Week 1).
- **B5 — Efficiency Lab Owner.** All six experiments end to end: execution discipline, plots, theory-vs-observed interpretation.
- **C1 — Report Editor & Evidence Registrar.** Report skeleton, evidence ledger, dev log, sign-in sheets, AI-prompt record, video. Can block a merge for missing evidence.

## 6. Definition of Done

A slot is done only when all five are true:

- [ ] Implementation against the frozen interface, no banned built-ins (`HashMap`, `TreeMap`, `PriorityQueue`, `Stack`, `ArrayDeque`, etc.)
- [ ] Three tests — normal, boundary, invalid input
- [ ] Trace output generated by the code from the real dataset
- [ ] Report section drafted into the shared skeleton
- [ ] Dev-log entry for the week

## 7. Assessment rubric (100 marks)

| Area | Marks |
|---|---|
| Local problem design and data quality | 10 |
| Data-structure implementation | 20 |
| Algorithm implementation | 20 |
| Database integration | 10 |
| Correctness and testing | 15 |
| Efficiency analysis and graphs | 15 |
| Report quality and oral defense | 10 |

## 8. Evidence ledger

**Trace tables (6):** binary search (A1), insertion sort (A5), merge sort + quicksort (B2, B1), Dijkstra distance/predecessor (C2), Kruskal/Prim MST (C3, C4), DP table + reconstruction (C5).

**Proof sketches (3):** loop invariant — binary search (A1); induction/recursion — merge sort (B2); correctness idea — greedy (C1).

**Counterexamples (2):** greedy failure (C1); binary search on unsorted input (A1).

**Experiments (6), coordinated by B5:** linear vs binary search; sort comparison; hash load factor; BST vs balanced tree; heap dispatch; BFS/DFS/Dijkstra/MST runtime.

## 9. Timeline — code freeze end of Week 3

| Week | Focus | Exit criteria |
|---|---|---|
| **1** | Foundations | Dataset, schema, `Config`, all 15 interfaces frozen (graph + heap first), test template, harness skeleton, report skeleton, A1's reference implementation shipped |
| **2** | Structure library | All 15 structures merged with tests + trace hooks; search/sort done; first DB integration run |
| **3** | Algorithms & experiments | Graph/greedy/DP done; full DB read/write; all 6 experiments run 3× each, CSVs + graphs + interpretation. **Freeze.** |
| **4** | Report & defense | Report assembled, video shot, mock oral panel — every member defends cold |

## 10. Individual accountability

At oral defense, every member explains their structure and algorithm: what it does, its complexity (best/average/worst), a walkthrough of their trace table, their three tests and why, and one thing it handles badly. If your backup wrote your code, you still answer these five.

## 11. Non-negotiables from the brief

1. No Java built-ins for assessed logic
2. Every structure: normal/boundary/invalid tests
3. Nothing hardcoded — examiner may live-edit a priority rule, location, or hash table size
4. At least 3 index-derived parameters (we use 5, §2.4)
5. Evidence generated from the real dataset, not written after the fact
6. Database is part of the running system
7. Weekly dev-log entries; sign-in at every meeting

## 12. Open items

1. Named facility and real catchment place names (owner: A3)
2. Names against slot IDs — everything above is unassigned until this happens
3. Confirm **S** — blocks the dataset generator (owner: A1)
4. Benchmark machine spec (owner: B5)
5. Layer-join integrity test (owners: A3, C4)

---

## Reference docs

This README is the up-to-date, comprehensive version — read this first. The files below hold supporting detail this README summarizes or supersedes:

- [`docs/team-charter.md`](docs/team-charter.md) — the original charter document. §§1–11 here are drawn from it; kept for the fuller prose version of the same allocation logic.
- [`docs/brief.md`](docs/brief.md) — condensed assignment brief; [`docs/original/`](docs/original/) has the unmodified submitted docx files.
- [`docs/checklist-cover-sheet.md`](docs/checklist-cover-sheet.md) — editable submission checklist; fill in as work completes, transcribe to the original docx before final submission.
- [`docs/starting-your-slot.md`](docs/starting-your-slot.md) — step-by-step guide for a new member starting their slot.
- [`CONTRIBUTING.md`](CONTRIBUTING.md) — branch/PR workflow and Definition-of-Done enforcement.

## Repo layout

```
docs/            brief, checklist, charter, starting-your-slot guide, original docx files
sql/             schema.sql + seed CSVs
src/main/java/gsoo/
  structures/    one folder per slot's data structure
  algorithms/    one folder per slot's algorithm
  db/            loader, DAO layer (A3)
  harness/       test template + benchmark runner (A4)
  app/           console menu, Config (A1)
src/test/java/gsoo/   mirrors structures/ and algorithms/
experiments/     csv/ and plots/
report/
```