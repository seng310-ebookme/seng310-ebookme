/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.presentations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class PresentationsExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/presentations");
		set.add("application/x-corelpresentations");
		set.add("application/x-shw-viewer");
		set.add("image/x-presentations");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new PresentationsExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
