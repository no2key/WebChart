package com.lfx.db;
public interface DBCommandHandler
{
	public void showResultSet(java.sql.ResultSet rset)
		throws java.sql.SQLException;
	public void showMessage(int rows);
	public void showMessage(String msg);
	public void showException(java.lang.Exception e);
	public void showSQLException(java.sql.SQLException sqle);
}