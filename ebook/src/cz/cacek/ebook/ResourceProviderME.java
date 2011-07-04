package cz.cacek.ebook;

import java.io.IOException;

import java.util.Hashtable;


/**
 * Holds translations for EBookME - list of resources is generated in
 * ResourceProvider class in packager application.
 *
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.6 $
 * @created $Date: 2010/01/08 08:29:48 $
 */
public final class ResourceProviderME {
	//~ Static fields/initializers =============================================

	// ~ Static fields/initializers
	// =============================================

	/**
	 * singleton instance of ResourceProvider
	 */
	public static final ResourceProviderME provider = new ResourceProviderME();

	//~ Instance fields ========================================================

	// ~ Instance fields
	// ========================================================
	private Hashtable messages;

	//~ Constructors ===========================================================

	// ~ Constructors
	// ===========================================================
	private ResourceProviderME() {
		super();

		final Hashtable tmpMsgs = new Hashtable();

		try {
			UTF8ISReader tmpReader =
				new UTF8ISReader(
						getClass().getResourceAsStream(
								"/" + Common.DATA_FOLDER + "/"
								+ Common.LANGUAGE_FILE));
			char[] tmpChr = new char[1];
			StringBuffer[] tmpSB = new StringBuffer[2];
			tmpSB[0] = new StringBuffer();
			tmpSB[1] = new StringBuffer();

			int tmpPos = 0;
			boolean tmpBSlash = false;

			while (1 == tmpReader.read(tmpChr)) {
				switch (tmpChr[0]) {
					case '\n':
						tmpPos = 0;
						tmpMsgs.put(tmpSB[0].toString(), tmpSB[1].toString());
						tmpSB[0].setLength(0);
						tmpSB[1].setLength(0);
						tmpBSlash = false;

						break;

					case '=':
						tmpPos = 1;

						break;

					case '\\':

						if (tmpBSlash) {
							tmpSB[tmpPos].append('\\');
						} else {
							tmpBSlash = true;
						}

						break;

					case 'n':

						if (tmpBSlash) {
							tmpBSlash = false;
							tmpSB[tmpPos].append('\n');

							break;
						}

					default:

						if (tmpBSlash) {
							tmpSB[tmpPos].append('\\');
							tmpBSlash = false;
						}

						tmpSB[tmpPos].append(tmpChr[0]);

						break;
				}
			}

			tmpReader.close();
			setMessages(tmpMsgs);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}
	}

	//~ Methods ================================================================

	// ~ Methods
	// ================================================================

	/**
	 * Returns message identified by given key.
	 *
	 * @param aKey
	 * @return message identified by given key
	 */
	public synchronized static String get(final String aKey) {
		final String tmpResult = (String) provider.getMessages().get(aKey);

		if (tmpResult == null) {
			return aKey;
		}

		return tmpResult;
	}

	public synchronized Hashtable getMessages() {
		return messages;
	}

	private synchronized void setMessages(final Hashtable aMsgs) {
		messages = aMsgs;
	}
}
