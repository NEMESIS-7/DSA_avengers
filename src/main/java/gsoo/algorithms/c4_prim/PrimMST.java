package gsoo.algorithms.c4_prim;

import gsoo.structures.Graph;
import gsoo.structures.Graph.Edge;


public class PrimMST {

    public static class MSTResult {
        public final Edge[] edges;
        public final double totalCost;
        public final boolean connected;

        public MSTResult(Edge[] edges, double totalCost, boolean connected) {
            this.edges = edges;
            this.totalCost = totalCost;
            this.connected = connected;
        }
    }

    private static int indexOf(String[] ids, String target) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }

    public MSTResult run(Graph graph, String startId) {
        String[] allIds = graph.getAllNodeIds();
        int n = allIds.length;

        if (n == 0) {
            return new MSTResult(new Edge[0], 0, true);
        }

        int startIndex = indexOf(allIds, startId);
        if (startIndex == -1) {
            throw new IllegalArgumentException("Unknown start node: " + startId);
        }

        boolean[] inTree = new boolean[n];
        inTree[startIndex] = true;
        int treeSize = 1;

        Edge[] mstEdges = new Edge[n - 1];
        int mstCount = 0;
        double totalCost = 0;

        while (treeSize < n) {
            Edge best = null;
            double bestCost = Double.POSITIVE_INFINITY;

            for (int i = 0; i < n; i++) {
                if (!inTree[i]) {
                    continue;
                }
                Edge[] connections = graph.getConnections(allIds[i]);
                for (int j = 0; j < connections.length; j++) {
                    Edge e = connections[j];
                    String other = e.fromId.equals(allIds[i]) ? e.toId : e.fromId;
                    int otherIndex = indexOf(allIds, other);

                    if (otherIndex == -1 || inTree[otherIndex]) {
                        continue; // both ends already in tree, would form a cycle
                    }
                    double cost = e.effectiveCost();
                    if (cost < bestCost) {
                        bestCost = cost;
                        best = e;
                    }
                }
            }

            if (best == null) {
                // remaining nodes aren't reachable - graph is disconnected
                break;
            }

            int uIndex = indexOf(allIds, best.fromId);
            int vIndex = indexOf(allIds, best.toId);
            int newIndex = inTree[uIndex] ? vIndex : uIndex;

            inTree[newIndex] = true;
            treeSize++;
            mstEdges[mstCount] = best;
            mstCount++;
            totalCost += bestCost;
        }

        Edge[] result = new Edge[mstCount];
        for (int i = 0; i < mstCount; i++) {
            result[i] = mstEdges[i];
        }

        boolean connected = (treeSize == n);
        return new MSTResult(result, totalCost, connected);
    }
}