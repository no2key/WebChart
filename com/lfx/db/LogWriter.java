package com.lfx.db;
public class LogWriter extends java.io.FileWriter
{
	public LogWriter(java.lang.String file) throws java.io.IOException
	{
		super(file);
	}
	public LogWriter(java.lang.String file,boolean create) throws java.io.IOException
	{
		super(file,create);
	}
	public LogWriter(java.io.File file) throws java.io.IOException
	{
		super(file);
	}
	public LogWriter(java.io.FileDescriptor file)
	{
		super(file);
	}
	public void write(java.lang.String msg) throws java.io.IOException
	{
		super.write(msg);
		flush();
		System.out.print(msg);
	}
}