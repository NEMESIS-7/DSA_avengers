package gsoo.structures.b5_map;

/**
 * Slot B5 — Custom Map (association-array based, no java.util.Map/HashMap/TreeMap used).
 *
 * Backed by a resizable array of key-value entries. Lookups use linear scan,
 * which is intentionally simple (readable, easy to trace/test) rather than
 * hashed — B4 already owns the project's Hash table slot, so this Map is the
 * "plain" baseline the efficiency lab can compare against later if needed.
 *
 * NOTE: check with A1 whether a frozen interface contract already exists for
 * this slot (e.g. an IMap<K,V> interface in gsoo.structures). If so, make
 * this class implement it and rename methods to match exactly — interfaces
 * are what everything else in the project compiles against.
 */
public class CustomMap<K, V> {

    private static final int DEFAULT_CAPACITY = 8;

    private Object[] keys;
    private Object[] values;
    private int size;

    public CustomMap() {
        keys = new Object[DEFAULT_CAPACITY];
        values = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    /** Inserts or updates a key-value pair. Returns the previous value, or null if new. */
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        int index = indexOf(key);
        if (index != -1) {
            @SuppressWarnings("unchecked")
            V old = (V) values[index];
            values[index] = value;
            return old;
        }

        if (size == keys.length) {
            resize();
        }

        keys[size] = key;
        values[size] = value;
        size++;
        return null;
    }

    /** Returns the value for a key, or null if absent. */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        int index = indexOf(key);
        if (index == -1) {
            return null;
        }
        @SuppressWarnings("unchecked")
        V value = (V) values[index];
        return value;
    }

    /** Removes a key. Returns the removed value, or null if the key wasn't present. */
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        int index = indexOf(key);
        if (index == -1) {
            return null;
        }

        @SuppressWarnings("unchecked")
        V removed = (V) values[index];

        // shift everything after index left by one to keep the array packed
        for (int i = index; i < size - 1; i++) {
            keys[i] = keys[i + 1];
            values[i] = values[i + 1];
        }
        keys[size - 1] = null;
        values[size - 1] = null;
        size--;

        return removed;
    }

    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return indexOf(key) != -1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns all keys currently stored, in insertion order. */
    @SuppressWarnings("unchecked")
    public K[] keySet() {
        K[] result = (K[]) new Object[size];
        System.arraycopy(keys, 0, result, 0, size);
        return result;
    }

    // --- internal helpers ---

    private int indexOf(K key) {
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }

    private void resize() {
        int newCapacity = keys.length * 2;
        Object[] newKeys = new Object[newCapacity];
        Object[] newValues = new Object[newCapacity];
        System.arraycopy(keys, 0, newKeys, 0, size);
        System.arraycopy(values, 0, newValues, 0, size);
        keys = newKeys;
        values = newValues;
    }
}
