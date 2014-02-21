package com.lfx.db;

public class TextCache
{
    private static java.util.HashMap cache = new java.util.HashMap(1024);
    private static long last_clear_time = System.currentTimeMillis();

    public static void putContent(String fkey, String fcontent)    
    {
	TextCacheEntry fentry = new TextCacheEntry(fcontent, System.currentTimeMillis() + 300 * 1000);
	cache.put(fkey, fentry);
    }

    public static void putContent(String fkey, String fcontent, int keep_time)    
    {
	TextCacheEntry fentry = new TextCacheEntry(fcontent, System.currentTimeMillis() + keep_time * 1000);
	cache.put(fkey, fentry);
    }

    public static void putContent(long currtime, String fkey, String fcontent, int keep_time)    
    {
	TextCacheEntry fentry = new TextCacheEntry(fcontent, currtime + keep_time * 1000);
	cache.put(fkey, fentry);
    }
        
    public static String getTextContent(String fkey)
    {
	/*
   	if ((System.currentTimeMillis() - last_clear_time) > 5 * 1000)
    	{
    		clearContent();
    		last_clear_time = System.currentTimeMillis();
    	}
	*/
  	if (cache.containsKey(fkey))
    	{
    	       TextCacheEntry fentry = (TextCacheEntry)(cache.get(fkey));
	       if (fentry.getLoadTime() > System.currentTimeMillis())
  	           return fentry.getContent();
    	}
	return null;
    }
    
    public static void removeContent(String fkey)
    {
        if (cache.containsKey(fkey)) cache.remove(fkey);
    }

    public static synchronized void clearContent()
    {
	if ((System.currentTimeMillis() - last_clear_time) > 5 * 1000)
	{
            long currtime = System.currentTimeMillis();
	    if (cache.size() > 0)
	    {
	       Object keyarr[] = cache.keySet().toArray();
               for(int i=0;i<keyarr.length;i++)
               {
                   TextCacheEntry fentry = (TextCacheEntry)(cache.get(keyarr[i]));
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