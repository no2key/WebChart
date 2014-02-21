<?xml version='1.0' encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="utf-8" />
<xsl:template match="root">
<xsl:choose>
    <xsl:when test="param[@id='JSP.TAG']">
	 <div>
	    <xsl:apply-templates select="inputs" />
	    <xsl:apply-templates select="urls" />
   	    <xsl:apply-templates select="webchart" />
	 </div>
    </xsl:when>
    <xsl:otherwise>
<html>
<head>
<meta http-equiv="Cache-Control" content="no-cache,no-store,must-revalidate,max-age=-1" />
<title><xsl:value-of select="param[@id='HTML.TITLE']" disable-output-escaping="yes" /></title>
<xsl:apply-templates select="reload" />
<script type="text/javascript" src="sysstatic.rhtml?res=defaultjs" />
<script type="text/javascript" src="sysstatic.rhtml?res=jscharts" />
<link href="sysstatic.rhtml?res=defaultcss" rel="stylesheet" type="text/css" />
<xsl:if test="param[@id='HTML.CSS'] != ''">
<style type="text/css">
<xsl:comment>
<xsl:value-of select="param[@id='HTML.CSS']" disable-output-escaping="yes" />
</xsl:comment>
</style>
</xsl:if>
</head>
<body>
<div id="container">
<xsl:if test="param[@id='HTML.TITLE'] != ''">
<div id="header">
    <div id="headerimg">
	  <h1><xsl:value-of select="param[@id='HTML.TITLE']" disable-output-escaping="yes" /></h1>
	  <div class="description"><xsl:value-of select="param[@id='HTML.SUBTITLE']" disable-output-escaping="yes" /></div>
    </div>
    <div id="navi">
	  <xsl:apply-templates select="topurls" />
    </div>
</div>
</xsl:if>
<xsl:if test="param[@id='HTML.MENU'] != ''">
<xsl:value-of select="param[@id='HTML.MENU']" disable-output-escaping="yes" />
</xsl:if>
<!--/header -->

<div id="wrapper">
<div id="page">
<xsl:choose>
	<xsl:when test="lefturls or tree">
		<div id="sidebar"> 
		    <xsl:if test="lefturls">
		    <div class="widget">
			<xsl:apply-templates select="lefturls" />
		    </div>  
		    </xsl:if>
		    <xsl:if test="tree">
		    <div>
			<xsl:apply-templates select="tree" />
		    </div>  
		    </xsl:if>
		</div>
		<!--/sidebar -->

		<div id="content">
		  <div class="center">
			<xsl:apply-templates select="inputs" />
			<xsl:apply-templates select="urls" />
		  </div>
		  <div>
            <xsl:choose>
	        <xsl:when test="layout">   
                    <xsl:apply-templates select="layout" />
                </xsl:when>
                <xsl:otherwise>
   	            <xsl:apply-templates select="webchart" />
                </xsl:otherwise>
            </xsl:choose>
		  </div>
		</div><!--/content -->
	</xsl:when>
	<xsl:otherwise>
		  <div class="center">
			<xsl:apply-templates select="inputs" />
			<xsl:apply-templates select="urls" />
		  </div>
		  <div>
            <xsl:choose>
	        <xsl:when test="layout">   
                    <xsl:apply-templates select="layout" />
                </xsl:when>
                <xsl:otherwise>
   	            <xsl:apply-templates select="webchart" />
                </xsl:otherwise>
            </xsl:choose>
		  </div>
	</xsl:otherwise>
</xsl:choose>

<hr class="clear" />
</div><!--/page -->
</div><!--/wrapper -->

<xsl:if test="param[@id='HTML.TITLE'] != ''"> 
<div id="footerbg">
     <div id="credits">
	<div class="alignleft"><a href="http://www.justskins.com/themes/wordpress-themes/">Wordpress Theme</a> by <a href="http://www.realgeek.com/forums/sitemap/f-19.html">Windows Vista Security</a></div> 
	<div class="alignright">Copyright 2006-2010 AnySQL.net. All rights reserved.</div>
     </div>
</div>
</xsl:if>
  
</div>
</body>
</html>
    </xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="reload" >
    <script language="JavaScript">
        window.timer=window.setTimeout('window.location.href="<xsl:value-of select="." />";',<xsl:value-of select="@time" /> * 1000);
    </script>
</xsl:template>

<xsl:template match="layout" >
    <table border="0" width="100%" cellspacing="0" cellpadding="0">
    <tr>
    <xsl:for-each select="column">
        <xsl:variable name="rowid"><xsl:value-of select="@id" /></xsl:variable>
	<td valign="top">
    	    <xsl:attribute name="width"><xsl:value-of select="." /></xsl:attribute>
            <xsl:apply-templates select="//webchart[@layout = $rowid]" />
	</td>
    </xsl:for-each>
    </tr>
    </table>
</xsl:template>

<xsl:template match="urls" >
    <div id="urls">
    <xsl:for-each select="url">
	<xsl:choose>
	    <xsl:when test="@id = '-' or @sep">   
	    	<xsl:value-of select="." disable-output-escaping="yes" />
		<xsl:text> </xsl:text>
	    </xsl:when>
	    <xsl:otherwise>   
	    	<a>
	    		<xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute>
			<xsl:value-of select="@id" disable-output-escaping="yes" />
	    	</a>
	    </xsl:otherwise>
	</xsl:choose>
    </xsl:for-each>
    </div>
</xsl:template>

<xsl:template match="tree" >
<div class="dtree">
<script type="text/javascript">
<xsl:comment>
<xsl:value-of select="." disable-output-escaping="yes" />
</xsl:comment>
</script>
</div>
</xsl:template>

<xsl:template match="topurls" >
    <ul id="nav">
    <xsl:for-each select="url">
	<xsl:choose>
	    <xsl:when test="@id = '-' or @sep">   
		<li class="page_item">
	    		<xsl:value-of select="." disable-output-escaping="yes" />
		</li>
	    </xsl:when>
	    <xsl:otherwise>   
		<li>
		<xsl:attribute name="class">
			<xsl:choose>
			<xsl:when test="@cur">page_item current_page_item</xsl:when>
			<xsl:otherwise>page_item</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>		
	    	<a>
	    		<xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute>
	    		<xsl:value-of select="@id" />
	    	</a>
		</li>
	    </xsl:otherwise>
	</xsl:choose>
    </xsl:for-each>
    </ul>
</xsl:template>

<xsl:template match="lefturls" >
    <ul class="list-caadt">
    <xsl:for-each select="url">
        <div>
	<xsl:choose>
	    <xsl:when test="@id = '-' or @sep">   
	    	<li class="sidebartitle"><xsl:value-of select="." disable-output-escaping="yes" /></li>
	    </xsl:when>
	    <xsl:otherwise>   
		<li class="cat-item">
	    	<a>
	    		<xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute>
	    		<xsl:value-of select="@id" />
	    	</a>
		</li>
	    </xsl:otherwise>
	</xsl:choose>
        </div>
    </xsl:for-each>
    </ul>
</xsl:template>

<xsl:template match="inputs" >
    <div>
    <form method="get">
    <xsl:attribute name="action"><xsl:value-of select="//param[@id='REQUEST.FILE']" /></xsl:attribute>
    <xsl:for-each select="item">
    	<xsl:if test="@label">
    		<xsl:value-of select="@label" />
    	</xsl:if>
        <xsl:choose>
        <xsl:when test="@type = 'custom'">
 		<xsl:value-of select="@value" disable-output-escaping="yes" />
        </xsl:when>
        <xsl:when test="@type = 'option'">
        <select>
 	    <xsl:attribute name="name"><xsl:value-of select="@name" /></xsl:attribute>
  	    <xsl:for-each select="option">
            <option>
                <xsl:attribute name="value"><xsl:value-of select="." /></xsl:attribute>
    		<xsl:if test="@selected">
    			<xsl:attribute name="selected">yes</xsl:attribute>
    		</xsl:if>
                <xsl:value-of select="." />
            </option>
            </xsl:for-each>
        </select>
        </xsl:when>
        <xsl:otherwise>
    	<input>
    		<xsl:attribute name="type"><xsl:value-of select="@type" /></xsl:attribute>
    		<xsl:attribute name="name"><xsl:value-of select="@name" /></xsl:attribute>
    		<xsl:attribute name="value"><xsl:value-of select="@value" /></xsl:attribute>
    		<xsl:if test="@size">
    			<xsl:attribute name="size"><xsl:value-of select="@size" /></xsl:attribute>
    		</xsl:if>
    		<xsl:if test="@checked">
    			<xsl:attribute name="checked"><xsl:value-of select="@checked" /></xsl:attribute>
    		</xsl:if>
    	</input>
        </xsl:otherwise>
        </xsl:choose>
    </xsl:for-each>
    <input type="submit" value="Query" />
    </form>
    </div>
</xsl:template>

<xsl:template match="webchart" >
    <xsl:if test="dataset">
        <table border="0" width="100%" cellspacing="0" cellpadding="2">
            <caption align="center"><font size="4"><xsl:value-of select="title" disable-output-escaping="yes" /></font></caption>
   	    <xsl:apply-templates select="dataset" />
        </table>	
    </xsl:if>
    <xsl:if test="image">
        <xsl:apply-templates select="image" />
    </xsl:if>
    <xsl:if test="memo">
        <p><xsl:value-of select="memo" disable-output-escaping="yes" /></p>
    </xsl:if>
    <xsl:if test="jschart">
	<div><xsl:attribute name="id">chart<xsl:value-of select="jschart/@id" /></xsl:attribute></div>
	<script type="text/javascript">
	<xsl:comment>
	<xsl:value-of select="." disable-output-escaping="yes" />
	</xsl:comment>
	</script>
    </xsl:if>
</xsl:template>

<xsl:template name="default-dataset" match="dataset" >
<xsl:choose>
<xsl:when test="page">
   <tr>
   <xsl:for-each select="page">
       <td valign="top">
	<table border="1" width="100%" cellspacing="0" cellpadding="0" class="data">
	<xsl:for-each select="../head">
	   <xsl:if test="content">
		<xsl:value-of select="content" disable-output-escaping="yes" />
	   </xsl:if>	
	   <xsl:if test="col">
	        <tr>
		<xsl:for-each select="../head/col">
		    <xsl:if test="super">
		    <th>
			<xsl:if test="colspan">
        	 	    <xsl:attribute name="colspan"><xsl:value-of select="colspan" /></xsl:attribute>
	        	</xsl:if>
			<xsl:if test="rowspan">
        		    <xsl:attribute name="rowspan"><xsl:value-of select="rowspan" /></xsl:attribute>
        		    <xsl:attribute name="width"><xsl:value-of select="@size" /></xsl:attribute>
		        </xsl:if>
			<xsl:value-of select="super" disable-output-escaping="yes" />
		    </th>
		    </xsl:if>
		</xsl:for-each>
		</tr>
		<tr>
		<xsl:for-each select="../head/col">
		    <xsl:if test="label">
		    <th>
        		<xsl:attribute name="width">
				<xsl:value-of select="@size" />
			</xsl:attribute>
			<xsl:choose>
			<xsl:when test="formater">
			    <xsl:value-of select="formater" disable-output-escaping="yes" />
			</xsl:when>
			<xsl:otherwise>	
			    <xsl:value-of select="label" />
			</xsl:otherwise>	
			</xsl:choose>
		    </th>
		    </xsl:if>
		</xsl:for-each>
		</tr>
	   </xsl:if>	
	</xsl:for-each>
	<xsl:for-each select="row">
	    <xsl:variable name="rowid"><xsl:value-of select="@id" /></xsl:variable>
	    <xsl:if test="content">
		<xsl:value-of select="content" disable-output-escaping="yes" />
	    </xsl:if>	
	    <xsl:if test="col">
		<tr>
                <xsl:if test="@color">
                    <xsl:attribute name="bgcolor"><xsl:value-of select="@color" /></xsl:attribute>
                </xsl:if>
		<xsl:for-each select="col">
		<xsl:choose>
		    <xsl:when test="@grp">
			<xsl:if test="@grp &gt; 0">
			<td>
			        <xsl:if test="@grp &gt; 1">
				    <xsl:attribute name="rowspan">
					<xsl:value-of select="@grp" />
				    </xsl:attribute>
				</xsl:if>
				<xsl:attribute name="align">
					<xsl:value-of select="@align" />
				</xsl:attribute>				
				<xsl:choose>
					<xsl:when test="@href">
				    		<a>
							<xsl:attribute name="href">
							    <xsl:value-of select="@href" disable-output-escaping="yes"/>
							</xsl:attribute>
							<div>
                        			        <xsl:if test="@style">
				                        	<xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                			</xsl:if>
				    			<xsl:value-of select="." disable-output-escaping="yes" />
							</div>
				    		</a>  
		    			</xsl:when>
		    			<xsl:otherwise>	
						<div>
                        			<xsl:if test="@style">
				                       	<xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                		</xsl:if>		
				    		<xsl:value-of select="." disable-output-escaping="yes" />  
						</div>
		    			</xsl:otherwise>				
		    		</xsl:choose>
			</td>
			</xsl:if>
		    </xsl:when>
		    <xsl:otherwise>
			<td>
				<xsl:if test="$rowid mod 2 = 0">
					<xsl:attribute name="class">data alt</xsl:attribute>					
				</xsl:if>

				<xsl:attribute name="align">
					<xsl:value-of select="@align" />
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="@href">
				    		<a>
							<xsl:attribute name="href">
								<xsl:value-of select="@href" disable-output-escaping="yes" />
							</xsl:attribute>				    		
							<div>
                        			        <xsl:if test="@style">
				                        	<xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                			</xsl:if>
				    			<xsl:value-of select="." disable-output-escaping="yes" />
							</div>
				    		</a>  
		    			</xsl:when>
		    			<xsl:otherwise>	
						<div>
                        			<xsl:if test="@style">
				                       	<xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                		</xsl:if>			
				    		<xsl:value-of select="." disable-output-escaping="yes" />  
						</div>
		    			</xsl:otherwise>				
		    		</xsl:choose>
			</td>
		    </xsl:otherwise>
		</xsl:choose>
		</xsl:for-each>
		</tr>
	    </xsl:if>
	</xsl:for-each>
        </table>
       </td>
   </xsl:for-each>
   </tr>
</xsl:when>
<xsl:otherwise>
<tr>
<td>
 <xsl:choose>
    <xsl:when test="@form">
        <table border="1" width="100%" cellspacing="0" cellpadding="2" class="editor">
	<xsl:for-each select="row">
	    <xsl:variable name="rowid"><xsl:value-of select="@id" /></xsl:variable>
	    <xsl:variable name="colcount"><xsl:value-of select="count(col)" /></xsl:variable>
	    <xsl:for-each select="col">
		<xsl:variable name="colid"><xsl:value-of select="@id" /></xsl:variable>
		<xsl:variable name="label"><xsl:value-of select="../../head/col[@id=$colid]/label" /></xsl:variable>
		<tr>
	    	   <xsl:if test="@id = 1">
			<td width="10%" align="center">
				<xsl:attribute name="rowspan">
					<xsl:value-of select="$colcount" />
				</xsl:attribute>
				<xsl:value-of select="$rowid"/>
			</td>
	    	   </xsl:if>
		   <td width="20%" align="center"><xsl:value-of select="$label" disable-output-escaping="yes" /></td>
		   <td><xsl:value-of select="." disable-output-escaping="yes" /></td>
	        </tr>
            </xsl:for-each>
	</xsl:for-each>
        </table>
    </xsl:when>
    <xsl:when test="@edit">
        <table border="0" width="100%" cellspacing="0" cellpadding="1" class="editor">
	<xsl:for-each select="row">
 	    <form method="post">
	    <xsl:attribute name="action"><xsl:value-of select="//param[@id='REQUEST.FILE']" /></xsl:attribute>
	    <xsl:for-each select="col">
		<xsl:variable name="colid"><xsl:value-of select="@id" /></xsl:variable>
		<xsl:variable name="label"><xsl:value-of select="../../head/col[@id=$colid]/super" /></xsl:variable>
		<xsl:variable name="fname"><xsl:value-of select="../../head/col[@id=$colid]/name" /></xsl:variable>
		<xsl:variable name="fsize"><xsl:value-of select="../../head/col[@id=$colid]/@len" /></xsl:variable>
		<xsl:variable name="iskey"><xsl:value-of select="../../head/col[@id=$colid]/@pk" /></xsl:variable>
		<xsl:variable name="formt"><xsl:value-of select="../../head/col[@id=$colid]/@form" /></xsl:variable>
                <xsl:variable name="cssstyle"><xsl:value-of select="../../head/col[@id=$colid]/@formstyle" /></xsl:variable>
		<tr>
		   <td width="20%" align="center"><xsl:value-of select="$label" disable-output-escaping="yes" /></td>
		   <td>
			<xsl:if test="$formt = 'text'">
                           <xsl:choose>
                           <xsl:when test="@htmlcode">
                           <select>
			      <xsl:attribute name="name"><xsl:value-of select="$fname" /></xsl:attribute>
                              <xsl:if test="$cssstyle">
                                      <xsl:attribute name="style"><xsl:value-of select="$cssstyle" /></xsl:attribute>
                              </xsl:if>
                              <xsl:value-of select="@htmlcode" disable-output-escaping="yes" />
			   </select>
                           </xsl:when>
                           <xsl:otherwise>
			   <input type="text">
				<xsl:attribute name="name"><xsl:value-of select="$fname" /></xsl:attribute>
				<xsl:attribute name="size"><xsl:value-of select="$fsize" /></xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="." disable-output-escaping="yes" /></xsl:attribute>
				<xsl:if test="$iskey = 'yes'">
					<xsl:attribute name="readonly">yes</xsl:attribute>
				</xsl:if>
                                <xsl:if test="$cssstyle">
                                        <xsl:attribute name="style"><xsl:value-of select="$cssstyle" /></xsl:attribute>
                                </xsl:if>
			   </input>
                           </xsl:otherwise>
                           </xsl:choose>
			</xsl:if>
			<xsl:if test="$formt = 'textarea'">
			   <textarea rows="15" cols="80">
				<xsl:attribute name="name"><xsl:value-of select="$fname" /></xsl:attribute>
                                <xsl:if test="$cssstyle">
                                        <xsl:attribute name="style"><xsl:value-of select="$cssstyle" /></xsl:attribute>
                                </xsl:if>
				<xsl:value-of select="." disable-output-escaping="yes" />
			   </textarea>
			</xsl:if>
		   </td>
	        </tr>
            </xsl:for-each>
	    <tr><td colspan="2" align="center"><input type="submit" name="sqleditmode" value="Update" /></td></tr>
	    </form>
	</xsl:for-each>
        <xsl:if test="//param[@id='EDIT.INSERT']">
	<xsl:for-each select="head">
 	    <form method="post">
	    <xsl:attribute name="action"><xsl:value-of select="//param[@id='REQUEST.FILE']" /></xsl:attribute>
	    <xsl:for-each select="col">
		<xsl:variable name="colid"><xsl:value-of select="@id" /></xsl:variable>
		<xsl:variable name="label"><xsl:value-of select="../../head/col[@id=$colid]/super" /></xsl:variable>
		<xsl:variable name="fname"><xsl:value-of select="../../head/col[@id=$colid]/name" /></xsl:variable>
		<xsl:variable name="fsize"><xsl:value-of select="../../head/col[@id=$colid]/@len" /></xsl:variable>
		<xsl:variable name="formt"><xsl:value-of select="../../head/col[@id=$colid]/@form" /></xsl:variable>
                <xsl:variable name="cssstyle"><xsl:value-of select="../../head/col[@id=$colid]/@formstyle" /></xsl:variable>
		<tr>
		   <td width="20%" align="center"><xsl:value-of select="$label" disable-output-escaping="yes" /></td>
		   <td>
			<xsl:if test="$formt = 'text'">
                           <xsl:choose>
                           <xsl:when test="@htmlcode">
                           <select>
                              <xsl:attribute name="name"><xsl:value-of select="$fname" /></xsl:attribute>
                              <xsl:if test="$cssstyle">
                                      <xsl:attribute name="style"><xsl:value-of select="$cssstyle" /></xsl:attribute>
                              </xsl:if>
                              <xsl:value-of select="@htmlcode" disable-output-escaping="yes" />
                           </select>
                           </xsl:when>
                           <xsl:otherwise>
			   <input type="text">
				<xsl:attribute name="name"><xsl:value-of select="$fname" /></xsl:attribute>
				<xsl:attribute name="size"><xsl:value-of select="$fsize" /></xsl:attribute>
                                <xsl:if test="$cssstyle">
                                        <xsl:attribute name="style"><xsl:value-of select="$cssstyle" /></xsl:attribute>
                                </xsl:if>
			   </input>
                           </xsl:otherwise>
			   </xsl:choose>
			</xsl:if>
			<xsl:if test="$formt = 'textarea'">
			   <textarea rows="15" cols="80">
				<xsl:attribute name="name"><xsl:value-of select="$fname" /></xsl:attribute>
                                <xsl:if test="$cssstyle">
                                        <xsl:attribute name="style"><xsl:value-of select="$cssstyle" /></xsl:attribute>
                                </xsl:if>
			   </textarea>
			</xsl:if>
		   </td>
	        </tr>
            </xsl:for-each>
	    <tr><td colspan="2" align="center"><input type="submit" name="sqleditmode" value="Insert" /></td></tr>
	    </form>
	</xsl:for-each>
        </xsl:if>
        </table>
    </xsl:when>
    <xsl:otherwise>
        <table border="1" width="100%" cellspacing="0" cellpadding="2" class="data">
	<xsl:for-each select="head">
	   <xsl:if test="content">
		<xsl:value-of select="content" disable-output-escaping="yes" />
	   </xsl:if>	
	   <xsl:if test="col">
                <tr>
                <xsl:for-each select="col">
                    <xsl:if test="super">
                    <th>
                        <xsl:if test="colspan">
                            <xsl:attribute name="colspan"><xsl:value-of select="colspan" /></xsl:attribute>
                        </xsl:if>
                        <xsl:if test="rowspan">
                            <xsl:attribute name="rowspan"><xsl:value-of select="rowspan" /></xsl:attribute>
        		    <xsl:attribute name="width"><xsl:value-of select="@size" /></xsl:attribute>
                        </xsl:if>
                        <xsl:value-of select="super" disable-output-escaping="yes" />
                    </th>
                    </xsl:if>
                </xsl:for-each>
                </tr>
		<tr>
  		<xsl:for-each select="col">
		<xsl:if test="label">
	    	<th>
        		<xsl:attribute name="width">
				<xsl:value-of select="@size" />
			</xsl:attribute>
			<xsl:choose>
			<xsl:when test="formater">
			    <xsl:value-of select="formater" disable-output-escaping="yes" />
			</xsl:when>
			<xsl:otherwise>	
			    <xsl:value-of select="label" />
			</xsl:otherwise>	
			</xsl:choose>
		</th>
		</xsl:if>
		</xsl:for-each>
		</tr>
	   </xsl:if>
	</xsl:for-each>
	<xsl:for-each select="row">
	    <xsl:variable name="rowid"><xsl:value-of select="@id" /></xsl:variable>
	    <xsl:if test="content">
		<xsl:value-of select="content" disable-output-escaping="yes" />
	    </xsl:if>	
	    <xsl:if test="col">
		<tr>
                <xsl:if test="@color">
                    <xsl:attribute name="bgcolor"><xsl:value-of select="@color" /></xsl:attribute>
                </xsl:if>
		<xsl:for-each select="col">
		<xsl:choose>
		    <xsl:when test="@grp">
			<xsl:if test="@grp &gt; 0">
			<td>
			        <xsl:if test="@grp &gt; 1">
				    <xsl:attribute name="rowspan">
					<xsl:value-of select="@grp" />
				    </xsl:attribute>
				</xsl:if>
				<xsl:attribute name="align">
					<xsl:value-of select="@align" />
				</xsl:attribute>

				<xsl:choose>
					<xsl:when test="@href">
				    		<a>
							<xsl:attribute name="href">
							    <xsl:value-of select="@href" disable-output-escaping="yes" />
							</xsl:attribute>
							<div>
                        			        <xsl:if test="@style">
				                        	<xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                			</xsl:if>				    		
				    			<xsl:value-of select="." disable-output-escaping="yes" />
							</div>
				    		</a>  
		    			</xsl:when>
		    			<xsl:otherwise>
						<div>
                        			<xsl:if test="@style">
				                      	<xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                		</xsl:if>	
				    		<xsl:value-of select="." disable-output-escaping="yes" />  
						</div>
		    			</xsl:otherwise>				
		    		</xsl:choose>
			</td>
			</xsl:if>
		    </xsl:when>
		    <xsl:otherwise>
			<td>
				<xsl:if test="$rowid mod 2 = 0">
					<xsl:attribute name="class">data alt</xsl:attribute>					
				</xsl:if>

				<xsl:attribute name="align">
					<xsl:value-of select="@align" />
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="@href">
				    		<a>
							<xsl:attribute name="href">
								<xsl:value-of select="@href" disable-output-escaping="yes"/>
							</xsl:attribute>				    		
							<div>
                        			        <xsl:if test="@style">
				                        	<xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                			</xsl:if>
				    			<xsl:value-of select="." disable-output-escaping="yes" />
							</div>
				    		</a>  
		    			</xsl:when>
		    			<xsl:otherwise>				
						<div>
                        			<xsl:if test="@style">
				                       	<xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                		</xsl:if>
				    		<xsl:value-of select="." disable-output-escaping="yes" />  
						</div>
		    			</xsl:otherwise>				
		    		</xsl:choose>
			</td>
		    </xsl:otherwise>
		</xsl:choose>
		</xsl:for-each>
		</tr>
	    </xsl:if>
	</xsl:for-each>
	</table>
    </xsl:otherwise>
 </xsl:choose>
</td>
</tr>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="image">
   <div style="float:left;">
   <img border="0" align="center" vspace="0">
	<xsl:attribute name="src">
		<xsl:value-of select="file" disable-output-escaping="yes" />
	</xsl:attribute>
	<xsl:attribute name="usemap">#<xsl:value-of select="image_map/@name" /></xsl:attribute> 
   </img>
   <xsl:for-each select="image_map">
      <map>
	<xsl:attribute name="name">
		<xsl:value-of select="@name" />
        </xsl:attribute>
	<xsl:for-each select="mapitem">
	   <area>
	      <xsl:attribute name="title"><xsl:value-of select="title" /></xsl:attribute>
	      <xsl:attribute name="shape"><xsl:value-of select="shape" /></xsl:attribute>
	      <xsl:attribute name="coords"><xsl:value-of select="coords" /></xsl:attribute>
	      <xsl:attribute name="href"><xsl:value-of select="href" disable-output-escaping="yes" /></xsl:attribute>
	  </area>
	</xsl:for-each>
      </map>
    </xsl:for-each>
    </div>
</xsl:template>

</xsl:stylesheet>
