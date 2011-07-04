/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.excel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class ExcelExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/vnd.ms-excel");
		set.add("application/msexcel");
		set.add("application/x-msexcel");
		set.add("application/x-ms-excel");
		set.add("application/x-excel");
		set.add("application/x-dos_ms_excel");
		set.add("application/xls");
		set.add("application/x-xls");
		set.add("zz-application/zz-winassoc-xls");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new ExcelExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
