package gsoo.algorithms.c4_prim;

import gsoo.app.Session;
import gsoo.structures.Graph;

public class PrimRealDataTrace {
    public static void main(String[] args) throws Exception {
        Session session = new Session();
        session.load();

        System.out.println("Graph loaded: " + session.graph.nodeCount() + " nodes, "
            + session.graph.edgeCount() + " edges");

        String startId = session.graph.getAllNodeIds()[0];
        System.out.println("Starting Prim's from: " + startId);
        System.out.println();

        PrimMST.MSTResult result = new PrimMST().run(session.graph, startId, true);

        System.out.println();
        System.out.println("Connected: " + result.connected);
        System.out.println("Total MST cost: " + result.totalCost);
        System.out.println("Edges in MST: " + result.edges.length);

        session.close();
    }
}