package gsoo.algorithms.a1_binary_search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinarySearchTest {

    @Test
    void testNormalSearchRun(){
        Integer[] arr = {1,2,3,4,5,6,7,8,9,10};
        BinarySearch<Integer> search = new BinarySearch<>();
        int foundIndex = search.binarySearch(0, arr.length - 1, arr, 7);
        assertEquals(6, foundIndex); // value 7 sits at index 6 in this 0-based array
    }

    @Test
    void boundaryCase_firstAndLastElement(){
        Integer[] arr = {1,2,3,4,5,6,7,8,9,10};
        BinarySearch<Integer> search = new BinarySearch<>();
        assertEquals(0, search.binarySearch(0, arr.length - 1, arr, 1));
        assertEquals(9, search.binarySearch(0, arr.length - 1, arr, 10));
    }

    @Test
    void boundaryCase_singleElementArray(){
        Integer[] arr = {5};
        BinarySearch<Integer> search = new BinarySearch<>();
        assertEquals(0, search.binarySearch(0, arr.length - 1, arr, 5));
        assertEquals(-1, search.binarySearch(0, arr.length - 1, arr, 9));
    }

    @Test
    void invalidCase_targetNotPresentReturnsNegativeOne(){
        Integer[] arr = {1,2,3,4,5,6,7,8,9,10};
        BinarySearch<Integer> search = new BinarySearch<>();
        assertEquals(-1, search.binarySearch(0, arr.length - 1, arr, 42));
    }

    @Test
    void invalidCase_emptyRangeReturnsNegativeOne(){
        Integer[] arr = {};
        BinarySearch<Integer> search = new BinarySearch<>();
        assertEquals(-1, search.binarySearch(0, arr.length - 1, arr, 1));
    }
}
