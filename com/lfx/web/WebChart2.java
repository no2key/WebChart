package com.lfx.web;
 
import com.lfx.db.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.HorizontalAlignment;
import java.text.NumberFormat;

import org.jfree.data.category.*; 
import org.jfree.data.xy.*;
import org.jfree.chart.title.*; 
import org.jfree.data.time.*;
import org.jfree.data.general.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.axis.*;
import org.jfree.data.gantt.*;
import org.jfree.util.TableOrder;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import java.awt.geom.Line2D;

import com.keypoint.PngEncoder;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.BasicStroke;

public final class WebChart2
{
	public static final int DOT   = 1;
	public static final int LINE  = 2;
	public static final int AREA  = 3;
	public static final int BAR   = 4;
	public static final int STACKBAR = 5;
	public static final int STOCK_HLC = 6;
	public static final int STOCK_OHLC = 7;
	public static final int STOCK = 8;
	public static final int STEP  = 9;
	public static final int DIFF  = 10;
	public static final int STACKAREA = 11;
	public static final int LINE2  = 12;
	public static final int GANTT  = 13;
	public static final int BUBBLE  = 14;
	public static final int LEVEL  = 15;
	public static final int LAYERBAR   = 16;
	public static final int SPIDER   = 17;
	public static final int WATER   = 18;


	public static final int LINE_SOLID  = 1;
	public static final int LINE_DASHED = 2;
	public static final int LINE_DASHED2= 3;
	public static final int LINE_NONE   = 4;
	public static final int LINE_BOLD   = 5;

	public static final int BAR_BASIC      = 4;
	public static final int BAR_SHADOW     = 5;
	public static final int BAR_3D         = 6;

	public static final int PIE_BASIC      = 7;
	public static final int PIE_SHADOW     = 8;
	public static final int PIE_3D         = 9;
	
	public static final int AXIS_Y1	       = 1;
	public static final int AXIS_Y2        = 2;

	public static final int MARKER_NONE    = 1;
	public static final int MARKER_CIRCLE  = 2;
	public static final int MARKER_SQUARE  = 3;
	public static final int MARKER_DIAMOND = 4;
	public static final int MARKER_PLUS    = 5;
	public static final int MARKER_X       = 6;
		
	public static final java.awt.Font SIMHEI18 = new java.awt.Font("Serif",java.awt.Font.PLAIN,18);
	public static final java.awt.Font SIMHEI12 = new java.awt.Font("Serif",java.awt.Font.PLAIN,12);
	public static final java.awt.Font SIMSUN12 = new java.awt.Font("Serif",java.awt.Font.PLAIN,12);
	public static final java.awt.Font SIMSUN10 = new java.awt.Font("Serif",java.awt.Font.PLAIN,10);

	public static final java.awt.Color COLORLIST[] = {
			new java.awt.Color(190,52,5),
			new java.awt.Color(34,99,99),
			new java.awt.Color(249,149,61),
			new java.awt.Color(63,170,67),
			new java.awt.Color(0,73,116),
			new java.awt.Color(171,222,239),
			new java.awt.Color(112,239,239),
			new java.awt.Color(239,179,179),
			new java.awt.Color(179,179,239),
			new java.awt.Color(179,239,179),
			new java.awt.Color(239,239,179),
			new java.awt.Color(239,179,239),
			new java.awt.Color(179,239,239)
		};

	private static  java.awt.Stroke STROKE_LINE_BOLD  = new java.awt.BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static  java.awt.Stroke STROKE_LINE_SOLID = new java.awt.BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static  java.awt.Stroke STROKE_LINE_DASH  = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {10.0f, 6.0f}, 0.0f);
	private static  java.awt.Stroke STROKE_LINE_DASH2 = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {10.0f, 3.0f, 3.0f}, 0.0f);

	private static 	StandardPieSectionLabelGenerator PIE_LABEL_VALUE = new StandardPieSectionLabelGenerator(
				"{1}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());

	private static 	StandardPieSectionLabelGenerator PIE_LABEL_PERCENT = new StandardPieSectionLabelGenerator(
				"{2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());


	private static final int MAX_IMAGE_CSS_ITEMS = 500;


	private static java.awt.Color prevColor(java.awt.Color curr)
	{
		if (curr == null) return null;
		java.awt.Color curr_b = curr.darker();
		return  new java.awt.Color((int)((curr.getRed() + curr_b.getRed())/2),
                                           (int)((curr.getGreen() + curr_b.getGreen())/2),
					   (int)((curr.getBlue() + curr_b.getBlue())/2));
	}

	private static java.awt.Color nextColor(java.awt.Color curr)
	{
		if (curr == null) return null;
		java.awt.Color curr_b = curr.brighter();
		return  new java.awt.Color((int)((curr.getRed() + curr_b.getRed())/2),
                                           (int)((curr.getGreen() + curr_b.getGreen())/2),
					   (int)((curr.getBlue() + curr_b.getBlue())/2));
	}

	public static String   getVariableTableValue(VariableTable vt, String varname, String index, boolean bupper)
	{
		if (vt == null) return null;
		StringBuffer buf = new StringBuffer();
		buf.append("WEBCHART.");
		buf.append(varname);
		buf.append('_');
		buf.append(index);
		String val = vt.getString(buf.toString());
		if (val == null && bupper)
		{
			buf.setLength(0);
			buf.append("WEBCHART.");
			buf.append(varname);
			val = vt.getString(buf.toString());
		}
		return val;
	}
	
	public static String   getVariableTableValue(VariableTable vt, String varname)
	{
		if (vt == null) return null;
		StringBuffer buf = new StringBuffer();
		buf.append("WEBCHART.");
		buf.append(varname);		
		String val = vt.getString(buf.toString());
		return val;		
	}	

	public static void setVariableTableValue(VariableTable vt, String varname, String val)
	{
		if (vt == null) return;
		StringBuffer buf = new StringBuffer();
		buf.append("WEBCHART.");
		buf.append(varname);	
		if (vt.exists(buf.toString()))
		    vt.setValue(buf.toString(), val);
		else
		{
		    vt.add(buf.toString(),java.sql.Types.VARCHAR);
		    vt.setValue(buf.toString(), val);
		}
	}	

	public static boolean  existVariableTableValue(VariableTable vt, String varname)
	{
		if (vt == null) return false;
		StringBuffer buf = new StringBuffer();
		buf.append("WEBCHART.");
		buf.append(varname);	
		return vt.exists(buf.toString());
	}	

	public static boolean  existVariableTableValue(VariableTable vt, String varname, String index)
	{
		if (vt == null) return false;
		StringBuffer buf = new StringBuffer();
		buf.append("WEBCHART.");
		buf.append(varname);
		buf.append('_');
		buf.append(index);
		return vt.exists(buf.toString());
	}	
	
	public static String[] getLines(String src)
	{
		return TextUtils.toStringArray(TextUtils.getLines(src));
	}

	private static int getChartType(String charttype)
        {
		int axis_type = LINE;
 		if ( charttype.equalsIgnoreCase("BAR"))
 			axis_type = BAR;
	 	else if (charttype.equalsIgnoreCase("LINE"))
	 		axis_type = LINE;
	 	else if (charttype.equalsIgnoreCase("DOT"))
	 		axis_type = DOT;
	 	else if (charttype.equalsIgnoreCase("AREA"))
	 		axis_type = AREA;
	 	else if (charttype.equalsIgnoreCase("STACKBAR"))
	 		axis_type = STACKBAR;
	 	else if (charttype.equalsIgnoreCase("STACKAREA"))
	 		axis_type = STACKAREA;
	 	else if (charttype.equalsIgnoreCase("STEP"))
	 		axis_type = STEP;
	 	else if (charttype.equalsIgnoreCase("DIFF"))
	 		axis_type = DIFF;
	 	else if (charttype.equalsIgnoreCase("HLC"))
	 		axis_type = STOCK_HLC;
	 	else if (charttype.equalsIgnoreCase("OHLC"))
	 		axis_type = STOCK_OHLC;
	 	else if (charttype.equalsIgnoreCase("STOCK"))
	 		axis_type = STOCK;
	 	else if (charttype.equalsIgnoreCase("LINE2"))
	 		axis_type = LINE2;
	 	else if (charttype.equalsIgnoreCase("GANTT"))
	 		axis_type = GANTT;
	 	else if (charttype.equalsIgnoreCase("BUBBLE"))
	 		axis_type = BUBBLE;
	 	else if (charttype.equalsIgnoreCase("LEVEL"))
	 		axis_type = LEVEL;
	 	else if (charttype.equalsIgnoreCase("LAYERBAR"))
	 		axis_type = LAYERBAR;
	 	else if (charttype.equalsIgnoreCase("SPIDER"))
	 		axis_type = SPIDER;
	 	else if (charttype.equalsIgnoreCase("WATER"))
	 		axis_type = WATER;
		return axis_type;				
        }

	private static int getChartSubType(int type,String subtype)
	{
		if (subtype != null)
		{
			if (subtype.equals("BAR_BASIC"))
				return BAR_BASIC;
			else if (subtype.equals("BAR_SHADOW"))
				return BAR_SHADOW;
			else if (subtype.equals("BAR_3D"))
				return BAR_3D;
			else if (subtype.equals("LINE_BOLD"))
				return LINE_BOLD;
			else if (subtype.equals("LINE_SOLID"))
				return LINE_SOLID;
			else if (subtype.equals("LINE_DASHED"))
				return LINE_DASHED;
			else if (subtype.equals("LINE_DASHED2"))
				return LINE_DASHED2;
			else if (subtype.equals("LINE_NONE"))
				return LINE_NONE;
			else if (subtype.equals("PIE_BASIC"))
				return PIE_BASIC;
			else if (subtype.equals("PIE_SHADOW"))
				return PIE_SHADOW;
			else if (subtype.equals("PIE_3D"))
				return PIE_3D;
		}
		switch(type)
		{
			case BAR:
			case STACKBAR:
				return BAR_3D;
			case LINE:
			case AREA:
				return LINE_SOLID;
		}
		return PIE_3D;
	}

	private static int getChartSubType2(String subtype)
	{
		if (subtype != null)
		{
			subtype = subtype.toUpperCase();
			if (subtype.equals("MARKER_NONE"))
				return MARKER_NONE;
			else if (subtype.equals("MARKER_CIRCLE"))
				return MARKER_CIRCLE;
			else if (subtype.equals("MARKER_SQUARE"))
				return MARKER_SQUARE;
			else if (subtype.equals("MARKER_DIAMOND"))
				return MARKER_DIAMOND;
			else if (subtype.equals("MARKER_PLUS"))
				return MARKER_PLUS;
			else if (subtype.equals("MARKER_X"))
				return MARKER_X;
		}
		return MARKER_NONE;
	}

	private static java.awt.Stroke getLineStyleStroke(int type)
	{
		switch (type)
		{
			case LINE_BOLD:
				return STROKE_LINE_BOLD;
			case LINE_SOLID:
				return STROKE_LINE_SOLID;
			case LINE_DASHED:
				return STROKE_LINE_DASH;
			case LINE_DASHED2:
				return STROKE_LINE_DASH2;
		}
		return STROKE_LINE_SOLID;
	}

	private static java.awt.Shape getLineMarkerShape(int subtype)
	{
		switch (subtype)
		{
			case MARKER_CIRCLE:
				return new java.awt.geom.Ellipse2D.Double(-2,-2,4,4);
			case MARKER_SQUARE:
				return new java.awt.geom.Rectangle2D.Double(-2,-2,4,4);
			case MARKER_DIAMOND:
				int x1[] = {0,-2,0,2,0};
				int y1[] = {2,0,-2,0,2};
				return new java.awt.Polygon(x1,y1, x1.length);
			case MARKER_PLUS:
				int x2[] = {0,0,-2,0,0,0,2,0};
				int y2[] = {2,0,0,0,-2,0,0,0};
				return new java.awt.Polygon(x2,y2, x2.length);
			case MARKER_X:
				int x3[] = {-2,2,0,2,0,-2,0,-2,2};
				int y3[] = {-2,2,0,-2,0,2,0,-2,2};
				return new java.awt.Polygon(x3,y3, x3.length);
		}
		return new java.awt.geom.Ellipse2D.Double(-2,-2,4,4);
	}

	private static int getChartAxisType(String subtype)
	{
		if (subtype != null)
		{
			if (subtype.equals("Y1"))
				return AXIS_Y1;
			else if (subtype.equals("Y2"))
				return AXIS_Y2;
		}
		return AXIS_Y1;
	}
	
	private static int[] getPropertyArray(int ycols, int level, String curval[])
	{
		int p[] = new int [ycols];
                for(int i=0;i<ycols;i++)
		{
			if (level == 1) p[i] = LINE;
			if (level == 2) p[i] = MARKER_NONE;
			if (level == 3) p[i] = MARKER_NONE;
		}
                for(int i=0; i< (ycols < curval.length ? ycols:curval.length); i++)
		{
			if (level == 1) p[i] = getChartType(curval[i]);
			if (level == 2) p[i] = getChartSubType(LINE, curval[i]);
			if (level == 3) p[i] = getChartSubType2(curval[i]);
		}
		return p;
	}

	private static int[] getAxisTypeArray(int ycols, String curval[])
	{
		int p[] = new int [ycols];
                for(int i=0;i<ycols;i++)
		{
			p[i] = AXIS_Y1;
		}
                for(int i=0; i< (ycols < curval.length ? ycols:curval.length); i++)
		{
			p[i] = getChartAxisType(curval[i]);
		}
		return p;
	}

	private static RectangleEdge getLegendPosition(String position)
	{
		if (position == null) return null;
		if (position.equalsIgnoreCase("NORTH"))
			return RectangleEdge.TOP;
		else if (position.equalsIgnoreCase("NORTHWEST"))
			return RectangleEdge.TOP;
		else if (position.equalsIgnoreCase("NORTHEAST"))
			return RectangleEdge.TOP;
		else if (position.equalsIgnoreCase("SOUTH"))
			return RectangleEdge.BOTTOM;
		else if (position.equalsIgnoreCase("SOUTHWEST"))
			return RectangleEdge.BOTTOM;
		else if (position.equalsIgnoreCase("SOUTHEAST"))
			return RectangleEdge.BOTTOM;
		else if (position.equalsIgnoreCase("WEST"))
			return RectangleEdge.LEFT;
		else if (position.equalsIgnoreCase("EAST"))
			return RectangleEdge.RIGHT;
		else
			return RectangleEdge.BOTTOM;
	}

	private static HorizontalAlignment getLegendHAlign(String position)
	{
		if (position == null) return null;
		if (position.equalsIgnoreCase("NORTH"))
			return HorizontalAlignment.CENTER;
		else if (position.equalsIgnoreCase("NORTHWEST"))
			return HorizontalAlignment.LEFT;
		else if (position.equalsIgnoreCase("NORTHEAST"))
			return HorizontalAlignment.RIGHT;
		else if (position.equalsIgnoreCase("SOUTH"))
			return HorizontalAlignment.CENTER;
		else if (position.equalsIgnoreCase("SOUTHWEST"))
			return HorizontalAlignment.LEFT;
		else if (position.equalsIgnoreCase("SOUTHEAST"))
			return HorizontalAlignment.RIGHT;
		else if (position.equalsIgnoreCase("WEST"))
			return HorizontalAlignment.CENTER;
		else if (position.equalsIgnoreCase("EAST"))
			return HorizontalAlignment.CENTER;
		else
			return HorizontalAlignment.CENTER;
	}
				
	private static java.awt.Color getColor(String col)
	{
		if (col != null)
		{
			if (col.equalsIgnoreCase("WHITE"))
				return java.awt.Color.white;
			else if (col.equalsIgnoreCase("LIGHTGRAY"))
				return java.awt.Color.lightGray;
			else if (col.equalsIgnoreCase("GRAY"))
				return java.awt.Color.gray;
			else if (col.equalsIgnoreCase("DARKGRAY"))
				return java.awt.Color.darkGray;
			else if (col.equalsIgnoreCase("BLACK"))
				return java.awt.Color.black;
			else if (col.equalsIgnoreCase("RED"))
				return java.awt.Color.red;
			else if (col.equalsIgnoreCase("PINK"))
				return java.awt.Color.pink;
			else if (col.equalsIgnoreCase("ORANGE"))
				return java.awt.Color.orange;
			else if (col.equalsIgnoreCase("YELLOW"))
				return java.awt.Color.yellow;
			else if (col.equalsIgnoreCase("MAGENTA"))
				return java.awt.Color.magenta;
			else if (col.equalsIgnoreCase("CYAN"))
				return java.awt.Color.cyan;
			else if (col.equalsIgnoreCase("BLUE"))
				return java.awt.Color.blue;
			else
			{
				try {
					java.util.Vector<String> words = TextUtils.getWords(col,",");
					if (words.size()>3)
					{
						return new java.awt.Color(
							Integer.valueOf(words.elementAt(0)).intValue(),
							Integer.valueOf(words.elementAt(1)).intValue(),
							Integer.valueOf(words.elementAt(2)).intValue(),
							Integer.valueOf(words.elementAt(3)).intValue());
					}
					else if (words.size()>2)
					{
						return new java.awt.Color(
							Integer.valueOf(words.elementAt(0)).intValue(),
							Integer.valueOf(words.elementAt(1)).intValue(),
							Integer.valueOf(words.elementAt(2)).intValue());
					}
				} catch (java.lang.NumberFormatException nfe) {}
			}
		}
		return null;
	}
	private static java.awt.Color[] getColorList(String colorlist)
	{
		java.util.Vector<java.awt.Color> colarr = new java.util.Vector<java.awt.Color>();
		String scolarr[] = TextUtils.toStringArray(TextUtils.getWords(colorlist,"|"));
		for(int i=0; i < scolarr.length; i++)
		{
			java.awt.Color tmpc = getColor(scolarr[i]);
			if (tmpc != null) colarr.addElement(tmpc);
		}
		java.awt.Color retcols[] = {};
		if (colarr.size() > 0)
		{
			retcols = new java.awt.Color[colarr.size()];
			for(int i=0;i<colarr.size();i++)
			{
				retcols[i] = (java.awt.Color)colarr.elementAt(i);
			}
		}
		return retcols;
	}
	private static java.awt.Color getChartSeriesColor(int ind, java.awt.Color colorlist[])
	{
		if (ind < colorlist.length)
		{	
		    return colorlist[ind];
		}
		else if (ind < COLORLIST.length)
		{
		    return COLORLIST[ind];
		}
	        return new java.awt.Color(
			(int)(255 * Math.random()),(int)(255 * Math.random()),(int)(255 * Math.random()));
	}
	private static java.awt.Font getFont(String sfont)
	{
		int font_style;
		String temp;
		java.util.Vector<String> words = TextUtils.getWords(sfont,",");
		if (words.size()>2)
		{
			temp = words.elementAt(1).toUpperCase();
			if (temp.equals("PLAIN"))
				font_style = java.awt.Font.PLAIN;
			else if (temp.equals("BOLD"))
				font_style = java.awt.Font.BOLD;
			else if (temp.equals("ITALIC"))
				font_style = java.awt.Font.ITALIC;
			else if (temp.equals("BOLDITALIC"))
				font_style = java.awt.Font.ITALIC+java.awt.Font.BOLD;
			else
				return null;
			try {
				return new java.awt.Font(
					words.elementAt(0),
					font_style,
					Integer.valueOf(words.elementAt(2)).intValue());
			} catch (java.lang.NumberFormatException nfe) {}
		}
		return null;
	}
					
	public static final Double toDouble(Object objval)
	{
		if (objval == null) return null;
		try {
			return Double.valueOf(objval.toString());
		} catch (NumberFormatException nfe) {};
		return null;
	}
	
	public static final boolean isTimeType(int dtype)
	{
		switch(dtype)
		{
			case java.sql.Types.DATE:
			case java.sql.Types.TIME:
			case java.sql.Types.TIMESTAMP:
			     return true;
		}
		return false;	
	}

	public static final boolean isNumberType(int dtype)
	{
		switch(dtype)
		{
			case java.sql.Types.TINYINT:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.BIGINT:
			case java.sql.Types.FLOAT:
			case java.sql.Types.REAL:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.NUMERIC:			
			case java.sql.Types.DECIMAL:			
			     return true;
		}
		return false;	
	}
		
	// For PIE chart
	public static final DefaultPieDataset getPieDataset(DBRowCache data, String xcol, String ycol)
	{
		int colxid=0, colyid=0;
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,","));
		DefaultPieDataset dataset = new DefaultPieDataset();
		if ((colxid = data.findColumn(xcol)) <= 0) return dataset; 
		
		for (int i=0;i<ycolumns.length; i++)
		{
			if ((colyid = data.findColumn(ycolumns[i])) > 0)
			{
				if (isNumberType(data.getColumnType(colyid)))
				{
					for(int row=1; row <= data.getRowCount(); row ++)
					{
						dataset.setValue((java.lang.Comparable)(data.getItem(row, colxid)),
								 (java.lang.Number)(data.getItem(row, colyid)));
					}
				}
			}
		}	
		return dataset;
	}

	// For categoryed non number and date X-AXIS Chart
	public static final DefaultCategoryDataset getCategoryDataset(DBRowCache data, String xcol, String ycol)
	{
		int colxid=0, colyid=0;
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,","));
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if ((colxid = data.findColumn(xcol)) <= 0) return dataset; 
		for (int i=0;i<ycolumns.length; i++)
		{
			if ((colyid = data.findColumn(ycolumns[i])) > 0)
			{
				if (isNumberType(data.getColumnType(colyid)))
				{			
					for(int row=1; row <= data.getRowCount(); row ++)
					{
						dataset.setValue((java.lang.Number)(data.getItem(row, colyid)),
								 data.getColumnLabel(colyid),
								 (java.lang.Comparable)(data.getItem(row, colxid)));
					}
				}
			}
		}	
		return dataset;
	}

	// get number range
	public static final void  getNumberPeriod (XYIntervalSeries series,DBRowCache data, int row, int xcol, int ycol)
	{
		if (isNumberType(data.getColumnType(xcol)) && isNumberType(data.getColumnType(ycol)))
		{
			java.lang.Number preval = (java.lang.Number)(data.getItem(row - 1, xcol));
			java.lang.Number curval = (java.lang.Number)(data.getItem(row, xcol));
			java.lang.Number nxtval = (java.lang.Number)(data.getItem(row + 1, xcol));
			java.lang.Number curyval = (java.lang.Number)(data.getItem(row, ycol));
			
			double predelta = 0.0;
			if (preval != null && curval != null) predelta = curval.doubleValue() - preval.doubleValue();
			double nxtdelta = 0.0;
			if (nxtval != null && curval != null) nxtdelta = nxtval.doubleValue() - curval.doubleValue();		
			
			if (curval != null && curyval != null)
			{
				series.add(curval.doubleValue(),
					   curval.doubleValue() - (preval == null ? nxtdelta/2.0 : predelta/2.0),
					   curval.doubleValue() + (nxtval == null ? predelta/2.0 : nxtdelta/2.0),
					   curyval.doubleValue(), curyval.doubleValue(), curyval.doubleValue());	
			}
		}
	}
	
	// For categoryed Number X-AXIS Chart
	public static final XYIntervalSeriesCollection getXYIntervalSeriesDataset(DBRowCache data, String xcol, String ycol)
	{
		int colxid=0, colyid=0;
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,","));
		XYIntervalSeriesCollection  dataset = new XYIntervalSeriesCollection();
		if ((colxid = data.findColumn(xcol)) <= 0) return dataset; 
		if (!isNumberType(data.getColumnType(colxid))) return dataset;

		for (int i=0;i<ycolumns.length; i++)
		{
			if ((colyid = data.findColumn(ycolumns[i])) > 0)
			{
				XYIntervalSeries tempseries = new XYIntervalSeries(data.getColumnLabel(colyid));
				if (isNumberType(data.getColumnType(colyid)))
				{	
					for(int row=1; row <= data.getRowCount(); row ++)
					{
						getNumberPeriod(tempseries,data,row,colxid, colyid);
					}
				}
				dataset.addSeries(tempseries);
			}
		}	
		return dataset;
	}

	// For categoryed Number X-AXIS Chart
	public static final DefaultTableXYDataset getXYSeriesDataset(DBRowCache data, String xcol, String ycol)
	{
		int colxid=0, colyid=0;
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,","));
		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		if ((colxid = data.findColumn(xcol)) <= 0) return dataset; 
		if (!isNumberType(data.getColumnType(colxid))) return dataset;

		for (int i=0;i<ycolumns.length; i++)
		{
			if ((colyid = data.findColumn(ycolumns[i])) > 0)
			{
				XYSeries tempseries = new XYSeries(data.getColumnLabel(colyid),false, false);
				if (isNumberType(data.getColumnType(colyid)))
				{	
					for(int row=1; row <= data.getRowCount(); row ++)
					{
						tempseries.add((Number)(data.getItem(row,colxid)),(Number)(data.getItem(row,colyid)));
					}
				}
				dataset.addSeries(tempseries);
			}
		}	
		return dataset;
	}

	// get date range
	public static final TimePeriod  getTimePeriod (DBRowCache data, int row, int xcol)
	{
		if (isTimeType(data.getColumnType(xcol)))
		{
			java.util.Date preval = (java.util.Date)(data.getItem(row - 1, xcol));
			java.util.Date curval = (java.util.Date)(data.getItem(row, xcol));
			java.util.Date nxtval = (java.util.Date)(data.getItem(row + 1, xcol));
			
			long predelta = 0;
			if (preval != null && curval != null) predelta = curval.getTime() - preval.getTime();
			long nxtdelta = 0;
			if (nxtval != null && curval != null) nxtdelta = nxtval.getTime() - curval.getTime();		
			
			if (curval != null)
			{
				return new SimpleTimePeriod(curval.getTime() - (preval == null ? nxtdelta/2 : predelta/2),
							    curval.getTime() + (nxtval == null ? predelta/2 : nxtdelta/2));	
			}
		}
		return null;
	}
			
	// For categoryed Time X-AXIS Chart
	public static final TimePeriodValuesCollection getTimePeriodDataset(DBRowCache data, String xcol, String ycol)
	{
		int colxid=0, colyid=0;
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,","));
		TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
		if ((colxid = data.findColumn(xcol)) <= 0) return dataset; 
		int coltype = data.getColumnType(colxid);
		if (!isTimeType(coltype)) return dataset;
		for (int i=0;i<ycolumns.length; i++)
		{		
			if ((colyid = data.findColumn(ycolumns[i])) > 0)
			{
				TimePeriodValues tempseries = new TimePeriodValues(data.getColumnLabel(colyid));
				if (isNumberType(data.getColumnType(colyid)))
				{	
					for(int row=1; row <= data.getRowCount(); row ++)
					{
						tempseries.add(getTimePeriod(data,row,colxid),
							    (java.lang.Number)(data.getItem(row, colyid)));
					}
				}
				dataset.addSeries(tempseries);
			}
		}	
		return dataset;
	}

	// For categoryed Time X-AXIS Chart
	public static final TimeTableXYDataset getTimeSeriesDataset(DBRowCache data, String xcol, String ycol)
	{
		int colxid=0, colyid=0;
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,","));
		TimeTableXYDataset dataset = new TimeTableXYDataset ();
		if ((colxid = data.findColumn(xcol)) <= 0) return dataset; 
		int coltype = data.getColumnType(colxid);
		if (!isTimeType(coltype)) return dataset;
		for (int i=0;i<ycolumns.length; i++)
		{		
			if ((colyid = data.findColumn(ycolumns[i])) > 0)
			{
				if (isNumberType(data.getColumnType(colyid)))
				{	
					for(int row=1; row <= data.getRowCount(); row ++)
					{
						dataset.add(getTimePeriod(data,row,colxid),
							    (java.lang.Number)(data.getItem(row, colyid)),
							    data.getColumnLabel(colyid), true);
					}
				}
			}
		}	
		return dataset;
	}

	// For stock chart.
	public static final DefaultOHLCDataset getOHLCDataset(DBRowCache data, String xcol, String ycol)
	{
		int colxid=0, y1id=0, y2id=0, y3id=0, y4id=0, y5id=0;
		Number vhigh, vlow, vopen, vclose, vvolume;
		java.util.Date vdate;
		OHLCDataItem ohlc_rows[] = {};
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,","));
		DefaultOHLCDataset dataset = new DefaultOHLCDataset(ycol, ohlc_rows);
		if (data.getRowCount() == 0) return dataset;
		if ((colxid = data.findColumn(xcol)) <= 0) return dataset; 
		int coltype = data.getColumnType(colxid);
		if (!isTimeType(coltype)) return dataset;
		for (int i=0;i<ycolumns.length;i++)
		{
			if (!isNumberType(data.getColumnType(ycolumns[i])))
				return dataset;
		}
		if (ycolumns.length > 0) y1id = data.findColumn(ycolumns[0]);
		if (ycolumns.length > 1) y2id = data.findColumn(ycolumns[1]);
		if (ycolumns.length > 2) y3id = data.findColumn(ycolumns[2]);
		if (ycolumns.length > 3) y4id = data.findColumn(ycolumns[3]);
		if (ycolumns.length > 4) y5id = data.findColumn(ycolumns[4]);

		ohlc_rows = new OHLCDataItem[data.getRowCount()];
		for (int row=1;row<=data.getRowCount(); row++)
		{		
			vdate   = (java.util.Date) (data.getItem(row, colxid));
			vhigh   = (java.lang.Number)(data.getItem(row, y1id));
			vlow    = (java.lang.Number)(data.getItem(row, y2id));
			vopen   = (java.lang.Number)(data.getItem(row, y3id));
			vclose  = (java.lang.Number)(data.getItem(row, y4id));
			vvolume = (java.lang.Number)(data.getItem(row, y5id));
			ohlc_rows [row - 1] = new OHLCDataItem(vdate, 
						(vhigh != null ? vhigh.doubleValue() : 0),
						(vlow  != null ? vlow.doubleValue() : 0),
						(vopen != null ? vopen.doubleValue() : 0),
						(vclose != null ? vclose.doubleValue() : 0),
						(vvolume != null ? vvolume.doubleValue() : 0));
		}	
		return new DefaultOHLCDataset(ycol, ohlc_rows);
	}

	// For High Low chart.
	public static final DefaultHighLowDataset getHighLowDataset(DBRowCache data, String xcol, String ycol)
	{
		int colxid=0, y1id=0, y2id=0, y3id=0, y4id=0, y5id=0;
		Number vhigh, vlow, vopen, vclose, vvolume;
		java.util.Date vdate;
		java.util.Date hl_date[] = {};
		double hl_high[] = {}, hl_low[] = {}, hl_open[] = {}, hl_close[] = {}, hl_volume[] = {};
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,","));
		DefaultHighLowDataset dataset = new DefaultHighLowDataset(ycol, hl_date, hl_open, hl_low, hl_open, hl_close, hl_volume);
		if (data.getRowCount() == 0) return dataset;
		if ((colxid = data.findColumn(xcol)) <= 0) return dataset; 
		int coltype = data.getColumnType(colxid);
		if (!isTimeType(coltype)) return dataset;
		for (int i=0;i<ycolumns.length;i++)
		{
			if (!isNumberType(data.getColumnType(ycolumns[i])))
				return dataset;
		}
		if (ycolumns.length > 0) y1id = data.findColumn(ycolumns[0]);
		if (ycolumns.length > 1) y2id = data.findColumn(ycolumns[1]);
		if (ycolumns.length > 2) y3id = data.findColumn(ycolumns[2]);
		if (ycolumns.length > 3) y4id = data.findColumn(ycolumns[3]);
		if (ycolumns.length > 4) y5id = data.findColumn(ycolumns[4]);

		hl_date = new java.util.Date[data.getRowCount()];
		hl_high = new double[data.getRowCount()];
		hl_low = new double[data.getRowCount()];
		hl_open = new double[data.getRowCount()];
		hl_close = new double[data.getRowCount()];
		hl_volume = new double[data.getRowCount()];

		for (int row=1;row<=data.getRowCount(); row++)
		{		
			vdate   = (java.util.Date) (data.getItem(row, colxid));
			vhigh   = (java.lang.Number)(data.getItem(row, y1id));
			vlow    = (java.lang.Number)(data.getItem(row, y2id));
			vopen   = (java.lang.Number)(data.getItem(row, y3id));
			vclose  = (java.lang.Number)(data.getItem(row, y4id));
			vvolume = (java.lang.Number)(data.getItem(row, y5id));
			hl_date [row - 1] = vdate;
			hl_high [row - 1] = (vhigh != null ? vhigh.doubleValue() : 0);
			hl_low [row - 1] = (vlow != null ? vlow.doubleValue() : 0);
			hl_open [row - 1] = (vopen != null ? vopen.doubleValue() : 0);
			hl_close [row - 1] = (vclose != null ? vclose.doubleValue() : 0);
			hl_volume [row - 1] = (vvolume != null ? vvolume.doubleValue() : 0);
		}
		return new DefaultHighLowDataset(ycol, hl_date, hl_high, hl_low, hl_open, hl_close, hl_volume);
	}

	// For Gantt chart, four columns (task id, start time, end time, percent completed)
	public static final IntervalCategoryDataset getGanttDataset(DBRowCache data, String xcol, String ycol)
	{
                TaskSeries s1 = new TaskSeries("Plan");
                TaskSeries s2 = new TaskSeries("Real");		
		int col1id=0, col2id=0, col3id=0, col4id=0, col5id=0;
		String tasklabel = null;
		java.util.Date taskstartd = null, taskendd=null;
		java.util.Date taskstartd2 = null, taskendd2=null;
                java.lang.Number taskpct = null;
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,",")); 

		col1id = data.findColumn(xcol);
		if (ycolumns.length > 0) col2id = data.findColumn(ycolumns[0]);
		if (ycolumns.length > 1) col3id = data.findColumn(ycolumns[1]);
		if (ycolumns.length > 2) col4id = data.findColumn(ycolumns[2]);
		if (ycolumns.length > 3) col5id = data.findColumn(ycolumns[3]);

		if (ycolumns.length == 3)
		{
 			if (data.getRowCount() > 0 && col1id > 0 && col2id > 0 && col3id>0 && col4id>0 && 
        	            isTimeType(data.getColumnType(col2id)) &&  isTimeType(data.getColumnType(col3id)) &&
	                    isNumberType(data.getColumnType(col4id)))
			{
				for (int row=1;row<=data.getRowCount(); row++)
				{
					tasklabel   = String.valueOf(data.getItem(row, col1id));
					taskstartd  = (java.util.Date)(data.getItem(row, col2id));
					taskendd    = (java.util.Date)(data.getItem(row, col3id));
					taskpct     = (java.lang.Number)(data.getItem(row, col4id));
					if (tasklabel != null && taskstartd != null && taskendd != null && taskpct != null)
					{
						Task t1 = new Task(tasklabel, new SimpleTimePeriod(taskstartd, taskendd));
						t1.setPercentComplete(taskpct.doubleValue());
						s1.add(t1);
					}
				}
			}
		}
		else if (ycolumns.length == 4)
		{
 			if (data.getRowCount() > 0 && col1id > 0 && col2id > 0 && col3id>0 && col4id>0 && col5id>0 &&
        	            isTimeType(data.getColumnType(col2id)) &&  isTimeType(data.getColumnType(col3id)) &&
	                    isTimeType(data.getColumnType(col4id)) &&  isTimeType(data.getColumnType(col5id)))
			{
				for (int row=1;row<=data.getRowCount(); row++)
				{
					tasklabel   = String.valueOf(data.getItem(row, col1id));
					taskstartd  = (java.util.Date)(data.getItem(row, col2id));
					taskendd    = (java.util.Date)(data.getItem(row, col3id));
					taskstartd2  = (java.util.Date)(data.getItem(row, col4id));
					taskendd2    = (java.util.Date)(data.getItem(row, col5id));
					if (tasklabel != null )
					{
						if (taskstartd != null && taskendd != null)
						{
							Task t1 = new Task(tasklabel, new SimpleTimePeriod(taskstartd, taskendd));
							s1.add(t1);
						}
						if (taskstartd2 != null && taskendd2 != null)
						{
							Task t1 = new Task(tasklabel, new SimpleTimePeriod(taskstartd2, taskendd2));
							s2.add(t1);
						}
					}
				}
			}
		}

		TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);
		if (ycolumns.length == 4) collection.add(s2);
		return collection;
	}


	// For Bubble chart, return XYZDataset
	public static final XYZDataset getBubbleDataset(DBRowCache data, String xcol, String ycol)
	{
		DefaultXYZDataset bubbledataset = new DefaultXYZDataset();

		int col1id=0, col2id=0, col3id=0;
		java.lang.String serialname = null;
                java.lang.Number vx = null, vy = null, vz = null;
		String ycolumns[] = TextUtils.toStringArray(TextUtils.getWords(ycol,",")); 


		col1id = data.findColumn(xcol);
		if (ycolumns.length > 0) col2id = data.findColumn(ycolumns[0]);
		if (ycolumns.length > 1) col3id = data.findColumn(ycolumns[1]);

		if (ycolumns.length == 1)
		{
 			if (data.getRowCount() > 0 && col1id > 0 && col2id > 0 && 
        	            isNumberType(data.getColumnType(col1id)) &&
                            isNumberType(data.getColumnType(col2id)))
			{
	                        double arrx[] = new double[data.getRowCount()];
	                        double arry[] = new double[data.getRowCount()];
	                        double arrz[] = new double[data.getRowCount()];
				double[][] seriesdata = new double[][] { arrx, arry, arrz };   

				serialname = data.getColumnLabel(col1id);
				for (int row=1;row<=data.getRowCount(); row++)
				{
					vx   = (java.lang.Number)(data.getItem(row, col1id));
					vy   = (java.lang.Number)(data.getItem(row, col2id));
					if (serialname != null && vx != null & vy != null)
					{
						arrx[row - 1] = vx.doubleValue();
						arry[row - 1] = vy.doubleValue();
						arrz[row - 1] = 3.0f;
					}
					else
					{
						arrx[row - 1] = 0.0f;
						arry[row - 1] = 0.0f;
						arrz[row - 1] = 3.0f;						
					}
				}
				bubbledataset.addSeries(serialname, seriesdata);
			}
		}
		else if (ycolumns.length == 2)
		{
 			if (data.getRowCount() > 0 && col1id > 0 && col2id > 0 && col3id > 0 &&
        	            isNumberType(data.getColumnType(col1id)) &&
                            isNumberType(data.getColumnType(col2id)) &&
                            isNumberType(data.getColumnType(col3id)))
			{
	                        double arrx[] = new double[data.getRowCount()];
	                        double arry[] = new double[data.getRowCount()];
	                        double arrz[] = new double[data.getRowCount()];
				double[][] seriesdata = new double[][] { arrx, arry, arrz };   

				serialname = data.getColumnLabel(col1id);
				for (int row=1;row<=data.getRowCount(); row++)
				{
					vx   = (java.lang.Number)(data.getItem(row, col1id));
					vy   = (java.lang.Number)(data.getItem(row, col2id));
					vz   = (java.lang.Number)(data.getItem(row, col3id));
					if (serialname != null && vx != null & vy != null && vz != null)
					{
						arrx[row - 1] = vx.doubleValue();
						arry[row - 1] = vy.doubleValue();
						arrz[row - 1] = vz.doubleValue();
					}
					else
					{
						arrx[row - 1] = 0.0f;
						arry[row - 1] = 0.0f;
						arrz[row - 1] = 3.0f;						
					}
				}
				bubbledataset.addSeries(serialname, seriesdata);
			}
		}

		return bubbledataset;
	}

	
	// Get chart type renderer for nun number and non date X axis
	public static final CategoryItemRenderer getCategoryItemRenderer(int type, int subtype, int subtype2)
	{
		CategoryItemRenderer catrend;
		switch(type)
		{
			case DOT:
				catrend = new LineAndShapeRenderer();
				((LineAndShapeRenderer)catrend).setLinesVisible(false);
				catrend.setShape(getLineMarkerShape(subtype2));
				break;
			case STEP:
			case LINE:
			case LINE2:
				catrend = new LineAndShapeRenderer();
				if (subtype == LINE_NONE)
				{
					((LineAndShapeRenderer)catrend).setLinesVisible(false);
				}
				else
				{
					((LineAndShapeRenderer)catrend).setLinesVisible(true);
					((LineAndShapeRenderer)catrend).setStroke(getLineStyleStroke(subtype));
				}
				if (subtype2 == MARKER_NONE)
				{
					((LineAndShapeRenderer)catrend).setShapesVisible(false);
				}
				else
				{
					catrend.setShape(getLineMarkerShape(subtype2));	
				}
				break;	
			case AREA:
				catrend = new AreaRenderer();
				break;
			case STACKAREA:
				catrend = new StackedAreaRenderer();
				break;
			case BAR:
				if (subtype == BAR_3D)
					catrend = new BarRenderer3D();
				else
					catrend = new BarRenderer();
				((BarRenderer)catrend).setItemMargin(0.0f);
				((BarRenderer)catrend).setDrawBarOutline(true);
				((BarRenderer)catrend).setBarPainter(new GradientBarPainter(0.0f,0.0f,0.0f));
				if (subtype == BAR_SHADOW)
					((BarRenderer)catrend).setShadowVisible(true);
				else
					((BarRenderer)catrend).setShadowVisible(false);		
				break;
			case WATER:
				catrend = new WaterfallBarRenderer();
				((BarRenderer)catrend).setItemMargin(0.0f);
				((BarRenderer)catrend).setDrawBarOutline(true);
				((BarRenderer)catrend).setBarPainter(new GradientBarPainter(0.0f,0.0f,0.0f));
				if (subtype == BAR_SHADOW)
					((BarRenderer)catrend).setShadowVisible(true);
				else
					((BarRenderer)catrend).setShadowVisible(false);		
				break;
			case LAYERBAR:
				catrend = new LayeredBarRenderer();
				((BarRenderer)catrend).setItemMargin(0.1f);
				/*
				((BarRenderer)catrend).setDrawBarOutline(true);
				((BarRenderer)catrend).setBarPainter(new GradientBarPainter(0.0f,0.0f,0.0f));
				if (subtype == BAR_SHADOW)
					((BarRenderer)catrend).setShadowVisible(true);
				else
					((BarRenderer)catrend).setShadowVisible(false);		
				*/
				break;
			case LEVEL:
				catrend = new LevelRenderer();
				catrend.setStroke(STROKE_LINE_BOLD);
				break;
			case STACKBAR:
				catrend = new StackedBarRenderer();
				((BarRenderer)catrend).setItemMargin(0.1f);
				((BarRenderer)catrend).setDrawBarOutline(true);
				((BarRenderer)catrend).setBarPainter(new GradientBarPainter(0.0f,0.0f,0.0f));
				if (subtype == BAR_SHADOW)
					((BarRenderer)catrend).setShadowVisible(true);
				else
					((BarRenderer)catrend).setShadowVisible(false);		
				break;
			case GANTT:
				catrend = new GanttRenderer();
				((BarRenderer)catrend).setItemMargin(0.0f);
				((BarRenderer)catrend).setDrawBarOutline(true);
				((BarRenderer)catrend).setBarPainter(new GradientBarPainter(0.0f,0.0f,0.0f));
				((BarRenderer)catrend).setShadowVisible(false);		
				break;
			default:
				catrend = new LineAndShapeRenderer();
				if (subtype == LINE_NONE)
				{
					((LineAndShapeRenderer)catrend).setLinesVisible(false);
				}
				else
				{
					((LineAndShapeRenderer)catrend).setLinesVisible(true);
				        ((LineAndShapeRenderer)catrend).setStroke(getLineStyleStroke(subtype));
				}

				if (subtype2 == MARKER_NONE)
				{
					((LineAndShapeRenderer)catrend).setShapesVisible(false);
				}
				else
				{
					catrend.setShape(getLineMarkerShape(subtype2));	
				}
				break;						
		}
		return catrend;
	}

	// Get chart type renderer for number and date x axis
	public static final XYItemRenderer getXYItemRenderer(int type, int subtype, int subtype2)
	{
		XYItemRenderer xyrend;
		switch(type)
		{
			case DOT:
				xyrend = new XYDotRenderer();
				((XYDotRenderer)xyrend).setDotWidth(3);
				((XYDotRenderer)xyrend).setDotHeight(3);
				break;
			case DIFF:
				xyrend = new XYDifferenceRenderer();
				break;
			case STOCK_HLC:
				xyrend = new HighLowRenderer();
				((HighLowRenderer)xyrend).setDrawOpenTicks(false);
				break;
			case STOCK_OHLC:
				xyrend = new HighLowRenderer();
				break;
			case STOCK:
				xyrend = new CandlestickRenderer();
				((CandlestickRenderer)xyrend).setCandleWidth(6d);
				/*
				((CandlestickRenderer)xyrend).setAutoWidthFactor(0.9);
				((CandlestickRenderer)xyrend).setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_AVERAGE);
				*/
				((CandlestickRenderer)xyrend).setDrawVolume(false);
				break;
			case STEP:
				xyrend = new XYStepRenderer();
				if (subtype == LINE_NONE)
				{
					((XYLineAndShapeRenderer)xyrend).setLinesVisible(false);
				}
				else
				{
					((XYLineAndShapeRenderer)xyrend).setLinesVisible(true);
					((XYLineAndShapeRenderer)xyrend).setStroke(getLineStyleStroke(subtype));
				}
				if (subtype2 == MARKER_NONE)
				{
					((XYLineAndShapeRenderer)xyrend).setShapesVisible(false);
				}
				else
				{
					xyrend.setShape(getLineMarkerShape(subtype2));
				}
				break;
			case LINE:
				xyrend = new XYLineAndShapeRenderer();
				if (subtype == LINE_NONE)
				{
					((XYLineAndShapeRenderer)xyrend).setLinesVisible(false);
				}
				else
				{
					((XYLineAndShapeRenderer)xyrend).setLinesVisible(true);
					((XYLineAndShapeRenderer)xyrend).setStroke(getLineStyleStroke(subtype));
				}
				if (subtype2 == MARKER_NONE)
				{
					((XYLineAndShapeRenderer)xyrend).setShapesVisible(false);
				}
				else
				{
					xyrend.setShape(getLineMarkerShape(subtype2));
				}
				break;
			case LINE2:
				xyrend = new SamplingXYLineRenderer();
                                /*
				if (subtype == LINE_NONE)
				{
					((SamplingXYLineRenderer)xyrend).setLinesVisible(false);
				}
				else
				{
					((SamplingXYLineRenderer)xyrend).setLinesVisible(true);
					((SamplingXYLineRenderer)xyrend).setStroke(getLineStyleStroke(subtype));
				}
				if (subtype2 == MARKER_NONE)
				{
					((SamplingXYLineRenderer)xyrend).setShapesVisible(false);
				}
				else
				{
					xyrend.setShape(getLineMarkerShape(subtype2));
				}
                                */
				break;
			case AREA:
				xyrend = new XYAreaRenderer2();
				break;
			case STACKAREA:
				xyrend = new StackedXYAreaRenderer2();
				break;
			case BAR:
				xyrend = new ClusteredXYBarRenderer();
				if (subtype == BAR_SHADOW)
					((XYBarRenderer)xyrend).setShadowVisible(true);
				else
					((XYBarRenderer)xyrend).setShadowVisible(false);
				((XYBarRenderer)xyrend).setDrawBarOutline(false);
				((XYBarRenderer)xyrend).setMargin(0.0f);
				((XYBarRenderer)xyrend).setBarPainter(new GradientXYBarPainter(0.0f,0.0f,0.0f));
				break;
			case STACKBAR:
				xyrend = new StackedXYBarRenderer();
				if (subtype == BAR_SHADOW)
					((XYBarRenderer)xyrend).setShadowVisible(true);
				else
					((XYBarRenderer)xyrend).setShadowVisible(false);
				((XYBarRenderer)xyrend).setDrawBarOutline(false);
				((XYBarRenderer)xyrend).setMargin(0.1f);
				((XYBarRenderer)xyrend).setBarPainter(new GradientXYBarPainter(0.0f,0.0f,0.0f));
				break;
			case BUBBLE:
				xyrend = new XYBubbleRenderer();
				break;
			default:
				xyrend = new XYLineAndShapeRenderer();
				if (subtype == LINE_NONE)
				{
					((XYLineAndShapeRenderer)xyrend).setLinesVisible(false);
				}
				else
				{
					((XYLineAndShapeRenderer)xyrend).setLinesVisible(true);
					((XYLineAndShapeRenderer)xyrend).setStroke(getLineStyleStroke(subtype));
				}
				if (subtype2 == MARKER_NONE)
				{
					((XYLineAndShapeRenderer)xyrend).setShapesVisible(false);
				}
				else
				{
					xyrend.setShape(getLineMarkerShape(subtype2));
				}
				break;					
		}
		return xyrend;
	}	

	public static final void setValueAxisProperty(ValueAxis axis, String ymaxval, String ylabel, String lcolor, String numfmt, String lfont)
	{
		double ymin=0, ymax=0, yinc=0;
		axis.setMinorTickCount(1);
		axis.setTickMarksVisible(false);
		if (lcolor != null)
		{
			 axis.setLabelPaint(getColor(lcolor));
			 axis.setTickLabelPaint(getColor(lcolor));
		}
               	if (ylabel != null) 
               	{
               	     if (ylabel.equalsIgnoreCase("OFF"))
               	       	 axis.setTickLabelsVisible(false);	
               	     else
               	     	 axis.setLabel(ylabel);
               	}
               	if (ymaxval != null)
               	{
               		String cols[] = TextUtils.toStringArray(TextUtils.getFields(ymaxval,"|"));
                	if (cols.length > 1)
                	{
                		try {
					if (cols[1] != null)
					{
                			   ymax = Double.valueOf(cols[1]).doubleValue();
                			   axis.setUpperBound(ymax);
					}
                		} catch (java.lang.NumberFormatException nfe) {};
                	}                		
                	if (cols.length > 0)
                	{
                		try {
					if (cols[0] != null)
					{
                			    ymin = Double.valueOf(cols[0]).doubleValue();
                			    axis.setLowerBound(ymin);
					}
                		} catch (java.lang.NumberFormatException nfe) {};
                	}               			
               	}
		if (axis instanceof NumberAxis)
		{
		    if (numfmt != null)
		    {
			java.text.DecimalFormat valueformat = new WebChartNumberFormat(numfmt);
			valueformat.setGroupingUsed(false);
			NumberAxis numaxis = (NumberAxis)(axis);
			numaxis.setNumberFormatOverride(valueformat);
		    }
		    else
		    {
			java.text.DecimalFormat valueformat = new WebChartNumberFormat();
			valueformat.setGroupingUsed(false);
			NumberAxis numaxis = (NumberAxis)(axis);
			numaxis.setNumberFormatOverride(valueformat);
		    }
		}
		if (lfont != null)
		{
		    java.awt.Font axisfont = getFont(lfont);
		    if (lfont != null)
		    {
			axis.setLabelFont(axisfont);
			axis.setTickLabelFont(axisfont);
		    }
		}
	}

	public static final void setDomainAxisProperty(Axis axis, String xmaxval, String ylabel, String lcolor, String lfont)
	{
		double ymin=0, ymax=0, yinc=0;
		axis.setTickMarksVisible(false);
		if (axis instanceof ValueAxis)
		{
			((ValueAxis)(axis)).setMinorTickCount(1);
		}
		if (lcolor != null)
		{
			 axis.setLabelPaint(getColor(lcolor));
			 axis.setTickLabelPaint(getColor(lcolor));
		}
               	if (ylabel != null) 
               	{
               	     if (ylabel.equalsIgnoreCase("OFF"))
               	       	 axis.setTickLabelsVisible(false);	
               	     else
               	     	 axis.setLabel(ylabel);
               	}
		if (axis instanceof NumberAxis)
		{
               	    if (xmaxval != null)
               	    {
               		String cols[] = TextUtils.toStringArray(TextUtils.getFields(xmaxval,"|"));
                	if (cols.length > 1)
                	{
                		try {
					if (cols[1] != null)
					{
                			   ymax = Double.valueOf(cols[1]).doubleValue();
                			   ((NumberAxis)axis).setUpperBound(ymax);
					}
                		} catch (java.lang.NumberFormatException nfe) {};
                	}                		
                	if (cols.length > 0)
                	{
                		try {
					if (cols[0] != null)
					{
                			    ymin = Double.valueOf(cols[0]).doubleValue();
                			    ((NumberAxis)axis).setLowerBound(ymin);
					}
                		} catch (java.lang.NumberFormatException nfe) {};
                	}
		    }
               	}
		if (lfont != null)
		{
		    java.awt.Font axisfont = getFont(lfont);
		    if (lfont != null)
		    {
			axis.setLabelFont(axisfont);
			axis.setTickLabelFont(axisfont);
		    }
		}
	}
		
	public static final CategoryPlot getChartCategoryPlot(DBRowCache data, String xcol, String ycols[], String y2cols[],
			int type[], int subtype[], int subtype2[], boolean withdomainaxis, VariableTable vt, java.awt.Color colorlist[],int startcolor)
	{
		final CategoryPlot plot = new CategoryPlot();
		int colorid = startcolor;

		if (withdomainaxis)
		{
			CategoryAxis domainaxis = new CategoryAxis("");
			domainaxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
			domainaxis.setTickLabelFont(SIMSUN12);
			domainaxis.setUpperMargin(0.0);
			domainaxis.setLowerMargin(0.0);
			plot.setDomainAxis(domainaxis);
		}

		NumberAxis rangeaxis = new NumberAxis("");
		rangeaxis.setAutoRangeIncludesZero(false);
		rangeaxis.setAutoRangeStickyZero(false);
		rangeaxis.setTickLabelFont(SIMSUN12);
		//rangeaxis.setUpperMargin(0.0);
		rangeaxis.setLowerMargin(0.0);
	        plot.setRangeAxis(0,rangeaxis);  
	        
        	if (y2cols.length > 0)
        	{
			NumberAxis rangeaxis2 = new NumberAxis("");
			rangeaxis2.setAutoRangeIncludesZero(false);
			rangeaxis2.setAutoRangeStickyZero(false);
			rangeaxis2.setTickLabelFont(SIMSUN12);
			//rangeaxis2.setUpperMargin(0.0);
			rangeaxis2.setLowerMargin(0.0);
        		plot.setRangeAxis(1,rangeaxis2);  
        	}

	        for(int i=0; i<ycols.length; i++)
        	{
			if (type[i] == GANTT)
			{

				DateAxis ganttaxis = new DateAxis("");
				ganttaxis.setTickLabelFont(SIMSUN12);
				//rangeaxis.setUpperMargin(0.0);
				ganttaxis.setLowerMargin(0.0);
			        plot.setRangeAxis(0,ganttaxis);  
				plot.setOrientation(PlotOrientation.HORIZONTAL);
				plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.STANDARD);

	        		IntervalCategoryDataset tempdataset =getGanttDataset(data, xcol, ycols[i]);
				CategoryItemRenderer temprender = getCategoryItemRenderer(type[i], subtype[i], subtype2[i]);

					((GanttRenderer)temprender).setBasePaint(getChartSeriesColor(0, colorlist));
					((GanttRenderer)temprender).setCompletePaint(getChartSeriesColor(1, colorlist));
					((GanttRenderer)temprender).setIncompletePaint(getChartSeriesColor(2, colorlist));

				if (vt != null && data.getRowCount() < 500)
				{				
					DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, ycols[i]);
					temprender.setBaseToolTipGenerator(url_tooltip);
					temprender.setBaseItemURLGenerator(url_tooltip);
				}
				plot.setDataset(i, tempdataset);
				plot.setRenderer(i, temprender);        	
			}
			else
			{
	        		DefaultCategoryDataset tempdataset = getCategoryDataset(data, xcol, ycols[i]);
				CategoryItemRenderer temprender = getCategoryItemRenderer(type[i], subtype[i], subtype2[i]);

				for(int j=0;j<tempdataset.getRowCount();j++)
					temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
				if (vt != null && data.getRowCount() < 500)
				{
					DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, ycols[i]);
					temprender.setBaseToolTipGenerator(url_tooltip);
					temprender.setItemURLGenerator(url_tooltip);						
				}
				plot.setDataset(i, tempdataset);
				plot.setRenderer(i, temprender); 
				if (temprender instanceof LayeredBarRenderer)
				{
				    for(int j = 0; j < tempdataset.getRowCount(); j++)
				    {
				    	    ((LayeredBarRenderer)temprender).setSeriesBarWidth(j, 1 - j * 0.2);
				    }
				}
			}
        	}
	        for(int i=0; i<y2cols.length; i++)
        	{
        		DefaultCategoryDataset tempdataset = getCategoryDataset(data, xcol, y2cols[i]);
			CategoryItemRenderer temprender = getCategoryItemRenderer(
			              type[ycols.length + i], subtype[ ycols.length + i], subtype2[ycols.length + i]);
			for(int j=0;j<tempdataset.getColumnCount();j++)
				temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
			if (vt != null && data.getRowCount() < 500)
			{			              
				DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, y2cols[i]);
				temprender.setBaseToolTipGenerator(url_tooltip);
				temprender.setItemURLGenerator(url_tooltip);									              
			}
			plot.setDataset(ycols.length + i, tempdataset);
			plot.setRenderer(ycols.length + i, temprender);        			
			plot.mapDatasetToRangeAxis(ycols.length + i,1);
        	}
        	return plot;
	}
	
	public static final XYPlot getChartXYPlot(DBRowCache data, String xcol, String ycols[], String y2cols[],
			int type[], int subtype[], int subtype2[], boolean withdomainaxis, VariableTable vt, java.awt.Color colorlist[],int startcolor)
	{
		final XYPlot plot = new XYPlot();
		int colorid = startcolor;

		if (isNumberType(data.getColumnType(xcol)))
		{
			if (withdomainaxis)
			{
				NumberAxis domainaxis = new NumberAxis("");
				domainaxis.setAutoRangeIncludesZero(false);
				domainaxis.setAutoRangeStickyZero(false);
				domainaxis.setTickLabelFont(SIMSUN12);
				domainaxis.setUpperMargin(0.0);
				domainaxis.setLowerMargin(0.0);
				plot.setDomainAxis(domainaxis);
			}
			
			NumberAxis rangeaxis = new NumberAxis("");
			rangeaxis.setAutoRangeIncludesZero(false);
			rangeaxis.setAutoRangeStickyZero(false);
			rangeaxis.setTickLabelFont(SIMSUN12);
			//rangeaxis.setUpperMargin(0.0);
			rangeaxis.setLowerMargin(0.0);
	        	plot.setRangeAxis(0,rangeaxis);        		

        		if (y2cols.length > 0)
        		{
				NumberAxis rangeaxis2 = new NumberAxis("");
				rangeaxis2.setAutoRangeIncludesZero(false);
				rangeaxis2.setAutoRangeStickyZero(false);
				rangeaxis2.setTickLabelFont(SIMSUN12);
				//rangeaxis2.setUpperMargin(0.0);
				rangeaxis2.setLowerMargin(0.0);
        			plot.setRangeAxis(1,rangeaxis2);  
        		}	
	        	        	
	        	for(int i=0; i<ycols.length; i++)
        		{
				if (type[i] == BUBBLE)
				{
		        		XYZDataset tempdataset = getBubbleDataset(data, xcol, ycols[i]);
					XYItemRenderer temprender = getXYItemRenderer(type[i], subtype[i], subtype2[i]);
					for(int j=0;j<tempdataset.getSeriesCount();j++)
						temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
					if (vt != null && data.getRowCount() < 500)
					{				
						DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, ycols[i]);
						temprender.setBaseToolTipGenerator(url_tooltip);
						temprender.setURLGenerator(url_tooltip);				
					}
					plot.setDataset(i, tempdataset);
					plot.setRenderer(i, temprender);        			
				}
				else
				{
		        		DefaultTableXYDataset tempdataset = getXYSeriesDataset(data, xcol, ycols[i]);
					XYItemRenderer temprender = getXYItemRenderer(type[i], subtype[i], subtype2[i]);
					for(int j=0;j<tempdataset.getSeriesCount();j++)
						temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
					if (vt != null && data.getRowCount() < 500)
					{				
						DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, ycols[i]);
						temprender.setBaseToolTipGenerator(url_tooltip);
						temprender.setURLGenerator(url_tooltip);				
					}
					plot.setDataset(i, tempdataset);
					plot.setRenderer(i, temprender);        			
				}
        		}
	        	for(int i=0; i<y2cols.length; i++)
        		{
        			DefaultTableXYDataset tempdataset = getXYSeriesDataset(data, xcol, y2cols[i]);
				XYItemRenderer temprender = getXYItemRenderer(
				          type[ycols.length + i], subtype[ycols.length + i], subtype2[ycols.length + i]);
				for(int j=0;j<tempdataset.getSeriesCount();j++)
					temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
				if (vt != null && data.getRowCount() < 500)
				{				          
					DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, y2cols[i]);
					temprender.setBaseToolTipGenerator(url_tooltip);
					temprender.setURLGenerator(url_tooltip);				          
				}
				plot.setDataset(ycols.length + i, tempdataset);
				plot.setRenderer(ycols.length + i, temprender);        			
				plot.mapDatasetToRangeAxis(ycols.length + i,1);				
        		}
		}
		else
		{
			if (withdomainaxis)
			{
				DateAxis domainaxis = new DateAxis("");	
				domainaxis.setTickLabelFont(SIMSUN12);
				domainaxis.setUpperMargin(0.0);
				domainaxis.setLowerMargin(0.0);
				plot.setDomainAxis(domainaxis);
			}

			NumberAxis rangeaxis = new NumberAxis("");
			rangeaxis.setAutoRangeIncludesZero(false);
			rangeaxis.setAutoRangeStickyZero(false);
			rangeaxis.setTickLabelFont(SIMSUN12);
			//rangeaxis.setUpperMargin(0.0);
			rangeaxis.setLowerMargin(0.0);
	        	plot.setRangeAxis(0,rangeaxis);        

        		if (y2cols.length > 0)
        		{
				NumberAxis rangeaxis2 = new NumberAxis("");
				rangeaxis2.setAutoRangeIncludesZero(false);
				rangeaxis2.setAutoRangeStickyZero(false);
				rangeaxis2.setTickLabelFont(SIMSUN12);
				//rangeaxis2.setUpperMargin(0.0);
				rangeaxis2.setLowerMargin(0.0);
        			plot.setRangeAxis(1,rangeaxis2);  
        		}
	        		        	
	        	for(int i=0; i<ycols.length; i++)
        		{
				if (type[i] == STOCK_HLC || type[i] == STOCK_OHLC || type[i] == STOCK)
				{
	        			DefaultHighLowDataset tempdataset =getHighLowDataset(data, xcol, ycols[i]);
					XYItemRenderer temprender = getXYItemRenderer(type[i], subtype[i], subtype2[i]);
					for(int j=0;j<tempdataset.getSeriesCount();j++)
						temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
					if (vt != null && data.getRowCount() < 500)
					{				
						DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, ycols[i]);
						temprender.setBaseToolTipGenerator(url_tooltip);
						temprender.setURLGenerator(url_tooltip);
					}
					plot.setDataset(i, tempdataset);
					plot.setRenderer(i, temprender);        			
				}
				else if (type[i] == STACKAREA || type[i] == STACKBAR)
				{
	        			TimeTableXYDataset tempdataset =getTimeSeriesDataset(data, xcol, ycols[i]);
					XYItemRenderer temprender = getXYItemRenderer(type[i], subtype[i], subtype2[i]);
					for(int j=0;j<tempdataset.getSeriesCount();j++)
						temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
					if (vt != null && data.getRowCount() < 500)
					{				
						DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, ycols[i]);
						temprender.setBaseToolTipGenerator(url_tooltip);
						temprender.setURLGenerator(url_tooltip);
					}
					plot.setDataset(i, tempdataset);
					plot.setRenderer(i, temprender);        			
				}
				else
				{
	        			TimePeriodValuesCollection tempdataset =getTimePeriodDataset(data, xcol, ycols[i]);
					XYItemRenderer temprender = getXYItemRenderer(type[i], subtype[i], subtype2[i]);
					for(int j=0;j<tempdataset.getSeriesCount();j++)
						temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
					if (vt != null && data.getRowCount() < 500)
					{				
						DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, ycols[i]);
						temprender.setBaseToolTipGenerator(url_tooltip);
						temprender.setURLGenerator(url_tooltip);
					}
					plot.setDataset(i, tempdataset);
					plot.setRenderer(i, temprender);        			
				}
        		}
	        	for(int i=0; i<y2cols.length; i++)
        		{
				if (type[ycols.length + i] == STOCK_HLC || type[ycols.length + i] == STOCK_OHLC || type[ycols.length + i] == STOCK)
				{
	        			DefaultHighLowDataset tempdataset =getHighLowDataset(data, xcol, y2cols[i]);
					XYItemRenderer temprender = getXYItemRenderer(
						type[ycols.length+i], subtype[ycols.length+i], subtype2[ycols.length+i]);
					for(int j=0;j<tempdataset.getSeriesCount();j++)
						temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
					if (vt != null && data.getRowCount() < 500)
					{				
						DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, y2cols[i]);
						temprender.setBaseToolTipGenerator(url_tooltip);
						temprender.setURLGenerator(url_tooltip);
					}
					plot.setDataset(i, tempdataset);
					plot.setRenderer(i, temprender);        			
				}
				else if (type[ycols.length + i] == STACKAREA || type[ycols.length + i] == STACKBAR)
				{
	        			TimeTableXYDataset tempdataset =getTimeSeriesDataset(data, xcol, y2cols[i]);
					XYItemRenderer temprender = getXYItemRenderer(
						type[ycols.length + i], subtype[ycols.length + i], subtype2[ycols.length + i]);
					for(int j=0;j<tempdataset.getSeriesCount();j++)
						temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
					if (vt != null && data.getRowCount() < 500)
					{					
						DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, y2cols[i]);
						temprender.setBaseToolTipGenerator(url_tooltip);
						temprender.setURLGenerator(url_tooltip);
					}
					plot.setDataset(ycols.length + i, tempdataset);
					plot.setRenderer(ycols.length + i, temprender);        								
				}
				else
				{
	        			TimePeriodValuesCollection tempdataset =getTimePeriodDataset(data, xcol, y2cols[i]);
					XYItemRenderer temprender = getXYItemRenderer(
						type[ycols.length + i], subtype[ycols.length + i], subtype2[ycols.length + i]);
					for(int j=0;j<tempdataset.getSeriesCount();j++)
						temprender.setSeriesPaint(j, getChartSeriesColor(colorid++, colorlist));
					if (vt != null && data.getRowCount() < 500)
					{					
						DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(data,vt, xcol, y2cols[i]);
						temprender.setBaseToolTipGenerator(url_tooltip);
						temprender.setURLGenerator(url_tooltip);
					}
					plot.setDataset(ycols.length + i, tempdataset);
					plot.setRenderer(ycols.length + i, temprender);        			
				}
				plot.mapDatasetToRangeAxis(ycols.length + i,1);				
        		}
		}
        	
        	return plot;
	}
	
	private static void setGridLineDesc(CategoryPlot plot, String gridline, String gridstyle, String gridcolor)
	{
		plot.setOutlineStroke(STROKE_LINE_SOLID);
		if (gridline != null)
		{
			if (gridline.equalsIgnoreCase("OFF"))
			{
				plot.setDomainGridlinesVisible(false);
				plot.setRangeGridlinesVisible(false);
			}
			else if (gridline.equalsIgnoreCase("X"))
				plot.setRangeGridlinesVisible(false);
			else if (gridline.equalsIgnoreCase("Y"))
				plot.setDomainGridlinesVisible(false);
			else if (gridline.equalsIgnoreCase("XY"))
			{
				plot.setDomainGridlinesVisible(true);
				plot.setRangeGridlinesVisible(true);
			}
		}
		if (gridcolor != null)
		{
			plot.setRangeGridlinePaint(getColor(gridcolor));
			plot.setDomainGridlinePaint(getColor(gridcolor));
		}
		if (gridstyle != null)
		{
			plot.setRangeGridlineStroke(getLineStyleStroke(getChartSubType(0, gridstyle)));
		}
	}
	private static void setGridLineDesc(XYPlot plot, String gridline, String gridstyle, String gridcolor)
	{
		plot.setOutlineStroke(STROKE_LINE_SOLID);
		if (gridline != null)
		{
			if (gridline.equalsIgnoreCase("OFF"))
			{
				plot.setDomainGridlinesVisible(false);
				plot.setRangeGridlinesVisible(false);
			}
			else if (gridline.equalsIgnoreCase("X"))
				plot.setRangeGridlinesVisible(false);
			else if (gridline.equalsIgnoreCase("Y"))
				plot.setDomainGridlinesVisible(false);
			else if (gridline.equalsIgnoreCase("XY"))
			{
				plot.setDomainGridlinesVisible(true);
				plot.setRangeGridlinesVisible(true);
			}
		}
		if (gridcolor != null)
		{
			plot.setRangeGridlinePaint(getColor(gridcolor));
			plot.setDomainGridlinePaint(getColor(gridcolor));
		}
		if (gridstyle != null)
		{
			plot.setRangeGridlineStroke(getLineStyleStroke(getChartSubType(0, gridstyle)));
			plot.setDomainGridlineStroke(getLineStyleStroke(getChartSubType(0, gridstyle)));
		}
	}

	private static void setPlotRangeAxisSpace(CategoryPlot plot, String margin)
	{
	    if (plot != null && margin != null)
	    {
		try {
			double  dmargin =  Double.parseDouble(margin);
			org.jfree.chart.axis.AxisSpace axisspace = plot.getFixedRangeAxisSpace();
			if (axisspace == null)
			{
			    axisspace = new org.jfree.chart.axis.AxisSpace();
			    plot.setFixedRangeAxisSpace(axisspace);
			} 
			axisspace.setLeft(dmargin);
		} catch (java.lang.NumberFormatException nfe) {};
	    }		
	}

	private static void setPlotRangeAxisSpace(XYPlot plot, String margin)
	{
	    if (plot != null && margin != null)
	    {
		try {
			double  dmargin =  Double.parseDouble(margin);
			org.jfree.chart.axis.AxisSpace axisspace = plot.getFixedRangeAxisSpace();
			if (axisspace == null)
			{
			    axisspace = new org.jfree.chart.axis.AxisSpace();
			    plot.setFixedRangeAxisSpace(axisspace);
			} 
			axisspace.setLeft(dmargin);
		} catch (java.lang.NumberFormatException nfe) {};
	    }		
	}

	private static void setPlotRangeAxisSpace(CombinedDomainCategoryPlot plot, String margin)
	{
	    if (plot != null && margin != null)
	    {
		try {
			double  dmargin =  Double.parseDouble(margin);
			org.jfree.chart.axis.AxisSpace axisspace = plot.getFixedRangeAxisSpace();
			if (axisspace == null)
			{
			    axisspace = new org.jfree.chart.axis.AxisSpace();
			    plot.setFixedRangeAxisSpace(axisspace);
			} 
			axisspace.setLeft(dmargin);
		} catch (java.lang.NumberFormatException nfe) {};
	    }		
	}

	private static void setPlotRangeAxisSpace(CombinedDomainXYPlot plot, String margin)
	{
	    if (plot != null && margin != null)
	    {
		try {
			double  dmargin =  Double.parseDouble(margin);
			org.jfree.chart.axis.AxisSpace axisspace = plot.getFixedRangeAxisSpace();
			if (axisspace == null)
			{
			    axisspace = new org.jfree.chart.axis.AxisSpace();
			    plot.setFixedRangeAxisSpace(axisspace);
			} 
			axisspace.setLeft(dmargin);
		} catch (java.lang.NumberFormatException nfe) {};
	    }		
	}

        private static boolean checkVariableList(String varlist, VariableTable vt)
	{
	    if (varlist == null) return true;
	    java.util.Vector<String> var_req = TextUtils.getWords(varlist,",");
	    if (var_req.size() == 0) return true;
	    for (int i=0; i< var_req.size(); i++)
	    {
		if (var_req.elementAt(i).startsWith("*"))
		{
			if (vt.getString(var_req.elementAt(i).substring(1)) == null) return false;			
		}
		else
		{
			if (vt.getString(var_req.elementAt(i)) == null) return false;
		}
	    }
            return true;
	}

	private static int getint(String val,int def)
	{
		try {
			if (val != null)
				return Integer.valueOf(val).intValue();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			return def = 2;
		}
		return def;
	}

	public static VariableTable readModuleConfig(String fname, VariableTable vt)
	{
		VariableTable newvt = new VariableTable();
		String varnames[] = vt.getNames();

		newvt.loadContent(FileCache.getFileContent(fname));
		for(int i=0;i<varnames.length;i++)
		{
			if (!varnames[i].startsWith("WEBCHART."))
			{
			     if (!newvt.exists(varnames[i]))
			     {
				   newvt.add(varnames[i], vt.getType(varnames[i]));
				   newvt.setValue(varnames[i], vt.getValue(varnames[i]));
			     }
			}
		}
		return newvt;
	}

				
	public static void generateChart(java.io.Writer out, java.io.OutputStream imgout,VariableTable vt, String fileextention) 
		throws java.io.IOException
    	{
		int i=0,sql_count=0;
		DBRowCache crosstab = null, temprows=null;
    		String chartquery = null;
		String querycache = null;
		int    querycachetime = 300;
                int querymaxrows=10000;
		String charttype  = null;
		String iscrosstab = null;
		String readdata   = null;
		String readmile   = null;
		String dbname = null;
		String dbrule = null;
		String groovydbname = null;
		String groovydbrule = null;
		String express = null;
		String foreach = null;
		String varlist = null;
		String joindata= null;
		String summarydata= null;
		String pagecount = null;
		String ignmarkdown = null;
		String ignsqlerror = null;
		boolean pageexpire = false;
		String head_formater = null;
		String data_formater = null;
		String row_color = null;
		String row_style = null;
		String row_align = null;
		String lay_out = null;
		String lay_style = null;
		String loadmodule = null;
		String rotatedata = null;
		String chartycolumn = null;

		String tablename = null;
		String updatesql = null;
		String columnlist = null;
		String columneditor = null;
		String columnstyle = null;
		String columnvalues = null;

		java.util.Vector<String> foreachlist = new java.util.Vector<String>();
		java.util.Vector<String> querylist = new java.util.Vector<String>();
		java.util.HashMap<String, DBRowCache> joindatacache = new java.util.HashMap<String, DBRowCache>();

		String imageonly = getVariableTableValue(vt, "IMAGEONLY");
		if (imageonly == null) imageonly = "NO";
		imageonly = imageonly.toUpperCase();

		DBPooledConnection db	= null;

		if(existVariableTableValue(vt, "QUERY") && getVariableTableValue(vt,"QUERY").length()>0)
                {
			querylist = TextUtils.getWords(getVariableTableValue(vt,"QUERY"),",");
                }
                else
                {
  		    for(i=0;i<100;i++)
		    {
			if (existVariableTableValue(vt,"QUERY", String.valueOf(i+1))) 
			{
				chartquery = getVariableTableValue(vt,"QUERY",String.valueOf(i+1),false);
				if (chartquery != null && chartquery.length() > 0)
				{
					querylist.addElement(String.valueOf(i+1));
				}			
			}
			else if (existVariableTableValue(vt,"DATA", String.valueOf(i+1))) 
			{
				chartquery = getVariableTableValue(vt,"DATA",String.valueOf(i+1),false);
				if (chartquery != null && chartquery.length() > 0)
				{
					querylist.addElement(String.valueOf(i+1));
				}			
			}
			else if (existVariableTableValue(vt,"MODULE", String.valueOf(i+1))) 
			{
				chartquery = getVariableTableValue(vt,"MODULE",String.valueOf(i+1),false);
				if (chartquery != null && chartquery.length() > 0)
				{
					querylist.addElement(String.valueOf(i+1));
				}			
			}
		    }
		}
		if (existVariableTableValue(vt, "EXCELURL"))
		{
		    setVariableTableValue(vt, "EXCELURL", vt.parseString(getVariableTableValue(vt,"EXCELURL")));
		}
		if (!imageonly.equals("YES"))
		{
			if (existVariableTableValue(vt, "RELOAD"))
			{
				java.lang.String reload_list = getVariableTableValue(vt,"RELOAD");
				String reload_arr[] = TextUtils.toStringArray(TextUtils.getWords(reload_list,"|"));
				if (reload_arr.length > 1)
				{
					out.write("<reload time=\""+reload_arr[0]+"\">"+vt.EncodeXML(vt.parseURLString(reload_arr[1]))+"</reload>\n");
				}
			}
			if (existVariableTableValue(vt, "LAYOUT"))
			{
				java.lang.String layout_list = getVariableTableValue(vt,"LAYOUT");
				String layout_arr[] = TextUtils.toStringArray(TextUtils.getWords(layout_list,"|"));
				if (layout_arr.length > 0)
				{
				    out.write("<layout>\n");
                                    for(i=0; i< layout_arr.length; i++)
				    {
					out.write("<column id=\""+i+"\">"+layout_arr[i]+"</column>\n");
				    }
				    out.write("</layout>\n");
				}
			}
			if (existVariableTableValue(vt, "TREEJS"))
			{
				out.write("<tree>\n");
				out.write(vt.EncodeXML(getVariableTableValue(vt,"TREEJS")));
				out.write("\n\n");				
				out.write("</tree>\n");				
			}
			if (existVariableTableValue(vt, "TREE"))
			{
				DBRowCache treedata = new SimpleDBRowCache();
				treedata.addColumn("ID", java.sql.Types.INTEGER);
				treedata.addColumn("PID", java.sql.Types.INTEGER);
				treedata.addColumn("NAME", java.sql.Types.VARCHAR);
				treedata.addColumn("URL", java.sql.Types.VARCHAR);
				treedata.addColumn("TITLE", java.sql.Types.VARCHAR);
				treedata.addColumn("TARGET", java.sql.Types.VARCHAR);
				treedata.addColumn("ICON", java.sql.Types.VARCHAR);
				treedata.addColumn("ICONOPEN", java.sql.Types.VARCHAR);

				java.io.BufferedReader fin = new java.io.BufferedReader(new java.io.StringReader(getVariableTableValue(vt,"TREE")));
				treedata.read(fin, ",", 10000);
				fin.close();				

				if (treedata.getRowCount() > 0)
				{
					StringBuffer treescript = new StringBuffer();
					if (treedata.getRowCount() > 0)
					{
					    Object nodeval;
					    java.util.Vector<String> treeargs = new java.util.Vector<String>();
					    treeargs.add("NAME");
					    treeargs.add("URL");
					    treeargs.add("TITLE");
					    treeargs.add("TARGET");
					    treeargs.add("ICON");
					    treeargs.add("ICONOPEN");

					    treescript.append("dt");
					    treescript.append(" = new dTree('dt");
					    treescript.append("');\n");
				            for(int j = 1; j <= treedata.getRowCount(); j++)
					    {
					         treescript.append("dt");
					         treescript.append(".add(");
						 nodeval = treedata.getItem(j, "ID");
						 if (nodeval != null)
						     treescript.append(nodeval.toString());
						 else
						     treescript.append("0");
						 nodeval = treedata.getItem(j, "PID");
						 if (nodeval != null)
						 {
						     treescript.append(",");						  
						     treescript.append(nodeval.toString());
						 }
						 else
						 {
						     treescript.append(",-1");						  
						 }
						 for (int k=0; k< treeargs.size(); k++)
						 {
						     nodeval = treedata.getItem(j, treeargs.elementAt(k));
						     if (nodeval != null)
						     {
						         treescript.append(",");
						         treescript.append("'");
						         treescript.append(vt.parseString(nodeval.toString()));
						         treescript.append("'");
						     }
						     else
						     {
						         treescript.append(",''");
						     }
						 }
					         treescript.append(");\n");
					    }
					    treescript.append("document.write(dt");
					    treescript.append(");\n");					    
					    treescript.append("\n");					    
				   	    out.write("<tree>\n");
					    out.write(vt.EncodeXML(treescript.toString()));
				   	    out.write("</tree>\n");
					}
				}
			}
			java.util.Vector<String> v_urls = new java.util.Vector<String>();
			v_urls.add("URLS");
			v_urls.add("URLS2");
			v_urls.add("URLS3");
			v_urls.add("URLS4");
			v_urls.add("URLS5");
			for(int vi=0; vi < v_urls.size(); vi ++)
			{
			    if (existVariableTableValue(vt, v_urls.elementAt(vi)))
			    {
				java.lang.String url_list = getVariableTableValue(vt,v_urls.elementAt(vi));
				String url_arr[] = TextUtils.toStringArray(TextUtils.getLines(url_list));
				if (url_arr.length > 0)
				{
				   out.write("<urls>\n");
				   for(i=0; i< url_arr.length; i++)
				   {
					String url_words[] = TextUtils.toStringArray(TextUtils.getWords(url_arr[i],"|"));
					if (url_words.length > 1)
					{
						out.write("<url id=\""+url_words[0]+"\" ");
						if (url_words.length>2)  out.write(url_words[2]);
						out.write(">");
						out.write(vt.EncodeXML(vt.parseURLString(url_words[1])));
						out.write("</url>\n");
					}
				   }
				   out.write("</urls>\n");
				}
			    }
			}
			if (existVariableTableValue(vt, "TOPURLS"))
			{
				java.lang.String url_list = getVariableTableValue(vt,"TOPURLS");
				String url_arr[] = TextUtils.toStringArray(TextUtils.getLines(url_list));
				if (url_arr.length > 0)
				{
				   out.write("<topurls>\n");
				   for(i=0; i< url_arr.length; i++)
				   {
					String url_words[] = TextUtils.toStringArray(TextUtils.getWords(url_arr[i],"|"));
					if (url_words.length > 1)
					{
						out.write("<url id=\""+url_words[0]+"\" ");
						if (url_words[0].equalsIgnoreCase(getVariableTableValue(vt,"TOPCURR"))) out.write(" cur=\"yes\" ");
						if (url_words.length>2)  out.write(url_words[2]);
						out.write(">");
						out.write(vt.EncodeXML(vt.parseURLString(url_words[1])));
						out.write("</url>\n");
					}
				   }
				   out.write("</topurls>\n");
				}
			}
			if (existVariableTableValue(vt, "LEFTURLS"))
			{
				java.lang.String url_list = getVariableTableValue(vt,"LEFTURLS");
				String url_arr[] = TextUtils.toStringArray(TextUtils.getLines(url_list));
				if (url_arr.length > 0)
				{
				   out.write("<lefturls>\n");
				   for(i=0; i< url_arr.length; i++)
				   {
					String url_words[] = TextUtils.toStringArray(TextUtils.getWords(url_arr[i],"|"));
					if (url_words.length > 1)
					{
						out.write("<url id=\""+url_words[0]+"\" ");
						if (url_words[0].equalsIgnoreCase(getVariableTableValue(vt,"LEFTCURR"))) out.write(" cur=\"yes\" ");
						if (url_words.length>2)  out.write(url_words[2]);
						out.write(">");
						out.write(vt.EncodeXML(vt.parseURLString(url_words[1])));
						out.write("</url>\n");
					}
				   }
				   out.write("</lefturls>\n");
				}
			}
			java.util.Vector<String> v_inputs = new java.util.Vector<String>();
			v_inputs.add("INPUTS");
			v_inputs.add("INPUTS2");
			v_inputs.add("INPUTS3");
			v_inputs.add("INPUTS4");
			v_inputs.add("INPUTS5");
			for(int vi = 0; vi < v_inputs.size(); vi ++)
			{
			    if (existVariableTableValue(vt, v_inputs.elementAt(vi)))
			    {
				java.lang.String input_list = getVariableTableValue(vt,v_inputs.elementAt(vi));
				String input_arr[] = TextUtils.toStringArray(TextUtils.getLines(input_list));
				if (input_arr.length > 0)
				{
				   out.write("<inputs action=\"" + vt.EncodeXML(vt.parseURLString("${REQUEST.FILE}")) + "\">\n");
				   for(i=0; i< input_arr.length; i++)
				   {
					String input_words[] = TextUtils.toStringArray(TextUtils.getWords(input_arr[i],"|"));
					if (input_words.length > 2)
					{
                                                if ("CUSTOM".equalsIgnoreCase(input_words[0]))
                                                {
						    out.write("<item type=\""+input_words[0]+"\" name=\""+
							      input_words[1]+"\" value=\""+vt.EncodeXML(input_words[2])+
							      "\" ");
						    out.write(" />\n");
                                                }
                                                else if ("option".equalsIgnoreCase(input_words[0]))
                                                {
						    String select_option_value = vt.parseString(input_words[2]);
						    out.write("<item type=\""+input_words[0]+"\" name=\""+
							      input_words[1]+"\" value=\""+vt.EncodeXML(select_option_value)+"\" ");
                                                    if (input_words.length > 4)
                                                       out.write(" label=\""+vt.EncodeXML(input_words[4])+"\" ");
                                                    out.write(">\n");
                                                    if (input_words.length > 3)
                                                    {
                                                        String select_values[] = TextUtils.toStringArray(TextUtils.getWords(input_words[3],";"));
                                                        for(int svi = 0; svi < select_values.length; svi ++)
                                                        {
                                                            if (select_values[svi].equals(select_option_value))
                                                                out.write("<option selected=\"1\">"+select_values[svi]+"</option>");
                                                            else
                                                                out.write("<option>"+select_values[svi]+"</option>\n");
                                                        }
                                                    }
						    out.write("</item>\n");
                                                }
                                                else if ("checkbox".equalsIgnoreCase(input_words[0]))
                                                {
						    String select_option_value = vt.parseString(input_words[2]);
						    out.write("<item type=\""+input_words[0]+"\" name=\""+
							      input_words[1]+"\" value=\""+vt.EncodeXML(select_option_value)+"\" ");
                                                    out.write(" label=\""+vt.EncodeXML(select_option_value)+"\" ");
                                                    if (input_words.length > 3)
                                                    {
							String field_sep = ",";
		                                        if (vt.exists("WEBCHART.SEP"))
                                                        {
                                                             if ("\\N".equalsIgnoreCase(vt.getString("WEBCHART.SEP")))
                         					   field_sep = "\n";
                                                             else
       					                           field_sep = vt.getString("WEBCHART.SEP");
                                                        }
                                                        String select_values[] = TextUtils.toStringArray(TextUtils.getWords(vt.parseString(input_words[3]),field_sep));
                                                        for(int svi = 0; svi < select_values.length; svi ++)
                                                        {
                                                            if (select_values[svi].equals(select_option_value))
	                                                        out.write(" checked=\"yes\" ");
                                                        }
                                                    }
                                                    out.write(" />\n");
                                                }
                                                else
                                                {
						    out.write("<item type=\""+input_words[0]+"\" name=\""+
							      input_words[1]+"\" value=\""+vt.EncodeXML(vt.parseString(input_words[2]))+
							      "\" ");
						    if (input_words.length > 3) out.write(input_words[3]);
                                                    if (input_words.length > 4)
                                                       out.write(" label=\""+vt.EncodeXML(input_words[4])+"\" ");
						    out.write(" />\n");
                                                }
					}
				   }
				   out.write("</inputs>\n");
				}
			    }
			}
			vt.writeXMLBody(out);
		}
		if (existVariableTableValue(vt, "EXPIRE"))
		{
		    if (DateOperator.getDay().compareTo(getVariableTableValue(vt,"EXPIRE")) > 0) pageexpire = true;
		}
		if (querylist.size()>0)
		{
			crosstab = DBOperation.getDBRowCache();
			for(i=0;i<querylist.size();i++)
			{	
			    /* crosstab = DBOperation.getDBRowCache();  */
			    iscrosstab = getVariableTableValue(vt, "CROSSTAB", querylist.elementAt(i), true);
			    chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i),false);
			    joindata   = getVariableTableValue(vt, "JOIN", querylist.elementAt(i),false);
			    summarydata   = getVariableTableValue(vt, "SUMMARY", querylist.elementAt(i),false);
			    loadmodule = getVariableTableValue(vt, "MODULE", querylist.elementAt(i),false);
			    querycache = getVariableTableValue(vt, "QUERYCACHE", querylist.elementAt(i),false);
			    head_formater = getVariableTableValue(vt, "HEADHTML", querylist.elementAt(i),false);
			    data_formater = getVariableTableValue(vt, "DATAHTML", querylist.elementAt(i),false);
			    row_color = getVariableTableValue(vt, "ROWCOLOR", querylist.elementAt(i),true);
			    row_style = getVariableTableValue(vt, "STYLE", querylist.elementAt(i),true);
			    row_align = getVariableTableValue(vt, "ALIGN", querylist.elementAt(i),true);
			    querycachetime = getint(getVariableTableValue(vt, "QUERYCACHETIME", querylist.elementAt(i),true),300);
			    charttype = getVariableTableValue(vt, "TYPE", querylist.elementAt(i),true);
			    varlist   = vt.parseString(getVariableTableValue(vt, "VARLIST", querylist.elementAt(i),true));
			    pagecount = getVariableTableValue(vt, "PAGES", querylist.elementAt(i),true);
			    dbname = getVariableTableValue(vt, "DBNAME", querylist.elementAt(i),true);
			    dbrule = getVariableTableValue(vt, "DBID", querylist.elementAt(i),true);
			    readdata = getVariableTableValue(vt, "DATA", querylist.elementAt(i),false);
			    readmile = getVariableTableValue(vt, "MILE", querylist.elementAt(i),true);
			    groovydbname = getVariableTableValue(vt, "GROOVYDBNAME", querylist.elementAt(i),true);
			    groovydbrule = getVariableTableValue(vt, "GROOVYDBID", querylist.elementAt(i),true);
			    ignsqlerror = getVariableTableValue(vt, "IGNORE_SQLERROR", querylist.elementAt(i),true);
			    ignmarkdown = getVariableTableValue(vt, "IGNORE_MARKDOWN", querylist.elementAt(i),true);
			    lay_out = getVariableTableValue(vt, "LAYOUT", querylist.elementAt(i),false);
			    lay_style = getVariableTableValue(vt, "LAYOUTSTYLE", querylist.elementAt(i),false);
			    tablename = getVariableTableValue(vt, "TABLE", querylist.elementAt(i),false);
			    updatesql = getVariableTableValue(vt, "UPDATESQL", querylist.elementAt(i),false);
			    columnlist   = getVariableTableValue(vt, "COLUMN", querylist.elementAt(i),false);
			    columneditor = getVariableTableValue(vt, "EDITOR", querylist.elementAt(i),false);
			    columnstyle = getVariableTableValue(vt, "EDITORSTYLE", querylist.elementAt(i),false);
			    columnvalues= getVariableTableValue(vt, "VALUES", querylist.elementAt(i),false);
			    querymaxrows = getint(getVariableTableValue(vt, "MAXROWS", querylist.elementAt(i),true),10000);
	   		    chartycolumn = vt.parseString(getVariableTableValue(vt,"YCOL",querylist.elementAt(i),true));

			    if (lay_out == null) lay_out = "0";

			    foreach = vt.parseString(getVariableTableValue(vt, "FORALL", querylist.elementAt(i),false));
				
			    foreachlist.removeAllElements();
			    if (foreach != null)
				foreachlist.addAll(TextUtils.getLines(foreach));
			    
			    for(int forj=0; forj < (foreachlist.size() > 0 ? foreachlist.size() : 1); forj++)
			    {
				if (forj < foreachlist.size())
				{
				  if (foreachlist.get(forj) == null ||
				    foreachlist.get(forj).trim().length() == 0)
				    continue;
				  vt.setValue(foreachlist.get(forj));
				}

				/*
				if (loadmodule != null)
				{
					VariableTable newvt = readModuleConfig(loadmodule, vt);
					generateChart(out, imgout, newvt, fileextention);
					continue;
				}
				*/

				if (!"-".equals(chartquery) || readdata != null || readmile != null)
				{
				    crosstab = DBOperation.getDBRowCache();
				    if (querycache != null)
				    {
					// DataCache.clearData();
					crosstab = joindatacache.get(vt.parseString(querycache));
				    }
				    if (crosstab == null || crosstab.getColumnCount() == 0)
				    {
				      if (!pageexpire && checkVariableList(varlist,vt))
			    	      {
				         for(int dsloop=0; dsloop < 2; dsloop ++)
				         {
				           try {
                                             /*
					     if (groovydbname != null || groovydbrule != null)
                                             {
                                                 DBGroovyScript dbgroovy = new DBGroovyScript();
                                                 if (groovydbname != null) dbname = String.valueOf(dbgroovy.getValue(vt, groovydbname));
                                                 if (groovydbrule != null) dbrule = String.valueOf(dbgroovy.getValue(vt, groovydbrule));
                                             }
                                             */

					     if (readdata == null && readmile == null)
					     {
					       if (dbname != null && (dbname.startsWith("url::") || dbname.startsWith("URL::")))
					       {
						   try {
				                       db = new DBPooledConnection(DBOperation.getConnection(dbname.substring(5)));
						   } catch (java.lang.ClassNotFoundException cnfe) { throw new java.io.IOException(cnfe.getMessage()); };
					       }
					       else
				                   db = DBLogicalManager.getPoolConnection(vt.parseString(dbname), dbrule);

  					       if (tablename != null && tablename.length() > 0 && columnlist != null && columnlist.length() > 0 && checkVariableList(columnlist,vt))
					       {
						  try {
							if (updatesql != null)
							{
							     DBOperation.executeUpdate(db, updatesql, vt);
	 						     try { db.commit(); } catch (java.sql.SQLException sqlecmt) {}
							}
							else
							{
							    if (vt.getString("sqleditmode").equalsIgnoreCase("INSERT"))
							    {
							        DBOperation.executeUpdate(db, SQLCreator.getInsertSQL(tablename, columnlist), vt);
	 						        try { db.commit(); } catch (java.sql.SQLException sqlecmt) {}
							    }
							    else if (vt.getString("sqleditmode").equalsIgnoreCase("UPDATE"))
							    {
							        DBOperation.executeUpdate(db, SQLCreator.getUpdateSQL(tablename, columnlist), vt);
	 						        try { db.commit(); } catch (java.sql.SQLException sqlecmt) {}
							    }
							    else if (vt.getString("sqleditmode").equalsIgnoreCase("DELETE"))
							    {
							        DBOperation.executeUpdate(db, SQLCreator.getDeleteSQL(tablename, columnlist), vt);
	 						        try { db.commit(); } catch (java.sql.SQLException sqlecmt) {}
							    }
							}
						  }
						  catch (java.sql.SQLException sqle)
						  {
						     sqle.printStackTrace();
						     if (db != null) { db.close(); db = null;}
						     throw new java.io.IOException(sqle.getMessage()); 
						  }
					       }
    					       chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i),false);
					       if (chartquery.equalsIgnoreCase("*"))
					       {
						  chartquery = getVariableTableValue(vt, "QUERY_"+db.getDBTag(), querylist.elementAt(i),false);
					       }
					       java.util.Vector<String> cross_fields = TextUtils.getWords(iscrosstab,"|");
					       if (cross_fields.size()==0)
						  crosstab = DBOperation.executeQuery(db,chartquery,vt);
					       else if(cross_fields.size() < 3)
						  crosstab = DBOperation.executeCrossTab(db,chartquery,vt);
					       else
						  crosstab = DBOperation.executeCrossTab(db,chartquery,vt,
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(0),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(1),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(2),",")));
                                             }
                                             else if (readdata != null)
                                             {
						String colarrs[] = null;
					        String readarr[] = TextUtils.toStringArray(TextUtils.getLines(readdata));
					        for(int tmpk=0; tmpk < readarr.length; tmpk++)
					        {
						    colarrs = TextUtils.toStringArray(TextUtils.getWords(readarr[tmpk]));
						    if (colarrs != null && colarrs.length > 0)
						    {
						        if ("ADD".equalsIgnoreCase(colarrs[0]))
						        {
						            if (colarrs.length > 2)
							       crosstab.addColumn(colarrs[1], SQLTypes.getTypeID(colarrs[2]));
						            else if (colarrs.length > 1)
							       crosstab.addColumn(colarrs[1], java.sql.Types.VARCHAR);
						        }
							else if ("LOAD".equalsIgnoreCase(colarrs[0]))
							{
							    if (colarrs.length > 2)
							    {
								try {
							            java.io.BufferedReader fin = new java.io.BufferedReader(new java.io.FileReader(colarrs[1]));
								    crosstab.read(fin, colarrs[2], 10000);
							            fin.close();
								} catch (java.io.IOException ioe) {}
							    }
							    else
							    {
								try {
							            java.io.BufferedReader fin = new java.io.BufferedReader(new java.io.FileReader(colarrs[1]));
								    crosstab.read(fin, ",", 10000);
							            fin.close();
								} catch (java.io.IOException ioe) {}							        
							    }		
							}
						    }
						}						
                                             }
					     else if (readmile != null)
					     {
						 crosstab = WebChartMileClient.executeQuery(readmile, chartquery,vt);
					         java.util.Vector<String> cross_fields = TextUtils.getWords(iscrosstab,"|");
					         if (cross_fields.size()==0)
						     crosstab = WebChartMileClient.executeQuery(readmile, chartquery,vt);
					         else if(cross_fields.size() < 3)
						     crosstab = WebChartMileClient.executeCrossTab(readmile, chartquery,vt);
					         else
						     crosstab = WebChartMileClient.executeCrossTab(readmile, chartquery,vt,
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(0),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(1),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(2),",")));

					     }
					     express = getVariableTableValue(vt, "EXPRESS", querylist.elementAt(i),true);
					     if (express != null)
					     {
					       String colname;
					       String expstr;
					       String expcols[] = null;
					       String colarrs[] = null;
					       java.util.Vector<String> expwords;
					       String exparr[] = TextUtils.toStringArray(TextUtils.getLines(express));
					       for(int tmpk=0; tmpk < exparr.length; tmpk++)
					       {
					    	  expwords = TextUtils.getWords(exparr[tmpk],"|");
					    	  if (expwords.size() == 3)
					    	  {
					    		colname = expwords.elementAt(0);
					    		expstr  = expwords.elementAt(1);
					    		expcols = TextUtils.toStringArray(TextUtils.getWords(expwords.elementAt(2),","));
							crosstab.addExpression(vt.parseString(colname), expstr, expcols);
						  }
						  else if (expwords.size() == 2)
					    	  {
					    		colname = expwords.elementAt(0);
					    		expstr  = expwords.elementAt(1);
							crosstab.addExpression(vt.parseString(colname), expstr);
						  }
					       }
					     }

					     if (joindata != null)
					     {
						  DBRowCache data_for_join = null;
						  String joinsarr[] = TextUtils.toStringArray(TextUtils.getLines(joindata));
						  for (int tmpk = 0; tmpk < joinsarr.length; tmpk++)
						  {
						      String joinarr[] = TextUtils.toStringArray(TextUtils.getWords(joinsarr[tmpk],"|"));
						      if (joinarr != null && joinarr.length >= 2 && (data_for_join = joindatacache.get(joinarr[0])) != null)
						      {
							  crosstab.joinData(data_for_join, joinarr[1]);
							  // DataCache.removeData(joinarr[0]);
						      }
						  }
					     }

					     if (summarydata != null)
					     {
						  DBRowCache data_for_summary = null;
						  String summaryarr[] = TextUtils.toStringArray(TextUtils.getWords(summarydata,"|"));
						  if (summaryarr.length == 2)
						  {
						      data_for_summary = crosstab.groupData(TextUtils.toStringArray(TextUtils.getWords(summaryarr[0],",")),
										   TextUtils.toStringArray(TextUtils.getWords(summaryarr[1],",")));
						  }
						  if (data_for_summary.getColumnCount() > 0)
						  {
							crosstab = data_for_summary;
						  }
					     }

                                             /*
					     express = getVariableTableValue(vt, "GROOVYEXPRESS", querylist.elementAt(i),true);
					     if (express != null)
					     {
					       String colname;
					       String expstr;
					       String expcols[] = null;
					       String colarrs[] = null;
					       java.util.Vector<String> expwords;
					       String exparr[] = TextUtils.toStringArray(TextUtils.getLines(express));
					       for(int tmpk=0; tmpk < exparr.length; tmpk++)
					       {
					    	  expwords = TextUtils.getWords(exparr[tmpk],"|");
					    	  if (expwords.size() > 1)
					    	  {
					    		colname = expwords.elementAt(0);
					    		expstr  = expwords.elementAt(1);
                                                        crosstab.addGroovyExpression(colname, expstr);
						  }
					       }
					     }
                                             */
					     express = getVariableTableValue(vt, "FILTER", querylist.elementAt(i),true);
					     if (express != null)
					     {
					       String expstr;
					       String expcols[] = null;
					       String exparr[] = TextUtils.toStringArray(TextUtils.getLines(express));
					       for(int tmpk=0; tmpk < exparr.length; tmpk++)
						   crosstab.expressFilter(exparr[tmpk]);
					     }
					     if (querycache!= null)
					     {
						joindatacache.put(vt.parseString(querycache), crosstab);
					     }
					     if (db != null) { db.close(); db = null; }
					     break;
				          }
			 	          catch(java.sql.SQLException sqle)
				          {
					     crosstab = DBOperation.getDBRowCache();
					     db.checkSQLState(sqle.getSQLState());
					     if (dsloop == 1)
					     {
					       if ("YES".equalsIgnoreCase(ignsqlerror))
					       {
						  continue;
					       }
					       else
					       {
						  throw new java.io.IOException(sqle.getMessage());
					       }
					    }
				          }
				          catch(DatabaseMarkdownException dme)
				          {
					     if (dsloop == 1)
					     {
					        if ("YES".equalsIgnoreCase(ignsqlerror) || "YES".equalsIgnoreCase(ignmarkdown))
					        {
						  continue;
					        }
					        else
					        {
						  throw dme;
					        }
					     }
				          }
				          finally
				          {
					    if (db != null) { db.close(); db = null; }
				          }
				        }
				      }
				   }
				}
				if (db != null) { db.close(); db = null; }

				
				String chartlabel = vt.parseString(getVariableTableValue(vt, "LABEL", querylist.elementAt(i),false));
				java.util.Vector<String> label = TextUtils.getFields(chartlabel,"|");
				for(int j=0;j<label.size() && j<crosstab.getColumnCount();j++)
				{
					if (label.elementAt(j) != null)
						crosstab.setColumnLabel(j+1,label.elementAt(j));
				}

				String superchartlabel = vt.parseString(getVariableTableValue(vt, "SUPER", querylist.elementAt(i),false));
				java.util.Vector<String> superlabel = TextUtils.getFields(superchartlabel,"|");
				for(int j=0;j<superlabel.size() && j<crosstab.getColumnCount();j++)
				{
					if (superlabel.elementAt(j) != null)
						crosstab.setColumnSuperLabel(j+1,superlabel.elementAt(j));
				}

				rotatedata = getVariableTableValue(vt, "ROTATE", querylist.elementAt(i),true);
				if ("YES".equalsIgnoreCase(rotatedata) || "ON".equalsIgnoreCase(rotatedata))
				{
				    DBRowCache newdata = crosstab.rotate();
				    crosstab = newdata;
				}

				if (head_formater != null) crosstab.setStringProperty("HEADFORMATER",head_formater);
				if (data_formater != null) crosstab.setStringProperty("DATAFORMATER",data_formater);
				if (columnlist != null) crosstab.setStringProperty("PRIMARYKEY",columnlist);
				if (columneditor != null) crosstab.setStringProperty("COLUMNEDITOR",columneditor);
				if (columnstyle != null) crosstab.setStringProperty("EDITORSTYLE",columnstyle);
				if (columnvalues != null) crosstab.setStringProperty("COLUMNVALUES",columnvalues);
				if (row_color != null) crosstab.setStringProperty("ROWCOLOR",row_color);
				if (row_style != null) crosstab.setStringProperty("ROWSTYLE",row_style);
				if (row_align != null) crosstab.setStringProperty("COLUMNALIGN",row_align);

				if (crosstab.getRowCount()==1)
				{
					for(int j=1;j<=crosstab.getColumnCount();j++)
					{
						vt.add("QUERY_"+querylist.elementAt(i)+"."+crosstab.getColumnName(j),
							java.sql.Types.VARCHAR);
						vt.setValue("QUERY_"+querylist.elementAt(i)+"."+crosstab.getColumnName(j),
							crosstab.getItem(1,j));
					}
				} 
				
				if ("SET".equalsIgnoreCase(charttype))
				{
					vt.add("ARRAY."+querylist.elementAt(i), java.sql.Types.VARCHAR);
					vt.setValue("ARRAY."+querylist.elementAt(i), crosstab.getFullText());
				}
				else if ("CACHE".equalsIgnoreCase(charttype))
				{
					// Do nothing.
				}
				else if ("TREE".equalsIgnoreCase(charttype))
				{
					StringBuffer treescript = new StringBuffer();
					if (crosstab.getRowCount() > 0)
					{
					    Object nodeval;
					    java.util.Vector<String> treeargs = new java.util.Vector<String>();
					    treeargs.add("NAME");
					    treeargs.add("URL");
					    treeargs.add("TITLE");
					    treeargs.add("TARGET");
					    treeargs.add("ICON");
					    treeargs.add("ICONOPEN");

					    treescript.append("dt");
					    treescript.append(querylist.elementAt(i));
					    treescript.append(" = new dTree('dt");
					    treescript.append(querylist.elementAt(i));
					    treescript.append("');\n");
				            for(int j = 1; j <= crosstab.getRowCount(); j++)
					    {
					         treescript.append("dt");
						 treescript.append(querylist.elementAt(i));
					         treescript.append(".add(");
						 nodeval = crosstab.getItem(j, "ID");
						 if (nodeval != null)
						     treescript.append(nodeval.toString());
						 else
						     treescript.append("0");
						 nodeval = crosstab.getItem(j, "PID");
						 if (nodeval != null)
						 {
						     treescript.append(",");						  
						     treescript.append(nodeval.toString());
						 }
						 else
						 {
						     treescript.append(",-1");						  
						 }
						 for (int k=0; k< treeargs.size(); k++)
						 {
						     nodeval = crosstab.getItem(j, treeargs.elementAt(k));
						     if (nodeval != null)
						     {
						         treescript.append(",");
						         treescript.append("'");
						         treescript.append(vt.parseString(nodeval.toString()));
						         treescript.append("'");
						     }
						     else
						     {
						         treescript.append(",''");
						     }
						 }
					         treescript.append(");\n");
					    }
					    treescript.append("document.write(dt");
					    treescript.append(querylist.elementAt(i));
					    treescript.append(");\n");					    
					    treescript.append("\n");					    
				   	    out.write("<tree>\n");
					    out.write(vt.EncodeXML(treescript.toString()));
				   	    out.write("</tree>\n");
					}
				}
				else if ("URL".equalsIgnoreCase(charttype))
				{
					String url_pattern = getVariableTableValue(vt, "URLSTRING", querylist.elementAt(i),false);
					if (url_pattern != null)
					{
						String url_words[] = TextUtils.toStringArray(TextUtils.getWords(url_pattern,"|"));
						if (url_words.length > 1)
						{
				   			out.write("<urls>\n");
				   			for(int j=1; j<=crosstab.getRowCount(); j++)
				   			{
								if (j>1) out.write("<url id=\"-\">,</url>\n");
								out.write("<url id=\""+crosstab.getItem(j,url_words[0])+"\">");
								out.write(vt.EncodeXML(crosstab.parseString(url_words[1], vt,j,1)));
								out.write("</url>\n");
				   			}
				   			out.write("</urls>\n");
						}
					}
				}
				else
				{
				    if (charttype == null || !charttype.equals("-"))
				    {
					if (temprows != null)
					{
						temprows.appendRow(crosstab);
						crosstab = temprows;
					}
					if ("YES".equals(imageonly))
					{
						generateChart(crosstab,imgout,vt,querylist.elementAt(i), fileextention);
						temprows = null;
						return;
					}
					else
					{
						for(int tmpj=crosstab.getRowCount(); tmpj > querymaxrows; tmpj --)
						{
						     crosstab.deleteRow(tmpj);
						}
						crosstab.setPageSize(0);
						if (pagecount != null)
						{
						     try {
							int pagerows = Integer.valueOf(pagecount).intValue();
							if (pagerows > 1 && pagerows < crosstab.getRowCount()) 
								crosstab.setPageSize((pagerows - 1 + crosstab.getRowCount())/pagerows);
						     } catch (NumberFormatException nfe) {}
						}
						if ("*".equalsIgnoreCase(chartycolumn))
						{
						    for(int tmpi=2;tmpi<=crosstab.getColumnCount();tmpi++)
						    {
							setVariableTableValue(vt,"YCOL_"+querylist.elementAt(i),crosstab.getColumnName(tmpi));
							if (lay_style == null)
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\">\n");
							else
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\" style=\""+vt.EncodeXML(lay_style)+"\">\n");
	
			                                if ((express=getVariableTableValue(vt,"XMLDATA",querylist.elementAt(i),false)) != null)
        			                            out.write(vt.parseString(express));
		        	                        if ((express=getVariableTableValue(vt,"TITLE",querylist.elementAt(i),false)) != null)
        		        	                    out.write("  <title><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></title>\n");
                		        	        if ((express=getVariableTableValue(vt,"SUBTITLE",querylist.elementAt(i),false)) != null)
                        		        	    out.write("  <subtitle><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></subtitle>\n");
	                		                if ((express=getVariableTableValue(vt,"FOOTNOTE",querylist.elementAt(i),false)) != null)
			                                    out.write("  <footnote><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></footnote>\n");
                			                if ((express=getVariableTableValue(vt,"MEMO",querylist.elementAt(i),false)) != null)
		        	                            out.write("  <memo><![CDATA["+ crosstab.parseString(express, vt, 0, 0) +"]]></memo>\n");
							if (crosstab.getColumnCount() > 0)
							    generateChart(crosstab,out,vt,querylist.elementAt(i), fileextention);
							out.write("</webchart>\n");
						    }
						}
						else if (chartycolumn != null && TextUtils.getLines(chartycolumn).size() > 1)
						{
						    String chartycolarr[] = TextUtils.toStringArray(TextUtils.getLines(chartycolumn));
						    for(int tmpi=0;tmpi<chartycolarr.length;tmpi++)
						    {
							if (chartycolarr[tmpi] == null || chartycolarr[tmpi].length() == 0) continue;
							setVariableTableValue(vt,"YCOL_"+querylist.elementAt(i),chartycolarr[tmpi]);
							if (lay_style == null)
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\">\n");
							else
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\" style=\""+vt.EncodeXML(lay_style)+"\">\n");
	
			                                if ((express=getVariableTableValue(vt,"XMLDATA",querylist.elementAt(i),false)) != null)
        			                            out.write(vt.parseString(express));
		        	                        if ((express=getVariableTableValue(vt,"TITLE",querylist.elementAt(i),false)) != null)
        		        	                    out.write("  <title><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></title>\n");
                		        	        if ((express=getVariableTableValue(vt,"SUBTITLE",querylist.elementAt(i),false)) != null)
                        		        	    out.write("  <subtitle><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></subtitle>\n");
	                		                if ((express=getVariableTableValue(vt,"FOOTNOTE",querylist.elementAt(i),false)) != null)
			                                    out.write("  <footnote><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></footnote>\n");
                			                if ((express=getVariableTableValue(vt,"MEMO",querylist.elementAt(i),false)) != null)
		        	                            out.write("  <memo><![CDATA["+ crosstab.parseString(express, vt, 0, 0) +"]]></memo>\n");
							if (crosstab.getColumnCount() > 0)
							    generateChart(crosstab,out,vt,querylist.elementAt(i), fileextention);
							out.write("</webchart>\n");
						    }
						}
						else
						{
							if (lay_style == null)
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\">\n");
							else
							    out.write("<webchart id=\""+querylist.elementAt(i)+"\" layout=\""+vt.parseString(lay_out)+"\" style=\""+vt.EncodeXML(lay_style)+"\">\n");
	
			                                if ((express=getVariableTableValue(vt,"XMLDATA",querylist.elementAt(i),false)) != null)
        			                            out.write(vt.parseString(express));
		        	                        if ((express=getVariableTableValue(vt,"TITLE",querylist.elementAt(i),false)) != null)
        		        	                    out.write("  <title><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></title>\n");
                		        	        if ((express=getVariableTableValue(vt,"SUBTITLE",querylist.elementAt(i),false)) != null)
                        		        	    out.write("  <subtitle><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></subtitle>\n");
	                		                if ((express=getVariableTableValue(vt,"FOOTNOTE",querylist.elementAt(i),false)) != null)
			                                    out.write("  <footnote><![CDATA["+ crosstab.parseString(express,vt,0,0) +"]]></footnote>\n");
                			                if ((express=getVariableTableValue(vt,"MEMO",querylist.elementAt(i),false)) != null)
		        	                            out.write("  <memo><![CDATA["+ crosstab.parseString(express, vt, 0, 0) +"]]></memo>\n");
							if (crosstab.getColumnCount() > 0)
							    generateChart(crosstab,out,vt,querylist.elementAt(i), fileextention);
							out.write("</webchart>\n");
						}
						temprows = null;
					}
				    }
				    else
				    {
					if (crosstab.getColumnCount() > 0)
					{
					   if (temprows==null || temprows.getColumnCount() != crosstab.getColumnCount())
					   {
						temprows = DBOperation.getDBRowCache();
						temprows.copyColumns(crosstab);
					   }
					   temprows.appendRow(crosstab);
					   crosstab = DBOperation.getDBRowCache();
					}
				    }
				}
			    }
			}
		}		
		joindatacache.clear();
	}

	private static void generateChart(DBRowCache crosstab,java.io.Writer out,VariableTable vt,String index, String fileextention)
		throws java.io.IOException 
    	{
    		int i;
    		String mapcss="";
 		int width=500,height=400,grpcolcount=2, unitsize=1;

 		String fileNameChart="";

    		String chartdefaultfont = getVariableTableValue(vt,"defaultfont");
    		String chartdefaultcolor = getVariableTableValue(vt,"defaultcolor");
    		
		String colors = getVariableTableValue(vt,"colors", index, true);
		
		String xmltag = getVariableTableValue(vt,"xmltag", index, true);
		String xmlattr = getVariableTableValue(vt,"xmlattr", index, true);

   		String legendposition    = getVariableTableValue(vt,"legend",index,true);
   		String legendfont        = getVariableTableValue(vt,"legendfont",index,true);
   		String legendcolor       = getVariableTableValue(vt,"legendcolor",index,true);
   		String plotbackcolor    = getVariableTableValue(vt,"plotbackcolor",index,true);
   		String plotedgecolor    = getVariableTableValue(vt,"plotedgecolor",index,true);   		
   		String chartorient    = getVariableTableValue(vt,"orient",index,true);		
    		String backcolor = getVariableTableValue(vt,"backcolor", index, true);
    		String edgecolor = getVariableTableValue(vt,"edgecolor",index, true);
   		String gridline     = getVariableTableValue(vt,"GRID",index,true);
   		String gridstyle     = getVariableTableValue(vt,"GRIDLINE",index,true);
   		String gridcolor     = getVariableTableValue(vt,"GRIDCOLOR",index,true);
    		
    		String charttype = getVariableTableValue(vt,"type", index, true);
    		String chartsubtype = getVariableTableValue(vt,"subtype", index, true);
    		String chartsubtype2 = getVariableTableValue(vt,"subtype2",index, true);

    		String chartunitsize = getVariableTableValue(vt,"unitsize",index,true);    		
    		String chartwidth = getVariableTableValue(vt,"width",index,true);
    		String chartheight = getVariableTableValue(vt,"height",index,true);
		String chartmargin = getVariableTableValue(vt,"ywidth",index,true);
		
    		String charttitle = crosstab.parseString(getVariableTableValue(vt,"title",index,false),vt,0,0);
    		String charttitlefont = getVariableTableValue(vt,"titlefont",index,true);
    		String charttitlecolor = getVariableTableValue(vt,"titlecolor",index,true);

    		String chartsubtitle = crosstab.parseString(getVariableTableValue(vt,"subtitle",index,false),vt,0,0);
    		String chartsubtitlefont = getVariableTableValue(vt,"subtitlefont",index,true);
    		String chartsubtitlecolor = getVariableTableValue(vt,"subtitlecolor",index,true);

    		String chartfootnote = crosstab.parseString(getVariableTableValue(vt,"footnote",index,false),vt,0,0);
    		String chartfootnotefont = getVariableTableValue(vt,"footnotefont",index,true);
    		String chartfootnotecolor = getVariableTableValue(vt,"footnotecolor",index,true);


   		String charthref    = getVariableTableValue(vt,"href",index,true);
   		String charthreftarget    = getVariableTableValue(vt,"hreftarget",index,true);
   		String chartformater    = getVariableTableValue(vt,"formater",index,true);
   		String headerformater    = getVariableTableValue(vt,"hformater",index,true);
   		String chartexclude    = getVariableTableValue(vt,"exclude",index,true);

    		String stock_open = getVariableTableValue(vt,"stock_open",index,false);
    		String stock_high = getVariableTableValue(vt,"stock_high",index,false);
    		String stock_low = getVariableTableValue(vt,"stock_low",index,false);
    		String stock_close = getVariableTableValue(vt,"stock_close",index,false);
    		String stock_volume = getVariableTableValue(vt,"stock_volume",index,false);

   		String pielabelstyle    = getVariableTableValue(vt,"PIELABEL",index,true);
   		String pielabeldigit    = getVariableTableValue(vt,"PIEDIGIT",index,true);
   		
   		String tooltipcolumn    = getVariableTableValue(vt,"TOOLTIP",index,true);
   		String chartxcolumn    = vt.parseString(getVariableTableValue(vt,"XCOL",index,true));
   		String chartxlabel    = getVariableTableValue(vt,"XLABEL",index,true);
   		String chartxmaxval    = getVariableTableValue(vt,"XMAX",index,true);
   		String chartycolumn    = vt.parseString(getVariableTableValue(vt,"YCOL",index,true));
   		String chartylabel    = getVariableTableValue(vt,"YLABEL",index,true);
   		String chartymaxval    = getVariableTableValue(vt,"YMAX",index,true);
		String chartyformat    = getVariableTableValue(vt,"YFORMAT",index,true);
   		String charty2column    = vt.parseString(getVariableTableValue(vt,"Y2COL",index,true));
   		String charty2maxval    = getVariableTableValue(vt,"Y2MAX",index,true);
		String charty2format    = getVariableTableValue(vt,"Y2FORMAT",index,true);

   		String subchartcolumn    = vt.parseString(getVariableTableValue(vt,"SUBCHARTCOL",index,true));
   		String subchartheight    = getVariableTableValue(vt,"SUBCHARTHEIGHT",index,true);
   		String subcharttype      = getVariableTableValue(vt,"SUBCHARTTYPE",index,true);
   		String subchartsubtype      = getVariableTableValue(vt,"SUBCHARTSUBTYPE",index,true);
   		String subchartsubtype2     = getVariableTableValue(vt,"SUBCHARTSUBTYPE2",index,true);
   		String subchartymaxval     = getVariableTableValue(vt,"SUBCHARTYMAX",index,true);

   		String groupcolumncount     = getVariableTableValue(vt,"GROUP",index,true);
   		String mergecolumnlist     = getVariableTableValue(vt,"MERGE",index,true);
   		String chartimagemap     = getVariableTableValue(vt,"IMAGEMAP",index,true);
   		String sortcolumns     = getVariableTableValue(vt,"SORT",index,true);
   		String collength     = getVariableTableValue(vt,"LENGTH",index,true);
                
   		String charty2label    = getVariableTableValue(vt,"Y2LABEL",index,true);
   		String subchartylabel     = getVariableTableValue(vt,"SUBCHARTLABEL",index,true);

		String foregroundalpha    = getVariableTableValue(vt,"ALPHA",index,true);

		double sub_height = 0.4;
		String empty_array[] ={};
		String ycolumn[] = {};
                String c_types[] = {};
                String c_subtypes[] = {};
                String c_subtypes2[] = {};
		String y2_columns[] = {};

		String sub_ycolumn[] = {};
                String sub_types[] = {};
                String sub_subtypes[] = {};
                String sub_subtypes2[] = {};

		float fg_alpha = 1.0f;
		java.awt.Color colorlist[] = getColorList(colors);

		if (legendfont == null) legendfont = chartdefaultfont;
		if (chartfootnotefont == null) chartfootnotefont = chartdefaultfont;

		if (foregroundalpha != null)
		{
			try {
				fg_alpha = Double.valueOf(foregroundalpha).floatValue();
			} catch (java.lang.NumberFormatException nfe) 
			{
				fg_alpha=0.6f;
			}
			if (fg_alpha > 1.0f) fg_alpha = 1.0f;
			if (fg_alpha < 0.1f) fg_alpha = 0.1f;
		}

		if (subchartheight != null)
		{
			try {
				sub_height = Double.valueOf(subchartheight).doubleValue();
			} catch (java.lang.NumberFormatException nfe) 
			{
				sub_height=0.4;
			}
			if (sub_height > 0.9) sub_height = 0.9;
			if (sub_height < 0.1) sub_height = 0.1;
		}

		if (sortcolumns != null)
		{
			crosstab.quicksort(TextUtils.toStringArray(TextUtils.getWords(sortcolumns,",")));
		}
				
		if (xmltag == null || xmltag.trim().length() == 0)
		{
			xmltag = "dataset";
		}

		if (charttype == null || charttype.length() == 0)
		{
			charttype="XML";
		}

                c_types = TextUtils.toStringArray(TextUtils.getWords(charttype,"|"));
		if (chartsubtype != null)
                   c_subtypes = TextUtils.toStringArray(TextUtils.getWords(chartsubtype,"|"));
                if (chartsubtype2 != null)
                   c_subtypes2 = TextUtils.toStringArray(TextUtils.getWords(chartsubtype2,"|"));
		if (charty2column != null)
                   y2_columns = TextUtils.toStringArray(TextUtils.getWords(charty2column,"|"));

		if (subchartcolumn != null)
		   sub_ycolumn = TextUtils.toStringArray(TextUtils.getWords(subchartcolumn,"|"));
		if (sub_ycolumn.length > 0)
		{
	                sub_types = TextUtils.toStringArray(TextUtils.getWords(subcharttype,"|"));
			if (chartsubtype != null)
                	   sub_subtypes = TextUtils.toStringArray(TextUtils.getWords(subchartsubtype,"|"));
	                if (chartsubtype2 != null)
        	           sub_subtypes2 = TextUtils.toStringArray(TextUtils.getWords(subchartsubtype2,"|"));
		}

		if (charthref != null)
		{
			String column_hrefs[] = TextUtils.toStringArray(TextUtils.getLines(charthref));
			for (i=0;i<column_hrefs.length;i++)
			{
				String col_href_attr[] = TextUtils.toStringArray(TextUtils.getWords(column_hrefs[i],"|"));
				if (col_href_attr.length > 1)
				{
					crosstab.setColumnMemo(col_href_attr[0],col_href_attr[1]);
				}
			}
		}

		if (tooltipcolumn != null)
		{
			String column_tooltips[] = TextUtils.toStringArray(TextUtils.getLines(tooltipcolumn));
			for (i=0;i<column_tooltips.length;i++)
			{
				String col_tooltip_attr[] = TextUtils.toStringArray(TextUtils.getWords(column_tooltips[i],"|"));
				if (col_tooltip_attr.length > 1)
				{
					crosstab.setColumnTooltip(col_tooltip_attr[0],col_tooltip_attr[1]);
				}
			}
		}

		if (chartformater != null)
		{
			String column_formater[] = TextUtils.toStringArray(TextUtils.getLines(chartformater));
			for (i=0;i<column_formater.length;i++)
			{
				String col_href_attr[] = TextUtils.toStringArray(TextUtils.getWords(column_formater[i],"|"));
				if (col_href_attr.length > 1)
				{
					crosstab.setColumnFormater(col_href_attr[0],col_href_attr[1]);
				}
			}
		}

		if (headerformater != null)
		{
			String column_formater[] = TextUtils.toStringArray(TextUtils.getLines(headerformater));
			for (i=0;i<column_formater.length;i++)
			{
				String col_href_attr[] = TextUtils.toStringArray(TextUtils.getWords(column_formater[i],"|"));
				if (col_href_attr.length > 1)
				{
					crosstab.setHeaderFormater(col_href_attr[0],col_href_attr[1]);
				}
			}
		}

		if (chartexclude != null)
		{
			String column_exclude[] = TextUtils.toStringArray(TextUtils.getWords(chartexclude,"|"));
			for (i=0;i<column_exclude.length;i++)
			{
				crosstab.setColumnVisible(column_exclude[i], false);	
			}
		}

		try {
			if (groupcolumncount != null)
				grpcolcount = Integer.valueOf(groupcolumncount).intValue();
			if (grpcolcount > crosstab.getColumnCount())
				grpcolcount = crosstab.getColumnCount();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			grpcolcount = 2;
		}

		if (charttype.equalsIgnoreCase("XML") || charttype.equalsIgnoreCase("EDIT"))
		{
			String cust_col_length[]={};
			String merge_columns[]={};
			if (collength != null)
			{
			     cust_col_length = TextUtils.toStringArray(TextUtils.getWords(collength,"|"));
			}
			if (mergecolumnlist != null)
			{
			     merge_columns = TextUtils.toStringArray(TextUtils.getWords(mergecolumnlist,"|"));
			}
			if (charttype.equalsIgnoreCase("EDIT"))
				crosstab.writeXMLBody(out,xmltag,xmlattr,grpcolcount, merge_columns,cust_col_length,vt, true);
			else
				crosstab.writeXMLBody(out,xmltag,xmlattr,grpcolcount, merge_columns,cust_col_length,vt, false);
			return;
		}

		if (chartxcolumn == null || crosstab.findColumn(chartxcolumn) == 0)
			chartxcolumn = crosstab.getColumnName(1);

		if (chartycolumn != null)
			ycolumn = TextUtils.toStringArray(TextUtils.getWords(chartycolumn,"|"));
		else
		{
			if (crosstab.getColumnCount()>1)
			{
				ycolumn = new String[1];
				ycolumn [0] = "";
				for(i = 2; i <= crosstab.getColumnCount() ; i ++)
				{
					if (i == 2)
						ycolumn [0] = crosstab.getColumnName(i);
					else
						ycolumn [0] = ycolumn[0]+","+crosstab.getColumnName(i);
				}
			}
		}

		try {
			if (chartunitsize != null)
				unitsize = Integer.valueOf(chartunitsize).intValue();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			unitsize = 1;
		}

		if (unitsize < 1) unitsize = 1;
			
		try {
			if (chartwidth != null)
				width = Integer.valueOf(chartwidth).intValue();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			width = 500;
		}
		try {
			if (chartheight != null)
				height = Integer.valueOf(chartheight).intValue();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			height = 400;
		}

		width = width * unitsize;
		height = height * unitsize;

		width = (width < 50?50:width);
		width = (width > 2048?2048:width);
		height = (height < 20?20:height);
		height = (height > 800?800:height);

		JFreeChart chart = null;

		if (charttype.equalsIgnoreCase("FLOW"))
                {
			BufferedImage gifimg = WebChartFlow.getFlowChart(getColor(backcolor), width, height, 
                                               crosstab, chartxlabel, chartylabel, chartsubtype, chartsubtype2);
			PngEncoder gifencoder = new PngEncoder(gifimg, false, 0, 5);
			fileNameChart = ImageCache.putContent(gifencoder.pngEncode(), vt.getInt("WEBCHART.KEEP_CACHE_TIME",300) + 10);

			out.write("\t<image>\n");
			out.write("\t\t<file>");
			if (getVariableTableValue(vt,"CACHE",index,true) == null)
				out.write("showimage"+fileextention+"?id="+fileNameChart+"&amp;del=yes");
			else
				out.write("showimage"+fileextention+"?id="+fileNameChart);
			out.write("</file>\n");
			out.write("\t</image>\n");			
			out.flush();
			return ;
                }
		else if (charttype.equalsIgnoreCase("PIE"))
		{
			DefaultPieDataset dataset = getPieDataset(crosstab, chartxcolumn, ycolumn[0]);
			PiePlot3D pieplot = new PiePlot3D(dataset);
			pieplot.setForegroundAlpha(fg_alpha);
			if (plotbackcolor != null)
			{
				pieplot.setBackgroundPaint(getColor(plotbackcolor));
			}
			if (pielabelstyle != null)
			{
				if (pielabelstyle.equalsIgnoreCase("VALUE"))
					pieplot.setLabelGenerator(PIE_LABEL_VALUE);
				else
					pieplot.setLabelGenerator(PIE_LABEL_PERCENT);
			}
			else	
				pieplot.setLabelGenerator(PIE_LABEL_PERCENT);
			if (plotbackcolor != null)
			{
				// pieplot.setBackgroundPaint(getColor(plotbackcolor));
				java.awt.Color plot_bg_color = getColor(plotbackcolor);
				GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
				pieplot.setBackgroundPaint(plot_bg_paint);
			}			
			if (plotedgecolor != null)
			{
				pieplot.setOutlinePaint(getColor(plotedgecolor));
			}
			DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(crosstab,vt, chartxcolumn, ycolumn[0]);
			pieplot.setToolTipGenerator(url_tooltip);
			pieplot.setURLGenerator(url_tooltip);	
			chart = new JFreeChart(pieplot);
		}
		else if (charttype.equalsIgnoreCase("SPIDER"))
		{
        		DefaultCategoryDataset dataset = getCategoryDataset(crosstab, chartxcolumn, ycolumn[0]);
			SpiderWebPlot spiderplot = new SpiderWebPlot(dataset);
			spiderplot.setForegroundAlpha(fg_alpha);
			if (plotbackcolor != null)
			{
				spiderplot.setBackgroundPaint(getColor(plotbackcolor));
			}
			if (plotbackcolor != null)
			{
				// pieplot.setBackgroundPaint(getColor(plotbackcolor));
				java.awt.Color plot_bg_color = getColor(plotbackcolor);
				GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
				spiderplot.setBackgroundPaint(plot_bg_paint);
			}			
			if (plotedgecolor != null)
			{
				spiderplot.setOutlinePaint(getColor(plotedgecolor));
			}
			DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(crosstab,vt, chartxcolumn, ycolumn[0]);
			spiderplot.setToolTipGenerator(url_tooltip);
			spiderplot.setURLGenerator(url_tooltip);	
			chart = new JFreeChart(spiderplot);
		}
 		else 
 		{
 			if (isNumberType(crosstab.getColumnType(chartxcolumn)))
 			{
				crosstab.quicksort(chartxcolumn);
				if (sub_ycolumn.length == 0)
				{
	 				XYPlot plot = getChartXYPlot(crosstab, chartxcolumn, ycolumn,y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							true, vt, colorlist,0);
 					setDomainAxisProperty(plot.getDomainAxis(0), chartxmaxval, chartxlabel, chartdefaultcolor,chartdefaultfont);
 					setValueAxisProperty(plot.getRangeAxis(0), chartymaxval, chartylabel, chartdefaultcolor, chartyformat,chartdefaultfont);
	 				if (plot.getRangeAxis(1) != null)
 					{
 						setValueAxisProperty(plot.getRangeAxis(1), charty2maxval, charty2label, chartdefaultcolor,charty2format,chartdefaultfont);
 					}
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					plot.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						// plot.setBackgroundPaint(getColor(plotbackcolor));
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot.setBackgroundPaint(plot_bg_color);
						plot.setRangeTickBandPaint(nextColor(plot_bg_color));
					}
					if (plotedgecolor != null)
					{
						plot.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot, chartmargin);
					chart = new JFreeChart(plot);
				}
				else
				{
	 				XYPlot plot1 = getChartXYPlot(crosstab, chartxcolumn, ycolumn,y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							false, vt, colorlist,0);
 					setValueAxisProperty(plot1.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
	 				if (plot1.getRangeAxis(1) != null)
 					{
 						setValueAxisProperty(plot1.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format,chartdefaultfont);
 					}
					plot1.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						// plot1.setBackgroundPaint(getColor(plotbackcolor));
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot1.setBackgroundPaint(plot_bg_color);
						plot1.setRangeTickBandPaint(nextColor(plot_bg_color));
					}
					if (plotedgecolor != null)
					{
						plot1.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot1, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot1, chartmargin);
					
	 				XYPlot plot2 = getChartXYPlot(crosstab, chartxcolumn, sub_ycolumn, empty_array,
 							getPropertyArray(sub_ycolumn.length , 1, sub_types),
 							getPropertyArray(sub_ycolumn.length , 2, sub_subtypes),
 							getPropertyArray(sub_ycolumn.length , 3, sub_subtypes2),
 							false,vt,colorlist,plot1.getDatasetCount() + 1);
 					setValueAxisProperty(plot2.getRangeAxis(0), subchartymaxval, subchartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
					plot2.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						// plot2.setBackgroundPaint(getColor(plotbackcolor));
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot2.setBackgroundPaint(plot_bg_color);
						plot2.setRangeTickBandPaint(nextColor(plot_bg_color));
					}
					if (plotedgecolor != null)
					{
						plot2.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot2, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot2, chartmargin);
					
					NumberAxis domainaxis = new NumberAxis("");
					domainaxis.setAutoRangeIncludesZero(false);
					domainaxis.setAutoRangeStickyZero(false);
					domainaxis.setUpperMargin(0.0);
					domainaxis.setLowerMargin(0.0);
 					setDomainAxisProperty(domainaxis, chartxmaxval, chartxlabel,chartdefaultcolor,chartdefaultfont);
					
					CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domainaxis);
					plot.add(plot1, (int)(10 - 10 * sub_height));
					plot.add(plot2, (int)(10 * sub_height));
					setPlotRangeAxisSpace(plot, chartmargin);
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					chart = new JFreeChart(plot);
				}
 			}
 			else if (isTimeType(crosstab.getColumnType(chartxcolumn)))
 			{
				crosstab.quicksort(chartxcolumn);
				if (sub_ycolumn.length == 0)
				{
	 				XYPlot plot = getChartXYPlot(crosstab, chartxcolumn, ycolumn, y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							true,vt,colorlist,0);
 					setDomainAxisProperty(plot.getDomainAxis(0),chartxmaxval, chartxlabel,chartdefaultcolor,chartdefaultfont);
 					setValueAxisProperty(plot.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
 					if (plot.getRangeAxis(1) != null)
 					{
 						setValueAxisProperty(plot.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format,chartdefaultfont);
 					}
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					plot.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						// plot.setBackgroundPaint(getColor(plotbackcolor));
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot.setBackgroundPaint(plot_bg_color);
						plot.setRangeTickBandPaint(nextColor(plot_bg_color));
					}
					if (plotedgecolor != null)
					{
						plot.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot, chartmargin);
	 				chart = new JFreeChart(plot); 
				}
				else
				{
	 				XYPlot plot1 = getChartXYPlot(crosstab, chartxcolumn, ycolumn,y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							false,vt,colorlist,0);
 					setValueAxisProperty(plot1.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
	 				if (plot1.getRangeAxis(1) != null)
 					{
 						setValueAxisProperty(plot1.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format,chartdefaultfont);
 					}
					plot1.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						// plot1.setBackgroundPaint(getColor(plotbackcolor));
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot1.setBackgroundPaint(plot_bg_color);
						plot1.setRangeTickBandPaint(nextColor(plot_bg_color));
					}
					if (plotedgecolor != null)
					{
						plot1.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot1, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot1, chartmargin);
					
	 				XYPlot plot2 = getChartXYPlot(crosstab, chartxcolumn, sub_ycolumn, empty_array,
 							getPropertyArray(sub_ycolumn.length , 1, sub_types),
 							getPropertyArray(sub_ycolumn.length , 2, sub_subtypes),
 							getPropertyArray(sub_ycolumn.length , 3, sub_subtypes2),
 							false,vt,colorlist,plot1.getDatasetCount() + 1);
 					setValueAxisProperty(plot2.getRangeAxis(0), subchartymaxval, subchartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
					plot2.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						// plot2.setBackgroundPaint(getColor(plotbackcolor));
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot2.setBackgroundPaint(plot_bg_color);
						plot2.setRangeTickBandPaint(nextColor(plot_bg_color));
					}
					if (plotedgecolor != null)
					{
						plot2.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot2, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot2, chartmargin);
					
					DateAxis domainaxis = new DateAxis("");
					domainaxis.setUpperMargin(0.0);
					domainaxis.setLowerMargin(0.0);
					setDomainAxisProperty(domainaxis, chartxmaxval, chartxlabel,chartdefaultcolor,chartdefaultfont);
					
					CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domainaxis);
					plot.add(plot1, (int)(10 - 10 * sub_height));
					plot.add(plot2, (int)(10 * sub_height));
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					setPlotRangeAxisSpace(plot, chartmargin);
					chart = new JFreeChart(plot);
				}
 			}
 			else
 			{
				if (sub_ycolumn.length == 0)
				{
	 				CategoryPlot plot = getChartCategoryPlot(crosstab, chartxcolumn, ycolumn,y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							true,vt,colorlist,0);
 					setDomainAxisProperty(plot.getDomainAxis(0),chartxmaxval, chartxlabel,chartdefaultcolor,chartdefaultfont);
 					setValueAxisProperty(plot.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
 					if (plot.getRangeAxis(1) != null)
	 				{
 						setValueAxisProperty(plot.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format,chartdefaultfont);
 					}
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					plot.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						// plot.setBackgroundPaint(getColor(plotbackcolor));
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot.setBackgroundPaint(plot_bg_paint);
					}
					if (plotedgecolor != null)
					{
						plot.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot, chartmargin);
					chart = new JFreeChart(plot);
				}
				else
				{
	 				CategoryPlot plot1 = getChartCategoryPlot(crosstab, chartxcolumn, ycolumn,y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							false,vt,colorlist,0);
 					setValueAxisProperty(plot1.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
	 				if (plot1.getRangeAxis(1) != null)
 					{
 						setValueAxisProperty(plot1.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format,chartdefaultfont);
 					}
					plot1.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						// plot1.setBackgroundPaint(getColor(plotbackcolor));
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot1.setBackgroundPaint(plot_bg_paint);
					}
					if (plotedgecolor != null)
					{
						plot1.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot1, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot1, chartmargin);
					
	 				CategoryPlot plot2 = getChartCategoryPlot(crosstab, chartxcolumn, sub_ycolumn, empty_array,
 							getPropertyArray(sub_ycolumn.length , 1, sub_types),
 							getPropertyArray(sub_ycolumn.length , 2, sub_subtypes),
 							getPropertyArray(sub_ycolumn.length , 3, sub_subtypes2),
 							false,vt,colorlist, plot1.getDataset().getColumnCount() + 1);
 					setValueAxisProperty(plot2.getRangeAxis(0), subchartymaxval, subchartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
					plot2.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						// plot2.setBackgroundPaint(getColor(plotbackcolor));
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot2.setBackgroundPaint(plot_bg_paint);
					}
					if (plotedgecolor != null)
					{
						plot2.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot2, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot2, chartmargin);
					
					CategoryAxis domainaxis = new CategoryAxis("");			
 					setDomainAxisProperty(domainaxis, chartxmaxval, chartxlabel,chartdefaultcolor,chartdefaultfont);
					
					CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(domainaxis);
					plot.add(plot1, (int)(10 - 10 * sub_height));
					plot.add(plot2, (int)(10 * sub_height));
					setPlotRangeAxisSpace(plot, chartmargin);
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					chart = new JFreeChart(plot);
				}
 			}
 		}


		if (chart != null)
		{
			chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			if (backcolor != null)
			{
				java.awt.Color chart_bg_color = getColor(backcolor);
				GradientPaint chart_bg_paint = new GradientPaint(0,0,chart_bg_color,width,height,prevColor(chart_bg_color));
				chart.setBackgroundPaint(chart_bg_paint);
			}
			if (edgecolor != null)
			{
				chart.setBorderVisible(true);
				chart.setBorderPaint(getColor(edgecolor));
			}
			if (chart.getLegend() != null)
			{
				chart.getLegend().setBackgroundPaint(chart.getBackgroundPaint());
				chart.getLegend().setBorder(org.jfree.chart.block.BlockBorder.NONE);
				if (legendcolor != null)
				{
					chart.getLegend().setItemPaint(getColor(legendcolor));
				}
				if (legendfont != null)
				{
					chart.getLegend().setItemFont(getFont(legendfont));
				}
				else
				{
					chart.getLegend().setItemFont(SIMHEI12);
				}
			}
			if (legendposition != null)
			{
				if (legendposition.equalsIgnoreCase("OFF"))
				{
					chart.removeLegend();
					org.jfree.chart.title.TextTitle subtitle = new org.jfree.chart.title.TextTitle("  ");
					subtitle.setPosition(RectangleEdge.BOTTOM);
					subtitle.setHorizontalAlignment(HorizontalAlignment.CENTER);
					chart.addSubtitle(subtitle);					
				}
				else
				{
					chart.getLegend().setPosition(getLegendPosition(legendposition));
					chart.getLegend().setHorizontalAlignment(getLegendHAlign(legendposition));
					if (!chart.getLegend().getPosition().equals(RectangleEdge.BOTTOM))
					{
						org.jfree.chart.title.TextTitle subtitle = new org.jfree.chart.title.TextTitle("  ");
						subtitle.setPosition(RectangleEdge.BOTTOM);
						subtitle.setHorizontalAlignment(HorizontalAlignment.CENTER);
						chart.addSubtitle(subtitle);	
					}
				}
			}
			if (charttitle != null)
			{
				chart.setTitle(vt.parseString(charttitle));
				if (charttitlefont != null)
				{
					chart.getTitle().setFont(getFont(charttitlefont));
				}
				else
				{
					chart.getTitle().setFont(SIMHEI18);
				}
				if (charttitlecolor != null)
				{
					chart.getTitle().setPaint(getColor(charttitlecolor));
				}
				else
				{
					if (chartdefaultcolor != null)
						chart.getTitle().setPaint(getColor(chartdefaultcolor));
				}
			}
			if (chartsubtitle != null)
			{
				org.jfree.chart.title.TextTitle subtitle = new org.jfree.chart.title.TextTitle(vt.parseString(chartsubtitle));
				if (chartsubtitlecolor != null)
				{
					subtitle.setPaint(getColor(chartsubtitlecolor));
				}
				else
				{
					if (chartdefaultcolor != null)
						subtitle.setPaint(getColor(chartdefaultcolor));
				}
				if (chartsubtitlefont != null)
				{
					subtitle.setFont(getFont(chartsubtitlefont));
				}
				else
				{
					subtitle.setFont(SIMHEI12);
				}
				subtitle.setPosition(RectangleEdge.TOP);
				subtitle.setHorizontalAlignment(HorizontalAlignment.CENTER);
				chart.addSubtitle(subtitle);
			}
			if (chartdefaultcolor != null)
			{
				if (chart.getLegend() != null)
					chart.getLegend().setItemPaint(getColor(chartdefaultcolor));
			}
			final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
		
			/*
			fileNameChart = savePNGFile(chart, width, height, info, chartfootnote, chartfootnotefont, chartfootnotecolor);
			*/

			BufferedImage gifimg = null;
			if ("OFF".equalsIgnoreCase(chartimagemap) || crosstab.getRowCount() > 500)
			    gifimg = chart.createBufferedImage(width,height,BufferedImage.TYPE_INT_BGR,null);
			else
			    gifimg = chart.createBufferedImage(width,height,BufferedImage.TYPE_INT_BGR,info);
			Graphics2D g2 = gifimg.createGraphics();

			if (chartfootnote != null)
			{
				if (chartfootnotecolor != null)
				{
					g2.setColor(getColor(chartfootnotecolor));
				}
				else
				{
					if (chartdefaultcolor != null)
						g2.setColor(getColor(chartdefaultcolor));
					else
						g2.setColor(java.awt.Color.BLACK);
				}
				if (chartfootnotefont != null)
				{
					g2.setFont(getFont(chartfootnotefont));
				}
				else if (chartdefaultfont != null)
				{
					g2.setFont(getFont(chartdefaultfont));
				}
				else
				{
					g2.setFont(SIMHEI12);
				}
				g2.drawString(vt.parseString(chartfootnote),5,height - 8);
			}
                        /*
			g2.setFont(SIMSUN12);
			g2.setColor(java.awt.Color.GRAY);
			g2.drawString("AnySQL.net",width - 60,height - 4);
                        */
			PngEncoder gifencoder = new PngEncoder(gifimg, false, 0, 5);
			
			fileNameChart = ImageCache.putContent(gifencoder.pngEncode(), vt.getInt("WEBCHART.KEEP_CACHE_TIME",300) + 10);

			out.write("\t<image>\n");
			out.write("\t\t<file>");
			if (getVariableTableValue(vt,"CACHE",index,true) == null)
				out.write("showimage"+fileextention+"?id="+fileNameChart+"&amp;del=yes");
			else
				out.write("showimage"+fileextention+"?id="+fileNameChart);
			out.write("</file>\n");
			if (!"OFF".equalsIgnoreCase(chartimagemap))
			{
			    if (crosstab.getRowCount() <= MAX_IMAGE_CSS_ITEMS)
	            		getImageMapCSS(out, fileNameChart, info);
			}
			out.write("\t</image>\n");
		}			

		out.flush();    		
    	}    		

	public static final void getImageMapCSS(java.io.Writer out, String name, ChartRenderingInfo info) throws java.io.IOException
	{
		EntityCollection entities = info.getEntityCollection();
		if (entities != null)
		{
			int count = entities.getEntityCount();
			out.write("\t\t<image_map name=\""+name+"\">\n");
			for (int i = count - 1; i >= 0; i--)
			{
				ChartEntity entity = entities.getEntity(i);
				if (entity.getToolTipText() != null)
				{
					out.write("\t\t\t<mapitem>\n");
					out.write("\t\t\t\t<title><![CDATA["+entity.getToolTipText()+"]]></title>\n");
					out.write("\t\t\t\t<shape><![CDATA["+entity.getShapeType()+"]]></shape>\n");
					out.write("\t\t\t\t<coords><![CDATA["+entity.getShapeCoords()+"]]></coords>\n");
					out.write("\t\t\t\t<href><![CDATA["+entity.getURLText()+"]]></href>\n");
					out.write("\t\t\t</mapitem>\n");
				}
			}
			out.write("\t\t</image_map>\n");
		}
	}

	private static void generateChart(DBRowCache crosstab,java.io.OutputStream out,VariableTable vt,String index, String fileextention)
		throws java.io.IOException 
    	{
    		int i;
    		String mapcss="";
 		int width=500,height=400,grpcolcount=2,unitsize=1;

 		String fileNameChart="";

    		String chartdefaultfont = getVariableTableValue(vt,"defaultfont");
    		String chartdefaultcolor = getVariableTableValue(vt,"defaultcolor");
    		
		String colors = getVariableTableValue(vt,"colors", index, true);
		
		String xmltag = getVariableTableValue(vt,"xmltag", index, true);
		String xmlattr = getVariableTableValue(vt,"xmlattr", index, true);

   		String legendposition    = getVariableTableValue(vt,"legend",index,true);
   		String legendfont        = getVariableTableValue(vt,"legendfont",index,true);
   		String legendcolor       = getVariableTableValue(vt,"legendcolor",index,true);

   		String plotbackcolor    = getVariableTableValue(vt,"plotbackcolor",index,true);
   		String plotedgecolor    = getVariableTableValue(vt,"plotedgecolor",index,true);   		
   		String chartorient    = getVariableTableValue(vt,"orient",index,true);		
    		String backcolor = getVariableTableValue(vt,"backcolor", index, true);
    		String edgecolor = getVariableTableValue(vt,"edgecolor",index, true);
   		String gridline     = getVariableTableValue(vt,"GRID",index,true);
   		String gridstyle     = getVariableTableValue(vt,"GRIDLINE",index,true);
   		String gridcolor     = getVariableTableValue(vt,"GRIDCOLOR",index,true);
    		
    		String charttype = getVariableTableValue(vt,"type", index, true);
    		String chartsubtype = getVariableTableValue(vt,"subtype", index, true);
    		String chartsubtype2 = getVariableTableValue(vt,"subtype2",index, true);
    		
    		String chartunitsize = getVariableTableValue(vt,"unitsize",index,true);    		
    		String chartwidth = getVariableTableValue(vt,"width",index,true);
    		String chartheight = getVariableTableValue(vt,"height",index,true);
    		String chartmargin = getVariableTableValue(vt,"ywidth",index,true);

    		String charttitle = crosstab.parseString(getVariableTableValue(vt,"title",index,false),vt,0,0);
    		String charttitlefont = getVariableTableValue(vt,"titlefont",index,true);
    		String charttitlecolor = getVariableTableValue(vt,"titlecolor",index,true);

    		String chartsubtitle = crosstab.parseString(getVariableTableValue(vt,"subtitle",index,false),vt,0,0);
    		String chartsubtitlefont = getVariableTableValue(vt,"subtitlefont",index,true);
    		String chartsubtitlecolor = getVariableTableValue(vt,"subtitlecolor",index,true);

    		String chartfootnote = crosstab.parseString(getVariableTableValue(vt,"footnote",index,false),vt,0,0);
    		String chartfootnotefont = getVariableTableValue(vt,"footnotefont",index,true);
    		String chartfootnotecolor = getVariableTableValue(vt,"footnotecolor",index,true);


   		String charthref    = getVariableTableValue(vt,"href",index,true);
   		String charthreftarget    = getVariableTableValue(vt,"hreftarget",index,true);
   		String chartformater    = getVariableTableValue(vt,"formater",index,true);
   		String chartexclude    = getVariableTableValue(vt,"exclude",index,true);

    		String stock_open = getVariableTableValue(vt,"stock_open",index,false);
    		String stock_high = getVariableTableValue(vt,"stock_high",index,false);
    		String stock_low = getVariableTableValue(vt,"stock_low",index,false);
    		String stock_close = getVariableTableValue(vt,"stock_close",index,false);
    		String stock_volume = getVariableTableValue(vt,"stock_volume",index,false);


   		String pielabelstyle    = getVariableTableValue(vt,"PIELABEL",index,true);
   		String pielabeldigit    = getVariableTableValue(vt,"PIEDIGIT",index,true);
   		
   		String tooltipcolumn    = getVariableTableValue(vt,"TOOLTIP",index,true);
   		String chartxcolumn    = vt.parseString(getVariableTableValue(vt,"XCOL",index,true));
   		String chartxlabel    = getVariableTableValue(vt,"XLABEL",index,true);
   		String chartxmaxval    = getVariableTableValue(vt,"XMAX",index,true);
   		String chartycolumn    = vt.parseString(getVariableTableValue(vt,"YCOL",index,true));
   		String chartylabel    = getVariableTableValue(vt,"YLABEL",index,true);
   		String chartymaxval    = getVariableTableValue(vt,"YMAX",index,true);
   		String charty2column    = vt.parseString(getVariableTableValue(vt,"Y2COL",index,true));
   		String charty2maxval    = getVariableTableValue(vt,"Y2MAX",index,true);
		String chartyformat    = getVariableTableValue(vt,"YFORMAT",index,true);
		String charty2format    = getVariableTableValue(vt,"Y2FORMAT",index,true);


   		String subchartcolumn    = vt.parseString(getVariableTableValue(vt,"SUBCHARTCOL",index,true));
   		String subchartheight    = getVariableTableValue(vt,"SUBCHARTHEIGHT",index,true);
   		String subcharttype      = getVariableTableValue(vt,"SUBCHARTTYPE",index,true);
   		String subchartsubtype      = getVariableTableValue(vt,"SUBCHARTSUBTYPE",index,true);
   		String subchartsubtype2     = getVariableTableValue(vt,"SUBCHARTSUBTYPE2",index,true);
   		String subchartymaxval     = getVariableTableValue(vt,"SUBCHARTYMAX",index,true);

   		String groupcolumncount     = getVariableTableValue(vt,"GROUP",index,true);
   		String chartimagemap     = getVariableTableValue(vt,"IMAGEMAP",index,true);
   		String sortcolumns     = getVariableTableValue(vt,"SORT",index,true);
   		String collength     = getVariableTableValue(vt,"LENGTH",index,true);

   		String charty2label    = getVariableTableValue(vt,"Y2LABEL",index,true);
   		String subchartylabel     = getVariableTableValue(vt,"SUBCHARTLABEL",index,true);

		String foregroundalpha    = getVariableTableValue(vt,"ALPHA",index,true);

		double sub_height = 0.4;
		String empty_array[] ={};
		String ycolumn[] = {};
                String c_types[] = {};
                String c_subtypes[] = {};
                String c_subtypes2[] = {};
		String y2_columns[] = {};

		String sub_ycolumn[] = {};
                String sub_types[] = {};
                String sub_subtypes[] = {};
                String sub_subtypes2[] = {};

		float fg_alpha = 1.0f;
		java.awt.Color colorlist[] = getColorList(colors);

		if (legendfont == null) legendfont = chartdefaultfont;
		if (chartfootnotefont == null) chartfootnotefont = chartdefaultfont;

		if (foregroundalpha != null)
		{
			try {
				fg_alpha = Double.valueOf(foregroundalpha).floatValue();
			} catch (java.lang.NumberFormatException nfe) 
			{
				fg_alpha=0.6f;
			}
			if (fg_alpha > 1.0f) fg_alpha = 1.0f;
			if (fg_alpha < 0.1f) fg_alpha = 0.1f;
		}

		if (subchartheight != null)
		{
			try {
				sub_height = Double.valueOf(subchartheight).doubleValue();
			} catch (java.lang.NumberFormatException nfe) 
			{
				sub_height=0.4;
			}
			if (sub_height > 0.9) sub_height = 0.9;
			if (sub_height < 0.1) sub_height = 0.1;
		}

		if (sortcolumns != null)
		{
			crosstab.quicksort(TextUtils.toStringArray(TextUtils.getWords(sortcolumns,",")));
		}
				
		if (charttype == null || charttype.length() == 0)
		{
			charttype="BAR|BAR|BAR|BAR|BAR|BAR|BAR";
		}

                c_types = TextUtils.toStringArray(TextUtils.getWords(charttype,"|"));
		if (chartsubtype != null)
                   c_subtypes = TextUtils.toStringArray(TextUtils.getWords(chartsubtype,"|"));
                if (chartsubtype2 != null)
                   c_subtypes2 = TextUtils.toStringArray(TextUtils.getWords(chartsubtype2,"|"));

		if (subchartcolumn != null)
		   sub_ycolumn = TextUtils.toStringArray(TextUtils.getWords(subchartcolumn,"|"));
		if (sub_ycolumn.length > 0)
		{
	                sub_types = TextUtils.toStringArray(TextUtils.getWords(subcharttype,"|"));
			if (chartsubtype != null)
                	   sub_subtypes = TextUtils.toStringArray(TextUtils.getWords(subchartsubtype,"|"));
	                if (chartsubtype2 != null)
        	           sub_subtypes2 = TextUtils.toStringArray(TextUtils.getWords(subchartsubtype2,"|"));
		}
                   
		if (charty2column != null)
                   y2_columns = TextUtils.toStringArray(TextUtils.getWords(charty2column,"|"));

		if (chartxcolumn == null || crosstab.findColumn(chartxcolumn) == 0)
			chartxcolumn = crosstab.getColumnName(1);

		if (chartycolumn != null)
			ycolumn = TextUtils.toStringArray(TextUtils.getWords(chartycolumn,"|"));
		else
		{
			if (crosstab.getColumnCount()>1)
			{
				ycolumn = new String[1];
				for(i = 2; i <= crosstab.getColumnCount() ; i ++)
				{
					if (i == 2)
						ycolumn [0] = crosstab.getColumnName(i);
					else
						ycolumn [0] = ycolumn[0]+","+crosstab.getColumnName(i);
				}
			}
		}

		try {
			if (chartunitsize != null)
				unitsize = Integer.valueOf(chartunitsize).intValue();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			unitsize = 1;
		}

		if (unitsize < 1) unitsize = 1;
			
		try {
			if (chartwidth != null)
				width = Integer.valueOf(chartwidth).intValue();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			width = 500;
		}
		try {
			if (chartheight != null)
				height = Integer.valueOf(chartheight).intValue();
		}
	 	catch (java.lang.NumberFormatException nfe)
		{
			height = 400;
		}

		width = width * unitsize;
		height = height * unitsize;

		width = (width < 50?50:width);
		width = (width > 2048?2048:width);
		height = (height < 20?20:height);
		height = (height > 800?800:height);
	
		JFreeChart chart = null;

		if (charttype.equalsIgnoreCase("FLOW"))
                {
			BufferedImage gifimg = WebChartFlow.getFlowChart(getColor(backcolor), width, height, 
                                               crosstab, chartxlabel, chartylabel, chartsubtype, chartsubtype2);
			PngEncoder gifencoder = new PngEncoder(gifimg, false, 0, 5);
			out.write(gifencoder.pngEncode());
			out.flush();
			return ;
                }
		else if (charttype.equalsIgnoreCase("PIE"))
		{
			DefaultPieDataset dataset = getPieDataset(crosstab, chartxcolumn, ycolumn[0]);
			PiePlot3D pieplot = new PiePlot3D(dataset);
			pieplot.setForegroundAlpha(fg_alpha);
			if (plotbackcolor != null)
			{
				pieplot.setBackgroundPaint(getColor(plotbackcolor));
			}
			if (pielabelstyle != null)
			{
				if (pielabelstyle.equalsIgnoreCase("VALUE"))
					pieplot.setLabelGenerator(PIE_LABEL_VALUE);
				else
					pieplot.setLabelGenerator(PIE_LABEL_PERCENT);
			}
			else	
				pieplot.setLabelGenerator(PIE_LABEL_PERCENT);
			if (plotbackcolor != null)
			{
				java.awt.Color plot_bg_color = getColor(plotbackcolor);
				GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
				pieplot.setBackgroundPaint(plot_bg_paint);
			}			
			if (plotedgecolor != null)
			{
				pieplot.setOutlinePaint(getColor(plotedgecolor));
			}
			chart = new JFreeChart(pieplot);
		}
		else if (charttype.equalsIgnoreCase("SPIDER"))
		{
        		DefaultCategoryDataset dataset = getCategoryDataset(crosstab, chartxcolumn, ycolumn[0]);
			SpiderWebPlot spiderplot = new SpiderWebPlot(dataset);
			spiderplot.setForegroundAlpha(fg_alpha);
			if (plotbackcolor != null)
			{
				spiderplot.setBackgroundPaint(getColor(plotbackcolor));
			}
			if (plotbackcolor != null)
			{
				// pieplot.setBackgroundPaint(getColor(plotbackcolor));
				java.awt.Color plot_bg_color = getColor(plotbackcolor);
				GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
				spiderplot.setBackgroundPaint(plot_bg_paint);
			}			
			if (plotedgecolor != null)
			{
				spiderplot.setOutlinePaint(getColor(plotedgecolor));
			}
			DBRowCacheURLGenerator url_tooltip = new DBRowCacheURLGenerator(crosstab,vt, chartxcolumn, ycolumn[0]);
			spiderplot.setToolTipGenerator(url_tooltip);
			spiderplot.setURLGenerator(url_tooltip);	
			chart = new JFreeChart(spiderplot);
		}
 		else 
 		{
 			if (isNumberType(crosstab.getColumnType(chartxcolumn)))
 			{
				crosstab.quicksort(chartxcolumn);
				if (sub_ycolumn.length == 0)
				{
	 				XYPlot plot = getChartXYPlot(crosstab, chartxcolumn, ycolumn,y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							true, null,colorlist,0);
 					setDomainAxisProperty(plot.getDomainAxis(0),chartxmaxval, chartxlabel,chartdefaultcolor,chartdefaultfont);
 					setValueAxisProperty(plot.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
	 				if (plot.getRangeAxis(1) != null)
 					{
 						setValueAxisProperty(plot.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format,chartdefaultfont);
 					}
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					plot.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot.setBackgroundPaint(plot_bg_color);
						plot.setRangeTickBandPaint(nextColor(plot_bg_color));
					}		
					if (plotedgecolor != null)
					{
						plot.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot, chartmargin);
					chart = new JFreeChart(plot);
				}
				else
				{
	 				XYPlot plot1 = getChartXYPlot(crosstab, chartxcolumn, ycolumn,y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							false,null,colorlist,0);
 					setValueAxisProperty(plot1.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
	 				if (plot1.getRangeAxis(1) != null)
 					{
 						setValueAxisProperty(plot1.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format,chartdefaultfont);
 					}
					plot1.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot1.setBackgroundPaint(plot_bg_color);
						plot1.setRangeTickBandPaint(nextColor(plot_bg_color));
					}		
					if (plotedgecolor != null)
					{
						plot1.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot1, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot1, chartmargin);

	 				XYPlot plot2 = getChartXYPlot(crosstab, chartxcolumn, sub_ycolumn, empty_array,
 							getPropertyArray(sub_ycolumn.length , 1, sub_types),
 							getPropertyArray(sub_ycolumn.length , 2, sub_subtypes),
 							getPropertyArray(sub_ycolumn.length , 3, sub_subtypes2),
 							false,null,colorlist,plot1.getDatasetCount() + 1);
 					setValueAxisProperty(plot2.getRangeAxis(0), subchartymaxval, subchartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
					plot2.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot2.setBackgroundPaint(plot_bg_color);
						plot2.setRangeTickBandPaint(nextColor(plot_bg_color));
					}
					if (plotedgecolor != null)
					{
						plot2.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot2, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot2, chartmargin);

					NumberAxis domainaxis = new NumberAxis("");
					domainaxis.setAutoRangeIncludesZero(false);
					domainaxis.setAutoRangeStickyZero(false);
					domainaxis.setUpperMargin(0.0);
					domainaxis.setLowerMargin(0.0);
 					setDomainAxisProperty(domainaxis,chartxmaxval, chartxlabel,chartdefaultcolor,chartdefaultfont);
					
					CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domainaxis);
					plot.add(plot1, (int)(10 - 10 * sub_height));
					plot.add(plot2, (int)(10 * sub_height));
					setPlotRangeAxisSpace(plot, chartmargin);

					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					chart = new JFreeChart(plot);
				}
 			}
 			else if (isTimeType(crosstab.getColumnType(chartxcolumn)))
 			{
				crosstab.quicksort(chartxcolumn);
				if (sub_ycolumn.length == 0)
				{
	 				XYPlot plot = getChartXYPlot(crosstab, chartxcolumn, ycolumn, y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							true,null,colorlist,0);
 					setDomainAxisProperty(plot.getDomainAxis(0), chartxmaxval, chartxlabel,chartdefaultcolor,chartdefaultfont);
 					setValueAxisProperty(plot.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
 					if (plot.getRangeAxis(1) != null)
 					{
 						setValueAxisProperty(plot.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format,chartdefaultfont);
 					}
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					plot.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot.setBackgroundPaint(plot_bg_color);
						plot.setRangeTickBandPaint(nextColor(plot_bg_color));
					}
					if (plotedgecolor != null)
					{
						plot.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot, chartmargin);
	 				chart = new JFreeChart(plot); 
				}
				else
				{
	 				XYPlot plot1 = getChartXYPlot(crosstab, chartxcolumn, ycolumn,y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							false,null,colorlist,0);
 					setValueAxisProperty(plot1.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
	 				if (plot1.getRangeAxis(1) != null)
 					{
 						setValueAxisProperty(plot1.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format,chartdefaultfont);
 					}
					plot1.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot1.setBackgroundPaint(plot_bg_color);
						plot1.setRangeTickBandPaint(nextColor(plot_bg_color));
					}
					if (plotedgecolor != null)
					{
						plot1.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot1, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot1, chartmargin);

	 				XYPlot plot2 = getChartXYPlot(crosstab, chartxcolumn, sub_ycolumn, empty_array,
 							getPropertyArray(sub_ycolumn.length , 1, sub_types),
 							getPropertyArray(sub_ycolumn.length , 2, sub_subtypes),
 							getPropertyArray(sub_ycolumn.length , 3, sub_subtypes2),
 							false,null,colorlist,plot1.getDatasetCount() + 1);
 					setValueAxisProperty(plot2.getRangeAxis(0), subchartymaxval, subchartylabel,chartdefaultcolor,chartyformat, chartdefaultfont);
					plot2.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						// GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot2.setBackgroundPaint(plot_bg_color);
						plot2.setRangeTickBandPaint(nextColor(plot_bg_color));
					}
					if (plotedgecolor != null)
					{
						plot2.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot2, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot2, chartmargin);

					DateAxis domainaxis = new DateAxis("");
					domainaxis.setUpperMargin(0.0);
					domainaxis.setLowerMargin(0.0);
 					setDomainAxisProperty(domainaxis, chartxmaxval, chartxlabel,chartdefaultcolor, chartdefaultfont);
					
					CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domainaxis);
					plot.add(plot1, (int)(10 - 10 * sub_height));
					plot.add(plot2, (int)(10 * sub_height));
					setPlotRangeAxisSpace(plot, chartmargin);
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					chart = new JFreeChart(plot);
				}
 			}
 			else
 			{
				if (sub_ycolumn.length == 0)
				{
	 				CategoryPlot plot = getChartCategoryPlot(crosstab, chartxcolumn, ycolumn,y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							true,null,colorlist,0);
 					setDomainAxisProperty(plot.getDomainAxis(0), chartxmaxval, chartxlabel,chartdefaultcolor, chartdefaultfont);
 					setValueAxisProperty(plot.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat, chartdefaultfont);
 					if (plot.getRangeAxis(1) != null)
	 				{
 						setValueAxisProperty(plot.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format, chartdefaultfont);
 					}
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					plot.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot.setBackgroundPaint(plot_bg_paint);
					}
					if (plotedgecolor != null)
					{
						plot.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot, chartmargin);
					chart = new JFreeChart(plot);
				}
				else
				{
	 				CategoryPlot plot1 = getChartCategoryPlot(crosstab, chartxcolumn, ycolumn,y2_columns,
 							getPropertyArray(ycolumn.length + y2_columns.length, 1, c_types),
 							getPropertyArray(ycolumn.length + y2_columns.length, 2, c_subtypes),
 							getPropertyArray(ycolumn.length + y2_columns.length, 3, c_subtypes2),
 							false,null,colorlist,0);
 					setValueAxisProperty(plot1.getRangeAxis(0), chartymaxval, chartylabel,chartdefaultcolor,chartyformat,chartdefaultfont);
	 				if (plot1.getRangeAxis(1) != null)
 					{
 						setValueAxisProperty(plot1.getRangeAxis(1), charty2maxval, charty2label,chartdefaultcolor,charty2format, chartdefaultfont);
 					}
					plot1.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot1.setBackgroundPaint(plot_bg_paint);
					}
					if (plotedgecolor != null)
					{
						plot1.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot1, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot1, chartmargin);

	 				CategoryPlot plot2 = getChartCategoryPlot(crosstab, chartxcolumn, sub_ycolumn, empty_array,
 							getPropertyArray(sub_ycolumn.length , 1, sub_types),
 							getPropertyArray(sub_ycolumn.length , 2, sub_subtypes),
 							getPropertyArray(sub_ycolumn.length , 3, sub_subtypes2),
 							false,null,colorlist,plot1.getDataset().getColumnCount() + 1);
 					setValueAxisProperty(plot2.getRangeAxis(0), subchartymaxval, subchartylabel,chartdefaultcolor,chartyformat, chartdefaultfont);
					plot2.setForegroundAlpha(fg_alpha);
					if (plotbackcolor != null)
					{
						java.awt.Color plot_bg_color = getColor(plotbackcolor);
						GradientPaint plot_bg_paint = new GradientPaint(0,0,plot_bg_color,width,height,prevColor(plot_bg_color));
						plot2.setBackgroundPaint(plot_bg_paint);
					}
					if (plotedgecolor != null)
					{
						plot2.setOutlinePaint(getColor(plotedgecolor));
					}
					setGridLineDesc(plot2, gridline, gridstyle, gridcolor);
					setPlotRangeAxisSpace(plot2, chartmargin);

					CategoryAxis domainaxis = new CategoryAxis("");
 					setDomainAxisProperty(domainaxis, chartxmaxval, chartxlabel,chartdefaultcolor, chartdefaultfont);
					
					CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(domainaxis);
					plot.add(plot1, (int)(10 - 10 * sub_height));
					plot.add(plot2, (int)(10 * sub_height));
					setPlotRangeAxisSpace(plot, chartmargin);
					if (chartorient != null)
					{
						if (chartorient.equalsIgnoreCase("HORIZONTAL"))
							plot.setOrientation(PlotOrientation.HORIZONTAL);
						else if (chartorient.equalsIgnoreCase("VERTICAL"))
							plot.setOrientation(PlotOrientation.VERTICAL);
					}
					chart = new JFreeChart(plot);
				}
 			}
 		}

		if (chart != null)
		{
			chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			if (backcolor != null)
			{
				java.awt.Color chart_bg_color = getColor(backcolor);
				GradientPaint chart_bg_paint = new GradientPaint(0,0,chart_bg_color,width,height,prevColor(chart_bg_color));
				chart.setBackgroundPaint(chart_bg_paint);
			}
			if (edgecolor != null)
			{
				chart.setBorderVisible(true);
				chart.setBorderPaint(getColor(edgecolor));
			}
			if (chart.getLegend() != null)
			{
				chart.getLegend().setBackgroundPaint(chart.getBackgroundPaint());
				chart.getLegend().setBorder(org.jfree.chart.block.BlockBorder.NONE);
				if (legendcolor != null)
				{
					chart.getLegend().setItemPaint(getColor(legendcolor));
				}
				if (legendfont != null)
				{
					chart.getLegend().setItemFont(getFont(legendfont));
				}
				else
				{
					chart.getLegend().setItemFont(SIMHEI12);
				}
			}
			if (legendposition != null)
			{
				if (legendposition.equalsIgnoreCase("OFF"))
				{
					chart.removeLegend();
					org.jfree.chart.title.TextTitle subtitle = new org.jfree.chart.title.TextTitle("  ");
					subtitle.setPosition(RectangleEdge.BOTTOM);
					subtitle.setHorizontalAlignment(HorizontalAlignment.CENTER);
					chart.addSubtitle(subtitle);					
				}
				else
				{
					chart.getLegend().setPosition(getLegendPosition(legendposition));
					chart.getLegend().setHorizontalAlignment(getLegendHAlign(legendposition));
					if (!chart.getLegend().getPosition().equals(RectangleEdge.BOTTOM))
					{
						org.jfree.chart.title.TextTitle subtitle = new org.jfree.chart.title.TextTitle("  ");
						subtitle.setPosition(RectangleEdge.BOTTOM);
						subtitle.setHorizontalAlignment(HorizontalAlignment.CENTER);
						chart.addSubtitle(subtitle);	
					}
				}
			}
			if (charttitle != null)
			{
				chart.setTitle(vt.parseString(charttitle));
				if (charttitlefont != null)
				{
					chart.getTitle().setFont(getFont(charttitlefont));
				}
				else
				{
					chart.getTitle().setFont(SIMHEI18);
				}
				if (charttitlecolor != null)
				{
					chart.getTitle().setPaint(getColor(charttitlecolor));
				}
				else
				{
					if (chartdefaultcolor != null)
						chart.getTitle().setPaint(getColor(chartdefaultcolor));
				}
			}
			if (chartsubtitle != null)
			{
				org.jfree.chart.title.TextTitle subtitle = new org.jfree.chart.title.TextTitle(vt.parseString(chartsubtitle));
				if (chartsubtitlecolor != null)
				{
					subtitle.setPaint(getColor(chartsubtitlecolor));
				}
				else
				{
					if (chartdefaultcolor != null)
						subtitle.setPaint(getColor(chartdefaultcolor));
				}
				if (chartsubtitlefont != null)
				{
					subtitle.setFont(getFont(chartsubtitlefont));
				}
				else
				{
					subtitle.setFont(SIMHEI12);
				}
				subtitle.setPosition(RectangleEdge.TOP);
				subtitle.setHorizontalAlignment(HorizontalAlignment.CENTER);
				chart.addSubtitle(subtitle);
			}
			if (chartdefaultcolor != null)
			{
				if (chart.getLegend() != null)
					chart.getLegend().setItemPaint(getColor(chartdefaultcolor));
			}

			BufferedImage gifimg = chart.createBufferedImage(width,height,BufferedImage.TYPE_INT_BGR,null);
			Graphics2D g2 = gifimg.createGraphics();

			if (chartfootnote != null)
			{
				if (chartfootnotecolor != null)
				{
					g2.setColor(getColor(chartfootnotecolor));
				}
				else
				{
					if (chartdefaultcolor != null)
						g2.setColor(getColor(chartdefaultcolor));
					else
						g2.setColor(java.awt.Color.BLACK);
				}
				if (chartfootnotefont != null)
				{
					g2.setFont(getFont(chartfootnotefont));
				}
				else if (chartdefaultfont != null)
				{
					g2.setFont(getFont(chartdefaultfont));
				}
				else
				{
					g2.setFont(SIMHEI12);
				}
				g2.drawString(vt.parseString(chartfootnote),5,height - 8);
			}
                        /*
			g2.setFont(SIMSUN12);
			g2.setColor(java.awt.Color.GRAY);
			g2.drawString("AnySQL.net",width - 60,height - 4);
                        */
			PngEncoder gifencoder = new PngEncoder(gifimg, false, 0, 5);
			out.write(gifencoder.pngEncode());
		}
		out.flush();    		
    	}
	
}