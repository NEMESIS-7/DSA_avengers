package gsoo.structures.b1_bst;

public class BinarySearchTree {
    private static class Node {
        int value;
        Node left;
        Node right;

        Node(int value) {
            this.value = value;
        }
    }

    private Node root;
    private int size;

    /** Adds value when it is not already present. */
    public boolean insert(int value) {
        if (root == null) {
            root = new Node(value);
            size = 1;
            return true;
        }

        Node current = root;
        while (true) {
            if (value == current.value) {
                return false;
            }
            if (value < current.value) {
                if (current.left == null) {
                    current.left = new Node(value);
                    size++;
                    return true;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(value);
                    size++;
                    return true;
                }
                current = current.right;
            }
        }
    }

    public boolean contains(int value) {
        Node current = root;
        while (current != null) {
            if (value == current.value) {
                return true;
            }
            current = value < current.value ? current.left : current.right;
        }
        return false;
    }

    /** Removes value, including nodes with zero, one, or two children. */
    public boolean remove(int value) {
        Node parent = null;
        Node current = root;

        while (current != null && current.value != value) {
            parent = current;
            current = value < current.value ? current.left : current.right;
        }
        if (current == null) {
            return false;
        }

        if (current.left != null && current.right != null) {
            Node successorParent = current;
            Node successor = current.right;
            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }
            current.value = successor.value;
            parent = successorParent;
            current = successor;
        }

        Node replacement = current.left != null ? current.left : current.right;
        if (parent == null) {
            root = replacement;
        } else if (parent.left == current) {
            parent.left = replacement;
        } else {
            parent.right = replacement;
        }
        size--;
        return true;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int min() {
        if (root == null) {
            throw new IllegalStateException("The tree is empty.");
        }
        Node current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    public int max() {
        if (root == null) {
            throw new IllegalStateException("The tree is empty.");
        }
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    /** Returns values in ascending order in a newly allocated primitive array. */
    public int[] inOrder() {
        int[] values = new int[size];
        fillInOrder(root, values, new int[]{0});
        return values;
    }

    private void fillInOrder(Node node, int[] values, int[] index) {
        if (node == null) {
            return;
        }
        fillInOrder(node.left, values, index);
        values[index[0]++] = node.value;
        fillInOrder(node.right, values, index);
    }
}
