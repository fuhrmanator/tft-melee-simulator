package gui;
import javax.swing.JTextArea;

/*
 * Created on May 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/*
 * Singleton class...
 * @author Cris
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Logger
{

	private static Logger logger = new Logger();
	private JTextArea textArea;
	private boolean verbose;
	
	private Logger()
	{
		verbose = false;
	}
	
	public static final Logger getInstance()
	{
		return logger;
	}
	
	/**
	 * @return
	 */
	public JTextArea getTextArea()
	{
		return textArea;
	}

	/**
	 * @return
	 */
	public boolean isVerbose()
	{
		return verbose;
	}

	/**
	 * @param b
	 */
	public void setVerbose(boolean b)
	{
		verbose = b;
	}

	/**
	 * @param area
	 */
	public void setTextArea(JTextArea area)
	{
		textArea = area;
	}

}
