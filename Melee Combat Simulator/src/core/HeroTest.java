package core;


/**
 * The test class HeroTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class HeroTest extends junit.framework.TestCase
{
    /**
     * Default constructor for test class HeroTest
     */
    public HeroTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Override
	protected void setUp()
    {
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @Override
	protected void tearDown()
    {
    }

    public void testTakeHits()
    {
        /* no armor test */
        Hero hero1 = new Hero("Panos", 13, 13, Weapon.NONE, Armor.NO_ARMOR, Shield.NO_SHIELD);
        assertEquals(6, hero1.takeHits(6));

        /*
         * Test for more less damage than armor blocks
         */
        hero1 = new Hero("Panos", 13, 13, Weapon.NONE, Armor.CHAIN, Shield.LARGE_SHIELD);
        assertEquals(2, hero1.takeHits(7));
        assertEquals(1, hero1.takeHits(6));
        assertEquals(0, hero1.takeHits(5));
        assertEquals(0, hero1.takeHits(4));
        assertEquals(0, hero1.takeHits(3));
        assertEquals(0, hero1.takeHits(2));
        assertEquals(0, hero1.takeHits(1));
        assertEquals(0, hero1.takeHits(0));

        /*
         * Shield only
         */
        hero1 = new Hero("Panos", 13, 13, Weapon.NONE, Armor.NO_ARMOR, Shield.LARGE_SHIELD);
        assertEquals(4, hero1.takeHits(6));
        assertEquals(3, hero1.takeHits(5));
        assertEquals(2, hero1.takeHits(4));
        assertEquals(1, hero1.takeHits(3));
        assertEquals(0, hero1.takeHits(2));
        assertEquals(0, hero1.takeHits(1));
        assertEquals(0, hero1.takeHits(0));
        
        /*
         * Knock-down
         */
        hero1 = new Hero("Panos", 13, 13, Weapon.NONE, Armor.NO_ARMOR, Shield.NO_SHIELD);
        hero1.takeHits(7);
        assertFalse(hero1.isKnockedDown());
        hero1.takeHits(1);
        assertTrue(hero1.isKnockedDown());

        hero1 = new Hero("Panos", 13, 13, Weapon.NONE, Armor.NO_ARMOR, Shield.NO_SHIELD);
        hero1.takeHits(8);
        assertTrue(hero1.isKnockedDown());
        hero1.takeHits(1);
        assertTrue(hero1.isKnockedDown());

        /*
         * AdjDx due to injury, should be -2
         */
        // all at once
        hero1 = new Hero("Panos", 13, 13, Weapon.NONE, Armor.NO_ARMOR, Shield.NO_SHIELD);
        hero1.takeHits(5);
        assertEquals(11, hero1.adjustedDx());

        // multiple blows
        hero1 = new Hero("Panos", 13, 13, Weapon.NONE, Armor.NO_ARMOR, Shield.NO_SHIELD);
        hero1.takeHits(4);
        assertEquals(13, hero1.adjustedDx());
        hero1.takeHits(1);
        assertEquals(11, hero1.adjustedDx());
        
        // for next turn as well
        hero1.newRound();
        assertEquals(11, hero1.adjustedDx());
        // back to normal after one complete round
        hero1.newRound();
        assertEquals(13, hero1.adjustedDx());        

		hero1 = new Hero("Panos", 13, 13, Weapon.NONE, Armor.NO_ARMOR, Shield.NO_SHIELD);
		assertEquals(13, hero1.adjustedDx());        
        hero1.takeHits(9);
        // DX should be -2 due to damage in current round
		assertEquals(11, hero1.adjustedDx());
		hero1.newRound();
		// DX should be -2 due to damage in last round
		assertEquals(11, hero1.adjustedDx());
		hero1.newRound();
		// DX should be normal
		assertEquals(13, hero1.adjustedDx());
		hero1.takeHits(1);
		// DX should be -3 due to ST <= 3
		assertEquals(10, hero1.adjustedDx());
		hero1.takeHits(1);
		assertEquals(10, hero1.adjustedDx());
    }
    
	public void testStandUp()
	{
		/*
		 * When knocked down, a hero should not be able to do anything for the
		 * remainder of the current round (stays knocked down)
		 * Then, when an attempt to stand up is done, that is the action for that
		 * round, and the hero remains technically knocked down (standing up)
		 * until the next round.
		 */
		Hero hero1 = new Hero("Panos", 13, 13, Weapon.NONE, Armor.NO_ARMOR, Shield.NO_SHIELD);
		hero1.takeHits(10);
		assertTrue(hero1.isKnockedDown());
		hero1.newRound();
		assertTrue(hero1.isKnockedDown());
		hero1.standUp();
		assertTrue(hero1.isKnockedDown());
		hero1.newRound();
		assertFalse(hero1.isKnockedDown());
	}

	public void testPickUpWeapon()
	{
		/*
		 * When knocked down, a hero should not be able to do anything for the
		 * remainder of the current round (stays knocked down)
		 * Then, when an attempt to stand up is done, that is the action for that
		 * round, and the hero remains technically knocked down (standing up)
		 * until the next round.
		 */
		Hero hero1 = new Hero("Panos", 13, 13, Weapon.DAGGER, Armor.NO_ARMOR, Shield.NO_SHIELD);
		assertEquals(Weapon.NONE, hero1.getDroppedWeapon());
		Weapon weaponToDrop = hero1.getReadiedWeapon();
		hero1.dropWeapon();
		assertEquals(Weapon.NONE, hero1.getReadiedWeapon());
		hero1.newRound();
		assertEquals(Weapon.NONE, hero1.getReadiedWeapon());
		hero1.pickUpWeapon();
		assertEquals(Weapon.NONE, hero1.getReadiedWeapon());
		hero1.newRound();
		assertEquals(weaponToDrop, hero1.getReadiedWeapon());
	}
	
	public void testIsCharging()
	{
		Hero hero1 = new Hero("Panos", 13, 13, Weapon.DAGGER, Armor.NO_ARMOR, Shield.NO_SHIELD);
		assertEquals(false, hero1.isCharging());
		hero1.setCharging(true);
		assertEquals(true, hero1.isCharging());
		hero1.setCharging(false);
		assertEquals(false, hero1.isCharging());
	}

}


