package gsoo.app;

import gsoo.algorithms.a1_binary_search.BinarySearch;
import gsoo.algorithms.b1_quicksort.QuickSort;
import gsoo.algorithms.b3_brute_force.BruteForceAssignment;
import gsoo.db.AuditEvent;
import gsoo.db.DatabaseLoader;
import gsoo.db.Resource;
import gsoo.db.ServiceRequest;
import gsoo.structures.Graph;
import gsoo.structures.a3_stack.Stack;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Console entry point — the single class named in pom.xml's shade-plugin
 * mainClass. Chapters share one loaded {@link Session} (backed by a live
 * Postgres connection, held open for the run) instead of each building its
 * own disposable toy data, so this reads as one request's walk through the
 * system rather than 8 unrelated demos. Load the dataset first (option 1);
 * everything else operates on that same real, live data.
 *
 * Only as true a story as what's actually merged: no priority queue,
 * routing, or dispatch algorithm exists yet (C1/C2/C5), so the walkthrough
 * stops at "index, look up, and inspect a real request" — it does not yet
 * decide, route, or dispatch anything. Each chapter says so where it applies.
 */
public class Main {

    private record MenuItem(String label, Runnable action) {
    }

    private static final Session session = new Session();

    public static void main(String[] args) {

        MenuItem[] items = {
                new MenuItem("Load the real dataset from the live database (start here)", Main::loadDataset),
                new MenuItem("Find today's focus request — binary search (A1)", Main::findFocusRequest),
                new MenuItem("Walk its audit trail — writes to the live DB (A3)", Main::auditTrailChapter),
                new MenuItem("Look up its category volume (B5)", Main::categoryVolumeChapter),
                new MenuItem("Confirm it in the request index (B3)", Main::requestIndexChapter),
                new MenuItem("Look at its location's road connections (C4)", Main::graphChapter),
                new MenuItem("Sort today's queue by urgency (B1)", Main::quickSortChapter),
                new MenuItem("Brute-force assign 3 porters to 3 jobs (B3)", Main::bruteForceChapter),
        };

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu(items);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Goodbye.");
                break;
            }

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a number, or 'q' to quit.");
                continue;
            }
            if (choice < 1 || choice > items.length) {
                System.out.println("No such option.");
                continue;
            }

            System.out.println();
            items[choice - 1].action().run();
            System.out.println();
        }
        scanner.close();
        session.close();
    }

    private static void printMenu(MenuItem[] items) {
        System.out.println("=== Ghana Smart Service Operations Optimizer ===");
        System.out.println("(" + (items.length - 1) + " of 15 slots wired into the walkthrough so far)");
        System.out.println(session.loaded
                ? "Dataset loaded from the live DB. Focus request: " + session.focusRequest.requestId
                : "Dataset not loaded yet — run option 1 first.");
        for (int i = 0; i < items.length; i++) {
            System.out.println("  " + (i + 1) + ". " + items[i].label());
        }
        System.out.println("  q. Quit");
        System.out.print("> ");
    }

    private static boolean requireLoaded() {
        if (!session.loaded) {
            System.out.println("Dataset isn't loaded yet. Run option 1 first.");
            return false;
        }
        return true;
    }

    // ---------------- Chapters ----------------

    private static void loadDataset() {
        try {
            session.load();
        } catch (SQLException e) {
            System.out.println("Could not load from the database: " + e.getMessage());
            System.out.println("Check: Postgres running, 'gsoo' database exists with sql/schema.sql "
                    + "and sql/seed/*.sql applied, and DB_PASSWORD is set in this shell.");
            return;
        }
        System.out.println("Loaded " + session.locations.length + " locations, "
                + session.roads.length + " roads, " + session.resources.length + " resources, "
                + session.requests.length + " service requests from the live database.");
        System.out.println("Built the graph (" + session.graph.nodeCount() + " nodes, "
                + session.graph.edgeCount() + " edges), indexed every request in the B-tree, "
                + "and tallied category volumes in the map.");
        System.out.println("\nToday's focus request: " + session.focusRequest);
    }

    private static void findFocusRequest() {
        if (!requireLoaded()) {
            return;
        }
        String[] ids = new String[session.requestTable.size()];
        for (int i = 0; i < session.requestTable.size(); i++) {
            ids[i] = session.requestTable.get(i).requestId;
        }

        BinarySearch<String> search = new BinarySearch<>();
        int index = search.binarySearch(0, ids.length - 1, ids, session.focusRequest.requestId);
        System.out.println("binarySearch(\"" + session.focusRequest.requestId + "\") over "
                + ids.length + " real request IDs -> index " + index);
        System.out.println("Retrieved: " + session.requestTable.get(index));
    }

    private static void auditTrailChapter() {
        if (!requireLoaded()) {
            return;
        }
        // Real writes against the live DB — same pattern as AuditTrailDemo:
        // every push is also an INSERT into audit_events plus an UPDATE to
        // service_requests.status, and undo (pop) logs a new UNDONE row
        // rather than deleting history.
        ServiceRequest r = session.focusRequest;
        Stack<AuditEvent> auditTrail = new Stack<>();
        try {
            AuditEvent created = new AuditEvent(r.requestId, "CREATED", null, "PENDING",
                    "app-walkthrough", new Timestamp(System.currentTimeMillis()));
            doAction(auditTrail, created);

            AuditEvent assigned = new AuditEvent(r.requestId, "ASSIGNED", "PENDING", "ASSIGNED",
                    "app-walkthrough", new Timestamp(System.currentTimeMillis()));
            doAction(auditTrail, assigned);

            System.out.println("Pushed " + auditTrail.size() + " real audit events for "
                    + r.requestId + " (written to audit_events, status updated live).");

            AuditEvent lastEvent = auditTrail.pop();
            AuditEvent undoEvent = new AuditEvent(r.requestId, "UNDONE",
                    lastEvent.newStatus, lastEvent.previousStatus,
                    "app-walkthrough", new Timestamp(System.currentTimeMillis()));
            DatabaseLoader.insertAuditEvent(session.connection(), undoEvent);
            DatabaseLoader.updateRequestStatus(session.connection(), r.requestId, lastEvent.previousStatus);
            System.out.println("Undid: " + lastEvent);
            System.out.println("size now=" + auditTrail.size());
        } catch (SQLException e) {
            System.out.println("Audit write failed: " + e.getMessage());
        }
    }

    private static void doAction(Stack<AuditEvent> stack, AuditEvent event) throws SQLException {
        stack.push(event);
        DatabaseLoader.insertAuditEvent(session.connection(), event);
        DatabaseLoader.updateRequestStatus(session.connection(), event.requestId, event.newStatus);
        System.out.println("Did: " + event);
    }

    private static void categoryVolumeChapter() {
        if (!requireLoaded()) {
            return;
        }
        ServiceRequest r = session.focusRequest;
        Integer count = session.categoryVolume.get(r.category);
        System.out.println(r.requestId + " is category " + r.category + ".");
        System.out.println(count + " of " + session.requests.length
                + " real requests fall into that category.");
    }

    private static void requestIndexChapter() {
        if (!requireLoaded()) {
            return;
        }
        ServiceRequest r = session.focusRequest;
        StringBuilder trace = new StringBuilder();
        ServiceRequest found = session.requestIndex.searchWithTrace(r.requestId, trace);
        System.out.print(trace);
        System.out.println("B-tree confirms: " + found);
    }

    private static void graphChapter() {
        if (!requireLoaded()) {
            return;
        }
        ServiceRequest r = session.focusRequest;
        System.out.println(r.requestId + " originates at " + r.sourceLocationId
                + ", heading to " + r.destinationLocationId + ".");
        System.out.println("Direct road connections from " + r.sourceLocationId + ":");
        Graph.Edge[] neighbors = session.graph.getNeighbors(r.sourceLocationId);
        if (neighbors.length == 0) {
            System.out.println("  (none directly connected)");
        }
        for (Graph.Edge e : neighbors) {
            double effectiveCost = e.travelTimeSecs * e.roadConditionWeight;
            System.out.println("  " + e.fromId + " -> " + e.toId + " cost=" + effectiveCost);
        }
        System.out.println("(Multi-hop shortest path needs Dijkstra — C2's heap and Dijkstra "
                + "aren't merged yet, so this chapter stops at direct connections.)");
    }

    private static void quickSortChapter() {
        if (!requireLoaded()) {
            return;
        }
        int[] urgencies = new int[session.requests.length];
        for (int i = 0; i < session.requests.length; i++) {
            urgencies[i] = session.requests[i].urgency;
        }
        System.out.println("Before (first 15 of " + urgencies.length + "): "
                + Arrays.toString(Arrays.copyOf(urgencies, 15)));
        QuickSort.quickSort(urgencies, 0, urgencies.length - 1);
        System.out.println("After  (first 15, ascending): "
                + Arrays.toString(Arrays.copyOf(urgencies, 15)));
        System.out.println("(This orders urgency values only — it doesn't preserve which request "
                + "each one belongs to. A real dispatch queue needs a stable sort or the heap, "
                + "not plain quicksort, if arrival order should be a tiebreaker.)");
    }

    private static void bruteForceChapter() {
        if (!requireLoaded()) {
            return;
        }
        Resource[] porters = firstNOfType(session.resources, "porter", 3);
        ServiceRequest[] pending = firstNPending(session.requests, 3);
        if (porters.length < 3 || pending.length < 3) {
            System.out.println("Not enough real porters/pending requests to build a 3x3 example.");
            return;
        }

        int[][] cost = new int[3][3];
        for (int p = 0; p < 3; p++) {
            for (int j = 0; j < 3; j++) {
                cost[p][j] = travelCostOrFallback(porters[p].homeLocationId, pending[j].sourceLocationId);
            }
        }

        System.out.println("Porters: " + porters[0].resourceId + ", " + porters[1].resourceId
                + ", " + porters[2].resourceId);
        System.out.println("Jobs: " + pending[0].requestId + ", " + pending[1].requestId
                + ", " + pending[2].requestId);
        BruteForceAssignment.Result result = new BruteForceAssignment().solve(cost);
        System.out.println("Best assignment (porter index -> job index): "
                + Arrays.toString(result.assignment));
        System.out.println("Total cost: " + result.totalCost);
        System.out.println("Permutations tried: " + result.permutationsTried);
        System.out.println("(Costs use the real road graph where a direct edge exists; falls back "
                + "to a fixed penalty otherwise, since multi-hop routing needs Dijkstra, not merged yet.)");
    }

    // ---------------- Small helpers ----------------

    private static Resource[] firstNOfType(Resource[] resources, String type, int n) {
        Resource[] found = new Resource[n];
        int count = 0;
        for (Resource r : resources) {
            if (r.type.equals(type)) {
                found[count++] = r;
                if (count == n) {
                    return found;
                }
            }
        }
        return Arrays.copyOf(found, count);
    }

    private static ServiceRequest[] firstNPending(ServiceRequest[] requests, int n) {
        ServiceRequest[] found = new ServiceRequest[n];
        int count = 0;
        for (ServiceRequest r : requests) {
            if ("PENDING".equals(r.status)) {
                found[count++] = r;
                if (count == n) {
                    return found;
                }
            }
        }
        return Arrays.copyOf(found, count);
    }

    private static final int NO_DIRECT_ROAD_PENALTY = 9999;

    private static int travelCostOrFallback(String fromId, String toId) {
        if (!session.graph.hasNode(fromId) || !session.graph.hasNode(toId)) {
            return NO_DIRECT_ROAD_PENALTY;
        }
        for (Graph.Edge e : session.graph.getNeighbors(fromId)) {
            if (e.toId.equals(toId) || e.fromId.equals(toId)) {
                return (int) Math.round(e.travelTimeSecs * e.roadConditionWeight);
            }
        }
        return NO_DIRECT_ROAD_PENALTY;
    }
}
