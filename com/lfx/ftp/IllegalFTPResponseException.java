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

import java.io.IOException;

/**
 * IllegalFTPResponseException is thrown whenever an FTP server returns an
 * unexpected response to the previous command.
 *
 * @author Jason Gurney
 * @version 0.8
 */
public class IllegalFTPResponseException extends IOException {

	/** The FTP server response that led to this exception */
	private FTPServerResponse response;

	/**
	 * Default protected constructor.
	 *
	 * @param response the FTP server response that led to this exception
	 */
	protected IllegalFTPResponseException(FTPServerResponse response) {
		super(response.getFullResponse());
		this.response = response;
	}

	/**
	 * Returns the numeric code from the FTP server response.
	 *
	 * @return the FTP server response code
	 */
	public int getResponseCode() {
		return response.getResponseCode();
	}

	/**
	 * Returns the text message from the FTP server response.
	 *
	 * @return the FTP server text message
	 */
	public String getResponseMessage() {
		return response.getResponseMessage();
	}

	/**
	 * Indicates whether the FTP server response code indicates that the
	 * directory already exists.
	 *
	 * @return true if the response code indicates that the directory exists
	 */
	public boolean isDirectoryExistsError() {
		return response.isDirectoryExistsError();
	}

	/**
	 * Indicates whether the FTP server response code indicates that the
	 * requested action was not taken.
	 *
	 * @return true if the response code indicates that the requested action
	 *         was not taken
	 */
	public boolean isActionNotTaken() {
		return response.isActionNotTaken();
	}

}