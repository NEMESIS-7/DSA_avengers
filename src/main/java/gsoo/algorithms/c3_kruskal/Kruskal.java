package gsoo.algorithms.c3_kruskal;

import gsoo.structures.Graph;
import gsoo.structures.c3_disjoint_set.DisjointSet;

/**
 * Slot C3 (Ayim Obed Boateng) — Kruskal's MST, paired with the DisjointSet
 * above (the structure IS the mechanism the algorithm runs on, same pattern
 * as Stack<->DFS and Heap<->Dijkstra elsewhere in the project).
 *
 * Frozen contract, set by A1 (Architect). Method body is a stub for C3 to
 * implement.
 */
public class Kruskal {

    /** MST result: the edges selected, and their combined effective cost. */
    public static class Result {
        public final Graph.Edge[] mstEdges;
        public final double totalCost;

        public Result(Graph.Edge[] mstEdges, double totalCost) {
            this.mstEdges = mstEdges;
            this.totalCost = totalCost;
        }
    }

    /**
     * Runs Kruskal's algorithm over every edge in graph: sort edges by
     * effective cost (travelTimeSecs * roadConditionWeight), then walk them
     * in order, using a DisjointSet (elements = graph.getAllNodeIds()) to
     * reject any edge that would form a cycle.
     */
    public Result run(Graph graph) {
        throw new UnsupportedOperationException("C3: implement me");
    }
}
