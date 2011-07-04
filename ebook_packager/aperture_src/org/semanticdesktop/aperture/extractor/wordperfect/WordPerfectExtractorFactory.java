/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.wordperfect;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class WordPerfectExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/vnd.wordperfect");
		set.add("application/wordperf");
		set.add("application/wordperfect");
		set.add("application/wpd");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new WordPerfectExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
