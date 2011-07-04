/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier;

import java.util.Set;

/**
 * A MimeTypeIdentifierRegistry keeps track of all known MimeTypeIdentifierFactories.
 */
public interface MimeTypeIdentifierRegistry {

    /**
     * Adds a MimeTypeIdentifierFactory to this registry.
     */
    public void add(MimeTypeIdentifierFactory factory);

    /**
     * Removes a MimeTypeIdentifierFactory from this registry.
     */
    public void remove(MimeTypeIdentifierFactory factory);
    
    /**
     * Returns all MimeTypeIdentifierFactories registered in this MimeTypeIdentifierRegistry.
     * @return a Set of MimeTypeIdentifierFactory instances.
     */
    public Set getAll();
}
