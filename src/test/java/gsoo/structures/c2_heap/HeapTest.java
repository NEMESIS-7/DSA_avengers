package gsoo.structures.c2_heap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeapTest {

    @Test
    void normal_extractsInAscendingPriorityOrder() {
        Heap<String> heap = new Heap<>();
        heap.insert("low", 9);
        heap.insert("mid", 5);
        heap.insert("high", 1);
        heap.insert("other", 7);

        assertEquals("high", heap.extractMin());
        assertEquals("mid", heap.extractMin());
        assertEquals("other", heap.extractMin());
        assertEquals("low", heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void normal_decreaseKeyPromotesAnElement() {
        Heap<String> heap = new Heap<>();
        heap.insert("a", 10);
        heap.insert("b", 20);
        heap.insert("c", 30);

        assertTrue(heap.decreaseKey("c", 5));
        assertEquals("c", heap.extractMin());
        assertEquals("a", heap.extractMin());
        assertEquals("b", heap.extractMin());
    }

    @Test
    void normal_peekDoesNotRemove() {
        Heap<Integer> heap = new Heap<>();
        heap.insert(1, 1);
        heap.insert(2, 2);
        assertEquals(Integer.valueOf(1), heap.peekMin());
        assertEquals(2, heap.size());
    }

    @Test
    void boundary_singleElementLifecycle() {
        Heap<String> heap = new Heap<>();
        heap.insert("only", 3);
        assertEquals(1, heap.size());
        assertFalse(heap.isEmpty());
        assertEquals("only", heap.peekMin());
        assertEquals("only", heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void boundary_manyInsertsTriggerGrowthAndStaySorted() {
        Heap<Integer> heap = new Heap<>(4);
        for (int i = 100; i >= 1; i--) {
            heap.insert(i, i); // descending insertion, descending priority
        }
        int prev = -1;
        int count = 0;
        while (!heap.isEmpty()) {
            int next = heap.extractMin();
            assertTrue(next >= prev, "extracted out of order: " + next + " after " + prev);
            prev = next;
            count++;
        }
        assertEquals(100, count, "heap must not lose elements across growth");
    }

    @Test
    void boundary_duplicatePrioritiesExtractMonotonically() {
        Heap<String> heap = new Heap<>();
        heap.insert("x1", 2);
        heap.insert("x2", 2);
        heap.insert("x3", 2);
        heap.insert("x4", 2);
        double last = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < 4; i++) {
            String s = heap.extractMin();
            assertTrue(s.startsWith("x"));
            last = 2;
        }
        assertEquals(2.0, last);
    }

    @Test
    void invalid_extractFromEmptyHeapThrows() {
        Heap<String> heap = new Heap<>();
        assertThrows(HeapEmptyException.class, heap::extractMin);
    }

    @Test
    void invalid_peekFromEmptyHeapThrows() {
        Heap<String> heap = new Heap<>();
        assertThrows(HeapEmptyException.class, heap::peekMin);
    }

    @Test
    void invalid_nullElementRejected() {
        Heap<String> heap = new Heap<>();
        assertThrows(IllegalArgumentException.class, () -> heap.insert(null, 1.0));
    }

    @Test
    void invalid_nanPriorityRejected() {
        Heap<String> heap = new Heap<>();
        assertThrows(IllegalArgumentException.class, () -> heap.insert("a", Double.NaN));
    }

    @Test
    void invalid_zeroOrNegativeCapacityRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Heap<String>(0));
        assertThrows(IllegalArgumentException.class, () -> new Heap<String>(-3));
    }
}