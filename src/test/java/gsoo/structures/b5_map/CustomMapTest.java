package gsoo.structures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Slot B5 — tests for CustomMap.
 * Three required cases per Definition of Done: normal, boundary, invalid input.
 * Adjust the JUnit import above if the project harness (A4) uses a different
 * test template — check src/test/java/gsoo for the shared pattern first.
 */
public class CustomMapTest {

    @Test
    void normal_putGetRemoveWorkTogether() {
        CustomMap<String, Integer> map = new CustomMap<>();

        map.put("ward-3", 12);
        map.put("pharmacy", 5);

        assertEquals(12, map.get("ward-3"));
        assertEquals(5, map.get("pharmacy"));
        assertEquals(2, map.size());

        map.put("ward-3", 20); // update existing key
        assertEquals(20, map.get("ward-3"));
        assertEquals(2, map.size(), "updating a key should not grow size");

        Integer removed = map.remove("pharmacy");
        assertEquals(5, removed);
        assertEquals(1, map.size());
        assertFalse(map.containsKey("pharmacy"));
    }

    @Test
    void boundary_emptyMapAndResizeAcrossDefaultCapacity() {
        CustomMap<Integer, String> map = new CustomMap<>();

        // empty map behaviour
        assertTrue(map.isEmpty());
        assertNull(map.get(1));
        assertNull(map.remove(1));
        assertFalse(map.containsKey(1));

        // push past DEFAULT_CAPACITY (8) to exercise resize()
        for (int i = 0; i < 20; i++) {
            map.put(i, "loc-" + i);
        }
        assertEquals(20, map.size());
        assertEquals("loc-19", map.get(19));
        assertEquals("loc-0", map.get(0));
    }

    @Test
    void invalid_nullKeyThrowsOnEveryEntryPoint() {
        CustomMap<String, Integer> map = new CustomMap<>();

        assertThrows(IllegalArgumentException.class, () -> map.put(null, 1));
        assertThrows(IllegalArgumentException.class, () -> map.get(null));
        assertThrows(IllegalArgumentException.class, () -> map.remove(null));
        assertThrows(IllegalArgumentException.class, () -> map.containsKey(null));
    }
}
