import java.util.ArrayList;
import java.util.Random;

public class Tester 
{
    
    public static void main(String[] args) throws Exception
    {
        Hypergraph hG = new Hypergraph();
        final int NUM_NODES = 10;
        final int NUM_EDGES = 6;
        
        for(int count = 0; count < NUM_NODES; count++)
        {
            hG.addNode(new Hypernode(count ,count));
        }
        
        for(int count = 0; count < NUM_EDGES; count++)
        {
            hG.addEdge(Utilities.createRandomEdge(NUM_NODES));
        }
                
        PebbledHypergraph pGraph = hG.getPebbledHypergraph();
        
        System.out.println(pGraph);
                
        Pebbler pebbler = new Pebbler(hG, pGraph);
        
        ArrayList<Integer> nodes = new ArrayList<>();

        nodes.add(0);
        nodes.add(1);
        
        pebbler.pebble(nodes);
        
        HyperedgeMultiMap map = pebbler.getForwardEdges();
        System.out.println(map);
    }
}
