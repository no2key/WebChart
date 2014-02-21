package com.lfx.web;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.lfx.db.*;

public class WebChart2Servlet extends BaseServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    	throws IOException, ServletException
    {
	VariableTable vt = new VariableTable();
	Enumeration e = null;
	String reqfile="";
	File os_file=null;
	String login_id = null;
	String login_role = null;
	java.util.Vector v_temp = null;
	String file_ext = getServletContext().getInitParameter("FileExtention");
	String dbfs_ext = getServletContext().getInitParameter("DatabaseExtention");
	String db_name = getServletContext().getInitParameter("DatabaseName");
	String db_query = getServletContext().getInitParameter("DatabaseQuery");
	int pos=-1;
        String sourcecode = null;

	reqfile = request.getRequestURI();

	if (reqfile.endsWith(concatString("/showimage",file_ext)))
	{
		String imgid = request.getParameter("id");
		String imgdel = request.getParameter("del");

		/* vt.loadContent(FileCache.getFileContent(getPhysicalPath(request,concatString("/global",file_ext)))); */
		/* getEnv(vt,request); */

		response.setContentType("image/png");
		response.setHeader("Cache-Control", "no-cache"); 
		if (imgid != null)	
		{
			java.io.OutputStream out = response.getOutputStream();
			byte imagedata[] = ImageCache.getImageContent(imgid);
			if (imagedata != null)
			{
				out.write(imagedata);
				out.flush();	
			}
			if ("YES".equalsIgnoreCase(imgdel)) ImageCache.removeContent(imgid);
		}
		return;
	}
	else if (reqfile.endsWith(concatString("/sysstatic",file_ext)))
	{
		/* getEnv(vt,request); */
		StaticResourceItem resitem = StaticResource.getResource(request.getParameter("res"));
		if (resitem != null)
		{
		    if (resitem.getType() == StaticResourceItem.GIF)
			response.setContentType("image/png");
		    else if (resitem.getType() == StaticResourceItem.CSS)
			response.setContentType("text/css");			
		    else if (resitem.getType() == StaticResourceItem.JS)
			response.setContentType("text/script");			
		    else if (resitem.getType() == StaticResourceItem.XSL)
			response.setContentType("text/xml");					
		    else 
			response.setContentType("text/html");			

		    if (resitem.getType() == StaticResourceItem.GIF ||
                        resitem.getType() == StaticResourceItem.JS  ||
                        resitem.getType() == StaticResourceItem.CSS)
		    {
			java.io.OutputStream out = response.getOutputStream();
			out.write(resitem.getByteContent());
			out.flush();
		    }
		    else if (resitem.getType() == StaticResourceItem.XSL)
		    {
			java.io.PrintWriter out = response.getWriter();
			out.println(resitem.getTextContent());
			out.flush();
		    }
		}
		else
		{
		    response.setContentType("text/html");			
		}
		return;
	}
	else if (reqfile.endsWith(concatString("/sysstatus",file_ext)))
	{
		setHTMLContentType(response,vt);
		response.setHeader("Cache-Control", "no-cache"); 
		java.io.PrintWriter xlsout = response.getWriter();

		xlsout.println(concatString("ImageCacheServiceCount=",String.valueOf(ImageCache.getServiceCount())));
		xlsout.println(concatString(",ImageCacheImageCount=",String.valueOf(ImageCache.getImageCount())));

		xlsout.flush();
		xlsout.close();
		return;
	}
	else if (reqfile.endsWith(concatString("/syslogin",file_ext)) ||
	         reqfile.endsWith(concatString("/sysloginform",file_ext)) ||
	         reqfile.endsWith(concatString("/syspassword",file_ext)) ||
	         reqfile.endsWith(concatString("/syspasswordform",file_ext)) ||
	         reqfile.endsWith(concatString("/sysencrypt",file_ext)) ||
	         reqfile.endsWith(concatString("/syslogout",file_ext)) ||
	         reqfile.endsWith(concatString("/syseditxsl",file_ext)) ||
	         reqfile.endsWith(concatString("/sysedit",file_ext)) ||
	         reqfile.endsWith(concatString("/sysdbmgr",file_ext)) )
	{
		HttpSession session = null;
	
		vt.loadFile(getPhysicalPath(request,"/global"+file_ext));

		if (!checkIPAccess(vt))
		{
			forward(response,vt.parseURLString(vt.getString("WEBCHART.IPACCESSDENY")));
			return;
		}

  		if (vt.exists("WEBCHART.DB_CHARSET"))
		{
			request.setCharacterEncoding(vt.getString("WEBCHART.DB_CHARSET"));
			response.setCharacterEncoding(vt.getString("WEBCHART.DB_CHARSET"));
		}

		getEnv(vt,request);
		if (vt.getString("LOGIN_PASSWORD") != null)
		{
	    		vt.setValue("LOGIN_PASSWORD",DBPassword.encrypt(vt.getString("LOGIN_PASSWORD")));	
		}
		if (vt.getString("LOGIN_NEWPASSWORD") != null)
		{
		    vt.setValue("LOGIN_NEWPASSWORD",DBPassword.encrypt(vt.getString("LOGIN_NEWPASSWORD")));	
		}
		if (vt.getString("LOGIN_OLDPASSWORD") != null)
		{
	    		vt.setValue("LOGIN_OLDPASSWORD",DBPassword.encrypt(vt.getString("LOGIN_OLDPASSWORD")));	
		}

		if (!vt.exists("LOGIN_PAGE") || vt.getString("LOGIN_PAGE") == null)
		{
			vt.add("LOGIN_PAGE", java.sql.Types.VARCHAR);
			vt.setValue("LOGIN_PAGE", request.getHeader("referer"));
		}

		vt.remove("WEBCHART.SECURITY");
		vt.add("WEBCHART.SECURITY", java.sql.Types.VARCHAR);
		vt.setValue("WEBCHART.SECURITY","TRUE");
		vt.add("WEBCHART.DEFAULTACCESS", java.sql.Types.VARCHAR);
		vt.setValue("WEBCHART.DEFAULTACCESS","ALLOW");
		vt.remove("WEBCHART.DENY");
		vt.remove("WEBCHART.ALLOW");

			
		if (reqfile.endsWith("/syslogin"+file_ext))
		{
			session = request.getSession(true);
			vt.add("SESSION.ID",java.sql.Types.VARCHAR);
			vt.add("SESSION.ADMINDB",java.sql.Types.VARCHAR);
			vt.setValue("SESSION.ID",session.getId());
			vt.setValue("SESSION.ADMINDB",session.getAttribute("SESSION.ADMINDB"));
			if (!login(session,vt))
			{
				session.invalidate();
				forward(response,vt.parseURLString(vt.getString("ERROR_PAGE")));
				return;
			}
			forward(response,vt.parseURLString(vt.getString("LOGIN_PAGE")));
			return;
		}
	        else if (reqfile.endsWith("/syspassword"+file_ext))
		{
			if (vt.exists("SESSION.LOGINID"))
			{
				int i = password(vt);
				if ( i == 1 )
				{
				  forward(response,vt.parseURLString(vt.getString("LOGIN_PAGE")));
				  return;
				}
			}
			forward(response,vt.parseURLString(vt.getString("ERROR_PAGE")));
			return;
		}
	        else if (reqfile.endsWith("/sysencrypt"+file_ext))
		{
		    setHTMLContentType(response,vt);
	  	    PrintWriter out = response.getWriter();

		    response.setHeader("Cache-Control", "no-cache"); 
		    out.write("<html>\n");
		    out.write("   <head>\n");
		    out.write("      <title>Password Encrypt Service</title>\n");
		    out.write("   </head>\n");
		    out.write("   <body>\n");
		    out.write("         <form action=\"sysencrypt"+file_ext+"\" method=\"post\">\n");
		    out.write("            <table border=\"0\">\n");
		    out.write("               <tr>\n");
		    out.write("                  <td> Source: </td>\n");
		    out.write("                  <td><input type=\"password\" name=\"S\" size=\"25\">\n");
		    out.write("                  <input type=\"submit\" Value=\"Encrypt\"></td>\n");
		    out.write("               </tr>\n");
		    out.write("               <tr>\n");
		    if (vt.exists("S"))
		        out.write("                  <td>Encrypted:</td><td>"+DBPassword.encrypt(vt.getString("S"))+"</td>\n");
		    else
		        out.write("                  <td>Encrypted:</td><td></td>\n");
		    out.write("                  <td>\n");
		    out.write("                  </td>\n");
		    out.write("               </tr>\n");
		    out.write("            </table>\n");
		    out.write("         </form>\n");
		    out.write("   </body>\n");
		    out.write("</html>\n");		
		    out.flush();		
		    return;
		}
		else if (reqfile.endsWith("/sysloginform"+file_ext))
		{
		    session = request.getSession(false);
		    if (session != null)
		    {
			session.invalidate();
		    }
	
		    setHTMLContentType(response,vt);
	  	    PrintWriter out = response.getWriter();

		    response.setHeader("Cache-Control", "no-cache"); 
		    out.write("<html>\n");
		    out.write("   <head>\n");
		    out.write("      <title>WebChart System</title>\n");
		    out.write("   </head>\n");
		    out.write("   <body>\n");
		    out.write("      <center>\n");
		    out.write("         <form action=\"syslogin"+file_ext+"\" method=\"post\">\n");
		    if (vt.getString("LOGIN_PAGE") != null && vt.getString("LOGIN_PAGE").length() > 0)
		    {
		       out.write("            <input type=\"hidden\" name=\"LOGIN_PAGE\" value=\""+vt.parseURLString(vt.getString("LOGIN_PAGE"))+"\">\n");
		    }
		    else if (vt.getString("REQUEST.REFERER") != null && vt.getString("REQUEST.REFERER").length()>0)
		    {
			out.write("            <input type=\"hidden\" name=\"LOGIN_PAGE\" value=\""+vt.parseURLString(vt.getString("REQUEST.REFERER"))+"\">\n");
		    }
		    else
		    {
		       out.write("            <input type=\"hidden\" name=\"LOGIN_PAGE\" value=\"sysloginform"+file_ext+"\">\n");
		    } 
		    if (vt.getString("ERROR_PAGE") != null)      
		    {
		    	out.write("            <input type=\"hidden\" name=\"ERROR_PAGE\" value=\""+vt.parseURLString(vt.getString("ERROR_PAGE"))+"\">\n");
		    }
		    else 
		    {
		    	if (vt.getString("LOGIN_PAGE") != null && vt.getString("LOGIN_PAGE").length() > 0)
		    		out.write("            <input type=\"hidden\" name=\"ERROR_PAGE\" value=\"sysloginform"
		    					+file_ext+"?login_page="+vt.getString("LOGIN_PAGE")+"\">\n");
			else if (vt.getString("REQUEST.REFERER") != null && vt.getString("REQUEST.REFERER").length()>0)
		    		out.write("            <input type=\"hidden\" name=\"ERROR_PAGE\" value=\"sysloginform"
		    					+file_ext+"?login_page="+vt.getString("REQUEST.REFERER")+"\">\n");
		    	else
		    		out.write("            <input type=\"hidden\" name=\"ERROR_PAGE\" value=\"sysloginform"+file_ext+"\">\n");
		    }
		    
		    out.write("            <table border=\"0\">\n");
		    out.write("               <tr>\n");
		    out.write("                  <td> Username: </td>\n");
		    out.write("                  <td><input type=\"text\" name=\"LOGIN_ID\" size=\"20\">\n");
		    out.write("                  </td>\n");
		    out.write("               </tr>\n");
		    out.write("               <tr>\n");
		    out.write("                  <td>Password: </td>\n");
		    out.write("                  <td><input type=\"password\" name=\"LOGIN_PASSWORD\" size=\"20\">\n");
		    out.write("                  <input type=\"submit\" Value=\"Login\">\n");
		    out.write("                  </td>\n");
		    out.write("               </tr>\n");
		    out.write("            </table>\n");
		    out.write("         </form>\n");
		    out.write("      </center>\n");
		    out.write("   </body>\n");
		    out.write("</html>\n");		
		    out.flush();
		}
		else if (reqfile.endsWith("/syspasswordform"+file_ext) && checkAccess(vt))
		{
		    setHTMLContentType(response,vt);
	  	    PrintWriter out = response.getWriter();
		    response.setHeader("Cache-Control", "no-cache"); 
		    out.write("<html>\n");
		    out.write("   <head>\n");
		    out.write("      <title>Change Password : "+vt.getString("SESSION.LOGINID")+"</title>\n");
		    out.write("   </head>\n");
		    out.write("   <body>\n");
		    out.write("      <center>\n");
		    out.write("      <SCRIPT TYPE=\"text/javascript\">\n");
		    out.write("      <!--\n");
		    out.write("      function chkPassword()\n");
		    out.write("      {\n");
	   	    out.write("      var oldpasswd = document.pwdfrm.LOGIN_OLDPASSWORD.value;\n");
	   	    out.write("      var newpasswd = document.pwdfrm.LOGIN_NEWPASSWORD.value;\n");
	   	    out.write("      var chkpasswd = document.pwdfrm.LOGIN_CHKPASSWORD.value;\n");
		    out.write("      if  (!oldpasswd)\n");
	   	    out.write("      {\n");   	        
	   	    out.write("         alert(\"Old password cannot be empty!\");\n");   	    
	   	    out.write("         return false;\n");   	    
	   	    out.write("      }\n");   	    
		    out.write("      if  (!newpasswd)\n");
	   	    out.write("      {\n");   	        
	   	    out.write("         alert(\"New password cannot be empty!\");\n");   	    
	   	    out.write("         return false;\n");   	    
	   	    out.write("      }\n");   	    
		    out.write("      if  (newpasswd == chkpasswd) return true;\n");
	   	    out.write("      alert(\"Password mismatch between first input and second input!\");\n");
	   	    out.write("      return false;\n");
		    out.write("      }\n");
		    out.write("      // -->\n");
		    out.write("      </SCRIPT>\n");
		    out.write("         <form action=\"syspassword"+file_ext+"\" name=\"pwdfrm\" method=\"post\" onSubmit=\"return chkPassword()\" >\n");
	            out.write("            <input type=\"hidden\" name=\"LOGIN_PAGE\" value=\"syspasswordform"+file_ext+"\">\n");
		    out.write("            <input type=\"hidden\" name=\"ERROR_PAGE\" value=\"passwordform"+file_ext+"\">\n");
	    	    out.write("            <input type=\"hidden\" name=\"LOGIN_ID\" value=\""+vt.getString("SESSION.LOGINID")+"\">\n");
		    out.write("            <table border=\"0\">\n");
		    out.write("               <tr>\n");
		    out.write("                  <td> Old Password: </td>\n");
		    out.write("                  <td><input type=\"password\" name=\"LOGIN_OLDPASSWORD\" size=\"20\">\n");
		    out.write("                  </td>\n");
		    out.write("               </tr>\n");
		    out.write("               <tr>\n");
		    out.write("                  <td>New Password: </td>\n");
		    out.write("                  <td><input type=\"password\" name=\"LOGIN_NEWPASSWORD\" size=\"20\">\n");
		    out.write("                  </td>\n");
		    out.write("               </tr>\n");
		    out.write("               <tr>\n");
		    out.write("                  <td>&nbsp; </td>\n");
		    out.write("                  <td><input type=\"password\" name=\"LOGIN_CHKPASSWORD\" size=\"20\">\n");
		    out.write("                  <input type=\"submit\" Value=\"Change\">\n");
		    out.write("                  </td>\n");
		    out.write("               </tr>\n");
		    out.write("            </table>\n");
		    out.write("         </form>\n");
		    out.write("      </center>\n");
		    out.write("   </body>\n");
		    out.write("</html>\n");		
		    out.flush();
		}
		else if (reqfile.endsWith("/syslogout"+file_ext))
		{
			session = request.getSession(false);
			if (session != null)
			{
				logout(vt);
				session.invalidate();
			}
			forward(response,vt.parseURLString(vt.getString("LOGIN_PAGE")));
			return;
		}
		else if (reqfile.endsWith("/sysdbmgr"+file_ext))
		{
			getEnv(vt,request);
			if ("PHYSICAL".equalsIgnoreCase(vt.getString("N")))
			{
				if (vt.getString("C") == null)
					DBPhysicalManager.markupAll();
				else
				{
					if ("MARKDOWN".equalsIgnoreCase(vt.getString("A")))
					    DBPhysicalManager.markdown(vt.getString("C"));
					else
					    DBPhysicalManager.markup(vt.getString("C"));
				}
			}
			else if ("LOGICAL".equalsIgnoreCase(vt.getString("N")))
			{
				if (vt.getString("C") == null)
					DBLogicalManager.markupAll();
				else
				{
					if ("MARKDOWN".equalsIgnoreCase(vt.getString("A")))
					    DBLogicalManager.markdown(vt.getString("C"));
					else
					    DBLogicalManager.markup(vt.getString("C"));
				}
			}
			setHTMLContentType(response,vt);
	    	        PrintWriter out = response.getWriter();
			response.setHeader("Cache-Control", "no-cache"); 
		        out.write("<html>\n");
		        out.write("   <head>\n");
		        out.write("      <title>Database Connection Manager</title>\n");
		        out.write("   </head>\n");
		        out.write("   <body>\n");
			if (vt.exists("N"))
			{
		            out.write("      <script language=\"JavaScript\">window.location=\"sysdbmgr"+file_ext+"\";</script>\n");	
			}
			else
			{
			    DBLogicalManager.writeHTML(out,file_ext);
			    DBPhysicalManager.writeHTML(out,file_ext);
			}
		        out.write("   </body>\n");
		        out.write("</html>\n");
			out.flush();
		}
		else if (reqfile.endsWith("/syseditxsl"+file_ext) || reqfile.endsWith("/sysedit"+file_ext))
		{
			String db_charset="gb2312";
		        if (vt.exists("WEBCHART.DB_CHARSET"))
		        {
			    db_charset=vt.getString("WEBCHART.DB_CHARSET");
        		}
			vt.add("WEBCHART.ALLOW", java.sql.Types.VARCHAR);
			vt.setValue("WEBCHART.ALLOW", "EDITOR");
			vt.remove("WEBCHART.SECURITY");
			vt.add("WEBCHART.SECURITY", java.sql.Types.VARCHAR);
			vt.setValue("WEBCHART.SECURITY","TRUE");
			vt.remove("WEBCHART.DEFAULTACCESS");			
			boolean is_access = checkAccess(vt);

		   	PrintWriter out = response.getWriter();
			if (reqfile.endsWith("/syseditxsl"+file_ext))
			{
			    setXMLContentType(response,vt);

			    response.setHeader("Cache-Control", "no-cache"); 
			    out.write("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
			    out.write("<xsl:output method=\"html\" encoding=\""+db_charset+"\" />");
			    out.write("<xsl:template match=\"root\">");
			    out.write("<html>");
			    out.write("<head>");
			    out.write("<title>Edit of <xsl:value-of select=\"param[@id='PAGE']\" /></title>");
			    out.write("</head>");
			    out.write("<body>");
			    	    	
			    out.write("<table frame=\"void\" border=\"0\"><tr><td valign=\"top\">");
			    
			    if (is_access)
			    	out.write("<p><a href=\"syslogout"+file_ext+"?login_page=sysedit"+file_ext+"\">Logout</a></p>");
			    else
			    	out.write("<p><a href=\"sysloginform"+file_ext+"\">Login</a></p>");
		
			    out.write("<xsl:apply-templates select=\"pages\" />");
			    out.write("</td><td valign=\"top\">");
			    out.write("<form method=\"post\" action=\"sysedit"+file_ext+"\">");
			    out.write("<input type=\"hidden\" name=\"page\" >");
			    out.write("<xsl:attribute name=\"value\"><xsl:value-of select=\"param[@id='PAGE']\"/></xsl:attribute>");
			    out.write("</input>");
			    out.write("<textarea id=\"content\" name=\"content\" rows=\"25\" cols=\"50\">");
			    out.write("<xsl:value-of select=\"content\"/>");
			    out.write("</textarea><br/>");
			    if (is_access)
			    	out.write("<input type=\"submit\" value=\"Save\" />");
			    out.write("<a target=\"_blank\">");
			    out.write("<xsl:attribute name=\"href\"><xsl:value-of select=\"param[@id='PAGE']\"/></xsl:attribute>");
			    out.write("Preview</a>");
			    out.write("</form>");
			    out.write("</td></tr></table>");
			    out.write("</body>");
			    out.write("</html>");
			    out.write("</xsl:template>");
			    out.write("<xsl:template match=\"pages\" >");
			    out.write("<xsl:for-each select=\"page\">");
		   	    out.write("<a><xsl:attribute name=\"href\">sysedit"+file_ext+"?page=<xsl:value-of select=\".\"/></xsl:attribute><xsl:value-of select=\".\"/></a><br />");
			    out.write("</xsl:for-each>");
			    out.write("</xsl:template>");    
			    out.write("</xsl:stylesheet>");
			    out.flush();
			}
			else
			{
				if (! is_access)
				{
				    setHTMLContentType(response,vt);
				    out.write("<html>\n");
				    out.write("   <head>\n");
				    out.write("      <title>WebChart System</title>\n");
				    out.write("   </head>\n");
				    out.write("   <body>\n");
				    out.write("   You must <a href=\"sysloginform"+file_ext+"?login_page=sysedit"
				    		  +file_ext+"\">login</a> with a \"EDITOR\" role.\n");
				    out.write("   </body>\n");
				    out.write("</html>\n");	    
				}
			        else
			        {
			            String fname = null;
			            if (vt.getString("PAGE") != null)
				       fname = getPhysicalPath(request,vt.getString("PAGE"));
				    if (fname != null && vt.getString("content") != null)
				    {
				    	   if (checkAccess(vt))
				    	   {
				    	      String content = vt.getString("content");
				    	      content = content.trim();
				    	      saveFile(fname, content);	
				    	   }
				    }
				    setXMLContentType(response,vt);
				    vt.remove("content");
				    writeXMLHeader(out,vt, "syseditxsl"+file_ext);
				    out.write("<root>\n");
				    vt.writeXMLBody(out);
				    listFiles(out, getPhysicalPath(request,"."));
				    out.write("<content>\n");
				    if (fname != null)
				        printFile(out,fname);
				    out.write("</content>\n");
				    out.write("</root>\n");	
				    out.flush();
				}				
			}
		}				
		return ;
	}
	
	vt.loadContent(FileCache.getFileContent(getPhysicalPath(request,"/global"+file_ext)));

	if (dbfs_ext != null && dbfs_ext.equals(request.getServletPath()))
	{
		VariableTable dbparam = new VariableTable();
		if (request.getPathInfo() != null && request.getPathInfo().length() > 1)
		{
		    dbparam.add("path", java.sql.Types.VARCHAR);
  		    dbparam.setValue("path", request.getPathInfo().substring(1));
		    String pagebody = TextCache.getTextContent("source::"+request.getRequestURI());
		    if (pagebody == null)
		    {
		        try {
			    DBPooledConnection dbconn = DBLogicalManager.getPoolConnection(db_name);
  			    try {
				pagebody = DBOperation.getString(dbconn,db_query, dbparam);
				vt.loadContent(pagebody);
				TextCache.putContent(System.currentTimeMillis(),"source::"+request.getRequestURI(), pagebody, 20);
			    } catch( java.sql.SQLException sqle) {}
			    dbconn.close();
		        } catch( java.lang.Exception sqle) {}
		    }
		    else
		    {
			vt.loadContent(pagebody);
		    }
                    sourcecode = pagebody;
		}
	}
	else
	{
		vt.loadContent(FileCache.getFileContent(getPhysicalPath(request,"default"+file_ext)));
		reqfile = getPhysicalPath(request,request.getServletPath());
		os_file = new File(reqfile);
		if (os_file.exists())
		{
			vt.loadContent(FileCache.getFileContent(os_file));
                        sourcecode = FileCache.getFileContent(os_file);
		}
	}

	if (vt.exists("WEBCHART.DB_CHARSET"))
	{
		request.setCharacterEncoding(vt.getString("WEBCHART.DB_CHARSET"));
		response.setCharacterEncoding(vt.getString("WEBCHART.DB_CHARSET"));
	}

	vt.remove("SESSION.ID");
	vt.remove("SESSION.ADMINDB");
	vt.remove("SESSION.LOGINID");
	vt.remove("SESSION.LOGINNAME");
	vt.remove("SESSION.LOGINROLE");

	getEnv(vt,request);
	vt.add("REQUEST.URL",java.sql.Types.VARCHAR);
	vt.add("REQUEST.FILE",java.sql.Types.VARCHAR);
	if (os_file != null)
	{
	    vt.setValue("REQUEST.URL",request.getRequestURI());
	    vt.setValue("REQUEST.FILE",os_file.getName());
	}
	else
	{
	    vt.setValue("REQUEST.URL",request.getRequestURI());
	    if (request.getPathInfo() != null && request.getPathInfo().length()>1)
		    vt.setValue("REQUEST.FILE",request.getPathInfo().substring(1));
	    else
		    vt.setValue("REQUEST.FILE","");
	}

	login_id = vt.getString("SESSION.LOGINID");
	login_role = vt.getString("SESSION.LOGINROLE");

	if (!checkAccess(login_id,login_role,vt))
	{
		if (vt.getString("WEBCHART.ACCESSDENY") != null)
		    forward(request,response,vt.parseURLString(vt.getString("WEBCHART.ACCESSDENY")));
		else 
		    forward(request,response,"sysloginform"+file_ext+"?login_page="+vt.getString("REQUEST.FILE"));
		return;
	}

	if (!checkIPAccess(vt))
	{
		forward(request,response,vt.parseURLString(vt.getString("WEBCHART.IPACCESSDENY")));
		return;
	}
	
	logAccess(vt);
	
	if (vt.exists("WEBCHART.KEEP_CACHE_TIME"))
	{
		KEEP_CACHE_TIME = vt.getInt("WEBCHART.KEEP_CACHE_TIME", 300);
		if (KEEP_CACHE_TIME < 5) KEEP_CACHE_TIME = 5;
	}

	String document_type = vt.getString("WEBCHART.DOCTYPE");
	if (document_type == null || document_type.length()==0)
	{
		document_type="XML";
	}
	else
	{
		document_type=document_type.toUpperCase();
	}

	String xsldoc = vt.getString("WEBCHART.XSLDOC");
	String xmlinclude = vt.parseString(vt.getString("WEBCHART.XMLDATA"));
	String cachekey = vt.parseString(vt.getString("WEBCHART.CACHE"));

	if (xsldoc == null) xsldoc = "NotExistAtAll.xsl";

	java.io.File xsl_file = new java.io.File(getPhysicalPath(request,xsldoc));


	String imageonly = vt.getString("WEBCHART.IMAGEONLY");
	if (imageonly == null) imageonly = "NO";
	imageonly = imageonly.toUpperCase();

	long current_time = System.currentTimeMillis();

	if (vt.exists("WEBCHART.DBID"))
	{
		int dbidseed = getInt(vt.getString("WEBCHART.DBIDSEED"),100);
		vt.setValue("WEBCHART.DBID",TextUtils.hashCode(vt.parseString(vt.getString("WEBCHART.DBID")),dbidseed));
	}

	if (vt.exists("WEBCHART.DATATYPE"))
	{
		String vartypes[] = TextUtils.toStringArray(TextUtils.getLines(vt.getString("WEBCHART.DATATYPE")));
		for(int i=0;i<vartypes.length;i++)
		{
			String tempwords[] = TextUtils.toStringArray(TextUtils.getWords(vartypes[i]));
			if (tempwords.length == 2)
				vt.setType(tempwords[0], SQLTypes.getTypeID(tempwords[1]));
		}
	}

	if (imageonly.equals("YES"))
	{
		java.io.OutputStream imgout = response.getOutputStream();

		response.setContentType("image/png");
		response.setHeader("Cache-Control", "no-cache"); 

		byte cache_content[] = null;
		if (cachekey != null && !vt.exists("WEBCHART.FORCECACHE"))
			cache_content = ImageCache.getImageContent(cachekey);

		if (cache_content == null)
		{
			java.io.ByteArrayOutputStream imgdoc = new java.io.ByteArrayOutputStream(16384);
			if ("ORACLE".equalsIgnoreCase(vt.getString("WEBCHART.ENGINE")))
			    WebChart.generateChart(null,imgdoc,vt, file_ext);				
			else
			    WebChart2.generateChart(null,imgdoc,vt, file_ext);				
			imgdoc.close();
			cache_content = imgdoc.toByteArray();
			imgout.write(cache_content);
			if (cachekey != null)
				ImageCache.putContent(current_time, cachekey, cache_content, KEEP_CACHE_TIME);
		}
		else
		{
			imgout.write(cache_content);
		}
		imgout.flush();
		imgout.close();
		cache_content = null;
	}
        else if ("SOURCE".equals(document_type))
	{
	        java.io.PrintWriter out = response.getWriter();
		setCSSContentType(response,vt);

		if (sourcecode != null)
		{
			out.write(sourcecode);
		}
		out.flush();
		out.close();
		sourcecode = null;
	}
        else if (xsldoc != null && xsldoc.trim().length()>0 && "HTML".equals(document_type))
	{
	        java.io.OutputStream out = response.getOutputStream();
		setHTMLContentType(response,vt);
		response.setHeader("Cache-Control", "no-cache"); 
		response.setHeader("Content-Encoding", "gzip"); 

		byte cache_content[] = null;

  		if (cachekey != null && !vt.exists("WEBCHART.FORCECACHE"))
			cache_content = ImageCache.getImageContent(cachekey);

		if (cache_content == null)
		{
			java.io.StringWriter xmldoc = new java.io.StringWriter(8192);
			writeXMLHeader(xmldoc,vt);
			xmldoc.write("<root>\n");
			if (xmlinclude != null)
			{
				xmldoc.write(vt.parseString(xmlinclude));
			}
			if ("ORACLE".equalsIgnoreCase(vt.getString("WEBCHART.ENGINE")))
			    WebChart.generateChart(xmldoc, null, vt, file_ext);
			else if ("JSCHART".equalsIgnoreCase(vt.getString("WEBCHART.ENGINE")))
			    WebChartJSChart.generateChart(xmldoc, null, vt, file_ext);
			else
			    WebChart2.generateChart(xmldoc, null, vt, file_ext);
			xmldoc.write("</root>\n");
			xmldoc.close();	
			
			java.io.ByteArrayOutputStream htmldata = new java.io.ByteArrayOutputStream(8192);
			java.io.PrintWriter htmldoc = new java.io.PrintWriter(new java.util.zip.GZIPOutputStream(htmldata));

			if (xsl_file.exists())
			    XML2HTML(htmldoc,new java.io.StringReader(xmldoc.toString()),
			         new java.io.StringReader(FileCache.getFileContent(xsl_file)), FileCache.getFileContent(xsl_file));
		        else
			    XML2HTML(htmldoc,new java.io.StringReader(xmldoc.toString()),
			         new java.io.StringReader(StaticResource.getTextResource("defaultxsl")), StaticResource.getTextResource("defaultxsl"));

			htmldoc.close();
			htmldata.close();

			cache_content = htmldata.toByteArray();
			out.write(cache_content);
			if (cachekey != null)
				ImageCache.putContent(current_time, cachekey, cache_content, KEEP_CACHE_TIME);	
		}
		else
		{
			out.write(cache_content);
		}
		out.flush();
		out.close();
		cache_content = null;
	}
        else if (xsldoc != null && xsldoc.trim().length()>0  && "MAIL".equals(document_type))
	{
	        java.io.PrintWriter out = response.getWriter();
		setHTMLContentType(response,vt);
		response.setHeader("Cache-Control", "no-cache"); 

		java.io.StringWriter xmldoc = new java.io.StringWriter(8192);
		writeXMLHeader(xmldoc,vt);
		xmldoc.write("<root>\n");
		if (xmlinclude != null)
		{
			xmldoc.write(vt.parseString(xmlinclude));
		}
		if ("ORACLE".equalsIgnoreCase(vt.getString("WEBCHART.ENGINE")))
		    WebChart.generateChart(xmldoc, null, vt, file_ext);
		else if ("JSCHART".equalsIgnoreCase(vt.getString("WEBCHART.ENGINE")))
		    WebChartJSChart.generateChart(xmldoc, null, vt, file_ext);
		else
		    WebChart2.generateChart(xmldoc, null, vt, file_ext);
		xmldoc.write("</root>\n");
		xmldoc.close();	
		
		java.io.StringWriter htmldata = new java.io.StringWriter(8192);
		java.io.PrintWriter htmldoc = new java.io.PrintWriter(htmldata);

		if (xsl_file.exists())
		    XML2HTML(htmldoc,new java.io.StringReader(xmldoc.toString()),
		         new java.io.StringReader(FileCache.getFileContent(xsl_file)), FileCache.getFileContent(xsl_file));
		else
		    XML2HTML(htmldoc,new java.io.StringReader(xmldoc.toString()),
		         new java.io.StringReader(StaticResource.getTextResource("defaultxsl")), StaticResource.getTextResource("defaultxsl"));

		htmldoc.close();
		htmldata.close();

		out.write(htmldata.toString());

		if (vt.exists("WEBCHART.EMAIL"))
		{
                        String mailparam[] = TextUtils.toStringArray(TextUtils.getWords(vt.parseString(vt.getString("WEBCHART.EMAIL"))));
			if (mailparam.length == 3)
		        {
				DBMailClient.sendMail(mailparam[0],null, null,mailparam[1],mailparam[2],
                                     vt.parseString(vt.getString("MAIL.TITLE")),htmldata.toString());
                        }
			else if (mailparam.length == 4)
		        {
				DBMailClient.sendMail(mailparam[0], mailparam[1],null, null,mailparam[2],mailparam[3],
                                     vt.parseString(vt.getString("MAIL.TITLE")),htmldata.toString());
                        }
			else if (mailparam.length == 5)
		        {
				DBMailClient.sendMail(mailparam[0],mailparam[1],mailparam[2],mailparam[3],mailparam[4],
                                     vt.parseString(vt.getString("MAIL.TITLE")),htmldata.toString());
                        }
			else if (mailparam.length == 6)
		        {
				DBMailClient.sendMail(mailparam[0],mailparam[1],mailparam[2],mailparam[3],mailparam[4],mailparam[5],
				     vt.parseString(vt.getString("MAIL.TITLE")),htmldata.toString());
                        }
		}	
		out.flush();
		out.close();
	}
	else if ("EXCEL".equals(document_type))
	{
		String xlsfname = vt.getString("WEBCHART.EXCEL");
		if (xlsfname == null) xlsfname = "report.xls";
		xlsfname = vt.parseString(xlsfname);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition","attachment;filename="+xlsfname);

		java.io.OutputStream xlsout = response.getOutputStream();
		ExcelOperation.generateExcel(xlsout,vt);

		xlsout.flush();
		xlsout.close();
	}
        else if ("CSS".equals(document_type))
	{
	        java.io.OutputStream out = response.getOutputStream();
		setCSSContentType(response,vt);
		/*
		response.setHeader("Cache-Control", "no-cache"); 
		*/
		response.setHeader("Content-Encoding", "gzip"); 

		byte cache_content[] = null;

  		if (cachekey != null && !vt.exists("WEBCHART.FORCECACHE"))
			cache_content = ImageCache.getImageContent(cachekey);

		if (cache_content == null)
		{
			String csscontent = vt.getString("WEBCHART.CSS");
			java.io.ByteArrayOutputStream htmldata = new java.io.ByteArrayOutputStream(8192);
			java.io.PrintWriter htmldoc = new java.io.PrintWriter(new java.util.zip.GZIPOutputStream(htmldata));
			htmldoc.write(vt.parseString(csscontent));
			htmldoc.close();	
			htmldata.close();
			
			cache_content = htmldata.toByteArray();
			out.write(cache_content);
			if (cachekey != null)
				ImageCache.putContent(current_time, cachekey, cache_content, KEEP_CACHE_TIME);	
		}
		else
		{
			out.write(cache_content);
		}
		out.flush();
		out.close();
		cache_content = null;
	}
	else if ("TEXT".equals(document_type))
	{
		setHTMLContentType(response,vt);
		java.io.PrintWriter xlsout = response.getWriter();

		DBQueryOperation.generateDBQuery(xlsout,vt);

		xlsout.flush();
		xlsout.close();
	}
	else if ("SEC".equals(document_type))
	{
		setHTMLContentType(response,vt);
		java.io.PrintWriter xlsout = response.getWriter();

		SecureOperation.execSecureCheck(xlsout,vt);

		xlsout.flush();
		xlsout.close();
	}
	else if ("JSON".equals(document_type))
	{
		setCSSContentType(response,vt);

		java.io.PrintWriter xlsout = response.getWriter();

		byte cache_content[] = null;

  		if (cachekey != null && !vt.exists("WEBCHART.FORCECACHE"))
			cache_content = ImageCache.getImageContent(cachekey);

		if (cache_content == null)
		{
		    java.io.StringWriter xmldoc = new java.io.StringWriter(8192);
		    DBJasonOperation.generateDBQuery(xmldoc,vt);
		    xmldoc.close();

		    if (cachekey != null)
		    {
		        cache_content = xmldoc.toString().getBytes();
			ImageCache.putContent(current_time, cachekey, cache_content, KEEP_CACHE_TIME);			
		    }

		    xlsout.write(xmldoc.toString());
		}
		else
		{
		    xlsout.write(new String(cache_content));
		}

		xlsout.flush();
		xlsout.close();
		cache_content = null;
	}
	else if ("UPDATE".equals(document_type))
	{
		setHTMLContentType(response,vt);
		response.setHeader("Cache-Control", "no-cache"); 

		java.io.PrintWriter xlsout = response.getWriter();

		DBUpdateOperation.generateDBQuery(xlsout,vt);

		xlsout.flush();
		xlsout.close();
	}
	else
	{
	        java.io.OutputStream out = response.getOutputStream();
		setXMLContentType(response,vt);
		response.setHeader("Cache-Control", "no-cache"); 
		response.setHeader("Content-Encoding", "gzip");

		byte cache_content[] = null;
		if (cachekey != null && !vt.exists("WEBCHART.FORCECACHE"))
		cache_content = ImageCache.getImageContent(cachekey);

		if (cache_content == null)
		{
			java.io.ByteArrayOutputStream htmldata = new java.io.ByteArrayOutputStream(8192);
			java.io.PrintWriter htmldoc = new java.io.PrintWriter(new java.util.zip.GZIPOutputStream(htmldata));

			if (xsldoc == null || xsldoc.length()==0 || xsl_file.exists())
				writeXMLHeader(htmldoc,vt,xsldoc);
			else
				writeXMLHeader(htmldoc,vt,"xsl/default.xsl");
			htmldoc.write("<root>\n");
			if (xmlinclude != null)
			{
				htmldoc.write(vt.parseString(xmlinclude));
			}
			if ("ORACLE".equalsIgnoreCase(vt.getString("WEBCHART.ENGINE")))
			    WebChart.generateChart(htmldoc,null,vt, file_ext);
			else if ("JSCHART".equalsIgnoreCase(vt.getString("WEBCHART.ENGINE")))
			    WebChartJSChart.generateChart(htmldoc, null, vt, file_ext);
			else
			    WebChart2.generateChart(htmldoc,null,vt, file_ext);
			htmldoc.write("</root>\n");
			htmldoc.close();
			htmldata.close();

			cache_content = htmldata.toByteArray();
			out.write(cache_content);
			if (cachekey != null)
				ImageCache.putContent(current_time, cachekey, cache_content, KEEP_CACHE_TIME);			
		}
		else
		{
			out.write(cache_content);
		}

		out.flush();
		out.close();
		cache_content = null;
	}
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    	throws IOException, ServletException
    {
	doGet(request,response);
    }
}
