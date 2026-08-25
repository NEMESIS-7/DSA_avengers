package gsoo.algorithms.a5_insertion_sort;

import java.util.Comparator;

public class InsertionSort {

    /**
     * Sorts an array in-place using Insertion Sort and logs the trace of each pass.
     */
    public static <T> void sortWithTrace(T[] array, Comparator<T> comparator) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null.");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null.");
        }

        System.out.println("=== INSERTION SORT INITIAL STATE ===");
        printArray(array);

        int n = array.length;
        for (int i = 1; i < n; i++) {
            T key = array[i];
            int j = i - 1;

            // Shift elements greater than key to one position ahead
            while (j >= 0 && comparator.compare(array[j], key) > 0) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;

            // Trace table output required by Definition of Done
            System.out.printf("Pass %d (Inserted key at index %d): ", i, (j + 1));
            printArray(array);
        }
        System.out.println("=== SORT COMPLETE ===\n");
    }

    private static <T> void printArray(T[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) sb.append(", ");
        }
        sb.append("]");
        System.out.println(sb.toString());
    }
}