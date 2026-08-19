package gsoo.db;

public class Road {
    public String roadId, fromLocationId, toLocationId, roadName;
    public double distanceM, travelTimeS, roadConditionWeight;
    public boolean isClosed;

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
    public double effectiveCost() {
        return travelTimeS * roadConditionWeight;
    }

    public String toString() {
        return roadId + " | " + roadName + " | " + fromLocationId + " -> " + toLocationId +
                " | cost=" + String.format("%.2f", effectiveCost()) + (isClosed ? " [CLOSED]" : "");
    }
}
