package com.lfx.db;

public class DBLogicalManager extends Object
{
	private static VariableTable _pool = new VariableTable();

	public static DBLogicalConnection getLogicalConnection(String name)
	{
		if (_pool.exists(name))
		{
		    return (DBLogicalConnection) (_pool.getValue(name));
		}
		return null;
	}

	public static void putLogicalConnection(DBLogicalConnection conn)
	{
		if (conn != null)
		{
			_pool.add(conn.toString(), java.sql.Types.JAVA_OBJECT);
			_pool.setValue(conn.toString(), conn);
		}
	}

	public static boolean exists(String name)
	{
		return _pool.exists(name);
	}

	public static void markdown(String name)
	{
		if (_pool.exists(name))
		{
			getLogicalConnection(name).markdown();
		}
	}

	public static void markup(String name)
	{
		if (_pool.exists(name))
		{
			getLogicalConnection(name).markup();
		}
	}

	public static boolean available(String name) 
	{
		if (_pool.exists(name))
		{
			return getLogicalConnection(name).available();
		}
		return false;
	}

	public static void markupAll()
	{
            String keys[] = _pool.getNames();
            for(int i=0;i<keys.length;i++)
            {
                DBLogicalConnection lc = (DBLogicalConnection)(_pool.getValue(keys[i]));
		lc.markup();
            }
	}

	public static String getMarkdownURL(String pname, String file_ext)
	{
	    return "<a href=\"sysdbmgr"+file_ext+"?N=LOGICAL&C="+pname+"&A=MARKDOWN\">Markdown</a>";
	}

	public static String getMarkupURL(String pname, String file_ext)
	{
	    return "<a href=\"sysdbmgr"+file_ext+"?N=LOGICAL&C="+pname+"&A=MARKUP\">Markup</a>";
	}

	public static void writeHTML(java.io.Writer out, String file_ext) throws java.io.IOException
	{
            String keys[] = _pool.getNames();
	    if (keys.length > 0)
	    {
	        out.write("    <h2>Logical Connection</h2>\n");
                for(int i=0;i<keys.length;i++)
                {
                    DBLogicalConnection lc = (DBLogicalConnection)(_pool.getValue(keys[i]));
		    out.write("    <div>"+lc.toString()+(lc.available()?
			      " is Available, " + getMarkdownURL(lc.toString(),file_ext):
			      " need "+getMarkupURL(lc.toString(),file_ext))+"</div>\n");
                }
	    }
	}

	public static int getLogicalConnectionMode(String mode)
	{
		if ("FIRST".equalsIgnoreCase(mode))
			return DBLogicalConnection.MODE_FIRST;
		else if ("RANDOM".equalsIgnoreCase(mode))
			return DBLogicalConnection.MODE_RANDOM;
		else if ("FAILOVER".equalsIgnoreCase(mode))
			return DBLogicalConnection.MODE_FAILOVER;
		else if ("POSITION".equalsIgnoreCase(mode))
			return DBLogicalConnection.MODE_POSITION;
		else if ("RANGE".equalsIgnoreCase(mode))
			return DBLogicalConnection.MODE_RANGE;
		else if ("LIST".equalsIgnoreCase(mode))
			return DBLogicalConnection.MODE_LIST;
		else
			return DBLogicalConnection.MODE_FIRST;
	}

	public static void loadDBConfig(VariableTable vt)
	{
		int i,k;
		java.util.Vector dblist = null;
                if (vt.exists("LOGICAL.DBLIST"))
                {
  		    dblist = TextUtils.getWords(vt.getString("LOGICAL.DBLIST"),"|");
                }
                else
                {
                    dblist = new java.util.Vector();
		    String lnames[] = vt.getNames();
		    if (lnames != null && lnames.length > 0)
                    {
                       for(i=0;i<lnames.length;i++)
                       {
                           if(lnames[i].startsWith("LOGICAL."))
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
			java.util.Vector dbline = TextUtils.getWords(vt.getString("LOGICAL."+dbname),"|");
			if (dbline.size() == 2)
			{
				DBLogicalConnection lc = new DBLogicalConnection(dbname,
				    getLogicalConnectionMode(dbline.elementAt(0).toString()),
				    dbline.elementAt(1).toString());
				lc.setRangeValues(vt.getString("LOGICAL."+dbname+".VALUES"));
				lc.setListValues(vt.getString("LOGICAL."+dbname+".VALUES"));
				putLogicalConnection(lc);
			}
		}
	}


	public static DBPooledConnection getPoolConnection() throws ConnectTimeoutException, DatabaseMarkdownException
	{
		DBLogicalConnection c = getLogicalConnection("DEFAULT");
		return c.getConnection();
	}

	public static DBPooledConnection getPoolConnection(String name) throws ConnectTimeoutException, DatabaseMarkdownException
	{
		String poolname = "DEFAULT";
		if (name != null)
		{
			poolname = name;
		}
		if (exists(poolname))
		{
			DBLogicalConnection c = getLogicalConnection(poolname);
			return c.getConnection();
		}
		throw new DatabaseMarkdownException("LOGICAL."+poolname);
	}

	public static DBPooledConnection getPoolConnection(int rule) throws ConnectTimeoutException, DatabaseMarkdownException
	{
		DBLogicalConnection c = getLogicalConnection("DEFAULT");
		return c.getConnection(rule);
	}

	public static DBPooledConnection getPoolConnection(String name, String srule) throws ConnectTimeoutException, DatabaseMarkdownException
	{
		int rule = 0;
		String poolname = "DEFAULT";
		if (name != null)
		{
			poolname = name;
		}
		if (srule != null)
		{
		   try { rule = Integer.parseInt(srule); } catch (NumberFormatException nfe) {}
		}
		if (exists(poolname))
		{
			DBLogicalConnection c = getLogicalConnection(poolname);
			return c.getConnection(rule);
		}
		throw new DatabaseMarkdownException("LOGICAL."+poolname);
	}

	public static DBPooledConnection getPoolConnection(String name, int rule) throws ConnectTimeoutException, DatabaseMarkdownException
	{
		String poolname = "DEFAULT";
		if (name != null)
		{
			poolname = name;
		}
		if (exists(poolname))
		{
			DBLogicalConnection c = getLogicalConnection(poolname);
			return c.getConnection(rule);
		}
		throw new DatabaseMarkdownException("LOGICAL."+poolname);
	}
}