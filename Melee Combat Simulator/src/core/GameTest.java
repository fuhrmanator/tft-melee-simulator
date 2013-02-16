package core;


/**
 * The test class GameTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class GameTest extends junit.framework.TestCase
{
    /**
     * Default constructor for test class GameTest
     */
    public GameTest()
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


	public void testResolveAttack()
	{
		Hero hero1 = new Hero("Panos", 13, 11, Weapon.MORNINGSTAR, Armor.LEATHER, Shield.SMALL_SHIELD);
		Hero hero2 = new Hero("Joe the dwarf", 16, 6, Weapon.MORNINGSTAR, Armor.PLATE, Shield.LARGE_SHIELD);
		Game game1 = new Game(hero1, hero2);
		game1.resolveAttack(hero1, hero2, 3, 3);
		/* need some assertions! */
	}
}


