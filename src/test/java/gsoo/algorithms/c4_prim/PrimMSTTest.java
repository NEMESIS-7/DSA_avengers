package gsoo.algorithms.c4_prim;

import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrimMSTTest {

    
    @Test
    void normalCase_sixNodeGraph_findsCorrectMST() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("A", "community");
        graph.addNode("B", "community");
        graph.addNode("C", "community");
        graph.addNode("D", "community");
        graph.addNode("E", "community");
        graph.addNode("F", "community");

        graph.addEdge("A", "B", 100, 4, 1.0, false);
        graph.addEdge("A", "C", 100, 2, 1.0, false);
        graph.addEdge("B", "D", 100, 5, 1.0, false);
        graph.addEdge("C", "D", 100, 3, 1.0, false);
        graph.addEdge("C", "E", 100, 6, 1.0, false);
        graph.addEdge("D", "E", 100, 1, 1.0, false);
        graph.addEdge("D", "F", 100, 7, 1.0, false);
        graph.addEdge("E", "F", 100, 2, 1.0, false);

        PrimMST.MSTResult result = new PrimMST().run(graph, "A");

        assertTrue(result.connected);
        assertEquals(12.0, result.totalCost, 0.0001);
        assertEquals(5, result.edges.length);
    }

    
    @Test
    void boundaryCase_disconnectedGraph_returnsPartialTreeAndFalse() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("A", "community");
        graph.addNode("B", "community");
        graph.addNode("C", "community");
        graph.addNode("D", "community");

        graph.addEdge("A", "B", 100, 5, 1.0, false); // component 1
        graph.addEdge("C", "D", 100, 5, 1.0, false); // component 2, unreachable from A

        PrimMST.MSTResult result = new PrimMST().run(graph, "A");

        assertFalse(result.connected);
        assertEquals(1, result.edges.length); // only reached B
    }

    // INVALID INPUT CASE: starting node that doesn't exist in the graph.
    @Test
    void invalidInput_unknownStartNode_throws() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("A", "community");

        assertThrows(IllegalArgumentException.class,
            () -> new PrimMST().run(graph, "DOES_NOT_EXIST"));
    }
}