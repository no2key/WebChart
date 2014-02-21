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
		root				: 'sysstatic.rhtml?res=iconbase',
		folder			: 'sysstatic.rhtml?res=iconfolder',
		folderOpen	: 'sysstatic.rhtml?res=iconfolderopen',
		node				: 'sysstatic.rhtml?res=iconpage',
		empty				: 'sysstatic.rhtml?res=iconempty',
		line				: 'sysstatic.rhtml?res=iconline',
		join				: 'sysstatic.rhtml?res=iconjoin',
		joinBottom	: 'sysstatic.rhtml?res=iconjoinbottom',
		plus				: 'sysstatic.rhtml?res=iconplus',
		plusBottom	: 'sysstatic.rhtml?res=iconplusbottom',
		minus				: 'sysstatic.rhtml?res=iconminus',
		minusBottom	: 'sysstatic.rhtml?res=iconminusbottom',
		nlPlus			: 'sysstatic.rhtml?res=iconnolines_plus',
		nlMinus			: 'sysstatic.rhtml?res=iconnolines_minus'
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


//** Chrome Drop Down Menu- Author: Dynamic Drive (http://www.dynamicdrive.com)

//** Updated: July 14th 06' to v2.0
	//1) Ability to "left", "center", or "right" align the menu items easily, just by modifying the CSS property "text-align".
	//2) Added an optional "swipe down" transitional effect for revealing the drop down menus.
	//3) Support for multiple Chrome menus on the same page.

//** Updated: Nov 14th 06' to v2.01- added iframe shim technique

//** Updated: July 23rd, 08 to v2.4
	//1) Main menu items now remain "selected" (CSS class "selected" applied) when user moves mouse into corresponding drop down menu. 
	//2) Adds ability to specify arbitrary HTML that gets added to the end of each menu item that carries a drop down menu (ie: a down arrow image).
	//3) All event handlers added to the menu are now unobstrusive, allowing you to define your own "onmouseover" or "onclick" events on the menu items.
	//4) Fixed elusive JS error in FF that sometimes occurs when mouse quickly moves between main menu items and drop down menus

//** Updated: Oct 29th, 08 to v2.5 (only .js file modified from v2.4)
	//1) Added ability to customize reveal animation speed (# of steps)
	//2) Menu now works in IE8 beta2 (a valid doctype at the top of the page is required)

var cssdropdown={
disappeardelay: 250, //set delay in miliseconds before menu disappears onmouseout
dropdownindicator: '<img src="sysstatic.rhtml?res=icondown" border="0" />', //specify full HTML to add to end of each menu item with a drop down menu
enablereveal: [true, 5], //enable swipe effect? [true/false, steps (Number of animation steps. Integer between 1-20. Smaller=faster)]
enableiframeshim: 1, //enable "iframe shim" in IE5.5 to IE7? (1=yes, 0=no)

//No need to edit beyond here////////////////////////

dropmenuobj: null, asscmenuitem: null, domsupport: document.all || document.getElementById, standardbody: null, iframeshimadded: false, revealtimers: {},

getposOffset:function(what, offsettype){
	var totaloffset=(offsettype=="left")? what.offsetLeft : what.offsetTop;
	var parentEl=what.offsetParent;
	while (parentEl!=null){
		totaloffset=(offsettype=="left")? totaloffset+parentEl.offsetLeft : totaloffset+parentEl.offsetTop;
		parentEl=parentEl.offsetParent;
	}
	return totaloffset;
},

css:function(el, targetclass, action){
	var needle=new RegExp("(^|\\s+)"+targetclass+"($|\\s+)", "ig")
	if (action=="check")
		return needle.test(el.className)
	else if (action=="remove")
		el.className=el.className.replace(needle, "")
	else if (action=="add" && !needle.test(el.className))
		el.className+=" "+targetclass
},

showmenu:function(dropmenu, e){
	if (this.enablereveal[0]){
		if (!dropmenu._trueheight || dropmenu._trueheight<10)
			dropmenu._trueheight=dropmenu.offsetHeight
		clearTimeout(this.revealtimers[dropmenu.id])
		dropmenu.style.height=dropmenu._curheight=0
		dropmenu.style.overflow="hidden"
		dropmenu.style.visibility="visible"
		this.revealtimers[dropmenu.id]=setInterval(function(){cssdropdown.revealmenu(dropmenu)}, 10)
	}
	else{
		dropmenu.style.visibility="visible"
	}
	this.css(this.asscmenuitem, "selected", "add")
},

revealmenu:function(dropmenu, dir){
	var curH=dropmenu._curheight, maxH=dropmenu._trueheight, steps=this.enablereveal[1]
	if (curH<maxH){
		var newH=Math.min(curH, maxH)
		dropmenu.style.height=newH+"px"
		dropmenu._curheight= newH + Math.round((maxH-newH)/steps) + 1
	}
	else{ //if done revealing menu
		dropmenu.style.height="auto"
		dropmenu.style.overflow="hidden"
		clearInterval(this.revealtimers[dropmenu.id])
	}
},

clearbrowseredge:function(obj, whichedge){
	var edgeoffset=0
	if (whichedge=="rightedge"){
		var windowedge=document.all && !window.opera? this.standardbody.scrollLeft+this.standardbody.clientWidth-15 : window.pageXOffset+window.innerWidth-15
		var dropmenuW=this.dropmenuobj.offsetWidth
		if (windowedge-this.dropmenuobj.x < dropmenuW)  //move menu to the left?
			edgeoffset=dropmenuW-obj.offsetWidth
	}
	else{
		var topedge=document.all && !window.opera? this.standardbody.scrollTop : window.pageYOffset
		var windowedge=document.all && !window.opera? this.standardbody.scrollTop+this.standardbody.clientHeight-15 : window.pageYOffset+window.innerHeight-18
		var dropmenuH=this.dropmenuobj._trueheight
		if (windowedge-this.dropmenuobj.y < dropmenuH){ //move up?
			edgeoffset=dropmenuH+obj.offsetHeight
			if ((this.dropmenuobj.y-topedge)<dropmenuH) //up no good either?
				edgeoffset=this.dropmenuobj.y+obj.offsetHeight-topedge
		}
	}
	return edgeoffset
},

dropit:function(obj, e, dropmenuID){
	if (this.dropmenuobj!=null) //hide previous menu
		this.hidemenu() //hide menu
	this.clearhidemenu()
	this.dropmenuobj=document.getElementById(dropmenuID) //reference drop down menu
	this.asscmenuitem=obj //reference associated menu item
	this.showmenu(this.dropmenuobj, e)
	this.dropmenuobj.x=this.getposOffset(obj, "left")
	this.dropmenuobj.y=this.getposOffset(obj, "top")
	this.dropmenuobj.style.left=this.dropmenuobj.x-this.clearbrowseredge(obj, "rightedge")+"px"
	this.dropmenuobj.style.top=this.dropmenuobj.y-this.clearbrowseredge(obj, "bottomedge")+obj.offsetHeight+1+"px"
	this.positionshim() //call iframe shim function
},

positionshim:function(){ //display iframe shim function
	if (this.iframeshimadded){
		if (this.dropmenuobj.style.visibility=="visible"){
			this.shimobject.style.width=this.dropmenuobj.offsetWidth+"px"
			this.shimobject.style.height=this.dropmenuobj._trueheight+"px"
			this.shimobject.style.left=parseInt(this.dropmenuobj.style.left)+"px"
			this.shimobject.style.top=parseInt(this.dropmenuobj.style.top)+"px"
			this.shimobject.style.display="block"
		}
	}
},

hideshim:function(){
	if (this.iframeshimadded)
		this.shimobject.style.display='none'
},

isContained:function(m, e){
	var e=window.event || e
	var c=e.relatedTarget || ((e.type=="mouseover")? e.fromElement : e.toElement)
	while (c && c!=m)try {c=c.parentNode} catch(e){c=m}
	if (c==m)
		return true
	else
		return false
},

dynamichide:function(m, e){
	if (!this.isContained(m, e)){
		this.delayhidemenu()
	}
},

delayhidemenu:function(){
	this.delayhide=setTimeout("cssdropdown.hidemenu()", this.disappeardelay) //hide menu
},

hidemenu:function(){
	this.css(this.asscmenuitem, "selected", "remove")
	this.dropmenuobj.style.visibility='hidden'
	this.dropmenuobj.style.left=this.dropmenuobj.style.top="-1000px"
	this.hideshim()
},

clearhidemenu:function(){
	if (this.delayhide!="undefined")
		clearTimeout(this.delayhide)
},

addEvent:function(target, functionref, tasktype){
	if (target.addEventListener)
		target.addEventListener(tasktype, functionref, false);
	else if (target.attachEvent)
		target.attachEvent('on'+tasktype, function(){return functionref.call(target, window.event)});
},

startchrome:function(){
	if (!this.domsupport)
		return
	this.standardbody=(document.compatMode=="CSS1Compat")? document.documentElement : document.body
	for (var ids=0; ids<arguments.length; ids++){
		var menuitems=document.getElementById(arguments[ids]).getElementsByTagName("a")
		for (var i=0; i<menuitems.length; i++){
			if (menuitems[i].getAttribute("rel")){
				var relvalue=menuitems[i].getAttribute("rel")
				var asscdropdownmenu=document.getElementById(relvalue)
				this.addEvent(asscdropdownmenu, function(){cssdropdown.clearhidemenu()}, "mouseover")
				this.addEvent(asscdropdownmenu, function(e){cssdropdown.dynamichide(this, e)}, "mouseout")
				this.addEvent(asscdropdownmenu, function(){cssdropdown.delayhidemenu()}, "click")
				try{
					menuitems[i].innerHTML=menuitems[i].innerHTML+" "+this.dropdownindicator
				}catch(e){}
				this.addEvent(menuitems[i], function(e){ //show drop down menu when main menu items are mouse over-ed
					if (!cssdropdown.isContained(this, e)){
						var evtobj=window.event || e
						cssdropdown.dropit(this, evtobj, this.getAttribute("rel"))
					}
				}, "mouseover")
				this.addEvent(menuitems[i], function(e){cssdropdown.dynamichide(this, e)}, "mouseout") //hide drop down menu when main menu items are mouse out
				this.addEvent(menuitems[i], function(){cssdropdown.delayhidemenu()}, "click") //hide drop down menu when main menu items are clicked on
			}
		} //end inner for
	} //end outer for
	if (this.enableiframeshim && document.all && !window.XDomainRequest && !this.iframeshimadded){ //enable iframe shim in IE5.5 thru IE7?
		document.write('<IFRAME id="iframeshim" src="about:blank" frameBorder="0" scrolling="no" style="left:0; top:0; position:absolute; display:none;z-index:90; background: transparent;"></IFRAME>')
		this.shimobject=document.getElementById("iframeshim") //reference iframe object
		this.shimobject.style.filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)'
		this.iframeshimadded=true
	}
} //end startchrome

}

