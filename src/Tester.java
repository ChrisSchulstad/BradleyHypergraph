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
        
        Random gen = new Random();
        gen.setSeed(0); //if seed set, all targets are the same

        for(int count = 0; count < NUM_EDGES; count++)
        {
            hG.addEdge(Utilities.createRandomEdge(gen, NUM_NODES));
        }
                
        PebbledHypergraph pGraph = hG.getPebbledHypergraph();
        
        System.out.println(pGraph);
                
        Pebbler pebbler = new Pebbler(hG, pGraph);
        
        ArrayList<Integer> nodes = new ArrayList<>();

        nodes.add(0);
        nodes.add(2);
        nodes.add(3);
        
        pebbler.pebble(nodes);
        
        HyperedgeMultiMap map = pebbler.getForwardEdges();
        System.out.println(map);
        
        int MAX_GIVENS = 3;
        PathGenerator pathGen = new PathGenerator(hG);
        PathHashMap pathMap = new PathHashMap(map, hG.size(), MAX_GIVENS);
        
        int GOAL_NODE = 4;
        pathGen.GeneratePathBackwardToLeaves(pathMap, map, GOAL_NODE);
        
        // Write code to print all the paths (from the pathHashMap) with goal node 3.
        for (Path path : pathMap.get(GOAL_NODE))
        {
            System.out.println(path);
        }
    }
}
