# Assignment Brief — Condensed

**Course:** DCIT 204/308 Data Structures and Algorithms I & II
**Mode:** Team project, 12–16 students, individual accountability
**Duration:** 4 weeks
**Language:** Java; DB may be SQLite, MySQL or PostgreSQL

Full original document: `docs/original/Joint_DSA_Project_Brief.docx`

## Non-negotiables

1. No Java built-ins for assessed logic (`HashMap`, `TreeMap`, `PriorityQueue`, `Stack`, `ArrayDeque`, etc. banned in core structures/algorithms; allowed for I/O, JDBC, printing, plotting, test scaffolding)
2. Every structure: normal, boundary, and invalid-input tests minimum
3. Nothing hardcoded — examiner may live-edit a priority rule, add a location, resize a hash table
4. At least 3 algorithm parameters derived from member index numbers (we use 5 — see charter §2.7)
5. Trace tables, proof sketches, counterexamples generated from the real dataset, not written after the fact
6. Database is part of the running system, not just storage
7. Weekly dev-log entries; sign-in at every meeting

## Minimum dataset

| Entity | Min records |
|---|---|
| Locations | 50 |
| Roads/edges | 100 |
| Service requests | 300 |
| Resources | 30 |
| Algorithm runs | 30 |

## Required structures (13) and algorithms (14–15)

See `docs/team-charter.md` §4 for the full slot-by-slot mapping to owners.

## Rubric (100 marks)

| Area | Marks |
|---|---|
| Local problem design and data quality | 10 |
| Data-structure implementation | 20 |
| Algorithm implementation | 20 |
| Database integration | 10 |
| Correctness and testing | 15 |
| Efficiency analysis and graphs | 15 |
| Report quality and oral defense | 10 |

## Submission items

Source code (repo export), database scripts (`schema.sql` + seed CSVs), technical report (PDF + DOCX), performance results (CSV + graphs), demo video (5–8 min), oral defense (every member explains one structure + one algorithm).

## Milestones

M1 dataset plan → M2 structure library → M3 search/sort → M4 graph/optimisation → M5 database integration → M6 efficiency study → M7 final defense.

Mapped to our 4-week internal timeline in `docs/team-charter.md` §8.