package gsoo.db;

import java.sql.*;

// ---------------- The loader itself ----------------
// Model classes (Location, Road, Resource, ServiceRequest) moved to their
// own files — a public top-level class must live in a file of the same name.

public class DatabaseLoader {

    // Reads from an environment variable rather than hardcoding a real password
    // in source control. Set it locally before running, e.g.:
    //   export DB_PASSWORD=your_actual_password
    private static final String URL      = System.getenv("DB_URL");
    private static final String USER     = System.getenv("DB_USER");
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

    // ---------------- Writes (mirrors AuditTrailDemo's private helpers, made
    // reusable so gsoo.app can persist through this class rather than reaching
    // into DB internals directly) ----------------

    public static void insertAuditEvent(Connection conn, AuditEvent event) throws SQLException {
        String sql = "INSERT INTO audit_events (request_id, action, previous_status, new_status, performed_by, event_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.requestId);
            ps.setString(2, event.action);
            ps.setString(3, event.previousStatus);
            ps.setString(4, event.newStatus);
            ps.setString(5, event.performedBy);
            ps.setTimestamp(6, event.eventTime);
            ps.executeUpdate();
        }
    }

    public static void updateRequestStatus(Connection conn, String requestId, String newStatus) throws SQLException {
        String sql = "UPDATE service_requests SET status = ? WHERE request_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, requestId);
            ps.executeUpdate();
        }
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
