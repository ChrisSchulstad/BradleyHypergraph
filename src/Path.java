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
	public ArrayList<Integer> suppressedStartNodes;
	public ArrayList<Integer> getSuppressedStartNodes(){ return suppressedStartNodes; }

	// Hyperedges making up this path
	public ArrayList<PebbledHyperedge> edges;
	public ArrayList<PebbledHyperedge> getEdges() { return edges; }

	public DiGraph graph;
	public DiGraph getGraph(){ return graph; }

	// For final determination of interestingness
	// public int interestingPercentage = 0;

	// For backward Path generation
	public Path()
	{
		startNodes = new ArrayList<Integer>();
		goalNode = -1;

		pathNodes = new ArrayList<Integer>();
		edges = new ArrayList<PebbledHyperedge>();
		suppressedStartNodes = new ArrayList<Integer>();

		graph = new DiGraph();
	}

	public Path(PebbledHyperedge edge)
	{
		startNodes = new ArrayList<Integer>(edge.sourceNodes);
		goalNode = edge.targetNode;

		pathNodes = new ArrayList<Integer>();
		edges = new ArrayList<PebbledHyperedge>();
		edges.add(edge);

		suppressedStartNodes = new ArrayList<Integer>();

		graph = new DiGraph();
		graph.AddHyperEdge(startNodes, goalNode);
	}

	public Path(Path thatPath)
	{
		startNodes = new ArrayList<Integer>(thatPath.startNodes);
		goalNode = thatPath.goalNode;

		pathNodes = new ArrayList<Integer>(thatPath.pathNodes);
		edges = new ArrayList<PebbledHyperedge>(thatPath.edges);
		suppressedStartNodes = new ArrayList<Integer>(thatPath.suppressedStartNodes);

		graph = new DiGraph(thatPath.graph);
	}

	public int getNumDeductiveSteps() { return edges.size(); }

	private int memoizedLength = -1;
	public int getLength()
	{
		if (memoizedLength == -1) memoizedLength = graph.GetLength();
		return memoizedLength;
	}

	private int memoizedWidth = -1;
	public int GetWidth()
	{
		if (memoizedWidth == -1) memoizedWidth = graph.GetWidth();
		return memoizedWidth;
	}

	public boolean containsGoalEdge(int targetNode)
	{
		for (PebbledHyperedge edge : edges)
		{
			if (edge.targetNode == targetNode) return true;
		}

		return false;
	}

	public boolean containsCycle() { return graph.ContainsCycle(); }

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
		graph.AddHyperEdge(edge.sourceNodes, edge.targetNode);

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
	public void append(/* Hypergraph graph, */ HyperedgeMultiMap forwardEdges, Path thatPath) throws Exception
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

			suppressedStartNodes = new ArrayList<Integer>(thatPath.suppressedStartNodes);

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
		Utilities.addUniqueList(this.suppressedStartNodes, thatPath.suppressedStartNodes);

		// Add all of the edges of that Path to this Path; this also adds to the Path graph
		for (PebbledHyperedge edge : thatPath.edges)
			this.addEdge(edge);

		if (this.containsCycle())
		{
			throw new Exception("Path contains a cycle" + this.graph.GetStronglyConnectedComponentDump());
			// Remove an edge from this Path?
		}

		// Now, if there exists a node in the path AND in the startNodes, remove it from the startNodes.
		for (int p : this.pathNodes)
		{
			if (this.startNodes.remove(new Integer(p)))
			{
				// if (Utilities.Path_GEN_DEBUG) System.Diagnostics.Debug.WriteLine("A node existed in the path AND startNodes (" + p + "); removing from startNodes");
			}
		}

		PerformDeducibilityCheck(forwardEdges);
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
	private void PerformDeducibilityCheck(HyperedgeMultiMap edgeDatabase)
	{
		// All the startNodes and path nodes for this Path; this includes the new edgeSources
		ArrayList<Integer> PathstartNodesAndPath = new ArrayList<Integer>(this.startNodes);
		PathstartNodesAndPath.addAll(this.pathNodes);

		// foreach given in the Path

		ArrayList<Integer> tempstartNodes = new ArrayList<Integer>(this.startNodes); // Make a copy because we may be modifying it below
		for(int given : tempstartNodes)
		{
			PebbledHyperedge savedEdge = null;

			// find all edges with target given
			ArrayList<PebbledHyperedge> forwardEdges = null;

			try {
			    forwardEdges = edgeDatabase.getBasedOnGoal(given);
			} catch (Exception e) { e.printStackTrace(System.out); }

			if (forwardEdges != null)
			{
				// foreach edge with target with given
				for (PebbledHyperedge edge : forwardEdges)
				{
					// if (all of the source nodes in edge are in the given OR path) then
					if (Utilities.subset(PathstartNodesAndPath, edge.sourceNodes))
					{
						// if this is a minimal edge (fewer sources better) then
						if (savedEdge == null) savedEdge = edge;
						else if (edge.sourceNodes.size() < savedEdge.sourceNodes.size())
						{
							savedEdge = edge;
						}
					}
				}

				if (savedEdge != null)
				{
					// if (Utilities.Path_GEN_DEBUG) System.Diagnostics.Debug.WriteLine("CTA: Found another edge which can deduce startNodes." + savedEdge);

					// Add the found edge to the Path
					this.addEdge(savedEdge);

					// move target given to path: (1) remove from startNodes; (2) add to path 
					this.startNodes.remove(savedEdge.targetNode);
					Utilities.addUnique(this.pathNodes, savedEdge.targetNode);
				}
			}
		}
	}

	//
	// Paths are equal only if the startNodes, goalNode, and paths are the same
	//
	//    @override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Path)) return false;

		Path thatPath = (Path) obj;

		if (this.goalNode != thatPath.goalNode) return false;

		if (this.startNodes.size() != thatPath.startNodes.size()) return false;

		if (this.pathNodes.size() != thatPath.pathNodes.size()) return false;

		// Union the sets; if the union is the same size as the original, they are the same
		ArrayList<Integer> union = new ArrayList<Integer>(this.startNodes);
		Utilities.addUniqueList(union, thatPath.startNodes);
		if (union.size() != this.startNodes.size()) return false;

		union = new ArrayList<Integer>(this.pathNodes);
		Utilities.addUniqueList(union, thatPath.pathNodes);
		if (union.size() != this.pathNodes.size()) return false;
		return true;
	}

	//    @override
	public String toString()
	{
		StringBuilder str = new StringBuilder();

		str.append("Given { ");
		for (int g : startNodes)
		{
			str.append(g + " ");
		}
		str.append("}, Path { ");
		for (int p : pathNodes)
		{
			str.append(p + " ");
		}
		str.append("} -> " + goalNode);

		return str.toString();
	}

//	//
//	// Determine which of the given nodes will be suppressed
//	//
//	//Hypergraph.Hypergraph<ConcreteAST.GroundedClause, Hypergraph.EdgeAnnotation>
//	public void DetermineSuppressedstartNodes(Hypergraph graph)
//	{
//		// Determine the suppressed nodes in the graph and break
//		// the startNodes into those that must be explicitly stated to the user and those that are implicit.
//		for (int g : startNodes)
//		{
//
//			ConcreteAST.GroundedClause clause = graph.vertices[g].data;
//			if (clause.IsAxiomatic() || clause.IsIntrinsic() || !clause.IsAbleToBeASourceNode())
//			{
//				suppressedStartNodes.add(g);
//			}
//		}
//		suppressedStartNodes.ForEach(s => startNodes.Remove(s));
//	}

	//Hypergraph.Hypergraph<ConcreteAST.GroundedClause, Hypergraph.EdgeAnnotation>
	public String ConstructPathAndSolution(Hypergraph graph)
	{
		// Sort the startNodes and path for ease in readability; they are reverse-sorted
		//trycatch
		try { TopologicalSortPath();
		} catch (Exception e){ e.printStackTrace(System.out); }

		StringBuilder str = new StringBuilder();

		str.append("Source: ");
		for (int g = startNodes.size() - 1; g >= 0; g--)
		{
			str.append("\t (" + startNodes.get(g) + ")" + graph.getNode(startNodes.get(g)).toString());
		}
		str.append("Suppressed Source: ");
		for (int s : suppressedStartNodes)
		{
			str.append("\t (" + s + ")" + graph.getNode(s).toString());
		}
		str.append("HyperEdges:");
		for (PebbledHyperedge edge : edges)
		{
			str.append("\t" + edge.toString() + "\t" + edge.annot.toString());
		}
		str.append("  Path:");
		for (int p = pathNodes.size() - 1; p >= 0; p--)
		{
			str.append("\t (" + pathNodes.get(p) + ")" + graph.getNode(pathNodes.get(p)).toString());
		}

		str.append("  -> Goal: (" + goalNode + ")" + graph.getNode(goalNode).toString());

		return str.toString();
	}

	private void TopologicalSortPath() throws Exception
	{
		ArrayList<Integer> sortedGiven = new ArrayList<Integer>();
		ArrayList<Integer> sortedPath = new ArrayList<Integer>();

		ArrayList<Integer> sortedNodes = this.graph.TopologicalSort();

		for (int node : sortedNodes)
		{
			if (startNodes.contains(node)) sortedGiven.add(node);
			else if (pathNodes.contains(node)) sortedPath.add(node);
			else if (!suppressedStartNodes.contains(node) && goalNode != node)
			{
				throw new Exception("Node " + node + " is not in either given, suppressed, path, nor goalNode for " + this.toString());
			}
		}

		startNodes = new ArrayList<Integer>(sortedGiven);
		pathNodes = new ArrayList<Integer>(sortedPath);
	}

	public String EdgeAndSCCDump()
	{
		StringBuilder str = new StringBuilder();

		str.append("HyperEdges:");
		for (PebbledHyperedge edge : edges)
		{
			str.append("\t" + edge.toString());
		}

		str.append(this.graph.GetStronglyConnectedComponentDump());

		return str.toString();
	}
}