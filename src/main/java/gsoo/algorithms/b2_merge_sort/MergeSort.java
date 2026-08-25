package gsoo.algorithms.b2_merge_sort;


public class MergeSort {




    public static <T extends Comparable<T>> void sort(T[] array) {
        if (array == null || array.length < 2) {
            return; // 0 or 1 element — already sorted, matches "boundary case" test
        }
        T[] buffer = array.clone(); // scratch space, allocated once
        mergeSort(array, buffer, 0, array.length - 1);
    }

    protected static <T extends Comparable<T>> void mergeSort(T[] array, T[] buffer, int left, int right) {
        if (left >= right) {
            return; // 0 or 1 element in this range — base case
        }

        int mid = left + (right - left) / 2; // avoids overflow vs (left+right)/2

        mergeSort(array, buffer, left, mid);
        mergeSort(array, buffer, mid + 1, right);
        merge(array, buffer, left, mid, right);
    }

    private static <T extends Comparable<T>> void merge(T[] array, T[] buffer, int left, int mid, int right) {
        // Copy the range into the buffer so we can read from it while writing back into array
        for (int i = left; i <= right; i++) {
            buffer[i] = array[i];
        }

        int i = left;      // pointer into left half (in buffer)
        int j = mid + 1;   // pointer into right half (in buffer)
        int k = left;      // pointer into array (write position)

        while (i <= mid && j <= right) {
            if (buffer[i].compareTo(buffer[j]) <= 0) {
                array[k++] = buffer[i++];
            } else {
                array[k++] = buffer[j++];
            }
        }

        // Drain whichever half still has leftover elements
        while (i <= mid) {
            array[k++] = buffer[i++];
        }
        while (j <= right) {
            array[k++] = buffer[j++];
        }
    }
}