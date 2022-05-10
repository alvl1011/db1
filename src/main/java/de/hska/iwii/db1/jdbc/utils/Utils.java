package de.hska.iwii.db1.jdbc.utils;

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
