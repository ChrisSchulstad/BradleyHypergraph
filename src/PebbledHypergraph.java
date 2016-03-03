import java.util.ArrayList;

public class PebbledHypergraph 
{
    public ArrayList<PebbledHypernode> nodes;  //private?
    private Hypergraph original;
    
    public PebbledHypergraph()
    {
        nodes = new ArrayList<PebbledHypernode>();
        original = null;
    }
    public PebbledHypergraph(Hypergraph o, ArrayList<PebbledHypernode> nodeList)
    {
        original = o;
        nodes = nodeList;
    }
    
    @Override
    public String toString()
    {
        String graphS = "";
        
        for(PebbledHypernode currNode: nodes)
        {
            graphS += "Vertex " + currNode.id + ": ";
            graphS += "(data: " + currNode.data + ", ";
            for(PebbledHyperedge currEdge: currNode.outEdges)
            {
                graphS += "edges: " + currEdge.toString() + ", ";
            }
            graphS += ") ";
        }
        return graphS;
    }
}
