package cz.cacek.ebook.packager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.nio.charset.Charset;

import java.util.Enumeration;
import java.util.Properties;


/**
 * Provides String (and text files) utilities
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.8 $
 * @created $Date: 2010/01/08 13:58:50 $
 */
public class StringUtils {
	/**
	 * character buffer length for reading text files
	 */
	public static final int BUFF_LEN = 2048;

	/**
	 * Name of charset used in MANIFEST.MF files
	 */
	public static final String MANIFEST_CHARSET = "ISO-8859-1";

	/**
	 * Reads input stream to string. If exception occurs, null is returned.
	 * Input stream is closed after reading!
	 * @param aIS input stream to read
	 * @param aCharset charset of input stream
	 * @return String readed from input stream or null (if exception is throwed)
	 */
	public static String read(InputStream aIS, String aCharset) {
		String tmpResult = null;

		try {
			InputStreamReader tmpISR =
				new InputStreamReader(aIS, getKnownCharset(aCharset));
			tmpResult = readInternal(tmpISR);
		} catch (IOException ioe) {
			//nothing to do - if exception occurs null is returned
		}

		return tmpResult;
	}

	/**
	 * Reads input stream to string. It gets default system charset as input encoding.
	 * If exception occurs, null is returned.
	 * Input stream is closed after reading!
	 * @param aIS
	 * @return String readed from input stream or null (if exception is throwed)
	 */
	public static String read(InputStream aIS) {
		String tmpResult = null;

		try {
			InputStreamReader tmpISR = new InputStreamReader(aIS);
			tmpResult = readInternal(tmpISR);
		} catch (IOException ioe) {
			//nothing to do - if exception occurs null is returned
		}

		return tmpResult;
	}

	/**
	 * Reads String from InputStreamReader and closes reader.
	 * @param aISR
	 * @return content of given InputStream
	 * @throws IOException
	 */
	protected static String readInternal(InputStreamReader aISR)
		throws IOException
	{
		char[] tmpBuff = new char[BUFF_LEN];
		int tmpLen;
		StringBuffer tmpSB = new StringBuffer();

		while ((tmpLen = aISR.read(tmpBuff)) != -1) {
			tmpSB.append(tmpBuff, 0, tmpLen);
		}

		aISR.close();

		return tmpSB.toString();
	}

	/**
	 * Reads text file to String
	 * @param aFile
	 * @return String readed from file or null (if exception is throwed)
	 */
	public static String read(File aFile) {
		String tmpResult = null;

		try {
			tmpResult = read(new FileInputStream(aFile));
		} catch (IOException ioe) {
			//nothing to do - if exception occurs null is returned
		}

		return tmpResult;
	}

	/**
	 * Reads text file with given encoding to String
	 * @param aFile
	 * @param aCharset
	 * @return String readed from file or null (if exception is throwed)
	 */
	public static String read(File aFile, String aCharset) {
		String tmpResult = null;

		try {
			tmpResult = read(new FileInputStream(aFile), aCharset);
		} catch (IOException ioe) {
			//nothing to do - if exception occurs null is returned
		}

		return tmpResult;
	}

	/**
	 * Loads properties from inputStream, returns null, if loading fails.
	 * @param aIS input stream
	 * @return Properties from given InputStream
	 */
	public static Properties readProperties(InputStream aIS) {
		Properties tmpProps = new Properties();

		try {
			tmpProps.load(aIS);
		} catch (IOException ioe) {
			tmpProps = null;
		}

		return tmpProps;
	}

	/**
	 * Loads properties from file, returns null, if loading fails.
	 * @param aFile
	 * @return Properties from given file
	 */
	public static Properties readProperties(File aFile) {
		try {
			return readProperties(new FileInputStream(aFile));
		} catch (FileNotFoundException e) {
		}

		return null;
	}

	/**
	 * Loads properties from file, returns null, if loading fails.
	 * @param aFileName
	 * @return Properties from given file
	 */
	public static Properties readProperties(String aFileName) {
		return readProperties(new File(aFileName));
	}

	/**
	 * Reads manifest file (special kind of properties).
	 * @param aFile
	 * @return properties readed from MANIFEST file
	 */
	public static Properties readManifest(File aFile) {
		String tmpMF = read(aFile);

		if (tmpMF != null) {
			tmpMF = tmpMF.replaceAll("[\r\n]+", "\n").replaceAll("\n ", "");

			try {
				ByteArrayInputStream tmpIS =
					new ByteArrayInputStream(tmpMF.getBytes(MANIFEST_CHARSET));

				return readProperties(tmpIS);
			} catch (UnsupportedEncodingException uee) {
			}
		}

		return null;
	}

	/**
	 * Reads manifest file (special kind of properties).
	 * @param aPath path to file
	 * @return properties readed from MANIFEST file
	 */
	public static Properties readManifest(String aPath) {
		return readManifest(new File(aPath));
	}

	/**
	 * Save properties in form, which is used in MANIFEST.MF
	 * @param aFile
	 * @param aProps
	 * @throws IOException
	 */
	public static void saveManifest(Properties aProps, File aFile)
		throws IOException
	{
		OutputStreamWriter tmpOSW =
			new OutputStreamWriter(new FileOutputStream(aFile), MANIFEST_CHARSET);
		Enumeration tmpKeys = aProps.keys();

		while (tmpKeys.hasMoreElements()) {
			String tmpKey = (String) tmpKeys.nextElement();
			tmpOSW.write(tmpKey + ": " + aProps.get(tmpKey) + "\n");
		}

		tmpOSW.flush();
		tmpOSW.close();
	}

	/**
	 * Strips path and extension from a filename example: lib/venus.jnlp -> venus
	 * @param aPath path to file
	 * @return filename base
	 */
	public static String getBaseName(String aPath) {
		return getBaseName(new File(aPath));
	}

	/**
	 * Returns filename base for given file
	 * @param aFile
	 * @return filename base
	 */
	public static String getBaseName(File aFile) {
		if (aFile == null) {
			throw new NullPointerException("File can't be null.");
		}

		// 1) Strip path.
		String base = aFile.getName();

		// 2) Strip possible extension.
		int index = base.lastIndexOf('.');

		if (index != -1) {
			base = base.substring(0, index);
		}

		return base;
	}

	/**
	 * Checks if given charset is supported if not returns the default one
	 * @param aCharset charset name
	 * @return charset name
	 */
	public static String getKnownCharset(String aCharset) {
		if ((aCharset == null) || Constants.PROP_CHARSET_DEF.equals(aCharset)) {
			try {
				aCharset = System.getProperty("file.encoding");
			} catch (Exception e) {
				//will be handled in next if :)
			}
		}

		if ((aCharset == null) || !Charset.isSupported(aCharset)) {
			//small hack
			aCharset = "ISO-8859-1";
		}

		return aCharset;
	}

	/**
	 * Converts object to normalized string (line breaks are removed), tabs are and multiple spaces replaced
	 * by normalized spaces. String is trimmed.
	 * @param aObj
	 * @return string representation of object in normalized form
	 */
	public static String toNormalizedString(Object aObj) {
		if (aObj == null) {
			return "";
		}

		return aObj.toString().replaceAll("[\r\n]+", " ")
				   .replaceAll("[ \t]+", " ").trim();
	}

	/**
	 * Returns true if trimmed input string is empty or null
	 * @param aStr string
	 * @return true if string is empty
	 */
	public static boolean isEmpty(final String aStr) {
		return (aStr == null) || "".equals(aStr.trim());
	}
}
