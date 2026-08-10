package gsoo.structures.a1_dynamic_array;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that try to BREAK the structure, not confirm it works.
 * One example given — the rest are yours. Minimum:
 * - empty array: get(0) throws, isEmpty() true, size() 0
 * - single element: add then removeAt(0) leaves it empty
 * - force a resize: add 1000 elements, verify all 1000 survive
 * - removeAt(middle): elements after it shift left correctly
 * - negative index: throws
 */
class DynamicArrayTest {

    @Test
    void getOnEmptyArrayThrows() {
        DynamicArray<String> arr = new DynamicArray<>();
        assertThrows(IndexOutOfBoundsException.class, () -> arr.get(0));
    }

    @Test
    void verifyIsEmpty() {
        DynamicArray<String> data = new DynamicArray<>(0);
        assertTrue(data.isEmpty());
    }

    @Test
    void verifyRemove() {
        DynamicArray<String> data = new DynamicArray<>();
        data.add("Acknowledge me");
        data.removeAt(0);
        assertTrue(data.isEmpty());
    }

    @Test
    void verifyResize() {
        DynamicArray<Integer> data = new DynamicArray<>(2);
        for (Integer i = 0; i <= 1000; i++) {
            data.add(i);
        }
        assertEquals(1001, data.size());
        assertEquals(1000, data.get(1000));
    }

    @Test
    void verifyRemoveAt() {
        DynamicArray<Integer> data = new DynamicArray<>(5);
        for (Integer i = 0; i < 5; i++) {
            data.add(i);
        }
        data.removeAt(2);
        assertEquals(3, data.get(2));
        assertEquals(0, data.get(0));
    }

    @Test
    void verifyNegativeIndex(){
        assertThrows(IllegalArgumentException.class, () -> new DynamicArray<>(-1));
    }

    @Test
    void verifySet(){
        DynamicArray<Integer> data = new DynamicArray<>(3);
        assertThrows(IndexOutOfBoundsException.class, () -> data.set(1, 50));
    }

    @Test
    void verifyRemoveAtThrows() {
        DynamicArray<Integer> data = new DynamicArray<>(5);
        assertThrows(IndexOutOfBoundsException.class, () -> data.removeAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> data.removeAt(data.size()));
    }
}
