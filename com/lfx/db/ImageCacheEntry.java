package com.lfx.db;

    public class ImageCacheEntry implements java.io.Serializable
    {
    	private byte   content[] = null;
    	private long   loadtime = 0;
    	
    	public ImageCacheEntry(byte fcontent[], long fload)
    	{
    	    content  = fcontent;
    	    loadtime = fload;	
    	}
    	public byte[] getContent()
    	{
    	    return content;
	}
	public long getLoadTime()
	{
	    return loadtime;
	}
    	public void setContent(byte fcontent[])
    	{
    	    content = fcontent;
	}
	public void setLoadTime(long fload)
	{
	    loadtime = fload;
	}	
    }