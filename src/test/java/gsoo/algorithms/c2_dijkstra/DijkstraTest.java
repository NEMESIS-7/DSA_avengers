package gsoo.algorithms.c2_dijkstra;

import gsoo.structures.Graph;
import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;
import org.junit.jupiter.api.Test;

import static gsoo.algorithms.c2_dijkstra.Dijkstra.INFINITY;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DijkstraTest {

    /**
     * Small weighted graph (all edges weight 1.0 so effective cost is just
     * travelTime — no flood penalty to confuse the expectations):
     *
     * A--1--B--2--C
     * |           |
     * 5           |
     * |           |
     * D-----------3----? 
     */
    private Graph buildGraph() {
        Graph g = new AdjacencyListGraph();
        g.addNode("A", "community");
        g.addNode("B", "chps");
        g.addNode("C", "health_centre");
        g.addNode("D", "community");
        g.addNode("E", "community"); // isolated: no edges at all
        g.addEdge("A", "B", 100, 1, 1.0, false, false);
        g.addEdge("B", "C", 100, 2, 1.0, false, false);
        g.addEdge("A", "D", 100, 5, 1.0, false, false);
        g.addEdge("D", "C", 100, 3, 1.0, false, false);
        return g;
    }

    @Test
    void normal_shortestDistancesAcrossMultipleHops() {
        Dijkstra.Result r = Dijkstra.shortestPaths(buildGraph(), "A");
        assertEquals(0.0, r.distance[index(r, "A")], 1e-9);
        assertEquals(1.0, r.distance[index(r, "B")], 1e-9);
        // A->C direct is 5, but A->B->C is 1+2=3, so Dijkstra picks the multi-hop route
        assertEquals(3.0, r.distance[index(r, "C")], 1e-9);
        assertEquals(5.0, r.distance[index(r, "D")], 1e-9);
    }

    @Test
    void normal_reconstructsTheShortestPath() {
        Dijkstra.Result r = Dijkstra.shortestPaths(buildGraph(), "A");
        assertArrayEquals(new String[]{"A", "B", "C"}, r.pathTo("C"));
        assertArrayEquals(new String[]{"A", "D"}, r.pathTo("D"));
    }

    @Test
    void normal_floodProneEdgeChargesRoutePenalty() {
        Graph g = new AdjacencyListGraph();
        g.addNode("X", "community");
        g.addNode("Y", "health_centre");
        // weight 2.0 >= FLOOD_PRONE_WEIGHT_THRESHOLD: nominal cost 10*2=20,
        // then the index-derived ROUTE_PENALTY multiplier applies.
        g.addEdge("X", "Y", 100, 10, 2.0, false, false);

        Dijkstra.Result r = Dijkstra.shortestPaths(g, "X");
        double penalized = r.distance[index(r, "Y")];
        assertTrue(penalized > 20.0, "flood-prone edge should cost more than its nominal weight");
        assertEquals(20.0 * 2.6, penalized, 1e-9);
    }

    @Test
    void normal_floodPenaltyCanFlipWhichRouteWins() {
        Graph g = new AdjacencyListGraph();
        g.addNode("S", "community");
        g.addNode("T", "health_centre");
        g.addNode("M", "chps");
        // direct S->T is flood-prone and heavy; the two-hop S->M->T stays cheap
        g.addEdge("S", "T", 100, 10, 3.0, false, false);   // 10*3*2.6 = 78
        g.addEdge("S", "M", 100, 10, 1.0, false, false);   // 10
        g.addEdge("M", "T", 100, 20, 1.0, false, false);   // 20

        Dijkstra.Result r = Dijkstra.shortestPaths(g, "S");
        assertEquals(30.0, r.distance[index(r, "T")], 1e-9);
        assertArrayEquals(new String[]{"S", "M", "T"}, r.pathTo("T"));
    }

    @Test
    void boundary_singleNodeGraph() {
        Graph g = new AdjacencyListGraph();
        g.addNode("solo", "ward");
        Dijkstra.Result r = Dijkstra.shortestPaths(g, "solo");
        assertEquals(0.0, r.distance[index(r, "solo")], 1e-9);
        assertArrayEquals(new String[]{"solo"}, r.pathTo("solo"));
        assertEquals(1, r.finalizeOrder.length);
    }

    @Test
    void boundary_twoNodeSingleEdge() {
        Graph g = new AdjacencyListGraph();
        g.addNode("P", "pharmacy");
        g.addNode("Q", "ward");
        g.addEdge("P", "Q", 100, 42, 1.0, false, false);
        Dijkstra.Result r = Dijkstra.shortestPaths(g, "P");
        assertEquals(42.0, r.distance[index(r, "Q")], 1e-9);
        assertArrayEquals(new String[]{"P", "Q"}, r.pathTo("Q"));
    }

    @Test
    void invalid_unknownSourceNodeThrows() {
        Graph g = buildGraph();
        assertThrows(IllegalArgumentException.class, () -> Dijkstra.shortestPaths(g, "NOT-A-NODE"));
    }

    @Test
    void invalid_nullGraphThrows() {
        assertThrows(IllegalArgumentException.class, () -> Dijkstra.shortestPaths(null, "A"));
    }

    @Test
    void invalid_emptyGraphThrows() {
        assertThrows(IllegalArgumentException.class, () -> Dijkstra.shortestPaths(new AdjacencyListGraph(), "A"));
    }

    @Test
    void normal_closedRoadIsSkippedEntirely() {
        Graph g = new AdjacencyListGraph();
        g.addNode("P", "pharmacy");
        g.addNode("Q", "ward");
        g.addNode("R", "ward");
        g.addEdge("P", "Q", 100, 5, 1.0, false, true);   // closed — must not be used
        g.addEdge("P", "R", 100, 5, 1.0, false, false);
        g.addEdge("R", "Q", 100, 5, 1.0, false, false);

        Dijkstra.Result r = Dijkstra.shortestPaths(g, "P");
        // direct P->Q would be 5, but it's closed, so the only route is via R: 5+5=10
        assertEquals(10.0, r.distance[index(r, "Q")], 1e-9);
        assertArrayEquals(new String[]{"P", "R", "Q"}, r.pathTo("Q"));
    }

    @Test
    void invalid_disconnectedNodeIsUnreachable() {
        Dijkstra.Result r = Dijkstra.shortestPaths(buildGraph(), "A");
        int e = index(r, "E");
        assertEquals(INFINITY, r.distance[e]);
        assertFalse(r.isReachable("E"));
        assertEquals(0, r.pathTo("E").length);
    }

    private static int index(Dijkstra.Result r, String nodeId) {
        for (int i = 0; i < r.nodeIds.length; i++) {
            if (r.nodeIds[i].equals(nodeId)) {
                return i;
            }
        }
        throw new AssertionError("node not in result: " + nodeId);
    }
}