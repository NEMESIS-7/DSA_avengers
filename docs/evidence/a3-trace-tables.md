# A3 Trace Tables — Stack & Undo-Dispatch

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

**Result:** `size() = 9`, `peek() = 9` — matches the test's expected values. The resize is triggered by the *last* push, not a push before it — worth being precise about that if asked, since `size == data.length` is checked *before* the write, not after.

---

## Table 2 — Functional trace: undo-dispatch on REQ-0001 (real seed data)

Traces `AuditTrailDemo.main()` end to end. REQ-0001's real seeded status is `COMPLETED` — the demo overwrites this regardless, which is the caveat noted above.

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
