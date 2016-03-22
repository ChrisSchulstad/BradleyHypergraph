import java.util.ArrayList;

public class Hypernode 
{
    public Object data;
    public int id;

    //edges in which this node is a source
    public ArrayList<Hyperedge> outEdges;
    
    //edges in which this node is the target
    public ArrayList<Hyperedge> inEdges;
    
    public Hypernode(Object theData, int theId)
    {
        data = theData;
        id = theId;
        outEdges = new ArrayList<Hyperedge>();
        inEdges = new ArrayList<Hyperedge>();
    }
    
    public boolean addInEdge(Hyperedge edge) throws Exception
    {
        if(edge.targetNode != this.id) throw new Exception("Hyperedge: " + edge.toString() + "has a different target node than expected " + this.id);
        //for performance, comment out the next line
        if(inEdges.contains(edge)) return false;
        return inEdges.add(edge);
    }
    
    public boolean addOutEdge(Hyperedge edge) throws Exception
    {
        if(!edge.sourceNodes.contains(this.id)) throw new Exception("Hyperedge: " + edge.toString() + "is not incident to " + this.id);
        //for performance, comment out the next line
        if(outEdges.contains(edge)) return false;
        return outEdges.add(edge);
    }
    
    public PebbledHypernode createPebbledNode() 
    {
        return new PebbledHypernode(data, id);
    }
}
