package gsoo.algorithms.b2_merge_sort;

import org.junit.jupiter.api.Test;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest {

    // Normal case
    @Test
    void sortsUnorderedArrayAscending() {
        Integer[] input = {38, 27, 43, 3, 9, 82, 10};
        Integer[] expected = {3, 9, 10, 27, 38, 43, 82};

        MergeSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    void sortsArrayWithDuplicates() {
        Integer[] input = {5, 2, 5, 1, 2, 5};
        Integer[] expected = {1, 2, 2, 5, 5, 5};

        MergeSort.sort(input);

        assertArrayEquals(expected, input, "Duplicates must be preserved and correctly ordered");
    }

    //  Boundary case
    @Test
    void handlesEmptyAndSingleElementArrays() {
        Integer[] empty = {};
        assertDoesNotThrow(() -> MergeSort.sort(empty));
        assertEquals(0, empty.length);

        Integer[] single = {7};
        MergeSort.sort(single);
        assertArrayEquals(new Integer[]{7}, single);
    }

    @Test
    void handlesAlreadySortedAndReverseSortedArrays() {
        Integer[] alreadySorted = {1, 2, 3, 4, 5};
        MergeSort.sort(alreadySorted);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, alreadySorted);

        Integer[] reverseSorted = {5, 4, 3, 2, 1};
        MergeSort.sort(reverseSorted);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, reverseSorted);
    }

    // Invalid input case
    @Test
    void handlesNullArrayWithoutThrowing() {
        Integer[] nullArray = null;
        assertDoesNotThrow(() -> MergeSort.sort(nullArray), "sort() must guard against null, not throw NPE");
    }
}