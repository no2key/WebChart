package com.lfx.db;

public final class SQLQuery extends Object 
{
	private String source=null;
	private String dest=null;
	private String paramlist[]=null;
	private String paramtype[]=null;
	public SQLQuery(String sql_src,String sql_dest,String p_name[],String p_type[])
	{
		source = sql_src;
		dest = sql_dest;
		paramlist = p_name;
		paramtype = p_type;
	}
	public final String getSourceSQL()
	{
		return source;
	}	
	public final String getDestSQL()
	{
		return dest;
	}	
	public final String[] getParamNames()
	{
		return paramlist;
	}	
	public final String[] getParamTypes()
	{
		return paramtype;
	}
}