/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.presentations;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.extractor.util.WPStringExtractor;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.extractor.DATA;

/**
 * An Extractor implementation for Corel Presentations files. This extractor knows how to handle Presentations
 * files using the MS Office file structure as well as those using the WordPerfect structure.
 */
public class PresentationsExtractor implements Extractor {

	/**
	 * The MS Office magic number. Only Quattro files with this MS Office (i.e. files that use the OLE format)
	 * can be handled by this Extractor.
	 */
	private static final byte[] OFFICE_MAGIC_BYTES = new byte[] { (byte) 0xd0, (byte) 0xcf, (byte) 0x11,
			(byte) 0xe0, (byte) 0xa1, (byte) 0xb1, (byte) 0x1a, (byte) 0xe1 };

	private static final byte[] WORDPERFECT_MAGIC_BYTES = new byte[] { (byte) 0xff, (byte) 0x57, (byte) 0x50,
			(byte) 0x43 };

	public void extract(InputStream stream, Charset charset, String mimeType, Map result)
			throws ExtractorException {
		try {
			// determine which file structure is used
			int length = Math.max(OFFICE_MAGIC_BYTES.length, WORDPERFECT_MAGIC_BYTES.length);
			stream.mark(length);
			byte[] bytes = IOUtil.readBytes(stream, length);
			stream.reset();

			if (hasMagicNumber(bytes, OFFICE_MAGIC_BYTES)) {
				PoiUtil.extractAll(stream, null, result);
			}
			else if (hasMagicNumber(bytes, WORDPERFECT_MAGIC_BYTES)) {
				PresentationsWPStringExtractor extractor = new PresentationsWPStringExtractor();
				String text = extractor.extract(stream).trim();
				if (text.length() > 0) {
					result.put(DATA.fullText, text);
				}
			}
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}

	private boolean hasMagicNumber(byte[] bytes, byte[] magicNumber) {
		if (bytes.length < magicNumber.length) {
			return false;
		}

		for (int i = 0; i < magicNumber.length; i++) {
			if (bytes[i] != magicNumber[i]) {
				return false;
			}
		}

		return true;
	}

	private static class PresentationsWPStringExtractor extends WPStringExtractor {

		private static final String[] EXCLUDES = { "title of show", "subtitle of show", "subitem",
				"first item", "second item", "third item", "fourth item", "title of slide",
				"subtitle of slide", "north", "south", "east", "west",
				"this is the first item.  this is the second item.",
				"this is the third item.  this is the fourth item.", "click to edit master title style",
				"click to edit master subtitle", "click to edit master text styles", "footer area",
				"first level", "second level", "third level", "fourth level", "fifth level" };

		protected boolean isValidLine(String lineLowerCase) {
			for (int i = 0; i < EXCLUDES.length; i++) {
				if (lineLowerCase.equals(EXCLUDES[i])) {
					return false;
				}
			}

			return super.isValidLine(lineLowerCase);
		}
	}
}
