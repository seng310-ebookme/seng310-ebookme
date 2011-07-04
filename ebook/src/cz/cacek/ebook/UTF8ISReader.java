package cz.cacek.ebook;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UTFDataFormatException;


/**
 * Encoding of ebooks is UTF-8 and not all J2ME implementations supports this
 * encoding. This class adds support for UTF-8
 *
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.7 $
 * @created $Date: 2010/01/08 08:29:49 $
 */
public class UTF8ISReader
	extends Reader
{
	//~ Instance fields ========================================================

	// ~ Instance fields
	// ========================================================
	private int[] readAhead;
	private boolean readNext;
	private InputStream inputStream;

	//~ Constructors ===========================================================

	// ~ Constructors
	// ===========================================================

	/**
	 * Constructor.
	 *
	 * @param anIn
	 */
	public UTF8ISReader(InputStream anIn) {
		if (anIn == null) {
			throw new NullPointerException("Can't read null stream.");
		}

		inputStream = anIn;
		readAhead = new int[3];
		prepareForNextChar();
	}

	//~ Methods ================================================================

	// ~ Methods
	// ================================================================

	/*
	 * (non-Javadoc)
	 *
	 * @see java.io.Reader#ready()
	 */
	public boolean ready() {
		try {
			return inputStream.available() > 0;
		} catch (IOException x) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.io.Reader#close()
	 */
	public void close()
		throws IOException
	{
		// bj check if is there something to close
		if (inputStream != null) {
			inputStream.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.io.Reader#read(char[], int, int)
	 */
	public int read(char[] cbuf, int off, int len)
		throws IOException
	{
		int count = 0;
		int currentChar = 0;

		if (len == 0) {
			return 0;
		}

		readNext = true;

		while (count < len) {
			int firstByte = getByteOfCurrentChar(0);

			if (firstByte < 0) {
				if ((firstByte == -1) && (count == 0)) {
					return -1;
				}

				return count;
			}

			int extraBytes;

			switch (firstByte >> 4) {
				case 0: // '\0'
				case 1: // '\001'
				case 2: // '\002'
				case 3: // '\003'
				case 4: // '\004'
				case 5: // '\005'
				case 6: // '\006'
				case 7: // '\007'
					extraBytes = 0;
					currentChar = firstByte;

					break;

				case 12: // '\f'
				case 13: // '\r'
					extraBytes = 1;
					currentChar = firstByte & 0x1f;

					break;

				case 14: // '\016'
					extraBytes = 2;
					currentChar = firstByte & 0xf;

					break;

				case 8: // '\b'
				case 9: // '\t'
				case 10: // '\n'
				case 11: // '\013'
				default:
					throw new UTFDataFormatException(
							"invalid first byte "
							+ Integer.toBinaryString(firstByte));
			}

			for (int j = 1; j <= extraBytes; j++) {
				int nextByte = getByteOfCurrentChar(j);

				if (nextByte == -2) {
					return count;
				}

				if (nextByte == -1) {
					throw new UTFDataFormatException("partial character");
				}

				if ((nextByte & 0xc0) != 128) {
					throw new UTFDataFormatException(
							"invalid byte " + Integer.toBinaryString(nextByte));
				}

				currentChar = (currentChar << 6) + (nextByte & 0x3f);
			}

			cbuf[off + count] = (char) currentChar;
			count++;
			readNext = false;
			prepareForNextChar();
		}

		return count;
	}

	private int getByteOfCurrentChar(int byteOfChar)
		throws IOException
	{
		if (readAhead[byteOfChar] != -2) {
			return readAhead[byteOfChar];
		}

		if (!readNext && (inputStream.available() <= 0)) {
			return -2;
		}

		readAhead[byteOfChar] = inputStream.read();

		return readAhead[byteOfChar];
	}

	private void prepareForNextChar() {
		readAhead[0] = -2;
		readAhead[1] = -2;
		readAhead[2] = -2;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.io.Reader#markSupported()
	 */
	public boolean markSupported() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.io.Reader#mark(int)
	 */
	public void mark(int readAheadLimit)
		throws IOException
	{
		throw new IOException("mark() not supported");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.io.Reader#reset()
	 */
	public void reset()
		throws IOException
	{
		throw new IOException("reset() not supported");
	}

	/**
	 * Returns count of characters (UTF-8) in given part of byte array.
	 *
	 * @param array
	 *            byte array
	 * @param offset
	 *            start position of counting
	 * @param length
	 *            count of bytes for counting characters
	 * @return count of characters in given part of byte array
	 */
	public int sizeOf(byte[] array, int offset, int length) {
		int count = 0;

		for (int endOfArray = offset + length; offset < endOfArray;) {
			count++;

			switch ((array[offset] & 0xff) >> 4) {
				case 0: // '\0'
				case 1: // '\001'
				case 2: // '\002'
				case 3: // '\003'
				case 4: // '\004'
				case 5: // '\005'
				case 6: // '\006'
				case 7: // '\007'
					offset++;

					break;

				case 12: // '\f'
				case 13: // '\r'
					offset += 2;

					break;

				case 14: // '\016'
					offset += 3;

					break;

				case 8: // '\b'
				case 9: // '\t'
				case 10: // '\n'
				case 11: // '\013'
				default:
					return count;
			}
		}

		return count;
	}
}
