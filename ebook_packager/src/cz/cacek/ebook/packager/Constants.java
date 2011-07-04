package cz.cacek.ebook.packager;


/**
 * Constants used in EBookME packager
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.6 $
 * @created $Date: 2010/01/08 13:58:51 $
 */
public class Constants {
	/**
	 * <code>PROPERTY_FILE</code> contains default filename for property file.
	 */
	public static final String PROPERTY_FILE = "application.properties";

	/**
	 * Default book name
	 */
	public static final String BOOK_NAME_DEF = "ebook";

	//	*************** Property names *************************

	/**
	 * Property name for book description
	 */
	public static final String PROP_DESCRIPTION = "description";

	/**
	 * Property name for input file charset
	 */
	public static final String PROP_CHARSET = "charset";

	/**
	 * Property name for input file charset
	 */
	public static final String PROP_NAME = "name";

	/**
	 * Property name for part size (and buffer size in EBookME)
	 */
	public static final String PROP_PARTSIZE = "part";

	/**
	 * Property name for enabling debug info
	 */
	public static final String PROP_DEBUG = "debug";

	/**
	 * Property name for filename base of output files
	 */
	public static final String PROP_OUT = "out";

	/**
	 * Property name for disable reformating texts (spaces, etc.)
	 */
	public static final String PROP_AUTOFORMAT = "autoformat";

	/**
	 * Property name for change splashscreen icon
	 */
	public static final String PROP_SPLASH = "splashimage";

	/**
	 * Property name for right-to-left texts flag.
	 */
	public static final String PROP_RIGHT_TO_LEFT = "righttoleft";

	//	*************** Property values ************************

	/**
	 * Default property value for input file charset
	 */
	public static final String PROP_CHARSET_DEF = "default";

	/**
	 * Default property value for part size
	 */
	public static final String PROP_PARTSIZE_DEF = "5000";

	/**
	 * Default property value for enable debuging
	 */
	public static final String PROP_DEBUG_DEF = "false";

	/**
	 * Default property value for filename base of output files
	 */
	public static final String PROP_OUT_DEF = "ebook";

	/**
	 * Default property value for autoformat texts flag
	 */
	public static final String PROP_AUTOFORMAT_DEF = "true";

	/**
	 * Default property value for right-to-left text flag
	 */
	public static final String PROP_RIGHT_TO_LEFT_DEF = "false";

	//	********************************************************	
}
