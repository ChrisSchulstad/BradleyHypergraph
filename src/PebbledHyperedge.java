import java.util.ArrayList;

public class PebbledHyperedge 
{
    public Annotation annot;
    public ArrayList<Integer> sourceNodes;
    public int targetNode;
    public boolean pebbled;
    
    public PebbledHyperedge(ArrayList<Integer> sources, int target, Annotation theAnnot)
    {
        sourceNodes = sources;
        targetNode = target;
        annot = theAnnot;
        pebbled = true;
    }
    
    public void pebble()
    {
        pebbled = true;
    }
    
    public void clearPebble()
    {
        pebbled = false;
    }

    public boolean isPebbled() { return pebbled; }
    
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
