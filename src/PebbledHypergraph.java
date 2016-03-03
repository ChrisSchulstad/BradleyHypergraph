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
    
    public void pebbleNode(int id){
    	nodes.get(id).activate();
    }
    
    //*****************************************************
    //loop over source nodes to see if each are pebbled
    public boolean isFullyPebbled(PebbledHyperedge edge){
    	if (edge.isPebbled()){
    		return true;
    	}
    	for(Integer src : edge.sourceNodes){
    		if (!nodes.get(src).isPebbled()){
    			return false;
    		}
    	}
    	edge.pebble();
    	return true;
    }
    
    public boolean isNodePebbled(int id){
    	return nodes.get(id).isPebbled();
    }
    
    public ArrayList<PebbledHyperedge> getEdges(int id){
    	return nodes.get(id).outEdges;
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
