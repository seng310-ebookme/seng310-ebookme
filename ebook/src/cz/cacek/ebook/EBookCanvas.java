package cz.cacek.ebook;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

/**
 * Main display. On this canvas is displayed current part of book.
 * 
 * @author Tomas Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author Jiri Bartos
 * @author $Author: kwart $
 * @version $Revision: 1.32 $
 * @created $Date: 2010/08/24 21:12:44 $
 */
public class EBookCanvas extends Canvas implements CommandListener {
	// ~ Static fields/initializers
	// =============================================

	private static int scrollDelay = Common.AUTOSCROLL_PAUSE;

	// ~ Instance fields
	// ========================================================

	private List list;
	private EBookMIDlet midlet;
	protected View view;
	private String message = null;
	private Font messageFont;
	private Command cmdLibrary;
	private Command cmdInfo;
	private Command cmdOptions;
	private Command cmdPosition;
	private Command cmdAddBookmark;
	private Command cmdOpenBookmark;
	private Command cmdExit;
	private Command cmdChapters;
	private Command cmdRemove;
	private Command cmdOK;
	private Command cmdCancel;
	private Gauge gauge;
	private TextField textfield;
	private Form form;
	protected final ScrollThread scrollThread = new ScrollThread();
	private Vector listVector;
	private byte screenIdx;
	private Display display;
	protected boolean fullscreen;
	protected boolean displayStatus;
	protected boolean positionAsPerc = true;

	// ~ Constructors
	// ===========================================================

	/**
	 * Constructor
	 * 
	 * @param aMidlet
	 * @throws Exception
	 */
	public EBookCanvas(EBookMIDlet aMidlet) throws Exception {
		midlet = aMidlet;
		display = Display.getDisplay(midlet);
		view = new View(getWidth(), getHeight());
		messageFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
		cmdChapters = new Command(ResourceProviderME.get("cmd.chapters"), Command.SCREEN, 1);
		cmdPosition = new Command(ResourceProviderME.get("cmd.position"), Command.SCREEN, 2);
		cmdOptions = new Command(ResourceProviderME.get("cmd.options"), Command.SCREEN, 3);
		cmdInfo = new Command(ResourceProviderME.get("cmd.info"), Command.SCREEN, 4);
		cmdAddBookmark = new Command(ResourceProviderME.get("cmd.addBmk"), Command.SCREEN, 5);
		cmdOpenBookmark = new Command(ResourceProviderME.get("cmd.openBmk"), Command.SCREEN, 6);
		cmdExit = new Command(ResourceProviderME.get("cmd.exit"), Command.SCREEN, 10);

		cmdRemove = new Command(ResourceProviderME.get("cmd.remove"), Command.SCREEN, 2);

		cmdOK = new Command(ResourceProviderME.get("btn.ok"), Command.OK, 1);
		cmdCancel = new Command(ResourceProviderME.get("btn.cancel"), Command.CANCEL, 1);
		addCommand(cmdPosition);
		addCommand(cmdInfo);
		addCommand(cmdOptions);
		addCommand(cmdAddBookmark);
		addCommand(cmdOpenBookmark);
		addCommand(cmdExit);

		if (Library.getInstance().getBookCount() > 1) {
			cmdLibrary = new Command(ResourceProviderME.get("cmd.choose"), Command.SCREEN, 4);
			addCommand(cmdLibrary);
		}

		setCommandListener(this);
		scrollThread.start();
	}

	// ~ Methods
	// ================================================================

	/**
	 * Choosing last used book from library.
	 */
	public void begin(final long aTime) {
		Common.debug("EBookCanvas.begin() started");

		final Library tmpLib = Library.getInstance();

		if (tmpLib.getBookCount() == 1) {
			tmpLib.setBookIdx(0);
		}

		final int tmpIdx = tmpLib.getBookIdx();

		if ((tmpIdx > -1) && (tmpLib.getBookCount() > tmpIdx)) {
			setBook(tmpLib.getBookIdx(), aTime);
		} else {
			createBookList(aTime); // new EBookBookList(this, view);
		}

		BackLight.getInstance().normal();
		Common.debug("EBookCanvas.begin() finished");
	}

	/**
	 * Creates and displays list with books present in this application.
	 * 
	 * @return Displayable
	 */
	private Displayable createBookList(final long aTime) {
		list = new List(ResourceProviderME.get("bl.choose"), Choice.IMPLICIT);

		Library tmpLib = Library.getInstance();

		for (int i = 0, n = tmpLib.getBookCount(); i < n; i++) {
			list.append(tmpLib.getBookName(i), null);
		}

		final int tmpIdx = tmpLib.getBookIdx();
		list.setSelectedIndex((tmpIdx > -1) ? tmpIdx : 0, true);
		list.setCommandListener(this);
		screenIdx = Common.SCREEN_BOOK_LIST;
		waitForIntroEnd(aTime);
		display.setCurrent(list);

		return list;
	}

	private void waitForIntroEnd(long aTime) {
		if (aTime > 0) {
			aTime = (EBookIntro.MIN_MILIS_DISPLAY + aTime) - System.currentTimeMillis();

			if (aTime > 0) {
				try {
					Thread.sleep(aTime);
				} catch (InterruptedException e) {
					Common.debugErr("Thread.sleep() problem: " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Creates book-info form
	 * 
	 * @return Displayable
	 */
	private Displayable createBookInfo(String aName, String aDesc) {
		form = new Form(ResourceProviderME.get("i.head"));

		if ((aName == null) || (aName.length() == 0)) {
			aName = ResourceProviderME.get("i.empty");
		}

		if ((aDesc == null) || (aDesc.length() == 0)) {
			aDesc = ResourceProviderME.get("i.empty");
		}

		form.append(new StringItem(ResourceProviderME.get("i.bk.name"), aName));
		form.append("\n");
		form.append(new StringItem(ResourceProviderME.get("i.bk.desc"), aDesc));
		form.append("\n");
		form.append(new StringItem(ResourceProviderME.get("i.copy.h"), ResourceProviderME.get("i.copy")));
		form.append("\n");
		form.append(new StringItem(ResourceProviderME.get("i.version"), Common.VERSION));
		form.append("\n");
		form.append(new StringItem(ResourceProviderME.get("i.help.h"), ResourceProviderME.get("i.help")));
		// fix for Siemens phones scrolling
		form.append(new StringItem("", ""));
		form.addCommand(cmdOK);
		form.setCommandListener(this);
		screenIdx = Common.SCREEN_BOOK_INFO;
		display.setCurrent(form);

		return form;
	}

	/**
	 * Creates and displays list of Chapters
	 * 
	 * @return Displayable
	 */
	private Displayable createChaptersList() {
		list = new List(ResourceProviderME.get("chap.head"), Choice.IMPLICIT);
		listVector = new Vector();
		view.book.getChapters(list, listVector);
		list.setCommandListener(this);
		list.addCommand(cmdOK);
		list.addCommand(cmdCancel);
		screenIdx = Common.SCREEN_CHAPTERS_LIST;
		display.setCurrent(list);

		return list;
	}

	/**
	 * Constructs gauge position form.
	 */
	private void createPositionForm() {
		form = new Form(ResourceProviderME.get("pos.head"));

		if (positionAsPerc) {
			final int tmpValue = view.getPercPosition();

			if ((tmpValue < 0) || (tmpValue > 100)) {
				throw new RuntimeException("Wrong parameters for Gauge constructor");
			}

			gauge = new Gauge(ResourceProviderME.get("pos.actual"), true, 100, 0);
			gauge.setValue(tmpValue);
			form.append(gauge);
		} else {
			final String tmpValue = Integer.toString(view.getPosition());
			final int tmpMaxLen = Integer.toString(view.book.size).length();
			textfield = new TextField(ResourceProviderME.get("pos.actual"), tmpValue, tmpMaxLen, TextField.NUMERIC);
			form.append(textfield);
		}

		form.addCommand(cmdOK);
		form.addCommand(cmdCancel);
		form.setCommandListener(this);
		display.setCurrent(form);
		screenIdx = Common.SCREEN_POSITION;
	}

	private void createAddBmkForm() {
		form = new Form(ResourceProviderME.get("addBmk.head"));
		textfield = new TextField(ResourceProviderME.get("addBmk.name"), null, 10, TextField.ANY);
		form.append(textfield);

		form.addCommand(cmdOK);
		form.addCommand(cmdCancel);
		form.setCommandListener(this);
		display.setCurrent(form);
		screenIdx = Common.SCREEN_ADD_BMK;
	}

	/**
	 * Creates and displays list of Chapters
	 * 
	 * @return Displayable
	 */
	private Displayable createOpenBmkList() {
		list = new List(ResourceProviderME.get("openBmk.head"), Choice.IMPLICIT);
		listVector = new Vector();

		if (Library.getInstance().fillBookmarkList(list, listVector) > 0) {
			list.addCommand(cmdOK);
			list.addCommand(cmdRemove);
		}

		list.addCommand(cmdCancel);
		list.setCommandListener(this);
		screenIdx = Common.SCREEN_OPEN_BMK;
		display.setCurrent(list);

		return list;
	}

	private void releaseForm() {
		form = null;
		textfield = null;
		gauge = null;
		setBookScreen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.microedition.lcdui.Displayable#paint(javax.microedition.lcdui.Graphics
	 * )
	 */
	public void paint(Graphics g) {
		Common.debug("EBookCanvas.paint(Graphics g)");
		view.draw(g, 0, 0);

		if (message != null) {
			int mx = 2;
			int my = 2;
			g.setFont(messageFont);

			int w = messageFont.stringWidth(message);
			int h = messageFont.getHeight();
			g.setColor(0xFFFFFF);
			g.fillRect(mx, my, w + 3, h + 3);
			g.setColor(0x000000);
			g.drawString(message, mx + 2, my + 2, Graphics.LEFT | Graphics.TOP);
			g.drawRect(mx, my, w + 3, h + 3);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.Displayable#keyPressed(int)
	 */
	public void keyPressed(int aKey) {
		Common.debug("Key pressed " + aKey);
		key(aKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.Displayable#keyRepeated(int)
	 */
	public void keyRepeated(int aKey) {
		Common.debug("Key repeated " + aKey);
		key(aKey);
	}

	/**
	 * Key actions handler
	 * 
	 * @param aKey
	 */
	protected synchronized void key(final int aKey) {
		final int action = getGameAction(aKey);
		Common.debug("Key: " + aKey + ", Action: " + action);

		if (!scrollThread.canRun()) {
			keyNormal(aKey, action);
		} else {
			keyAutoRun(aKey, action);
		}

		Common.debug("Key action finished.");
	}

	/**
	 * Key actions handler
	 * 
	 * @param aKey
	 */
	protected void keyNormal(final int aKey, final int action) {
		switch (action) {
		case UP:
			prevPage();

			break;

		case DOWN:
			nextPage();

			break;

		case RIGHT:
			nextLine();

			break;

		case LEFT:
			prevLine();

			break;

		default:

			switch (aKey) {
			case KEY_NUM1:
			case KEY_NUM2:
			case KEY_NUM3:
			case -13:
				prevPage();

				break;

			case KEY_NUM4:
				prevLine();

				break;

			case KEY_NUM7:
			case KEY_NUM8:
			case KEY_NUM9:
			case KEY_NUM0:
			case -14:
				nextPage();

				break;

			case KEY_NUM6:
				nextLine();

				break;

			case KEY_NUM5:
				scrollThread.setRun(true);

				break;
			}
		}
	}

	/**
	 * Key handlers for Autorun book reading.
	 * 
	 * @param aKey
	 */
	protected void keyAutoRun(final int aKey, final int action) {
		if ((action == UP) || (action == LEFT) || (aKey == KEY_NUM4)) {
			addScrollDelay(Common.AUTOSCROLL_STEP);
		} else if ((action == DOWN) || (action == RIGHT) || (aKey == KEY_NUM6)) {
			addScrollDelay(-Common.AUTOSCROLL_STEP);
		}

		if (aKey == KEY_NUM5) {
			messageOn(ResourceProviderME.get("i.stop"));
			scrollThread.setRun(false);
		} else {
			messageOn(ResourceProviderME.get("i.delay") + getScrollDelay());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.Displayable#pointerPressed(int, int)
	 */
	public void pointerPressed(int aX, int aY) {
		Common.debug("Pointer pressed (" + aX + "," + aY + ")");

		int seg = (aY * 4) / getHeight();
		synchronized (this) {

			if (scrollThread.canRun()) {
				switch (seg) {
				case 0:
				case 1:
					keyAutoRun(0, UP);
					break;
				case 2:
				case 3:
					keyAutoRun(0, DOWN);
					break;
				}
				return;
			} else {

				switch (seg) {
				case 0:
					prevPage();
					break;
				case 1:
					prevLine();
					break;
				case 2:
					nextLine();
					break;
				case 3:
					nextPage();
					break;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.microedition.lcdui.CommandListener#commandAction(javax.microedition
	 * .lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command aCmd, Displayable aDisp) {
		Common.debug("Command action " + aCmd);

		switch (screenIdx) {
		case Common.SCREEN_BOOK:
			commandActNormal(aCmd);

			break;

		case Common.SCREEN_BOOK_LIST:
			commandActBookList();

			break;

		case Common.SCREEN_POSITION:
			commandActPosition(aCmd);

			break;

		case Common.SCREEN_ADD_BMK:
			commandActAddBmk(aCmd);

			break;

		case Common.SCREEN_OPEN_BMK:
			commandActOpenBmk(aCmd);

			break;

		case Common.SCREEN_LIGHT:
		case Common.SCREEN_BOOK_INFO:
			releaseForm();

			break;

		case Common.SCREEN_CHAPTERS_LIST:
			commandActChapters(aCmd);

			break;

		default:
			break;
		}
	}

	private void commandActPosition(final Command aCmd) {
		Common.debug("commandAction() for setting position started");

		if ((form != null) && (aCmd == cmdOK)) {
			if (positionAsPerc) {
				view.setPercPosition(gauge.getValue());
			} else {
				try {
					int tmpPos = Integer.parseInt(textfield.getString());
					view.setPosition(tmpPos);
				} catch (NumberFormatException nfe) {
					if (Common.DEBUG) {
						Common
								.debugErr("commandActPosition: Can't convert '" + textfield.getString()
										+ "' to integer.");
					}
				}
			}
		}

		releaseForm();
	}

	private void commandActAddBmk(final Command aCmd) {
		Common.debug("commandAction() for adding bookmark started ");

		if ((form != null) && (aCmd == cmdOK)) {
			Library.getInstance().addBookmark(textfield.getString(), getViewPosition());
		}

		releaseForm();
	}

	private void commandActOpenBmk(final Command aCmd) {
		Common.debug("commandAction() for list of bookmarks started");

		if ((list != null) && (listVector != null)) {
			if (aCmd == cmdRemove) {
				Library.getInstance().removeBookmark(list.getSelectedIndex(), list, listVector);

				if (listVector.size() > 0) {
					list.setSelectedIndex(0, true);
				} else {
					list.removeCommand(cmdRemove);
					list.removeCommand(cmdOK);
				}
			} else {
				if (aCmd == cmdOK) {
					int tmpIdx = list.getSelectedIndex();

					if (tmpIdx >= 0) {
						Integer tmpPos = (Integer) listVector.elementAt(tmpIdx);
						view.setPosition(tmpPos.intValue());
					}
				}

				setBookScreen();
				list = null;
				listVector = null;
			}
		}
	}

	private void commandActBookList() {
		Common.debug("commandAction() for book list started");

		if (list != null) {
			setBook(list.getSelectedIndex(), -1L);
			list = null;
		}
	}

	private void commandActChapters(final Command aCmd) {
		Common.debug("commandAction() for chapters list started");

		if ((list != null) && (listVector != null)) {
			if (aCmd == cmdOK) {
				final Integer tmpPos = (Integer) listVector.elementAt(list.getSelectedIndex());
				view.setPosition(tmpPos.intValue());
			}

			setBookScreen();
			list = null;
			listVector = null;
		}
	}

	/**
	 *
	 */
	private void setBook(final int aIdx, final long aTime) {
		final Book book = Library.getInstance().getBook(aIdx);

		if (book.isChapterFile()) {
			addCommand(cmdChapters);
		} else {
			removeCommand(cmdChapters);
		}

		view.setBook(book);
		waitForIntroEnd(aTime);
		setBookScreen();
	}

	protected void setBookScreen() {
		display.setCurrent(this);
		screenIdx = Common.SCREEN_BOOK;
	}

	private void commandActNormal(final Command aCmd) {
		if (aCmd == cmdChapters) {
			createChaptersList();
		} else if (aCmd == cmdExit) {
			midlet.destroyApp(false);
		} else if (aCmd == cmdPosition) {
			createPositionForm();
		} else if (aCmd == cmdInfo) {
			createBookInfo(view.book.name, view.book.description);
		} else if (aCmd == cmdOptions) {
			display.setCurrent(new OptionsForm(this));
		} else if (aCmd == cmdAddBookmark) {
			createAddBmkForm();
		} else if (aCmd == cmdOpenBookmark) {
			createOpenBmkList();
		} else if (aCmd == cmdLibrary) {
			Library.getInstance().savePosition(getViewPosition());
			createBookList(0);
		}
	}

	/**
	 * Scrolls one line ahead in current book
	 */
	protected void nextLine() {
		try {
			pauseOn();
			view.fwdLine();
			messageOff();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Scrolls one page ahead in current book
	 */
	protected void nextPage() {
		try {
			pauseOn();
			view.fwdPage();
			messageOff();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Scrolls one line back in current book
	 */
	protected void prevLine() {
		try {
			pauseOn();
			view.bckLine();
			messageOff();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Scrolls one page back in current book
	 */
	protected void prevPage() {
		try {
			pauseOn();
			view.bckPage();
			messageOff();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays "Wait" message
	 */
	protected void pauseOn() {
		messageOn(ResourceProviderME.get("i.wait"));
	}

	/**
	 * Displays system message (e.g. Wait) display
	 */
	protected synchronized void messageOn(final String aMsg) {
		if (displayStatus) {
			message = aMsg;
			repaint();
			serviceRepaints();
		}
	}

	/**
	 * Disable system message (e.g. Wait) display
	 */
	protected synchronized void messageOff() {
		message = null;
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.Displayable#hideNotify()
	 */
	protected void hideNotify() {
		Common.debug("EBookCanvas.hideNotify()");
		scrollThread.setRun(false);
	}

	/**
	 * Sets pause between scrolling.
	 * 
	 * @param aDelay
	 */
	public static synchronized void setScrollDelay(int aDelay) {
		if (aDelay < Common.AUTOSCROLL_STEP) {
			aDelay = Common.AUTOSCROLL_STEP;
		}

		scrollDelay = aDelay;
	}

	public static synchronized void addScrollDelay(int aDelay) {
		setScrollDelay(getScrollDelay() + aDelay);
	}

	/**
	 * Returns position of view.
	 * 
	 * @see View#getPosition()
	 * @return position of view
	 */
	public int getViewPosition() {
		return view.getPosition();
	}

	/**
	 * @return Returns the scrollDelay.
	 */
	public static synchronized int getScrollDelay() {
		return scrollDelay;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
		// MIDP-2.0
		// setFullScreenMode(fullscreen);
		view.setDimension(getWidth(), getHeight());
	}

	public boolean isDisplayStatus() {
		return displayStatus;
	}

	public void setDisplayStatus(boolean displayStatus) {
		this.displayStatus = displayStatus;
	}

	public boolean isPositionAsPerc() {
		return positionAsPerc;
	}

	public void setPositionAsPerc(boolean positionAsPerc) {
		this.positionAsPerc = positionAsPerc;
	}

	// ~ Inner Classes
	// ==========================================================

	/**
	 * Thread which provides autoscroll functionality.
	 * 
	 * @author Josef Cacek
	 */
	class ScrollThread extends Thread {
		// ~ Instance fields
		// ====================================================

		boolean run = false;

		// ~ Methods
		// ============================================================

		public void run() {
			try {
				while (true) {
					if (canRun()) {
						if (!view.fwdLine()) {
							setRun(false);
						}

						messageOff();
					}

					Thread.sleep(getScrollDelay());
				}
			} catch (Exception e) {
			}
		}

		public synchronized void setRun(final boolean aRun) {
			run = aRun;

			if (run) {
				BackLight.getInstance().autoscroll();
			} else {
				BackLight.getInstance().normal();
				messageOff();
			}
		}

		public synchronized boolean canRun() {
			return run;
		}
	}
}
