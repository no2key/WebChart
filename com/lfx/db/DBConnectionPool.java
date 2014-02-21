package com.lfx.db;

import java.util.Vector;
import java.sql.Connection;
import java.sql.SQLException;

public final class DBConnectionPool extends java.lang.Object implements java.io.Serializable
{

    private String  db_pool = "DEFAULT";
    private String  db_type = "ORACLE";
    private String  db_host = "";
    private String  db_user = "";
    private String  db_pass = "";
    private String  db_props = "";
    private String  db_tag  = null;
    private boolean db_autocommit = false;
    private boolean db_markdown = false;

    private String db_locale="SIMPLIFIED_CHINESE";
    private DatabaseMarkdownException db_ex = new DatabaseMarkdownException("PHYSICAL.DEFAULT"); 

    private int max_conns = 10;
    private int init_conns = 4;
    private int current_conns = 0;

    private java.util.Stack  nouse_pool = new java.util.Stack();
    private java.util.Vector inuse_pool = new java.util.Vector();

    private String getDBURL()
    {
	return db_type + " \"" + db_host +"\" "+
		db_user + " " + db_pass + " " + db_props;
    }

    public void setPoolName(String name)
    {
	if (name != null)
	{
	    db_pool = name;
	    db_ex = new DatabaseMarkdownException("PHYSICAL."+db_pool); 
	}
    }

    public void setDBType(String type)
    {
	if (type != null && type.length()>0) db_type = type;
    }

    public void setDBHost(String host)
    {
	if (host != null && host.length()>0) db_host = host;
    }

    public void setDBUser(String user)
    {
	if (user != null && user.length()>0) db_user = user;
    }

    public void setDBPass(String pass)
    {
	if (pass != null && pass.length()>0) db_pass = pass;
    }

    public void setDBProperty(String prop)
    {
	if (prop != null) db_props = prop;
    }

    public void setDBLocale(String locale)
    {
	if (locale != null) db_locale = locale;
    }

    public void setDBTag(String tag)
    {
	db_tag = tag;
    }

    public void setDBAutoCommit(boolean autocommit)
    {
	db_autocommit = autocommit;
    }

    public void decreaseCurrent(DBPooledConnection c)
    {
	inuse_pool.removeElement(c);
        current_conns --;
    }

    public void markdown()
    {
	freePool();
	db_markdown = true;
    }

    public void markup() 
    {
	if (db_markdown || nouse_pool.size() == 0)
	{
	  try {
	    java.sql.Connection c = getNativeConnection();
	    c.close();
	    db_markdown = false;
	  }
	  catch (java.sql.SQLException sqle) {db_markdown = true;}
	  catch (ClassNotFoundException cnfe) {db_markdown = true;}
	}
    }

    public boolean available()
    {
	return !db_markdown;
    }

    public String getPoolName()
    {
	return db_pool;
    }

    public String getDBType()
    {
	return db_type;
    }

    public String getDBHost()
    {
	return db_host;
    }

    public String getDBUser()
    {
	return db_user;
    }

    public String getDBProperty()
    {
	return db_props;
    }

    public String getDBTag()
    {
	if (db_tag == null)
		return db_type;
	return db_tag;
    }

    public void setInitConns(int i)
    {
	init_conns = i;
	if ( i < 0)
		init_conns = 0;
	if ( i > max_conns )
		init_conns = max_conns;
    }

    public int getInitConns()
    {
	return init_conns;
    }

    public void setMaxConns(int i)
    {
	max_conns = 10;
	if (i > 1)
		max_conns = i;	
    }

    public int getMaxConns()
    {
	return max_conns;
    }

    public int getCurrentConns()
    {
	return nouse_pool.size() + inuse_pool.size();
    }

    public int getIdleConns()
    {
	return nouse_pool.size();
    }

    public int getBusyConns()
    {
	return inuse_pool.size();
    }

    public void Active() 
    {
	/* notify(); */
    }

    public java.sql.Connection getNativeConnection() throws java.sql.SQLException, ClassNotFoundException 
    {
	return DBOperation.getConnection(getDBURL());
    }

    private synchronized DBPooledConnection tryGetConnection() throws DatabaseMarkdownException
    {
	DBPooledConnection c = null;
	if (db_markdown) throw db_ex;
	if (current_conns < nouse_pool.size()) current_conns = nouse_pool.size();
	if (current_conns > max_conns) current_conns = nouse_pool.size();
	if (!nouse_pool.empty() && current_conns >= init_conns)
	{
	       	c = (DBPooledConnection) nouse_pool.pop();
		if (c != null)
		{
			inuse_pool.addElement(c);
			return c;
		}

		current_conns --;
		try {
			DBOperation.setLocale(db_locale);
			c = new DBPooledConnection(this,DBOperation.getConnection(getDBURL()));
			c.setAutoCommit(db_autocommit);
			current_conns ++;
			inuse_pool.addElement(c);
			return c;
		} catch (java.sql.SQLException sqle) { db_markdown = true; }
		  catch (ClassNotFoundException cnfe) { db_markdown = true; }
	}
	else if (current_conns < max_conns)
	{
		try {
			DBOperation.setLocale(db_locale);
			c = new DBPooledConnection(this,DBOperation.getConnection(getDBURL()));
			c.setAutoCommit(db_autocommit);
			inuse_pool.addElement(c);
			current_conns ++;
			return c;
		} catch (java.sql.SQLException sqle) { db_markdown = true; }
		  catch (ClassNotFoundException cnfe) { db_markdown = true; }
	}
        return c;
    }

    public DBPooledConnection getConnection() throws ConnectTimeoutException, DatabaseMarkdownException
    {
	int retry_count=0;
	DBPooledConnection c = null;
	long starttime = System.currentTimeMillis();
	while(true)
	{
		c = tryGetConnection();
		if (c == null)
		{
		    if (System.currentTimeMillis() - starttime > 5 * 1000)
		    {
			throw new ConnectTimeoutException();
		    }
		    else
		    {
  			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {}
		   }
		}
		else
		{
		   return c;
		}
	}
    }

    public synchronized void putConnection(DBPooledConnection c)
    {
	if ((c==null)||nouse_pool.contains(c))
	{
		notify();
		return ;
	}
	if (! db_markdown && current_conns <= init_conns)
	{		
		nouse_pool.push(c);
		inuse_pool.removeElement(c);
		notify();
	}
	else
	{
		inuse_pool.removeElement(c);
		c.release();
		current_conns --;
		notify();
	}
    }

    public synchronized final void initPool() throws java.sql.SQLException,java.lang.ClassNotFoundException
    {
	int i;
	String db_cmd="";
	SQLException sql_error = null;
	ClassNotFoundException cnfe_error = null;
	db_cmd = getDBURL();
	for(i=current_conns;i<init_conns;i++)
	{
		try {
			DBOperation.setLocale(db_locale);
			DBPooledConnection db = new DBPooledConnection(this,DBOperation.getConnection(db_cmd));
			db.setAutoCommit(db_autocommit);
			nouse_pool.push(db);
			current_conns ++;
		} catch (java.sql.SQLException sqle) {sql_error = sqle; db_markdown = true; }
		  catch (ClassNotFoundException cnfe) {cnfe_error = cnfe; db_markdown = true; }
	}
	notify();
	if (sql_error != null)
		throw sql_error;
	if (cnfe_error != null)
		throw cnfe_error;
	return;
    }
    public synchronized final void freePool()
    {
	int i;
	DBPooledConnection db=null;
	while(!nouse_pool.empty())
	{
		db = (DBPooledConnection)nouse_pool.pop();			
		if (db != null)
		{
			db.release();
			current_conns --;
		}
	}
   }
   protected void finalize() throws java.lang.Throwable
   {
	freePool();
   }
}
