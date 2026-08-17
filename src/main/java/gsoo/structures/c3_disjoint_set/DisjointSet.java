package gsoo.structures.c3_disjoint_set;

/**
 * Slot C3 (Ayim Obed Boateng) — Disjoint Set / Union-Find.
 *
 * Array-backed implementation using:
 * - union by rank
 * - path compression
 *
 * No HashMap, TreeMap, or other banned Java collection structures are used.
 */
public class DisjointSet {

    private static final int DEFAULT_CAPACITY = 10;

    // Stores the actual String IDs, e.g. "EXT-C01", "INT-08"
    private String[] ids;

    // parent[i] stores the index of the parent of element i
    private int[] parent;

    // rank[i] helps keep the trees shallow during union
    private int[] rank;

    // Number of elements currently stored
    private int size;

    // Number of separate sets currently remaining
    private int setCount;

    /**
     * Creates an empty Disjoint Set.
     */
    public DisjointSet() {
        ids = new String[DEFAULT_CAPACITY];
        parent = new int[DEFAULT_CAPACITY];
        rank = new int[DEFAULT_CAPACITY];

        size = 0;
        setCount = 0;
    }

    /**
     * Creates a new singleton set for id.
     * Throws IllegalArgumentException if id is null or already exists.
     */
    public void makeSet(String id) {

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (indexOf(id) != -1) {
            throw new IllegalArgumentException("ID already exists: " + id);
        }

        ensureCapacity();

        ids[size] = id;

        // Every new element begins as its own parent/root.
        parent[size] = size;

        // A single-node tree starts at rank 0.
        rank[size] = 0;

        size++;
        setCount++;
    }

    /**
     * Returns the representative/root of id's set.
     * Uses path compression.
     *
     * Throws IllegalArgumentException if id is unknown.
     */
    public String find(String id) {

        int index = requireIndex(id);

        int rootIndex = findRootIndex(index);

        return ids[rootIndex];
    }

    /**
     * Unions the sets containing a and b using union by rank.
     *
     * Returns:
     * true  -> two different sets were merged
     * false -> a and b were already in the same set
     */
    public boolean union(String a, String b) {

        int indexA = requireIndex(a);
        int indexB = requireIndex(b);

        int rootA = findRootIndex(indexA);
        int rootB = findRootIndex(indexB);

        // Already connected.
        if (rootA == rootB) {
            return false;
        }

        // Attach smaller-rank tree below larger-rank tree.
        if (rank[rootA] < rank[rootB]) {

            parent[rootA] = rootB;

        } else if (rank[rootA] > rank[rootB]) {

            parent[rootB] = rootA;

        } else {

            // Same rank: choose rootA as parent
            parent[rootB] = rootA;

            // Tree height may increase by one.
            rank[rootA]++;
        }

        // Two sets became one.
        setCount--;

        return true;
    }

    /**
     * Returns true if a and b belong to the same set.
     */
    public boolean connected(String a, String b) {

        int indexA = requireIndex(a);
        int indexB = requireIndex(b);

        return findRootIndex(indexA) == findRootIndex(indexB);
    }

    /**
     * Same idea as find(), but records the path followed.
     *
     * Useful for trace evidence in the report/demo.
     */
    public String findWithTrace(String id, StringBuilder trace) {

        if (trace == null) {
            throw new IllegalArgumentException("Trace cannot be null");
        }

        int index = requireIndex(id);

        trace.append("find(")
                .append(id)
                .append("): ");

        int current = index;

        // Show the path before compression.
        while (parent[current] != current) {

            trace.append(ids[current])
                    .append(" -> ");

            current = parent[current];
        }

        // current is now the root.
        trace.append(ids[current])
                .append(" [root]");

        int root = current;

        // Perform path compression.
        current = index;

        while (parent[current] != current) {

            int next = parent[current];

            parent[current] = root;

            current = next;
        }

        trace.append(System.lineSeparator());

        return ids[root];
    }

    /**
     * Number of disjoint sets currently remaining.
     */
    public int setCount() {
        return setCount;
    }

    /**
     * Finds the array index belonging to an ID.
     *
     * Returns -1 when not found.
     *
     * Linear search is used because HashMap is banned for assessed core logic.
     */
    private int indexOf(String id) {

        if (id == null) {
            return -1;
        }

        for (int i = 0; i < size; i++) {

            if (ids[i].equals(id)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the index belonging to id or throws an exception
     * if the element does not exist.
     */
    private int requireIndex(String id) {

        int index = indexOf(id);

        if (index == -1) {
            throw new IllegalArgumentException(
                    "Unknown ID: " + id
            );
        }

        return index;
    }

    /**
     * Finds the root index with path compression.
     */
    private int findRootIndex(int index) {

        // If this element is not the root...
        if (parent[index] != index) {

            // Recursively find the root and point directly to it.
            parent[index] = findRootIndex(parent[index]);
        }

        return parent[index];
    }

    /**
     * Doubles the arrays when they become full.
     */
    private void ensureCapacity() {

        if (size < ids.length) {
            return;
        }

        int newCapacity = ids.length * 2;

        String[] newIds = new String[newCapacity];
        int[] newParent = new int[newCapacity];
        int[] newRank = new int[newCapacity];

        for (int i = 0; i < size; i++) {

            newIds[i] = ids[i];
            newParent[i] = parent[i];
            newRank[i] = rank[i];
        }

        ids = newIds;
        parent = newParent;
        rank = newRank;
    }
}