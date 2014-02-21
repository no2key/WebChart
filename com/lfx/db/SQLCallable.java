package com.lfx.db;

import java.sql.CallableStatement;

public final class SQLCallable
{
	public CallableStatement stmt = null;
	public String paramNames[] = {};
	public String paramTypes[] = {};

	public SQLCallable(CallableStatement pt,String p_names[],String p_types[])
	{
		stmt = pt;
		paramNames = p_names;
		paramTypes = p_types;
	}

	protected void finalize() throws java.lang.Throwable
	{
		if (stmt != null)
		{
			try { 
				stmt.close();
				stmt = null;
			} 
			 catch (java.sql.SQLException sqle)
			{}
		}
	}

	public void bind(VariableTable vt) throws java.sql.SQLException
	{
		int rows;
		Object val;
		try {
			stmt.clearParameters();
		}
		 catch (java.sql.SQLException sqle)
		{}
		if (paramNames.length>0)
		{
		    for (rows=0;rows<paramNames.length;rows++)
	    	    {
			val = vt.getValue(paramNames[rows]);
			if (paramTypes[rows].equals("IN"))
			{
				if (val==null)
					stmt.setNull(rows+1,1);
				else
				{
					if (vt.getType(paramNames[rows]) != java.sql.Types.LONGVARCHAR &&
						vt.getType(paramNames[rows]) != java.sql.Types.LONGVARBINARY )
						stmt.setObject(rows+1,val);
					else if (vt.getType(paramNames[rows]) == java.sql.Types.LONGVARCHAR)
					{
						java.io.StringReader long_var =
							new java.io.StringReader(val.toString());
						stmt.setCharacterStream(rows+1,long_var,16384);
						//long_var.close();
					}
					else if (vt.getType(paramNames[rows]) == java.sql.Types.LONGVARBINARY)
					{
						java.io.File os_file = new java.io.File(val.toString());
						if (os_file.exists() && os_file.isFile() && os_file.canRead())
						{
							try {
								java.io.FileInputStream long_var =
									new java.io.FileInputStream(os_file);
								stmt.setBinaryStream(rows+1,long_var,16384);
								//long_var.close();
							} catch (java.io.IOException ioe) {}
						}
						else
							stmt.setNull(rows+1,1);
					}
				}
			}
			else if (paramTypes[rows].equals("OUT"))
			{
				stmt.registerOutParameter(rows+1,vt.getType(paramNames[rows]));
			}
			else if (paramTypes[rows].equals("INOUT"))
			{
				if (val==null)
					stmt.setNull(rows+1,1);
				else
				{
					if (vt.getType(paramNames[rows]) != java.sql.Types.LONGVARCHAR &&
						vt.getType(paramNames[rows]) != java.sql.Types.LONGVARBINARY )
						stmt.setObject(rows+1,val);
					else if (vt.getType(paramNames[rows]) != java.sql.Types.LONGVARCHAR)
					{
						java.io.StringReader long_var =
							new java.io.StringReader(val.toString());
						stmt.setCharacterStream(rows+1,long_var,16384);
						//long_var.close();
					}
					else if (vt.getType(paramNames[rows]) != java.sql.Types.LONGVARBINARY)
					{
						java.io.File os_file = new java.io.File(val.toString());
						if (os_file.exists() && os_file.isFile() && os_file.canRead())
						{
							try {
								java.io.FileInputStream long_var =
									new java.io.FileInputStream(os_file);
								stmt.setBinaryStream(rows+1,long_var,16384);
								//long_var.close();
							} catch (java.io.IOException ioe) {}
						}
						else
							stmt.setNull(rows+1,1);
					}
				}
				stmt.registerOutParameter(rows+1,vt.getType(paramNames[rows]));
			}
		   }
		}
	}
	public void fetch(VariableTable vt) throws java.sql.SQLException
	{
		int rows,j=0,len=0;
		Object val;
		if (paramNames.length>0)
		{
		    for (rows=0;rows<paramNames.length;rows++)
		    {
			if (paramTypes[rows].equals("OUT"))
			{
			  j++;
		  	  val = stmt.getObject(j);
			  if (val instanceof java.sql.ResultSet)
			  {
			  	try {
			  		java.sql.ResultSet rs = (java.sql.ResultSet)val;
			  		rs.close();
			  	} catch (java.sql.SQLException sqle) {}
			  	vt.setValue(paramNames[rows],null);
			  }
			  else
			    	vt.setValue(paramNames[rows],val);
			}
			else if (paramTypes[rows].equals("INOUT"))
			{
				j++;
		  	   	val = stmt.getObject(j);
			    	vt.setValue(paramNames[rows],val);
			}
		    }
		}
	}
}