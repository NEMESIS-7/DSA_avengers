package gsoo.structures.a5_deque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class CustomDequeTest {

    private CustomDeque<String> deque;

    @BeforeEach
    void setUp() {
        deque = new CustomDeque<>();
    }

    // ==========================================
    // TEST 1: NORMAL CASE
    // ==========================================
    @Test
    void testNormalCase() {
        // Add items to both front and back
        deque.addFirst("Request_B");
        deque.addFirst("Request_A");
        deque.addLast("Request_C");
        deque.addLast("Request_D");

        // Expected sequence: [Request_A, Request_B, Request_C, Request_D]
        assertEquals(4, deque.size());
        assertEquals("Request_A", deque.peekFirst());
        assertEquals("Request_D", deque.peekLast());

        // Remove from both ends and verify FIFO/LIFO ordering
        assertEquals("Request_A", deque.removeFirst());
        assertEquals("Request_D", deque.removeLast());
        assertEquals(2, deque.size());

        assertEquals("Request_B", deque.removeFirst());
        assertEquals("Request_C", deque.removeLast());
        assertTrue(deque.isEmpty());
    }

    // ==========================================
    // TEST 2: BOUNDARY CASE
    // ==========================================
    @Test
    void testBoundaryCase() {
        // Boundary 1: Deque starts completely empty
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());

        // Boundary 2: Single element transition (empty -> 1 -> empty)
        deque.addFirst("Solo_Request");
        assertFalse(deque.isEmpty());
        assertEquals(1, deque.size());
        assertEquals("Solo_Request", deque.peekFirst());
        assertEquals("Solo_Request", deque.peekLast());

        String item = deque.removeLast();
        assertEquals("Solo_Request", item);
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());

        // Boundary 3: Re-adding after complete drainage
        deque.addLast("New_Request");
        assertEquals(1, deque.size());
        assertEquals("New_Request", deque.removeFirst());
        assertTrue(deque.isEmpty());
    }

    // ==========================================
    // TEST 3: INVALID INPUT & ERROR HANDLING
    // ==========================================
    @Test
    void testInvalidInput() {
        // Check null element rejections
        assertThrows(IllegalArgumentException.class, () -> deque.addFirst(null),
                "Should reject null elements on addFirst");
        assertThrows(IllegalArgumentException.class, () -> deque.addLast(null),
                "Should reject null elements on addLast");

        // Check underflow rejections on an empty deque
        assertThrows(NoSuchElementException.class, () -> deque.removeFirst(),
                "Cannot removeFirst from empty deque");
        assertThrows(NoSuchElementException.class, () -> deque.removeLast(),
                "Cannot removeLast from empty deque");
        assertThrows(NoSuchElementException.class, () -> deque.peekFirst(),
                "Cannot peekFirst from empty deque");
        assertThrows(NoSuchElementException.class, () -> deque.peekLast(),
                "Cannot peekLast from empty deque");
    }
}