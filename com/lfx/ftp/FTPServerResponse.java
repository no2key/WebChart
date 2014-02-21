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

/**
 * FTPServerResponse is a wrapper for FTP server responses, including response
 * codes and text messages.<p>
 *
 * This class incorporates elements from Bret Taylor's FTPConection.
 *
 * @author Jason Gurney
 * @version 0.8
 */
class FTPServerResponse {

	/** The numeric code for this response (should be between 100 and 600) */
	private int responseCode;

	/** The text message for this response */
	private String responseMessage;

	/**
	 * Default protected constructor.
	 *
	 * @param responseCode the numeric response code
	 * @param responseMessage the text message
	 */
	protected FTPServerResponse(int responseCode, String responseMessage) {
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	/**
	 * Protected constructor parses the response code and text message from
	 * the full response String.
	 *
	 * @param fullResponse the full FTP server response
	 */
	protected FTPServerResponse(String fullResponse) {
		responseCode = Integer.parseInt(fullResponse.substring(0, 3));
		responseMessage = fullResponse.substring(4);
	}

	/**
	 * Returns the numeric code for this response (should be between 100 and
	 * 600).
	 *
	 * @return the response code
	 */
	protected int getResponseCode() {
		return responseCode;
	}

	/**
	 * Returns the text message for this response.
	 *
	 * @return the text message
	 */
	protected String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * Returns the full FTP server response.
	 *
	 * @return the full FTP server response
	 */
	protected String getFullResponse() {
		return responseCode + " " + responseMessage;
	}

	/**
	 * Indicates whether the response code corresponds to a positive
	 * preliminary response.
	 *
	 * @param true if the response is positive preliminary
	 */

	protected boolean isPositivePreliminary() {
		return (responseCode >= 100 && responseCode < 200);
	}

	/**
	 * Indicates whether the response code corresponds to a positive
	 * preliminary response.
	 *
	 * @param true if the response is positive intermediate
	 */
	protected boolean isPositiveIntermediate() {
		return (responseCode >= 300 && responseCode < 400);
	}

	/**
	 * Indicates whether the response code corresponds to a positive
	 * complete response.
	 *
	 * @param true if the response is positive complete
	 */
	protected boolean isPositiveComplete() {
		return (responseCode >= 200 && responseCode < 300);
	}

	/**
	 * Indicates whether the response code corresponds to a transient
	 * negative response.
	 *
	 * @param true if the response is transient negative
	 */
	protected boolean isTransientNegative() {
		return (responseCode >= 400 && responseCode < 500);
	}

	/**
	 * Indicates whether the response code corresponds to a permanent
	 * negative response.
	 *
	 * @param true if the response is permanent negative
	 */
	protected boolean isPermanentNegative() {
		return (responseCode >= 500 && responseCode < 600);
	}

	/**
	 * Indicates whether the response code corresponds to a directory exists
	 * error.
	 *
	 * @param true if the response is directory exists
	 */
	public boolean isDirectoryExistsError() {
		return responseCode == 521;
	}

	/**
	 * Indicates whether the response code corresponds to an action not taken
	 * error.
	 *
	 * @param true if the response is action not taken
	 */
	public boolean isActionNotTaken() {
		return responseCode == 550;
	}

}