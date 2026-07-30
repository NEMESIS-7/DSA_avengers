# Submission Checklist and Cover Sheet

Original form: `docs/original/Joint_DSA_Project_Checklist_Cover_Sheet.docx`. Fill this in as we go; transcribe to the original docx before final submission.

## Cover sheet

| Field | Value |
|---|---|
| Team name | TBD |
| Selected Ghana context | District hospital operations |
| Organisation/problem modelled | External referral network + internal hospital corridor network, joined at the ambulance bay — see `team-charter.md` §2 |
| Database used | PostgreSQL |
| Programming language/version | Java (version TBD) |
| Total records in dataset | 50 locations, 100 roads, 300 requests, 30 resources, 30+ algorithm runs |
| Repository or submitted ZIP name | TBD |

## Checklist

| Requirement | Done? | Evidence location |
|---|---|---|
| Local dataset with data dictionary | ☐ | |
| Database schema and seed data | ☐ | `sql/schema.sql`, `sql/seed/` |
| Custom data structures implemented | ☐ | `src/main/java/gsoo/structures/` |
| Searching and sorting algorithms | ☐ | `src/main/java/gsoo/algorithms/` |
| Graph algorithms implemented | ☐ | `src/main/java/gsoo/algorithms/c2_dijkstra`, `c3_kruskal`, `c4_prim`, `a3_dfs`, `a4_bfs` |
| Greedy and DP algorithms | ☐ | `c1_greedy`, `c5_dp_knapsack` |
| Correctness tests and trace tables | ☐ | `src/test/java/gsoo/`, evidence ledger in `team-charter.md` §7 |
| Performance CSV and graphs | ☐ | `experiments/csv/`, `experiments/plots/` |
| Technical report | ☐ | `report/` |
| Demo video / oral defense prepared | ☐ | |