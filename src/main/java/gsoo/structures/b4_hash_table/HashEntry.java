package structures.b4_hash_table;

// One entry (node) stored in the hash table.
// We use chaining to deal with collisions, so each bucket is really
// a small linked list of these nodes.
public class HashEntry {

    String key;             
    ServiceRequest value;
    HashEntry next;          

    public HashEntry(String key, ServiceRequest value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }
}