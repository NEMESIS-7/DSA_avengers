package gsoo.algorithms.a4_bfs;

import gsoo.structures.c5_graph_adjacency_matrix.Graph;
import gsoo.structures.c4_graph_adjacency_list.AdjacencyListGraph;

public class RealDatasetLoader {

    private static final String[][] LOCATIONS = {
            // {id, type}
            {"EXT-C01", "community"}, {"EXT-C02", "community"}, {"EXT-C03", "community"},
            {"EXT-C04", "community"}, {"EXT-C05", "community"}, {"EXT-C06", "community"},
            {"EXT-C07", "community"}, {"EXT-C08", "community"},
            {"EXT-H01", "chps"}, {"EXT-H02", "mission_hospital"}, {"EXT-H03", "chps"},
            {"EXT-H04", "chps"}, {"EXT-H05", "chps"}, {"EXT-H06", "chps"},
            {"EXT-HC01", "health_centre"}, {"EXT-HC02", "health_centre"},
            {"EXT-HC03", "health_centre"}, {"EXT-HC04", "health_centre"},
            {"EXT-R01", "regional_hospital"},
            {"GATE-01", "gateway"},
            {"INT-01", "opd"}, {"INT-02", "opd"}, {"INT-03", "opd"}, {"INT-04", "opd"},
            {"INT-05", "opd"}, {"INT-06", "opd"}, {"INT-07", "opd"},
            {"INT-08", "emergency"},
            {"INT-09", "ward"}, {"INT-10", "ward"}, {"INT-11", "ward"}, {"INT-12", "ward"}, {"INT-13", "ward"},
            {"INT-14", "theatre"}, {"INT-15", "theatre"}, {"INT-16", "theatre"},
            {"INT-17", "lab"}, {"INT-18", "lab"}, {"INT-19", "lab"},
            {"INT-20", "pharmacy"}, {"INT-21", "pharmacy"},
            {"INT-22", "store"}, {"INT-23", "store"},
            {"INT-24", "support"}, {"INT-25", "support"}, {"INT-26", "support"}, {"INT-27", "support"},
            {"INT-28", "store"},
            {"INT-29", "admin"}, {"INT-30", "admin"}
    };

    // {roadId, fromId, toId, distanceM, travelTimeS, weight, isClosed}
    private static final Object[][] ROADS = {
            {"R-001", "EXT-C01", "EXT-HC02", 2412.0, 325.0, 1.4, false},
            {"R-002", "EXT-C02", "EXT-H01", 3753.0, 550.0, 1.0, false},
            {"R-003", "EXT-C03", "EXT-HC02", 2643.0, 404.0, 1.4, false},
            {"R-004", "EXT-C04", "EXT-HC01", 5967.0, 389.0, 1.4, false},
            {"R-005", "EXT-C05", "EXT-HC03", 4956.0, 332.0, 1.0, false},
            {"R-006", "EXT-C06", "EXT-H02", 2267.0, 523.0, 1.0, false},
            {"R-007", "EXT-C07", "EXT-HC01", 5639.0, 327.0, 1.4, false},
            {"R-008", "EXT-C08", "EXT-HC03", 3128.0, 858.0, 1.0, false},
            {"R-009", "EXT-H01", "EXT-HC02", 3805.0, 819.0, 1.8, false},
            {"R-010", "EXT-H02", "GATE-01", 5561.0, 439.0, 1.0, false},
            {"R-011", "EXT-H03", "EXT-HC01", 5462.0, 708.0, 1.4, false},
            {"R-012", "EXT-H04", "EXT-HC03", 3273.0, 580.0, 1.4, false},
            {"R-013", "EXT-H05", "EXT-HC04", 2837.0, 454.0, 1.4, false},
            {"R-014", "EXT-H06", "EXT-HC04", 2792.0, 727.0, 1.4, false},
            {"R-015", "EXT-HC01", "GATE-01", 8945.0, 1141.0, 1.0, false},
            {"R-016", "EXT-HC02", "GATE-01", 9977.0, 1540.0, 1.8, false},
            {"R-017", "EXT-HC03", "GATE-01", 5022.0, 1375.0, 1.0, false},
            {"R-018", "EXT-HC04", "GATE-01", 8522.0, 1200.0, 1.8, false},
            {"R-019", "GATE-01", "EXT-R01", 39000.0, 2700.0, 1.0, false},
            {"R-020", "EXT-C01", "EXT-H01", 6566.0, 1040.0, 1.4, false},
            {"R-021", "EXT-C02", "EXT-HC02", 6229.0, 693.0, 1.0, false},
            {"R-022", "EXT-C03", "EXT-H02", 7271.0, 442.0, 1.4, false},
            {"R-023", "EXT-C04", "EXT-H03", 1875.0, 766.0, 1.8, false},
            {"R-024", "EXT-C05", "EXT-H04", 7832.0, 892.0, 1.4, false},
            {"R-025", "EXT-C06", "EXT-HC01", 2153.0, 776.0, 1.0, false},
            {"R-026", "EXT-C07", "EXT-H03", 8598.0, 506.0, 1.4, false},
            {"R-027", "EXT-C08", "EXT-H06", 4613.0, 869.0, 1.8, false},
            {"R-028", "EXT-H01", "EXT-H02", 5214.0, 1047.0, 1.4, false},
            {"R-029", "EXT-H03", "EXT-HC02", 2832.0, 1058.0, 1.8, false},
            {"R-030", "EXT-H04", "EXT-H06", 4410.0, 729.0, 1.4, false},
            {"R-031", "EXT-H05", "EXT-H06", 6990.0, 846.0, 1.0, false},
            {"R-032", "EXT-HC01", "EXT-HC02", 7249.0, 446.0, 1.4, false},
            {"R-033", "EXT-HC02", "EXT-HC03", 6490.0, 650.0, 1.8, false},
            {"R-034", "EXT-HC03", "EXT-HC04", 5875.0, 801.0, 1.4, false},
            {"R-035", "EXT-H02", "GATE-01", 2838.0, 1246.0, 2.5, false}, // DUPLICATE of R-010's pair — see note
            {"R-036", "GATE-01", "INT-08", 63.0, 89.0, 1.0, false},
            {"R-037", "GATE-01", "INT-01", 96.0, 162.0, 1.3, false},
            {"R-038", "INT-01", "INT-02", 43.0, 103.0, 1.0, false},
            {"R-039", "INT-01", "INT-03", 113.0, 34.0, 1.0, false},
            {"R-040", "INT-01", "INT-04", 44.0, 28.0, 1.3, false},
            {"R-041", "INT-03", "INT-08", 118.0, 100.0, 1.0, false},
            {"R-042", "INT-03", "INT-05", 66.0, 88.0, 1.3, false},
            {"R-043", "INT-04", "INT-05", 23.0, 74.0, 1.0, false},
            {"R-044", "INT-05", "INT-06", 87.0, 100.0, 1.0, false},
            {"R-045", "INT-06", "INT-07", 42.0, 147.0, 1.0, false},
            {"R-046", "INT-08", "INT-14", 65.0, 137.0, 1.6, false},
            {"R-047", "INT-08", "INT-09", 33.0, 87.0, 1.3, false},
            {"R-048", "INT-08", "INT-10", 32.0, 83.0, 1.3, false},
            {"R-049", "INT-08", "INT-22", 110.0, 163.0, 1.6, false},
            {"R-050", "INT-09", "INT-10", 83.0, 87.0, 1.0, false},
            {"R-051", "INT-10", "INT-11", 110.0, 169.0, 1.0, false},
            {"R-052", "INT-11", "INT-12", 69.0, 169.0, 1.0, false},
            {"R-053", "INT-12", "INT-13", 66.0, 112.0, 1.0, false},
            {"R-054", "INT-14", "INT-15", 43.0, 55.0, 1.0, false},
            {"R-055", "INT-15", "INT-16", 80.0, 146.0, 1.0, false},
            {"R-056", "INT-14", "INT-23", 26.0, 32.0, 2.0, false},
            {"R-057", "INT-15", "INT-23", 29.0, 59.0, 2.0, false},
            {"R-058", "INT-17", "INT-18", 95.0, 60.0, 1.0, false},
            {"R-059", "INT-18", "INT-19", 116.0, 128.0, 1.0, false},
            {"R-060", "INT-09", "INT-17", 91.0, 36.0, 1.3, false},
            {"R-061", "INT-10", "INT-17", 64.0, 117.0, 1.3, false},
            {"R-062", "INT-11", "INT-17", 91.0, 139.0, 1.3, false},
            {"R-063", "INT-12", "INT-17", 82.0, 84.0, 1.3, false},
            {"R-064", "INT-13", "INT-17", 85.0, 22.0, 1.3, false},
            {"R-065", "INT-20", "INT-21", 102.0, 49.0, 1.0, false},
            {"R-066", "INT-21", "INT-09", 102.0, 157.0, 1.3, false},
            {"R-067", "INT-21", "INT-10", 111.0, 88.0, 1.3, false},
            {"R-068", "INT-21", "INT-11", 113.0, 107.0, 1.3, false},
            {"R-069", "INT-21", "INT-12", 29.0, 95.0, 1.3, false},
            {"R-070", "INT-21", "INT-13", 70.0, 60.0, 1.3, false},
            {"R-071", "INT-22", "INT-14", 73.0, 20.0, 1.6, false},
            {"R-072", "INT-22", "INT-15", 107.0, 87.0, 1.6, false},
            {"R-073", "INT-22", "INT-12", 79.0, 65.0, 1.3, false},
            {"R-074", "INT-24", "INT-09", 79.0, 47.0, 3.0, false},
            {"R-075", "INT-24", "INT-10", 95.0, 96.0, 3.0, false},
            {"R-076", "INT-24", "INT-08", 96.0, 149.0, 1.6, false},
            {"R-077", "INT-25", "INT-26", 92.0, 70.0, 1.0, false},
            {"R-078", "INT-25", "INT-09", 34.0, 115.0, 1.0, false},
            {"R-079", "INT-25", "INT-10", 112.0, 61.0, 1.0, false},
            {"R-080", "INT-25", "INT-11", 84.0, 155.0, 1.0, false},
            {"R-081", "INT-25", "INT-12", 15.0, 173.0, 1.0, false},
            {"R-082", "INT-25", "INT-13", 56.0, 145.0, 1.0, false},
            {"R-083", "INT-26", "INT-09", 17.0, 48.0, 1.0, false},
            {"R-084", "INT-26", "INT-10", 61.0, 98.0, 1.0, false},
            {"R-085", "INT-26", "INT-11", 45.0, 34.0, 1.0, false},
            {"R-086", "INT-26", "INT-12", 45.0, 165.0, 1.0, false},
            {"R-087", "INT-26", "INT-13", 25.0, 41.0, 1.0, false},
            {"R-088", "INT-27", "INT-14", 108.0, 144.0, 1.3, false},
            {"R-089", "INT-27", "INT-17", 119.0, 37.0, 1.3, false},
            {"R-090", "INT-27", "INT-18", 112.0, 156.0, 1.3, false},
            {"R-091", "INT-28", "INT-20", 113.0, 52.0, 1.0, false},
            {"R-092", "INT-28", "INT-23", 31.0, 141.0, 1.0, false},
            {"R-093", "INT-29", "INT-30", 85.0, 62.0, 1.0, false},
            {"R-094", "INT-29", "INT-01", 48.0, 155.0, 1.0, false},
            {"R-095", "INT-29", "INT-02", 92.0, 128.0, 1.0, false},
            {"R-096", "INT-02", "INT-30", 19.0, 93.0, 1.0, false},
            {"R-097", "INT-06", "INT-08", 104.0, 56.0, 1.3, false},
            {"R-098", "INT-14", "INT-08", 74.0, 114.0, 1.6, false}, // DUPLICATE of R-046's pair — see note
            {"R-099", "INT-20", "INT-08", 100.0, 44.0, 1.3, false},
            {"R-100", "INT-23", "INT-08", 73.0, 173.0, 1.6, false}
    };

    public static Graph load() {
        AdjacencyListGraph graph = new AdjacencyListGraph();

        for (String[] loc : LOCATIONS) {
            graph.addNode(loc[0], loc[1]);
        }

        int loaded = 0;
        int skipped = 0;
        for (Object[] road : ROADS) {
            String roadId = (String) road[0];
            String fromId = (String) road[1];
            String toId = (String) road[2];
            double distanceM = (Double) road[3];
            double travelTimeS = (Double) road[4];
            double weight = (Double) road[5];
            boolean isClosed = (Boolean) road[6];

            if (graph.hasEdge(fromId, toId)) {
                System.out.println("SKIPPED " + roadId + " (" + fromId + " <-> " + toId
                        + "): edge already exists between these two nodes — duplicate in seed data");
                skipped++;
                continue;
            }
            graph.addEdge(fromId, toId, distanceM, travelTimeS, weight, false, isClosed);
            loaded++;
        }

        System.out.println("Loaded " + graph.nodeCount() + " locations, "
                + loaded + " roads (" + skipped + " skipped as duplicates)");
        System.out.println();

        return graph;
    }
}