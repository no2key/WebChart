package com.lfx.db;
import java.net.URLEncoder;

public final class VariableTable 
{

	class CellValue 
	{
		private String var_name = null;
		private int    var_type = 0;
		private int    var_expr = 0;
		private Object var_value= null;

		public CellValue(String v_name, int v_type)
		{
			var_name = v_name;
			var_type = v_type;
		}

		public CellValue(String v_name, int v_type, int v_expr)
		{
			var_name = v_name;
			var_type = v_type;
			var_expr = v_expr;
		}

		public String getName()
		{
			return var_name;
		}

		public int getType()
		{
			return var_type;
		}


		public int getExpr()
		{
			return var_expr;
		}

		public void setType(int v_type)
		{
			var_type = v_type;
		}

		public Object getValue()
		{
			return var_value;
		}
		
		public void setValue(Object v_value)
		{
			if (var_expr == 0)
				var_value = SQLTypes.getValue(var_type,v_value);
			else
				var_value = v_value;
		}
	}

	protected java.util.HashMap<String,CellValue> var_list = new java.util.HashMap<String,CellValue>(32);

	public final int size()
	{
		return var_list.size();
	}

	public final String[] getNames()
	{
		String var_names[] = {};
		if (var_list.size() == 0)
			return var_names;
		var_names = new String[var_list.size()];
		Object v_arr[] = var_list.values().toArray();
		for(int i=0;i<v_arr.length;i++)
		{
			var_names[i] = ((CellValue)v_arr[i]).getName();
		}
		return var_names;
	}
	
	public final boolean exists(String var)
	{
		if (var!=null)
		{
			if (var.equalsIgnoreCase("SYS.DATE"))
			{
				return true;
			}
			else if (var.equalsIgnoreCase("SYS.TIME"))
			{
				return true;
			}
			else if (var.equalsIgnoreCase("SYS.DATETIME"))
			{
				return true;
			}
		}
		return (var_list.containsKey(var.toUpperCase()));
	}

	public final synchronized void remove(String var)
	{
		var_list.remove(var.toUpperCase());
	}

	public synchronized final void removeAll()
	{
		var_list.clear();
	}

	public final String getString(String var)
	{
		Object temp = getValue(var);
		if (temp!=null)
		{
			return temp.toString();
		}
		return null;
	}
	public final String getString(String var,String def)
	{
		Object temp = getValue(var);
		if (temp!=null)
		{
			return temp.toString();
		}
		return def;
	}
	public final int getInt(String stname,int idef)
   	{
		String temp = getString(stname);
		if (temp == null)
			return idef;
		try {
			return Integer.valueOf(temp).intValue();
		}
		 catch (NumberFormatException nfe) {}
		return idef;
	}
	public final long getLong(String stname,long ldef)
   	{
		String temp = getString(stname);
		if (temp == null)
			return ldef;
		try {
			return Long.valueOf(temp).longValue();
		}
		 catch (NumberFormatException nfe) {}
		return ldef;
   	}
	public final float getFloat(String stname,float fdef)
   	{
		String temp = getString(stname);
		if (temp == null)
			return fdef;
		try {
			return Float.valueOf(temp).floatValue();
		}
		catch (NumberFormatException nfe) {}
		return fdef;
   	}
	public final double getDouble(String stname,double ddef)
   	{
		String temp = getString(stname);
		if (temp == null)
			return ddef;
		try {
			return Double.valueOf(temp).doubleValue();
		}
		catch (NumberFormatException nfe) {}
		return ddef;
   	}

	public final boolean getBoolean(String stname,boolean bdef)
   	{
		String temp = getString(stname);
		if (temp == null)
			return bdef;
		return Boolean.valueOf(temp).booleanValue();
   	}

	public final Object getValue(String var)
	{
		int i;
		if (var!=null)
		{
			if (var.equalsIgnoreCase("SYS.DATE"))
			{
				return new java.sql.Date(System.currentTimeMillis());
			}
			else if (var.equalsIgnoreCase("SYS.TIME"))
			{
				return new java.sql.Time(System.currentTimeMillis());
			}
			else if (var.equalsIgnoreCase("SYS.DATETIME"))
			{
				return new java.sql.Timestamp(System.currentTimeMillis());
			}
		}
		if (var != null && var_list.containsKey(var.toUpperCase()))
		{
			CellValue temp = (CellValue)(var_list.get(var.toUpperCase()));
			if (temp.getExpr() > 0)
			{
				if (temp.getValue() != null)
				{
 					DBRowCacheExpression expression = new DBRowCacheExpression(temp.getValue().toString());
					return SQLTypes.getValue(temp.getType(),Double.valueOf(expression.value(this)));
				}
				else
				{
					return null;
				}
			}
			return temp.getValue();
		}
		return null;
	}
	public final int getType(String var)
	{
		int i;
		if (var!=null)
		{
			if (var.equalsIgnoreCase("SYS.DATE"))
			{
				return java.sql.Types.DATE;
			}
			else if (var.equalsIgnoreCase("SYS.TIME"))
			{
				return java.sql.Types.TIME;
			}
			else if (var.equalsIgnoreCase("SYS.DATETIME"))
			{
				return java.sql.Types.TIMESTAMP;
			}
		}
		if (var != null && var_list.containsKey(var.toUpperCase()))
		{
			CellValue temp = (CellValue)(var_list.get(var.toUpperCase()));
			return temp.getType();
		}
		return java.sql.Types.VARCHAR;
	} 
	public final void setValue(String var,Object val)
		throws NumberFormatException
	{
		int i;
		if (var!=null)
		{
			if (var.equalsIgnoreCase("SYS.DATE"))
			{
				return ;
			}
			else if (var.equalsIgnoreCase("SYS.TIME"))
			{
				return;
			}
			else if (var.equalsIgnoreCase("SYS.DATETIME"))
			{
				return;
			}
		}
		if (var != null && var_list.containsKey(var.toUpperCase()))
		{
			CellValue var_value = (CellValue)(var_list.get(var.toUpperCase()));
			if (val == null)
				var_value.setValue(null);
			else
			{
				Object temp=val;
				if (val instanceof java.lang.String)
				{
					if (val.toString().equals("$today"))
					{
						temp = DBOperation.getDay("yyyyMMdd");
					}
					else if (val.toString().startsWith("$today+"))
					{
						temp = DateOperator.addDays(DBOperation.getDay("yyyyMMdd"),
							Integer.valueOf(val.toString().substring(7)).intValue());
					}
					else if (val.toString().startsWith("$today-"))
					{
						temp = DateOperator.addDays(DBOperation.getDay("yyyyMMdd"),
							Integer.valueOf(val.toString().substring(6)).intValue());
					}
					else if (val.toString().equals("$month"))
					{
						temp = DBOperation.getDay("yyyyMM");
					}
					else if (val.toString().startsWith("$month+"))
					{
						temp = DateOperator.addMonths(DBOperation.getDay("yyyyMMdd"),
							Integer.valueOf(val.toString().substring(7)).intValue()).substring(0,6);
					}
					else if (val.toString().startsWith("$month-"))
					{
						temp = DateOperator.addMonths(DBOperation.getDay("yyyyMMdd"),
							Integer.valueOf(val.toString().substring(6)).intValue()).substring(0,6);
					}
					else if (val.toString().equals("$year"))
					{
						temp = DBOperation.getDay("yyyy");
					}
					else if (val.toString().startsWith("$year+"))
					{
						temp =DateOperator.addMonths(DBOperation.getDay("yyyyMMdd"),
							Integer.valueOf(val.toString().substring(6)).intValue() * 12).substring(0,4);
					}
					else if (val.toString().startsWith("$year-"))
					{
						temp = DateOperator.addMonths(DBOperation.getDay("yyyyMMdd"),
							Integer.valueOf(val.toString().substring(5)).intValue() * 12).substring(0,4);
					}
					else
					{
						temp = val;
					}
				}
				var_value.setValue(temp);
			}
		}
	}

	public final void setValue(String line)
	{
		if (line != null)
		{
		  java.util.Vector line_vector = TextUtils.getWords(line,";");
		  for(int i=0;i<line_vector.size();i++)
		  {
		    if (line_vector.get(i) != null)
		    {
			java.util.Vector word_vector = TextUtils.getWords(line_vector.get(i).toString().trim(),"=");
			if (word_vector.size() >= 2)
			{
			   if (word_vector.get(0) != null && word_vector.get(1) != null)
			   {
			      if (!exists(word_vector.get(0).toString()))
				  add(word_vector.get(0).toString(),java.sql.Types.VARCHAR);
			      setValue(word_vector.get(0).toString(), word_vector.get(1));
			   }
			}
			else if (word_vector.size() >= 1)
			{
			      if (!exists("ARGV"+i)) add("ARGV"+i,java.sql.Types.VARCHAR);
			      setValue("ARGV"+i, word_vector.get(0));
			}
		    }
		  }
		}
	}

	public final void setType(String var,int type)
	{
		int i;
		if (var!=null)
		{
			if (var.equalsIgnoreCase("SYS.DATE"))
			{
				return ;
			}
			else if (var.equalsIgnoreCase("SYS.TIME"))
			{
				return;
			}
			else if (var.equalsIgnoreCase("SYS.DATETIME"))
			{
				return;
			}
		}
		if (var != null && var_list.containsKey(var.toUpperCase()))
		{
			CellValue temp = (CellValue)(var_list.get(var.toUpperCase()));
			temp.setType(type);
		}
	}

	public synchronized final void add(String var,int type)
	{
		int i;
		if (var == null)
			return ;
		if (var!=null)
		{
			if (var.equalsIgnoreCase("SYS.DATE"))
			{
				return ;
			}
			else if (var.equalsIgnoreCase("SYS.TIME"))
			{
				return;
			}
			else if (var.equalsIgnoreCase("SYS.DATETIME"))
			{
				return;
			}
		}
		if (!var_list.containsKey(var.toUpperCase()))
		{
			CellValue temp = new CellValue(var.toUpperCase(), type);
			var_list.put(temp.getName(), temp);
		}
	}

	public synchronized final void add(String var,int type, int expr)
	{
		int i;
		if (var == null)
			return ;
		if (var!=null)
		{
			if (var.equalsIgnoreCase("SYS.DATE"))
			{
				return ;
			}
			else if (var.equalsIgnoreCase("SYS.TIME"))
			{
				return;
			}
			else if (var.equalsIgnoreCase("SYS.DATETIME"))
			{
				return;
			}
		}
		if (!var_list.containsKey(var.toUpperCase()))
		{
			CellValue temp = new CellValue(var.toUpperCase(), type, expr);
			var_list.put(temp.getName(), temp);
		}
	}
	public final void loadURL(String url)
   	{
		try {
			java.net.URL urlfile = getClass().getResource(url);
			if (urlfile == null) return;
			load(new java.io.BufferedReader(
				new java.io.InputStreamReader(urlfile.openStream())));
		} catch (java.io.IOException e)	{}
   	}

	public final void loadFile(String url)
   	{
		try {
			java.io.BufferedReader file = new java.io.BufferedReader(
				new java.io.InputStreamReader
					(new java.io.FileInputStream(url)));
			load(file);
		} catch (java.io.IOException e) {}
   	}

   public final void loadInputStream(java.io.InputStream in)
   {
	java.io.BufferedReader file = new java.io.BufferedReader(
		new java.io.InputStreamReader(in));
	load(file);
   }
   
   public final void loadContent(String content)
   {
	java.io.BufferedReader fin = new java.io.BufferedReader(
		new java.io.StringReader(content));
	load(fin);   		
   }

   private final void load(java.io.BufferedReader in)
   {
	int rows=0,pos=0, lastslash=0;
	String temp="";
	String line="";
	try {
		if (in==null) return;
		while((temp=in.readLine())!=null)
		{
			if (temp.trim().length()==0) continue;
			if (temp.substring(0,1).equals("#")) continue;
			if (temp.startsWith("var ") || temp.startsWith("VAR "))
			{
				String cmdwords[] = TextUtils.toStringArray(TextUtils.getWords(temp.substring(1)));
				if (cmdwords.length >= 3)
				{
				    if (cmdwords.length == 4)
					    add(cmdwords[1], SQLTypes.getTypeID(cmdwords[2]), "EXPR".equalsIgnoreCase(cmdwords[3])?1:0);
				    else
					    add(cmdwords[1], SQLTypes.getTypeID(cmdwords[2]));
				}
				continue;
			}
			char temp_arr[] = temp.toCharArray();
			for(pos = temp_arr.length;pos > 0;pos --)
			{
				if (temp_arr[pos - 1] != ' ' &&
					temp_arr[pos - 1] != '\t') break;
			}
			temp = String.valueOf(temp_arr,0,pos);
			pos=0;
			if (temp.endsWith("\\"))
			{
				if (lastslash==0)
			        {
				    pos = line.indexOf("=");
				    if (pos>0)
				    {
					add(line.substring(0,pos).trim().toUpperCase(),java.sql.Types.VARCHAR);
					if (pos == (line.length() - 1))
						setValue(line.substring(0,pos).trim().toUpperCase(),"");
					else
						setValue(line.substring(0,pos).trim().toUpperCase(),
							line.substring(pos+1));
				    }
				    line = temp.substring(0,temp.length()-1);
				}
				else
				{
				    line = line + "\n" + temp.substring(0,temp.length()-1);
				}
				lastslash=1;
				continue;
			}
			else
			{
				if (temp_arr[0] == ' ' || temp_arr[0] == '\t')
                                {
                                    line=line + "\n" + temp.substring(1);
                                }
				else
				{
				    if (lastslash > 0) line = line + "\n" + temp;  
				    pos = line.indexOf("=");
				    if (pos>0)
				    {
					add(line.substring(0,pos).trim().toUpperCase(),java.sql.Types.VARCHAR);
					if (pos == (line.length() - 1))
						setValue(line.substring(0,pos).trim().toUpperCase(),"");
					else
						setValue(line.substring(0,pos).trim().toUpperCase(),
							line.substring(pos+1));
				    }
				    if (lastslash > 0)
					line = "";
				    else
					line = temp;
                                }
		    		lastslash=0;
			}
		}
		if (line.length()>0)
		{
			pos = line.indexOf("=");
			if (pos>0)
			{
				add(line.substring(0,pos).trim().toUpperCase(),java.sql.Types.VARCHAR);
				if (pos == (line.length() - 1))
					setValue(line.substring(0,pos).trim().toUpperCase(),"");
				else
					setValue(line.substring(0,pos).trim().toUpperCase(),
						line.substring(pos+1));
			}
		}
	} catch (java.io.IOException e)
	{
	}
	try {
		if (in != null)
			in.close();
	} catch (java.io.IOException ioe){}
   }

   public final String parseURLString(String sfile)
   {
	return parseURLString(sfile,'$','~');
   }

   public final String parseURLString(String sfile,char sep, char mac)
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
	            result_buf.append(sfile.charAt(i));
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
		      if (exists(temp_buf.toString()))
		      {
			  val = getValue(temp_buf.toString());                  
		      }
		      if (val != null)
		      {
			  result_buf.append(URLEncoder.encode(val.toString()));
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
   		        if (exists(temp_buf.toString()))
  	   		{
	  		    val = getValue(temp_buf.toString());                  
	      		}
	 	        if (val != null)
	      		{
			   result_buf.append(URLEncoder.encode(val.toString()));
	      		}
			if (i<sfile.length() && sfile.charAt(i) == '.' ) i++;
            	  }
	          else
        	  {
			i++;
            	  }
          	}
	      }
	        else if (sfile.charAt(i) == mac)
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
		      if (exists(temp_buf.toString()))
		      {
			  val = getValue(temp_buf.toString());                  
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
   		        if (exists(temp_buf.toString()))
  	   		{
	  		    val = getValue(temp_buf.toString());                  
	      		}
	 	        if (val != null)
	      		{
			   result_buf.append(val.toString());
	      		}
			if (i<sfile.length() && sfile.charAt(i) == '.' ) i++;
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

   public final String parseString(String sfile)
   {
	return parseString(sfile,'$');
   }

   public final String parseString(String sfile, char sep)
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
	            result_buf.append(sfile.charAt(i));
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
		      if (exists(temp_buf.toString()))
		      {
			  val = getValue(temp_buf.toString());                  
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
   		        if (exists(temp_buf.toString()))
  	   		{
	  		    val = getValue(temp_buf.toString());                  
	      		}
	 	        if (val != null)
	      		{
			   result_buf.append(val.toString());
	      		}
			if (i<sfile.length() && sfile.charAt(i) == '.' ) i++;
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
	  else
	  {
		return null;
	  }
   	  return result_buf.toString();
   }


   public final String parseString(String sfile, DBRowCache data, int row)
   {
      return parseString(sfile,'$',data, row);
   }
   public final String parseString(String sfile, char sep,  DBRowCache data, int row)
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
	            result_buf.append(sfile.charAt(i));
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
		      if (exists(temp_buf.toString()))
		      {
			  val = getValue(temp_buf.toString());                  
		      }
		      else if (data.findColumn(temp_buf.toString()) > 0)
		      {
			  val = data.getItem(row, temp_buf.toString());
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
   		        if (exists(temp_buf.toString()))
  	   		{
	  		    val = getValue(temp_buf.toString());                  
	      		}
	  	        else if (data.findColumn(temp_buf.toString()) > 0)
		        {
	  		    val = data.getItem(row, temp_buf.toString());
	  	        }
	 	        if (val != null)
	      		{
			   result_buf.append(val.toString());
	      		}
			if (i<sfile.length() && sfile.charAt(i) == '.') i++;
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
	  else
	  {
		return null;
	  }
   	  return result_buf.toString();
   }
      
   public void  writeXMLBody(java.io.Writer out) throws java.io.IOException
   {
	writeXMLBody(out,"param", "WEBCHART.");
   }

   public void  writeXMLBody(java.io.Writer out, String tag) throws java.io.IOException
   {
	writeXMLBody(out,tag, "WEBCHART.");
   }

   public void  writeXMLBody(java.io.Writer out,String tag, String ignore) throws java.io.IOException
   {
	int row,col,cat;
	Object v_arr[] = var_list.values().toArray();
	for(row=0;row<v_arr.length;row++)
	{
		CellValue temp = (CellValue)(v_arr[row]);
		if (temp.getName().startsWith(ignore))
		{
			continue;
		}
		else
		{
			cat = 0;
			if (temp.getName().startsWith("REQUEST."))
			{
				cat = 1;
			}
			else if (temp.getName().startsWith("SESSION."))
			{
				cat = 2;
			}
			else if (temp.getName().startsWith("HTML."))
			{
				cat = 3;
			}
			out.write("<");
			out.write(tag);
			out.write(" id=\"");
			out.write(temp.getName());
			out.write("\" cat=\"");
			out.write(String.valueOf(cat));
			out.write("\">");
			// out.write("<![CDATA[");
			if (temp.getValue() != null)
				out.write(EncodeXML(parseString(getString(temp.getName()))));
			// out.write("]]>");
			out.write("</");
			out.write(tag);
			out.write(">\n");
		}
	}
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
}