package cz.cacek.ebook;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 * Intro page with Ebook splash-screen
 *
 * @author Tomas Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.6 $
 * @created $Date: 2010/01/08 08:29:49 $
 */
public class EBookIntro
	extends Canvas
{
	//~ Static fields/initializers =============================================

	// ~ Static fields/initializers
	// =============================================
	public static final long MIN_MILIS_DISPLAY = 1000;

	//~ Instance fields ========================================================

	// ~ Instance fields
	// ========================================================
	protected EBookMIDlet midlet;

	//~ Constructors ===========================================================

	// ~ Constructors
	// ===========================================================

	/**
	 * Constructor
	 *
	 * @param aMidlet
	 * @throws Exception
	 */
	public EBookIntro(EBookMIDlet aMidlet)
		throws Exception
	{
		midlet = aMidlet;
	}

	//~ Methods ================================================================

	// ~ Methods
	// ================================================================

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.microedition.lcdui.Displayable#paint(javax.microedition.lcdui.Graphics
	 * )
	 */
	public void paint(Graphics g) {
		try {
			int w = getWidth();
			int h = getHeight();
			Image img = Image.createImage("/ebook.png");
			g.setColor(0xFFFFFF);
			g.fillRect(0, 0, w, h);
			g.drawImage(
				img,
				(w - img.getWidth()) / 2,
				(h - img.getHeight()) / 2,
				Graphics.LEFT | Graphics.TOP);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
