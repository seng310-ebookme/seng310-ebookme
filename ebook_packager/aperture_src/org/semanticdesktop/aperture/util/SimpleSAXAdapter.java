/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.util.Map;
import org.xml.sax.SAXException;

/**
 * An implementation of SimpleSAXListener providing dummy implementations for all its methods.
 */
public class SimpleSAXAdapter implements SimpleSAXListener {

    public void startDocument() throws SAXException { }

    public void endDocument() throws SAXException { }

    public void startTag(String tagName, Map atts, String text) throws SAXException { }

    public void endTag(String tagName) throws SAXException { }
}
