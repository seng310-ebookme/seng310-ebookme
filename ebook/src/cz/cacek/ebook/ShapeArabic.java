package cz.cacek.ebook;

import java.util.Hashtable;


/**
 * Arabic shaping implementation (for R-L texts)
 * @author Mohammed Yousif
 * @author Josef Cacek
 * @author $Author: kwart $
 * @version $Revision: 1.3 $
 * @created $Date: 2008/06/03 14:20:20 $
 */
public class ShapeArabic {
	//~ Static fields/initializers =============================================

	private static final char NIL = '\000';
	protected static final Hashtable charsMap = new Hashtable();

	static {
		charsMap.put("\u0621", new CharRep('\uFE80', NIL, NIL, NIL));
		charsMap.put("\u0622", new CharRep('\uFE81', NIL, NIL, '\uFE82'));
		charsMap.put("\u0623", new CharRep('\uFE83', NIL, NIL, '\uFE84'));
		charsMap.put("\u0624", new CharRep('\uFE85', NIL, NIL, '\uFE86'));
		charsMap.put("\u0625", new CharRep('\uFE87', NIL, NIL, '\uFE88'));
		charsMap.put("\u0626",
			new CharRep('\uFE89', '\uFE8B', '\uFE8C', '\uFE8A'));
		charsMap.put("\u0627", new CharRep('\uFE8D', NIL, NIL, '\uFE8E'));
		charsMap.put("\u0628",
			new CharRep('\uFE8F', '\uFE91', '\uFE92', '\uFE90'));
		charsMap.put("\u0629", new CharRep('\uFE93', NIL, NIL, '\uFE94'));
		charsMap.put("\u062A",
			new CharRep('\uFE95', '\uFE97', '\uFE98', '\uFE86'));
		charsMap.put("\u062B",
			new CharRep('\uFE99', '\uFE9B', '\uFE9C', '\uFE9A')); // THEH
		charsMap.put("\u062C",
			new CharRep('\uFE9D', '\uFE9F', '\uFEA0', '\uFE9E')); // JEEM
		charsMap.put("\u062D",
			new CharRep('\uFEA1', '\uFEA3', '\uFEA4', '\uFEA2')); // HAH
		charsMap.put("\u062E",
			new CharRep('\uFEA5', '\uFEA7', '\uFEA8', '\uFEA6')); // KHAH
		charsMap.put("\u062F", new CharRep('\uFEA9', NIL, NIL, '\uFEAA')); // DAL
		charsMap.put("\u0630", new CharRep('\uFEAB', NIL, NIL, '\uFEAC')); // THAL
		charsMap.put("\u0631", new CharRep('\uFEAD', NIL, NIL, '\uFEAE')); // REH
		charsMap.put("\u0632", new CharRep('\uFEAF', NIL, NIL, '\uFEB0')); // ZAIN
		charsMap.put("\u0633",
			new CharRep('\uFEB1', '\uFEB3', '\uFEB4', '\uFEB2')); // SEEN
		charsMap.put("\u0634",
			new CharRep('\uFEB5', '\uFEB7', '\uFEB8', '\uFEB6')); // SHEEN
		charsMap.put("\u0635",
			new CharRep('\uFEB9', '\uFEBB', '\uFEBC', '\uFEBA')); // SAD
		charsMap.put("\u0636",
			new CharRep('\uFEBD', '\uFEBF', '\uFEC0', '\uFEBE')); // DAD
		charsMap.put("\u0637",
			new CharRep('\uFEC1', '\uFEC3', '\uFEC4', '\uFEC2')); // TAH
		charsMap.put("\u0638",
			new CharRep('\uFEC5', '\uFEC7', '\uFEC8', '\uFEC6')); // ZAH
		charsMap.put("\u0639",
			new CharRep('\uFEC9', '\uFECB', '\uFECC', '\uFECA')); // AIN
		charsMap.put("\u063A",
			new CharRep('\uFECD', '\uFECF', '\uFED0', '\uFECE')); // GHAIN
		charsMap.put("\u0640", new CharRep('\u0640', NIL, NIL, NIL)); // TATWEEL
		charsMap.put("\u0641",
			new CharRep('\uFED1', '\uFED3', '\uFED4', '\uFED2')); // FEH
		charsMap.put("\u0642",
			new CharRep('\uFED5', '\uFED7', '\uFED8', '\uFED6')); // QAF
		charsMap.put("\u0643",
			new CharRep('\uFED9', '\uFEDB', '\uFEDC', '\uFEDA')); // KAF
		charsMap.put("\u0644",
			new CharRep('\uFEDD', '\uFEDF', '\uFEE0', '\uFEDE')); // LAM
		charsMap.put("\u0645",
			new CharRep('\uFEE1', '\uFEE3', '\uFEE4', '\uFEE2')); // MEEM
		charsMap.put("\u0646",
			new CharRep('\uFEE5', '\uFEE7', '\uFEE8', '\uFEE6')); // NOON
		charsMap.put("\u0647",
			new CharRep('\uFEE9', '\uFEEB', '\uFEEC', '\uFEEA')); // HEH
		charsMap.put("\u0648", new CharRep('\uFEED', NIL, NIL, '\uFEEE')); // WAW
		charsMap.put("\u0649",
			new CharRep('\uFEEF', '\uFBE8', '\uFBE9', '\uFEF0')); // ALEF_MAKSURA
		charsMap.put("\u064A",
			new CharRep('\uFEF1', '\uFEF3', '\uFEF4', '\uFEF2')); // YEH
																  // Exceptions

		charsMap.put("\u0644\u0622", new CharRep('\uFEF5', NIL, NIL, '\uFEF6')); // LAM_ALEF_MADDA
		charsMap.put("\u0644\u0623", new CharRep('\uFEF7', NIL, NIL, '\uFEF8')); // LAM_ALEF_HAMZA_ABOVE
		charsMap.put("\u0644\u0625", new CharRep('\uFEF9', NIL, NIL, '\uFEFA')); // LAM_ALEF_HAMZA_BELOW
		charsMap.put("\u0644\u0627", new CharRep('\uFEFB', NIL, NIL, '\uFEFC')); // LAM_ALEF
	}

	//~ Methods ================================================================

	/**
	 * Proceed the shaping on the given string
	 * @param str a string
	 * @return shaped string
	 */
	public static String render(final String str) {
		StringBuffer buffer = new StringBuffer(0);
		CharRep crep = null;

		for (int i = 0; i < str.length(); i++) {
			char current = str.charAt(i);

			if (charsMap.containsKey(String.valueOf(current))) {
				char prev = NIL;
				char next = NIL;

				if ((i == 0)
						|| !charsMap.containsKey(
								String.valueOf(prev = str.charAt(i - 1)))
						|| (!((crep = (CharRep) charsMap.get(
								String.valueOf(prev))).mInitial != NIL)
						&& !(crep.mMedial != NIL))) {
					prev = NIL;
				}

				if ((i == (str.length() - 1))
						|| !charsMap.containsKey(
								String.valueOf(next = str.charAt(i + 1)))
						|| (!((crep = (CharRep) charsMap.get(
								String.valueOf(next))).mMedial != NIL)
						&& !((crep = (CharRep) charsMap.get(
								String.valueOf(next))).mFinal != NIL)
						&& (next != '\u0640'))) {
					next = NIL;
				}

				// Combinations
				if ((current == '\u0644') && (next != NIL)
						&& ((next == '\u0622') || (next == '\u0623')
						|| (next == '\u0625') || (next == '\u0627'))) {
					String index =
						String.valueOf(current) + String.valueOf(next);
					crep = (CharRep) charsMap.get(index);

					if (crep == null) {
						Common.debugErr(
								"CharRep is null for index: "
								+ toHexString(index));
					} else {
						if (prev != NIL) {
							buffer.append(crep.mFinal);
						} else {
							buffer.append(crep.mIsolated);
						}
					}

					i++;

					continue;
				}

				crep = (CharRep) charsMap.get(String.valueOf(current));

				if (crep == null) {
					Common.debugErr(
							"CharRep is null for current: "
							+ toHexString(current));
				} else {
					// Medial
					if ((prev != NIL) && (next != NIL) && (crep.mMedial != NIL)) {
						buffer.append(crep.mMedial);

						continue;

						// Final
					} else if ((prev != NIL) && (crep.mFinal != NIL)) {
						buffer.append(crep.mFinal);

						continue;

						// Initial
					} else if ((next != NIL) && (crep.mInitial != NIL)) {
						buffer.append(crep.mInitial);

						continue;
					}

					// Isolated
					buffer.append(crep.mIsolated);
				}
			} else {
				buffer.append(current);
			}
		}

		return buffer.reverse().toString();
	}

	/**
	 * Helper for displaying strings as escaped unicodes
	 * @param s string
	 * @return string
	 */
	public static String toHexString(final String s) {
		final StringBuffer tmpResult = new StringBuffer();

		for (int i = 0; i < s.length(); i++) {
			tmpResult.append(toHexString(s.charAt(i)));
		}

		return tmpResult.toString();
	}

	/**
	 * Helper for displaying char as escaped unicode
	 * @param data char
	 * @return string
	 */
	public static String toHexString(final char data) {
		final StringBuffer tmpResult = new StringBuffer("\\u");
		final String hex = Integer.toHexString(data);

		switch (hex.length()) {
			case 1:
				tmpResult.append('0');

			case 2:
				tmpResult.append('0');

			case 3:
				tmpResult.append('0');

			case 4:
				tmpResult.append(hex);

				break;

			default:
				throw new RuntimeException(hex
					+ " is tool long to be a Character");
		}

		return tmpResult.toString();
	}

	//~ Inner Classes ==========================================================

	protected static class CharRep {
		//~ Instance fields ====================================================

		private char mIsolated;
		private char mInitial;
		private char mMedial;
		private char mFinal;

		//~ Constructors =======================================================

		CharRep(char s, char i, char m, char f) {
			mIsolated = s;
			mInitial = i;
			mMedial = m;
			mFinal = f;
		}
	}
}
