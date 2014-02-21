package com.lfx.db;

    public class FileCacheEntry
    {
    	private String content = null;
    	private long   loadtime = 0;
    	
    	public FileCacheEntry(String fcontent, long fload)
    	{
    	    content  = fcontent;
    	    loadtime = fload;	
    	}
    	public String getContent()
    	{
    	    return content;
	}
	public long getLoadTime()
	{
	    return loadtime;
	}
    	public void setContent(String fcontent)
    	{
    	    content = fcontent;
	}
	public void setLoadTime(long fload)
	{
	    loadtime = fload;
	}	
    }