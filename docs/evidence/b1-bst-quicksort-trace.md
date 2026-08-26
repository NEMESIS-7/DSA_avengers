# B1 - BST and Quicksort Trace Tables

**Owner:** Tenkorang Roland Yeboah  
**Slot:** B1  
**Data structure:** Binary Search Tree  
**Algorithm:** Quicksort

## Dataset

This trace uses pending service requests from the committed project seed dataset:

`sql/seed/service_requests_seed.sql`

The live database was not available while this evidence was written, so the committed seed file is used as the reproducible project data source. These rows are real project seed records, not invented sample values.

Selected pending records:

| Input Order | Request ID | Category | Urgency | Deadline | BST Deadline Key |
|---:|---|---|---:|---|---:|
| 1 | `REQ-0005` | `REFERRAL_IN` | 5 | `2026-07-29T20:19:00` | 7292019 |
| 2 | `REQ-0008` | `REFERRAL_IN` | 5 | `2026-07-15T07:56:00` | 7150756 |
| 3 | `REQ-0010` | `REFERRAL_IN` | 5 | `2026-07-04T03:34:00` | 7040334 |
| 4 | `REQ-0012` | `REFERRAL_IN` | 4 | `2026-07-22T12:33:00` | 7221233 |
| 5 | `REQ-0014` | `REFERRAL_IN` | 5 | `2026-07-14T22:57:00` | 7142257 |
| 6 | `REQ-0042` | `REFERRAL_OUT` | 4 | `2026-07-06T13:59:00` | 7061359 |
| 7 | `REQ-0043` | `REFERRAL_OUT` | 5 | `2026-07-23T03:43:00` | 7230343 |
| 8 | `REQ-0045` | `REFERRAL_OUT` | 5 | `2026-07-13T08:11:00` | 7130811 |
| 9 | `REQ-0049` | `REFERRAL_OUT` | 4 | `2026-07-13T05:12:00` | 7130512 |
| 10 | `REQ-0070` | `PATIENT_TRANSFER` | 5 | `2026-07-06T02:53:00` | 7060253 |

The current BST implementation stores `int` values, so the deadline is converted into the integer key `MMDDHHMM`. Since these selected seed records are all from July 2026, that key preserves the same chronological order for this trace.

## Table 1 - BST Insert Trace

| Step | Request ID | Inserted Key | Comparison Path | Result |
|---:|---|---:|---|---|
| 1 | `REQ-0005` | 7292019 | Tree empty | Insert as root |
| 2 | `REQ-0008` | 7150756 | 7150756 < 7292019 | Insert left of 7292019 |
| 3 | `REQ-0010` | 7040334 | 7040334 < 7292019, 7040334 < 7150756 | Insert left of 7150756 |
| 4 | `REQ-0012` | 7221233 | 7221233 < 7292019, 7221233 > 7150756 | Insert right of 7150756 |
| 5 | `REQ-0014` | 7142257 | 7142257 < 7292019, 7142257 < 7150756, 7142257 > 7040334 | Insert right of 7040334 |
| 6 | `REQ-0042` | 7061359 | 7061359 < 7292019, 7061359 < 7150756, 7061359 > 7040334, 7061359 < 7142257 | Insert left of 7142257 |
| 7 | `REQ-0043` | 7230343 | 7230343 < 7292019, 7230343 > 7150756, 7230343 > 7221233 | Insert right of 7221233 |
| 8 | `REQ-0045` | 7130811 | 7130811 < 7292019, 7130811 < 7150756, 7130811 > 7040334, 7130811 < 7142257, 7130811 > 7061359 | Insert right of 7061359 |
| 9 | `REQ-0049` | 7130512 | 7130512 < 7292019, 7130512 < 7150756, 7130512 > 7040334, 7130512 < 7142257, 7130512 > 7061359, 7130512 < 7130811 | Insert left of 7130811 |
| 10 | `REQ-0070` | 7060253 | 7060253 < 7292019, 7060253 < 7150756, 7060253 > 7040334, 7060253 < 7142257, 7060253 < 7061359 | Insert left of 7061359 |

## Table 2 - BST In-Order Output

In-order traversal visits left subtree, node, then right subtree. That produces the pending requests ordered by deadline key:

| Visit Order | Request ID | Deadline Key | Deadline |
|---:|---|---:|---|
| 1 | `REQ-0010` | 7040334 | `2026-07-04T03:34:00` |
| 2 | `REQ-0070` | 7060253 | `2026-07-06T02:53:00` |
| 3 | `REQ-0042` | 7061359 | `2026-07-06T13:59:00` |
| 4 | `REQ-0049` | 7130512 | `2026-07-13T05:12:00` |
| 5 | `REQ-0045` | 7130811 | `2026-07-13T08:11:00` |
| 6 | `REQ-0014` | 7142257 | `2026-07-14T22:57:00` |
| 7 | `REQ-0008` | 7150756 | `2026-07-15T07:56:00` |
| 8 | `REQ-0012` | 7221233 | `2026-07-22T12:33:00` |
| 9 | `REQ-0043` | 7230343 | `2026-07-23T03:43:00` |
| 10 | `REQ-0005` | 7292019 | `2026-07-29T20:19:00` |

Result:

```text
inOrder() = [7040334, 7060253, 7061359, 7130512, 7130811, 7142257, 7150756, 7221233, 7230343, 7292019]
```

## Table 3 - Quicksort Partition Trace

The quicksort input is the urgency values from the same selected pending requests:

```text
[5, 5, 5, 4, 5, 4, 5, 5, 4, 5]
```

The implementation uses the last element as the pivot and partitions with `arr[j] <= pivot`.

| Step | Active Range | Pivot | Array After Partition | Pivot Final Index |
|---:|---|---:|---|---:|
| 1 | 0..9 | 5 | `[5, 5, 5, 4, 5, 4, 5, 5, 4, 5]` | 9 |
| 2 | 0..8 | 4 | `[4, 4, 4, 5, 5, 5, 5, 5, 5, 5]` | 2 |
| 3 | 0..1 | 4 | `[4, 4, 4, 5, 5, 5, 5, 5, 5, 5]` | 1 |
| 4 | 3..8 | 5 | `[4, 4, 4, 5, 5, 5, 5, 5, 5, 5]` | 8 |
| 5 | 3..7 | 5 | `[4, 4, 4, 5, 5, 5, 5, 5, 5, 5]` | 7 |
| 6 | 3..6 | 5 | `[4, 4, 4, 5, 5, 5, 5, 5, 5, 5]` | 6 |
| 7 | 3..5 | 5 | `[4, 4, 4, 5, 5, 5, 5, 5, 5, 5]` | 5 |
| 8 | 3..4 | 5 | `[4, 4, 4, 5, 5, 5, 5, 5, 5, 5]` | 4 |

Final sorted urgency array:

```text
[4, 4, 4, 5, 5, 5, 5, 5, 5, 5]
```

## Result

- BST insertion placed each deadline key according to binary-search-tree ordering.
- BST in-order traversal returned the selected pending requests from earliest deadline to latest deadline.
- Quicksort sorted the selected pending request urgency values from lowest urgency to highest urgency.
- The trace uses committed project seed data because live database access was not available during documentation.

