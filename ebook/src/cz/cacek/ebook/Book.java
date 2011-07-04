package cz.cacek.ebook;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Vector;

import javax.microedition.lcdui.List;


/**
 * Class for representation of one book in the library.
 *
 * @author Tomas Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.18 $
 * @created $Date: 2010/01/08 14:04:36 $
 */
public class Book {
	//~ Static fields/initializers =============================================

	private static BufferHolder nextBuff;
	private static BufferThread bfThread = null;

	//~ Instance fields ========================================================

	/**
	 * book identifier - generated from book name and used for directory name
	 */
	private String id;

	/**
	 * book name
	 */
	public String name;

	/**
	 * book description
	 */
	public String description;
	protected int size;
	private int partSize;
	private boolean rightToLeft;
	private BufferHolder buffer;
	private int index;

	//~ Constructors ===========================================================

	/**
	 * Reads book with given name
	 *
	 * @param aName
	 *            name of the book
	 */
	public Book(String aName) {
		name = aName;

		InputStream tmpIS = open(Common.INFO_FILE);

		if (tmpIS == null) {
			Common.debugErr("Book constructor -  Cannot get book info");
			throw new RuntimeException("Cannot get book info");
		}

		DataInputStream tmpDIS = new DataInputStream(tmpIS);

		try {
			size = tmpDIS.readInt();
			partSize = tmpDIS.readInt();
			description = tmpDIS.readUTF();
			rightToLeft = tmpDIS.readBoolean();
			tmpDIS.close();
		} catch (IOException ioe) {
			Common.debugErr("Book constructor: " + ioe.getMessage());
			throw new RuntimeException(ioe.getMessage());
		}
	}

	//~ Methods ================================================================

	/**
	 * Opens inputstream from file in current book directory.
	 *
	 * @param aPath
	 *            filename to open
	 * @return InputStream for given path
	 */
	protected InputStream open(String aPath) {
		return (getClass()).getResourceAsStream(
				"/" + Common.DATA_FOLDER + "/" + getId() + "/" + aPath);
	}

	/**
	 * Fills given part into buffer and runs reading of next part on background
	 *
	 * @param aPart
	 *            number of part which should be saved to buffer
	 * @throws Exception
	 */
	protected synchronized void fillBuffer(final int aPart)
		throws Exception
	{
		Common.debug("Book.fillBuffer() started");

		if ((aPart * partSize) > (size - 1)) {
			Common.debug("Book.fillBuffer() - End of book.");
			throw new EOFException();
		}

		while (getBfThread() != null) {
			Common.debug("Book.fillBuffer() - Waiting for buffer thread.");
			Thread.sleep(200);
		}

		BufferHolder tmpNext = getNextBuff();

		if ((tmpNext != null) && (aPart == tmpNext.partNo)) {
			buffer = tmpNext;
			setNextBuff(null);
		} else {
			buffer = new BufferHolder(aPart, partSize, getId());
			buffer.read(rightToLeft);
		}

		Common.debug("Book.fillBuffer() starting BufferThread");

		BufferThread tmpThread =
			new BufferThread(aPart + 1, partSize, getId(), rightToLeft);
		tmpThread.setPriority(2);
		tmpThread.start();
		Common.debug("Book.fillBuffer() finished");
	}

	/**
	 * Returns character and moves bufferOffset backward
	 *
	 * @return current character
	 * @throws Exception
	 */
	public char readPrev()
		throws Exception
	{
		final char tmpChr = readCurr();
		index--;

		return tmpChr;
	}

	/**
	 * Returns character and moves bufferOffset ahead
	 *
	 * @return current character
	 * @throws Exception
	 */
	public char readNext()
		throws Exception
	{
		char tmpChr = readCurr();
		index++;

		return tmpChr;
	}

	/**
	 * Returns character from buffer (on bufferOffset position). If it's
	 * necessary reads new data to buffer (prev/next).
	 *
	 * @throws Exception
	 */
	protected char readCurr()
		throws Exception
	{
		if ((getPosition() == size) || (getPosition() == -1)) {
			Common.debug("readCurr EOFException");
			throw new EOFException();
		}

		if (index == -1) {
			rollPrev();
			index = buffer.bufferLength - 1;
		} else if (index == buffer.bufferLength) {
			rollNext();
			index = 0;
		}

		Common.debug("Book.readCurr() - " + buffer.getBuffer()[index]);

		return buffer.getBuffer()[index];
	}

	/**
	 * Reads previous part of book to buffer
	 *
	 * @throws Exception
	 */
	protected void rollPrev()
		throws Exception
	{
		if ((buffer == null) || (buffer.partNo <= 0)) {
			Common.debugErr("rollPrev problem\n" + buffer);
			throw new Exception("Cannot roll previous buffer");
		}

		rollBuffer(buffer.partNo - 1);
	}

	/**
	 * Reads next part of book to buffer
	 *
	 * @throws Exception
	 */
	protected void rollNext()
		throws Exception
	{
		if ((buffer == null) || (((buffer.partNo + 1) * partSize) >= size)) {
			Common.debugErr("rollNext problem\n" + buffer);
			throw new Exception("Cannot roll next buffer");
		}

		rollBuffer(buffer.partNo + 1);
	}

	/**
	 * Reads part of text (starting on position aPart * partSize) to buffer
	 *
	 * @param aPart
	 *            which part of book should be read
	 * @throws Exception
	 */
	protected void rollBuffer(final int aPart)
		throws Exception
	{
		if ((buffer != null) && (aPart == buffer.partNo)) {
			return;
		}

		if (aPart < 0) {
			throw new RuntimeException("Negative buffer num");
		}

		int tmpBuffMax = size / partSize;

		if ((size % partSize) == 0) {
			tmpBuffMax--;
		}

		if (aPart > tmpBuffMax) {
			throw new RuntimeException("Buffer num too large");
		}

		fillBuffer(aPart);
	}

	/**
	 * Returns position of actual character in book.
	 *
	 * @return position of actual character in book
	 */
	public synchronized int getPosition() {
		return (buffer == null) ? 0 : ((buffer.partNo * partSize) + index);
	}

	/**
	 * Sets new actual position in book (moves buffer and bufferOffset)
	 *
	 * @param aPos
	 */
	public synchronized int setPosition(int aPos) {
		Common.debug("Book - setPosition: " + aPos);

		if (aPos < 0) {
			aPos = 0;
		} else if (aPos >= size) {
			aPos = size - 1;
		}

		try {
			rollBuffer(aPos / partSize);
			index = aPos % partSize;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return aPos;
	}

	/**
	 * Sets position in book from the given percentage
	 *
	 * @param aPerc
	 * @throws Exception
	 */
	public void setPercPosition(int aPerc)
		throws Exception
	{
		if (aPerc < 0) {
			aPerc = 0;
		} else if (aPerc > 100) {
			aPerc = 100;
		}

		setPosition(((size - 1) * aPerc) / 100);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Book (id=" + id + ", name=\"" + name + "\", size=" + size
		+ ", position=" + getPosition() + ")";
	}

	/**
	 * Returns ID of book.
	 *
	 * @return ID of book
	 * @see Common#createIdFromName(String)
	 */
	public String getId() {
		if (id == null) {
			id = Common.createIdFromName(name);
		}

		return id;
	}

	public synchronized static BufferHolder getNextBuff() {
		return nextBuff;
	}

	public synchronized static void setNextBuff(BufferHolder nextBuff) {
		Book.nextBuff = nextBuff;
	}

	public synchronized static BufferThread getBfThread() {
		return bfThread;
	}

	public synchronized static void setBfThread(BufferThread bfThread) {
		Book.bfThread = bfThread;
	}

	public boolean isChapterFile() {
		InputStream tmpIS = open(Common.CHAPTERS_FILE);

		if (tmpIS == null) {
			return false;
		}

		try {
			tmpIS.close();
		} catch (IOException e) {
			Common.error(e);
		}

		return true;
	}

	public void getChapters(List aList, Vector anIdxVector) {
		InputStream tmpIS = open(Common.CHAPTERS_FILE);

		if (tmpIS == null) {
			Common.debugErr("No chapter file's present.");
		} else {
			DataInputStream tmpDIS = new DataInputStream(tmpIS);

			try {
				final int tmpCount = tmpDIS.readInt();
				Common.debug(tmpCount + " chapters available");

				for (int i = 0; i < tmpCount; i++) {
					aList.append(tmpDIS.readUTF(), null);
					anIdxVector.addElement(new Integer(tmpDIS.readInt()));
				}

				tmpDIS.close();
			} catch (IOException ioe) {
				Common.debugErr("getChapters() : " + ioe.getMessage());
				throw new RuntimeException(ioe.getMessage());
			}
		}
	}

	public boolean isRightToLeft() {
		return rightToLeft;
	}

	public void setRightToLeft(boolean rightToLeft) {
		this.rightToLeft = rightToLeft;
	}

	//~ Inner Classes ==========================================================

	/**
	 * Class which reads part of book to buffer.
	 *
	 * @author Josef Cacek
	 */
	static class BufferHolder {
		//~ Instance fields ====================================================

		private char[] buffer;
		volatile int bufferLength;
		volatile int partNo;
		private String path;

		//~ Constructors =======================================================

		/**
		 * Constructor in which one part of book is readed to buffer
		 *
		 * @param aPart
		 *            number of part which should be read
		 */
		public BufferHolder(final int aPart, final int aSize, final String aID) {
			partNo = aPart;
			bufferLength = aSize;
			path = "/" + Common.DATA_FOLDER + "/" + aID + "/"
				+ Common.PART_FILE_PREFIX + partNo;
		}

		//~ Methods ============================================================

		/**
		 * Reads buffer
		 *
		 * @throws IOException
		 */
		public void read(final boolean aRightToLeft)
			throws IOException
		{
			Common.debug("BufferHolder.read() started");

			InputStream tInStream = getClass().getResourceAsStream(path);

			// bj check if a data block is there
			if (tInStream == null) {
				return;
			}

			UTF8ISReader tmpReader = new UTF8ISReader(tInStream);
			char[] tmpBuff = new char[bufferLength];
			int tmpCount = tmpReader.read(tmpBuff);
			bufferLength = tmpCount;

			// bug fix (reported by Nick Maher - SonyEricsson phones)
			while ((tmpCount > 0) && (bufferLength < tmpBuff.length)) {
				tmpCount =
					tmpReader.read(
						tmpBuff,
						bufferLength,
						tmpBuff.length - bufferLength);

				if (tmpCount > 0) {
					bufferLength += tmpCount;
				}
			}

			if (aRightToLeft) {
				// do shaping
				String tmpNew =
					ShapeArabic.render(new String(tmpBuff, 0, bufferLength));
				tmpBuff = tmpNew.toCharArray();
				bufferLength = tmpBuff.length;
			}

			setBuffer(tmpBuff);
			tmpReader.close();
			Common.debug("BufferHolder.read() finished");
		}

		public synchronized char[] getBuffer() {
			return buffer;
		}

		private synchronized void setBuffer(char[] aBuff) {
			buffer = aBuff;
		}

		public String toString() {
			if (Common.DEBUG) {
				return "buffer: " + new String(buffer) + "\nbufferLength: "
				+ bufferLength + "\npartNo: " + partNo + "\npath: " + path;
			}

			return super.toString();
		}
	}

	/**
	 * Class for reading next part of book in separate thread (prebuffering).
	 *
	 * @author Josef Cacek
	 */
	static class BufferThread
		extends Thread
	{
		//~ Instance fields ====================================================

		private BufferHolder holder;
		private boolean rightToLeft;

		//~ Constructors =======================================================

		/**
		 * Constructor
		 *
		 * @param aPart
		 *            number of next part
		 */
		public BufferThread(final int aPart, final int aSize, final String aId,
			final boolean aRL) {
			setHolder(new BufferHolder(aPart, aSize, aId));
			setBfThread(this);
			rightToLeft = aRL;
		}

		//~ Methods ============================================================

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			Common.debug("BufferThread.run() started");

			try {
				getHolder().read(rightToLeft);
			} catch (IOException e) {
				Common.debugErr("BufferThread.run() IOException: " + e);
			}

			setNextBuff(getHolder());
			setBfThread(null);
			Common.debug("BufferThread.run() finished");
		}

		/**
		 * Sets buffer
		 *
		 * @param aHolder
		 */
		public synchronized void setHolder(final BufferHolder aHolder) {
			holder = aHolder;
		}

		/**
		 * Returns buffer
		 */
		public synchronized BufferHolder getHolder() {
			return holder;
		}
	}
}
