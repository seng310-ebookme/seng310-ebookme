package cz.cacek.ebook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;


/**
 * Main (MIDlet) class of Ebook J2ME application.
 *
 * @author Tomas Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.19 $
 * @created $Date: 2010/01/08 08:29:49 $
 */
public class EBookMIDlet
	extends MIDlet
{
	//~ Instance fields ========================================================

	// ~ Instance fields
	// ========================================================

	// TODO text find functionality

	/**
	 * <code>canvas</code> is basic controller for EBookME
	 */
	protected EBookCanvas canvas;

	//~ Methods ================================================================

	// ~ Methods
	// ================================================================

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	public void startApp() {
		if (canvas == null) {
			try {
				Display.getDisplay(this).setCurrent(new EBookIntro(this));

				final long tmpTime = System.currentTimeMillis();
				canvas = new EBookCanvas(this);

				try {
					load();
				} catch (Exception e) {
					Common.debugErr("load() - " + e.getMessage());
					Common.error(e);
				}

				canvas.begin(tmpTime);
			} catch (Exception e) {
				Common.debugErr("EBookMIDlet.startApp() - " + e.getMessage());
				Common.error(e);
			}
		} else {
			Common.debugErr("EBookMIDlet.startApp() - " + " skipped.");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	public void pauseApp() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	public void destroyApp(boolean unconditional) {
		Library.getInstance().savePosition(canvas.getViewPosition());

		try {
			save();
		} catch (Exception e) {
			Common.debugErr(
					"Saving config in destroyApp failed: " + e.getMessage());
			Common.error(e);
		}

		notifyDestroyed();
	}

	/**
	 * Loads configuration of this ebook application (fonts, colors, active
	 * book, ...)
	 *
	 * @throws Exception
	 */
	public void load()
		throws Exception
	{
		RecordStore tmpRS = null;

		try {
			tmpRS = RecordStore.openRecordStore(Common.STORE_CONFIG, true);

			if ((tmpRS != null) && (tmpRS.getNumRecords() > 0)) {
				byte[] bytes = tmpRS.getRecord(1);
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				DataInputStream dis = new DataInputStream(bais);
				loadConfig(dis);
				dis.close();
			}
		} finally {
			if (tmpRS != null) {
				tmpRS.closeRecordStore();
			}
		}
	}

	/**
	 * Loads configuration from given DataInput
	 *
	 * @param aDis
	 * @throws Exception
	 */
	public void loadConfig(DataInput aDis)
		throws Exception
	{
		canvas.view.setColorScheme(aDis.readInt());

		int face = aDis.readInt();
		int style = aDis.readInt();
		int size = aDis.readInt();
		Library.getInstance().setBookIdx(aDis.readInt());
		EBookCanvas.setScrollDelay(aDis.readInt());

		BackLight tmpBL = BackLight.getInstance();
		tmpBL.setLight(false, aDis.readInt());
		tmpBL.setLight(true, aDis.readInt());
		tmpBL.normal();
		canvas.view.setWrapSpaces(aDis.readBoolean());
		canvas.setFullscreen(aDis.readBoolean());
		canvas.view.setRotated(aDis.readBoolean());
		canvas.setDisplayStatus(aDis.readBoolean());
		canvas.setPositionAsPerc(aDis.readBoolean());
		// should be the last - because of redrawing :)
		canvas.view.setFont(Font.getFont(face, style, size));
	}

	/**
	 * Saves configuration of this ebook application (fonts, colors, active
	 * book, ...)
	 *
	 * @throws Exception
	 */
	public void save()
		throws Exception
	{
		RecordStore tmpRS = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			saveConfig(dos);
			dos.flush();

			byte[] bytes = baos.toByteArray();
			tmpRS = RecordStore.openRecordStore(Common.STORE_CONFIG, true);

			if (tmpRS.getNumRecords() == 0) {
				tmpRS.addRecord(bytes, 0, bytes.length);
			} else {
				tmpRS.setRecord(1, bytes, 0, bytes.length);
			}

			dos.close();
		} catch (Exception e) {
			throw e;
		} finally {
			if (tmpRS != null) {
				tmpRS.closeRecordStore();
			}
		}
	}

	/**
	 * Saves configuration to given DataOutput
	 *
	 * @param aDos
	 * @throws Exception
	 */
	public void saveConfig(DataOutput aDos)
		throws Exception
	{
		aDos.writeInt(canvas.view.getColorScheme());

		Font font = canvas.view.getFont();
		aDos.writeInt(font.getFace());
		aDos.writeInt(font.getStyle());
		aDos.writeInt(font.getSize());
		aDos.writeInt(Library.getInstance().getBookIdx());
		aDos.writeInt(EBookCanvas.getScrollDelay());
		aDos.writeInt(BackLight.getInstance().getLight(false));
		aDos.writeInt(BackLight.getInstance().getLight(true));
		aDos.writeBoolean(canvas.view.isWrapSpaces());
		aDos.writeBoolean(canvas.isFullscreen());
		aDos.writeBoolean(canvas.view.isRotated());
		aDos.writeBoolean(canvas.isDisplayStatus());
		aDos.writeBoolean(canvas.isPositionAsPerc());
	}
}
