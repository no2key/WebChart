package com.lfx.db;

import java.sql.*;

public class AnySQLDriver extends java.lang.Object implements Driver
{
	public final static int MAJOR_VERSION = 1;
	public final static int MINOR_VERSION = 0;
	public final static DriverPropertyInfo null_info[] = {};

    	protected static String URL_PREFIX = "jdbc:anysql:";
    	protected static int URL_PREFIX_LEN = URL_PREFIX.length();

	public AnySQLDriver()
	{
	    try {
	    	DriverManager.registerDriver(this);
	    } catch (SQLException sqle) {}
	    String cfgfile = System.getProperty("db.config");
	    VariableTable vt = new VariableTable();
	    if (cfgfile != null)
	    {
	        vt.loadFile(cfgfile);
	        DBPhysicalManager.loadDBConfig(vt);
	        DBLogicalManager.loadDBConfig(vt);
	        DBPhysicalManager.markupAll();	
	    }
	}
   	
	public AnySQLDriver(String cfgfile)
	{
	    try {
	    	DriverManager.registerDriver(this);
	    } catch (SQLException sqle) {}

	    VariableTable vt = new VariableTable();
	    vt.loadFile(cfgfile);
	    DBPhysicalManager.loadDBConfig(vt);
	    DBLogicalManager.loadDBConfig(vt);
	    DBPhysicalManager.markupAll();
	}
	
	public int getMajorVersion()
	{
		return MAJOR_VERSION;
	}
	public int getMinorVersion()
	{
		return MINOR_VERSION;
	}
	public  boolean jdbcCompliant()
	{
		return true;	
	}
	public java.sql.DriverPropertyInfo[] getPropertyInfo(java.lang.String p1, java.util.Properties p2) throws SQLException
	{
		return null_info;	
	}
	public boolean acceptsURL(java.lang.String dburl) throws SQLException
	{
            try {
            	return dburl.startsWith(URL_PREFIX);
            } catch(NullPointerException e) {
                return false;
            }		
	}

	private int getIntValue(String val,int idef)
   	{
		if (val == null)
			return idef;
		try {
			return Integer.valueOf(val).intValue();
		}
		catch (NumberFormatException nfe) {}
		return idef;
	}
	    	
	public Connection connect(java.lang.String dburl, java.util.Properties info) throws SQLException
	{
	    if(acceptsURL(dburl))
	    {
	    	String dbname = dburl.substring(URL_PREFIX_LEN);
	    	try {
	    		java.util.Vector param = TextUtils.getWords(dbname,"/");
	    		if (param.size() > 1)
	    			return DBLogicalManager.getPoolConnection(param.elementAt(0).toString(),
	    					getIntValue(param.elementAt(1).toString(),0));
	    		else 
	    			return DBLogicalManager.getPoolConnection(dbname);
	    	}
	    	catch (ConnectTimeoutException cte) {throw new SQLException(cte.getMessage(),"TIMEOUT"); }
	    	catch (DatabaseMarkdownException dme) { throw new SQLException(dme.getMessage(),"MARKDOWN"); }
	    }
	    throw new SQLException("invalid url string error.");
	}
}