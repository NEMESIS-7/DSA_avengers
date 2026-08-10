package gsoo.structures;


public interface Graph {

    void addNode(String id, String type);

    void addEdge(String fromId, String toId,
                 double distanceMetres, double travelTimeSecs,
                 double roadConditionWeight, boolean directed);

    boolean hasNode(String id);

    boolean hasEdge(String fromId, String toId);

    String getNodeType(String id);

    int nodeCount();

    int edgeCount();

    String[] getAllNodeIds();

    Edge[] getAllEdges();

    
    Edge[] getNeighbors(String nodeId);

    Edge[] getConnections(String nodeId);

    final class Edge {
        public final String fromId;
        public final String toId;
        public final double distanceMetres;
        public final double travelTimeSecs;
        public final double roadConditionWeight;
        public final boolean directed;

        public Edge(String fromId, String toId,
                     double distanceMetres, double travelTimeSecs,
                     double roadConditionWeight, boolean directed) {
            this.fromId = fromId;
            this.toId = toId;
            this.distanceMetres = distanceMetres;
            this.travelTimeSecs = travelTimeSecs;
            this.roadConditionWeight = roadConditionWeight;
            this.directed = directed;
        }
    }
}