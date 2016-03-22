import java.util.ArrayList;

//
// A Path is defined as a the sub-hypergraph from a set of source nodes to a goalNode node
//
public class Path
{
    // Leaf nodes Statement (when viewed as upside-down tree)
    public ArrayList<Integer> startNodes;
    public ArrayList<Integer> getStartNodes() { return startNodes; }

    // Internal nodes in the path
    public ArrayList<Integer> pathNodes;
    public ArrayList<Integer> getPathNodes() { return pathNodes; }

    // Single, final target node
    private int goalNode;
    public int getGoal() { return goalNode; }

    // Path from start of Path to end of Path
    //public ArrayList<Integer> suppressedstartNodes { get; private set; }

    // Hyperedges making up this path
    public ArrayList<PebbledHyperedge> edges;
    public ArrayList<PebbledHyperedge> getEdges() { return edges; }

    //public DiGraph graph { get; private set; }

    // For final determination of interestingness
    //public int interestingPercentage = 0;

    // For backward Path generation
    public Path()
    {
        startNodes = new ArrayList<Integer>();
        goalNode = -1;

        pathNodes = new ArrayList<Integer>();
        edges = new ArrayList<PebbledHyperedge>();
        //suppressedstartNodes = new ArrayList<Integer>();

        //graph = new DiGraph();
    }

    public Path(PebbledHyperedge edge)
    {
        startNodes = new ArrayList<Integer>(edge.sourceNodes);
        goalNode = edge.targetNode;

        pathNodes = new ArrayList<Integer>();
        edges = new ArrayList<PebbledHyperedge>();
        edges.add(edge);

        //suppressedstartNodes = new ArrayList<Integer>();

        //graph = new DiGraph();
        //graph.AddHyperEdge(startNodes, goalNode);
    }

    public Path(Path thatPath)
    {
        startNodes = new ArrayList<Integer>(thatPath.startNodes);
        goalNode = thatPath.goalNode;

        pathNodes = new ArrayList<Integer>(thatPath.pathNodes);
        edges = new ArrayList<PebbledHyperedge>(thatPath.edges);
        //suppressedstartNodes = new ArrayList<Integer>(thatPath.suppressedstartNodes);

        //graph = new DiGraph(thatPath.graph);
    }

    public int getNumDeductiveSteps() { return edges.size(); }

    private int memoizedLength = -1;
    //    public int getLength()
    //    {
    //        if (memoizedLength == -1) memoizedLength = graph.GetLength();
    //        return memoizedLength;
    //    }

    private int memoizedWidth = -1;
    //    public int GetWidth()
    //    {
    //        if (memoizedWidth == -1) memoizedWidth = graph.GetWidth();
    //        return memoizedWidth;
    //    }

    public boolean containsGoalEdge(int targetNode)
    {
        for (PebbledHyperedge edge : edges)
        {
            if (edge.targetNode == targetNode) return true;
        }

        return false;
    }

    //    public boolean containsCycle() { return graph.containsCycle(); }

    //    public override int GetHashCode() { return base.GetHashCode(); }

    //
    // Just a simple hashing mechanism
    //
    public long getHashKey()
    {
        long key = 1;

        key *= startNodes.get(0);

        if (!pathNodes.isEmpty()) key *= pathNodes.get(0);

        key *= goalNode;

        return key;
    }

    public boolean inSource(int n) { return startNodes.contains(n); }
    public boolean inPath(int n) { return pathNodes.contains(n); }
    public boolean hasGoal(int n) { return goalNode == n; }

    private void addEdge(PebbledHyperedge edge)
    {
        // Add to the graph
        //graph.AddHyperEdge(edge.sourceNodes, edge.targetNode);

        if (this.edges.contains(edge)) return;

        // Add in an ordered manner according to the target node.
        int e = 0;
        for ( ; e < this.edges.size(); e++)
        {
            if (edge.targetNode < this.edges.get(e).targetNode) break;
        }

        this.edges.add(e, edge);
    }

    //
    // Create a new Path based on thisPath and thatPath in accordance with the above comments (repeated here)
    //
    // This Path                       { This startNodes } { This Path } -> This Goal
    // The new Path is of the form:    { That startNodes } { That Path } -> Goal
    //                       Combined:    { New startNodes  U  This startNodes \minus This Goal} {This Path  U  This Goal } -> Goal
    //
    public void append(/* Hypergraph graph, */ HyperedgeMultiMap forwardEdges, Path thatPath)
    {
        if (thatPath.goalNode == -1)
        {
            throw new IllegalArgumentException("Attempt to append with an empty Path " + this + " " + thatPath);
        }

        //
        // If this is an empty Path, populate it like a copy constructor and return
        //
        if (this.goalNode == -1)
        {
            startNodes = new ArrayList<Integer>(thatPath.startNodes);
            goalNode = thatPath.goalNode;

            pathNodes = new ArrayList<Integer>(thatPath.pathNodes);
            edges = new ArrayList<PebbledHyperedge>(thatPath.edges);

            //suppressedstartNodes = new ArrayList<Integer>(thatPath.suppressedstartNodes);

            for (PebbledHyperedge thatEdge : thatPath.edges)
            {
                this.addEdge(thatEdge);
            }
            return;
        }

        //
        // Standard appending of an existent Path to another existent Path
        //
        if (!this.startNodes.contains(thatPath.goalNode))
        {
            throw new IllegalArgumentException("Attempt to append Paths that do not connect goalNode->given" + this + " " + thatPath);
        }

        // Degenerate by removing the new Path goalNode from THIS source node.
        this.startNodes.remove(new Integer(thatPath.goalNode));

        // Add the 'new Path' goalNode node to the path of the new Path (uniquely)
        Utilities.addUnique(this.pathNodes, thatPath.goalNode);

        // Add the path nodes to THIS path
        Utilities.addUniqueList(this.pathNodes, thatPath.pathNodes);

        // Add all the new sources to the degenerated old sources; do so uniquely
        Utilities.addUniqueList(this.startNodes, thatPath.startNodes);
        //Utilities.addUniqueList(this.suppressedstartNodes, thatPath.suppressedstartNodes);

        // Add all of the edges of that Path to this Path; this also adds to the Path graph
        for (PebbledHyperedge edge : thatPath.edges)
            this.addEdge(edge);

//        if (this.containsCycle())
//        {
//            throw new Exception("Path contains a cycle" + this.graph.GetStronglyConnectedComponentDump());
//            // Remove an edge from this Path?
//        }

        // Now, if there exists a node in the path AND in the startNodes, remove it from the startNodes.
        for (int p : this.pathNodes)
        {
            if (this.startNodes.remove(new Integer(p)))
            {
                // if (Utilities.Path_GEN_DEBUG) System.Diagnostics.Debug.WriteLine("A node existed in the path AND startNodes (" + p + "); removing from startNodes");
            }
        }

        //PerformDeducibilityCheck(forwardEdges);
    }

    //    //
    //    // The combination of new information may lead to other given information being deducible
    //    //
    //    //
    //    // foreach given in the Path
    //    //   find all edges with target given 
    //    //   foreach edge with target with given
    //    //     if (all of the source nodes in edge are in the given OR path) then
    //    //       if this is a minimal edge (fewer sources better) then
    //    //         save edge
    //    //   if (found edge) then
    //    //     AddEdge to Path
    //    //     move target given to path
    //    //       
    //    private void PerformDeducibilityCheck(HyperEdgeMultiMap<A> edgeDatabase)
    //    {
    //        // All the startNodes and path nodes for this Path; this includes the new edgeSources
    //        ArrayList<Integer> PathstartNodesAndPath = new ArrayList<Integer>(this.startNodes);
    //        PathstartNodesAndPath.AddRange(this.path);
    //
    //        // foreach given in the Path
    //
    //        ArrayList<Integer> tempstartNodes = new ArrayList<Integer>(this.startNodes); // Make a copy because we may be modifying it below
    //        foreach (int given in tempstartNodes)
    //        {
    //            PebbledHyperedge savedEdge = null;
    //
    //            // find all edges with target given 
    //            ArrayList<PebbledHyperedge> forwardEdges = edgeDatabase.GetBasedOnGoal(given);
    //            if (forwardEdges != null)
    //            {
    //                // foreach edge with target with given
    //                foreach (PebbledHyperedge edge in forwardEdges)
    //                {
    //                    // if (all of the source nodes in edge are in the given OR path) then
    //                    if (Utilities.Subset<Integer>(PathstartNodesAndPath, edge.sourceNodes))
    //                    {
    //                        // if this is a minimal edge (fewer sources better) then
    //                        if (savedEdge == null) savedEdge = edge;
    //                        else if (edge.sourceNodes.Count < savedEdge.sourceNodes.Count)
    //                        {
    //                            savedEdge = edge;
    //                        }
    //                    }
    //                }
    //
    //                if (savedEdge != null)
    //                {
    //                    // if (Utilities.Path_GEN_DEBUG) System.Diagnostics.Debug.WriteLine("CTA: Found another edge which can deduce startNodes." + savedEdge);
    //
    //                    // Add the found edge to the Path
    //                    this.AddEdge(savedEdge);
    //
    //                    // move target given to path: (1) remove from startNodes; (2) add to path 
    //                    this.startNodes.Remove(savedEdge.targetNode);
    //                    Utilities.AddUnique<Integer>(this.path, savedEdge.targetNode);
    //                }
    //            }
    //        }
    //    }

    //
    // Paths are equal only if the startNodes, goalNode, and paths are the same
    //
//    @override
//    public boolean equals(Object obj)
//    {
//        Path<Hypergraph.EdgeAnnotation> thatPath = obj as Path<Hypergraph.EdgeAnnotation>;
//        if (thatPath == null) return false;
//
//        if (this.goalNode != thatPath.goalNode) return false;
//
//        if (this.startNodes.Count != thatPath.startNodes.Count) return false;
//
//        if (this.path.Count != thatPath.path.Count) return false;
//
//        // Union the sets; if the union is the same size as the original, they are the same
//        ArrayList<Integer> union = new ArrayList<Integer>(this.startNodes);
//        Utilities.AddUniqueList<Integer>(union, thatPath.startNodes);
//        if (union.Count != this.startNodes.Count) return false;
//
//        union = new ArrayList<Integer>(this.path);
//        Utilities.AddUniqueList<Integer>(union, thatPath.path);
//        if (union.Count != this.path.Count) return false;
//
//        return true;
//    }
//
//    public override string ToString()
//    {
//        StringBuilder str = new StringBuilder();
//
//        str.Append("Given { ");
//        foreach (int g in startNodes)
//        {
//            str.Append(g + " ");
//        }
//        str.Append("}, Path { ");
//        foreach (int p in path)
//        {
//            str.Append(p + " ");
//        }
//        str.Append("} -> " + goalNode);
//
//        return str.ToString();
//    }
//
//    //
//    // Determine which of the given nodes will be suppressed
//    //
//    public void DetermineSuppressedstartNodes(Hypergraph.Hypergraph<ConcreteAST.GroundedClause, Hypergraph.EdgeAnnotation> graph)
//    {
//        // Determine the suppressed nodes in the graph and break
//        // the startNodes into those that must be explicitly stated to the user and those that are implicit.
//        foreach (int g in startNodes)
//        {
//            ConcreteAST.GroundedClause clause = graph.vertices[g].data;
//            if (clause.IsAxiomatic() || clause.IsIntrinsic() || !clause.IsAbleToBeASourceNode())
//            {
//                suppressedstartNodes.Add(g);
//            }
//        }
//        suppressedstartNodes.ForEach(s => startNodes.Remove(s));
//    }
//
//    public string ConstructPathAndSolution(Hypergraph.Hypergraph<ConcreteAST.GroundedClause, Hypergraph.EdgeAnnotation> graph)
//    {
//        // Sort the startNodes and path for ease in readability; they are reverse-sorted
//        TopologicalSortPath();
//
//        StringBuilder str = new StringBuilder();
//
//        str.AppendLine("Source: ");
//        for (int g = startNodes.Count - 1; g >= 0; g--)
//        {
//            str.AppendLine("\t (" + startNodes[g] + ")" + graph.GetNode(startNodes[g]).ToString());
//        }
//        str.AppendLine("Suppressed Source: ");
//        foreach (int s in suppressedstartNodes)
//        {
//            str.AppendLine("\t (" + s + ")" + graph.GetNode(s).ToString());
//        }
//        str.AppendLine("HyperEdges:");
//        foreach (PebbledHyperedge edge in edges)
//        {
//            str.AppendLine("\t" + edge.ToString() + "\t" + edge.annotation.ToString());
//        }
//        str.AppendLine("  Path:");
//        for (int p = path.Count - 1; p >= 0; p--)
//        {
//            str.AppendLine("\t (" + path[p] + ")" + graph.GetNode(path[p]).ToString());
//        }
//
//        str.Append("  -> Goal: (" + goalNode + ")" + graph.GetNode(goalNode).ToString());
//
//        return str.ToString();
//    }
//
//    private void TopologicalSortPath()
//    {
//        ArrayList<Integer> sortedGiven = new ArrayList<Integer>();
//        ArrayList<Integer> sortedPath = new ArrayList<Integer>();
//
//        ArrayList<Integer> sortedNodes = this.graph.TopologicalSort();
//
//        foreach (int node in sortedNodes)
//        {
//            if (startNodes.Contains(node)) sortedGiven.Add(node);
//            else if (path.Contains(node)) sortedPath.Add(node);
//            else if (!suppressedstartNodes.Contains(node) && !goalNode.Equals(node))
//            {
//                throw new ArgumentException("Node " + node + " is not in either given, suppressed, path, nor goalNode for " + this.ToString());
//            }
//        }
//
//        startNodes = new ArrayList<Integer>(sortedGiven);
//        path = new ArrayList<Integer>(sortedPath);
//    }
//
//    public string EdgeAndSCCDump()
//    {
//        StringBuilder str = new StringBuilder();
//
//        str.AppendLine("HyperEdges:");
//        foreach (PebbledHyperedge edge in edges)
//        {
//            str.AppendLine("\t" + edge.ToString());
//        }
//
//        str.AppendLine(this.graph.GetStronglyConnectedComponentDump());
//
//        return str.ToString();
//    }
}