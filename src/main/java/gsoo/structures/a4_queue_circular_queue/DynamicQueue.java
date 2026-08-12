package gsoo.structures.a4_queue_circular_queue;

public class DynamicQueue<T> implements QueueADT<T> {

    private static final int DEFAULT_CAPACITY = 8;

    private Object[] data;
    private int front;
    private int count;

    public DynamicQueue() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicQueue(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity must be positive, got " + initialCapacity);
        }
        this.data = new Object[initialCapacity];
        this.front = 0;
        this.count = 0;
    }

    @Override
    public void enqueue(T item) {
        if (item == null) {
            throw new IllegalArgumentException("DynamicQueue does not accept null items");
        }
        if (count == data.length) {
            grow();
        }
        int rear = (front + count) % data.length;
        data[rear] = item;
        count++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new QueueEmptyException("dequeue() called on an empty DynamicQueue");
        }
        T item = (T) data[front];
        data[front] = null; // avoid holding a stale reference
        front = (front + 1) % data.length;
        count--;
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new QueueEmptyException("peek() called on an empty DynamicQueue");
        }
        return (T) data[front];
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public int size() {
        return count;
    }

    public int capacity() {
        return data.length;
    }

    private void grow() {
        Object[] bigger = new Object[data.length * 2];
        for (int i = 0; i < count; i++) {
            bigger[i] = data[(front + i) % data.length];
        }
        data = bigger;
        front = 0;
    }
}
