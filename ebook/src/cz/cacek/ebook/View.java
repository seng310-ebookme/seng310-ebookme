package cz.cacek.ebook;

import java.io.EOFException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 * Implementation of ebook content screen.
 *
 * @author Tomas Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.14 $
 * @created $Date: 2010/01/08 08:29:49 $
 */
public class View {
	//~ Instance fields ========================================================

	// ~ Instance fields
	// ========================================================
	protected int width;
	protected int height;
	protected int widthOrig;
	protected int heightOrig;
	protected int background;
	protected int foreground;
	protected Font font;
	protected StringBuffer buffer;
	protected int borderSpace;
	protected int lineSpace;
	protected boolean wrapSpaces;
	protected boolean rotated;
	protected Book book;
	protected int position;
	protected int scrollWidth;
	protected int scrollHeight;
	protected int[] charWidths;
	protected Image offscreen;
	protected Image origscreen;
	private int colorScheme = 0;
	private Screen screen;

	//~ Constructors ===========================================================

	// ~ Constructors
	// ===========================================================

	/**
	 * Constructor
	 *
	 * @param aWidth
	 * @param aHeight
	 * @throws Exception
	 */
	public View(int aWidth, int aHeight)
		throws Exception
	{
		setDimension(aWidth, aHeight);
		buffer = new StringBuffer(256);
		borderSpace = 2;
		lineSpace = 0;
		wrapSpaces = true;
		scrollWidth = 5;
		scrollHeight = 5;
		charWidths = new int[256];
		setColors(0xFFFFFF, 0x000000);
		setFont(
			Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
	}

	//~ Methods ================================================================

	// ~ Methods
	// ================================================================
	protected void setDimension(int aWidth, int aHeight) {
		Common.debug("Setting screen w/h: " + aWidth + "/" + aHeight);
		widthOrig = aWidth;
		heightOrig = aHeight;
		scaleScreen();
	}

	protected void scaleScreen() {
		Common.debug("Scale - isRotated: " + rotated);

		if (rotated) {
			width = heightOrig;
			height = widthOrig;
			origscreen = Image.createImage(widthOrig, heightOrig);
		} else {
			width = widthOrig;
			height = heightOrig;
		}

		offscreen = Image.createImage(width, height);
	}

	/**
	 * Sets active book for view.
	 *
	 * @param aBook
	 */
	public void setBook(Book aBook) {
		book = aBook;
		setPosition(book.getPosition());
	}

	/**
	 * Sets font for view
	 *
	 * @param aFont
	 * @throws Exception
	 */
	public void setFont(Font aFont)
		throws Exception
	{
		font = aFont;

		int tmpLines =
			(height - (2 * borderSpace)) / (font.getHeight() + lineSpace);
		screen = new Screen(tmpLines);

		for (int i = 0; i < charWidths.length; i++) {
			charWidths[i] = font.charWidth((char) i);
		}

		if (book != null) {
			fillPage();
		}
	}

	/**
	 * Returns font
	 *
	 * @return font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets color pair as in given scheme
	 *
	 * @param aSchemeNr
	 */
	public void setColorScheme(int aSchemeNr) {
		colorScheme = aSchemeNr;

		switch (colorScheme) {
			// case 0 is handled by default
			case 1:
				// addColors(0xFFFFFF, tmpWhite, 0x0000FF, tmpBlue);
				setColors(0xFFFFFF, 0x0000FF);

				break;

			case 2:
				// addColors(0x000000, tmpBlack, 0x00FF00,
				// ResourceProviderME.get("cl.green"));
				setColors(0x000000, 0x00FF00);

				break;

			case 3:
				// addColors(0x0000A0, tmpBlue, 0xFFFFFF, tmpWhite);
				setColors(0x0000A0, 0xFFFFFF);

				break;

			default:
				// addColors(0xFFFFFF, tmpWhite, 0x000000, tmpBlack);
				setColors(0xFFFFFF, 0x000000);

				break;
		}
	}

	/**
	 * Sets FG/BG colors for view
	 *
	 * @param aBG
	 * @param aFG
	 */
	public void setColors(int aBG, int aFG) {
		// TODO make it private
		background = aBG;
		foreground = aFG;
	}

	/**
	 * Returns foreground color.
	 *
	 * @return foreground color
	 */
	public int getForegroundColor() {
		return foreground;
	}

	/**
	 * Returns background color.
	 *
	 * @return background color
	 */
	public int getBackgroundColor() {
		return background;
	}

	/**
	 * Sets position in active book in characters
	 *
	 * @param aPos
	 */
	public void setPosition(int aPos) {
		Common.debug("View.setPosition(aPos) started");

		if (aPos > book.size) {
			aPos = book.size - 1;
		}

		if (aPos < 0) {
			aPos = 0;
		}

		position = aPos;
		book.setPosition(aPos);
		fillPage();
	}

	/**
	 * Sets position in active book in percents
	 *
	 * @param aPerc
	 */
	public void setPercPosition(int aPerc) {
		setPosition(((book.size - 1) * aPerc) / 100);
	}

	/**
	 * Returns current position as a percentige of book.
	 *
	 * @return current position as a percentige of book
	 */
	public int getPercPosition() {
		if (book.size < 2) {
			return 0;
		}

		return (position * 100) / (book.size - 1);
	}

	/**
	 * Moves view one page ahead
	 *
	 * @throws Exception
	 */
	public void fwdPage()
		throws Exception
	{
		position = screen.getPosition(screen.size());
		fillPage();
	}

	/**
	 * Moves view one page back
	 *
	 * @throws Exception
	 */
	public void bckPage()
		throws Exception
	{
		for (int i = 0, n = screen.size(); i < n; i++) {
			bckLine();
		}
	}

	/**
	 * Moves view one line ahead
	 *
	 * @throws Exception
	 * @return true if scrolling is succesfull
	 */
	public synchronized boolean fwdLine()
		throws Exception
	{
		Common.debug("fwdLine() started");
		book.setPosition(screen.getPosition(screen.size()));

		final String tmpLine = nextLine();
		final boolean tmpResult = tmpLine != null;

		if (tmpResult) {
			screen.rollFw(tmpLine, book.getPosition());
			position = screen.getPosition(0);
		}

		Common.debug("fwdLine() finished (" + tmpResult + ")");

		return tmpResult;
	}

	/**
	 * Moves view one line back
	 *
	 * @throws Exception
	 */
	public synchronized void bckLine()
		throws Exception
	{
		Common.debug("bckLine() started");
		book.setPosition(screen.getPosition(0) - 1);

		String line = prevLine();

		if (line != null) {
			screen.rollBw(line, book.getPosition() + 1);
			position = screen.getPosition(0);
		}
	}

	/**
	 * fills page from current position
	 */
	public void fillPage() {
		Common.debug("fillPage() started");
		book.setPosition(position);
		screen.setPosition(0, position);

		try {
			for (int i = 0, n = screen.size(); i < n; i++) {
				screen.setContent(i, nextLine());
				screen.setPosition(i + 1, book.getPosition());
			}
		} catch (Exception e) {
			Common.debugErr(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}

		Common.debug("fillPage() finished");
	}

	/**
	 * Reads and returns next line for view.
	 *
	 * @return next line
	 * @throws Exception
	 */
	protected String nextLine()
		throws Exception
	{
		if (book.getPosition() >= (book.size - 1)) {
			return null;
		}

		int len = 0;
		int ws = -1;
		int index = 0;
		boolean eof = false;
		buffer.setLength(0);

		for (;;) {
			char c;

			try {
				c = book.readNext();
			} catch (EOFException e) {
				eof = true;

				break;
			}

			if (c == '\t') {
				c = ' ';
			}

			if ((c == '\r') || ((c == ' ') && (index == 0))) {
				continue;
			}

			if (c == '\n') {
				break;
			}

			if (c == ' ') {
				ws = index;
			}

			len += charWidth(c);

			if (len > (width - (2 * borderSpace) - scrollWidth)) {
				book.readPrev();

				if ((ws != -1) && wrapSpaces) {
					int discard = index - ws - 1;

					for (int i = 0; i < discard; i++) {
						book.readPrev();
					}

					index = ws;
					buffer.setLength(index);
				}

				break;
			}

			buffer.append(c);
			index++;
		}

		if (eof && (index == 0)) {
			return null;
		}

		if (book.isRightToLeft()) {
			buffer.reverse();
		}

		return (buffer.toString()).trim();
	}

	/**
	 * Reads and returns previous line. (backward reading)
	 *
	 * @return previous line
	 * @throws Exception
	 */
	protected String prevLine()
		throws Exception
	{
		if (book.getPosition() <= 0) {
			return null;
		}

		int len = 0;
		int ws = -1;
		int index = 0;
		boolean eof = false;
		buffer.setLength(0);

		for (;;) {
			char c;

			try {
				c = book.readPrev();
			} catch (EOFException e) {
				eof = true;

				break;
			}

			if (c == '\t') {
				c = ' ';
			}

			if ((c == '\r') || ((c == ' ') && (index == 0))) {
				continue;
			}

			if (c == '\n') {
				break;
			}

			if (c == ' ') {
				ws = index;
			}

			len += charWidth(c);

			if (len > (width - (2 * borderSpace) - scrollWidth)) {
				book.readNext();

				if ((ws != -1) && wrapSpaces) {
					int discard = index - ws - 1;

					for (int i = 0; i < discard; i++) {
						book.readNext();
					}

					index = ws;
					buffer.setLength(index);
				}

				break;
			}

			buffer.append(c);
			index++;
		}

		if (eof && (index == 0)) {
			return null;
		}

		if (!book.isRightToLeft()) {
			buffer.reverse();
		}

		return (buffer.toString()).trim();
	}

	/**
	 * returns width of given character for current font
	 *
	 * @param aChr
	 *            character
	 * @return width of given character
	 */
	protected int charWidth(char aChr) {
		return (aChr < 256) ? charWidths[aChr] : font.charWidth(aChr);
	}

	/**
	 * Draw current view to display.
	 *
	 * @param aGraphic
	 * @param aX
	 * @param aY
	 */
	public void draw(Graphics aGraphic, int aX, int aY) {
		Graphics g = offscreen.getGraphics();
		// Draw background
		g.setColor(background);
		g.fillRect(0, 0, width, height);
		// Draw text
		g.setColor(foreground);
		g.setFont(font);

		int pos = 0;
		int anchor;
		int xPos;

		if (book.isRightToLeft()) {
			anchor = Graphics.RIGHT | Graphics.TOP;
			xPos = width - scrollWidth - borderSpace;
		} else {
			anchor = Graphics.LEFT | Graphics.TOP;
			xPos = borderSpace;
		}

		for (int i = 0, n = screen.size(); i < n; i++) {
			String line = screen.getContent(i);

			if (line != null) {
				g.drawString(line, xPos, borderSpace + pos, anchor);
			}

			pos += (font.getHeight() + lineSpace);
		}

		// Draw border
		g.setColor(foreground);
		g.drawRect(0, 0, width - 1, height - 1);
		// Draw scroll
		g.setColor(background);
		g.fillRect(width - scrollWidth, 0, scrollWidth - 1, height - 1);
		g.setColor(foreground);
		g.drawRect(width - scrollWidth, 0, scrollWidth - 1, height - 1);

		int scroll = ((height - scrollHeight) * book.getPosition()) / book.size;
		g.fillRect(width - scrollWidth, scroll, scrollWidth - 1,
			scrollHeight - 1);

		if (rotated) {
			// rotation of screen (270 degrees)
			// it's simple with MIDP-2.0:
			// draw the text to an image and use g.drawRegion() with a
			// Sprite.TRANS_XXX to get the 90,180, and 270 degree angles.
			final Graphics g2 = origscreen.getGraphics();

			for (int i = 0; i < widthOrig; i++) {
				for (int j = 0; j < heightOrig; j++) {
					g2.setClip(i, j, 1, 1);
					g2.drawImage(
						offscreen,
						i - j,
						(j + i) - widthOrig,
						Graphics.TOP | Graphics.LEFT);
				}
			}
		} else {
			origscreen = offscreen;
		}

		// Draw screen
		aGraphic.drawImage(origscreen, aX, aY, Graphics.LEFT | Graphics.TOP);
	}

	/**
	 * Returns position of first character on screen of current view.
	 *
	 * @return current position of view
	 */
	public int getPosition() {
		return (screen == null) ? 0 : screen.getPosition(0);
	}

	public boolean isWrapSpaces() {
		return wrapSpaces;
	}

	public void setWrapSpaces(boolean wrapSpaces) {
		this.wrapSpaces = wrapSpaces;

		if (book != null) {
			fillPage();
		}
	}

	public boolean isRotated() {
		return rotated;
	}

	public void setRotated(boolean rotated) {
		this.rotated = rotated;
		scaleScreen();
	}

	public int getColorScheme() {
		return colorScheme;
	}

	//~ Inner Classes ==========================================================

	// ~ Inner Classes
	// ==========================================================

	/**
	 * Class Screen holds lines which are currently displayed on the screen.
	 * Class is synchronized.
	 * <p/>
	 * Instances of this class are created during LCD-screen font change.
	 *
	 * @author Josef Cacek [josef.cacek (at) atlas.cz]
	 * @author $Author: kwart $
	 * @version $Revision: 1.14 $
	 * @created $Date: 2010/01/08 08:29:49 $
	 */
	static class Screen {
		//~ Instance fields ====================================================

		// ~ Instance fields
		// ====================================================
		private int[] positions;
		private String[] content;

		//~ Constructors =======================================================

		// ~ Constructors
		// =======================================================

		/**
		 * Creates new screen object
		 *
		 * @param aLines
		 */
		public Screen(int aLines) {
			positions = new int[aLines + 1];
			content = new String[aLines];
		}

		//~ Methods ============================================================

		// ~ Methods
		// ============================================================

		/**
		 * Returns count of rows displayed on screen.
		 *
		 * @return count of rows displayed on screen
		 */
		synchronized public int size() {
			return content.length;
		}

		/**
		 * Returns position (in book) of first character displayed on given
		 * line.
		 *
		 * @param aLine
		 *            line for which is position returned
		 * @return position of line in book
		 */
		synchronized public int getPosition(int aLine) {
			return positions[aLine];
		}

		/**
		 * Sets position of line
		 *
		 * @param aLine
		 * @param aPos
		 */
		synchronized public void setPosition(int aLine, int aPos) {
			positions[aLine] = aPos;
		}

		/**
		 * Returns string displayed in given row.
		 *
		 * @param aLine
		 *            index of row
		 * @return string displayed in given row
		 */
		synchronized public String getContent(int aLine) {
			return content[aLine];
		}

		/**
		 * Sets string to display in given line
		 *
		 * @param aLine
		 *            index of row
		 * @param aStr
		 */
		synchronized public void setContent(int aLine, String aStr) {
			content[aLine] = aStr;
		}

		/**
		 * Rolls positions and content forward and adds new line. First
		 * displayed line is deleted (rolled out). to the end.
		 *
		 * @param aLine
		 *            new line
		 * @param aNewPosition
		 *            position of new line (position in book)
		 */
		synchronized void rollFw(String aLine, int aNewPosition) {
			for (int i = 0; i < (content.length - 1); i++) {
				content[i] = content[i + 1];
				positions[i] = positions[i + 1];
			}

			positions[content.length - 1] = positions[content.length];
			content[content.length - 1] = aLine;
			positions[positions.length - 1] = aNewPosition;
		}

		/**
		 * Rolls backward, new line (given as parameter) is added to the
		 * beginning.
		 *
		 * @param aLine
		 *            new line
		 * @param aNewPosition
		 * @see #rollFw(String, int)
		 */
		synchronized void rollBw(String aLine, int aNewPosition) {
			positions[content.length] = positions[content.length - 1];

			for (int i = content.length - 1; i > 0; i--) {
				content[i] = content[i - 1];
				positions[i] = positions[i - 1];
			}

			content[0] = aLine;
			positions[0] = aNewPosition;
		}
	}
}
