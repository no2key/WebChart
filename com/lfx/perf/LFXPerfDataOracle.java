package com.lfx.perf;
import com.lfx.db.*;

public class LFXPerfDataOracle extends LFXPerfData
{
    private String dbname=null;
    private DBRowCache data = new SimpleDBRowCache();
    private DBRowCache prevdata = null;

    public void getNextValue(String db)
    {
         if (db != null && db.equalsIgnoreCase(dbname))
         {
              if (data.getColumnCount() == 0)
              {
                  data.addColumn("DAY",java.sql.Types.TIMESTAMP);
                  data.addColumn("VAL01",java.sql.Types.INTEGER);
                  data.addColumn("VAL02",java.sql.Types.INTEGER);
                  data.addColumn("VAL03",java.sql.Types.INTEGER);
                  data.addColumn("VAL04",java.sql.Types.INTEGER);
                  data.addColumn("VAL05",java.sql.Types.INTEGER);
                  data.addColumn("VAL06",java.sql.Types.INTEGER);
                  data.addColumn("VAL07",java.sql.Types.INTEGER);
                  data.addColumn("VAL08",java.sql.Types.INTEGER);
                  data.addColumn("VAL09",java.sql.Types.INTEGER);
                  data.addColumn("VAL10",java.sql.Types.INTEGER);
              }
         }
         else if (db != null)
         {
              data.deleteAllRow();
              dbname = db;
         }
         else
         {
              data.deleteAllRow();
              dbname = null;
         }
    }
    public DBRowCache getPerfData()
    {
         return data;
    }
}