package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import core.Game;
import core.Hero;

/**
 * Melee simulator main (Applet) class
 * 
 * $Id: MeleeJApplet.java,v 1.3 2013/02/16 20:50:07 cfuhrman Exp $ 
 *
 * @author Cris Fuhrman
 * @author <a href="mailto:fuhrmanator@gmail.com">fuhrmanator@gmail.com</a>
 * @version 1.2,    2012-11-15
 */

public class MeleeJApplet extends JApplet
{

	public MeleeJApplet()
	{
		
	}

	private static final String SIMULATOR_NAME = "Melee Simulator";
	private static final String VERSION = "1.2b2 (2013-02-16)";
	private static final String COPYRIGHT_YEARS = "2004-2013";
	private static final String COPYRIGHT_OWNER = "Christopher Fuhrman -- All rights reserved.";

	private static final String VERSION_INFO_HTML = "<html><font color=red>"
			+ SIMULATOR_NAME
			+ " v"
			+ VERSION
			+ "</font> &copy; "
			+ COPYRIGHT_YEARS
			+ " " + COPYRIGHT_OWNER 
			+ "</html>";

	protected static final String VERSION_INFO_CONSOLE = SIMULATOR_NAME
			+ " v"
			+ VERSION
			+ " (c) "
			+ COPYRIGHT_YEARS
			+ " " + COPYRIGHT_OWNER 
			+ "\n";

	@Override
	public void init()
	{
		// bogus call to make sure we instantiate the Logger
		Logger.getInstance();
		TreeSet<Hero> players = new TreeSet<Hero>(Game.makeHeroSet().keySet());
		final JList playerList = new JList(players.toArray());
		JScrollPane listScroller = new JScrollPane(playerList);
		listScroller.setPreferredSize(new Dimension(250, 80));

		Container pane = getContentPane();
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
		topPanel.add(new JLabel(VERSION_INFO_HTML));
		topPanel.add(new JLabel("Select two or more heroes from the list below for 1-on-1 combat simulation:"));
		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(listScroller, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		// panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		pane.add(panel, BorderLayout.SOUTH);

		panel.add(new JLabel("Bouts per matchup:"));		
		final JSpinner boutCount = new JSpinner();
		boutCount.setModel(new SpinnerNumberModel(50, 1, 10000, 1));
		panel.add(boutCount);

		final JCheckBox verboseCheckBox = new JCheckBox("Verbose output (caution!)");
		panel.add(verboseCheckBox);

		JButton startButton = new JButton("Start Mêlée simulator");
		panel.add(startButton);
		
		final Component theApplet = this;
		
		startButton.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					final TreeSet<Hero> selectedPlayers =
						new TreeSet<Hero>();
					for (Object hero : playerList.getSelectedValuesList()) {
						selectedPlayers.add((Hero)hero);
					}

					if (selectedPlayers.size() >= 2)
					{
						JFrame frame = new JFrame("Simulation output");
						JTextArea textArea = new JTextArea(20, 80);
						textArea.setFont(new Font("Courier", Font.PLAIN, 11));
						textArea.setEditable(false);
						textArea.append(VERSION_INFO_CONSOLE);
						Logger.getInstance().setTextArea(textArea);						
						JScrollPane scrollPane = 
							new JScrollPane(textArea,
											JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
											JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
						frame.getContentPane().add(scrollPane);
						frame.pack();
						// center it on screen relative to JApplet
						frame.setLocationRelativeTo(null);
						frame.setVisible(true);
						/*
						 * Should be launched in a separate thread!
						 */
						final SwingWorker worker = new SwingWorker()
						{
							@Override
							public Object construct()
							{
									//...code that might take a while to execute is here...
								Logger.getInstance().setVerbose(verboseCheckBox.isSelected());
								Game.tryAllCombinations(
														selectedPlayers,
														((Integer) boutCount.getValue()).intValue());
								return new Integer(0);
							}
						};
						worker.start(); //required for SwingWorker 3
					}
					else
					{
						JOptionPane.showMessageDialog(theApplet, "You must select at least two heros for the simulation.", "Too few heroes selected.", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		);			
	}

}
