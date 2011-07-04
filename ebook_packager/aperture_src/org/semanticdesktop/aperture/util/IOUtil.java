/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 * I/O utility methods for working with Readers, Writers, InputStreams, OutputStreams and URLs.
 */
public class IOUtil {

    /**
     * Reads all characters from the supplied reader and returns them as an array
     */
    public static char[] readChars(Reader r) throws IOException {
        return readFully(r).toCharArray();
    }

    /**
     * Reads the contents from the given file as a String.
     */
    public static String readString(File file) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            return readString(in);
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Reads the contents from the given URL as a String.
     */
    public static String readString(URL url) throws IOException {
        String result = null;

        Reader reader = urlToReader(url);
        try {
            result = readString(reader);
        }
        finally {
            reader.close();
        }

        return result;
    }

    /**
     * Read the contents of the given stream as a String, using the default Charset.
     */
    public static String readString(InputStream in) throws IOException {
        return readString(new InputStreamReader(in));
    }

    /**
     * Reads all characters from the supplied reader and returns them as a String.
     */
    public static String readString(Reader r) throws IOException {
        return readFully(r).toString();
    }

    /**
     * Reads a string of at most length <tt>maxChars</tt> from the supplied Reader.
     * 
     * @param r The Reader to read the string from.
     * @param maxChars The maximum number of characters to read.
     * @return A String of length <tt>maxChars</tt>, or less if the supplied Reader did not contain
     *         that much characters.
     */
    public static String readString(Reader r, int maxChars) throws IOException {
        char[] charBuf = new char[maxChars];
        int charsRead = fillCharArray(r, charBuf);
        return new String(charBuf, 0, charsRead);
    }

    /**
     * Fills the supplied character array with characters read from the specified Reader. This method
     * will only stop reading when the character array has been filled completely, or when the end of the
     * stream has been reached.
     * 
     * @param r The Reader to read the characters from.
     * @param charArray The character array to fill with characters.
     * @return The number of characters written to the character array.
     */
    public static int fillCharArray(Reader r, char[] charArray) throws IOException {
        int result = 0;

        int charsRead = r.read(charArray);

        while (charsRead >= 0) {
            result += charsRead;

            if (result == charArray.length) {
                break;
            }

            charsRead = r.read(charArray, result, charArray.length - result);
        }

        return result;
    }

    /**
     * Reads all bytes from the supplied input stream and returns them as a byte array.
     * 
     * @param in The InputStream supplying the bytes.
     * @return A byte array containing all bytes from the supplied input stream.
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        writeStream(in, out);
        return out.toByteArray();
    }

    /**
     * Reads bytes from the supplied input stream up until a maximum number of bytes has been reached and
     * returns them as a byte array.
     * 
     * @param in The InputStream supplying the bytes.
     * @param maxBytes The maximum number of bytes to read from the input stream.
     * @return A byte array of size maxBytes if the input stream can produce that amount of bytes, or a
     *         smaller array containing all available bytes from the stream otherwise.
     */
    public static byte[] readBytes(InputStream in, int maxBytes) throws IOException {
        byte[] result = new byte[maxBytes];

        int bytesRead = fillByteArray(in, result);

        if (bytesRead < maxBytes) {
            // create smaller byte array
            byte[] tmp = new byte[bytesRead];
            System.arraycopy(result, 0, tmp, 0, bytesRead);
            result = tmp;
        }

        return result;
    }

    /**
     * Fills the supplied byte array with bytes read from the specified InputStream. This method will
     * only stop reading when the byte array has been filled completely, or when the end of the stream
     * has been reached.
     * 
     * @param in The InputStream to read the bytes from.
     * @param byteArray The byte array to fill with bytes.
     * @return The number of bytes written to the byte array.
     */
    public static int fillByteArray(InputStream in, byte[] byteArray) throws IOException {
        int result = 0;

        int bytesRead = in.read(byteArray);

        while (bytesRead >= 0) {
            result += bytesRead;

            if (result == byteArray.length) {
                break;
            }

            bytesRead = in.read(byteArray, result, byteArray.length - result);
        }

        return result;
    }

    /**
     * Writes all data that can be read from the supplied InputStream to the specified file.
     */
    public static void writeStream(InputStream in, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);

        try {
            writeStream(in, out);
        }
        finally {
            try {
                out.flush();
            }
            finally {
                out.close();
            }
        }
    }

    /**
     * Writes all data that can be read from the supplied InputStream to the supplied OutputStream.
     */
    public static void writeStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        int bytesRead = 0;

        while ((bytesRead = in.read(buf)) != -1) {
            out.write(buf, 0, bytesRead);
        }
    }

    /**
     * Writes the contents of the specified String to the specified File.
     */
    public static void writeString(String contents, File file) throws IOException {
        FileWriter out = new FileWriter(file);
        try {
            out.write(contents);
        }
        finally {
            out.close();
        }
    }

    /**
     * Write the contents of the specified contents String to a file with the specified file name.
     */
    public static void writeString(String contents, String filename) throws IOException {
        writeString(contents, new File(filename));
    }

    /**
     * Creates a Reader accessing the contents of the specified URL.
     */
    public static Reader urlToReader(URL url) throws IOException {
        URLConnection con = url.openConnection();
        return new InputStreamReader(con.getInputStream());
    }

    private static CharArrayWriter readFully(Reader r) throws IOException {
        char[] buf = new char[4096];
        int charsRead = 0;
        CharArrayWriter result = new CharArrayWriter();

        while ((charsRead = r.read(buf)) != -1) {
            result.write(buf, 0, charsRead);
        }

        return result;
    }
}
