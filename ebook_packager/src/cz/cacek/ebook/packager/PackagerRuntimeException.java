package cz.cacek.ebook.packager;


/**
 * Runtime exceptions used in ebook packager.
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.4 $
 * @created $Date: 2010/01/08 13:58:51 $
 */
public class PackagerRuntimeException
	extends RuntimeException
{
	private static final long serialVersionUID = 4972482960704920319L;
	private static ResourceProvider res = ResourceProvider.getInstance();

	/**
	 * Inherited constructor
	 */
	public PackagerRuntimeException() {
		super();
	}

	/**
	 * Inherited constructor
	 */
	public PackagerRuntimeException(String aKey) {
		super(res.get(aKey));
	}

	/**
	 * Constructor with parametrized message
	 */
	public PackagerRuntimeException(String aKey, String[] args) {
		super(res.get(aKey, args));
	}

	/**
	 * Inherited constructor
	 */
	public PackagerRuntimeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Inherited constructor
	 */
	public PackagerRuntimeException(String aKey, Throwable cause) {
		super(res.get(aKey), cause);
	}

	/**
	 * Constructor with parametrized message
	 */
	public PackagerRuntimeException(String aKey, String[] anArgs,
		Throwable cause) {
		super(res.get(aKey, anArgs), cause);
	}
}
