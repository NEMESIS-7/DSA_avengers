package gsoo.structures.a1_dynamic_array;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void verifyAddGrowsFromZeroCapacity() {
        DynamicArray<String> data = new DynamicArray<>(0);
        data.add("first");
        assertEquals(1, data.size());
        assertEquals("first", data.get(0));
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
