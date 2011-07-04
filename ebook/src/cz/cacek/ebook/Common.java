package cz.cacek.ebook;

/**
 * Class contains constants and methods common for both ebook and packager.
 * 
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.28 $
 * @created $Date: 2010/01/19 17:14:02 $
 */
public class Common {
	/**
	 * Set to true, if you want to debug your ebook
	 */
	public static final boolean DEBUG = false;

	/**
	 * EBookME & packager version
	 */
	public static final String VERSION = "@EBOOKME_VERSION@";

	/**
	 * Folder (in ebook JAR) in which all book data are stored
	 */
	public static final String DATA_FOLDER = "data";

	/**
	 * Name of file where informations about book are stored
	 */
	public static final String INFO_FILE = "info";

	/**
	 * Name of file where chapters positions are stored
	 */
	public static final String CHAPTERS_FILE = "chapters";

	/**
	 * Name of file where list of book names is stored
	 */
	public static final String LIBRARY_FILE = "library";

	/**
	 * Language file
	 */
	public static final String LANGUAGE_FILE = "lang";

	/**
	 * Language file
	 */
	public static final String PART_FILE_PREFIX = "part";

	/**
	 * Encoding used in ebooks
	 */
	public static final String ENCODING = "UTF-8";

	/**
	 * Maximal length of ID
	 */
	public static final int MAX_ID_LENGTH = 20;

	/**
	 * Record store name for saving font, colors, etc.
	 */
	public static final String STORE_CONFIG = "config";

	/**
	 * Record store name for saving bookmarks
	 */
	public static final String STORE_BOOKMARKS = "bookmarks";

	/**
	 * saved as index of a book when no book is loaded
	 */
	public static final int NO_BOOK_SELECTED = -1;

	/**
	 * delay between showing next line
	 */
	public static final int AUTOSCROLL_PAUSE = 1000;

	/**
	 * step for setting delay between showing next line
	 */
	public static final int AUTOSCROLL_STEP = 50;

	/**
	 * default intensity of backlight (0 = off)
	 */
	public static final int BACKLIGHT = 30;

	/**
	 * default intensity of backlight during autoscroll
	 */
	public static final int BACKLIGHT_AS = 30;
	public static final byte SCREEN_BOOK = 0;
	public static final byte SCREEN_BOOK_LIST = 1;
	public static final byte SCREEN_COLOR_LIST = 2;
	public static final byte SCREEN_FONT_LIST = 3;
	public static final byte SCREEN_POSITION = 4;
	public static final byte SCREEN_LIGHT_AS = 5;
	public static final byte SCREEN_LIGHT = 6;
	public static final byte SCREEN_BOOK_INFO = 7;
	public static final byte SCREEN_CHAPTERS_LIST = 8;
	public static final byte SCREEN_WRAP_WORD_LIST = 9;
	public static final byte SCREEN_ADD_BMK = 10;
	public static final byte SCREEN_OPEN_BMK = 11;

	/**
	 * Creates ID from given name
	 * 
	 * @param aName
	 *            name which should be transformed to ID
	 * @return ID for book
	 */
	public synchronized static String createIdFromName(String aName) {
		String tmpResult = aName.toLowerCase();
		final StringBuffer tmpSB = new StringBuffer();

		for (int j = 0; j < tmpResult.length(); j++) {
			char c = tmpResult.charAt(j);

			if ("\t\r\n ".indexOf(c) != -1) {
				tmpSB.append('_');
			} else if ("abcdefghijklmnopqrstuvwxyz0123456789-_".indexOf(c) != -1) {
				tmpSB.append(c);
			} else {
				tmpSB.append('X');
			}
		}

		if (tmpSB.length() > MAX_ID_LENGTH) {
			tmpSB.setLength(MAX_ID_LENGTH);
		}

		tmpResult = tmpSB.toString();

		return tmpResult;
	}

	/**
	 * Debug function
	 * 
	 * @param aWhat
	 */
	public synchronized static void debug(final String aWhat) {
		if (DEBUG) {
			System.out.println(">>>DEBUG " + aWhat);
		}
	}

	/**
	 * Debug function for errors
	 * 
	 * @param aWhat
	 */
	public synchronized static void debugErr(final String aWhat) {
		if (DEBUG) {
			System.err.println(">>>ERROR " + aWhat);
		}
	}

	/**
	 * Prints error.
	 * 
	 * @param anErr
	 */
	public static void error(final Object anErr) {
		System.err.print("ERROR: ");

		if (anErr instanceof Throwable) {
			((Throwable) anErr).printStackTrace();
		} else {
			System.err.println(anErr.toString());
		}
	}
}
