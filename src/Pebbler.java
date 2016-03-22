
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.swing.text.Utilities;

//
// A reduced version of the original hypergraph that provides simple pebbling and exploration
//
public class Pebbler
{

    // The pebbling version (integer-based) of the hypergraph to work on. 
    private PebbledHypergraph pebblerGraph;

    // The actual hypergraph for reference purposes only
    // below was the previous default hypergraph, idk what the EdgeAnnotation part is
    // private Hypergraph.Hypergraph<GeometryTutorLib.ConcreteAST.GroundedClause, Hypergraph.EdgeAnnotation> graph;
    private Hypergraph graph;

    // multimap
    // A list of edges that can be processed using means other than a fixpoint analysis.
    private HyperedgeMultiMap forwardPebbledEdges;
    public HyperedgeMultiMap getForwardEdges() { return forwardPebbledEdges; }
    private HyperedgeMultiMap backwardPebbledEdges;
    public HyperedgeMultiMap getBackwardEdges() { return backwardPebbledEdges; }


    public Pebbler(Hypergraph graph, PebbledHypergraph pGraph)
    {

        this.graph = graph;
        this.pebblerGraph = pGraph;

        //Multimap
        forwardPebbledEdges = new HyperedgeMultiMap(pGraph.nodes.size());
        backwardPebbledEdges = new HyperedgeMultiMap(pGraph.nodes.size());

        //Multimap
        forwardPebbledEdges.setOriginalHypergraph(graph);
        backwardPebbledEdges.setOriginalHypergraph(graph);
    }


    // Clear all pebbles from all nodes and edges in the hypergraph
    public void clearPebbles()
    {
        for (PebbledHypernode node : pebblerGraph.nodes)
        {
            node.deactivate();

            for (PebbledHyperedge edge : node.getOutEdges())
            {
                edge.clearPebble();
            }
        }
    }
    

    public void pebble(ArrayList<Integer> nodesToPebble)
    {
        // Sort in ascending order for pebbling
        Collections.sort(nodesToPebble);

        // Forward pebble: it acquires the valid list of forward edges 
        // Pebble all nodes and percolate
        ForwardTraversal(forwardPebbledEdges, nodesToPebble);
    }

    //
    // Given a node, pebble the reachable parts of the graph (in the forward direction)
    // We pebble in a breadth first manner
    // 
    private void ForwardTraversal(HyperedgeMultiMap edgeDatabase, ArrayList<Integer> nodesToPebble)
    {
        ArrayList<Integer> worklist = new ArrayList<Integer>();
        Collections.copy(worklist, nodesToPebble);

        // Pebble until the list is empty
        while (!worklist.isEmpty())
        {
            // Acquire the next value to consider
            int currentNodeIndex = worklist.get(0);
            worklist.remove(0);

            // Pebble the current node as a forward node and percolate forward
            pebblerGraph.pebbleNode(currentNodeIndex);

            //PebblerHyperedge<Hypergraph.EdgeAnnotation>
            for(PebbledHyperedge currentEdge : pebblerGraph.getEdges(currentNodeIndex))
            {
                //add code here to restrict user defined choices (A LA Geometry Axioms or Quest Actions)
                if (!pebblerGraph.isFullyPebbled(currentEdge))
                {
                    //*** Has the target of this edge been pebbled previously? Pebbled -> Pebbled means we have a backward edge
                    if(!pebblerGraph.isNodePebbled(currentEdge.targetNode)){

                        // Success, we have an edge
                        // Construct a static set of pebbled hyperedges for problem construction
                        edgeDatabase.putUnchecked(currentEdge);

                        // Add this node to the worklist to percolate further
                        if (!worklist.contains(currentEdge.targetNode))
                        {
                            worklist.add(currentEdge.targetNode);
                            Collections.sort(worklist);
                        }
                    }
                }
            }
        }    
    }
}