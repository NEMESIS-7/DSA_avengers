package gsoo.structures.a4_queue_circular_queue;

public class QueueFullException extends RuntimeException {
    public QueueFullException(String message) {
        super(message);
    }
}