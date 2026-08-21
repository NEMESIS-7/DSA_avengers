package gsoo.structures.b3_btree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for B3 — B-tree.
 * Covers the three required cases: normal, boundary, invalid input.
 */
public class BTreeTest {

    private BTree<Integer, String> tree;

    @BeforeEach
    void setUp() {
        // Minimum degree 2 -> each node holds at most 3 keys before splitting.
        // Small on purpose, so boundary/split behaviour is easy to trigger and verify.
        tree = new BTree<>(2);
    }

    // ---------- 1. Normal case ----------
    // Insert a handful of keys, confirm each one is found with the right value.

    @Test
    void insertAndSearch_returnsCorrectValuesForExistingKeys() {
        tree.insert(10, "Request-10");
        tree.insert(20, "Request-20");
        tree.insert(5, "Request-5");

        assertEquals("Request-10", tree.search(10));
        assertEquals("Request-20", tree.search(20));
        assertEquals("Request-5", tree.search(5));
    }

    // ---------- 2. Boundary case ----------
    // Insert enough keys to force at least one node split (min degree 2 means
    // a node holds max 3 keys before splitting), then confirm every key --
    // including the ones that caused the split -- is still findable and correct.

    @Test
    void insert_triggersSplit_allKeysStillFindableAfterward() {
        // With t = 2, inserting a 4th key into a node forces a split.
        int[] keysToInsert = {1, 2, 3, 4, 5, 6, 7};
        for (int key : keysToInsert) {
            tree.insert(key, "Request-" + key);
        }

        // Every key inserted before and after the split must still resolve correctly.
        for (int key : keysToInsert) {
            assertEquals("Request-" + key, tree.search(key),
                    "Key " + key + " should still be findable after node splits");
        }
    }

    // ---------- 3. Invalid input case ----------
    // Two forms of "invalid" for a B-tree: searching for a key that was never
    // inserted (should return null, not throw or return garbage), and
    // inserting a null key (should fail loudly, not corrupt the tree silently).

    @Test
    void search_forKeyNeverInserted_returnsNull() {
        tree.insert(1, "Request-1");
        tree.insert(2, "Request-2");

        assertNull(tree.search(999), "Searching for an absent key should return null, not throw");
    }

    @Test
    void insert_withNullKey_throwsException() {
        assertThrows(NullPointerException.class, () -> tree.insert(null, "Request-null"),
                "Inserting a null key should fail explicitly rather than corrupt the tree");
    }
}
