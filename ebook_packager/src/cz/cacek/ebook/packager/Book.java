package cz.cacek.ebook.packager;

import com.ibm.icu.text.Normalizer;

import cz.cacek.ebook.Common;

import java.io.File;


/**
 * Book representation
 *
 * @author Tomas Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.17 $
 * @created $Date: 2010/01/08 13:58:51 $
 */
public class Book {
	/**
	 * Minimum of characters in one part of book
	 */
	public transient static final int MIN_PART_SIZE = 200;
	private String name;
	private String description;
	private String text;
	private int partSize;
	private boolean rightToLeft = false;
	private String id;

	// GUI can use this
	public Book() {
		setName(Constants.BOOK_NAME_DEF);
		setDescription("");
		setText("");
		setPartSize(Integer.parseInt(Constants.PROP_PARTSIZE_DEF));
	}

	/**
	 * Creates Book object from given parameters
	 *
	 * @param aName
	 *            name of book
	 * @param aDesc
	 *            description
	 * @param aPartSize
	 *            size of parts to which will be book divided (and size of read
	 *            buffer in ebook application)
	 */
	public Book(String aName, String aDesc, int aPartSize) {
		setName(aName);
		setDescription(aDesc);
		setPartSize(aPartSize);
	}

	/**
	 * @param aFile
	 *            filename of plain-text file from which will be book content
	 *            generated
	 * @param aCharset
	 */
	public void readText(String aFile, String aCharset) {
		String tmpText = null;

		if (aFile != null) {
			File tmpFile = new File(aFile);

			try {
				tmpText =
					ApertureExtractor.parseText(
						tmpFile,
						StringUtils.getKnownCharset(aCharset),
						this);
			} catch (Throwable ex) {
				// something's wrong, try to read simple text
			}

			if (tmpText == null) {
				try {
					tmpText =
						StringUtils.read(
							tmpFile,
							StringUtils.getKnownCharset(aCharset));
				} catch (Throwable ex2) {
					// no way
				}
			}
		}

		setTextWithFmt(tmpText);
	}

	/**
	 * Returns ID generated from name
	 *
	 * @return bookID
	 */
	public String getId() {
		if (id == null) {
			id = Common.createIdFromName(name);
		}

		return id;
	}

	/**
	 * Returns description of book
	 *
	 * @return book description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets description
	 *
	 * @param aDesc
	 */
	public void setDescription(String aDesc) {
		description = (aDesc == null) ? "" : aDesc;
	}

	/**
	 * Returns book name
	 *
	 * @return book name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns book name without special chars. Because of Manifest and Jad
	 * file.
	 *
	 * @return book name without special chars
	 */
	public String getAsciiName() {
		return normalizeString(name);
	}

	/**
	 * normalizes string
	 *
	 * @param s
	 * @return
	 */
	private String normalizeString(String s) {
		String temp = Normalizer.normalize(s, Normalizer.NFD);

		return temp.replaceAll("[\t\n ]+", " ").replaceAll("[^\\p{ASCII} ]", "");
	}

	/**
	 * Sets book name
	 *
	 * @param aName
	 * @throws PackagerRuntimeException
	 *             name is empty
	 */
	public void setName(String aName) {
		if ((aName == null) || (aName.trim().length() == 0)) {
			aName = Constants.BOOK_NAME_DEF;
		} else {
			aName = aName.trim();
		}

		id = null;
		name = aName;
	}

	/**
	 * Returns size of parts
	 *
	 * @return size of one book part
	 */
	public int getPartSize() {
		return partSize;
	}

	/**
	 * Sets size of parts
	 *
	 * @param aPart
	 * @throws PackagerRuntimeException
	 *             size is &lt; 1
	 */
	public void setPartSize(int aPart) {
		if (aPart < MIN_PART_SIZE) {
			aPart = Integer.parseInt(Constants.PROP_PARTSIZE_DEF);
		}

		partSize = aPart;
	}

	/**
	 * @return Returns the text.
	 */
	public String getText() {
		if ((text == null) || (text.length() == 0)) {
			text = "...";
		}

		return text;
	}

	/**
	 * Sets text of book without any formating.
	 *
	 * @param aText
	 */
	public void setText(String aText) {
		text = aText;
	}

	/**
	 * Sets text of book and formats it better readability on mobile devices
	 * (only if enabled in properties).
	 *
	 * @param aText
	 *            The text to set.
	 */
	public void setTextWithFmt(String aText) {
		if (aText == null) {
			aText = "";
		} else {
			// Edit input text to better readability on mobile devices
			if (PropertyProvider.getInstance()
									.getAsBool(Constants.PROP_AUTOFORMAT)) {
				// replace line ends and tabs
				aText = aText.replaceAll("\r", "").replaceAll("\t", " ");
				// trim spaces
				aText = aText.replaceAll("[ ]*\n[ ]*", "\n")
							 .replaceAll("[ ]+", " ");
				// replace multiple lineends
				aText = aText.replaceAll("\n[\n]+", "\n\n");
			}
		}

		this.text = aText;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + getName();
	}

	public boolean isRightToLeft() {
		return rightToLeft;
	}

	public void setRightToLeft(boolean rightToLeft) {
		this.rightToLeft = rightToLeft;
	}
}
