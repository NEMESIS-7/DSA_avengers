package gsoo.structures.c1_set;

/**
 * Slot C1 (Antwi Prince Walker) — Set (hash-backed).
 * Frozen contract, set by A1 (Architect). Method bodies are stubs
 * (UnsupportedOperationException) for C1 to implement — signatures below
 * are what the rest of the project compiles against, so the signatures
 * themselves should not change without going back through A1.
 *
 * Elements are plain String ids (location ids, e.g. "EXT-C01", "INT-08"),
 * matching how every other structure in the project already keys on
 * location/request ids as strings — no custom id type needed.
 *
 * No java.util built-ins (HashMap, HashSet, etc.) — array-backed only,
 * per the project's core-logic constraint.
 */
public class HashSet {

    public HashSet() {
        throw new UnsupportedOperationException("C1: implement me");
    }

    /** Adds element. Returns true if newly added, false if already present. */
    public boolean add(String element) {
        throw new UnsupportedOperationException("C1: implement me");
    }

    /** Removes element. Returns true if removed, false if it wasn't present. */
    public boolean remove(String element) {
        throw new UnsupportedOperationException("C1: implement me");
    }

    public boolean contains(String element) {
        throw new UnsupportedOperationException("C1: implement me");
    }

    public int size() {
        throw new UnsupportedOperationException("C1: implement me");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("C1: implement me");
    }

    /** All elements currently stored, for iteration / evidence printing. */
    public String[] toArray() {
        throw new UnsupportedOperationException("C1: implement me");
    }
}
