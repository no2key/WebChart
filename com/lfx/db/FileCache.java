package com.lfx.db;

public class FileCache
{
    private static java.util.HashMap cache = new java.util.HashMap(512);

    public static String readFile(java.io.File fh)
    {
    	StringBuffer result = new StringBuffer(8192);
    	String temp=null;
    	try {
    	    java.io.BufferedReader in = new java.io.BufferedReader(new java.io.FileReader(fh));
    	    while((temp=in.readLine()) != null)
    	    {
    	    	result.append(temp);
    	    	result.append("\n");
    	    }
    	    in.close();
	} catch (java.io.IOException ioe) {};
	return result.toString();
    }

    public static String getFileContent(String fname)    
    {
    	return getFileContent(new java.io.File(fname));	
    }
    
    public static String getFileContent(java.io.File fh)
    {
    	String abpath = fh.getAbsolutePath();
    	if (fh.exists())
    	{
    	    if (cache.containsKey(abpath))
    	    {
    	        FileCacheEntry fentry = (FileCacheEntry)(cache.get(abpath));
    	        if (fh.lastModified() <= fentry.getLoadTime())
    	        {
    	            return fentry.getContent();
    	        }
    	        else
    	        {
    	            fentry.setContent(readFile(fh));
    	            fentry.setLoadTime(fh.lastModified());
    	            return fentry.getContent();	
    	        } 	
    	    }
    	    else
    	    {
    	    	FileCacheEntry fentry = new FileCacheEntry(readFile(fh), fh.lastModified());
    	    	cache.put(abpath, fentry);
    	    	return fentry.getContent();
    	    }
	}
	else
	{
	    cache.remove(fh.getAbsolutePath());	
	}
	return "";
    }
}