
package hypergraph;

import java.util.ArrayList;
import java.util.Collections;

public class HyperedgeMultiMap 
{
    private final int TABLE_SIZE;
    private ArrayList<PebbledHyperedge>[] table;
    public int size;
    private Hypergraph graph;
    
    public HyperedgeMultiMap(int sz)
    {
        size = 0;
        TABLE_SIZE = sz;
        table = new ArrayList[TABLE_SIZE];
    }
    
    public void SetOriginalHypergraph(Hypergraph originalGraph)
    {
        graph = originalGraph;
    }
    
    public ArrayList<PebbledHyperedge> collectAllEdges()
    {
        ArrayList<PebbledHyperedge> edges = new ArrayList<PebbledHyperedge>();
        
        for(int ell = 0; ell < TABLE_SIZE; ell++)
        {
            if(table[ell] != null)
            {
                edges.addAll(table[ell]);
            }
        }
        
        return edges;
    }
    
//    public void put(PebbledHyperedge edge)
//    {
//        Collections.sort(edge.sourceNodes);
//        int minSrc = edge.sourceNodes.get(edge.sourceNodes.size() - 1);
//        for(int src: edge.sourceNodes)
//        {
//            
//        }
//    }
    
    public boolean putUnchecked(PebbledHyperedge thatEdge)
    {
        Collections.sort(thatEdge.sourceNodes);
        long hashVal = (thatEdge.targetNode % TABLE_SIZE);
        
        if(table[(int)hashVal] == null)
        {
            table[(int)hashVal] = new ArrayList<PebbledHyperedge>();
        }
        
        for(PebbledHyperedge edge: table[(int)hashVal])
        {
            if(edge.equals(thatEdge)) return false;
        }
        
        table[(int)hashVal].add(thatEdge);
        size++;
        
        return true;
    }
    
    public boolean putUnchecked(ArrayList<Integer> ante, int target, Annotation annot)
    {
        return putUnchecked(new PebbledHyperedge(ante, target, annot));
    }
    
    public ArrayList<PebbledHyperedge> getBasedOnGoal(int goalNodeIndex) throws Exception
    {
        if (goalNodeIndex < 0 || goalNodeIndex >= TABLE_SIZE)
        {
            throw new Exception("HyperEdgeMultimap index out of bounds (" + goalNodeIndex + ")");
        }

        return table[goalNodeIndex];
    }
    
    public boolean HasEdge(PebbledHyperedge thatEdge)
    {
        ArrayList<PebbledHyperedge> targetEdges = table[thatEdge.targetNode % TABLE_SIZE];

        if (targetEdges == null) return false;

        for(PebbledHyperedge edge:targetEdges)
        {
            if (edge.equals(thatEdge)) return true;
        }

        return false;
    }
    
    @Override
    public String toString()
    {
        String retS = "";

        for (int ell = 0; ell < TABLE_SIZE; ell++)
        {
            if (table[ell] != null)
            {
                retS += ell + ":\n";
                for(PebbledHyperedge PebbledHyperedge:table[ell])
                {
                    retS += PebbledHyperedge.toString() + "\n";
                }
            }
        }

        return retS;
    }
}
