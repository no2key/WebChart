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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * FTPClient is a public class that handles many standard FTP client commands,
 * including opening and closing connections, uploading and downloading files,
 * creating and removing directories, renaming and deleting files, etc.<p>
 *
 * It can be used like this:
 * <pre> FTPClient ftpClient = new FTPClient();
 * try {
 *   ftpClient.openConnection(server, port);
 *   ftpClient.login(username, password);
 *   ftpClient.uploadFile(serverPath, localPath, asciiMode);
 * } catch (IOException e) {
 *   e.printStackTrace();
 * } finally {
 *   ftpClient.closeConnection();
 * }</pre><p>
 *
 * This class incorporates elements from Bret Taylor's FTPConection.
 *
 * @author Jason Gurney
 * @version 0.8
 */
public class FTPClient {

	/** The object that handles all communication with the FTP server */
	private FTPSocket ftpSocket;

	/** Indicates whether this client is currently in ASCII transfer mode */
	private boolean asciiTransfer = true;
	
	private String nameListCommand = FTPCommandRegistry.NAME_LIST_COMMAND;

	/** The default FTP port (21) */
	private static final int DEFAULT_PORT = 21;

	/** The default socket timeout (120 seconds) */
	private static final int SOCKET_TIMEOUT = 120 * 1000;

	/** The character that separates remote directories (/) */
	private static final String REMOTE_DIR_SEPARATOR = "/";

	/** The username for anonymous login */
	private static final String ANON_USERNAME = "anonymous";

	/** The password for anonymous login */
	private static final String ANON_PASSWORD = "guest";

	/** Whether to use continue download */
	private boolean continueTransfer = false;
	
	/**
	 * Default public constructor creates an FTP client with debug mode turned
	 * off and the default socket timeout.
	 */
	public FTPClient() {
		this(false);
	}

	/**
	 * Public constructor creates an FTP client with the specified debug mode
	 * and the default socket timeout.
	 *
	 * @param debugMode indicates whether server responses should be logged
	 */
	public FTPClient(boolean debugMode) {
		this(SOCKET_TIMEOUT, debugMode);
	}

	/**
	 * Public constructor creates an FTP client with the specified debug mode
	 * and socket timeout.
	 *
	 * @param socketTimeout specifies the socket timeout delay
	 * @param debugMode indicates whether server responses should be logged
	 */
	public FTPClient(int socketTimeout, boolean debugMode) {
		ftpSocket = new FTPSocket(socketTimeout, debugMode);
	}

	/**
	 * Opens a connection to the specified host through the default FTP port.
	 *
	 * @param host the host address of the FTP server
	 */
	public void openConnection(String host)
	throws UnknownHostException, IOException, IllegalFTPResponseException {
		openConnection(host, DEFAULT_PORT);
	}

	/**
	 * Opens a connection to the specified host through the specified port.
	 *
	 * @param host the host address of the FTP server
	 * @param port the FTP server connection port
	 */
	public void openConnection(String host, int port)
	throws UnknownHostException, IOException, IllegalFTPResponseException {
		ftpSocket.openConnection(host, port);
	}

	/**
	 * Disconnects this client from the FTP server.
	 */
	public void closeConnection() throws IOException {
		ftpSocket.executeCompleteCommand(FTPCommandRegistry.QUIT_COMMAND);
		ftpSocket.closeConnection();
	}

	/**
	 * Logs in this client to the FTP server using the specified username
	 * and password.
	 *
	 * @param username the FTP server username
	 * @param password the FTP server password
	 */
	public void login(String username, String password)
	throws IOException, IllegalFTPResponseException {
		ftpSocket.executeIntermediateCommand(FTPCommandRegistry.getUsernameCommand(username));
		ftpSocket.executeCompleteCommand(FTPCommandRegistry.getPasswordCommand(password));
	}

	/**
	 * Logs in this client anonymously to the FTP server.
	 */
	public void loginAnonymous()
	throws IOException, IllegalFTPResponseException {
		login(ANON_USERNAME, ANON_PASSWORD);
	}

	/**
	 * Changes the current FTP server directory to the specified (absolute or
	 * relative) path.
	 *
	 * @param directory the new FTP server directory
	 */
	public void changeDirectory(String directory)
	throws IOException, IllegalFTPResponseException {
		ftpSocket.executeCompleteCommand(FTPCommandRegistry.getChangeDirectoryCommand(directory));
	}

	/**
	 * Renames the specified file on the FTP server with the specified name.
	 *
	 * @param oldName the name of an existing file on the FTP server
	 * @param newName the new name for that file
	 */
	public void renameFile(String oldName, String newName)
	throws IOException, IllegalFTPResponseException {
		ftpSocket.executeIntermediateCommand(FTPCommandRegistry.getRenameFromCommand(oldName));
		ftpSocket.executeCompleteCommand(FTPCommandRegistry.getRenameToCommand(newName));
	}

	/**
	 * Creates a directory on the FTP server with the specified name.
	 *
	 * @param directory the directory to create
	 */
	public void makeDirectory(String directory)
	throws IOException, IllegalFTPResponseException {
		ftpSocket.executeCompleteCommand(FTPCommandRegistry.getMakeDirectoryCommand(directory));
	}

	/**
	 * Removes the specified directory (including its contents and
	 * subdirectories) from the FTP server.
	 *
	 * @param directory the directory to delete
	 */
	public void removeDirectory(String directory)
	throws IOException, IllegalFTPResponseException {
		String oldDirectory = getCurrentDirectory();
		changeDirectory(directory);
		removeDirectoryContents();
		changeDirectory(oldDirectory);
		ftpSocket.executeCompleteCommand(FTPCommandRegistry.getRemoveDirectoryCommand(directory));
	}

	/**
	 * Removes all files and subdirectories from the current directory on the
	 * FTP server.
	 */
	private void removeDirectoryContents()
	throws IOException, IllegalFTPResponseException {
		String[] fileNames = getNameArray();
		for (int i = 0; i < fileNames.length; i++) {
			if (!FTPUtil.isRelativePath(fileNames[i])) {
				try {
					removeDirectory(fileNames[i]);
				} catch (IllegalFTPResponseException e) {
					if (e.isActionNotTaken()) {
						deleteFile(fileNames[i]);
					} else {
						throw e;
					}
				}
			}
		}
	}

	/**
	 * Changes the current FTP server directory up one level to its parent
	 * directory.
	 */
	public void parentDirectory()
	throws IOException, IllegalFTPResponseException {
		ftpSocket.executeCompleteCommand(FTPCommandRegistry.UP_DIRECTORY_COMMAND);
	}

	/**
	 * Deletes the specified file from the FTP server.
	 *
	 * @param fileName the file to delete
	 */
	public void deleteFile(String fileName)
		throws IOException, IllegalFTPResponseException {
		ftpSocket.executeCompleteCommand(FTPCommandRegistry.getDeleteFileCommand(fileName));
	}

	/**
	 * Returns the current working directory on the FTP server.
	 *
	 * @return the path to the current working directory
	 */
	public String getCurrentDirectory() throws IOException {
		FTPServerResponse response = ftpSocket.executeCompleteCommand(FTPCommandRegistry.PRINT_DIRECTORY_COMMAND);
		String directoryName = response.getResponseMessage();
		return FTPUtil.removeSurroundingQuotes(directoryName);
	}

	/**
	 * Returns the system type of the FTP server.
	 *
	 * @return the FTP server system type
	 */
	public String getSystemType() throws IOException {
		FTPServerResponse resp = ftpSocket.executeCompleteCommand(FTPCommandRegistry.SYSTEM_TYPE_COMMAND);
		return resp.getResponseMessage();
	}

	/**
	 * Returns the last modification time of the specified file on the FTP
	 * server.
	 *
	 * @param fileName the file to check
	 * @return the last modification time of that file
	 */
	public long getModificationTime(String fileName) throws IOException {
		FTPServerResponse resp = ftpSocket.executeCompleteCommand(FTPCommandRegistry.getModifyDateCommand(fileName));
		String time = resp.getResponseMessage();
		try {
			return Long.parseLong(time);
		} catch (NumberFormatException e) {
			return -1L;
		}
	}

	/**
	 * Returns the size of the specified file on the FTP server.
	 *
	 * @param fileName the file to check
	 * @return the size of that file
	 */
	public long getFileSize(String fileName) throws IOException {
		FTPServerResponse resp = ftpSocket.executeCompleteCommand(FTPCommandRegistry.getFileSizeCommand(fileName));
		String size = resp.getResponseMessage();
		try {
			return Long.parseLong(size);
		} catch (NumberFormatException e) {
			return -1L;
		}
	}

	/**
	 * Downloads the specified file from the FTP server to the current local
	 * working directory using the current client transfer mode.
	 *
	 * @param fileName the file to download
	 */
	public void downloadFile(String fileName)
	throws IOException, IllegalFTPResponseException {
		downloadFile(fileName, fileName);
	}

	/**
	 * Downloads the specified file from the FTP server to the specified local
	 * path using the current client transfer mode.
	 *
	 * @param serverPath the file to download
	 * @param localPath the local file destination
	 */
	public void downloadFile(String serverPath, String localPath)
	throws IOException, IllegalFTPResponseException {
		downloadFile(serverPath, localPath, asciiTransfer);
	}

	/**
	 * Downloads the specified file from the FTP server to the specified local
	 * path using the specified transfer mode.
	 *
	 * @param serverPath the file to download
	 * @param localPath the local file destination
	 * @param ascii indicates whether ascii transfer mode should be used
	 */
	public void downloadFile(String serverPath, String localPath, boolean ascii)
	throws IOException, IllegalFTPResponseException {
		ftpSocket.setTransferType(ascii);
		ftpSocket.readDataToFile(FTPCommandRegistry.getRetrieveCommand(serverPath), localPath,continueTransfer);
	}

	/**
	 * Downloads the specified directory from the FTP server to the specified
	 * local path using the specified transfer mode.
	 *
	 * @param serverPath the directory to download
	 * @param localPath the local directory destination
	 * @param ascii indicates whether ascii transfer mode should be used
	 * @param recursive indicates whether subdirectories should be downloaded
	 */
	public void downloadDirectory(String serverPath, String localPath, boolean ascii, boolean recursive)
	throws IOException, IllegalFTPResponseException {
		String oldDirectory = getCurrentDirectory();
		changeDirectory(serverPath);
		downloadDirectory(localPath, ascii, recursive);
		changeDirectory(oldDirectory);
	}

	/**
	 * Downloads the current working directory from the FTP server to the
	 * specified local path using the specified transfer mode.
	 *
	 * @param localPath the local directory destination
	 * @param ascii indicates whether ascii transfer mode should be used
	 * @param recursive indicates whether subdirectories should be downloaded
	 */
	private void downloadDirectory(String localPath, boolean ascii, boolean recursive)
	throws IOException, IllegalFTPResponseException {
		new File(localPath).mkdirs();
		String[] fileNames = getNameArray();
		for (int i = 0; i < fileNames.length; i++) {
			if (!FTPUtil.isRelativePath(fileNames[i])) {
				try {
					changeDirectory(fileNames[i]);
					if (recursive) {
						downloadDirectory(localPath + File.separator + fileNames[i], ascii, recursive);
					}
					parentDirectory();
				} catch (IllegalFTPResponseException e) {
					if (e.isActionNotTaken()) {
						downloadFile(fileNames[i], localPath + File.separator + fileNames[i], ascii);
					} else {
						throw e;
					}
				}
			}
		}
	}

	/**
	 * Uploads the specified file from the local machine to the current working
	 * directory of the FTP server using the current client transfer mode.
	 *
	 * @param fileName the file to upload
	 */
	public void uploadFile(String fileName)
	throws IOException, IllegalFTPResponseException {
		uploadFile(fileName, fileName);
	}

	/**
	 * Uploads the specified file from the local machine to the specified path
	 * of the FTP server using the current client transfer mode.
	 *
	 * @param serverPath the FTP server destination
	 * @param localPath the file to upload
	 */
	public void uploadFile(String serverPath, String localPath)
	throws IOException, IllegalFTPResponseException {
		uploadFile(serverPath, localPath, asciiTransfer);
	}

	/**
	 * Uploads the specified file from the local machine to the specified path
	 * of the FTP server using the specified transfer mode.
	 *
	 * @param serverPath the FTP server destination
	 * @param localPath the file to upload
	 * @param ascii indicates whether ascii transfer mode should be used
	 */
	public void uploadFile(String serverPath, String localPath, boolean ascii)
	throws IOException, IllegalFTPResponseException {
		ftpSocket.setTransferType(ascii);
		ftpSocket.writeDataFromFile(FTPCommandRegistry.getStoreCommand(serverPath), localPath);
	}

	/**
	 * Uploads the specified directory from the local machine to the specified
	 * path of the FTP server using the specified transfer mode.
	 *
	 * @param serverPath the FTP server destination
	 * @param localPath the directory to upload
	 * @param ascii indicates whether ascii transfer mode should be used
	 * @param recursive indicates whether subdirectories should be uploaded
	 */
	public void uploadDirectory(String serverPath, String localPath, boolean ascii, boolean recursive)
	throws IOException, IllegalFTPResponseException {
		uploadDirectory(serverPath, localPath, ascii, recursive, null);
	}

	/**
	 * Uploads files matching the specified filename filter in the specified
	 * directory from the local machine to the specified path of the FTP server
	 * using the specified transfer mode.
	 *
	 * @param serverPath the FTP server destination
	 * @param localPath the directory to upload
	 * @param ascii indicates whether ascii transfer mode should be used
	 * @param recursive indicates whether subdirectories should be uploaded
	 * @param filenameFilter indicates which files should be uploaded
	 */
	public void uploadDirectory(String serverPath, String localPath, boolean ascii, boolean recursive, FilenameFilter filenameFilter)
	throws IOException, IllegalFTPResponseException {
		try {
			makeDirectory(serverPath);
		} catch (IllegalFTPResponseException e) {
			if (!e.isDirectoryExistsError() && !e.isActionNotTaken()) {
				throw e;
			}
		}
		File directory = new File(localPath);
		File[] subFiles = null;
		subFiles = directory.listFiles(filenameFilter);
		for (int i = 0; i < subFiles.length; i++) {
			String subFileName = subFiles[i].getName();
			if (subFiles[i].isDirectory() && recursive) {
				uploadDirectory(serverPath + REMOTE_DIR_SEPARATOR + subFileName, localPath + File.separator + subFileName, ascii, recursive, filenameFilter);
			} else if (!subFiles[i].isDirectory()) {
				uploadFile(serverPath + REMOTE_DIR_SEPARATOR + subFileName, localPath + File.separator + subFileName);
			}
		}
	}

	/**
	 * Returns a String array representing the files and directories contained
	 * in the current FTP server working directory.
	 *
	 * @return the contents of the FTP server working directory
	 */
	public String[] getNameArray() throws IOException, IllegalFTPResponseException {
		ftpSocket.setTransferType(true);
		String[] ret = ftpSocket.readDataToString(nameListCommand);
		ftpSocket.setTransferType(asciiTransfer);
		return ret;
	}

	/**
	 * Returns a new-line-delimited String representing the files and
	 * directories contained in the current FTP server working directory.
	 *
	 * @return the contents of the FTP server working directory
	 */
	public String getNameList() throws IOException, IllegalFTPResponseException {
		String[] names = getNameArray();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < names.length; i++) {
			buffer.append(names[i]);
			if (i + 1 < names.length) {
				buffer.append("\n");
			}
		}
		return buffer.toString();
	}

	/**
	 * Sets the default transfer type (ASCII or binary) for this client.
	 *
	 * @param ascii the new default transfer type
	 */
	public void setTransferType(boolean ascii) {
        asciiTransfer = ascii;
	}


	public void setContinueTransfer(boolean contf) {
        continueTransfer = contf;
	}
	
	/**
	 * Sets the name list command for this client.  The default is set to "nlst" (the value
	 * from <a href="FTPCommandRegistry.html#NAME_LIST_COMMAND">FTPCommandRegistry.NAME_LIST_COMMAND)</a>,
	 * but it can be changed to values such as "nlst -a" (from
	 * <a href="CustomFTPCommandRegistry.html#WU_FTP_NAME_LIST_COMMAND">CustomFTPCommandRegistry.WU_FTP_NAME_LIST_COMMAND</a>)
	 * for WU-FTP servers.
	 * 
	 * @param nameListCommand the new name list command
	 */
	public void setNameListCommand(String nameListCommand) {
		this.nameListCommand = nameListCommand;
	}
	
	/**
	 * Sets the line separator used for sending commands.  Default value is the
	 * "line.separator" System property.
	 * 
	 * @param lineSeparator the new line separator String
	 */
	public void setLineSeparator(String lineSeparator) {
		ftpSocket.setLineSeparator(lineSeparator);
	}

}
