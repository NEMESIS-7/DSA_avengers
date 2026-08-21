package gsoo.structures.a4_queue_circular_queue;

public class CircularQueue<T> implements QueueADT<T> {

    private final Object[] data;
    private final int capacity;
    private int front;
    private int rear;
    private int count;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive, got " + capacity);
        }
        this.capacity = capacity;
        this.data = new Object[capacity];
        this.front = 0;
        this.rear = -1;
        this.count = 0;
    }

    @Override
    public void enqueue(T item) {
        if (item == null) {
            throw new IllegalArgumentException("CircularQueue does not accept null items");
        }
        if (isFull()) {
            throw new QueueFullException(
                    "enqueue() called on a full CircularQueue (capacity = " + capacity + ")");
        }
        rear = (rear + 1) % capacity;
        data[rear] = item;
        count++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new QueueEmptyException("dequeue() called on an empty CircularQueue");
        }
        T item = (T) data[front];
        data[front] = null;
        front = (front + 1) % capacity;
        count--;
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new QueueEmptyException("peek() called on an empty CircularQueue");
        }
        return (T) data[front];
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == capacity;
    }

    @Override
    public int size() {
        return count;
    }

    public int capacity() {
        return capacity;
    }
}
