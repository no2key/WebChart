package com.lfx.web;

import com.lfx.db.*;
import com.alipay.mile.client.*;
import com.alipay.mile.client.result.*;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.HashMap;
import java.util.ArrayList;

public class WebChartMileClient
{
    private static HashMap<String, Stack<ApplationClientImpl>>  _clientds = new HashMap<String, Stack<ApplationClientImpl>>();

    public static DBRowCache executeCrossTab(String mileconn, String  sqlquery, VariableTable vt, String r[], String c[], String v[])
    {
	DBRowCache data = new SimpleDBRowCache();
	data.addCrossTab(executeQuery(mileconn, sqlquery, vt),r,c,v);
	return data;
    }
    public static DBRowCache executeCrossTab(String mileconn, String  sqlquery, VariableTable vt)
    {
	DBRowCache data = new SimpleDBRowCache();
	data.addCrossTab(executeQuery(mileconn, sqlquery, vt),1,2,3);
	return data;
    }
    public static DBRowCache executeQuery(String mileconn, String  sqlquery, VariableTable vt)
    {
	 DBRowCache datarows = new SimpleDBRowCache();

	 Stack<ApplationClientImpl> _pool = _clientds.get(mileconn);
	 if (_pool == null)
	 {
	     _pool = new Stack<ApplationClientImpl>();
	     _clientds.put(mileconn, _pool);
         }
	 ApplationClientImpl mileclient = (_pool.empty()?null:_pool.pop());
	 if (mileclient == null)
	 {
  	     mileclient = new ApplationClientImpl();
	     List<String> serverList = new ArrayList<String>();
	     
	     serverList.addAll(TextUtils.getWords(mileconn,","));
	     mileclient.setMergeServerList(serverList);
	     mileclient.setBossExecutorCount(4);
	     mileclient.setWorkerExecutorCount(4);
	     try  {
	          mileclient.init();
	      } catch (Exception e) { e.printStackTrace(); mileclient = null;}
         }
         if (mileclient != null)
         {
             Object[] params = null;
	     int timeOut = 30000;
	     try {
		 SQLQuery sql_query = SQLConvert.parseSQL(sqlquery,vt);
		 if (sql_query.getParamNames().length > 0)
		 {
		     params = new Object[sql_query.getParamNames().length];
		     for(int i=0;i<params.length;i++)
		     {
			 params[i] = vt.getValue(sql_query.getParamNames()[i]);
		     }
		 }
                 MileQueryResult queryResult = mileclient.preQueryForList(sql_query.getDestSQL(), params,timeOut);
	         if (queryResult != null)
		 {
	            List<Map<String, Object>> resultList = queryResult.getQueryResult();
		    if (resultList.size() > 0)
		    {
			Map rec = resultList.get(0);
			java.util.Set cols = rec.keySet();
			if (cols != null)
			{
			    java.lang.Object colnames[] = cols.toArray();
			    if (colnames.length > 0)
			    {
				for(int i=0;i<colnames.length;i++)
				{
				   if (rec.get(colnames[i]) != null)
				   {
				      if (rec.get(colnames[i]) instanceof String)
					   datarows.addColumn(colnames[i].toString(), java.sql.Types.VARCHAR);
				      else  if (rec.get(colnames[i]) instanceof java.sql.Date)			
					   datarows.addColumn(colnames[i].toString(), java.sql.Types.DATE);
				      else  if (rec.get(colnames[i]) instanceof java.sql.Time)			
					   datarows.addColumn(colnames[i].toString(), java.sql.Types.TIME);
				      else  if (rec.get(colnames[i]) instanceof java.sql.Timestamp)			
					   datarows.addColumn(colnames[i].toString(), java.sql.Types.TIMESTAMP);
				      else  if (rec.get(colnames[i]) instanceof Integer)			
					   datarows.addColumn(colnames[i].toString(), java.sql.Types.INTEGER);
				      else  if (rec.get(colnames[i]) instanceof Float)			
					   datarows.addColumn(colnames[i].toString(), java.sql.Types.FLOAT);
				      else  if (rec.get(colnames[i]) instanceof Double)			
					   datarows.addColumn(colnames[i].toString(), java.sql.Types.DOUBLE);
				      else  			
					   datarows.addColumn(colnames[i].toString(), java.sql.Types.JAVA_OBJECT);
				   }
				   else
				   {
				      datarows.addColumn(colnames[i].toString(), java.sql.Types.JAVA_OBJECT);
				   }
 				}
                            }
			}
	            }
	            for (int i = 0; i < resultList.size(); i++) 
	            {
		       datarows.appendRow();
		       for(int col = 1; col <= datarows.getColumnCount(); col++)
		       {
			   datarows.setItem(i+1, col, resultList.get(i).get(datarows.getColumnName(col)));
                       }
	            }
	         }
	      } catch (Exception e) {  e.printStackTrace();  }
	      _pool.push(mileclient);
         }
         return datarows;
    }
   
    /*
    public static void main(String args[])
    {
	MileClientTest.executeQuery("10.253.104.222:8964", "select CLIENT_IP, USER_ID, GMT_OCCUR from CTU_EVENT_DAILY limit 5");
    }
    */
}