package cz.cacek.ebook.packager;

import org.semanticdesktop.aperture.extractor.DATA;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;

import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;

import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;

import org.semanticdesktop.aperture.util.IOUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.nio.charset.Charset;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ApertureExtractor {
	public static String parseText(File tmpFile, String aCharset, Book aBook) {
		try {
			//try to use aperture framework to parse text
			//create a MimeTypeIdentifier
			MimeTypeIdentifier identifier = new MagicMimeTypeIdentifier();

			//create an ExtractorRegistry containing all available ExtractorFactories
			ExtractorRegistry extractorRegistry =
				new DefaultExtractorRegistry();

			//read as many bytes of the file as desired by the MIME type identifier
			FileInputStream stream = new FileInputStream(tmpFile);
			BufferedInputStream buffer = new BufferedInputStream(stream);
			byte[] bytes =
				IOUtil.readBytes(buffer, identifier.getMinArrayLength());
			stream.close();

			//let the MimeTypeIdentifier determine the MIME type of this file
			String mimeType =
				identifier.identify(bytes, tmpFile.getPath(), null);

			//skip when the MIME type could not be determined
			if (mimeType == null) {
				throw new Exception("MIME type could not be established.");
			}

			HashMap container = new HashMap();

			//determine and apply an Extractor that can handle this MIME type
			Set factories = extractorRegistry.get(mimeType);

			if ((factories != null) && !factories.isEmpty()) {
				// just fetch the first available Extractor
				ExtractorFactory factory =
					(ExtractorFactory) factories.iterator().next();
				Extractor extractor = factory.get();

				// apply the extractor on the specified file
				// (just open a new stream rather than buffer the previous stream)
				stream = new FileInputStream(tmpFile);
				buffer = new BufferedInputStream(stream, 8192);
				extractor.extract(
					buffer,
					Charset.forName(aCharset),
					mimeType,
					container);
				stream.close();
			}

			String tmpStr = getString(container, DATA.description);

			if (!StringUtils.isEmpty(tmpStr)) {
				aBook.setDescription(tmpStr);
			}

			tmpStr = getString(container, DATA.title);

			if (!StringUtils.isEmpty(tmpStr)) {
				aBook.setName(tmpStr);
			}

			return getString(container, DATA.fullText);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String getString(Map aMap, String aKey) {
		Object tmpResult = null;

		if (aMap != null) {
			tmpResult = aMap.get(aKey);
		}

		return (tmpResult == null) ? "" : tmpResult.toString();
	}
}
