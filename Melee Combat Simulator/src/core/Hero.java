package core;
import gui.Logger;

/**
 * Write a description of class Hero here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Hero implements Cloneable, Comparable<Object> 
{
	private String name;
    private int st;
    private int dx;
    private Weapon readiedWeapon;
    private Armor armor;
    private Shield shield;
    private boolean knockedDown;
    private boolean standingUp;
    private boolean pickingUpWeapon;
    private Weapon droppedWeapon;
    
    private int damageTaken = 0;
    private int damageTakenThisRound = 0;
    private boolean injuryDexPenalty;
    private boolean recovering;
    private boolean defending;
	private boolean charging;
    
    /*
     * Some test Heroes
     */
    static public final Hero PANOS = new Hero("Panos", 13, 11, Weapon.MORNINGSTAR, Armor.LEATHER, Shield.SMALL_SHIELD);
    static public final Hero JOE_THE_DWARF = new Hero("Joe the dwarf", 16, 6, Weapon.MORNINGSTAR, Armor.PLATE, Shield.LARGE_SHIELD);
    static public final Hero Z_MAN = new Hero("Z Man", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.NO_ARMOR, Shield.NO_SHIELD);

    /**
     * Constructor for objects of class Hero
     */
    public Hero(String name, int st, int dx, Weapon weapon, Armor armor, Shield shield)
    {
        // initialize instance variables
        this.name = name;
        this.st = st;
        this.dx = dx;
        this.readiedWeapon = weapon;
        this.droppedWeapon = Weapon.NONE;
        this.armor = armor;
        this.shield = shield;
        this.standingUp = false;
        if (Logger.getInstance().isVerbose()) Logger.getInstance().getTextArea().append("New hero: " + name + ", ST: " + st + ", DX: " + dx + ", adjDX: " + this.adjustedDx() + "\n");
    }

    @Override
	public Object clone()
    {
        Object o = null;
        try {
          o = super.clone();
        } catch(CloneNotSupportedException e) {
          System.err.println("MyObject can't clone");
        }
        return o;
    }

    public String getName()
    {
        return this.name;
    }
    
	public Weapon getReadiedWeapon()
	{
		return this.readiedWeapon;
	}
    
	public Weapon getDroppedWeapon()
	{
		return this.droppedWeapon;
	}
    
    public int adjustedDx()
    {
		/*
		 * Armor adjustment
		 * Shield adjustment
		 * injuryDexPenalty
		 * low Strength penalty
		 */
		return dx
			- this.armor.getDexAdjustment()
			- this.shield.getDexAdjustment()
			- (injuryDexPenalty ? 2 : 0)
			- (isStrengthLowPenalty() ? 3 : 0);
    }
    
    public boolean isAlive()
    {
        return (this.st - this.damageTaken > 0);
    }

    public boolean isConscious()
    {
        return (this.st - this.damageTaken > 1);
    }
    
    public boolean isKnockedDown()
    {
        return knockedDown;
    }    

    public void standUp()
    {
        standingUp = true;
    }    

    /*
     * Indicate the start of a new round
     */
    public void newRound()
    {
    	charging = false;
		defending = false;
        damageTakenThisRound = 0;
        if (standingUp)
        {
            knockedDown = false;
            standingUp = false;
        }
        else if (pickingUpWeapon)  // technically "was" picking up weapon last round
        {
			this.readiedWeapon = this.droppedWeapon;
			this.droppedWeapon = Weapon.NONE;
			pickingUpWeapon = false;
        }

        /*
         * Dex penalty due to injury lasts one complete round
         */
        if (injuryDexPenalty && recovering)
        {
            injuryDexPenalty = false;
            recovering = false;
        }
        else if (injuryDexPenalty)
        {
            recovering = true;
        }
        
    }
    
    /**
     * Hits can be absorbed by armor, shields, etc.
     */
    public int takeHits(int hits)
    {
        int armorPoints = this.armor.hitsStopped() + this.shield.hitsStopped();
        int damageDone = hits - armorPoints;
        if (damageDone < 0) damageDone = 0;

        if (Logger.getInstance().isVerbose()) 
        {
            Logger.getInstance().getTextArea().append(this.name + " taking " + hits + " hits.\n");
            Logger.getInstance().getTextArea().append(this.armor.getName() + " stops " + this.armor.hitsStopped() + "\n");
            Logger.getInstance().getTextArea().append(this.shield.getName() + " stops " + this.shield.hitsStopped() + "\n");
            Logger.getInstance().getTextArea().append(this.name + " taking " + damageDone + " damage.\n");
        }

        this.takeDamage(damageDone);
        return damageDone;
    }

    /**
     * Hits can be absorbed by armor, shields, etc.
     */
    protected void takeDamage(int damageDone)
    {
        this.damageTaken += damageDone;
        this.damageTakenThisRound += damageDone;
        injuryDexPenalty = sufferingDexPenalty();

        if (Logger.getInstance().isVerbose() && injuryDexPenalty) Logger.getInstance().getTextArea().append(this.name + " has an adjDx penalty of -2 for remainder of this round and the NEXT round.\n");
        if (Logger.getInstance().isVerbose()) Logger.getInstance().getTextArea().append(this.name + " has now taken " + damageTaken + " points of damage, ST = " + this.st + "\n");

        if (damageTakenThisRound >= 8)
        {
            knockedDown = true;
            if (Logger.getInstance().isVerbose()) Logger.getInstance().getTextArea().append(this.name + " has been knocked down by damage.\n");   
        }

		if (Logger.getInstance().isVerbose() && isStrengthLowPenalty()) Logger.getInstance().getTextArea().append(this.name + " has an additional DX adjustment of -3 due to ST <= 3.\n");
    }

	public void dropWeapon()
	{
		this.droppedWeapon = this.readiedWeapon;
		this.readiedWeapon = Weapon.NONE;
	}
    
	public void pickUpWeapon()
	{
		pickingUpWeapon = true;
	}
    
    public void breakWeapon()
    {
        this.readiedWeapon = Weapon.NONE;
    }
    
    public int armorPoints()
    {
        return armor.hitsStopped() + shield.hitsStopped();
    }
    
    @Override
	public String toString()
    {
        return "\n" + name;
    }

	/**
	 * @return
	 */
	public boolean sufferingDexPenalty()
	{
		return (damageTakenThisRound >= 5 || recovering);
	}

	protected boolean isStrengthLowPenalty()
	{
		return this.st - this.damageTaken <= 3;
	}

	/**
	 * 
	 */
	public void setDefending()
	{
		defending = true;
	}

	/**
	 * @return
	 */
	public boolean isDefending()
	{
		return defending;
	}

//	/* (non-Javadoc)
//	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
//	 */
//	public int compare(Object o1, Object o2)
//	{
//		return ((Hero)o1).getName().compareTo(((Hero)o2).getName());
//	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		return this.getName().compareTo(((Hero)o).getName());
	}

	/**
	 * @return
	 */
	public int getST()
	{
		return this.st;
	}

	/**
	 * @return
	 */
	public int adjST()
	{
		return Math.max(this.st - this.damageTaken, 0);
	}

	/**
	 * 
	 * @param b
	 */
	public void setCharging(boolean b) {
		charging = b;
	}
	/**
	 * @return
	 */
	public boolean isCharging()
	{
		return charging;
	}

	public boolean isProne() {
		return pickingUpWeapon;
	}

}
