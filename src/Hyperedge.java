import java.util.ArrayList;

public class Hyperedge 
{
    public Annotation annot;
    public ArrayList<Integer> sourceNodes;
    public int targetNode;
    
    public Hyperedge(Annotation theAnnot, int target, int... sources)
    {
        //creates an arraylist containing the integers that represent the source nodes
        sourceNodes = new ArrayList<Integer>();
        for(int source: sources)
        {
            sourceNodes.add(source);
        }
        targetNode = target;
        annot = theAnnot;
    }
    
    public Hyperedge(Annotation theAnnot, int target, ArrayList<Integer> sources)
    {
        sourceNodes = sources;
        targetNode = target;
        annot = theAnnot;
    }
    
    public boolean edgeEquals(Object o)
    {
        if(!(o instanceof Hyperedge)) return false;
        Hyperedge that = (Hyperedge) o;
        //compare target
        if(that.targetNode != this.targetNode) return false;
        //compare sources
        for(Integer thisInt: this.sourceNodes)
        {
            if(!thisInt.equals(that.sourceNodes.get(thisInt))) return false;
        }
        return true;
    }
    
    @Override
    public String toString()
    {
        String edgeS = "";
        
        edgeS += "{";
        for(Integer currNode: sourceNodes)
        {
            edgeS += currNode + ", ";
        }
        edgeS += "} -> ";
        edgeS += targetNode;
        return edgeS;
    }
}
