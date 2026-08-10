package gsoo.app;

import gsoo.algorithms.a1_binary_search.BinarySearch;
import gsoo.algorithms.b1_quicksort.QuickSort;
import gsoo.algorithms.b3_brute_force.BruteForceAssignment;
import gsoo.db.DatabaseLoader;
import gsoo.db.Location;
import gsoo.structures.Graph;
import gsoo.structures.a1_dynamic_array.DynamicArray;
import gsoo.structures.a3_stack.Stack;
import gsoo.structures.b3_btree.BTree;
import gsoo.structures.b5_map.CustomMap;
import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Console entry point — the single class named in pom.xml's shade-plugin
 * mainClass. Everything below is a menu registry: each finished slot gets
 * one MenuItem. Wiring in a newly-merged slot is a two-line addition here,
 * not a rewrite, since most of the 15 slots are still in progress.
 */
public class Main {

    private record MenuItem(String label, Runnable action) {
    }

    public static void main(String[] args) {
        MenuItem[] items = {
                new MenuItem("Dynamic array + binary search demo (A1)", Main::binarySearchDemo),
                new MenuItem("Check database connection (A3)", Main::checkDatabaseConnection),
                new MenuItem("Stack demo — audit trail undo (A3)", Main::stackDemo),
                new MenuItem("B-tree demo — request index (B3)", Main::btreeDemo),
                new MenuItem("Map demo — category lookup (B5)", Main::mapDemo),
                new MenuItem("Graph demo — adjacency list (C4)", Main::graphDemo),
                new MenuItem("Quicksort demo (B1)", Main::quickSortDemo),
                new MenuItem("Brute-force porter assignment demo (B3)", Main::bruteForceDemo),
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
    }

    private static void printMenu(MenuItem[] items) {
        System.out.println("=== Ghana Smart Service Operations Optimizer ===");
        System.out.println("(" + items.length + " of 15 slots wired in so far)");
        for (int i = 0; i < items.length; i++) {
            System.out.println("  " + (i + 1) + ". " + items[i].label());
        }
        System.out.println("  q. Quit");
        System.out.print("> ");
    }

    // ---------------- Wired demos ----------------

    private static void binarySearchDemo() {
        // Request IDs load into the dynamic array in whatever order they arrive —
        // sorted here because binary search requires it; A1's DynamicArray itself
        // doesn't enforce ordering, that's the caller's job (same as the real system,
        // where the sort step is a separate slot: quicksort/merge sort).
        DynamicArray<String> requestIds = new DynamicArray<>();
        for (String id : new String[]{"RQ-00001", "RQ-00005", "RQ-00010", "RQ-00015", "RQ-00020"}) {
            requestIds.add(id);
        }
        System.out.println("Loaded " + requestIds.size() + " request IDs into the dynamic array.");

        String[] sorted = new String[requestIds.size()];
        for (int i = 0; i < requestIds.size(); i++) {
            sorted[i] = requestIds.get(i);
        }

        BinarySearch<String> search = new BinarySearch<>();
        String target = "RQ-00015";
        int foundIndex = search.binarySearch(0, sorted.length - 1, sorted, target);
        System.out.println("binarySearch(\"" + target + "\") -> index " + foundIndex);

        String missing = "RQ-99999";
        int missingIndex = search.binarySearch(0, sorted.length - 1, sorted, missing);
        System.out.println("binarySearch(\"" + missing + "\") -> " + missingIndex + " (not present)");
    }

    private static void checkDatabaseConnection() {
        try (Connection conn = DatabaseLoader.connect()) {
            System.out.println("Connected: " + conn.getCatalog());
            Location[] locations = DatabaseLoader.loadLocations(conn);
            System.out.println("Loaded " + locations.length + " locations (expect 50)");
            for (int i = 0; i < 3 && i < locations.length; i++) {
                System.out.println("  " + locations[i]);
            }
        } catch (SQLException e) {
            System.out.println("Could not connect: " + e.getMessage());
        }
    }

    private static void stackDemo() {
        Stack<String> auditTrail = new Stack<>();
        auditTrail.push("RQ-00001 created");
        auditTrail.push("RQ-00001 assigned to porter P-04");
        auditTrail.push("RQ-00001 marked IN_TRANSIT");
        System.out.println("Pushed 3 audit events. size=" + auditTrail.size());
        System.out.println("Undo last event (pop): " + auditTrail.pop());
        System.out.println("size now=" + auditTrail.size());
    }

    private static void btreeDemo() {
        BTree<Integer, String> requestIndex = new BTree<>(2);
        requestIndex.insert(10, "RQ-00010");
        requestIndex.insert(20, "RQ-00020");
        requestIndex.insert(5, "RQ-00005");
        requestIndex.insert(15, "RQ-00015");

        System.out.println("Tree after inserts:");
        requestIndex.printTree();

        StringBuilder trace = new StringBuilder();
        String found = requestIndex.searchWithTrace(15, trace);
        System.out.println("search(15) -> " + found);
        System.out.print(trace);
    }

    private static void mapDemo() {
        CustomMap<String, Integer> categoryCost = new CustomMap<>();
        categoryCost.put("REFERRAL_IN", 1);
        categoryCost.put("BLOOD", 5);
        categoryCost.put("MEALS", 1);
        System.out.println("BLOOD -> " + categoryCost.get("BLOOD"));
        System.out.println("size=" + categoryCost.size());
    }

    private static void graphDemo() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        graph.addNode("HOSP-GATE", "gateway");
        graph.addNode("WARD-A", "ward");
        graph.addNode("THEATRE-1", "theatre");
        graph.addEdge("HOSP-GATE", "WARD-A", 50.0, 60.0, 1.0, false);
        graph.addEdge("WARD-A", "THEATRE-1", 30.0, 40.0, 1.3, false);

        System.out.println("nodes=" + graph.nodeCount() + " edges=" + graph.edgeCount());
        System.out.println("Neighbors of HOSP-GATE:");
        for (Graph.Edge e : graph.getNeighbors("HOSP-GATE")) {
            double effectiveCost = e.travelTimeSecs * e.roadConditionWeight;
            System.out.println("  " + e.fromId + " -> " + e.toId + " cost=" + effectiveCost);
        }
    }

    private static void quickSortDemo() {
        int[] arr = {10, 7, 8, 9, 1, 5};
        System.out.println("Before: " + Arrays.toString(arr));
        QuickSort.quickSort(arr, 0, arr.length - 1);
        System.out.println("After:  " + Arrays.toString(arr));
    }

    private static void bruteForceDemo() {
        int[][] cost = {
                {4, 2, 8},
                {4, 3, 7},
                {3, 1, 6},
        };
        BruteForceAssignment.Result result = new BruteForceAssignment().solve(cost);
        System.out.println("Best assignment (porter -> job): " + Arrays.toString(result.assignment));
        System.out.println("Total cost: " + result.totalCost);
        System.out.println("Permutations tried: " + result.permutationsTried);
    }
}
