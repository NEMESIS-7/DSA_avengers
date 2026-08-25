package gsoo.algorithms.a2_linear_search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearSearchTest {

    @Test
    void normalCase_findsElementInTheMiddleOfAnUnsortedArray() {
        String[] categories = {"DRUG_DELIVERY", "REFERRAL_IN", "BLOOD", "MEALS", "SPECIMEN"};
        LinearSearch<String> search = new LinearSearch<>();

        assertEquals(2, search.search(categories, "BLOOD"));
    }

    @Test
    void boundaryCase_firstAndLastElement() {
        Integer[] arr = {5, 4, 3, 2, 1};
        LinearSearch<Integer> search = new LinearSearch<>();

        assertEquals(0, search.search(arr, 5));
        assertEquals(4, search.search(arr, 1));
    }

    @Test
    void boundaryCase_singleElementArray() {
        String[] arr = {"only"};
        LinearSearch<String> search = new LinearSearch<>();

        assertEquals(0, search.search(arr, "only"));
        assertEquals(-1, search.search(arr, "missing"));
    }

    @Test
    void invalidCase_targetNotPresentReturnsNegativeOne() {
        Integer[] arr = {1, 2, 3};
        LinearSearch<Integer> search = new LinearSearch<>();

        assertEquals(-1, search.search(arr, 42));
    }

    @Test
    void invalidCase_emptyArrayReturnsNegativeOne() {
        String[] arr = {};
        LinearSearch<String> search = new LinearSearch<>();

        assertEquals(-1, search.search(arr, "anything"));
    }

    @Test
    void invalidCase_nullArrayThrows() {
        LinearSearch<String> search = new LinearSearch<>();
        assertThrows(IllegalArgumentException.class, () -> search.search(null, "x"));
    }

    @Test
    void searchWithTrace_recordsOneStepPerElementChecked() {
        Integer[] arr = {10, 20, 30};
        LinearSearch<Integer> search = new LinearSearch<>();
        StringBuilder trace = new StringBuilder();

        int index = search.searchWithTrace(arr, 30, trace);

        assertEquals(2, index);
        String output = trace.toString();
        assertTrue(output.contains("check index 0"));
        assertTrue(output.contains("check index 1"));
        assertTrue(output.contains("check index 2: 30 -> match, return index 2"));
    }
}
