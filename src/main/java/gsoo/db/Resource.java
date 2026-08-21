package gsoo.db;

public class Resource {
    public String resourceId, type, homeLocationId;
    public int capacity;
    public boolean isAvailable;

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
