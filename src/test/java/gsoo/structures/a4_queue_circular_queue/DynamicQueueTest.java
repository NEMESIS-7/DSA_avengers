package gsoo.structures.a4_queue_circular_queue;

import gsoo.harness.TestCase;
import gsoo.harness.TestRunner;
import static gsoo.harness.TestAssert.*;

public class DynamicQueueTest {

    public static void main(String[] args) {
        TestCase[] cases = new TestCase[] {

                new TestCase() {
                    public String name() { return "DynamicQueue: FIFO order preserved"; }
                    public Kind kind() { return Kind.NORMAL; }
                    public void run() {
                        DynamicQueue<String> q = new DynamicQueue<>();
                        q.enqueue("REQ-001");
                        q.enqueue("REQ-002");
                        q.enqueue("REQ-003");
                        assertEquals(3, q.size(), "size after 3 enqueues");
                        assertEquals("REQ-001", q.dequeue(), "first dequeue");
                        assertEquals("REQ-002", q.dequeue(), "second dequeue");
                        assertEquals(1, q.size(), "size after 2 dequeues");
                    }
                },

                new TestCase() {
                    public String name() { return "DynamicQueue: grows past initial capacity without losing order"; }
                    public Kind kind() { return Kind.BOUNDARY; }
                    public void run() {
                        DynamicQueue<Integer> q = new DynamicQueue<>(2); // tiny capacity to force resize
                        int startCapacity = q.capacity();
                        for (int i = 0; i < 10; i++) {
                            q.enqueue(i);
                        }
                        assertTrue(q.capacity() > startCapacity, "capacity should have grown beyond " + startCapacity);
                        for (int i = 0; i < 10; i++) {
                            assertEquals(i, q.dequeue(), "dequeue order after growth, item " + i);
                        }
                        assertTrue(q.isEmpty(), "queue should be empty after draining all 10 items");
                    }
                },

                new TestCase() {
                    public String name() { return "DynamicQueue: dequeue on empty throws QueueEmptyException"; }
                    public Kind kind() { return Kind.INVALID; }
                    public void run() {
                        DynamicQueue<String> q = new DynamicQueue<>();
                        assertThrows(QueueEmptyException.class, q::dequeue, "dequeue on empty queue");
                        assertThrows(QueueEmptyException.class, q::peek, "peek on empty queue");
                    }
                }
        };

        TestRunner.run(cases);
    }
}
