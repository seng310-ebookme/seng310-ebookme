package cz.cacek.ebook;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;


/**
 * Form with options
 *
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.4 $
 * @created $Date: 2010/01/08 08:29:49 $
 */
public class OptionsForm
	extends Form
	implements CommandListener
{
	//~ Static fields/initializers =============================================

	// ~ Static fields/initializers
	// =============================================
	final static BackLight BL = BackLight.getInstance();

	//~ Instance fields ========================================================

	// ~ Instance fields
	// ========================================================
	final ChoiceGroup choices =
		new ChoiceGroup(ResourceProviderME.get("opt.choices"), Choice.MULTIPLE);
	final Gauge backLight =
		new Gauge(ResourceProviderME.get("opt.backlig"), true, 100, 0);
	final Gauge backLightAS =
		new Gauge(ResourceProviderME.get("opt.backligas"), true, 100, 0);
	final ChoiceGroup colors =
		new ChoiceGroup(ResourceProviderME.get("opt.colors"), Choice.EXCLUSIVE);
	final ChoiceGroup fontSize =
		new ChoiceGroup(ResourceProviderME.get("opt.fontsz"), Choice.EXCLUSIVE);
	final ChoiceGroup fontStyle =
		new ChoiceGroup(ResourceProviderME.get("opt.fontst"), Choice.EXCLUSIVE);
	final ChoiceGroup fontFace =
		new ChoiceGroup(ResourceProviderME.get("opt.fontfc"), Choice.EXCLUSIVE);
	private Command cmdOK =
		new Command(ResourceProviderME.get("btn.ok"), Command.OK, 1);
	private Command cmdCancel =
		new Command(ResourceProviderME.get("btn.cancel"), Command.CANCEL, 1);
	private EBookCanvas canvas;

	//~ Constructors ===========================================================

	// ~ Constructors
	// ===========================================================
	public OptionsForm(final EBookCanvas aCanvas) {
		super(ResourceProviderME.get("opt.head"));
		canvas = aCanvas;
		setCommandListener(this);

		choices.append(ResourceProviderME.get("opt.wrapword"), null);
		// choices.append(ResourceProviderME.get("opt.fullscreen"), null);
		choices.append(ResourceProviderME.get("opt.rotate"), null);
		choices.append(ResourceProviderME.get("opt.status"), null);
		choices.append(ResourceProviderME.get("opt.posperc"), null);

		int i = 0;
		choices.setSelectedIndex(i++, canvas.view.isWrapSpaces());
		// choices.setSelectedIndex(i++, canvas.isFullscreen());
		choices.setSelectedIndex(i++, canvas.view.isRotated());
		choices.setSelectedIndex(i++, canvas.isDisplayStatus());
		choices.setSelectedIndex(i++, canvas.isPositionAsPerc());

		backLight.setValue(BL.getLight(false));
		backLightAS.setValue(BL.getLight(true));

		final String tmpWhite = ResourceProviderME.get("cl.white");
		final String tmpBlack = ResourceProviderME.get("cl.black");
		final String tmpBlue = ResourceProviderME.get("cl.blue");
		addColors(tmpWhite, tmpBlack);
		addColors(tmpWhite, tmpBlue);
		addColors(tmpBlack, ResourceProviderME.get("cl.green"));
		addColors(tmpBlue, tmpWhite);
		colors.setSelectedIndex(canvas.view.getColorScheme(), true);

		int tmpIdx = 0;
		final Font tmpFont = canvas.view.font;

		fontSize.append(ResourceProviderME.get("f.small"), null);
		fontSize.append(ResourceProviderME.get("f.medium"), null);
		fontSize.append(ResourceProviderME.get("f.large"), null);

		switch (tmpFont.getSize()) {
			case Font.SIZE_LARGE:
				tmpIdx++;

			case Font.SIZE_MEDIUM:
				tmpIdx++;

			default:
				break;
		}

		fontSize.setSelectedIndex(tmpIdx, true);
		append(fontSize);

		fontStyle.append(ResourceProviderME.get("f.normal"), null);
		fontStyle.append(ResourceProviderME.get("f.bold"), null);
		fontStyle.setSelectedIndex(
				(tmpFont.getStyle() == Font.STYLE_PLAIN) ? 0 : 1,
				true);
		append(fontStyle);

		fontFace.append(ResourceProviderME.get("f.proportional"), null);
		fontFace.append(ResourceProviderME.get("f.monospace"), null);
		fontFace.setSelectedIndex(
				(tmpFont.getFace() == Font.FACE_PROPORTIONAL) ? 0 : 1,
				true);
		append(fontFace);

		append(choices);

		if (BackLight.isLight()) {
			append(backLight);
			append(backLightAS);
		}

		append(colors);

		addCommand(cmdOK);
		addCommand(cmdCancel);
	}

	//~ Methods ================================================================

	/**
	 * Adds color scheme to the list.
	 *
	 * @param aBgName
	 *            name of background color
	 * @param aFgName
	 *            name of foreground color
	 */
	private void addColors(String aBgName, String aFgName) {
		colors.append(aBgName + "/" + aFgName, null);
	}

	/**
	 * @param aCmd
	 * @param aDisp
	 */
	public void commandAction(Command aCmd, Displayable aDisp) {
		Common.debug("Options - Command action " + aCmd);

		if (aCmd == cmdCancel) {
			canvas.setBookScreen();
		} else if (aCmd == cmdOK) {
			int i = 0;
			canvas.view.setWrapSpaces(choices.isSelected(i++));
			// canvas.setFullscreen(choices.isSelected(i++));
			canvas.view.setRotated(choices.isSelected(i++));
			canvas.setDisplayStatus(choices.isSelected(i++));
			canvas.setPositionAsPerc(choices.isSelected(i++));

			BL.setLight(false, backLight.getValue());
			BL.setLight(true, backLightAS.getValue());

			canvas.view.setColorScheme(colors.getSelectedIndex());

			int tmpSize;

			switch (fontSize.getSelectedIndex()) {
				case 1:
					tmpSize = Font.SIZE_MEDIUM;

					break;

				case 2:
					tmpSize = Font.SIZE_LARGE;

					break;

				default:
					tmpSize = Font.SIZE_SMALL;

					break;
			}

			int tmpStyle = Font.STYLE_PLAIN;

			if (fontStyle.getSelectedIndex() == 1) {
				tmpStyle = Font.STYLE_BOLD;
			}

			int tmpFace = Font.FACE_PROPORTIONAL;

			if (fontFace.getSelectedIndex() == 1) {
				tmpFace = Font.FACE_MONOSPACE;
			}

			final Font font = Font.getFont(tmpFace, tmpStyle, tmpSize);

			try {
				canvas.view.setFont(font);
			} catch (Exception e) {
				Common.error(e);
			}

			canvas.setBookScreen();
		}
	}
}
