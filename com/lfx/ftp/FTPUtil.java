/**
 * Copyright (C) 2002-2003 Jason Gurney.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions, and the disclaimer that follows
 *    these conditions in the documentation and/or other materials
 *    provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR THE CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.lfx.ftp;

import java.util.StringTokenizer;

/**
 * FTPUtil provides static utility methods for the FTPClient and FTPSocket.
 *
 * @author Jason Gurney
 * @version 0.8
 */
abstract class FTPUtil {

	/** A quotation mark */
	private static final String QUOTE = "\"";

	/** The up relative directory filename */
	private static final String UP_RELATIVE_DIRECTORY = "..";

	/** The current relative directory filename */
	private static final String CURRENT_RELATIVE_DIRECTORY = ".";

	/** A period (the IP address value delimiter) */
	private final static String IP_SEPARATOR = ".";

	/**
	 * Indicates whether the specified file name is a relative path filename.
	 * 
	 * @param fileName the file name to compare
	 * @return true if the file name is ".." or "."
	 */
	protected static boolean isRelativePath(String fileName) {
		return UP_RELATIVE_DIRECTORY.equals(fileName) || CURRENT_RELATIVE_DIRECTORY.equals(fileName);
	}

	/**
	 * Returns surrounding quotation marks (when present) from the specified
	 * text.
	 *
	 * @param text the String with surrounding quotation marks
	 * @return that String without surrounding quotation marks
	 */
	protected static String removeSurroundingQuotes(String text) {
		int begin = text.indexOf(QUOTE) + 1;
		int end = text.lastIndexOf(QUOTE);
		if (end > -1) {
			text = text.substring(begin, end);
		}
		return text;
	}

	/**
	 * Returns a String array comprised of the elements of the specified
	 * StringTokenizer.
	 *
	 * @param st the StringTokenizer containing the results of the data command
	 * @return a String array containing the StringTokenizer elements
	 */
	protected static String[] getStringArray(StringTokenizer st) {
        int tokenCount = st.countTokens();
        String[] tokens = new String[tokenCount];
		for (int i = 0; i < tokenCount; i++ ) {
			tokens[i] = st.nextToken();
        }
        return tokens;
	}

	/**
	 * Constructs an IP address from the specified String array of numbers.
	 *
	 * @return an IP address
	 */
	protected static String getIPAddress(String[] parts) {
        StringBuffer ipAddress = new StringBuffer();
        for (int i = 0; i < 3; i++) {
            char character = parts[0].charAt(parts[0].length() - (3 - i));
            if (Character.isDigit(character)) {
                ipAddress.append(character);
            }
        }
        ipAddress.append(IP_SEPARATOR);
        ipAddress.append(parts[1]);
        ipAddress.append(IP_SEPARATOR);
        ipAddress.append(parts[2]);
        ipAddress.append(IP_SEPARATOR);
        ipAddress.append(parts[3]);
        return ipAddress.toString();
	}

	/**
	 * Determines the port from the specified String array of numbers.
	 *
	 * @return the server port
	 */
	protected static int getPort(String[] parts) {
		StringBuffer lastNumber = new StringBuffer();
        for(int k = 0; k < 3; k++) {
            if (k < parts[5].length()) {
                char character = parts[5].charAt(k);
				if (Character.isDigit(character)) {
					lastNumber.append(character);
				}
			}
        }
		int big = Integer.parseInt(parts[4]) << 8;
		int small = Integer.parseInt(lastNumber.toString());
		return big + small;
	}

	/**
	 * Indicates whether the specified line is a proper FTP server response.
	 *
	 * @param response a line received from the FTP server
	 * @return true if the line begins with three digits and a space
	 */
	protected static boolean isFTPResponse(String response) {
		if (response.charAt(3) != ' ') {
			return false;
		}
		for (int i = 0; i < 3; i++) {
			if (!Character.isDigit(response.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
