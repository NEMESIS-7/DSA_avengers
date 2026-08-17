package gsoo.structures.b2_avl_tree;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class AVLTree<T extends Comparable<T>> {

    private Node root;
    private int size;




    private class Node {
        T value;
        Node left, right;
        int height; // height of this subtree, leaf = 0

        Node(T value) {
            this.value = value;
            this.height = 0;
        }
    }

    //Public API

    public void insert(T value) {
        root = insert(root, value);
    }

    public void delete(T value) {
        root = delete(root, value);
    }

    public boolean contains(T value) {
        return contains(root, value);
    }

    public int size() {
        return size;
    }

    public int height() {
        return height(root);
    }

    public List<T> inOrder() {
        List<T> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    public boolean isEmpty() {
        return root == null;
    }

    // Height / balance helpers

    private int height(Node node) {
        return node == null ? -1 : node.height;
    }

    private int balanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    // Rotations

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t2 = x.right;

        x.right = y;
        y.left = t2;

        updateHeight(y);
        updateHeight(x);

        return x; // new subtree root
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t2 = y.left;

        y.left = x;
        x.right = t2;

        updateHeight(x);
        updateHeight(y);

        return y; // new subtree root
    }

    private Node rebalance(Node node) {
        updateHeight(node);
        int balance = balanceFactor(node);

        // Left heavy
        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left); // left-right case
            }
            return rotateRight(node); // left-left case
        }

        // Right heavy
        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right); // right-left case
            }
            return rotateLeft(node); // right-right case
        }

        return node; // already balanced
    }

    // Insert

    private Node insert(Node node, T value) {
        if (node == null) {
            size++;
            return new Node(value);
        }

        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = insert(node.left, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, value);
        } else {
            return node; // no duplicates , decide with team if duplicates should be allowed
        }

        return rebalance(node);
    }

    //  Delete

    private Node delete(Node node, T value) {
        if (node == null) {
            return null;
        }

        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = delete(node.left, value);
        } else if (cmp > 0) {
            node.right = delete(node.right, value);
        } else {
            // found the node to delete
            if (node.left == null || node.right == null) {
                Node child = (node.left != null) ? node.left : node.right;
                size--;
                if (child == null) {
                    return null; // no children
                }
                return child; // one child — promote it
            } else {
                // two children: replace with in-order successor
                Node successor = minNode(node.right);
                node.value = successor.value;
                node.right = delete(node.right, successor.value);
                // size already decremented in the recursive call above
                return rebalance(node);
            }
        }

        return rebalance(node);
    }

    private Node minNode(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Search

    private boolean contains(Node node, T value) {
        if (node == null) {
            return false;
        }
        int cmp = value.compareTo(node.value);
        if (cmp == 0) {
            return true;
        }
        return cmp < 0 ? contains(node.left, value) : contains(node.right, value);
    }

    // Traversal

    private void inOrder(Node node, List<T> acc) {
        if (node == null) {
            return;
        }
        inOrder(node.left, acc);
        acc.add(node.value);
        inOrder(node.right, acc);
    }
}