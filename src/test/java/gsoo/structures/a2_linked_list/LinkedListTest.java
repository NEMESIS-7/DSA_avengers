package gsoo.structures.a2_linked_list;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {

    @Test
    void normalCase_addAndIterateInInsertionOrder() {
        LinkedList<String> list = new LinkedList<>();
        list.add("CREATED");
        list.add("ASSIGNED");
        list.add("IN_TRANSIT");
        list.add("COMPLETED");

        assertEquals(4, list.size());
        assertEquals("CREATED", list.get(0));
        assertEquals("COMPLETED", list.get(3));

        StringBuilder walked = new StringBuilder();
        for (String event : list) {
            walked.append(event).append(",");
        }
        assertEquals("CREATED,ASSIGNED,IN_TRANSIT,COMPLETED,", walked.toString());
    }

    @Test
    void normalCase_removeMiddleElementKeepsRestConnected() {
        LinkedList<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        assertTrue(list.remove(2));
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test
    void boundaryCase_emptyListAndSingleElement() {
        LinkedList<String> empty = new LinkedList<>();
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.size());
        assertFalse(empty.iterator().hasNext());

        LinkedList<String> single = new LinkedList<>();
        single.add("only");
        assertEquals(1, single.size());
        assertEquals("only", single.get(0));
        assertEquals("only", single.get(single.size() - 1));
    }

    @Test
    void boundaryCase_removingTailUpdatesTailPointer() {
        // Removing the tail and then adding again must not silently drop the
        // append — this is the case that catches a stale tail reference.
        LinkedList<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);

        assertTrue(list.remove(2));
        list.add(3);

        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test
    void invalidCase_getOutOfBoundsThrows() {
        LinkedList<String> list = new LinkedList<>();
        list.add("a");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> new LinkedList<String>().get(0));
    }

    @Test
    void invalidCase_addingNullThrows() {
        LinkedList<String> list = new LinkedList<>();
        assertThrows(IllegalArgumentException.class, () -> list.add(null));
    }

    @Test
    void invalidCase_removingAbsentOrFromEmptyListReturnsFalse() {
        LinkedList<String> list = new LinkedList<>();
        assertFalse(list.remove("nothing here"));

        list.add("present");
        assertFalse(list.remove("absent"));
        assertEquals(1, list.size());
    }
}
