package com.lfx.web;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.lfx.db.*;
import com.lfx.web.*;

public class BaseServlet extends HttpServlet {

    protected javax.servlet.ServletContext Application = null;
    protected static final String WEBCHART_PREFIX = "WEBCHART.";
    protected int    KEEP_CACHE_TIME = 300;
    private   static javax.xml.transform.TransformerFactory transFact =
                javax.xml.transform.TransformerFactory.newInstance();
    private   static java.util.HashMap xsltcache = new java.util.HashMap();
    
    public  void init(javax.servlet.ServletConfig sc)
	 throws javax.servlet.ServletException
    {
	super.init(sc);
	Application = sc.getServletContext();
    }

    public static String getURL(VariableTable vt)
    {
	return getURL(vt,vt.getNames());
    }
    public static String getURL(VariableTable vt,String vars[])
    {
	String val;
	StringBuffer result = new StringBuffer();
	if (vt == null || vars == null || vars.length == 0) return "";
	for(int i=0;i<vars.length;i++)
	{
		if (i>0)
			result.append("&");
		result.append(vars[i]);
		result.append("=");
		val = vt.getString(vars[i]);
		if(val != null)
		{
			result.append(getURLString(val));
		}
	}
	return result.toString();
    }
    public static String getURL(DBRowCache vt,int row)
    {
	Object val;
	StringBuffer result = new StringBuffer();
	if (vt == null || row < 1 || row > vt.getRowCount()) return "";
	for(int i=1;i<=vt.getColumnCount();i++)
	{
		if (i>1)
			result.append("&");
		result.append(vt.getColumnName(i));
		result.append("=");
		val = vt.getItem(row,i);
		if(val != null)
		{
			result.append(getURLString(val.toString()));
		}
	}
	return result.toString();
    }
    public static String getURLString(String url)
    {
	char hex_arr[] = {
		'0','1','2','3','4','5','6','7',
		'8','9','a','b','c','d','e','f'};

	if (url == null || url.length() == 0) return url;

        byte content[] = url.getBytes();

        StringBuffer result = new StringBuffer(content.length * 3);

        for (int i = 0; i < content.length; i++)
	{
		if ((content[i] >= '0' && content[i] <= '9') ||
		    (content[i] >= 'A' && content[i] <= 'Z') ||
		    (content[i] >= 'a' && content[i] <= 'z'))
		    result.append((char) content[i]);
		else
		{
			result.append('%');
			result.append((char)hex_arr[((content[i]+256)%256)/16]);
			result.append((char)hex_arr[((content[i]+256)%256)%16]);
		}
        }
        return (result.toString());
    }
    public static boolean checkAccess(VariableTable vt)
    {
	return checkAccess(vt.getString("SESSION.LOGINID"),
		vt.getString("SESSION.LOGINROLE"),vt);
    }
    public static boolean checkAccess(String username,String rolelist,VariableTable vt)
    {
        java.util.Vector<String> v_role,v_deny,v_allow;
	int i;
	String temp = vt.getString("WEBCHART.SECURITY");
	if (temp == null || !temp.equalsIgnoreCase("TRUE")) return true;
	if (username == null) return false;
        v_role = TextUtils.getWords(rolelist,",");
        v_deny = TextUtils.getWords(vt.getString("WEBCHART.DENY"),"|");
        v_allow = TextUtils.getWords(vt.getString("WEBCHART.ALLOW"),"|");
	if (v_deny.contains(username)) return false;
	for(i=0;i<v_role.size();i++)
	{
		if (v_deny.contains(v_role.elementAt(i))) return false;
	}
        temp = vt.getString("WEBCHART.DEFAULTACCESS");
	if (temp != null && temp.equalsIgnoreCase("ALLOW")) return true;
	if (v_allow.contains(username)) return true;
	for(i=0;i<v_role.size();i++)
	{
		if (v_allow.contains(v_role.elementAt(i))) return true;
	}
	return false;
    }
    public static boolean checkIPAccess(VariableTable vt)
    {
        java.util.Vector<String> v_ipdeny,v_ipallow;
	String v_ip = null;
	int i;
	String temp = vt.getString("WEBCHART.IPSECURITY");
	if (temp == null || !temp.equalsIgnoreCase("TRUE")) return true;
	v_ip = vt.getString("REQUEST.REMOTEADDR");
	if (v_ip == null) return false;
        v_ipdeny = TextUtils.getWords(vt.getString("WEBCHART.IPDENY"),"|");
        v_ipallow = TextUtils.getWords(vt.getString("WEBCHART.IPALLOW"),"|");
	for(i=0;i<v_ipdeny.size();i++)
	{
		if (v_ip.startsWith(v_ipdeny.elementAt(i))) return false;
	}
        temp = vt.getString("WEBCHART.IPACCESS");
	if (temp == null || temp.equalsIgnoreCase("ALLOW")) return true;
	for(i=0;i<v_ipallow.size();i++)
	{
		if (v_ip.startsWith(v_ipallow.elementAt(i))) return true;
	}
	return false;
    }
    public static void  writeXMLHeader(java.io.Writer out,VariableTable vt) throws java.io.IOException
    {
		writeXMLHeader(out,vt,null);
    }

    public static void  writeXMLHeader(java.io.Writer out,VariableTable vt,String xsldoc) throws java.io.IOException
    {
      String db_charset="gb2312";
      if (vt.exists("WEBCHART.DB_CHARSET"))
      {
	  db_charset=vt.getString("WEBCHART.DB_CHARSET");
      }
      if (db_charset != null)
	      out.write("<?xml version='1.0' encoding='"+db_charset+"'?>\n");
      else
	      out.write("<?xml version='1.0'?>\n");
      out.write("<!-- Generated by AnySQL DataReport (WebChart), Version 4.0.0 (2009-12-04) -->\n");
      out.write("<!-- Copyright reserved, Lou Fangxin (AnySQL.net) 2003-2009 -->\n");
      if (xsldoc != null && xsldoc.length()>0)
      { 
   	  out.write("<?xml-stylesheet type='text/xsl' href='");
	  out.write(xsldoc+"'?>\n");
      }
    }

    public static String getAbsolutePath(HttpServletRequest request,String url)
    {
	int pos;
	if (url == null) return null;
	if (url.startsWith("/")) return url;
	pos = request.getServletPath().lastIndexOf("/");
	return request.getServletPath().substring(0,pos+1)+url;
    }

    public static String getClientIPAddr(HttpServletRequest request)
    {
        String remoteAddr = request.getRemoteAddr();
        String x;
        if ((x = request.getHeader("X-FORWARDED-FOR")) != null) 
        {
            remoteAddr = x;
            int idx = remoteAddr.indexOf(',');
            if (idx > -1) 
            {
                remoteAddr = remoteAddr.substring(0, idx);
            }
        }
	return remoteAddr;
    }

    public static String getPhysicalPath(HttpServlet servlet,HttpServletRequest request,String url)
    {
	return servlet.getServletContext().getRealPath(getAbsolutePath(request,url));
    }

    public String getPhysicalPath(HttpServletRequest request,String url)
    {
	return getServletContext().getRealPath(getAbsolutePath(request,url));
    }

    public static void getEnv(VariableTable vt,HttpServletRequest request)
    {
        Enumeration e = null;
	HttpSession session = request.getSession(false);

	String db_charset="gb2312";
	String url_charset = null;

	vt.remove("SESSION.LOGINID");
	vt.remove("SESSION.LOGINNAME");
	vt.remove("SESSION.LOGINROLE");

	if (vt.exists("WEBCHART.DB_CHARSET"))
	{
		db_charset=vt.getString("WEBCHART.DB_CHARSET");
	}

	if (vt.exists("WEBCHART.URL_CHARSET"))
	{
		url_charset=vt.getString("WEBCHART.URL_CHARSET");
	}

	if (session != null )
	{
		e =  session.getAttributeNames();
	        while (e.hasMoreElements())
        	{
	            String name = (String)e.nextElement();
        	    Object value = session.getAttribute(name);
	    	    vt.add(name,java.sql.Types.VARCHAR);
		    if (value != null)
			    vt.setValue(name,value.toString());
	        }
		vt.add("SESSION.ID",java.sql.Types.VARCHAR);
		vt.setValue("SESSION.ID",session.getId());
		vt.add("SESSION.CREATE",java.sql.Types.VARCHAR);
		vt.setValue("SESSION.CREATE",DBOperation.toString(new java.util.Date(
			session.getCreationTime()),"yyyy-MM-dd HH:mm:ss"));
		vt.add("SESSION.ACCESS",java.sql.Types.VARCHAR);
		vt.setValue("SESSION.ACCESS",DBOperation.toString(new java.util.Date(
			session.getLastAccessedTime()),"yyyy-MM-dd HH:mm:ss"));
	}
	e = request.getParameterNames();
        while (e.hasMoreElements())
        {
            String name = (String)e.nextElement();
            String value = request.getParameter(name);;
	    String par_values[] = request.getParameterValues(name);
	    name = name.toUpperCase();

	    if (name.startsWith("WEBCHART.") && ! name.equals("WEBCHART.DOCTYPE"))
                continue;
            if (name.startsWith("SESSION.") || name.startsWith("HTML.") || name.startsWith("REQUEST."))
                continue;
    	    vt.add(name,java.sql.Types.VARCHAR);

	    if (par_values != null && par_values.length > 1)
	    {
		StringBuffer temp = new StringBuffer();
		for(int i=0;i<par_values.length;i++)
		{
			if (par_values[i] != null && par_values[i].trim().length()>0)
			{
				if (temp.length()>0)
				{
                                     if (vt.exists("WEBCHART.SEP"))
                                     {
                                        if ("\\N".equalsIgnoreCase(vt.getString("WEBCHART.SEP")))
					   temp.append("\n");
                                        else
					   temp.append(vt.getString("WEBCHART.SEP"));
                                     }
                                     else
					temp.append(",");
				}
				temp.append(par_values[i]);
			}
		}
		value = temp.toString();
	    }	
	    if (url_charset != null)
	    {
               try {
                    value = new String(value.getBytes(url_charset),db_charset);
               } catch (java.io.UnsupportedEncodingException uee) {};
            }
	    vt.setValue(name,value);
	}
	vt.add("REQUEST.REMOTEADDR",java.sql.Types.VARCHAR);
	vt.setValue("REQUEST.REMOTEADDR",getClientIPAddr(request));
	vt.add("REQUEST.REMOTEHOST",java.sql.Types.VARCHAR);
	vt.setValue("REQUEST.REMOTEHOST",request.getRemoteAddr());
	vt.add("REQUEST.REFERER",java.sql.Types.VARCHAR);
	vt.setValue("REQUEST.REFERER", request.getHeader("Referer")); 
	vt.add("REQUEST.QUERYSTRING",java.sql.Types.VARCHAR);
	vt.setValue("REQUEST.QUERYSTRING", request.getQueryString()); 
    }

    protected static void forward(HttpServletResponse response,String htmllink) throws IOException
    {
	response.sendRedirect(response.encodeRedirectURL(htmllink));
	return;
	/*
	response.getWriter().flush();
 	response.flushBuffer();
	*/
   }

    protected static void forward(HttpServletRequest request,HttpServletResponse response,String htmllink) 
	throws IOException,javax.servlet.ServletException
    {
	RequestDispatcher rd = request.getRequestDispatcher(response.encodeRedirectURL(htmllink));
	rd.forward(request, response);
	return;
	/*
	response.getWriter().flush();
	response.flushBuffer();
	*/
    }

    protected static void setHTMLContentType(HttpServletResponse response,VariableTable vt)
    {
	String db_charset="gb2312";
        if (vt.exists("WEBCHART.DB_CHARSET"))
        {
	    db_charset=vt.getString("WEBCHART.DB_CHARSET");
        }
        if (db_charset != null)
	   response.setContentType("text/html;charset="+db_charset);
	else
	   response.setContentType("text/html");
    }

    protected static void setXMLContentType(HttpServletResponse response,VariableTable vt)
    {
	String db_charset="gb2312";
        if (vt.exists("WEBCHART.DB_CHARSET"))
        {
	    db_charset=vt.getString("WEBCHART.DB_CHARSET");
        }
        if (db_charset != null)
  	   response.setContentType("text/xml;charset="+db_charset);
	else
	   response.setContentType("text/xml");
    }

    protected static void setCSSContentType(HttpServletResponse response,VariableTable vt)
    {
	String db_charset="gb2312";
        if (vt.exists("WEBCHART.DB_CHARSET"))
        {
	    db_charset=vt.getString("WEBCHART.DB_CHARSET");
        }
        if (db_charset != null)
  	   response.setContentType("text/plain;charset="+db_charset);
	else
	   response.setContentType("text/plain");
    }

    public static void XML2HTML(java.io.Writer out,java.io.StringReader xmlsrc,java.io.StringReader xslsrc, String xslcontent)
	throws java.io.IOException 
    {

	javax.xml.transform.Templates cachedXSLT = null;
	javax.xml.transform.Transformer trans    = null;
        javax.xml.transform.Source xsltSource = null;

        javax.xml.transform.Source xmlSource =
                new javax.xml.transform.stream.StreamSource(xmlsrc);
        javax.xml.transform.Result result =
                new javax.xml.transform.stream.StreamResult(out);

	if (xsltcache.containsKey(xslcontent))
	{
 	     try {
	        cachedXSLT = (javax.xml.transform.Templates)(xsltcache.get(xslcontent));
	        trans = cachedXSLT.newTransformer();
	        trans.transform(xmlSource, result);
		trans.reset();
	     }
	     catch (javax.xml.transform.TransformerException tfe)
	    {
		System.out.println(tfe.getMessage());
	    }
	}
	else
	{
             xsltSource =  new javax.xml.transform.stream.StreamSource(xslsrc);
	     
 	     try {
		cachedXSLT = transFact.newTemplates(xsltSource);
		xsltcache.put(xslcontent, cachedXSLT);
	        trans = cachedXSLT.newTransformer();
	        trans.transform(xmlSource, result);
		trans.reset();
	     }
	     catch (javax.xml.transform.TransformerException tfe)
	    {
		System.out.println(tfe.getMessage());
	    }
        }
	out.flush();
	xmlsrc.close();
	xslsrc.close();
    }

    public static void XML2HTML(java.io.Writer out,String xmllink,String xsllink)
	throws java.io.IOException 
    {
	javax.xml.transform.Templates cachedXSLT = null;
	javax.xml.transform.Transformer trans    = null;
        javax.xml.transform.Source xsltSource = null;

	java.io.FileInputStream xmlsrc = new java.io.FileInputStream(xmllink);
	java.io.FileInputStream xslsrc = new java.io.FileInputStream(xsllink);

        javax.xml.transform.Source xmlSource =
                new javax.xml.transform.stream.StreamSource(xmlsrc);
        javax.xml.transform.Result result =
                new javax.xml.transform.stream.StreamResult(out);
 
	if (xsltcache.containsKey(xsllink))
	{
 	     try {
	        cachedXSLT = (javax.xml.transform.Templates)(xsltcache.get(xsllink));
	        trans = cachedXSLT.newTransformer();
	        trans.transform(xmlSource, result);
		trans.reset();
	     }
	     catch (javax.xml.transform.TransformerException tfe)
	    {
		System.out.println(tfe.getMessage());
	    }
	}
	else
	{
             xsltSource =  new javax.xml.transform.stream.StreamSource(xslsrc);
	     
 	     try {
		cachedXSLT = transFact.newTemplates(xsltSource);
		xsltcache.put(xsllink, cachedXSLT);
	        trans = cachedXSLT.newTransformer();
	        trans.transform(xmlSource, result);
		trans.reset();
	     }
	     catch (javax.xml.transform.TransformerException tfe)
	    {
		System.out.println(tfe.getMessage());
	    }
        }
	out.flush();
	xmlsrc.close();
	xslsrc.close();
    }

    protected static void writeFile(java.io.Writer out,String file)
    {
	java.io.File xml_include = new java.io.File(file);
	if (xml_include.exists() && xml_include.isFile()
		 && xml_include.canRead() && xml_include.length()<=256 * 1024)
	{
		try {
			java.io.FileReader xml_include_reader = new java.io.FileReader(file);
			char buf[]=new char[(int)xml_include.length()];
			int len = xml_include_reader.read(buf);
			out.write(buf,0,len);
			xml_include_reader.close();
		} catch (java.io.IOException ioe) {}
	}
    }

    protected boolean login(HttpSession session,VariableTable sess_vt) throws ConnectTimeoutException, DatabaseMarkdownException
    {
	boolean result=false;
	session.removeAttribute("SESSION.LOGINID");
	session.removeAttribute("SESSION.LOGINNAME");
	session.removeAttribute("SESSION.LOGINROLE");
	DBPooledConnection db = DBLogicalManager.getPoolConnection(sess_vt.getString("SESSION.ADMINDB"));
	try {
		
		DBRowCache data = DBOperation.executeQuery(db,
			"SELECT LOGIN_ID,LOGIN_NAME,LOGIN_ROLE FROM WEB_USERS "+
			" WHERE LOGIN_ID = :LOGIN_ID AND LOGIN_PASSWD = :LOGIN_PASSWORD ",sess_vt,100);
		if (data.getRowCount() == 1)
		{
			session.setAttribute("SESSION.LOGINID",data.getItem(1,1));
			session.setAttribute("SESSION.LOGINNAME",data.getItem(1,2));
			session.setAttribute("SESSION.LOGINROLE",data.getItem(1,3));
			sess_vt.add("SESSION.LOGINID",java.sql.Types.VARCHAR);
			sess_vt.add("SESSION.LOGINNAME",java.sql.Types.VARCHAR);
			sess_vt.add("SESSION.LOGINROLE",java.sql.Types.VARCHAR);
			sess_vt.setValue("SESSION.LOGINID",data.getItem(1,1));
			sess_vt.setValue("SESSION.LOGINNAME",data.getItem(1,2));
			sess_vt.setValue("SESSION.LOGINROLE",data.getItem(1,3));
			result = true;
			DBOperation.executeUpdate(db,
				"INSERT INTO WEB_SESSION_LOG "+
				" (SESSIONID,CLIENT_IP,LOGIN_ID,LOGIN_NAME,LOGIN_ROLE) "+
				"VALUES"+
				" ( :SESSION.ID , :REQUEST.REMOTEADDR , :SESSION.LOGINID , :SESSION.LOGINNAME , :SESSION.LOGINROLE )",sess_vt);
			DBOperation.executeUpdate(db,
				"INSERT INTO WEB_SESSION "+
				" (SESSIONID,CLIENT_IP,LOGIN_ID,LOGIN_NAME,LOGIN_ROLE) "+
				"VALUES"+
				" ( :SESSION.ID , :REQUEST.REMOTEADDR , :SESSION.LOGINID , :SESSION.LOGINNAME , :SESSION.LOGINROLE )",sess_vt);
		}
		else
		{
			if (sess_vt.getString("LOGIN_ID") != null)
			{
				DBOperation.executeUpdate(db,
					"INSERT INTO WEB_LOGIN_LOG (CLIENT_IP,LOGIN_ID,LOGIN_PASSWD,LOGIN_RESULT) "+
					"VALUES( :REQUEST.REMOTEADDR , :LOGIN_ID , :LOGIN_PASSWORD , 'DENY')",sess_vt);
			}
		}
		db.commit();
	}
	catch (java.sql.SQLException sqle)
	{
	    db.checkSQLState(sqle.getSQLState());
	}
	finally
	{
	    if (db != null) db.close();
	}
	return result;
    }
    
    protected void logout(VariableTable sess_vt) throws ConnectTimeoutException, DatabaseMarkdownException
    {
	DBPooledConnection db = DBLogicalManager.getPoolConnection(sess_vt.getString("SESSION.ADMINDB"));
	try {
		DBOperation.executeUpdate(db,
			"UPDATE WEB_SESSION_LOG SET LOGOUT_TIME=SYSDATE , LOGOUT_ACTION='LOGOUT' "+
			" WHERE SESSIONID = :SESSION.ID AND LOGIN_ID = :SESSION.LOGINID ",sess_vt);		
		DBOperation.executeUpdate(db,
			"DELETE FROM WEB_SESSION "+
			" WHERE SESSIONID = :SESSION.ID AND LOGIN_ID = :SESSION.LOGINID ",sess_vt);		
		db.commit();
	}
	catch (java.sql.SQLException sqle)
	{
		db.checkSQLState(sqle.getSQLState());
	}
	finally
	{
	    if (db != null) db.close();
	}
    }
    
    protected int password(VariableTable sess_vt) throws ConnectTimeoutException, DatabaseMarkdownException
    {
  	int result = 0;
	DBPooledConnection db = DBLogicalManager.getPoolConnection(sess_vt.getString("SESSION.ADMINDB"));
	try {
		result = DBOperation.executeUpdate(db,
			"UPDATE WEB_USERS SET LOGIN_PASSWD= :LOGIN_NEWPASSWORD "+
			" WHERE LOGIN_ID = :LOGIN_ID AND LOGIN_PASSWD = :LOGIN_OLDPASSWORD",sess_vt);		
		db.commit();
	}
	catch (java.sql.SQLException sqle)
	{
		db.checkSQLState(sqle.getSQLState());
	}
	finally
	{
	    if (db != null) db.close();
	}
	return result;
    } 

    protected int logAccess(VariableTable sess_vt) throws ConnectTimeoutException, DatabaseMarkdownException
    {
  	int result = 0;
	if (sess_vt.exists("WEBCHART.LOGSQL") && sess_vt.exists("SESSION.ADMINDB"))
	{
	   DBPooledConnection db = DBLogicalManager.getPoolConnection(sess_vt.getString("SESSION.ADMINDB"));
	   try {
		result = DBOperation.executeUpdate(db,sess_vt.getString("WEBCHART.LOGSQL"),sess_vt);		
		db.commit();
	   } 
	   catch (java.sql.SQLException sqle) { result = -1 ;}
	   finally
	   {
		if (db != null) db.close();
	   }
        }
	return result;
    }

    protected void printFile(PrintWriter out, String fname)
    {
    	String temp=null;
    	out.write("<![CDATA[");
	try {
		java.io.BufferedReader fin = new java.io.BufferedReader(
			new java.io.InputStreamReader
				(new java.io.FileInputStream(fname)));
		while((temp = fin.readLine()) != null)
		{
			out.write(temp);
			out.write("\n");	
		}
		fin.close();
	} catch (java.io.IOException e) {}   
	out.write("]]>"); 
    }

    protected void saveFile(String fname, String content)
    {
	try {
		java.io.PrintWriter fout = new java.io.PrintWriter(new java.io.FileOutputStream(fname));
		fout.print(content);
		fout.close();
	} catch (java.io.IOException e) {}   
    }
    
    protected void listFiles(PrintWriter out, String dirname)
    {
    	java.io.File tempdir = null;
    	tempdir = new java.io.File(dirname);
    	if (tempdir.exists())
    	{
    	    if (!tempdir.isDirectory())
    	    tempdir = tempdir.getParentFile();
    	}
    	if(tempdir.isDirectory() && tempdir.exists())
    	{
    		java.io.File file_list[] = tempdir.listFiles();
    		out.write("<pages>\n");
    		for(int i=0;i<file_list.length;i++)
    		{
    		    if (file_list[i].isFile())
    		    {
    		    	out.write("	<page>"+file_list[i].getName()+"</page>\n");
    		    }
    		}
    		out.write("</pages>\n");
    	}
    }        
    
    protected byte[] getGZIPContent(String src) throws java.io.IOException
    {
    	java.io.ByteArrayOutputStream arrout = new java.io.ByteArrayOutputStream(8192);
    	java.util.zip.GZIPOutputStream gzipout = new java.util.zip.GZIPOutputStream(arrout);
    	gzipout.write(src.getBytes());
    	gzipout.close();
    	arrout.close();
    	return arrout.toByteArray();
    }

    protected byte[] getGZIPContent(byte src[]) throws java.io.IOException
    {
    	java.io.ByteArrayOutputStream arrout = new java.io.ByteArrayOutputStream(8192);
    	java.util.zip.GZIPOutputStream gzipout = new java.util.zip.GZIPOutputStream(arrout);
    	gzipout.write(src);
    	gzipout.close();
    	arrout.close();
    	return arrout.toByteArray();
    }

    protected String concatString(String from, String to)
    {
        StringBuffer buf = new StringBuffer(128);
	if (from != null ) buf.append(from);
        if (to != null) buf.append(to);
        return buf.toString();
    }

    protected int getInt(String val,int def)
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
