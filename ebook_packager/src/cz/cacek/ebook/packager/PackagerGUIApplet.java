package cz.cacek.ebook.packager;

import java.applet.Applet;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.UIManager;


/**
 * Applet class for using packager on web.
 * @author Tomas Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.6 $
 * @created $Date: 2010/01/08 13:58:50 $
 */
public class PackagerGUIApplet
	extends Applet
{
	private static final long serialVersionUID = -7495985796601249434L;

	/* (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Cannot set Look&Feel.");
		}

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(new NewEbGui()));
	}
}
