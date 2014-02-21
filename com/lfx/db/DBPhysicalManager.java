package com.lfx.db;

public class DBPhysicalManager
{
	private static VariableTable connpools = new VariableTable();

	public static void loadDBConfig(VariableTable vt)
	{
		int i,k;
		java.util.Vector dblist=null;
                if (vt.exists("PHYSICAL.DBLIST"))
                {
  		    dblist = TextUtils.getWords(vt.getString("PHYSICAL.DBLIST"),"|");
                }
                else
                {
                    dblist = new java.util.Vector();
		    String lnames[] = vt.getNames();
		    if (lnames != null && lnames.length > 0)
                    {
                       for(i=0;i<lnames.length;i++)
                       {
                           if(lnames[i].startsWith("PHYSICAL."))
                           { 
                              java.util.Vector tmpkeys = TextUtils.getWords(lnames[i],".");
			      if (tmpkeys.size() > 1 && !dblist.contains(tmpkeys.elementAt(1)))
                                  dblist.addElement(tmpkeys.elementAt(1));
                           }
                       }
                    }
                }

		if (dblist.size() == 0) dblist.addElement("DEFAULT");
		for(k = 0; k < dblist.size() ; k++)
		{
			String dbname = dblist.elementAt(k).toString();
			getConnectionPool(dbname).setDBType(vt.getString("PHYSICAL."+dbname+".DBTYPE"));
			getConnectionPool(dbname).setDBHost(vt.getString("PHYSICAL."+dbname+".DBHOST"));
			getConnectionPool(dbname).setDBUser(vt.getString("PHYSICAL."+dbname+".DBUSER"));
			getConnectionPool(dbname).setDBPass(DBPassword.decrypt("531616C7983404028ECA6D7E75E91B27", 
					vt.getString("PHYSICAL."+dbname+".DBPASS")));
			getConnectionPool(dbname).setDBProperty(vt.getString("PHYSICAL."+dbname+".PROPERTY"));
			getConnectionPool(dbname).setDBLocale(vt.getString("PHYSICAL."+dbname+".LOCALE"));
			getConnectionPool(dbname).setDBTag(vt.getString("PHYSICAL."+dbname+".DBTAG"));
			getConnectionPool(dbname).setDBAutoCommit("TRUE".equalsIgnoreCase(vt.getString("PHYSICAL."+dbname+".AUTOCOMMIT")));
			try { 
				if (vt.getString("PHYSICAL."+dbname+".MAXCONNS") != null)
				{
					i = Integer.valueOf(vt.getString("PHYSICAL."+dbname+".MAXCONNS")).intValue();
					getConnectionPool(dbname).setMaxConns(i);
				}
			} catch (java.lang.NumberFormatException nfe) {}
			try { 
				if (vt.getString("PHYSICAL."+dbname+".INITCONNS") != null)
				{
					i = Integer.valueOf(vt.getString("PHYSICAL."+dbname+".INITCONNS")).intValue();
					getConnectionPool(dbname).setInitConns(i);
				}
			} catch (java.lang.NumberFormatException nfe) {}
			DBLogicalConnection lc = new DBLogicalConnection(dbname, dbname);
			DBLogicalManager.putLogicalConnection(lc);
		}
	}
	public static void loadDBConfig(String cfgfile)
	{
		VariableTable vt = new VariableTable();
		vt.loadFile(cfgfile);
		loadDBConfig(vt);	
	}
	public static boolean exists(String pname)
	{
		return connpools.exists(pname);
	}
	
	public static String[] getPoolArray()
	{
		return connpools.getNames();
	}

	public static void ActivePool()
	{
		String pools[] =  connpools.getNames();
		for(int i=0;i<pools.length;i++)
			getConnectionPool(pools[i]).Active();
	}
	public static DBPooledConnection getPoolConnection() throws ConnectTimeoutException, DatabaseMarkdownException
	{
		return getConnectionPool().getConnection();
	}
	public static DBPooledConnection getPoolConnection(String poolname) throws ConnectTimeoutException, DatabaseMarkdownException
	{
		return getConnectionPool(poolname).getConnection();
	}
	public static void putPoolConnection(DBPooledConnection db)
	{
		getConnectionPool().putConnection(db);
	}
	public static void putPoolConnection(String poolname,DBPooledConnection db)
	{
		getConnectionPool(poolname).putConnection(db);
	}
	public static DBConnectionPool getConnectionPool()
	{
		return getConnectionPool("DEFAULT");
	}

	public static int getCurrentConns(String poolname)
	{
		return getConnectionPool(poolname).getCurrentConns();
	}

	public static int getCurrentConns()
	{
		return getConnectionPool("DEFAULT").getCurrentConns();
	}

	public static void markdown(String poolname)
	{
		if (connpools.exists(poolname))
		{
			getConnectionPool(poolname).markdown();
		}
	}

	public static void markup(String poolname) 
	{
		if (connpools.exists(poolname))
		{
			getConnectionPool(poolname).markup();
		}
	}

	public static boolean available(String poolname) 
	{
		if (connpools.exists(poolname))
		{
			return getConnectionPool(poolname).available();
		}
		return false;
	}

	public static void markupAll()
	{
		int i;
		DBConnectionPool pool;
		String poolname[] = connpools.getNames();
		if (poolname.length > 0)
		{
		    for(i=0;i<poolname.length;i++)
		    {
			pool = getConnectionPool(poolname[i]);
			pool.markup();
		    }
		}
	}

	public static String getMarkdownURL(String pname, String file_ext)
	{
	    return "<a href=\"sysdbmgr"+file_ext+"?N=PHYSICAL&C="+pname+"&A=MARKDOWN\">Markdown</a>";
	}

	public static String getMarkupURL(String pname, String file_ext)
	{
	    return "<a href=\"sysdbmgr"+file_ext+"?N=PHYSICAL&C="+pname+"&A=MARKUP\">Markup</a>";
	}

	public static void writeHTML(java.io.Writer out, String file_ext) throws java.io.IOException
	{
            String poolname[] = connpools.getNames();
	    if (poolname.length > 0)
	    {
	        out.write("    <h2>Physical Connection</h2>\n");
                for(int i=0;i<poolname.length;i++)
                {
                    DBConnectionPool lc = getConnectionPool(poolname[i]);
		    out.write("    <div>"+lc.getPoolName()+(lc.available()?
			      " is Available, " + getMarkdownURL(lc.getPoolName(),file_ext):
			      " need "+getMarkupURL(lc.getPoolName(),file_ext))+"("+
			      lc.getMaxConns()+"/"+lc.getBusyConns()+"/"+lc.getIdleConns()+
			      ")</div>\n");
                }
	    }
	}


	public static DBConnectionPool getConnectionPool(String poolname) 
	{
		String p_name=poolname;
		if (poolname == null || poolname.trim().length()==0)
		{
			p_name = "DEFAULT";
		}
		if (connpools.exists(p_name))
		{
			return (DBConnectionPool)(connpools.getValue(p_name));
		}
		else
		{
			DBConnectionPool pool = new DBConnectionPool();
			pool.setPoolName(p_name);
			connpools.add(p_name,java.sql.Types.JAVA_OBJECT);
			connpools.setValue(p_name,pool);
			return pool;
		}
	}

	public static void modifyConnectionPool ( String dbname, String dbtype,
		String dbhost, String dbuser, String dbpass, String property, 
                String locale, String initconns, String maxconns)
	{
		int i;
		getConnectionPool(dbname).freePool();
		getConnectionPool(dbname).setDBType(dbtype);
		getConnectionPool(dbname).setDBHost(dbhost);
		getConnectionPool(dbname).setDBUser(dbuser);
		getConnectionPool(dbname).setDBPass(dbpass);
		getConnectionPool(dbname).setDBProperty(property);
		getConnectionPool(dbname).setDBLocale(locale);
		try { 
			if (maxconns != null)
			{
				i = Integer.valueOf(maxconns).intValue();
				getConnectionPool(dbname).setMaxConns(i);
			}
		} catch (java.lang.NumberFormatException nfe) {}
		try { 
			if (initconns != null)
			{
				i = Integer.valueOf(initconns).intValue();
				getConnectionPool(dbname).setInitConns(i);
			}
		} catch (java.lang.NumberFormatException nfe) {}		
	}

	public static void freeAllConnectionPool()
	{
		int i;
		DBConnectionPool pool;
		String poolname[] = connpools.getNames();
		for(i=0;i<poolname.length;i++)
		{
			pool = getConnectionPool(poolname[i]);
			if (pool != null) pool.freePool();
		}
		connpools.removeAll();
	}

	public static void freeConnectionPool()
	{
		freeConnectionPool("DEFAULT");	
	}

	public static void freeConnectionPool(String  poolname)
	{
		String p_name=poolname;
		if (poolname == null || poolname.trim().length()==0)
		{
			p_name = "DEFAULT";
		}
		if (connpools.exists(p_name))
		{
			DBConnectionPool pool = getConnectionPool(p_name);
			if (pool != null)
			{
				pool.freePool();
				connpools.remove(p_name);
			}
		}
	}

	public static void resetConnectionPool()
	{
		freeConnectionPool("DEFAULT");	
	}

	public static void resetConnectionPool(String  poolname)
	{
		String p_name=poolname;
		if (poolname == null || poolname.trim().length()==0)
		{
			p_name = "DEFAULT";
		}
		if (connpools.exists(p_name))
		{
			DBConnectionPool pool = (DBConnectionPool)(connpools.getValue(p_name));
			if (pool != null) pool.freePool();
		}
	}

	public static void resetAllConnectionPool()
	{
		int i;
		DBConnectionPool pool;
		String poolname[] = connpools.getNames();
		for(i=0;i<poolname.length;i++)
		{
			pool = getConnectionPool(poolname[i]);
			if (pool != null)
				pool.freePool();
		}
	}
}