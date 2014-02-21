package com.lfx.db;

public class DataCache
{
    private static java.util.HashMap cache = new java.util.HashMap(1024);
    private static long last_clear_time = System.currentTimeMillis();
   
    public static String putData(String fkey, DBRowCache data)    
    {
	data.setLoadTime(System.currentTimeMillis() + 300 * 1000);
	cache.put(fkey, data);
	return fkey;
    }

    public static String putData(long current, String fkey, DBRowCache data)    
    {
	data.setLoadTime(current + 300 * 1000);
	cache.put(fkey, data);
	return fkey;
    }

    public static String putData(String fkey, DBRowCache data, int keep_time)    
    {
	data.setLoadTime(System.currentTimeMillis() + keep_time * 1000);
	cache.put(fkey, data);
	return fkey;
    }

    public static String putData(long current, String fkey, DBRowCache data, int keep_time)    
    {
	data.setLoadTime(current + keep_time * 1000);
	cache.put(fkey, data);
	return fkey;
    }

    public static DBRowCache getData(String fkey)
    {
    	if ((System.currentTimeMillis() - last_clear_time) > 5 * 1000)
    	{
    		clearData();
    		last_clear_time = System.currentTimeMillis();
    	}
  	if (cache.containsKey(fkey))
    	{
    	       DBRowCache data = (DBRowCache)(cache.get(fkey));
  	       return data;
    	}
	return null;
    }
    
    public static void removeData(String fkey)
    {
        if (cache.containsKey(fkey)) cache.remove(fkey);
    }

    public static synchronized void clearData()
    {
	if ((System.currentTimeMillis() - last_clear_time) > 5 * 1000)
	{
            long currtime = System.currentTimeMillis();
	    if (cache.size() > 0)
	    {
	       Object keyarr[] = cache.keySet().toArray();
               for(int i=0;i<keyarr.length;i++)
               {
                   DBRowCache fentry = (DBRowCache)(cache.get(keyarr[i]));
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