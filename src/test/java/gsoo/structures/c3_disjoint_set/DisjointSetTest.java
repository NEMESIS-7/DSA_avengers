package gsoo.structures.c3_disjoint_set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DisjointSetTest {

    /**
     * NORMAL CASE
     */
    @Test
    void shouldUnionAndFindConnectedLocations() {

        DisjointSet ds = new DisjointSet();

        ds.makeSet("AMBULANCE-BAY");
        ds.makeSet("OPD");
        ds.makeSet("LAB");
        ds.makeSet("PHARMACY");

        ds.union("AMBULANCE-BAY", "OPD");
        ds.union("LAB", "PHARMACY");
        ds.union("OPD", "LAB");

        assertTrue(
                ds.connected(
                        "AMBULANCE-BAY",
                        "PHARMACY"
                )
        );

        assertEquals(
                ds.find("AMBULANCE-BAY"),
                ds.find("PHARMACY")
        );

        assertEquals(
                1,
                ds.setCount()
        );
    }

    /**
     * BOUNDARY CASE
     */
    @Test
    void singleElementShouldBeItsOwnRoot() {

        DisjointSet ds = new DisjointSet();

        ds.makeSet("AMBULANCE-BAY");

        assertEquals(
                "AMBULANCE-BAY",
                ds.find("AMBULANCE-BAY")
        );

        assertEquals(
                1,
                ds.setCount()
        );
    }

    /**
     * INVALID INPUT CASE
     */
    @Test
    void unknownElementShouldThrowException() {

        DisjointSet ds = new DisjointSet();

        ds.makeSet("OPD");

        assertThrows(
                IllegalArgumentException.class,
                () -> ds.find("UNKNOWN")
        );
    }

    /**
     * Extra useful test:
     * duplicate IDs are not allowed.
     */
    @Test
    void duplicateElementShouldThrowException() {

        DisjointSet ds = new DisjointSet();

        ds.makeSet("OPD");

        assertThrows(
                IllegalArgumentException.class,
                () -> ds.makeSet("OPD")
        );
    }

    /**
     * Trace evidence hook.
     */
    @Test
    void shouldGenerateFindTrace() {

        DisjointSet ds = new DisjointSet();

        ds.makeSet("A");
        ds.makeSet("B");
        ds.makeSet("C");

        ds.union("A", "B");
        ds.union("A", "C");

        StringBuilder trace =
                new StringBuilder();

        String root =
                ds.findWithTrace(
                        "C",
                        trace
                );

        assertEquals(
                ds.find("C"),
                root
        );

        assertFalse(
                trace.isEmpty()
        );

        System.out.println(trace);
    }
}