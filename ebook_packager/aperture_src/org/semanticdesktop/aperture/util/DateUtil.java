package org.semanticdesktop.aperture.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides utility methods for handling dates.
 */
public class DateUtil {

    private static DateFormat dateFormat = null;

    /**
     * Format the given date in a good dateTime format: ISO-8601, using the T separator and the - and :
     * seperators accordingly. Example: 2003-01-22T17:00:00.
     * 
     * @param date A date instance.
     * @return A String containing the date in ISO-8601 format.
     * @see #string2DateTime(String)
     */
    public static String dateTime2String(Date date) {
        return getISO8601DateFormat().format(date);
    }

    /**
     * Parses the given string as a Date using the same date format as dateTime2String.
     * 
     * @param string A String in ISO-8601 format.
     * @return A Date instance with a timestamp obtained from the specified String.
     * @throws ParseException when the specified string did not conform to the ISO-8601 standard.
     * @see #dateTime2String(Date)
     */
    public static Date string2DateTime(String string) throws ParseException {
        return getISO8601DateFormat().parse(string);
    }

    /**
     * Returns a statically shared DateFormat that uses the ISO-8601 format, which is used by
     * XSD-DATETIME.
     */
    public static DateFormat getISO8601DateFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        }
        return dateFormat;
    }
}
