package com.lfx.db;

public class RowValuesObject extends Object implements java.lang.Cloneable, java.io.Serializable
{
       private int __hashvalue = 0;
       private int __cols[] = null;
       private Object __vals[] = null;

       public RowValuesObject(int _cols[], Object _vals[])
       {
	   __cols = _cols;
	   __vals = _vals;
	   if (_vals != null)
	   {
		for(int i=0;i<_vals.length;i++)
		{
		   if (_vals[i] != null)
		       __hashvalue += _vals[i].toString().hashCode();
		}
	   }
       }

       public int hashCode()
       {
           return __hashvalue;
       }

       public int[] getColumns()
       {
	   return __cols;
       }

       public Object[] getValues()
       {
	   return __vals;
       }

       public boolean equals(java.lang.Object val)
       {
	   if (val == null) return false;
	   if (!getClass().equals(val.getClass())) return false;
	   RowValuesObject newval = (RowValuesObject) val;
	   if (!isSameColumns(newval.getColumns())) return false;
	   if (!isSameValues(newval.getValues())) return false;
	   return true;
       }

       private boolean isSameColumns(int _cols[])
       {
           if (__cols == null)
	   {
		if(_cols == null)
		    return true;
		else 
		    return false;
	   }
	   if (_cols == null) return false;
	   if (__cols.length != _cols.length) return false;
	   for(int i=0;i<__cols.length;i++)
	   {
		if (__cols[i] != _cols[i]) return false;
	   }
	   return true;
       }

       private boolean isSameValues(Object _vals[])
       {
           if (__vals == null)
	   {
		if(_vals == null)
		    return true;
		else 
		    return false;
	   }
	   if (_vals == null) return false;
	   if (__vals.length != _vals.length) return false;
	   for(int i=0;i<__vals.length;i++)
	   {
		if (__vals[i] == null)
		{
		    if (_vals[i] != null) return false;
		}
		else
		{
		    if (!__vals[i].equals(_vals[i])) return false;
		}
	   }
	   return true;           
       }
}
