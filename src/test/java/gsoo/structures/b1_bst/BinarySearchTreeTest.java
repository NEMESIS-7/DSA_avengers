package gsoo.structures.b1_bst;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinarySearchTreeTest {

    @Test
    void insertsValuesAndRejectsDuplicates() {
        BinarySearchTree tree = populatedTree();

        assertEquals(7, tree.size());
        assertFalse(tree.insert(70));
        assertEquals(7, tree.size(), "duplicate insert must not change the tree size");
    }

    @Test
    void findsValuesAndReportsMinMax() {
        BinarySearchTree tree = populatedTree();

        assertTrue(tree.contains(40));
        assertFalse(tree.contains(99));
        assertEquals(20, tree.min());
        assertEquals(80, tree.max());
    }

    @Test
    void returnsValuesInAscendingOrder() {
        BinarySearchTree tree = populatedTree();

        assertArrayEquals(new int[]{20, 30, 40, 50, 60, 70, 80}, tree.inOrder());
    }

    @Test
    void removesLeafOneChildAndTwoChildNodes() {
        BinarySearchTree tree = populatedTree();

        assertTrue(tree.remove(20));
        assertTrue(tree.remove(30));
        assertTrue(tree.remove(70));
        assertFalse(tree.remove(999));

        assertArrayEquals(new int[]{40, 50, 60, 80}, tree.inOrder());
        assertEquals(4, tree.size());
    }

    @Test
    void removesRootAndKeepsTreeOrdered() {
        BinarySearchTree tree = populatedTree();

        assertTrue(tree.remove(50));
        assertArrayEquals(new int[]{20, 30, 40, 60, 70, 80}, tree.inOrder());
        assertEquals(6, tree.size());
    }

    @Test
    void handlesEmptyTreeEdgeCases() {
        BinarySearchTree empty = new BinarySearchTree();

        assertTrue(empty.isEmpty());
        assertEquals(0, empty.size());
        assertFalse(empty.remove(1));
        assertArrayEquals(new int[0], empty.inOrder());
        assertThrows(IllegalStateException.class, empty::min);
        assertThrows(IllegalStateException.class, empty::max);
    }

    private static BinarySearchTree populatedTree() {
        BinarySearchTree tree = new BinarySearchTree();
        int[] input = {50, 30, 70, 20, 40, 60, 80};
        for (int value : input) {
            assertTrue(tree.insert(value));
        }
        return tree;
    }
}
