/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.visio;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class VisioExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/visio");
		set.add("application/x-visio");
		set.add("application/vnd.visio");
		set.add("application/visio.drawing");
		set.add("application/vsd");
		set.add("application/x-vsd");
		set.add("image/x-vsd");
		set.add("zz-application/zz-winassoc-vsd");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new VisioExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
