/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.works;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class WorksExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/vnd.ms-works");
		set.add("application/x-msworks");
		set.add("application/x-msworks-wp");
		set.add("zz-application/zz-winassoc-wps");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new WorksExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
