public class Main {
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
        quickSort(arr, 0, arr.length - 1);
        System.out.print(label + ": ");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}