package gsoo.algorithms.a4_bfs;

import gsoo.structures.Graph;
import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BFSTest {

    // NORMAL CASE: a small graph shaped like the real domain — external
    // community joining the internal hospital layer at exactly one
    // gateway node. Confirms distances, predecessors, and path
    // reconstruction are all correct.
    @Test
    void normalCase_reachabilityAndShortestPathFromGateway() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("COMM_01", "community");
        graph.addNode("CHPS_01", "chps");
        graph.addNode("GATEWAY", "gateway");
        graph.addNode("OPD", "opd");
        graph.addNode("WARD_01", "ward");

        graph.addEdge("COMM_01", "CHPS_01", 3000, 600, 1.0, false);
        graph.addEdge("CHPS_01", "GATEWAY", 5000, 900, 1.3, false);
        graph.addEdge("GATEWAY", "OPD", 80, 40, 1.0, true);
        graph.addEdge("OPD", "WARD_01", 60, 30, 1.0, false);

        BFS.Result result = BFS.run(graph, "GATEWAY");

        assertEquals(0, result.distanceTo("GATEWAY"));
        assertEquals(1, result.distanceTo("CHPS_01"));
        assertEquals(1, result.distanceTo("OPD"));
        assertEquals(2, result.distanceTo("COMM_01"));
        assertEquals(2, result.distanceTo("WARD_01"));

        assertArrayEquals(
                new String[] { "GATEWAY", "CHPS_01", "COMM_01" },
                result.pathTo("COMM_01"));
        assertArrayEquals(
                new String[] { "GATEWAY", "OPD", "WARD_01" },
                result.pathTo("WARD_01"));
    }

    // BOUNDARY CASE: an unreachable node (no edges at all) must be
    // correctly reported as such, not crash or return a false distance.
    // Also covers a single-node graph (source with no neighbors).
    @Test
    void boundaryCase_unreachableNodeAndSingleNodeGraph() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("GATEWAY", "gateway");
        graph.addNode("ISOLATED", "store"); // deliberately no edges

        BFS.Result result = BFS.run(graph, "GATEWAY");

        assertTrue(result.isReachable("GATEWAY"));
        assertEquals(0, result.distanceTo("GATEWAY"));
        assertFalse(result.isReachable("ISOLATED"));
        assertThrows(IllegalStateException.class, () -> result.pathTo("ISOLATED"));

        // Single-node graph: source visits only itself.
        AdjacencyListGraph single = new AdjacencyListGraph();
        single.addNode("ONLY", "community");
        BFS.Result singleResult = BFS.run(single, "ONLY");
        assertEquals(1, singleResult.visitOrder().length);
        assertEquals(0, singleResult.distanceTo("ONLY"));
    }

    // INVALID INPUT CASE: null graph, and a source id that isn't a node
    // in the graph, must both throw rather than NPE or silently return
    // an empty/wrong result.
    @Test
    void invalidInput_nullGraphAndUnknownSourceThrow() {
        assertThrows(IllegalArgumentException.class, () -> BFS.run(null, "GATEWAY"));

        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("GATEWAY", "gateway");
        assertThrows(IllegalArgumentException.class, () -> BFS.run(graph, "DOES_NOT_EXIST"));
        assertThrows(IllegalArgumentException.class, () -> BFS.run(graph, null));
    }
}