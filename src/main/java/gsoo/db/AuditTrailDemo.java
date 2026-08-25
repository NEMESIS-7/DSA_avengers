package gsoo.db;

import gsoo.structures.a3_stack.Stack;
import java.sql.*;


// This demonstrates the real job of the Stack: undo-dispatch.
//
// Flow:
//   1. Simulate a couple of real actions happening to a real
//      service request (CREATED, then ASSIGNED) — each one gets
//      pushed onto the Stack AND written to audit_events.
//   2. "Undo" = pop the Stack's top event, then:
//        - revert the request's status in service_requests
//        - log the reversal itself as a new UNDONE audit event
//          (audit trails are append-only — we never delete history,
//           we log the correction as a new entry)


public class AuditTrailDemo {

    public static void main(String[] args) throws SQLException {
        try (Connection conn = DatabaseLoader.connect()) {

            String requestId = "REQ-0001";

            // Show the real current status before we touch anything
            String currentStatus = getStatus(conn, requestId);
            System.out.println("Before: " + requestId + " status = " + currentStatus);

            Stack<AuditEvent> auditStack = new Stack<>();

            //Simulate two real actions happening, pushing + persisting each
            AuditEvent created = new AuditEvent(requestId, "CREATED", null, "PENDING",
                    "A3-demo", new Timestamp(System.currentTimeMillis()));
            doAction(conn, auditStack, created);

            AuditEvent assigned = new AuditEvent(requestId, "ASSIGNED", "PENDING", "ASSIGNED",
                    "A3-demo", new Timestamp(System.currentTimeMillis()));
            doAction(conn, auditStack, assigned);

            System.out.println("\nAfter two actions: " + requestId + " status = " + getStatus(conn, requestId));
            System.out.println("Stack size = " + auditStack.size() + " (expect 2)");

            //Now UNDO the most recent action
            System.out.println("\n--- Undoing last action ---");
            undoLast(conn, auditStack, requestId);

            System.out.println("After undo: " + requestId + " status = " + getStatus(conn, requestId));
            System.out.println("Stack size = " + auditStack.size() + " (expect 1)");
        }
    }

    // Push an event, persist it to audit_events, and apply it to service_requests.status
    private static void doAction(Connection conn, Stack<AuditEvent> stack, AuditEvent event) throws SQLException {
        stack.push(event);
        insertAuditEvent(conn, event);
        updateStatus(conn, event.requestId, event.newStatus);
        System.out.println("Did: " + event);
    }

    // Pop the stack, revert the DB status, and log the reversal as a NEW audit event
    private static void undoLast(Connection conn, Stack<AuditEvent> stack, String requestId) throws SQLException {
        AuditEvent lastEvent = stack.pop();

        AuditEvent undoEvent = new AuditEvent(requestId, "UNDONE",
                lastEvent.newStatus, lastEvent.previousStatus,
                "A3-demo", new Timestamp(System.currentTimeMillis()));

        insertAuditEvent(conn, undoEvent);
        updateStatus(conn, requestId, lastEvent.previousStatus);

        System.out.println("Undid: " + lastEvent);
        System.out.println("Logged: " + undoEvent);
    }

    private static String getStatus(Connection conn, String requestId) throws SQLException {
        String sql = "SELECT status FROM service_requests WHERE request_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, requestId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getString("status");
        }
    }

    private static void updateStatus(Connection conn, String requestId, String newStatus) throws SQLException {
        String sql = "UPDATE service_requests SET status = ? WHERE request_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, requestId);
            ps.executeUpdate();
        }
    }

    private static void insertAuditEvent(Connection conn, AuditEvent event) throws SQLException {
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
}
