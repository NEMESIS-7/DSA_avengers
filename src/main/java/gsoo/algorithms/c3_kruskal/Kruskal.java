package gsoo.algorithms.c3_kruskal;

import gsoo.structures.Graph;
import gsoo.structures.c3_disjoint_set.DisjointSet;

/**
 * Slot C3 (Ayim Obed Boateng) — Kruskal's Minimum Spanning Tree algorithm.
 *
 * Uses the C3 DisjointSet to detect and reject cycles.
 */
public class Kruskal {

    /**
     * MST result:
     * selected edges and their combined effective cost.
     */
    public static class Result {

        public final Graph.Edge[] mstEdges;
        public final double totalCost;

        public Result(Graph.Edge[] mstEdges, double totalCost) {
            this.mstEdges = mstEdges;
            this.totalCost = totalCost;
        }
    }

    /**
     * Runs Kruskal's algorithm.
     *
     * Effective edge cost =
     * travelTimeSecs * roadConditionWeight
     */
    public Result run(Graph graph) {

        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }

        String[] nodeIds = graph.getAllNodeIds();
        Graph.Edge[] graphEdges = graph.getAllEdges();

        DisjointSet disjointSet = new DisjointSet();

        // Initially every graph node is its own set.
        for (int i = 0; i < nodeIds.length; i++) {
            disjointSet.makeSet(nodeIds[i]);
        }

        /*
         * Copy edges before sorting so we do not depend on
         * whether the Graph implementation returns an internal array.
         */
        Graph.Edge[] edges = new Graph.Edge[graphEdges.length];

        for (int i = 0; i < graphEdges.length; i++) {
            edges[i] = graphEdges[i];
        }

        // Sort from smallest effective cost to largest.
        sortByEffectiveCost(edges);

        // An MST of V vertices contains at most V - 1 edges.
        int maximumMstEdges = 0;

        if (nodeIds.length > 0) {
            maximumMstEdges = nodeIds.length - 1;
        }

        Graph.Edge[] selected =
                new Graph.Edge[maximumMstEdges];

        int selectedCount = 0;

        double totalCost = 0.0;

        // Examine edges from cheapest to most expensive.
        for (int i = 0; i < edges.length; i++) {

            Graph.Edge edge = edges[i];

            /*
             * If the endpoints are already connected,
             * adding this edge would create a cycle.
             */
            if (!disjointSet.connected(
                    edge.fromId,
                    edge.toId)) {

                selected[selectedCount] = edge;
                selectedCount++;

                disjointSet.union(
                        edge.fromId,
                        edge.toId);

                totalCost += edge.effectiveCost();

                /*
                 * Once we have V - 1 edges,
                 * a connected graph's MST is complete.
                 */
                if (selectedCount == maximumMstEdges) {
                    break;
                }
            }
        }

        /*
         * Trim the result array.
         *
         * This also handles disconnected graphs, where fewer
         * than V - 1 edges may be selected.
         */
        Graph.Edge[] resultEdges =
                new Graph.Edge[selectedCount];

        for (int i = 0; i < selectedCount; i++) {
            resultEdges[i] = selected[i];
        }

        return new Result(
                resultEdges,
                totalCost
        );
    }

    /**
     * Custom insertion sort.
     *
     * Avoids relying on built-in collection sorting for
     * assessed algorithmic logic.
     */
    private void sortByEffectiveCost(Graph.Edge[] edges) {

        for (int i = 1; i < edges.length; i++) {

            Graph.Edge current = edges[i];

            double currentCost =
                    current.effectiveCost();

            int j = i - 1;

            while (j >= 0
                    && edges[j].effectiveCost()
                    > currentCost) {

                edges[j + 1] = edges[j];

                j--;
            }

            edges[j + 1] = current;
        }
    }
}