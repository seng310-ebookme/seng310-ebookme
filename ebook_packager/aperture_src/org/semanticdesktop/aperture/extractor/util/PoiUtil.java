/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.semanticdesktop.aperture.extractor.DATA;

/**
 * Features Apache POI-specific utility methods for text and metadata extraction purposes.
 * 
 * <p>
 * Some methods use a buffer to be able to reset the InputStream to its start. The buffer size can be altered
 * by giving the "aperture.poiUtil.bufferSize" system property a value holding the number of bytes that the buffer may use.
 * 
 */
public class PoiUtil {

	private static final Logger LOGGER = Logger.getLogger(PoiUtil.class.getName());

	private static final String BUFFER_SIZE_PROPERTY = "aperture.poiUtil.bufferSize";
	
	/**
	 * Returns the SummaryInformation holding the document metadata from a POIFSFileSystem. Any POI-related or
	 * I/O Exceptions that may occur during this operation are ignored and 'null' is returned in those cases.
	 * 
	 * @param poiFileSystem The POI file system to obtain the metadata from.
	 * @return A populated SummaryInformation, or 'null' when the relevant document parts could not be
	 *         located.
	 */
	public static SummaryInformation getSummaryInformation(POIFSFileSystem poiFileSystem) {
		SummaryInformation summary = null;

		try {
			DocumentInputStream docInputStream = poiFileSystem
					.createDocumentInputStream(SummaryInformation.DEFAULT_STREAM_NAME);
			summary = (SummaryInformation) PropertySetFactory.create(docInputStream);
			docInputStream.close();
		}
		catch (Exception e) {
			// ignore
		}

		return summary;
	}

	/**
	 * Extract all metadata from an OLE document.
	 * 
	 * @param stream The stream containing the OLD document.
	 * @param resetStream Specified whether the stream should be buffered and reset. The buffer size can be
	 *            determined by the system property described in the class documentation.
	 * @param container The RDFContainer to store the metadata in.
	 * @throws IOException When resetting of the buffer resulted in an IOException.
	 */
	public static void extractMetadata(InputStream stream, boolean resetStream, Map container)
			throws IOException {
		if (resetStream) {
			int bufferSize = getBufferSize();
			if (!stream.markSupported()) {
				stream = new BufferedInputStream(stream, bufferSize);
			}
			stream.mark(bufferSize);
		}

		POIFSFileSystem fileSystem = new POIFSFileSystem(stream);
		extractMetadata(fileSystem, container);

		if (resetStream) {
			stream.reset();
		}
	}

	/**
	 * Extracts all metadata from the POIFSFileSystem's SummaryInformation and transforms it to RDF statements
	 * that are stored in the specified RDFContainer.
	 * 
	 * @param poiFileSystem The POI file system to obtain the metadata from.
	 * @param container The RDFContainer to store the created RDF statements in.
	 */
	public static void extractMetadata(POIFSFileSystem poiFileSystem, Map container) {
		SummaryInformation summary = getSummaryInformation(poiFileSystem);
		if (summary != null) {
			copyString(summary.getTitle(), DATA.title, container);
			copyString(summary.getSubject(), DATA.subject, container);
			copyString(summary.getComments(), DATA.description, container);
			copyString(summary.getApplicationName(), DATA.generator, container);
			copyString(summary.getAuthor(), DATA.creator, container);
			copyString(summary.getLastAuthor(), DATA.creator, container);

			copyDate(summary.getCreateDateTime(), DATA.created, container);
			copyDate(summary.getLastSaveDateTime(), DATA.date, container);

			int nrPages = summary.getPageCount();
			if (nrPages > 1) {
				// '0' means 'unknown' according to POI's API (<sigh>)
				// '1' is often erroneously returned and can thus not be trusted
				// higher values tend to be right (not seen a counter example yet) and are
				// therefore included
				container.put(DATA.pageCount, new Integer(nrPages));
			}

			container.put(DATA.keywords, summary.getKeywords());
		}
	}

	private static void copyString(String value, String property, Map container) {
		if (value != null) {
			value = value.trim();
			if (!value.equals("")) {
				// NOTE: "add", not "put", as some properties will be used multiple times!!
				container.put(property, value);
			}
		}
	}

	private static void copyDate(Date date, String property, Map container) {
		if (date != null) {
			container.put(property, date);
		}
	}

	/**
	 * Extract full-text and metadata from an MS Office document contained in the specified stream. A
	 * TextExtractor is specified to handle the specifics of full-text extraction for this particular MS
	 * Office document type.
	 */
	public static void extractAll(InputStream stream, TextExtractor textExtractor, Map container) {
		// mark the stream with a sufficiently large buffer so that, when POI chokes on a document, there is a
		// good chance we can reset to the beginning of the buffer and apply a StringExtractor
		int bufferSize = getBufferSize();
		if (!stream.markSupported()) {
			stream = new BufferedInputStream(stream, bufferSize);
		}
		stream.mark(bufferSize);

		// apply the POI-based extraction code
		String text = null;

		try {
			// try to create a POI file system
			POIFSFileSystem fileSystem = new POIFSFileSystem(stream);

			// try to extract the text, ignoring any exceptions as metadata extraction may still succeed
			try {
				if (textExtractor != null) {
					text = textExtractor.getText(fileSystem);
				}
			}
			catch (Exception e) {
				// ignore
			}

			PoiUtil.extractMetadata(fileSystem, container);
		}
		catch (IOException e1) {
			// ignore
		}

		// if text extraction was not successfull, try a StringExtractor as a fallback
		if (text == null) {
			if (textExtractor != null) {
				LOGGER.log(Level.INFO,
					"regular POI-based processing failed, falling back to heuristic string extraction");
			}

			try {
				stream.reset();
				StringExtractor extractor = new StringExtractor();
				text = extractor.extract(stream);
			}
			catch (IOException e) {
			}
		}

		// store the full-text, if any
		if (text != null) {
			// some problem with paragraph start (char 8)
			text = text.trim().replaceAll("[\\x01\\x08\\x0C]", " ");
			if (!text.equals("")) {
				container.put(DATA.fullText, text);
			}
		}
	}

	/**
	 * Returns the buffer size to use when buffering the contents of a document.
	 * 
	 * @return The specified buffer size to use, or the default size when the indicated system property is not
	 *         set or has an illegal value.
	 */
	private static int getBufferSize() {
		int result = -1;

		// see if the system property is set
		String property = System.getProperty(BUFFER_SIZE_PROPERTY);
		if (property != null && !property.equals("")) {
			try {
				result = Integer.parseInt(property);
			}
			catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "invalid buffer size: " + property);
			}
		}

		// overrule negative or unspecified values with a 4 MB buffer (yes, I know that's a lot, but we want
		// quality output, right?)
		if (result < 0) {
			result = 4 * 1024 * 1024;
		}

		return result;
	}

	/**
	 * A TextExtractor is a delegate that extracts the full-text from an MS Office document using a
	 * POIFSFileSystem. Implementations typically support specific MS Office document types, such as Word,
	 * Excel and PowerPoint.
	 */
	public static interface TextExtractor {

		/**
		 * Extract the full-text from an MS Office document.
		 * 
		 * @param fileSystem The POIFSFileSystem providing structural access to the MS Office document.
		 * @return A String containing the full-text of the document.
		 * @throws IOException whenever access to the POIFSFileSystem caused an IOException.
		 */
		public String getText(POIFSFileSystem fileSystem) throws IOException;
	}
}
