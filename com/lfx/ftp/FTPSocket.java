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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * FTPSocket encapsulates all communication between the FTP client and the
 * FTP server.<p>
 *
 * This class incorporates elements from Bret Taylor's FTPConection.
 *
 * @author Jason Gurney
 * @version 0.8
 */
class FTPSocket {

	/** Indicates whether server responses should be logged */
	private boolean debugMode;

	/** The communication socket connecting the FTP client and server */
	private Socket connectionSocket = null;

	/** The output stream for the primary communication socket */
	private PrintStream outputStream = null;

	/** The input stream for the primary communication socket */
	private BufferedReader inputStream = null;

	/** The socket timeout delay */
	private int socketTimeout;
	
	/** 
	 * The line separator used for sending commands (default value is the
	 * "line.seperator" System property)
	 * */
	private String lineSeparator;

	/**
	 * Default protected constructor.
	 *
	 * @param socketTimeout specifies the socket timeout delay
	 * @param debugMode indicates whether server responses should be logged
	 */
	protected FTPSocket(int socketTimeout, boolean debugMode) {
		this.socketTimeout = socketTimeout;
		this.debugMode = debugMode;
		lineSeparator = System.getProperty("line.separator");
	}

	/**
	 * Opens a connection to the specified host through the specified port.
	 *
	 * @param host the host address of the FTP server
	 * @param port the FTP server connection port
	 * @exception IllegalFTPResponseException if a positive complete response
	 *            is not received
	 */
	protected void openConnection(String host, int port)
	throws UnknownHostException, IOException, IllegalFTPResponseException {
		connectionSocket = new Socket(host, port);
		connectionSocket.setSoTimeout(socketTimeout);
		outputStream = new PrintStream(connectionSocket.getOutputStream());
		inputStream = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		FTPServerResponse response = getServerResponse();
		if (!response.isPositiveComplete()) {
			closeConnection();
			throw new IllegalFTPResponseException(response);
		}
	}

	/**
	 * Closes this connection from the FTP server.
	 */
	protected void closeConnection() throws IOException {
		if (outputStream != null) {
			outputStream.close();
			inputStream.close();
			connectionSocket.close();
		}
		outputStream = null;
		inputStream = null;
		connectionSocket = null;
	}

	/**
	 * Sets the transfer type (ASCII or binary) for this connection.
	 *
	 * @param ascii the new transfer type
	 */
	protected void setTransferType(boolean ascii)
	throws IOException, IllegalFTPResponseException {
		executeCompleteCommand(FTPCommandRegistry.getTransferTypeCommand(ascii));
	}

	/**
	 * Returns the FTP server response to the last client command.
	 *
	 * @return the FTPServerResponse object
	 */
	private FTPServerResponse getServerResponse() throws IOException {
		String response;
		do {
			if (inputStream == null)
				throw new IOException ("Server not connected");
			response = inputStream.readLine();
			debugPrint(response);
		} while (!FTPUtil.isFTPResponse(response));
		return new FTPServerResponse(response);
	}

	/**
	 * Sends the specified command to the FTP server.
	 *
	 * @param command the command to send to the FTP server
	 * @return the response from the FTP server
	 */
	private FTPServerResponse executeCommand(String command) throws IOException {
		if (outputStream == null)
			throw new IOException ("Server not connected");
		outputStream.print(command + lineSeparator);
		return getServerResponse();
	}

	/**
	 * Sends the specified command to the FTP server (expecting a positive
	 * complete response).
	 *
	 * @param command the command to send to the FTP server
	 * @return the response from the FTP server
	 * @exception IllegalFTPResponseException if a positive complete response
	 *            is not received
	 */
	protected FTPServerResponse executeCompleteCommand(String command)
	throws IOException, IllegalFTPResponseException {
		FTPServerResponse resp = executeCommand(command);
		if (!resp.isPositiveComplete()) {
			throw new IllegalFTPResponseException(resp);
		}
		return resp;
	}

	/**
	 * Sends the specified command to the FTP server (expecting a positive
	 * intermediate response).
	 *
	 * @param command the command to send to the FTP server
	 * @return the response from the FTP server
	 * @exception IllegalFTPResponseException if a positive intermediate
	 *            response is not received
	 */
	protected FTPServerResponse executeIntermediateCommand(String command)
	throws IOException, IllegalFTPResponseException {
		FTPServerResponse resp = executeCommand(command);
		if (!resp.isPositiveIntermediate()) {
			throw new IllegalFTPResponseException(resp);
		}
		return resp;
	}

	/**
	 * Sends the specified command to the FTP server (expecting a positive
	 * preliminary response).
	 *
	 * @param command the command to send to the FTP server
	 * @return the response from the FTP server
	 * @exception IllegalFTPResponseException if a positive preliminary
	 *            response is not received
	 */
	protected FTPServerResponse executePreliminaryCommand(String command)
	throws IOException, IllegalFTPResponseException {
		FTPServerResponse resp = executeCommand(command);
		if (!resp.isPositivePreliminary()) {
			throw new IllegalFTPResponseException(resp);
		}
		return resp;
	}

	/**
	 * Sends the specified data command to the FTP server, transferring the
	 * results to the file at the specified path.
	 *
	 * @param command the command to send to the FTP server
	 * @param fileName the path to the destination file
	 */
	protected void readDataToFile(String command, String fileName)
	throws IOException, IllegalFTPResponseException {
		readDataToFile(command,fileName,false);
	}

	protected void readDataToFile(String command, String fileName, boolean cont)
	throws IOException, IllegalFTPResponseException {
		if (cont)
		{
			FTPServerResponse response = 
				executeIntermediateCommand(FTPCommandRegistry.getResetCommand(0));
			if (response.isPositiveIntermediate())
			{
				long filesize=new java.io.File(fileName).length();
				FileOutputStream fileStream = new FileOutputStream(fileName,true);
				executeDataCommand(command, fileStream,filesize);
				fileStream.close();
				return;
			}
		}
		FileOutputStream fileStream = new FileOutputStream(fileName);
		executeDataCommand(command, fileStream,0);
		fileStream.close();	
	}

	/**
	 * Sends the specified data command to the FTP server, returning the
	 * results as a String array.
	 *
	 * @param command the command to send to the FTP server
	 * @return a String array containing the results of the data command
	 */
	protected String[] readDataToString(String command)
	throws IOException, IllegalFTPResponseException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		executeDataCommand(command, byteStream);
		String files = byteStream.toString();
		byteStream.close();
		StringTokenizer st = new StringTokenizer(files, "\r\n");
		return FTPUtil.getStringArray(st);
	}

	/**
	 * Sends the specified data command to the FTP server, then transfers the
	 * contents of the file at the specified path.
	 *
	 * @param command the command to send to the FTP server
	 * @param fileName the path to the source file
	 */
	protected void writeDataFromFile(String command, String fileName)
	throws IOException, IllegalFTPResponseException {
		FileInputStream fileStream = new FileInputStream(fileName);
		executeDataCommand(command, fileStream);
		fileStream.close();
	}

	/**
	 * Sends the specified data command to the FTP server, transferring the
	 * results to the specified output stream.
	 *
	 * @param command the command to send to the FTP server
	 * @param out the destination output stream
	 */

	protected void executeDataCommand(String command, OutputStream out)
	throws IOException, IllegalFTPResponseException {
		executeDataCommand(command,out,0);
	}

	protected void executeDataCommand(String command, OutputStream out,long startsize)
	throws IOException, IllegalFTPResponseException {
		Socket clientSocket = null;
		InputStream in = null;
		try {
			clientSocket = getDataSocket();
			clientSocket.setSoTimeout(socketTimeout);
			in = clientSocket.getInputStream();
			if (startsize > 0)
				executeIntermediateCommand(FTPCommandRegistry.getResetCommand(startsize));
			executePreliminaryCommand(command);
			transferData(in, out);
		} finally {
			if (in != null) {
				in.close();
			}
			if (clientSocket != null) {
				clientSocket.close();
			}
		}
		FTPServerResponse response = getServerResponse();
		if (!response.isPositiveComplete()) {
			throw new IllegalFTPResponseException(response);
		}
	}

	/**
	 * Sends the specified data command to the FTP server, then transfers the
	 * contents of the specified input stream.
	 *
	 * @param command the command to send to the FTP server
	 * @param in the source input stream
	 */
	protected void executeDataCommand(String command, InputStream in)
	throws IOException, IllegalFTPResponseException {
		Socket clientSocket = null;
		OutputStream out = null;
		try {
			clientSocket = getDataSocket();
			clientSocket.setSoTimeout(socketTimeout);
			out = clientSocket.getOutputStream();
			executePreliminaryCommand(command);
			transferData(in, out);
		} finally {
			if (out != null) {
				out.close();
			}
			if (clientSocket != null) {
				clientSocket.close();
			}
		}
		FTPServerResponse response = getServerResponse();
		if (!response.isPositiveComplete()) {
			throw new IllegalFTPResponseException(response);
		}
	}

	/**
	 * Sets the line separator used for sending commands.  Default value is the
	 * "line.separator" System property.
	 * 
	 * @param lineSeparator the new line separator String
	 */
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	/**
	 * Creates and returns a data socket between the client and the FTP server.
	 *
	 * @return a new data socket
	 */
    private Socket getDataSocket() throws IOException {
		FTPServerResponse response = executeCompleteCommand(FTPCommandRegistry.PASSIVE_MODE_COMMAND);
        String reply = response.getResponseMessage();
        StringTokenizer st = new StringTokenizer(reply, ",");
        String[] tokens = FTPUtil.getStringArray(st);
        String ipAddress = FTPUtil.getIPAddress(tokens);
        int port = FTPUtil.getPort(tokens);
        if (ipAddress != null && port != -1) {
            return new Socket(ipAddress, port);
    	} else {
        	throw new IOException();
        }
    }

	/**
	 * Transfers data in one kilobyte blocks from the specified input stream
	 * to the specified output stream.
	 *
	 * @param in the source stream
	 * @param out the destination stream
	 */
	private void transferData(InputStream in, OutputStream out)
	throws IOException {
		byte b[] = new byte[4096];
		int amount;
		while ((amount = in.read(b)) > 0) {
			out.write(b, 0, amount);
		}
	}

	/**
	 * When debug mode is on, prints the specified message to the System output
	 * stream.
	 *
	 * @param message the String to print
	 */
	private void debugPrint(String message) {
		if (debugMode) {
			System.out.println(message);
		}
	}

}
