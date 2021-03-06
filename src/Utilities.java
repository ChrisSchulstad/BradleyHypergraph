import java.util.ArrayList;
import java.util.Random;

public class Utilities 
{
    public static final boolean DEBUG = true;
    
    public static ArrayList<Integer> genSubset(Random gen, int size, int low, int high)
    {
        ArrayList<Integer> ary = new ArrayList<>();
        int count = 0;
        
        while(count < size)
        {
            int num = gen.nextInt(high - low) + low;
            
            if(!ary.contains(num))
            {
                ary.add(num);
                count++;
            }
        }
        return ary;
    }
    
    public static Hyperedge createRandomEdge(Random gen, int numNodes)
    {
        //to have random number of source nodes, put random generator as first parameter of genSubset
        //currently at size 3 subset of sources for simple testing
        Hyperedge newEdge = new Hyperedge(new Annotation(), gen.nextInt(numNodes), Utilities.genSubset(gen, 3, 0, numNodes));
                                          //annotation,     target,                set of sources
        
        boolean success = true;
        for(int source: newEdge.sourceNodes)
        {
            if(newEdge.targetNode == source)
            {
                success = false;
                break;
            }
        }
        if (!success) newEdge = createRandomEdge(gen, numNodes);
        
        return newEdge;
    }
    
    // Is smaller \subseteq larger
    public static boolean subset(ArrayList<Integer> larger, ArrayList<Integer> smaller)
    {
        for (Integer o : smaller)
        {
            if (!larger.contains(o)) return false;
        }

        return true;
    }

    // Is set1 \equals set2
    public static boolean equalSets(ArrayList<Integer> set1, ArrayList<Integer> set2)
    {
        if (set1.size() != set2.size()) return false;

        return Utilities.subset(set1, set2);
    }
    
    public static boolean addUnique(ArrayList<Integer> list, Integer obj)
    {
        if (list.contains(obj)) return false;

        list.add(obj);

        return true;
    }
    
    public static void addUniqueList(ArrayList<Integer> list, ArrayList<Integer> objList)
    {
        for (Integer o : objList)
        {
            addUnique(list, o);
        }
    }
}
