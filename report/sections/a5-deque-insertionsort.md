# Slot A5: Double-Ended Queue (Deque) and Insertion Sort

**Author:** Slot A5 (Pod A)  
**Modules:** `gsoo.structures.a5_deque.CustomDeque`, `gsoo.algorithms.a5_insertion_sort.InsertionSort`

---

## 1. Custom Data Structure: Deque

### 1.1 Structural Design & Invariants
The `CustomDeque<T>` is implemented using a custom doubly linked list. Each node maintains generic data `T`, a forward pointer `next`, and a backward pointer `prev`. 

- **Pointers:** Internal references `head` and `tail` track the front and back of the queue.
- **Invariants:** 
  - An empty deque maintains `head == null && tail == null && size == 0`.
  - For any single-element deque, `head == tail`, `head.prev == null`, and `head.next == null`.
  - Null element insertion is explicitly rejected with `IllegalArgumentException`.

### 1.2 Complexity Analysis
| Operation | Time Complexity | Auxiliary Space | Operational Rationale |
|---|---|---|---|
| `addFirst(item)` | $O(1)$ | $O(1)$ | Direct pointer update at `head`. |
| `addLast(item)` | $O(1)$ | $O(1)$ | Direct pointer update at `tail`. |
| `removeFirst()` | $O(1)$ | $O(1)$ | Unlinks current `head` and advances pointer. |
| `removeLast()` | $O(1)$ | $O(1)$ | Unlinks current `tail` and retreats pointer. |
| `peekFirst()` / `peekLast()` | $O(1)$ | $O(1)$ | Immediate reference access. |

---

## 2. Algorithm Implementation: Insertion Sort

### 2.1 Algorithmic Mechanics
`InsertionSort` maintains a sorted prefix on the left subarray. For each index $i \in [1, n-1]$, the element $A[i]$ is stored as `key` and compared backward against elements in the sorted prefix $A[0 \dots i-1]$. Elements strictly greater than `key` are shifted one position to the right until the correct insertion index is found.

- **Stability:** Stable ($A[j] > \text{key}$ uses strict inequality, preserving original order of duplicate keys).
- **Adaptability:** Runs in linear $O(n)$ time when input is already sorted.

### 2.2 Complexity Profile
- **Best Case:** $O(n)$ comparisons, $0$ shifts (Array already sorted).
- **Average Case:** $O(n^2)$ comparisons and shifts ($\approx n(n-1)/4$).
- **Worst Case:** $O(n^2)$ comparisons and shifts (Array sorted in reverse order).
- **Space Complexity:** $O(1)$ auxiliary space (in-place mutation).

---

## 3. Empirical Trace Table (Evidence Ledger §8)

The table below illustrates `InsertionSort` prioritizing hospital service requests by urgency descending ($U5 \rightarrow U1$), generated from the hospital dataset:

| Pass ($i$) | Key Element Inserted | Comparisons | Shifts | Array State After Pass |
|---|---|---|---|---|
| 0 (Init) | N/A | 0 | 0 | `[[REQ-101\|U1\|MEALS], [REQ-102\|U5\|REFERRAL_IN], [REQ-103\|U3\|SPECIMEN], [REQ-104\|U5\|BLOOD], [REQ-105\|U2\|LINEN]]` |
| 1 | `[REQ-102\|U5\|REFERRAL_IN]` | 1 | 1 | `[[REQ-102\|U5\|REFERRAL_IN], [REQ-101\|U1\|MEALS], [REQ-103\|U3\|SPECIMEN], [REQ-104\|U5\|BLOOD], [REQ-105\|U2\|LINEN]]` |
| 2 | `[REQ-103\|U3\|SPECIMEN]` | 2 | 1 | `[[REQ-102\|U5\|REFERRAL_IN], [REQ-103\|U3\|SPECIMEN], [REQ-101\|U1\|MEALS], [REQ-104\|U5\|BLOOD], [REQ-105\|U2\|LINEN]]` |
| 3 | `[REQ-104\|U5\|BLOOD]` | 1 | 0 | `[[REQ-102\|U5\|REFERRAL_IN], [REQ-104\|U5\|BLOOD], [REQ-103\|U3\|SPECIMEN], [REQ-101\|U1\|MEALS], [REQ-105\|U2\|LINEN]]` |
| 4 | `[REQ-105\|U2\|LINEN]` | 3 | 1 | `[[REQ-102\|U5\|REFERRAL_IN], [REQ-104\|U5\|BLOOD], [REQ-103\|U3\|SPECIMEN], [REQ-105\|U2\|LINEN], [REQ-101\|U1\|MEALS]]` |

---

## 4. Verification & Testing Strategy

1. **Normal Cases:** Verified dual-ended deque queueing invariants and multi-element sorting order across standard operational batches.
2. **Boundary Cases:** Asserted behavior on empty collections ($n=0$), single-element transitions ($n=1$), and already-sorted/reverse-sorted arrays.
3. **Invalid Inputs:** Verified that null pointers throw `IllegalArgumentException` and empty deque extractions throw `NoSuchElementException`.

---

## 5. Domain Limitations & Failure Modes
Insertion Sort scales poorly ($O(n^2)$ quadratic cost) when applied to large, unsorted or reverse-ordered batches of hospital requests (such as a backlog of 300+ incoming referrals). In full system dispatching, divide-and-conquer algorithms like Merge Sort (Slot B2) or Heap priority queues (Slot C2) are preferred for large datasets.