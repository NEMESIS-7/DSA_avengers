package gsoo.structures.b4_hash_table;

import gsoo.db.ServiceRequest;
import java.sql.Timestamp;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HashTableTest {

    // helper just to keep test methods short
    private ServiceRequest makeRequest(String id) {
        return new ServiceRequest(id, "PATIENT_TRANSFER", "PAT-001",
                "WARD-3", "THEATRE-1", 4, "PENDING",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), null);
    }

    // 1. Normal case - insert then find it again
    @Test
    public void testInsertAndSearch() {
        HashTable table = new HashTable();
        ServiceRequest request = makeRequest("REQ-0001");

        table.insert("REQ-0001", request);

        assertEquals(request, table.search("REQ-0001"));
        assertEquals(1, table.getSize());
    }

    // 2. Boundary case: table resizes correctly once load factor is passed
    @Test
    public void testResizeOnHighLoadFactor() {
        HashTable table = new HashTable(4);

        table.insert("REQ-0001", makeRequest("REQ-0001"));
        table.insert("REQ-0002", makeRequest("REQ-0002"));
        table.insert("REQ-0003", makeRequest("REQ-0003"));
        table.insert("REQ-0004", makeRequest("REQ-0004")); // 4/4 = 1.0, now past the 0.75 limit - this should trigger resize

        assertTrue(table.getCapacity() > 4, "table should have grown past its starting capacity");
        assertEquals(4, table.getSize());

        assertNotNull(table.search("REQ-0001"));
        assertNotNull(table.search("REQ-0002"));
        assertNotNull(table.search("REQ-0003"));
        assertNotNull(table.search("REQ-0004"));
    }

    // 3. Invalid input case: null key should not be accepted
    @Test
    public void testInsertNullKeyThrows() {
        HashTable table = new HashTable();

        assertThrows(IllegalArgumentException.class, () -> {
            table.insert(null, makeRequest("REQ-0001"));
        });
    }

    @Test
    public void testSearchMissingKeyReturnsNull() {
        HashTable table = new HashTable();
        table.insert("REQ-0001", makeRequest("REQ-0001"));

        assertNull(table.search("REQ-9999"));
    }

    @Test
    public void testDeleteRemovesEntry() {
        HashTable table = new HashTable();
        table.insert("REQ-0001", makeRequest("REQ-0001"));

        boolean wasDeleted = table.delete("REQ-0001");

        assertTrue(wasDeleted);
        assertNull(table.search("REQ-0001"));
        assertEquals(0, table.getSize());
    }
}