/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.visio;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;

/**
 * An Extractor implementation for MS Visio documents. This implementation uses heuristic string extraction,
 * so results may be imperfect.
 */
public class VisioExtractor implements Extractor {

	public void extract(InputStream stream, Charset charset, String mimeType, Map result)
			throws ExtractorException {
		// do not specify a TextExtractor, PoiUtil will fall-back on using a StringExtractor
		PoiUtil.extractAll(stream, null, result);
	}
}
