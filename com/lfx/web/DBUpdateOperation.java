package com.lfx.web;
import com.lfx.db.*;

public class DBUpdateOperation
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

        private static boolean checkVariableList(String varlist, VariableTable vt)
	{
	    if (varlist == null) return true;
	    java.util.Vector var_req = TextUtils.getWords(varlist,"|");
	    if (var_req.size() == 0) return true;
	    for (int i=0; i< var_req.size(); i++)
	    {
		if (vt.getString(var_req.elementAt(i).toString()) == null) return false;
	    }
            return true;
	}
	
	public static void generateDBQuery(java.io.Writer out,VariableTable vt) 
		throws java.io.IOException
    	{
		int i=0,sql_count=0;
    		String chartquery = null;
		String dbname = null;
		String dbrule = null;
		String foreach = null;
		String dmlkey = null;
		String varlist = null;
		java.util.Vector foreachlist = new java.util.Vector();
		java.util.Vector querylist = new java.util.Vector();

		DBPooledConnection db	= null;

  	        dmlkey = getVariableTableValue(vt,"DMLKEY");

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
			    dbname = getVariableTableValue(vt, "DBNAME", querylist.elementAt(i).toString(),true);
			    dbrule = getVariableTableValue(vt, "DBID", querylist.elementAt(i).toString(),true);
			    varlist   = getVariableTableValue(vt, "VARLIST", querylist.elementAt(i).toString(),true);
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

				if (!chartquery.equals("-") && checkVariableList(varlist,vt))
				{
				    for(int dsloop=0; dsloop < 2; dsloop ++)
				    {
				      try {
				        db = DBLogicalManager.getPoolConnection(vt.parseString(dbname), dbrule);
					chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i).toString(),false);
					if (chartquery.equalsIgnoreCase("*"))
					{
						chartquery = getVariableTableValue(vt, "QUERY_"+db.getDBTag(), querylist.elementAt(i).toString(),false);
					}
					if (vt.getString("DMLKEY") != null && vt.getString("DMLKEY").equalsIgnoreCase(dmlkey))
					{
					   sql_count = DBOperation.executeUpdate(db, chartquery, vt);
					   db.commit();
					   out.write("OK\n");
					}
					else
					{
					   out.write("NO\n");
					}
					if (db != null) { db.close(); db = null; }
					break;
				      }
			 	      catch(java.sql.SQLException sqle)
				      {
					db.checkSQLState(sqle.getSQLState());
				        out.write(String.valueOf(sqle.getErrorCode())+"\n");
					break;
				      }
				      catch(DatabaseMarkdownException dme)
				      {
					if (dsloop == 1)
				           out.write(dme.getMessage());
				      }
				      finally
				      {
					if (db != null) { db.close(); db = null; }
				      }
				    }
				}
				if (db != null) { db.close(); db = null; }
			    }
			}
		}
		out.flush();
		out.close();
	}
}