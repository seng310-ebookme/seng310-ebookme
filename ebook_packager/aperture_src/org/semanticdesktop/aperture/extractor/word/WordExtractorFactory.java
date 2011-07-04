/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.word;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class WordExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/vnd.ms-word");
		set.add("application/vnd.msword");
		set.add("application/msword");
		set.add("application/doc");
		set.add("application/winword");
		set.add("application/word");
		set.add("application/x-msw6");
		set.add("application/x-msword");
		set.add("zz-application/zz-winassoc-doc");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new WordExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
