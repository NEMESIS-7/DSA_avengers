package gsoo.structures.a2_linked_list;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Slot A2 (Mensah Constance Awura Adwoa) — Linked list + iterator.
 *
 * Singly linked, hand-built nodes, no java.util.LinkedList anywhere.
 * Domain use (team-charter.md §2.8): a per-request event timeline — events
 * append in the order they happen, so add() is append-to-tail (O(1), via a
 * kept tail pointer), not insert-anywhere.
 *
 * "+ iterator": implements java.lang.Iterable so a plain for-each loop
 * walks the real node chain directly — no array snapshot underneath.
 */
public class LinkedList<T> implements Iterable<T> {

    private static class Node<T> {
        final T value;
        Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public LinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /** Appends item to the tail. O(1) — an event timeline grows in arrival order. */
    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null to the list");
        }
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /**
     * Returns the item at index, walking from the head. O(n).
     * Throws IndexOutOfBoundsException if index is out of range.
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    /** Removes the first occurrence of item. Returns true if something was removed. */
    public boolean remove(T item) {
        if (item == null || head == null) {
            return false;
        }

        if (head.value.equals(item)) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
            size--;
            return true;
        }

        Node<T> previous = head;
        Node<T> current = head.next;
        while (current != null) {
            if (current.value.equals(item)) {
                previous.next = current.next;
                if (current == tail) {
                    tail = previous;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Walks the real node chain head to tail — no array snapshot underneath. */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements in the list");
                }
                T value = current.value;
                current = current.next;
                return value;
            }
        };
    }
}
