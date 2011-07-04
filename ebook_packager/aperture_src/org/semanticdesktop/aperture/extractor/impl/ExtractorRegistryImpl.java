/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;

/**
 * A trivial default implementation of the ExtractorRegistry interface.
 */
public class ExtractorRegistryImpl implements ExtractorRegistry {

    /**
     * A mapping from MIME types (Strings) to Sets of ExtractorFactories.
     */
    private HashMap factories = new HashMap();

    public void add(ExtractorFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }

        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();

            Set factorySet = (Set) factories.get(mimeType);
            if (factorySet == null) {
                factorySet = new HashSet();
                factories.put(mimeType, factorySet);
            }

            factorySet.add(factory);
        }
    }

    public void remove(ExtractorFactory factory) {
        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();
            Set factorySet = (Set) factories.get(mimeType);
            if (factorySet != null) {
                factorySet.remove(factory);

                if (factorySet.isEmpty()) {
                    factories.remove(mimeType);
                }
            }
        }
    }

    public Set get(String mimeType) {
        Set factorySet = (Set) factories.get(mimeType);
        if (factorySet == null) {
            return Collections.EMPTY_SET;
        }
		return new HashSet(factorySet);
    }

    public Set getAll() {
        HashSet result = new HashSet();

        Iterator sets = factories.values().iterator();
        while (sets.hasNext()) {
            Set factorySet = (Set) sets.next();
            result.addAll(factorySet);
        }

        return result;
    }
}
