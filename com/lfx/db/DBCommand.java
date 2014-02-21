package com.lfx.db;
public class DBCommand
{
	public final static void clearWarnings(java.sql.Connection db,DBCommandHandler msghandler)
	{
		try {
			java.sql.SQLWarning warn = db.getWarnings();
			if (warn != null )
			{
				msghandler.showSQLException(warn);
				while((warn = warn.getNextWarning())!=null)
				{
					msghandler.showSQLException(warn);
				}
			}
			db.clearWarnings();
		}catch (java.sql.SQLException sqle)
		{
			msghandler.showSQLException(sqle);
		} 
	}
	public final static void doTransaction(java.sql.Connection db,boolean commit,DBCommandHandler msghandler)
	{
		try {
			if (commit)
				db.commit();
			else
				db.rollback();
		}
		 catch(java.sql.SQLException sqle)
		{
			msghandler.showSQLException(sqle);
		}
		clearWarnings(db,msghandler);
	}
	public final static void doCommit(java.sql.Connection db,DBCommandHandler msghandler)
	{
		doTransaction(db,true,msghandler);
	}
	public final static void doRollback(java.sql.Connection db,DBCommandHandler msghandler)
	{
		doTransaction(db,false,msghandler);
	}
	public final static void doSQL(java.sql.Connection db,String cmd,DBCommandHandler cmdhandler)
	{
		VariableTable vt = new VariableTable();
		doSQL(db,cmd,vt,cmdhandler);
	}
	public final static void doSQL(java.sql.Connection db,String cmd,VariableTable vt,DBCommandHandler cmdhandler)
	{
		int rows=0;
		int rowcount=-1;
		boolean resultreturned=false;
		java.sql.ResultSet rset=null;
		SQLStatement pstmt = null;		
		try {
			pstmt =	DBOperation.prepareStatement(db,cmd,vt);
			if (pstmt.stmt == null) return;
			pstmt.bind(vt);
			resultreturned = pstmt.stmt.execute();
			do{
				if (resultreturned)
				{
					rset = pstmt.stmt.getResultSet();
					cmdhandler.showResultSet(rset);
					rset.close();
				}
				else
				{
					try {
						rowcount = pstmt.stmt.getUpdateCount();
					}
					 catch(java.sql.SQLException e)
					{
						rowcount = -1;
					}
					if (rowcount>=0)
					{
						cmdhandler.showMessage(rowcount);
					}
				}
				resultreturned = pstmt.stmt.getMoreResults();
			}while(resultreturned||rowcount!=-1);
		}catch(java.sql.SQLException sqle)
		{
			cmdhandler.showSQLException(sqle);
		}
		finally
		{
			if (pstmt != null && pstmt.stmt != null)
			{
				try {
					pstmt.stmt.close();
				} catch(java.sql.SQLException e) {}
			}
		}
		clearWarnings(db,cmdhandler);
	}
	public final static void doCall(java.sql.Connection db,String cmd,DBCommandHandler cmdhandler)
	{
		VariableTable vt = new VariableTable();
		doCall(db,cmd,vt,cmdhandler);
	}
	public final static void doCall(java.sql.Connection db,String cmd,VariableTable vt,DBCommandHandler cmdhandler)
	{
		int rows=0;
		int rowcount=-1;
		boolean resultreturned=false;
		java.sql.ResultSet rset=null;
		SQLCallable pstmt = null;		
		try {
			pstmt =	DBOperation.prepareCall(db,cmd,vt);
			if (pstmt.stmt == null) return;
			pstmt.bind(vt);
			resultreturned = pstmt.stmt.execute();
			pstmt.fetch(vt);
			do{
				if (resultreturned)
				{
					rset = pstmt.stmt.getResultSet();
					cmdhandler.showResultSet(rset);
					rset.close();
				}
				else
				{
					try {
						rowcount = pstmt.stmt.getUpdateCount();
					}
					 catch(java.sql.SQLException e)
					{
						rowcount = -1;
					}
					if (rowcount>=0)
					{
						cmdhandler.showMessage(rowcount);
					}
				}
				resultreturned = pstmt.stmt.getMoreResults();
			}while(resultreturned||rowcount!=-1);
		}catch(java.sql.SQLException sqle)
		{
			cmdhandler.showSQLException(sqle);
		}
		finally
		{
			if (pstmt != null && pstmt.stmt != null)
			{
				try {
					pstmt.stmt.close();
				} catch(java.sql.SQLException e) {}
			}
		}
		clearWarnings(db,cmdhandler);
	}
}