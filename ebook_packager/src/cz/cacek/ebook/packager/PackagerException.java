package cz.cacek.ebook.packager;


/**
 * Exceptions used in Packager.
 *
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.4 $
 * @created $Date: 2010/01/08 13:58:51 $
 */
public class PackagerException
	extends Exception
{
	private static final long serialVersionUID = -6999809413108186092L;
	private static ResourceProvider res = ResourceProvider.getInstance();

	/**
	 * Inherited constructor
	 */
	public PackagerException() {
		super();
	}

	/**
	 * Inherited constructor
	 */
	public PackagerException(String aKey) {
		super(res.get(aKey));
	}

	/**
	 * Constructor with parametrized message
	 */
	public PackagerException(String aKey, String[] args) {
		super(res.get(aKey, args));
	}

	/**
	 * Inherited constructor
	 */
	public PackagerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Inherited constructor
	 */
	public PackagerException(String aKey, Throwable cause) {
		super(res.get(aKey), cause);
	}

	/**
	 * Constructor with parametrized message
	 */
	public PackagerException(String aKey, String[] anArgs, Throwable cause) {
		super(res.get(aKey, anArgs), cause);
	}
}
