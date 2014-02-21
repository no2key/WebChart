package com.lfx.db;

public class ConnectTimeoutException extends java.io.IOException
{
	public ConnectTimeoutException()
	{
		super("Get Pooled Database Connection Timeout After 10 Seconds!");
	}
}