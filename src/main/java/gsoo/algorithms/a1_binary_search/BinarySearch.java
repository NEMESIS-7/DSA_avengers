package gsoo.algorithms.a1_binary_search;


public class BinarySearch<T extends Comparable<T>> {

    public int binarySearch(int left, int right, T[] arr, T target){
        if (left > right) {
            return -1;
        }

        int mid = left + (right - left) / 2;
        int cmp = arr[mid].compareTo(target);

        if (cmp == 0) {
            return mid;
        }
        if (cmp > 0) {
            // arr[mid] > target: target must be in the left half
            return binarySearch(left, mid - 1, arr, target);
        }
        // arr[mid] < target: target must be in the right half
        return binarySearch(mid + 1, right, arr, target);
    }

    /** Same algorithm, but appends a human-readable step trace — evidence for the report/oral defense. */
    public int binarySearchWithTrace(int left, int right, T[] arr, T target, StringBuilder trace) {
        if (left > right) {
            trace.append("range empty (left=").append(left).append(" > right=").append(right)
                    .append(") -> not found\n");
            return -1;
        }

        int mid = left + (right - left) / 2;
        int cmp = arr[mid].compareTo(target);
        trace.append("left=").append(left).append(" right=").append(right)
                .append(" mid=").append(mid).append(" arr[mid]=").append(arr[mid]);

        if (cmp == 0) {
            trace.append(" -> match, return index ").append(mid).append("\n");
            return mid;
        }
        if (cmp > 0) {
            trace.append(" > target -> search left half\n");
            return binarySearchWithTrace(left, mid - 1, arr, target, trace);
        }
        trace.append(" < target -> search right half\n");
        return binarySearchWithTrace(mid + 1, right, arr, target, trace);
    }
}
