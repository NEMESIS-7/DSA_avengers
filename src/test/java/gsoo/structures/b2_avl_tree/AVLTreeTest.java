package gsoo.structures.b2_avl_tree;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    // Normal case
    @Test
    void insertAndTraverseInSortedOrder() {
        AVLTree<Integer> tree = new AVLTree<>();
        int[] values = {50, 30, 70, 20, 40, 60, 80};
        for (int v : values) {
            tree.insert(v);
        }

        List<Integer> result = tree.inOrder();
        List<Integer> expected = List.of(20, 30, 40, 50, 60, 70, 80);

        assertEquals(expected, result, "In-order traversal must be sorted ascending");
        assertEquals(7, tree.size());
        assertTrue(tree.contains(40));
        assertFalse(tree.contains(999));
    }

    // Boundary case
    @Test
    void emptyTreeAndSingleNodeBehaviour() {
        AVLTree<Integer> tree = new AVLTree<>();

        // empty tree
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
        assertEquals(-1, tree.height(), "Empty tree height must be -1 per spec");
        assertTrue(tree.inOrder().isEmpty());
        assertFalse(tree.contains(1));

        // single node
        tree.insert(42);
        assertFalse(tree.isEmpty());
        assertEquals(1, tree.size());
        assertEquals(0, tree.height(), "Single-node tree height must be 0 per spec");
        assertEquals(List.of(42), tree.inOrder());
    }

    //Boundary case: balancing under worst-case insertion order
    @Test
    void sequentialInsertsStayBalanced() {
        AVLTree<Integer> tree = new AVLTree<>();
        // Ascending inserts would degrade a plain BST into a linked list (height n-1).
        // An AVL tree must stay balanced: height should be O(log n).
        for (int i = 1; i <= 15; i++) {
            tree.insert(i);
        }
        // 15 nodes: a plain BST would have height 14. AVL must be much shorter.
        assertTrue(tree.height() <= 4, "AVL height for 15 sequential inserts should stay near log2(15)");
        assertEquals(15, tree.size());
    }

    // ---------- Invalid input case ----------
    @Test
    void deletingNonexistentValueIsNoOpAndDuplicateInsertIsIgnored() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(10);
        tree.insert(20);

        // deleting a value never inserted should not throw, and should not change size
        assertDoesNotThrow(() -> tree.delete(999));
        assertEquals(2, tree.size());
        assertTrue(tree.contains(10));
        assertTrue(tree.contains(20));

        // inserting a duplicate should not throw, and should not change size
        assertDoesNotThrow(() -> tree.insert(10));
        assertEquals(2, tree.size(), "Duplicate insert must not increase size");

        // deleting from an empty tree should not throw
        AVLTree<Integer> emptyTree = new AVLTree<>();
        assertDoesNotThrow(() -> emptyTree.delete(1));
    }
}