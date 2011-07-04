package cz.cacek.ebook.packager;

import java.io.UnsupportedEncodingException;

import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


/**
 * UTF-8 support for ResourceBundle
 * @author thoughts (http://www.thoughtsabout.net/)
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.6 $
 * @created $Date: 2010/01/08 13:58:50 $
 */
public final class Utf8ResourceBundle {
	private Utf8ResourceBundle() {
	}

	public static final ResourceBundle getBundle(String baseName) {
		ResourceBundle bundle = ResourceBundle.getBundle(baseName);

		return createUtf8PropertyResourceBundle(bundle);
	}

	public static final ResourceBundle getBundle(String baseName, Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);

		return createUtf8PropertyResourceBundle(bundle);
	}

	public static ResourceBundle getBundle(String baseName, Locale locale,
		ClassLoader loader) {
		ResourceBundle bundle =
			ResourceBundle.getBundle(baseName, locale, loader);

		return createUtf8PropertyResourceBundle(bundle);
	}

	private static ResourceBundle createUtf8PropertyResourceBundle(
		ResourceBundle bundle) {
		if (!(bundle instanceof PropertyResourceBundle)) {
			return bundle;
		}

		return new Utf8PropertyResourceBundle((PropertyResourceBundle) bundle);
	}

	/**
	 * New handler for PropertyResourceBundle. Strings which was readed as Latin-1 are
	 * re-created from original bytes as UTF-8
	 * @author thoughts (http://www.thoughtsabout.net/)
	 * @author Josef Cacek <josef.cacek@atlas.cz>
	 * @author $Author: kwart $
	 * @version $Revision: 1.6 $
	 * @created $Date: 2010/01/08 13:58:50 $
	 */
	private static class Utf8PropertyResourceBundle
		extends ResourceBundle
	{
		PropertyResourceBundle bundle;

		Utf8PropertyResourceBundle(PropertyResourceBundle bundle) {
			this.bundle = bundle;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.ResourceBundle#getKeys()
		 */
		public Enumeration getKeys() {
			return bundle.getKeys();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
		 */
		protected Object handleGetObject(String key) {
			String tmpResult = key;

			try {
				tmpResult = bundle.getString(key);

				if (tmpResult != null) {
					try {
						tmpResult =
							new String(tmpResult.getBytes("ISO-8859-1"), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// Shouldn't fail - but should we still add logging message?
					}
				} else {
					tmpResult = key;
				}
			} catch (Exception e) {
				//ok maybe missing resources
			}

			return tmpResult;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer tmpBuff = new StringBuffer();

			for (Enumeration en = getKeys(); en.hasMoreElements();) {
				String tmpKey = (String) en.nextElement();
				tmpBuff.append(tmpKey).append("='")
					   .append(handleGetObject(tmpKey)).append("'\n");
			}

			return tmpBuff.toString();
		}
	}
}
