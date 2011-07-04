/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier;

/**
 * A MimeTypeIdentifierFactory creates instances of a specific MimeTypeIdentifier implementation.
 * 
 * <P>
 * MimeTypeIdentifierFactories should be very light-weight to create. This allows them to be used for
 * service registration in service-oriented architectures.
 */
public interface MimeTypeIdentifierFactory {

    /**
     * Get a MimeTypeIdentifier.
     */
    public MimeTypeIdentifier get();
}
