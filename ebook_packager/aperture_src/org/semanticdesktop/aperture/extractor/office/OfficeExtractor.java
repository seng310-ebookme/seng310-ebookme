/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.office;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;

/**
 * An Extractor implementation that can be used to process MS Office documents when we don't know its specific
 * subtype (e.g. Word, Excel, PowerPoint) or when we don't have an Extractor for that particular subtype. This
 * extractor is capable of extracting all metadata but not the textual contents.
 */
public class OfficeExtractor implements Extractor {

	public void extract(InputStream stream, Charset charset, String mimeType, Map result)
			throws ExtractorException {
		// do not specify a TextExtractor, PoiUtil will fall-back on using a StringExtractor
		PoiUtil.extractAll(stream, null, result);
	}
}
