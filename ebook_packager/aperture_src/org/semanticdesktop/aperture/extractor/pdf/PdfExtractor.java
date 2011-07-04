/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;
import org.semanticdesktop.aperture.extractor.DATA;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;

/**
 * Extracts full-text and metadata from Adobe Acrobat (PDF) files.
 */
public class PdfExtractor implements Extractor {

	private static final Logger LOGGER = Logger.getLogger(PdfExtractor.class.getName());

	public void extract(InputStream stream, Charset charset, String mimeType, Map result) throws ExtractorException {
		// setup a PDDocument
		PDDocument document = null;

		try {
			try {
				PDFParser parser = new PDFParser(stream);
				parser.parse();
				document = parser.getPDDocument();
			} catch (IOException e) {
				throw new ExtractorException(e);
			}

			// decrypt and extract info from this document
			processDocument(document, result);
		} finally {
			if (document != null) {
				// close the document
				try {
					document.close();
				} catch (IOException e) {
					throw new ExtractorException(e);
				}
			}
		}
	}

	private void processDocument(PDDocument document, Map result) throws ExtractorException {
		// try to decrypt it, if necessary
		if (document.isEncrypted()) {
			try {
				if (document.isOwnerPassword("") || document.isUserPassword("")) {
					document.decrypt("");
					LOGGER.log(Level.INFO, "Decryption succeeded");
				}
			} catch (CryptographyException e) {
				throw new ExtractorException(e);
			} catch (IOException e) {
				throw new ExtractorException(e);
			} catch (InvalidPasswordException e) {
				LOGGER.log(Level.INFO, "Decryption failed", e);
			}
		}

		// extract the full-text
		try {
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(document);
			if (text != null) {
				result.put(DATA.fullText, text);
			}
		} catch (IOException e) {
			// exception ends here, maybe we can still extract metadata
			LOGGER.log(Level.WARNING, "IOException while extracting full-text", e);
		}

		// extract the metadata
		// note: we map both pdf:creator and pdf:producer to aperture:generator
		// note2: every call to PDFBox is wrapper in a separate try-catch, as an
		// error
		// in one of these calls doesn't automatically mean that the others
		// won't work well
		PDDocumentInformation metadata = document.getDocumentInformation();

		try {
			addStringMetadata(DATA.creator, metadata.getAuthor(), result);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while extracting author", e);
		}

		try {
			addStringMetadata(DATA.title, metadata.getTitle(), result);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while extracting title", e);
		}

		try {
			addStringMetadata(DATA.subject, metadata.getSubject(), result);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while extracting subject", e);
		}

		try {
			addStringMetadata(DATA.generator, metadata.getCreator(), result);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while extracting creator", e);
		}

		try {
			addStringMetadata(DATA.generator, metadata.getProducer(), result);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while extracting producer", e);
		}

		try {
			addCalendarMetadata(DATA.created, metadata.getCreationDate(), result);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while extracting creation date", e);
		}

		try {
			addCalendarMetadata(DATA.date, metadata.getModificationDate(), result);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while extracting modification date", e);
		}

		try {
			int nrPages = document.getNumberOfPages();
			if (nrPages >= 0) {
				result.put(DATA.pageCount, new Integer(nrPages));
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while extracting number of pages", e);
		}

		try {
			String keywords = metadata.getKeywords();
			result.put(DATA.keywords, keywords);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while extracting keywords", e);
		}
	}

	private void addStringMetadata(String property, String value, Map result) {
		if (value != null) {
			result.put(property, value);
		}
	}

	private void addCalendarMetadata(String property, Calendar value, Map result) {
		if (value != null) {
			result.put(property, value);
		}
	}
}
