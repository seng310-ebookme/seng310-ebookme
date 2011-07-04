package cz.cacek.ebook.packager;

import org.microemu.MIDletBridge;

import org.microemu.app.Common;
import org.microemu.app.Config;
import org.microemu.app.Main;

import org.microemu.app.util.IOUtils;

import org.microemu.device.impl.Rectangle;

import org.microemu.device.j2se.J2SEDevice;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.midlet.MIDletStateChangeException;


/**
 * Helper class for use MicroEmulator 2.0 in EBookME. It disables menubar and
 * System.exit() call in window-close listener.
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.5 $
 * @created $Date: 2010/01/08 13:58:51 $
 */
public class MicroEmulatorBridge
	extends Main
{
	private static final long serialVersionUID = 3141092212285498403L;
	protected static MicroEmulatorBridge app = null;

	/**
	 * Displays jad file in Microemulator
	 * @param aFile jad file path
	 */
	public static synchronized void run(final String aFile) {
		if (app == null) {
			//create singleton
			app = new MicroEmulatorBridge();
			app.init(aFile);
		} else {
			//use existing instance
			final File tmpFile = new File(aFile);
			Config.setRecentDirectory(
				"recentJadDirectory",
				tmpFile.getAbsoluteFile().getParent());
			Config.saveConfig();

			String url = IOUtils.getCanonicalFileURL(tmpFile);
			Common.openJadUrlSafe(url);
			app.setVisible(true);
		}
	}

	/**
	 * Inits MicroEmulator instance, disables menu and replaces window listeners
	 * @param aFile JAD file
	 */
	protected void init(String aFile) {
		// disable some features of MicroEmulator
		setJMenuBar(null);

		WindowListener[] tmpListeners = getWindowListeners();

		for (int i = 0; i < tmpListeners.length; i++) {
			removeWindowListener(tmpListeners[i]);
		}

		addWindowListener(
				new WindowAdapter() {
					public void windowClosing(WindowEvent ev) {
						Config.setWindow(
							"main",
							new Rectangle(
								MicroEmulatorBridge.this.getX(),
								MicroEmulatorBridge.this.getY(),
								MicroEmulatorBridge.this.getWidth(),
								MicroEmulatorBridge.this.getHeight()),
							true);
						Config.saveConfig();
						setVisible(false);
					}

					public void windowIconified(WindowEvent ev) {
						MIDletBridge.getMIDletAccess(
								MIDletBridge.getCurrentMIDlet()).pauseApp();
					}

					public void windowDeiconified(WindowEvent ev) {
						try {
							MIDletBridge.getMIDletAccess(
									MIDletBridge.getCurrentMIDlet()).startApp();
						} catch (MIDletStateChangeException ex) {
							System.err.println(ex);
						}
					}
				});

		final List params = new ArrayList();
		params.add(aFile);
		common.initParams(
			params,
			app.selectDevicePanel.getSelectedDeviceEntry(),
			J2SEDevice.class);
		updateDevice();
		common.initMIDlet(false);
		validate();
		setVisible(true);
	}
}
