/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * ResourceUtil is a utility class for retrieving resources (images, property-files, etc) from the
 * classpath.
 */
public class ResourceUtil {

    public static URL getURL(String resourceName) {
        URL result = null;

        result = ResourceUtil.class.getResource(resourceName);
        if (result == null) {
            result = ResourceUtil.class.getClassLoader().getResource(resourceName);
            if (result == null) {
                result = ClassLoader.getSystemResource(resourceName);
            }
        }

        return result;
    }

    public static InputStream getInputStream(String resourceName) {
        InputStream result = null;

        result = ResourceUtil.class.getResourceAsStream(resourceName);
        if (result == null) {
            result = ResourceUtil.class.getClassLoader().getResourceAsStream(resourceName);
            if (result == null) {
                result = ClassLoader.getSystemResourceAsStream(resourceName);
            }
        }

        return result;
    }

    public static String getString(String resourceName) throws IOException {
        String result = null;

        InputStream in = ResourceUtil.getInputStream(resourceName);

        if (in != null) {
            try {
                result = IOUtil.readString(in);
            }
            finally {
                in.close();
            }
        }

        return result;
    }
    
    public static ImageIcon getImageIcon(String resourceName) {
        ImageIcon result = null;

        URL resourceURL = getURL(resourceName);
        if (resourceURL != null) {
            result = new ImageIcon(resourceURL);
        }
        
        return result;
    }
}
