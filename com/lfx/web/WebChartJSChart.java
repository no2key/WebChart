package com.lfx.web;
 
import com.lfx.db.*;

public final class WebChartJSChart
{
	public static final int LINE   = 1;
	public static final int BAR    = 2;
	public static final int PIE    = 3;
	
	public static final String COLORLIST[] = {};

	public static String   getVariableTableValue(VariableTable vt, String varname, String index, boolean bupper)
	{
		if (vt == null) return null;
		StringBuffer buf = new StringBuffer();
		buf.append("WEBCHART.");
		buf.append(varname);
		buf.append('_');
		buf.append(index);
		String val = vt.getString(buf.toString());
		if (val == null && bupper)
		{
			buf.setLength(0);
			buf.append("WEBCHART.");
			buf.append(varname);
			val = vt.getString(buf.toString());
		}
		return val;
	}
	
	public static String   getVariableTableValue(VariableTable vt, String varname)
	{
		if (vt == null) return null;
		StringBuffer buf = new StringBuffer();
		buf.append("WEBCHART.");
		buf.append(varname);		
		String val = vt.getString(buf.toString());
		return val;		
	}	

	public static void setVariableTableValue(VariableTable vt, String varname, String val)
	{
		if (vt == null) return;
		StringBuffer buf = new StringBuffer();
		buf.append("WEBCHART.");
		buf.append(varname);	
		if (vt.exists(buf.toString()))
		    vt.setValue(buf.toString(), val);
	}	

	public static boolean  existVariableTableValue(VariableTable vt, String varname)
	{
		if (vt == null) return false;
		StringBuffer buf = new StringBuffer();
		buf.append("WEBCHART.");
		buf.append(varname);	
		return vt.exists(buf.toString());
	}	

	public static boolean  existVariableTableValue(VariableTable vt, String varname, String index)
	{
		if (vt == null) return false;
		StringBuffer buf = new StringBuffer();
		buf.append("WEBCHART.");
		buf.append(varname);
		buf.append('_');
		buf.append(index);
		return vt.exists(buf.toString());
	}	
	
	public static String[] getLines(String src)
	{
		return TextUtils.toStringArray(TextUtils.getLines(src));
	}

	private static int getChartType(String charttype)
        {
		int axis_type = LINE;
 		if ( charttype.equalsIgnoreCase("BAR"))
 			axis_type = BAR;
	 	else if (charttype.equalsIgnoreCase("LINE"))
	 		axis_type = LINE;
	 	else if (charttype.equalsIgnoreCase("PIE"))
	 		axis_type = PIE;
		return axis_type;				
        }
						
	public static final Double toDouble(Object objval)
	{
		if (objval == null) return null;
		try {
			return Double.valueOf(objval.toString());
		} catch (NumberFormatException nfe) {};
		return null;
	}
	
        private static boolean checkVariableList(String varlist, VariableTable vt)
	{
	    if (varlist == null) return true;
	    java.util.Vector var_req = TextUtils.getWords(varlist,",");
	    if (var_req.size() == 0) return true;
	    for (int i=0; i< var_req.size(); i++)
	    {
		if (var_req.elementAt(i).toString().startsWith("*"))
		{
			if (vt.getString(var_req.elementAt(i).toString().substring(1)) == null) return false;			
		}
		else
		{
			if (vt.getString(var_req.elementAt(i).toString()) == null) return false;
		}
	    }
            return true;
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

	public static VariableTable readModuleConfig(String fname, VariableTable vt)
	{
		VariableTable newvt = new VariableTable();
		String varnames[] = vt.getNames();

		newvt.loadContent(FileCache.getFileContent(fname));
		for(int i=0;i<varnames.length;i++)
		{
			if (!varnames[i].startsWith("WEBCHART."))
			{
			     if (!newvt.exists(varnames[i]))
			     {
				   newvt.add(varnames[i], vt.getType(varnames[i]));
				   newvt.setValue(varnames[i], vt.getValue(varnames[i]));
			     }
			}
		}
		return newvt;
	}

				
	public static void generateChart(java.io.Writer out, java.io.OutputStream imgout,VariableTable vt, String fileextention) 
		throws java.io.IOException
    	{
		int i=0,sql_count=0;
		DBRowCache crosstab = null, temprows=null;
    		String chartquery = null;
		String querycache = null;
		int    querycachetime = 300;
                int querymaxrows=5000;
		String charttype  = null;
		String iscrosstab = null;
		String readdata   = null;
		String readmile   = null;
		String dbname = null;
		String dbrule = null;
		String groovydbname = null;
		String groovydbrule = null;
		String express = null;
		String foreach = null;
		String varlist = null;
		String joindata= null;
		String summarydata= null;
		String pagecount = null;
		String ignmarkdown = null;
		String ignsqlerror = null;
		boolean pageexpire = false;
		String head_formater = null;
		String data_formater = null;
		String row_color = null;
		String row_style = null;
		String row_align = null;
		String lay_out = null;
		String lay_style = null;
		String loadmodule = null;
		String rotatedata = null;
		String chartycolumn = null;

		String tablename = null;
		String columnlist = null;
		String columneditor = null;
		String columnstyle = null;
		String columnvalues = null;

		java.util.Vector<String> foreachlist = new java.util.Vector<String>();
		java.util.Vector<String> querylist = new java.util.Vector<String>();
		java.util.HashMap<String, DBRowCache> joindatacache = new java.util.HashMap<String, DBRowCache>();

		String imageonly = getVariableTableValue(vt, "IMAGEONLY");
		if (imageonly == null) imageonly = "NO";
		imageonly = imageonly.toUpperCase();

		DBPooledConnection db	= null;

		if(existVariableTableValue(vt, "QUERY") && getVariableTableValue(vt,"QUERY").length()>0)
                {
			querylist = TextUtils.getWords(getVariableTableValue(vt,"QUERY"),",");
                }
                else
                {
  		    for(i=0;i<100;i++)
		    {
			if (existVariableTableValue(vt,"QUERY", String.valueOf(i+1))) 
			{
				chartquery = getVariableTableValue(vt,"QUERY",String.valueOf(i+1),false);
				if (chartquery != null && chartquery.length() > 0)
				{
					querylist.addElement(String.valueOf(i+1));
				}			
			}
			else if (existVariableTableValue(vt,"DATA", String.valueOf(i+1))) 
			{
				chartquery = getVariableTableValue(vt,"DATA",String.valueOf(i+1),false);
				if (chartquery != null && chartquery.length() > 0)
				{
					querylist.addElement(String.valueOf(i+1));
				}			
			}
			else if (existVariableTableValue(vt,"MODULE", String.valueOf(i+1))) 
			{
				chartquery = getVariableTableValue(vt,"MODULE",String.valueOf(i+1),false);
				if (chartquery != null && chartquery.length() > 0)
				{
					querylist.addElement(String.valueOf(i+1));
				}			
			}
		    }
		}
		if (existVariableTableValue(vt, "EXCELURL"))
		{
		    setVariableTableValue(vt, "EXCELURL", vt.parseString(getVariableTableValue(vt,"EXCELURL")));
		}
		if (!imageonly.equals("YES"))
		{
			if (existVariableTableValue(vt, "RELOAD"))
			{
				java.lang.String reload_list = getVariableTableValue(vt,"RELOAD");
				String reload_arr[] = TextUtils.toStringArray(TextUtils.getWords(reload_list,"|"));
				if (reload_arr.length > 1)
				{
					out.write("<reload time=\""+reload_arr[0]+"\">"+vt.EncodeXML(vt.parseURLString(reload_arr[1]))+"</reload>\n");
				}
			}
			if (existVariableTableValue(vt, "LAYOUT"))
			{
				java.lang.String layout_list = getVariableTableValue(vt,"LAYOUT");
				String layout_arr[] = TextUtils.toStringArray(TextUtils.getWords(layout_list,"|"));
				if (layout_arr.length > 0)
				{
				    out.write("<layout>\n");
                                    for(i=0; i< layout_arr.length; i++)
				    {
					out.write("<column id=\""+i+"\">"+layout_arr[i]+"</column>\n");
				    }
				    out.write("</layout>\n");
				}
			}
			if (existVariableTableValue(vt, "TREEJS"))
			{
				out.write("<tree>\n");
				out.write(vt.EncodeXML(getVariableTableValue(vt,"TREEJS")));
				out.write("\n\n");				
				out.write("</tree>\n");				
			}
			if (existVariableTableValue(vt, "TREE"))
			{
				DBRowCache treedata = new SimpleDBRowCache();
				treedata.addColumn("ID", java.sql.Types.INTEGER);
				treedata.addColumn("PID", java.sql.Types.INTEGER);
				treedata.addColumn("NAME", java.sql.Types.VARCHAR);
				treedata.addColumn("URL", java.sql.Types.VARCHAR);
				treedata.addColumn("TITLE", java.sql.Types.VARCHAR);
				treedata.addColumn("TARGET", java.sql.Types.VARCHAR);
				treedata.addColumn("ICON", java.sql.Types.VARCHAR);
				treedata.addColumn("ICONOPEN", java.sql.Types.VARCHAR);

				java.io.BufferedReader fin = new java.io.BufferedReader(new java.io.StringReader(getVariableTableValue(vt,"TREE")));
				treedata.read(fin, ",", 5000);
				fin.close();				

				if (treedata.getRowCount() > 0)
				{
					StringBuffer treescript = new StringBuffer();
					if (treedata.getRowCount() > 0)
					{
					    Object nodeval;
					    java.util.Vector<String> treeargs = new java.util.Vector<String>();
					    treeargs.add("NAME");
					    treeargs.add("URL");
					    treeargs.add("TITLE");
					    treeargs.add("TARGET");
					    treeargs.add("ICON");
					    treeargs.add("ICONOPEN");

					    treescript.append("dt");
					    treescript.append(" = new dTree('dt");
					    treescript.append("');\n");
				            for(int j = 1; j <= treedata.getRowCount(); j++)
					    {
					         treescript.append("dt");
					         treescript.append(".add(");
						 nodeval = treedata.getItem(j, "ID");
						 if (nodeval != null)
						     treescript.append(nodeval.toString());
						 else
						     treescript.append("0");
						 nodeval = treedata.getItem(j, "PID");
						 if (nodeval != null)
						 {
						     treescript.append(",");						  
						     treescript.append(nodeval.toString());
						 }
						 else
						 {
						     treescript.append(",-1");						  
						 }
						 for (int k=0; k< treeargs.size(); k++)
						 {
						     nodeval = treedata.getItem(j, treeargs.elementAt(k));
						     if (nodeval != null)
						     {
						         treescript.append(",");
						         treescript.append("'");
						         treescript.append(vt.parseString(nodeval.toString()));
						         treescript.append("'");
						     }
						     else
						     {
						         treescript.append(",''");
						     }
						 }
					         treescript.append(");\n");
					    }
					    treescript.append("document.write(dt");
					    treescript.append(");\n");					    
					    treescript.append("\n");					    
				   	    out.write("<tree>\n");
					    out.write(vt.EncodeXML(treescript.toString()));
				   	    out.write("</tree>\n");
					}
				}
			}
			java.util.Vector<String> v_urls = new java.util.Vector<String>();
			v_urls.add("URLS");
			v_urls.add("URLS2");
			v_urls.add("URLS3");
			v_urls.add("URLS4");
			v_urls.add("URLS5");
			for(int vi=0; vi < v_urls.size(); vi ++)
			{
			    if (existVariableTableValue(vt, v_urls.elementAt(vi)))
			    {
				java.lang.String url_list = getVariableTableValue(vt,v_urls.elementAt(vi));
				String url_arr[] = TextUtils.toStringArray(TextUtils.getLines(url_list));
				if (url_arr.length > 0)
				{
				   out.write("<urls>\n");
				   for(i=0; i< url_arr.length; i++)
				   {
					String url_words[] = TextUtils.toStringArray(TextUtils.getWords(url_arr[i],"|"));
					if (url_words.length > 1)
					{
						out.write("<url id=\""+url_words[0]+"\" ");
						if (url_words.length>2)  out.write(url_words[2]);
						out.write(">");
						out.write(vt.EncodeXML(vt.parseURLString(url_words[1])));
						out.write("</url>\n");
					}
				   }
				   out.write("</urls>\n");
				}
			    }
			}
			if (existVariableTableValue(vt, "TOPURLS"))
			{
				java.lang.String url_list = getVariableTableValue(vt,"TOPURLS");
				String url_arr[] = TextUtils.toStringArray(TextUtils.getLines(url_list));
				if (url_arr.length > 0)
				{
				   out.write("<topurls>\n");
				   for(i=0; i< url_arr.length; i++)
				   {
					String url_words[] = TextUtils.toStringArray(TextUtils.getWords(url_arr[i],"|"));
					if (url_words.length > 1)
					{
						out.write("<url id=\""+url_words[0]+"\" ");
						if (url_words[0].equalsIgnoreCase(getVariableTableValue(vt,"TOPCURR"))) out.write(" cur=\"yes\" ");
						if (url_words.length>2)  out.write(url_words[2]);
						out.write(">");
						out.write(vt.EncodeXML(vt.parseURLString(url_words[1])));
						out.write("</url>\n");
					}
				   }
				   out.write("</topurls>\n");
				}
			}
			if (existVariableTableValue(vt, "LEFTURLS"))
			{
				java.lang.String url_list = getVariableTableValue(vt,"LEFTURLS");
				String url_arr[] = TextUtils.toStringArray(TextUtils.getLines(url_list));
				if (url_arr.length > 0)
				{
				   out.write("<lefturls>\n");
				   for(i=0; i< url_arr.length; i++)
				   {
					String url_words[] = TextUtils.toStringArray(TextUtils.getWords(url_arr[i],"|"));
					if (url_words.length > 1)
					{
						out.write("<url id=\""+url_words[0]+"\" ");
						if (url_words[0].equalsIgnoreCase(getVariableTableValue(vt,"LEFTCURR"))) out.write(" cur=\"yes\" ");
						if (url_words.length>2)  out.write(url_words[2]);
						out.write(">");
						out.write(vt.EncodeXML(vt.parseURLString(url_words[1])));
						out.write("</url>\n");
					}
				   }
				   out.write("</lefturls>\n");
				}
			}
			java.util.Vector<String> v_inputs = new java.util.Vector<String>();
			v_inputs.add("INPUTS");
			v_inputs.add("INPUTS2");
			v_inputs.add("INPUTS3");
			v_inputs.add("INPUTS4");
			v_inputs.add("INPUTS5");
			for(int vi = 0; vi < v_inputs.size(); vi ++)
			{
			    if (existVariableTableValue(vt, v_inputs.elementAt(vi)))
			    {
				java.lang.String input_list = getVariableTableValue(vt,v_inputs.elementAt(vi));
				String input_arr[] = TextUtils.toStringArray(TextUtils.getLines(input_list));
				if (input_arr.length > 0)
				{
				   out.write("<inputs action=\"" + vt.EncodeXML(vt.parseURLString("${REQUEST.FILE}")) + "\">\n");
				   for(i=0; i< input_arr.length; i++)
				   {
					String input_words[] = TextUtils.toStringArray(TextUtils.getWords(input_arr[i],"|"));
					if (input_words.length > 2)
					{
                                                if ("CUSTOM".equalsIgnoreCase(input_words[0]))
                                                {
						    out.write("<item type=\""+input_words[0]+"\" name=\""+
							      input_words[1]+"\" value=\""+vt.EncodeXML(input_words[2])+
							      "\" ");
						    out.write(" />\n");
                                                }
                                                else if ("option".equalsIgnoreCase(input_words[0]))
                                                {
						    String select_option_value = vt.parseString(input_words[2]);
						    out.write("<item type=\""+input_words[0]+"\" name=\""+
							      input_words[1]+"\" value=\""+vt.EncodeXML(select_option_value)+"\" ");
                                                    if (input_words.length > 4)
                                                       out.write(" label=\""+vt.EncodeXML(input_words[4])+"\" ");
                                                    out.write(">\n");
                                                    if (input_words.length > 3)
                                                    {
                                                        String select_values[] = TextUtils.toStringArray(TextUtils.getWords(input_words[3],";"));
                                                        for(int svi = 0; svi < select_values.length; svi ++)
                                                        {
                                                            if (select_values[svi].equals(select_option_value))
                                                                out.write("<option selected=\"1\">"+select_values[svi]+"</option>");
                                                            else
                                                                out.write("<option>"+select_values[svi]+"</option>\n");
                                                        }
                                                    }
						    out.write("</item>\n");
                                                }
                                                else if ("checkbox".equalsIgnoreCase(input_words[0]))
                                                {
						    String select_option_value = vt.parseString(input_words[2]);
						    out.write("<item type=\""+input_words[0]+"\" name=\""+
							      input_words[1]+"\" value=\""+vt.EncodeXML(select_option_value)+"\" ");
                                                    out.write(" label=\""+vt.EncodeXML(select_option_value)+"\" ");
                                                    if (input_words.length > 3)
                                                    {
							String field_sep = ",";
		                                        if (vt.exists("WEBCHART.SEP"))
                                                        {
                                                             if ("\\N".equalsIgnoreCase(vt.getString("WEBCHART.SEP")))
                         					   field_sep = "\n";
                                                             else
       					                           field_sep = vt.getString("WEBCHART.SEP");
                                                        }
                                                        String select_values[] = TextUtils.toStringArray(TextUtils.getWords(vt.parseString(input_words[3]),field_sep));
                                                        for(int svi = 0; svi < select_values.length; svi ++)
                                                        {
                                                            if (select_values[svi].equals(select_option_value))
	                                                        out.write(" checked=\"yes\" ");
                                                        }
                                                    }
                                                    out.write(" />\n");
                                                }
                                                else
                                                {
						    out.write("<item type=\""+input_words[0]+"\" name=\""+
							      input_words[1]+"\" value=\""+vt.EncodeXML(vt.parseString(input_words[2]))+
							      "\" ");
						    if (input_words.length > 3) out.write(input_words[3]);
                                                    if (input_words.length > 4)
                                                       out.write(" label=\""+vt.EncodeXML(input_words[4])+"\" ");
						    out.write(" />\n");
                                                }
					}
				   }
				   out.write("</inputs>\n");
				}
			    }
			}
			vt.writeXMLBody(out);
		}
		if (existVariableTableValue(vt, "EXPIRE"))
		{
		    if (DateOperator.getDay().compareTo(getVariableTableValue(vt,"EXPIRE")) > 0) pageexpire = true;
		}
		if (querylist.size()>0)
		{
			crosstab = DBOperation.getDBRowCache();
			for(i=0;i<querylist.size();i++)
			{	
			    /* crosstab = DBOperation.getDBRowCache();  */
			    iscrosstab = getVariableTableValue(vt, "CROSSTAB", querylist.elementAt(i), true);
			    chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i),false);
			    joindata   = getVariableTableValue(vt, "JOIN", querylist.elementAt(i),false);
			    summarydata   = getVariableTableValue(vt, "SUMMARY", querylist.elementAt(i),false);
			    loadmodule = getVariableTableValue(vt, "MODULE", querylist.elementAt(i),false);
			    querycache = getVariableTableValue(vt, "QUERYCACHE", querylist.elementAt(i),false);
			    head_formater = getVariableTableValue(vt, "HEADHTML", querylist.elementAt(i),false);
			    data_formater = getVariableTableValue(vt, "DATAHTML", querylist.elementAt(i),false);
			    row_color = getVariableTableValue(vt, "ROWCOLOR", querylist.elementAt(i),true);
			    row_style = getVariableTableValue(vt, "STYLE", querylist.elementAt(i),true);
			    row_align = getVariableTableValue(vt, "ALIGN", querylist.elementAt(i),true);
			    querycachetime = getint(getVariableTableValue(vt, "QUERYCACHETIME", querylist.elementAt(i),true),300);
			    charttype = getVariableTableValue(vt, "TYPE", querylist.elementAt(i),true);
			    varlist   = vt.parseString(getVariableTableValue(vt, "VARLIST", querylist.elementAt(i),true));
			    pagecount = getVariableTableValue(vt, "PAGES", querylist.elementAt(i),true);
			    dbname = getVariableTableValue(vt, "DBNAME", querylist.elementAt(i),true);
			    dbrule = getVariableTableValue(vt, "DBID", querylist.elementAt(i),true);
			    readdata = getVariableTableValue(vt, "DATA", querylist.elementAt(i),false);
			    readmile = getVariableTableValue(vt, "MILE", querylist.elementAt(i),true);
			    groovydbname = getVariableTableValue(vt, "GROOVYDBNAME", querylist.elementAt(i),true);
			    groovydbrule = getVariableTableValue(vt, "GROOVYDBID", querylist.elementAt(i),true);
			    ignsqlerror = getVariableTableValue(vt, "IGNORE_SQLERROR", querylist.elementAt(i),true);
			    ignmarkdown = getVariableTableValue(vt, "IGNORE_MARKDOWN", querylist.elementAt(i),true);
			    lay_out = getVariableTableValue(vt, "LAYOUT", querylist.elementAt(i),false);
			    lay_style = getVariableTableValue(vt, "LAYOUTSTYLE", querylist.elementAt(i),false);
			    tablename = getVariableTableValue(vt, "TABLE", querylist.elementAt(i),false);
			    columnlist   = getVariableTableValue(vt, "COLUMN", querylist.elementAt(i),false);
			    columneditor = getVariableTableValue(vt, "EDITOR", querylist.elementAt(i),false);
			    columnstyle = getVariableTableValue(vt, "EDITORSTYLE", querylist.elementAt(i),false);
			    columnvalues= getVariableTableValue(vt, "VALUES", querylist.elementAt(i),false);
			    querymaxrows = getint(getVariableTableValue(vt, "MAXROWS", querylist.elementAt(i),true),5000);
	   		    chartycolumn = vt.parseString(getVariableTableValue(vt,"YCOL",querylist.elementAt(i),true));

			    if (lay_out == null) lay_out = "0";

			    foreach = vt.parseString(getVariableTableValue(vt, "FORALL", querylist.elementAt(i),false));
				
			    foreachlist.removeAllElements();
			    if (foreach != null)
				foreachlist.addAll(TextUtils.getLines(foreach));
			    
			    for(int forj=0; forj < (foreachlist.size() > 0 ? foreachlist.size() : 1); forj++)
			    {
				if (forj < foreachlist.size())
				{
				  if (foreachlist.get(forj) == null ||
				    foreachlist.get(forj).trim().length() == 0)
				    continue;
				  vt.setValue(foreachlist.get(forj));
				}

				/*
				if (loadmodule != null)
				{
					VariableTable newvt = readModuleConfig(loadmodule, vt);
					generateChart(out, imgout, newvt, fileextention);
					continue;
				}
				*/

				if (!"-".equals(chartquery) || readdata != null || readmile != null)
				{
				    crosstab = DBOperation.getDBRowCache();
				    if (querycache != null)
				    {
					// DataCache.clearData();
					crosstab = joindatacache.get(vt.parseString(querycache));
				    }
				    if (crosstab == null || crosstab.getColumnCount() == 0)
				    {
				      if (!pageexpire && checkVariableList(varlist,vt))
			    	      {
				         for(int dsloop=0; dsloop < 2; dsloop ++)
				         {
				           try {
                                             /*
					     if (groovydbname != null || groovydbrule != null)
                                             {
                                                 DBGroovyScript dbgroovy = new DBGroovyScript();
                                                 if (groovydbname != null) dbname = String.valueOf(dbgroovy.getValue(vt, groovydbname));
                                                 if (groovydbrule != null) dbrule = String.valueOf(dbgroovy.getValue(vt, groovydbrule));
                                             }
                                             */

					     if (readdata == null && readmile == null)
					     {
					       if (dbname != null && (dbname.startsWith("url::") || dbname.startsWith("URL::")))
					       {
						   try {
				                       db = new DBPooledConnection(DBOperation.getConnection(dbname.substring(5)));
						   } catch (java.lang.ClassNotFoundException cnfe) { throw new java.io.IOException(cnfe.getMessage()); };
					       }
					       else
				                   db = DBLogicalManager.getPoolConnection(vt.parseString(dbname), dbrule);

  					       if (tablename != null && tablename.length() > 0 && columnlist != null && columnlist.length() > 0 && checkVariableList(columnlist,vt))
					       {
						  try {
							if (vt.getString("sqleditmode").equalsIgnoreCase("INSERT"))
							{
							    DBOperation.executeUpdate(db, SQLCreator.getInsertSQL(tablename, columnlist), vt);
	 						    try { db.commit(); } catch (java.sql.SQLException sqlecmt) {}
							}
							else if (vt.getString("sqleditmode").equalsIgnoreCase("UPDATE"))
							{
							    DBOperation.executeUpdate(db, SQLCreator.getUpdateSQL(tablename, columnlist), vt);
	 						    try { db.commit(); } catch (java.sql.SQLException sqlecmt) {}
							}
							else if (vt.getString("sqleditmode").equalsIgnoreCase("DELETE"))
							{
							    DBOperation.executeUpdate(db, SQLCreator.getDeleteSQL(tablename, columnlist), vt);
	 						    try { db.commit(); } catch (java.sql.SQLException sqlecmt) {}
							}
						  }
						  catch (java.sql.SQLException sqle)
						  {
						     sqle.printStackTrace();
						     if (db != null) { db.close(); db = null;}
						     throw new java.io.IOException(sqle.getMessage()); 
						  }
					       }
    					       chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i),false);
					       if (chartquery.equalsIgnoreCase("*"))
					       {
						  chartquery = getVariableTableValue(vt, "QUERY_"+db.getDBTag(), querylist.elementAt(i),false);
					       }
					       java.util.Vector<String> cross_fields = TextUtils.getWords(iscrosstab,"|");
					       if (cross_fields.size()==0)
						  crosstab = DBOperation.executeQuery(db,chartquery,vt);
					       else if(cross_fields.size() < 3)
						  crosstab = DBOperation.executeCrossTab(db,chartquery,vt);
					       else
						  crosstab = DBOperation.executeCrossTab(db,chartquery,vt,
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(0),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(1),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(2),",")));
                                             }
                                             else if (readdata != null)
                                             {
						String colarrs[] = null;
					        String readarr[] = TextUtils.toStringArray(TextUtils.getLines(readdata));
					        for(int tmpk=0; tmpk < readarr.length; tmpk++)
					        {
						    colarrs = TextUtils.toStringArray(TextUtils.getWords(readarr[tmpk]));
						    if (colarrs != null && colarrs.length > 0)
						    {
						        if ("ADD".equalsIgnoreCase(colarrs[0]))
						        {
						            if (colarrs.length > 2)
							       crosstab.addColumn(colarrs[1], SQLTypes.getTypeID(colarrs[2]));
						            else if (colarrs.length > 1)
							       crosstab.addColumn(colarrs[1], java.sql.Types.VARCHAR);
						        }
							else if ("LOAD".equalsIgnoreCase(colarrs[0]))
							{
							    if (colarrs.length > 2)
							    {
								try {
							            java.io.BufferedReader fin = new java.io.BufferedReader(new java.io.FileReader(colarrs[1]));
								    crosstab.read(fin, colarrs[2], 5000);
							            fin.close();
								} catch (java.io.IOException ioe) {}
							    }
							    else
							    {
								try {
							            java.io.BufferedReader fin = new java.io.BufferedReader(new java.io.FileReader(colarrs[1]));
								    crosstab.read(fin, ",", 5000);
							            fin.close();
								} catch (java.io.IOException ioe) {}							        
							    }		
							}
						    }
						}						
                                             }
					     else if (readmile != null)
					     {
						 crosstab = WebChartMileClient.executeQuery(readmile, chartquery,vt);
					         java.util.Vector<String> cross_fields = TextUtils.getWords(iscrosstab,"|");
					         if (cross_fields.size()==0)
						     crosstab = WebChartMileClient.executeQuery(readmile, chartquery,vt);
					         else if(cross_fields.size() < 3)
						     crosstab = WebChartMileClient.executeCrossTab(readmile, chartquery,vt);
					         else
						     crosstab = WebChartMileClient.executeCrossTab(readmile, chartquery,vt,
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(0),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(1),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(2),",")));

					     }
					     express = getVariableTableValue(vt, "EXPRESS", querylist.elementAt(i),true);
					     if (express != null)
					     {
					       String colname;
					       String expstr;
					       String expcols[] = null;
					       String colarrs[] = null;
					       java.util.Vector<String> expwords;
					       String exparr[] = TextUtils.toStringArray(TextUtils.getLines(express));
					       for(int tmpk=0; tmpk < exparr.length; tmpk++)
					       {
					    	  expwords = TextUtils.getWords(exparr[tmpk],"|");
					    	  if (expwords.size() == 3)
					    	  {
					    		colname = expwords.elementAt(0);
					    		expstr  = expwords.elementAt(1);
					    		expcols = TextUtils.toStringArray(TextUtils.getWords(expwords.elementAt(2),","));
							crosstab.addExpression(vt.parseString(colname), expstr, expcols);
						  }
						  else if (expwords.size() == 2)
					    	  {
					    		colname = expwords.elementAt(0);
					    		expstr  = expwords.elementAt(1);
							crosstab.addExpression(vt.parseString(colname), expstr);
						  }
					       }
					     }

					     if (joindata != null)
					     {
						  DBRowCache data_for_join = null;
						  String joinsarr[] = TextUtils.toStringArray(TextUtils.getLines(joindata));
						  for (int tmpk = 0; tmpk < joinsarr.length; tmpk++)
						  {
						      String joinarr[] = TextUtils.toStringArray(TextUtils.getWords(joinsarr[tmpk],"|"));
						      if (joinarr != null && joinarr.length >= 2 && (data_for_join = joindatacache.get(joinarr[0])) != null)
						      {
							  crosstab.joinData(data_for_join, joinarr[1]);
							  // DataCache.removeData(joinarr[0]);
						      }
						  }
					     }

					     if (summarydata != null)
					     {
						  DBRowCache data_for_summary = null;
						  String summaryarr[] = TextUtils.toStringArray(TextUtils.getWords(summarydata,"|"));
						  if (summaryarr.length == 2)
						  {
						      data_for_summary = crosstab.groupData(TextUtils.toStringArray(TextUtils.getWords(summaryarr[0],",")),
										   TextUtils.toStringArray(TextUtils.getWords(summaryarr[1],",")));
						  }
						  if (data_for_summary.getColumnCount() > 0)
						  {
							crosstab = data_for_summary;
						  }
					     }

                                             /*
					     express = getVariableTableValue(vt, "GROOVYEXPRESS", querylist.elementAt(i),true);
					     if (express != null)
					     {
					       String colname;
					       String expstr;
					       String expcols[] = null;
					       String colarrs[] = null;
					       java.util.Vector<String> expwords;
					       String exparr[] = TextUtils.toStringArray(TextUtils.getLines(express));
					       for(int tmpk=0; tmpk < exparr.length; tmpk++)
					       {
					    	  expwords = TextUtils.getWords(exparr[tmpk],"|");
					    	  if (expwords.size() > 1)
					    	  {
					    		colname = expwords.elementAt(0);
					    		expstr  = expwords.elementAt(1);
                                                        crosstab.addGroovyExpression(colname, expstr);
						  }
					       }
					     }
                                             */
					     express = getVariableTableValue(vt, "FILTER", querylist.elementAt(i),true);
					     if (express != null)
					     {
					       String expstr;
					       String expcols[] = null;
					       String exparr[] = TextUtils.toStringArray(TextUtils.getLines(express));
					       for(int tmpk=0; tmpk < exparr.length; tmpk++)
						   crosstab.expressFilter(exparr[tmpk]);
					     }
					     if (querycache!= null)
					     {
						joindatacache.put(vt.parseString(querycache), crosstab);
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
				   }
				}
				if (db != null) { db.close(); db = null; }

				
				String chartlabel = vt.parseString(getVariableTableValue(vt, "LABEL", querylist.elementAt(i),false));
				java.util.Vector<String> label = TextUtils.getFields(chartlabel,"|");
				for(int j=0;j<label.size() && j<crosstab.getColumnCount();j++)
				{
					if (label.elementAt(j) != null)
						crosstab.setColumnLabel(j+1,label.elementAt(j));
				}

				String superchartlabel = vt.parseString(getVariableTableValue(vt, "SUPER", querylist.elementAt(i),false));
				java.util.Vector<String> superlabel = TextUtils.getFields(superchartlabel,"|");
				for(int j=0;j<superlabel.size() && j<crosstab.getColumnCount();j++)
				{
					if (superlabel.elementAt(j) != null)
						crosstab.setColumnSuperLabel(j+1,superlabel.elementAt(j));
				}

				rotatedata = getVariableTableValue(vt, "ROTATE", querylist.elementAt(i),true);
				if ("YES".equalsIgnoreCase(rotatedata) || "ON".equalsIgnoreCase(rotatedata))
				{
				    DBRowCache newdata = crosstab.rotate();
				    crosstab = newdata;
				}

				if (head_formater != null) crosstab.setStringProperty("HEADFORMATER",head_formater);
				if (data_formater != null) crosstab.setStringProperty("DATAFORMATER",data_formater);
				if (columnlist != null) crosstab.setStringProperty("PRIMARYKEY",columnlist);
				if (columneditor != null) crosstab.setStringProperty("COLUMNEDITOR",columneditor);
				if (columnstyle != null) crosstab.setStringProperty("EDITORSTYLE",columnstyle);
				if (columnvalues != null) crosstab.setStringProperty("COLUMNVALUES",columnvalues);
				if (row_color != null) crosstab.setStringProperty("ROWCOLOR",row_color);
				if (row_style != null) crosstab.setStringProperty("ROWSTYLE",row_style);
				if (row_align != null) crosstab.setStringProperty("COLUMNALIGN",row_align);

				if (crosstab.getRowCount()==1)
				{
					for(int j=1;j<=crosstab.getColumnCount();j++)
					{
						vt.add("QUERY_"+querylist.elementAt(i)+"."+crosstab.getColumnName(j),
							java.sql.Types.VARCHAR);
						vt.setValue("QUERY_"+querylist.elementAt(i)+"."+crosstab.getColumnName(j),
							crosstab.getItem(1,j));
					}
				} 
				
				if ("SET".equalsIgnoreCase(charttype))
				{
					vt.add("ARRAY."+querylist.elementAt(i), java.sql.Types.VARCHAR);
					vt.setValue("ARRAY."+querylist.elementAt(i), crosstab.getFullText());
				}
				else if ("CACHE".equalsIgnoreCase(charttype))
				{
					// Do nothing.
				}
				else if ("TREE".equalsIgnoreCase(charttype))
				{
					StringBuffer treescript = new StringBuffer();
					if (crosstab.getRowCount() > 0)
					{
					    Object nodeval;
					    java.util.Vector<String> treeargs = new java.util.Vector<String>();
					    treeargs.add("NAME");
					    treeargs.add("URL");
					    treeargs.add("TITLE");
					    treeargs.add("TARGET");
					    treeargs.add("ICON");
					    treeargs.add("ICONOPEN");

					    treescript.append("dt");
					    treescript.append(querylist.elementAt(i));
					    treescript.append(" = new dTree('dt");
					    treescript.append(querylist.elementAt(i));
					    treescript.append("');\n");
				            for(int j = 1; j <= crosstab.getRowCount(); j++)
					    {
					         treescript.append("dt");
						 treescript.append(querylist.elementAt(i));
					         treescript.append(".add(");
						 nodeval = crosstab.getItem(j, "ID");
						 if (nodeval != null)
						     treescript.append(nodeval.toString());
						 else
						     treescript.append("0");
						 nodeval = crosstab.getItem(j, "PID");
						 if (nodeval != null)
						 {
						     treescript.append(",");						  
						     treescript.append(nodeval.toString());
						 }
						 else
						 {
						     treescript.append(",-1");						  
						 }
						 for (int k=0; k< treeargs.size(); k++)
						 {
						     nodeval = crosstab.getItem(j, treeargs.elementAt(k));
						     if (nodeval != null)
						     {
						         treescript.append(",");
						         treescript.append("'");
						         treescript.append(vt.parseString(nodeval.toString()));
						         treescript.append("'");
						     }
						     else
						     {
						         treescript.append(",''");
						     }
						 }
					         treescript.append(");\n");
					    }
					    treescript.append("document.write(dt");
					    treescript.append(querylist.elementAt(i));
					    treescript.append(");\n");					    
					    treescript.append("\n");					    
				   	    out.write("<tree>\n");
					    out.write(vt.EncodeXML(treescript.toString()));
				   	    out.write("</tree>\n");
					}
				}
				else if ("URL".equalsIgnoreCase(charttype))
				{
					String url_pattern = getVariableTableValue(vt, "URLSTRING", querylist.elementAt(i),false);
					if (url_pattern != null)
					{
						String url_words[] = TextUtils.toStringArray(TextUtils.getWords(url_pattern,"|"));
						if (url_words.length > 1)
						{
				   			out.write("<urls>\n");
				   			for(int j=1; j<=crosstab.getRowCount(); j++)
				   			{
								if (j>1) out.write("<url id=\"-\">,</url>\n");
								out.write("<url id=\""+crosstab.getItem(j,url_words[0])+"\">");
								out.write(vt.EncodeXML(crosstab.parseString(url_words[1], vt,j,1)));
								out.write("</url>\n");
				   			}
				   			out.write("</urls>\n");
						}
					}
				}
				else
				{
				    if (charttype == null || !charttype.equals("-"))
				    {
					if (temprows != null)
					{
						temprows.appendRow(crosstab);
						crosstab = temprows;
					}
					if ("YES".equals(imageonly))
					{
						generateChart(crosstab,out,vt,querylist.elementAt(i), fileextention);
						temprows = null;
						return;
					}
					else
					{
						for(int tmpj=crosstab.getRowCount(); tmpj > querymaxrows; tmpj --)
						{
						     crosstab.deleteRow(tmpj);
						}
						crosstab.setPageSize(0);
						if (pagecount != null)
						{
						     try {
							int pagerows = Integer.valueOf(pagecount).intValue();
							if (pagerows > 1 && pagerows < crosstab.getRowCount()) 
								crosstab.setPageSize((pagerows - 1 + crosstab.getRowCount())/pagerows);
						     } catch (NumberFormatException nfe) {}
						}
						if ("*".equalsIgnoreCase(chartycolumn))
						{
						    for(int tmpi=2;tmpi<=crosstab.getColumnCount();tmpi++)
						    {
							setVariableTableValue(vt,"YCOL_"+querylist.elementAt(i),crosstab.getColumnName(tmpi));
							if (lay_style == null)
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\">\n");
							else
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\" style=\""+vt.EncodeXML(lay_style)+"\">\n");
	
			                                if ((express=getVariableTableValue(vt,"XMLDATA",querylist.elementAt(i),false)) != null)
        			                            out.write(vt.parseString(express));
		        	                        if ((express=getVariableTableValue(vt,"TITLE",querylist.elementAt(i),false)) != null)
        		        	                    out.write("  <title><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></title>\n");
                		        	        if ((express=getVariableTableValue(vt,"SUBTITLE",querylist.elementAt(i),false)) != null)
                        		        	    out.write("  <subtitle><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></subtitle>\n");
	                		                if ((express=getVariableTableValue(vt,"FOOTNOTE",querylist.elementAt(i),false)) != null)
			                                    out.write("  <footnote><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></footnote>\n");
                			                if ((express=getVariableTableValue(vt,"MEMO",querylist.elementAt(i),false)) != null)
		        	                            out.write("  <memo><![CDATA["+ crosstab.parseString(express, vt, 0, 0) +"]]></memo>\n");
							if (crosstab.getColumnCount() > 0)
							    generateChart(crosstab,out,vt,querylist.elementAt(i), fileextention);
							out.write("</webchart>\n");
						    }
						}
						else if (chartycolumn != null && TextUtils.getLines(chartycolumn).size() > 1)
						{
						    String chartycolarr[] = TextUtils.toStringArray(TextUtils.getLines(chartycolumn));
						    for(int tmpi=0;tmpi<chartycolarr.length;tmpi++)
						    {
							if (chartycolarr[tmpi] == null || chartycolarr[tmpi].length() == 0) continue;
							setVariableTableValue(vt,"YCOL_"+querylist.elementAt(i),chartycolarr[tmpi]);
							if (lay_style == null)
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\">\n");
							else
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\" style=\""+vt.EncodeXML(lay_style)+"\">\n");
	
			                                if ((express=getVariableTableValue(vt,"XMLDATA",querylist.elementAt(i),false)) != null)
        			                            out.write(vt.parseString(express));
		        	                        if ((express=getVariableTableValue(vt,"TITLE",querylist.elementAt(i),false)) != null)
        		        	                    out.write("  <title><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></title>\n");
                		        	        if ((express=getVariableTableValue(vt,"SUBTITLE",querylist.elementAt(i),false)) != null)
                        		        	    out.write("  <subtitle><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></subtitle>\n");
	                		                if ((express=getVariableTableValue(vt,"FOOTNOTE",querylist.elementAt(i),false)) != null)
			                                    out.write("  <footnote><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></footnote>\n");
                			                if ((express=getVariableTableValue(vt,"MEMO",querylist.elementAt(i),false)) != null)
		        	                            out.write("  <memo><![CDATA["+ crosstab.parseString(express, vt, 0, 0) +"]]></memo>\n");
							if (crosstab.getColumnCount() > 0)
							    generateChart(crosstab,out,vt,querylist.elementAt(i), fileextention);
							out.write("</webchart>\n");
						    }
						}
						else
						{
							if (lay_style == null)
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\">\n");
							else
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\" style=\""+vt.EncodeXML(lay_style)+"\">\n");
	
			                                if ((express=getVariableTableValue(vt,"XMLDATA",querylist.elementAt(i),false)) != null)
        			                            out.write(vt.parseString(express));
		        	                        if ((express=getVariableTableValue(vt,"TITLE",querylist.elementAt(i),false)) != null)
        		        	                    out.write("  <title><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></title>\n");
                		        	        if ((express=getVariableTableValue(vt,"SUBTITLE",querylist.elementAt(i),false)) != null)
                        		        	    out.write("  <subtitle><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></subtitle>\n");
	                		                if ((express=getVariableTableValue(vt,"FOOTNOTE",querylist.elementAt(i),false)) != null)
			                                    out.write("  <footnote><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></footnote>\n");
                			                if ((express=getVariableTableValue(vt,"MEMO",querylist.elementAt(i),false)) != null)
		        	                            out.write("  <memo><![CDATA["+ crosstab.parseString(express, vt, 0, 0) +"]]></memo>\n");
							if (crosstab.getColumnCount() > 0)
							    generateChart(crosstab,out,vt,querylist.elementAt(i), fileextention);
							out.write("</webchart>\n");
						}
						temprows = null;
					}
				    }
				    else
				    {
					if (crosstab.getColumnCount() > 0)
					{
					   if (temprows==null || temprows.getColumnCount() != crosstab.getColumnCount())
					   {
						temprows = DBOperation.getDBRowCache();
						temprows.copyColumns(crosstab);
					   }
					   temprows.appendRow(crosstab);
					   crosstab = DBOperation.getDBRowCache();
					}
				    }
				}
			    }
			}
		}		
		joindatacache.clear();
	}

	public static final boolean isTimeType(int dtype)
	{
		switch(dtype)
		{
			case java.sql.Types.DATE:
			case java.sql.Types.TIME:
			case java.sql.Types.TIMESTAMP:
			     return true;
		}
		return false;	
	}

	public static final boolean isNumberType(int dtype)
	{
		switch(dtype)
		{
			case java.sql.Types.TINYINT:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.BIGINT:
			case java.sql.Types.FLOAT:
			case java.sql.Types.REAL:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.NUMERIC:			
			case java.sql.Types.DECIMAL:			
			     return true;
		}
		return false;	
	}

	private static void writeChartData(java.io.Writer out, DBRowCache crosstab, String index, String ctype, String xcol, String ycol)
                       throws java.io.IOException
	{
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,","));
		int xcolid = crosstab.findColumn(xcol);
		int ycolid = 0, ycolids[];

		if (ycolumns.length == 0) return;

		if ("PIE".equalsIgnoreCase(ctype))
		{
		     ycolid = crosstab.findColumn(ycolumns[0]);
		     if (xcolid > 0 && ycolid > 0)
		     {
			if (isNumberType(crosstab.getColumnType(ycolid)))
			{
			    if (isNumberType(crosstab.getColumnType(xcolid)))
			    {
				out.write("\nvar myData_");
				out.write(index);
				out.write(" = new Array(");
		                for(int row=1; row <= crosstab.getRowCount(); row ++)
			        {
			            Number xvalue = (Number)(crosstab.getItem(row, xcolid));
			            Number yvalue = (Number)(crosstab.getItem(row, ycolid));
				    if (row > 1) out.write(",");
				    if (row % 10 == 0) out.write("\n    ");
				    if (xvalue != null && yvalue != null)
				    {
					out.write("[");
					out.write(xvalue.toString());
					out.write(",");
					out.write(yvalue.toString());
					out.write("]");
				    }
			        }
				out.write(");\n");
			    }
			    else
			    {
				out.write("\nvar myData_");
				out.write(index);
				out.write(" = new Array(");
		                for(int row=1; row <= crosstab.getRowCount(); row ++)
			        {
			            Object xvalue =         (crosstab.getItem(row, xcolid));
			            Number yvalue = (Number)(crosstab.getItem(row, ycolid));
				    if (row > 1) out.write(",");
				    if (row % 10 == 0) out.write("\n    ");
				    if (xvalue != null && yvalue != null)
				    {
					out.write("['");
					out.write(xvalue.toString());
					out.write("',");
					out.write(yvalue.toString());
					out.write("]");
				    }
			        }
				out.write(");\n");			          
			    }
			    out.write("var myChart_");
			    out.write(index);
			    out.write(" = new JSChart('chart"+index+"', 'PIE');\n");
			    out.write("myChart_");
			    out.write(index);
			    out.write(".setDataArray(myData_"+index+");\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setPieRadius(130);\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setTitleColor('#000');\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setPieUnitsColor('#000');\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setPieValuesFontSize(11);\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setPieValuesColor('#000');\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setTitle('WebChart');\n");	
		    
			    out.write("myChart_");
			    out.write(index);
			    out.write(".draw();\n");			    
			}
		     }
		}
		else if ("BAR".equalsIgnoreCase(ctype))
		{
		     ycolids = new int[ycolumns.length];
		     for(int i=0; i< ycolumns.length; i++)
		     {
		         ycolids[i] = crosstab.findColumn(ycolumns[i]);
		     }
		     if (xcolid > 0 )
		     {
			    out.write("var myChart_");
			    out.write(index);
			    out.write(" = new JSChart('chart"+index+"', 'bar');\n");

			    if (isNumberType(crosstab.getColumnType(xcolid)))
			    {
				for (int col = 0; col < ycolids.length; col ++)
				{
				    out.write("myChart_");
			            out.write(index);
			            out.write(".setDataArray(");	
				    out.write("[");
		                    for(int row=1; row <= crosstab.getRowCount(); row ++)
			            {
			                Number xvalue = (Number)(crosstab.getItem(row, xcolid));
			                Number yvalue = (Number)(crosstab.getItem(row, ycolids[col]));
				        if (row > 1) out.write(",");
				        if (row % 10 == 0) out.write("\n     ");
				        if (xvalue != null && yvalue != null)
				        {
					   out.write("[");
					   out.write(xvalue.toString());
  					   out.write(",");
					   out.write(yvalue.toString());
					   out.write("]");
				       }
			            }
				    out.write("],'");
				    out.write(crosstab.getColumnName(ycolids[col]));
				    out.write("');\n");
				}                   
			    }
			    else
			    {
				for (int col = 0; col < ycolids.length; col ++)
				{
				    out.write("myChart_");
			            out.write(index);
			            out.write(".setDataArray(");	
				    out.write("[");
		                    for(int row=1; row <= crosstab.getRowCount(); row ++)
			            {
			                Object xvalue = (crosstab.getItem(row, xcolid));
			                Number yvalue = (Number)(crosstab.getItem(row, ycolids[col]));
				        if (row > 1) out.write(",");
				        if (row % 10 == 0) out.write("\n     ");
				        if (xvalue != null && yvalue != null)
				        {
					   out.write("['");
					   out.write(xvalue.toString());
  					   out.write("',");
					   out.write(yvalue.toString());
					   out.write("]");
				        }
			            }
				    out.write("],'");
				    out.write(crosstab.getColumnName(ycolids[col]));
				    out.write("');\n");
				}    		          
			    }

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setSize(600,400);\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setBarValues(false);\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setBarOpacity(0.7);\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setBarSpacingRatio(35);\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setBarBorderWidth(0);\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setAxisValuesColor('#408F7F');\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setAxisColor('#5DB0A0');\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setGridOpacity(0.8);\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setGridColor('#B9D7C9');\n");	


			    out.write("myChart_");
			    out.write(index);
			    out.write(".setTitle('WebChart');\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".draw();\n");	
		     }
		}
		else if ("LINE".equalsIgnoreCase(ctype))
		{
		     ycolids = new int[ycolumns.length];
		     for(int i=0; i< ycolumns.length; i++)
		     {
		         ycolids[i] = crosstab.findColumn(ycolumns[i]);
		     }
		     if (xcolid > 0 )
		     {
			    out.write("var myChart_");
			    out.write(index);
			    out.write(" = new JSChart('chart"+index+"', 'line');\n");
			    out.write("myChart_");
			    out.write(index);
			    out.write(".setDataArray(");	

			    if (isNumberType(crosstab.getColumnType(xcolid)))
			    {
				out.write("[");
		                for(int row=1; row <= crosstab.getRowCount(); row ++)
			        {
			            Number xvalue = (Number)(crosstab.getItem(row, xcolid));
				    if (row > 1) out.write(",");
				    if (row % 10 == 0) out.write("\n    ");
				    if (xvalue != null)
				    {
					out.write("[");
					out.write(xvalue.toString());
					for (int col = 0; col < ycolids.length; col ++)
					{
			                   Number yvalue = (Number)(crosstab.getItem(row, ycolids[col]));
  					   out.write(",");
					   out.write(yvalue.toString());
					}                   
					out.write("]");
				    }
			        }
				out.write("]);\n");
			    }
			    else
			    {
				out.write("[");
		                for(int row=1; row <= crosstab.getRowCount(); row ++)
			        {
			            Object xvalue =         (crosstab.getItem(row, xcolid));
				    if (row > 1) out.write(",");
				    if (row % 10 == 0) out.write("\n    ");
				    if (xvalue != null)
				    {
					out.write("['");
					out.write(xvalue.toString());
					out.write("'");
					for (int col = 0; col < ycolids.length; col ++)
					{
			                   Number yvalue = (Number)(crosstab.getItem(row, ycolids[col]));
  					   out.write(",");
					   out.write(yvalue.toString());
					}  
					out.write("]");
				    }
			        }
				out.write("]);\n");			          
			    }

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setSize(600,400);\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setAxisValuesColor('#408F7F');\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setAxisColor('#5DB0A0');\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setGridOpacity(0.8);\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".setGridColor('#B9D7C9');\n");	


			    out.write("myChart_");
			    out.write(index);
			    out.write(".setTitle('WebChart');\n");	

			    out.write("myChart_");
			    out.write(index);
			    out.write(".draw();\n");	
		     }
		}
	}

	private static void generateChart(DBRowCache crosstab,java.io.Writer out,VariableTable vt,String index, String fileextention)
		throws java.io.IOException 
    	{
    		int i;
    		String mapcss="";
 		int width=500,height=400,grpcolcount=2;
	
    		String chartdefaultfont = getVariableTableValue(vt,"defaultfont");
    		String chartdefaultcolor = getVariableTableValue(vt,"defaultcolor");
    		
		String colors = getVariableTableValue(vt,"colors", index, true);
		
		String xmltag = getVariableTableValue(vt,"xmltag", index, true);
		String xmlattr = getVariableTableValue(vt,"xmlattr", index, true);

   		String legendposition    = getVariableTableValue(vt,"legend",index,true);
   		String legendfont        = getVariableTableValue(vt,"legendfont",index,true);
   		String legendcolor       = getVariableTableValue(vt,"legendcolor",index,true);
   		String plotbackcolor    = getVariableTableValue(vt,"plotbackcolor",index,true);
   		String plotedgecolor    = getVariableTableValue(vt,"plotedgecolor",index,true);   		
   		String chartorient    = getVariableTableValue(vt,"orient",index,true);		
    		String backcolor = getVariableTableValue(vt,"backcolor", index, true);
    		String edgecolor = getVariableTableValue(vt,"edgecolor",index, true);
   		String gridline     = getVariableTableValue(vt,"GRID",index,true);
   		String gridstyle     = getVariableTableValue(vt,"GRIDLINE",index,true);
   		String gridcolor     = getVariableTableValue(vt,"GRIDCOLOR",index,true);
    		
    		String charttype = getVariableTableValue(vt,"type", index, true);
    		String chartsubtype = getVariableTableValue(vt,"subtype", index, true);
    		String chartsubtype2 = getVariableTableValue(vt,"subtype2",index, true);
    		
    		String chartwidth = getVariableTableValue(vt,"width",index,true);
    		String chartheight = getVariableTableValue(vt,"height",index,true);
		String chartmargin = getVariableTableValue(vt,"ywidth",index,true);
		
    		String charttitle = vt.parseString(getVariableTableValue(vt,"title",index,false));
    		String charttitlefont = getVariableTableValue(vt,"titlefont",index,true);
    		String charttitlecolor = getVariableTableValue(vt,"titlecolor",index,true);

    		String chartsubtitle = vt.parseString(getVariableTableValue(vt,"subtitle",index,false));
    		String chartsubtitlefont = getVariableTableValue(vt,"subtitlefont",index,true);
    		String chartsubtitlecolor = getVariableTableValue(vt,"subtitlecolor",index,true);

    		String chartfootnote = vt.parseString(getVariableTableValue(vt,"footnote",index,false));
    		String chartfootnotefont = getVariableTableValue(vt,"footnotefont",index,true);
    		String chartfootnotecolor = getVariableTableValue(vt,"footnotecolor",index,true);


   		String charthref    = getVariableTableValue(vt,"href",index,true);
   		String charthreftarget    = getVariableTableValue(vt,"hreftarget",index,true);
   		String chartformater    = getVariableTableValue(vt,"formater",index,true);
   		String headerformater    = getVariableTableValue(vt,"hformater",index,true);
   		String chartexclude    = getVariableTableValue(vt,"exclude",index,true);

    		String stock_open = getVariableTableValue(vt,"stock_open",index,false);
    		String stock_high = getVariableTableValue(vt,"stock_high",index,false);
    		String stock_low = getVariableTableValue(vt,"stock_low",index,false);
    		String stock_close = getVariableTableValue(vt,"stock_close",index,false);
    		String stock_volume = getVariableTableValue(vt,"stock_volume",index,false);

   		String pielabelstyle    = getVariableTableValue(vt,"PIELABEL",index,true);
   		String pielabeldigit    = getVariableTableValue(vt,"PIEDIGIT",index,true);
   		
   		String tooltipcolumn    = getVariableTableValue(vt,"TOOLTIP",index,true);
   		String chartxcolumn    = vt.parseString(getVariableTableValue(vt,"XCOL",index,true));
   		String chartxlabel    = getVariableTableValue(vt,"XLABEL",index,true);
   		String chartxmaxval    = getVariableTableValue(vt,"XMAX",index,true);
   		String chartycolumn    = vt.parseString(getVariableTableValue(vt,"YCOL",index,true));
   		String chartylabel    = getVariableTableValue(vt,"YLABEL",index,true);
   		String chartymaxval    = getVariableTableValue(vt,"YMAX",index,true);
		String chartyformat    = getVariableTableValue(vt,"YFORMAT",index,true);
   		String charty2column    = vt.parseString(getVariableTableValue(vt,"Y2COL",index,true));
   		String charty2maxval    = getVariableTableValue(vt,"Y2MAX",index,true);
		String charty2format    = getVariableTableValue(vt,"Y2FORMAT",index,true);

   		String subchartcolumn    = vt.parseString(getVariableTableValue(vt,"SUBCHARTCOL",index,true));
   		String subchartheight    = getVariableTableValue(vt,"SUBCHARTHEIGHT",index,true);
   		String subcharttype      = getVariableTableValue(vt,"SUBCHARTTYPE",index,true);
   		String subchartsubtype      = getVariableTableValue(vt,"SUBCHARTSUBTYPE",index,true);
   		String subchartsubtype2     = getVariableTableValue(vt,"SUBCHARTSUBTYPE2",index,true);
   		String subchartymaxval     = getVariableTableValue(vt,"SUBCHARTYMAX",index,true);

   		String groupcolumncount     = getVariableTableValue(vt,"GROUP",index,true);
   		String mergecolumnlist     = getVariableTableValue(vt,"MERGE",index,true);
   		String chartimagemap     = getVariableTableValue(vt,"IMAGEMAP",index,true);
   		String sortcolumns     = getVariableTableValue(vt,"SORT",index,true);
   		String collength     = getVariableTableValue(vt,"LENGTH",index,true);
                
   		String charty2label    = getVariableTableValue(vt,"Y2LABEL",index,true);
   		String subchartylabel     = getVariableTableValue(vt,"SUBCHARTLABEL",index,true);

		String foregroundalpha    = getVariableTableValue(vt,"ALPHA",index,true);

		String ycolumn[] = {};
                
		if (sortcolumns != null)
		{
			crosstab.quicksort(TextUtils.toStringArray(TextUtils.getWords(sortcolumns,",")));
		}
				
		if (xmltag == null || xmltag.trim().length() == 0)
		{
			xmltag = "dataset";
		}

		if (charttype == null || charttype.length() == 0)
		{
			charttype="XML";
		}


		if (chartexclude != null)
		{
			String column_exclude[] = TextUtils.toStringArray(TextUtils.getWords(chartexclude,"|"));
			for (i=0;i<column_exclude.length;i++)
			{
				crosstab.setColumnVisible(column_exclude[i], false);	
			}
		}

		try {
			if (groupcolumncount != null)
				grpcolcount = Integer.valueOf(groupcolumncount).intValue();
			if (grpcolcount > crosstab.getColumnCount())
				grpcolcount = crosstab.getColumnCount();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			grpcolcount = 2;
		}

		if (charttype.equalsIgnoreCase("XML") || charttype.equalsIgnoreCase("EDIT"))
		{
			String cust_col_length[]={};
			String merge_columns[]={};
			if (collength != null)
			{
			     cust_col_length = TextUtils.toStringArray(TextUtils.getWords(collength,"|"));
			}
			if (mergecolumnlist != null)
			{
			     merge_columns = TextUtils.toStringArray(TextUtils.getWords(mergecolumnlist,"|"));
			}
			if (charttype.equalsIgnoreCase("EDIT"))
				crosstab.writeXMLBody(out,xmltag,xmlattr,grpcolcount, merge_columns,cust_col_length,vt, true);
			else
				crosstab.writeXMLBody(out,xmltag,xmlattr,grpcolcount, merge_columns,cust_col_length,vt, false);
			return;
		}

		if (chartxcolumn == null || crosstab.findColumn(chartxcolumn) == 0)
			chartxcolumn = crosstab.getColumnName(1);

		if (chartycolumn != null)
		{
			ycolumn = TextUtils.toStringArray(TextUtils.getWords(chartycolumn,"|"));
		}
		else
		{
			if (crosstab.getColumnCount()>1)
			{
				ycolumn = new String[1];
				ycolumn [0] = "";
				for(i = 2; i <= crosstab.getColumnCount() ; i ++)
				{
					if (i == 2)
						ycolumn [0] = crosstab.getColumnName(i);
					else
						ycolumn [0] = ycolumn[0]+","+crosstab.getColumnName(i);
				}
			}
		}
			
		try {
			if (chartwidth != null)
				width = Integer.valueOf(chartwidth).intValue();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			width = 500;
		}
		try {
			if (chartheight != null)
				height = Integer.valueOf(chartheight).intValue();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			height = 400;
		}
		width = (width < 50?50:width);
		width = (width > 1000?1000:width);
		height = (height < 20?20:height);
		height = (height > 800?800:height);

		out.write("<jschart id=\"");
		out.write(index);
		out.write("\">");
		out.write("<![CDATA[");
		writeChartData(out, crosstab, index, charttype, chartxcolumn, ycolumn[0]);
		out.write("]]></jschart>");
		out.flush();    		
    	}    			
}