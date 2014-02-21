package com.lfx.db;

    public class TextCacheEntry
    {
    	private String   content = null;
    	private long   loadtime = 0;
    	
    	public TextCacheEntry(String fcontent, long fload)
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