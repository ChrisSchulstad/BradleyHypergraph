public class Annotation 
{
    private String name;
    private boolean active;
    
    public Annotation()
    {
        name = null;
        active = true;
    }
    
    public Annotation(String theName)
    {
        name = theName;
        active = true;
    }
    
    public Annotation(String theName, boolean active)
    {
        name = theName;
        this.active = active;
    }
    
    public boolean isActive()
    {
        return active;
    }
    
    public void inactivate()
    {
        active = false;
    }
    
    public void activate()
    {
        active = true;
    }
    
    public String getName()
    {
        return name;
    }
}
