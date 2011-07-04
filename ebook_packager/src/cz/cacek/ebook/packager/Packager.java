package cz.cacek.ebook.packager;

import cz.cacek.ebook.Common;

import java.awt.image.BufferedImage;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;


/**
 * This class contains main logic of Ebook packager.
 *
 * @author Tomas Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author: kwart $
 * @version $Revision: 1.25 $
 * @created $Date: 2010/01/08 13:58:51 $
 */
public class Packager {
	private static PropertyProvider props = PropertyProvider.getInstance();
	private String dir;
	private PSWrapper psWrapper;
	private Set bookIds;
	private List books;
	private String prefixBase;

	/**
	 * Constructor
	 */
	public Packager() {
		books = new ArrayList();
		bookIds = new HashSet();
		psWrapper = new PSWrapper(System.out);
		initDefaults();
	}

	/**
	 * Initialization of default properties
	 */
	private void initDefaults() {
		if (!props.exists(Constants.PROP_CHARSET)) {
			props.setProperty(Constants.PROP_CHARSET, Constants.PROP_CHARSET_DEF);
		}

		if (!props.exists(Constants.PROP_PARTSIZE)) {
			props.setProperty(Constants.PROP_PARTSIZE,
				Constants.PROP_PARTSIZE_DEF);
		}

		if (!props.exists(Constants.PROP_DEBUG)) {
			props.setProperty(Constants.PROP_DEBUG, Constants.PROP_DEBUG_DEF);
		}

		if (!props.exists(Constants.PROP_OUT)) {
			props.setProperty(Constants.PROP_OUT, Constants.PROP_OUT_DEF);
		}

		if (!props.exists(Constants.PROP_AUTOFORMAT)) {
			props.setProperty(
				Constants.PROP_AUTOFORMAT,
				Constants.PROP_AUTOFORMAT_DEF);
		}
	}

	/**
	 * Handle command line parameters.
	 *
	 * @param args
	 *            parameters
	 * @throws PackagerException
	 *             PropertyProvider fails
	 * @see PropertyProvider
	 */
	private boolean processArgs(String[] args)
		throws PackagerException
	{
		boolean tmpResult = true;
		printDebug("Processing arguments");

		for (int i = 0; i < args.length; i++) {
			String tmpArg = args[i];
			printDebug("Processing argument '" + tmpArg + "'");

			if (tmpArg.equals("--help") || tmpArg.equals("-h")) {
				printHelp();
				tmpResult = false;
			} else if (tmpArg.equals("--version") || tmpArg.equals("-v")) {
				printVersion();
				tmpResult = false;
			} else if (tmpArg.startsWith("-f")) {
				String tmpFile = tmpArg.substring(2);
				PropertyProvider.getInstance().loadProperties(tmpFile);
			} else if (tmpArg.startsWith("-F")) {
				String tmpFile = tmpArg.substring(2);
				PropertyProvider.getInstance().clear();
				PropertyProvider.getInstance().loadProperties(tmpFile);
			} else if (tmpArg.startsWith("-D")) {
				String tmpPropExp = tmpArg.substring(2);
				PropertyProvider.getInstance().setProperty(tmpPropExp);
			} else {
				Book tmpBook =
					new Book(
						props.getProperty(Constants.PROP_NAME),
						props.getProperty(Constants.PROP_DESCRIPTION),
						props.getAsInt(Constants.PROP_PARTSIZE));
				tmpBook.setRightToLeft(
						props.getAsBool(Constants.PROP_RIGHT_TO_LEFT));
				tmpBook.readText(
					tmpArg,
					props.getProperty(Constants.PROP_CHARSET));
				addBook(tmpBook);
				tmpResult = false;
			}
		}

		return tmpResult;
	}

	/**
	 * prints the version of the EBookME components
	 */
	public void printVersion() {
		psWrapper.println("packager.version", Common.VERSION);
	}

	/**
	 * Prints help message
	 */
	public void printHelp() {
		psWrapper.println("packager.help");
	}

	/**
	 * Something to run ;)
	 *
	 * @param anArgs
	 */
	public static void main(String[] anArgs) {
		Packager tmpPackager = new Packager();

		try {
			if (tmpPackager.processArgs(anArgs)) {
				// run gui
				new PackagerGUIApplic();
			} else {
				if (tmpPackager.getBookCount() > 0) {
					tmpPackager.createSuite(System.out);
				}
			}
		} catch (Exception e) {
			if (isDebug()) {
				System.err.println("====================================");
				System.err.println("=== Application caused an error: ===");
				System.err.println("====================================");
				e.printStackTrace();
				System.err.println("====================================");
			} else {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	/**
	 * Adds new book to library which will be generated.
	 *
	 * @param book
	 * @throws PackagerException
	 */
	public void addBook(Book book)
		throws PackagerException
	{
		if (bookIds.contains(book.getId())) {
			// throw new PackagerException("exception.book.id",new String[]
			// {book.getId()});
			int i = bookIds.size() + 1;
			String tmpName = "Book " + i;

			while (bookIds.contains(Common.createIdFromName(tmpName))) {
				i++;
				tmpName = "Book " + i;
			}

			// TODO localize message
			psWrapper.println(
					"Book ID (generated from name) already used, using name '"
					+ tmpName + "' instead.");
			book.setName(tmpName);
		}

		bookIds.add(book.getId());
		books.add(book);
	}

	/**
	 * Returns count of books in current library
	 *
	 * @return count of books
	 */
	public int getBookCount() {
		return books.size();
	}

	/**
	 * Returns book on given position in library.
	 *
	 * @param index
	 * @return book with given index
	 */
	public Book getBook(int index) {
		return (Book) books.get(index);
	}

	/**
	 * Removes book from library
	 *
	 * @param anIndex
	 */
	public void removeBook(int anIndex) {
		bookIds.remove(getBook(anIndex).getId());
		books.remove(anIndex);
	}

	/**
	 * Creates midlet application from library (list of books).
	 *
	 * @param aPS
	 * @throws PackagerException
	 */
	protected void createSuite(PrintStream aPS)
		throws PackagerException
	{
		psWrapper.setPStream(aPS);
		prefixBase = null;
		psWrapper.println("packager.create.suite");
		psWrapper.println(
			"packager.output",
			props.getProperty(Constants.PROP_OUT));

		String tmpName = "EBookPackager_" + new Random().nextInt();
		psWrapper.println("packager.temp.create", tmpName);
		dir = System.getProperty("java.io.tmpdir");

		if (dir == null) {
			throw new PackagerRuntimeException("exception.null",
				new String[] { "Temporary directory" });
		}

		dir = dir.replace('\\', '/');

		if (!dir.endsWith("/")) {
			dir += "/";
		}

		dir += tmpName;

		if (!new File(dir).mkdirs()) {
			throw new PackagerException("exception.directory.create",
				new String[] { dir });
		}

		try {
			writeBooks();

			StringBuffer tmpSB = new StringBuffer();

			for (int i = 0; i < books.size(); i++) {
				if (tmpSB.length() > 0) {
					tmpSB.append(", ");
				}

				tmpSB.append(getBook(i).getAsciiName());
			}

			String tmpBooks = tmpSB.toString();
			unpack();
			modifyManifest(tmpBooks);
			createJar();
			createJad(tmpBooks);
			psWrapper.println("packager.temp.delete");
			deleteRec(new File(dir));
		} catch (IOException ioe) {
			throw new PackagerException(ioe);
		}
	}

	/**
	 * Creates text and configuration part of output library (jar)
	 *
	 * @throws PackagerException
	 * @throws IOException
	 */
	protected void writeBooks()
		throws PackagerException, IOException
	{
		psWrapper.println("packager.books.write");

		if (!new File(dir, Common.DATA_FOLDER).mkdir()) {
			throw new PackagerException("exception.directory.create",
				new String[] { Common.DATA_FOLDER });
		}

		for (int i = 0; i < this.books.size(); i++) {
			Book book = getBook(i);
			writeBook(book);
		}

		createLibrary();
		createResource();
	}

	/**
	 *
	 * @param book
	 * @throws PackagerException
	 * @throws IOException
	 */
	protected void writeBook(Book book)
		throws PackagerException, IOException
	{
		printDebug("Writing book " + book.getId());

		String tmpFoldName = Common.DATA_FOLDER + '/' + book.getId();

		if (!new File(dir, tmpFoldName).mkdir()) {
			throw new PackagerException("exception.directory.create",
				new String[] { tmpFoldName });
		}

		// backup text (with chapter markup)
		final String tmpTextWithMarkup = book.getText();
		createChapters(book);

		int tmpSize = createParts(book);
		createInfo(book, tmpSize);
		// set the original text
		book.setText(tmpTextWithMarkup);
	}

	/**
	 * Creates configuration file for library
	 *
	 * @throws IOException
	 */
	private void createLibrary()
		throws IOException
	{
		final String tmpLib =
			dir + '/' + Common.DATA_FOLDER + '/' + Common.LIBRARY_FILE;
		printDebug("Creating library file " + tmpLib);

		DataOutputStream tmpDOS =
			new DataOutputStream(new FileOutputStream(tmpLib));
		tmpDOS.writeInt(books.size());

		for (int i = 0; i < books.size(); i++) {
			tmpDOS.writeUTF(getBook(i).getName());
		}

		tmpDOS.flush();
		tmpDOS.close();
	}

	/**
	 * Creates resource file (translations) for EbookME
	 *
	 * @throws IOException
	 */
	private void createResource()
		throws IOException
	{
		final String tmpFile =
			dir + '/' + Common.DATA_FOLDER + '/' + Common.LANGUAGE_FILE;
		printDebug(
			"Creating resource file (translations) for EBookME: " + tmpFile);

		FileOutputStream tmpOS = new FileOutputStream(tmpFile);
		ResourceProvider.getInstance().createMEResources(tmpOS);
		tmpOS.flush();
		tmpOS.close();
	}

	/**
	 * Creates configuration file for one book
	 *
	 * @param book
	 * @param aSize
	 * @throws IOException
	 */
	protected void createInfo(Book book, int aSize)
		throws IOException
	{
		final String tmpFile =
			dir + '/' + Common.DATA_FOLDER + '/' + book.getId() + '/'
			+ Common.INFO_FILE;
		printDebug("Creating configuration: " + tmpFile);

		FileOutputStream fos = new FileOutputStream(tmpFile);
		DataOutputStream dos = new DataOutputStream(fos);
		dos.writeInt(aSize);
		dos.writeInt(book.getPartSize());
		dos.writeUTF(book.getDescription());
		dos.writeBoolean(book.isRightToLeft());
		dos.flush();
		dos.close();
	}

	/**
	 * Creates chapters file for one book
	 *
	 * @param aBook
	 * @throws IOException
	 */
	protected void createChapters(final Book aBook)
		throws IOException
	{
		String tmpText = aBook.getText();
		boolean tmpDo = true;
		final List tmpIdxList = new ArrayList();
		final List tmpNameList = new ArrayList();

		do {
			final int startIdx = tmpText.indexOf("[*");
			final int endIdx = tmpText.indexOf("*]");

			if ((startIdx > -1) || (endIdx > -1)) {
				// we've found part of markup
				if ((startIdx != -1) && (endIdx > (startIdx + 1))
						&& ((startIdx + 162) >= endIdx)) {
					String tmpChapter =
						StringUtils.toNormalizedString(
								tmpText.substring(startIdx + 2, endIdx));

					if (tmpChapter.length() == 0) {
						tmpChapter = "???";
					}

					tmpIdxList.add(new Integer(startIdx));
					tmpNameList.add(tmpChapter);
					tmpText =
						tmpText.substring(0, startIdx)
						+ tmpText.substring(endIdx + 2);
				} else {
					printDebug("Wrong chapters markup.");
					tmpIdxList.clear();
					tmpNameList.clear();
					tmpDo = false;
				}
			} else {
				tmpDo = false;
			}
		} while (tmpDo);

		if (tmpIdxList.size() > 0) {
			aBook.setText(tmpText);

			final String tmpFile =
				dir + '/' + Common.DATA_FOLDER + '/' + aBook.getId() + '/'
				+ Common.CHAPTERS_FILE;
			printDebug("Creating chapters: " + tmpFile);

			FileOutputStream fos = new FileOutputStream(tmpFile);
			DataOutputStream dos = new DataOutputStream(fos);
			final int tmpCount = tmpIdxList.size();
			dos.writeInt(tmpCount);

			for (int i = 0; i < tmpCount; i++) {
				dos.writeUTF((String) tmpNameList.get(i));
				dos.writeInt(((Integer) tmpIdxList.get(i)).intValue());
			}

			dos.flush();
			dos.close();
		} else {
			printDebug("No chapter file will be created.");
		}
	}

	/**
	 * Creates single parts of book with requested count of characters. Parts
	 * have UTF-8 encoding.
	 *
	 * @param aBook
	 * @return Lenght of book (it's not count of bytes, but count of UTF-8
	 *         characters)
	 * @throws IOException
	 */
	protected int createParts(Book aBook)
		throws IOException
	{
		File tmpDir =
			new File(dir + '/' + Common.DATA_FOLDER + '/' + aBook.getId());
		tmpDir.mkdirs();

		final String tmpText = aBook.getText();
		final int tmpPart = aBook.getPartSize();

		int tmpIndex = 0;
		final int tmpLenSum = tmpText.length();

		while ((tmpIndex * tmpPart) < tmpLenSum) {
			OutputStreamWriter tmpOSW =
				new OutputStreamWriter(new FileOutputStream(
						new File(tmpDir, Common.PART_FILE_PREFIX + tmpIndex)),
					Common.ENCODING);
			final int tmpLen =
				Math.min(tmpPart, tmpLenSum - (tmpIndex * tmpPart));
			tmpOSW.write(tmpText, tmpIndex * tmpPart, tmpLen);
			tmpOSW.flush();
			tmpOSW.close();
			tmpIndex++;
		}

		return tmpLenSum;
	}

	/**
	 * Upnacks base for J2ME application. It's part of packager resources.
	 *
	 * @throws IOException
	 */
	protected void unpack()
		throws IOException
	{
		psWrapper.println("packager.unpack");

		ZipInputStream tmpIS =
			new ZipInputStream(getClass().getResourceAsStream("/ebook.jar"));
		ZipEntry entry;

		while ((entry = tmpIS.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				continue;
			}

			File file = new File(dir + '/' + entry.getName());
			String filePath = file.getPath().replace('\\', '/');
			String parentPath =
				filePath.substring(0, filePath.lastIndexOf('/'));
			new File(parentPath).mkdirs();

			FileOutputStream fos = new FileOutputStream(file);
			byte[] b = new byte[2048];
			int tmpLen;

			while ((tmpLen = tmpIS.read(b)) != -1) {
				fos.write(b, 0, tmpLen);
			}

			fos.flush();
			fos.close();
		}

		if (props.existsNotNull(Constants.PROP_SPLASH)) {
			final String tmpName = props.getProperty(Constants.PROP_SPLASH);
			final File tmpNewSplash = new File(tmpName);
			final File tmpSplash = new File(dir + "/ebook.png");

			if (tmpNewSplash.canRead()
					&& ImageChooser.isFileImage(tmpNewSplash)) {
				BufferedImage tmpImg = ImageIO.read(tmpNewSplash);
				ImageIO.write(tmpImg, "png", tmpSplash);
			} else {
				printDebug("Can't read splash-screen file '" + tmpName + "'.");
			}
		}
	}

	/**
	 * Changes MANIFEST.MF file.
	 *
	 * @param aBooks
	 * @throws PackagerException
	 * @throws IOException
	 */
	protected void modifyManifest(String aBooks)
		throws PackagerException, IOException
	{
		psWrapper.println("packager.manifest");

		File tmpManifest = new File(this.dir, "META-INF/MANIFEST.MF");
		Properties tmpProps = StringUtils.readManifest(tmpManifest);

		if ((tmpProps == null) || !tmpManifest.canWrite()) {
			throw new PackagerException("exception.file.write",
				new String[] { tmpManifest.toString() });
		}

		setMidletName(tmpProps, aBooks);
		StringUtils.saveManifest(tmpProps, tmpManifest);
	}

	/**
	 * Packs J2ME ebook application
	 *
	 * @throws IOException
	 */
	protected void createJar()
		throws IOException
	{
		psWrapper.println("packager.pack");

		final String tmpJar = props.getProperty(Constants.PROP_OUT) + ".jar";
		printDebug("Output jar: " + tmpJar);

		FileOutputStream fos = new FileOutputStream(tmpJar);
		ZipOutputStream zos = new ZipOutputStream(fos);
		packRec(new File(dir), zos);
		zos.flush();
		zos.close();
	}

	/**
	 * Packs recursively directories and files to ZipOutputStream
	 *
	 * @param file
	 * @param zos
	 * @throws IOException
	 */
	protected void packRec(File file, ZipOutputStream zos)
		throws IOException
	{
		if (file.isDirectory()) {
			String[] children = file.list();

			for (int i = 0; i < children.length; i++) {
				packRec(new File(file, children[i]), zos);
			}
		} else {
			String filePath = file.getPath();
			String entryPath =
				filePath.substring(dir.length() + 1).replace('\\', '/');
			ZipEntry entry = new ZipEntry(entryPath);
			zos.putNextEntry(entry);

			FileInputStream fis = new FileInputStream(file);
			byte[] b = new byte[2048];
			int tmpLen;

			while ((tmpLen = fis.read(b)) != -1) {
				zos.write(b, 0, tmpLen);
			}

			zos.flush();
			fis.close();
		}
	}

	/**
	 * Creates JAD file for generated EBookME application
	 *
	 * @param bookList
	 * @throws IOException
	 */
	protected void createJad(String bookList)
		throws IOException
	{
		psWrapper.println("packager.jad");

		InputStream is = (this.getClass()).getResourceAsStream("/ebook.jad");
		Properties properties = new Properties();
		properties.load(is);
		is.close();

		final String tmpOut = props.getProperty(Constants.PROP_OUT);
		File jar = new File(tmpOut + ".jar");
		setMidletName(properties, bookList);
		properties.setProperty("MIDlet-Jar-URL", getOutPrefixBase() + ".jar");
		properties.setProperty("MIDlet-Jar-Size", String.valueOf(jar.length()));
		StringUtils.saveManifest(properties, new File(tmpOut + ".jad"));
	}

	private void setMidletName(Properties aProperties, String aBooks) {
		aProperties.setProperty("MIDlet-Name", getOutPrefixBase() + "_EBookME");
		aProperties.setProperty("MIDlet-Description", aBooks + " - EBookME");
	}

	/**
	 * Deletes recursively files and folders.
	 *
	 * @param aFile
	 * @throws PackagerException
	 */
	protected void deleteRec(File aFile)
		throws PackagerException
	{
		if (aFile.isDirectory()) {
			String[] children = aFile.list();

			for (int i = 0; i < children.length; i++) {
				deleteRec(new File(aFile, children[i]));
			}
		}

		printDebug("Deleting: " + aFile.getAbsolutePath());

		if (!aFile.delete()) {
			throw new PackagerException("exception.file.delete",
				new String[] { aFile.toString() });
		}
	}

	/**
	 * Returns true if debug mode is enabled.
	 *
	 * @return true if debug mode is enabled
	 */
	public static boolean isDebug() {
		return props.getAsBool(Constants.PROP_DEBUG);
	}

	/**
	 * Prints debug message to System.out stream, if debug mode is enabled.
	 *
	 * @param aMsg
	 */
	public static void printDebug(final String aMsg) {
		if (isDebug()) {
			System.out.println(">>DEBUG: " + aMsg);
		}
	}

	/**
	 * Returns filename (without suffix) for output jar file.
	 *
	 * @return filename (without suffix) for output jar file.
	 */
	public String getOutPrefixBase() {
		if (prefixBase == null) {
			final String tmpPrefix = props.getProperty(Constants.PROP_OUT);
			final int tmpPos =
				Math.max(
					tmpPrefix.lastIndexOf('/'),
					tmpPrefix.lastIndexOf('\\'));
			prefixBase = (tmpPos > 0) ? tmpPrefix.substring(tmpPos + 1)
									  : tmpPrefix;
		}

		return prefixBase;
	}

	public Set getBookIds() {
		return bookIds;
	}

	public List getBooks() {
		return books;
	}
}
