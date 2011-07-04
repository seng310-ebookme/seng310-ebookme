/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.works;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.semanticdesktop.aperture.extractor.DATA;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.StringExtractor;

/**
 * An Extractor implementation for MS Works files.
 * 
 * <p>
 * The current implementation only works well on Works 3.0 and 4.0 document and Works 4.0/2000 spreadsheets.
 * Other versions typically produce garbage, if anything at all.
 */
public class WorksExtractor implements Extractor {

	// Note: even though some MS Works documents have the MS Office magic number, it seems
	// that POI cannot extract anything useful for it. Hence the application of a StringExtractor
	// tuned for MS Works documents, rather than the use of PoiUtil.

	public void extract(InputStream stream, Charset charset, String mimeType, Map result)
			throws ExtractorException {
		try {
			WorksStringExtractor extractor = new WorksStringExtractor();
			String text = extractor.extract(stream).trim();
			if (text.length() > 0) {
				result.put(DATA.fullText, text);
			}
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}

	private static class WorksStringExtractor extends StringExtractor {

		private static final int MSWORKS_DOCUMENT = 0;

		private static final int MSWORKS_SPREADSHEET = 1;

		// indicates that this extractor has concluded that the end of the document has been reached
		private boolean endOfDocumentReached = false;

		// the number of trimmed lines already deemed 'okay'
		private int okayTrimmedLineCount = 0;

		// the detected type of msworks file
		private int worksType = MSWORKS_DOCUMENT;

		protected boolean isTextCharacter(int charNumber) {
			return super.isTextCharacter(charNumber) || charNumber == 0;
		}

		protected boolean isStartLine(String lineLowerCase) {
			if ("gtt".equals(lineLowerCase)) {
				return true;
			}

			return false;
		}

		protected boolean isValidLine(String lineLowerCase) {
			if ("microsoft works".equals(lineLowerCase) || "msworkswpdoc".equals(lineLowerCase)) {
				return false;
			}

			return super.isValidLine(lineLowerCase);
		}

		protected String postProcessLine(String line) {
			// if the last text line has already been passed, bail out always
			if (this.endOfDocumentReached) {
				return null;
			}

			// detect specific works format
			if (okayTrimmedLineCount < 4) {
				// Most works spreadsheets have a line like "N&T" or "VT&" as a header line
				if ((line.length() == 3 && line.endsWith("&T")) || line.startsWith("VT&")) {
					this.worksType = MSWORKS_SPREADSHEET;
					return null;
				}
			}

			line = super.postProcessLine(line);

			if (line == null) {
				return null;
			}

			// check whether control character is in the line
			if (this.worksType == MSWORKS_SPREADSHEET) {
				// some works spreadsheet specific excludes...
				if (line.startsWith("@")) {
					return null;
				}
				else if (line.length() < 6 && (line.indexOf('@') >= 0 || isAllUppercase(line))) {
					return null;
				}
			}
			else if (line.indexOf('\0') >= 0) {
				// check whether this is the last line...
				if (okayTrimmedLineCount > 5) {
					this.endOfDocumentReached = true;
				}
				return null;
			}

			// not at the end yet, let's check if it's okay
			boolean okay = line.length() > 2 && !Character.isWhitespace(line.charAt(1))
					&& line.charAt(1) != '\0';

			if (okay) {
				okayTrimmedLineCount++;

				if (worksType == MSWORKS_SPREADSHEET) {
					// need to remove '\0' chars, since these lines are not excluded
					// for spreadsheets (while they are for works documents)
					line = line.replace('\0', ' ');
				}

				return line;
			}

			return null;
		}

		private boolean isAllUppercase(String string) {
			for (int i = string.length(); i-- > 0;) {
				char c = string.charAt(i);
				if (Character.isLetter(c) && !Character.isUpperCase(c)) {
					return false;
				}
			}
			return true;
		}
	}
}
