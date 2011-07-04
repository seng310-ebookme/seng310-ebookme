/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * An XML parser that generates "simple" SAX-like events from a limited subset
 * of XML documents. The SimpleSAXParser can parse simple XML documents; it
 * doesn't support processing instructions or elements that contain both
 * sub-element and character data; character data is only supported in the
 * "leaves" of the XML element tree.
 * 
 * <h3>Example:</h3>
 * <p>
 * Parsing the following XML:
 * 
 * <pre>
 * &lt;?xml version='1.0' encoding='UTF-8'?&gt;
 * &lt;xml-doc&gt;
 *   &lt;foo a=&quot;1&quot; b=&quot;2&amp;amp;3&quot;/&gt;
 *   &lt;bar&gt;Hello World!&lt;/bar&gt;
 * &lt;/xml-doc&gt;
 * </pre>
 * 
 * <p>
 * will result in the following method calls to the SimpleSAXListener:
 * 
 * <pre>
 * startDocument()
 * startTag(&quot;xml-doc&quot;, emptyMap, &quot;&quot;)
 *
 * startTag(&quot;foo&quot;, a_b_Map, &quot;&quot;)
 * endTag(&quot;foo&quot;)
 *
 * startTag(&quot;bar&quot;, emptyMap, &quot;Hello World!&quot;)
 * endTag(&quot;bar&quot;)
 *
 * endTag(&quot;xml-doc&quot;)
 * endDocument()
 * </pre>
 */
public class SimpleSAXParser {

    /**
     * The SAXParser to use for parsing the XML.
     */
    private SAXParser saxParser;

    /**
     * The listener to report the events to.
     */
    SimpleSAXListener listener;

    /**
     * Creates a new SimpleSAXParser that will use the supplied SAXParser for parsing the XML. One must
     * set a SimpleSAXListener on this SimpleSAXParser before calling one of the parse() methods.
     * 
     * @param saxParser The SAXParser to use for parsing.
     * 
     * @see #setListener
     */
    public SimpleSAXParser(SAXParser saxParser) {
        this.saxParser = saxParser;
    }

    /**
     * Creates a new SimpleSAXParser that will create a new SAXParser using a SAXParserFactory for
     * parsing the XML. One must set a SimpleSAXListener on this object before calling one of the parse()
     * methods.
     * 
     * @exception ParserConfigurationException If the SimpleSAXParser was unable to create a SAXParser.
     * @exception SAXException
     * 
     * @see #setListener
     * @see javax.xml.parsers.SAXParser
     * @see javax.xml.parsers.SAXParserFactory
     */
    public SimpleSAXParser() throws ParserConfigurationException, SAXException {
        this(SAXParserFactory.newInstance().newSAXParser());
    }

    /**
     * Sets the (new) listener that should receive any events from this parser. This listener will
     * replace any previously set listener.
     * 
     * @param listener The (new) listener for events from this parser.
     */
    public void setListener(SimpleSAXListener listener) {
        this.listener = listener;
    }

    /**
     * Gets the listener that currently receives the events from this parser.
     * 
     * @return The listener for events from this parser.
     */
    public SimpleSAXListener getListener() {
        return listener;
    }

    /**
     * Parses the content of the supplied InputStream as XML.
     * 
     * @param in An InputStream containing XML data.
     */
    public synchronized void parse(InputStream in) throws SAXException, IOException {
        saxParser.parse(in, new SimpleSAXDefaultHandler());
    }

    /**
     * Parses the content of the supplied InputStream as XML.
     * 
     * @param in An InputStream containing XML data.
     * @param systemId The systemId used to resolve relative URIs.
     */
    public synchronized void parse(InputStream in, String systemId) throws SAXException, IOException {
        saxParser.parse(in, new SimpleSAXDefaultHandler(), systemId);
    }

    /**
     * Parses the content of the supplied File as XML.
     * 
     * @param file The file containing the XML to parse.
     */
    public synchronized void parse(File file) throws SAXException, IOException {
        saxParser.parse(file, new SimpleSAXDefaultHandler());
    }

    /**
     * This DefaultHandler extension translates SAX2 events to the simpler to use SimpleSAXListener
     * events.
     */
    private class SimpleSAXDefaultHandler extends DefaultHandler {

        /**
         * StringBuffer used to collect text during parsing.
         */
        private StringBuffer charBuf = new StringBuffer(512);

        /**
         * The tag name of a deferred start tag.
         */
        private String deferredStartTag = null;

        /**
         * The attributes of a deferred start tag.
         */
        private Map deferredAttributes = null;

        public SimpleSAXDefaultHandler() {
            super();
        }

        public void startDocument() throws SAXException {
            listener.startDocument();
        }

        public void endDocument() throws SAXException {
            listener.endDocument();
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            charBuf.append(ch, start, length);
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes attributes)
                throws SAXException {
            // Report any deferred start tag
            if (deferredStartTag != null) {
                reportDeferredStartElement();
            }

            // Make current tag new deferred start tag
            deferredStartTag = qName;

            // Copy attributes to _deferredAttributes
            int attCount = attributes.getLength();
            if (attCount == 0) {
                deferredAttributes = Collections.EMPTY_MAP;
            }
            else {
                deferredAttributes = new HashMap(attCount * 2);

                for (int i = 0; i < attCount; i++) {
                    deferredAttributes.put(attributes.getQName(i), attributes.getValue(i));
                }
            }

            // Clear character buffer
            charBuf.setLength(0);
        }

        private void reportDeferredStartElement() throws SAXException {
            listener.startTag(deferredStartTag, deferredAttributes, "");
            deferredStartTag = null;
            deferredAttributes = null;
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            if (deferredStartTag != null) {
                // Check if any character data has been collected in the _charBuf
                String text = charBuf.toString().trim();

                // Report deferred start tag
                listener.startTag(deferredStartTag, deferredAttributes, text);
                deferredStartTag = null;
                deferredAttributes = null;
            }

            // Report the end tag
            listener.endTag(qName);

            // Clear character buffer
            charBuf.setLength(0);
        }
    }
}
