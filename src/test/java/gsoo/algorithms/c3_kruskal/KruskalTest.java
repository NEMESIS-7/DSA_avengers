package gsoo.algorithms.c3_kruskal;

import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KruskalTest {

    /**
     * NORMAL CASE
     */
    @Test
    void shouldProduceMinimumSpanningTree() {

        AdjacencyListGraph graph =
                new AdjacencyListGraph();

        graph.addNode("A", "ward");
        graph.addNode("B", "lab");
        graph.addNode("C", "pharmacy");
        graph.addNode("D", "gateway");

        /*
         * effective cost =
         * travelTimeSecs * roadConditionWeight
         */

        // Cost = 1
        graph.addEdge(
                "A", "B",
                10,
                1,
                1.0,
                false
        );

        // Cost = 2
        graph.addEdge(
                "B", "C",
                10,
                2,
                1.0,
                false
        );

        // Cost = 5
        graph.addEdge(
                "A", "C",
                10,
                5,
                1.0,
                false
        );

        // Cost = 1.5
        graph.addEdge(
                "C", "D",
                10,
                1.5,
                1.0,
                false
        );

        // Cost = 4
        graph.addEdge(
                "B", "D",
                10,
                4,
                1.0,
                false
        );

        Kruskal kruskal =
                new Kruskal();

        Kruskal.Result result =
                kruskal.run(graph);

        /*
         * Expected MST:
         *
         * A-B = 1
         * C-D = 1.5
         * B-C = 2
         *
         * Total = 4.5
         */

        assertEquals(
                3,
                result.mstEdges.length
        );

        assertEquals(
                4.5,
                result.totalCost,
                0.0001
        );
    }

    /**
     * BOUNDARY CASE
     */
    @Test
    void singleNodeShouldHaveEmptyMst() {

        AdjacencyListGraph graph =
                new AdjacencyListGraph();

        graph.addNode(
                "AMBULANCE-BAY",
                "gateway"
        );

        Kruskal kruskal =
                new Kruskal();

        Kruskal.Result result =
                kruskal.run(graph);

        assertEquals(
                0,
                result.mstEdges.length
        );

        assertEquals(
                0.0,
                result.totalCost,
                0.0001
        );
    }

    /**
     * INVALID INPUT CASE
     */
    @Test
    void nullGraphShouldThrowException() {

        Kruskal kruskal =
                new Kruskal();

        assertThrows(
                IllegalArgumentException.class,
                () -> kruskal.run(null)
        );
    }
}