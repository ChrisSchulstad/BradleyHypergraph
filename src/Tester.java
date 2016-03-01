import java.util.ArrayList;
import java.util.Random;

public class Tester 
{
    
    public static void main(String[] args) throws Exception
    {
        Hypergraph hG = new Hypergraph();
        
        for(int count = 0; count < 1000; count++)
        {
            hG.addNode(new Hypernode(count ,count));
        }
        
        Random gen = new Random();
        
        for(int count = 0; count < 9000; count++)
        {
            hG.addEdge(Utilities.createRandomEdge(1000));
        }
        
        hG.getPebbledHypergraph();
    }
    
}
