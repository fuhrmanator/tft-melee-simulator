package core;


/**
 * The test class DieTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class DieTest extends junit.framework.TestCase
{
    /**
     * Default constructor for test class DieTest
     */
    public DieTest()
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

    public void testRoll()
    {
        for (int i = 0; i < 100; i++)
        {
            int x = Die.roll();
            assertTrue((x<=6) && (x>=1));
        }
    }
}

