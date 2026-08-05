package gsoo.structures;

import java.util.List;

/**
 * Shared contract for graph representations in the GSOO project.
 *
 * Two implementations exist against this interface:
 *   - c4_graph_adjacency_list  (C4 — Botwe Michael)
 *   - c5_graph_adjacency_matrix (C5 — Jarawura Williams Koyiri)
 *
 * Consumers depending on this interface (per README §4):
 *   - DFS        (A3)
 *   - BFS        (A4)
 *   - Dijkstra   (C2, also uses the heap)
 *   - Kruskal    (C3, also uses disjoint set)
 *   - Prim       (C4)
 *
 * IMPORTANT — MST algorithms (Prim, Kruskal) are only defined on undirected
 * graphs. Any edge added with directed=true should be excluded from, or
 * explicitly handled by, MST construction. This is a correctness constraint,
 * not a style choice — see project notes.
 *
 * Effective edge cost (travelTime * roadConditionWeight) is intentionally
 * NOT computed here. It must be computed via gsoo.app.Config, exactly once,
 * so every algorithm agrees on cost. This interface only exposes the raw
 * fields needed for that computation.
 */
public interface Graph {

    // ---- Construction ----

    /**
     * Adds a node. Throws IllegalArgumentException if id already exists.
     * @param id unique node identifier (e.g. "COMM_04", "WARD_12", "GATEWAY")
     * @param type node type, e.g. "community", "CHPS", "health_centre",
     *             "polyclinic", "regional_hospital", "gateway",
     *             "ward", "theatre", "lab", "pharmacy", "stores", "opd"
     */
    void addNode(String id, String type);

    /**
     * Adds an edge between two existing nodes.
     * Throws IllegalArgumentException if either node doesn't exist,
     * or if the edge already exists.
     *
     * @param fromId          source node id
     * @param toId            destination node id
     * @param distanceMetres  physical distance in metres
     * @param travelTimeSecs  base travel time in seconds
     * @param roadConditionWeight traversal-cost multiplier (1.0 = good/flat,
     *             up to 2.5-3.0 for flood-prone / infection-control-restricted)
     * @param directed        false = bidirectional (default case),
     *                        true = one-way from fromId to toId only
     */
    void addEdge(String fromId, String toId,
                 double distanceMetres, double travelTimeSecs,
                 double roadConditionWeight, boolean directed);

    // ---- Queries ----

    boolean hasNode(String id);

    boolean hasEdge(String fromId, String toId);

    String getNodeType(String id);

    /** Total number of nodes currently in the graph. */
    int nodeCount();

    /** Total number of edges (each undirected edge counted once). */
    int edgeCount();

    /** All node ids currently in the graph, in insertion order. */
    List<String> getAllNodeIds();

    /**
     * All edges in the graph, each counted once (used by Kruskal, which
     * needs to sort the full edge list). Order is not guaranteed.
     */
    List<Edge> getAllEdges();

    /**
     * All edges LEGALLY TRAVERSABLE starting from nodeId — respects the
     * `directed` flag. For an undirected edge (a,b), this appears in
     * getNeighbors(a) AND getNeighbors(b). For a directed edge (a->b),
     * this appears ONLY in getNeighbors(a), never getNeighbors(b).
     *
     * Use this for: Dijkstra, DFS, BFS — anything answering
     * "where can I legally go from here?"
     *
     * Throws IllegalArgumentException if nodeId doesn't exist.
     */
    List<Edge> getNeighbors(String nodeId);

    /**
     * All edges PHYSICALLY TOUCHING nodeId, regardless of the `directed`
     * flag. A one-way road from A->B still counts as a connection for
     * getConnections(B), because the road physically exists between them.
     *
     * Use this for: Prim, Kruskal (MST) — anything answering
     * "what's connected to what, ignoring legal direction of travel?"
     * MST is undefined on directed graphs, so MST algorithms must use
     * this method, not getNeighbors().
     *
     * Throws IllegalArgumentException if nodeId doesn't exist.
     */
    List<Edge> getConnections(String nodeId);

    /**
     * Immutable value object representing one edge.
     * effectiveCost is NOT stored here — compute it via Config when needed,
     * from travelTimeSecs and roadConditionWeight, so there is exactly one
     * formula in the whole codebase.
     */
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