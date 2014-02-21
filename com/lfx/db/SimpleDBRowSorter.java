package com.lfx.db;

public final class SimpleDBRowSorter implements sun.misc.Compare
{
	private DBRowCache sortdata    = null;
	private Integer    sortrows[]  = null;
	private String     sortcols[]    = null;
	private boolean    sortorder   = true;
	
	public int doCompare(java.lang.Object row1,java.lang.Object row2)
	{
		 int rtn=0;
		 if (sortcols == null || sortcols.length == 0) return 0;
		 if (sortcols.length < 2)
		 {
		    Object colval1 = null;
    		    Object colval2 = null;
   		    colval1 = sortdata.getItem(((Integer)row1).intValue(), sortcols[0]);
    		    colval2 = sortdata.getItem(((Integer)row2).intValue(), sortcols[0]);	
		    if (colval1 != null && colval2 != null)
		    {
			rtn= ((java.lang.Comparable)colval1).compareTo((java.lang.Comparable)colval2);
		    }
		    else
		    {
       			if (colval1 != null) rtn=-1;
		 	if (colval2 != null) rtn= 1;
		    }
		 }
		 else
		 {
		    for(int i=0; i< sortcols.length; i++)
		    {
		        Object colval1 = null;
    		        Object colval2 = null;
   		        colval1 = sortdata.getItem(((Integer)row1).intValue(), sortcols[i]);
    		        colval2 = sortdata.getItem(((Integer)row2).intValue(), sortcols[i]);	
		        if (colval1 != null && colval2 != null)
		        {
			    rtn= ((java.lang.Comparable)colval1).compareTo((java.lang.Comparable)colval2);
		        }
		        else
		        {
       			    if (colval1 != null) rtn=-1;
		 	    if (colval2 != null) rtn= 1;
		        }
			if (rtn != 0) break;
		    }
		 }
		 return (sortorder?rtn:-rtn);
	} 	

	public Integer[] quicksort(DBRowCache data, int rows[], String cols)
	{
		return quicksort(data,rows,cols,true);
	}

	public Integer[] quicksort(DBRowCache data, int rows[], String cols, boolean sord)
	{
		sortcols = new String[1];
		sortcols[0] = cols;
		sortdata = data;
		sortrows = new Integer[rows.length];
		for(int i=0;i<rows.length; i++)
			sortrows[i] = new Integer(rows[i]);
		sortorder = sord;
		
		sun.misc.Sort.quicksort(sortrows,this);
		
		return sortrows;
	}	
	public Integer[] quicksort(DBRowCache data, int rows[], String cols[])
	{
		return quicksort(data,rows,cols,true);
	}

	public Integer[] quicksort(DBRowCache data, int rows[], String cols[], boolean sord)
	{
		sortdata = data;
		sortcols = cols;
		sortrows = new Integer[rows.length];
		for(int i=0;i<rows.length; i++)
			sortrows[i] = new Integer(rows[i]);
		sortorder = sord;
		
		sun.misc.Sort.quicksort(sortrows,this);
		
		return sortrows;
	}	
}