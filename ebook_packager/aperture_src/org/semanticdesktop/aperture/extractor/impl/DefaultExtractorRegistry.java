/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.semanticdesktop.aperture.util.SimpleSAXAdapter;
import org.semanticdesktop.aperture.util.SimpleSAXParser;
import org.xml.sax.SAXException;

/**
 * DefaultExtractorRegistry provides the complete set of ExtractorFactories available in Aperture.
 * 
 * <p>
 * The main purpose of this class is to be able to conveniently access the set of ExtractorFactories in
 * non-OSGi applications, which take care of this initialization in a different way. A single line of
 * code gives you the entire set without requiring further setup.
 * 
 * <p>
 * The set of factory class names are loaded from an XML file which can optionally be specified to the
 * constructor. This class requires all the listed classes to have a no-argument constructor.
 */
public class DefaultExtractorRegistry extends ExtractorRegistryImpl {

    private static final String DEFAULT_FILE = "org/semanticdesktop/aperture/extractor/impl/defaults.xml";

    private static final String EXTRACTOR_FACTORY_TAG = "extractorFactory";

    private static final String NAME_TAG = "name";

    static final Logger LOGGER = Logger.getLogger(DefaultExtractorRegistry.class.getName());

    public DefaultExtractorRegistry() {
        try {
            InputStream stream = ResourceUtil.getInputStream(DEFAULT_FILE);
            BufferedInputStream buffer = new BufferedInputStream(stream);
            parse(buffer);
            buffer.close();
        }
        catch (IOException e) {
            throw new RuntimeException("unable to parse " + DEFAULT_FILE, e);
        }
    }

    public DefaultExtractorRegistry(InputStream stream) throws IOException {
        parse(stream);
    }

    private void parse(InputStream stream) throws IOException {
        try {
            // Parse the document
            SimpleSAXParser parser = new SimpleSAXParser();
            parser.setListener(new ExtractorParser());
            parser.parse(stream);
        }
        catch (ParserConfigurationException e) {
            IOException ie = new IOException(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        catch (SAXException e) {
            IOException ie = new IOException(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
    }

    private class ExtractorParser extends SimpleSAXAdapter {

        private boolean insideFactoryElement = false;

        public void startTag(String tagName, Map atts, String text) throws SAXException {
            if (EXTRACTOR_FACTORY_TAG.equals(tagName)) {
                insideFactoryElement = true;
            }
            else if (NAME_TAG.equals(tagName) && insideFactoryElement && text != null) {
                processClassName(text);
            }
        }

        public void endTag(String tagName) {
            if (EXTRACTOR_FACTORY_TAG.equals(tagName)) {
                insideFactoryElement = false;
            }
        }

        private void processClassName(String className) {
            className = className.trim();
            if (!className.equals("")) {
                try {
                    Class clazz = Class.forName(className);
                    Object instance = clazz.newInstance();
                    ExtractorFactory factory = (ExtractorFactory) instance;
                    add(factory);
                }
                catch (ClassNotFoundException e) {
                    LOGGER.log(Level.WARNING, "unable to find class " + className + ", ignoring", e);
                }
                catch (InstantiationException e) {
                    LOGGER.log(Level.WARNING, "unable to instantiate class " + className + ", ignoring", e);
                }
                catch (IllegalAccessException e) {
                    LOGGER.log(Level.WARNING, "unable to access class " + className + ", ignoring", e);
                }
                catch (ClassCastException e) {
                    LOGGER.log(Level.WARNING, "unable to cast instance to "
                            + ExtractorFactory.class.getName() + ", ignoring", e);
                }
            }
        }
    }
}
