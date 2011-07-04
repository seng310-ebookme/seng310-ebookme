package cz.cacek.ebook.packager;

import cz.cacek.ebook.Common;

import java.awt.Font;
import java.awt.Toolkit;

import java.net.URL;

import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;


/**
 * Base frame for Swing UI
 * @author Tomas Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: jiritusla $
 * @version $Revision: 1.10 $
 * @created $Date: 2008/07/03 16:07:22 $
 */
public class PackagerGUIApplic
	extends JFrame
{
	private static final long serialVersionUID = 1051393144301555996L;

	/**
	 * Default constructor.
	 */
	public PackagerGUIApplic() {
		super("EBook Packager (v. " + Common.VERSION + ")");

		final boolean tmpTamil =
			"ta".equalsIgnoreCase(Locale.getDefault().getLanguage());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			if (tmpTamil) {
				Font tmpFont = null; //Font.decode("Latha");

				if (tmpFont == null) {
					tmpFont =
						Font.createFont(
								Font.TRUETYPE_FONT,
								getClass().getResourceAsStream(
										"/cz/cacek/ebook/packager/resources/fonts/FreeSerif.ttf"));
					tmpFont = tmpFont.deriveFont(Font.PLAIN, 12f);
				}

				java.util.Enumeration keys = UIManager.getDefaults().keys();

				while (keys.hasMoreElements()) {
					Object key = keys.nextElement();
					Object value = UIManager.get(key);

					if (value instanceof javax.swing.plaf.FontUIResource) {
						UIManager.put(key, tmpFont);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Cannot set Look&Feel.");
		}

		setSize(650, 780);
		getContentPane().add(new JScrollPane(new NewEbGui()));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//		setLocation(100, 100);
		URL tmpImgUrl =
			PackagerGUIApplic.class.getClassLoader().getResource(
					"cz/cacek/ebook/packager/resources/images/icon.jpg");
		setIconImage(Toolkit.getDefaultToolkit().getImage(tmpImgUrl));

		if (tmpTamil) {
			pack();
		}

		setVisible(true);
	}
}
