package gsoo.algorithms.c2_dijkstra;

import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evidence generator for C2: rebuilds the real road graph straight out of the
 * committed seed data (sql/seed/locations_seed.sql + sql/seed/roads_seed.sql —
 * no live DB required, so reproducible from a clean checkout) and runs traced
 * Dijkstra on it. Output feeds docs/evidence/c2-trace-tables.md directly — the
 * numbers there are copied from a real run of this class, not typed by hand.
 *
 * Two routes are traced, one per layer:
 *   1. External referral: EXT-C01 (Agomeda Township) -> GATE-01 (Ambulance Bay)
 *   2. Internal porter/specimen run: INT-01 (OPD Reception) -> INT-17 (Laboratory)
 */
public class DijkstraTraceDemo {

    private static final String LOCATIONS_SEED = "sql/seed/locations_seed.sql";
    private static final String ROADS_SEED = "sql/seed/roads_seed.sql";

    private static final Pattern LOCATION =
            Pattern.compile("'([A-Z]+-[A-Z]*\\d+)', '[^']*', '(EXTERNAL|INTERNAL)', '([^']*)'");
    private static final Pattern ROAD =
            Pattern.compile("'([A-Z]+-[A-Z]*\\d+)', '([A-Z]+-[A-Z]*\\d+)', '([A-Z]+-[A-Z]*\\d+)', '[^']*', "
                    + "([0-9.]+), ([0-9.]+), ([0-9.]+), (TRUE|FALSE)");

    public static void main(String[] args) throws IOException {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        loadLocations(graph);
        loadRoads(graph);
        System.out.println("Real dataset graph rebuilt from seed files: "
                + graph.nodeCount() + " locations, " + graph.edgeCount() + " roads.");

        traceRoute(graph, "EXT-C01", "GATE-01", "External referral: community -> ambulance bay");
        System.out.println();
        traceRoute(graph, "INT-01", "INT-17", "Internal run: OPD reception -> laboratory");
    }

    private static void traceRoute(AdjacencyListGraph graph, String source, String target, String title) {
        StringBuilder trace = new StringBuilder();
        Dijkstra.Result result = Dijkstra.shortestPathsWithTrace(graph, source, trace);

        System.out.println("=== " + title + " ===");
        System.out.println("Shortest path: " + source + " -> " + target);
        System.out.print(trace);
        System.out.println("Path (" + result.pathTo(target).length + " stops): "
                + String.join(" -> ", result.pathTo(target)));
        System.out.println("Total cost: " + format(result.distance[index(result, target)]));
        System.out.println("Nodes finalized: " + result.finalizeOrder.length);
    }

    private static void loadLocations(AdjacencyListGraph graph) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOCATIONS_SEED))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = LOCATION.matcher(line);
                if (m.find()) {
                    graph.addNode(m.group(1), m.group(3));
                }
            }
        }
    }

    private static void loadRoads(AdjacencyListGraph graph) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(ROADS_SEED))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = ROAD.matcher(line);
                if (m.find() && !graph.hasEdge(m.group(2), m.group(3))) {
                    graph.addEdge(m.group(2), m.group(3),
                            Double.parseDouble(m.group(4)),
                            Double.parseDouble(m.group(5)),
                            Double.parseDouble(m.group(6)),
                            false,
                            "TRUE".equalsIgnoreCase(m.group(7)));
                }
            }
        }
    }

    private static int index(Dijkstra.Result r, String nodeId) {
        for (int i = 0; i < r.nodeIds.length; i++) {
            if (r.nodeIds[i].equals(nodeId)) {
                return i;
            }
        }
        return -1;
    }

    private static String format(double d) {
        if (d == Dijkstra.INFINITY) {
            return "inf";
        }
        return String.format("%.1f", d);
    }
}