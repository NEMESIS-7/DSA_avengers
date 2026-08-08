package gsoo.db;

import java.sql.Timestamp;

// Mirrors the audit_events table exactly. eventId is null until the
// database assigns it (it's a SERIAL column — Postgres automatically generates  it).
public class AuditEvent {
    Integer eventId;           // null until inserted (DB assigns this)
    String requestId;
    String action;             // for example:  CREATED, ASSIGNED, STATUS_CHANGE, UNDONE
    String previousStatus;
    String newStatus;
    String performedBy;
    Timestamp eventTime;

    public AuditEvent(String requestId, String action, String previousStatus,
                       String newStatus, String performedBy, Timestamp eventTime) {
        this.requestId = requestId;
        this.action = action;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.performedBy = performedBy;
        this.eventTime = eventTime;
    }

    public String toString() {
        return "[" + action + "] " + requestId + ": " + previousStatus + " -> " + newStatus +
               " (by " + performedBy + " at " + eventTime + ")";
    }
}
