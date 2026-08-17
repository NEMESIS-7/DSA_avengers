package gsoo.algorithms.a4_bfs;

import gsoo.structures.Graph;
import gsoo.structures.Graph.Edge;
import gsoo.structures.a4_queue_circular_queue.DynamicQueue;

public class BFSTraceDemo {

    public static void main(String[] args) {
        Graph graph = RealDatasetLoader.load();

        String source = "GATE-01"; // Ambulance Bay — the single external/internal join node
        System.out.println("BFS trace from " + source + " (Ambulance Bay)");
        System.out.println("=".repeat(70));

        BFS.Result result = runWithTrace(graph, source);

        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("Summary: " + result.visitOrder().length + " of "
                + graph.nodeCount() + " locations reached from " + source);

        int unreachable = graph.nodeCount() - result.visitOrder().length;
        if (unreachable > 0) {
            System.out.println(unreachable + " location(s) unreachable:");
            for (String id : graph.getAllNodeIds()) {
                if (!result.isReachable(id)) {
                    System.out.println("  " + id);
                }
            }
        }
    }

    /**
     * Same algorithm as BFS.run(), but prints a trace row for every step
     * so the traversal can be captured as evidence. Kept separate from
     * BFS.run() itself so the production algorithm stays free of
     * System.out calls.
     */
    private static BFS.Result runWithTrace(Graph graph, String sourceId) {
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

        System.out.printf("%-4s %-10s %-45s %-30s%n", "Step", "Dequeued", "Newly visited (distance)", "Frontier after");
        System.out.println("-".repeat(95));

        int step = 0;
        while (!frontier.isEmpty()) {
            step++;
            String current = frontier.dequeue();
            int currentIndex = indexOf(nodeIds, current);
            visitOrder[visitCount] = current;
            visitCount++;

            StringBuilder newlyVisited = new StringBuilder();
            Edge[] edges = graph.getNeighbors(current);
            for (Edge e : edges) {
                String neighbor = e.fromId.equals(current) ? e.toId : e.fromId;
                int neighborIndex = indexOf(nodeIds, neighbor);
                if (!visited[neighborIndex]) {
                    visited[neighborIndex] = true;
                    distance[neighborIndex] = distance[currentIndex] + 1;
                    predecessor[neighborIndex] = current;
                    frontier.enqueue(neighbor);
                    if (newlyVisited.length() > 0) newlyVisited.append(", ");
                    newlyVisited.append(neighbor).append("(d=").append(distance[neighborIndex]).append(")");
                }
            }

            System.out.printf("%-4d %-10s %-45s %-30s%n",
                    step, current,
                    newlyVisited.length() > 0 ? newlyVisited.toString() : "(none new)",
                    frontierContents(frontier));
        }

        String[] trimmedVisitOrder = new String[visitCount];
        System.arraycopy(visitOrder, 0, trimmedVisitOrder, 0, visitCount);

        return new BFS.Result(nodeIds, distance, predecessor, trimmedVisitOrder);
    }

    // Reads the DynamicQueue's contents without mutating it, purely for trace printing.
    private static String frontierContents(DynamicQueue<String> frontier) {
        DynamicQueue<String> copy = new DynamicQueue<>();
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        while (!frontier.isEmpty()) {
            String item = frontier.dequeue();
            if (!first) sb.append(", ");
            sb.append(item);
            first = false;
            copy.enqueue(item);
        }
        while (!copy.isEmpty()) {
            frontier.enqueue(copy.dequeue());
        }
        sb.append("]");
        return sb.toString();
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