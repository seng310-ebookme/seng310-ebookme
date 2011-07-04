package cz.cacek.ebook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Vector;

import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;


/**
 * Library. Holds book names in ebook application. Class is final because of
 * calling getClass().getResourceAsStream(...) in constructor.
 *
 * @author Tomas Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.14 $
 * @created $Date: 2010/01/08 08:29:49 $
 */
public final class Library {
	//~ Static fields/initializers =============================================

	// ~ Static fields/initializers
	// =============================================
	private static Library instance;

	//~ Instance fields ========================================================

	// ~ Instance fields
	// ========================================================
	private String[] books;
	private Book activeBook;
	private int bookIdx = Common.NO_BOOK_SELECTED;

	//~ Constructors ===========================================================

	// ~ Constructors
	// ===========================================================

	/**
	 * Reads information about library
	 *
	 * @throws IOException
	 *             library information file doesn't exist
	 */
	private Library()
		throws IOException
	{
		InputStream tmpIS =
			(getClass()).getResourceAsStream(
					"/" + Common.DATA_FOLDER + "/" + Common.LIBRARY_FILE);

		if (tmpIS == null) {
			throw new RuntimeException("Cannot get library info");
		}

		DataInputStream tmpDIS = new DataInputStream(tmpIS);
		int count = tmpDIS.readInt();
		books = new String[count];

		for (int i = 0; i < count; i++) {
			books[i] = tmpDIS.readUTF();
		}

		tmpDIS.close();
	}

	//~ Methods ================================================================

	// ~ Methods
	// ================================================================

	/**
	 * Returns singleton of this class
	 *
	 * @return singleton
	 */
	public synchronized static Library getInstance() {
		if (instance == null) {
			try {
				instance = new Library();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe.getMessage());
			}
		}

		return instance;
	}

	/**
	 * Count of books in library
	 *
	 * @return Pocet knih v knihovne
	 */
	public int getBookCount() {
		return books.length;
	}

	/**
	 * Returns name of book on a given position
	 *
	 * @param anIdx
	 * @return name of book on a given position
	 */
	public String getBookName(int anIdx) {
		if (anIdx < 0) {
			throw new RuntimeException("Negative book index");
		}

		if (anIdx >= books.length) {
			throw new RuntimeException("Book index too large");
		}

		return books[anIdx];
	}

	/**
	 * Returns book on a given position
	 *
	 * @param anIdx
	 * @return book on a given position
	 */
	public Book getBook(int anIdx) {
		Common.debug("Book.getBook(anIdx) started");

		if ((anIdx >= 0) && ((bookIdx != anIdx) || (activeBook == null))) {
			bookIdx = anIdx;
			activeBook = new Book(getBookName(anIdx));
			activeBook.setPosition(getActivePosition());
		}

		return activeBook;
	}

	public Book getActive() {
		return activeBook;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Library (books=" + books.length + ")";
	}

	/**
	 * Creates bookmark for active book from library
	 */
	private int getActivePosition() {
		int tmpResult = 0;

		if (bookIdx >= 0) {
			try {
				RecordStore tmpBookmarksRs = getBookmarksRS();
				final byte[] tmpBytes = tmpBookmarksRs.getRecord(1);
				tmpBookmarksRs.closeRecordStore();

				final int n = bookIdx * 4;
				tmpResult = 0;

				for (int i = 0; i < 4; i++) {
					tmpResult = tmpResult
						| ((tmpBytes[i + n] & 0xff) << (8 * i));
				}
			} catch (Exception e) {
				Common.error(e);
			}
		}

		return tmpResult;
	}

	/**
	 * Creates bookmark for active book from library
	 */
	public void savePosition(final int aPos) {
		if (bookIdx < 0) {
			return;
		}

		try {
			RecordStore tmpBookmarksRs = getBookmarksRS();
			final byte[] tmpBytes = tmpBookmarksRs.getRecord(1);
			final int n = bookIdx * 4;
			tmpBytes[n] = (byte) (aPos & 0xff);
			tmpBytes[n + 1] = (byte) ((aPos & 0xff00) >> 8);
			tmpBytes[n + 2] = (byte) ((aPos & 0xff0000) >> 16);
			tmpBytes[n + 3] = (byte) ((aPos & 0xff000000) >> 24);
			tmpBookmarksRs.setRecord(1, tmpBytes, 0, tmpBytes.length);
			tmpBookmarksRs.closeRecordStore();
		} catch (Exception e) {
			Common.error(e);
		}
	}

	/**
	 * @return bookmarks record store
	 * @throws RecordStoreFullException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreException
	 */
	private RecordStore getBookmarksRS()
		throws RecordStoreFullException, RecordStoreNotFoundException,
			RecordStoreException
	{
		RecordStore tmpRS = null;
		tmpRS = RecordStore.openRecordStore(Common.STORE_BOOKMARKS, true);

		if (tmpRS.getNumRecords() == 0) {
			byte[] tmpBytes = new byte[getBookCount() * 4];
			tmpRS.addRecord(tmpBytes, 0, tmpBytes.length);
			tmpBytes = new byte[0];

			// create additional records for user bookmarks
			for (int i = 0, n = getBookCount(); i < n; i++) {
				tmpRS.addRecord(tmpBytes, 0, 0);
			}
		}

		return tmpRS;
	}

	/**
	 * Returns (zero-based) index of active book
	 *
	 * @return the bookIdx
	 */
	public int getBookIdx() {
		return bookIdx;
	}

	/**
	 * Sets (zero-based) index of active book
	 *
	 * @param aBookIdx
	 *            the bookIdx to set
	 */
	public void setBookIdx(int aBookIdx) {
		if (aBookIdx >= getBookCount()) {
			bookIdx = -1;

			try {
				RecordStore.deleteRecordStore(Common.STORE_BOOKMARKS);
			} catch (Exception e) {
				// Nothing to do
			}
		} else {
			this.bookIdx = aBookIdx;
		}
	}

	/**
	 * Add bookmark to the current book.
	 *
	 * @param aName
	 *            bookmark name (will be trimmed & checked if not empty)
	 * @param aPosition
	 *            bookmark position
	 * @return true if bookmark is created, false otherwise
	 */
	public boolean addBookmark(String aName, int aPosition) {
		Common.debug("Library.addBookmark started");

		if ((aName == null) || (getBookIdx() < 0)) {
			return false;
		}

		aName = aName.trim();

		if (aName.length() == 0) {
			return false;
		}

		boolean tmpResult = true;

		try {
			final RecordStore tmpBookmarkRs = getBookmarksRS();
			final ByteArrayOutputStream tmpBaos = new ByteArrayOutputStream();
			final DataOutputStream tmpDos = new DataOutputStream(tmpBaos);
			byte[] tmpBytes = tmpBookmarkRs.getRecord(2 + getBookIdx());

			if (Common.DEBUG) {
				Common.debug(
						"Old bookmarks byte array length: "
						+ ((tmpBytes == null) ? (-1) : tmpBytes.length));
			}

			if ((tmpBytes != null) && (tmpBytes.length > 0)) {
				tmpDos.write(tmpBytes);
			}

			tmpDos.writeUTF(aName);
			tmpDos.writeInt(aPosition);
			tmpDos.flush();
			tmpBytes = tmpBaos.toByteArray();

			if (Common.DEBUG) {
				Common.debug(
					"New bookmarks byte array length: " + tmpBytes.length);
			}

			tmpBookmarkRs.setRecord(2 + getBookIdx(), tmpBytes, 0,
				tmpBytes.length);
			tmpDos.close();
		} catch (Exception e) {
			tmpResult = false;
			Common.error(e);
		}

		return tmpResult;
	}

	/**
	 * Fills Bookmark list (and positions vector)
	 *
	 * @param aList
	 * @param aPosVector
	 */
	public int fillBookmarkList(List aList, Vector aPosVector) {
		Common.debug("Library.fillBookmarkList started");

		int tmpResult = 0;

		try {
			final RecordStore tmpBookmarkRs = getBookmarksRS();
			final byte[] tmpBytes = tmpBookmarkRs.getRecord(2 + getBookIdx());

			if (Common.DEBUG) {
				Common.debug(
						"Bookmarks byte array length: "
						+ ((tmpBytes == null) ? (-1) : tmpBytes.length));
			}

			if ((tmpBytes != null) && (tmpBytes.length > 0)) {
				final ByteArrayInputStream tmpBais =
					new ByteArrayInputStream(tmpBytes);
				final DataInputStream tmpDis = new DataInputStream(tmpBais);

				try {
					String tmpName;
					int tmpPos;

					while (true) {
						tmpName = tmpDis.readUTF();
						tmpPos = tmpDis.readInt();
						aList.append(tmpName, null);
						aPosVector.addElement(new Integer(tmpPos));
						tmpResult++;
					}
				} catch (EOFException eofe) {
					// if we are here, it's correct - no more bookmarks in RS
				}

				tmpDis.close();
			}
		} catch (Exception e) {
			Common.error(e);
		}

		if (Common.DEBUG) {
			Common.debug("Found " + tmpResult + " bookmarks");
		}

		return tmpResult;
	}

	/**
	 * Removes bookmark on given index
	 */
	public void removeBookmark(int anIndex, List aList, Vector aPosVector) {
		Common.debug("Library.removeBookmark started");

		if ((anIndex < 0) || (anIndex >= aList.size())) {
			Common.debug("Bookmark index out of bounds");

			return;
		}

		aList.delete(anIndex);
		aPosVector.removeElementAt(anIndex);

		try {
			// Recreate RS (delete bookmark entry)
			final RecordStore tmpBookmarkRs = getBookmarksRS();
			byte[] tmpBytes = tmpBookmarkRs.getRecord(2 + getBookIdx());

			if ((tmpBytes != null) && (tmpBytes.length > 0)) {
				final ByteArrayInputStream tmpBais =
					new ByteArrayInputStream(tmpBytes);
				final DataInputStream tmpDis = new DataInputStream(tmpBais);
				final ByteArrayOutputStream tmpBaos =
					new ByteArrayOutputStream();
				final DataOutputStream tmpDos = new DataOutputStream(tmpBaos);

				try {
					int tmpIdx = 0;

					while (true) {
						if (tmpIdx != anIndex) {
							tmpDos.writeUTF(tmpDis.readUTF());
							tmpDos.writeInt(tmpDis.readInt());
						} else {
							Common.debug(
								"Deleting bookmark at index " + tmpIdx);
						}

						tmpIdx++;
					}
				} catch (EOFException eofe) {
					// if we are here, it's correct - no more bookmarks in RS
				}

				tmpDis.close();
				tmpDos.flush();
				tmpBytes = tmpBaos.toByteArray();
				tmpBookmarkRs.setRecord(
					2 + getBookIdx(),
					tmpBytes,
					0,
					tmpBytes.length);
				tmpDos.close();
			}
		} catch (Exception e) {
			Common.error(e);
		}
	}
}
