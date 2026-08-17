package gsoo.structures.a4_queue_circular_queue;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CircularQueueTest {

    @Test
    void normalCase_frontIndexWrapsAroundCorrectly() {
        CircularQueue<String> q = new CircularQueue<>(3);
        q.enqueue("A");
        q.enqueue("B");
        q.enqueue("C");

        assertEquals("A", q.dequeue());
        q.enqueue("D");
        assertEquals("B", q.dequeue());
        assertEquals("C", q.dequeue());
        assertEquals("D", q.dequeue());
        assertTrue(q.isEmpty());
    }

    @Test
    void boundaryCase_isFullTrueExactlyAtCapacity() {
        CircularQueue<Integer> q = new CircularQueue<>(4);

        for (int i = 0; i < 4; i++) {
            assertFalse(q.isFull());
            q.enqueue(i);
        }
        assertTrue(q.isFull());
        assertEquals(4, q.size());
    }

    @Test
    void invalidInput_overflowAndUnderflowBothThrow() {
        CircularQueue<String> q = new CircularQueue<>(2);
        q.enqueue("X");
        q.enqueue("Y");
        assertThrows(QueueFullException.class, () -> q.enqueue("Z"));

        CircularQueue<String> empty = new CircularQueue<>(1);
        assertThrows(QueueEmptyException.class, empty::dequeue);
        assertThrows(QueueEmptyException.class, empty::peek);
    }
}