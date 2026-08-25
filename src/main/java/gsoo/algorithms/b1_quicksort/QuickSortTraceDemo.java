package gsoo.algorithms.b1_quicksort;

public class QuickSortTraceDemo {
    public static void main(String[] args) {
        testCase(new int[]{10, 7, 8, 9, 1, 5}, "Normal case");
        testCase(new int[]{}, "Empty array");
        testCase(new int[]{5}, "Single element");
        testCase(new int[]{1, 2, 3, 4, 5}, "Already sorted");
        testCase(new int[]{5, 4, 3, 2, 1}, "Reverse sorted");
        testCase(new int[]{3, 3, 3, 3}, "All duplicates");
        testCase(new int[]{-5, 3, -1, 0, 8, -2}, "Negative numbers");
    }

    private static void testCase(int[] arr, String label) {
        QuickSort.quickSort(arr, 0, arr.length - 1);
        System.out.print(label + ": ");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}
