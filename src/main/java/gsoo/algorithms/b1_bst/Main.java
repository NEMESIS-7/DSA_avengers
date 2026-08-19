package gsoo.algorithms.b1_bst;

public class Main {
    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();
        int[] input = {50, 30, 70, 20, 40, 60, 80};
        for (int value : input) {
            check(tree.insert(value), "insert " + value);
        }

        check(tree.size() == 7, "size after inserts");
        check(!tree.insert(70), "duplicate is rejected");
        check(tree.contains(40), "find existing value");
        check(!tree.contains(99), "missing value is not found");
        check(tree.min() == 20, "minimum value");
        check(tree.max() == 80, "maximum value");
        checkArray(tree.inOrder(), new int[]{20, 30, 40, 50, 60, 70, 80}, "in-order traversal");

        check(tree.remove(20), "remove leaf");
        check(tree.remove(30), "remove one-child node");
        check(tree.remove(70), "remove two-child node");
        check(!tree.remove(999), "removing absent value");
        checkArray(tree.inOrder(), new int[]{40, 50, 60, 80}, "order after removals");

        check(tree.remove(50), "remove root");
        checkArray(tree.inOrder(), new int[]{40, 60, 80}, "order after root removal");

        BinarySearchTree empty = new BinarySearchTree();
        check(empty.isEmpty(), "new tree is empty");
        check(!empty.remove(1), "remove from empty tree");

        System.out.println("All BinarySearchTree tests passed.");
    }

    private static void check(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError("Failed: " + testName);
        }
        System.out.println("PASS: " + testName);
    }

    private static void checkArray(int[] actual, int[] expected, String testName) {
        if (actual.length != expected.length) {
            throw new AssertionError("Failed: " + testName + " (different lengths)");
        }
        for (int index = 0; index < actual.length; index++) {
            if (actual[index] != expected[index]) {
                throw new AssertionError("Failed: " + testName + " at index " + index);
            }
        }
        System.out.println("PASS: " + testName);
    }
}
