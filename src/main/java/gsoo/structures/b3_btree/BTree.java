package gsoo.structures.b3_btree;

/**
 * B-tree — Slot B3 (Gyankomah Samuel Offei-Dei)
 * Confirmed role (Slot Board, checked 01/08/2026): page index over the
 * growing request/runs tables — simulates how a real database index
 * speeds up lookups as a table grows.
 *
 * A self-balancing search tree where each node can hold multiple keys
 * (up to 2t-1) instead of just one. Mirrors how a real database index
 * page works: keep nodes wide and shallow so search stays fast even
 * as the dataset grows.
 *
 * NOTE: no interface has been frozen for B3 yet and nothing else in
 * the project depends on this one, so there's no risk of breaking a
 * downstream slot by building ahead. Once the real key/value types
 * are settled (likely K = Integer requestId, V = the request record
 * type), swap the generics below — the split/search logic underneath
 * won't need to change.
 *
 * Constraint compliance: no java.util.TreeMap, HashMap, or any other
 * banned built-in is used anywhere in this class. Only arrays.
 */
public class BTree<K extends Comparable<K>, V> {

    // Minimum degree t. Each node holds between (t-1) and (2t-1) keys,
    // except the root. Every internal node has one more child than keys.
    private final int t;
    private Node<K, V> root;

    public BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("Minimum degree must be >= 2");
        }
        this.t = minDegree;
        this.root = new Node<>(minDegree, true);
    }

    // ---------- Node ----------
    // Declared static (not a plain inner class) on purpose: Java does not
    // allow creating arrays of a non-static inner class of a generic type
    // (it's treated as "generic array creation" and fails to compile).
    // Making Node its own static generic class, with t passed in
    // explicitly, sidesteps that restriction cleanly.

    private static class Node<K extends Comparable<K>, V> {
        int numKeys;
        K[] keys;
        V[] values;
        Node<K, V>[] children;
        boolean isLeaf;

        @SuppressWarnings("unchecked")
        Node(int t, boolean isLeaf) {
            this.isLeaf = isLeaf;
            this.keys = (K[]) new Comparable[2 * t - 1];
            this.values = (V[]) new Object[2 * t - 1];
            // Raw array creation here (no <K,V>) is legal Java — only
            // creating an array of an explicitly parameterized type
            // (e.g. new Node<K,V>[n]) is disallowed. This line compiles
            // with an "unchecked" warning, not an error.
            this.children = new Node[2 * t];
            this.numKeys = 0;
        }
    }

    // ---------- Search ----------

    /**
     * Search returns the value for a key, or null if absent.
     * Also builds a human-readable trace of the path taken — this is
     * your evidence artifact for the report/oral defense.
     */
    public V search(K key) {
        return search(root, key, new StringBuilder(), true);
    }

    public V searchWithTrace(K key, StringBuilder trace) {
        return search(root, key, trace, true);
    }

    private V search(Node<K, V> node, K key, StringBuilder trace, boolean rootCall) {
        int i = 0;
        while (i < node.numKeys && key.compareTo(node.keys[i]) > 0) {
            i++;
        }
        trace.append("Visit node keys=").append(keysToString(node)).append(" -> ");

        if (i < node.numKeys && key.compareTo(node.keys[i]) == 0) {
            trace.append("found at index ").append(i).append("\n");
            return node.values[i];
        }
        if (node.isLeaf) {
            trace.append("leaf reached, not found\n");
            return null;
        }
        trace.append("descend to child ").append(i).append("\n");
        return search(node.children[i], key, trace, false);
    }

    // ---------- Insert ----------

    public void insert(K key, V value) {
        if (key == null) {
            throw new NullPointerException("B-tree key cannot be null");
        }
        Node<K, V> r = root;
        if (r.numKeys == 2 * t - 1) {
            // Root is full: split proactively so insert always has room.
            Node<K, V> newRoot = new Node<>(t, false);
            newRoot.children[0] = r;
            root = newRoot;
            splitChild(newRoot, 0);
            insertNonFull(newRoot, key, value);
        } else {
            insertNonFull(r, key, value);
        }
    }

    private void insertNonFull(Node<K, V> node, K key, V value) {
        int i = node.numKeys - 1;

        if (node.isLeaf) {
            while (i >= 0 && key.compareTo(node.keys[i]) < 0) {
                node.keys[i + 1] = node.keys[i];
                node.values[i + 1] = node.values[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.values[i + 1] = value;
            node.numKeys++;
        } else {
            while (i >= 0 && key.compareTo(node.keys[i]) < 0) {
                i--;
            }
            i++;
            if (node.children[i].numKeys == 2 * t - 1) {
                splitChild(node, i);
                if (key.compareTo(node.keys[i]) > 0) {
                    i++;
                }
            }
            insertNonFull(node.children[i], key, value);
        }
    }

    /**
     * Splits the full child at index i of parent into two nodes,
     * pushing the median key up into parent.
     *
     * THIS is the method you'll screenshot/log for your "before/after
     * split diagram" evidence requirement — call splitChild directly
     * in a unit test, print the node before and after, done.
     */
    private void splitChild(Node<K, V> parent, int i) {
        Node<K, V> fullChild = parent.children[i];
        Node<K, V> newChild = new Node<>(t, fullChild.isLeaf);
        newChild.numKeys = t - 1;

        // Right half of keys/values moves to the new node.
        for (int j = 0; j < t - 1; j++) {
            newChild.keys[j] = fullChild.keys[j + t];
            newChild.values[j] = fullChild.values[j + t];
        }
        if (!fullChild.isLeaf) {
            for (int j = 0; j < t; j++) {
                newChild.children[j] = fullChild.children[j + t];
            }
        }

        K medianKey = fullChild.keys[t - 1];
        V medianValue = fullChild.values[t - 1];
        fullChild.numKeys = t - 1;

        // Shift parent's children/keys right to make room, then insert.
        for (int j = parent.numKeys; j >= i + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[i + 1] = newChild;

        for (int j = parent.numKeys - 1; j >= i; j--) {
            parent.keys[j + 1] = parent.keys[j];
            parent.values[j + 1] = parent.values[j];
        }
        parent.keys[i] = medianKey;
        parent.values[i] = medianValue;
        parent.numKeys++;
    }

    // ---------- Trace / debug helpers (evidence generation) ----------

    private String keysToString(Node<K, V> node) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < node.numKeys; i++) {
            sb.append(node.keys[i]);
            if (i < node.numKeys - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    /** Prints the tree level by level — useful for before/after split screenshots. */
    public void printTree() {
        printLevel(root, 0);
    }

    private void printLevel(Node<K, V> node, int depth) {
        System.out.println("  ".repeat(depth) + keysToString(node));
        if (!node.isLeaf) {
            for (int i = 0; i <= node.numKeys; i++) {
                if (node.children[i] != null) {
                    printLevel(node.children[i], depth + 1);
                }
            }
        }
    }
}
