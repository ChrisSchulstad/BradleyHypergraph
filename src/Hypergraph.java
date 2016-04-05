import java.util.ArrayList;
import java.util.Collections;

public class Hypergraph 
{
    public ArrayList<Hypernode> vertices;
    
    public Hypergraph()
    {
        vertices = new ArrayList<Hypernode>();
    }
 
    //trys to add a vertex to the graph and returns whether it was successful
    public boolean addNode(Object data)
    {
        return vertices.add(new Hypernode(data, vertices.size()));
    }
    
    //adds new edge and returns whether it was successful
    public boolean addEdge(Hyperedge newEdge)
    {
        //for every vertex check if the edge already exists
        if(hasEdge(newEdge)) 
        {
            return false;
        }
        
        
        //for every vertex in the edge's sourcenodes list, add the edge to out edges
        for(Integer currNode: newEdge.sourceNodes)
        {
            this.getNode(currNode).outEdges.add(newEdge);
        }
        //add the edge to the edge's targetnode's in edge list
        this.getNode(newEdge.targetNode).inEdges.add(newEdge);
        return true;
    }
    
    //checks and returns if each vertex is incident to the edge
    public boolean hasEdge(Hyperedge e)
    {
        for(Hypernode currNode: vertices)
        {
            if(currNode.inEdges.contains(e)) return true;
            if(currNode.outEdges.contains(e)) return true;
        }
        return false;
    }
    
    //returns the node with a specific id
    public Hypernode getNode(int id)
    {
        return vertices.get(id);
    }
    
    //return integer-based representation of hypergraph
    public PebbledHypergraph getPebbledHypergraph() throws Exception
    {
        ArrayList<PebbledHypernode> pebbledNodes = new ArrayList<PebbledHypernode>(vertices.size());
        //create nodes
        for (int v = 0; v < vertices.size(); v++)
        {
            pebbledNodes.add(v, vertices.get(v).createPebbledNode());
        }

        //create hyperedges
        for (int v = 0; v < vertices.size(); v++)
        {
            for(Hyperedge currEdge: vertices.get(v).outEdges)
            {
                //only add if it is the "minimum" source node, so the edge is not added twice to any node
                if(v == Collections.min(currEdge.sourceNodes))
                {
                    PebbledHyperedge newEdge = new PebbledHyperedge(currEdge.sourceNodes, currEdge.targetNode, currEdge.annot);
                    for(int src: currEdge.sourceNodes)
                    {
                        pebbledNodes.get(src).addEdge(newEdge);
                    }
                }
            }
        }

        return new PebbledHypergraph(this, pebbledNodes);
    }
    
    @Override
    public String toString()
    {
        String graphS = "";
        
        for(Hypernode currNode: vertices)
        {
            graphS += "Vertex " + currNode.id + ": ";
            graphS += "(data: " + currNode.data + ", ";
            graphS += "out edges: ";
            for(Hyperedge currEdge: currNode.outEdges)
            {
                graphS += currEdge.toString() + ", ";
            }
            graphS += "in edges: ";
            for(Hyperedge currEdge: currNode.inEdges)
            {
                graphS += currEdge.toString();
            }
            graphS += ")";
        }
        return graphS;
    }
}
