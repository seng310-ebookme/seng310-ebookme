package cz.cacek.ebook.packager;

import cz.cacek.ebook.Common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.text.MessageFormat;

import java.util.Enumeration;
import java.util.ResourceBundle;


/**
 * Entry point to ebook and packager internationalization. Resource bundles has
 * base "cz.cacek.ebook.packager.resources.messages".
 *
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.11 $
 * @created $Date: 2010/01/08 13:58:51 $
 */
public class ResourceProvider {
	public static final String ME_PREFIX = "ebook.";
	private static final ResourceProvider provider = new ResourceProvider();
	private ResourceBundle bundle;

	private ResourceProvider() {
		bundle =
			ResourceBundle.getBundle(
				"cz.cacek.ebook.packager.resources.messages");
	}

	/**
	 * Returns singleton of ResourceProvider
	 *
	 * @return singleton
	 */
	public synchronized static ResourceProvider getInstance() {
		return provider;
	}

	/**
	 * Returns ResourceBundle object
	 *
	 * @return resource bundle holded by this provider
	 */
	public ResourceBundle getBundle() {
		return bundle;
	}

	/**
	 * Returns message for given key from active ResourceBundle
	 *
	 * @param aKey
	 *            name of key in resource bundle
	 * @return message for given key
	 */
	public String get(String aKey) {
		return bundle.getString(aKey);
	}

	/**
	 * Returns message for given key from active ResourceBundle and replaces
	 * parameters with values given in array.
	 *
	 * @param aKey
	 *            key in resource bundle
	 * @param anArgs
	 *            array of parameters to replace in message
	 * @return message for given key with given arguments
	 */
	public String get(String aKey, String[] anArgs) {
		String tmpResource = get(aKey);

		if (tmpResource == null) {
			return aKey;
		} else if ((anArgs == null) || (anArgs.length == 0)) {
			return tmpResource;
		}

		MessageFormat tempFormat = new MessageFormat(tmpResource);

		return tempFormat.format(anArgs);
	}

	/**
	 * Writes ebook messages to given outputstream.
	 *
	 * @param aOS
	 * @throws IOException
	 */
	public void createMEResources(OutputStream aOS)
		throws IOException
	{
		Enumeration tmpKeys = bundle.getKeys();
		Writer tmpWriter = new OutputStreamWriter(aOS, Common.ENCODING);

		while (tmpKeys.hasMoreElements()) {
			String tmpKey = (String) tmpKeys.nextElement();

			if (tmpKey.startsWith(ME_PREFIX)) {
				// don't forget to replace end of lines
				String tmpValue =
					bundle.getString(tmpKey).replaceAll("[\r]?[\n]", "\\\\n");
				String tmpNewKey = tmpKey.substring(ME_PREFIX.length());
				tmpWriter.write(tmpNewKey + "=" + tmpValue + "\n");
			}
		}

		tmpWriter.flush();
	}
}
