/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier.magic;

import java.lang.ref.WeakReference;

import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierFactory;

public class MagicMimeTypeIdentifierFactory implements MimeTypeIdentifierFactory {

	// rationale for using a WeakReference to maintain the returned instance: on the one hand
	// MagicMimeTypeIdentifiers have a relatively costly initialization procedure (XML parsing and
	// interpretation, object creation) and are stateless after initialization, so they can be shared by
	// whoever needs a MimeTypeIdentifier. On the other hand, you don't want to keep the MIME type
	// descriptions with all information about magic numbers, file extensions etc. in main memory
	// indefinitely. A SoftReference could have been used as well, which is kept in main memory for a longer
	// period.

	private WeakReference reference;

	public MimeTypeIdentifier get() {
		synchronized (this) {
			// obtain the wrapped identifier, if any
			MagicMimeTypeIdentifier result = null;
			if (reference != null) {
				result = (MagicMimeTypeIdentifier) reference.get();
			}

			// if either the weak reference or the identifier was missing, create a new one
			if (result == null) {
				result = new MagicMimeTypeIdentifier();
				reference = new WeakReference(result);
			}

			return result;
		}
	}
}
