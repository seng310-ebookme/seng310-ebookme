package cz.cacek.ebook.packager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * This class provides basic functionality for work with property files.
 * It encapsulates work with java.util.Properties class.
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.7 $
 * @created $Date: 2010/01/08 13:58:51 $
 * @see java.util.Properties
 */
public class PropertyProvider {
	private static PropertyProvider provider = null;
	private Map properties;

	/**
	 * Creates new instance of PropertyProvider and loads default properties - if exist.
	 */
	private PropertyProvider() {
		properties = new HashMap();

		try {
			loadDefault();
		} catch (PackagerException e) {
			// default file probably doesn't exist
			// do nothing in this time
		}
	}

	/**
	 * Returns singleton of this class (first call tries to load
	 * properties from default file, if it exists).
	 * @return instance of PropertyProvider
	 * @see Constants#PROPERTY_FILE
	 */
	public static PropertyProvider getInstance() {
		if (provider == null) {
			provider = new PropertyProvider();
		}

		return provider;
	}

	/**
	 * Loads properties from file with given filename.
	 * @param aFileName name of file from which are properties loaded
	 * @throws PackagerException
	 */
	public void loadProperties(String aFileName)
		throws PackagerException
	{
		if (aFileName != null) {
			loadProperties(new File(aFileName));
		} else {
			throw new PackagerException("exception.file.exist",
				new String[] { null });
		}
	}

	/**
	 * Loads properties from given file.
	 * @param aFile
	 * @throws PackagerException
	 */
	public void loadProperties(File aFile)
		throws PackagerException
	{
		if ((aFile != null) && aFile.canRead()) {
			try {
				BufferedReader tmpBR =
					new BufferedReader(new FileReader(aFile));
				String tmpLine = null;

				while (null != (tmpLine = tmpBR.readLine())) {
					tmpLine = tmpLine.trim();

					int tmpPos = tmpLine.indexOf('=');

					if ((tmpPos > 0) && (tmpLine.charAt(0) != '#')) {
						String tmpKey = tmpLine.substring(0, tmpPos).trim();

						if (tmpKey.length() > 0) {
							String tmpVal =
								tmpLine.substring(tmpPos + 1).trim();
							properties.put(tmpKey, tmpVal);
						}
					}
				}

				tmpBR.close();
			} catch (Exception e) {
				throw new PackagerException("exception.file.read",
					new String[] { aFile.toString() }, e);
			}
		} else {
			throw new PackagerException("exception.file.read",
				new String[] { "" + aFile });
		}
	}

	/**
	 * Sets property from given parameter. An expression must be
	 * in this form: <code>propertyName=propertyValue</code>
	 * @param anExpr string in this form: key=value
	 * @throws PackagerException parameter is not in desired form
	 */
	public void setProperty(String anExpr)
		throws PackagerException
	{
		int tmpPos;

		if ((anExpr != null) && ((tmpPos = anExpr.indexOf('=')) > -1)) {
			setProperty(
				anExpr.substring(0, tmpPos),
				anExpr.substring(tmpPos + 1));
		} else {
			throw new PackagerException("exception.property.set",
				new String[] { anExpr });
		}
	}

	/**
	 * Sets property with given name to given value
	 * @param aKey name of a property
	 * @param aValue value of a property
	 */
	public void setProperty(String aKey, String aValue) {
		properties.put(aKey, aValue);
	}

	/**
	 * Returns value of property with given name.
	 * @param aKey name of a property
	 * @return value of property or null if property doesn't exist
	 */
	public String getProperty(String aKey) {
		return (String) properties.get(aKey);
	}

	/**
	 * Deletes all properties from PropertyProvider.
	 */
	public void clear() {
		properties.clear();
	}

	/**
	 * Loads properties from default file.
	 * @throws PackagerException
	 */
	public void loadDefault()
		throws PackagerException
	{
		try {
			loadProperties(Constants.PROPERTY_FILE);
		} catch (SecurityException ex) {
			//seems as applet -> can't load defaults 
		}
	}

	/**
	 * Save current set of properties holded by PropertyProvider to a given file.
	 * @param aFile file to which will be properties saved
	 * @throws PackagerException
	 */
	public void saveProperties(File aFile)
		throws PackagerException
	{
		if (aFile != null) {
			String tmpSep = "\n";

			try {
				tmpSep = System.getProperty("line.separator");
			} catch (Exception e) { /* Maybe applet */
			}

			try {
				FileWriter tmpFW = new FileWriter(aFile);
				tmpFW.write("# Generated by PropertyProvider");
				tmpFW.write(tmpSep);
				tmpFW.write(tmpSep);

				for (Iterator it = properties.keySet().iterator();
						it.hasNext();) {
					String tmpKey = (String) it.next();
					String tmpValue = (String) properties.get(tmpKey);
					tmpFW.write(tmpKey.trim());
					tmpFW.write("=");
					tmpFW.write(tmpValue.trim());
					tmpFW.write(tmpSep);
				}

				tmpFW.close();
			} catch (Exception e) {
				throw new PackagerException("exception.property.store",
					new String[] { aFile.toString() }, e);
			}
		} else {
			throw new PackagerException("exception.file.write",
				new String[] { null });
		}
	}

	/**
	 * Save current set of properties holded by PropertyProvider to a file with given filename.
	 * @param aFileName
	 * @throws PackagerException
	 */
	public void saveProperties(String aFileName)
		throws PackagerException
	{
		if (aFileName != null) {
			saveProperties(new File(aFileName));
		} else {
			throw new PackagerException("exception.file.write",
				new String[] { null });
		}
	}

	/**
	 * Save current set of properties to default file.
	 * @throws PackagerException
	 */
	public void saveDefault()
		throws PackagerException
	{
		saveProperties(Constants.PROPERTY_FILE);
	}

	/**
	 * Returns value for given key converted to integer;
	 * @param aKey
	 * @return value from properties converted to integer (if value doesn't exist, 0 is returned)
	 * @see #exists(String)
	 */
	public int getAsInt(String aKey) {
		return getAsInt(aKey, 0);
	}

	/**
	 * Returns value for given key converted to integer; If property doesn't exist default
	 * value is returned.
	 * @param aKey
	 * @param aDefault
	 * @return integer value for given key
	 */
	public int getAsInt(String aKey, int aDefault) {
		int tmpResult = aDefault;

		if (properties.containsKey(aKey)) {
			tmpResult = Integer.parseInt((String) properties.get(aKey));
		}

		return tmpResult;
	}

	/**
	 * Returns value for given key converted to long;
	 * @param aKey
	 * @return value from properties converted to long (if value doesn't exist, 0 is returned)
	 * @see #exists(String)
	 */
	public long getAsLong(String aKey) {
		String tmpValue = (String) properties.get(aKey);

		return (tmpValue == null) ? 0L : Long.parseLong(tmpValue);
	}

	/**
	 * Returns value for given key converted to boolean;
	 * @param aKey
	 * @return value from properties converted to boolean (if value doesn't exist, false is returned)
	 * @see #exists(String)
	 */
	public boolean getAsBool(String aKey) {
		return getAsBool(aKey, false);
	}

	/**
	 * Returns value for given key converted to boolean - if key doesn't exists returns default value
	 * @param aKey property name
	 * @param aDefault default value
	 * @return value from properties converted to boolean (if value doesn't exist, default is returned)
	 * @see #exists(String)
	 */
	public boolean getAsBool(String aKey, boolean aDefault) {
		boolean tmpResult = aDefault;

		if (properties.containsKey(aKey)) {
			tmpResult = Boolean.valueOf((String) properties.get(aKey)) == Boolean.TRUE;
		}

		return tmpResult;
	}

	/**
	 * Tests if exists property with given name.
	 * @param aKey
	 * @return true if exists property with given name
	 */
	public boolean exists(String aKey) {
		return properties.containsKey(aKey);
	}

	/**
	 * Tests if exists property with given name and has not null value.
	 * @param aKey
	 * @return true if exists not null property with given name
	 */
	public boolean existsNotNull(String aKey) {
		return properties.get(aKey) != null;
	}

	/**
	 * Throws PackagerException if given key doesn't exist.
	 * @param aKey property name which must be included in properties, if not exception is thrown
	 * @throws PackagerException key doesn't exist
	 */
	public void checkMandatory(String aKey)
		throws PackagerException
	{
		if (!properties.containsKey(aKey)) {
			throw new PackagerException("exception.property.mandatory",
				new String[] { aKey });
		}
	}

	/**
	 * Returns property value for the given name. If not found returns default value (2nd param).
	 * @param aKey property name
	 * @param aDefault default value
	 * @return property value
	 */
	public String getProperty(String aKey, String aDefault) {
		final String tmpValue = (String) properties.get(aKey);

		return (tmpValue == null) ? aDefault : tmpValue;
	}
}
