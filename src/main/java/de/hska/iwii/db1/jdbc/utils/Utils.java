package de.hska.iwii.db1.jdbc.utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import oracle.sql.DATE;

public final class Utils {
	
	/**
	 * Check integer
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
	
	/**
	 * Check double
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
	
	/**
	 * Check date
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isDate(String string) {
        try {
        	 Date.valueOf(string);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

	/**
	 * Concatenation of strings with length
	 * 
	 * @param string
	 * @param length
	 * @return
	 */
    public static String concat(String string, int length) {
        int suffix;
        if (string == null) {
            string = "";
        }
        if (length - string.length() > 0) {
            suffix = length - string.length();
        } else {
            suffix = string.length() - length;
        }
        if (isInt(string)) {
            string = " ".repeat(Math.max(0, suffix)) + string;
        } else {
            string = string + " ".repeat(Math.max(0, suffix));
        }
        return string;
    }
}
