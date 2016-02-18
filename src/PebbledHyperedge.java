import java.util.ArrayList;

public class PebbledHyperedge 
{
    public Annotation annot;
    public ArrayList<Integer> sourceNodes;
    public int targetNode;
    public boolean active;
    
    public PebbledHyperedge(ArrayList<Integer> sources, int target, Annotation theAnnot)
    {
        sourceNodes = sources;
        targetNode = target;
        annot = theAnnot;
        active = true;
    }
    
    public void activate()
    {
        active = true;
    }
    
    public void deactivate()
    {
        active = false;
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
