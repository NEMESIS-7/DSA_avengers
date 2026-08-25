package gsoo.structures.c1_set;

/**
 * Slot C1 (Antwi Prince Walker) - Set (hash-backed).
 *
 * Custom hash-backed Set implementation.
 * No java.util HashMap or HashSet is used.
 */
public class HashSet {

    private String[] table;
    private int size;

    private static final String DELETED = "__C1_DELETED__";

    public HashSet() {
        table = new String[16];
        size = 0;
    }

    /** Adds element. Returns true if newly added, false if already present. */
    public boolean add(String element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }

        if ((size + 1) * 2 >= table.length) {
            resize();
        }

        int index = findSlot(element);

        if (table[index] != null && !table[index].equals(DELETED)) {
            return false;
        }

        table[index] = element;
        size++;

        return true;
    }

    /** Removes element. Returns true if removed, false if it wasn't present. */
    public boolean remove(String element) {
        if (element == null) {
            return false;
        }

        int index = findExistingIndex(element);

        if (index == -1) {
            return false;
        }

        table[index] = DELETED;
        size--;

        return true;
    }

    /** Returns true if the element exists in the set. */
    public boolean contains(String element) {
        if (element == null) {
            return false;
        }

        return findExistingIndex(element) != -1;
    }

    /** Returns the number of elements in the set. */
    public int size() {
        return size;
    }

    /** Returns true when the set contains no elements. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** All elements currently stored, for iteration / evidence printing. */
    public String[] toArray() {
        String[] result = new String[size];

        int position = 0;

        for (int i = 0; i < table.length; i++) {
            if (table[i] != null && !table[i].equals(DELETED)) {
                result[position] = table[i];
                position++;
            }
        }

        return result;
    }

    private int hash(String element) {
        int hash = element.hashCode() & 0x7fffffff;
        return hash % table.length;
    }

    private int findSlot(String element) {
        int index = hash(element);
        int firstDeleted = -1;

        for (int i = 0; i < table.length; i++) {

            int current = (index + i) % table.length;

            if (table[current] == null) {
                if (firstDeleted != -1) {
                    return firstDeleted;
                }

                return current;
            }

            if (table[current].equals(DELETED)) {
                if (firstDeleted == -1) {
                    firstDeleted = current;
                }
            } else if (table[current].equals(element)) {
                return current;
            }
        }

        return firstDeleted;
    }

    private int findExistingIndex(String element) {
        int index = hash(element);

        for (int i = 0; i < table.length; i++) {

            int current = (index + i) % table.length;

            if (table[current] == null) {
                return -1;
            }

            if (!table[current].equals(DELETED)
                    && table[current].equals(element)) {
                return current;
            }
        }

        return -1;
    }

    private void resize() {
        String[] oldTable = table;

        table = new String[oldTable.length * 2];
        size = 0;

        for (String element : oldTable) {
            if (element != null && !element.equals(DELETED)) {
                add(element);
            }
        }
    }
}