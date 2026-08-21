package gsoo.structures.c4_graph_adjacency_list;

import gsoo.structures.Graph;

// Adjacency list graph. Node lookup is a linear scan (O(n)) since we're
// not allowed to use HashMap - could be sped up later with B4's hash table.
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

    private NodeEntry[] nodes;
    private int nodeCount;
    private int totalEdgeCount;

    public AdjacencyListGraph() {
        nodes = new NodeEntry[16];
        nodeCount = 0;
        totalEdgeCount = 0;
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
    }

    @Override
    public void addEdge(String fromId, String toId,
                         double distanceMetres, double travelTimeSecs,
                         double roadConditionWeight, boolean directed, boolean isClosed) {
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
                              roadConditionWeight, directed, isClosed);

        NodeEntry fromNode = nodes[fromIndex];
        growEdgesArrayIfNeeded(fromNode);
        fromNode.edges[fromNode.edgeCount] = edge;
        fromNode.edgeCount++;

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
    }

    @Override
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
    public String[] getAllNodeIds() {
        String[] ids = new String[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            ids[i] = nodes[i].id;
        }
        return ids;
    }

    @Override
    public Edge[] getAllEdges() {
        int count = 0;
        for (int i = 0; i < nodeCount; i++) {
            NodeEntry node = nodes[i];
            for (int j = 0; j < node.edgeCount; j++) {
                if (node.edges[j].fromId.equals(node.id)) {
                    count++;
                }
            }
        }

        Edge[] result = new Edge[count];
        int index = 0;
        for (int i = 0; i < nodeCount; i++) {
            NodeEntry node = nodes[i];
            for (int j = 0; j < node.edgeCount; j++) {
                Edge e = node.edges[j];
                if (e.fromId.equals(node.id)) {
                    result[index] = e;
                    index++;
                }
            }
        }
        return result;
    }

    @Override
    public Edge[] getNeighbors(String nodeId) {
        int index = findNodeIndex(nodeId);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown node id: " + nodeId);
        }
        NodeEntry node = nodes[index];

        int count = 0;
        for (int i = 0; i < node.edgeCount; i++) {
            Edge e = node.edges[i];
            if (!e.directed || e.fromId.equals(nodeId)) {
                count++;
            }
        }

        Edge[] result = new Edge[count];
        int pos = 0;
        for (int i = 0; i < node.edgeCount; i++) {
            Edge e = node.edges[i];
            if (!e.directed || e.fromId.equals(nodeId)) {
                result[pos] = e;
                pos++;
            }
        }
        return result;
    }

    @Override
    public Edge[] getConnections(String nodeId) {
        int index = findNodeIndex(nodeId);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown node id: " + nodeId);
        }
        NodeEntry node = nodes[index];
        Edge[] result = new Edge[node.edgeCount];
        for (int i = 0; i < node.edgeCount; i++) {
            result[i] = node.edges[i];
        }
        return result;
    }
}