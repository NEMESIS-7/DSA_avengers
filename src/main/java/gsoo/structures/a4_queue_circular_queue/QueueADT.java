package gsoo.structures.a4_queue_circular_queue;

public interface QueueADT<T> {

    void enqueue(T item);

    T dequeue();

    T peek();

    boolean isEmpty();

    int size();
}