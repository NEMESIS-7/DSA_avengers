package gsoo.structures.b1_bst;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();
        int[] input = {50, 30, 70, 20, 40, 60, 80};
        for (int value : input) {
            tree.insert(value);
        }

        System.out.println("BinarySearchTree demo: " + Arrays.toString(tree.inOrder()));
    }
}
