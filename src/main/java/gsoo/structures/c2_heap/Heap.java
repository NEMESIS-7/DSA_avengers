package gsoo.structures.c2_heap;

/**
 * Binary min-heap over (element, priority) pairs, built on a plain Object[]
 * that doubles in capacity.
 *
 * The invariant is the classic heap property: every node's priority is less
 * than or equal to each of its children's, so the minimum sits at index 0.
 * insert/extractMin are O(log n); peekMin is O(1); decreaseKey is O(n) here
 * because it locates the element by linear scan — we deliberately trade the
 * index bookkeeping for simplicity, which is fine for the 50-node graphs and
 * 300-request queues this system actually sees.
 *
 * @param <T> element type (e.g. a node id String, or an Integer node index)
 */
public class Heap<T> {

    private static final int DEFAULT_CAPACITY = 16;

    private static final class Entry {
        final Object element;
        double priority;

        Entry(Object element, double priority) {
            this.element = element;
            this.priority = priority;
        }
    }

    private Entry[] entries;
    private int size;

    public Heap() {
        this(DEFAULT_CAPACITY);
    }

    public Heap(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Capacity must be >= 1, was " + initialCapacity);
        }
        this.entries = new Entry[initialCapacity];
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * O(log n). Invalid inputs (null element, NaN priority) are rejected so
     * the ordering guarantee can never be silently corrupted.
     */
    public void insert(T element, double priority) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot insert a null element");
        }
        if (Double.isNaN(priority)) {
            throw new IllegalArgumentException("Priority cannot be NaN");
        }
        if (size == entries.length) {
            grow();
        }
        entries[size] = new Entry(element, priority);
        size++;
        siftUp(size - 1);
    }

    /** O(1). */
    public T peekMin() {
        if (size == 0) {
            throw new HeapEmptyException("Cannot peek an empty heap");
        }
        return elementAt(0);
    }

    /** O(log n). */
    public T extractMin() {
        if (size == 0) {
            throw new HeapEmptyException("Cannot extract from an empty heap");
        }
        T min = elementAt(0);
        size--;
        entries[0] = entries[size];
        entries[size] = null;
        if (size > 0) {
            siftDown(0);
        }
        return min;
    }

    /**
     * Lower an element's priority, then restore the heap property. O(n) to
     * find the element (linear scan), O(log n) to re-heapify. No-op if the
     * new priority is not an improvement — the caller is asking to raise it.
     *
     * @return true if the element was present (and possibly improved)
     */
    public boolean decreaseKey(T element, double newPriority) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        int i = indexOf(element);
        if (i == -1) {
            return false;
        }
        if (newPriority >= entries[i].priority) {
            return true; // not an improvement; heap property already holds
        }
        entries[i].priority = newPriority;
        siftUp(i);
        return true;
    }

    public boolean contains(T element) {
        return indexOf(element) != -1;
    }

    private int indexOf(T element) {
        for (int i = 0; i < size; i++) {
            if (entries[i].element.equals(element)) {
                return i;
            }
        }
        return -1;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (entries[parent].priority <= entries[i].priority) {
                break;
            }
            swap(parent, i);
            i = parent;
        }
    }

    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;
            if (left < size && entries[left].priority < entries[smallest].priority) {
                smallest = left;
            }
            if (right < size && entries[right].priority < entries[smallest].priority) {
                smallest = right;
            }
            if (smallest == i) {
                break;
            }
            swap(i, smallest);
            i = smallest;
        }
    }

    private void swap(int a, int b) {
        Entry tmp = entries[a];
        entries[a] = entries[b];
        entries[b] = tmp;
    }

    private void grow() {
        Entry[] bigger = new Entry[entries.length * 2];
        for (int i = 0; i < entries.length; i++) {
            bigger[i] = entries[i];
        }
        entries = bigger;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int index) {
        return (T) entries[index].element;
    }
}