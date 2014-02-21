package com.lfx.db;
import net.spy.memcached.*;
import net.spy.memcached.internal.OperationFuture;
import java.util.concurrent.TimeUnit;

public class ImageCacheMemCached
{
     private static String           serverlist = null;
     private static String           failoverlist = null;

     private static MemcachedClient  _cache = null;
     private static MemcachedClient  _failover = null;

     public static void setServerList(String servers)
     {
          serverlist = servers;
	  if (serverlist != null)
          {
	    try {	
	       ConnectionFactoryBuilder bcf = new ConnectionFactoryBuilder();
	       bcf.setOpTimeout(1000);
	       bcf.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY);
	       bcf.setFailureMode(FailureMode.Redistribute);
	       bcf.setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT);
	       bcf.setHashAlg(HashAlgorithm.KETAMA_HASH);
	       _cache=new MemcachedClient(bcf.build(), AddrUtil.getAddresses(serverlist));
	    } catch (java.io.IOException ioe) {};
          }
     }

     public static void setFailoverList(String servers)
     {
          failoverlist = servers;
	  if (failoverlist != null)
          {
	    try {	
	       ConnectionFactoryBuilder bcf = new ConnectionFactoryBuilder();
	       bcf.setOpTimeout(1000);
	       bcf.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY);
	       bcf.setFailureMode(FailureMode.Redistribute);
	       bcf.setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT);
	       bcf.setHashAlg(HashAlgorithm.KETAMA_HASH);
	       _failover=new MemcachedClient(bcf.build(), AddrUtil.getAddresses(failoverlist));
	    } catch (java.io.IOException ioe) {};
          }
     }

     public static boolean isMemCachedAlive()
     {
         return (_cache != null || _failover != null);
     }

     public static  boolean putObject(String _key, Object _value, long _keeptime)
     {
	int expire = (int)(_keeptime - System.currentTimeMillis())/1000;
	boolean result=false;

	while(!result && (_cache != null || _failover != null))
	{
 	    if (_cache != null)
	    {
	      try {
  	        OperationFuture<Boolean> putok = _cache.set(_key, expire, _value);
		_cache.waitForQueues(5, TimeUnit.SECONDS);
		result = putok.get().booleanValue();
	      } catch (OperationTimeoutException ote) {ote.printStackTrace();}
		catch (InterruptedException ie) {ie.printStackTrace();}
		catch (java.util.concurrent.ExecutionException ee) {ee.printStackTrace();}
	        catch (RuntimeException re) {re.printStackTrace();};	    
	    }
	    if (_failover != null)
	    {
	       try {
	           OperationFuture<Boolean> putok = _failover.set(_key, expire, _value);
		   _failover.waitForQueues(5, TimeUnit.SECONDS);
		   result = putok.get().booleanValue();
	       }catch (OperationTimeoutException ote) {ote.printStackTrace();}
		catch (InterruptedException ie) {ie.printStackTrace();}
		catch (java.util.concurrent.ExecutionException ee) {ee.printStackTrace();}
	        catch (RuntimeException re) {re.printStackTrace();};	    
	    }
	    if (!result)
	    {
	        try { Thread.currentThread().sleep(50);
		     } catch (InterruptedException ie) {}
	    }
	}
	return result;
     }

     public static  Object getObject(String _key)
     {
        Object val = null;
	boolean result=false;

	while(!result && (_cache != null || _failover != null))
	{
	    if (_cache != null)
	    {
	        try {
		    val = _cache.get(_key);
		    result = true;
	        } catch (OperationTimeoutException ote) {}
	          catch (RuntimeException re) {};	    
	    }
	    if (_failover != null && val == null)
	    {
	        try {
  	       	    val = _failover.get(_key);
		    result = true;
	        } catch (OperationTimeoutException ote) {}
	          catch (RuntimeException re) {};	    
	    }
	    if (!result)
	    {
	        try { Thread.currentThread().sleep(50);
		     } catch (InterruptedException ie) {}
	    }
	}
	return val;
     }
     public static void delObject(String _key)
     {
	if (_cache != null)
	{
	    try {
	        _cache.delete(_key);
	    } catch (OperationTimeoutException ote) {}
	      catch (RuntimeException re) {};	    
	}
	if (_failover != null)
	{
	    try {
	        _failover.delete(_key);
	    } catch (OperationTimeoutException ote) {}
	      catch (RuntimeException re) {};
	}
     }
}