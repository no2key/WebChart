package com.lfx.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Savepoint;
import java.util.List;
import java.util.Map;

public class DBPooledConnection extends java.lang.Object implements Connection
{
	protected Connection _conn = null;
	protected DBConnectionPool _pool = null;
	protected long  _last = System.currentTimeMillis();


	public DBPooledConnection(Connection c)
	{
		_conn = c;		
	}
	
	public DBPooledConnection(DBConnectionPool p, Connection c)
	{
		_pool = p;
		_conn = c;		
	}

	public String getDBType()
	{
		if (_pool != null)
			return _pool.getDBType();
		return  null;
	}

	public String getDBTag()
	{
		if (_pool != null)
			return _pool.getDBTag();
		return null;
	}
	
	public long getLastTime()
	{
		return _last;
	}
	
	public void setLastTime()
	{
		_last = System.currentTimeMillis();	
	}
	
	public Statement createStatement()       throws SQLException
	{
		return _conn.createStatement();
	}
	
	public PreparedStatement prepareStatement(java.lang.String sql)       throws SQLException
	{
		return _conn.prepareStatement(sql);
	}
	
	public CallableStatement prepareCall(java.lang.String sql)       throws SQLException
	{
		return _conn.prepareCall(sql);
	}
	
	public java.lang.String nativeSQL(java.lang.String sql)       throws SQLException
	{
		return _conn.nativeSQL(sql);	
	}
	public void setAutoCommit(boolean ac)       throws SQLException
	{
		_conn.setAutoCommit(ac);	
	}
	public boolean getAutoCommit()       throws SQLException
	{
		return _conn.getAutoCommit();
	}
	public void commit()       throws SQLException
	{
		_conn.commit();
	}
	public void rollback()       throws SQLException
	{
		_conn.rollback();
	}
	public void release()  
	{
	    try {
                _conn.rollback();
	    } catch (SQLException sqle) {} 
	    try {
		_conn.close();
	    } catch (SQLException sqle) {} 
	    _conn = null;
	    if (_pool != null)
  	        _pool.decreaseCurrent(this);
	}
	public void close()  
	{
	    if (_conn != null)
	    {
 	        if (_pool != null)
		    _pool.putConnection(this);	 
		else
		{
		   try { _conn.close(); } catch (java.sql.SQLException sqle) {}
		}
	    }
	}
	public void checkSQLState(String statecode)
	{
	    if (statecode == null || "08S01".equalsIgnoreCase(statecode))
	    {
		_conn = null;
		if (_pool != null)
		{
		    _pool.decreaseCurrent(this);
		    _pool.markdown();
		}
	    }
	}
	public boolean isClosed()       throws SQLException
	{
		return _conn.isClosed();	
	}
	public DatabaseMetaData getMetaData()       throws SQLException
	{
		return _conn.getMetaData();	
	}
	public void setReadOnly(boolean ro)       throws SQLException
	{
		_conn.setReadOnly(ro);
	}
	public boolean isReadOnly()       throws SQLException
	{
		return _conn.isReadOnly();	
	}
	public void setCatalog(java.lang.String cat)       throws SQLException
	{
		_conn.setCatalog(cat);
	}
	public java.lang.String getCatalog()       throws SQLException
	{
		return _conn.getCatalog();	
	}
	public void setTransactionIsolation(int lev)       throws SQLException
	{
		_conn.setTransactionIsolation(lev);
	}
	public int getTransactionIsolation()       throws SQLException
	{
		return _conn.getTransactionIsolation();
	}
	public SQLWarning getWarnings()       throws SQLException
	{
		return _conn.getWarnings();	
	}
	public void clearWarnings()       throws SQLException
	{
		_conn.clearWarnings();	
	}
	public  Statement createStatement(int p1, int p2)       throws SQLException
	{
		return _conn.createStatement(p1,p2);	
	}
	public  PreparedStatement prepareStatement(java.lang.String sql, int p1, int p2)       throws SQLException
	{
		return _conn.prepareStatement(sql, p1, p2);	
	}
	public  CallableStatement prepareCall(java.lang.String sql, int p1, int p2)       throws SQLException
	{
		return _conn.prepareCall(sql, p1, p2);	
	}
	public  java.util.Map getTypeMap()       throws SQLException
	{
		return _conn.getTypeMap();	
	}
	public  void setTypeMap(java.util.Map map)       throws SQLException
	{
		_conn.setTypeMap(map);		
	}
	public  void setHoldability(int p1)       throws SQLException
	{
		_conn.setHoldability(p1);	
	}
	public  int getHoldability()       throws SQLException
	{
		return _conn.getHoldability();	
	}
	public  Savepoint setSavepoint()       throws SQLException
	{
		return _conn.setSavepoint();	
	}
	public  Savepoint setSavepoint(java.lang.String p1)       throws SQLException
	{
		return _conn.setSavepoint(p1);
	}
	public  void rollback(Savepoint sp)       throws SQLException
	{
		_conn.rollback(sp);
	}
	public  void releaseSavepoint(Savepoint sp)       throws SQLException
	{
		_conn.releaseSavepoint(sp);
	}
	public  Statement createStatement(int p1, int p2, int p3)       throws SQLException
	{
		return _conn.createStatement(p1,p2,p3);	
	}
	public  PreparedStatement prepareStatement(java.lang.String sql, int p1, int p2, int p3)       throws SQLException
	{
		return _conn.prepareStatement(sql, p1, p2, p3);
	}
	public  CallableStatement prepareCall(java.lang.String sql, int p1, int p2, int p3)       throws SQLException
	{
		return _conn.prepareCall(sql, p1, p2, p3);
	}
	public  PreparedStatement prepareStatement(java.lang.String sql, int p1)       throws SQLException
	{
		return _conn.prepareStatement(sql, p1);
	}
	public  PreparedStatement prepareStatement(java.lang.String sql, int p[])       throws SQLException
	{
		return _conn.prepareStatement(sql, p);
	}
	public  PreparedStatement prepareStatement(java.lang.String sql, java.lang.String p[])       throws SQLException
	{
		return _conn.prepareStatement(sql, p);		
	}
	protected void finalize()       throws java.lang.Throwable
	{
		if (_conn != null)
		{
			if (_pool != null)
				_pool.putConnection(this);	
			else
				_conn.close();
		}
	}

	/*
        public java.sql.Struct createStruct(java.lang.String name, java.lang.Object objs[] )       throws java.sql.SQLException
	{
		return _conn.createStruct(name, objs);
	}

        public java.sql.Array createArrayOf(java.lang.String name, java.lang.Object objs[])       throws java.sql.SQLException
	{
		return _conn.createArrayOf(name, objs);
	}
	*/
}