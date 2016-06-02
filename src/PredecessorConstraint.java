
import java.util.ArrayList;

public class PredecessorConstraint implements Constraint
{
    private ArrayList<Action> _actions;
    private Action _theAction;
    
    public PredecessorConstraint(ArrayList<Action> preActions, Action theAction)
    {
        _actions = preActions;
        _theAction = theAction;
    }
}
