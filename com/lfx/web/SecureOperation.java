package com.lfx.web;
import com.lfx.db.*;

public class SecureOperation
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
	
	public static void execSecureCheck(java.io.Writer out,VariableTable vt) 
		throws java.io.IOException
    	{
		int i=0,sql_count=0;
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

	        out.write("<html><head><title>SQL Secure Fields Check Utility</title></head><body>\n");
		out.write("<form action=\"");
		out.write(vt.getString("request.file"));
		out.write("\"  method=\"post\">\n");
		out.write("<textarea name=\"q\" cols=\"100\" rows=\"20\">");
		if (vt.getString("Q") != null)
			out.write(vt.EncodeXML(vt.getString("Q")));
		out.write("</textarea>\n");
		out.write("<br />\n");
       		out.write("<input type=\"submit\" value=\"Check\"></input>\n");
       		out.write("</form>\n");
		out.write("<pre>");

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
			for(i=0;i<querylist.size();i++)
			{
			    chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i).toString(),false);
				
			    if (!chartquery.equals("-"))
			    {
					chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i).toString(),false);
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
						if (!has_secure_words && vt.getString("Q") != null)
						{
							out.write(getVariableTableValue(vt,"SECUREMEMO"));
							out.write("\n");
						}
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