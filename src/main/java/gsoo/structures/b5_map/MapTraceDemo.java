package gsoo.structures;

/**
 * Slot B5 — trace demo.
 *
 * This uses placeholder location/request data so there's a real trace to
 * show at the meeting today. Once A3 ships the actual 50-location /
 * 300-request dataset (sql/seed), swap the sample data below for real rows
 * loaded via the DB/JDBC layer so the trace counts as "generated from the
 * real dataset" per the Definition of Done.
 */
public class MapTraceDemo {

    public static void main(String[] args) {
        CustomMap<String, Integer> pendingRequestsByLocation = new CustomMap<>();

        System.out.println("-- put --");
        put(pendingRequestsByLocation, "Ambulance Bay", 3);
        put(pendingRequestsByLocation, "Ward 3", 1);
        put(pendingRequestsByLocation, "Pharmacy", 5);
        put(pendingRequestsByLocation, "CHPS-Ashongman", 2);

        System.out.println("\n-- get --");
        System.out.println("Ambulance Bay -> " + pendingRequestsByLocation.get("Ambulance Bay"));
        System.out.println("Unknown -> " + pendingRequestsByLocation.get("Unknown Location"));

        System.out.println("\n-- update existing key --");
        put(pendingRequestsByLocation, "Ward 3", 4);

        System.out.println("\n-- remove --");
        System.out.println("removed Pharmacy -> " + pendingRequestsByLocation.remove("Pharmacy"));

        System.out.println("\n-- final state --");
        for (String key : pendingRequestsByLocation.keySet()) {
            System.out.println(key + " : " + pendingRequestsByLocation.get(key));
        }
        System.out.println("size = " + pendingRequestsByLocation.size());
    }

    private static void put(CustomMap<String, Integer> map, String key, int value) {
        Integer previous = map.put(key, value);
        System.out.println("put(" + key + ", " + value + ") -> previous=" + previous);
    }
}
