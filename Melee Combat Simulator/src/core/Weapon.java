package core;
import gui.Logger;


/**
 * Write a description of class Weapon here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Weapon
{
    // instance variables
    private String name;
    private int strengthRequired;
    private int damageDice;
    private int damageModifier;
    private boolean twoHanded;
    private boolean thrown;
	private boolean pole;

    public static final Weapon NONE = new Weapon("None", 0, 0, 0);
//     public static final Weapon BROADSWORD = new Weapon("Broadsword", 12, 2, 0);
//     public static final Weapon TWO_HANDED_SWORD = new Weapon("Two-handed sword", 14, 3, -1, true, false);
//     public static final Weapon MORNINGSTAR = new Weapon("Morningstar", 13, 2, 1);
//     public static final Weapon CUTLASS = new Weapon("Cutlass", 10, 2, -2);
// 
//     public static final Weapon[] WEAPON_LIST = 
//     {
//         BROADSWORD, TWO_HANDED_SWORD, MORNINGSTAR, CUTLASS
//     };

	// define all the static pre-defined weapons
	public static final Weapon 
	    DAGGER = new Weapon("Dagger", 0, 1, -1, true, false), 
	    RAPIER = new Weapon("Rapier", 9, 1, 0, false, false),
	    CLUB = new Weapon("Club", 9, 1, 0, true, false),
	    HAMMER = new Weapon("Hammer", 10, 1, 1, true, false),
	    CUTLASS = new Weapon("Cutlass", 10, 2, -2, false, false),
	    SHORTSWORD = new Weapon("Shortsword", 11, 2, -1, false, false),
	    MACE = new Weapon("Mace", 11, 2, -1, true, false),
	    SMALL_AX = new Weapon("Small ax", 11, 1, 2, false, false),
	    BROADSWORD = new Weapon("Broadsword", 12, 2, 0, false, false),
	    MORNINGSTAR = new Weapon("Morningstar", 13, 2, 1, false, false),
	    TWO_HANDED_SWORD = new Weapon("Two-handed sword", 14, 3, -1, false, true),
	    BATTLEAXE = new Weapon("Battleaxe", 15, 3, 0, false, true),

    	// pole weapons
	    JAVELIN = new Weapon("Javelin", 9, 1, -1, true, false, true),
	    SPEAR = new Weapon("Spear", 11, 1, 2, true, true, true),
	    HALBERD = new Weapon("Halberd", 13, 2, -1, false, true, true),
	    PIKE_AXE = new Weapon("Pike axe", 15, 2, 2, false, true, true);

// 	// missile weapons
// 	public static final Weapon THROWN_ROCK = new Weapon("Thrown rock", 0, new Damage(1, -4), false, false, MISSILE_WEAPON);
// 	public static final Weapon SLING = new Weapon("Sling", 0, new Damage(1, -2), false, false, MISSILE_WEAPON);
// 	public static final Weapon SMALL_BOW = new Weapon("Small bow", 9, new Damage(1, -1), false, true, MISSILE_WEAPON, 1.0f, 2.0f, 15);
// 	public static final Weapon HORSE_BOW = new Weapon("Horse bow", 10, new Damage(1), false, true, MISSILE_WEAPON, 1.0f, 2.0f, 16);
// 	public static final Weapon LONGBOW = new Weapon("Longbow", 11, new Damage(1, 2), false, true, MISSILE_WEAPON, 1.0f, 2.0f, 18);
// 	public static final Weapon LIGHT_CROSSBOW = new Weapon("Light crossbow", 12, new Damage(2), false, true, MISSILE_WEAPON, 0.5f, 1.0f, 14);
// 	public static final Weapon HEAVY_CROSSBOW = new Weapon("Heavy crossbow", 15, new Damage(3), false, true, MISSILE_WEAPON, 0.34f, 0.5f, 16);

	// two lists of weapons in Melee -- Daggers and Weapons.
	public static final Weapon[] WEAPON_LIST = {DAGGER, RAPIER, CLUB, HAMMER, CUTLASS,
		SHORTSWORD, MACE, SMALL_AX, BROADSWORD, MORNINGSTAR, TWO_HANDED_SWORD,
		BATTLEAXE, JAVELIN, SPEAR, HALBERD, PIKE_AXE};
		

    /**
     * Constructor for objects of class Weapon
     */
    public Weapon(String name, int st, int dice, int modifier)
    {
        // initialise instance variables
        this.name = name;
        this.strengthRequired = st;
        this.damageDice = dice;
        this.damageModifier = modifier;
    }

    /**
     * Constructor for objects of class Weapon
     */
    public Weapon(String name, int st, int dice, int modifier, boolean twoHanded, boolean thrown)
    {
        this(name, st, dice, modifier);
        this.twoHanded = twoHanded;
        this.twoHanded = thrown;
    }

    /**
     * Constructor for objects of class Weapon
     */
    public Weapon(String name, int st, int dice, int modifier, boolean twoHanded, boolean thrown, boolean pole)
    {
        this(name, st, dice, modifier, twoHanded, thrown);
        this.pole = pole;
    }

    public int doDamage()
    {
		if (Logger.getInstance().isVerbose())
			Logger.getInstance().getTextArea().append(
					"Rolling for weapon doing "
							+ damageDice
							+ "d"
							+ ((damageModifier > 0) ? "+" : "")
							+ ((damageModifier != 0) ? Integer
									.toString(damageModifier) : "")
							+ " damage.\n");
        int damage = 0;
        for (int i = 0; i < damageDice; i++)
        {
            damage += Die.roll();
        }
        damage += damageModifier;
        if (damage < 0) damage = 0;
        return damage;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getStengthRequired()
    {
        return strengthRequired;
    }
    
    public boolean isTwoHanded()
    {
        return twoHanded;
    }

    public boolean isThrown()
    {
        return thrown;
    }

	public boolean isPole() {
		return pole;
	}

    @Override
	public String toString()
    {
        return name + " (" + damageDice + "D" +
        ((damageModifier > 0) ? "+" : "") + ((damageModifier != 0) ? Integer.toString(damageModifier) : "") + ")";
    }

}
