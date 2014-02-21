package com.lfx.db;

public class DatabaseMarkdownException extends java.io.IOException
{
	public DatabaseMarkdownException(String pool)
	{
		super("Database ("+pool+") is markdown!");
	}
}