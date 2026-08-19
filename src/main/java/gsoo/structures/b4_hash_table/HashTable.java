package gsoo.structures.b4_hash_table;

import gsoo.db.ServiceRequest;
import gsoo.app.Config;

// Custom hash table implementation using separate chaining. where collisions are handled by linking entries together.

public class HashTable {

    private HashEntry[] buckets; // the actual array of buckets
    private int size;            // how many entries are currently stored
    private int capacity;        // number of buckets

    // Load factor is entries / capacity. Once it goes above this,
    // we resize the table so lookups don't get slow.
    private static final double LOAD_FACTOR_LIMIT = 0.75;

    public HashTable() {
        this(Config.HASH_TABLE_SIZE);
    }

    // Still allow a custom capacity to be passed in, useful for testing
    public HashTable(int capacity) {
        this.capacity = capacity;
        this.buckets = new HashEntry[capacity];
        this.size = 0;
    }

    //sum up the character codes of the key, then mod by the number of buckets so it fits in the array.
    private int hash(String key) {
        int sum = 0;
        for (int i = 0; i < key.length(); i++) {
            sum = sum + key.charAt(i);
        }
        // Math.abs just in case sum overflows into a negative number
        return Math.abs(sum) % capacity;
    }

    // Insert a request. If the requestId already exists, we update its value
    // instead of adding a duplicate.
    public void insert(String requestId, ServiceRequest value) {
        if (requestId == null) {
            throw new IllegalArgumentException("requestId cannot be null");
        }

        int index = hash(requestId);
        HashEntry current = buckets[index];

        // Walk the chain at this bucket to check if the key is already there
        while (current != null) {
            if (current.key.equals(requestId)) {
                current.value = value; // key exists, just update it
                return;
            }
            current = current.next;
        }

        // key not found in the chain, so add a new entry at the front
        HashEntry newEntry = new HashEntry(requestId, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;

        // check if we need to grow the table
        double currentLoadFactor = (double) size / capacity;
        if (currentLoadFactor > LOAD_FACTOR_LIMIT) {
            resize();
        }
    }

    // Look up a ServiceRequest by requestId. Returns null if not found.
    public ServiceRequest search(String requestId) {
        if (requestId == null) {
            return null;
        }

        int index = hash(requestId);
        HashEntry current = buckets[index];

        while (current != null) {
            if (current.key.equals(requestId)) {
                return current.value;
            }
            current = current.next;
        }

        return null; // key was not found
    }

    // Remove a request by requestId. Returns true if something was removed.
    public boolean delete(String requestId) {
        if (requestId == null) {
            return false;
        }

        int index = hash(requestId);
        HashEntry current = buckets[index];
        HashEntry previous = null;

        while (current != null) {
            if (current.key.equals(requestId)) {
                if (previous == null) {
                    // it was the first entry in the bucket
                    buckets[index] = current.next;
                } else {
                    // skip over the entry we are removing
                    previous.next = current.next;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }

        return false; // key was not found, nothing removed
    }

    // Doubles the table size (roughly) and re-inserts every entry. This has to be done because changing capacity changes what hash(key) returns for every key.
    private void resize() {
        HashEntry[] oldBuckets = buckets;
        capacity = capacity * 2;
        buckets = new HashEntry[capacity];
        size = 0; // will be recounted as we re-insert

        for (int i = 0; i < oldBuckets.length; i++) {
            HashEntry current = oldBuckets[i];
            while (current != null) {
                insert(current.key, current.value);
                current = current.next;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getLoadFactor() {
        return (double) size / capacity;
    }
}