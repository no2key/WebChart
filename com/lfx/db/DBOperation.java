package com.lfx.db;
import java.sql.Types;
public final class DBOperation
{
        private static int MAX_QUERY_ROWS   =10000;
	private static int SQL_QUERY_TIMEOUT= 180;

	private static final String JDBC_CONNECT[] = {
		"ORACLE","SYBASE","DB2APP",
		"DB2NET","MSSQL","DDORA","DDSYB","DDDB2",
                "DDSQL","DDINFX","MYSQL","ODBC", "SQLITE",
                "PGSQL", "HIVE"};

	private static final String JDBC_DRIVER[] = {
		"oracle.jdbc.driver.OracleDriver",
		"com.sybase.jdbc2.jdbc.SybDriver",
		"COM.ibm.db2.jdbc.app.DB2Driver",
		"COM.ibm.db2.jdbc.net.DB2Driver",
		"com.microsoft.jdbc.sqlserver.SQLServerDriver",
		"com.ddtek.jdbc.oracle.OracleDriver",
		"com.ddtek.jdbc.sybase.SybaseDriver",
		"com.ddtek.jdbc.db2.DB2Driver",
		"com.ddtek.jdbc.sqlserver.SQLServerDriver",
		"com.ddtek.jdbc.informix.InformixDriver",
		"com.mysql.jdbc.Driver",
		"sun.jdbc.odbc.JdbcOdbcDriver",
                "org.sqlite.JDBC",
                "org.postgresql.Driver",
		"org.apache.hadoop.hive.jdbc.HiveDriver"};

	private static final String JDBC_URL[]={
		"jdbc:oracle:thin:@", 
		"jdbc:sybase:Tds:",
		"jdbc:db2:",
		"jdbc:db2://",
		"jdbc:microsoft:sqlserver://",
		"jdbc:datadirect:oracle://",
		"jdbc:datadirect:sybase://",
		"jdbc:datadirect:db2://",
		"jdbc:datadirect:sqlserver://",
		"jdbc:datadirect:infomix://",
		"jdbc:mysql://",
		"jdbc:odbc:",
                "jdbc:sqlite:",
                "jdbc:postgresql://",
		"jdbc:hive://"};

	private static final String JDBC_PROPERTY[]={
		"-oracle.jdbc.V8Compatible true",
		"","","","-SelectMethod cursor",
		"",
		"-SelectMethod Cursor",
		"",
		"-SelectMethod Cursor",
		"-SelectMethod Cursor",
		"-autoReconnect true","","","",""};

	public final static void setQueryMaxRows(int rows)
	{
		MAX_QUERY_ROWS   = rows;
		if (MAX_QUERY_ROWS   < 100) MAX_QUERY_ROWS   =100;
	}

	public final static void setQueryTimeout(int secs)
	{
		SQL_QUERY_TIMEOUT = secs;
		if (SQL_QUERY_TIMEOUT   < 5) SQL_QUERY_TIMEOUT   =5;
	}


	public final static String toString(java.util.Date d,String fmt)
	{
		java.text.SimpleDateFormat sdft = new java.text.SimpleDateFormat(fmt);
		return sdft.format(d);
	}

	public final static String toString(java.util.Date d)
	{
		java.text.SimpleDateFormat sdft = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdft.format(d);
	}

	public final static String getDay(String fmt)
	{
		java.text.SimpleDateFormat sdft = new java.text.SimpleDateFormat(fmt);
		return sdft.format(new java.util.Date());
	}

	public final static String getDay()
	{
		java.text.SimpleDateFormat sdft = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdft.format(new java.util.Date());
	}

	public final static String getElapsed(long millsec)
	{
		long p,p_millsec,p_sec,p_min,p_hour;
		p = millsec / 1000;
		p_millsec = millsec % 1000;
		p_sec = p % 60;
		p=p/60;
		p_min = p % 60;
		p_hour = p / 60;
		return (p_hour<10?"0":"")+String.valueOf(p_hour)+":"+
		       (p_min<10?"0":"")+String.valueOf(p_min)+":"+
		       (p_sec<10?"0":"")+String.valueOf(p_sec)+"."+String.valueOf(p_millsec);
	}

	public static final DBRowCache getDBRowCache()
	{
		return new SimpleDBRowCache();
	}

	public static final Object getObject(java.sql.Connection db,String sql,VariableTable vt)
		throws java.sql.SQLException 
	{
		Object o_value=null;
		java.sql.SQLException sqle=null;
		SQLStatement sql_stmt = null;
		java.sql.ResultSet rset = null;
		try {
			sql_stmt = prepareStatement(db,sql,vt);
			sql_stmt.bind(vt);
			rset = sql_stmt.stmt.executeQuery();
			rset.next();
			o_value = rset.getObject(1);
		} 
		 catch (java.sql.SQLException sqle1)
		{
			sqle = sqle1;
		}
		try {
			if (rset != null)
			{
				rset.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt!=null)
			{
				sql_stmt.stmt.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		if (sqle != null)
			throw sqle;
		return o_value;
	}

	public static final String getString(java.sql.Connection db,String sql,VariableTable vt)
		throws java.sql.SQLException 
	{
		String s_value=null;
		DBRowCache rowcache = new SimpleDBRowCache();
		java.sql.SQLException sqle=null;
		SQLStatement sql_stmt = null;
		java.sql.ResultSet rset = null;
		try {
			sql_stmt = prepareStatement(db,sql,vt);
			sql_stmt.bind(vt);
			rset = sql_stmt.stmt.executeQuery();
			fetch(rset,rowcache,1);
			Object val = rowcache.getItem(1,1);
			if (val != null)
				s_value = val.toString();
		} 
		 catch (java.sql.SQLException sqle1)
		{
			sqle = sqle1;
		}
		try {
			if (rset != null)
			{
				rset.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt!=null)
			{
				sql_stmt.stmt.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		if (sqle != null)
			throw sqle;
		return s_value;
	}

	public static final int getInt(java.sql.Connection db,String sql,VariableTable vt)
		throws java.sql.SQLException 
	{
		int i_value=0;
		java.sql.SQLException sqle=null;
		SQLStatement sql_stmt = null;
		java.sql.ResultSet rset = null;
		try {
			sql_stmt = prepareStatement(db,sql,vt);
			sql_stmt.bind(vt);
			rset = sql_stmt.stmt.executeQuery();
			rset.next();
			i_value = rset.getInt(1);
		} 
		 catch (java.sql.SQLException sqle1)
		{
			sqle = sqle1;
		}
		try {
			if (rset != null)
			{
				rset.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt!=null)
			{
				sql_stmt.stmt.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		if (sqle != null)
			throw sqle;
		return i_value;
	}

	public static final long getLong(java.sql.Connection db,String sql,VariableTable vt)
		throws java.sql.SQLException 
	{
		long l_value=0;
		java.sql.SQLException sqle=null;
		SQLStatement sql_stmt = null;
		java.sql.ResultSet rset = null;
		try {
			sql_stmt = prepareStatement(db,sql,vt);
			sql_stmt.bind(vt);
			rset = sql_stmt.stmt.executeQuery();
			rset.next();
			l_value = rset.getLong(1);
		} 
		 catch (java.sql.SQLException sqle1)
		{
			sqle = sqle1;
		}
		try {
			if (rset != null)
			{
				rset.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt!=null)
			{
				sql_stmt.stmt.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		if (sqle != null)
			throw sqle;
		return l_value;
	}

	public static final float getFloat(java.sql.Connection db,String sql,VariableTable vt)
		throws java.sql.SQLException 
	{
		float f_value=0f;
		java.sql.SQLException sqle=null;
		SQLStatement sql_stmt = null;
		java.sql.ResultSet rset = null;
		try {
			sql_stmt = prepareStatement(db,sql,vt);
			sql_stmt.bind(vt);
			rset = sql_stmt.stmt.executeQuery();
			rset.next();
			f_value = rset.getFloat(1);
		} 
		 catch (java.sql.SQLException sqle1)
		{
			sqle = sqle1;
		}
		try {
			if (rset != null)
			{
				rset.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt!=null)
			{
				sql_stmt.stmt.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		if (sqle != null)
			throw sqle;
		return f_value;
	}

	public static final double getDouble(java.sql.Connection db,String sql,VariableTable vt)
		throws java.sql.SQLException 
	{
		double d_value=0f;
		java.sql.SQLException sqle=null;
		SQLStatement sql_stmt = null;
		java.sql.ResultSet rset = null;
		try {
			sql_stmt = prepareStatement(db,sql,vt);
			sql_stmt.bind(vt);
			rset = sql_stmt.stmt.executeQuery();
			rset.next();
			d_value = rset.getDouble(1);
		} 
		 catch (java.sql.SQLException sqle1)
		{
			sqle = sqle1;
		}
		try {
			if (rset != null)
			{
				rset.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt!=null)
			{
				sql_stmt.stmt.close();
			}
		}
		 catch (java.sql.SQLException sqle1) {}
		if (sqle != null)
			throw sqle;
		return d_value;
	}

	public static final SQLStatement prepareStatement(
		java.sql.Connection conn,String cmd)
		throws java.sql.SQLException
	{
		return prepareStatement(conn,cmd,null,java.sql.ResultSet.TYPE_FORWARD_ONLY,
			java.sql.ResultSet.CONCUR_READ_ONLY);
	}
	public static final SQLStatement prepareStatement(
		java.sql.Connection conn,String cmd,VariableTable vt)
		throws java.sql.SQLException
	{
		return prepareStatement(conn,cmd,vt,java.sql.ResultSet.TYPE_FORWARD_ONLY,
			java.sql.ResultSet.CONCUR_READ_ONLY);
	}
	public static final SQLStatement prepareStatement(
		java.sql.Connection conn,String cmd,VariableTable vt,int scrolltype,int rwmode)
		throws java.sql.SQLException
	{
		java.sql.PreparedStatement pstmt=null;
		SQLQuery sql_query = SQLConvert.parseSQL(cmd==null?"":cmd,vt);
		pstmt = conn.prepareStatement(sql_query.getDestSQL(),scrolltype,rwmode);
		/* pstmt.setQueryTimeout(SQL_QUERY_TIMEOUT); */
		if (pstmt == null)
		{
			throw new java.sql.SQLException("Null Statument Returned!");
		}
		pstmt.setFetchSize(20);
		return new SQLStatement(pstmt,sql_query.getParamNames(),sql_query.getParamTypes());
	}
	public static final SQLCallable prepareCall(
		java.sql.Connection conn,String cmd)
		throws java.sql.SQLException
	{
		return prepareCall(conn,cmd,null,java.sql.ResultSet.TYPE_FORWARD_ONLY,
			java.sql.ResultSet.CONCUR_READ_ONLY);
	}
	public static final SQLCallable prepareCall(
		java.sql.Connection conn,String cmd,VariableTable vt)
		throws java.sql.SQLException
	{
		return prepareCall(conn,cmd,vt,java.sql.ResultSet.TYPE_FORWARD_ONLY,
			java.sql.ResultSet.CONCUR_READ_ONLY);
	}
	public static final SQLCallable prepareCall(
		java.sql.Connection conn,String cmd,VariableTable vt,int scrolltype,int rwmode)
		throws java.sql.SQLException
	{
		java.sql.CallableStatement pstmt=null;
		SQLQuery sql_query = SQLConvert.parseCall(cmd==null?"":cmd,vt);
		pstmt = conn.prepareCall("{ "+sql_query.getDestSQL()+" }",scrolltype,rwmode);
		/* pstmt.setQueryTimeout(SQL_QUERY_TIMEOUT); */
		if (pstmt == null)
		{
			throw new java.sql.SQLException("Null Statument Returned!");
		}
		pstmt.setFetchSize(20);
		return new SQLCallable(pstmt,sql_query.getParamNames(),sql_query.getParamTypes());
	}
	public static final int fetch(java.sql.ResultSet rs,DBRowCache rowcache) 
		throws java.sql.SQLException
	{
		return fetch(rs,rowcache,100);
	}
	public static final int fetch(java.sql.ResultSet rs,DBRowCache rowcache,int rows) 
		throws java.sql.SQLException
	{
		int i=0,j=0,ins_row=0,len;
		Object val,record[];
		if (rowcache.getColumnCount()==0)
		{
			java.sql.ResultSetMetaData rs_meta = rs.getMetaData();
			for(i = 1; i <= rs_meta.getColumnCount();i++)
			{
				if(rs_meta.getColumnName(i)!=null)
				{
					rowcache.addColumn(rs_meta.getColumnName(i),
						rs_meta.getColumnType(i));
				}
				else
				{
					j=1;
					while(rowcache.findColumn("NULL"+j)!=0)j++;
					rowcache.addColumn("NULL"+j,
						rs_meta.getColumnType(i));					
				}
			}
		}
		if (rowcache.getColumnCount() == 0) return 0;
		i=rowcache.getRowCount();
		while( i < rows && rs.next())
		{
			record = new Object[rowcache.getColumnCount()];
			for(j=1;j<=rowcache.getColumnCount();j++)
			{
				  val = null;
				  if (rowcache.getColumnType(j) != java.sql.Types.LONGVARCHAR &&
					rowcache.getColumnType(j) != java.sql.Types.LONGVARBINARY  &&
					rowcache.getColumnType(j) != java.sql.Types.CLOB &&
					rowcache.getColumnType(j) != java.sql.Types.BLOB)
				  {
					if (rowcache.getColumnType(j) == java.sql.Types.BINARY ||
					    rowcache.getColumnType(j) == java.sql.Types.VARBINARY)
						val = rs.getString(j);
					else if (rowcache.getColumnType(j) == java.sql.Types.TIMESTAMP)
					  	val = rs.getTimestamp(j);
					else
					  	val = rs.getObject(j);
				  }
				  else
				  {
					java.io.Reader long_out = rs.getCharacterStream(j);
					if (long_out == null)
					{
						val = null;
					}
					else
					{
						char[] long_buf=new char[16384];
						try {
							len = long_out.read(long_buf);
							if (len > 0)
								val = String.valueOf(long_buf,0,len);
							long_out.close();
						} 
						 catch ( java.io.IOException ioe) {};
					}
				  }
				  record[j - 1] = val;
			}
			i = rowcache.appendRow(record);
		}
		return i;
	}
	public static final int fetchString(java.sql.ResultSet rs,DBRowCache rowcache) 
		throws java.sql.SQLException
	{
		return fetchString(rs,rowcache,100);
	}
	public static final int fetchString(java.sql.ResultSet rs,DBRowCache rowcache,int rows) 
		throws java.sql.SQLException
	{
		int i=0,j=0,ins_row=0,len;
		String val;
		Object record[];
		if (rowcache.getColumnCount()==0)
		{
			java.sql.ResultSetMetaData rs_meta = rs.getMetaData();
			for(i = 1; i <= rs_meta.getColumnCount();i++)
			{
				if(rs_meta.getColumnName(i)!=null)
				{
					rowcache.addColumn(rs_meta.getColumnName(i),
						rs_meta.getColumnType(i));
				}
				else
				{
					j=1;
					while(rowcache.findColumn("NULL"+j)!=0)j++;
					rowcache.addColumn("NULL"+j,
						rs_meta.getColumnType(i));					
				}
			}
		}
		if (rowcache.getColumnCount() == 0) return 0;
		i=rowcache.getRowCount();
		while( i < rows && rs.next())
		{
			record = new Object[rowcache.getColumnCount()];
			for(j=1;j<=rowcache.getColumnCount();j++)
			{
				  val = null;
				  if (rowcache.getColumnType(j) != java.sql.Types.LONGVARCHAR &&
					rowcache.getColumnType(j) != java.sql.Types.LONGVARBINARY &&
					rowcache.getColumnType(j) != java.sql.Types.CLOB &&
					rowcache.getColumnType(j) != java.sql.Types.BLOB)
					val = rs.getString(j);
				  else 
				  {
					java.io.Reader long_out = rs.getCharacterStream(j);
					if (long_out == null)
					{
						val = null;
					}
					else
					{
						char[] long_buf=new char[16384];
						try {
							len = long_out.read(long_buf);
							if (len > 0)
								val = String.valueOf(long_buf,0,len);
							long_out.close();
						} 
						 catch ( java.io.IOException ioe) {};
					}
				  }
				  record[j - 1] = val;
			}
			i = rowcache.appendRow(record);
		}
		return i;
	}
	public static final int[] addBatch(SQLStatement sql_stmt,DBRowCache row_cache)
		throws java.sql.BatchUpdateException
	{
		return addBatch(sql_stmt,null,row_cache,1,row_cache.getRowCount());
	}
	public static final int[] addBatch(SQLStatement sql_stmt,
		VariableTable vt,DBRowCache row_cache)
		throws java.sql.BatchUpdateException
	{
		return addBatch(sql_stmt,vt,row_cache,1,row_cache.getRowCount());
	}
	public static final int[] addBatch(SQLStatement sql_stmt,
		DBRowCache row_cache,int start_row,int end_row)
		throws java.sql.BatchUpdateException
	{
		return addBatch(sql_stmt,null,row_cache,start_row,end_row);	
	}
	public static final int[] addBatch(SQLStatement sql_stmt,
		VariableTable vt,DBRowCache row_cache,int start_row,int end_row)
		throws java.sql.BatchUpdateException
	{
		int result[]={},arg_position[]={},arg_type[]={},added_rows=0;
		int rows=0,batch_loop=1;
		String varname;
		Object val=null;
		result = new int[end_row - start_row + 1];
		if (sql_stmt.paramNames.length>0)
		{
		    arg_position = new int [sql_stmt.paramNames.length];
		    arg_type = new int [sql_stmt.paramNames.length];
		    for (rows=0;rows<sql_stmt.paramNames.length;rows++)
		    {
			arg_position[rows] = row_cache.findColumn(sql_stmt.paramNames[rows]);
			if (arg_position[rows] > 0)
				arg_type[rows] = row_cache.getColumnType(arg_position[rows]);
			else
				arg_type[rows] = vt.getType(sql_stmt.paramNames[rows]);
		    }
		}
		for(batch_loop = start_row;batch_loop <= end_row;batch_loop++)
		{
			try {
				if (sql_stmt.paramNames.length>0)
				{
				    for (rows=0;rows<sql_stmt.paramNames.length;rows++)
    	    			    {
					if (arg_position[rows] > 0)
					{	
						val = row_cache.getItem(batch_loop,arg_position[rows]);
					}
					else
					{
						val = vt.getValue(sql_stmt.paramNames[rows]);
					}
					if (val==null)
						sql_stmt.stmt.setNull(rows+1,1);
					else
					{
						if (arg_type[rows] != java.sql.Types.LONGVARCHAR &&
							arg_type[rows] != java.sql.Types.LONGVARBINARY )
						{
							sql_stmt.stmt.setObject(rows+1,val);
						}
						else if (arg_type[rows] == java.sql.Types.LONGVARCHAR)
						{
							java.io.StringReader long_var =
								new java.io.StringReader(val.toString());
							sql_stmt.stmt.setCharacterStream(rows+1,long_var,16384);
						}
						else if (arg_type[rows] == java.sql.Types.LONGVARBINARY)
						{
							java.io.File os_file = new java.io.File(val.toString());
							if (os_file.exists() && os_file.isFile() && os_file.canRead())
							{
								try {
									java.io.FileInputStream long_var =
										new java.io.FileInputStream(os_file);
									sql_stmt.stmt.setBinaryStream(rows+1,long_var,16384);
								} catch (java.io.IOException ioe) {}
							}
							else
								sql_stmt.stmt.setNull(rows+1,1);
						}
					}
			    	    }
	   	        	}
				sql_stmt.stmt.addBatch();
			}
			 catch (java.sql.SQLException sqle)
			{
				if (added_rows > 0)
					result = new int[added_rows];
				for(int i=0;i<added_rows;i++)
				{
					result[i]=1;
				}

				try {
					sql_stmt.stmt.clearParameters();
				} catch(java.sql.SQLException sqle1) {}

				java.sql.BatchUpdateException bue = new 
					java.sql.BatchUpdateException(sqle.getMessage(),sqle.getSQLState(),
						sqle.getErrorCode(),result);
				throw bue;
			}
		}
		if (added_rows > 0)
			result = new int[added_rows];
		for(int i=0;i<added_rows;i++)
		{
			result[i]=1;
		}
		return result;
	}
	
	public static final int[] executeUpdate(SQLStatement sql_stmt,DBRowCache row_cache)
		throws java.sql.BatchUpdateException
	{
		return executeUpdate(sql_stmt,null,row_cache,1,row_cache.getRowCount());
	}
	public static final int[] executeUpdate(SQLStatement sql_stmt,
		VariableTable vt,DBRowCache row_cache)
		throws java.sql.BatchUpdateException
	{
		return executeUpdate(sql_stmt,vt,row_cache,1,row_cache.getRowCount());
	}
	public static final int[] executeUpdate(SQLStatement sql_stmt,
		DBRowCache row_cache,int start_row,int end_row)
		throws java.sql.BatchUpdateException
	{
		return executeUpdate(sql_stmt,null,row_cache,start_row,end_row);	
	}

	public static final int[] executeUpdate(SQLStatement sql_stmt,
		VariableTable vt,DBRowCache row_cache,int start_row,int end_row)
		throws java.sql.BatchUpdateException
	{
		int result[]={},updated_rows[]={},arg_position[]={},arg_type[]={},added_rows=0;
		int rows,batch_loop;
		String varname;
		Object val;
		result = new int[end_row - start_row + 1];
		if (sql_stmt.paramNames.length>0)
		{
		    arg_position = new int [sql_stmt.paramNames.length];
		    arg_type = new int [sql_stmt.paramNames.length];
		    for (rows=0;rows<sql_stmt.paramNames.length;rows++)
		    {
			arg_position[rows] = row_cache.findColumn(sql_stmt.paramNames[rows]);
			if (arg_position[rows] > 0)
				arg_type[rows] = row_cache.getColumnType(arg_position[rows]);
			else
				arg_type[rows] = vt.getType(sql_stmt.paramNames[rows]);
		    }
		}
		for(batch_loop = start_row;batch_loop <= end_row;batch_loop++)
		{
			try {
				if (sql_stmt.paramNames.length>0)
				{
				    for (rows=0;rows<sql_stmt.paramNames.length;rows++)
	    	    		    {
					if (arg_position[rows] > 0)
					{	
						val = row_cache.getItem(batch_loop,arg_position[rows]);
					}
					else
					{
						val = vt.getValue(sql_stmt.paramNames[rows]);
					}
					if (val==null)
						sql_stmt.stmt.setNull(rows+1,1);
					else
					{
						if (arg_type[rows] != java.sql.Types.LONGVARCHAR &&
							arg_type[rows] != java.sql.Types.LONGVARBINARY )
						{
							sql_stmt.stmt.setObject(rows+1,val);
						}
						else if (arg_type[rows] == java.sql.Types.LONGVARCHAR)
						{
							java.io.StringReader long_var =
								new java.io.StringReader(val.toString());
							sql_stmt.stmt.setCharacterStream(rows+1,long_var,16384);
						}
						else if (arg_type[rows] == java.sql.Types.LONGVARBINARY)
						{
							java.io.File os_file = new java.io.File(val.toString());
							if (os_file.exists() && os_file.isFile() && os_file.canRead())
							{
								try {
									java.io.FileInputStream long_var =
										new java.io.FileInputStream(os_file);
									sql_stmt.stmt.setBinaryStream(rows+1,long_var,16384);
								} catch (java.io.IOException ioe) {}
							}
							else
								sql_stmt.stmt.setNull(rows+1,1);
						}
					}
			    	    }
	   	        	}
				result[batch_loop - start_row] = sql_stmt.stmt.executeUpdate();
				added_rows ++;
			}
			 catch(java.sql.SQLException sqle)
			{
				if (added_rows > 0)
					updated_rows = new int[added_rows];
				for(int i=0;i<added_rows;i++)
				{
					updated_rows[i] = result[i];
				}
				java.sql.BatchUpdateException bue = new 
					java.sql.BatchUpdateException(sqle.getMessage(),sqle.getSQLState(),
						sqle.getErrorCode(),updated_rows);
				throw bue;
			}
		}
		return result;
	}

	public static DBRowCache executeQuery(java.sql.Connection db,String query)
		throws java.sql.SQLException 
	{
		return executeQuery(db,query,null,MAX_QUERY_ROWS);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,String query,int maxrow)
		throws java.sql.SQLException 
	{
		return executeQuery(db,query,null,maxrow);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,String query,VariableTable vt)
		throws java.sql.SQLException 
	{
		return executeQuery(db,query,vt,MAX_QUERY_ROWS);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,String query,VariableTable vt,int maxrow)
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			sql_stmt.stmt.setMaxRows(maxrow);
			sql_stmt.bind(vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_rset = sql_stmt.stmt.executeQuery();
			DBOperation.fetch(sql_rset,data_rows,maxrow);
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
		return data_rows;
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,VariableTable vt,int r_col,int c_col,int v_col)
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,vt,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,VariableTable vt,int r_col,int c_col[],int v_col[])
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,vt,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,VariableTable vt,String r_col,String c_col,String v_col)
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,vt,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,VariableTable vt,String r_col,String c_col[],String v_col[])
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,vt,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,VariableTable vt,int r_col[],int c_col,int v_col)
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,vt,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,VariableTable vt,int r_col[],int c_col[],int v_col[])
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,vt,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,VariableTable vt,String r_col[],String c_col,String v_col)
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,vt,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,VariableTable vt,String r_col[],String c_col[],String v_col[])
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,vt,r_col,c_col,v_col);
	}


	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,int r_col,int c_col,int v_col)
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,int r_col,int c_col[],int v_col[])
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,String r_col,String c_col,String v_col)
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,String r_col,String c_col[],String v_col[])
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,int r_col[],int c_col,int v_col)
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,int r_col[],int c_col[],int v_col[])
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,String r_col[],String c_col,String v_col)
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,r_col,c_col,v_col);
	}

	public static DBRowCache executeQuery(java.sql.Connection db,
		String query,String r_col[],String c_col[],String v_col[])
		throws java.sql.SQLException
	{
		return executeCrossTab(db,query,r_col,c_col,v_col);
	}

	// --
	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,int r_col,int c_col,int v_col)
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,vt,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,int r_col,int c_col[],int v_col[])
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,vt,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,String r_col,String c_col,String v_col)
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,vt,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,String r_col,String c_col[],String v_col[])
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,vt,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,int r_col[],int c_col,int v_col)
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,vt,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,int r_col[],int c_col[],int v_col[])
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,vt,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,String r_col[],String c_col,String v_col)
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,vt,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,String r_col[],String c_col[],String v_col[])
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,vt,r_col,c_col,v_col);
	}


	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,int r_col,int c_col,int v_col)
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,int r_col,int c_col[],int v_col[])
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,String r_col,String c_col,String v_col)
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,String r_col,String c_col[],String v_col[])
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,int r_col[],int c_col,int v_col)
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,int r_col[],int c_col[],int v_col[])
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,String r_col[],String c_col,String v_col)
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,r_col,c_col,v_col);
	}

	public static void executeQuery(DBRowCache crosstab,java.sql.Connection db,
		String query,String r_col[],String c_col[],String v_col[])
		throws java.sql.SQLException
	{
		executeCrossTab(crosstab,db,query,r_col,c_col,v_col);
	}
	// --
	public static int executeUpdate(java.sql.Connection db,String query)
		throws java.sql.SQLException 
	{
		return executeUpdate(db,query,null);
	}
	public static int executeUpdate(java.sql.Connection db,String query,VariableTable vt)
		throws java.sql.SQLException 
	{
		int rt_code=0;
		SQLStatement sql_stmt = null;
		java.sql.SQLException sql_sqle = null;
		try {
			sql_stmt = DBOperation.prepareStatement(db,query);
			sql_stmt.bind(vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			rt_code = sql_stmt.stmt.executeUpdate();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
		return rt_code;
	}

	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,VariableTable vt,int r_col,int c_col[],int v_col[])
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		DBRowCache crosstab = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
		return crosstab;
	}

	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,VariableTable vt,int r_col,int c_col,int v_col)
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		DBRowCache crosstab = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
		return crosstab;
	}
	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,VariableTable vt,String r_col,String c_col,String v_col)
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		DBRowCache crosstab = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
		return crosstab;
	}

	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,VariableTable vt,String r_col,String c_col[],String v_col[])
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		DBRowCache crosstab = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
		return crosstab;
	}

	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,VariableTable vt,int r_col[],int c_col[],int v_col[])
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		DBRowCache crosstab = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
		return crosstab;
	}

	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,VariableTable vt,int r_col[],int c_col,int v_col)
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		DBRowCache crosstab = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
		return crosstab;
	}
	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,VariableTable vt,String r_col[],String c_col,String v_col)
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		DBRowCache crosstab = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
		return crosstab;
	}

	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,VariableTable vt,String r_col[],String c_col[],String v_col[])
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		DBRowCache crosstab = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
		return crosstab;
	}

        // Added at 2004.04.12

	public static void  executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,int r_col,int c_col[],int v_col[])
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
	}

	public static void  executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,int r_col,int c_col,int v_col)
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
	}
	public static void  executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,String r_col,String c_col,String v_col)
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
	}

	public static void  executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,String r_col,String c_col[],String v_col[])
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
	}

	public static void  executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,int r_col[],int c_col[],int v_col[])
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
	}

	public static void  executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,int r_col[],int c_col,int v_col)
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
	}
	public static void  executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,String r_col[],String c_col,String v_col)
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
	}

	public static void  executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt,String r_col[],String c_col[],String v_col[])
		throws java.sql.SQLException 
	{
		SQLStatement sql_stmt = null;
		java.sql.ResultSet sql_rset = null;
		java.sql.SQLException sql_sqle = null;
		int fetch_rows = 1000;
		DBRowCache data_rows = DBOperation.getDBRowCache();
		try {
			sql_stmt = DBOperation.prepareStatement(db,query,vt);
			if (sql_stmt.stmt == null)
			{
				throw new java.sql.SQLException("Null Statument Returned!");
			}
			sql_stmt.bind(vt);
			sql_rset = sql_stmt.stmt.executeQuery();
			while(fetch_rows == 1000)
			{
				fetch_rows = DBOperation.fetch(sql_rset,data_rows,1000);
				crosstab.addCrossTab(data_rows,r_col,c_col,v_col);
				data_rows.deleteAllRow();
				if (crosstab.getRowCount() >= MAX_QUERY_ROWS) break;
			}
			sql_rset.close();
		}
		 catch (java.sql.SQLException sqle)
		{
			sql_sqle = sqle;
		}
		try {
			if (sql_rset != null)
				sql_rset.close();
		} catch (java.sql.SQLException sqle) {}
		try {
			if (sql_stmt != null && sql_stmt.stmt != null)
				sql_stmt.stmt.close();
		} catch (java.sql.SQLException sqle) {}
		if (sql_sqle != null) throw sql_sqle;
	}


	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,int r_col,int c_col,int v_col) throws java.sql.SQLException 
	{
		return executeCrossTab(db,query,null,r_col,c_col,v_col);
	}
	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,String r_col,String c_col,String v_col) throws java.sql.SQLException 
	{
		return executeCrossTab(db,query,null,r_col,c_col,v_col);
	}
	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,int r_col,int c_col[],int v_col[]) throws java.sql.SQLException 
	{
		return executeCrossTab(db,query,null,r_col,c_col,v_col);
	}
	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,String r_col,String c_col[],String v_col[]) throws java.sql.SQLException 
	{
		return executeCrossTab(db,query,null,r_col,c_col,v_col);
	}

	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,int r_col[],int c_col,int v_col) throws java.sql.SQLException 
	{
		return executeCrossTab(db,query,null,r_col,c_col,v_col);
	}
	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,String r_col[],String c_col,String v_col) throws java.sql.SQLException 
	{
		return executeCrossTab(db,query,null,r_col,c_col,v_col);
	}
	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,int r_col[],int c_col[],int v_col[]) throws java.sql.SQLException 
	{
		return executeCrossTab(db,query,null,r_col,c_col,v_col);
	}
	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,String r_col[],String c_col[],String v_col[]) throws java.sql.SQLException 
	{
		return executeCrossTab(db,query,null,r_col,c_col,v_col);
	}

	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query,VariableTable vt)
		throws java.sql.SQLException 
	{
		return executeCrossTab(db,query,vt,1,2,3);
	}
	public static DBRowCache executeCrossTab(java.sql.Connection db,
		String query)
		throws java.sql.SQLException 
	{
		return executeCrossTab(db,query,null,1,2,3);
	}

	// --

	public static void executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,int r_col,int c_col,int v_col) throws java.sql.SQLException 
	{
		executeCrossTab(crosstab,db,query,null,r_col,c_col,v_col);
	}
	public static void executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,String r_col,String c_col,String v_col) throws java.sql.SQLException 
	{
		executeCrossTab(crosstab,db,query,null,r_col,c_col,v_col);
	}
	public static void executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,int r_col,int c_col[],int v_col[]) throws java.sql.SQLException 
	{
		executeCrossTab(crosstab,db,query,null,r_col,c_col,v_col);
	}
	public static void executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,String r_col,String c_col[],String v_col[]) throws java.sql.SQLException 
	{
		executeCrossTab(crosstab,db,query,null,r_col,c_col,v_col);
	}

	public static void executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,int r_col[],int c_col,int v_col) throws java.sql.SQLException 
	{
		executeCrossTab(crosstab,db,query,null,r_col,c_col,v_col);
	}
	public static void executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,String r_col[],String c_col,String v_col) throws java.sql.SQLException 
	{
		executeCrossTab(crosstab,db,query,null,r_col,c_col,v_col);
	}
	public static void executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,int r_col[],int c_col[],int v_col[]) throws java.sql.SQLException 
	{
		executeCrossTab(crosstab,db,query,null,r_col,c_col,v_col);
	}
	public static void executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,String r_col[],String c_col[],String v_col[]) throws java.sql.SQLException 
	{
		executeCrossTab(crosstab,db,query,null,r_col,c_col,v_col);
	}

	public static void executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query,VariableTable vt)
		throws java.sql.SQLException 
	{
		executeCrossTab(crosstab,db,query,vt,1,2,3);
	}
	public static void executeCrossTab(DBRowCache crosstab,java.sql.Connection db,
		String query)
		throws java.sql.SQLException 
	{
		executeCrossTab(crosstab,db,query,null,1,2,3);
	}

	public static final java.sql.Connection getConnection(String command)
		throws ClassNotFoundException,java.sql.SQLException
	{
		int row,i;
		String DatabaseDriver="";
		String DatabaseURL="";
		String UserName="";
		String UserPassword="";
		String ConnectProperty="";
		if (command==null || command.trim().length()==0)
		{
			throw new java.sql.SQLException("Connect command is null!");
		}
		java.util.Vector cmdwords = TextUtils.getWords(command," ");
		if (cmdwords == null || cmdwords.size()==0)
		{
			throw new java.sql.SQLException("Connect command is null!");
		}
		for(i=cmdwords.size();i>0;i--)
			if(cmdwords.elementAt(i-1) == null)
				cmdwords.removeElementAt(i-1);
		if (cmdwords.size()<2)
		{
			throw new java.sql.SQLException("Connect information imcomplete!");
		}
		else
		{
			int position=-1;
			for(i=0;i<JDBC_CONNECT.length;i++)
			{
				if (cmdwords.elementAt(0).toString().toUpperCase().equals(
					JDBC_CONNECT[i]))
				{
					position=i;
					break;
				}
			}
			if (position>=0) 
			{
				// For Oracle Database 
				// ORACLE "(DESCRIPTION =(ADDRESS_LIST= &
				//      (ADDRESS = (PROTOCOL = TCP)(HOST = 192.1.1.21)(PORT = 1521))) &
				//      (CONNECT_DATA =  (SID = TEST)(SERVER = DEDICATED)))" ccc001 123456

				if (position == 0)
				{
					java.util.Vector orahost = TextUtils.getWords(cmdwords.elementAt(1).toString(),":");
					if (orahost.size()==3)
					{
						DatabaseURL = JDBC_URL[position]+"(DESCRIPTION=(SDU=8192)(TDU=32768)(ADDRESS_LIST="+
							"(ADDRESS=(PROTOCOL=TCP)(HOST="+orahost.elementAt(0).toString()+
							")(PORT="+orahost.elementAt(1).toString()+")))(CONNECT_DATA=(SID="+
							orahost.elementAt(2).toString()+")(SERVER=DEDICATED)))";
					}
					else
						DatabaseURL = JDBC_URL[position]+cmdwords.elementAt(1).toString();
				}
				else		
					DatabaseURL = JDBC_URL[position]+cmdwords.elementAt(1).toString();
				DatabaseDriver = JDBC_DRIVER[position];
			}
			else
			{
				throw new java.sql.SQLException("Connect information invalid");
			}
			UserName = "";
			UserPassword="";
			if (cmdwords.size()>2)
			{
				UserName = cmdwords.elementAt(2).toString();
				if (cmdwords.size()>3)
					UserPassword = cmdwords.elementAt(3).toString();
			}
			ConnectProperty = command +" "+ JDBC_PROPERTY[position];
		}
		Class.forName(DatabaseDriver);
		java.util.Properties conn_prop= TextUtils.getProperties(ConnectProperty,true);
		conn_prop.setProperty("user",UserName);
		conn_prop.setProperty("password",UserPassword);
		java.sql.Connection db = java.sql.DriverManager.getConnection(DatabaseURL,conn_prop);
		try {
			db.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED);
		}
		 catch (java.sql.SQLException sqle) {}
		try {
			if (DatabaseURL.startsWith("jdbc:oracle:"))
			{
				java.sql.Statement stmt = db.createStatement();
				stmt.execute("BEGIN DBMS_APPLICATION_INFO.SET_CLIENT_INFO('com.lfx.db.DBOperation'); END;");
				stmt.execute("ALTER SESSION SET NLS_DATE_FORMAT='YYYY-MM-DD HH24:MI:SS'");
				stmt.close();
			}
		}
		 catch ( java.sql.SQLException sqle) {}
		return db;
	}
	
	public static void setLocale(String locale)
	{
		String objecttype=locale;
		if (objecttype == null) return;
		objecttype = objecttype.toUpperCase();
		if (objecttype.equals("ENGLISH"))
			java.util.Locale.setDefault(java.util.Locale.ENGLISH);
		else if (objecttype.equals("FRENCH"))
			java.util.Locale.setDefault(java.util.Locale.FRENCH);
		else if (objecttype.equals("GERMAN"))
			java.util.Locale.setDefault(java.util.Locale.GERMAN);
		else if (objecttype.equals("ITALIAN"))
			java.util.Locale.setDefault(java.util.Locale.ITALIAN);
		else if (objecttype.equals("JAPANESE"))
			java.util.Locale.setDefault(java.util.Locale.JAPANESE);
		else if (objecttype.equals("KOREAN"))
			java.util.Locale.setDefault(java.util.Locale.KOREAN);
		else if (objecttype.equals("CHINESE"))
			java.util.Locale.setDefault(java.util.Locale.CHINESE);
		else if (objecttype.equals("SIMPLIFIED_CHINESE"))
			java.util.Locale.setDefault(java.util.Locale.SIMPLIFIED_CHINESE);
		else if (objecttype.equals("TRADITIONAL_CHINESE"))
			java.util.Locale.setDefault(java.util.Locale.TRADITIONAL_CHINESE);
		else if (objecttype.equals("FRANCE"))
			java.util.Locale.setDefault(java.util.Locale.FRANCE);
		else if (objecttype.equals("GERMANY"))
			java.util.Locale.setDefault(java.util.Locale.GERMANY);
		else if (objecttype.equals("ITALY"))
			java.util.Locale.setDefault(java.util.Locale.ITALY);
		else if (objecttype.equals("JAPAN"))
			java.util.Locale.setDefault(java.util.Locale.JAPAN);
		else if (objecttype.equals("KOREA"))
			java.util.Locale.setDefault(java.util.Locale.KOREA);
		else if (objecttype.equals("CHINA"))
			java.util.Locale.setDefault(java.util.Locale.CHINA);
		else if (objecttype.equals("PRC"))
			java.util.Locale.setDefault(java.util.Locale.PRC);
		else if (objecttype.equals("TAIWAN"))
			java.util.Locale.setDefault(java.util.Locale.TAIWAN);
		else if (objecttype.equals("UK"))
			java.util.Locale.setDefault(java.util.Locale.UK);
		else if (objecttype.equals("US"))
			java.util.Locale.setDefault(java.util.Locale.US);
		else if (objecttype.equals("CANADA"))
			java.util.Locale.setDefault(java.util.Locale.CANADA);
		else if (objecttype.equals("CANADA_FRENCH"))
			java.util.Locale.setDefault(java.util.Locale.CANADA_FRENCH);
	}
	public static long exportData(String args[])
	{
		int pos=0;
		long rows=-1;
		String file=null;
		System.out.println();
		System.out.println("JBCP: Release 3.4.0 - Product on "+getDay());
		System.out.println("Copyright (c) 2003 Lou Fangxin(blinkstar@163.net). All right reserved.");
		System.out.println();

		VariableTable vt = new VariableTable();
		for(int i=0;i<args.length;i++)
		{
			if (args[i].toUpperCase().startsWith("PARFILE=") &&
				args[i].length()>5)
			{
				file = args[i].substring(8);
				break;
			}
		}
		if (file == null)
		{
			System.out.println("Format:  JBCP KEYWORD1=value1 KEYWORD2=value2 ...");
			System.out.println();
			System.out.println("Keyword        Description");
			System.out.println("-------------  ---------------------------------");
			System.out.println("PARFILE        Parameter filename");
			System.out.println("LOCALE         Database Client Locale");
			System.out.println("DATABASE       Database connection syntax");
			System.out.println("QUERY          Query statement");
			System.out.println("HEADER         Data file header");
			System.out.println("DATAFILE       Data file name");
			System.out.println("LOGFILE        Log  file name");
			System.out.println("SEPERATOR      Field seperator char");
			System.out.println("ROWNUM         Write rownum at first field");
			System.out.println("PIVOT          Pivot consistant column count");
			System.out.println(".....          User defined parameter");
			System.out.println();
			return -1;
		}
		java.util.Vector files = TextUtils.getWords(file,",");
		for(int j = 0; j<files.size(); j++)
		{
			vt.loadFile(files.elementAt(j).toString());
			for(int i=0;i<args.length;i++)
			{
				if (args[i].toUpperCase().startsWith("PARFILE=")) continue;
				pos = args[i].indexOf("=");
				if (pos <= 0) continue;
				vt.add(args[i].substring(0,pos),java.sql.Types.VARCHAR);
				vt.setValue(args[i].substring(0,pos),args[i].substring(pos+1));
			}
			rows = exportData(vt);
			System.out.println();
		}
		return rows;
	}
	public static long exportData(String file)
	{
		VariableTable vt = new VariableTable();
		vt.loadFile(file);
		return exportData(vt);
	}
	public static long exportData(VariableTable vt)
	{
		java.sql.Connection db = null;
		SQLStatement stmt = null;
		java.sql.ResultSet rset = null;
		java.io.BufferedWriter data = null;
		java.io.FileWriter log = null;
		String database = null;
		String sql_query = null;
		String data_file = "expdata.Txt";
		String log_file = "expdata.Log";
		String seperator = "|";
		String rowid = "FALSE";
		String file_header = null;
		String format_file = null;
		int pivot = 0;
		long rows=0;
		if (vt == null) return -1;
		if ((database = vt.getString("DATABASE")) == null) return -1;
		if ((sql_query = vt.getString("QUERY")) == null) return -1;
		if (vt.getString("DATAFILE") !=  null)
			data_file = vt.parseString(vt.getString("DATAFILE"));
		if (vt.getString("LOGFILE") != null)
			log_file = vt.parseString(vt.getString("LOGFILE"));
		if (vt.getString("SEPERATOR") != null)
			seperator = vt.getString("SEPERATOR").trim();
		if (vt.getString("ROWNUM") != null)
			rowid = vt.getString("ROWNUM").trim().toUpperCase();
		if (vt.getString("HEADER") != null)
			file_header = vt.getString("HEADER");
		if (vt.getString("FORMAT") != null)
			format_file = vt.getString("FORMAT");
		if (vt.getString("PIVOT") != null)
		{
			try {
				pivot = Integer.valueOf(vt.getString("PIVOT")).intValue();
			} catch (NumberFormatException nfe) { pivot = 0;}		
		}
		try {
			String locale=vt.getString("LOCALE");
			if (locale != null)
				setLocale(locale);
			db = getConnection(database);
		}
		 catch (java.sql.SQLException sqle)
		{
			System.out.println(sqle.getMessage());
			return -1;
		}	
		 catch (ClassNotFoundException cnfe)
		{
			System.out.println(cnfe.getMessage());
			return -1;
		}
		java.io.File os_data_file = new java.io.File(data_file);
		java.io.File os_log_file = new java.io.File(log_file);
		try {
			data = new java.io.BufferedWriter(
				new java.io.FileWriter(os_data_file),256 * 1024);
			log  = new LogWriter(os_log_file);
			log.write("Export using:\r\n");
			if (file_header != null)
			{
				log.write("  HEADER    = "+file_header+"\r\n");
				log.write("            = "+vt.parseString(file_header)+"\r\n");
			}
			java.util.Vector sql_lines = TextUtils.getLines(sql_query);
			for(int i=0;i<sql_lines.size();i++)
			{
				if (i == 0)
					log.write("  QUERY     = "+sql_lines.elementAt(i).toString()+"\r\n");
				else
					log.write("              "+sql_lines.elementAt(i).toString()+"\r\n");
			}
			log.write("  DATAFILE  = "+data_file+"\r\n");
			log.write("  LOGFILE   = "+log_file+"\r\n");
			log.write("  SEPERATOR = "+seperator+"\r\n");
			log.write("  ROWNUM    = "+rowid+"\r\n");
			log.write("  PIVOT     = "+pivot+"\r\n");
			log.write("Parameter list:\r\n");
			String paras[]=vt.getNames();
			for(int i=0;i<paras.length;i++)
			{
				if (paras[i].equals("DATABASE") ||
					paras[i].equals("QUERY") ||
					paras[i].equals("DATAFILE") ||
					paras[i].equals("LOGFILE") ||
					paras[i].equals("SEPERATOR") ||
					paras[i].equals("ROWNUM") ||
					paras[i].equals("PIVOT") || paras[i].equals("HEADER") )
					continue;
				log.write("  "+paras[i]+"\t = ");
				if (vt.getString(paras[i])!=null)
					log.write(vt.getString(paras[i]));
				else
					log.write("<null>");
				log.write("\r\n");
			}
			log.write("Export begin:\r\n");
			log.write("  Begin query at "+getDay()+"\r\n");
			long begin_time = System.currentTimeMillis();
			stmt = DBOperation.prepareStatement(db,sql_query,vt);
			stmt.bind(vt);
			rset = stmt.stmt.executeQuery();
			if(format_file != null)
			{
				try {
					java.io.FileWriter format_file_writer = new java.io.FileWriter(format_file);
					java.sql.ResultSetMetaData rsetmeta = rset.getMetaData();
					if (rowid.equals("TRUE"))
					{
						format_file_writer.write("ROWID\tBIGINT\r\n");
					}
					for(int i = 0;i<rsetmeta.getColumnCount();i++)
					{
						format_file_writer.write(rsetmeta.getColumnName(i+1)
							+"\t"+SQLTypes.getTypeName(rsetmeta.getColumnType(i+1))+"\r\n");
					}
					format_file_writer.close();
				} catch(java.io.IOException ioe_format) {}
			}
			log.write("  Begin fetch at "+getDay()+"\r\n");
			if (file_header != null)
			{
				data.write(vt.parseString(file_header));
				data.write(" -1 0                            \r\n");
			}
			else
			{
				java.sql.ResultSetMetaData rsetmeta = rset.getMetaData();
				if (rowid.equals("TRUE"))
				{
					data.write("ROWID");
					data.write(seperator);
				}
				for(int i = 1;i<=rsetmeta.getColumnCount();i++)
				{
					data.write(rsetmeta.getColumnName(i));
					if (i < rsetmeta.getColumnCount())
						data.write(seperator);
				}
				data.write("\r\n");
			}
			rows = exportData(data,log,rset,seperator,rowid.equals("TRUE"),pivot);
			rset.close();
			rset = null;
			stmt.stmt.close();
			stmt = null;
			db.close();
			db = null;
			data.close();
			data = null;
			begin_time = System.currentTimeMillis() - begin_time;
			if (rows % 100000 != 0)
				log.write("  "+rows + " exported at "+getDay()+"\r\n");
			log.write("  "+rows + " exported in "+getElapsed(begin_time)+" on " + rows*1000/begin_time  +" rows/s \r\n");
			log.write("Exported end normal!\r\n");
			log.close();
			log = null;
			if (file_header != null)
			{
				java.io.RandomAccessFile raf = new java.io.RandomAccessFile(os_data_file,"rw");
				raf.writeBytes(vt.parseString(file_header));
				raf.writeBytes(" "+rows+" "+os_data_file.length());
				raf.close();
			}			
			return rows;
		} 
		 catch (java.sql.SQLException sqle)
		{
			System.out.println(sqle.getMessage());
		}
		 catch (java.io.IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
		if (rset != null)
		{
			try {
				rset.close();
			} catch (java.sql.SQLException sqle) {}
		}
		if (stmt != null && stmt.stmt != null)
		{
			try {
				stmt.stmt.close();
			} catch (java.sql.SQLException sqle) {}
		}
		if (db != null )
		{
			try {
				db.close();
			} catch (java.sql.SQLException sqle) {}
		}
		if (data != null)
		{
			try {
				data.close();
			} catch (java.io.IOException ioe) {}
		}
		if (log != null)
		{
			try {
				log.close();
			} catch (java.io.IOException ioe) {}
		}
		if (os_data_file.exists())
			os_data_file.delete();
		//if (os_log_file.exists())
		//	os_log_file.delete();
		return -1;
	}
	public static long exportData(java.io.Writer file,java.io.Writer log,java.sql.ResultSet data)
		throws java.sql.SQLException, java.io.IOException
	{
		return exportData(file,log,data,"|",false,0);
	}
	public static long exportData(java.io.Writer file,java.io.Writer log,java.sql.ResultSet data,boolean rowid)
		throws java.sql.SQLException, java.io.IOException
	{
		return exportData(file,log,data,"|",rowid,0);
	}
	public static long exportData(java.io.Writer file,java.io.Writer log,java.sql.ResultSet data,int pivot)
		throws java.sql.SQLException, java.io.IOException
	{
		return exportData(file,log,data,"|",false,pivot);
	}
	public static long exportData(java.io.Writer file,java.io.Writer log,
		java.sql.ResultSet data,boolean rowid,int pivot)
		throws java.sql.SQLException, java.io.IOException
	{
		return exportData(file,log,data,"|",rowid,pivot);
	}
	public static long exportData(java.io.Writer file,java.io.Writer log,
		java.sql.ResultSet data,String seperator,boolean rowid,int pivot_col)
		 throws java.sql.SQLException, java.io.IOException
	{
		long rows = 0;
		java.text.SimpleDateFormat simple_fmt = 
			new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.sql.ResultSetMetaData rsetmeta = data.getMetaData();
		int colcount = rsetmeta.getColumnCount();
		int coltype[] = new int   [colcount];
		int k;
		String val = null;
		StringBuffer pre_record = new StringBuffer();
		int pivot = 0;
		String new_line_tag="\r\n";
		if (pivot_col > 0 && pivot_col < colcount - 1)
			pivot = pivot_col;
		for(int i = 0;i<colcount;i++)
		{
			coltype[i] = rsetmeta.getColumnType(i+1);
		}
		while( data.next() )
		{
			if (pivot > 0)
			{
				pre_record.delete(0,pre_record.length());
				for(int i=0;i<pivot;i++)
				{
					switch (coltype[i])
					{
						case java.sql.Types.CHAR:
						case java.sql.Types.VARCHAR:
						case java.sql.Types.LONGVARCHAR:
							val = data.getString(i+1);
							if (val!=null)
							{
								k = 0;
								char temp_arr[] = val.toCharArray();
							   	for (k=temp_arr.length;k>0;k--)
						    		{
									if (temp_arr[k-1] != ' ') break;	
								}
	  							if (k>0)
									pre_record.append(temp_arr,0,k);
							}
							break;
						case java.sql.Types.DATE:
							java.sql.Date date_val = data.getDate(i+1);
							if (date_val != null)
								pre_record.append(simple_fmt.format(date_val));
							break;
						case java.sql.Types.TIME:
							java.sql.Time time_val = data.getTime(i+1);
							if (time_val != null)
								pre_record.append(simple_fmt.format(time_val));
							break;
						case java.sql.Types.TIMESTAMP:
							java.sql.Timestamp timestamp_val = data.getTimestamp(i+1);
							if (timestamp_val != null)
								pre_record.append(simple_fmt.format(timestamp_val));
							break;
						default:
							val = data.getString(i+1);
							if (val != null)
							{
								pre_record.append(val);
							}
							break;
					}
					pre_record.append(seperator);
				}
			}
			if (pivot == 0)
			{
				if (rows > 0)
					file.write(new_line_tag);
				if (rowid)
				{
					file.write((rows + 1)+seperator);
				}
			}
			for(int i=pivot;i<colcount;i++)
			{
				switch (coltype[i])
				{
					case java.sql.Types.CHAR:
					case java.sql.Types.VARCHAR:
					case java.sql.Types.LONGVARCHAR:
						val = data.getString(i+1);
						if (val!=null)
						{
							k = 0;
							char temp_arr[] = val.toCharArray();
						   	for (k=temp_arr.length;k>0;k--)
						    	{
								if (temp_arr[k-1] != ' ') break;	
							}
	  						if (k>0)
							{
								if (pivot > 0)
								{
									if (rows > 0)
										file.write(new_line_tag);
									if (rowid)
									{
										file.write((rows + 1)+seperator);
									}
									file.write(pre_record.toString());
									rows ++;
									if (rows % 100000 == 0)
										log.write("  "+rows + " exported at "+
											getDay("yyyy-MM-dd HH:mm:ss")+new_line_tag);
								}
								file.write(temp_arr,0,k);
							}
						}
						else
						{
							if (pivot > 0) continue;
						}
						break;
					case java.sql.Types.DATE:
						java.sql.Date date_val = data.getDate(i+1);
						if (date_val != null)
						{
							if (pivot > 0)
							{
								if ( rows > 0)
									file.write(new_line_tag);
								if (rowid)
								{
									file.write((rows + 1)+seperator);
								}
								file.write(pre_record.toString());
								rows ++;
								if (rows % 100000 == 0)
									log.write("  "+rows + " exported at "+
										getDay("yyyy-MM-dd HH:mm:ss")+new_line_tag);
							}
							file.write(simple_fmt.format(date_val));
						}
						else
						{
							if (pivot > 0) continue;
						}						
						break;
					case java.sql.Types.TIME:
						java.sql.Time time_val = data.getTime(i+1);
						if (time_val != null)
						{
							if (pivot > 0)
							{
								if (rows > 0)
									file.write(new_line_tag);
								if (rowid)
								{
									file.write((rows + 1)+seperator);
								}
								file.write(pre_record.toString());
								rows ++;
								if (rows % 100000 == 0)
									log.write("  "+rows + " exported at "+
										getDay("yyyy-MM-dd HH:mm:ss")+new_line_tag);
							}
							file.write(simple_fmt.format(time_val));
						}
						else
						{
							if (pivot > 0) continue;
						}
						break;
					case java.sql.Types.TIMESTAMP:
						java.sql.Timestamp timestamp_val = data.getTimestamp(i+1);
						if (timestamp_val != null)
						{
							if (pivot > 0)
							{
								if (rows > 0)
									file.write(new_line_tag);
								if (rowid)
								{
									file.write((rows + 1)+seperator);
								}
								file.write(pre_record.toString());
								rows ++;
								if (rows % 100000 == 0)
									log.write("  "+rows + " exported at "+
										getDay("yyyy-MM-dd HH:mm:ss")+new_line_tag);
							}
							file.write(simple_fmt.format(timestamp_val));
						}
						else
						{
							if (pivot > 0) continue;
						}
						break;
					default:
						val = data.getString(i+1);
						if (val != null)
						{
							if (pivot > 0)
							{
								if (rows > 0)
									file.write(new_line_tag);
								if (rowid)
								{
									file.write((rows + 1)+seperator);
								}
								file.write(pre_record.toString());
								rows ++;
								if (rows % 100000 == 0)
									log.write("  "+rows + " exported at "+
										getDay("yyyy-MM-dd HH:mm:ss")+new_line_tag);
							}
							file.write(val);
						}
						else
						{
							if (pivot > 0) continue;
						}
						break;
				}
				if (i < colcount - 1 && pivot == 0)
					file.write(seperator);
			}
			if (pivot == 0)
			{
				rows ++;
				if (rows % 100000 == 0)
					log.write("  "+rows + " exported at "+
						getDay("yyyy-MM-dd HH:mm:ss")+new_line_tag);
			}
		}
		return rows;
	}
}