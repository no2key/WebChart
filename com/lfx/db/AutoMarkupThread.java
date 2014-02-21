package com.lfx.db;

public class AutoMarkupThread extends Thread
{
    private boolean _stop = false;

    public void run()
    {
	while(!_stop)
	{
	    String poolname[] = DBPhysicalManager.getPoolArray();
	    if (poolname == null || poolname.length == 0)
	    {
		_stop = true;
		break;
	    }
	    for(int i=0;i<poolname.length;i++)
	    {
		DBConnectionPool p = DBPhysicalManager.getConnectionPool(poolname[i]);
		p.markup();
	    }
	    ImageCache.clearContent();
	    TextCache.clearContent();
	    try {
		sleep(10 * 1000);
	        ImageCache.clearContent();
	        TextCache.clearContent();
		sleep(10 * 1000);
	        ImageCache.clearContent();
	        TextCache.clearContent();
	    } catch (java.lang.InterruptedException ie) {}
	}
    }
}