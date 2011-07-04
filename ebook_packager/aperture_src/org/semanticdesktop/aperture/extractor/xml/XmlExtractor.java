/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.xml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.semanticdesktop.aperture.extractor.DATA;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Extracts text inside elements and attribute values from XML documents.
 */
public class XmlExtractor implements Extractor {

	public void extract(InputStream stream, Charset charset, String mimeType, Map result)
			throws ExtractorException {
		try {
			// setup a parser
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			XmlTextExtractor listener = new XmlTextExtractor();

			// parse the stream
			parser.parse(stream, listener);

			// store the extracted text
			String text = listener.getText();
			if (!text.equals("")) {
				result.put(DATA.fullText, text);
			}
		}
		catch (ParserConfigurationException e) {
			throw new ExtractorException(e);
		}
		catch (SAXException e) {
			throw new ExtractorException(e);
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}

	private static class XmlTextExtractor extends DefaultHandler {

		private StringBuffer buffer = new StringBuffer(64 * 1024);

		public String getText() {
			return buffer.toString().trim();
		}

		public void startElement(String namespaceURI, String localName, String qName, Attributes attributes)
				throws SAXException {
			int nrAtts = attributes.getLength();
			for (int i = 0; i < nrAtts; i++) {
				String value = attributes.getValue(i);
				if (value != null && value.length() > 0 && !isGarbage(value)) {
					buffer.append(value);
					buffer.append(' ');
				}
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			buffer.append(ch, start, length);
			buffer.append(' ');
		}

		/**
		 * Returns true if the given attribute value is considered to be garbage.
		 */
		private boolean isGarbage(String attsValue) {
			return "true".equalsIgnoreCase(attsValue) || "false".equalsIgnoreCase(attsValue)
					|| "yes".equalsIgnoreCase(attsValue) || "no".equalsIgnoreCase(attsValue);
		}
	}
}
