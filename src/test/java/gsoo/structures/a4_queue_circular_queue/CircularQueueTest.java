package gsoo.structures.a4_queue_circular_queue;

import gsoo.harness.TestCase;
import gsoo.harness.TestRunner;
import static gsoo.harness.TestAssert.*;

public class CircularQueueTest {

    public static void main(String[] args) {
        TestCase[] cases = new TestCase[] {

                new TestCase() {
                    public String name() { return "CircularQueue: front index wraps around correctly"; }
                    public Kind kind() { return Kind.NORMAL; }
                    public void run() {
                        CircularQueue<String> q = new CircularQueue<>(3);
                        q.enqueue("A"); q.enqueue("B"); q.enqueue("C");
                        assertEquals("A", q.dequeue(), "first out");
                        q.enqueue("D"); // this wraps rear back to index 0
                        assertEquals("B", q.dequeue(), "second out");
                        assertEquals("C", q.dequeue(), "third out");
                        assertEquals("D", q.dequeue(), "fourth out, from wrapped slot");
                        assertTrue(q.isEmpty(), "queue empty after draining");
                    }
                },

                new TestCase() {
                    public String name() { return "CircularQueue: isFull() true exactly at capacity"; }
                    public Kind kind() { return Kind.BOUNDARY; }
                    public void run() {
                        CircularQueue<Integer> q = new CircularQueue<>(4);
                        for (int i = 0; i < 4; i++) {
                            assertTrue(!q.isFull(), "should not be full before item " + i);
                            q.enqueue(i);
                        }
                        assertTrue(q.isFull(), "should be full after exactly 4 enqueues on capacity-4 queue");
                        assertEquals(4, q.size(), "size should equal capacity");
                    }
                },

                // INVALID: enqueue beyond capacity, and dequeue/peek on empty, must both
                // raise the documented exceptions rather than overwrite data or return null
                new TestCase() {
                    public String name() { return "CircularQueue: overflow and underflow both throw"; }
                    public Kind kind() { return Kind.INVALID; }
                    public void run() {
                        CircularQueue<String> q = new CircularQueue<>(2);
                        q.enqueue("X");
                        q.enqueue("Y");
                        assertThrows(QueueFullException.class, () -> q.enqueue("Z"), "enqueue past capacity");

                        CircularQueue<String> empty = new CircularQueue<>(1);
                        assertThrows(QueueEmptyException.class, empty::dequeue, "dequeue on empty");
                        assertThrows(QueueEmptyException.class, empty::peek, "peek on empty");
                    }
                }
        };

        TestRunner.run(cases);
    }
}