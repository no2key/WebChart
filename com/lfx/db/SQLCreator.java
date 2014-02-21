package com.lfx.db;

public final class SQLCreator
{
    public final static String getInsertSQL(String tablename, String columns)
    {
	StringBuffer sqlbuf = new StringBuffer();
	String _cols[] = TextUtils.toStringArray(TextUtils.getWords(columns,","));
        sqlbuf.append("INSERT INTO ");
	sqlbuf.append(tablename);
	sqlbuf.append(" ( ");
	for(int i=0;i<_cols.length;i++)
	{
	    if (i>0) sqlbuf.append(", ");
	    if (_cols[i].startsWith("*"))
               sqlbuf.append(_cols[i].substring(1));
            else
               sqlbuf.append(_cols[i]);
	}
        sqlbuf.append(" ) VALUES ( ");
	for(int i=0;i<_cols.length;i++)
	{
	    if (i>0) sqlbuf.append(", ");
	    sqlbuf.append(":");
	    if (_cols[i].startsWith("*"))
               sqlbuf.append(_cols[i].substring(1));
            else
               sqlbuf.append(_cols[i]);
	}
        sqlbuf.append(" )");
	return sqlbuf.toString();
    }
    public final static String getUpdateSQL(String tablename, String columns)
    {
	StringBuffer sqlbuf = new StringBuffer();
	String _cols[] = TextUtils.toStringArray(TextUtils.getWords(columns,","));
        int pos = 0;
        sqlbuf.append("UPDATE ");
	sqlbuf.append(tablename);
	sqlbuf.append(" SET ");
	for(int i=0;i<_cols.length;i++)
	{
	    if (_cols[i].startsWith("*") == false)
	    {
	       if (pos>0) sqlbuf.append(" , ");
	       sqlbuf.append(_cols[i]);
	       sqlbuf.append("=:");
               sqlbuf.append(_cols[i]);
	       pos ++;
	     }
	}
        sqlbuf.append(" WHERE ");
	pos = 0;
	for(int i=0;i<_cols.length;i++)
	{
	    if (_cols[i].startsWith("*"))
	    {
	       if (pos>0) sqlbuf.append(" AND ");
	       sqlbuf.append(_cols[i].substring(1));
	       sqlbuf.append("=:");
               sqlbuf.append(_cols[i].substring(1));
	       pos ++;
	     }
	}
	return sqlbuf.toString();
    }
    public final static String getDeleteSQL(String tablename, String columns)
    {
	StringBuffer sqlbuf = new StringBuffer();
	String _cols[] = TextUtils.toStringArray(TextUtils.getWords(columns,","));
	int pos=0;

        sqlbuf.append("DELETE FROM ");
	sqlbuf.append(tablename);
	sqlbuf.append(" WHERE ");
	for(int i=0;i<_cols.length;i++)
	{
	    if (_cols[i].startsWith("*"))
	    {
	       if (pos>0) sqlbuf.append(" AND ");
	       sqlbuf.append(_cols[i].substring(1));
	       sqlbuf.append("=:");
               sqlbuf.append(_cols[i].substring(1));
	       pos ++;
	    }
	}
	return sqlbuf.toString();
    }
}