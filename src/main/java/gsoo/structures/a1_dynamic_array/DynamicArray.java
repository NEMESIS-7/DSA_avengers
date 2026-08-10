package com.reynolds.dsa.linear.array;

/**
 * A dynamic array backed by a plain Object[].
 * <p>
 * THE CONTRACT — implement all of these:
 * add(item)          — append to end. O(?) amortized — why?
 * get(index)         — O(?)
 * set(index, item)   — O(?)
 * removeAt(index)    — O(?) — what has to happen to the elements after index?
 * size()             — O(?)
 * isEmpty()          — O(?)
 * <p>
 * RULES:
 * - No java.util imports. The whole point is building it yourself.
 * - Start with capacity 10. When full, grow. Decide your growth factor
 * and be able to defend it (what does ArrayList use? why?).
 * - Out-of-bounds access must throw IndexOutOfBoundsException.
 * - Write the complexity + WHY as a comment above every method.
 * <p>
 * WHEN DONE:
 * - Open the JDK's ArrayList source (Ctrl+click in IntelliJ).
 * Find three things it does that yours doesn't. Write them at the
 * bottom of this file as comments.
 */
public class DynamicArray<T> {

    private Object[] data = new Object[10];
    private int size = 0;

    public DynamicArray(int initialCapacity) {
        if (initialCapacity > 0) {
            this.data = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.data = new Object[0];
        } else {
            throw new IllegalArgumentException("Illegal capacity");
        }
    }

    public DynamicArray() {

    }

    /*
    * O(1) one field read*/
    public int size() {
        return size;
    }

    /*
    * O(1) one field read plus the comparison*/
    public boolean isEmpty() {
        return size == 0;
    }

    // O(1) amortized.
    // Most adds do one write and one size++ — constant.
    // When size == capacity we grow: allocate new array of 2× size,
    // copy n elements over — that single op is O(n).
    // But growth happens rarely: to reach size n you resize at
    // 1, 2, 4, 8, ..., n. Total copy work: 1+2+4+...+n ≈ 2n.
    // Spread over n adds, that's ~2 extra ops per add. Constant.
    public T add(T item) {
        if (size == data.length) {
            grow();
        }
        data[size] = item;
        size++;
        return item;
    }

    /*
    * O(1). Array access is arithmetic, there's no traversal, just one memery read
    *      * */
    public T get(int index) {
        if (index < 0 || index > size - 1) {
            throw new IndexOutOfBoundsException();
        }
        return (T) data[index];

    }

    /*
     * Here again there's no traversal similar to get, we locate using the index and then set the new value...no traversal so the time is O(1)
     * */
    public void set(int index, T item) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        data[index] = item;
    }

    /*
    * Worst case O(n), O(1) best case
    * at the worst case, we remove at 0 so every element shifts, one slot left, meaning n-1 copies
    * best case we remove the last element  at size - 1. there we null the slot and decrease the array size
    * */
    public void removeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;
        size--;
    }

    private void grow() {
        Object[] newArray = new Object[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            newArray[i] = data[i];
        }
        data = newArray;
    }

    /*
     * Grow in the ArrayList class uses the Arrays.copy of method or otherwise returns a new object array
     * Set checks if we've reached the length first and grows if so, before adding element and increasing the size by one
     * Remove in array list fist checks if the index is within the bounds of the range 0 to the length and then calls a fast remove method. This method computes a new size, copies the array and then sets the last index to null
     *
     * */
}


