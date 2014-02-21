package com.lfx.db;

public class DBLogicalConnection extends Object
{
	protected String  _id = null;
	protected int     _mode = 0;
	protected String  _conns[] = {};
	protected int     _value[] = {};
	protected java.util.HashMap _list = new java.util.HashMap();
        protected boolean _markdown = false;
	protected DatabaseMarkdownException _ex = null; 

	public static final int MODE_FIRST      = 0;
	public static final int MODE_RANDOM     = 1;
	public static final int MODE_FAILOVER   = 2;
	public static final int MODE_POSITION	= 3;
	public static final int MODE_RANGE	= 4;
	public static final int MODE_LIST	= 5;

	public DBLogicalConnection (String id, String conns)
	{
		_id = id;
		_conns = TextUtils.toStringArray(TextUtils.getWords(conns,","));
		_ex = new DatabaseMarkdownException("LOGICAL."+_id);
		if (_conns.length > 0)
		{
		    _value = new int[_conns.length];
		    for(int i=0;i<_conns.length;i++)
		    {
		        _value[i] = i * 100;
		    }
		}
	}

	public DBLogicalConnection (String id, int mode, String conns)
	{
		_id = id;
		_mode = mode;
		_conns = TextUtils.toStringArray(TextUtils.getWords(conns,","));
		_ex = new DatabaseMarkdownException("LOGICAL."+_id);
		if (_conns.length > 0)
		{
		    _value = new int[_conns.length];
		    for(int i=0;i<_conns.length;i++)
		    {
		        _value[i] = i * 100;
		    }
		}
	}

	public void setRangeValues(String valstring)
	{
		String vals[] = TextUtils.toStringArray(TextUtils.getWords(valstring,","));
		for(int i=0; i<vals.length && i < _value.length;i++)
		{
		    try {
			_value[i] = Integer.parseInt(vals[i]);
			} catch (java.lang.NumberFormatException nfe) {}
		
		}
	}

	public void setListValues(String valstring)
	{
		String vals[] = TextUtils.toStringArray(TextUtils.getWords(valstring,","));
		for(int i=0; i<vals.length && i < _value.length;i++)
		{
		    String valkeys[] = TextUtils.toStringArray(TextUtils.getWords(vals[i]));
		    for( int j=0; j<valkeys.length && j<_conns.length; j++)
		    {
			_list.put(Integer.getInteger(valkeys[j]), _conns[i]);
		    }		
		}
	}

	public int hashCode()
	{
		if (_id == null) return 0;
		return _id.hashCode();
	}

	public java.lang.String toString()
	{
		return _id;
	}

	public void markdown()
	{
		_markdown = true;
	}

	public void markup()
	{
		_markdown = false;
	}

	public boolean available()
	{
		return !_markdown;
	}

	public int getSize()
	{
		return _conns.length;
	}

	public DBPooledConnection getIndexConnection(int rule) throws ConnectTimeoutException, DatabaseMarkdownException
	{
		if (DBPhysicalManager.exists(_conns[rule % _conns.length]))
		{
		    if (DBPhysicalManager.available(_conns[rule % _conns.length]))
			return DBPhysicalManager.getPoolConnection(_conns[rule % _conns.length]);
		}
		else if (DBLogicalManager.exists(_conns[rule % _conns.length]))
		{
		    DBLogicalConnection c = DBLogicalManager.getLogicalConnection(_conns[rule % _conns.length]);
		    if (c.available())
			return c.getConnection(rule / _conns.length);
		}	
		throw _ex;
	}

	public DBPooledConnection getConnection() throws ConnectTimeoutException, DatabaseMarkdownException
	{
		return getConnection(0);
	}

	public DBPooledConnection getConnection(int rule) throws ConnectTimeoutException, DatabaseMarkdownException
	{
		if (_markdown) 	throw _ex;
		if (_conns == null || _conns.length == 0) throw _ex;
		switch(_mode)
		{
		    case MODE_FIRST:
			if (DBPhysicalManager.exists(_conns[0]))
			{
				if (DBPhysicalManager.available(_conns[0]))
					return DBPhysicalManager.getPoolConnection(_conns[0]);
			}
			else if (DBLogicalManager.exists(_conns[0]))
			{
				DBLogicalConnection c = DBLogicalManager.getLogicalConnection(_conns[0]);
				if (c.available())
					return c.getConnection(rule);
			}
			break;			
		    case MODE_RANDOM:
			int tempid = (int)(Math.round(10000 * Math.random() / _conns.length) % (_conns.length));
			if (DBPhysicalManager.exists(_conns[tempid]))
			{
				if (DBPhysicalManager.available(_conns[tempid]))
					return DBPhysicalManager.getPoolConnection(_conns[tempid]);
			}
			else if (DBLogicalManager.exists(_conns[tempid]))
			{
				DBLogicalConnection c = DBLogicalManager.getLogicalConnection(_conns[tempid]);
				if (c.available())
					return c.getConnection(rule);
			}
			for(int i=tempid + 1; i<tempid + _conns.length; i++)
			{
			    if (DBPhysicalManager.exists(_conns[i%_conns.length]))
			    {
				if (!DBPhysicalManager.available(_conns[i%_conns.length])) continue;
				return DBPhysicalManager.getPoolConnection(_conns[i%_conns.length]);
			    }
			    else if (DBLogicalManager.exists(_conns[i%_conns.length]))
			    {
				DBLogicalConnection c = DBLogicalManager.getLogicalConnection(_conns[i%_conns.length]);
				if (!c.available()) continue;
				return c.getConnection(rule);
			    }
			}
			break;			
		    case MODE_FAILOVER:
			for(int i=0; i<_conns.length; i++)
			{
			    if (DBPhysicalManager.exists(_conns[i]))
			    {
				if (!DBPhysicalManager.available(_conns[i])) continue;
				return DBPhysicalManager.getPoolConnection(_conns[i]);
			    }
			    else if (DBLogicalManager.exists(_conns[i]))
			    {
				DBLogicalConnection c = DBLogicalManager.getLogicalConnection(_conns[i]);
				if (!c.available()) continue;
				return c.getConnection(rule);
			    }
			}
			break;
		    case MODE_LIST:
			Integer rulekey = Integer.valueOf(rule);
			if (_list.containsKey(rulekey))
			{
			    String connkey = (String)(_list.get(rulekey));
			    if (DBPhysicalManager.exists(connkey))
			    {
				return DBPhysicalManager.getPoolConnection(connkey);
			    }
			    else if (DBLogicalManager.exists(connkey))
			    {
				DBLogicalConnection c = DBLogicalManager.getLogicalConnection(connkey);
				if (!c.available()) return c.getConnection(rule);
			    }
			}
			break;
		    case MODE_POSITION:
			if (DBPhysicalManager.exists(_conns[rule % _conns.length]))
			{
			    if (DBPhysicalManager.available(_conns[rule % _conns.length]))
				return DBPhysicalManager.getPoolConnection(_conns[rule % _conns.length]);
			}
			else if (DBLogicalManager.exists(_conns[rule % _conns.length]))
			{
			    DBLogicalConnection c = DBLogicalManager.getLogicalConnection(_conns[rule % _conns.length]);
			    if (c.available())
				return c.getConnection(rule / _conns.length);
			}	
			break;
		    case MODE_RANGE:
			int rangeid = 0;
			for(int i=0;i<_conns.length;i++)
			{
				if (rule < _value[i]) 
				{
				    rangeid = i;
				    break;
				}
			}
			if (rangeid < _conns.length)
			{
			    if (DBPhysicalManager.exists(_conns[rangeid]))
			    {
			        if (DBPhysicalManager.available(_conns[rangeid]))
				    return DBPhysicalManager.getPoolConnection(_conns[rangeid]);
			    }
			    else if (DBLogicalManager.exists(_conns[rangeid]))
			    {
			        DBLogicalConnection c = DBLogicalManager.getLogicalConnection(_conns[rangeid]);
			        if (c.available())
				    return c.getConnection(rangeid);
			    }
			}	
			break;
		}
		throw _ex;
	}
}