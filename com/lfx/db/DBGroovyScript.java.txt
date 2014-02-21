package com.lfx.db;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class DBGroovyScript
{
     private        GroovyShell shell   = null;

     public DBGroovyScript()
     {
         shell   = new GroovyShell(new Binding());
     }
     
     public Object getValue(VariableTable vt, String gscript)
     {
         if (vt != null)
         {
            String varnames[] = vt.getNames();
            for(int i=0;i<varnames.length;i++)
            {
                shell.setVariable(varnames[i], vt.getValue(varnames[i]));
            }
         }
         return shell.evaluate(gscript);
     }

     public Object getValue(VariableTable vt, DBRowCache data, int row, String gscript)
     {
         if (vt != null)
         {
            String varnames[] = vt.getNames();
            for(int i=0;i<varnames.length;i++)
            {
                shell.setVariable(varnames[i], vt.getValue(varnames[i]));
            }
         }
         if (data != null)
         {
            for(int i=0;i<data.getColumnCount();i++)
            {
                shell.setVariable(data.getColumnName(i+1), data.getItem(row, i+1));
            }
         }
         return shell.evaluate(gscript);
     }
     public Object getValue(DBRowCache data, int row, String gscript)
     {
         if (data != null)
         {
            for(int i=0;i<data.getColumnCount();i++)
            {
                shell.setVariable(data.getColumnName(i+1), data.getItem(row, i+1));
            }
         }
         return shell.evaluate(gscript);
     }
}


