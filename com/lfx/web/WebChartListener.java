package com.lfx.web;
import com.lfx.db.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public final class WebChartListener
    implements HttpSessionListener,ServletContextListener {

    private String admin_db = null;
    private static AutoMarkupThread markupthread = new AutoMarkupThread();
    
    public void contextInitialized(javax.servlet.ServletContextEvent sce)
    {
	int i,k;
	java.util.Vector dblist=null;
	VariableTable vt = new VariableTable();
	vt.loadFile(sce.getServletContext().getRealPath("/WEB-INF/dbconn.cfg"));
	admin_db = vt.getString("ADMINDB");
	ImageCacheMemCached.setServerList(vt.getString("MEMCACHED"));
	ImageCacheMemCached.setFailoverList(vt.getString("MEMCACHED2"));
	ImageCache.setServiceName(sce.getServletContext().getContextPath());
	DBPhysicalManager.loadDBConfig(vt);
	DBLogicalManager.loadDBConfig(vt);
	DBPhysicalManager.ActivePool();
	markupthread.start();
    }
    
    public void contextDestroyed(javax.servlet.ServletContextEvent sce) 
    {
	try {
	    DBPooledConnection db = DBLogicalManager.getPoolConnection(admin_db);
	    try {
		DBOperation.executeUpdate(db,
			"UPDATE WEB_SESSION_LOG SET LOGOUT_TIME=SYSDATE,LOGOUT_ACTION='STOP' "+
			"WHERE (SESSIONID,LOGIN_ID) IN (SELECT SESSIONID,LOGIN_ID FROM WEB_SESSION)");
		DBOperation.executeUpdate(db,
			"DELETE FROM WEB_SESSION");
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
	} catch (Exception e) {};
	markupthread.stop();
	DBPhysicalManager.freeAllConnectionPool();
    }

    public void sessionCreated(HttpSessionEvent event)
    {
	event.getSession().setAttribute("SESSION.ADMINDB",admin_db);
    }

    public void sessionDestroyed(HttpSessionEvent event)
    {
	try {
		DBPooledConnection db = DBLogicalManager.getPoolConnection(admin_db);
		try {
			VariableTable vt = new VariableTable();
			vt.add("SESSIONID",java.sql.Types.VARCHAR);
			vt.setValue("SESSIONID",event.getSession().getId());
			DBOperation.executeUpdate(db,
				"DELETE FROM WEB_SESSION WHERE SESSIONID = :SESSIONID",vt);
			DBOperation.executeUpdate(db,
				"UPDATE WEB_SESSION_LOG SET LOGOUT_TIME = SYSDATE,LOGOUT_ACTION='TIMEOUT' "+
				" WHERE SESSIONID = :SESSIONID AND LOGOUT_ACTION IS NULL",vt);
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
	} catch (Exception e) {}
    }

}
