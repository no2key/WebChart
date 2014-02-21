package com.lfx.web;

import com.lfx.db.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class WebChartJSPTag extends BodyTagSupport 
{
    private   PageContext pageContext = null;
    private   String pageurl = null;
    private   String pagebody = null;
    private   String dbfsurl = null;



    public void setPageContext(PageContext pc)
    {
        pageContext = pc;
    }

    public void setPage(String url)
    {
        pageurl = url;
    }

    public void setDbfs(String url)
    {
        dbfsurl = url;
    }

    public String getAbsolutePath(String url)
    {
	int pos;
	if (url == null) return null;
	if (url.startsWith("/")) return url;
	HttpServletRequest request = (HttpServletRequest)(pageContext.getRequest());

	pos = request.getServletPath().lastIndexOf("/");
	return request.getServletPath().substring(0,pos+1)+url;
    }

    public String getPhysicalPath(String url)
    {
	return pageContext.getServletContext().getRealPath(getAbsolutePath(url));
    }

    public String getClientIPAddr()
    {
	HttpServletRequest request = (HttpServletRequest)(pageContext.getRequest());
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

    public void getEnv(VariableTable vt)
    {
        Enumeration e = null;
	HttpServletRequest request = (HttpServletRequest)(pageContext.getRequest());
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
	    if (name.equalsIgnoreCase("WEBCHART.SECURITY") ||
		name.equalsIgnoreCase("WEBCHART.DEFAULTACCESS") ||
		name.equalsIgnoreCase("WEBCHART.ALLOW") ||
		name.equalsIgnoreCase("WEBCHART.DENY") ||
		name.equalsIgnoreCase("WEBCHART.IPSECURITY") ||
		name.equalsIgnoreCase("WEBCHART.IPACCESS") ||
		name.equalsIgnoreCase("WEBCHART.IPALLOW") ||
		name.equalsIgnoreCase("WEBCHART.IPDENY") ||
		name.equalsIgnoreCase("WEBCHART.XSLDOC") ||
		name.equalsIgnoreCase("WEBCHART.IMAGEONLY") ||
		name.equalsIgnoreCase("WEBCHART.XMLDATA") ||
		name.equalsIgnoreCase("WEBCHART.LOGSQL") ||
		name.equalsIgnoreCase("WEBCHART.DATATYPE") ||
		name.equalsIgnoreCase("WEBCHART.URLS") ||
		name.equalsIgnoreCase("WEBCHART.TOPURLS") ||
		name.equalsIgnoreCase("WEBCHART.TOPCURR") ||
		name.equalsIgnoreCase("WEBCHART.LEFTURLS") ||
		name.equalsIgnoreCase("WEBCHART.LEFTCURR") ||
		name.equalsIgnoreCase("WEBCHART.INPUTS") ||
		name.equalsIgnoreCase("WEBCHART.CACHE") ||
		name.equalsIgnoreCase("WEBCHART.DATA") ||
		name.equalsIgnoreCase("WEBCHART.CSS") ||
		name.equalsIgnoreCase("WEBCHART.RELOAD") ||
		name.equalsIgnoreCase("WEBCHART.EXPIRE") ||
		name.equalsIgnoreCase("WEBCHART.DMLKEY") ||
		name.equalsIgnoreCase("WEBCHART.ENGINE") ||
		name.equalsIgnoreCase("WEBCHART.EXCELURL") ||
		name.equalsIgnoreCase("WEBCHART.DBID") ||
		name.equalsIgnoreCase("WEBCHART.DBIDSEED") ||
		name.equalsIgnoreCase("WEBCHART.SECUREFIELDS") ||
		name.equalsIgnoreCase("WEBCHART.KEEP_CACHE_IMAGE") ||
		name.equalsIgnoreCase("WEBCHART.KEEP_CACHE_TIME") ||
		name.startsWith("WEBCHART.SECUREMEMO") ||
		name.startsWith("WEBCHART.QUERY_") ||
		name.startsWith("WEBCHART.HEADHTML_") ||
		name.startsWith("WEBCHART.DATAHTML_") ||
		name.startsWith("WEBCHART.VARLIST_") ||
		name.startsWith("WEBCHART.FORALL_") ||
		name.startsWith("WEBCHART.XMLDATA_") ||
		name.startsWith("WEBCHART.TABLE_") ||
		name.startsWith("WEBCHART.COLUMN_") ||
		name.startsWith("SESSION."))
		continue;
	    if (name.startsWith("WEBCHART.") && ! name.equals("WEBCHART.DOCTYPE"))
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
	vt.setValue("REQUEST.REMOTEADDR",getClientIPAddr());
	vt.add("REQUEST.REMOTEHOST",java.sql.Types.VARCHAR);
	vt.setValue("REQUEST.REMOTEHOST",request.getRemoteAddr());
	vt.add("REQUEST.REFERER",java.sql.Types.VARCHAR);
	vt.setValue("REQUEST.REFERER", request.getHeader("Referer")); 
	vt.add("REQUEST.QUERYSTRING",java.sql.Types.VARCHAR);
	vt.setValue("REQUEST.QUERYSTRING", request.getQueryString()); 
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

    public int doAfterBody() throws JspException 
    {
         BodyContent bc = getBodyContent();
         pagebody = bc.getString();
	 return SKIP_BODY;
    }

    public int doEndTag() throws JspException 
    {
      try {
 	 HttpServletRequest request = (HttpServletRequest)(pageContext.getRequest());
	 String file_ext = pageContext.getServletContext().getInitParameter("FileExtention");
	 String dbfs_ext = pageContext.getServletContext().getInitParameter("DatabaseExtention");
	 String db_name = pageContext.getServletContext().getInitParameter("DatabaseName");
	 String db_query = pageContext.getServletContext().getInitParameter("DatabaseQuery");
         JspWriter out = pageContext.getOut();

	 int KEEP_CACHE_TIME = 300;
	 long current_time = System.currentTimeMillis();

         if(pagebody != null || pageurl != null || dbfsurl != null) 
         {
    	 	VariableTable vt = new VariableTable();
		vt.loadContent(FileCache.getFileContent(getPhysicalPath("/global"+file_ext)));
		vt.loadContent(FileCache.getFileContent(getPhysicalPath("default"+file_ext)));
		if (pageurl != null ) vt.loadContent(FileCache.getFileContent(getPhysicalPath(pageurl)));
		if (dbfsurl != null)
		{
		    VariableTable dbparam = new VariableTable();
		    dbparam.add("path", java.sql.Types.VARCHAR);
  		    dbparam.setValue("path", dbfsurl);
		    String pagebody = TextCache.getTextContent("source::"+dbfsurl);
		    if (pagebody == null)
		    {
		        try {
			    DBPooledConnection dbconn = DBLogicalManager.getPoolConnection(db_name);
  			    try {
				pagebody = DBOperation.getString(dbconn,db_query, dbparam);
				vt.loadContent(pagebody);
				TextCache.putContent(System.currentTimeMillis(),"source::"+dbfsurl, pagebody, 20);
			    } catch( java.sql.SQLException sqle) {}
			    dbconn.close();
		        } catch( java.lang.Exception sqle) {}
		    }
		    else
		    {
			vt.loadContent(pagebody);
		    }
		}
	        if (pagebody != null) vt.loadContent(pagebody);
		getEnv(vt);
		vt.add("JSP.TAG", java.sql.Types.VARCHAR);
		vt.setValue("JSP.TAG", "YES");
		vt.add("REQUEST.URL",java.sql.Types.VARCHAR);
		vt.setValue("REQUEST.URL",request.getRequestURI());

		if (vt.exists("WEBCHART.KEEP_CACHE_TIME"))
		{
			KEEP_CACHE_TIME = vt.getInt("WEBCHART.KEEP_CACHE_TIME", 300);
			if (KEEP_CACHE_TIME < 5) KEEP_CACHE_TIME = 5;
		}	
		java.io.File xsl_file = null;
		if (vt.getString("WEBCHART.XSLDOC") != null)
			xsl_file = new java.io.File(getPhysicalPath(vt.getString("WEBCHART.XSLDOC")));
		String cachekey = vt.parseString(vt.getString("WEBCHART.CACHE"));

		String cache_content = null;

  		if (cachekey != null && !vt.exists("WEBCHART.FORCECACHE"))
			cache_content = TextCache.getTextContent(cachekey);
		
		if (cache_content == null)
		{
                    java.io.StringWriter xmlbuf = new java.io.StringWriter();
 		    writeXMLHeader(xmlbuf,vt);
		    xmlbuf.write("<root>\n");
		    WebChart2.generateChart(xmlbuf, null, vt, file_ext);
		    xmlbuf.write("</root>\n");

		    java.io.StringWriter htmlbuf = new java.io.StringWriter();
		    if (xsl_file != null && xsl_file.exists())
		          BaseServlet.XML2HTML(htmlbuf,new java.io.StringReader(xmlbuf.toString()),
			         new java.io.StringReader(FileCache.getFileContent(xsl_file)),
				 FileCache.getFileContent(xsl_file));		
		    else
		          BaseServlet.XML2HTML(htmlbuf,new java.io.StringReader(xmlbuf.toString()),
			         new java.io.StringReader(StaticResource.getTextResource("defaultxsl")),
				 StaticResource.getTextResource("defaultxsl"));		
		    cache_content = htmlbuf.toString();
		    out.write(cache_content);
		    if (cachekey != null)
			TextCache.putContent(current_time, cachekey, cache_content, KEEP_CACHE_TIME);	
		}
		else
		{
                    out.write(cache_content);
		}
         }
      }
      catch(IOException ioe) 
      {
         throw new JspException("Error:    "+ioe.getMessage());   
      }
      return EVAL_PAGE;
    }
}
