/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.util.Set;

/**
 * An ExtractorFactory create instances of a specific Extractor implementation. As such, it embodies
 * knowledge about whether a singleton or unique instances are best returned and for which MIME types the
 * Extractors can be used.
 * 
 * <P>
 * ExtractorFactories should be very light-weight to create. This allows them to be used for service
 * registration in service-oriented architectures.
 */
public interface ExtractorFactory {

    /**
     * Returns an instance of the represented Extractor implementation. Subsequent invocations may or may
     * not return the same instance.
     * 
     * @return An instance of the Extractor interface.
     */
    public Extractor get();

    /**
     * Returns the MIME types of the formats supported by the returned Extractor.
     * 
     * @return A Set of Strings.
     */
    public Set getSupportedMimeTypes();
}
