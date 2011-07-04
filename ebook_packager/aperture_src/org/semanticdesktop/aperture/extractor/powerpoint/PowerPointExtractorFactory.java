/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.powerpoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class PowerPointExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/vnd-mspowerpoint");
		set.add("application/vnd-ms-powerpoint");
		set.add("application/vnd.mspowerpoint");
		set.add("application/vnd.ms-powerpoint");
		set.add("/vnd.ms-powerpoint");
		set.add("application/mspowerpoint");
		set.add("application/ms-powerpoint");
		set.add("application/mspowerpnt");
		set.add("application/powerpoint");
		set.add("application/x-powerpoint");
		set.add("application/x-mspowerpoint");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public PowerPointExtractor extractor;

	public Extractor get() {
		return new PowerPointExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
