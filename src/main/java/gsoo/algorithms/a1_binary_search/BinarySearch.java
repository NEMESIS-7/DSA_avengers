package gsoo.algorithms.a1_binary_search;


public class BinarySearch<T extends Comparable<T>> {

    public int binarySearch(int left, int right, T[] arr, T target){
        int mid = left + (right - left) / 2;

        if(arr[mid].compareTo(target) == 0){
            return mid;
        }
        if(arr[mid].compareTo(target) > 0) {
            return binarySearch(mid + 1, right, arr, target);
        }
        if(arr[mid].compareTo(target) < 0){
            return binarySearch(left, mid - 1, arr,target);
        }

        return 0;
    }
}
