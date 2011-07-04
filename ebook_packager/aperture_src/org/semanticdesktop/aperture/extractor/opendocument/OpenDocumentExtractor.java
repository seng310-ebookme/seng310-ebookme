/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.opendocument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.semanticdesktop.aperture.extractor.DATA;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.util.DateUtil;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.semanticdesktop.aperture.util.SimpleSAXAdapter;
import org.semanticdesktop.aperture.util.SimpleSAXParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Extracts full-text and metadata from OpenDocument files and is backwards compatible with older
 * OpenOffice (1.x) and StarOffice (6.x and 7.x) documents.
 */
public class OpenDocumentExtractor implements Extractor {

    // used to append to extracted text, to make it more readable
    static final String END_OF_LINE = System.getProperty("line.separator", "\n");

    // used to fool the parser, when it tries to load the system dtd.
    // seems to work better than tricks such as providing a dummy EntityResolver, which is probably parser implementation-dependent
    // (see e.g. http://www.jroller.com/comments/santhosh/Weblog/putoff_dtd_parsing_html)
    private static String SYSTEM_ID;
    
    static {
    	final String tmpFile = "math.dtd";
    	URL tmpUrl = ResourceUtil.getURL("org/semanticdesktop/aperture/extractor/opendocument/"+ tmpFile);
    	if (tmpUrl!=null) {
    		String tmpStr = tmpUrl.toString();
    		if (tmpStr.length()>tmpFile.length()) {
    			SYSTEM_ID = tmpStr.substring(0, tmpStr.length() - tmpFile.length());
    		}
    	}
    }
    
    public void extract(InputStream stream, Charset charset, String mimeType, Map result)
            throws ExtractorException {
        byte[] contentBytes = null;
        byte[] metadataBytes = null;

        // fetch the byte arrays from the zip file that contain the document content and metadata
        try {
            ZipInputStream zipStream = new ZipInputStream(stream);
            ZipEntry entry = null;
            while ((entry = zipStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if ("content.xml".equals(entryName)) {
                    contentBytes = IOUtil.readBytes(zipStream);
                }
                else if ("meta.xml".equals(entryName)) {
                    metadataBytes = IOUtil.readBytes(zipStream);
                }

                zipStream.closeEntry();
            }
            zipStream.close();
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }

        // extract the document text
        if (contentBytes != null) {
            extractFullText(contentBytes, result);
        }

        // extract the metadata
        if (metadataBytes != null) {
            extractMetadata(metadataBytes, result);
        }
    }

    private void extractFullText(byte[] bytes, Map result) throws ExtractorException {
        // create a SimpleSaxParser
        SimpleSAXParser parser = null;
        try {
            parser = new SimpleSAXParser();
        }
        catch (Exception e) {
            // this is an internal error rather than an extraction problem, hence the RuntimeException
            throw new RuntimeException("unable to instantiate SAXParser", e);
        }

        // create a listener that will interpret the document events
        ContentExtractor contentExtractor = new ContentExtractor();
        parser.setListener(contentExtractor);

        // parse the byte array
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        try {
            parser.parse(stream, SYSTEM_ID);
        }
        catch (SAXException e) {
            throw new ExtractorException(e);
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }

        // put the extracted full-text in the RDF container
        String contents = contentExtractor.getContents();
        if (contents != null && !contents.equals("")) {
            result.put(DATA.fullText, contents);
        }
    }

    /**
     * Reads the metadata of the document from the specified byte array
     */
    private void extractMetadata(byte[] bytes, Map result) throws ExtractorException {
        // create a DocumentBuilder instance
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setValidating(false);
        docBuilderFactory.setExpandEntityReferences(false);
        DocumentBuilder docBuilder;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            // this is an internal error rather than an extraction problem, hence the RuntimeException
            throw new RuntimeException("unable to instantiate DocumentBuilder", e);
        }

        // parse the XML using the DocumentBuilder
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        Document doc;
        try {
            doc = docBuilder.parse(stream, SYSTEM_ID);
        }
        catch (SAXException e) {
            throw new ExtractorException(e);
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }

        // iterate over the nodes containing the metadata
        Element rootelement = doc.getDocumentElement();
        Node metaNode = rootelement.getFirstChild();
        NodeList metaChildren = metaNode.getChildNodes();
        int nrChildren = metaChildren.getLength();

        for (int i = 0; i < nrChildren; i++) {
            Node metaChild = metaChildren.item(i);
            String name = metaChild.getNodeName();

            // determine which metadata property we're dealing with
            if ("dc:creator".equals(name)) {
                addStatement(DATA.creator, metaChild.getFirstChild(), result);
            }
            else if ("meta:initial-creator".equals(name)) {
                addStatement(DATA.creator, metaChild.getFirstChild(), result);
            }
            else if ("dc:title".equals(name)) {
                addStatement(DATA.title, metaChild.getFirstChild(), result);
            }
            else if ("dc:description".equals(name)) {
                addStatement(DATA.description, metaChild.getFirstChild(), result);
            }
            else if ("dc:subject".equals(name)) {
                addStatement(DATA.subject, metaChild.getFirstChild(), result);
            }
            else if ("dc:date".equals(name)) {
                addDateStatement(DATA.date, metaChild.getFirstChild(), result);
            }
            else if ("meta:creation-date".equals(name)) {
                addDateStatement(DATA.created, metaChild.getFirstChild(), result);
            }
            else if ("meta:print-date".equals(name)) {
                addDateStatement(DATA.printDate, metaChild.getFirstChild(), result);
            }
            else if ("dc:language".equals(name)) {
                addStatement(DATA.language, metaChild.getFirstChild(), result);
            }
            else if ("meta:generator".equals(name)) {
                addStatement(DATA.generator, metaChild.getFirstChild(), result);
            }
            else if ("meta:keywords".equals(name)) {
                // handles OpenOffice 1.x keywords
//                NodeList keywordNodes = metaChild.getChildNodes();
//                int nrKeywordNodes = keywordNodes.getLength();
//                for (int j = 0; j < nrKeywordNodes; j++) {
//                    Node keywordNode = keywordNodes.item(j);
//                    if ("meta:keyword".equals(keywordNode.getNodeName())) {
//                        addStatement(DATA.keyword, keywordNode.getFirstChild(), result);
//                    }
//                }
            }
            else if ("meta:keyword".equals(name)) {
                // handles OpenOffice 2.x, i.e. OpenDocument
                addStatement(DATA.keywords, metaChild.getFirstChild(), result);
            }
            else if ("meta:document-statistic".equals(name)) {
                NamedNodeMap attributes = metaChild.getAttributes();
                if (attributes != null) {
                    Node pageNode = attributes.getNamedItem("meta:page-count");
                    if (pageNode != null) {
                        String pageNodeValue = pageNode.getNodeValue();
                        if (pageNodeValue != null) {
                            try {
                                result.put(DATA.pageCount, pageNodeValue);
                            }
                            catch (NumberFormatException e) {
                                // ignore
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void addStatement(String uri, Node node, Map container) {
        if (node != null) {
            addStatement(uri, node.getNodeValue(), container);
        }
    }

    private void addStatement(String uri, String value, Map container) {
        if (value != null) {
            container.put(uri, value);
        }
    }
    
    private void addDateStatement(String uri, Node node, Map container) {
        if (node != null) {
            String value = node.getNodeValue();
            if (value != null) {
                try {
                    Date date = DateUtil.string2DateTime(value);
                    container.put(uri, date);
                }
                catch (ParseException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Inner class for extracting full-text from the content.xml part of an
     * OpenDocument/OpenOffice/StarOffice document.
     */
    private static class ContentExtractor extends SimpleSAXAdapter {

        private static final String OFFICE_BODY = "office:body";
        
        private static final String MATH_MATH = "math:math";

        private static final String TEXT_P = "text:p";
        
        private static final String TEXT_H = "text:h";

        private StringBuffer contents = new StringBuffer(4096);

        private boolean insideBody = false;

        public String getContents() {
            return contents.toString();
        }

        public void startTag(String tagName, Map atts, String text) {
            if (OFFICE_BODY.equals(tagName) || MATH_MATH.equals(tagName)) {
                insideBody = true;
            }
            else if (insideBody && text.length() > 0) {
                if (TEXT_H.equals(tagName) && contents.length() > 0) {
                    contents.append(END_OF_LINE);
                    contents.append(END_OF_LINE);
                }
                
                contents.append(text);

                if (TEXT_P.equals(tagName)) {
                    contents.append(END_OF_LINE);
                }
                else if (TEXT_H.equals(tagName)) {
                    contents.append(END_OF_LINE);
                    contents.append(END_OF_LINE);
                }
                else {
                    contents.append(' ');
                }
            }
        }

        public void endTag(String tagName) {
            if (OFFICE_BODY.equals(tagName) || MATH_MATH.equals(tagName)) {
                insideBody = false;
            }
        }
    }
}
