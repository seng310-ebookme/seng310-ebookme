package cz.cacek.ebook.packager;

import java.io.PrintStream;


/**
 * Wrapper for print streams, which uses
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.4 $
 * @created $Date: 2010/01/08 13:58:51 $
 */
public class PSWrapper {
	private static ResourceProvider res = ResourceProvider.getInstance();
	private PrintStream pStream;

	/**
	 * Constructor - creates wrapper for given print stream
	 * @param aPS
	 */
	public PSWrapper(PrintStream aPS) {
		super();
		setPStream(aPS);
	}

	/**
	 * Prints localized message to printstream
	 * @param aKey
	 */
	public void print(String aKey) {
		pStream.print(res.get(aKey));
	}

	/**
	 * Prints localized message to printstream
	 * @param aKey
	 */
	public void println(String aKey) {
		pStream.println(res.get(aKey));
	}

	/**
	 * Prints localized message with given arguments to printstream
	 * @param aKey key of message
	 * @param anArg argument for message
	 */
	public void print(String aKey, String anArg) {
		print(aKey, new String[] { anArg });
	}

	/**
	 * Prints localized message with given arguments to printstream
	 * @param aKey key of message
	 * @param anArgs arguments for message
	 */
	public void print(String aKey, String[] anArgs) {
		pStream.print(res.get(aKey, anArgs));
	}

	/**
	 * Prints localized message with given arguments to printstream
	 * @param aKey key of message
	 * @param anArg argument for message
	 */
	public void println(String aKey, String anArg) {
		println(aKey, new String[] { anArg });
	}

	/**
	 * Prints localized message with given arguments to printstream
	 * @param aKey key of message
	 * @param anArgs arguments for message
	 */
	public void println(String aKey, String[] anArgs) {
		pStream.println(res.get(aKey, anArgs));
	}

	/**
	 * Returns printstream which is wrapped
	 * @return printstream which is wrapped
	 */
	public PrintStream getPStream() {
		return pStream;
	}

	/**
	 * Sets printStream
	 * @param aPS
	 */
	public void setPStream(PrintStream aPS) {
		if (aPS == null) {
			throw new PackagerRuntimeException("exception.null",
				new String[] { "PrintStream" });
		}

		pStream = aPS;
	}
}
