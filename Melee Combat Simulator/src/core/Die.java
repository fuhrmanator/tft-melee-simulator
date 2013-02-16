package core;
import gui.Logger;


/**
 * Write a description of class Die here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Die
{

	/**
	 * Constructor for objects of class Die
	 */
	public Die()
	{
		// initialise instance variables
	}

	static int roll()
	{
		int roll = (int) (Math.random() * 6 + 1);
		if (Logger.getInstance().isVerbose())
			Logger.getInstance().getTextArea().append(
				"Die roll: " + roll + "\n");
		return roll;
	}

	static int rollDice(int numDice)
	{
		if (Logger.getInstance().isVerbose())
			Logger.getInstance().getTextArea().append(
				"Rolling " + numDice + " dice...\n");

		int result = 0;

		for (int i = 0; i < numDice; i++)
		{
			result += roll();
		}

		return result;

	}

	static int rollThreeDice()
	{
		if (Logger.getInstance().isVerbose())
			Logger.getInstance().getTextArea().append(
				"Rolling three dice...\n");
		return roll() + roll() + roll();
	}

	/**
	 * @return
	 */
	public static int rollFourDice()
	{
		if (Logger.getInstance().isVerbose())
			Logger.getInstance().getTextArea().append("Rolling four dice...\n");
		return roll() + roll() + roll() + roll();
	}


}
