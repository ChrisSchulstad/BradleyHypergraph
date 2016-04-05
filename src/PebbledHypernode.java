import java.util.ArrayList;

public class PebbledHypernode 
{
    public Object data;
    public int id;
    
    // edges in which this node is a source
    public ArrayList<PebbledHyperedge> outEdges;
    public boolean active;

    public ArrayList<PebbledHyperedge> getOutEdges() { return outEdges; }
    
    public PebbledHypernode(Object theData, int theId)
    {
        data = theData;
        id = theId;
        outEdges = new ArrayList<PebbledHyperedge>();
        active = false;
    }
    
    public void activate() { active = true; }
    public void deactivate() { active = false; }
    public boolean isPebbled() { return active; }
    public int getID() { return id; }
    
    public boolean addEdge(PebbledHyperedge newEdge) throws Exception
    {
        if(!newEdge.sourceNodes.contains(this.id)) throw new Exception("Hyperedge: " + newEdge.toString() + "is not incident to " + this.id);

        //for performance, comment out the next line
        if(outEdges.contains(newEdge)) return false;
        
        return outEdges.add(newEdge);
    }
}
