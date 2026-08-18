package gsoo.structures.c5_graph_adjacency_matrix;

import gsoo.structures.Graph;

public class AdjacencyMatrixGraph implements Graph {

    private static final int INITIAL_CAPACITY = 16;

    private static final class NodeEntry {
        private final String id;
        private final String type;

        private NodeEntry(String id, String type) {
            this.id = id;
            this.type = type;
        }
    }

    private NodeEntry[] nodes;
    private Edge[][] matrix;
    private int nodeCount;
    private int totalEdgeCount;

    public AdjacencyMatrixGraph() {
        nodes = new NodeEntry[INITIAL_CAPACITY];
        matrix = new Edge[INITIAL_CAPACITY][INITIAL_CAPACITY];
        nodeCount = 0;
        totalEdgeCount = 0;
    }
//searches for a node manually and returns the index of the node, returns -1 if it is not found.
    private int findNodeIndex(String id) {
        if (id == null) {
            return -1;
        }

        for (int i = 0; i < nodeCount; i++) {
            if (nodes[i].id.equals(id)) {
                return i;
            }
        }

        return -1;
    }

    
    private void growIfNeeded() {
        if (nodeCount < nodes.length) {
            return;
        }

        int newCapacity = nodes.length * 2;

        NodeEntry[] largerNodes = new NodeEntry[newCapacity];
        Edge[][] largerMatrix = new Edge[newCapacity][newCapacity];

        for (int i = 0; i < nodeCount; i++) {
            largerNodes[i] = nodes[i];
        }

        for (int row = 0; row < nodeCount; row++) {
            for (int column = 0; column < nodeCount; column++) {
                largerMatrix[row][column] = matrix[row][column];
            }
        }

        nodes = largerNodes;
        matrix = largerMatrix;
    }

    private void validateNodeText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                fieldName + " cannot be null or blank"
            );
        }
    }

    private void validatePositiveNumber(
        double value,
        String fieldName
    ) {
        if (
            Double.isNaN(value)
                || Double.isInfinite(value)
                || value <= 0
        ) {
            throw new IllegalArgumentException(
                fieldName + " must be a positive finite number"
            );
        }
    }

    @Override
    public void addNode(String id, String type) {
        validateNodeText(id, "Node id");
        validateNodeText(type, "Node type");

        if (hasNode(id)) {
            throw new IllegalArgumentException(
                "Node already exists: " + id
            );
        }

        growIfNeeded();

        nodes[nodeCount] = new NodeEntry(id, type);
        nodeCount++;
    }

    @Override
public void addEdge(
    String fromId,
    String toId,
    double distanceMetres,
    double travelTimeSecs,
    double roadConditionWeight,
    boolean directed,
    boolean isClosed
) {
        int fromIndex = findNodeIndex(fromId);
        int toIndex = findNodeIndex(toId);

        if (fromIndex == -1) {
            throw new IllegalArgumentException(
                "Unknown fromId: " + fromId
            );
        }

        if (toIndex == -1) {
            throw new IllegalArgumentException(
                "Unknown toId: " + toId
            );
        }

        validatePositiveNumber(distanceMetres, "Distance");
        validatePositiveNumber(travelTimeSecs, "Travel time");
        validatePositiveNumber(
            roadConditionWeight,
            "Road condition weight"
        );

        if (matrix[fromIndex][toIndex] != null) {
            throw new IllegalArgumentException(
                "Edge already exists from "
                    + fromId
                    + " to "
                    + toId
            );
        }

        /*
         * An undirected edge occupies both directions.
         * We must not overwrite an existing reverse edge.
         */
        if (!directed && matrix[toIndex][fromIndex] != null) {
            throw new IllegalArgumentException(
                "A connection already exists between "
                    + fromId
                    + " and "
                    + toId
            );
        }

       Edge edge = new Edge(
            fromId,
            toId,
            distanceMetres,
            travelTimeSecs,
            roadConditionWeight,
            directed,
            isClosed
);

        matrix[fromIndex][toIndex] = edge;

        
        if (!directed) {
            matrix[toIndex][fromIndex] = edge;
        }

       
        totalEdgeCount++;
    }

    @Override
    public boolean hasNode(String id) {
        return findNodeIndex(id) != -1;
    }

    @Override
    public boolean hasEdge(String fromId, String toId) {
        int fromIndex = findNodeIndex(fromId);
        int toIndex = findNodeIndex(toId);

        if (fromIndex == -1 || toIndex == -1) {
            return false;
        }

        return matrix[fromIndex][toIndex] != null;
    }

    @Override
    public String getNodeType(String id) {
        int index = findNodeIndex(id);

        if (index == -1) {
            throw new IllegalArgumentException(
                "Unknown node id: " + id
            );
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
        Edge[] edges = new Edge[totalEdgeCount];
        int resultIndex = 0;

        for (int row = 0; row < nodeCount; row++) {
            for (int column = 0; column < nodeCount; column++) {
                Edge edge = matrix[row][column];

                if (edge == null) {
                    continue;
                }

                /*
                 * Directed edges occur once in the matrix.
                 *
                 * Undirected edges occur twice, so only take
                 * the copy on or above the main diagonal.
                 */
                if (edge.directed || row <= column) {
                    edges[resultIndex] = edge;
                    resultIndex++;
                }
            }
        }

        return edges;
    }

    @Override
    public Edge[] getNeighbors(String nodeId) {
        int nodeIndex = findNodeIndex(nodeId);

        if (nodeIndex == -1) {
            throw new IllegalArgumentException(
                "Unknown node id: " + nodeId
            );
        }

        /*
         * First count the legal outgoing edges so we can
         * create an array of the exact size.
         */
        int neighborCount = 0;

        for (int column = 0; column < nodeCount; column++) {
            if (matrix[nodeIndex][column] != null) {
                neighborCount++;
            }
        }

        Edge[] neighbors = new Edge[neighborCount];
        int resultIndex = 0;

        /*
         * Fill the result array.
         *
         * Because directed edges are stored only in their legal
         * direction, every edge in this row is traversable.
         */
        for (int column = 0; column < nodeCount; column++) {
            Edge edge = matrix[nodeIndex][column];

            if (edge != null) {
                neighbors[resultIndex] = edge;
                resultIndex++;
            }
        }

        return neighbors;
    }

    @Override
    public Edge[] getConnections(String nodeId) {
        int nodeIndex = findNodeIndex(nodeId);

        if (nodeIndex == -1) {
            throw new IllegalArgumentException(
                "Unknown node id: " + nodeId
            );
        }

        /*
         * Connections include:
         * 1. Outgoing edges in the node's row.
         * 2. Incoming edges in the node's column.
         */
        int connectionCount = 0;

        for (
            int otherIndex = 0;
            otherIndex < nodeCount;
            otherIndex++
        ) {
            Edge outgoingEdge =
                matrix[nodeIndex][otherIndex];

            Edge incomingEdge =
                matrix[otherIndex][nodeIndex];

            if (outgoingEdge != null) {
                connectionCount++;
            }

            /*
             * An undirected edge uses the same object in both cells.
             * This reference comparison prevents counting it twice.
             *
             * Two opposite directed edges are different objects,
             * so both will be counted.
             */
            if (
                otherIndex != nodeIndex
                    && incomingEdge != null
                    && incomingEdge != outgoingEdge
            ) {
                connectionCount++;
            }
        }

        Edge[] connections = new Edge[connectionCount];
        int resultIndex = 0;

        for (
            int otherIndex = 0;
            otherIndex < nodeCount;
            otherIndex++
        ) {
            Edge outgoingEdge =
                matrix[nodeIndex][otherIndex];

            Edge incomingEdge =
                matrix[otherIndex][nodeIndex];

            if (outgoingEdge != null) {
                connections[resultIndex] = outgoingEdge;
                resultIndex++;
            }

            if (
                otherIndex != nodeIndex
                    && incomingEdge != null
                    && incomingEdge != outgoingEdge
            ) {
                connections[resultIndex] = incomingEdge;
                resultIndex++;
            }
        }

        return connections;
    }

    /**
     * Prints a simple matrix for demonstrations and evidence.
     * 1 means an edge exists; 0 means no edge exists.
     */
    public void printMatrix() {
        System.out.println(
            "Adjacency matrix (1 = edge, 0 = no edge)"
        );

        for (int row = 0; row < nodeCount; row++) {
            for (
                int column = 0;
                column < nodeCount;
                column++
            ) {
                if (matrix[row][column] == null) {
                    System.out.print("0 ");
                } else {
                    System.out.print("1 ");
                }
            }

            System.out.println();
        }
    }
}