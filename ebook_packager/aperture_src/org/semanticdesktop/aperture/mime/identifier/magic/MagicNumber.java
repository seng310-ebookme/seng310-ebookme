/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier.magic;

public class MagicNumber {

    private byte[] magicBytes;
    
    private int offset;
    
    private int minimumLength;
    
    public MagicNumber(byte[] magicBytes, int offset) {
    	if (magicBytes == null) {
    		throw new IllegalArgumentException("magicBytes should not be null");
    	}
    	if (offset < 0) {
    		throw new IllegalArgumentException("offset should be >= 0");
    	}
    	
        this.magicBytes = magicBytes;
        this.offset = offset;
        this.minimumLength = magicBytes.length + offset;
    }
    
    public byte[] getMagicBytes() {
        return magicBytes;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public int getMinimumLength() {
        return minimumLength;
    }
    
    public boolean matches(byte[] bytes, int skippedLeadingBytes) {
        // check whether the specified array is long enough to check for the byte sequence
        if (bytes.length < minimumLength + skippedLeadingBytes) {
            return false;
        }
        
        // chech the magic bytes
        int realOffset = offset + skippedLeadingBytes;
        for (int i = 0; i < magicBytes.length; i++) {
            if (magicBytes[i] != bytes[i + realOffset]) {
                return false;
            }
        }

        // apparently all magic bytes are present
        return true;
    }
}
