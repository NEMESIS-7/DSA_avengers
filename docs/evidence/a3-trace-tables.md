# A3 Trace Tables — Stack & Undo-Dispatch

`Stack.java` and `StackTest.java` live at `src/main/java/gsoo/structures/a3_stack/` (package `gsoo.structures.a3_stack`), corrected from an earlier package mismatch during Week 2 integration.

## Table 1 — Array-level trace: `push()` and resize (boundary case)

Traces `Stack.java`'s internal array exactly as `StackTest.boundaryCase_pushingPastDefaultCapacityTriggersResize()` exercises it: pushing 9 integers into a stack that starts at `DEFAULT_CAPACITY = 8`.

| Step | Operation | `size` before | `data.length` | Resize? | `data[]` contents after | `size` after |
|------|-----------|---------------|---------------|---------|--------------------------|--------------|
| 1 | `push(1)` | 0 | 8 | No (0 ≠ 8) | `[1]` | 1 |
| 2 | `push(2)` | 1 | 8 | No | `[1,2]` | 2 |
| 3 | `push(3)` | 2 | 8 | No | `[1,2,3]` | 3 |
| 4 | `push(4)` | 3 | 8 | No | `[1,2,3,4]` | 4 |
| 5 | `push(5)` | 4 | 8 | No | `[1,2,3,4,5]` | 5 |
| 6 | `push(6)` | 5 | 8 | No | `[1,2,3,4,5,6]` | 6 |
| 7 | `push(7)` | 6 | 8 | No | `[1,2,3,4,5,6,7]` | 7 |
| 8 | `push(8)` | 7 | 8 | No | `[1,2,3,4,5,6,7,8]` | 8 |
| 9 | `push(9)` | 8 | 8 | **Yes** — `resize(16)` copies all 8 existing elements into a new 16-slot array, *then* `9` is written to index 8 | `[1,2,3,4,5,6,7,8,9]` (capacity now 16, 7 unused trailing slots) | 9 |

**Result:** `size() = 9`, `peek() = 9` — matches the test's expected values. The resize is triggered by the *last* push, not a push before it.

---

## Table 2 — Functional trace: undo-dispatch on REQ-0001 (real seed data)

Traces `AuditTrailDemo.main()` end to end. REQ-0001's real seeded status is `COMPLETED` — the demo overwrites this regardless, which is a known caveat of running the demo against real seed data. REQ-0001 is a `REFERRAL_IN` request and also carries a real `patient_ref` (`PAT-0011`) in the current dataset — not touched by this demo, since the undo-dispatch flow only reads/writes `status`, but worth knowing it's there if asked.

| Step | Line(s) in code | Action | `auditStack` (bottom→top) | New `audit_events` row | `service_requests.status` |
|------|------------------|--------|----------------------------|--------------------------|------------------------------|
| 1 | `getStatus(conn, "REQ-0001")` | Read current status | — (empty) | — | `COMPLETED` (real seed value, unchanged so far) |
| 2 | `new Stack<AuditEvent>()` | Create empty stack | `[]` | — | `COMPLETED` |
| 3 | `doAction(created)` — `push`, insert, update | Log + apply CREATED | `[CREATED]` | `(REQ-0001, CREATED, null → PENDING)` | `PENDING` |
| 4 | `doAction(assigned)` — `push`, insert, update | Log + apply ASSIGNED | `[CREATED, ASSIGNED]` | `(REQ-0001, ASSIGNED, PENDING → ASSIGNED)` | `ASSIGNED` |
| 5 | `auditStack.size()` | Confirm state | `[CREATED, ASSIGNED]` — size 2 | — | `ASSIGNED` |
| 6 | `undoLast()` → `stack.pop()` | Pop most recent event | `[CREATED]` — size 1 | — | `ASSIGNED` (not yet reverted) |
| 7 | `undoLast()` → build `undoEvent`, insert, update | Log UNDONE, revert status | `[CREATED]` | `(REQ-0001, UNDONE, ASSIGNED → PENDING)` | `PENDING` |
| 8 | `auditStack.size()` | Confirm final state | `[CREATED]` — size 1 | — | `PENDING` |

**Result:** 3 permanent rows added to `audit_events` (CREATED, ASSIGNED, UNDONE — nothing deleted, consistent with append-only audit history). Stack ends with 1 item (`CREATED`), since only the `ASSIGNED` action was undone. Final `service_requests.status = PENDING` — note this is **not** REQ-0001's original `COMPLETED` state, since the demo doesn't know or restore the real prior status, only the status from *within its own simulated sequence*.

---

## Table 3 — Mechanics-level trace: DFS with a simulated corridor closure

Traces `DFS.reachableFrom()` exactly as `DFSTest.boundaryCase_simulatingClosureOfOnlyPath_isolatesNode()` exercises it: a 3-node chain A–B–C, run twice — once normally, once simulating B→C as closed.

**Run 1 — normal, nothing closed:**

| Step | Stack before | Action | `visited` after | `visitOrder` after |
|------|--------------|--------|------------------|---------------------|
| 1 | `[]` | `push(A)` | — | — |
| 2 | `[A]` | `pop()` → A, mark visited | `{A}` | `[A]` |
| 3 | `[]` | Check A's neighbors: edge A–B, other endpoint = B, not visited → `push(B)` | `{A}` | `[A]` |
| 4 | `[B]` | `pop()` → B, mark visited | `{A,B}` | `[A,B]` |
| 5 | `[]` | Check B's neighbors: edge A–B → other = A, already visited, skip. Edge B–C → other = C, not visited → `push(C)` | `{A,B}` | `[A,B]` |
| 6 | `[C]` | `pop()` → C, mark visited | `{A,B,C}` | `[A,B,C]` |
| 7 | `[]` | Check C's neighbors: edge B–C → other = B, already visited, skip. Stack empty, done. | `{A,B,C}` | `[A,B,C]` |

**Result:** `visitedCount = 3` — all reachable.

**Run 2 — simulating B→C closed (`reachableFrom(graph, "A", "B", "C")`):**

Steps 1–4 identical to Run 1. At step 5, checking B's neighbor edge B–C: `isSimulatedClosed()` matches (`fromId="B"`, `toId="C"`), so this edge is skipped entirely — C is never pushed.

| Step | Stack before | Action | `visited` after | `visitOrder` after |
|------|--------------|--------|------------------|---------------------|
| 5 | `[]` | Edge A–B → other=A, visited, skip. Edge B–C → **simulated closed, skip.** Nothing pushed. | `{A,B}` | `[A,B]` |
| 6 | `[]` | Stack empty, done. | `{A,B}` | `[A,B]` |

**Result:** `visitedCount = 2` — C is correctly isolated, since B–C was its only connection.

---

## Table 4 — Real-dataset case: a genuinely isolatable location

Rather than hand-tracing all 50 real locations (too large to verify reliably by hand), this proves one specific, checkable claim: **`EXT-R01` (Greater Accra Regional Hospital) has exactly one road in the entire real dataset** — `R-019`, connecting it to `GATE-01` (Ambulance Bay). Confirmed directly against `roads_template.csv`.

| Condition | Is `EXT-R01` reachable from the rest of the network? | Why |
|---|---|---|
| Normal — `R-019` open | **Yes** | Its one connection, `R-019`, links it into `GATE-01`, which connects into the rest of the network |
| `R-019` closed (real `is_closed` flag, or simulated via `reachableFrom(graph, start, "GATE-01", "EXT-R01")`) | **No — provably isolated** | A degree-1 node's reachability depends entirely on its one edge. With that edge gone, there is no other path in or out, regardless of how large or connected the rest of the graph is |

**Why this is provable without a full trace:** reachability of a degree-1 node isn't a property that needs a step-by-step walk to confirm — it's a direct consequence of the node's connectivity. This is also a real-world meaningful case, not a toy example: if the referral road to the Greater Accra Regional Hospital closes, that hospital becomes completely cut off from every other request in the system, which is exactly the kind of scenario this slot exists to answer.

