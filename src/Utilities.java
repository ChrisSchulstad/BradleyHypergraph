
package hypergraph;

import java.util.ArrayList;
import java.util.Random;

public class Utilities 
{
    public static ArrayList genSubset(int size, int low, int high)
    {
        ArrayList ary = new ArrayList();
        int count = 0;
        Random gen = new Random();
        
        while(count < size)
        {
            int num = gen.nextInt(high - low) + low;
            
            if(!ary.contains(num))
            {
                ary.add(num);
                count++;
            }
        }
        return ary;
    }
    
    public static Hyperedge createRandomEdge(int numNodes)
    {
        Random gen = new Random();
        
        Hyperedge newEdge = new Hyperedge(new Annotation(), gen.nextInt(numNodes), Utilities.genSubset(gen.nextInt(numNodes) + 1, 0, numNodes));
        
        for(int source: newEdge.sourceNodes)
        {
            if(newEdge.targetNode == source)
            {
                newEdge = createRandomEdge(numNodes);
            }
        }
        return newEdge;
    }
}
