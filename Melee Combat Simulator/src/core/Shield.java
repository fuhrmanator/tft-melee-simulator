package core;

/**
 * Write a description of class Shield here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Shield
{
    // instance variables
    private String name;
    private int hitsBlocked;
    private int dexAdjustment;

    public static final Shield NO_SHIELD = new Shield("No shield", 0, 0);
    public static final Shield SMALL_SHIELD = new Shield("Small shield", 1, 0);
    public static final Shield LARGE_SHIELD = new Shield("Large shield", 2, 1);
    
    /**
     * Constructor for objects of class Shield
     */
    public Shield(String name, int hitsBlocked, int dxAdj)
    {
        // initialise instance variables
        this.name = name;
        this.hitsBlocked = hitsBlocked;
        this.dexAdjustment = dxAdj;
    }

    public String getName()
    {
        return name;
    }

    public int hitsStopped()
    {
        return hitsBlocked;
    }
    
    public int getDexAdjustment()
    {
        return dexAdjustment;
    }
    
    @Override
	public String toString()
    {
        return name + " (" + hitsBlocked + ")";
    }
}