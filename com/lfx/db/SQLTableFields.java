package com.lfx.db;

public class SQLTableFields
{
     public final static java.util.Vector parse(String sfile)
     {
	    java.util.Vector  tablefield = new java.util.Vector();

            java.util.Vector  field_list = new java.util.Vector();
            java.util.Vector  table_list = new java.util.Vector();
            java.util.HashMap alias_list = new java.util.HashMap();

	    StringBuffer last_buf   = new StringBuffer();
	    StringBuffer temp_buf   = new StringBuffer();
	    boolean field_start = false;
	    boolean table_start = false;
	    boolean last_is_op=true;
	    boolean last_is_table=false;

	    if (sfile != null)
	    {
	      for(int i=0;i<sfile.length();)
	      {
		  temp_buf.delete(0,temp_buf.length());
		  while (i<sfile.length() && Character.isWhitespace(sfile.charAt(i)))  { i++; continue; };

		  // get Words
		  if (i < sfile.length() && (Character.isLetter(sfile.charAt(i)) || sfile.charAt(i) == '_'))
		  {
		      while(i<sfile.length() && (Character.isLetter(sfile.charAt(i)) || 
			    Character.isDigit(sfile.charAt(i)) || sfile.charAt(i) == '_' || sfile.charAt(i) == '.'
			    || sfile.charAt(i) == '#' || sfile.charAt(i) == '$' ))
		      {
		         temp_buf.append(sfile.charAt(i));
		         i++;
		         if (sfile.charAt(i-1) == '.' && sfile.charAt(i) == '*')
			 {
				temp_buf.append(sfile.charAt(i));
				i++;
			 }
		      }
		      if ("SELECT".equalsIgnoreCase(temp_buf.toString())) { field_start = true; table_start=false; last_is_op=true; last_is_table=false; continue;}
		      if ("FROM".equalsIgnoreCase(temp_buf.toString())) { table_start = true; field_start = false; last_is_table=false; continue;}
		      if ("WHERE".equalsIgnoreCase(temp_buf.toString())) { table_start = false; continue;}
		      if ("ORDER".equalsIgnoreCase(temp_buf.toString())) { table_start = false; continue;}
		      if ("GROUP".equalsIgnoreCase(temp_buf.toString())) { table_start = false; continue;}
		      if ("AS".equalsIgnoreCase(temp_buf.toString())) { continue;}
		      if (field_start && last_is_op) 
		      {
			     field_list.addElement(temp_buf.toString());
		             last_is_op = false;
		      }
		      else
		      {
			   last_is_op = true;
		      }
		      if (table_start) 
		      {
			  if (last_is_table)
			  {
				alias_list.put(temp_buf.toString().toUpperCase(),last_buf.toString());
				last_is_table = false;
			  }
			  else
			  {
				table_list.addElement(temp_buf.toString());
				alias_list.put(temp_buf.toString().toUpperCase(),temp_buf.toString());
				last_buf.delete(0,last_buf.length());
				last_buf.append(temp_buf);
				last_is_table = true;
			  }
				
		      }
		  }
		  else  if (i < sfile.length() && (Character.isDigit(sfile.charAt(i)) || sfile.charAt(i) == '.'))
		  {
		      while(i<sfile.length() && (Character.isDigit(sfile.charAt(i)) || sfile.charAt(i) == '.'))
		      {
		         temp_buf.append(sfile.charAt(i));
		         i++;
		      }
		  }
		  else if (i < sfile.length() && sfile.charAt(i) == '\'')
		  {
		      i++;
		      while(i<sfile.length() &&  sfile.charAt(i) != '\'') i++;
		      last_is_op = false;
		      i++;
		  }
		  else if (i < sfile.length() && sfile.charAt(i) == '"')
		  {
		      i++;
		      while(i<sfile.length() &&  sfile.charAt(i) != '"') i++;
		      last_is_op = false;
		      i++;
		  }
		  else if (i<sfile.length() && !Character.isWhitespace(sfile.charAt(i)))
		  {
		      if (sfile.charAt(i) == '+' || sfile.charAt(i) == '-' || sfile.charAt(i) == '*' 
                          || sfile.charAt(i) == '/' || sfile.charAt(i) == ',' || sfile.charAt(i) == '|'
			  || sfile.charAt(i) == '(')
		      {
			  if (last_is_op == true && sfile.charAt(i) == '*')
			     field_list.addElement("*");
			  else
			     last_is_op = true;
		      }
		      if (table_start && last_is_table) last_is_table = false;
		      i++;
		  }
	      }
              for(int i=0;i<field_list.size();i++)
	      {
		  String field_arr[] = TextUtils.toStringArray(TextUtils.getWords(field_list.elementAt(i).toString(),"."));
		  if (field_arr.length == 1)
		  {
	             for(int j=0;j<table_list.size();j++)
		     {
			String table_arr[] = TextUtils.toStringArray(TextUtils.getWords(table_list.elementAt(j).toString(),"."));
			if (table_arr.length == 1)
			{
				if (!tablefield.contains(table_arr[0]+"."+field_arr[0]))
				    tablefield.addElement(table_arr[0]+"."+field_arr[0]);
			}
			else if (table_arr.length == 2)
			{
				 if (!tablefield.contains(table_arr[1]+"."+field_arr[0]))
			   	    tablefield.addElement(table_arr[1]+"."+field_arr[0]);
			}
		     }
		  }
		  else if (field_arr.length == 2)
		  {
			if (alias_list.containsKey(field_arr[0].toUpperCase()))
			{
				String table_arr[] = TextUtils.toStringArray(TextUtils.getWords(alias_list.get(field_arr[0].toUpperCase()).toString(),"."));
				if (table_arr.length == 1)
				{
				    if (!tablefield.contains(table_arr[0]+"."+field_arr[1]))
				        tablefield.addElement(table_arr[0]+"."+field_arr[1]);
				}
				else if (table_arr.length == 2)
				{
				    if (!tablefield.contains(table_arr[1]+"."+field_arr[1]))
				        tablefield.addElement(table_arr[1]+"."+field_arr[1]);
				}
			}
		  }
	      }
      	    }
   	    return tablefield;
     }     
}