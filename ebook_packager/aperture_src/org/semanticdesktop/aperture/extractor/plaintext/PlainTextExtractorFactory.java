/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.plaintext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;


public class PlainTextExtractorFactory implements ExtractorFactory {

   private static final Set MIME_TYPES;
    
    static {
        HashSet set = new HashSet();
        set.add("text/plain");
        set.add("application/txt");
        set.add("browser/internal");
        set.add("text/anytext");
        set.add("widetext/plain");
        set.add("widetext/paragraph");
        
        MIME_TYPES = Collections.unmodifiableSet(set);
    }
    
    public PlainTextExtractor extractor;
    
    public Extractor get() {
    	return new PlainTextExtractor();
    }

    public Set getSupportedMimeTypes() {
        return MIME_TYPES;
    }
}
