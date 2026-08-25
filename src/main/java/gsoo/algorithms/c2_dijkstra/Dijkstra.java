package gsoo.algorithms.c2_dijkstra;

import gsoo.app.Config;
import gsoo.structures.Graph;
import gsoo.structures.c2_heap.Heap;

/**
 * Dijkstra's single-source shortest paths over the frozen {@link Graph}
 * interface (the C4/C5 graph API). Edge weights are
 * Config.effectiveEdgeCost(travelTimeSecs, roadConditionWeight) — travel time
 * degraded by the road condition weight, plus the index-derived ROUTE_PENALTY
 * on flood-prone roads — so a "fastest route under weighted, degraded roads"
 * is exactly what this returns.
 *
 * The frontier is a {@link Heap} (C2's own structure, no java.util.PriorityQueue):
 * each node index is inserted with priority = current best distance, and a
 * decreaseKey lowers it whenever a better path is found. A finalized node may
 * still sit in the heap as a stale entry, so extraction is followed by a
 * finalized[] skip — the standard lazy-deletion check.
 *
 * O((V + E) log V) with the heap; the linear-scan decreaseKey in Heap makes
 * the practical constant worse than the theoretical bound, which is fine at
 * this dataset's size and is called out in the report.
 */
public final class Dijkstra {

    public static final double INFINITY = Double.POSITIVE_INFINITY;

    public static final class Result {
        public final String source;
        public final String[] nodeIds;      // index -> node id (same order as graph.getAllNodeIds())
        public final double[] distance;     // index -> shortest distance from source
        public final int[] previous;        // index -> previous node index on the path, -1 for source/unreachable
        public final int[] finalizeOrder;   // node indices in the order Dijkstra finalized them (trace evidence)

        Result(String source, String[] nodeIds, double[] distance, int[] previous, int[] finalizeOrder) {
            this.source = source;
            this.nodeIds = nodeIds;
            this.distance = distance;
            this.previous = previous;
            this.finalizeOrder = finalizeOrder;
        }

        public boolean isReachable(String target) {
            int t = indexOf(nodeIds, target);
            return t != -1 && distance[t] != INFINITY;
        }

        /**
         * Reconstructs the shortest path source -> target by walking the
         * predecessor chain backwards. Empty array if target is the source
         * or unreachable.
         */
        public String[] pathTo(String target) {
            int t = indexOf(nodeIds, target);
            if (t == -1 || distance[t] == INFINITY) {
                return new String[0];
            }
            String[] reversed = new String[nodeIds.length];
            int count = 0;
            for (int at = t; at != -1; at = previous[at]) {
                reversed[count++] = nodeIds[at];
            }
            String[] path = new String[count];
            for (int i = 0; i < count; i++) {
                path[i] = reversed[count - 1 - i];
            }
            return path;
        }
    }

    private Dijkstra() {
    }

    public static Result shortestPaths(Graph graph, String source) {
        return run(graph, source, null);
    }

    /** Same algorithm; appends a human-readable step trace for evidence. */
    public static Result shortestPathsWithTrace(Graph graph, String source, StringBuilder trace) {
        return run(graph, source, trace);
    }

    private static Result run(Graph graph, String source, StringBuilder trace) {
        if (graph == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }
        String[] nodeIds = graph.getAllNodeIds();
        if (nodeIds.length == 0) {
            throw new IllegalArgumentException("graph has no nodes");
        }
        int sourceIndex = indexOf(nodeIds, source);
        if (sourceIndex == -1) {
            throw new IllegalArgumentException("Unknown source node: " + source);
        }

        int n = nodeIds.length;
        double[] distance = new double[n];
        int[] previous = new int[n];
        boolean[] finalized = new boolean[n];
        int[] finalizeOrder = new int[n];
        int orderCount = 0;

        for (int i = 0; i < n; i++) {
            distance[i] = INFINITY;
            previous[i] = -1;
        }
        distance[sourceIndex] = 0;

        Heap<Integer> frontier = new Heap<>();
        frontier.insert(sourceIndex, 0);

        if (trace != null) {
            trace.append("Dijkstra single-source shortest paths from ").append(source)
                    .append(" (").append(n).append(" nodes)\n");
        }

        while (!frontier.isEmpty()) {
            int u = frontier.extractMin();
            if (finalized[u]) {
                continue; // stale entry from a previous decreaseKey
            }
            finalized[u] = true;
            finalizeOrder[orderCount++] = u;

            Graph.Edge[] neighbors = graph.getNeighbors(nodeIds[u]);
            int improved = 0;
            for (Graph.Edge e : neighbors) {
                String otherId = e.fromId.equals(nodeIds[u]) ? e.toId : e.fromId;
                int v = indexOf(nodeIds, otherId);
                if (v == -1 || finalized[v]) {
                    continue;
                }
                double weight = Config.effectiveEdgeCost(e.travelTimeSecs, e.roadConditionWeight);
                double candidate = distance[u] + weight;
                if (candidate < distance[v]) {
                    boolean wasInfinite = distance[v] == INFINITY;
                    distance[v] = candidate;
                    previous[v] = u;
                    if (wasInfinite) {
                        frontier.insert(v, candidate);
                    } else {
                        frontier.decreaseKey(v, candidate);
                    }
                    improved++;
                }
            }

            if (trace != null) {
                trace.append("finalize ").append(nodeIds[u])
                        .append(" | dist=").append(format(distance[u]))
                        .append(" | pred=").append(previous[u] == -1 ? "-" : nodeIds[previous[u]])
                        .append(" | relaxations=").append(neighbors.length)
                        .append(" improved=").append(improved).append("\n");
            }
        }

        return new Result(source, nodeIds, distance, previous, finalizeOrder);
    }

    private static int indexOf(String[] arr, String target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }

    private static String format(double d) {
        if (d == INFINITY) {
            return "inf";
        }
        return String.format("%.1f", d);
    }
}