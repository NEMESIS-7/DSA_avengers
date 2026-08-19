package gsoo.structures.b4_hash_table;

import gsoo.db.ServiceRequest;

// One entry (node) stored in the hash table.
// We use chaining to deal with collisions, so each bucket is a small linked list of these nodes.
public class HashEntry {

    String key;              // the requestId
    ServiceRequest value;    
    HashEntry next;          // pointer to the next entry in the same bucket (for chaining)

    public HashEntry(String key, ServiceRequest value) {
        this.key = key;
        this.value = value;
        this.next = null; // no next entry yet when we first create it
    }
}