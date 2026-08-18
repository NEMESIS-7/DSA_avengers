package gsoo.structures;

// Shared graph contract, both c4 (adjacency list) and c5 (adjacency matrix)
// implement this. DFS/BFS/Dijkstra/Kruskal/Prim all build against it.
// No java.util collections used anywhere, per team decision.
public interface Graph {

    void addNode(String id, String type);

    void addEdge(String fromId, String toId,
                 double distanceMetres, double travelTimeSecs,
                 double roadConditionWeight, boolean directed, boolean isClosed);

    boolean hasNode(String id);

    boolean hasEdge(String fromId, String toId);

    String getNodeType(String id);

    int nodeCount();

    int edgeCount();

    String[] getAllNodeIds();

    Edge[] getAllEdges();

    
    Edge[] getNeighbors(String nodeId);

    // All physical connections, ignoring direction - for Prim/Kruskal (MST)
    Edge[] getConnections(String nodeId);

    final class Edge {
        public final String fromId;
        public final String toId;
        public final double distanceMetres;
        public final double travelTimeSecs;
        public final double roadConditionWeight;
        public final boolean directed;
        public final boolean isClosed;

        public Edge(String fromId, String toId,
                     double distanceMetres, double travelTimeSecs,
                     double roadConditionWeight, boolean directed, boolean isClosed) {
            this.fromId = fromId;
            this.toId = toId;
            this.distanceMetres = distanceMetres;
            this.travelTimeSecs = travelTimeSecs;
            this.roadConditionWeight = roadConditionWeight;
            this.directed = directed;
            this.isClosed = isClosed;
        }

        /**
         * The single project-wide traversal cost formula (team-charter.md §2.4):
         * effective cost = travelTimeSecs x roadConditionWeight. Every algorithm
         * that weighs edges (Dijkstra, Kruskal, Prim, ...) should call this
         * instead of recomputing the formula itself, so it's never computed two
         * different ways in two different classes.
         */
        public double effectiveCost() {
            return travelTimeSecs * roadConditionWeight;
        }
    }
}