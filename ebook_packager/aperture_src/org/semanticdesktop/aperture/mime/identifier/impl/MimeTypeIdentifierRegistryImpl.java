/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierFactory;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierRegistry;

/**
 * A trivial default implementation of the MimeTypeIdentifierRegistry interface.
 */
public class MimeTypeIdentifierRegistryImpl implements MimeTypeIdentifierRegistry {

    private HashSet factories = new HashSet();
    
    public void add(MimeTypeIdentifierFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }
        factories.add(factory);
    }

    public void remove(MimeTypeIdentifierFactory factory) {
        factories.remove(factory);
    }

    public Set getAll() {
        return Collections.unmodifiableSet(factories);
    }
}
