import java.util.ArrayList;
import java.util.Collections;

public class PathHashMap 
{
    private final int TABLE_SIZE;
    private ArrayList<Path>[] table;

    private boolean[] generated;
    private int size;

    private HyperedgeMultiMap edgeDatabase;

    private final int MAX_GIVENS;
    private final int DEFAULT_MAX_BACKWARD_GIVENS = 2;
    
    public PathHashMap(HyperedgeMultiMap edges, int sz, int maxGivens)
    {
        size = 0;
        TABLE_SIZE = sz;

        table = new ArrayList[TABLE_SIZE];
        generated = new boolean[TABLE_SIZE];

        edgeDatabase = edges;
        MAX_GIVENS = maxGivens;
        // FORWARD_GENERATION = true;
    }
    
    public PathHashMap(HyperedgeMultiMap edges, int sz)
    {
        size = 0;
        TABLE_SIZE = sz;

        table = new ArrayList[TABLE_SIZE];
        generated = new boolean[TABLE_SIZE];

        edgeDatabase = edges;
        MAX_GIVENS = DEFAULT_MAX_BACKWARD_GIVENS;
        // FORWARD_GENERATION = false;
    }
    
    public PathHashMap(ArrayList<Path> paths, int sz)
    {
        size = paths.size();
        TABLE_SIZE = sz;

        table = new ArrayList[TABLE_SIZE];
        generated = new boolean[TABLE_SIZE];

        MAX_GIVENS = DEFAULT_MAX_BACKWARD_GIVENS;

        // Insert all the input Paths
        for(Path path : paths)
        {
            if (table[path.getGoal()] == null)
            {
                table[path.getGoal()] = new ArrayList<Path>();
            }

            table[path.getGoal()].add(path);
        }
    }
    
    public boolean hasNodeBeenGenerated(int node)
    {
        return generated[node];
    }
    
    public void setGenerated(int node)
    {
        // If the node results in no Paths, create an empty list
        if (table[node] == null) table[node] = new ArrayList<Path>();
        generated[node] = true;
    }
    
    // Get a single list of all the Paths
    public ArrayList<Path> getAll() throws Exception
    {
        ArrayList<Path> all = new ArrayList<Path>();

        for (int i = 0; i < table.length; i++)
        {
            if (table[i] != null) all.addAll(table[i]);
        }

        if (all.size() != size)
        {
            throw new Exception("Unexpectedly the number of Paths advertised (" + size + ")is inconsistent with the actual (" + all.size() + ") number of Paths.");
        }

        return all;
    }

    // Acquire a problem based on the goal only
    public ArrayList<Path> get(Path p) { return get(p.getGoal()); }
 
    //
    // Another option to acquire the pertinent problems
    //
    public ArrayList<Path> get(int key)
    {
        if (key < 0 || key >= TABLE_SIZE)
        {
            throw new IllegalArgumentException("PathHashMap(" + key + ")");
        }

        return table[key];
    }
    
    //
    // Add the problem to all source node hash values
    //
    public void put(Path newPath)
    {
        if (newPath.getStartNodes().size() > MAX_GIVENS) return;

        //  Check that no edges may be used to deduce any given in the problem
        PebbledHyperedge edge = null; // BasicMinimality(newPath);
        if (edge != null)
        {
            throw new IllegalArgumentException("The following problem is not minimal since " + edge + " can deduce a given in " + newPath);
        }

        // It is important that only when problems are added to the list, the new lists are allocated
        // Keeping null lists is important to determine if no problems exist vs. exploration has not occurred
        if (table[newPath.getGoal()] == null)
        {
            table[newPath.getGoal()] = new ArrayList<Path>();
            table[newPath.getGoal()].add(newPath);
            size++;
            return;
        }

        //
        // We verify minimality here for problems in the map
        // We note that the goals equate already; we are verifying the givens (not the suppressed)
        // Based on this criteria for entry in the table, a problem may only equate with a single other problem in terms of goal / sources
        ArrayList<Path> oldPaths = table[newPath.getGoal()];
        for (int p = 0; p < oldPaths.size(); p++)
        {
            // Check if the givens from the minimal problem and this candidate problem equate exactly
            if (Utilities.equalSets(newPath.getStartNodes(), oldPaths.get(p).getStartNodes()))
            {
                // Choose the shorter problem (fewer edges wins)
                if (newPath.getEdges().size() < oldPaths.get(p).getEdges().size())
                {
                    //if (Utilities.PROBLEM_GEN_DEBUG) System.Diagnostics.Debug.WriteLine("In ProblemHashMap, removing problem " + oldProblems[p] + " for " + newProblem);

                    // Remove the old problem and add the new problem
                    table[newPath.getGoal()].remove(p);
                    table[newPath.getGoal()].add(newPath);
                }
                // else the list remains unchanged

                // Either way, we are done.
                return;
            }
            // Check if the givens from new problem are a subset of the givens of the minimal problem.
            else if (Utilities.subset(newPath.getStartNodes(), oldPaths.get(p).getStartNodes()))
            {
                if (Utilities.DEBUG)
                {
                    System.out.println("Filtering for Minimal Givens: " + newPath + " for " + oldPaths.get(p));
                }

                return;
            }
            // Check if the givens from new problem are a subset of the givens of the minimal problem.
            else if (Utilities.subset(oldPaths.get(p).getStartNodes(), newPath.getStartNodes()))
            {
                if (Utilities.DEBUG)
                {
                    System.out.println("Filtering for Minimal Givens: " + oldPaths.get(p) + " for " + newPath);
                }
                table[newPath.getGoal()].remove(p);
                table[newPath.getGoal()].add(newPath);
                return;
            }
        }

        // No problems did equate; so add this new problem
        table[newPath.getGoal()].add(newPath);
        size++;
    }

    // Add a list of Paths to the structure unchecked (we assume all have the same goal node)
    public void putUnchecked(ArrayList<Path> paths) throws Exception
    {
        int goal = paths.get(0).getGoal();
        for(Path path : paths)
        {
            if (path.getGoal() != goal)
            {
                throw new Exception("Not all unchecked Paths have the same goal: " + goal + " " + path.getGoal());
            }
        }

        if (table[paths.get(0).getGoal()] == null)
        {
            table[paths.get(0).getGoal()] = new ArrayList<Path>(paths);
            size += paths.size();
        }
        else
        {
            // table[Paths[0].goal].AddRange(Paths);
            throw new Exception("We don't expect to add Paths in this UNCHECKED manner (with goal): " + paths.get(0).getGoal());
        }
    }
    
    // Can any edge in the edgeDatabase be used to deduce any of the givens?
    private PebbledHyperedge BasicMinimality(Path newPath) throws Exception
    {
        // Combine the givens and path into a single list
        ArrayList<Integer> givensAndPath = new ArrayList(newPath.getStartNodes());
        givensAndPath.addAll(newPath.getPathNodes());

        // For each given, determine if it could have been simply deduced from a combination of given and path nodes
        for(int given : newPath.getStartNodes())
        {
            // Acquire all edges with this given as a goal
            ArrayList<PebbledHyperedge> edges = edgeDatabase.getBasedOnGoal(given);

            if (edges != null)
            {
                // Analyze each basic edge to see if all of the hyperedge nodes are in the givens or path
                for(PebbledHyperedge edge:edges)
                {
                    if (givensAndPath.containsAll(edge.sourceNodes)) return edge;
                }

            }
        }

        return null;
    }
}
