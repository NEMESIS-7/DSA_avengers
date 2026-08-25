package gsoo.structures.c3_disjoint_set;

/**
 * Slot C3 (Ayim Obed Boateng) — Disjoint set.
 * Frozen contract, set by A1 (Architect). Method bodies are stubs
 * (UnsupportedOperationException) for C3 to implement — signatures below
 * are what the rest of the project (Kruskal, and any counterexample/
 * connectivity scenario built on top of it) compiles against, so the
 * signatures themselves should not change without going back through A1.
 *
 * Elements are plain String ids (location ids, e.g. "EXT-C01", "INT-08"),
 * matching how every other structure in the project already keys on
 * location/request ids as strings — no custom id type needed.
 *
 * No java.util built-ins (HashMap, TreeMap, etc.) — array-backed only,
 * per the project's core-logic constraint.
 */
public class DisjointSet {

    public DisjointSet() {
        throw new UnsupportedOperationException("C3: implement me");
    }

    /**
     * Creates a new singleton set for id.
     * Throws IllegalArgumentException if id is null or already exists.
     */
    public void makeSet(String id) {
        throw new UnsupportedOperationException("C3: implement me");
    }

    /**
     * Returns the representative (root) of id's set, with path compression.
     * Throws IllegalArgumentException if id was never passed to makeSet().
     */
    public String find(String id) {
        throw new UnsupportedOperationException("C3: implement me");
    }

    /**
     * Unions the sets containing a and b (union by rank/size).
     * Returns true if they were merged, false if already in the same set.
     * Throws IllegalArgumentException if either id is unknown.
     */
    public boolean union(String a, String b) {
        throw new UnsupportedOperationException("C3: implement me");
    }

    /** Convenience: true if a and b are currently in the same set. */
    public boolean connected(String a, String b) {
        throw new UnsupportedOperationException("C3: implement me");
    }

    /**
     * Same as find(), but appends a human-readable step trace to `trace` —
     * evidence hook, same convention as BTree.searchWithTrace().
     */
    public String findWithTrace(String id, StringBuilder trace) {
        throw new UnsupportedOperationException("C3: implement me");
    }

    /**
     * Number of disjoint sets remaining. Useful for checking whether an MST
     * is complete (count == 1) and for "still connected when a road floods"
     * style queries.
     */
    public int setCount() {
        throw new UnsupportedOperationException("C3: implement me");
    }
}
