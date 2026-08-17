package gsoo.db;

import java.sql.Timestamp;

public class ServiceRequest {
    public String requestId, category, patientRef, sourceLocationId, destinationLocationId, status;
    public int urgency;
    public Timestamp submittedAt, deadlineAt;
    public String assignedResourceId;   // may be null

<<<<<<< HEAD
    ServiceRequest(String requestId, String category, String patientRef, String sourceLocationId, String destinationLocationId,
=======
    public ServiceRequest(String requestId, String category, String patientRef, String sourceLocationId, String destinationLocationId,
>>>>>>> 6402d570fdb7195621108a0df30be174bd14fe8f
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
}
