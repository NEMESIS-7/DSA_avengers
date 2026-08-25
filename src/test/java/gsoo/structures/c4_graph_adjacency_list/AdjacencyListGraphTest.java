package gsoo.structures.c4_graph_adjacency_list;

import gsoo.structures.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdjacencyListGraphTest {

    // NORMAL CASE: typical small graph, mix of directed and undirected edges,
    // matching the real domain (community -> gateway -> ward).
    @Test
    void normalCase_addNodesAndEdges_neighborsRespectDirection() {
        AdjacencyListGraph graph = new AdjacencyListGraph();

        graph.addNode("COMM_01", "community");
        graph.addNode("GATEWAY", "gateway");
        graph.addNode("WARD_01", "ward");

        graph.addEdge("COMM_01", "GATEWAY", 2000, 300, 1.0, false);
        graph.addEdge("GATEWAY", "WARD_01", 50, 20, 1.2, true);

        assertEquals(3, graph.nodeCount());
        assertEquals(2, graph.edgeCount());

        assertEquals(2, graph.getNeighbors("GATEWAY").length);
        assertEquals(0, graph.getNeighbors("WARD_01").length);
        assertEquals(1, graph.getConnections("WARD_01").length);
    }

    // BOUNDARY CASE: a node with zero edges. Nothing should break, and
    // both neighbor methods should return empty (not null) arrays.
    @Test
    void boundaryCase_isolatedNodeWithNoEdges() {
        AdjacencyListGraph graph = new AdjacencyListGraph();

        graph.addNode("LONELY", "community");

        assertEquals(1, graph.nodeCount());
        assertEquals(0, graph.edgeCount());

        Graph.Edge[] neighbors = graph.getNeighbors("LONELY");
        Graph.Edge[] connections = graph.getConnections("LONELY");

        assertNotNull(neighbors);
        assertNotNull(connections);
        assertEquals(0, neighbors.length);
        assertEquals(0, connections.length);
    }

    // INVALID INPUT CASE: operations that should throw rather than silently
    // fail or corrupt state.
    @Test
    void invalidInput_throwsOnBadOperations() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("A", "community");

        assertThrows(IllegalArgumentException.class, () -> graph.addNode("A", "community"));

        assertThrows(IllegalArgumentException.class,
            () -> graph.addEdge("A", "DOES_NOT_EXIST", 100, 50, 1.0, false));

        assertThrows(IllegalArgumentException.class, () -> graph.getNeighbors("NOPE"));
    }
}