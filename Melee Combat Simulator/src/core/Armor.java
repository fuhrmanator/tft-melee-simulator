package core;

/**
 * Write a description of class Armor here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Armor
{
    // instance variables
    private String name;
    private int hitsBlocked;
    @SuppressWarnings("unused")
	private int movementAdjustment;
    private int dexAdjustment;

    public static final Armor NO_ARMOR = new Armor("No armor", 0, 0, 0);
    public static final Armor LEATHER = new Armor("Leather", 2, 2, 2);
    public static final Armor CHAIN = new Armor("Chain", 3, 4, 4);
    public static final Armor PLATE = new Armor("Plate", 5, 6, 6);

    /**
     * Constructor for objects of class Armor
     */
    public Armor(String name, int hitsBlocked, int maAdj, int dxAdj)
    {
        // initialise instance variables
        this.name = name;
        this.hitsBlocked = hitsBlocked;
        this.movementAdjustment = maAdj;
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
