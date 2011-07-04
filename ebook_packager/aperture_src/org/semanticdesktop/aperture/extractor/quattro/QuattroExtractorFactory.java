/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.quattro;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class QuattroExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/wb3");
		set.add("application/x-quattropro");
		set.add("application/x-QuattroPro-viewer");
		set.add("application/x-quattro-win");
		set.add("application/x-wb3");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new QuattroExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
