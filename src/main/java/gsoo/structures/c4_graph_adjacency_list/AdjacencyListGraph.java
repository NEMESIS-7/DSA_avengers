package gsoo.structures.c4_graph_adjacency_list;

import gsoo.structures.Graph;
import java.util.ArrayList;
import java.util.List;

public class AdjacencyListGraph implements Graph {

    private static class NodeEntry {
        String id;
        String type;
        Edge[] edges;
        int edgeCount;

        NodeEntry(String id, String type) {
            this.id = id;
            this.type = type;
            this.edges = new Edge[4];
            this.edgeCount = 0;
        }
    }

    private static final int INITIAL_NODE_CAPACITY = 16;

    private NodeEntry[] nodes;
    private int nodeCount;
    private int totalEdgeCount;

    public AdjacencyListGraph() {
        this.nodes = new NodeEntry[INITIAL_NODE_CAPACITY];
        this.nodeCount = 0;
        this.totalEdgeCount = 0;
    }

    private int findNodeIndex(String id) {
        for (int i = 0; i < nodeCount; i++) {
            if (nodes[i].id.equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void growNodesArrayIfNeeded() {
        if (nodeCount == nodes.length) {
            NodeEntry[] bigger = new NodeEntry[nodes.length * 2];
            for (int i = 0; i < nodeCount; i++) {
                bigger[i] = nodes[i];
            }
            nodes = bigger;
        }
    }

    private void growEdgesArrayIfNeeded(NodeEntry node) {
        if (node.edgeCount == node.edges.length) {
            Edge[] bigger = new Edge[node.edges.length * 2];
            for (int i = 0; i < node.edgeCount; i++) {
                bigger[i] = node.edges[i];
            }
            node.edges = bigger;
        }
    }

    @Override
    public void addNode(String id, String type) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Node id cannot be null or empty");
        }
        if (findNodeIndex(id) != -1) {
            throw new IllegalArgumentException("Node already exists: " + id);
        }
        growNodesArrayIfNeeded();
        nodes[nodeCount] = new NodeEntry(id, type);
        nodeCount++;
    }@Override
    public void addEdge(String fromId, String toId,
                         double distanceMetres, double travelTimeSecs,
                         double roadConditionWeight, boolean directed) {
        int fromIndex = findNodeIndex(fromId);
        int toIndex = findNodeIndex(toId);

        if (fromIndex == -1) {
            throw new IllegalArgumentException("Unknown fromId: " + fromId);
        }
        if (toIndex == -1) {
            throw new IllegalArgumentException("Unknown toId: " + toId);
        }
        if (hasEdge(fromId, toId)) {
            throw new IllegalArgumentException(
                "Edge already exists between " + fromId + " and " + toId);
        }

        Edge edge = new Edge(fromId, toId, distanceMetres, travelTimeSecs,
                              roadConditionWeight, directed);

        // Store the SAME edge object on both endpoints, regardless of
        // `directed`. This is what makes getConnections() work correctly --
        // a one-way road A->B still shows up as a physical connection when
        // you look from B's side. getNeighbors() is the one that filters
        // by direction later, not this storage step.
        NodeEntry fromNode = nodes[fromIndex];
        growEdgesArrayIfNeeded(fromNode);
        fromNode.edges[fromNode.edgeCount] = edge;
        fromNode.edgeCount++;

        // Avoid double-storing a self-loop (fromId == toId) twice.
        if (!fromId.equals(toId)) {
            NodeEntry toNode = nodes[toIndex];
            growEdgesArrayIfNeeded(toNode);
            toNode.edges[toNode.edgeCount] = edge;
            toNode.edgeCount++;
        }

        totalEdgeCount++;
    }

    @Override
    public boolean hasEdge(String fromId, String toId) {
        int fromIndex = findNodeIndex(fromId);
        if (fromIndex == -1) {
            return false;
        }
        NodeEntry fromNode = nodes[fromIndex];
        for (int i = 0; i < fromNode.edgeCount; i++) {
            Edge e = fromNode.edges[i];
            boolean sameDirection = e.fromId.equals(fromId) && e.toId.equals(toId);
            boolean reverseOfUndirected =
                !e.directed && e.fromId.equals(toId) && e.toId.equals(fromId);
            if (sameDirection || reverseOfUndirected) {
                return true;
            }
        }
        return false;
    }@Override
    public boolean hasNode(String id) {
        return findNodeIndex(id) != -1;
    }

    @Override
    public String getNodeType(String id) {
        int index = findNodeIndex(id);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown node id: " + id);
        }
        return nodes[index].type;
    }

    @Override
    public int nodeCount() {
        return nodeCount;
    }

    @Override
    public int edgeCount() {
        return totalEdgeCount;
    }

    @Override
    public List<String> getAllNodeIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            ids.add(nodes[i].id);
        }
        return ids;
    }@Override
    public List<Edge> getAllEdges() {
        List<Edge> allEdges = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            NodeEntry node = nodes[i];
            for (int j = 0; j < node.edgeCount; j++) {
                Edge e = node.edges[j];
                // Every edge is stored on its fromNode first (see addEdge),
                // and only ALSO on its toNode if fromId != toId. So counting
                // an edge only when we're currently looking at its fromNode
                // guarantees each edge is added to the result exactly once,
                // with no extra "seen" tracking needed.
                if (e.fromId.equals(node.id)) {
                    allEdges.add(e);
                }
            }
        }
        return allEdges;
    }

    @Override
    public List<Edge> getNeighbors(String nodeId) {
        int index = findNodeIndex(nodeId);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown node id: " + nodeId);
        }
        NodeEntry node = nodes[index];
        List<Edge> legalEdges = new ArrayList<>();
        for (int i = 0; i < node.edgeCount; i++) {
            Edge e = node.edges[i];
            // Undirected edges are always legal from either endpoint.
            // Directed edges are only legal starting from their fromId.
            boolean legalFromHere = !e.directed || e.fromId.equals(nodeId);
            if (legalFromHere) {
                legalEdges.add(e);
            }
        }
        return legalEdges;
    }

    @Override
    public List<Edge> getConnections(String nodeId) {
        int index = findNodeIndex(nodeId);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown node id: " + nodeId);
        }
        NodeEntry node = nodes[index];
        List<Edge> connections = new ArrayList<>();
        for (int i = 0; i < node.edgeCount; i++) {
            connections.add(node.edges[i]);
        }
        return connections;
    }
}