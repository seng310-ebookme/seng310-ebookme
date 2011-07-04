/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.rtf;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class RtfExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("text/rtf");
		set.add("text/richtext");
		set.add("application/rtf");
		set.add("application/x-rtf");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new RtfExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
