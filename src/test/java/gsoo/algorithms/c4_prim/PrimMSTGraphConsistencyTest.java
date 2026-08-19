package gsoo.algorithms.c4_prim;

import gsoo.structures.Graph;
import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;
import gsoo.structures.c5_graph_adjacency_matrix.AdjacencyMatrixGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Not testing Prim's correctness again here, PrimMSTTest already does that.
// This is checking a different thing: that C4's and C5's graphs agree with
// each other. Same edges built into both, same algorithm run on both, the
// numbers coming out the other end should match exactly. If they don't,
// that means the two graphs don't actually honor the same contract, even
// though both compile against the same Graph interface.
class PrimMSTGraphConsistencyTest {

    @Test
    void normalCase_sixNodeGraph_bothGraphsAgree() {
        AdjacencyListGraph listGraph = new AdjacencyListGraph();
        AdjacencyMatrixGraph matrixGraph = new AdjacencyMatrixGraph();

        buildSixNodeGraph(listGraph);
        buildSixNodeGraph(matrixGraph);

        PrimMST.MSTResult listResult = new PrimMST().run(listGraph, "A");
        PrimMST.MSTResult matrixResult = new PrimMST().run(matrixGraph, "A");

        assertEquals(listResult.connected, matrixResult.connected);
        assertEquals(listResult.totalCost, matrixResult.totalCost, 0.0001);
        assertEquals(listResult.edges.length, matrixResult.edges.length);
    }

    @Test
    void boundaryCase_disconnectedGraph_bothGraphsAgree() {
        AdjacencyListGraph listGraph = new AdjacencyListGraph();
        AdjacencyMatrixGraph matrixGraph = new AdjacencyMatrixGraph();

        buildDisconnectedGraph(listGraph);
        buildDisconnectedGraph(matrixGraph);

        PrimMST.MSTResult listResult = new PrimMST().run(listGraph, "A");
        PrimMST.MSTResult matrixResult = new PrimMST().run(matrixGraph, "A");

        assertFalse(listResult.connected);
        assertFalse(matrixResult.connected);
        assertEquals(listResult.edges.length, matrixResult.edges.length);
    }

    // Same graph as PrimMSTTest's normal case, expected total cost 12.0 with 5 edges.
    // Kept identical on purpose so a mismatch here points at the graph, not at the test data.
    private void buildSixNodeGraph(Graph graph) {
        graph.addNode("A", "community");
        graph.addNode("B", "community");
        graph.addNode("C", "community");
        graph.addNode("D", "community");
        graph.addNode("E", "community");
        graph.addNode("F", "community");

        graph.addEdge("A", "B", 100, 4, 1.0, false, false);
        graph.addEdge("A", "C", 100, 2, 1.0, false, false);
        graph.addEdge("B", "D", 100, 5, 1.0, false, false);
        graph.addEdge("C", "D", 100, 3, 1.0, false, false);
        graph.addEdge("C", "E", 100, 6, 1.0, false, false);
        graph.addEdge("D", "E", 100, 1, 1.0, false, false);
        graph.addEdge("D", "F", 100, 7, 1.0, false, false);
        graph.addEdge("E", "F", 100, 2, 1.0, false, false);
    }

    private void buildDisconnectedGraph(Graph graph) {
        graph.addNode("A", "community");
        graph.addNode("B", "community");
        graph.addNode("C", "community");
        graph.addNode("D", "community");

        graph.addEdge("A", "B", 100, 5, 1.0, false, false);
        graph.addEdge("C", "D", 100, 5, 1.0, false, false);
    }
}
