package com.lfx.db;

public class ImageCache 
{
    private static java.util.HashMap cache = new java.util.HashMap(1024);
    private static long cache_key = 1;
    private static long last_clear_time = System.currentTimeMillis();
    private static String _serverid = "b-";
    private static String _servlet  = "0-";

    static 
    {
	try {
           _serverid = java.net.InetAddress.getLocalHost().getHostName();
	   _serverid = String.valueOf(_serverid.hashCode()) + "-";
        } catch (java.net.UnknownHostException uhe) { _serverid = "b-"; }
    }

    public static void   setServiceName(String name)
    {
        if (name != null)
	{
	    _servlet = String.valueOf(name.hashCode()) + "-";
	}
    }

    public static long   getServiceCount()
    {
	return cache_key;
    }

    public static long   getImageCount()
    {
	return cache.size();
    }

    public static String putData(String fkey, DBRowCache data)    
    {
	data.setLoadTime(System.currentTimeMillis() + 300 * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    if (!ImageCacheMemCached.putObject(fkey, data, System.currentTimeMillis() + 300 * 1000))
   	        cache.put(fkey, data);
        }
	else
		cache.put(fkey, data);
	return fkey;
    }

    public static String putData(long current, String fkey, DBRowCache data)    
    {
	data.setLoadTime(current + 300 * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    if (!ImageCacheMemCached.putObject(fkey, data, current + 300 * 1000))
   	        cache.put(fkey, data);
        }
	else
		cache.put(fkey, data);
	return fkey;
    }

    public static String putData(String fkey, DBRowCache data, int keep_time)    
    {
	data.setLoadTime(System.currentTimeMillis() + keep_time * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    if (!ImageCacheMemCached.putObject(fkey, data, System.currentTimeMillis() + keep_time * 1000))
   	        cache.put(fkey, data);
        }
	else
		cache.put(fkey, data);
	return fkey;
    }

    public static String putData(long current, String fkey, DBRowCache data, int keep_time)    
    {
	data.setLoadTime(current + keep_time * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    if (!ImageCacheMemCached.putObject(fkey, data, current + keep_time * 1000))
   	        cache.put(fkey, data);
        }
	else
		cache.put(fkey, data);
	return fkey;
    }

    public static String putContent(byte fcontent[])    
    {
    	String fkey = _servlet + _serverid+(cache_key++);
	ImageCacheEntry fentry = new ImageCacheEntry(fcontent, System.currentTimeMillis() + 300 * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    if (!ImageCacheMemCached.putObject(fkey, fentry, System.currentTimeMillis() + 300 * 1000))
   	        cache.put(fkey, fentry);
        }
	else
	    cache.put(fkey, fentry);
	return fkey;
    }

    public static String putContent(long current, byte fcontent[])    
    {
    	String fkey = _servlet + _serverid+(cache_key++);
	ImageCacheEntry fentry = new ImageCacheEntry(fcontent, current + 300 * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
	    ImageCacheMemCached.putObject(fkey, fentry, current + 300 * 1000);
	else
	    cache.put(fkey, fentry);
	return fkey;
    }

    public static String putContent(byte fcontent[], int keep_time)    
    {
    	String fkey = _servlet + _serverid+(cache_key++);
	ImageCacheEntry fentry = new ImageCacheEntry(fcontent, System.currentTimeMillis() + keep_time * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    if(!ImageCacheMemCached.putObject(fkey, fentry, System.currentTimeMillis() + keep_time * 1000))
   	        cache.put(fkey, fentry);
        }
	else
	    cache.put(fkey, fentry);
	return fkey;
    }

    public static String putContent(long current, byte fcontent[], int keep_time)    
    {
    	String fkey = _servlet + _serverid+(cache_key++);
	ImageCacheEntry fentry = new ImageCacheEntry(fcontent, current + keep_time * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    if (!ImageCacheMemCached.putObject(fkey, fentry, current + keep_time * 1000))
   	        cache.put(fkey, fentry);
        }
	else
	    cache.put(fkey, fentry);
	return fkey;
    }
    
    public static String putContent(String fkey, byte fcontent[])    
    {
	ImageCacheEntry fentry = new ImageCacheEntry(fcontent, System.currentTimeMillis() + 300 * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    if (!ImageCacheMemCached.putObject(fkey, fentry, System.currentTimeMillis() + 300 * 1000))
   	        cache.put(fkey, fentry);
        }
	else
	    cache.put(fkey, fentry);
	return fkey;
    }

    public static String putContent(long current, String fkey, byte fcontent[])    
    {
	ImageCacheEntry fentry = new ImageCacheEntry(fcontent, current + 300 * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    if (!ImageCacheMemCached.putObject(fkey, fentry, current + 300 * 1000))
   	        cache.put(fkey, fentry);
        }
	else
	    cache.put(fkey, fentry);
	return fkey;
    }

    public static String putContent(String fkey, byte fcontent[], int keep_time)    
    {
	ImageCacheEntry fentry = new ImageCacheEntry(fcontent, System.currentTimeMillis() + keep_time * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    if (!ImageCacheMemCached.putObject(fkey, fentry, System.currentTimeMillis() + keep_time * 1000))
   	        cache.put(fkey, fentry);
        }
	else
	    cache.put(fkey, fentry);
	return fkey;
    }

    public static String putContent(long current, String fkey, byte fcontent[], int keep_time)    
    {
	ImageCacheEntry fentry = new ImageCacheEntry(fcontent, current + keep_time * 1000);
	if (ImageCacheMemCached.isMemCachedAlive())
        {
	    ImageCacheMemCached.putObject(fkey, fentry, current + keep_time * 1000);
   	        cache.put(fkey, fentry);
        }
	else
	    cache.put(fkey, fentry);
	return fkey;
    }

    public static DBRowCache getData(String fkey)
    {
	if (ImageCacheMemCached.isMemCachedAlive())
	{
		DBRowCache  fentry = (DBRowCache )(ImageCacheMemCached.getObject(fkey));
		if (fentry != null)
		{
			if (System.currentTimeMillis() <= fentry.getLoadTime() )
				return fentry;
			else
				ImageCacheMemCached.delObject(fkey);
		}
                else 
                {
                      fentry = (DBRowCache )(cache.get(fkey));
                      if (fentry != null && System.currentTimeMillis() <= fentry.getLoadTime()) return fentry;
                }
	}
	else
	{
	    	if ((System.currentTimeMillis() - last_clear_time) > 5 * 1000)
    		{
	    		last_clear_time = System.currentTimeMillis();
    		}
	  	if (cache.containsKey(fkey))
    		{
	    	       DBRowCache data = (DBRowCache)(cache.get(fkey));
  		       return data;
    		}
	}
	return null;
    }
    
    public static byte[] getImageContent(String fkey)
    {
	if (ImageCacheMemCached.isMemCachedAlive())
	{
		ImageCacheEntry fentry = (ImageCacheEntry)(ImageCacheMemCached.getObject(fkey));
		if (fentry != null)
		{
			if (System.currentTimeMillis() <= fentry.getLoadTime() )
				return fentry.getContent();
			else
				ImageCacheMemCached.delObject(fkey);
		}
                else 
                {
                      fentry = (ImageCacheEntry)(cache.get(fkey));
                      if (fentry != null && System.currentTimeMillis() <= fentry.getLoadTime()) return fentry.getContent();
                }
        }
	else
	{
 	    if (cache.containsKey(fkey))
    	    {
    	       ImageCacheEntry fentry = (ImageCacheEntry)(cache.get(fkey));
  	       if (fentry != null)
	       {
		   if (System.currentTimeMillis() <= fentry.getLoadTime())  return fentry.getContent();
	       }
    	    }
        }
	return null;
    }
    
    public static void removeContent(String fkey)
    {
	if (ImageCacheMemCached.isMemCachedAlive()) ImageCacheMemCached.delObject(fkey);
        if (cache.containsKey(fkey)) cache.remove(fkey);
    }

    public static synchronized void clearContent()
    {
	if ((System.currentTimeMillis() - last_clear_time) > 5 * 1000)
	{
            long currtime = System.currentTimeMillis();
	    if (cache != null && cache.size() > 0)
	    {
	       Object keyarr[] = cache.keySet().toArray();
               for(int i=0;i<keyarr.length;i++)
               {
                   ImageCacheEntry fentry = (ImageCacheEntry)(cache.get(keyarr[i]));
	           if (fentry != null && fentry.getLoadTime() < currtime)
	           {
		       cache.remove(keyarr[i]);
	           }
               }
	    }
	    last_clear_time = System.currentTimeMillis();
	}
    }
}