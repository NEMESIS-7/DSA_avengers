package gsoo.algorithms.b4_selection_sort;

import gsoo.db.ServiceRequest;


// This is intentionally the "worst performer" in our project - it's O(n^2) o matter what, even if the array is already sorted. We use it as a
// baseline to compare against the faster sorts other people are doing (quicksort, merge sort).
public class SelectionSort {

    // Sorts the array in place, by requestId, smallest to largest. lso returns how many comparisons were made, since that's useful
   
    public static int sort(ServiceRequest[] array) {
        int comparisons = 0;

        for (int i = 0; i < array.length - 1; i++) {
            int minIndex = i;

            // find the request with the smallest requestId in the unsorted part
            for (int j = i + 1; j < array.length; j++) {
                comparisons++;
                if (array[j].requestId.compareTo(array[minIndex].requestId) < 0) {
                    minIndex = j;
                }
            }

            // swap the smallest found into position i
            if (minIndex != i) {
                ServiceRequest temp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = temp;
            }
        }

        return comparisons;
    }
}