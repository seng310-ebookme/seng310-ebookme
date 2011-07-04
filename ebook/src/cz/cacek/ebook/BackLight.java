package cz.cacek.ebook;


/**
 * Class providing switching functionality for LCD backlight in Siemens and
 * Nokia mobiles
 *
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.10 $
 * @created $Date: 2010/01/08 08:29:49 $
 */
public class BackLight {
	//~ Static fields/initializers =============================================

	// ~ Static fields/initializers
	// =============================================
	private static BackLight instance;

	//~ Instance fields ========================================================

	// ~ Instance fields
	// ========================================================

	/**
	 * State of light
	 */
	private int backLight;
	private int autoLight;

	//~ Constructors ===========================================================

	// ~ Constructors
	// ===========================================================
	private BackLight() {
		backLight = Common.BACKLIGHT;
		autoLight = Common.BACKLIGHT_AS;
	}

	//~ Methods ================================================================

	// ~ Methods
	// ================================================================

	/**
	 * Sets the backlight directly (without setting normal or autoScroll
	 * preferences)
	 */
	protected void setLigthInternal(int aLight) {
		try {
			Class.forName("com.siemens.mp.game.Light");

			if (aLight > 0) {
				com.siemens.mp.game.Light.setLightOn();
			} else {
				com.siemens.mp.game.Light.setLightOff();
			}
		} catch (Throwable e0) {
			try {
				Class.forName("com.nokia.mid.ui.DeviceControl");
				com.nokia.mid.ui.DeviceControl.setLights(0, aLight);
			} catch (Throwable e1) {
			}
		}
	}

	/**
	 * Sets normal mode for backlight
	 */
	public void normal() {
		setLigthInternal(backLight);
	}

	/**
	 * Sets autoscroll mode for backlight
	 */
	public void autoscroll() {
		setLigthInternal(autoLight);
	}

	/**
	 * Returns intensity of light. Intensity has sense only in Nokia mobiles
	 * (siemens has only 2 states 0=disabled, anything else=enabled).
	 *
	 * @return intensity of light
	 */
	public int getLight(boolean anAutoScroll) {
		return anAutoScroll ? autoLight : backLight;
	}

	/**
	 * Sets backlight
	 *
	 * @param aLight
	 */
	public void setLight(boolean aAutoScroll, int aLight) {
		if (aAutoScroll) {
			autoLight = aLight;
		} else {
			backLight = aLight;
		}
	}

	/**
	 * Tests if back-light functionality is available
	 */
	public static boolean isLight() {
		try {
			Class.forName("com.siemens.mp.game.Light");

			return true;
		} catch (Throwable e0) {
			try {
				Class.forName("com.nokia.mid.ui.DeviceControl");

				return true;
			} catch (Throwable e1) {
			}
		}

		return false;
	}

	/**
	 * Returns singleton of this class
	 *
	 * @return instance of BackLight
	 */
	public static BackLight getInstance() {
		if (instance == null) {
			instance = new BackLight();
		}

		return instance;
	}
}
