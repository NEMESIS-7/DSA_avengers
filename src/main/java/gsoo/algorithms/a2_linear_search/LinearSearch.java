package gsoo.algorithms.a2_linear_search;

/**
 * Slot A2 (Mensah Constance Awura Adwoa) — Linear search, the deliberate
 * baseline (team-charter.md §2.8: "category/status scan with no index").
 *
 * Shaped like BinarySearch (A1) on purpose — same search(arr, target)
 * signature, same searchWithTrace evidence hook — since the required
 * "Linear vs binary search" experiment (evidence ledger, owned jointly by
 * A1 and A2) needs both algorithms run against identical input to be a fair
 * comparison. The difference that matters: BinarySearch requires a sorted
 * array and gets O(log n); this makes no such assumption and is O(n) in the
 * worst case — the whole point of the baseline.
 */
public class LinearSearch<T> {

    /**
     * Scans arr left to right for target. Returns the index of the first
     * match, or -1 if not found. No ordering precondition on arr, unlike
     * BinarySearch — that's exactly why it has to check every element.
     */
    public int search(T[] arr, T target) {
        if (arr == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        for (int i = 0; i < arr.length; i++) {
            if (matches(arr[i], target)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Same as search(), but appends a step-by-step trace to trace —
     * evidence hook, same convention as BinarySearch.binarySearchWithTrace().
     */
    public int searchWithTrace(T[] arr, T target, StringBuilder trace) {
        if (arr == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        for (int i = 0; i < arr.length; i++) {
            trace.append("check index ").append(i).append(": ").append(arr[i]);
            if (matches(arr[i], target)) {
                trace.append(" -> match, return index ").append(i).append("\n");
                return i;
            }
            trace.append(" -> no match\n");
        }
        trace.append("reached end of array (").append(arr.length).append(" checked) -> not found\n");
        return -1;
    }

    private boolean matches(T element, T target) {
        return element == null ? target == null : element.equals(target);
    }
}
