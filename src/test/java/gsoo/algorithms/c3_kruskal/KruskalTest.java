package gsoo.algorithms.c3_kruskal;

import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;

import gsoo.structures.Graph;

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

    @Test
    void shouldGenerateKruskalTraceEvidence() {

        AdjacencyListGraph graph =
                new AdjacencyListGraph();

        graph.addNode("AMBULANCE-BAY", "gateway");
        graph.addNode("OPD", "department");
        graph.addNode("LAB", "department");
        graph.addNode("PHARMACY", "department");

        // Effective cost = travelTimeSecs * roadConditionWeight

        graph.addEdge(
                "AMBULANCE-BAY", "OPD",
                50,
                10,
                1.0,
                false
        ); // cost = 10

        graph.addEdge(
                "OPD", "LAB",
                40,
                8,
                1.0,
                false
        ); // cost = 8

        graph.addEdge(
                "LAB", "PHARMACY",
                30,
                5,
                1.0,
                false
        ); // cost = 5

        graph.addEdge(
                "AMBULANCE-BAY", "LAB",
                70,
                15,
                1.0,
                false
        ); // cost = 15

        graph.addEdge(
                "OPD", "PHARMACY",
                60,
                12,
                1.0,
                false
        ); // cost = 12

        Kruskal kruskal =
                new Kruskal();

        Kruskal.Result result =
                kruskal.run(graph);

        StringBuilder trace =
                new StringBuilder();

        trace.append("\n=== C3 KRUSKAL MST TRACE ===\n");
        trace.append("Step | From | To | Effective Cost\n");
        trace.append("--------------------------------\n");

        for (int i = 0; i < result.mstEdges.length; i++) {

            Graph.Edge edge =
                    result.mstEdges[i];

            double cost =
                    edge.travelTimeSecs
                            * edge.roadConditionWeight;

            trace.append(i + 1)
                    .append(" | ")
                    .append(edge.fromId)
                    .append(" | ")
                    .append(edge.toId)
                    .append(" | ")
                    .append(cost)
                    .append("\n");
        }

        trace.append("--------------------------------\n");

        trace.append("Total MST Cost: ")
                .append(result.totalCost)
                .append("\n");

        trace.append("Edges Selected: ")
                .append(result.mstEdges.length)
                .append("\n");

        System.out.println(trace);

        assertEquals(
                graph.nodeCount() - 1,
                result.mstEdges.length
        );
    }
}