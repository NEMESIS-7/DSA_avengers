package gsoo.app;

import gsoo.db.DatabaseLoader;
import gsoo.db.Location;
import gsoo.db.Resource;
import gsoo.db.Road;
import gsoo.db.ServiceRequest;
import gsoo.structures.a1_dynamic_array.DynamicArray;
import gsoo.structures.b3_btree.BTree;
import gsoo.structures.b5_map.CustomMap;
import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The one shared, loaded dataset every menu chapter in Main operates on —
 * replaces each demo building its own disposable toy data. Reads from (and,
 * for the audit trail chapter, writes back to) a live Postgres database —
 * held open for the session's lifetime so chapters share one connection
 * instead of reconnecting per operation.
 */
public class Session {

    public Location[] locations;
    public Road[] roads;
    public Resource[] resources;
    public ServiceRequest[] requests;

    public DynamicArray<ServiceRequest> requestTable;   // A1 — the loaded working set, sorted by requestId
    public AdjacencyListGraph graph;                    // C4 — built from real locations + roads
    public BTree<String, ServiceRequest> requestIndex;  // B3 — indexed by requestId
    public CustomMap<String, Integer> categoryVolume;   // B5 — category -> how many real requests fall in it

    public ServiceRequest focusRequest;   // the one request the walkthrough follows chapter to chapter
    public boolean loaded = false;

    private Connection connection;

    public Connection connection() {
        return connection;
    }

    public void load() throws SQLException {
        connection = DatabaseLoader.connect();

        locations = DatabaseLoader.loadLocations(connection);
        roads = DatabaseLoader.loadRoads(connection);
        resources = DatabaseLoader.loadResources(connection);
        requests = DatabaseLoader.loadServiceRequests(connection);

        requestTable = new DynamicArray<>();
        for (ServiceRequest r : requests) {
            requestTable.add(r);
        }
        sortByRequestId(requestTable);

        graph = new AdjacencyListGraph();
        for (Location loc : locations) {
            graph.addNode(loc.locationId, loc.type);
        }
        for (Road road : roads) {
            if (!graph.hasEdge(road.fromLocationId, road.toLocationId)) {
                graph.addEdge(road.fromLocationId, road.toLocationId,
                        road.distanceM, road.travelTimeS, road.roadConditionWeight, false);
            }
        }

        requestIndex = new BTree<>(3);
        for (ServiceRequest r : requests) {
            requestIndex.insert(r.requestId, r);
        }

        categoryVolume = new CustomMap<>();
        for (ServiceRequest r : requests) {
            Integer count = categoryVolume.get(r.category);
            categoryVolume.put(r.category, count == null ? 1 : count + 1);
        }

        focusRequest = pickFocusRequest();
        loaded = true;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Could not close the database connection cleanly: " + e.getMessage());
            }
        }
    }

    private ServiceRequest pickFocusRequest() {
        for (ServiceRequest r : requests) {
            if ("PENDING".equals(r.status)) {
                return r;
            }
        }
        return requests.length > 0 ? requests[0] : null;
    }

    // Defensive, same as BinarySearchTraceDemo: don't assume row order from the
    // DB is sorted just because there's no ORDER BY guaranteeing it isn't.
    private static void sortByRequestId(DynamicArray<ServiceRequest> arr) {
        for (int i = 1; i < arr.size(); i++) {
            ServiceRequest key = arr.get(i);
            int j = i - 1;
            while (j >= 0 && arr.get(j).requestId.compareTo(key.requestId) > 0) {
                arr.set(j + 1, arr.get(j));
                j--;
            }
            arr.set(j + 1, key);
        }
    }
}
