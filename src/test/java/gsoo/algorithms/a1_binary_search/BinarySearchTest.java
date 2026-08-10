package gsoo.algorithms.a1_binary_search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinarySearchTest {

    BinarySearch<Integer> search;

    @Test
    void  testNormalSearchRun(){
        Integer[] arr = {1,2,3,4,5,6,7,8,9,10};
        int foundIndex = search.binarySearch(0, arr.length - 1, arr,7);
        assertEquals(7,foundIndex);
    }

}
