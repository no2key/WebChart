package com.lfx.db;
import java.io.Serializable;
import java.util.Vector;
import java.net.URLEncoder;

public final class SimpleDBRowCache  
	implements DBRowCache, sun.misc.Compare, java.io.Serializable
{
	private int    column_count = 0;
	private String column_super[] = new String[10];
	private String column_label[] = new String[10];
	private String column_name [] = new String[10];
	private int    column_type [] = new int[10];
	private int    column_size [] = new int[10];
	private boolean column_visible[] = new boolean[10];
	private String column_memo [] = new String[10];
	private String column_tooltip [] = new String[10];
	private String column_fmter[] = new String[10];
	private String column_hfmter[] = new String[10];
	private Vector cache_data = new Vector(100,100);

  	private int sort_columns[] = {1};
	private boolean sort_asc= true;    
	
	private int sql_error_code = 0;
	private String sql_error_msg  = "";
        private int page_size = 0;
	
	private java.util.HashMap filter_cache = new java.util.HashMap(128);
	private java.util.HashMap str_props = new java.util.HashMap(10);
        
        private int last_cum_row = -1;
        private String last_cum_col = null;
        private double last_cum_val = 0;
        private long  _loadtime = System.currentTimeMillis();

	public final long getLoadTime()
	{
		return _loadtime;
	}
	public final void setLoadTime(long loadtime)
	{
		_loadtime = loadtime;
	}

        public final void setStringProperty(String prop, String value)
	{
		if (prop==null) return;
		str_props.put(prop.toUpperCase(), value);
	}

        public final String getStringProperty(String prop)
	{	
		if (prop==null) return null;
		if (str_props.containsKey(prop.toUpperCase()))
		   return (String)(str_props.get(prop.toUpperCase()));
		return null;
	}

	public final int findColumn(String col)
	{
		int i;
		if (col == null)
			return 0;
		for (i=0;i<column_count;i++)
		{
			if (column_name[i] != null && 
				column_name[i].equalsIgnoreCase(col.trim()))
					break;
		}
		return (i == column_count?0:i+1);
	}

	public final void setColumnVisible(int col,boolean bvis)
	{
		if (col < 1 || col > column_count)
			return ;
		column_visible[col - 1] = bvis;
	}
	public final void setColumnVisible(String colname,boolean bvis)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return ;
		column_visible[col - 1] = bvis;
	}
	public final boolean getColumnVisible(int col)
	{
		if (col < 1 || col > column_count)
			return false;
		return column_visible[col - 1];
	}
	public final boolean getColumnVisible(String colname)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return false;
		return column_visible[col - 1];
	}

	public final int getColumnCount()
	{
		return column_count;
	}

	public final int  getErrorCode()
	{
		return sql_error_code;
	}
	public final String getErrorMessage()
	{
		return sql_error_msg;
	}
	public final void setErrorCode(int errcode)
	{
		sql_error_code=errcode;	
	}
	public final void setErrorMessage(String errmsg)
	{
		sql_error_msg=errmsg;	
	}	

	
	public synchronized final void removeAllColumn()
	{
	    cache_data.removeAllElements();
	    column_name  = new String[10];
	    column_type  = new int[10];
	    column_super = new String[10];
	    column_label = new String[10];
	    column_size  = new int[10];
            column_memo  = new String[10];
	    column_visible = new boolean[10];
	    column_tooltip = new String[10];
	    column_fmter = new String[10];
	    column_hfmter = new String[10];
	    column_count = 0;
	    filter_cache.clear();
	}

	public final void setPageSize(int pg)
	{
	    page_size = pg;
	}
	public final int  getPageSize()
	{
	    return page_size;
	}

	public final String getColumnName(int col)
	{
		if (col < 1 || col > column_count)
			return null;
		return column_name[col - 1];
	} 
	public final String getColumnLabel(int col)
	{
		if (col < 1 || col > column_count)
			return null;
		return column_label[col - 1];
	} 
	public final String getColumnLabel(String colname)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return null;
		return column_label[col - 1];
	} 
	public final String getColumnSuperLabel(int col)
	{
		if (col < 1 || col > column_count)
			return null;
		return column_super[col - 1];
	} 
	public final String getColumnSuperLabel(String colname)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return null;
		return column_super[col - 1];
	} 
	public final void setColumnLabel(int col,String slabel)
	{
		if (col < 1 || col > column_count)
			return ;
		column_label[col - 1] = slabel;
		column_super[col - 1] = slabel;
	}
	public final void setColumnLabel(String colname,String slabel)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return ;
		column_label[col - 1] = slabel;
		column_super[col - 1] = slabel;
	}
	public final void setColumnSuperLabel(int col,String slabel)
	{
		if (col < 1 || col > column_count)
			return ;
		column_super[col - 1] = slabel;
	}
	public final void setColumnSuperLabel(String colname,String slabel)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return ;
		column_super[col - 1] = slabel;
	}

	public final void setColumnType(int col,int type)
	{
		if (col < 1 || col > column_count)
			return ;
		column_type[col - 1] = type;
	}
	public final void setColumnType(String colname,int type)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return ;
		column_type[col - 1] = type;
	}

	public final void setColumnSize(int col,int len)
	{
		if (col < 1 || col > column_count)
			return ;
		column_size[col - 1] = len;
	}
	public final void setColumnSize(String colname,int len)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return ;
		column_size[col - 1] = len;
	}

	public final void setColumnMemo(int col,String memo)
	{
		if (col < 1 || col > column_count)
			return ;
		column_memo[col - 1] = memo;
	}
	public final void setColumnMemo(String colname,String memo)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return ;
		column_memo[col - 1] = memo;
	}

	public final void setColumnTooltip(int col,String tooltip)
	{
		if (col < 1 || col > column_count)
			return ;
		column_tooltip[col - 1] = tooltip;
	}
	public final void setColumnTooltip(String colname,String tooltip)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return ;
		column_tooltip[col - 1] = tooltip;
	}

	public final void setColumnFormater(int col,String fmter)
	{
		if (col < 1 || col > column_count)
			return ;
		column_fmter[col - 1] = fmter;
	}
	public final void setColumnFormater(String colname,String fmter)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return ;
		column_fmter[col - 1] = fmter;
	}

	public final void setHeaderFormater(int col,String fmter)
	{
		if (col < 1 || col > column_count)
			return ;
		column_hfmter[col - 1] = fmter;
	}
	public final void setHeaderFormater(String colname,String fmter)
	{
		int col = findColumn(colname);
		if (col < 1 || col > column_count)
			return ;
		column_hfmter[col - 1] = fmter;
	}

	public final int getColumnType(int col)
	{
		if (col < 1 || col > column_count)
			return java.sql.Types.VARCHAR;
		return column_type[col - 1];
	} 
	public final int getColumnType(String col)
	{
		return getColumnType(findColumn(col));
	} 

	public final int getColumnSize(int col)
	{
		if (col < 1 || col > column_count)
			return 4;
		return column_size[col - 1];
	}

	public final int getColumnSize(String col)
	{
		return getColumnSize(findColumn(col));
	} 

	public final String getColumnMemo(int col)
	{
		if (col < 1 || col > column_count)
			return null;
		return column_memo[col - 1];
	}

	public final String getColumnMemo(String col)
	{
		return getColumnMemo(findColumn(col));
	} 

	public final String getColumnTooltip(int col)
	{
		if (col < 1 || col > column_count)
			return null;
		return column_tooltip[col - 1];
	}

	public final String getColumnTooltip(String col)
	{
		return getColumnTooltip(findColumn(col));
	} 

	public final String getColumnFormater(int col)
	{
		if (col < 1 || col > column_count)
			return null;
		return column_fmter[col - 1];
	}

	public final String getColumnFormater(String col)
	{
		return getColumnFormater(findColumn(col));
	} 

	public final String getHeaderFormater(int col)
	{
		if (col < 1 || col > column_count)
			return null;
		return column_hfmter[col - 1];
	}

	public final String getHeaderFormater(String col)
	{
		return getHeaderFormater(findColumn(col));
	} 

	public final void copyColumns(DBRowCache data)
	{
		for(int i=1;i<=data.getColumnCount(); i++)
		{
			addColumn(data.getColumnName(i), data.getColumnType(i));       
			setColumnLabel(i,  data.getColumnLabel(i));
			setColumnMemo(i,  data.getColumnMemo(i));
			setColumnFormater(i,  data.getColumnFormater(i));
			setHeaderFormater(i,  data.getHeaderFormater(i));
		}
	}

	public synchronized final void addColumn(String col,int type)
	{
		int i;
		Object data_row[],temp_row[];

		if (col == null || col.trim().length()==0) return;
		i = findColumn(col);
		if (i > 0 ) return;

		if (column_count == column_name.length)
		{
			String old_column_super[] = column_super;
			String old_column_label[] = column_label;
			String old_column_name[] = column_name;
			int    old_column_type[] = column_type;
			int    old_column_size[] = column_size;
			boolean    old_column_visible[] = column_visible;
			String old_column_memo[] = column_memo;
			String old_column_fmter[] = column_fmter;
			String old_column_hfmter[] = column_hfmter;
			String old_column_tooltip[] = column_tooltip;

			column_super = new String[column_count+10];
			column_label = new String[column_count+10];
			column_name  = new String[column_count+10];
			column_type  = new int   [column_count+10];
			column_size  = new int   [column_count+10];
			column_visible=new boolean[column_count+10];
			column_memo  = new String[column_count+10];
			column_fmter = new String[column_count+10];
			column_hfmter = new String[column_count+10];
			column_tooltip = new String[column_count+10];

			System.arraycopy(old_column_super,0,column_super,0,column_count);
			System.arraycopy(old_column_label,0,column_label,0,column_count);
			System.arraycopy(old_column_name,0,column_name,0,column_count);
			System.arraycopy(old_column_type,0,column_type,0,column_count);
			System.arraycopy(old_column_size,0,column_size,0,column_count);
			System.arraycopy(old_column_visible,0,column_visible,0,column_count);
			System.arraycopy(old_column_memo,0,column_memo,0,column_count);
			System.arraycopy(old_column_fmter,0,column_fmter,0,column_count);
			System.arraycopy(old_column_hfmter,0,column_hfmter,0,column_count);
			System.arraycopy(old_column_tooltip,0,column_tooltip,0,column_count);

			column_super[column_count] = col.trim();
			column_label[column_count] = col.trim();
			column_name[column_count] = col.trim();
			column_type[column_count] = type;
			column_size[column_count] = 4;
			column_visible[column_count] = true;
			column_count ++;
			for(i=0;i<cache_data.size();i++)
			{
				temp_row = (Object []) (cache_data.elementAt(i));
				data_row = new Object[column_name.length];
				System.arraycopy(temp_row,0,data_row,0,temp_row.length);
				cache_data.setElementAt(data_row,i);
			}
		}
		else
		{
			column_super[column_count] = col.trim();
			column_label[column_count] = col.trim();
			column_name[column_count] = col.trim();
			column_type[column_count] = type;
			column_size[column_count] = 4;
			column_visible[column_count] = true;
			column_count ++;
		}
	}
	
	public int getRowCount()
	{
		return cache_data.size();
	}
	
	public void setItem(int row,int col,Object val)
	{
		if (col < 1 || col > column_count) 
			return;
		if (row < 1 || row > getRowCount()) 
			return;
		Object record[]  = null;
		record = (Object [])cache_data.elementAt(row - 1);		
		record[col - 1] = val;
	}
	public void setItem(int row,String col,Object val)
	{
		setItem(row,findColumn(col),val);
	}
	public Object getItem(int row,int col) 
	{
		if (col < 1 || col > column_count) 
			return null;
		if (row < 1 || row > getRowCount()) 
			return null;
		Object record[]  = null;
		record = (Object [])cache_data.elementAt(row - 1);		
		return record[col - 1];
	}
	public Object getItem(int row,String col)
	{
		return getItem(row,findColumn(col));
	}

	public Object[] getRow(int row)
	{
		Object row_data[] = {};
		if (column_count < 1) return row_data;
		if (row > cache_data.size() || row < 1) return row_data;
		row_data = (Object []) (cache_data.elementAt(row - 1));
		return row_data;
	}

	public final void appendRow(DBRowCache data)
	{
		if (getColumnCount() == data.getColumnCount())
		{
			for(int i=1;i<=data.getRowCount(); i ++)
			{
				appendRow(data.getRow(i));
			}
		}
	}

	public synchronized int insertRow(int row) 
	{
		if (column_count == 0) return 0;
		Object new_rec = new Object[column_name.length];
		if (row <= 1)
		{
			filter_cache.clear();
			cache_data.insertElementAt(new_rec,0);
			return 1;
		}
		else if (row >= cache_data.size())
		{
			cache_data.addElement(new_rec);
			return cache_data.size();
		}
		else
		{
			filter_cache.clear();
			cache_data.insertElementAt(new_rec,row);
			return row;			
		}
	}

	public synchronized int insertRow(int row,Object record[]) 
	{
		if (column_count == 0) return 0;
		Object ins_record[] = record;
		if (record.length > column_name.length)
		{
			ins_record = new Object[column_name.length];
			System.arraycopy(record,0,ins_record,0,column_name.length);
		}
		else if  (record.length < column_name.length)
		{
			ins_record = new Object[column_name.length];
			System.arraycopy(record,0,ins_record,0,record.length);
		}
		if (row <= 1)
		{
			filter_cache.clear();
			cache_data.insertElementAt(ins_record,0);
			return 1;
		}
		else if (row >= cache_data.size())
		{
			cache_data.addElement(ins_record);
			return cache_data.size();
		}
		else
		{
			filter_cache.clear();
			cache_data.insertElementAt(ins_record,row);
			return row;			
		}
	}

	public synchronized int appendRow() 
	{
		if (column_count == 0) return 0;
		Object new_rec = new Object[column_name.length];
		cache_data.addElement(new_rec);
		return cache_data.size();
	}

	public synchronized int appendRow(Object record[]) 
	{
		if (column_count == 0) return 0;
		Object ins_record[] = record;
		if (record.length > column_name.length)
		{
			ins_record = new Object[column_name.length];
			System.arraycopy(record,0,ins_record,0,column_name.length);
		}
		else if  (record.length < column_name.length)
		{
			ins_record = new Object[column_name.length];
			System.arraycopy(record,0,ins_record,0,record.length);
		}
		cache_data.addElement(ins_record);
		return cache_data.size();
	}

	public int getWidth(boolean percent)
	{
		int totalsize=0;
		Object record[]=null;
		for(int i=0;i<column_count;i++)
			column_size[i] = 4;
		for(int row = 1;row <= getRowCount();row++)
		{
			record = (Object [])(cache_data.elementAt(row - 1));
			for(int i=0;i<column_count;i++)
			{
				if (column_type [i] == java.sql.Types.DATE ||
					column_type [i] == java.sql.Types.TIME ||
					column_type [i] == java.sql.Types.TIMESTAMP )
				{
					column_size[i] = 21;
				}
				else
				{
					int len = 0;
					if (record[i] != null && (len=record[i].toString().getBytes().length) > column_size[i])
					{
						column_size[i] = len;
					}
				}
			}
		}
		for(int i=0;i<column_count;i++)
		{
			if (column_visible[i])
				 totalsize = totalsize + column_size[i];
		}
		if (percent && totalsize > 0)
		{
			for(int i=0;i<column_count;i++)
			{
				if (column_visible[i])
					column_size[i] = (int)(100.0 * column_size[i] / totalsize);
			}
		}
		return totalsize;
	}
	public synchronized void deleteRow(int row)
	{
		if (row < 1 || row > getRowCount()) 
			return;
		cache_data.removeElementAt(row - 1);
		filter_cache.clear();
	}
	public synchronized void deleteAllRow()
	{
		cache_data.removeAllElements();
		filter_cache.clear();
	}

	public double min(String field)
	{
		double result = Double.MAX_VALUE;
		double current = 0;
		int row,col;
		if ((col=findColumn(field)) == 0) return 0;
		try {
			for(row = 1;row <= getRowCount();row++)
			{
				if (getItem(row,col) != null)
				{
					current = Double.valueOf(
						getItem(row,col).toString()
						).doubleValue();
					if (current < result)
						result = current;
				}
			}
		} catch (NumberFormatException nfe) {}
		return result;
	}
	public double min(String field[])
	{
		int i;
		double result=Double.MAX_VALUE;
		double current=0;
		for(i=0;i<field.length;i++)
		{
			current = min(field[i]);
			if (current < result)
				result = current;
		}
		return result;
	}
	public double max(String field)
	{
		double result = Double.MIN_VALUE;
		double current = 0;
		int row,col;
		if ((col=findColumn(field)) == 0) return 0;
		try {
			for(row = 1;row <= getRowCount();row++)
			{
				if (getItem(row,col) != null)
				{
					current = Double.valueOf(
						getItem(row,col).toString()
						).doubleValue();
					if (current > result)
						result = current;
				}
			}
		} catch (NumberFormatException nfe) {}
		return result;
	}
	public double max(String field[])
	{
		int i;
		double result=Double.MIN_VALUE;
		double current=0;
		for(i=0;i<field.length;i++)
		{
			current = max(field[i]);
			if (current > result)
				result = current;
		}
		return result;
	}
	public double avg(String field)
	{
		int rows = count(field);
		if (rows > 0)
			return sum(field)/rows;
		return 0.0;
	}
	public double sum(String field)
	{
		double result=0.0;
		int row=0,col=0;
		if ((col=findColumn(field)) == 0) return 0;
		try {
			for(row=1;row<=getRowCount();row++)
			{
				if (getItem(row,col) != null)
					result = result + Double.valueOf(
						getItem(row,col).toString()
						).doubleValue();
			}
		} catch (NumberFormatException nfe) {}
		return result;		
	}
	public int count(String field)
	{
		int row=0,col=0,rows=0;
		if ((col=findColumn(field)) == 0) return 0;
		for(row=1;row<=getRowCount();row++)
		{
			if (getItem(row,col) != null)
				rows ++;
		}
		return rows;		
	}
	
	public int count()
	{
		return getRowCount();
	}

	public int rownumber(int currow, String field)
	{
		int frows[] = new int[getRowCount()];
		for(int i=1; i<= getRowCount(); i++) frows[i-1] = i;
		
		SimpleDBRowSorter rowsorter = new SimpleDBRowSorter();
		Integer rowid[] = rowsorter.quicksort(this, frows, field);
		
		for(int i=0;i<rowid.length;i++)
		{
			if (rowid[i].intValue() == currow) return i+1;
		}

		return 0;
	}

	public int ranknumber(int currow, String field)
	{
		int frows[] = new int[getRowCount()];
		for(int i=1; i<= getRowCount(); i++) frows[i-1] = i;
		
		SimpleDBRowSorter rowsorter = new SimpleDBRowSorter();
		Integer rowid[] = rowsorter.quicksort(this, frows, field, false);
		
		for(int i=0;i<rowid.length;i++)
		{
			if (rowid[i].intValue() == currow) return i+1;
		}

		return 0;
	}

	public double moveaverage(int currow, String field)
	{			
		double totalvalue = 0.0;  
		if (currow>1) totalvalue += doubleValue(getItem(currow - 1,field),0);
		totalvalue += doubleValue(getItem(currow,field),0); 
		if (currow<getRowCount()) totalvalue += doubleValue(getItem(currow + 1,field),0);
		if (currow==1 || currow == getRowCount()) 
		{
		    if (getRowCount()>1)
		        return totalvalue / 2;
		    else
			return totalvalue / 1;
		}
		else
		    return totalvalue / 3;
	}

	public double deltanumber(int currow, String field)
	{			
		double val1 = 0.0, val2=0.0;  
		int row = currow;

		if (row > 1)
		{
		   val1 = doubleValue(getItem(row-1,field),0);
		   val2 = doubleValue(getItem(row,field),0);
		}
		else
		{
		   val1 = doubleValue(getItem(row,field),0);
		   val2 = val1;
		   if (getRowCount()>1)
		      val2 = doubleValue(getItem(row+1,field),0);
		}
		return val2 - val1;
	}


	public double nextnumber(int currow, String field)
	{			
		double totalvalue = 0.0;  
		int row = currow;
		if (row == getRowCount())
		   row = getRowCount();
		else
		   row = currow + 1;
		totalvalue = doubleValue(getItem(row,field),0); 
		return totalvalue;
	}

	public double prevnumber(int currow, String field)
	{			
		double totalvalue = 0.0;  
		int row = currow;
		if (row == 1)
		   row = 1;
		else
		   row = currow - 1;
		totalvalue = doubleValue(getItem(row,field),0); 
		return totalvalue;
	}

	public double shiftright(int currow, String field)
	{			
		double totalvalue = 0.0;  
		int row = currow;
		if (row == getRowCount())
		   row = 1;
		else
		   row = currow + 1;
		totalvalue = doubleValue(getItem(row,field),0); 
		return totalvalue;
	}

	public double shiftleft(int currow, String field)
	{			
		double totalvalue = 0.0;  
		int row = currow;
		if (row == 1)
		   row = getRowCount();
		else
		   row = currow - 1;
		totalvalue = doubleValue(getItem(row,field),0); 
		return totalvalue;
	}

	public double cum(int currow, String field)
	{			
		double totalvalue = 0.0;  
		if (currow == last_cum_row + 1 && field.equalsIgnoreCase(last_cum_col))
		{
			totalvalue = last_cum_val + doubleValue(getItem(currow,field),0);
		}
		else
		{
		   for(int row=1; row <= currow; row ++) 
		   {
		      totalvalue += doubleValue(getItem(row,field),0); 
		   }
		   last_cum_row = currow;
                   last_cum_val = totalvalue;
		   last_cum_col = field;
		}
		return totalvalue;
	}

	private double min(String field,int frows[])
	{
		double result = 0;
		double current = 0;
		int row,col;
		if (frows == null) return 0;
		if ((col=findColumn(field)) == 0) return 0;
		try {
			for(row = 0;row < frows.length; row++)
			{
				if (getItem(frows[row],col) != null)
				{
					current = Double.valueOf(
						getItem(frows[row],col).toString()
						).doubleValue();
					if (current < result)
						result = current;
				}
			}
		} catch (NumberFormatException nfe) {}
		return result;
	}
	private double min(String field[],int frows[])
	{
		int i;
		double result=0;
		double current=0;
		if (frows == null) return 0;
		for(i=0;i<field.length;i++)
		{
			current = min(field[i], frows);
			if (current < result)
				result = current;
		}
		return result;
	}
	private double max(String field, int frows[])
	{
		double result = 0;
		double current = 0;
		int row,col;
		if (frows == null) return 0;
		if ((col=findColumn(field)) == 0) return 0;
		try {
			for(row = 0;row < frows.length; row++)
			{
				if (getItem(frows[row],col) != null)
				{
					current = Double.valueOf(
						getItem(frows[row],col).toString()
						).doubleValue();
					if (current > result)
						result = current;
				}
			}
		} catch (NumberFormatException nfe) {}
		return result;
	}
	private double max(String field[],int frows[])
	{
		int i;
		double result=0;
		double current=0;
		for(i=0;i<field.length;i++)
		{
			current = max(field[i]);
			if (current > result)
				result = current;
		}
		return result;
	}

	private double avg(String field, int frows[])
	{
		int row = count(field, frows);
		if (row > 0)
			return sum(field,frows)/row;
		return 0.0;
	}

	private double sum(String field, int frows[])
	{
		double result=0.0;
		int row=0,col=0;
		if (frows == null) return 0;
		if ((col=findColumn(field)) == 0) return 0;
		try {
			for(row=0;row<frows.length;row++)
			{
				if (getItem(frows[row],col) != null)
					result = result + Double.valueOf(
						getItem(frows[row],col).toString()
						).doubleValue();
			}
		} catch (NumberFormatException nfe) {}
		return result;		
	}

	private int count(String field,int frows[])
	{
		int row=0,col=0,rows=0;
		if (frows == null) return 0;
		if ((col=findColumn(field)) == 0) return 0;
		for(row=0;row<frows.length;row++)
		{
			if (getItem(frows[row],col) != null)
				rows ++;
		}
		return rows;			
	}

	private int count(int frows[])
	{
		if (frows == null) return 0;
		return frows.length;
	}

	public double min(int currow, String field, int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
		
		return min(field,frows);
	}

	public double min(int currow, String field[], int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
		
		return min(field,frows);
	}

	public double max(int currow, String field, int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
		
		return max(field,frows);
	}

	public double max(int currow, String field[], int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
		
		return max(field,frows);
	}

	public double avg(int currow, String field, int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
		
		return avg(field,frows);
	}

	public double sum(int currow, String field, int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
		
		return sum(field,frows);	
	}

	public int count(int currow, String field, int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
		
		return count(field,frows);		
	}

	public int count(int currow, int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
		
		return count(frows);
	}

	public int rownumber(int currow, String field , int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
		
		SimpleDBRowSorter rowsorter = new SimpleDBRowSorter();
		Integer rowid[] = rowsorter.quicksort(this, frows, field);
		
		for(int i=0;i<rowid.length;i++)
		{
			if (rowid[i].intValue() == currow) return i+1;
		}

		return 0;
	}

	public int ranknumber(int currow, String field , int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
		
		SimpleDBRowSorter rowsorter = new SimpleDBRowSorter();
		Integer rowid[] = rowsorter.quicksort(this, frows, field, false);
		
		for(int i=0;i<rowid.length;i++)
		{
			if (rowid[i].intValue() == currow) return i+1;
		}

		return 0;
	}

	public double moveaverage(int currow, String field , int grpcol[])
	{
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
				
		for(int i=0;i<frows.length;i++)
		{
			if (frows[i] == currow)
			{
			    double totalvalue = 0.0;  
			    if (i>0) totalvalue += doubleValue(getItem(frows[i-1],field),0);
			    totalvalue += doubleValue(getItem(currow,field),0); 
			    if (i<frows.length-1) totalvalue += doubleValue(getItem(frows[i+1],field),0);
			    if (i==0 || i == frows.length - 1) 
			    {
				if (frows.length > 1)
			           return totalvalue / 2;
				else
			           return totalvalue / 1;
			    }
			    else
			        return totalvalue / 3;
			}
		}

		return 0;
	}

	public double deltanumber(int currow, String field, int grpcol[])
	{			
		double val1 = 0.0, val2=0.0;  
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
				
		for(int i=0;i<frows.length;i++)
		{
			if (frows[i] == currow)
			{
			    if (i > 0)
			    {
                                val1 = doubleValue(getItem(frows[i-1],field),0);
                                val2 = doubleValue(getItem(frows[i],field),0);
			    }
			    else
			    {
				val1 = doubleValue(getItem(frows[i],field),0);
				val2 = val1;
				if (frows.length > 1)
				   val2 = doubleValue(getItem(frows[i+1],field),0);
			    }
			    break;
			}
		}

		return val2 - val1;
	}

	public double nextnumber(int currow, String field, int grpcol[])
	{			
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
				
		for(int i=0;i<frows.length;i++)
		{
			if (frows[i] == currow)
			{
			    double totalvalue = 0.0;  
			    int row = currow;
			    if (i==frows.length - 1)
				row = frows[i];
			    else
				row = frows[i+1];

			    totalvalue = doubleValue(getItem(row,field),0);
			    return totalvalue;
			}
		}

		return 0;
	}

	public double prevnumber(int currow, String field, int grpcol[])
	{			
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
				
		for(int i=0;i<frows.length;i++)
		{
			if (frows[i] == currow)
			{
			    double totalvalue = 0.0;  
			    int row = currow;
			    if (i==0)
				row = frows[0];
			    else
				row = frows[i-1];

			    totalvalue = doubleValue(getItem(row,field),0);
			    return totalvalue;
			}
		}

		return 0;
	}

	public double shiftright(int currow, String field, int grpcol[])
	{			
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
				
		for(int i=0;i<frows.length;i++)
		{
			if (frows[i] == currow)
			{
			    double totalvalue = 0.0;  
			    int row = currow;
			    if (i==frows.length - 1)
				row = frows[0];
			    else
				row = frows[i+1];

			    totalvalue = doubleValue(getItem(row,field),0);
			    return totalvalue;
			}
		}

		return 0;
	}

	public double shiftleft(int currow, String field, int grpcol[])
	{			
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
				
		for(int i=0;i<frows.length;i++)
		{
			if (frows[i] == currow)
			{
			    double totalvalue = 0.0;  
			    int row = currow;
			    if (i==0)
				row = frows[frows.length - 1];
			    else
				row = frows[i-1];

			    totalvalue = doubleValue(getItem(row,field),0);
			    return totalvalue;
			}
		}

		return 0;
	}

	public double cum(int currow, String field, int grpcol[])
	{			
		if (grpcol == null || grpcol.length == 0) return 0;
		Object grpval[] = new Object[grpcol.length];
		for(int i=0;i<grpcol.length;i++)
			grpval[i] = getItem(currow, grpcol[i]);
		int frows[] = filter(grpcol, grpval);
				
		double totalvalue = 0.0;  
		for(int i=0;i<frows.length;i++)
		{
		    totalvalue += doubleValue(getItem(frows[i],field),0);
		    if (frows[i] == currow) break;
		}

		return totalvalue;
	}
	
	private String readLine(java.io.BufferedReader ib,String lineend) throws java.io.IOException
	{
		String record=null;
		String line;
		while((line=ib.readLine())!=null)
		{
			if (record == null)
				record = "";
			if (line.endsWith(lineend))
				record = record + "\r\n"+line.substring(0,line.length()-lineend.length());
			else
				record = record + "\r\n"+line;
		}
		return record;
	}

	public int  read(java.io.BufferedReader ib,int rows) throws java.io.IOException
	{
		return read(ib,"\t",rows);
	}
	public int  read(java.io.BufferedReader ib,String seperator,int rows) throws java.io.IOException
	{
		String line;
		int row,col,ins_row;
		row = getRowCount();
		while(row < rows && (line = ib.readLine()) != null)
		{
			Object fields[] = TextUtils.getFields(line,seperator).toArray();
			for(col = 0 ;col < fields.length && col < getColumnCount();col ++)
			{
				fields[col] = SQLTypes.getValue(getColumnType(col + 1),fields[col]); 
			}
			appendRow(fields);
			row ++;
		}
		return row;
	}
	public int  read(java.io.BufferedReader ib,String seperator,String rec_end,int rows) throws java.io.IOException
	{
		String line;
		int row,col,ins_row;
		row = getRowCount();
		while(row < rows && (line = readLine(ib,rec_end)) != null)
		{
			Object fields[] = TextUtils.getFields(line,seperator).toArray();
			for(col = 0 ;col < fields.length && col < getColumnCount();col ++)
			{
				fields[col] = SQLTypes.getValue(getColumnType(col + 1),fields[col]); 
			}
			appendRow(fields);
			row ++;
		}
		return row;
	}

	public final java.util.HashMap getHashProperty(String line)
	{
		java.util.HashMap _hashmap = new java.util.HashMap();
		if (line != null)
		{
		  java.util.Vector<String> line_vector = TextUtils.getWords(line,";");
		  for(int i=0;i<line_vector.size();i++)
		  {
		    if (line_vector.get(i) != null)
		    {
			java.util.Vector word_vector = TextUtils.getWords(line_vector.get(i).trim(),"=");
			if (word_vector.size() >= 2)
			{
			   if (word_vector.get(0) != null && word_vector.get(1) != null)
			   {
			      _hashmap.put(word_vector.get(0), word_vector.get(1));
			   }
			}
		    }
		  }
		}
		return _hashmap;
	}

	public void  writeXMLBody(java.io.Writer out,VariableTable vt) throws java.io.IOException
	{
		writeXMLBody(out,"dataset","",column_count,vt);
	}

	public void  writeXMLBody(java.io.Writer out,int grpcount,VariableTable vt) throws java.io.IOException
	{
		writeXMLBody(out,"dataset","",grpcount,vt);
	}

	public void  writeXMLBody(java.io.Writer out,String tag,int grpcount,VariableTable vt) throws java.io.IOException
	{
		writeXMLBody(out,tag,"",grpcount,vt);
	}

	public void  writeXMLBody(java.io.Writer out,String tag,int grpcount,String collen[],VariableTable vt) throws java.io.IOException
	{
		writeXMLBody(out,tag,"",grpcount, collen,vt);
	}

	public void  writeXMLBody(java.io.Writer out,String tag,String attr,int grpcolcount,VariableTable vt) throws java.io.IOException
	{
		String collen[]={};
		writeXMLBody(out,tag,attr,grpcolcount, collen,vt);
	}

	public void  writeXMLBody(java.io.Writer out,String tag,String attr,int grpcolcount, String collen[],VariableTable vt) throws java.io.IOException
	{
		writeXMLBody(out, tag, attr, grpcolcount, null, collen, vt);
	}

	public void  writeXMLBody(java.io.Writer out,String tag,String attr,int grpcolcount, String mergecols[], String collen[],VariableTable vt) throws java.io.IOException
	{
		writeXMLBody(out, tag, attr, grpcolcount, mergecols, collen, vt, false);
	}

	public void  writeXMLBody(java.io.Writer out,String tag,String attr,int grpcolcount, String mergecols[], String collen[],VariableTable vt, boolean editmode) throws java.io.IOException
	{
		int row,col,totallen=0, lencount=0,duplabelcnt=0;
		Object row_data[];
		String col_align[] = new String[column_count];
		int mgcols[] = new int[1];
		int _mergecols[] = new int[column_count];
		String _editors[] = new String[column_count];
		String _styles[] = new String[column_count];
		String _values[] = new String[column_count];
		String _editorstyle[] = new String[column_count];
		boolean _iskey_column[] = new boolean[column_count];
		String head_formater = getStringProperty("HEADFORMATER");				
		String data_formater = getStringProperty("DATAFORMATER");
		String column_align  = getStringProperty("COLUMNALIGN");
		String column_editor = getStringProperty("COLUMNEDITOR");
		String column_style = getStringProperty("EDITORSTYLE");
		String column_values = getStringProperty("COLUMNVALUES");
		String key_columns   = getStringProperty("PRIMARYKEY");
		String rowcolor = getStringProperty("ROWCOLOR");
		String rowstyle = getStringProperty("ROWSTYLE");
		String color_column = null;
		java.util.HashMap color_maping = new java.util.HashMap();

		if (rowcolor != null)
		{
			String row_color_words[] = TextUtils.toStringArray(TextUtils.getWords(rowcolor,"|"));
			if (row_color_words.length == 2)
			{
				color_column = row_color_words[0];
				color_maping = getHashProperty(row_color_words[1]);
			}
		}

                if (collen != null) lencount = collen.length;

		if (attr != null && attr.trim().length()>0)
		{
			if (editmode)
			{
			   out.write("\t<");
			   out.write(tag);
			   out.write(" edit=\"yes\" ");
			   out.write(attr);
			   out.write(">\n");
			}
			else
			{
			   out.write("\t<");
			   out.write(tag);
			   out.write(" ");
			   out.write(attr);
			   out.write(">\n");
			}
		}
		else
		{
			if (editmode)
			{
				out.write("\t<");
				out.write(tag);
				out.write(" edit=\"yes\">\n");
			}
			else
			{
				out.write("\t<");
				out.write(tag);
				out.write(">\n");
			}
		}
		totallen=getWidth(false);
		out.write("\t\t<head len=\"");
		out.write(String.valueOf(totallen));
		out.write("\">\n");
		if (mergecols != null)
		{
		    for(int i=0;i<mergecols.length;i++)
		    {
			_mergecols[i] = findColumn(mergecols[i]);
		    }
		}
		for(int i=0;i<_iskey_column.length;i++) _iskey_column[i] = false;
		if (key_columns != null)
		{
		    String tmpkey[] = TextUtils.toStringArray(TextUtils.getWords(key_columns,","));
		    for(int i=0;i<tmpkey.length;i++)
		    {
			if (tmpkey[i].startsWith("*"))
			{
			   row = findColumn(tmpkey[i].substring(1));
			   if (row > 0) _iskey_column[row - 1] = true;
			}
		    }
		}
		for(int i=0;i<_editors.length;i++) _editors[i] = "text";
		if (column_editor != null)
		{
		    String tmpkey[] = TextUtils.toStringArray(TextUtils.getWords(column_editor,","));
		    for(int i=0;i<tmpkey.length;i++)
		    {
			    row = findColumn(tmpkey[i]);
			    if (row > 0) _editors[row - 1] = "textarea";
		    }
		}

		for(int i=0;i<_editorstyle.length;i++) _editorstyle[i] = null;
		if (column_style != null)
		{
		    String tmpline[] = TextUtils.toStringArray(TextUtils.getLines(column_style));
		    for(int i=0;i<tmpline.length;i++)
		    {
			    String tmpkey[] = TextUtils.toStringArray(TextUtils.getWords(tmpline[i],"|"));
			    if (tmpkey.length == 2)
			    {
			       row = findColumn(tmpkey[0]);
			       if (row > 0) _editorstyle[row - 1] = tmpkey[1];
			    }
		    }
		}

		for(int i=0;i<_values.length;i++) _values[i] = null;
		if (column_values != null)
		{
		    String tmpline[] = TextUtils.toStringArray(TextUtils.getLines(column_values));
		    for(int i=0;i<tmpline.length;i++)
		    {
			    String tmpkey[] = TextUtils.toStringArray(TextUtils.getWords(tmpline[i],"|"));
			    if (tmpkey.length == 2)
			    {
			       row = findColumn(tmpkey[0]);
			       if (row > 0) _values[row - 1] = tmpkey[1];
			    }
		    }
		}

		for(int i=0;i<_styles.length;i++) _styles[i] = null;
		if (rowstyle != null)
		{
		    String tmpline[] = TextUtils.toStringArray(TextUtils.getLines(rowstyle));
		    for(int i=0;i<tmpline.length;i++)
		    {
			    String tmpkey[] = TextUtils.toStringArray(TextUtils.getWords(tmpline[i],"|"));
			    if (tmpkey.length == 2)
			    {
			       row = findColumn(tmpkey[0]);
			       if (row > 0) _styles[row - 1] = tmpkey[1];
			    }
		    }
		}

		for(row=1;row<=getColumnCount();row++)
		{
		    if (head_formater != null)
		    {
			out.write("\t\t<content><![CDATA[");
			out.write(vt.parseString(head_formater));
			out.write("]]></content>\n");
		    }
		    else
		    {
			if (! column_visible[row - 1]) continue;
		        if (row - 1 < lencount)
                        {
			      out.write("\t\t\t<col id=\"");
			      out.write(String.valueOf(row));
			      out.write("\" type=\"");
			      out.write(SQLTypes.getTypeName(column_type[row - 1]));
			      out.write("\" typeid=\"");
			      out.write(String.valueOf(column_type[row - 1]));
			      out.write("\" size=\"");
                              out.write(String.valueOf(collen[row-1]));
			      out.write("\" len=\"");
			      out.write(String.valueOf(column_size[row - 1]));
			      out.write("\" ");
			}
			else
                        {
			      out.write("\t\t\t<col id=\"");
			      out.write(String.valueOf(row));
			      out.write("\" type=\"");
			      out.write(SQLTypes.getTypeName(column_type[row - 1]));
			      out.write("\" typeid=\"");
			      out.write(String.valueOf(column_type[row - 1]));
			      out.write("\" size=\"");
			      out.write(String.valueOf(((int)(100.0 * column_size[row - 1] / totallen))));
			      out.write("%\" len=\"");
			      out.write(String.valueOf(column_size[row - 1]));
			      out.write("\" ");
			}
			out.write(" form=\""+_editors[row-1]+"\" ");
			if (_editorstyle[row - 1] != null)
			{
			      out.write(" formstyle=\""+ EncodeXML(_editorstyle[row - 1]) + "\" ");
			}
			if (_iskey_column[row - 1]) out.write(" pk=\"yes\" ");
			if (column_type [row - 1] == java.sql.Types.LONGVARCHAR ||
				column_type [row - 1] == java.sql.Types.VARCHAR ||
				column_type [row - 1] == java.sql.Types.LONGVARBINARY ||
				column_type [row - 1] == java.sql.Types.CLOB ||
				column_type [row - 1] == java.sql.Types.BLOB ||
				column_type [row - 1] == java.sql.Types.CHAR )
			{
				if (column_size[row - 1] < 8)
				{
					out.write("align=\"center\">\n");
					col_align[row - 1] = "center";
				}
				else
				{
					out.write("align=\"left\">\n");
					col_align[row - 1] = "left";
				}
			}
			else if (column_type [row - 1] == java.sql.Types.DATE ||
				column_type [row - 1] == java.sql.Types.TIME ||
				column_type [row - 1] == java.sql.Types.TIMESTAMP )
			{
				out.write("align=\"center\">\n");
				col_align[row - 1] = "center";
			}
			else
			{
				out.write("align=\"right\">\n");
				col_align[row - 1] = "right";
			}
			out.write("\t\t\t\t<name><![CDATA[");
			out.write(column_name[row - 1]);
			out.write("]]></name>\n");
			
                        duplabelcnt = 0;
			for(int i=row+1;i<=column_count;i++)
			{
			    if (column_super[row-1].equals(column_super[i - 1])) duplabelcnt ++;
			}

			if (duplabelcnt == 0)
			{
			    if (row > 1 && column_super[row - 1].equals(column_super[row - 2]))
		            {
			       // out.write("\t\t\t\t<super colspan=\"0\"><![CDATA[");
			       // out.write(TextUtils.toHtmlLines(column_super[row - 1]));
			       // out.write("]]></super>\n");
			       out.write("\t\t\t\t<label><![CDATA[");
			       out.write(TextUtils.toHtmlLines(column_label[row - 1]));
			       out.write("]]></label>\n");
                            }
                            else if (column_super[row - 1].equals(column_label[row - 1]))
		            {
			       out.write("\t\t\t\t<rowspan>2</rowspan>\n");
			       out.write("\t\t\t\t<super><![CDATA[");
			       out.write(TextUtils.toHtmlLines(column_super[row - 1]));
			       out.write("]]></super>\n");
			       // out.write("\t\t\t\t<label><![CDATA[");
			       // out.write(TextUtils.toHtmlLines(column_label[row - 1]));
			       // out.write("]]></label>\n");
                            }
			    else
		            {
			       out.write("\t\t\t\t<super><![CDATA[");
			       out.write(TextUtils.toHtmlLines(column_super[row - 1]));
			       out.write("]]></super>\n");
			       out.write("\t\t\t\t<label><![CDATA[");
			       out.write(TextUtils.toHtmlLines(column_label[row - 1]));
			       out.write("]]></label>\n");
                            }
                        }
			else
			{
			    if (row > 1 && column_super[row - 1].equals(column_super[row - 2]))
		            {
			       // out.write("\t\t\t\t<super colspan=\"0\"><![CDATA[");
			       // out.write(TextUtils.toHtmlLines(column_super[row - 1]));
			       // out.write("]]></super>\n");
			       out.write("\t\t\t\t<label><![CDATA[");
			       out.write(TextUtils.toHtmlLines(column_label[row - 1]));
			       out.write("]]></label>\n");
                            }
			    else
			    {
			       out.write("\t\t\t\t<colspan>"+(duplabelcnt+1)+"</colspan>\n");
			       out.write("\t\t\t\t<super><![CDATA[");
			       out.write(TextUtils.toHtmlLines(column_super[row - 1]));
			       out.write("]]></super>\n");
			       out.write("\t\t\t\t<label><![CDATA[");
			       out.write(TextUtils.toHtmlLines(column_label[row - 1]));
			       out.write("]]></label>\n");
                            }
			}

			if (column_hfmter[row - 1] != null)
			{
			    out.write("\t\t\t\t<formater><![CDATA[");
			    out.write(vt.parseString(column_hfmter[row - 1]));
			    out.write("]]></formater>\n");
			}
			out.write("\t\t\t</col>\n");
		    }
		}
		out.write("\t\t</head>\n");
		if (column_align != null)
		{
		    String tmpwords[] = TextUtils.toStringArray(TextUtils.getWords(column_align,"|"));
		    for(row = 0; row < tmpwords.length && row < getColumnCount(); row++)
		    {
			if (tmpwords[row] != null) col_align[row] = tmpwords[row];
		    }
		}
		boolean newgrp[] = new boolean[column_count];
		int 	grprows[] = new int[column_count];
		for(row=1;row<=getRowCount();row++)
		{
		    if (page_size > 0 && row % page_size == 1)
		    {
			if (row > 1) out.write("\t\t</page>\n");
			    out.write("\t\t<page>\n");
		    }
		    if (color_column != null && color_maping.containsKey(getItem(row, color_column)))
		    {
		       out.write("\t\t<row id=\"");
		       out.write(String.valueOf(row));
		       out.write("\" color=\"");
		       out.write(color_maping.get(getItem(row, color_column)).toString());
		       out.write("\">\n");
		    }
		    else
		    {
		       out.write("\t\t<row id=\"");
		       out.write(String.valueOf(row));
		       out.write("\">\n");
		    }
		    if (data_formater != null)
		    {
			out.write("\t\t<content><![CDATA[");
			out.write(parseString(data_formater,vt,row, 1));
			out.write("]]></content>\n");
		    }
		    else
		    {
			row_data = (Object [])(cache_data.elementAt(row - 1));

			//int col_span[] = countspan(row);

			for(col=1;col<=getColumnCount();col++)
			{
				if (! column_visible[col - 1]) continue;
				out.write("\t\t\t<col id=\"");
				out.write(String.valueOf(col));
				out.write("\" align=\"");
				out.write(col_align[col - 1]);
				out.write("\" ");

				if (column_memo[col-1] != null)
                                {
                                    out.write("href=\"");
				    out.write(EncodeHTML(parseString(column_memo[col-1],vt,row, 1)));
				    out.write("\" ");
                                }

				if (column_tooltip[col-1] != null)
                                {
                                    out.write("title=\"");
				    out.write(EncodeHTML(parseString(column_tooltip[col-1],vt,row, 2)));
				    out.write("\" ");
                                }

				if (_values[col-1] != null)
                                {
                                    out.write("htmlcode=\"");
				    if (row_data[col - 1] != null)
				        out.write(EncodeXML(getSelectHTML(_values[col-1],row_data[col - 1].toString())));
				    else
				        out.write(EncodeXML(getSelectHTML(_values[col-1],null)));
				    out.write("\" ");
                                }

				if (_styles[col-1] != null)
                                {
                                    out.write("style=\"");
				    out.write(EncodeXML(parseString(_styles[col-1],vt,row, 0)));
				    out.write("\" ");
                                }

				if (col <= grpcolcount)
				{
					newgrp[col - 1] = rowEquals(row,row-1,col);
					if (page_size > 0 && row % page_size == 1) newgrp[col - 1] = false;

					if (!newgrp[col - 1])
					{
						int col_id[] = new int[col];
						for(int i=0;i<col;i++)
							col_id[i]=i+1;
						grprows[col - 1] = countgroup(col_id,row);
					}
					
					if (newgrp[col - 1])
					{
						out.write("grp=\"0\" ");
					}
					else 
					{
						out.write("grp=\"");
						out.write(String.valueOf(grprows[col-1]));
						out.write("\" ");
					}
				}	
				else
				{		
				    if (mergecols != null && mergecols.length > 0)
				    {
					for(int i=0;i<mergecols.length;i++)
					{
						if (_mergecols[i] == col)
						{
							mgcols[0] = col; 
							newgrp[col - 1] = rowEquals(row,row-1,mgcols);
							if (page_size > 0 && row % page_size == 1) newgrp[col - 1] = false;

							if (!newgrp[col - 1])
								grprows[col - 1] = countgroup(mgcols,row);
					
							if (newgrp[col - 1])
							{
								out.write("grp=\"0\" ");
							}
							else 
							{
								out.write("grp=\"");
								out.write(String.valueOf(grprows[col-1]));
								out.write("\" ");
							}
						}
					}
				    }
				}

				if ( column_fmter[col - 1] != null)
				{
					out.write("cv=\"1\" ");
				}

				if (    column_fmter[col - 1] != null ||
				        column_type [col - 1] == java.sql.Types.LONGVARCHAR ||
					column_type [col - 1] == java.sql.Types.VARCHAR ||
					column_type [col - 1] == java.sql.Types.CLOB ||
					column_type [col - 1] == java.sql.Types.BLOB ||
					column_type [col - 1] == java.sql.Types.CHAR )
				{
					out.write("><![CDATA[");
				}
				else
				{
						out.write(">");
				}
				if ( column_fmter[col - 1] != null )
				{
					if(row_data[col - 1] != null)
					{
						out.write(parseString(column_fmter[col-1],vt,row, 2));
					}
					/*
					else
					{
					    if (column_fmter[col - 1] != null ||
					        column_type [col - 1] == java.sql.Types.LONGVARCHAR ||
						column_type [col - 1] == java.sql.Types.VARCHAR ||
						column_type [col - 1] == java.sql.Types.CLOB ||
						column_type [col - 1] == java.sql.Types.BLOB ||
						column_type [col - 1] == java.sql.Types.CHAR )
					    {
						// out.write("&nbsp;");
						out.write(" ");
					    }
					    else
					    {
						// out.write("&amp;nbsp;");
						out.write(" ");
					    }
					}					
					*/
				}
				else
				{
					if(row_data[col - 1] != null)
					{
						out.write(row_data[col - 1].toString());
					}
					/*
					else
					{
					    if (    column_fmter[col - 1] != null ||
					        column_type [col - 1] == java.sql.Types.LONGVARCHAR ||
						column_type [col - 1] == java.sql.Types.VARCHAR ||
						column_type [col - 1] == java.sql.Types.CLOB ||
						column_type [col - 1] == java.sql.Types.BLOB ||
						column_type [col - 1] == java.sql.Types.CHAR )
					    {
						// out.write("&nbsp;");
						out.write(" ");
					    }
					    else
					    {
						// out.write("&amp;nbsp;");
						out.write(" ");
					    }
					}
					*/
				}
				if (    column_fmter[col - 1] != null ||
					column_type [col - 1] == java.sql.Types.LONGVARCHAR ||
					column_type [col - 1] == java.sql.Types.VARCHAR ||
					column_type [col - 1] == java.sql.Types.CLOB ||
					column_type [col - 1] == java.sql.Types.BLOB ||
					column_type [col - 1] == java.sql.Types.CHAR )
					out.write("]]>");
				out.write("</col>\n");				
			}
		    }
		    out.write("\t\t</row>\n");
		}
		if (page_size > 0)  out.write("\t\t</page>\n");
		out.write("\t</");
		out.write(tag);
		out.write(">\n");		
	}

	public void  write(java.io.PrintStream ps)
	{
		write(ps,"\t");
	}
	public void  write(java.io.PrintStream ps,int row)
	{
		write(ps,"\t",row);
	}
	public void  write(java.io.PrintStream ps,String seperator)
	{
		Object val;
		int row,col;
		for(row = 1;row<=getRowCount();row++)
		{
			for(col=1;col<=column_count;col++)
			{
				val = getItem(row,col);
				if (val != null)
					ps.print(val);
				if (col != column_count)
					ps.print(seperator);
			}
			ps.print("\r\n");
		}
	}
	public void  write(java.io.PrintStream ps,String seperator,int row)
	{
		Object val;
		int col;
		for(col=1;col<=column_count;col++)
		{
			val = getItem(row,col);
			if (val != null)
				ps.print(val);
			if (col != column_count)
				ps.print(seperator);
		}
		ps.print("\r\n");
	}

	public void  write(java.io.PrintStream ps,String seperator,String rec_end)
	{
		Object val;
		int row,col;
		for(row = 1;row<=getRowCount();row++)
		{
			for(col=1;col<=column_count;col++)
			{
				val = getItem(row,col);
				if (val != null)
					ps.print(val);
				if (col != column_count)
					ps.print(seperator);
			}
			ps.print(rec_end);
			ps.print("\r\n");
		}
	}

	public void  write(java.io.Writer ps)  throws java.io.IOException
	{
		write(ps,"\t");
	}
	public void  write(java.io.Writer ps,int row)  throws java.io.IOException
	{
		write(ps,"\t",row);
	}
	public void  write(java.io.Writer ps,String seperator)  throws java.io.IOException
	{
		Object val;
		int row,col;
		for(row = 1;row<=getRowCount();row++)
		{
			for(col=1;col<=column_count;col++)
			{
				val = getItem(row,col);
				if (val != null)
					ps.write(val.toString());
				if (col != column_count)
					ps.write(seperator);
			}
			ps.write("\r\n");
		}
	}
	public void  write(java.io.Writer ps,String seperator,int row) throws java.io.IOException
	{
		Object val;
		int col;
		for(col=1;col<=column_count;col++)
		{
			val = getItem(row,col);
			if (val != null)
				ps.write(val.toString());
			if (col != column_count)
				ps.write(seperator);
		}
		ps.write("\r\n");
	}

	public void  writeForm(java.io.Writer ps) throws java.io.IOException
	{
		Object val;
		int row, col, maxlen=0;
		for(col=1;col<=column_count;col++)
		{
		    if (getColumnName(col).length() > maxlen)
		    {
			maxlen = getColumnName(col).length();
		    } 
		}
		for(row=1;row<=getRowCount();row++)
		{
		    for(col=1;col<=column_count;col++)
		    {
			ps.write(getColumnName(col));
			ps.write("                                                      ".substring(0, maxlen -  getColumnName(col).length() + 1));
			ps.write("= ");
			val = getItem(row,col);
			if (val != null)
				ps.write(val.toString());
			else
				ps.write("(null)");
  		        ps.write("\r\n");
		   }
	           ps.write("\r\n");
		}
	}

	public void  writeForm(java.io.Writer ps, int row) throws java.io.IOException
	{
		Object val;
		int col, maxlen=0;
		for(col=1;col<=column_count;col++)
		{
		    if (getColumnName(col).length() > maxlen)
		    {
			maxlen = getColumnName(col).length();
		    } 
		}
		for(col=1;col<=column_count;col++)
		{
			ps.write(getColumnName(col));
			ps.write("                                                      ".substring(0, maxlen -  getColumnName(col).length() + 1));
			ps.write("= ");
			val = getItem(row,col);
			if (val != null)
				ps.write(val.toString());
			else
				ps.write("(null)");
  		        ps.write("\r\n");
		}
	}

	public void  writeForm(java.io.PrintStream ps) throws java.io.IOException
	{
		Object val;
		int row, col, maxlen=0;
		for(col=1;col<=column_count;col++)
		{
		    if (getColumnName(col).length() > maxlen)
		    {
			maxlen = getColumnName(col).length();
		    } 
		}
		for(row=1;row<=getRowCount();row++)
		{
		    for(col=1;col<=column_count;col++)
		    {
			ps.print(getColumnName(col));
			ps.print("                                                      ".substring(0, maxlen -  getColumnName(col).length() + 1));
			ps.print("= ");
			val = getItem(row,col);
			if (val != null)
				ps.print(val.toString());
			else
				ps.print("(null)");
  		        ps.print("\r\n");
		   }
	           ps.print("\r\n");
		}
	}

	public void  writeForm(java.io.PrintStream ps, int row) throws java.io.IOException
	{
		Object val;
		int col, maxlen=0;
		for(col=1;col<=column_count;col++)
		{
		    if (getColumnName(col).length() > maxlen)
		    {
			maxlen = getColumnName(col).length();
		    } 
		}
		for(col=1;col<=column_count;col++)
		{
			ps.print(getColumnName(col));
			ps.print("                                                      ".substring(0, maxlen -  getColumnName(col).length() + 1));
			ps.print("= ");
			val = getItem(row,col);
			if (val != null)
				ps.print(val.toString());
			else
				ps.print("(null)");
  		        ps.print("\r\n");
		}
	}

	public void  write(java.io.Writer ps,String seperator,String rec_end)  throws java.io.IOException
	{
		Object val;
		int row,col;
		for(row = 1;row<=getRowCount();row++)
		{
			for(col=1;col<=column_count;col++)
			{
				val = getItem(row,col);
				if (val != null)
					ps.write(val.toString());
				if (col != column_count)
					ps.write(seperator);
			}
			ps.write(rec_end);
			ps.write("\r\n");
		}
	}

	public void  write(java.io.Writer ps,String seperator,String rec_end,int row) throws java.io.IOException
	{
		Object val;
		int col;
		for(col=1;col<=column_count;col++)
		{
			val = getItem(row,col);
			if (val != null)
				ps.write(val.toString());
			if (col != column_count)
				ps.write(seperator);
		}
		ps.write(rec_end);
		ps.write("\r\n");
	}

	public void  write(java.io.RandomAccessFile ps) throws java.io.IOException
	{
		write(ps,"\t");
	}

	public void  write(java.io.RandomAccessFile ps,String seperator) throws java.io.IOException
	{
		Object val;
		int row,col;
		for(row = 1;row<=getRowCount();row++)
		{
			for(col=1;col<=column_count;col++)
			{
				val = getItem(row,col);
				if (val != null)
					ps.writeBytes(val.toString());
				if (col != column_count)
					ps.writeBytes(seperator);
			}
			ps.writeBytes("\r\n");
		}
	}


	public void  write(java.io.RandomAccessFile ps,String seperator,String rec_end) throws java.io.IOException
	{
		Object val;
		int row,col;
		for(row = 1;row<=getRowCount();row++)
		{
			for(col=1;col<=column_count;col++)
			{
				val = getItem(row,col);
				if (val != null)
					ps.writeBytes(val.toString());
				if (col != column_count)
					ps.writeBytes(seperator);
			}
			ps.writeBytes(rec_end);
			ps.writeBytes("\r\n");
		}
	}

	public void  write(java.io.PrintStream ps,String seperator,String rec_end,int row)
	{
		Object val;
		int col;
		for(col=1;col<=column_count;col++)
		{
			val = getItem(row,col);
			if (val != null)
				ps.print(val);
			if (col != column_count)
				ps.print(seperator);
		}
		ps.print(rec_end);
		ps.print("\r\n");
	}

	protected void finalize() throws java.lang.Throwable
	{
		cache_data.removeAllElements();
	}

	public String  getString(int row)
	{
		return getString("|",row);
	}
	public String  getString(String seperator,int row)
	{
		String record="";
		Object val;
		int col;
		for(col=1;col<=column_count;col++)
		{
			val = getItem(row,col);
			if (val != null)
				record=record+val.toString();
			if (col != column_count)
				record=record+seperator;
		}
		return record;
	}

	public final String  getFullText()
	{
		StringBuffer valbuf = new StringBuffer(1024);
		for(int i=1;i<=getRowCount();i++)
		{
		   for(int j=1; j<=getColumnCount();j++)
		   {
			Object itemval = getItem(i,j);
			if (itemval != null)
			{
			    valbuf.append("ARR_");
			    valbuf.append(getColumnName(j));
			    valbuf.append("=");
			    valbuf.append(itemval.toString());
			    valbuf.append(";");
			}
		   }
		   valbuf.append("\n");
		}
		return valbuf.toString();
	}

	public final int find(int col,Object val)
	{
		return find(col,val,1);	
	}

	public final int find(int col,Object val,int start)
	{
		int i;
		if (col<1 || col > column_count) return 0;
		for(i=start;i<=cache_data.size();i++)
		{
			if (val == null)
			{
				if (getItem(i,col) == null)
					return i;
			}
			else
			{
				if (val.equals(getItem(i,col)))
					return i;
			}
		}
		return 0;
	}

	public final int find(int col[],Object val[])
	{
		return find(col,val,1);
	}
	public final int find(int col[],Object val[],int start)
	{
		int i,j;
		boolean found=true;
		if (col.length != val.length) return 0;
		for(i=0;i<col.length;i++)
		{
			if (col[i]<1 || col[i] > column_count) return 0;
		}
		for(i=start;i<=cache_data.size();i++)
		{
			found=true;
			for(j=0;j<col.length;j++)
			{
				if (val[j] == null)
				{
					if (getItem(i,col[j]) != null)
					{
						found=false;
						break;
					}
				}
				else
				{
					if (!val[j].equals(getItem(i,col[j])))
					{
						found=false;
						break;
					}
				}
			}
			if (found)
				return i;
		}
		return 0;
	}

	public final int count(int col,Object val)
	{
		int i=0,count=0;
		while(true)
		{
			i=find(col,val,i+1);
			if (i==0) break;
			count ++;
		}
		return count;
	}

	public final int[] filter(int col,Object val)
	{
		int rows[]={},i,j;
		if ((i=count(col,val)) == 0) return rows;
		rows = new int[i];
		i=0;
		j=0;
		while((i=find(col,val,i+1))>0)
		{
			rows[j]=i;
			j++;
		}
		return rows;		
	}

	public final int count(int col[],Object val[])
	{
		/*
		int i=0,count=0;
		while(true)
		{
			i=find(col,val,i+1);
			if (i==0) break;
			count ++;
		}
		return count;
		*/
		return filter(col,val).length;
	}

	public final int[] filter(int col[],Object val[])
	{
		
		int rows[]={},i=0;

		if (col ==null || val==null || col.length==0 || col.length != val.length)
			return rows;

		RowValuesObject filteritem = new RowValuesObject(col, val);

		if (filter_cache.size()>256) filter_cache.clear();
		if (filter_cache.containsKey(filteritem))
		{
		    rows = (int []) (filter_cache.get(filteritem));
		    return rows;
		}
		rows = filter(col[0],val[0]);

		if (rows.length == 0)
		{
		    filter_cache.put(filteritem, rows);
		    return rows;
		}

		for(i=1;i<col.length;i ++)
		{
			rows=filter(col[i],val[i],rows);
			if (rows.length == 0)
				break;
		}
		filter_cache.put(filteritem, rows);

		return rows;
	}

	public final int[] countspan(int row)
	{
		int c_span[] = new int[getColumnCount()];
		int start=0;
		Object val1,val2;
		for(int i=0;i<getColumnCount();i++) c_span[i]=0;
		for(int i=0;i<getColumnCount()-1;i++)
		{
			val1 = getItem(row,i+1);
			c_span[start]=1;
			for(int j=i+1;j<getColumnCount();j++)
			{
				val2 = getItem(row,j+1);
				if (val1 == null && val2 == null)
				{
					c_span[start]++;
				}
				else if (val1 != null && val1.equals(val2))
				{
					c_span[start]++;					
				}
				else if (val2 != null && val2.equals(val1))
				{
					c_span[start]++;
				}
				else
				{
					start = j;
					c_span[start]=1;
					i=j-1;	
					break;
				}
			}
		}
		return c_span;
	}

	public final int countgroup(int col[],int start)
	{
		int rows=1;
		for(int i=start;i<cache_data.size();i++	)
		{
			if (rowEquals(i,i+1,col))
				rows ++;
			else
				break;
		}
		return rows;
	}

	public final int countgroup(int col,int start)
	{
		int rows=1;
		int cols[] = new int[1];
		cols[0] = col;

		for(int i=start;i<cache_data.size();i++	)
		{
			if (rowEquals(i,i+1,cols))
				rows ++;
			else
				break;
		}
		return rows;
	}

	public boolean rowEquals(int row1,int row2,int cols[])
	{
		boolean b_equals=true;
		if (row1 == row2) return true;
		if (cols==null || cols.length==0) return true;
		if (row1 < 1 || row1 > cache_data.size() ||
			row2 < 1 || row2 > cache_data.size()) return false;

		Object row_data1[] = (Object [])(cache_data.elementAt(row1 - 1));
		Object row_data2[] = (Object [])(cache_data.elementAt(row2 - 1));

		for(int i = 0;i<cols.length;i++)
		{
			if (cols[i] < 1 || cols[i] > column_count) continue;
			Object val1 = row_data1[cols[i]-1];
			Object val2 = row_data2[cols[i]-1];
			if (val1 == null)
			{
				if (val2 != null) b_equals=false;
			}
			else
			{
				b_equals = val1.equals(val2);
			}
			if (!b_equals) break;
		}
		return b_equals;
	}

	public boolean rowEquals(int row1,int row2,int col)
	{
		boolean b_equals=true;
		if (col < 0 || col > column_count) return true;
		if (row1 == row2) return true;
		if (row1 < 1 || row1 > cache_data.size() ||
			row2 < 1 || row2 > cache_data.size()) return false;

		Object row_data1[] = (Object [])(cache_data.elementAt(row1 - 1));
		Object row_data2[] = (Object [])(cache_data.elementAt(row2 - 1));

		for(int i = 0;i<col;i++)
		{
			Object val1 = row_data1[i];
			Object val2 = row_data2[i];
			if (val1 == null)
			{
				if (val2 != null) b_equals=false;
			}
			else
			{
				b_equals = val1.equals(val2);
			}
			if (!b_equals) break;
		}
		return b_equals;
	}

	public boolean rowEquals(int row1,int row2,int cols[],int col)
	{
		boolean b_equals=true;
		if (col<0) return true;
		if (row1 == row2) return true;
		if (cols==null || cols.length==0) return true;
		if (row1 < 1 || row1 > cache_data.size() ||
			row2 < 1 || row2 > cache_data.size()) return false;

		Object row_data1[] = (Object [])(cache_data.elementAt(row1 - 1));
		Object row_data2[] = (Object [])(cache_data.elementAt(row2 - 1));

		for(int i = 0;i<cols.length && i <col;i++)
		{
			if (cols[i] < 1 || cols[i] > column_count) continue;
			Object val1 = row_data1[cols[i]-1];
			Object val2 = row_data2[cols[i]-1];
			if (val1 == null)
			{
				if (val2 != null) b_equals=false;
			}
			else
			{
				b_equals = val1.equals(val2);
			}
			if (!b_equals) break;
		}
		return b_equals;
	}


	public final Object[] distinct(int col)
	{
		int row;
		Object val;
		if (col < 1 && col > column_count) return new Object[] {};
		java.util.TreeSet set = new java.util.TreeSet();
		for(row = 1; row <= getRowCount(); row ++)
		{
			val = getItem(row,col);
			if (val != null)
				set.add(val);
		}
		return set.toArray();
	}

	public final Object[] distinct(String col)
	{
		return distinct(findColumn(col));
	}

	private final int find(int col,Object val,int start,int rowlist[])
	{
		int i;

		if (col<1 || col > column_count) return 0;
		if (rowlist == null || rowlist.length==0) return 0;

		for(i=start;i<rowlist.length;i++)
		{
			if (val == null)
			{
				if (getItem(rowlist[i],col) == null)
					return i+1;
			}
			else
			{
				if (val.equals(getItem(rowlist[i],col)))
					return i+1;
			}
		}
		return 0;
	}

	private final int count(int col,Object val,int rowlist[])
	{
		int i=0,count=0;
		while(true)
		{
			i=find(col,val,i,rowlist);
			if (i==0) break;
			count ++;
		}
		return count;		
	}

	private final int[] filter(int col,Object val,int rowlist[])
	{
		int rows[]={},i,j;
		if ((i=count(col,val,rowlist)) == 0) return rows;
		rows = new int[i];
		i=0;
		j=0;
		while((i=find(col,val,i,rowlist))>0)
		{
			rows[j]=rowlist[i-1];
			j++;
		}
		return rows;		
	}

	public void joinData(DBRowCache data2, String joincols)
	{
		String _joincols[] = TextUtils.toStringArray(TextUtils.getWords(joincols,","));
		joinData(data2, _joincols);
	}

	/*
	public void joinData(DBRowCache data2, String joincols[])
	{
		int pos1=0, pos2=0;
		if (data2 == null && data2.getColumnCount() <= joincols.length) return;

		java.util.Vector<String> newcols= new java.util.Vector<String>();

		if (joincols.length > 0)
		{
		    for(int i=0; i< joincols.length; i++)
		    {
			pos1 = findColumn(joincols[i]);
			pos2 = data2.findColumn(joincols[i]);
			if (pos1 == 0 || pos2 == 0 || getColumnType(pos1) != data2.getColumnType(pos2))
			{
			    return;
			}
		    }
		    for(int i=1; i<= data2.getColumnCount(); i++)
		    {
			pos1 = findColumn(data2.getColumnName(i));
			if (pos1 == 0)
			{
			    addColumn(data2.getColumnName(i), data2.getColumnType(i));
			    newcols.addElement(data2.getColumnName(i));
			}
		    }
		    if (newcols.size() > 0)
		    {
			int filtercols[] = new int[joincols.length];
			Object filtervals[] = new Object[joincols.length];
			for(int i=0; i< joincols.length; i++)
			{
			    filtercols[i] = findColumn(joincols[i]);
			}
			for(int row=1;row<=data2.getRowCount();row++)
			{
			    for(int i=0; i< joincols.length; i++)
			    {
			        filtervals[i] = data2.getItem(row,joincols[i]);
			    }
			    int matchrows[] = filter(filtercols, filtervals);
			    if (matchrows.length > 0)
			    {
				for(int j=0; j< matchrows.length; j++)
			        {
				     for(int i=0; i< newcols.size(); i++)
			             {
				          setItem(matchrows[j], newcols.elementAt(i), data2.getItem(row, newcols.elementAt(i)));
				     }
				}
			    }
			}
		    }
		}
	}
	*/

	public void joinData(DBRowCache data2, String joincols[])
	{
		int pos1=0, pos2=0;
		int cols1[], cols2[];
		Object vals[];

		if (data2 == null && data2.getColumnCount() <= joincols.length) return;

		java.util.Vector<String> newcols= new java.util.Vector<String>();

		if (joincols.length > 0)
		{
		    cols1 = new int[joincols.length];
		    cols2 = new int[joincols.length];
		    vals  = new Object[joincols.length];

		    for(int i=0; i< joincols.length; i++)
		    {
			pos1 = findColumn(joincols[i]);
			pos2 = data2.findColumn(joincols[i]);
			cols1[i] = pos1;
			cols2[i] = pos2;
			if (pos1 == 0 || pos2 == 0 || getColumnType(pos1) != data2.getColumnType(pos2))
			{
			    return;
			}
		    }
		    for(int i=1; i<= data2.getColumnCount(); i++)
		    {
			pos1 = findColumn(data2.getColumnName(i));
			if (pos1 == 0)
			{
			    addColumn(data2.getColumnName(i), data2.getColumnType(i));
			    newcols.addElement(data2.getColumnName(i));
			}
		    }
		    if (data2.getRowCount() > 0 && newcols.size() > 0)
		    {
			java.util.HashMap<RowValuesObject, Integer> hashmap = new java.util.HashMap<RowValuesObject, Integer>();
			for(int row=1;row<=data2.getRowCount();row++)
			{
			     for(int i=0; i<cols2.length; i++)
				 vals[i] = data2.getItem(row, cols2[i]);
			     hashmap.put(new RowValuesObject(cols2,vals), Integer.valueOf(row));
			}
			
			for(int row=1;row<=getRowCount();row++)
			{
			    for(int i=0; i<cols1.length; i++)
				 vals[i] = getItem(row, cols1[i]);
			    RowValuesObject hashkey = new RowValuesObject(cols2, vals);
			    if (hashmap.containsKey(hashkey))
			    {
			        int matchrow = hashmap.get(hashkey).intValue();
			        for(int i=0; i< newcols.size(); i++)
			        {
				     setItem(matchrow, newcols.elementAt(i), data2.getItem(matchrow, newcols.elementAt(i)));
				}
			    }
			}
		    }
		}
	}

	public DBRowCache groupData(String gcols[], String vcols[])
	{
		DBRowCache data = new SimpleDBRowCache();

		if (gcols == null || gcols.length == 0) return data;
		if (vcols == null || vcols.length == 0) return data;

		int collist[] = new int[gcols.length];
		int collist2[] = new int[gcols.length];
		Object vallist[] = new Object[gcols.length];
		int pos;

		for(int i=0;i<gcols.length;i++)
		{
			if ((collist[i] = findColumn(gcols[i])) == 0) return data;
		}

		for(int i=0;i<gcols.length;i++)
		{
			data.addColumn(gcols[i], getColumnType(gcols[i]));
			collist2[i] = (i+1);
		}

		for(int i=0;i<vcols.length;i++)
		{
			data.addColumn(vcols[i], java.sql.Types.DOUBLE);
		}

		for(int row=1;row<=getRowCount();row++)
		{
			for(int i=0;i<gcols.length;i++)
			{
				vallist[i] = getItem(row, collist[i]);
			}
			if (data.find(collist2, vallist) == 0)
			{
				pos = data.appendRow();
				for(int i=0;i<gcols.length;i++)
				{
					data.setItem(pos, i+1, vallist[i]);
				}
				for(int i=0;i<vcols.length;i++)
				{
					data.setItem(pos, gcols.length + i + 1, Double.valueOf(getItemExpr(row, vcols[i], collist)));
				}								
			}
		}
		return data;
	}

	public final void addCrossTab(DBRowCache data,int r_col,int c_col[],int v_col[])
	{
		if (c_col.length == v_col.length)
		{
			for(int i=0;i<c_col.length;i++)
			{
				addCrossTab(data,r_col,c_col[i],v_col[i]);
			}
		}
	}
	public final void addCrossTab(DBRowCache data,String r_col,String c_col[],String v_col[])
	{
		if (c_col.length == v_col.length)
		{
			for(int i=0;i<c_col.length;i++)
			{
				addCrossTab(data,r_col,c_col[i],v_col[i]);
			}
		}
	}

	public final void addCrossTab(DBRowCache data,String r_col,String c_col,String v_col)
	{
		addCrossTab(data,data.findColumn(r_col),data.findColumn(c_col),data.findColumn(v_col));
	}

	public final void addCrossTab(DBRowCache data,int r_col,int c_col,int v_col)
	{
		int row,tab_row=0,tab_col=0,val_type;

		Object val_row,val_col;
		Object prev_val_row=null,prev_val_col=null;

		if (r_col < 1 || r_col > data.getColumnCount() ||
			c_col < 1 || c_col > data.getColumnCount() ||
			v_col < 1 || v_col > data.getColumnCount() )
			return;

		if ((row=findColumn(data.getColumnName(r_col)))==0)
		{
			addColumn(data.getColumnName(r_col),
				data.getColumnType(r_col));
		}
		else
		{
			setColumnType(row,data.getColumnType(r_col));
		}

		val_type = data.getColumnType(v_col);

		Object dist_cols[] = data.distinct(c_col);
		for(row=0;row<dist_cols.length;row++)	
		{
			if (findColumn(dist_cols[row].toString())==0)
				addColumn(dist_cols[row].toString(),val_type);			
		}

		for(row = 1;row <= data.getRowCount();row ++)
		{
			if ((val_row=data.getItem(row,r_col)) == null ||
				(val_col = data.getItem(row,c_col)) == null)
				continue;
			if (!val_col.equals(prev_val_col))
			{
				if ((tab_col = findColumn(val_col.toString()))==0)
				{
					addColumn(val_col.toString(),val_type);
					tab_col = column_count;
				}
				prev_val_col = val_col;
			}
			if (!val_row.equals(prev_val_row))
			{
				if ((tab_row = find(1,val_row)) == 0)
				{
					tab_row = appendRow();
					setItem(tab_row,1,val_row);
				}
				prev_val_row = val_row;
			}
			setItem(tab_row,tab_col,data.getItem(row,v_col));
		}
	}

	public final void addCrossTab(DBRowCache data,int r_col[],int c_col[],int v_col[])
	{
		if (c_col.length == v_col.length)
		{
			for(int i=0;i<c_col.length;i++)
			{
				addCrossTab(data,r_col,c_col[i],v_col[i]);
			}
		}
	}
	public final void addCrossTab(DBRowCache data,String r_col[],String c_col[],String v_col[])
	{
		if (c_col.length == v_col.length)
		{
			for(int i=0;i<c_col.length;i++)
			{
				addCrossTab(data,r_col,c_col[i],v_col[i]);
			}
		}
	}

	public final void addCrossTab(DBRowCache data,String r_col[],String c_col,String v_col)
	{
		if (r_col == null || r_col.length == 0) return;
		int i_r_col[] = new int[r_col.length];
		for(int i=0;i<r_col.length;i++)
			i_r_col[i] = data.findColumn(r_col[i]);
		addCrossTab(data,i_r_col,data.findColumn(c_col),data.findColumn(v_col));
	}

	public final void addCrossTab(DBRowCache data,int r_col[],int c_col,int v_col)
	{
		int row,tab_row=0,tab_col=0,val_type;
		int cross_rows[];
		Object val_row[],val_col;
		Object prev_val_row=null,prev_val_col=null;
		
		if (r_col == null || r_col.length == 0) return;		

		for(int i = 0;i<r_col.length;i++)
		{
			if (r_col[i] < 1 || r_col[i] > data.getColumnCount()) return;
		}

		if (c_col < 1 || c_col > data.getColumnCount() ||
			v_col < 1 || v_col > data.getColumnCount() )
			return;

		cross_rows = new int[r_col.length];

		for(int i = 0;i<r_col.length;i++)
		{
			if ((cross_rows[i]=findColumn(data.getColumnName(r_col[i])))==0)
			{
				addColumn(data.getColumnName(r_col[i]),
					data.getColumnType(r_col[i]));
				cross_rows[i] = getColumnCount();
			}
			else
			{
				//cross_rows[i] = findColumn(data.getColumnName(r_col[i]));
				setColumnType(cross_rows[i],data.getColumnType(r_col[i]));
			}
		}

		val_type = data.getColumnType(v_col);

		Object dist_cols[] = data.distinct(c_col);
		for(row=0;row<dist_cols.length;row++)	
		{
			if (findColumn(dist_cols[row].toString())==0)
				addColumn(dist_cols[row].toString(),val_type);			
		}

		val_row = new Object[r_col.length];
		
		for(row = 1;row <= data.getRowCount();row ++)
		{
			if ((val_col = data.getItem(row,c_col)) == null)
				continue;
			if (!val_col.equals(prev_val_col))
			{
				if ((tab_col = findColumn(val_col.toString()))==0)
				{
					addColumn(val_col.toString(),val_type);
					tab_col = column_count;
				}
				prev_val_col = val_col;
			}
			for(int i=0;i<r_col.length;i++)
			{
				val_row[i] = data.getItem(row,r_col[i]);
			}

			tab_row = find(cross_rows,val_row);

			if (tab_row == 0)
			{
				tab_row = appendRow();
				for(int i=0;i<r_col.length;i++)
				{
					setItem(tab_row,cross_rows[i],data.getItem(row,r_col[i]));
				}
			}
			setItem(tab_row,tab_col,data.getItem(row,v_col));
		}
	}


	public final DBRowCache getCrossTab(int r_col,int c_col,int v_col)
	{
		DBRowCache crosstab = DBOperation.getDBRowCache();
		crosstab.addCrossTab(this,r_col,c_col,v_col);
		return crosstab;
	}
	public final DBRowCache getCrossTab(int r_col,int c_col[],int v_col[])
	{
		DBRowCache crosstab = DBOperation.getDBRowCache();
		crosstab.addCrossTab(this,r_col,c_col,v_col);
		return crosstab;
	}
	public final DBRowCache getCrossTab(String r_col,String c_col,String v_col)
	{
		DBRowCache crosstab = DBOperation.getDBRowCache();
		crosstab.addCrossTab(this,r_col,c_col,v_col);
		return crosstab;
	}
	public final DBRowCache getCrossTab(String r_col,String c_col[],String v_col[])
	{
		DBRowCache crosstab = DBOperation.getDBRowCache();
		crosstab.addCrossTab(this,r_col,c_col,v_col);
		return crosstab;
	}

	public final DBRowCache getCrossTab(int r_col[],int c_col,int v_col)
	{
		DBRowCache crosstab = DBOperation.getDBRowCache();
		crosstab.addCrossTab(this,r_col,c_col,v_col);
		return crosstab;
	}
	public final DBRowCache getCrossTab(int r_col[],int c_col[],int v_col[])
	{
		DBRowCache crosstab = DBOperation.getDBRowCache();
		crosstab.addCrossTab(this,r_col,c_col,v_col);
		return crosstab;
	}
	public final DBRowCache getCrossTab(String r_col[],String c_col,String v_col)
	{
		DBRowCache crosstab = DBOperation.getDBRowCache();
		crosstab.addCrossTab(this,r_col,c_col,v_col);
		return crosstab;
	}
	public final DBRowCache getCrossTab(String r_col[],String c_col[],String v_col[])
	{
		DBRowCache crosstab = DBOperation.getDBRowCache();
		crosstab.addCrossTab(this,r_col,c_col,v_col);
		return crosstab;
	}

	private final double doubleValue(Object item, double ddef)
   	{
		if (item == null)
			return ddef;
		try {
			return Double.valueOf(item.toString()).doubleValue();
		}
		catch (NumberFormatException nfe) {}
		return 1.0;
   	}

        boolean isrowlevelfunction(String colname)
        {
		return colname.startsWith("row::") || colname.startsWith("rnk::") || colname.startsWith("mov::") 
                       || colname.startsWith("pre::") || colname.startsWith("nxt::") || colname.startsWith("cum::")
                       || colname.startsWith("shr::") || colname.startsWith("shl::")  || colname.startsWith("del::");
	}

        public void  addGroovyExpression(String col, String expr)
        {
            /*
            Object val=null;
	    if (expr != null)
            {
                DBGroovyScript dbgv = new DBGroovyScript();
                addColumn(col, java.sql.Types.VARCHAR);
                for(int row=1; row <= getRowCount(); row ++)
                {
                    val = dbgv.getValue(this, row, expr);
                    if (row == 1)
                    {
                        if (val != null)
                        {
                            if (val.getClass() != java.lang.String.class)
                                setColumnType(col, java.sql.Types.DOUBLE);
                        }
                    }
                    setItem(row, col, val);
                }
            }
            */
        }

	public final double getExprValue(int row,String col)
	{
		double expval = 0;
		boolean rowval = isrowlevelfunction(col);
	        if (findColumn(col) <= 0) expval = getItemExpr(col);		
		return doubleValue(getItem(row, col),rowval?getItemExpr(row, col):expval);
	}
	public final double getExprValue(int row,String col, int grpcols[])
	{
		return doubleValue(getItem(row, col),getItemExpr(row, col, grpcols));
	}

	public final void  addExpression(String col, String expr)
	{
	    if (expr != null)
	    {
  	          DBRowCacheExpression expression = new DBRowCacheExpression(expr);
		  String tmpcol[] = TextUtils.toStringArray(TextUtils.getWords(col));
		  if (tmpcol.length == 1)
		       	  addColumn(col, java.sql.Types.DOUBLE);
		  else
			  addColumn(tmpcol[0],SQLTypes.getTypeID(tmpcol[1]));
	       	  for(int row=1;row <= getRowCount(); row++)
	    	  {
	    	      	      java.math.BigDecimal val = new java.math.BigDecimal(expression.value(this,row));
	    	      	      if (val.scale() > 4)
	    	      	         setItem(row, tmpcol[0], SQLTypes.getValue(getColumnType(tmpcol[0]),val.setScale(4,java.math.BigDecimal.ROUND_HALF_UP)));
	    	      	      else
	    	      	         setItem(row, tmpcol[0], SQLTypes.getValue(getColumnType(tmpcol[0]),val));
	    	  }
	    }
	}

	public void  addExpression(String col, String expr, String grpcols[])
	{
	    if (grpcols == null || grpcols.length == 0)
	    {
		addExpression(col, expr);
		return;
	    }
	    filter_cache.clear();
	    int colarr[] = new int[grpcols.length];
	    for(int i=0;i<grpcols.length;i++)
	    {
		colarr[i] = findColumn(grpcols[i]);
	    }
  
	    if (expr != null)
	    {
	        DBRowCacheExpression expression = new DBRowCacheExpression(expr);
		String tmpcol[] = TextUtils.toStringArray(TextUtils.getWords(col));
		if (tmpcol.length == 1)
		       	  addColumn(tmpcol[0], java.sql.Types.DOUBLE);
		else
			  addColumn(tmpcol[0],SQLTypes.getTypeID(tmpcol[1]));
	        for(int row=1;row <= getRowCount(); row++)
	        {
	    	      	  java.math.BigDecimal val = new java.math.BigDecimal(expression.value(this,row,colarr));
    		      	  if (val.scale() > 4)
	    	      	     setItem(row, tmpcol[0], SQLTypes.getValue(getColumnType(tmpcol[0]),val.setScale(4,java.math.BigDecimal.ROUND_HALF_UP)));
	    	      	  else
    		      	     setItem(row, tmpcol[0], SQLTypes.getValue(getColumnType(tmpcol[0]),val));
	        }
	    }
	}

	public void  expressFilter(String expr)
	{
	    if (expr != null)
	    {
  	          DBRowCacheExpression expression = new DBRowCacheExpression(expr);
	       	  for(int row=getRowCount();row >0; row--)
	    	  {
	    	      double val = expression.value(this,row);
	    	      if (val < 0f) deleteRow(row);
	    	  }
	    }
	}

	private double getItemExpr(String col)
	{
		String realcol = col;
		if (realcol.length() > 5) realcol = col.substring(5);
		if (findColumn(realcol) == 0)
                    realcol = getColumnName(Integer.valueOf(realcol).intValue());

		if (col.startsWith("min::"))
			return Double.valueOf(min(realcol));
		else if (col.startsWith("max::"))
			return Double.valueOf(max(realcol));
		else if (col.startsWith("avg::"))
			return Double.valueOf(avg(realcol));
		else if (col.startsWith("sum::"))
			return Double.valueOf(sum(realcol));
		else if (col.startsWith("cnt::"))
			return Double.valueOf(count(realcol));
		return 0;
	}

	private double getItemExpr(int currow, String col)
	{
		String realcol = col;
		if (realcol.length() > 5) realcol = col.substring(5);
		if (findColumn(realcol) == 0)
                    realcol = getColumnName(Integer.valueOf(realcol).intValue());

		if (col.startsWith("min::"))
			return Double.valueOf(min(realcol));
		else if (col.startsWith("max::"))
			return Double.valueOf(max(realcol));
		else if (col.startsWith("avg::"))
			return Double.valueOf(avg(realcol));
		else if (col.startsWith("sum::"))
			return Double.valueOf(sum(realcol));
		else if (col.startsWith("cnt::"))
			return Double.valueOf(count(realcol));
		else if (col.startsWith("row::"))
			return Double.valueOf(rownumber(currow, realcol));
		else if (col.startsWith("rnk::"))
			return Double.valueOf(ranknumber(currow, realcol));
		else if (col.startsWith("mov::"))
			return Double.valueOf(moveaverage(currow,realcol));
		else if (col.startsWith("pre::"))
			return Double.valueOf(prevnumber(currow,realcol));
		else if (col.startsWith("nxt::"))
			return Double.valueOf(nextnumber(currow,realcol));
		else if (col.startsWith("cum::"))
			return Double.valueOf(cum(currow,realcol));
		else if (col.startsWith("shl::"))
			return Double.valueOf(shiftleft(currow,realcol));
		else if (col.startsWith("shr::"))
			return Double.valueOf(shiftright(currow,realcol));
		else if (col.startsWith("del::"))
			return Double.valueOf(deltanumber(currow,realcol));
		return 0;
	}

	private double getItemExpr(int currow, String col, int grpcol[])
	{
		String realcol = col;
		if (realcol.length() > 5) realcol = col.substring(5);
		if (findColumn(realcol) == 0)
                    realcol = getColumnName(Integer.valueOf(realcol).intValue());

		if (col.startsWith("min::"))
			return Double.valueOf(min(currow, realcol, grpcol));
		else if (col.startsWith("max::"))
			return Double.valueOf(max(currow, realcol, grpcol));
		else if (col.startsWith("avg::"))
			return Double.valueOf(avg(currow, realcol, grpcol));
		else if (col.startsWith("sum::"))
			return Double.valueOf(sum(currow, realcol, grpcol));
		else if (col.startsWith("cnt::"))
			return Double.valueOf(count(currow, realcol, grpcol));
		else if (col.startsWith("row::"))
			return Double.valueOf(rownumber(currow, realcol, grpcol));
		else if (col.startsWith("rnk::"))
			return Double.valueOf(ranknumber(currow, realcol, grpcol));
		else if (col.startsWith("mov::"))
			return Double.valueOf(moveaverage(currow, realcol, grpcol));
		else if (col.startsWith("pre::"))
			return Double.valueOf(prevnumber(currow, realcol, grpcol));
		else if (col.startsWith("nxt::"))
			return Double.valueOf(nextnumber(currow, realcol, grpcol));
		else if (col.startsWith("cum::"))
			return Double.valueOf(cum(currow, realcol, grpcol));
		else if (col.startsWith("shl::"))
			return Double.valueOf(shiftleft(currow, realcol, grpcol));
		else if (col.startsWith("shr::"))
			return Double.valueOf(shiftright(currow, realcol, grpcol));
		else if (col.startsWith("del::"))
			return Double.valueOf(deltanumber(currow,realcol, grpcol));
		return 0;
	}

	private boolean isExprItem(String col)
	{
		String realcol = col;
		if (findColumn(realcol) > 0) return false;
		if (realcol.length() > 5)
		{
			realcol = col.substring(5);
			if (col.charAt(3) == ':'  && col.charAt(4) == ':')
			{
			     if (findColumn(realcol) > 0) return true;
			     try {
                                 realcol = getColumnName(Integer.valueOf(realcol).intValue());
			         if (realcol != null) return true;
			     } catch (java.lang.NumberFormatException nfe) {}
			}
		}
		return false;
	}

	public void quicksort(int col)
	{
		quicksort(col,true);
  	}
	public void quicksort(String col)
	{
		quicksort(findColumn(col),true);
  	}  
	public void quicksort(String col,boolean asc)
	{
		quicksort(findColumn(col),asc);
  	}
	public void quicksort(int col,boolean asc)
	{
		if (col > 0 && col <= column_count)
		{
			 sort_columns = new int[1];
		  	 sort_columns[0] = col;
			 sort_asc    = asc;
      			 Object rows[] =  cache_data.toArray();
		   	 sun.misc.Sort.quicksort(rows,this);
			 deleteAllRow();
			 for(int row=0;row<rows.length;row++)
			 {
			     appendRow((Object [])(rows[row]));
			 }
   		 }
	}

	public void quicksort(int col[])
	{
		quicksort(col,true);
  	}
	public void quicksort(String col[])
	{
		quicksort(col,true);
  	}  
	public void quicksort(String col[],boolean asc)
	{
		int scols=0;
		for(int i=0; i< col.length; i++)
		{
		    if (findColumn(col[i])>0) scols++;
		}	
		if (scols > 0)
		{
			 sort_columns = new int[scols];
			 scols = 0;
			 for(int i=0; i< col.length; i++)
			 {
		    		if (findColumn(col[i])>0)
		    		{
		    		   sort_columns[scols] = findColumn(col[i]);
		    		   scols++;
		    		}
			 }			 
			 sort_asc    = asc;
      			 Object rows[] =  cache_data.toArray();
		   	 sun.misc.Sort.quicksort(rows,this);
			 deleteAllRow();
			 for(int row=0;row<rows.length;row++)
			 {
			     appendRow((Object [])(rows[row]));
			 }
   		 }
    	}
	public void quicksort(int col[],boolean asc)
	{
		int scols=0;
		for(int i=0; i< col.length; i++)
		{
		    if (col[i]>0 && col[i] < column_count) scols++;
		}	
		if (scols > 0)
		{
			 sort_columns = new int[scols];
			 scols = 0;
			 for(int i=0; i< col.length; i++)
			 {
		    		if (col[i]>0 && col[i] < column_count)
		    		{
		    		   sort_columns[scols] = col[i];
		    		   scols++;
		    		}
			 }			 
			 sort_asc    = asc;
      			 Object rows[] =  cache_data.toArray();
		   	 sun.misc.Sort.quicksort(rows,this);
			 deleteAllRow();
			 for(int row=0;row<rows.length;row++)
			 {
			     appendRow((Object [])(rows[row]));
			 }
   		 }
	}
	
  	public int doCompare(java.lang.Object row1,java.lang.Object row2)
	{
		 int rtn=0;
		 Object colval1 = null;
    		 Object colval2 = null;
    		 for (int i=0; i< sort_columns.length;i++)
    		 {
    		 	colval1 = ((Object [])row1)[sort_columns[i] - 1];
    		 	colval2 = ((Object [])row2)[sort_columns[i] - 1];	
			if (colval1 != null && colval2 != null)
		 	{
				rtn= ((java.lang.Comparable)colval1).compareTo((java.lang.Comparable)colval2);
				if (rtn != 0) break;
		 	}
		 	else
		 	{
       				if (colval1 != null) rtn=-1;
		   		if (colval2 != null) rtn= 1;
		 	}
		 }
		 return (sort_asc?rtn:0-rtn);
	}  

        public final String parseString(String sfile,VariableTable vt, int row, int level)
        {
               return parseString(sfile, '$', vt, row, level);
        }

	public final String parseString(String sfile, char sep, VariableTable vt, int row, int level)
	{
	    StringBuffer result_buf = new StringBuffer();
	    StringBuffer temp_buf   = new StringBuffer();
	    Object val;

	    if (sfile != null)
	    {
	      for(int i=0;i<sfile.length();)
	      {
        	if (sfile.charAt(i) == '\\')
	        {
        	  i++;
	          if (i<sfile.length())
        	  {
                    if (sfile.charAt(i) != 'n')
  	               result_buf.append(sfile.charAt(i));
                    else
  	               result_buf.append('\n');
        	    i++;
          	  }
        	}
	        else if (sfile.charAt(i) == sep)
        	{
	          i++;
	          if (i<sfile.length())
        	  {
	            if (sfile.charAt(i) == '{')
        	    {
	              i++;
        	      temp_buf.delete(0, temp_buf.length());
	              while(i<sfile.length() && sfile.charAt(i) != '}')
        	      {
	                temp_buf.append(sfile.charAt(i));
        	        i++;
	              }
	              if (i<sfile.length()) i++;
		      val = null;
		      if (vt.exists(temp_buf.toString()))
		      {
			  val = vt.getValue(temp_buf.toString());                  
		      }
		      else if (findColumn(temp_buf.toString()) > 0)
		      {
			  val = getItem(row, temp_buf.toString());
		      }
		      else if (isExprItem(temp_buf.toString()))
		      {
			  val = Double.valueOf(getItemExpr(row, temp_buf.toString()));
		      }
		      if (val != null)
		      {
			  if (level==2)
				  result_buf.append(EncodeHTML(val.toString()));
			  else if (level == 1)
				  result_buf.append(EncodeXML(URLEncoder.encode(val.toString())));
			  else
				  result_buf.append(val.toString());
		      }
        	    }
	            else if (Character.isLetter(sfile.charAt(i)) || sfile.charAt(i) == '_')
        	    {
	            	temp_buf.delete(0, temp_buf.length());
        	        temp_buf.append(sfile.charAt(i));
	                i++;
	                while(i<sfile.length() && (Character.isLetter(sfile.charAt(i)) || Character.isDigit(sfile.charAt(i)) || sfile.charAt(i) == '_'))
        	        {
	                   temp_buf.append(sfile.charAt(i));
	                   i++;
	                }
	  	        val = null;
   		        if (vt.exists(temp_buf.toString()))
  	   		{
	  		    val = vt.getValue(temp_buf.toString());                  
	      		}
	  	        else if (findColumn(temp_buf.toString()) > 0)
		        {
	  		    val = getItem(row, temp_buf.toString());
	  	        }
		        else if (isExprItem(temp_buf.toString()))
		        {
			    val = Double.valueOf(getItemExpr(row, temp_buf.toString()));
		        }
	 	        if (val != null)
	      		{
			    if (level==2)
			        result_buf.append(EncodeHTML(val.toString()));
	 		    else if (level == 1)
			  	result_buf.append(EncodeXML(URLEncoder.encode(val.toString())));
			    else
			  	result_buf.append(val.toString());
	      		}
			if (i< sfile.length() && sfile.charAt(i) == '.') i++;
            	    }
	            else
        	    {
			i++;
            	    }
          	  }
	        }
	        else if (sfile.charAt(i) == '@')
        	{
	          i++;
	          if (i<sfile.length())
        	  {
	            if (sfile.charAt(i) == '{')
        	    {
	              i++;
        	      temp_buf.delete(0, temp_buf.length());
	              while(i<sfile.length() && sfile.charAt(i) != '}')
        	      {
	                temp_buf.append(sfile.charAt(i));
        	        i++;
	              }
	              if (i<sfile.length()) i++;
		      val = null;
		      if (vt.exists(temp_buf.toString()))
		      {
			  val = vt.getValue(temp_buf.toString());                  
		      }
		      else if (findColumn(temp_buf.toString()) > 0)
		      {
			  val = getItem(row, temp_buf.toString());
		      }
		      else if (isExprItem(temp_buf.toString()))
		      {
			  val = Double.valueOf(getItemExpr(row, temp_buf.toString()));
		      }
		      if (val != null)
		      {
			  result_buf.append(val.toString());
		      }
        	    }
	            else if (Character.isLetter(sfile.charAt(i)) || sfile.charAt(i) == '_')
        	    {
	            	temp_buf.delete(0, temp_buf.length());
        	        temp_buf.append(sfile.charAt(i));
	                i++;
	                while(i<sfile.length() && (Character.isLetter(sfile.charAt(i)) || Character.isDigit(sfile.charAt(i)) || sfile.charAt(i) == '_'))
        	        {
	                   temp_buf.append(sfile.charAt(i));
	                   i++;
	                }
	  	        val = null;
   		        if (vt.exists(temp_buf.toString()))
  	   		{
	  		    val = vt.getValue(temp_buf.toString());                  
	      		}
	  	        else if (findColumn(temp_buf.toString()) > 0)
		        {
	  		    val = getItem(row, temp_buf.toString());
	  	        }
		        else if (isExprItem(temp_buf.toString()))
		        {
			    val = Double.valueOf(getItemExpr(row, temp_buf.toString()));
		        }
	 	        if (val != null)
	      		{
			    result_buf.append(val.toString());
	      		}
			if (i< sfile.length() && sfile.charAt(i) == '.') i++;
            	   }
	           else
        	   {
			i++;
            	   }
          	}
	      }
	      else
	      {
		 if (sfile.charAt(i) > 0)
	        	 result_buf.append(sfile.charAt(i));
        	 i++;
	      }
      	    }
    	  }
   	  return result_buf.toString();
	}

	public final String EncodeXML(String from)
        {
	     if (from == null) return null;
	     char fromchar[] = from.toCharArray();
	     StringBuffer tobuf = new StringBuffer();
	     for(int i=0;i<fromchar.length;i++)
	     {
		  switch(fromchar[i])
		  {
			case '&':
				tobuf.append("&amp;");
				break;
			case '>':
				tobuf.append("&gt;");
				break;
			case '<':
				tobuf.append("&lt;");
				break;
			case '\"':
				tobuf.append("&quot;");
				break;
			default:
				if (fromchar[i] >= 0x20 || fromchar[i] == 0x9 ||
				    fromchar[i] == 0xa  || fromchar[i] == 0xd )
				    tobuf.append(fromchar[i]);
				break;
		  }
	     }
	     return tobuf.toString();
        }

	public final String EncodeHTML(String from)
        {
	     if (from == null) return null;
	     char fromchar[] = from.toCharArray();
	     StringBuffer tobuf = new StringBuffer();
	     for(int i=0;i<fromchar.length;i++)
	     {
		  switch(fromchar[i])
		  {
			case '&':
				tobuf.append("&amp;");
				break;
			case '>':
				tobuf.append("&gt;");
				break;
			case '<':
				tobuf.append("&lt;");
				break;
			case '\"':
				tobuf.append("&quot;");
				break;
			case '\r':
				break;
			case '\n':
				tobuf.append("<br />\n");
				break;
			default:
				if (fromchar[i] >= 0x20 || fromchar[i] == 0x9 ||
				    fromchar[i] == 0xa  || fromchar[i] == 0xd )
				    tobuf.append(fromchar[i]);
				break;
		  }
	     }
	     return tobuf.toString();
        }

	private String toJasonString(String from)
	{
	     if (from == null) return null;
	     char fromchar[] = from.toCharArray();
	     StringBuffer tobuf = new StringBuffer();
	     for(int i=0;i<fromchar.length;i++)
	     {
		  switch(fromchar[i])
		  {
			case '\\':
				tobuf.append("\\\\");
				break;
			case '\"':
				tobuf.append("\\\"");
				break;
			case '\r':
				tobuf.append("\\r");
				break;
			case '\n':
				tobuf.append("\\n");
				break;
			case '\b':
				tobuf.append("\\b");
				break;
			case '\f':
				tobuf.append("\\f");
				break;
			case '\t':
				tobuf.append("\\t");
				break;
			case '/':
				tobuf.append("\\/");
				break;
			default:
                                if(fromchar[i]>='\u0000' && fromchar[i]<='\u001F')
				{ 
                                        String ss=Integer.toHexString(fromchar[i]); 
                                        tobuf.append("\\u"); 
                                        for(int k=0;k<4-ss.length();k++)
					{ 
                                                tobuf.append('0'); 
                                        } 
                                        tobuf.append(ss.toUpperCase()); 
                                } 
                                else{ 
                                        tobuf.append(fromchar[i]); 
                                } 
				break;
		  }
	     }
	     return tobuf.toString();
	}

	public final void  writeJasonFormat(java.io.Writer ps) throws java.io.IOException
	{
	     Object val=null;
	     StringBuffer tobuf = new StringBuffer();
	     tobuf.append("{\n");
	     tobuf.append("  \"metaData\":\n");
	     if (column_count > 0)
	     {
		tobuf.append("  {\n");
		tobuf.append("      \"root\": \"rows\",\n");
		tobuf.append("      \"fields\": [\n");
	        for(int i=0; i< column_count; i++)
	        {
		    if (i>0) tobuf.append(",\n");
		    switch(getColumnType(i+1))
		    {
			case java.sql.Types.TINYINT:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.BIGINT:
		            tobuf.append("        {\"name\": \"");
		            tobuf.append(toJasonString(getColumnName(i+1)));
		            tobuf.append("\", \"type\": \"int\"}");
                            break;
			case java.sql.Types.FLOAT:
			case java.sql.Types.REAL:
		            tobuf.append("        {\"name\": \"");
		            tobuf.append(toJasonString(getColumnName(i+1)));
		            tobuf.append("\", \"type\": \"float\"}");
                            break;
			case java.sql.Types.DOUBLE:
			case java.sql.Types.NUMERIC:
			case java.sql.Types.DECIMAL:
		            tobuf.append("        {\"name\": \"");
		            tobuf.append(toJasonString(getColumnName(i+1)));
		            tobuf.append("\", \"type\": \"double\"}");
                            break;
			case java.sql.Types.DATE:
		            tobuf.append("        {\"name\": \"");
		            tobuf.append(toJasonString(getColumnName(i+1)));
		            tobuf.append("\", \"type\": \"date\", \"dateFormat\": \"Y-m-d\"}");
                            break;
			case java.sql.Types.TIME:
		            tobuf.append("        {\"name\": \"");
		            tobuf.append(toJasonString(getColumnName(i+1)));
		            tobuf.append("\", \"type\": \"date\", \"dateFormat\": \"H:i:s\"}");
                            break;
			case java.sql.Types.TIMESTAMP:
		            tobuf.append("        {\"name\": \"");
		            tobuf.append(toJasonString(getColumnName(i+1)));
		            tobuf.append("\", \"type\": \"date\", \"dateFormat\": \"Y-m-d H:i:s\"}");
                            break;
			default:
		            tobuf.append("        {\"name\": \"");
		            tobuf.append(toJasonString(getColumnName(i+1)));
		            tobuf.append("\"}");
                            break;
		    }
   	        }
		tobuf.append("],\n");
		tobuf.append("      \"remoteSort\": true\n");
		/*
		tobuf.append("      \"sortInfo\": { \"field\": \"");
		tobuf.append(toJasonString(getColumnName(1)));
		tobuf.append("\", \"direction\": \"ASC\" }\n");
		*/
		tobuf.append("  }");
             }
	     tobuf.append(",\n");
	     /*
	     tobuf.append("  \"head\":\n");
	     if (column_count > 0)
	     {
		tobuf.append("  [\n");
	        for(int i=0; i< column_count; i++)
	        {
		    if (i>0) tobuf.append(",\n");
		    tobuf.append("      {");
		    tobuf.append("\"name\": \"");
		    tobuf.append(toJasonString(getColumnName(i+1)));
		    tobuf.append("\", \"label\": \"");
		    tobuf.append(toJasonString(getColumnLabel(i+1)));
		    tobuf.append("\"}");
   	        }
		tobuf.append("\n  ]");
             }
	     tobuf.append(",\n");
	     */
	     tobuf.append("  \"rows\":\n");	     
	     if (getRowCount() > 0)
	     {
		tobuf.append("  [\n");
	        for(int i=0; i< getRowCount(); i++)
	        {
		    if (i>0) tobuf.append(",\n");
		    tobuf.append("      {\n");
		    for(int j=0;j<column_count;j++)
		    {
			if (j>0) tobuf.append(",\n");
			tobuf.append("        \"");
			tobuf.append(toJasonString(getColumnName(j+1)));
			tobuf.append("\": ");
			val = getItem(i+1,j+1);
			if (val != null)
			{
			   if (!(val instanceof Number)) tobuf.append("\"");
			   tobuf.append(toJasonString(val.toString()));
			   if (!(val instanceof Number)) tobuf.append("\"");
			}
		    }
		    tobuf.append("\n      }");
   	        }
		tobuf.append("\n  ]\n");
             }
	     tobuf.append("}\n");
	     ps.write(tobuf.toString());
	}


	public final DBRowCache rotate()
	{
		DBRowCache tempdata = new SimpleDBRowCache();
		if (getRowCount() > 0)
		{
			tempdata.addColumn(getColumnLabel(1), java.sql.Types.VARCHAR);
			for(int row = 1; row <= getRowCount(); row ++)
			{
				Object item = getItem(row, 1);
				if (item != null)
					tempdata.addColumn(item.toString(), java.sql.Types.OTHER);
				else
					tempdata.addColumn(String.valueOf(row), java.sql.Types.OTHER);
			}
			for(int row = 2; row <= getColumnCount(); row ++)
			{
				tempdata.appendRow();
				tempdata.setItem(row - 1,1,getColumnLabel(row)); 
			}
			for(int col = 2; col <= getColumnCount(); col ++)
			{
				for(int row = 1; row <= getRowCount(); row ++)
				{
					Object item = getItem(row, col);
					tempdata.setItem(col - 1, row+1, item);
				}
			}
		}
		return tempdata;
	}

        private String getSelectHTML(String strValues, String curValue)
        {
	    StringBuffer htmlcode = new StringBuffer();

	    String vararray[] = TextUtils.toStringArray(TextUtils.getWords(strValues,";"));

	    for(int i=0;i<vararray.length;i++)
	    {
		String keyval[] = TextUtils.toStringArray(TextUtils.getWords(vararray[i],"="));
		if (keyval.length == 2)
		{
		    if (keyval[0].equals(curValue))
		        htmlcode.append("<option value=\""+keyval[0]+"\" selected>"+keyval[1]+"</option>");
		    else
		        htmlcode.append("<option value=\""+keyval[0]+"\" >"+keyval[1]+"</option>");
		}
		else
		{
	            if (vararray[i].equals(curValue))
           		htmlcode.append("<option value=\""+curValue+"\" selected>"+curValue+"</option>");
		    else
		        htmlcode.append("<option value=\""+vararray[i]+"\" >"+vararray[i]+"</option>");
		}
	    }	    
	    return htmlcode.toString();
	}
}
