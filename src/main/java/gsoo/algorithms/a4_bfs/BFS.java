package gsoo.algorithms.a4_bfs;

import gsoo.structures.a4_queue_circular_queue.DynamicQueue;
import gsoo.structures.c5_graph_adjacency_matrix.Graph;

public class BFS {

    public static class Result {
        private final String[] nodeIds;
        private final int[] distance;
        private final String[] predecessor;
        private final String[] visitOrder;

        Result(String[] nodeIds, int[] distance, String[] predecessor, String[] visitOrder) {
            this.nodeIds = nodeIds;
            this.distance = distance;
            this.predecessor = predecessor;
            this.visitOrder = visitOrder;
        }

        private int indexOf(String id) {
            for (int i = 0; i < nodeIds.length; i++) {
                if (nodeIds[i].equals(id)) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Unknown node id: " + id);
        }

        public boolean isReachable(String id) {
            return distance[indexOf(id)] != -1;
        }

        public int distanceTo(String id) {
            return distance[indexOf(id)];
        }

        public String predecessorOf(String id) {
            return predecessor[indexOf(id)];
        }

        public String[] visitOrder() {
            String[] copy = new String[visitOrder.length];
            System.arraycopy(visitOrder, 0, copy, 0, visitOrder.length);
            return copy;
        }

        public String[] pathTo(String id) {
            if (!isReachable(id)) {
                throw new IllegalStateException(
                        "No path: " + id + " is not reachable from the BFS source");
            }
            String[] reversed = new String[nodeIds.length];
            int count = 0;
            String current = id;
            while (current != null) {
                reversed[count] = current;
                count++;
                current = predecessorOf(current);
            }
            String[] path = new String[count];
            for (int i = 0; i < count; i++) {
                path[i] = reversed[count - 1 - i];
            }
            return path;
        }
    }

    private BFS() { }

    public static Result run(Graph graph, String sourceId) {
        if (graph == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }
        if (sourceId == null || !graph.hasNode(sourceId)) {
            throw new IllegalArgumentException("Unknown source node id: " + sourceId);
        }

        String[] nodeIds = graph.getAllNodeIds();
        int n = nodeIds.length;

        boolean[] visited = new boolean[n];
        int[] distance = new int[n];
        String[] predecessor = new String[n];
        for (int i = 0; i < n; i++) {
            distance[i] = -1;
        }

        String[] visitOrder = new String[n];
        int visitCount = 0;

        DynamicQueue<String> frontier = new DynamicQueue<>();

        int sourceIndex = indexOf(nodeIds, sourceId);
        visited[sourceIndex] = true;
        distance[sourceIndex] = 0;
        frontier.enqueue(sourceId);

        while (!frontier.isEmpty()) {
            String current = frontier.dequeue();
            int currentIndex = indexOf(nodeIds, current);
            visitOrder[visitCount] = current;
            visitCount++;

            Graph.Edge[] edges = graph.getNeighbors(current);
            for (int i = 0; i < edges.length; i++) {
                Graph.Edge e = edges[i];
                String neighbor = e.fromId.equals(current) ? e.toId : e.fromId;
                int neighborIndex = indexOf(nodeIds, neighbor);
                if (!visited[neighborIndex]) {
                    visited[neighborIndex] = true;
                    distance[neighborIndex] = distance[currentIndex] + 1;
                    predecessor[neighborIndex] = current;
                    frontier.enqueue(neighbor);
                }
            }
        }

        String[] trimmedVisitOrder = new String[visitCount];
        System.arraycopy(visitOrder, 0, trimmedVisitOrder, 0, visitCount);

        return new Result(nodeIds, distance, predecessor, trimmedVisitOrder);
    }

    private static int indexOf(String[] ids, String target) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(target)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unknown node id: " + target);
    }
}
