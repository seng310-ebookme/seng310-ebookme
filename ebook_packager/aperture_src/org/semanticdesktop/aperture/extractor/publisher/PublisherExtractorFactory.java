/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.publisher;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class PublisherExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES = Collections.singleton("application/x-mspublisher");

	public Extractor get() {
		return new PublisherExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
