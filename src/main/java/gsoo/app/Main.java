package gsoo.app;

import gsoo.algorithms.a1_binary_search.BinarySearch;
import gsoo.algorithms.a2_linear_search.LinearSearch;
import gsoo.algorithms.a3_dfs.DFS;
import gsoo.algorithms.a5_insertion_sort.InsertionSort;
import gsoo.algorithms.b1_quicksort.QuickSort;
import gsoo.algorithms.b2_merge_sort.MergeSort;
import gsoo.algorithms.b3_brute_force.BruteForceAssignment;
import gsoo.algorithms.b4_selection_sort.SelectionSort;
import gsoo.algorithms.c1_greedy.GreedyAssignment;
import gsoo.algorithms.c2_dijkstra.Dijkstra;
import gsoo.algorithms.c3_kruskal.Kruskal;
import gsoo.algorithms.c4_prim.PrimMST;
import gsoo.algorithms.c5_dp_knapsack.HospitalKnapsackPlanner;
import gsoo.algorithms.c5_dp_knapsack.Knapsack;
import gsoo.db.AuditEvent;
import gsoo.db.DatabaseLoader;
import gsoo.db.Location;
import gsoo.db.Resource;
import gsoo.db.Road;
import gsoo.db.ServiceRequest;
import gsoo.structures.Graph;
import gsoo.structures.a2_linked_list.LinkedList;
import gsoo.structures.a3_stack.Stack;
import gsoo.structures.a4_queue_circular_queue.CircularQueue;
import gsoo.structures.a4_queue_circular_queue.DynamicQueue;
import gsoo.structures.a5_deque.CustomDeque;
import gsoo.structures.b2_avl_tree.AVLTree;
import gsoo.structures.b4_hash_table.HashTable;
import gsoo.structures.c1_set.HashSet;
import gsoo.structures.c2_heap.Heap;
import gsoo.structures.c5_graph_adjacency_matrix.AdjacencyMatrixGraph;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
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
 * Only as true a story as what's actually merged — all 15 slots now have
 * real, tested code wired in here.
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
            new Chapter("c2a", "C", "Priority dispatch queue — heap", Main::heapDispatchChapter),
            new Chapter("c2b", "C", "Fastest route, multi-hop — Dijkstra", Main::dijkstraChapter),
            new Chapter("b1", "B", "Sort today's queue by urgency (quicksort)", Main::quickSortChapter),
            new Chapter("b3b", "B", "Brute-force assign 3 porters to 3 jobs", Main::bruteForceChapter),
            new Chapter("b2a", "B", "Stress-test the AVL tree's balance", Main::avlChapter),
            new Chapter("b2b", "B", "Sort fairly — merge sort's stability", Main::mergeSortChapter),
            new Chapter("b4a", "B", "Look it up in the hash table", Main::hashTableChapter),
            new Chapter("b4b", "B", "Selection sort — the deliberate worst performer", Main::selectionSortChapter),
            new Chapter("c3", "C", "Minimum spanning tree — Kruskal", Main::kruskalChapter),
            new Chapter("c4b", "C", "Minimum spanning tree — Prim", Main::primChapter),
            new Chapter("c5", "C", "Same MST, matrix-backed instead of list-backed", Main::adjacencyMatrixChapter),
            new Chapter("a3b", "A", "Departments still reachable if a corridor closes", Main::dfsChapter),
            new Chapter("a5a", "A", "Triage deque — critical to front", Main::dequeChapter),
            new Chapter("a5b", "A", "Sort near-sorted arrivals — insertion sort", Main::insertionSortChapter),
            new Chapter("c1a", "C", "Track locked-down departments — hash set", Main::hashSetChapter),
            new Chapter("c1b", "C", "Greedy vs brute-force assignment", Main::greedyChapter),
            new Chapter("c5b", "C", "Plan one shift under the time budget — DP knapsack", Main::knapsackChapter),
            new Chapter("a2a", "A", "Its event timeline — linked list", Main::eventTimelineChapter),
            new Chapter("a2b", "A", "Scan for it without an index — linear search", Main::linearSearchChapter),
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
        System.out.println("  3. Explore chapters by pod (" + distinctSlotCount() + " of 15 slots wired in so far)");
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

    // Several slots (A4, B2, B3, B4, C4) contribute more than one chapter, so
    // CHAPTERS.length overcounts "slots wired in". A chapter's slot is its
    // code's first two characters (pod letter + single-digit slot number,
    // e.g. "a4a" and "a4b" both belong to slot "a4") — every current code
    // follows that shape.
    private static int distinctSlotCount() {
        String[] slots = new String[CHAPTERS.length];
        int count = 0;
        for (Chapter c : CHAPTERS) {
            String slot = c.code().substring(0, 2);
            boolean seen = false;
            for (int i = 0; i < count; i++) {
                if (slots[i].equals(slot)) {
                    seen = true;
                    break;
                }
            }
            if (!seen) {
                slots[count++] = slot;
            }
        }
        return count;
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
            System.out.println("  " + e.fromId + " -> " + e.toId + " cost=" + e.effectiveCost());
        }
        System.out.println("(Deliberately stops at direct connections — see the Dijkstra chapter "
                + "for the real multi-hop fastest route from here.)");
    }

    private static void heapDispatchChapter() {
        if (!requireLoaded()) {
            return;
        }
        ServiceRequest[] pending = allWithStatus(session.requests, "PENDING");
        Heap<ServiceRequest> dispatchQueue = new Heap<>();
        for (ServiceRequest r : pending) {
            long minutesUntilDeadline = 60; // placeholder slack window shared by every request in this demo
            double priority = Config.dispatchPriority(r.urgency, 1.0, minutesUntilDeadline);
            dispatchQueue.insert(r, -priority); // heap is min-first; negate so higher priority extracts first
        }
        System.out.println("Loaded " + dispatchQueue.size()
                + " real PENDING requests into C2's priority heap, keyed by Config.dispatchPriority.");

        System.out.println("First 3 to be served under real urgency-weighted priority:");
        for (int i = 0; i < 3 && !dispatchQueue.isEmpty(); i++) {
            ServiceRequest r = dispatchQueue.extractMin();
            String marker = r.requestId.equals(session.focusRequest.requestId) ? "  <- today's focus request" : "";
            System.out.println("  " + r + marker);
        }
        System.out.println(dispatchQueue.size() + " remain in the queue.");
        System.out.println("(Contrast with the FIFO walk-in line chapter — same real requests, "
                + "but urgency now actually decides who goes first.)");
    }

    private static void dijkstraChapter() {
        if (!requireLoaded()) {
            return;
        }
        ServiceRequest r = session.focusRequest;
        Dijkstra.Result result = Dijkstra.shortestPaths(session.graph, r.sourceLocationId);
        System.out.println("Dijkstra from " + r.sourceLocationId + " (" + r.requestId + "'s origin), "
                + result.nodeIds.length + " nodes, " + result.finalizeOrder.length + " finalized.");

        if (result.isReachable(r.destinationLocationId)) {
            String[] path = result.pathTo(r.destinationLocationId);
            System.out.println("Fastest route to " + r.destinationLocationId + " ("
                    + String.format("%.1f", result.distance[indexOf(result.nodeIds, r.destinationLocationId)])
                    + " effective cost, " + path.length + " hops):");
            System.out.println("  " + String.join(" -> ", path));
        } else {
            System.out.println(r.destinationLocationId + " is not reachable from " + r.sourceLocationId
                    + " under the current road-closure state.");
        }
        System.out.println("(Closed roads are skipped entirely; flood-prone roads "
                + "(roadConditionWeight >= " + Config.FLOOD_PRONE_WEIGHT_THRESHOLD
                + ") additionally carry Config.ROUTE_PENALTY, per team-charter.md sec2.7.)");
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
        System.out.println("(Pure FIFO ignores urgency entirely — see the priority dispatch queue "
                + "chapter for what C2's heap does instead with these same real requests.)");
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
        System.out.println("(Deliberately uses direct-edge cost only, not full Dijkstra routing, to "
                + "keep this a fast 3x3 illustration — the Dijkstra chapter shows the real multi-hop cost.)");
    }

    private static void avlChapter() {
        if (!requireLoaded()) {
            return;
        }
        // Sequential inserts are the exact case that degrades a plain BST into
        // a linked list (height n-1) — this is the same stress test
        // AVLTreeTest.sequentialInsertsStayBalanced() runs, scaled up to the
        // real dataset size instead of a toy n=15.
        int n = session.requests.length;
        AVLTree<Integer> tree = new AVLTree<>();
        for (int i = 1; i <= n; i++) {
            tree.insert(i);
        }
        double log2n = Math.log(n + 1) / Math.log(2);
        System.out.println("Inserted " + n + " sequential values (the worst case for a plain BST) into the AVL tree.");
        System.out.println("Resulting height: " + tree.height() + "  (log2(" + (n + 1) + ") ~ "
                + String.format("%.1f", log2n) + " — a degenerate BST would instead be height " + (n - 1) + ")");
        System.out.println("(This is the same lesson as B1's BST from the other direction: sorted/sequential "
                + "input is B1's worst case and does nothing to B2, because B2 rebalances after every insert.)");
    }

    private record PriorityEntry(int urgency, String requestId) implements Comparable<PriorityEntry> {
        @Override
        public int compareTo(PriorityEntry other) {
            return Integer.compare(urgency, other.urgency);
        }

        @Override
        public String toString() {
            return requestId + " (urgency " + urgency + ")";
        }
    }

    private static void mergeSortChapter() {
        if (!requireLoaded()) {
            return;
        }
        PriorityEntry[] entries = new PriorityEntry[session.requests.length];
        for (int i = 0; i < session.requests.length; i++) {
            ServiceRequest r = session.requests[i];
            entries[i] = new PriorityEntry(r.urgency, r.requestId);
        }

        // Find two real requests that already share a urgency and are adjacent
        // in arrival order, so the stability claim below is checkable by eye.
        int tieA = -1, tieB = -1;
        for (int i = 0; i < entries.length - 1; i++) {
            if (entries[i].urgency() == entries[i + 1].urgency()) {
                tieA = i;
                tieB = i + 1;
                break;
            }
        }

        System.out.println("Before (first 10 of " + entries.length + "): "
                + Arrays.toString(Arrays.copyOf(entries, 10)));
        MergeSort.sort(entries);
        System.out.println("After  (first 10, ascending by urgency): "
                + Arrays.toString(Arrays.copyOf(entries, 10)));

        if (tieA != -1) {
            System.out.println("Stability check: " + session.requests[tieA].requestId + " arrived before "
                    + session.requests[tieB].requestId + " with the same urgency (" + entries[tieA].urgency() + ") — "
                    + "merge sort must keep them in that same relative order after sorting.");
        }
        System.out.println("(Contrast with the quicksort chapter: same urgency values, but merge sort's stability "
                + "means arrival order survives as the tiebreaker — that's the fairness argument B2 makes.)");
    }

    private static void hashTableChapter() {
        if (!requireLoaded()) {
            return;
        }
        HashTable table = new HashTable();
        for (ServiceRequest r : session.requests) {
            table.insert(r.requestId, r);
        }
        ServiceRequest found = table.search(session.focusRequest.requestId);
        System.out.println("Inserted all " + table.getSize() + " real requests into the hash table "
                + "(capacity " + table.getCapacity() + ", from Config.HASH_TABLE_SIZE — no separate copy of that number).");
        System.out.println("search(\"" + session.focusRequest.requestId + "\") -> " + found);
        System.out.println("Load factor: " + String.format("%.3f", table.getLoadFactor()));
    }

    private static void selectionSortChapter() {
        if (!requireLoaded()) {
            return;
        }
        ServiceRequest[] copy = Arrays.copyOf(session.requests, session.requests.length);
        int comparisons = SelectionSort.sort(copy);
        System.out.println("Sorted all " + copy.length + " real requests by requestId.");
        System.out.println("First: " + copy[0].requestId + "  Last: " + copy[copy.length - 1].requestId);
        System.out.println("Comparisons made: " + comparisons + " (always ~n^2/2 regardless of input order — "
                + "the deliberate worst-performer baseline the other sorts get measured against).");
    }

    private static void kruskalChapter() {
        if (!requireLoaded()) {
            return;
        }
        Kruskal.Result result = new Kruskal().run(session.graph);
        System.out.println("Kruskal's MST over the real " + session.graph.nodeCount() + "-node, "
                + session.graph.edgeCount() + "-edge network:");
        System.out.println("  Edges selected: " + result.mstEdges.length);
        System.out.println("  Total effective cost: " + String.format("%.2f", result.totalCost));
    }

    private static void primChapter() {
        if (!requireLoaded()) {
            return;
        }
        String startId = "GATE-01"; // the ambulance bay — the network's single external/internal join point
        if (!session.graph.hasNode(startId)) {
            System.out.println("Expected start node " + startId + " not found in the loaded graph.");
            return;
        }
        PrimMST.MSTResult result = new PrimMST().run(session.graph, startId);
        System.out.println("Prim's MST over the same network, starting from the gateway (" + startId + "):");
        System.out.println("  Edges selected: " + result.edges.length + "  connected=" + result.connected);
        System.out.println("  Total effective cost: " + String.format("%.2f", result.totalCost));
        System.out.println("(Same MST problem as the Kruskal chapter, solved by growing outward from one node "
                + "instead of sorting every edge — compare the two total costs.)");
    }

    private static void adjacencyMatrixChapter() {
        if (!requireLoaded()) {
            return;
        }
        // C5 owns the dense INTERNAL corridor layout (team-charter.md §2.8) —
        // built here as a second, matrix-backed Graph over just those real
        // locations/roads, separate from C4's list-backed graph over the whole network.
        AdjacencyMatrixGraph internalGraph = new AdjacencyMatrixGraph();
        for (Location loc : session.locations) {
            if ("INTERNAL".equals(loc.layer)) {
                internalGraph.addNode(loc.locationId, loc.type);
            }
        }
        int edgesAdded = 0;
        for (Road road : session.roads) {
            if (internalGraph.hasNode(road.fromLocationId) && internalGraph.hasNode(road.toLocationId)
                    && !internalGraph.hasEdge(road.fromLocationId, road.toLocationId)) {
                internalGraph.addEdge(road.fromLocationId, road.toLocationId,
                        road.distanceM, road.travelTimeS, road.roadConditionWeight, false, road.isClosed);
                edgesAdded++;
            }
        }
        System.out.println("Built a second graph — adjacency matrix instead of adjacency list — over just the "
                + internalGraph.nodeCount() + " real INTERNAL locations and " + edgesAdded + " real corridors "
                + "(C5's dense internal layout, vs C4's sparse external+internal list).");

        String[] internalIds = internalGraph.getAllNodeIds();
        if (internalIds.length == 0) {
            System.out.println("No INTERNAL nodes loaded — nothing to run Prim's over.");
            return;
        }
        String startId = internalIds[0];
        PrimMST.MSTResult result = new PrimMST().run(internalGraph, startId);
        System.out.println("Prim's MST over this matrix-backed graph, starting from " + startId + ":");
        System.out.println("  Edges selected: " + result.edges.length + "  connected=" + result.connected);
        System.out.println("  Total effective cost: " + String.format("%.2f", result.totalCost));
        System.out.println("(Same PrimMST class the earlier chapter used, completely unmodified — it only "
                + "depends on the Graph interface, so it runs identically against C5's matrix as it did "
                + "against C4's list. That's the point of coding against an interface.)");
    }

    private static void dfsChapter() {
        if (!requireLoaded()) {
            return;
        }
        ServiceRequest r = session.focusRequest;
        String start = r.sourceLocationId;

        DFS dfs = new DFS();
        DFS.ReachabilityResult before = dfs.reachableFrom(session.graph, start);
        System.out.println("From " + start + ", " + before.visitedCount + " of "
                + session.graph.nodeCount() + " real locations are reachable right now.");

        Graph.Edge[] neighbors = session.graph.getNeighbors(start);
        if (neighbors.length == 0) {
            System.out.println("(No outgoing roads from " + start + " to simulate closing.)");
            return;
        }
        Graph.Edge toClose = neighbors[0];
        DFS.ReachabilityResult after = dfs.reachableFrom(session.graph, start, toClose.fromId, toClose.toId);
        System.out.println("Simulating the real road " + toClose.fromId + " -> " + toClose.toId + " as closed:");
        System.out.println("  Reachable locations drops to " + after.visitedCount
                + " (" + (before.visitedCount - after.visitedCount) + " fewer).");
        System.out.println("(Real closed roads in the data are already respected too — this is one "
                + "additional closure layered on top, for the 'what if' scenario.)");
    }

    private static void dequeChapter() {
        if (!requireLoaded()) {
            return;
        }
        ServiceRequest[] pending = allWithStatus(session.requests, "PENDING");
        CustomDeque<ServiceRequest> triage = new CustomDeque<>();
        for (ServiceRequest r : pending) {
            if (r.urgency >= 4) {
                triage.addFirst(r); // critical — jump to the front
            } else {
                triage.addLast(r); // routine — join the back
            }
        }
        System.out.println("Loaded " + triage.size() + " real PENDING requests into the triage deque "
                + "(urgency >= 4 pushed to the front, everything else to the back).");
        System.out.println("Next off the front (most critical): " + triage.peekFirst());
        System.out.println("Last in line (least urgent): " + triage.peekLast());
    }

    private static void insertionSortChapter() {
        if (!requireLoaded()) {
            return;
        }
        // A small slice, deliberately — insertion sort's whole story is being
        // efficient on data that's already close to sorted, and sortWithTrace
        // prints one line per pass, so a small n keeps that story readable
        // instead of dumping 300 lines.
        int sliceSize = Math.min(8, session.requests.length);
        ServiceRequest[] slice = Arrays.copyOf(session.requests, sliceSize);
        System.out.println("Sorting the first " + sliceSize + " real requests (arrival order) by urgency:");
        InsertionSort.sortWithTrace(slice, Comparator.comparingInt(req -> req.urgency));
    }

    private static void hashSetChapter() {
        if (!requireLoaded()) {
            return;
        }
        ServiceRequest r = session.focusRequest;
        String start = r.sourceLocationId;

        Graph.Edge[] neighbors = session.graph.getNeighbors(start);
        DFS.ReachabilityResult reachable;
        if (neighbors.length == 0) {
            reachable = new DFS().reachableFrom(session.graph, start);
        } else {
            Graph.Edge toClose = neighbors[0];
            reachable = new DFS().reachableFrom(session.graph, start, toClose.fromId, toClose.toId);
        }

        HashSet visited = new HashSet();
        for (String id : reachable.reachableIds) {
            visited.add(id);
        }
        System.out.println("Tracked " + visited.size() + " real locations still reachable from " + start
                + " (with one corridor simulated closed) in a hash set.");

        String[] allIds = session.graph.getAllNodeIds();
        String lockedDown = null;
        for (String id : allIds) {
            if (!visited.contains(id)) {
                lockedDown = id;
                break;
            }
        }
        System.out.println("contains(\"" + start + "\") -> " + visited.contains(start));
        if (lockedDown != null) {
            System.out.println("contains(\"" + lockedDown + "\") -> " + visited.contains(lockedDown)
                    + "  (cut off by the simulated closure — a locked-down department)");
        } else {
            System.out.println("Every real location is still reachable — nothing locked down this time.");
        }
    }

    private static void greedyChapter() {
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
        int[] urgency = new int[3];
        for (int p = 0; p < 3; p++) {
            for (int j = 0; j < 3; j++) {
                cost[p][j] = travelCostOrFallback(porters[p].homeLocationId, pending[j].sourceLocationId);
            }
        }
        for (int j = 0; j < 3; j++) {
            urgency[j] = pending[j].urgency;
        }

        GreedyAssignment.Result greedy = new GreedyAssignment().solve(cost, urgency);
        BruteForceAssignment.Result optimal = new BruteForceAssignment().solve(cost);

        System.out.println("Same real porters/jobs as the brute-force chapter — same cost matrix, this time "
                + "with real urgency values driving greedy's job order.");
        System.out.println("Greedy total cost:      " + greedy.totalCost);
        System.out.println("Brute-force (optimal):  " + optimal.totalCost);
        if (greedy.totalCost > optimal.totalCost) {
            System.out.println("Greedy landed on a worse total cost than the guaranteed-optimal brute-force "
                    + "result — the counterexample, on real data, not a constructed one.");
        } else {
            System.out.println("Greedy matched the optimal cost this time — it doesn't always, which is exactly "
                    + "why brute force stays the guaranteed-correct baseline.");
        }
    }

    private static void knapsackChapter() {
        if (!requireLoaded()) {
            return;
        }
        try {
            Knapsack.Result result = new HospitalKnapsackPlanner().planOneShift(session.requests);
            System.out.println("Planning one shift (" + result.capacityMinutes
                    + " minutes, from Config.SHIFT_BUDGET_MINUTES) over every real PENDING request:");
            System.out.println("  Requests selected: " + result.selectedItems.length);
            System.out.println("  Minutes used: " + result.totalMinutes + " / " + result.capacityMinutes
                    + "  (remaining: " + result.remainingMinutes() + ")");
            System.out.println("  Total priority value served: " + result.totalValue);
            if (result.selectedItems.length > 0) {
                System.out.println("  First selected: " + result.selectedItems[0].requestId);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Couldn't plan this shift: " + e.getMessage());
        }
    }

    private static void eventTimelineChapter() {
        if (!requireLoaded()) {
            return;
        }
        // Read-only, append-in-order — the other real event history for a
        // request, A3's Stack (audit_events, undo-dispatch), is push/pop and
        // writes to the live DB. This is the plain "what happened, in
        // order" view: no undo, just iterate the real node chain.
        ServiceRequest r = session.focusRequest;
        LinkedList<String> timeline = new LinkedList<>();
        timeline.add(r.requestId + " CREATED at " + r.submittedAt);
        timeline.add(r.requestId + " currently " + r.status);
        if (r.assignedResourceId != null) {
            timeline.add(r.requestId + " assigned to " + r.assignedResourceId);
        }
        timeline.add(r.requestId + " deadline " + r.deadlineAt);

        System.out.println("Built a " + timeline.size() + "-event timeline for " + r.requestId
                + " and walked it with the list's own iterator:");
        for (String event : timeline) {
            System.out.println("  " + event);
        }
    }

    private static void linearSearchChapter() {
        if (!requireLoaded()) {
            return;
        }
        // Same real sorted id array the a1 chapter searches — deliberately,
        // so the comparison counts below are a fair, same-input measurement
        // of linear vs. binary search, not two different setups.
        String[] ids = new String[session.requestTable.size()];
        for (int i = 0; i < session.requestTable.size(); i++) {
            ids[i] = session.requestTable.get(i).requestId;
        }

        LinearSearch<String> search = new LinearSearch<>();
        int index = search.search(ids, session.focusRequest.requestId);
        int comparisons = index == -1 ? ids.length : index + 1;

        System.out.println("search(\"" + session.focusRequest.requestId + "\") over "
                + ids.length + " real request IDs, no index, no ordering assumed -> index " + index);
        System.out.println("Comparisons made: " + comparisons
                + " (worst case n=" + ids.length + " — contrast with the a1 chapter's binary "
                + "search on the same array, which never takes more than " + worstCaseBinarySearchComparisons(ids.length)
                + " comparisons).");
    }

    private static int worstCaseBinarySearchComparisons(int n) {
        int comparisons = 0;
        int range = n;
        while (range > 0) {
            range /= 2;
            comparisons++;
        }
        return comparisons;
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

    private static int indexOf(String[] arr, String target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(target)) {
                return i;
            }
        }
        throw new IllegalArgumentException("not found: " + target);
    }

    private static final int NO_DIRECT_ROAD_PENALTY = 9999;

    private static int travelCostOrFallback(String fromId, String toId) {
        if (!session.graph.hasNode(fromId) || !session.graph.hasNode(toId)) {
            return NO_DIRECT_ROAD_PENALTY;
        }
        for (Graph.Edge e : session.graph.getNeighbors(fromId)) {
            if (e.toId.equals(toId) || e.fromId.equals(toId)) {
                return (int) Math.round(e.effectiveCost());
            }
        }
        return NO_DIRECT_ROAD_PENALTY;
    }
}
