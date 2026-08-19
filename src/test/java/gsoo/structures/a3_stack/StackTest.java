package gsoo.structures.a3_stack;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
// The three required tests for my (A3) Stack: normal, boundary, invalid.
// These count toward the team's 40-test floor — the main() demo in
// Stack.java does NOT, since it's not a real JUnit test.
public class StackTest {

    @Test
    void normalCase_pushThenPopReturnsLastPushedFirst() {
        Stack<String> stack = new Stack<>();
        stack.push("A");
        stack.push("B");
        stack.push("C");

        assertEquals("C", stack.pop());
        assertEquals("B", stack.pop());
        assertEquals("A", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void boundaryCase_pushingPastDefaultCapacityTriggersResize() {
        Stack<Integer> stack = new Stack<>();
        // Default capacity is 8 — push 9 to force at least one resize
        for (int i = 1; i <= 9; i++) {
            stack.push(i);
        }

        assertEquals(9, stack.size());
        assertEquals(9, stack.peek());   // most recently pushed item is still on top
    }

    @Test
    void invalidCase_popOnEmptyStackThrows() {
        Stack<Integer> stack = new Stack<>();

        assertThrows(IllegalStateException.class, stack::pop);
        assertThrows(IllegalStateException.class, stack::peek);
    }

    @Test
    void invalidCase_pushingNullThrows() {
        Stack<Integer> stack = new Stack<>();

        assertThrows(IllegalArgumentException.class, () -> stack.push(null));
    }

    @Test
    void lifoOrder_isPreservedAcrossMultiplePushesAndPops() {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }
}