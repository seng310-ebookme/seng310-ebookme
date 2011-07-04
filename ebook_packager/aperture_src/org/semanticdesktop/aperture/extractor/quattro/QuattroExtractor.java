/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.quattro;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.util.IOUtil;

/**
 * An Extractor implementation for Quattro Spreadsheets. This Extractor only knows how to handle recent
 * Quattro Formats, as used by Quattro Pro 7 and Quattro Pro X3, as these have a structure similar to MS
 * Office documents. Older versions are not supported. Furthermore, text extraction is heuristic, so expect
 * some noise in the output.
 */
public class QuattroExtractor implements Extractor {

	/**
	 * The MS Office magic number. Only Quattro files with this MS Office (i.e. files that use the OLE format)
	 * can be handled by this Extractor.
	 */
	private static final byte[] MAGIC_BYTES = new byte[] { (byte) 0xd0, (byte) 0xcf, (byte) 0x11,
			(byte) 0xe0, (byte) 0xa1, (byte) 0xb1, (byte) 0x1a, (byte) 0xe1 };

	public void extract(InputStream stream, Charset charset, String mimeType, Map result)
			throws ExtractorException {
		// Verify that this file has the MS Office magic number, else the application of PoiUtil will not make
		// sense. We double-check the outcome of the MimeTypeIdentifier as the quattro MIME type is also used
		// for older, incompatible formats.
		try {
			if (hasMSOfficeMagicNumber(stream)) {
				PoiUtil.extractAll(stream, null, result);
			}
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}

	private boolean hasMSOfficeMagicNumber(InputStream stream) throws IOException {
		int length = MAGIC_BYTES.length;
		stream.mark(length);
		byte[] bytes = IOUtil.readBytes(stream, length);
		stream.reset();
		return Arrays.equals(bytes, MAGIC_BYTES);
	}
}
