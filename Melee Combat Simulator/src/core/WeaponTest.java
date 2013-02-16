package core;


/**
 * The test class WeaponTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class WeaponTest extends junit.framework.TestCase
{
    /**
     * Default constructor for test class WeaponTest
     */
    public WeaponTest()
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

    public void testdoDamage()
    {
        Weapon weapon1 = new Weapon("test", 12, 2, 0);
        for (int i = 0; i < 100; i++)
        {
            int x = weapon1.doDamage();
            assertTrue((x <= 12) && (x >= 2));
        }

        weapon1 = new Weapon("test", 12, 2, 2);
        for (int i = 0; i < 100; i++)
        {
            int x = weapon1.doDamage();
            assertTrue((x <= 14) && (x >= 4));
        }

        weapon1 = new Weapon("Rock", 12, 1, -4);
        for (int i = 0; i < 100; i++)
        {
            int x = weapon1.doDamage();
            assertTrue((x <= 6-4) && (x >= 0));
        }
    }
}

