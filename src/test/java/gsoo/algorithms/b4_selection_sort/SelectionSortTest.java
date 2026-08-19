package gsoo.algorithms.b4_selection_sort;

import gsoo.db.ServiceRequest;
import java.sql.Timestamp;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SelectionSortTest {

    private ServiceRequest makeRequest(String id) {
        return new ServiceRequest(id, "PATIENT_TRANSFER", "PAT-001",
                "WARD-3", "THEATRE-1", 4, "PENDING",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), null);
    }

    @Test
    public void testSortsUnsortedArray() {
        ServiceRequest[] requests = new ServiceRequest[] {
            makeRequest("REQ-0005"),
            makeRequest("REQ-0002"),
            makeRequest("REQ-0009"),
            makeRequest("REQ-0001")
        };

        SelectionSort.sort(requests);

        assertEquals("REQ-0001", requests[0].requestId);
        assertEquals("REQ-0002", requests[1].requestId);
        assertEquals("REQ-0005", requests[2].requestId);
        assertEquals("REQ-0009", requests[3].requestId);
    }

    @Test
    public void testEmptyArrayDoesNotError() {
        ServiceRequest[] empty = new ServiceRequest[0];

        int comparisons = SelectionSort.sort(empty);

        assertEquals(0, comparisons);
        assertEquals(0, empty.length);
    }

    @Test
    public void testSingleElementArray() {
        ServiceRequest[] single = new ServiceRequest[] { makeRequest("REQ-0001") };

        int comparisons = SelectionSort.sort(single);

        assertEquals(0, comparisons);
        assertEquals("REQ-0001", single[0].requestId);
    }

    @Test
    public void testNullArrayThrows() {
        assertThrows(NullPointerException.class, () -> {
            SelectionSort.sort(null);
        });
    }

    @Test
    public void testAlreadySortedArrayStillWorks() {
        ServiceRequest[] requests = new ServiceRequest[] {
            makeRequest("REQ-0001"),
            makeRequest("REQ-0002"),
            makeRequest("REQ-0003")
        };

        int comparisons = SelectionSort.sort(requests);

        assertEquals("REQ-0001", requests[0].requestId);
        assertEquals("REQ-0002", requests[1].requestId);
        assertEquals("REQ-0003", requests[2].requestId);
        assertEquals(3, comparisons);
    }
}