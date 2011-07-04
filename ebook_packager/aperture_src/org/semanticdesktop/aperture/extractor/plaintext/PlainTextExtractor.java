/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.plaintext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticdesktop.aperture.extractor.DATA;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.util.IOUtil;

public class PlainTextExtractor implements Extractor {

    private static final Logger LOGGER = Logger.getLogger(PlainTextExtractor.class.getName());
    
    public void extract(InputStream stream, Charset charset, String mimeType, Map result) throws ExtractorException {
        try {
            // create a Reader that will convert the bytes to characters
            Reader reader = charset == null ? new InputStreamReader(stream) : new InputStreamReader(stream, charset);

            // verify that the first 256 characters really are text characters
            String firstChars = IOUtil.readString(reader, 256);

            int nrChars = firstChars.length();
            for (int i = 0; i < nrChars; i++) {
                char c = firstChars.charAt(i);
                if (!Character.isDefined(c) || (Character.isISOControl(c) && !Character.isWhitespace(c))) {
                    // c is not defined in Unicode or it is a control character that is not a whitespace character
                    LOGGER.log(Level.WARNING, "Document does not contain plain text");
                    return;
                }
            }

            // everything is ok, read the full document 
            String remainingChars = IOUtil.readString(reader);
            result.put(DATA.fullText, firstChars + remainingChars);
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }
    }
}
