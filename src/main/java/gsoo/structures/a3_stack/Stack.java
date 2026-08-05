package gsoo.structures;

// ================================================================
// A3 — Stack (custom, array-based, built from scratch)
//
// No java.util.Stack, no java.util.ArrayList — just a plain Java
// array that we manage and resize ourselves.
//
// Used by: audit_events / undo-dispatch. Every time something
// happens to a service request, we push an AuditEvent. To "undo"
// the last action, we pop it off.
// ================================================================

public class Stack<T> {

    private T[] data;
    private int size;          // how many elements are actually in the stack right now
    private static final int DEFAULT_CAPACITY = 8;

    @SuppressWarnings("unchecked")
    public Stack() {
        data = (T[]) new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    // ---------------- Core operations ----------------

    public void push(T item) {
        if (size == data.length) {
            resize(data.length * 2);   // double the array when it's full
        }
        data[size] = item;
        size++;
    }

    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot pop from an empty stack");
        }
        size--;
        T item = data[size];
        data[size] = null;   // avoid holding a reference the stack no longer owns
        return item;
    }

    // Look at the top item WITHOUT removing it
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot peek an empty stack");
        }
        return data[size - 1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    // ---------------- Internal resizing ----------------

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] newData = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    // ---------------- Quick manual test (run this file directly) ----------------
    public static void main(String[] args) {
        Stack<String> s = new Stack<>();

        // NORMAL CASE: push a few, pop them back in reverse order
        s.push("A");
        s.push("B");
        s.push("C");
        System.out.println("Normal case — expect C, B, A:");
        System.out.println("  " + s.pop());
        System.out.println("  " + s.pop());
        System.out.println("  " + s.pop());

        // BOUNDARY CASE: push exactly enough to force a resize (default capacity is 8)
        Stack<Integer> s2 = new Stack<>();
        for (int i = 1; i <= 9; i++) s2.push(i);   // 9th push triggers a resize
        System.out.println("\nBoundary case — pushed 9 items past default capacity of 8:");
        System.out.println("  size() = " + s2.size() + " (expect 9)");
        System.out.println("  peek() = " + s2.peek() + " (expect 9)");

        // INVALID CASE: popping an empty stack should throw, not crash silently
        Stack<Integer> s3 = new Stack<>();
        System.out.println("\nInvalid case — pop on empty stack:");
        try {
            s3.pop();
            System.out.println("  ERROR: should have thrown but didn't!");
        } catch (IllegalStateException e) {
            System.out.println("  Correctly threw: " + e.getMessage());
        }
    }
}
