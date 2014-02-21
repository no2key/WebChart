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
<script type="text/javascript">
<xsl:comment>
<![CDATA[
function Node(id, pid, name, url, title, target, icon, iconOpen, open) {
	this.id = id;
	this.pid = pid;
	this.name = name;
	this.url = url;
	this.title = title;
	this.target = target;
	this.icon = icon;
	this.iconOpen = iconOpen;
	this._io = open || false;
	this._is = false;
	this._ls = false;
	this._hc = false;
	this._ai = 0;
	this._p;
};
function dTree(objName) {
	this.config = {
		target					: null,
		folderLinks			: true,
		useSelection		: true,
		useCookies			: true,
		useLines				: true,
		useIcons				: true,
		useStatusText		: false,
		closeSameLevel	: false,
		inOrder					: false
	}
	this.icon = {
		root				: 'img/base.gif',
		folder			: 'img/folder.gif',
		folderOpen	: 'img/folderopen.gif',
		node				: 'img/page.gif',
		empty				: 'img/empty.gif',
		line				: 'img/line.gif',
		join				: 'img/join.gif',
		joinBottom	: 'img/joinbottom.gif',
		plus				: 'img/plus.gif',
		plusBottom	: 'img/plusbottom.gif',
		minus				: 'img/minus.gif',
		minusBottom	: 'img/minusbottom.gif',
		nlPlus			: 'img/nolines_plus.gif',
		nlMinus			: 'img/nolines_minus.gif'
	};
	this.obj = objName;
	this.aNodes = [];
	this.aIndent = [];
	this.root = new Node(-1);
	this.selectedNode = null;
	this.selectedFound = false;
	this.completed = false;
};

dTree.prototype.add = function(id, pid, name, url, title, target, icon, iconOpen, open) {
	this.aNodes[this.aNodes.length] = new Node(id, pid, name, url, title, target, icon, iconOpen, open);
};

dTree.prototype.openAll = function() {
	this.oAll(true);
};
dTree.prototype.closeAll = function() {
	this.oAll(false);
};

dTree.prototype.toString = function() {
	var str = '<div class="dtree">\n';
	if (document.getElementById) {
		if (this.config.useCookies) this.selectedNode = this.getSelected();
		str += this.addNode(this.root);
	} else str += 'Browser not supported.';
	str += '</div>';
	if (!this.selectedFound) this.selectedNode = null;
	this.completed = true;
	return str;
};

dTree.prototype.addNode = function(pNode) {
	var str = '';
	var n=0;
	if (this.config.inOrder) n = pNode._ai;
	for (n; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == pNode.id) {
			var cn = this.aNodes[n];
			cn._p = pNode;
			cn._ai = n;
			this.setCS(cn);
			if (!cn.target && this.config.target) cn.target = this.config.target;
			if (cn._hc && !cn._io && this.config.useCookies) cn._io = this.isOpen(cn.id);
			if (!this.config.folderLinks && cn._hc) cn.url = null;
			if (this.config.useSelection && cn.id == this.selectedNode && !this.selectedFound) {
					cn._is = true;
					this.selectedNode = n;
					this.selectedFound = true;
			}
			str += this.node(cn, n);
			if (cn._ls) break;
		}
	}
	return str;
};

dTree.prototype.node = function(node, nodeId) {
	var str = '<div class="dTreeNode">' + this.indent(node, nodeId);
	if (this.config.useIcons) {
		if (!node.icon) node.icon = (this.root.id == node.pid) ? this.icon.root : ((node._hc) ? this.icon.folder : this.icon.node);
		if (!node.iconOpen) node.iconOpen = (node._hc) ? this.icon.folderOpen : this.icon.node;
		if (this.root.id == node.pid) {
			node.icon = this.icon.root;
			node.iconOpen = this.icon.root;
		}
		str += '<img id="i' + this.obj + nodeId + '" src="' + ((node._io) ? node.iconOpen : node.icon) + '" alt="" />';
	}
	if (node.url) {
		str += '<a id="s' + this.obj + nodeId + '" class="' + ((this.config.useSelection) ? ((node._is ? 'nodeSel' : 'node')) : 'node') + '" href="' + node.url + '"';
		if (node.title) str += ' title="' + node.title + '"';
		if (node.target) str += ' target="' + node.target + '"';
		if (this.config.useStatusText) str += ' onmouseover="window.status=\'' + node.name + '\';return true;" onmouseout="window.status=\'\';return true;" ';
		if (this.config.useSelection && ((node._hc && this.config.folderLinks) || !node._hc))
			str += ' onclick="javascript: ' + this.obj + '.s(' + nodeId + ');"';
		str += '>';
	}
	else if ((!this.config.folderLinks || !node.url) && node._hc && node.pid != this.root.id)
		str += '<a href="javascript: ' + this.obj + '.o(' + nodeId + ');" class="node">';
	str += node.name;
	if (node.url || ((!this.config.folderLinks || !node.url) && node._hc)) str += '</a>';
	str += '</div>';
	if (node._hc) {
		str += '<div id="d' + this.obj + nodeId + '" class="clip" style="display:' + ((this.root.id == node.pid || node._io) ? 'block' : 'none') + ';">';
		str += this.addNode(node);
		str += '</div>';
	}
	this.aIndent.pop();
	return str;
};

dTree.prototype.indent = function(node, nodeId) {
	var str = '';
	if (this.root.id != node.pid) {
		for (var n=0; n<this.aIndent.length; n++)
			str += '<img src="' + ( (this.aIndent[n] == 1 && this.config.useLines) ? this.icon.line : this.icon.empty ) + '" alt="" />';
		(node._ls) ? this.aIndent.push(0) : this.aIndent.push(1);
		if (node._hc) {
			str += '<a href="javascript: ' + this.obj + '.o(' + nodeId + ');"><img id="j' + this.obj + nodeId + '" src="';
			if (!this.config.useLines) str += (node._io) ? this.icon.nlMinus : this.icon.nlPlus;
			else str += ( (node._io) ? ((node._ls && this.config.useLines) ? this.icon.minusBottom : this.icon.minus) : ((node._ls && this.config.useLines) ? this.icon.plusBottom : this.icon.plus ) );
			str += '" alt="" /></a>';
		} else str += '<img src="' + ( (this.config.useLines) ? ((node._ls) ? this.icon.joinBottom : this.icon.join ) : this.icon.empty) + '" alt="" />';
	}
	return str;
};

dTree.prototype.setCS = function(node) {
	var lastId;
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == node.id) node._hc = true;
		if (this.aNodes[n].pid == node.pid) lastId = this.aNodes[n].id;
	}
	if (lastId==node.id) node._ls = true;
};

dTree.prototype.getSelected = function() {
	var sn = this.getCookie('cs' + this.obj);
	return (sn) ? sn : null;
};

dTree.prototype.s = function(id) {
	if (!this.config.useSelection) return;
	var cn = this.aNodes[id];
	if (cn._hc && !this.config.folderLinks) return;
	if (this.selectedNode != id) {
		if (this.selectedNode || this.selectedNode==0) {
			eOld = document.getElementById("s" + this.obj + this.selectedNode);
			eOld.className = "node";
		}
		eNew = document.getElementById("s" + this.obj + id);
		eNew.className = "nodeSel";
		this.selectedNode = id;
		if (this.config.useCookies) this.setCookie('cs' + this.obj, cn.id);
	}
};

dTree.prototype.o = function(id) {
	var cn = this.aNodes[id];
	this.nodeStatus(!cn._io, id, cn._ls);
	cn._io = !cn._io;
	if (this.config.closeSameLevel) this.closeLevel(cn);
	if (this.config.useCookies) this.updateCookie();
};

dTree.prototype.oAll = function(status) {
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n]._hc && this.aNodes[n].pid != this.root.id) {
			this.nodeStatus(status, n, this.aNodes[n]._ls)
			this.aNodes[n]._io = status;
		}
	}
	if (this.config.useCookies) this.updateCookie();
};

dTree.prototype.openTo = function(nId, bSelect, bFirst) {
	if (!bFirst) {
		for (var n=0; n<this.aNodes.length; n++) {
			if (this.aNodes[n].id == nId) {
				nId=n;
				break;
			}
		}
	}
	var cn=this.aNodes[nId];
	if (cn.pid==this.root.id || !cn._p) return;
	cn._io = true;
	cn._is = bSelect;
	if (this.completed && cn._hc) this.nodeStatus(true, cn._ai, cn._ls);
	if (this.completed && bSelect) this.s(cn._ai);
	else if (bSelect) this._sn=cn._ai;
	this.openTo(cn._p._ai, false, true);
};

dTree.prototype.closeLevel = function(node) {
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == node.pid && this.aNodes[n].id != node.id && this.aNodes[n]._hc) {
			this.nodeStatus(false, n, this.aNodes[n]._ls);
			this.aNodes[n]._io = false;
			this.closeAllChildren(this.aNodes[n]);
		}
	}
}

dTree.prototype.closeAllChildren = function(node) {
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == node.id && this.aNodes[n]._hc) {
			if (this.aNodes[n]._io) this.nodeStatus(false, n, this.aNodes[n]._ls);
			this.aNodes[n]._io = false;
			this.closeAllChildren(this.aNodes[n]);		
		}
	}
}

dTree.prototype.nodeStatus = function(status, id, bottom) {
	eDiv	= document.getElementById('d' + this.obj + id);
	eJoin	= document.getElementById('j' + this.obj + id);
	if (this.config.useIcons) {
		eIcon	= document.getElementById('i' + this.obj + id);
		eIcon.src = (status) ? this.aNodes[id].iconOpen : this.aNodes[id].icon;
	}
	eJoin.src = (this.config.useLines)?
	((status)?((bottom)?this.icon.minusBottom:this.icon.minus):((bottom)?this.icon.plusBottom:this.icon.plus)):
	((status)?this.icon.nlMinus:this.icon.nlPlus);
	eDiv.style.display = (status) ? 'block': 'none';
};

dTree.prototype.clearCookie = function() {
	var now = new Date();
	var yesterday = new Date(now.getTime() - 1000 * 60 * 60 * 24);
	this.setCookie('co'+this.obj, 'cookieValue', yesterday);
	this.setCookie('cs'+this.obj, 'cookieValue', yesterday);
};

dTree.prototype.setCookie = function(cookieName, cookieValue, expires, path, domain, secure) {
	document.cookie =
		escape(cookieName) + '=' + escape(cookieValue)
		+ (expires ? '; expires=' + expires.toGMTString() : '')
		+ (path ? '; path=' + path : '')
		+ (domain ? '; domain=' + domain : '')
		+ (secure ? '; secure' : '');
};

dTree.prototype.getCookie = function(cookieName) {
	var cookieValue = '';
	var posName = document.cookie.indexOf(escape(cookieName) + '=');
	if (posName != -1) {
		var posValue = posName + (escape(cookieName) + '=').length;
		var endPos = document.cookie.indexOf(';', posValue);
		if (endPos != -1) cookieValue = unescape(document.cookie.substring(posValue, endPos));
		else cookieValue = unescape(document.cookie.substring(posValue));
	}
	return (cookieValue);
};

dTree.prototype.updateCookie = function() {
	var str = '';
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n]._io && this.aNodes[n].pid != this.root.id) {
			if (str) str += '.';
			str += this.aNodes[n].id;
		}
	}
	this.setCookie('co' + this.obj, str);
};

dTree.prototype.isOpen = function(id) {
	var aOpen = this.getCookie('co' + this.obj).split('.');
	for (var n=0; n<aOpen.length; n++)
		if (aOpen[n] == id) return true;
	return false;
};

if (!Array.prototype.push) {
	Array.prototype.push = function array_push() {
		for(var i=0;i<arguments.length;i++)
			this[this.length]=arguments[i];
		return this.length;
	}
};
if (!Array.prototype.pop) {
	Array.prototype.pop = function array_pop() {
		lastElement = this[this.length-1];
		this.length = Math.max(this.length-1,0);
		return lastElement;
	}
};
]]>
</xsl:comment>
</script>
<style type="text/css">
<xsl:comment>
<![CDATA[
body {
        font: 90%/150% Tahoma, "Trebuchet MS",  Arial;
        color: #333333;
        background: #f0f0f0;
        margin: 0px auto;
        padding: 0px;
        text-align: center;
}

#container{
        margin: 0px auto;
        padding: 0px;
        width: 1100px;
        border-left: 1px dotted #3cbcfe;
        border-right: 1px dotted #3cbcfe;
        text-align: left;
}


a {
        color: #0082FF;
        text-decoration: none;
}

a:visited {
        color: #0082FF;
        text-decoration: none;
}

a:hover {
        color: #0082FF;
        background:
        border-bottom: 1px solid #0C72A2;
}

img {
        border: none;
}

p {
        padding: 0px 0px 5px;
        margin: 0px;
}

h1 {
        margin: 10px 0 0 0;
        height: 30px;
        font: bold 24px Tahoma,Georgia,"Times New Roman",Times,serif;
        color: #000000;
        text-shadow: #36414d 0 3px 4px;
}

h1 a, h1 a:visited{
        color: #000000;
        text-decoration: none;
}

h1 a:hover{
        color: #000000;
        text-decoration: none;
        border: none;
        background: none;
}

h2 {
        color: #0082FF;
        margin: 0px 0px 5px;
        border-bottom: 1px dotted #4395BC;
        width:100%;
        font: bold 18px Tahoma,Georgia,"Times New Roman",Times,serif;
        padding-bottom: 1px;
}

h2 a, h2 a:visited {
        color: #0082FF;
        text-decoration: none;
}

h2 a:hover {
        color: #0082FF;
        text-decoration: none;
}

h3 {
        font: normal 120%/100% "Trebuchet MS", Tahoma, Arial;
        color: #000000;
        margin: 10px 0px 5px;
}

h4 {
        font: normal 100%/100% "Trebuchet MS", Tahoma, Arial;
        color: #000000;
        margin: 10px 0px 5px;
}

form {
        margin:0px;
        padding:0px;
}

#wrapper {
        background: #FFFFFF;
        border-top: 2px solid #FFFFFF;
        margin: 0px auto;
        width: 100%;
}

#page {
        margin: 0px auto;
        width: 100%;
}

/* header area */
#header {
        background: #ffffff; /* #0C72A2; */
        height: 100px;
        border-top: 2px solid #3cbcfe;
        border-bottom: 2px solid #3cbcfe;
        position: relative;
        color: #000000;
        width: 100%;
        margin: 0px auto;
        padding: 10px 0px 0px 0px;
}
#headerimg {
        position: relative;
        padding: 10px 0px 0px 10px;
        height: 60px;
        margin: 0px auto;
        width: 1080px;
}
#headerimg .description{
        padding: 0px 0px 0px 0px;
        position: absolute;
        height: 20px;
        bottom: 3px;
        color: #000000;
        font-size: 14px;
        text-shadow: #36414d 0 2px 3px;
}


#navi {
        height: 30px;
        width: 1080px;
        margin: 0px 0px 0px 0px;
        padding: 0px 0px 0px 0px;
        text-align: center;
        line-height: normal;
}
#nav {
        list-style: none;
        height: 30px;
        margin: 0 3px 0 3px;
        float: right;
}
#nav li {
        float: left;
        height: 30px;
        background: #3cbcfe;
        margin: 0 3px;
        white-space: nowrap;
}
#nav .page_item a{
        color: #000000; /* #FFFFFF; */
        display: block;
        background: #d3e7eF;
        text-decoration: none;
        padding: 0px 10px 0px 10px;
        font: bold 12px Arial, Helvetica, sans-serif;
        line-height: 30px;
}
#nav li:hover {
        background-position: 100% -36px;
}
#nav .page_item a:hover {
        background-position: 0px -36px;
        border: none;
}

#nav li.current_page_item, li.current_page_parent {
        color: #000000;
        text-decoration: none;
        background: #3cbcfe;
}
#nav .current_page_item a, #nav .current_page_item a:visited , #nav .current_page_parent a, #nav .current_page_parent a:visited{
        color: #000000;
        text-decoration: none;
        background: #3cbcfe;
}

#content {
        font-size: 90%;
        padding: 7px 5px 20px 3px;
        float: right;
        width: 900px;
}

#content img {
        padding: 3px;
}

.module-list {
        padding: 0;
        margin-top: 0;
        margin-bottom: 0;
}

.module-list-item {
        margin: 0;
        padding: 0;
}

.prefont {
        font-family: "Courier New", Courier, monospace !important;
        line-height: 140%;
        font-size: 12px !important;
}

.navigation {
        clear: both;
        padding: 10px 0px;
}

.navigation a, .navigation a:visited {
        color: #59770e;
}

#sidebar {
        float: left;
        padding: 10px 3px 20px 5px;
        width: 180px;
        color: #666666;
        overflow: hidden;
}

#sidebar h2, #sidebar .sidebartitle{
        font: bold 1em Tahoma, "Trebuchet MS",  Arial;
        color: #0082FF;
        border-bottom: 1px solid #DBEFF6;
        margin-top: 5px;
}

#sidebar a:hover{
        border: none;
}
#sidebar a, #sidebar a:visited{
        color: #0082FF;
        text-decoration: none;
}
#sidebar li a:hover{
        color: #0082FF;
        border-bottom: 1px solid #789d47;
        text-decoration: none;
}
.widget {
        padding: 5px 10px 10px 10px;
        margin-bottom: 10px;
        border: 1px solid #DBEFF6;
}
#sidebar ul {
        margin: 0;
        padding: 0;
}
#sidebar ul li {
        list-style: none;
        margin: 0;
        padding: 0;
        border: none;
}
#sidebar ul li ul {
        padding: 0;
        margin: 0;
}
#sidebar ul li ul li{
        padding: 2px 0 2px 0px;
        border-bottom: 1px solid #dbefc1;
}
#sidebar ul li ul li ul li{
        padding: 1px 0 1px 10px;
        border: none;
}

#footerbg {
        clear: both;
        background: #FFFFFF; /* url(images/content-bg.gif) repeat-x; */
        border-top: 2px solid #3cbcfe;
        margin: 0px auto;
        padding: 0px;
        width: 100%;
}
#footer {
        width: 100%;
        margin: 0px auto;
        font-size: 95%;
        color: #303030;
        line-height: 130%;
}
#footer a, #footer a:visited {
        color: #0082FF;
}
#footer h4 {
        font: normal 146%/100% "Trebuchet MS", Tahoma, Arial;
        color: #000000;
        margin: 10px 0px 10px;
}

#credits {
        margin: 0px auto;
        width: 100%;
        color: white;
        background: #014e82;
        font-size: 85%;
        line-height: 120%;
        padding: 10px 0px 20px 0px;
}
#credits a, #credit a:visited {
        color: white;
}

.center {
        text-align: center;
}
img.center, img[align="center"] {
        display: block;
        margin-left: auto;
        margin-right: auto;
}
.alignleft {
        float: left;
        padding: 0px 0px 0px 10px;
}

.alignright {
        float: right;
        padding: 0px 10px 0px 0px;
}

.clear {
        clear:both;
}
hr.clear {
        clear:both;
        visibility: hidden;
        margin: 0px;
        padding: 0px;
}


table {
        padding: 0;
        margin: 0;
}

caption {
        padding: 0 0 5px 0;
        font: 10px Tahoma,Georgia,"Times New Roman",Times,serif;
        text-align: center;
}

th {
        font: bold 12px Tahoma,Georgia,"Times New Roman",Times,serif;
        border-right: 1px solid #C1DAD7;
        border-bottom: 1px solid #C1DAD7;
        border-top: 1px solid #C1DAD7;
        padding: 3px 3px 3px 3px;
        background: #CAE8EA  no-repeat;
}

td {
        border-right: 1px solid #C1DAD7;
        border-bottom: 1px solid #C1DAD7;
        background: #f5fafa;
        font-size:12px;
        padding: 3px 3px 3px 3px;
}
.dtree {
	font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: #666;
	white-space: nowrap;
}
.dtree img {
	border: 0px;
	vertical-align: middle;
}
.dtree a {
	color: #333;
	text-decoration: none;
}
.dtree a.node, .dtree a.nodeSel {
	white-space: nowrap;
	padding: 1px 2px 1px 2px;
}
.dtree a.node:hover, .dtree a.nodeSel:hover {
	color: #333;
	text-decoration: underline;
}
.dtree a.nodeSel {
	background-color: #c0d2ec;
}
.dtree .clip {
	overflow: hidden;
}
]]>
</xsl:comment>
</style>
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
    <table border="0" width="100%">
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
</xsl:template>

<xsl:template name="default-dataset" match="dataset" >
<xsl:choose>
<xsl:when test="page">
   <tr>
   <xsl:for-each select="page">
       <td valign="top">
	<table border="1" width="100%" cellspacing="0" cellpadding="2">
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
				<div>
				<xsl:if test="@style">
					<xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>					
				</xsl:if>					
				<xsl:choose>
					<xsl:when test="@href">
				    		<a>
							<xsl:attribute name="href">
							    <xsl:value-of select="@href" disable-output-escaping="yes"/>
							</xsl:attribute>				    		
				    			<xsl:value-of select="." disable-output-escaping="yes" />
				    		</a>  
		    			</xsl:when>
		    			<xsl:otherwise>				
				    		<xsl:value-of select="." disable-output-escaping="yes" />  
		    			</xsl:otherwise>				
		    		</xsl:choose>
				</div>
			</td>
			</xsl:if>
		    </xsl:when>
		    <xsl:otherwise>
			<td>
                                <!--
				<xsl:if test="$rowid mod 2 = 1">
					<xsl:attribute name="bgcolor">#eeeeee</xsl:attribute>					
				</xsl:if>
			        -->
	
				<xsl:attribute name="align">
					<xsl:value-of select="@align" />
				</xsl:attribute>
                                <div>
                                <xsl:if test="@style">
                                        <xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                </xsl:if>
				<xsl:choose>
					<xsl:when test="@href">
				    		<a>
							<xsl:attribute name="href">
								<xsl:value-of select="@href" disable-output-escaping="yes" />
							</xsl:attribute>				    		
				    			<xsl:value-of select="." disable-output-escaping="yes" />
				    		</a>  
		    			</xsl:when>
		    			<xsl:otherwise>				
				    		<xsl:value-of select="." disable-output-escaping="yes" />  
		    			</xsl:otherwise>				
		    		</xsl:choose>
				</div>
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
        <table border="1" width="100%" cellspacing="0" cellpadding="2">
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
        <table border="0" width="100%" cellspacing="0" cellpadding="1">
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
        <table border="1" width="100%" cellspacing="0" cellpadding="2">
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
                                <div>
                                <xsl:if test="@style">
                                        <xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                </xsl:if>
				<xsl:choose>
					<xsl:when test="@href">
				    		<a>
							<xsl:attribute name="href">
							    <xsl:value-of select="@href" disable-output-escaping="yes" />
							</xsl:attribute>				    		
				    			<xsl:value-of select="." disable-output-escaping="yes" />
				    		</a>  
		    			</xsl:when>
		    			<xsl:otherwise>				
				    		<xsl:value-of select="." disable-output-escaping="yes" />  
		    			</xsl:otherwise>				
		    		</xsl:choose>
				</div>
			</td>
			</xsl:if>
		    </xsl:when>
		    <xsl:otherwise>
			<td>
                                <!--
				<xsl:if test="$rowid mod 2 = 1">
					<xsl:attribute name="bgcolor">#eeeeee</xsl:attribute>					
				</xsl:if>
			        -->
	
				<xsl:attribute name="align">
					<xsl:value-of select="@align" />
				</xsl:attribute>
                                <div>
                                <xsl:if test="@style">
                                        <xsl:attribute name="style"><xsl:value-of select="@style" /></xsl:attribute>
                                </xsl:if>
				<xsl:choose>
					<xsl:when test="@href">
				    		<a>
							<xsl:attribute name="href">
								<xsl:value-of select="@href" disable-output-escaping="yes"/>
							</xsl:attribute>				    		
				    			<xsl:value-of select="." disable-output-escaping="yes" />
				    		</a>  
		    			</xsl:when>
		    			<xsl:otherwise>				
				    		<xsl:value-of select="." disable-output-escaping="yes" />  
		    			</xsl:otherwise>				
		    		</xsl:choose>
				</div>
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
