package com.lfx.web;

import com.lfx.db.DBRowCache;
import com.lfx.db.VariableTable;
import com.lfx.db.TextUtils;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;


public class DBRowCacheURLGenerator implements CategoryURLGenerator, XYURLGenerator, CategoryToolTipGenerator, XYToolTipGenerator, PieURLGenerator, PieToolTipGenerator
{
	private DBRowCache data = null;
	private VariableTable vt = null;
	private int xcolid = 0;
	private int ycolid[] = {};

	public DBRowCacheURLGenerator(DBRowCache p_data, VariableTable p_vt, String p_xcol, String p_ycol)
	{
		data = p_data;
		vt = p_vt;	
		xcolid = data.findColumn(p_xcol);
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(p_ycol,","));
		if (ycolumns.length > 0)
		{
			ycolid = new int [ ycolumns.length];
			for(int i =0;i<ycolumns.length;i++)
			{
				ycolid[i] = data.findColumn(ycolumns[i]);	
			}	
		}
	}
	
	public DBRowCacheURLGenerator(DBRowCache p_data, VariableTable p_vt, int p_xcol, int p_ycol[])
	{
		data = p_data;
		vt = p_vt;	
		xcolid = p_xcol;
		ycolid = p_ycol;
	}
	
	public java.lang.String generateURL(org.jfree.data.category.CategoryDataset dataset, int col, int row)
	{
		String url_return = "#myanchor";
		if (col < ycolid.length)
		{
			if (data.getColumnMemo(ycolid[col]) != null)
			{
				url_return = data.parseString(data.getColumnMemo(ycolid[col]), vt,  row+1, 1);
			}
		}
		return url_return;
	}
	
	public java.lang.String generateURL(org.jfree.data.xy.XYDataset dataset, int col, int row)
	{
		String url_return = "#myanchor";
		if (col < ycolid.length)
		{
			if (data.getColumnMemo(ycolid[col]) != null)
			{
				url_return = data.parseString(data.getColumnMemo(ycolid[col]), vt,  row+1, 1);
			}
		}
		return url_return;		
	}

        public java.lang.String generateURL(org.jfree.data.general.PieDataset dataset, java.lang.Comparable key, int row)
	{
		String url_return = "#myanchor";
		if (ycolid.length>0)
		{
			if (data.getColumnMemo(ycolid[0]) != null)
			{
				url_return = data.parseString(data.getColumnMemo(ycolid[0]), vt,  dataset.getIndex(key)+1, 1);
			}
		}
		return url_return;		
	}

	public java.lang.String generateToolTip(org.jfree.data.general.PieDataset dataset, java.lang.Comparable key)
	{
        	String str_tooltip = "AnySQL DataReport";
        	if (ycolid.length > 0)
        	{
			int row = dataset.getIndex(key);
			if (data.getColumnTooltip(ycolid[0]) == null)
			{
        			if (data.getItem(row+1, ycolid[0]) != null)
        				str_tooltip = data.getItem(row+1, xcolid).toString() + "=" + data.getItem(row+1, ycolid[0]).toString();
			}
			else
			{
				str_tooltip = data.parseString(data.getColumnTooltip(ycolid[0]), vt, row+1,1);
			}
        	}
        	return str_tooltip;
	}

        public java.lang.String generateToolTip(org.jfree.data.category.CategoryDataset dataset, int col, int row)
        {
        	String str_tooltip = "AnySQL DataReport";
        	if (col < ycolid.length)
        	{
			if (data.getColumnTooltip(ycolid[col]) == null)
			{
        			if (data.getItem(row+1, ycolid[col]) != null)
        				str_tooltip = data.getItem(row+1, xcolid).toString() + "=" + data.getItem(row+1, ycolid[col]).toString();
			}
			else
			{
				str_tooltip = data.parseString(data.getColumnTooltip(ycolid[col]), vt, row+1,1);
			}
        	}
        	return str_tooltip;
        }

        public java.lang.String generateToolTip(org.jfree.data.xy.XYDataset dataset, int col, int row)
        {
        	String str_tooltip = "AnySQL DataReport";
        	if (col < ycolid.length)
        	{
			if (data.getColumnTooltip(ycolid[col]) == null)
			{
        			if (data.getItem(row+1, ycolid[col]) != null)
        				str_tooltip = data.getItem(row+1, xcolid).toString() + "=" + data.getItem(row+1, ycolid[col]).toString();
			}
			else
			{
				str_tooltip = data.parseString(data.getColumnTooltip(ycolid[col]), vt, row+1,1);
			}
        	}
		return str_tooltip;        	
        }
	
}