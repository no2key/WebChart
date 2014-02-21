package com.lfx.db;

public class SingleMarkupThread extends Thread
{
    public void run()
    {
	String poolname[] = DBPhysicalManager.getPoolArray();
	if (poolname != null && poolname.length == 0)
	{
	    for(int i=0;i<poolname.length;i++)
	    {
		DBConnectionPool p = DBPhysicalManager.getConnectionPool(poolname[i]);
		p.markup();
	    }
        }
    }
}