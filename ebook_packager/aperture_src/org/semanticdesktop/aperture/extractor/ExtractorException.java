/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

/**
 * Thrown to indicate that an error occurred while extracting information from an InputStream by an
 * Extractor. A typical use case of this exception is for reporting parse errors.
 */
public class ExtractorException extends Exception {

	private static final long serialVersionUID = -5864662022881785572L;

	/**
     * Constructs an ExtractorException with no detail message.
     */
    public ExtractorException() {
        super();
    }

    /**
     * Constructs an ExtractorException with the specified detail message.
     * 
     * @param msg The detail message.
     */
    public ExtractorException(String msg) {
        super(msg);
    }

    /**
     * Constructs an ExtractorException with the specified detail message and cause.
     * 
     * Note that the detail message associated with the cause is not automatically incorporated in this
     * exception's detail message.
     * 
     * @param msg The detail message.
     * @param source The cause, which is saved for later retrieval by the Throwable.getCause() method. A
     *            null value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public ExtractorException(String msg, Throwable source) {
        super(msg, source);
    }

    /**
     * Constructs an ExtractorException with the specified cause and a detail message of <tt>(cause==null ?
     * null : cause.toString())</tt> (which typically contains the class and detail message of cause).
     * This constructor is useful for exceptions that are little more than wrappers for other throwables.
     * 
     * @param source The cause, which is saved for later retrieval by the Throwable.getCause() method. A
     *            null value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public ExtractorException(Throwable source) {
        super(source);
    }
}
