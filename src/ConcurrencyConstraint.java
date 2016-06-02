
import java.util.ArrayList;

public class ConcurrencyConstraint implements Constraint
{
    private ArrayList<Action> _actions;
    private Action _theAction;
    
    public ConcurrencyConstraint(ArrayList<Action> concurrentActions, Action theAction)
    {
        _actions = concurrentActions;
        _theAction = theAction;
    }
}
