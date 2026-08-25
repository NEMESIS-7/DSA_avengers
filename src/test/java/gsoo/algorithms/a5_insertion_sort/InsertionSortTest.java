package gsoo.algorithms.a5_insertion_sort;

import org.junit.jupiter.api.Test;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class InsertionSortTest {

    private final Comparator<Integer> naturalOrder = Integer::compareTo;

    // ==========================================
    // TEST 1: NORMAL CASE
    // ==========================================
    @Test
    void testNormalCase() {
        // Standard unsorted hospital request priorities: [Urgency 4, 1, 5, 2, 3]
        Integer[] requests = {4, 1, 5, 2, 3};
        Integer[] expected = {1, 2, 3, 4, 5};

        InsertionSort.sortWithTrace(requests, naturalOrder);

        assertArrayEquals(expected, requests, "Array should be sorted in ascending order");
    }

    // ==========================================
    // TEST 2: BOUNDARY CASE
    // ==========================================
    @Test
    void testBoundaryCase() {
        // Boundary 1: Empty array
        Integer[] emptyArray = {};
        InsertionSort.sortWithTrace(emptyArray, naturalOrder);
        assertEquals(0, emptyArray.length);

        // Boundary 2: Single-element array
        Integer[] singleItem = {42};
        InsertionSort.sortWithTrace(singleItem, naturalOrder);
        assertArrayEquals(new Integer[]{42}, singleItem);

        // Boundary 3: Already sorted array (Best-case scenario: O(n))
        Integer[] alreadySorted = {10, 20, 30, 40};
        InsertionSort.sortWithTrace(alreadySorted, naturalOrder);
        assertArrayEquals(new Integer[]{10, 20, 30, 40}, alreadySorted);

        // Boundary 4: Reverse-sorted array (Worst-case scenario: O(n^2))
        Integer[] reverseSorted = {50, 40, 30, 20, 10};
        InsertionSort.sortWithTrace(reverseSorted, naturalOrder);
        assertArrayEquals(new Integer[]{10, 20, 30, 40, 50}, reverseSorted);

        // Boundary 5: Array with duplicate elements (Tests stability)
        Integer[] duplicates = {3, 1, 3, 2, 3};
        InsertionSort.sortWithTrace(duplicates, naturalOrder);
        assertArrayEquals(new Integer[]{1, 2, 3, 3, 3}, duplicates);
    }

    // ==========================================
    // TEST 3: INVALID INPUT & ERROR HANDLING
    // ==========================================
    @Test
    void testInvalidInput() {
        Integer[] validArray = {3, 1, 2};

        // Assert invalid null array
        assertThrows(IllegalArgumentException.class, () -> 
            InsertionSort.sortWithTrace(null, naturalOrder),
            "Passing a null array must throw IllegalArgumentException"
        );

        // Assert invalid null comparator
        assertThrows(IllegalArgumentException.class, () -> 
            InsertionSort.sortWithTrace(validArray, null),
            "Passing a null comparator must throw IllegalArgumentException"
        );
    }
}