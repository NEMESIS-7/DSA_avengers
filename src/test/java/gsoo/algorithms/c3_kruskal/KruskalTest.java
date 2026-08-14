package gsoo.algorithms.c3_kruskal;

import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;
import gsoo.structures.Graph;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KruskalTest {

    @Test
    void shouldGenerateRealDatasetKruskalTrace() throws Exception {

        Path locationsPath =
                Paths.get("sql", "seed", "locations_seed.sql");

        Path roadsPath =
                Paths.get("sql", "seed", "roads_seed.sql");

        AdjacencyListGraph graph =
                new AdjacencyListGraph();

        /*
         * Parse the team's REAL 50-location dataset.
         *
         * We only need:
         * group 1 = location ID
         * group 2 = location type
         */
        Pattern locationPattern = Pattern.compile(
                "^\\('([^']+)',\\s*'[^']*',\\s*'[^']*',\\s*'([^']+)'"
        );

        int locationCount = 0;

        for (String line : Files.readAllLines(locationsPath)) {

            Matcher matcher =
                    locationPattern.matcher(line.trim());

            if (matcher.find()) {

                String id = matcher.group(1);
                String type = matcher.group(2);

                graph.addNode(id, type);

                locationCount++;
            }
        }

        /*
         * Parse the team's REAL 100-road dataset.
         *
         * Groups:
         * 1 = road ID
         * 2 = from location
         * 3 = to location
         * 4 = distance
         * 5 = travel time
         * 6 = road condition weight
         * 7 = closed/open
         */
        Pattern roadPattern = Pattern.compile(
                "^\\('([^']+)',\\s*'([^']+)',\\s*'([^']+)',\\s*'[^']*'," +
                        "\\s*([0-9.]+),\\s*([0-9.]+),\\s*([0-9.]+)," +
                        "\\s*(TRUE|FALSE)\\)[,;]?$"
        );

        int roadCount = 0;

        /*
         * The real dataset can contain parallel road records:
         * two different road IDs connecting the same pair of locations.
         *
         * C4's Graph is a simple graph and therefore permits only one
         * edge between a pair of vertices.
         *
         * For Kruskal, when parallel edges exist, keeping the cheaper
         * effective-cost edge is sufficient because the more expensive
         * parallel edge can never improve the MST.
         */

        String[] roadFrom = new String[100];
        String[] roadTo = new String[100];

        double[] roadDistance = new double[100];
        double[] roadTravelTime = new double[100];
        double[] roadWeight = new double[100];

        int uniqueRoadCount = 0;
        int duplicateRoadCount = 0;

        for (String line : Files.readAllLines(roadsPath)) {

            Matcher matcher =
                    roadPattern.matcher(line.trim());

            if (matcher.matches()) {

                roadCount++;

                String fromId = matcher.group(2);
                String toId = matcher.group(3);

                double distance =
                        Double.parseDouble(matcher.group(4));

                double travelTime =
                        Double.parseDouble(matcher.group(5));

                double conditionWeight =
                        Double.parseDouble(matcher.group(6));

                boolean closed =
                        Boolean.parseBoolean(
                                matcher.group(7).toLowerCase()
                        );

                // Closed roads do not participate in the active graph.
                if (closed) {
                    continue;
                }

                int existingIndex = -1;

                /*
                 * Search for another usable road connecting
                 * the same two locations.
                 *
                 * Roads are undirected, so A-B and B-A
                 * represent the same endpoint pair.
                 */
                for (int i = 0; i < uniqueRoadCount; i++) {

                    boolean sameDirection =
                            roadFrom[i].equals(fromId)
                                    && roadTo[i].equals(toId);

                    boolean reverseDirection =
                            roadFrom[i].equals(toId)
                                    && roadTo[i].equals(fromId);

                    if (sameDirection || reverseDirection) {
                        existingIndex = i;
                        break;
                    }
                }

                if (existingIndex == -1) {

                    // First road seen for this endpoint pair.
                    roadFrom[uniqueRoadCount] = fromId;
                    roadTo[uniqueRoadCount] = toId;

                    roadDistance[uniqueRoadCount] = distance;
                    roadTravelTime[uniqueRoadCount] = travelTime;
                    roadWeight[uniqueRoadCount] = conditionWeight;

                    uniqueRoadCount++;

                } else {

                    duplicateRoadCount++;

                    double existingCost =
                            roadTravelTime[existingIndex]
                                    * roadWeight[existingIndex];

                    double newCost =
                            travelTime
                                    * conditionWeight;

                    /*
                     * Keep whichever parallel road has the
                     * smaller effective traversal cost.
                     */
                    if (newCost < existingCost) {

                        roadFrom[existingIndex] = fromId;
                        roadTo[existingIndex] = toId;

                        roadDistance[existingIndex] = distance;
                        roadTravelTime[existingIndex] = travelTime;
                        roadWeight[existingIndex] = conditionWeight;
                    }
                }
            }
        }

        /*
         * Now construct C4's simple graph using the cheapest
         * usable road for each endpoint pair.
         */
        for (int i = 0; i < uniqueRoadCount; i++) {

            graph.addEdge(
                    roadFrom[i],
                    roadTo[i],
                    roadDistance[i],
                    roadTravelTime[i],
                    roadWeight[i],
                    false
            );
        }

        System.out.println(
                "Road records in dataset: " + roadCount
        );

        System.out.println(
                "Unique usable graph edges: " + uniqueRoadCount
        );

        System.out.println(
                "Parallel road records collapsed: " + duplicateRoadCount
        );

        System.out.println(
                "\n=== C3 REAL HOSPITAL DATASET ==="
        );

        System.out.println(
                "Locations loaded: " + locationCount
        );

        System.out.println(
                "Road records loaded: " + roadCount
        );

        Kruskal kruskal =
                new Kruskal();

        Kruskal.Result result =
                kruskal.run(graph);

        System.out.println(
                "\n=== C3 KRUSKAL REAL-DATASET TRACE ==="
        );

        System.out.println(
                "Step | From | To | Effective Cost"
        );

        System.out.println(
                "----------------------------------------"
        );

        for (int i = 0;
             i < result.mstEdges.length;
             i++) {

            Graph.Edge edge =
                    result.mstEdges[i];

            double cost =
                    edge.travelTimeSecs
                            * edge.roadConditionWeight;

            System.out.printf(
                    "%d | %s | %s | %.2f%n",
                    i + 1,
                    edge.fromId,
                    edge.toId,
                    cost
            );
        }

        System.out.println(
                "----------------------------------------"
        );

        System.out.println(
                "MST edges selected: "
                        + result.mstEdges.length
        );

        System.out.printf(
                "Total MST effective cost: %.2f%n",
                result.totalCost
        );

        /*
         * Verify that we really consumed the expected
         * project dataset.
         */
        assertEquals(
                50,
                locationCount
        );

        assertEquals(
                100,
                roadCount
        );

        /*
         * A connected graph with V vertices
         * must have V - 1 MST edges.
         */
        assertEquals(
                locationCount - 1,
                result.mstEdges.length
        );
    }

    /**
     * NORMAL CASE
     */
    @Test
    void shouldProduceMinimumSpanningTree() {

        AdjacencyListGraph graph =
                new AdjacencyListGraph();

        graph.addNode("A", "ward");
        graph.addNode("B", "lab");
        graph.addNode("C", "pharmacy");
        graph.addNode("D", "gateway");

        /*
         * effective cost =
         * travelTimeSecs * roadConditionWeight
         */

        // Cost = 1
        graph.addEdge(
                "A", "B",
                10,
                1,
                1.0,
                false
        );

        // Cost = 2
        graph.addEdge(
                "B", "C",
                10,
                2,
                1.0,
                false
        );

        // Cost = 5
        graph.addEdge(
                "A", "C",
                10,
                5,
                1.0,
                false
        );

        // Cost = 1.5
        graph.addEdge(
                "C", "D",
                10,
                1.5,
                1.0,
                false
        );

        // Cost = 4
        graph.addEdge(
                "B", "D",
                10,
                4,
                1.0,
                false
        );

        Kruskal kruskal =
                new Kruskal();

        Kruskal.Result result =
                kruskal.run(graph);

        /*
         * Expected MST:
         *
         * A-B = 1
         * C-D = 1.5
         * B-C = 2
         *
         * Total = 4.5
         */

        assertEquals(
                3,
                result.mstEdges.length
        );

        assertEquals(
                4.5,
                result.totalCost,
                0.0001
        );
    }

    /**
     * BOUNDARY CASE
     */
    @Test
    void singleNodeShouldHaveEmptyMst() {

        AdjacencyListGraph graph =
                new AdjacencyListGraph();

        graph.addNode(
                "AMBULANCE-BAY",
                "gateway"
        );

        Kruskal kruskal =
                new Kruskal();

        Kruskal.Result result =
                kruskal.run(graph);

        assertEquals(
                0,
                result.mstEdges.length
        );

        assertEquals(
                0.0,
                result.totalCost,
                0.0001
        );
    }

    /**
     * INVALID INPUT CASE
     */
    @Test
    void nullGraphShouldThrowException() {

        Kruskal kruskal =
                new Kruskal();

        assertThrows(
                IllegalArgumentException.class,
                () -> kruskal.run(null)
        );
    }

    @Test
    void shouldGenerateKruskalTraceEvidence() {

        AdjacencyListGraph graph =
                new AdjacencyListGraph();

        graph.addNode("AMBULANCE-BAY", "gateway");
        graph.addNode("OPD", "department");
        graph.addNode("LAB", "department");
        graph.addNode("PHARMACY", "department");

        // Effective cost = travelTimeSecs * roadConditionWeight

        graph.addEdge(
                "AMBULANCE-BAY", "OPD",
                50,
                10,
                1.0,
                false
        ); // cost = 10

        graph.addEdge(
                "OPD", "LAB",
                40,
                8,
                1.0,
                false
        ); // cost = 8

        graph.addEdge(
                "LAB", "PHARMACY",
                30,
                5,
                1.0,
                false
        ); // cost = 5

        graph.addEdge(
                "AMBULANCE-BAY", "LAB",
                70,
                15,
                1.0,
                false
        ); // cost = 15

        graph.addEdge(
                "OPD", "PHARMACY",
                60,
                12,
                1.0,
                false
        ); // cost = 12

        Kruskal kruskal =
                new Kruskal();

        Kruskal.Result result =
                kruskal.run(graph);

        StringBuilder trace =
                new StringBuilder();

        trace.append("\n=== C3 KRUSKAL MST TRACE ===\n");
        trace.append("Step | From | To | Effective Cost\n");
        trace.append("--------------------------------\n");

        for (int i = 0; i < result.mstEdges.length; i++) {

            Graph.Edge edge =
                    result.mstEdges[i];

            double cost =
                    edge.travelTimeSecs
                            * edge.roadConditionWeight;

            trace.append(i + 1)
                    .append(" | ")
                    .append(edge.fromId)
                    .append(" | ")
                    .append(edge.toId)
                    .append(" | ")
                    .append(cost)
                    .append("\n");
        }

        trace.append("--------------------------------\n");

        trace.append("Total MST Cost: ")
                .append(result.totalCost)
                .append("\n");

        trace.append("Edges Selected: ")
                .append(result.mstEdges.length)
                .append("\n");

        System.out.println(trace);

        assertEquals(
                graph.nodeCount() - 1,
                result.mstEdges.length
        );
    }
}