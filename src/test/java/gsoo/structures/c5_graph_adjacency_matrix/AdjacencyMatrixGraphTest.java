package gsoo.structures.c5_graph_adjacency_matrix;

import gsoo.structures.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjacencyMatrixGraphTest {

    /*
     * NORMAL CASE
     * Checks ordinary nodes, directed edges,
     * undirected edges and closed roads.
     */
    @Test
    void normalCase_addNodesAndEdges_worksCorrectly() {
        AdjacencyMatrixGraph graph =
            new AdjacencyMatrixGraph();

        graph.addNode("WARD_A", "ward");
        graph.addNode("WARD_B", "ward");
        graph.addNode("LAB", "laboratory");

        // directed = false, isClosed = false
        graph.addEdge(
            "WARD_A",
            "WARD_B",
            100,
            30,
            1.0,
            false,
            false
        );

        // directed = true, isClosed = true
        graph.addEdge(
            "WARD_B",
            "LAB",
            50,
            15,
            1.2,
            true,
            true
        );

        assertEquals(3, graph.nodeCount());
        assertEquals(2, graph.edgeCount());

        assertTrue(graph.hasNode("WARD_A"));
        assertEquals("laboratory", graph.getNodeType("LAB"));

        // Undirected edge works in both directions
        assertTrue(graph.hasEdge("WARD_A", "WARD_B"));
        assertTrue(graph.hasEdge("WARD_B", "WARD_A"));

        // Directed edge works only in its given direction
        assertTrue(graph.hasEdge("WARD_B", "LAB"));
        assertFalse(graph.hasEdge("LAB", "WARD_B"));

        assertEquals(
            1,
            graph.getNeighbors("WARD_A").length
        );

        assertEquals(
            2,
            graph.getNeighbors("WARD_B").length
        );

        assertEquals(
            0,
            graph.getNeighbors("LAB").length
        );

        // LAB still has one physical incoming connection
        assertEquals(
            1,
            graph.getConnections("LAB").length
        );

        Graph.Edge[] edges = graph.getAllEdges();

        assertEquals(2, edges.length);

        Graph.Edge closedEdge =
            findEdge(edges, "WARD_B", "LAB");

        assertNotNull(closedEdge);
        assertTrue(closedEdge.directed);
        assertTrue(closedEdge.isClosed);
    }

    /*
     * BOUNDARY CASE
     * The starting capacity is 16.
     * Adding 17 nodes proves that resizing works.
     */
    @Test
    void boundaryCase_moreThanInitialCapacity_resizesCorrectly() {
        AdjacencyMatrixGraph graph =
            new AdjacencyMatrixGraph();

        for (int i = 0; i < 17; i++) {
            graph.addNode("NODE_" + i, "room");
        }

        assertEquals(17, graph.nodeCount());

        assertTrue(graph.hasNode("NODE_0"));
        assertTrue(graph.hasNode("NODE_16"));

        graph.addEdge(
            "NODE_0",
            "NODE_16",
            20,
            10,
            1.0,
            false,
            false
        );

        assertTrue(
            graph.hasEdge("NODE_0", "NODE_16")
        );

        assertTrue(
            graph.hasEdge("NODE_16", "NODE_0")
        );

        // This node has no edges
        assertEquals(
            0,
            graph.getNeighbors("NODE_8").length
        );
    }

    /*
     * INVALID INPUT
     * Checks that incorrect operations are rejected.
     */
    @Test
    void invalidInput_throwsIllegalArgumentException() {
        AdjacencyMatrixGraph graph =
            new AdjacencyMatrixGraph();

        graph.addNode("A", "ward");
        graph.addNode("B", "laboratory");

        assertThrows(
            IllegalArgumentException.class,
            () -> graph.addNode("A", "ward")
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> graph.addNode("", "ward")
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> graph.addNode(null, "ward")
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> graph.addEdge(
                "A",
                "UNKNOWN",
                20,
                10,
                1.0,
                false,
                false
            )
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> graph.addEdge(
                "A",
                "B",
                0,
                10,
                1.0,
                false,
                false
            )
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> graph.addEdge(
                "A",
                "B",
                20,
                Double.NaN,
                1.0,
                false,
                false
            )
        );

        graph.addEdge(
            "A",
            "B",
            20,
            10,
            1.0,
            false,
            false
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> graph.addEdge(
                "A",
                "B",
                20,
                10,
                1.0,
                false,
                false
            )
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> graph.getNeighbors("UNKNOWN")
        );
    }

    /*
     * Searches an ordinary array manually.
     * No ArrayList, HashMap or other collection is used.
     */
    private Graph.Edge findEdge(
        Graph.Edge[] edges,
        String fromId,
        String toId
    ) {
        for (int i = 0; i < edges.length; i++) {
            Graph.Edge edge = edges[i];

            if (
                edge.fromId.equals(fromId)
                    && edge.toId.equals(toId)
            ) {
                return edge;
            }
        }

        return null;
    }
}