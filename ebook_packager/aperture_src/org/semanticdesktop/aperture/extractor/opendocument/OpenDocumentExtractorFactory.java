/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.opendocument;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class OpenDocumentExtractorFactory implements ExtractorFactory {

    // see http://framework.openoffice.org/documentation/mimetypes/mimetypes.html for more info on
    // OpenOffice/OpenDocument MIME types

    private static final Set MIME_TYPES;

    static {
        HashSet set = new HashSet();

        // all OpenDocument MIME types
        add("vnd.oasis.opendocument.text", set);
        add("vnd.oasis.opendocument.spreadsheet", set);
        add("vnd.oasis.opendocument.graphics", set);
        add("vnd.oasis.opendocument.presentation", set);
        add("vnd.oasis.opendocument.image", set);
        add("vnd.oasis.opendocument.formula", set);
        add("vnd.oasis.opendocument.chart", set);

        set.add("application/vnd.oasis.opendocument.text-master");
        set.add("application/vnd.oasis.opendocument.text-web");
        set.add("application/x-vnd.oasis.opendocument.text-master");
        set.add("application/x-vnd.oasis.opendocument.text-web");
        
        // all OpenOffice 1.x and StarOffice 6.x/7.x MIME types
        set.add("application/vnd.sun.xml.writer");
        set.add("application/vnd.sun.xml.writer.template");
        set.add("application/vnd.sun.xml.writer.global");
        set.add("application/vnd.sun.xml.calc");
        set.add("application/vnd.sun.xml.calc.template");
        set.add("application/vnd.sun.xml.draw");
        set.add("application/vnd.sun.xml.draw.template");
        set.add("application/vnd.sun.xml.impress");
        set.add("application/vnd.sun.xml.impress.template");
        set.add("application/vnd.sun.xml.math");
        set.add("application/x-soffice");

        MIME_TYPES = Collections.unmodifiableSet(set);
    }
    
    private static void add(String baseType, Set set) {
        set.add("application/" + baseType);
        set.add("application/" + baseType + "-template");
        set.add("application/x-" + baseType);
        set.add("application/x-" + baseType + "-template");
    }

    public Extractor get() {
    	return new OpenDocumentExtractor();
    }

    public Set getSupportedMimeTypes() {
        return MIME_TYPES;
    }
}
