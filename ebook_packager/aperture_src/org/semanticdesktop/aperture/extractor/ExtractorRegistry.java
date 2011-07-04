/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.util.Set;

/**
 * An ExtractorRegistry serves as a central registry for registering and obtaining ExtractorFactories.
 */
public interface ExtractorRegistry {

    /**
     * Adds an ExtractorFactory to this registry.
     */
    public void add(ExtractorFactory factory);

    /**
     * Removes an ExtractorFactory from this registry.
     */
    public void remove(ExtractorFactory factory);

    /**
     * Returns all ExtractorFactories that support the specified MIME ype.
     * 
     * @return A Set of ExtractorFactories whose getSupportedMimeTypes method results contain the
     *         specified MIME type.
     */
    public Set get(String mimeType);

    /**
     * Returns all ExtractorFactories registered in this ExtractorRegistry.
     * 
     * @return A Set of ExtractorFactory instances.
     */
    public Set getAll();
}
