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
import gsoo.structures.a4_queue_circular_queue.CircularQueue;
import gsoo.structures.a4_queue_circular_queue.DynamicQueue;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Console entry point — the single class named in pom.xml's shade-plugin
 * mainClass. Chapters share one loaded {@link Session} (backed by a live
 * Postgres connection, held open for the run) instead of each building its
 * own disposable toy data, so this reads as one request's walk through the
 * system rather than unrelated demos.
 *
 * Navigation is two-level: a top menu (load / run the full story / explore
 * chapters by pod / quit), and an explore submenu grouped by pod using short
 * mnemonic codes (a1, b3b, c4, ...) instead of raw numbers, so the menu reads
 * as "which pod's work do you want to see" rather than an insertion-ordered
 * list. {@link #CHAPTERS} is the single source of truth for both the story
 * order (array order) and the pod grouping (each chapter's {@code pod} tag) —
 * adding a chapter for a newly-merged slot is one array entry, not a rewrite.
 *
 * Only as true a story as what's actually merged: no priority queue,
 * routing, or dispatch algorithm exists yet (C1/C2/C5), so the walkthrough
 * stops at "index, look up, and inspect a real request" — it does not yet
 * decide, route, or dispatch anything. Each chapter says so where it applies.
 */
public class Main {

    private record Chapter(String code, String pod, String label, Runnable action) {
    }

    private static final Chapter[] CHAPTERS = {
            new Chapter("a1", "A", "Find today's focus request — binary search", Main::findFocusRequest),
            new Chapter("a3", "A", "Walk its audit trail — writes to the live DB", Main::auditTrailChapter),
            new Chapter("a4a", "A", "Walk-in line — FIFO queue", Main::opdQueueChapter),
            new Chapter("a4b", "A", "Porter roster wraps — circular queue", Main::porterRosterChapter),
            new Chapter("b5", "B", "Look up its category volume", Main::categoryVolumeChapter),
            new Chapter("b3a", "B", "Confirm it in the request index (B-tree)", Main::requestIndexChapter),
            new Chapter("c4", "C", "Look at its location's road connections", Main::graphChapter),
            new Chapter("b1", "B", "Sort today's queue by urgency (quicksort)", Main::quickSortChapter),
            new Chapter("b3b", "B", "Brute-force assign 3 porters to 3 jobs", Main::bruteForceChapter),
    };

    private static final Session session = new Session();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        while (!quit) {
            printTopMenu();
            String input = scanner.nextLine().trim();
            switch (input.toLowerCase()) {
                case "q" -> quit = true;
                case "1" -> {
                    System.out.println();
                    loadDataset();
                    System.out.println();
                }
                case "2" -> runFullStory();
                case "3" -> quit = exploreByPod(scanner);
                default -> System.out.println("No such option.");
            }
        }
        System.out.println("Goodbye.");
        scanner.close();
        session.close();
    }

    private static void printTopMenu() {
        System.out.println("=== Ghana Smart Service Operations Optimizer ===");
        System.out.println(session.loaded
                ? "Dataset loaded from the live DB. Focus request: " + session.focusRequest.requestId
                : "Dataset not loaded yet — start with option 1.");
        System.out.println("  1. Load the real dataset");
        System.out.println("  2. Run the full story, start to finish");
        System.out.println("  3. Explore chapters by pod (" + CHAPTERS.length + " of 15 slots wired in so far)");
        System.out.println("  q. Quit");
        System.out.print("> ");
    }

    private static void runFullStory() {
        System.out.println();
        if (!session.loaded) {
            System.out.println("Loading the dataset first...");
            loadDataset();
            System.out.println();
        }
        for (Chapter c : CHAPTERS) {
            System.out.println("--- [" + c.pod() + "] " + c.label() + " ---");
            c.action().run();
            System.out.println();
        }
    }

    /** Returns true if the user asked to quit the whole app from inside this submenu. */
    private static boolean exploreByPod(Scanner scanner) {
        while (true) {
            printPodMenu();
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("q")) {
                return true;
            }
            if (input.equals("b") || input.equals("back")) {
                return false;
            }
            Chapter match = findByCode(input);
            if (match == null) {
                System.out.println("No such chapter code.");
                continue;
            }
            System.out.println();
            match.action().run();
            System.out.println();
        }
    }

    private static void printPodMenu() {
        System.out.println();
        String[] podsInFirstSeenOrder = distinctPodsInOrder();
        for (String pod : podsInFirstSeenOrder) {
            System.out.println("-- Pod " + pod + " --");
            for (Chapter c : CHAPTERS) {
                if (c.pod().equals(pod)) {
                    System.out.println("  " + c.code() + ". " + c.label());
                }
            }
        }
        System.out.println("  b. Back    q. Quit");
        System.out.print("> ");
    }

    private static String[] distinctPodsInOrder() {
        String[] pods = new String[CHAPTERS.length];
        int count = 0;
        for (Chapter c : CHAPTERS) {
            boolean seen = false;
            for (int i = 0; i < count; i++) {
                if (pods[i].equals(c.pod())) {
                    seen = true;
                    break;
                }
            }
            if (!seen) {
                pods[count++] = c.pod();
            }
        }
        return Arrays.copyOf(pods, count);
    }

    private static Chapter findByCode(String code) {
        for (Chapter c : CHAPTERS) {
            if (c.code().equals(code)) {
                return c;
            }
        }
        return null;
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

    private static void opdQueueChapter() {
        if (!requireLoaded()) {
            return;
        }
        ServiceRequest[] pending = allWithStatus(session.requests, "PENDING");
        sortBySubmittedAt(pending);

        DynamicQueue<ServiceRequest> walkInLine = new DynamicQueue<>();
        for (ServiceRequest r : pending) {
            walkInLine.enqueue(r);
        }
        System.out.println("Enqueued " + walkInLine.size() + " real PENDING requests in arrival order (FIFO).");

        System.out.println("First 3 to be served in pure arrival order:");
        for (int i = 0; i < 3 && !walkInLine.isEmpty(); i++) {
            ServiceRequest r = walkInLine.dequeue();
            String marker = r.requestId.equals(session.focusRequest.requestId) ? "  <- today's focus request" : "";
            System.out.println("  " + r + marker);
        }
        System.out.println(walkInLine.size() + " remain in the line.");
        System.out.println("(Pure FIFO ignores urgency entirely — that's exactly why C2's priority "
                + "heap, not merged yet, is what the real dispatch queue needs instead of this.)");
    }

    private static void porterRosterChapter() {
        if (!requireLoaded()) {
            return;
        }
        Resource[] porters = allOfType(session.resources, "porter");
        CircularQueue<String> roster = new CircularQueue<>(porters.length);
        for (Resource p : porters) {
            roster.enqueue(p.resourceId);
        }
        System.out.println("Loaded " + roster.size() + " real porters into a capacity-"
                + porters.length + " circular queue.");

        System.out.println("Rotating the roster one full lap plus 2, to show it wraps instead of running out:");
        for (int i = 0; i < porters.length + 2; i++) {
            String onDeck = roster.dequeue();
            roster.enqueue(onDeck); // back of the line after their turn
            System.out.println("  turn " + (i + 1) + ": " + onDeck + " on deck");
        }
        System.out.println("Still holding all " + roster.size() + " porters (isFull()=" + roster.isFull()
                + ") — nothing lost across the wrap.");
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

    private static ServiceRequest[] allWithStatus(ServiceRequest[] requests, String status) {
        int count = 0;
        for (ServiceRequest r : requests) {
            if (status.equals(r.status)) {
                count++;
            }
        }
        ServiceRequest[] found = new ServiceRequest[count];
        int i = 0;
        for (ServiceRequest r : requests) {
            if (status.equals(r.status)) {
                found[i++] = r;
            }
        }
        return found;
    }

    private static void sortBySubmittedAt(ServiceRequest[] arr) {
        for (int i = 1; i < arr.length; i++) {
            ServiceRequest key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].submittedAt.compareTo(key.submittedAt) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static Resource[] allOfType(Resource[] resources, String type) {
        int count = 0;
        for (Resource r : resources) {
            if (r.type.equals(type)) {
                count++;
            }
        }
        Resource[] found = new Resource[count];
        int i = 0;
        for (Resource r : resources) {
            if (r.type.equals(type)) {
                found[i++] = r;
            }
        }
        return found;
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
