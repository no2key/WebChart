package com.lfx.web;

import com.lfx.db.*;

public final class ChartColumn
{
    private java.util.Vector ycols = new java.util.Vector();
    private java.util.Vector types = new java.util.Vector();
    private java.util.Vector subtypes = new java.util.Vector();
    private java.util.Vector subtypes2 = new java.util.Vector();

    public ChartColumn(String y[], String t1[], String t2[], String t3[])
    {
         for(int i=0; i<y.length; i++)
	 {
              java.util.Vector temp = TextUtils.getWords(y[i],",");
	      for(int j=0;j<temp.size();j++)
	      {
	           ycols.add(temp.elementAt(j));
		   if (i<t1.length) types.add(t1[i]);
		   if (i<t2.length) subtypes.add(t2[i]);
		   if (i<t3.length) subtypes2.add(t3[i]);
	      }
	 }
    }
    public final String[] getColumns()
    {
         return TextUtils.toStringArray(ycols);
    }
    public final String[] getTypes()
    {
         return TextUtils.toStringArray(types);
    }
    public final String[] getSubTypes()
    {
         return TextUtils.toStringArray(subtypes);
    }
    public final String[] getSubTypes2()
    {
         return TextUtils.toStringArray(subtypes2);
    }
}