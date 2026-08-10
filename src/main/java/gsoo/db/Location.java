package gsoo.db;

public class Location {
    public String locationId, name, layer, type, area;
    public Double latitude, longitude;   // Double (not double) so a NULL can be represented as null

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
