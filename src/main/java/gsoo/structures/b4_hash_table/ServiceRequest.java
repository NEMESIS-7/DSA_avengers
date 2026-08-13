package structures.b4_hash_table;

// This is what gets stored in the hash table - looking up a requestId
public class ServiceRequest {

    String requestId;             // e.g. "REQ-0001"(this is the key we hash on) 
    String category;              
    String sourceLocationId;  
    String destinationLocationId; 
    int urgency;                  // 1 to 5
    String status;                // PENDING, ASSIGNED, IN_TRANSIT, COMPLETED, CANCELLED
    String submittedAt;          
    String deadlineAt;
    String assignedResourceId;    

    public ServiceRequest(String requestId, String category, String sourceLocationId,
                           String destinationLocationId, int urgency, String status,
                           String submittedAt, String deadlineAt, String assignedResourceId) {
        this.requestId = requestId;
        this.category = category;
        this.sourceLocationId = sourceLocationId;
        this.destinationLocationId = destinationLocationId;
        this.urgency = urgency;
        this.status = status;
        this.submittedAt = submittedAt;
        this.deadlineAt = deadlineAt;
        this.assignedResourceId = assignedResourceId;
    }

    // Simple readable print out, useful for testing/debugging
    public String toString() {
        return requestId + " | " + category + " | from " + sourceLocationId
                + " to " + destinationLocationId + " | urgency " + urgency
                + " | " + status;
    }

    public String getRequestId() {
        return requestId;
    }
}