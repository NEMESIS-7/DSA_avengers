package gsoo.structures.a4_queue_circular_queue;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DynamicQueueTest {

    @Test
    void normalCase_fifoOrderPreserved() {
        DynamicQueue<String> q = new DynamicQueue<>();
        q.enqueue("REQ-001");
        q.enqueue("REQ-002");
        q.enqueue("REQ-003");

        assertEquals(3, q.size());
        assertEquals("REQ-001", q.dequeue());
        assertEquals("REQ-002", q.dequeue());
        assertEquals(1, q.size());
    }

    @Test
    void boundaryCase_growsPastInitialCapacityWithoutLosingOrder() {
        DynamicQueue<Integer> q = new DynamicQueue<>(2);
        int startCapacity = q.capacity();

        for (int i = 0; i < 10; i++) {
            q.enqueue(i);
        }
        assertTrue(q.capacity() > startCapacity);

        for (int i = 0; i < 10; i++) {
            assertEquals(i, q.dequeue());
        }
        assertTrue(q.isEmpty());
    }

    @Test
    void invalidInput_dequeueAndPeekOnEmptyThrow() {
        DynamicQueue<String> q = new DynamicQueue<>();
        assertThrows(QueueEmptyException.class, q::dequeue);
        assertThrows(QueueEmptyException.class, q::peek);
    }
}