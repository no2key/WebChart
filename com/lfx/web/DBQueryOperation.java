package com.lfx.web;
import com.lfx.db.*;

public class DBQueryOperation
{

	public static String   getVariableTableValue(VariableTable vt, String varname, String index, boolean bupper)
	{
		if (vt == null) return null;
		String val = vt.getString("WEBCHART."+varname+"_"+index);
		if (val == null && bupper)
			val = vt.getString("WEBCHART."+varname);
		return val;
	}

	public static String   getVariableTableValue(VariableTable vt, String varname)
	{
		if (vt == null) return null;
		String val = vt.getString("WEBCHART."+varname);
		return val;		
	}	

	public static int getVariableTableInt(VariableTable vt, String varname, int def)
	{
		if (vt == null) return def;
		int val = vt.getInt("WEBCHART."+varname, def);
		return val;		
	}	


	public static boolean  existVariableTableValue(VariableTable vt, String varname)
	{
		if (vt == null) return false;
		return vt.exists("WEBCHART."+varname);
	}	

	public static boolean  existVariableTableValue(VariableTable vt, String varname, String index)
	{
		if (vt == null) return false;
		return vt.exists("WEBCHART."+varname+"_"+index);
	}
	
	public static void generateDBQuery(java.io.Writer out,VariableTable vt) 
		throws java.io.IOException
    	{
		int i=0,sql_count=0;
		DBRowCache crosstab = null, temprows=null;
    		String chartquery = null;
		String charttype  = null;
		String iscrosstab = null;
		String dbname = null;
		String dbrule = null;
		String express = null;
		String foreach = null;
		String ignmarkdown = null;
		String ignsqlerror = null;
		java.util.Vector foreachlist = new java.util.Vector();
		java.util.Vector querylist = new java.util.Vector();
		boolean has_secure_words = false;

		DBPooledConnection db	= null;

	        out.write("<html><body><pre>");

		if (existVariableTableValue(vt,"SECUREFIELDS"))
		{
			SQLSecureFields.loadFieldsFromFile(getVariableTableValue(vt,"SECUREFIELDS"));
		}

		if(existVariableTableValue(vt,"QUERY") && getVariableTableValue(vt,"QUERY") != null)
                {
			querylist = TextUtils.getWords(getVariableTableValue(vt,"QUERY"),",");
                }
                else
                {
  		    for(i=0;i<100;i++)
		    {
			if (existVariableTableValue(vt,"QUERY",String.valueOf(i+1))) 
			{
				chartquery = getVariableTableValue(vt,"QUERY",String.valueOf(i+1),false);
				if (chartquery != null && chartquery.length() > 0)
				{
					querylist.addElement(String.valueOf(i+1));
				}			
			}
		    }
		}

		if (querylist.size()>0)
		{
			crosstab = DBOperation.getDBRowCache();
			for(i=0;i<querylist.size();i++)
			{
			    crosstab = DBOperation.getDBRowCache();
			    iscrosstab = getVariableTableValue(vt, "CROSSTAB", querylist.elementAt(i).toString(), true);
			    chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i).toString(),false);
			    charttype = getVariableTableValue(vt, "TYPE", querylist.elementAt(i).toString(),false);
			    dbname = getVariableTableValue(vt, "DBNAME", querylist.elementAt(i).toString(),true);
			    dbrule = getVariableTableValue(vt, "DBID", querylist.elementAt(i).toString(),true);
			    ignsqlerror = getVariableTableValue(vt, "IGNORE_SQLERROR", querylist.elementAt(i).toString(),true);
			    ignmarkdown = getVariableTableValue(vt, "IGNORE_MARKDOWN", querylist.elementAt(i).toString(),true);

			    foreach = vt.parseString(getVariableTableValue(vt, "FORALL", querylist.elementAt(i).toString(),false));
				
			    foreachlist.removeAllElements();
			    if (foreach != null)
				foreachlist.addAll(TextUtils.getLines(foreach));

			    for(int forj=0; forj < (foreachlist.size() > 0 ? foreachlist.size() : 1); forj++)
			    {
				if (forj < foreachlist.size())
				{
				  if (foreachlist.get(forj) == null ||
				    foreachlist.get(forj).toString().trim().length() == 0)
				    continue;
				  vt.setValue(foreachlist.get(forj).toString());
				}

				if (!chartquery.equals("-"))
				{
				    crosstab = null;
				    crosstab = DBOperation.getDBRowCache();
				    for(int dsloop=0; dsloop < 2; dsloop ++)
				    {
				      try {
				        db = DBLogicalManager.getPoolConnection(vt.parseString(dbname),dbrule);
					chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i).toString(),false);
					if (chartquery.equalsIgnoreCase("*"))
					{
						chartquery = getVariableTableValue(vt, "QUERY_"+db.getDBTag(), querylist.elementAt(i).toString(),false);
					}
					if (existVariableTableValue(vt,"SECUREFIELDS"))
					{
						SQLQuery sql_query = SQLConvert.parseSQL(chartquery,vt);
						java.util.Vector tab_fields = SQLTableFields.parse(sql_query.getDestSQL());
						for(int tfi = 0; tfi<tab_fields.size(); tfi++)
						{
							sql_count = SQLSecureFields.isSecureFields(tab_fields.elementAt(tfi).toString());
							if (sql_count > 0)
							{
								has_secure_words = true;
								if (sql_count == 1)
								{
								   out.write(tab_fields.elementAt(tfi).toString());
								   out.write(getVariableTableValue(vt,"SECUREMEMO1"));
								   out.write("\n");
								}
								else if (sql_count == 2)
								{
								   out.write(tab_fields.elementAt(tfi).toString());
								   out.write(getVariableTableValue(vt,"SECUREMEMO2"));
								   out.write("\n");
								}
								else if (sql_count == 3)
								{
								   out.write(tab_fields.elementAt(tfi).toString());
								   out.write(getVariableTableValue(vt,"SECUREMEMO3"));
								   out.write("\n");
								}
								/* break; */
							}
						}
					}
					if (has_secure_words == false)
					{
					    java.util.Vector cross_fields = TextUtils.getWords(iscrosstab,"|");
					    if (cross_fields.size()==0)
						crosstab = DBOperation.executeQuery(db,chartquery,vt);
					    else if(cross_fields.size() < 3)
						crosstab = DBOperation.executeCrossTab(db,chartquery,vt);
					    else
						crosstab = DBOperation.executeCrossTab(db,chartquery,vt,
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(0).toString(),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(1).toString(),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(2).toString(),",")));

					    express = getVariableTableValue(vt, "EXPRESS", querylist.elementAt(i).toString(),true);
					    if (express != null)
					    {
					   	 String colname;
					   	 String expstr;
					  	 String expcols[] = null;
					  	 String colarrs[] = null;
					  	 java.util.Vector expwords;
						 String exparr[] = TextUtils.toStringArray(TextUtils.getLines(express));
						 for(int tmpk=0; tmpk < exparr.length; tmpk++)
						 {
						    	expwords = TextUtils.getWords(exparr[tmpk],"|");
						    	if (expwords.size() >= 3)
						    	{
						    		colname = expwords.elementAt(0).toString();
						    		expstr  = expwords.elementAt(1).toString();
						    		expcols = TextUtils.toStringArray(TextUtils.getWords(expwords.elementAt(2).toString(),","));
								if (expwords.size() > 3)
									colarrs = TextUtils.toStringArray(TextUtils.getWords(expwords.elementAt(3).toString(),","));
					    			if (expcols.length < 2)
						    		{
					    			    crosstab.addExpression(colname, expstr, expcols[0], colarrs);
					    			}
					    			else if (expcols.length < 3)
					    			{
					    			    crosstab.addExpression(colname, expstr, expcols[0],expcols[1], colarrs);
					    			}
					    			else if (expcols.length < 4)
					    			{
					    			    crosstab.addExpression(colname, expstr, expcols[0],expcols[1],expcols[2], colarrs);
					    			}					    		
							}
					   	 }
					    }
					    express = getVariableTableValue(vt, "FILTER", querylist.elementAt(i).toString(),true);
				    	    if (express != null)
				    	    {
					        String expstr;
						java.util.Vector expwords;
						String expcols[] = null;
						String exparr[] = TextUtils.toStringArray(TextUtils.getLines(express));
						for(int tmpk=0; tmpk < exparr.length; tmpk++)
						{
						    expwords = TextUtils.getWords(exparr[tmpk],"|");
						    if (expwords.size() >= 2)
						    {
						    	expstr = expwords.elementAt(0).toString();
						    	expcols = TextUtils.toStringArray(TextUtils.getWords(expwords.elementAt(1).toString(),","));
					    		if (expcols.length < 2)
					    		{
						    	    crosstab.expressFilter(expstr, expcols[0]);
						    	}
						    	else if (expcols.length < 3)
						    	{
						    	    crosstab.expressFilter(expstr, expcols[0], expcols[1]);
						    	}
					    		else if (expcols.length < 4)
					    		{
						            crosstab.expressFilter(expstr, expcols[0], expcols[1], expcols[2]);
						    	}					    		
						    }
					        }
					    }
					}					
					if (db != null) { db.close(); db = null; }
					break;
				     }
			 	     catch(java.sql.SQLException sqle)
				     {
					crosstab = DBOperation.getDBRowCache();
					db.checkSQLState(sqle.getSQLState());
					if (dsloop == 1)
					{
					    if ("YES".equalsIgnoreCase(ignsqlerror))
					    {
						continue;
					    }
					    else
					    {
						throw new java.io.IOException(sqle.getMessage());
					    }
					}
				     }
				     catch(DatabaseMarkdownException dme)
				     {
					if (dsloop == 1)
					{
					    if ("YES".equalsIgnoreCase(ignsqlerror) || "YES".equalsIgnoreCase(ignmarkdown))
					    {
						    continue;
					    }
					    else
					    {
						    throw dme;
					    }
					}
				     }
				     finally
				     {
					if (db != null) { db.close(); db = null; }
				     }
				   }
				}
				if (db != null) { db.close(); db = null; }

				String chartlabel = getVariableTableValue(vt,"LABEL",querylist.elementAt(i).toString(),true);
				java.util.Vector label = TextUtils.getFields(chartlabel,"|");
				for(int j=0;j<label.size() && j<crosstab.getColumnCount();j++)
				{
					if (label.elementAt(j) != null)
						crosstab.setColumnLabel(j+1,label.elementAt(j).toString());
				}
				if (crosstab.getRowCount()==1)
				{
					for(int j=1;j<=crosstab.getColumnCount();j++)
					{
						vt.add("QUERY_"+querylist.elementAt(i).toString()+"."+crosstab.getColumnName(j),
							crosstab.getColumnType(j));
						vt.setValue("QUERY_"+querylist.elementAt(i).toString()+"."+crosstab.getColumnName(j),
							crosstab.getItem(1,j));
					}
				}
		                String sortcolumns = getVariableTableValue(vt,"SORT",querylist.elementAt(i).toString(),true);
				if (sortcolumns != null)
				{
					crosstab.quicksort(TextUtils.toStringArray(TextUtils.getWords(sortcolumns,",")));
				}				
				if (charttype == null || !charttype.equals("-"))
				{
					if (temprows != null)
					{
						temprows.appendRow(crosstab);
						crosstab = temprows;
					}
					if (!has_secure_words)
					{
					    String chartdisplay = getVariableTableValue(vt,"DISPLAY",querylist.elementAt(i).toString(),true);
					    String fielddelimter = getVariableTableValue(vt,"FIELDTAG",querylist.elementAt(i).toString(),true);
					    if (fielddelimter == null) fielddelimter=",";
					    if ("FORM".equalsIgnoreCase(chartdisplay))
					    {
						crosstab.writeForm(out);
					    }
					    else
					    {
						crosstab.write(out,fielddelimter);
					    }
					}
					else
					{
					    out.write(getVariableTableValue(vt,"SECUREMEMO"));
					    out.write("\n");
					}
				}
				else
				{
					if (temprows==null || temprows.getColumnCount() != crosstab.getColumnCount())
					{
						temprows = DBOperation.getDBRowCache();
						temprows.copyColumns(crosstab);
					}
					temprows.appendRow(crosstab);
				}
			    }
			}
		}
		out.write("</pre></body></html>");
		out.flush();
		out.close();
	}

	private static int getint(String val,int def)
	{
		try {
			if (val != null)
				return Integer.valueOf(val).intValue();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			return def = 2;
		}
		return def;
	}
}