/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.wordperfect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.semanticdesktop.aperture.extractor.DATA;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.WPStringExtractor;

/**
 * An Extractor implementation for WordPerfect documents.
 * 
 * <p>
 * This implementation uses heuristic string extraction algorithms, tuned for WordPerfect files but without
 * any intrinsic knowledge of the WordPerfect file format(s). Consequently, the extracted full-text may be
 * imperfect, e.g. contain some noise that's not part of the document text. Also, the document metadata is not
 * extracted.
 * 
 * <p>
 * The current status of this implementation is that the complete full-text is extracted from WordPerfect
 * documents from version 4.2 up to WordPerfect X3 (tested with 4.2, 5.0, 5.1/5.2 and X3, all created using
 * WordPerfect X3), except for the 5.1/5.2 Far East format for which our test did not return any text at all.
 * This is probably due to encoding issues. These tests showed that for WordPerfect 5.0 and 5.1 the document
 * metadata also ends up at the start of the extracted full-text.
 */
public class WordPerfectExtractor implements Extractor {

	public void extract(InputStream stream, Charset charset, String mimeType, Map result)
			throws ExtractorException {
		try {
			WPStringExtractor extractor = new WPStringExtractor();
			String text = extractor.extract(stream).trim();
			if (text.length() > 0) {
				result.put(DATA.fullText, text);
			}
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}
}
