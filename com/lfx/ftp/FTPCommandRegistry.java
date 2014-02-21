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
 * FTPCommandRegistry includes all basic FTP commands and provides convenience
 * methods for concatenating commands with arguments.
 *
 * @author Jason Gurney
 * @version 0.8
 */
public abstract class FTPCommandRegistry {

	/** "abor" - abort a file transfer */
	public static final String ABORT_COMMAND = "abor";

	/** "cwd" - change working directory */
	public static final String CHANGE_DIRECTORY_COMMAND = "cwd";

	/** "dele" - delete a remote file */
	public static final String DELETE_FILE_COMMAND = "dele";

	/** "list" - list remote files */
	public static final String LIST_FILES_COMMAND = "list";

	/** "mdtm" - return the modification time of a file */
	public static final String MODIFY_DATE_COMMAND = "mdtm";

	/** "mkd" - make a remote directory */
	public static final String MAKE_DIRECTORY_COMMAND = "mkd";

	/** "nlst" - name list of remote directory */
	public static final String NAME_LIST_COMMAND = "nlst";

	/** "pass" - send password */
	public static final String PASSWORD_COMMAND = "pass";

	/** "pasv" - enter passive mode */
	public static final String PASSIVE_MODE_COMMAND = "pasv";

	/** "port" - open a data port */
	public static final String PORT_COMMAND = "port";

	/** "pwd" - print working directory */
	public static final String PRINT_DIRECTORY_COMMAND = "pwd";

	/** "quit" - terminate the connection */
	public static final String QUIT_COMMAND = "quit";

	/** "retr" - retrieve a remote file */
	public static final String RETRIEVE_COMMAND = "retr";

	/** "rmd" - remove a remote directory */
	public static final String REMOVE_DIRECTORY_COMMAND = "rmd";

	/** "rnfr - rename from */
	public static final String RENAME_FROM_COMMAND = "rnfr";

	/** "rnto" - rename to */
	public static final String RENAME_TO_COMMAND = "rnto";

	/** "site" - site-specific commands */
	public static final String SITE_COMMAND = "site";

	/** "size" - return the size of a file */
	public static final String FILE_SIZE_COMMAND = "size";

	/** "stor" - store a file on the remote host */
	public static final String STORE_COMMAND = "stor";

	/** "type" - set transfer type */
	public static final String TRANSFER_TYPE_COMMAND = "type";

	/** "user" - send username */
	public static final String USERNAME_COMMAND = "user";

	/** "acct" - send account information */
	public static final String ACCOUNT_COMMAND = "acct";

	/** "appe" - append to a remote file */
	public static final String APPEND_COMMAND = "appe";

	/** "cdup" - cwd to the parent of the current directory */
	public static final String UP_DIRECTORY_COMMAND = "cdup";

	/** "help" - return help on using the server */
	public static final String HELP_COMMAND = "help";

	/** "mode" - set transfer mode */
	public static final String MODE_COMMAND = "mode";

	/** "noop" - do nothing */
	public static final String NULL_COMMAND = "noop";

	/** "rein" - reinitialize the connection */
	public static final String REINITIALIZE_COMMAND = "rein";

	/** "stat" - return server status */
	public static final String STATUS_COMMAND = "stat";

	/** "stou" - store a file uniquely */
	public static final String STORE_UNIQUE_COMMAND = "stou";

	/** "stru" - set file transfer structure */
	public static final String STRUCTURE_COMMAND = "stru";


	/** "rest" - Rest Command */
	public static final String RESET_COMMAND = "rest";

	/** "syst" - return system type */
	public static final String SYSTEM_TYPE_COMMAND = "syst";

	/** "a" - ascii transfer type */
	public static final String ASCII_TRANSFER_TYPE = "a";

	/** "i" - binary transfer type */
	public static final String BINARY_TRANSFER_TYPE = "i";

	/** a single space */
	private static final String SPACE = " ";

	/**
	 * Returns a username command for the specified username.
	 *
	 * @param username the username to send
	 * @return the complete username command
	 */

	protected static String getResetCommand(long size)
	{
		return getCommandWithArgument(RESET_COMMAND, String.valueOf(size));
	}

	protected static String getUsernameCommand(String username) {
		return getCommandWithArgument(USERNAME_COMMAND, username);
	}

	/**
	 * Returns a password command for the specified password.
	 *
	 * @param password the password to send
	 * @return the complete password command
	 */
	protected static String getPasswordCommand(String password) {
		return getCommandWithArgument(PASSWORD_COMMAND, password);
	}

	/**
	 * Returns a change directory command for the specified directory.
	 *
	 * @param directory the directory to change to
	 * @return the complete change directory command
	 */
	protected static String getChangeDirectoryCommand(String directory) {
		return getCommandWithArgument(CHANGE_DIRECTORY_COMMAND, directory);
	}

	/**
	 * Returns a rename from command for the specified filename.
	 *
	 * @param oldName the name of the file to rename
	 * @return the complete rename from command
	 */
	protected static String getRenameFromCommand(String oldName) {
		return getCommandWithArgument(RENAME_FROM_COMMAND, oldName);
	}

	/**
	 * Returns a rename to command for the specified filename.
	 *
	 * @param newName the new name of the file
	 * @return the complete rename to command
	 */
	protected static String getRenameToCommand(String newName) {
		return getCommandWithArgument(RENAME_TO_COMMAND, newName);
	}

	/**
	 * Returns a make directory command for the specified directory.
	 *
	 * @param directory the directory to create
	 * @return the complete make directory command
	 */
	protected static String getMakeDirectoryCommand(String directory) {
		return getCommandWithArgument(MAKE_DIRECTORY_COMMAND, directory);
	}

	/**
	 * Returns a remove directory command for the specified directory.
	 *
	 * @param directory the directory to delete
	 * @return the complete remove directory command
	 */
	protected static String getRemoveDirectoryCommand(String directory) {
		return getCommandWithArgument(REMOVE_DIRECTORY_COMMAND, directory);
	}

	/**
	 * Returns a delete file command for the specified filename.
	 *
	 * @param fileName the file to delete
	 * @return the complete delete file command
	 */
	protected static String getDeleteFileCommand(String fileName) {
		return getCommandWithArgument(DELETE_FILE_COMMAND, fileName);
	}

	/**
	 * Returns a modify date command for the specified filename.
	 *
	 * @param fileName the file to query
	 * @return the complete modify date command
	 */
	protected static String getModifyDateCommand(String fileName) {
		return getCommandWithArgument(MODIFY_DATE_COMMAND, fileName);
	}

	/**
	 * Returns a file size command for the specified filename.
	 *
	 * @param fileName the file to query
	 * @return the complete file size command
	 */
	protected static String getFileSizeCommand(String fileName) {
		return getCommandWithArgument(FILE_SIZE_COMMAND, fileName);
	}

	/**
	 * Returns a retrieve command for the specified filename.
	 *
	 * @param serverPath the file to retrieve
	 * @return the complete retrieve command
	 */
	protected static String getRetrieveCommand(String serverPath) {
		return getCommandWithArgument(RETRIEVE_COMMAND, serverPath);
	}

	/**
	 * Returns a store command for the specified filename.
	 *
	 * @param serverPath the file to store
	 * @return the complete store command
	 */
	protected static String getStoreCommand(String serverPath) {
		return getCommandWithArgument(STORE_COMMAND, serverPath);
	}

	/**
	 * Returns a transfer type command for the specified transfer type.
	 *
	 * @param ascii indicates whether ASCII transfer type should be used
	 * @return the complete transfer type command
	 */
	protected static String getTransferTypeCommand(boolean ascii) {
		return getCommandWithArgument(TRANSFER_TYPE_COMMAND, getTransferType(ascii));
	}

	/**
	 * Composes a complete command from the specified command and argument.
	 *
	 * @param command the command to execute
	 * @param argument the argument to pass in
	 * @return the complete command
	 */
	private static String getCommandWithArgument(String command, String argument) {
		return command + SPACE + argument;
	}

	/**
	 * Converts the specified boolean to a String transfer type.
	 *
	 * @param ascii indicates whether ASCII transfer type should be used
	 * @return the corresponding transfer type String
	 */
	private static String getTransferType(boolean ascii) {
		if (ascii)
			return ASCII_TRANSFER_TYPE;
		else
			return BINARY_TRANSFER_TYPE;
	}

}