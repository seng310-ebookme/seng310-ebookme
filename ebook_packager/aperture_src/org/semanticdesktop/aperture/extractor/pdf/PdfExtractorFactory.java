/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class PdfExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/pdf");
		set.add("application/x-pdf");
		set.add("application/acrobat");
		set.add("application/vnd.pdf");
		set.add("text/pdf");
		set.add("text/x-pdf");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new PdfExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
