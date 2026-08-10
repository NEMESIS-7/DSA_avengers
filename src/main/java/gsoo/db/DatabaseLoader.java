package gsoo.db;

import java.sql.*;



/*class Location {
    String locationId, name, layer, type, area;
    Double latitude, longitude;   // Double (not double) so a NULL can be represented as null

    Location(String locationId, String name, String layer, String type,
             String area, Double latitude, Double longitude) {
        this.locationId = locationId;
        this.name = name;
        this.layer = layer;
        this.type = type;
        this.area = area;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString() {
        return locationId + " | " + name + " | " + layer + "/" + type;
    }
}

class Road {
    String roadId, fromLocationId, toLocationId, roadName;
    double distanceM, travelTimeS, roadConditionWeight;
    boolean isClosed;

    Road(String roadId, String fromLocationId, String toLocationId, String roadName,
         double distanceM, double travelTimeS, double roadConditionWeight, boolean isClosed) {
        this.roadId = roadId;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.roadName = roadName;
        this.distanceM = distanceM;
        this.travelTimeS = travelTimeS;
        this.roadConditionWeight = roadConditionWeight;
        this.isClosed = isClosed;
    }

    // This is the number DFS/BFS/Dijkstra/Kruskal/Prim all actually use
    double effectiveCost() {
        return travelTimeS * roadConditionWeight;
    }

    public String toString() {
        return roadId + " | " + roadName + " | " + fromLocationId + " -> " + toLocationId +
                " | cost=" + String.format("%.2f", effectiveCost()) + (isClosed ? " [CLOSED]" : "");
    }
}

class Resource {
    String resourceId, type, homeLocationId;
    int capacity;
    boolean isAvailable;

    Resource(String resourceId, String type, int capacity, boolean isAvailable, String homeLocationId) {
        this.resourceId = resourceId;
        this.type = type;
        this.capacity = capacity;
        this.isAvailable = isAvailable;
        this.homeLocationId = homeLocationId;
    }

    public String toString() {
        return resourceId + " | " + type + " | home=" + homeLocationId +
               (isAvailable ? " [available]" : " [busy]");
    }
}

class ServiceRequest {
    String requestId, category, patientRef, sourceLocationId, destinationLocationId, status;
    int urgency;
    Timestamp submittedAt, deadlineAt;
    String assignedResourceId;   // may be null

    ServiceRequest(String requestId, String category, String patientRef, String sourceLocationId, String destinationLocationId,
                   int urgency, String status, Timestamp submittedAt, Timestamp deadlineAt, String assignedResourceId) {
        this.requestId = requestId;
        this.category = category;
        this.patientRef = patientRef;   // null for non-patient-specific categories
        this.sourceLocationId = sourceLocationId;
        this.destinationLocationId = destinationLocationId;
        this.urgency = urgency;
        this.status = status;
        this.submittedAt = submittedAt;
        this.deadlineAt = deadlineAt;
        this.assignedResourceId = assignedResourceId;
    }

    public String toString() {
        return requestId + " | " + category + " | patient=" + (patientRef != null ? patientRef : "-") +
               " | " + sourceLocationId + "->" + destinationLocationId +
               " | urgency=" + urgency + " | " + status;
    }
}*/

// ---------------- The loader itself ----------------

public class DatabaseLoader {

    // Reads from an environment variable rather than hardcoding a real password
    // in source control. Set it locally before running, e.g.:
    //   export DB_PASSWORD=your_actual_password
    private static final String URL      = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER     = "postgres";
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Counts rows first so we can allocate an exactly-sized array
    // (no ArrayList needed — this is the plain-array equivalent of "resizing as you go")
    private static int countRows(Connection conn, String table) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public static Location[] loadLocations(Connection conn) throws SQLException {
        int n = countRows(conn, "locations");
        Location[] locations = new Location[n];

        String sql = "SELECT location_id, name, layer, type, area, latitude, longitude FROM locations";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int i = 0;
            while (rs.next()) {
                double lat = rs.getDouble("latitude");
                Double latitude = rs.wasNull() ? null : lat;
                double lon = rs.getDouble("longitude");
                Double longitude = rs.wasNull() ? null : lon;

                locations[i++] = new Location(
                    rs.getString("location_id"),
                    rs.getString("name"),
                    rs.getString("layer"),
                    rs.getString("type"),
                    rs.getString("area"),
                    latitude,
                    longitude
                );
            }
        }
        return locations;
    }

    public static Road[] loadRoads(Connection conn) throws SQLException {
        int n = countRows(conn, "roads");
        Road[] roads = new Road[n];

        String sql = "SELECT road_id, from_location_id, to_location_id, road_name, distance_m, " +
                     "travel_time_s, road_condition_weight, is_closed FROM roads";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int i = 0;
            while (rs.next()) {
                roads[i++] = new Road(
                    rs.getString("road_id"),
                    rs.getString("from_location_id"),
                    rs.getString("to_location_id"),
                    rs.getString("road_name"),
                    rs.getDouble("distance_m"),
                    rs.getDouble("travel_time_s"),
                    rs.getDouble("road_condition_weight"),
                    rs.getBoolean("is_closed")
                );
            }
        }
        return roads;
    }

    public static Resource[] loadResources(Connection conn) throws SQLException {
        int n = countRows(conn, "resources");
        Resource[] resources = new Resource[n];

        String sql = "SELECT resource_id, type, capacity, is_available, home_location_id FROM resources";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int i = 0;
            while (rs.next()) {
                resources[i++] = new Resource(
                    rs.getString("resource_id"),
                    rs.getString("type"),
                    rs.getInt("capacity"),
                    rs.getBoolean("is_available"),
                    rs.getString("home_location_id")
                );
            }
        }
        return resources;
    }

    public static ServiceRequest[] loadServiceRequests(Connection conn) throws SQLException {
        int n = countRows(conn, "service_requests");
        ServiceRequest[] requests = new ServiceRequest[n];

        String sql = "SELECT request_id, category, patient_ref, source_location_id, destination_location_id, " +
                     "urgency, status, submitted_at, deadline_at, assigned_resource_id FROM service_requests";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int i = 0;
            while (rs.next()) {
                requests[i++] = new ServiceRequest(
                    rs.getString("request_id"),
                    rs.getString("category"),
                    rs.getString("patient_ref"),   // null-safe automatically for non-patient categories
                    rs.getString("source_location_id"),
                    rs.getString("destination_location_id"),
                    rs.getInt("urgency"),
                    rs.getString("status"),
                    rs.getTimestamp("submitted_at"),
                    rs.getTimestamp("deadline_at"),
                    rs.getString("assigned_resource_id")   // null-safe automatically
                );
            }
        }
        return requests;
    }

    // ---------------- Demo / sanity check ----------------
    public static void main(String[] args) {
        try (Connection conn = connect()) {
            Location[] locations = loadLocations(conn);
            Road[] roads = loadRoads(conn);
            Resource[] resources = loadResources(conn);
            ServiceRequest[] requests = loadServiceRequests(conn);

            System.out.println("Loaded " + locations.length + " locations (expect 50)");
            System.out.println("Loaded " + roads.length + " roads (expect 100)");
            System.out.println("Loaded " + resources.length + " resources (expect 30)");
            System.out.println("Loaded " + requests.length + " service requests (expect 300)");

            System.out.println("\nFirst 3 locations:");
            for (int i = 0; i < 3 && i < locations.length; i++) System.out.println("  " + locations[i]);

            System.out.println("\nFirst 3 roads:");
            for (int i = 0; i < 3 && i < roads.length; i++) System.out.println("  " + roads[i]);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
