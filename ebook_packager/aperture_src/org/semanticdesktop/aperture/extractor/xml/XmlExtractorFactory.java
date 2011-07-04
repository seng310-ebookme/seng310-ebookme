/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.xml;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class XmlExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("text/xml");
		set.add("application/xml");
		set.add("application/x-xml");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new XmlExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
