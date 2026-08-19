package gsoo.algorithms.a3_dfs;

import gsoo.structures.Graph;
import gsoo.structures.Graph.Edge;
import gsoo.structures.a3_stack.Stack;

// ================================================================
// A3 — DFS: reachable departments when a corridor closes
//
// Two things can make a corridor "closed" here:
//   1. The road's own is_closed flag from the real data (schema.sql)
//   2. An extra corridor we're simulating as closed just for this
//      run, without needing a removeEdge() on the Graph interface
//      at all — we just skip that one edge while walking neighbors.
//
// Uses our own Stack, same one from the audit trail. Same underlying
// idea both places: "last placed, first removed." For undo it meant
// reversing the most recent action first. Here it means backtracking
// out of a dead end by going back to wherever we most recently were,
// not by picking some arbitrary earlier point.
// ================================================================

public class DFS {

    public static class ReachabilityResult {
        public final String[] reachableIds;
        public final int visitedCount;

        public ReachabilityResult(String[] reachableIds, int visitedCount) {
            this.reachableIds = reachableIds;
            this.visitedCount = visitedCount;
        }
    }

    public ReachabilityResult reachableFrom(Graph graph, String startId) {
        return reachableFrom(graph, startId, null, null);
    }

    // extraClosedFromId/extraClosedToId simulate one additional corridor
    // closing, on top of whatever's already marked closed in the real data.
    // Pass null for both if you're not simulating anything extra.
    public ReachabilityResult reachableFrom(Graph graph, String startId,
                                             String extraClosedFromId, String extraClosedToId) {
        if (!graph.hasNode(startId)) {
            throw new IllegalArgumentException("Unknown start node: " + startId);
        }

        String[] allIds = graph.getAllNodeIds();
        boolean[] visited = new boolean[allIds.length];
        String[] visitOrder = new String[allIds.length];
        int visitedCount = 0;

        Stack<String> stack = new Stack<>();
        stack.push(startId);

        while (!stack.isEmpty()) {
            String current = stack.pop();
            int currentIndex = indexOf(allIds, current);

            if (visited[currentIndex]) {
                continue;   // got here already through a different path, skip
            }
            visited[currentIndex] = true;
            visitOrder[visitedCount] = current;
            visitedCount++;

            Edge[] neighbors = graph.getNeighbors(current);
            for (int i = 0; i < neighbors.length; i++) {
                Edge edge = neighbors[i];

                if (edge.isClosed) {
                    continue;   // closed in the real data, can't pass through
                }
                if (isSimulatedClosed(edge, extraClosedFromId, extraClosedToId)) {
                    continue;   // closed just for this run
                }

                int neighborIndex = indexOf(allIds, otherEndpoint(edge, current));
                if (!visited[neighborIndex]) {
                    stack.push(otherEndpoint(edge, current));
                }
            }
        }

        String[] reachable = new String[visitedCount];
        for (int i = 0; i < visitedCount; i++) {
            reachable[i] = visitOrder[i];
        }
        return new ReachabilityResult(reachable, visitedCount);
    }

    private boolean isSimulatedClosed(Edge edge, String fromId, String toId) {
        if (fromId == null || toId == null) {
            return false;
        }
        return edge.fromId.equals(fromId) && edge.toId.equals(toId);
    }

    // For an undirected edge, the same Edge object is stored on both ends, so
    // edge.toId isn't reliably "the neighbor" — it depends on which direction
    // the edge was originally added in, not which direction we're traversing
    // it now. This resolves the actual other endpoint regardless of that.
    private String otherEndpoint(Edge edge, String current) {
        return edge.fromId.equals(current) ? edge.toId : edge.fromId;
    }

    private int indexOf(String[] ids, String target) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}
