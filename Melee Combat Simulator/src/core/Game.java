package core;

import gui.Logger;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * Write a description of class Game here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Game
{

    Hero hero1;
    Hero hero2;
    boolean verbose;

    boolean winHero1;
    boolean winHero2;
    int criticalMisses;
    int criticalHits;
    int round;
    
//    private static final int NUMBER_OF_BOUTS_PER_MATCHUP = 100;
	private static boolean poleCharge;
	private static boolean defendOnPoleCharge;
    @SuppressWarnings("unused")
	private static final boolean DEBUG = false;

    /**
     * Constructor for objects of class Game
     */
    public Game(Hero hero1, Hero hero2)
    {
        if (verbose) Logger.getInstance().getTextArea().append("Starting a bout with " + hero1 + " and " + hero2 + "\n");
        this.hero1 = hero1; 
        this.hero2 = hero2;
        verbose = Logger.getInstance().isVerbose();
        round = 0;
    }

	public static void tryAllCombinations(
		Set<Hero> heroSet,
		int count, 
		boolean poleCharge,
		boolean defendOnPoleCharge)
    {
		Game.poleCharge = poleCharge;
		Game.defendOnPoleCharge = defendOnPoleCharge;
		Map<Hero, Integer> heroStats;

		if (heroSet == null)
    	{
			heroStats = makeHeroSet();
    	}
    	else
    	{
       		heroStats = new HashMap<Hero, Integer>();
       		for (Iterator<Hero> iter = heroSet.iterator(); iter.hasNext();)
			{
				Hero element = iter.next();
				heroStats.put(element, new Integer(0));
			}
    	}
    	Logger.getInstance().getTextArea().append("Simulation running. Please be patient.\n");
		if (Logger.getInstance().isVerbose())
		{
			Logger.getInstance().getTextArea().append("Matching up the following heroes, one at a time to fight " + count + " bouts per match-up.\n");
			for (Iterator<Hero> iter = heroStats.keySet().iterator(); iter.hasNext();)
			{
				Hero hero = iter.next();
				Logger.getInstance().getTextArea().append(hero.getName() + "\n");
			}
		}

        Collection<Hero> heroes = heroStats.keySet();

        // initialize the wins map (matrix of wins)
        Map<String, Integer> winsMap = new HashMap<String, Integer>();
        for (Hero hero1 : heroes) {
			for (Hero hero2: heroes) {
				winsMap.put(hero1.getName()+hero2.getName(), 0);
			}
		}
		

        /* every hero has to fight every other hero, resulting in N-1 matchups */
        double matchUps = count * (heroes.size() - 1);
        int[] score = new int[2];
        Game game = null;
        int tempCounter = 1;
        
        for (Iterator<Hero> itHero1 = heroes.iterator(); itHero1.hasNext(); tempCounter++)
        {
        	if (!Logger.getInstance().isVerbose()) Logger.getInstance().getTextArea().append("Processing hero: " + tempCounter + "\n");
            Hero hero1 = itHero1.next();
            /*
             * avoid repeating bouts, start hero2 at just after hero1
             */
            Iterator<Hero> itHero2 = heroes.iterator();
            Hero hero2 = itHero2.next();
            while (hero1 != hero2 && itHero2.hasNext())
            {
                hero2 = (Hero) itHero2.next();
            }
            for ( ; itHero2.hasNext(); )
            {
                hero2 = (Hero) itHero2.next();
                double sumRounds = 0;
                score[0] = score[1] = 0;
                for (int i = 0; i < count; i++)
                {
                    /*
                     * clone the objects, so we don't have do re-set them 
                     * each time they may die or are injured
                     */
                    Hero tempHero1 = (Hero) hero1.clone();
                    Hero tempHero2 = (Hero) hero2.clone();
                    game = new Game(tempHero1, tempHero2);
                    Hero winner = game.dukeItOut();
                    
                    /*
                     * In the event of a draw, winner == null
                     */
                    if (winner != null)
                    {
                    	Hero loser = (winner == game.hero1 ? game.hero2 : game.hero1);
                        score[(winner == game.hero1 ? 0 : 1)] ++;
                        // track matchup
                        String key = winner.getName()+loser.getName();
                        winsMap.put(key, winsMap.get(key).intValue()+1);
                    }
                    sumRounds += game.round;
                }

                if (Logger.getInstance().isVerbose()) 
                {
                    Logger.getInstance().getTextArea().append("------------------------------\n");
                    Logger.getInstance().getTextArea().append("After " + count + " bouts, the score is:" + "\n");
                    Logger.getInstance().getTextArea().append(game.hero1.getName() + ": " + score[0] + "\n");
                    Logger.getInstance().getTextArea().append(game.hero2.getName() + ": " + score[1] + "\n");
                    Logger.getInstance().getTextArea().append("Draws: " + (count - score[0] - score[1]) + "\n");
                    Logger.getInstance().getTextArea().append("Average number of rounds per bout: " + (sumRounds/count) + "\n");
                }
                /*
                 * Update the total stats for these heros
                 */
                heroStats.put(hero1, new Integer(((Integer)(heroStats.get(hero1))).intValue() + score[0]));
                heroStats.put(hero2, new Integer(((Integer)(heroStats.get(hero2))).intValue() + score[1]));
            }
        }
        
        /*
         * Put the results into a TreeSet to be sorted
         */
        Collection<String> sortedStats = new TreeSet<String>();
        for (Iterator<Hero> iter = heroes.iterator(); iter.hasNext(); )
        {
            Hero hero = iter.next();
            int wins = ((Integer) heroStats.get(hero)).intValue();
            /*
             * Java 1.4.x still doesn't have printf-style formatting,
             * so we'll add a big number to the wins/percents to make it padded 
             * for string sorting, and trim off the '1' from the start with
             * substring.
             */
            sortedStats.add(String.valueOf(100000000 + wins).substring(1) + ", " + String.valueOf(100 + 100 * wins / matchUps).substring(1, 5) + "% " + hero.getName());
        }

        /*
         * Output the sorted results
         */
        Logger.getInstance().getTextArea().append("\n-----------------------------------------------\n");
        Logger.getInstance().getTextArea().append("Results for " + heroStats.size() + " heroes, paired up for " + count + " bouts each\n");
                          //00007482, 77.1% 068:ST13;DX11;MORNINGSTAR;NO_ARMOR;SMALL_SHIELD
        Logger.getInstance().getTextArea().append("  Victs   %Vict Hero#:stats and equipment\n");
        
        for (Iterator<String> iter = sortedStats.iterator(); iter.hasNext(); )
        {
            Logger.getInstance().getTextArea().append(iter.next() + "\n");
        }

        /*
         * Output the wins matrix
         */
        Logger.getInstance().getTextArea().append("\n---------------------Wins matrix, CSV format--------------------------\n");
    	Logger.getInstance().getTextArea().append(",");
    	List<Hero> heroesSorted = new ArrayList<Hero>(heroes);
    	Collections.sort(heroesSorted);
        for (Hero hero1 : heroesSorted) {
        	Logger.getInstance().getTextArea().append( hero1.getName() + ",");
        }
    	Logger.getInstance().getTextArea().append("\n");			
        
        for (Hero hero1 : heroesSorted) {
        	Logger.getInstance().getTextArea().append( hero1.getName() + ",");
			for (Hero hero2: heroesSorted) {
	        	Logger.getInstance().getTextArea().append( winsMap.get(hero1.getName() + hero2.getName()) + ",");
			}
        	Logger.getInstance().getTextArea().append("\n");			
		}
        
    }
    
    /*
     * Inner class for keeping track of game stats
     */
    protected class HeroStats implements Comparator<Object>
    {
        protected Hero hero;
        protected int victories;
        
        public HeroStats(Hero hero, int victories)
        {
            this.hero = hero;
            this.victories = victories;
        }

        public int compare(Object obj1, Object obj2) 
        {
            HeroStats heroStats1 = (HeroStats)obj1;
            HeroStats heroStats2 = (HeroStats)obj2;

            return (heroStats1.victories < heroStats2.victories) ? -1 : (heroStats1.victories == heroStats2.victories) ? 0 : 1;
        }
        
        @Override
		public boolean equals(Object obj) 
        {
            return ((HeroStats)obj).hero == this.hero;
        }
    }

    /*
     * Argument is the number of rounds for the bout
     */
//    public static void main(String [] args)
//    {
//    	JFrame frame = new JFrame("Simulation Output");
//		JTextArea textArea = new JTextArea(20, 80);
//		JScrollPane scrollPane = 
//			new JScrollPane(textArea,
//							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//							JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		textArea.setEditable(false);
//		textArea.setFont(new Font("Courier", Font.PLAIN, 11));
//		frame.getContentPane().add(scrollPane);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setVisible(true);
//
//    	tryAllCombinations(null, NUMBER_OF_BOUTS_PER_MATCHUP);
////        Game game = null;
////        int count = Integer.parseInt(args[0]);
////        int[] score = new int[2];
////        double sumRounds = 0;
////
////        for (int i = 0; i < count; i++)
////        {
//////             game = new Game(new Hero("Panos", 13, 11, Weapon.MORNINGSTAR, Armor.LEATHER, Shield.SMALL_SHIELD),
//////                             new Hero("Joe the dwarf", 16, 6, Weapon.MORNINGSTAR, Armor.PLATE, Shield.LARGE_SHIELD));
//////             game = new Game(new Hero("Z Man", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.NO_ARMOR, Shield.NONE),
//////                             new Hero("Hacker", 12, 12, Weapon.BROADSWORD, Armor.LEATHER, Shield.SMALL_SHIELD));
////
////             game = new Game(new Hero("Panos", 13, 11, Weapon.MORNINGSTAR, Armor.LEATHER, Shield.SMALL_SHIELD),
////                             new Hero("Z Man", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.NO_ARMOR, Shield.NO_SHIELD));
//////            game = new Game(new Hero("Z Man", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.NO_ARMOR, Shield.NONE),
//////                            new Hero("Panos", 13, 11, Weapon.MORNINGSTAR, Armor.LEATHER, Shield.SMALL_SHIELD));
////
////            Hero winner = game.dukeItOut();
////            if (winner != null)
////            {
////                score[(winner == game.hero1 ? 0 : 1)] ++;
////            }
////            sumRounds += game.round;
////        }
////        if (DEBUG) 
////        {
////            Logger.getInstance().getTextArea().append("After " + count + " bouts, the score is:");
////            Logger.getInstance().getTextArea().append(game.hero1.getName() + ": " + score[0]);
////            Logger.getInstance().getTextArea().append(game.hero2.getName() + ": " + score[1]);
////            Logger.getInstance().getTextArea().append("Draws: " + (count - score[0] - score[1]));
////            Logger.getInstance().getTextArea().append("Average number of rounds per bout: " + (sumRounds/count));
////        }
//    }

    /**
     * attack another hero
     */
    public void resolveAttack(Hero attacker, Hero attackee, int roll, int numDice)
    {
    	int facingBonus = attackee.isProne() ? 4 : 0;   // adjust for attackee picking up a weapon with all 4 rear hexes
    	if (verbose) Logger.getInstance().getTextArea().append(attacker.getName() + " rolling to hit. Rolled " + roll + " and adjDex is " 
    			+ (attackee.isProne() ? (attacker.adjustedDx() + facingBonus + " (" + attacker.adjustedDx() + " + " + facingBonus + ", target is picking up a weapon)") 
    			: attacker.adjustedDx()) 
    			+ "\n");
        /*
         * A hit is a roll that is 
         * NOT an automatic miss AND 
         * (below or equal to the attacker's adjDex OR an automatic hit)
         */
		if (!isAutomaticMiss(roll, numDice)
			&& (roll <= attacker.adjustedDx() + facingBonus || isAutomaticHit(roll, numDice)))
        {
            if (verbose) Logger.getInstance().getTextArea().append("Hit! \n");
            int hits = attacker.getReadiedWeapon().doDamage();
            if (attacker.isCharging() && attacker.getReadiedWeapon().isPole())
            {
                if (verbose) Logger.getInstance().getTextArea().append("Pole weapon charge does double damage!\n");
                criticalHits++;
                hits *= 2;            	
            }
            if (isDoubleDamage(roll, numDice))
            {
                if (verbose) Logger.getInstance().getTextArea().append("Double damage! (roll of " + roll + " on " + numDice + " dice.\n");
                criticalHits++;
                hits *= 2;
            }
            else if (isTripleDamage(roll, numDice))
            {
                if (verbose) Logger.getInstance().getTextArea().append("Triple damage! (roll of " + roll + " on " + numDice + " dice.\n");
                criticalHits++;
                hits *= 3;
            }
            if (verbose) Logger.getInstance().getTextArea().append("Total damage done by " + attacker.getReadiedWeapon().getName() + ": "+ hits + " hits\n");
            attackee.takeHits(hits);
        }
        /* else it's a miss! */
        else
        {
            if (verbose) Logger.getInstance().getTextArea().append("Missed. \n");
            if (isDroppedWeapon(roll, numDice))
            {
                if (verbose) Logger.getInstance().getTextArea().append("Dropped weapon! \n");
                criticalMisses++;
                attacker.dropWeapon();
            }
            else if (isBrokenWeapon(roll, numDice))
            {
                if (verbose) Logger.getInstance().getTextArea().append("Broke weapon! \n");
                criticalMisses++;
                attacker.breakWeapon();
            }
        }
    }

    public Hero dukeItOut()
    {
    	/*
    	 * Fight while at least one hero is conscious
    	 * and has a weapon to fight with (can be dropped, but not broken)
    	 */
		while (hero1.isConscious()
			&& hero2.isConscious()
			&& ((hero1.getDroppedWeapon() != Weapon.NONE
				|| hero1.getReadiedWeapon() != Weapon.NONE)
				|| (hero2.getDroppedWeapon() != Weapon.NONE
					|| hero2.getReadiedWeapon() != Weapon.NONE)))
        {
            round++;
            hero1.newRound();
            hero2.newRound();
            
            if (verbose) 
            {
            	Logger.getInstance().getTextArea().append("---> Round " + round + "\n");
				Logger.getInstance().getTextArea().append("Hero 1: " + hero1.getName() + ", ST: " + hero1.getST() + "(" + hero1.adjST() + ")\n");
				Logger.getInstance().getTextArea().append("Hero 2: " + hero2.getName() + ", ST: " + hero2.getST() + "(" + hero2.adjST() + ")\n");
            }

            /* Charge attack */
            if (Game.poleCharge && round==1) 
            {
            	hero1.setCharging(true);
            	hero2.setCharging(true);
            }


			/*
			 * Decide if defending
			 */
			tryDefending(hero1, hero2);
			tryDefending(hero2, hero1);

            Hero firstAttacker = hero1, secondAttacker = hero2;
            
            /* highest adjDx attacks first */
            if (hero1.adjustedDx() < hero2.adjustedDx())
            {
                firstAttacker = hero2;
                secondAttacker = hero1;
            }
            /* roll to see who attacks first */
            else if (hero1.adjustedDx() == hero2.adjustedDx())
            {
                if (verbose) Logger.getInstance().getTextArea().append("Adjusted dexterities are equal, rolling to decide attack order\n");
                if (Math.random() < 0.5)
                {
                    firstAttacker = hero2;
                    secondAttacker = hero1;
                }
            }
            if (verbose) Logger.getInstance().getTextArea().append(firstAttacker.getName() + 
                               " (adjDx = " + firstAttacker.adjustedDx() + 
                               ") attacks before " + secondAttacker.getName() +
                               " (adjDx = " + secondAttacker.adjustedDx() + ")\n" );

            tryStandUp(firstAttacker);
            tryStandUp(secondAttacker);
			tryPickUp(firstAttacker);
			tryPickUp(secondAttacker);
            tryAttack(firstAttacker, secondAttacker);
            tryAttack(secondAttacker, firstAttacker);

            
        }

        Hero winner = null;
        /* both broke/dropped weapons, draw */
        if (hero1.isConscious() && hero2.isConscious())
        {
            winner = null;
        }
        else 
        {
            winner = (hero1.isConscious() ? hero1 : hero2);
        }

        if (winner != null)
        {
            if (verbose) Logger.getInstance().getTextArea().append("-------> The winner of this bout is " + winner.getName() + "\n");
        }
        else
        {
            if (verbose) Logger.getInstance().getTextArea().append("-------> This bout was a TIE!\n");
        }
        
        return winner;
    }
    
    /**
	 * @param firstAttacker
	 */
	private void tryDefending(Hero defender, Hero attacker)
	{
		if (!defender.isKnockedDown() 
				&& defender.getReadiedWeapon() != Weapon.NONE
				&& defender.sufferingDexPenalty()
				&& defender.adjustedDx() < 8)
		{
			defender.setDefending();
			if (verbose) Logger.getInstance().getTextArea().append(defender.getName() + " is defending this turn because adjDX < 8 and temporarily penalized.\n");
		}
		else if (Game.defendOnPoleCharge 
				&& !defender.isKnockedDown() 
				&& defender.getReadiedWeapon() != Weapon.NONE
				&& attacker.getReadiedWeapon() != Weapon.NONE
				&& attacker.getReadiedWeapon().isPole()
				&& attacker.isCharging())
		{
			defender.setDefending();
			if (verbose) Logger.getInstance().getTextArea().append(defender.getName() + " is defending this turn because attacker is charging with pole weapon.\n");
		}
	}

	private void tryStandUp(Hero hero)
    {
        if (hero.isKnockedDown())
        {
            hero.standUp();
            if (verbose) Logger.getInstance().getTextArea().append(hero.getName() + " is standing up this turn.\n");
        }
    }

	private void tryPickUp(Hero hero)
	{
		if (hero.getDroppedWeapon() != Weapon.NONE)
		{
			hero.pickUpWeapon();
			if (verbose) Logger.getInstance().getTextArea().append(hero.getName() + " is picking up his weapon this turn (facing rear in all six directions).\n");
		}
	}

	private void tryAttack(Hero attacker, Hero attackee) {
		if (!attacker.isDefending()) {
			if (attacker.isConscious()) {
				if (!attacker.isKnockedDown()) {
					if (attacker.getReadiedWeapon() != Weapon.NONE) {
						int numDice = attackee.isDefending() ? 4 : 3;
						resolveAttack(attacker, attackee,
								Die.rollDice(numDice), numDice);
					} else {
						if (verbose)
							Logger.getInstance()
									.getTextArea()
									.append(attacker.getName()
											+ " is not able to attack because he has has no readied weapon.\n");
					}
				} else {
					if (verbose)
						Logger.getInstance()
								.getTextArea()
								.append(attacker.getName()
										+ " is not able to attack because he was knocked down.\n");
				}
			} else {
				if (verbose)
					Logger.getInstance()
							.getTextArea()
							.append(attacker.getName()
									+ " is not able to attack because he is unconscious.\n");
			}
		} else {
			if (verbose)
				Logger.getInstance().getTextArea()
						.append(attacker.getName() + " is defending.\n");
		}
	}
    
	private boolean isAutomaticHit(int total, int numDice)
	{
		boolean result;
		switch (numDice)
		{
			case 3 :
				if (total <= 5)
				{
					result = true;
				} else
				{
					result = false;
				}
				break;

			case 4 :
				/*
				 * Four and five are automatic hits on four dice, according to the
				 * rules on defending/dodging of Melee, 5th edition.
				 */
				if (total <= 5)
				{
					result = true;
				} else
				{
					result = false;
				}
				break;

			default :
				System.out.println(
					"isAutomaticHit() -- unsupported number of dice "
						+ numDice);
				result = false;
				break;
		}

		return result;

	}

	private boolean isAutomaticMiss(int total, int numDice)
	{
		boolean result;
		switch (numDice)
		{
			case 3 :
				if (total >= 16)
				{
					result = true;
				} else
				{
					result = false;
				}
				break;

				/*
				 * Twenty and above are automatic misses, according to the
				 * rules on defending/dodging of Melee, 5th edition.
				 */
			case 4 :
				if (total >= 20)
				{
					result = true;
				} else
				{
					result = false;
				}
				break;

			default :
				System.out.println(
					"isAutomaticMiss() -- unsupported number of dice "
						+ numDice);
				result = false;
				break;
		}

		return result;

	}

	private boolean isDoubleDamage(int total, int numDice)
	{
		boolean result;
		switch (numDice)
		{
			case 3 :
				if (total == 4)
				{
					result = true;
				} else
				{
					result = false;
				}
				break;

			case 4 :
				/*
				 * According to rules on defending/dodging, double damage
				 * is not mentioned
				 */
				result = false;
				break;

			default :
				System.out.println(
					"isDoubleDamage() -- unsupported number of dice "
						+ numDice);
				result = false;
				break;
		}

		return result;

	}

	private boolean isTripleDamage(int total, int numDice)
	{
		boolean result;
		switch (numDice)
		{
			case 3 :
				if (total == 3)
				{
					result = true;
				} else
				{
					result = false;
				}
				break;

			case 4 :
				/*
				 * According to rules on defending/dodging, triple damage
				 * is not mentioned
				 */
				result = false;
				break;

			default :
				System.out.println(
					"isTripleDamage() -- unsupported number of dice "
						+ numDice);
				result = false;
				break;
		}

		return result;

	}

	private boolean isDroppedWeapon(int total, int numDice)
	{
		boolean result;
		switch (numDice)
		{
			case 3 :
				if (total == 17)
				{
					result = true;
				} else
				{
					result = false;
				}
				break;

			case 4 :
				if (total == 21 || total == 22)
				{
					result = true;
				} else
				{
					result = false;
				}
				break;

			default :
				System.out.println(
					"isDroppedWeapon() -- unsupported number of dice "
						+ numDice);
				result = false;
				break;
		}

		return result;

	}

	private boolean isBrokenWeapon(int total, int numDice)
	{
		boolean result;
		switch (numDice)
		{
			case 3 :
				if (total == 18)
				{
					result = true;
				} else
				{
					result = false;
				}
				break;

			case 4 :
				if (total == 23 || total == 24)
				{
					result = true;
				} else
				{
					result = false;
				}
				break;

			default :
				System.out.println(
					"isBrokenWeapon() -- unsupported number of dice "
						+ numDice);
				result = false;
				break;
		}

		return result;

	}    
	/*
	 * 
	 */
    public static Map<Hero, Integer> makeHeroSet()
    {
        Map<Hero, Integer> heroSet = new HashMap<Hero, Integer>();
        /*
         * The following is generated code, using MS Excel and Word with mail merge,
         * and was copied from a word document.
         */
//        Hero hero1 = new Hero("001:ST8;DX16;DAGGER;NO_ARMOR;SMALL_SHIELD", 8, 16, Weapon.DAGGER, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
//        heroSet.put(hero1, new Integer(0));
//        Hero hero2 = new Hero("002:ST8;DX16;DAGGER;LEATHER;SMALL_SHIELD", 8, 16, Weapon.DAGGER, Armor.LEATHER, Shield.SMALL_SHIELD);
//        heroSet.put(hero2, new Integer(0));
//        Hero hero3 = new Hero("003:ST8;DX16;DAGGER;CHAIN;SMALL_SHIELD", 8, 16, Weapon.DAGGER, Armor.CHAIN, Shield.SMALL_SHIELD);
//        heroSet.put(hero3, new Integer(0));
//        Hero hero4 = new Hero("004:ST8;DX16;DAGGER;PLATE;SMALL_SHIELD", 8, 16, Weapon.DAGGER, Armor.PLATE, Shield.SMALL_SHIELD);
//        heroSet.put(hero4, new Integer(0));
//        Hero hero5 = new Hero("005:ST8;DX16;DAGGER;LEATHER;LARGE_SHIELD", 8, 16, Weapon.DAGGER, Armor.LEATHER, Shield.LARGE_SHIELD);
//        heroSet.put(hero5, new Integer(0));
//        Hero hero6 = new Hero("006:ST8;DX16;DAGGER;CHAIN;LARGE_SHIELD", 8, 16, Weapon.DAGGER, Armor.CHAIN, Shield.LARGE_SHIELD);
//        heroSet.put(hero6, new Integer(0));
//        Hero hero7 = new Hero("007:ST8;DX16;DAGGER;PLATE;LARGE_SHIELD", 8, 16, Weapon.DAGGER, Armor.PLATE, Shield.LARGE_SHIELD);
//        heroSet.put(hero7, new Integer(0));
//        Hero hero8 = new Hero("008:ST9;DX15;RAPIER;NO_ARMOR;SMALL_SHIELD", 9, 15, Weapon.RAPIER, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
//        heroSet.put(hero8, new Integer(0));
//        Hero hero9 = new Hero("009:ST9;DX15;RAPIER;LEATHER;SMALL_SHIELD", 9, 15, Weapon.RAPIER, Armor.LEATHER, Shield.SMALL_SHIELD);
//        heroSet.put(hero9, new Integer(0));
//        Hero hero10 = new Hero("010:ST9;DX15;RAPIER;CHAIN;SMALL_SHIELD", 9, 15, Weapon.RAPIER, Armor.CHAIN, Shield.SMALL_SHIELD);
//        heroSet.put(hero10, new Integer(0));
//        Hero hero11 = new Hero("011:ST9;DX15;RAPIER;PLATE;SMALL_SHIELD", 9, 15, Weapon.RAPIER, Armor.PLATE, Shield.SMALL_SHIELD);
//        heroSet.put(hero11, new Integer(0));
//        Hero hero12 = new Hero("012:ST9;DX15;RAPIER;LEATHER;LARGE_SHIELD", 9, 15, Weapon.RAPIER, Armor.LEATHER, Shield.LARGE_SHIELD);
//        heroSet.put(hero12, new Integer(0));
//        Hero hero13 = new Hero("013:ST9;DX15;RAPIER;CHAIN;LARGE_SHIELD", 9, 15, Weapon.RAPIER, Armor.CHAIN, Shield.LARGE_SHIELD);
//        heroSet.put(hero13, new Integer(0));
//        Hero hero14 = new Hero("014:ST9;DX15;RAPIER;PLATE;LARGE_SHIELD", 9, 15, Weapon.RAPIER, Armor.PLATE, Shield.LARGE_SHIELD);
//        heroSet.put(hero14, new Integer(0));
//        Hero hero15 = new Hero("015:ST9;DX15;CLUB;NO_ARMOR;SMALL_SHIELD", 9, 15, Weapon.CLUB, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
//        heroSet.put(hero15, new Integer(0));
//        Hero hero16 = new Hero("016:ST9;DX15;CLUB;LEATHER;SMALL_SHIELD", 9, 15, Weapon.CLUB, Armor.LEATHER, Shield.SMALL_SHIELD);
//        heroSet.put(hero16, new Integer(0));
//        Hero hero17 = new Hero("017:ST9;DX15;CLUB;CHAIN;SMALL_SHIELD", 9, 15, Weapon.CLUB, Armor.CHAIN, Shield.SMALL_SHIELD);
//        heroSet.put(hero17, new Integer(0));
//        Hero hero18 = new Hero("018:ST9;DX15;CLUB;PLATE;SMALL_SHIELD", 9, 15, Weapon.CLUB, Armor.PLATE, Shield.SMALL_SHIELD);
//        heroSet.put(hero18, new Integer(0));
//        Hero hero19 = new Hero("019:ST9;DX15;CLUB;LEATHER;LARGE_SHIELD", 9, 15, Weapon.CLUB, Armor.LEATHER, Shield.LARGE_SHIELD);
//        heroSet.put(hero19, new Integer(0));
//        Hero hero20 = new Hero("020:ST9;DX15;CLUB;CHAIN;LARGE_SHIELD", 9, 15, Weapon.CLUB, Armor.CHAIN, Shield.LARGE_SHIELD);
//        heroSet.put(hero20, new Integer(0));
//        Hero hero21 = new Hero("021:ST9;DX15;CLUB;PLATE;LARGE_SHIELD", 9, 15, Weapon.CLUB, Armor.PLATE, Shield.LARGE_SHIELD);
//        heroSet.put(hero21, new Integer(0));
//        Hero hero22 = new Hero("022:ST9;DX15;JAVELIN;NO_ARMOR;SMALL_SHIELD", 9, 15, Weapon.JAVELIN, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
//        heroSet.put(hero22, new Integer(0));
//        Hero hero23 = new Hero("023:ST9;DX15;JAVELIN;LEATHER;SMALL_SHIELD", 9, 15, Weapon.JAVELIN, Armor.LEATHER, Shield.SMALL_SHIELD);
//        heroSet.put(hero23, new Integer(0));
//        Hero hero24 = new Hero("024:ST9;DX15;JAVELIN;CHAIN;SMALL_SHIELD", 9, 15, Weapon.JAVELIN, Armor.CHAIN, Shield.SMALL_SHIELD);
//        heroSet.put(hero24, new Integer(0));
//        Hero hero25 = new Hero("025:ST9;DX15;JAVELIN;PLATE;SMALL_SHIELD", 9, 15, Weapon.JAVELIN, Armor.PLATE, Shield.SMALL_SHIELD);
//        heroSet.put(hero25, new Integer(0));
//        Hero hero26 = new Hero("026:ST9;DX15;JAVELIN;LEATHER;LARGE_SHIELD", 9, 15, Weapon.JAVELIN, Armor.LEATHER, Shield.LARGE_SHIELD);
//        heroSet.put(hero26, new Integer(0));
//        Hero hero27 = new Hero("027:ST9;DX15;JAVELIN;CHAIN;LARGE_SHIELD", 9, 15, Weapon.JAVELIN, Armor.CHAIN, Shield.LARGE_SHIELD);
//        heroSet.put(hero27, new Integer(0));
//        Hero hero28 = new Hero("028:ST9;DX15;JAVELIN;PLATE;LARGE_SHIELD", 9, 15, Weapon.JAVELIN, Armor.PLATE, Shield.LARGE_SHIELD);
//        heroSet.put(hero28, new Integer(0));
//        Hero hero29 = new Hero("029:ST10;DX14;HAMMER;NO_ARMOR;SMALL_SHIELD", 10, 14, Weapon.HAMMER, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
//        heroSet.put(hero29, new Integer(0));
//        Hero hero30 = new Hero("030:ST10;DX14;HAMMER;LEATHER;SMALL_SHIELD", 10, 14, Weapon.HAMMER, Armor.LEATHER, Shield.SMALL_SHIELD);
//        heroSet.put(hero30, new Integer(0));
//        Hero hero31 = new Hero("031:ST10;DX14;HAMMER;CHAIN;SMALL_SHIELD", 10, 14, Weapon.HAMMER, Armor.CHAIN, Shield.SMALL_SHIELD);
//        heroSet.put(hero31, new Integer(0));
//        Hero hero32 = new Hero("032:ST10;DX14;HAMMER;PLATE;SMALL_SHIELD", 10, 14, Weapon.HAMMER, Armor.PLATE, Shield.SMALL_SHIELD);
//        heroSet.put(hero32, new Integer(0));
//        Hero hero33 = new Hero("033:ST10;DX14;HAMMER;LEATHER;LARGE_SHIELD", 10, 14, Weapon.HAMMER, Armor.LEATHER, Shield.LARGE_SHIELD);
//        heroSet.put(hero33, new Integer(0));
//        Hero hero34 = new Hero("034:ST10;DX14;HAMMER;CHAIN;LARGE_SHIELD", 10, 14, Weapon.HAMMER, Armor.CHAIN, Shield.LARGE_SHIELD);
//        heroSet.put(hero34, new Integer(0));
//        Hero hero35 = new Hero("035:ST10;DX14;HAMMER;PLATE;LARGE_SHIELD", 10, 14, Weapon.HAMMER, Armor.PLATE, Shield.LARGE_SHIELD);
//        heroSet.put(hero35, new Integer(0));
//        Hero hero36 = new Hero("036:ST10;DX14;CUTLASS;NO_ARMOR;SMALL_SHIELD", 10, 14, Weapon.CUTLASS, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
//        heroSet.put(hero36, new Integer(0));
//        Hero hero37 = new Hero("037:ST10;DX14;CUTLASS;LEATHER;SMALL_SHIELD", 10, 14, Weapon.CUTLASS, Armor.LEATHER, Shield.SMALL_SHIELD);
//        heroSet.put(hero37, new Integer(0));
//        Hero hero38 = new Hero("038:ST10;DX14;CUTLASS;CHAIN;SMALL_SHIELD", 10, 14, Weapon.CUTLASS, Armor.CHAIN, Shield.SMALL_SHIELD);
//        heroSet.put(hero38, new Integer(0));
//        Hero hero39 = new Hero("039:ST10;DX14;CUTLASS;PLATE;SMALL_SHIELD", 10, 14, Weapon.CUTLASS, Armor.PLATE, Shield.SMALL_SHIELD);
//        heroSet.put(hero39, new Integer(0));
//        Hero hero39b = new Hero("039b:ST10;DX14;CUTLASS;NO_ARMOR;LARGE_SHIELD", 10, 14, Weapon.CUTLASS, Armor.NO_ARMOR, Shield.LARGE_SHIELD);
//        heroSet.put(hero39b, new Integer(0));
//        Hero hero40 = new Hero("040:ST10;DX14;CUTLASS;LEATHER;LARGE_SHIELD", 10, 14, Weapon.CUTLASS, Armor.LEATHER, Shield.LARGE_SHIELD);
//        heroSet.put(hero40, new Integer(0));
//        Hero hero41 = new Hero("041:ST10;DX14;CUTLASS;CHAIN;LARGE_SHIELD", 10, 14, Weapon.CUTLASS, Armor.CHAIN, Shield.LARGE_SHIELD);
//        heroSet.put(hero41, new Integer(0));
//        Hero hero42 = new Hero("042:ST10;DX14;CUTLASS;PLATE;LARGE_SHIELD", 10, 14, Weapon.CUTLASS, Armor.PLATE, Shield.LARGE_SHIELD);
//        heroSet.put(hero42, new Integer(0));
//        Hero hero43 = new Hero("043:ST11;DX13;SHORTSWORD;NO_ARMOR;SMALL_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
//        heroSet.put(hero43, new Integer(0));
//        Hero hero44 = new Hero("044:ST11;DX13;SHORTSWORD;LEATHER;SMALL_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.LEATHER, Shield.SMALL_SHIELD);
//        heroSet.put(hero44, new Integer(0));
//        Hero hero45 = new Hero("045:ST11;DX13;SHORTSWORD;CHAIN;SMALL_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.CHAIN, Shield.SMALL_SHIELD);
//        heroSet.put(hero45, new Integer(0));
//        Hero hero46 = new Hero("046:ST11;DX13;SHORTSWORD;PLATE;SMALL_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.PLATE, Shield.SMALL_SHIELD);
//        heroSet.put(hero46, new Integer(0));
//        Hero hero47 = new Hero("047:ST11;DX13;SHORTSWORD;LEATHER;LARGE_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.LEATHER, Shield.LARGE_SHIELD);
//        heroSet.put(hero47, new Integer(0));
//        Hero hero48 = new Hero("048:ST11;DX13;SHORTSWORD;CHAIN;LARGE_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.CHAIN, Shield.LARGE_SHIELD);
//        heroSet.put(hero48, new Integer(0));
//        Hero hero49 = new Hero("049:ST11;DX13;SHORTSWORD;PLATE;LARGE_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.PLATE, Shield.LARGE_SHIELD);
//        heroSet.put(hero49, new Integer(0));
//        Hero hero50 = new Hero("050:ST11;DX13;MACE;NO_ARMOR;SMALL_SHIELD", 11, 13, Weapon.MACE, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
//        heroSet.put(hero50, new Integer(0));
//        Hero hero51 = new Hero("051:ST11;DX13;MACE;LEATHER;SMALL_SHIELD", 11, 13, Weapon.MACE, Armor.LEATHER, Shield.SMALL_SHIELD);
//        heroSet.put(hero51, new Integer(0));
//        Hero hero52 = new Hero("052:ST11;DX13;MACE;CHAIN;SMALL_SHIELD", 11, 13, Weapon.MACE, Armor.CHAIN, Shield.SMALL_SHIELD);
//        heroSet.put(hero52, new Integer(0));
//        Hero hero53 = new Hero("053:ST11;DX13;MACE;PLATE;SMALL_SHIELD", 11, 13, Weapon.MACE, Armor.PLATE, Shield.SMALL_SHIELD);
//        heroSet.put(hero53, new Integer(0));
//        Hero hero54 = new Hero("054:ST11;DX13;MACE;LEATHER;LARGE_SHIELD", 11, 13, Weapon.MACE, Armor.LEATHER, Shield.LARGE_SHIELD);
//        heroSet.put(hero54, new Integer(0));
//        Hero hero55 = new Hero("055:ST11;DX13;MACE;CHAIN;LARGE_SHIELD", 11, 13, Weapon.MACE, Armor.CHAIN, Shield.LARGE_SHIELD);
//        heroSet.put(hero55, new Integer(0));
//        Hero hero56 = new Hero("056:ST11;DX13;MACE;PLATE;LARGE_SHIELD", 11, 13, Weapon.MACE, Armor.PLATE, Shield.LARGE_SHIELD);
//        heroSet.put(hero56, new Integer(0));
//        Hero hero57 = new Hero("057:ST11;DX13;SPEAR;NO_ARMOR;NO_SHIELD", 11, 13, Weapon.SPEAR, Armor.NO_ARMOR, Shield.NO_SHIELD);
//        heroSet.put(hero57, new Integer(0));
//        Hero hero58 = new Hero("058:ST11;DX13;SPEAR;LEATHER;NO_SHIELD", 11, 13, Weapon.SPEAR, Armor.LEATHER, Shield.NO_SHIELD);
//        heroSet.put(hero58, new Integer(0));
//        Hero hero59 = new Hero("059:ST11;DX13;SPEAR;CHAIN;NO_SHIELD", 11, 13, Weapon.SPEAR, Armor.CHAIN, Shield.NO_SHIELD);
//        heroSet.put(hero59, new Integer(0));
//        Hero hero60 = new Hero("060:ST11;DX13;SPEAR;PLATE;NO_SHIELD", 11, 13, Weapon.SPEAR, Armor.PLATE, Shield.NO_SHIELD);
//        heroSet.put(hero60, new Integer(0));
//		Hero hero61 = new Hero("061:ST12;DX12;BROADSWORD;NO_ARMOR;SMALL_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
//		heroSet.put(hero61, new Integer(0));
////		Hero hero = new Hero("061b:ST12;DX12;BROADSWORD;NO_ARMOR;SMALL_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
////		heroSet.put(hero, new Integer(0));
//        Hero hero62 = new Hero("062:ST12;DX12;BROADSWORD;LEATHER;SMALL_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.LEATHER, Shield.SMALL_SHIELD);
//        heroSet.put(hero62, new Integer(0));
//        Hero hero63 = new Hero("063:ST12;DX12;BROADSWORD;CHAIN;SMALL_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.CHAIN, Shield.SMALL_SHIELD);
//        heroSet.put(hero63, new Integer(0));
//        Hero hero64 = new Hero("064:ST12;DX12;BROADSWORD;PLATE;SMALL_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.PLATE, Shield.SMALL_SHIELD);
//        heroSet.put(hero64, new Integer(0));
//        Hero hero65 = new Hero("065:ST12;DX12;BROADSWORD;LEATHER;LARGE_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.LEATHER, Shield.LARGE_SHIELD);
//        heroSet.put(hero65, new Integer(0));
//        Hero hero66 = new Hero("066:ST12;DX12;BROADSWORD;CHAIN;LARGE_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.CHAIN, Shield.LARGE_SHIELD);
//        heroSet.put(hero66, new Integer(0));
//        Hero hero67 = new Hero("067:ST12;DX12;BROADSWORD;PLATE;LARGE_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.PLATE, Shield.LARGE_SHIELD);
//        heroSet.put(hero67, new Integer(0));
//        Hero hero68 = new Hero("068:ST13;DX11;MORNINGSTAR;NO_ARMOR;SMALL_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.NO_ARMOR, Shield.SMALL_SHIELD);
//        heroSet.put(hero68, new Integer(0));
//        Hero hero69 = new Hero("069:ST13;DX11;MORNINGSTAR;LEATHER;SMALL_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.LEATHER, Shield.SMALL_SHIELD);
//        heroSet.put(hero69, new Integer(0));
//        Hero hero70 = new Hero("070:ST13;DX11;MORNINGSTAR;CHAIN;SMALL_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.CHAIN, Shield.SMALL_SHIELD);
//        heroSet.put(hero70, new Integer(0));
//        Hero hero71 = new Hero("071:ST13;DX11;MORNINGSTAR;PLATE;SMALL_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.PLATE, Shield.SMALL_SHIELD);
//        heroSet.put(hero71, new Integer(0));
//        Hero hero72 = new Hero("072:ST13;DX11;MORNINGSTAR;LEATHER;LARGE_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.LEATHER, Shield.LARGE_SHIELD);
//        heroSet.put(hero72, new Integer(0));
//        Hero hero73 = new Hero("073:ST13;DX11;MORNINGSTAR;CHAIN;LARGE_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.CHAIN, Shield.LARGE_SHIELD);
//        heroSet.put(hero73, new Integer(0));
//        Hero hero74 = new Hero("074:ST13;DX11;MORNINGSTAR;PLATE;LARGE_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.PLATE, Shield.LARGE_SHIELD);
//        heroSet.put(hero74, new Integer(0));
//        Hero hero75 = new Hero("075:ST13;DX11;HALBERD;NO_ARMOR;NO_SHIELD", 13, 11, Weapon.HALBERD, Armor.NO_ARMOR, Shield.NO_SHIELD);
//        heroSet.put(hero75, new Integer(0));
//        Hero hero76 = new Hero("076:ST13;DX11;HALBERD;LEATHER;NO_SHIELD", 13, 11, Weapon.HALBERD, Armor.LEATHER, Shield.NO_SHIELD);
//        heroSet.put(hero76, new Integer(0));
//        Hero hero77 = new Hero("077:ST13;DX11;HALBERD;CHAIN;NO_SHIELD", 13, 11, Weapon.HALBERD, Armor.CHAIN, Shield.NO_SHIELD);
//        heroSet.put(hero77, new Integer(0));
//        Hero hero78 = new Hero("078:ST13;DX11;HALBERD;PLATE;NO_SHIELD", 13, 11, Weapon.HALBERD, Armor.PLATE, Shield.NO_SHIELD);
//        heroSet.put(hero78, new Integer(0));
//        Hero hero79 = new Hero("079:ST14;DX10;TWO_HANDED_SWORD;NO_ARMOR;NO_SHIELD", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.NO_ARMOR, Shield.NO_SHIELD);
//        heroSet.put(hero79, new Integer(0));
//        Hero hero80 = new Hero("080:ST14;DX10;TWO_HANDED_SWORD;LEATHER;NO_SHIELD", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.LEATHER, Shield.NO_SHIELD);
//        heroSet.put(hero80, new Integer(0));
//        Hero hero81 = new Hero("081:ST14;DX10;TWO_HANDED_SWORD;CHAIN;NO_SHIELD", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.CHAIN, Shield.NO_SHIELD);
//        heroSet.put(hero81, new Integer(0));
//        Hero hero82 = new Hero("082:ST14;DX10;TWO_HANDED_SWORD;PLATE;NO_SHIELD", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.PLATE, Shield.NO_SHIELD);
//        heroSet.put(hero82, new Integer(0));
//        Hero hero83 = new Hero("083:ST15;DX9;BATTLEAXE;NO_ARMOR;NO_SHIELD", 15, 9, Weapon.BATTLEAXE, Armor.NO_ARMOR, Shield.NO_SHIELD);
//        heroSet.put(hero83, new Integer(0));
//        Hero hero84 = new Hero("084:ST15;DX9;BATTLEAXE;LEATHER;NO_SHIELD", 15, 9, Weapon.BATTLEAXE, Armor.LEATHER, Shield.NO_SHIELD);
//        heroSet.put(hero84, new Integer(0));
//        Hero hero85 = new Hero("085:ST15;DX9;BATTLEAXE;CHAIN;NO_SHIELD", 15, 9, Weapon.BATTLEAXE, Armor.CHAIN, Shield.NO_SHIELD);
//        heroSet.put(hero85, new Integer(0));
//        Hero hero86 = new Hero("086:ST15;DX9;BATTLEAXE;PLATE;NO_SHIELD", 15, 9, Weapon.BATTLEAXE, Armor.PLATE, Shield.NO_SHIELD);
//        heroSet.put(hero86, new Integer(0));
//        Hero hero87 = new Hero("087:ST15;DX9;PIKE_AXE;NO_ARMOR;NO_SHIELD", 15, 9, Weapon.PIKE_AXE, Armor.NO_ARMOR, Shield.NO_SHIELD);
//        heroSet.put(hero87, new Integer(0));
//        Hero hero88 = new Hero("088:ST15;DX9;PIKE_AXE;LEATHER;NO_SHIELD", 15, 9, Weapon.PIKE_AXE, Armor.LEATHER, Shield.NO_SHIELD);
//        heroSet.put(hero88, new Integer(0));
//        Hero hero89 = new Hero("089:ST15;DX9;PIKE_AXE;CHAIN;NO_SHIELD", 15, 9, Weapon.PIKE_AXE, Armor.CHAIN, Shield.NO_SHIELD);
//        heroSet.put(hero89, new Integer(0));
//        Hero hero90 = new Hero("090:ST15;DX9;PIKE_AXE;PLATE;NO_SHIELD", 15, 9, Weapon.PIKE_AXE, Armor.PLATE, Shield.NO_SHIELD);
//        heroSet.put(hero90, new Integer(0));
//        Hero hero91 = new Hero("091:ST16;DX8;BATTLEAXE;NO_ARMOR;NO_SHIELD", 16, 8, Weapon.BATTLEAXE, Armor.NO_ARMOR, Shield.NO_SHIELD);
//        heroSet.put(hero91, new Integer(0));
//        Hero hero92 = new Hero("092:ST16;DX8;BATTLEAXE;LEATHER;NO_SHIELD", 16, 8, Weapon.BATTLEAXE, Armor.LEATHER, Shield.NO_SHIELD);
//        heroSet.put(hero92, new Integer(0));
//        Hero hero93 = new Hero("093:ST16;DX8;BATTLEAXE;CHAIN;NO_SHIELD", 16, 8, Weapon.BATTLEAXE, Armor.CHAIN, Shield.NO_SHIELD);
//        heroSet.put(hero93, new Integer(0));
//        Hero hero94 = new Hero("094:ST16;DX8;BATTLEAXE;PLATE;NO_SHIELD", 16, 8, Weapon.BATTLEAXE, Armor.PLATE, Shield.NO_SHIELD);
//        heroSet.put(hero94, new Integer(0));
//        Hero hero95 = new Hero("095:ST16;DX8;PIKE_AXE;NO_ARMOR;NO_SHIELD", 16, 8, Weapon.PIKE_AXE, Armor.NO_ARMOR, Shield.NO_SHIELD);
//        heroSet.put(hero95, new Integer(0));
//        Hero hero96 = new Hero("096:ST16;DX8;PIKE_AXE;LEATHER;NO_SHIELD", 16, 8, Weapon.PIKE_AXE, Armor.LEATHER, Shield.NO_SHIELD);
//        heroSet.put(hero96, new Integer(0));
//        Hero hero97 = new Hero("097:ST16;DX8;PIKE_AXE;CHAIN;NO_SHIELD", 16, 8, Weapon.PIKE_AXE, Armor.CHAIN, Shield.NO_SHIELD);
//        heroSet.put(hero97, new Integer(0));
//        Hero hero98 = new Hero("098:ST16;DX8;PIKE_AXE;PLATE;NO_SHIELD", 16, 8, Weapon.PIKE_AXE, Armor.PLATE, Shield.NO_SHIELD);
//        heroSet.put(hero98, new Integer(0));
        heroSet.put(new Hero("001:ST8;DX16;DAGGER;NO_ARMOR;SMALL_SHIELD", 8, 16, Weapon.DAGGER, Armor.NO_ARMOR, Shield.SMALL_SHIELD), new Integer(0));
        
        heroSet.put(new Hero("002:ST8;DX16;DAGGER;LEATHER;SMALL_SHIELD", 8, 16, Weapon.DAGGER, Armor.LEATHER, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("003:ST8;DX16;DAGGER;CHAIN;SMALL_SHIELD", 8, 16, Weapon.DAGGER, Armor.CHAIN, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("004:ST8;DX16;DAGGER;PLATE;SMALL_SHIELD", 8, 16, Weapon.DAGGER, Armor.PLATE, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("005:ST8;DX16;DAGGER;NO_ARMOR;LARGE_SHIELD", 8, 16, Weapon.DAGGER, Armor.NO_ARMOR, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("006:ST8;DX16;DAGGER;LEATHER;LARGE_SHIELD", 8, 16, Weapon.DAGGER, Armor.LEATHER, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("007:ST8;DX16;DAGGER;CHAIN;LARGE_SHIELD", 8, 16, Weapon.DAGGER, Armor.CHAIN, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("008:ST8;DX16;DAGGER;PLATE;LARGE_SHIELD", 8, 16, Weapon.DAGGER, Armor.PLATE, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("009:ST9;DX15;RAPIER;NO_ARMOR;SMALL_SHIELD", 9, 15, Weapon.RAPIER, Armor.NO_ARMOR, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("010:ST9;DX15;RAPIER;LEATHER;SMALL_SHIELD", 9, 15, Weapon.RAPIER, Armor.LEATHER, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("011:ST9;DX15;RAPIER;CHAIN;SMALL_SHIELD", 9, 15, Weapon.RAPIER, Armor.CHAIN, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("012:ST9;DX15;RAPIER;PLATE;SMALL_SHIELD", 9, 15, Weapon.RAPIER, Armor.PLATE, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("013:ST9;DX15;RAPIER;NO_ARMOR;LARGE_SHIELD", 9, 15, Weapon.RAPIER, Armor.NO_ARMOR, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("014:ST9;DX15;RAPIER;LEATHER;LARGE_SHIELD", 9, 15, Weapon.RAPIER, Armor.LEATHER, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("015:ST9;DX15;RAPIER;CHAIN;LARGE_SHIELD", 9, 15, Weapon.RAPIER, Armor.CHAIN, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("016:ST9;DX15;RAPIER;PLATE;LARGE_SHIELD", 9, 15, Weapon.RAPIER, Armor.PLATE, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("017:ST9;DX15;CLUB;NO_ARMOR;SMALL_SHIELD", 9, 15, Weapon.CLUB, Armor.NO_ARMOR, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("018:ST9;DX15;CLUB;LEATHER;SMALL_SHIELD", 9, 15, Weapon.CLUB, Armor.LEATHER, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("019:ST9;DX15;CLUB;CHAIN;SMALL_SHIELD", 9, 15, Weapon.CLUB, Armor.CHAIN, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("020:ST9;DX15;CLUB;PLATE;SMALL_SHIELD", 9, 15, Weapon.CLUB, Armor.PLATE, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("021:ST9;DX15;CLUB;NO_ARMOR;LARGE_SHIELD", 9, 15, Weapon.CLUB, Armor.NO_ARMOR, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("022:ST9;DX15;CLUB;LEATHER;LARGE_SHIELD", 9, 15, Weapon.CLUB, Armor.LEATHER, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("023:ST9;DX15;CLUB;CHAIN;LARGE_SHIELD", 9, 15, Weapon.CLUB, Armor.CHAIN, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("024:ST9;DX15;CLUB;PLATE;LARGE_SHIELD", 9, 15, Weapon.CLUB, Armor.PLATE, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("025:ST9;DX15;JAVELIN;NO_ARMOR;SMALL_SHIELD", 9, 15, Weapon.JAVELIN, Armor.NO_ARMOR, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("026:ST9;DX15;JAVELIN;LEATHER;SMALL_SHIELD", 9, 15, Weapon.JAVELIN, Armor.LEATHER, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("027:ST9;DX15;JAVELIN;CHAIN;SMALL_SHIELD", 9, 15, Weapon.JAVELIN, Armor.CHAIN, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("028:ST9;DX15;JAVELIN;PLATE;SMALL_SHIELD", 9, 15, Weapon.JAVELIN, Armor.PLATE, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("029:ST9;DX15;JAVELIN;NO_ARMOR;LARGE_SHIELD", 9, 15, Weapon.JAVELIN, Armor.NO_ARMOR, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("030:ST9;DX15;JAVELIN;LEATHER;LARGE_SHIELD", 9, 15, Weapon.JAVELIN, Armor.LEATHER, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("031:ST9;DX15;JAVELIN;CHAIN;LARGE_SHIELD", 9, 15, Weapon.JAVELIN, Armor.CHAIN, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("032:ST9;DX15;JAVELIN;PLATE;LARGE_SHIELD", 9, 15, Weapon.JAVELIN, Armor.PLATE, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("033:ST10;DX14;HAMMER;NO_ARMOR;SMALL_SHIELD", 10, 14, Weapon.HAMMER, Armor.NO_ARMOR, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("034:ST10;DX14;HAMMER;LEATHER;SMALL_SHIELD", 10, 14, Weapon.HAMMER, Armor.LEATHER, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("035:ST10;DX14;HAMMER;CHAIN;SMALL_SHIELD", 10, 14, Weapon.HAMMER, Armor.CHAIN, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("036:ST10;DX14;HAMMER;PLATE;SMALL_SHIELD", 10, 14, Weapon.HAMMER, Armor.PLATE, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("037:ST10;DX14;HAMMER;NO_ARMOR;LARGE_SHIELD", 10, 14, Weapon.HAMMER, Armor.NO_ARMOR, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("038:ST10;DX14;HAMMER;LEATHER;LARGE_SHIELD", 10, 14, Weapon.HAMMER, Armor.LEATHER, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("039:ST10;DX14;HAMMER;CHAIN;LARGE_SHIELD", 10, 14, Weapon.HAMMER, Armor.CHAIN, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("040:ST10;DX14;HAMMER;PLATE;LARGE_SHIELD", 10, 14, Weapon.HAMMER, Armor.PLATE, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("041:ST10;DX14;CUTLASS;NO_ARMOR;SMALL_SHIELD", 10, 14, Weapon.CUTLASS, Armor.NO_ARMOR, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("042:ST10;DX14;CUTLASS;LEATHER;SMALL_SHIELD", 10, 14, Weapon.CUTLASS, Armor.LEATHER, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("043:ST10;DX14;CUTLASS;CHAIN;SMALL_SHIELD", 10, 14, Weapon.CUTLASS, Armor.CHAIN, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("044:ST10;DX14;CUTLASS;PLATE;SMALL_SHIELD", 10, 14, Weapon.CUTLASS, Armor.PLATE, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("045:ST10;DX14;CUTLASS;NO_ARMOR;LARGE_SHIELD", 10, 14, Weapon.CUTLASS, Armor.NO_ARMOR, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("046:ST10;DX14;CUTLASS;LEATHER;LARGE_SHIELD", 10, 14, Weapon.CUTLASS, Armor.LEATHER, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("047:ST10;DX14;CUTLASS;CHAIN;LARGE_SHIELD", 10, 14, Weapon.CUTLASS, Armor.CHAIN, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("048:ST10;DX14;CUTLASS;PLATE;LARGE_SHIELD", 10, 14, Weapon.CUTLASS, Armor.PLATE, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("049:ST11;DX13;SHORTSWORD;NO_ARMOR;SMALL_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.NO_ARMOR, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("050:ST11;DX13;SHORTSWORD;LEATHER;SMALL_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.LEATHER, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("051:ST11;DX13;SHORTSWORD;CHAIN;SMALL_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.CHAIN, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("052:ST11;DX13;SHORTSWORD;PLATE;SMALL_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.PLATE, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("053:ST11;DX13;SHORTSWORD;NO_ARMOR;LARGE_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.NO_ARMOR, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("054:ST11;DX13;SHORTSWORD;LEATHER;LARGE_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.LEATHER, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("055:ST11;DX13;SHORTSWORD;CHAIN;LARGE_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.CHAIN, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("056:ST11;DX13;SHORTSWORD;PLATE;LARGE_SHIELD", 11, 13, Weapon.SHORTSWORD, Armor.PLATE, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("057:ST11;DX13;MACE;NO_ARMOR;SMALL_SHIELD", 11, 13, Weapon.MACE, Armor.NO_ARMOR, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("058:ST11;DX13;MACE;LEATHER;SMALL_SHIELD", 11, 13, Weapon.MACE, Armor.LEATHER, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("059:ST11;DX13;MACE;CHAIN;SMALL_SHIELD", 11, 13, Weapon.MACE, Armor.CHAIN, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("060:ST11;DX13;MACE;PLATE;SMALL_SHIELD", 11, 13, Weapon.MACE, Armor.PLATE, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("061:ST11;DX13;MACE;NO_ARMOR;LARGE_SHIELD", 11, 13, Weapon.MACE, Armor.NO_ARMOR, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("062:ST11;DX13;MACE;LEATHER;LARGE_SHIELD", 11, 13, Weapon.MACE, Armor.LEATHER, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("063:ST11;DX13;MACE;CHAIN;LARGE_SHIELD", 11, 13, Weapon.MACE, Armor.CHAIN, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("064:ST11;DX13;MACE;PLATE;LARGE_SHIELD", 11, 13, Weapon.MACE, Armor.PLATE, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("065:ST11;DX13;SPEAR;NO_ARMOR;NO_SHIELD", 11, 13, Weapon.SPEAR, Armor.NO_ARMOR, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("066:ST11;DX13;SPEAR;LEATHER;NO_SHIELD", 11, 13, Weapon.SPEAR, Armor.LEATHER, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("067:ST11;DX13;SPEAR;CHAIN;NO_SHIELD", 11, 13, Weapon.SPEAR, Armor.CHAIN, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("068:ST11;DX13;SPEAR;PLATE;NO_SHIELD", 11, 13, Weapon.SPEAR, Armor.PLATE, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("069:ST12;DX12;BROADSWORD;NO_ARMOR;SMALL_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.NO_ARMOR, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("070:ST12;DX12;BROADSWORD;LEATHER;SMALL_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.LEATHER, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("071:ST12;DX12;BROADSWORD;CHAIN;SMALL_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.CHAIN, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("072:ST12;DX12;BROADSWORD;PLATE;SMALL_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.PLATE, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("073:ST12;DX12;BROADSWORD;NO_ARMOR;LARGE_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.NO_ARMOR, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("074:ST12;DX12;BROADSWORD;LEATHER;LARGE_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.LEATHER, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("075:ST12;DX12;BROADSWORD;CHAIN;LARGE_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.CHAIN, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("076:ST12;DX12;BROADSWORD;PLATE;LARGE_SHIELD", 12, 12, Weapon.BROADSWORD, Armor.PLATE, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("077:ST13;DX11;MORNINGSTAR;NO_ARMOR;SMALL_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.NO_ARMOR, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("078:ST13;DX11;MORNINGSTAR;LEATHER;SMALL_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.LEATHER, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("079:ST13;DX11;MORNINGSTAR;CHAIN;SMALL_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.CHAIN, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("080:ST13;DX11;MORNINGSTAR;PLATE;SMALL_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.PLATE, Shield.SMALL_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("081:ST13;DX11;MORNINGSTAR;NO_ARMOR;LARGE_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.NO_ARMOR, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("082:ST13;DX11;MORNINGSTAR;LEATHER;LARGE_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.LEATHER, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("083:ST13;DX11;MORNINGSTAR;CHAIN;LARGE_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.CHAIN, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("084:ST13;DX11;MORNINGSTAR;PLATE;LARGE_SHIELD", 13, 11, Weapon.MORNINGSTAR, Armor.PLATE, Shield.LARGE_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("085:ST13;DX11;HALBERD;NO_ARMOR;NO_SHIELD", 13, 11, Weapon.HALBERD, Armor.NO_ARMOR, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("086:ST13;DX11;HALBERD;LEATHER;NO_SHIELD", 13, 11, Weapon.HALBERD, Armor.LEATHER, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("087:ST13;DX11;HALBERD;CHAIN;NO_SHIELD", 13, 11, Weapon.HALBERD, Armor.CHAIN, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("088:ST13;DX11;HALBERD;PLATE;NO_SHIELD", 13, 11, Weapon.HALBERD, Armor.PLATE, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("089:ST14;DX10;TWO_HANDED_SWORD;NO_ARMOR;NO_SHIELD", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.NO_ARMOR, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("090:ST14;DX10;TWO_HANDED_SWORD;LEATHER;NO_SHIELD", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.LEATHER, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("091:ST14;DX10;TWO_HANDED_SWORD;CHAIN;NO_SHIELD", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.CHAIN, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("092:ST14;DX10;TWO_HANDED_SWORD;PLATE;NO_SHIELD", 14, 10, Weapon.TWO_HANDED_SWORD, Armor.PLATE, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("093:ST15;DX9;BATTLEAXE;NO_ARMOR;NO_SHIELD", 15, 9, Weapon.BATTLEAXE, Armor.NO_ARMOR, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("094:ST15;DX9;BATTLEAXE;LEATHER;NO_SHIELD", 15, 9, Weapon.BATTLEAXE, Armor.LEATHER, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("095:ST15;DX9;BATTLEAXE;CHAIN;NO_SHIELD", 15, 9, Weapon.BATTLEAXE, Armor.CHAIN, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("096:ST15;DX9;BATTLEAXE;PLATE;NO_SHIELD", 15, 9, Weapon.BATTLEAXE, Armor.PLATE, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("097:ST15;DX9;PIKE_AXE;NO_ARMOR;NO_SHIELD", 15, 9, Weapon.PIKE_AXE, Armor.NO_ARMOR, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("098:ST15;DX9;PIKE_AXE;LEATHER;NO_SHIELD", 15, 9, Weapon.PIKE_AXE, Armor.LEATHER, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("099:ST15;DX9;PIKE_AXE;CHAIN;NO_SHIELD", 15, 9, Weapon.PIKE_AXE, Armor.CHAIN, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("100:ST15;DX9;PIKE_AXE;PLATE;NO_SHIELD", 15, 9, Weapon.PIKE_AXE, Armor.PLATE, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("101:ST16;DX8;BATTLEAXE;NO_ARMOR;NO_SHIELD", 16, 8, Weapon.BATTLEAXE, Armor.NO_ARMOR, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("102:ST16;DX8;BATTLEAXE;LEATHER;NO_SHIELD", 16, 8, Weapon.BATTLEAXE, Armor.LEATHER, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("103:ST16;DX8;BATTLEAXE;CHAIN;NO_SHIELD", 16, 8, Weapon.BATTLEAXE, Armor.CHAIN, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("104:ST16;DX8;BATTLEAXE;PLATE;NO_SHIELD", 16, 8, Weapon.BATTLEAXE, Armor.PLATE, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("105:ST16;DX8;PIKE_AXE;NO_ARMOR;NO_SHIELD", 16, 8, Weapon.PIKE_AXE, Armor.NO_ARMOR, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("106:ST16;DX8;PIKE_AXE;LEATHER;NO_SHIELD", 16, 8, Weapon.PIKE_AXE, Armor.LEATHER, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("107:ST16;DX8;PIKE_AXE;CHAIN;NO_SHIELD", 16, 8, Weapon.PIKE_AXE, Armor.CHAIN, Shield.NO_SHIELD), new Integer(0));
         
        heroSet.put(new Hero("108:ST16;DX8;PIKE_AXE;PLATE;NO_SHIELD", 16, 8, Weapon.PIKE_AXE, Armor.PLATE, Shield.NO_SHIELD), new Integer(0));
         

        
        
        return heroSet;
    }
    
}
