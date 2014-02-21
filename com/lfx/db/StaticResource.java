package com.lfx.db;
import java.io.*;

public class StaticResource
{
    private static java.util.HashMap<String, StaticResourceItem> resmap = new java.util.HashMap<String, StaticResourceItem>();

    public static void loadContent(String id, int type, String file)
    {
	try {
	    if (id != null && file != null)
	    {
	        int pos=0;
	        byte readbuf[] = new byte[4096];
	        StaticResourceItem item = null;	

	        ByteArrayOutputStream os = new ByteArrayOutputStream(8192);
	        InputStream is = StaticResource.class.getResourceAsStream(file);
	        while((pos = is.read(readbuf)) > 0)
	            os.write(readbuf,0,pos);
                is.close();
	        os.close();

	        if (type == StaticResourceItem.GIF || type == StaticResourceItem.JS || type == StaticResourceItem.CSS)
                    item = new StaticResourceItem(id, type, os.toByteArray());
	        else if (type == StaticResourceItem.XSL)
                    item = new StaticResourceItem(id, type, os.toString());
	        resmap.put(id, item);
	    }
	} catch (java.io.IOException ioe) {}
    }
    
    public static StaticResourceItem getResource(String id)
    {
        if (id != null && resmap.containsKey(id)) return resmap.get(id);
	return null;
    }

    public static String getTextResource(String id)
    {
        StaticResourceItem item = getResource(id);
	if (item != null) return item.getTextContent();
        return null;
    }

    public static byte[] getByteResource(String id)
    {
        StaticResourceItem item = getResource(id);
	if (item != null) return item.getByteContent();
        return null;
    }

    static 
    {
	loadContent("defaultxsl", StaticResourceItem.XSL, "/com/lfx/static/default.xsl");
	loadContent("defaultjs", StaticResourceItem.JS, "/com/lfx/static/default.js");
	loadContent("defaultcss", StaticResourceItem.CSS, "/com/lfx/static/default.css");
	loadContent("iconbase", StaticResourceItem.GIF, "/com/lfx/static/base.gif");
	loadContent("iconcd", StaticResourceItem.GIF, "/com/lfx/static/cd.gif");
	loadContent("iconempty", StaticResourceItem.GIF, "/com/lfx/static/empty.gif");
	loadContent("iconfolder", StaticResourceItem.GIF, "/com/lfx/static/folder.gif");
	loadContent("iconfolderopen", StaticResourceItem.GIF, "/com/lfx/static/folderopen.gif");
	loadContent("iconglobe", StaticResourceItem.GIF, "/com/lfx/static/globe.gif");
	loadContent("iconimgfolder", StaticResourceItem.GIF, "/com/lfx/static/imgfolder.gif");
	loadContent("iconjoin", StaticResourceItem.GIF, "/com/lfx/static/join.gif");
	loadContent("iconjoinbottom", StaticResourceItem.GIF, "/com/lfx/static/joinbottom.gif");
	loadContent("iconline", StaticResourceItem.GIF, "/com/lfx/static/line.gif");
	loadContent("iconminus", StaticResourceItem.GIF, "/com/lfx/static/minus.gif");
	loadContent("iconminusbottom", StaticResourceItem.GIF, "/com/lfx/static/minusbottom.gif");
	loadContent("iconmusicfolder", StaticResourceItem.GIF, "/com/lfx/static/musicfolder.gif");
	loadContent("iconnolinesminus", StaticResourceItem.GIF, "/com/lfx/static/nolines_minus.gif");
	loadContent("iconnolinesplus", StaticResourceItem.GIF, "/com/lfx/static/nolines_plus.gif");
	loadContent("iconpage", StaticResourceItem.GIF, "/com/lfx/static/page.gif");
	loadContent("iconplus", StaticResourceItem.GIF, "/com/lfx/static/plus.gif");
	loadContent("iconplusbottom", StaticResourceItem.GIF, "/com/lfx/static/plusbottom.gif");
	loadContent("iconquestion", StaticResourceItem.GIF, "/com/lfx/static/question.gif");
	loadContent("icontrash", StaticResourceItem.GIF, "/com/lfx/static/trash.gif");
	loadContent("icontrback", StaticResourceItem.GIF, "/com/lfx/static/trback.gif");
	loadContent("icondown", StaticResourceItem.GIF, "/com/lfx/static/down.gif");
	loadContent("iconmenubg", StaticResourceItem.GIF, "/com/lfx/static/menubg.gif");
	loadContent("iconmenubgover", StaticResourceItem.GIF, "/com/lfx/static/menubgover.gif");
	loadContent("jqueryjs", StaticResourceItem.JS, "/com/lfx/static/jquery.js");
	loadContent("jscharts", StaticResourceItem.JS, "/com/lfx/static/jscharts.js");
    }
}