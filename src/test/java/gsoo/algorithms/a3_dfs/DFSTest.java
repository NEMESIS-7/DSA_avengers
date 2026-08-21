package gsoo.algorithms.a3_dfs;

import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DFSTest {

    @Test
    void normalCase_allNodesReachable_whenNothingIsClosed() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("A", "community");
        graph.addNode("B", "community");
        graph.addNode("C", "community");
        graph.addNode("D", "community");

        graph.addEdge("A", "B", 100, 5, 1.0, false, false);
        graph.addEdge("B", "C", 100, 5, 1.0, false, false);
        graph.addEdge("C", "D", 100, 5, 1.0, false, false);

        DFS.ReachabilityResult result = new DFS().reachableFrom(graph, "A");

        assertEquals(4, result.visitedCount);
    }

    @Test
    void normalCase_respectsClosedFlagFromRealData_butStillReachesViaAlternatePath() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("A", "community");
        graph.addNode("B", "community");
        graph.addNode("C", "community");
        graph.addNode("D", "community");

        graph.addEdge("A", "B", 100, 5, 1.0, false, false);
        graph.addEdge("B", "C", 100, 5, 1.0, false, true);   // closed in the real data
        graph.addEdge("A", "D", 100, 5, 1.0, false, false);
        graph.addEdge("D", "C", 100, 5, 1.0, false, false);  // alternate route to C

        DFS.ReachabilityResult result = new DFS().reachableFrom(graph, "A");

        // C is still reachable via A -> D -> C, even though B -> C is closed
        assertEquals(4, result.visitedCount);
    }

    @Test
    void boundaryCase_simulatingClosureOfOnlyPath_isolatesNode() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("A", "community");
        graph.addNode("B", "community");
        graph.addNode("C", "community");

        graph.addEdge("A", "B", 100, 5, 1.0, false, false);
        graph.addEdge("B", "C", 100, 5, 1.0, false, false);   // the ONLY way to reach C

        DFS.ReachabilityResult normalResult = new DFS().reachableFrom(graph, "A");
        assertEquals(3, normalResult.visitedCount);   // A, B, C all reachable normally

        // simulate B -> C closing, even though it's not marked closed in the data
        DFS.ReachabilityResult simulatedResult =
                new DFS().reachableFrom(graph, "A", "B", "C");

        assertEquals(2, simulatedResult.visitedCount);   // only A and B now, C is cut off
    }

    @Test
    void normalCase_undirectedEdgeDiscoveredFromEitherSide() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("P", "community");
        graph.addNode("Q", "community");

        // Deliberately added "backwards" — fromId is Q, toId is P — then we
        // traverse starting from P, approaching this edge from its toId side.
        graph.addEdge("Q", "P", 100, 5, 1.0, false, false);

        DFS.ReachabilityResult result = new DFS().reachableFrom(graph, "P");

        // P must still discover Q even though the edge's own fromId/toId
        // point the "wrong" way relative to this traversal direction.
        assertEquals(2, result.visitedCount);
    }

    @Test
    void invalidCase_unknownStartNodeThrows() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("A", "community");

        assertThrows(IllegalArgumentException.class,
                () -> new DFS().reachableFrom(graph, "DOES_NOT_EXIST"));
    }
}
