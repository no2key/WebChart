package com.lfx.web;

import com.lfx.db.*;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import oracle.charts.codec.PNGEncoder;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.awt.Font;

public final class WebChart 
{

	public static final int HORIZONTAL = oracle.charts.axischart.AxisChart.HORIZONTAL;
	public static final int VERTICAL = oracle.charts.axischart.AxisChart.VERTICAL;

	public static final int DOT   = oracle.charts.axischart.AxisChart.POINT;
	public static final int LINE  = oracle.charts.axischart.AxisChart.LINE;
	public static final int AREA  = oracle.charts.axischart.AxisChart.AREA;
	public static final int BAR   = oracle.charts.axischart.AxisChart.BAR;
	public static final int STACKBAR = oracle.charts.axischart.AxisChart.BAR_STACKED;
	public static final int STOCK_HLC = oracle.charts.axischart.AxisChart.HILOCLOSE;
	public static final int STOCK_OHLC = oracle.charts.axischart.AxisChart.OPENHILOCLOSE;
	public static final int STOCK = oracle.charts.axischart.AxisChart.CANDLESTICK;


	public static final int NORTHWEST = oracle.charts.legend.Legend.NORTHWEST;
	public static final int NORTHEAST = oracle.charts.legend.Legend.NORTHEAST;
	public static final int NORTH     = oracle.charts.legend.Legend.NORTH;
	public static final int SOUTHWEST = oracle.charts.legend.Legend.SOUTHWEST;
	public static final int SOUTHEAST = oracle.charts.legend.Legend.SOUTHEAST;
	public static final int SOUTH     = oracle.charts.legend.Legend.SOUTH;
	public static final int EAST      = oracle.charts.legend.Legend.EAST;
	public static final int WEST      = oracle.charts.legend.Legend.WEST;

	public static final int LINE_SOLID  = oracle.charts.types.LineDesc.LINE_SOLID;
	public static final int LINE_DASHED = oracle.charts.types.LineDesc.LINE_DASHED;
	public static final int LINE_DASHED2= oracle.charts.types.LineDesc.LINE_DASHED2;

	public static final int MARKER_NONE    = oracle.charts.types.MarkerDesc.MARKER_NONE;
	public static final int MARKER_CIRCLE  = oracle.charts.types.MarkerDesc.MARKER_CIRCLE;
	public static final int MARKER_SQUARE  = oracle.charts.types.MarkerDesc.MARKER_SQUARE;
	public static final int MARKER_DIAMOND = oracle.charts.types.MarkerDesc.MARKER_DIAMOND;
	public static final int MARKER_PLUS    = oracle.charts.types.MarkerDesc.MARKER_PLUS;
	public static final int MARKER_X       = oracle.charts.types.MarkerDesc.MARKER_X;

	public static final int BAR_BASIC      = oracle.charts.types.BarDesc.STYLE_BASIC;
	public static final int BAR_SHADOW     = oracle.charts.types.BarDesc.STYLE_DROP_SHADOW;
	public static final int BAR_3D         = oracle.charts.types.BarDesc.STYLE_EFFECT3D;

	public static final int PIE_BASIC      = oracle.charts.types.PieStyleDesc.STYLE_BASIC;
	public static final int PIE_SHADOW     = oracle.charts.types.PieStyleDesc.STYLE_DROP_SHADOW;
	public static final int PIE_3D         = oracle.charts.types.PieStyleDesc.STYLE_EFFECT_3D;

	public static final java.awt.Font SIMHEI18 = new java.awt.Font("Serif",java.awt.Font.PLAIN,18);
	public static final java.awt.Font SIMHEI12 = new java.awt.Font("Serif",java.awt.Font.PLAIN,12);
	public static final java.awt.Font SIMSUN12 = new java.awt.Font("Serif",java.awt.Font.PLAIN,12);
	public static final java.awt.Font SIMSUN10 = new java.awt.Font("Serif",java.awt.Font.PLAIN,10);

	private static final int MAX_IMAGE_CSS_ITEMS = 500;

	public static final java.awt.Color COLORLIST[] = {
			new java.awt.Color(239,55,55),
			new java.awt.Color(55,55,239),
			new java.awt.Color(239,112,112),
			new java.awt.Color(112,112,239),
			new java.awt.Color(239,239,112),
			new java.awt.Color(239,112,239),
			new java.awt.Color(112,239,239),
			new java.awt.Color(239,179,179),
			new java.awt.Color(179,179,239),
			new java.awt.Color(179,239,179),
			new java.awt.Color(239,239,179),
			new java.awt.Color(239,179,239),
			new java.awt.Color(179,239,239)
		};

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
		
	public static void saveJpgFile(BufferedImage bi,java.io.OutputStream out)
		throws java.io.IOException
	{
		/*
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam  param = encoder.getDefaultJPEGEncodeParam(bi);
		param.setQuality(1.0f,false);
		encoder.setJPEGEncodeParam(param);
		encoder.encode(bi);
		*/
		oracle.charts.codec.PNGEncoder encoder = new oracle.charts.codec.PNGEncoder(out);
	        encoder.encode(bi);
	}
	public static void saveGifFile(BufferedImage bi,java.io.OutputStream out)
		throws java.io.IOException
	{
		oracle.charts.codec.PNGEncoder encoder = new oracle.charts.codec.PNGEncoder(out);
	        encoder.encode(bi);
	}
	public static String[] getLines(String src)
	{
		return TextUtils.toStringArray(TextUtils.getLines(src));
	}
	public static String[] getImageMapLines(String src)
	{
		int row;
		java.util.Vector lines = TextUtils.getLines(src);
		for(row = lines.size() - 1; row >= 0; row --)
		{
			if (lines.elementAt(row).toString().trim().startsWith("<!--"))
				lines.removeElementAt(row);
			if (lines.elementAt(row).toString().trim().startsWith("#"))
				lines.removeElementAt(row);			
		}
		return TextUtils.toStringArray(lines);
	}
	private static int getOrientation(String orient)
	{
		if (orient != null && orient.equalsIgnoreCase("HORIZONTAL"))
			return HORIZONTAL;
		return VERTICAL;	
	}
	private static int getLegendPosition(String position)
	{
		if (position!=null)
		{
			if (position.equals("NORTHWEST"))
				return NORTHWEST;
			else if (position.equals("NORTHEAST"))
				return NORTHEAST;
			else if (position.equals("NORTH"))
				return NORTH;
			else if (position.equals("SOUTHEAST"))
				return SOUTHWEST;
			else if (position.equals("SOUTHWEST"))
				return SOUTHEAST;
			else if (position.equals("SOUTH"))
				return SOUTH;
			else if (position.equals("EAST"))
				return EAST;
			else if (position.equals("WEST"))
				return WEST;
		}
		return SOUTH;
	}

	private static int getAxisGridType(String charttype)
        {
		int axis_type = 3;
	 	if (charttype.equalsIgnoreCase("NONE"))
	 		axis_type = 0;
	 	else if (charttype.equalsIgnoreCase("X"))
	 		axis_type = 1;
	 	else if (charttype.equalsIgnoreCase("Y"))
	 		axis_type = 2;
	 	else if (charttype.equalsIgnoreCase("XY"))
	 		axis_type = 3;
		return axis_type;				
        }

	private static int getChartType(String charttype)
        {
		int axis_type = WebChart.LINE;
 		if ( charttype.equalsIgnoreCase("BAR"))
 			axis_type = WebChart.BAR;
	 	else if (charttype.equalsIgnoreCase("LINE"))
	 		axis_type = WebChart.LINE;
	 	else if (charttype.equalsIgnoreCase("DOT"))
	 		axis_type = WebChart.DOT;
	 	else if (charttype.equalsIgnoreCase("AREA"))
	 		axis_type = WebChart.AREA;
	 	else if (charttype.equalsIgnoreCase("STACKBAR"))
	 		axis_type = WebChart.STACKBAR;
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
				return 4;
			else if (subtype.equals("LINE_SOLID"))
				return LINE_SOLID;
			else if (subtype.equals("LINE_DASHED"))
				return LINE_DASHED;
			else if (subtype.equals("LINE_DASHED2"))
				return LINE_DASHED2;
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

	private static int getChartSubType2(String subtype)
	{
		if (subtype != null)
		{
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
		return MARKER_CIRCLE;
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
					java.util.Vector words = TextUtils.getWords(col,",");
					if (words.size()>3)
					{
						return new java.awt.Color(
							Integer.valueOf(words.elementAt(0).toString()).intValue(),
							Integer.valueOf(words.elementAt(1).toString()).intValue(),
							Integer.valueOf(words.elementAt(2).toString()).intValue(),
							Integer.valueOf(words.elementAt(3).toString()).intValue());
					}
					else if (words.size()>2)
					{
						return new java.awt.Color(
							Integer.valueOf(words.elementAt(0).toString()).intValue(),
							Integer.valueOf(words.elementAt(1).toString()).intValue(),
							Integer.valueOf(words.elementAt(2).toString()).intValue());
					}

				} catch (java.lang.NumberFormatException nfe) {}
			}
		}
		return null;
	}
	private static java.awt.Color[] getColorList(String colorlist)
	{
		java.util.Vector colarr = new java.util.Vector();
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
		//PLAIN;
		//BOLD;
		//ITALIC;
		int font_style;
		String temp;
		java.util.Vector words = TextUtils.getWords(sfont,",");
		if (words.size()>2)
		{
			temp = words.elementAt(1).toString().toUpperCase();
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
					words.elementAt(0).toString(),
					font_style,
					Integer.valueOf(words.elementAt(2).toString()).intValue());
			} catch (java.lang.NumberFormatException nfe) {}
		}
		return null;
	}
	
	private static boolean getPieLabelStyle(String style)
	{
		if (style == null) return true;
		if (style.equalsIgnoreCase("PERCENT")) return true;
		return false;
	}

        private static void saveErrorImage(java.io.OutputStream out, int width, int height,String message)
	{
	        java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        java.awt.Graphics2D g2 = null;
                g2 = bi.createGraphics();
                g2.setColor(new java.awt.Color(51, 153, 255));
                g2.fillRect(0,0,width,height);
                g2.setColor(java.awt.Color.black);
		g2.drawRect(0,0,width,height);
		g2.drawString(message,10,height/2 - 9);

		try {
			WebChart.saveJpgFile(bi,out);
		} catch (java.io.IOException ioe) {}
        }

        private static String saveErrorImage(int width, int height,String message, int keep_time)
	{
	        java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        java.awt.Graphics2D g2 = null;
                g2 = bi.createGraphics();
                g2.setColor(new java.awt.Color(51, 153, 255));
                g2.fillRect(0,0,width,height);
                g2.setColor(java.awt.Color.black);
		g2.drawRect(0,0,width,height);
		g2.drawString(message,10,height/2 - 9);

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			WebChart.saveJpgFile(bi,out);
			out.close();
			return ImageCache.putContent(out.toByteArray(), keep_time + 10);
		} catch (java.io.IOException ioe) {}
		return "b0";
        }

        private static boolean checkVariableList(String varlist, VariableTable vt)
	{
	    if (varlist == null) return true;
	    java.util.Vector var_req = TextUtils.getWords(varlist,",");
	    if (var_req.size() == 0) return true;
	    for (int i=0; i< var_req.size(); i++)
	    {
		if (var_req.elementAt(i).toString().startsWith("*"))
		{
			if (vt.getString(var_req.elementAt(i).toString().substring(1)) == null) return false;			
		}
		else
		{
			if (vt.getString(var_req.elementAt(i).toString()) == null) return false;
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
				
	public static void generateChart(java.io.Writer out, java.io.OutputStream imgout,VariableTable vt, String fileextention) 
		throws java.io.IOException
    	{
		int i=0,sql_count=0;
		DBRowCache crosstab = null, temprows=null;
    		String chartquery = null;
		String querycache = null;
		int querycachetime = 300;
                int querymaxrows=10000;
		String charttype  = null;
		String iscrosstab = null;
		String dbname = null;
		String dbrule = null;
		String express = null;
		String foreach = null;
		String varlist = null;
		String pagecount = null;
		String ignmarkdown = null;
		String ignsqlerror = null;
		boolean pageexpire = false;
		String head_formater = null;
		String data_formater = null;
		String row_color = null;
		String row_align = null;
		String lay_out = null;

		String tablename = null;
		String columnlist = null;

		java.util.Vector foreachlist = new java.util.Vector();
		java.util.Vector querylist = new java.util.Vector();

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
			if (existVariableTableValue(vt, "URLS"))
			{
				java.lang.String url_list = getVariableTableValue(vt,"URLS");
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
			if (existVariableTableValue(vt, "INPUTS"))
			{
				java.lang.String input_list = getVariableTableValue(vt,"INPUTS");
				String input_arr[] = TextUtils.toStringArray(TextUtils.getLines(input_list));
				if (input_arr.length > 0)
				{
				   out.write("<inputs action=\"" + vt.EncodeXML(vt.parseURLString("${REQUEST.FILE}")) + "\">\n");
				   for(i=0; i< input_arr.length; i++)
				   {
					String input_words[] = TextUtils.toStringArray(TextUtils.getWords(input_arr[i],"|"));
					if (input_words.length > 2)
					{
						out.write("<item type=\""+input_words[0]+"\" name=\""+
							  input_words[1]+"\" value=\""+vt.EncodeXML(vt.parseString(input_words[2]))+
							  "\" ");
						if (input_words.length > 3) out.write(input_words[3]);
						out.write(" />\n");
					}
				   }
				   out.write("</inputs>\n");
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
			    iscrosstab = getVariableTableValue(vt, "CROSSTAB", querylist.elementAt(i).toString(), true);
			    chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i).toString(),false);
			    querycache = getVariableTableValue(vt, "QUERYCACHE", querylist.elementAt(i).toString(),false);
			    head_formater = getVariableTableValue(vt, "HEADHTML", querylist.elementAt(i).toString(),false);
			    data_formater = getVariableTableValue(vt, "DATAHTML", querylist.elementAt(i).toString(),false);
			    row_color = getVariableTableValue(vt, "ROWCOLOR", querylist.elementAt(i).toString(),true);
			    row_align = getVariableTableValue(vt, "ALIGN", querylist.elementAt(i).toString(),true);
			    querycachetime = getint(getVariableTableValue(vt, "QUERY_CACHE_TIME", querylist.elementAt(i).toString(),true),300);
			    charttype = getVariableTableValue(vt, "TYPE", querylist.elementAt(i).toString(),true);
			    varlist   = getVariableTableValue(vt, "VARLIST", querylist.elementAt(i).toString(),true);
			    pagecount = getVariableTableValue(vt, "PAGES", querylist.elementAt(i).toString(),true);
			    dbname = getVariableTableValue(vt, "DBNAME", querylist.elementAt(i).toString(),true);
			    dbrule = getVariableTableValue(vt, "DBID", querylist.elementAt(i).toString(),true);
			    ignsqlerror = getVariableTableValue(vt, "IGNORE_SQLERROR", querylist.elementAt(i).toString(),true);
			    ignmarkdown = getVariableTableValue(vt, "IGNORE_MARKDOWN", querylist.elementAt(i).toString(),true);
			    lay_out = getVariableTableValue(vt, "LAYOUT", querylist.elementAt(i).toString(),false);
			    tablename  = getVariableTableValue(vt, "TABLE", querylist.elementAt(i).toString(),false);
			    columnlist = getVariableTableValue(vt, "COLUMN", querylist.elementAt(i).toString(),false);
			    querymaxrows = getint(getVariableTableValue(vt, "MAXROWS", querylist.elementAt(i).toString(),true),10000);

			    if (lay_out == null) lay_out = "0";

			    foreach = vt.parseString(getVariableTableValue(vt, "FORALL", querylist.elementAt(i).toString(),false));
				
			    foreachlist.removeAllElements();
			    if (foreach != null)
				foreachlist.addAll(TextUtils.getLines(foreach));
			    
			    for(int forj=0; forj < (foreachlist.size() > 0 ? foreachlist.size() : 1); forj++)
			    {
				if (forj < foreachlist.size())
				{
				  if (foreachlist.get(forj) == null ||
				    foreachlist.get(forj).toString().trim().length() == 0)
				    continue;
				  vt.setValue(foreachlist.get(forj).toString());
				}

				if (!chartquery.equals("-"))
				{
				    crosstab = DBOperation.getDBRowCache();
				    if (querycache != null)
				    {
					DataCache.clearData();
					crosstab = DataCache.getData(vt.parseString(querycache));
				    }
				    if (crosstab == null || crosstab.getColumnCount() == 0)
				    {
				      if (!pageexpire && checkVariableList(varlist,vt))
			    	      {
				         for(int dsloop=0; dsloop < 2; dsloop ++)
				         {
				           try {
				             db = DBLogicalManager.getPoolConnection(vt.parseString(dbname), dbrule);

					     if (tablename != null && tablename.length() > 0 && columnlist != null && columnlist.length() > 0 && checkVariableList(columnlist,vt))
					     {
						try {
							if (vt.getString("sqleditmode").equalsIgnoreCase("INSERT"))
							{
							    DBOperation.executeUpdate(db, SQLCreator.getInsertSQL(tablename, columnlist), vt);
	 						    db.commit();
							}
							else if (vt.getString("sqleditmode").equalsIgnoreCase("UPDATE"))
							{
							    DBOperation.executeUpdate(db, SQLCreator.getUpdateSQL(tablename, columnlist), vt);
	 						    db.commit();
							}
							else if (vt.getString("sqleditmode").equalsIgnoreCase("DELETE"))
							{
							    DBOperation.executeUpdate(db, SQLCreator.getDeleteSQL(tablename, columnlist), vt);
	 						    db.commit();
							}
						}
						 catch (java.sql.SQLException sqle)
						{
						  if (db != null) { db.close(); db = null;}
						  throw new java.io.IOException(sqle.getMessage()); 
						}
					     }

    					     chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i).toString(),false);
					     if (chartquery.equalsIgnoreCase("*"))
					     {
						chartquery = getVariableTableValue(vt, "QUERY_"+db.getDBTag(), querylist.elementAt(i).toString(),false);
					     }
					     java.util.Vector cross_fields = TextUtils.getWords(iscrosstab,"|");
					     if (cross_fields.size()==0)
						crosstab = DBOperation.executeQuery(db,chartquery,vt);
					     else if(cross_fields.size() < 3)
						crosstab = DBOperation.executeCrossTab(db,chartquery,vt);
					     else
						crosstab = DBOperation.executeCrossTab(db,chartquery,vt,
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(0).toString(),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(1).toString(),",")),
							TextUtils.toStringArray(
								TextUtils.getWords(cross_fields.elementAt(2).toString(),",")));
					     express = getVariableTableValue(vt, "EXPRESS", querylist.elementAt(i).toString(),true);
					     if (express != null)
					     {
					       String colname;
					       String expstr;
					       String expcols[] = null;
					       String colarrs[] = null;
					       java.util.Vector expwords;
					       String exparr[] = TextUtils.toStringArray(TextUtils.getLines(express));
					       for(int tmpk=0; tmpk < exparr.length; tmpk++)
					       {
					    	  expwords = TextUtils.getWords(exparr[tmpk],"|");
					    	  if (expwords.size() == 3)
					    	  {
					    		colname = expwords.elementAt(0).toString();
					    		expstr  = expwords.elementAt(1).toString();
					    		expcols = TextUtils.toStringArray(TextUtils.getWords(expwords.elementAt(2).toString(),","));
							crosstab.addExpression(vt.parseString(colname), expstr, expcols);
						  }
						  else if (expwords.size() == 2)
					    	  {
					    		colname = expwords.elementAt(0).toString();
					    		expstr  = expwords.elementAt(1).toString();
							crosstab.addExpression(vt.parseString(colname), expstr);
						  }
					       }
					     }
					     express = getVariableTableValue(vt, "FILTER", querylist.elementAt(i).toString(),true);
					     if (express != null)
					     {
					       String expstr;
					       java.util.Vector expwords;
					       String expcols[] = null;
					       String exparr[] = TextUtils.toStringArray(TextUtils.getLines(express));
					       for(int tmpk=0; tmpk < exparr.length; tmpk++)
						   crosstab.expressFilter(exparr[tmpk]);
					     }
					     if (querycache!= null)
					     {
						DataCache.putData(vt.parseString(querycache), crosstab, querycachetime);
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

				if (head_formater != null) crosstab.setStringProperty("HEADFORMATER",head_formater);
				if (data_formater != null) crosstab.setStringProperty("DATAFORMATER",data_formater);
				if (columnlist != null) crosstab.setStringProperty("PRIMARYKEY",columnlist);
				if (row_color != null) crosstab.setStringProperty("ROWCOLOR",row_color);
				if (row_align != null) crosstab.setStringProperty("COLUMALIGN",row_align);
				
				String chartlabel = vt.parseString(getVariableTableValue(vt, "LABEL", querylist.elementAt(i).toString(),false));
				java.util.Vector label = TextUtils.getFields(chartlabel,"|");
				for(int j=0;j<label.size() && j<crosstab.getColumnCount();j++)
				{
					if (label.elementAt(j) != null)
						crosstab.setColumnLabel(j+1,label.elementAt(j).toString());
				}
				if (crosstab.getRowCount()==1)
				{
					for(int j=1;j<=crosstab.getColumnCount();j++)
					{
						vt.add("QUERY_"+querylist.elementAt(i).toString()+"."+crosstab.getColumnName(j),
							java.sql.Types.VARCHAR);
						vt.setValue("QUERY_"+querylist.elementAt(i).toString()+"."+crosstab.getColumnName(j),
							crosstab.getItem(1,j));
					}
				}
				if ("SET".equalsIgnoreCase(charttype))
				{
					vt.add("ARRAY."+querylist.elementAt(i).toString(), java.sql.Types.VARCHAR);
					vt.setValue("ARRAY."+querylist.elementAt(i).toString(), crosstab.getFullText());
				}
				else if ("URL".equalsIgnoreCase(charttype))
				{
					String url_pattern = getVariableTableValue(vt, "URLSTRING", querylist.elementAt(i).toString(),false);
					if (url_pattern != null)
					{
						String url_words[] = TextUtils.toStringArray(TextUtils.getWords(url_pattern,"|"));
						if (url_words.length > 1)
						{
				   			out.write("<urls>\n");
				   			for(int j=1; j<=crosstab.getRowCount(); j++)
				   			{
								if (j>1) out.write("<url id=\"-\">,</url>\n");
								out.write("<url id=\""+crosstab.getItem(j,url_words[0]).toString()+"\">");
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
						generateChart(crosstab,imgout,vt,querylist.elementAt(i).toString(), fileextention);
						temprows = null;
						return;
					}
					else
					{
						for(int tmpj=0; tmpj < crosstab.getRowCount() - querymaxrows; tmpj ++)
						{
						     crosstab.deleteRow(crosstab.getRowCount());
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
						out.write("<webchart id=\""+querylist.elementAt(i).toString()+"\" layout=\""+vt.parseString(lay_out)+"\">\n");
		                                if ((express=getVariableTableValue(vt,"XMLDATA",querylist.elementAt(i).toString(),false)) != null)
        		                            out.write(vt.parseString(express));
		                                if ((express=getVariableTableValue(vt,"TITLE",querylist.elementAt(i).toString(),false)) != null)
        		                            out.write("  <title><![CDATA["+ vt.parseString(express) +"]]></title>\n");
                		                if ((express=getVariableTableValue(vt,"SUBTITLE",querylist.elementAt(i).toString(),false)) != null)
                        		            out.write("  <subtitle><![CDATA["+ vt.parseString(express) +"]]></subtitle>\n");
                		                if ((express=getVariableTableValue(vt,"FOOTNOTE",querylist.elementAt(i).toString(),false)) != null)
		                                    out.write("  <footnote><![CDATA["+ vt.parseString(express) +"]]></footnote>\n");
                		                if ((express=getVariableTableValue(vt,"MEMO",querylist.elementAt(i).toString(),false)) != null)
		                                    out.write("  <memo><![CDATA["+ vt.parseString(express) +"]]></memo>\n");
						if (crosstab.getColumnCount() > 0)
						    generateChart(crosstab,out,vt,querylist.elementAt(i).toString(), fileextention);
						out.write("</webchart>\n");
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
	}

	private static void generateChart(DBRowCache crosstab,java.io.Writer out,VariableTable vt,String index, String fileextention)
		throws java.io.IOException 
    	{
    		int i;
    		String mapcss="";
 		int width=500,height=400,grpcolcount=2;

 		String fileNameChart="";

    		String chartdefaultfont = getVariableTableValue(vt,"defaultfont");
    		String chartdefaultcolor = getVariableTableValue(vt,"defaultcolor");
    		
		String colors = getVariableTableValue(vt,"colors", index, true);
		
		String xmltag = getVariableTableValue(vt,"xmltag", index, true);
		String xmlattr = getVariableTableValue(vt,"xmlattr", index, true);

   		String legendposition    = getVariableTableValue(vt,"legend",index,true);
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
    		
    		String chartwidth = getVariableTableValue(vt,"width",index,true);
    		String chartheight = getVariableTableValue(vt,"height",index,true);

    		String charttitle = vt.parseString(getVariableTableValue(vt,"title",index,false));
    		String charttitlefont = getVariableTableValue(vt,"titlefont",index,true);
    		String charttitlecolor = getVariableTableValue(vt,"titlecolor",index,true);

    		String chartsubtitle = vt.parseString(getVariableTableValue(vt,"subtitle",index,false));
    		String chartsubtitlefont = getVariableTableValue(vt,"subtitlefont",index,true);
    		String chartsubtitlecolor = getVariableTableValue(vt,"subtitlecolor",index,true);

    		String chartfootnote = vt.parseString(getVariableTableValue(vt,"footnote",index,false));
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


   		String pielabelstyle    = getVariableTableValue(vt,"PIELABEL",index,true);
   		String pielabeldigit    = getVariableTableValue(vt,"PIEDIGIT",index,true);
   		
   		String tooltipcolumn    = getVariableTableValue(vt,"TOOLTIP",index,true);
   		String chartxcolumn    = vt.parseString(getVariableTableValue(vt,"XCOL",index,true));
   		String chartxlabel    = getVariableTableValue(vt,"XLABEL",index,true);
   		String chartycolumn    = vt.parseString(getVariableTableValue(vt,"YCOL",index,true));
   		String chartylabel    = getVariableTableValue(vt,"YLABEL",index,true);
   		String chartymaxval    = getVariableTableValue(vt,"YMAX",index,true);
   		String charty2column    = vt.parseString(getVariableTableValue(vt,"Y2COL",index,true));
   		String charty2maxval    = getVariableTableValue(vt,"Y2MAX",index,true);

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
   		
		double sub_height = 0.5;
		String ycolumn[] = {};
                String c_types[] = {};
                String c_subtypes[] = {};
                String c_subtypes2[] = {};
		String y2_columns[] = {};
		java.awt.Color colorlist[] = getColorList(colors);

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
				crosstab.writeXMLBody(out,xmltag,xmlattr,grpcolcount, merge_columns,cust_col_length,vt,true);
			else
				crosstab.writeXMLBody(out,xmltag,xmlattr,grpcolcount, merge_columns,cust_col_length,vt,false);
			return;
		}

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
		width = (width < 100?100:width);
		width = (width > 1000?1000:width);
		height = (height < 80?80:height);
		height = (height > 800?800:height);

		if ( crosstab.getColumnCount() == 0)
		{
			String chartfile = saveErrorImage(width,height,"Query execute error!", vt.getInt("WEBCHART.KEEP_CACHE_TIME",300));
			fileNameChart = chartfile;
			out.write("\t<image>\n");
			out.write("\t\t<file>");
			if (getVariableTableValue(vt,"CACHE",index,true) == null)
				out.write("showimage"+fileextention+"?id="+fileNameChart+"&amp;del=yes");
			else
				out.write("showimage"+fileextention+"?id="+fileNameChart);
			out.write("</file>\n");
			out.write("\t</image>\n");
			out.flush();
			return;
		}
		if ( crosstab.getRowCount() == 0)
		{
			String chartfile = saveErrorImage(width,height,"Query execute error!", vt.getInt("WEBCHART.KEEP_CACHE_TIME",300));
			fileNameChart = chartfile;
			out.write("\t<image>\n");
			out.write("\t\t<file>");
			if (getVariableTableValue(vt,"CACHE",index,true) == null)
				out.write("showimage"+fileextention+"?id="+fileNameChart+"&amp;del=yes");
			else
				out.write("showimage"+fileextention+"?id="+fileNameChart);
			out.write("</file>\n");
			out.write("\t</image>\n");
			out.flush();
			return;
		}

		java.awt.Font  dfont =  getFont(chartdefaultfont);
		java.awt.Color defaultcolor = getColor(chartdefaultcolor);
		
		String chartfile = null;

		if (charttype.equalsIgnoreCase("PIE"))
		{
 			WebChart.PieChart PieCh = new WebChart.PieChart();

			if (dfont != null)
			{
				PieCh.setLegendFont(dfont);
				PieCh.setFont(dfont);
			}
			else
			{
				PieCh.setLegendFont(SIMSUN12);
				PieCh.setFont(SIMSUN12);
			}

			if (defaultcolor != null)
			{
				PieCh.setForeground(defaultcolor);
			}
			
			PieCh.setSize(width,height);

			if (backcolor != null && getColor(backcolor) != null)
				PieCh.setBackground(getColor(backcolor.toString()));
			else
				PieCh.setBackground(new java.awt.Color(236,236,236));
			if (edgecolor != null && getColor(edgecolor) != null)
				PieCh.setEdgeColor(new java.awt.Color(236,236,236));
			else
				PieCh.setEdgeColor(java.awt.Color.GRAY);
			if (charttitle != null)
			{
				PieCh.setTitle(vt.parseString(charttitle),
					(charttitlefont==null?SIMHEI18:getFont(charttitlefont)),
					getColor((charttitlecolor==null?null:charttitlecolor)));					
			}
			if (chartsubtitle != null)
			{
				PieCh.setSubtitle(vt.parseString(chartsubtitle),
					(chartsubtitlefont==null?SIMSUN12:getFont(chartsubtitlefont)),
					getColor((chartsubtitlecolor==null?null:chartsubtitlecolor)));
			}
			
			if (chartfootnote != null)
			{
				PieCh.setFootnote(vt.parseString(chartfootnote),
					(chartfootnotefont==null?SIMSUN12:getFont(chartfootnotefont)),
					getColor((chartfootnotecolor==null?null:chartfootnotecolor)));
			}
			if (legendposition!=null && legendposition.trim().length()>0)
			{
				PieCh.setLegendPosition(getLegendPosition(legendposition));
			}

			int labeldigit = 2;
			try {
				if (pielabeldigit != null)
					labeldigit = Integer.valueOf(pielabeldigit).intValue();
			}
		 	catch (java.lang.NumberFormatException nfe)
			{
				labeldigit = 2;
			}
			
			PieCh.setLabelStyle(
				getPieLabelStyle(pielabelstyle==null?null:pielabelstyle),labeldigit);

			PieCh.setPieStyle(getChartSubType(-9999,
				(chartsubtype==null?null:chartsubtype)));
			if (chartxcolumn == null || crosstab.findColumn(chartxcolumn) == 0)
				chartxcolumn = crosstab.getColumnName(1);
			if (chartycolumn == null || crosstab.findColumn(chartycolumn) == 0)
				chartycolumn = crosstab.getColumnName(2);				

			PieCh.setData(crosstab,chartxcolumn,chartycolumn,colorlist);

 			chartfile = PieCh.saveGifFile(crosstab.getRowCount() < MAX_IMAGE_CSS_ITEMS, vt.getInt("WEBCHART.KEEP_CACHE_TIME",300));
			fileNameChart = chartfile;

			if (tooltipcolumn == null)
			{
				tooltipcolumn = chartxcolumn;
			}
			if (chartimagemap == null || chartimagemap.equalsIgnoreCase("YES"))
			{
				if (crosstab.getRowCount() < MAX_IMAGE_CSS_ITEMS)
				{
	 				mapcss = PieCh.getImageMapXML(fileNameChart,
							crosstab,tooltipcolumn,	chartycolumn, charthreftarget,vt);
				}
	 		}
		}
 		else
 		{
 			int axis_type = WebChart.BAR;

 			if ( charttype.equalsIgnoreCase("BAR"))
 				axis_type = WebChart.BAR;
	 		else if (charttype.equalsIgnoreCase("LINE"))
	 			axis_type = WebChart.LINE;
	 		else if (charttype.equalsIgnoreCase("DOT"))
	 			axis_type = WebChart.DOT;
	 		else if (charttype.equalsIgnoreCase("AREA"))
	 			axis_type = WebChart.AREA;
	 		else if (charttype.equalsIgnoreCase("STACKBAR"))
	 			axis_type = WebChart.STACKBAR;
	 		else if (charttype.equalsIgnoreCase("STOCK"))
	 			axis_type = WebChart.STOCK;
	 		else if (charttype.equalsIgnoreCase("STOCK_HLC"))
	 			axis_type = WebChart.STOCK_HLC;
	 		else if (charttype.equalsIgnoreCase("STOCK_OHLC"))
	 			axis_type = WebChart.STOCK_OHLC;

	 		WebChart.AxisChart PieCh = new WebChart.AxisChart();

			try {
				oracle.charts.types.AxisDesc axisdesc = new oracle.charts.types.AxisDesc();
				if (dfont != null)
				{
					PieCh.setLegendFont(dfont);
					axisdesc.setFont(dfont);
				}
				else
				{
					PieCh.setLegendFont(SIMSUN12);
					axisdesc.setFont(SIMSUN12);
				}
				if (defaultcolor != null)
				{
					PieCh.setForeground(defaultcolor);
					axisdesc.setColor(defaultcolor);
				}
				if (chartxlabel != null && chartxlabel.equalsIgnoreCase("OFF"))
				    axisdesc.setDrawLabelOff();
				PieCh.setChartAttributes(PieCh.XYAXIS,axisdesc);
			}catch(oracle.charts.types.ChartException ce){}

			PieCh.setSize(width,height);

			if (backcolor != null && getColor(backcolor) != null)
				PieCh.setBackground(getColor(backcolor.toString()));
			else
				PieCh.setBackground(new java.awt.Color(236,236,236));

			if (edgecolor != null && getColor(edgecolor) != null)
				PieCh.setEdgeColor(getColor(edgecolor));
			else
				PieCh.setEdgeColor(java.awt.Color.GRAY);

			if (plotbackcolor != null && getColor(plotbackcolor) != null)
				PieCh.setPlotBackground(getColor(plotbackcolor));
			else
				PieCh.setPlotBackground(java.awt.Color.WHITE);

			if (plotedgecolor != null && getColor(plotedgecolor) != null)
				PieCh.setPlotEdgeColor(getColor(plotedgecolor));
			else
				PieCh.setPlotEdgeColor(java.awt.Color.GRAY);

			if (charttitle != null)
			{
				PieCh.setTitle(vt.parseString(charttitle),
					(charttitlefont==null?SIMHEI18:getFont(charttitlefont)),
					getColor((charttitlecolor==null?null:charttitlecolor)));
			}
			if (chartsubtitle != null)
			{
				PieCh.setSubtitle(vt.parseString(chartsubtitle),
					(chartsubtitlefont==null?SIMSUN12:getFont(chartsubtitlefont)),
					getColor((chartsubtitlecolor==null?null:chartsubtitlecolor)));
			}
			
			if (chartfootnote != null)
			{
				PieCh.setFootnote(vt.parseString(chartfootnote),
					(chartfootnotefont==null?SIMSUN12:getFont(chartfootnotefont)),
					getColor((chartfootnotecolor==null?null:chartfootnotecolor)));
			}

			PieCh.setChartGridDesc(gridline, gridstyle, gridcolor);

			if (chartorient != null && 
				getOrientation(chartorient) == HORIZONTAL)
				PieCh.setOrientation(HORIZONTAL);

			if (legendposition!=null && legendposition.trim().length()>0)
			{
				PieCh.setLegendPosition(getLegendPosition(legendposition));
			}

			if (chartxcolumn == null || crosstab.findColumn(chartxcolumn) == 0)
				chartxcolumn = crosstab.getColumnName(1);
			crosstab.quicksort(chartxcolumn);
			if (chartycolumn != null)
			{
				ycolumn = TextUtils.toStringArray(TextUtils.getWords(chartycolumn,"|"));
				ChartColumn chartcolumn=new ChartColumn(ycolumn, c_types,c_subtypes, c_subtypes2);
				ycolumn = chartcolumn.getColumns();
				c_types = chartcolumn.getTypes();
				c_subtypes = chartcolumn.getSubTypes();
				c_subtypes2= chartcolumn.getSubTypes2();
			}
			else
			{
				if (crosstab.getColumnCount()>1)
				{
					ycolumn = new String[crosstab.getColumnCount() - 1];
					for(i = 2; i <= crosstab.getColumnCount() ; i ++)
						ycolumn [i - 2] = crosstab.getColumnName(i);
				}
			}
			PieCh.setYMaxValue(chartymaxval, dfont, chartylabel);
			switch(axis_type)
			{
				case BAR:
				case LINE:
				case STACKBAR:
				case AREA:
					
					PieCh.setData(crosstab,chartxcolumn,ycolumn,
						getPropertyArray(ycolumn.length,1,c_types),
						getPropertyArray(ycolumn.length,2,c_subtypes),
						getPropertyArray(ycolumn.length,3,c_subtypes2),dfont,colorlist);
					PieCh.setY2Axis(y2_columns, charty2maxval, dfont);
					break;
				case STOCK:
					PieCh.setStockData(chartxcolumn,
						crosstab,
						(stock_open==null?crosstab.getColumnName(2):stock_open),
						(stock_high==null?crosstab.getColumnName(3):stock_high),
						(stock_low==null?crosstab.getColumnName(4):stock_low),
						(stock_close==null?crosstab.getColumnName(5):stock_close));
					break;
				case STOCK_OHLC:
					PieCh.setStockLine(
						chartxcolumn,
						crosstab,
						(stock_open==null?crosstab.getColumnName(2):stock_open),
						(stock_high==null?crosstab.getColumnName(3):stock_high),
						(stock_low==null?crosstab.getColumnName(4):stock_low),
						(stock_close==null?crosstab.getColumnName(5):stock_close));
					break;
				case STOCK_HLC:
					PieCh.setStockLine(chartxcolumn,
						crosstab,
						(stock_high==null?crosstab.getColumnName(2):stock_high),
						(stock_low==null?crosstab.getColumnName(3):stock_low),
						(stock_close==null?crosstab.getColumnName(4):stock_close));
					break;
				case WebChart.DOT:
					PieCh.setData(crosstab,chartxcolumn,ycolumn,axis_type,
						getChartSubType2(
						(chartsubtype2==null?null:chartsubtype2)),
						getChartSubType(axis_type,
						(chartsubtype==null?null:chartsubtype)),dfont,colorlist);
					break;					
				default:
					PieCh.setData(crosstab,chartxcolumn,ycolumn,axis_type,dfont,colorlist);
			}

			if (subchartcolumn != null && crosstab.findColumn(subchartcolumn) > 0)
			{
				try {
					if (subchartheight != null)
						sub_height = Double.valueOf(subchartheight).doubleValue();
				} catch (java.lang.NumberFormatException nfe) 
				{
					sub_height=0.5;
				}
				int sub_type = WebChart.BAR;
				if (subcharttype != null)
				{
	 				if ( subcharttype.equalsIgnoreCase("BAR"))
	 					sub_type = WebChart.BAR;
	 				else if (subcharttype.equalsIgnoreCase("LINE"))
	 					sub_type = WebChart.LINE;
 					else if (subcharttype.equalsIgnoreCase("DOT"))
 						sub_type = WebChart.DOT;
	 				else if (subcharttype.equalsIgnoreCase("AREA"))
	 					sub_type = WebChart.AREA;
				}

				java.awt.Color random_color = getChartSeriesColor(ycolumn.length, colorlist);
				PieCh.setSubChart(crosstab.getColumnLabel(subchartcolumn),crosstab,chartxcolumn,subchartcolumn,
					sub_height,sub_type,
					getChartSubType(sub_type,
						(subchartsubtype==null?null:subchartsubtype)),
					getChartSubType2(
						(subchartsubtype2==null?null:subchartsubtype2)),
					random_color,dfont);
				PieCh.setY2MaxValue(crosstab.getColumnLabel(subchartcolumn), subchartymaxval,dfont);
			}
				
	 		chartfile = PieCh.saveGifFile(crosstab.getRowCount() < MAX_IMAGE_CSS_ITEMS, vt.getInt("WEBCHART.KEEP_CACHE_TIME",300));
			fileNameChart = chartfile;

	 		String fields[]= {};
			if (subchartcolumn != null && crosstab.findColumn(subchartcolumn) > 0)
			{
				fields = new String[ycolumn.length + 1];
				for(i=0;i<ycolumn.length;i++)
					fields[ i ] =  ycolumn[i];
				fields[fields.length - 1] = subchartcolumn;
 			}
			else
			{
				if (ycolumn.length > 0)
					fields = ycolumn;
			}

			if (tooltipcolumn == null)
			{
				tooltipcolumn = chartxcolumn;
			}
			if (chartimagemap == null || chartimagemap.equalsIgnoreCase("YES"))
			{
				if (crosstab.getRowCount() < MAX_IMAGE_CSS_ITEMS)
				{
	 				mapcss = PieCh.getImageMapXML(fileNameChart,
							crosstab,tooltipcolumn,	fields, charthreftarget,vt);
				}
 		        }
	 	}
		out.write("\t<image>\n");
		out.write("\t\t<file>");
		if (getVariableTableValue(vt,"CACHE",index,true) == null)
			out.write("showimage"+fileextention+"?id="+fileNameChart+"&amp;del=yes");
		else
			out.write("showimage"+fileextention+"?id="+fileNameChart);
		out.write("</file>\n");
		if (chartimagemap == null || chartimagemap.equalsIgnoreCase("YES")) 
		{
			if (crosstab.getRowCount() < MAX_IMAGE_CSS_ITEMS)
				out.write(mapcss);
		}
		out.write("\t</image>\n");
		/*
		crosstab.writeXMLBody(out,xmltag,xmlattr,grpcolcount,vt);
		*/
		out.flush();
  	}

	private static void generateChart(DBRowCache crosstab,java.io.OutputStream out,VariableTable vt,String index, String fileextention)
		throws java.io.IOException 
    	{
    		int i;
    		String mapcss="";
 		int width=500,height=400,grpcolcount=2;

 		String fileNameChart="";

    		String chartdefaultfont = getVariableTableValue(vt,"defaultfont");
    		String chartdefaultcolor = getVariableTableValue(vt,"defaultcolor");
    		
		String colors = getVariableTableValue(vt,"colors", index, true);
		
		String xmltag = getVariableTableValue(vt,"xmltag", index, true);
		String xmlattr = getVariableTableValue(vt,"xmlattr", index, true);

   		String legendposition    = getVariableTableValue(vt,"legend",index,true);
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
    		
    		String chartwidth = getVariableTableValue(vt,"width",index,true);
    		String chartheight = getVariableTableValue(vt,"height",index,true);

    		String charttitle = vt.parseString(getVariableTableValue(vt,"title",index,false));
    		String charttitlefont = getVariableTableValue(vt,"titlefont",index,true);
    		String charttitlecolor = getVariableTableValue(vt,"titlecolor",index,true);

    		String chartsubtitle = vt.parseString(getVariableTableValue(vt,"subtitle",index,false));
    		String chartsubtitlefont = getVariableTableValue(vt,"subtitlefont",index,true);
    		String chartsubtitlecolor = getVariableTableValue(vt,"subtitlecolor",index,true);

    		String chartfootnote = vt.parseString(getVariableTableValue(vt,"footnote",index,false));
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


   		String pielabelstyle    = getVariableTableValue(vt,"PIELABEL",index,true);
   		String pielabeldigit    = getVariableTableValue(vt,"PIEDIGIT",index,true);
   		
   		String tooltipcolumn    = getVariableTableValue(vt,"TOOLTIP",index,true);
   		String chartxcolumn    = vt.parseString(getVariableTableValue(vt,"XCOL",index,true));
   		String chartxlabel    = getVariableTableValue(vt,"XLABEL",index,true);
   		String chartycolumn    = vt.parseString(getVariableTableValue(vt,"YCOL",index,true));
   		String chartylabel    = getVariableTableValue(vt,"YLABEL",index,true);
   		String chartymaxval    = getVariableTableValue(vt,"YMAX",index,true);
   		String charty2column    = vt.parseString(getVariableTableValue(vt,"Y2COL",index,true));
   		String charty2maxval    = getVariableTableValue(vt,"Y2MAX",index,true);

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

		double sub_height = 0.5;
		String ycolumn[] = {};
                String c_types[] = {};
                String c_subtypes[] = {};
                String c_subtypes2[] = {};
		String y2_columns[] = {};
		java.awt.Color colorlist[] = getColorList(colors);

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
		if (charty2column != null)
                   y2_columns = TextUtils.toStringArray(TextUtils.getWords(charty2column,"|"));

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
		width = (width < 100?100:width);
		width = (width > 1000?1000:width);
		height = (height < 80?80:height);
		height = (height > 800?800:height);

		if ( crosstab.getColumnCount() == 0)
		{
			saveErrorImage(out, width,height,"Query execute error!");
			out.flush();
			return;
		}
		if ( crosstab.getRowCount() == 0)
		{
			saveErrorImage(out, width,height,"Query execute error!");
			out.flush();
			return;
		}

		java.awt.Font  dfont =  getFont(chartdefaultfont);
		java.awt.Color defaultcolor = getColor(chartdefaultcolor);
		
		String chartfile = null;

		if (charttype.equalsIgnoreCase("PIE"))
		{
 			WebChart.PieChart PieCh = new WebChart.PieChart();

			if (dfont != null)
			{
				PieCh.setLegendFont(dfont);
				PieCh.setFont(dfont);
			}
			else
			{
				PieCh.setLegendFont(SIMSUN12);
				PieCh.setFont(SIMSUN12);
			}			

			if (defaultcolor != null)
			{
				PieCh.setForeground(defaultcolor);
			}
			
			PieCh.setSize(width,height);

			if (backcolor != null && getColor(backcolor) != null)
				PieCh.setBackground(getColor(backcolor.toString()));
			else
				PieCh.setBackground(new java.awt.Color(236,236,236));
			if (edgecolor != null && getColor(edgecolor) != null)
				PieCh.setEdgeColor(new java.awt.Color(236,236,236));
			else
				PieCh.setEdgeColor(java.awt.Color.GRAY);

			if (charttitle != null)
			{
				PieCh.setTitle(vt.parseString(charttitle),
					(charttitlefont==null?SIMHEI18:getFont(charttitlefont)),
					getColor((charttitlecolor==null?null:charttitlecolor)));					
			}
			if (chartsubtitle != null)
			{
				PieCh.setSubtitle(vt.parseString(chartsubtitle),
					(chartsubtitlefont==null?SIMSUN12:getFont(chartsubtitlefont)),
					getColor((chartsubtitlecolor==null?null:chartsubtitlecolor)));
			}
			
			if (chartfootnote != null)
			{
				PieCh.setFootnote(vt.parseString(chartfootnote),
					(chartfootnotefont==null?SIMSUN12:getFont(chartfootnotefont)),
					getColor((chartfootnotecolor==null?null:chartfootnotecolor)));
			}
			if (legendposition!=null && legendposition.trim().length()>0)
			{
				PieCh.setLegendPosition(getLegendPosition(legendposition));
			}

			int labeldigit = 2;
			try {
				if (pielabeldigit != null)
					labeldigit = Integer.valueOf(pielabeldigit).intValue();
			}
		 	catch (java.lang.NumberFormatException nfe)
			{
				labeldigit = 2;
			}
			
			PieCh.setLabelStyle(
				getPieLabelStyle(pielabelstyle==null?null:pielabelstyle),labeldigit);

			PieCh.setPieStyle(getChartSubType(-9999,
				(chartsubtype==null?null:chartsubtype)));
			if (chartxcolumn == null || crosstab.findColumn(chartxcolumn) == 0)
				chartxcolumn = crosstab.getColumnName(1);
			if (chartycolumn == null || crosstab.findColumn(chartycolumn) == 0)
				chartycolumn = crosstab.getColumnName(2);				

			PieCh.setData(crosstab,chartxcolumn,chartycolumn,colorlist);

 			PieCh.saveGifFile(out, false);
		}
 		else
 		{
 			int axis_type = WebChart.BAR;

 			if ( charttype.equalsIgnoreCase("BAR"))
 				axis_type = WebChart.BAR;
	 		else if (charttype.equalsIgnoreCase("LINE"))
	 			axis_type = WebChart.LINE;
	 		else if (charttype.equalsIgnoreCase("DOT"))
	 			axis_type = WebChart.DOT;
	 		else if (charttype.equalsIgnoreCase("AREA"))
	 			axis_type = WebChart.AREA;
	 		else if (charttype.equalsIgnoreCase("STACKBAR"))
	 			axis_type = WebChart.STACKBAR;
	 		else if (charttype.equalsIgnoreCase("STOCK"))
	 			axis_type = WebChart.STOCK;
	 		else if (charttype.equalsIgnoreCase("STOCK_HLC"))
	 			axis_type = WebChart.STOCK_HLC;
	 		else if (charttype.equalsIgnoreCase("STOCK_OHLC"))
	 			axis_type = WebChart.STOCK_OHLC;

	 		WebChart.AxisChart PieCh = new WebChart.AxisChart();

			try {
				oracle.charts.types.AxisDesc axisdesc = new oracle.charts.types.AxisDesc();
				if (dfont != null)
				{
					PieCh.setLegendFont(dfont);
					axisdesc.setFont(dfont);
				}
				else
				{
					PieCh.setLegendFont(SIMSUN12);
					axisdesc.setFont(SIMSUN12);
				}
				if (defaultcolor != null)
				{
					PieCh.setForeground(defaultcolor);
					axisdesc.setColor(defaultcolor);
				}
				if (chartxlabel != null && chartxlabel.equalsIgnoreCase("OFF"))
				    axisdesc.setDrawLabelOff();
				PieCh.setChartAttributes(PieCh.XYAXIS,axisdesc);
			}catch(oracle.charts.types.ChartException ce){}

			PieCh.setSize(width,height);

			if (backcolor != null && getColor(backcolor) != null)
				PieCh.setBackground(getColor(backcolor.toString()));
			else
				PieCh.setBackground(new java.awt.Color(236,236,236));

			if (edgecolor != null && getColor(edgecolor) != null)
				PieCh.setEdgeColor(getColor(edgecolor));
			else
				PieCh.setEdgeColor(java.awt.Color.GRAY);

			if (plotbackcolor != null && getColor(plotbackcolor) != null)
				PieCh.setPlotBackground(getColor(plotbackcolor));
			else
				PieCh.setPlotBackground(java.awt.Color.WHITE);

			if (plotedgecolor != null && getColor(plotedgecolor) != null)
				PieCh.setPlotEdgeColor(getColor(plotedgecolor));
			else
				PieCh.setPlotEdgeColor(java.awt.Color.GRAY);

			if (charttitle != null)
			{
				PieCh.setTitle(vt.parseString(charttitle),
					(charttitlefont==null?SIMHEI18:getFont(charttitlefont)),
					getColor((charttitlecolor==null?null:charttitlecolor)));
			}
			if (chartsubtitle != null)
			{
				PieCh.setSubtitle(vt.parseString(chartsubtitle),
					(chartsubtitlefont==null?SIMSUN12:getFont(chartsubtitlefont)),
					getColor((chartsubtitlecolor==null?null:chartsubtitlecolor)));
			}
			
			if (chartfootnote != null)
			{
				PieCh.setFootnote(vt.parseString(chartfootnote),
					(chartfootnotefont==null?SIMSUN12:getFont(chartfootnotefont)),
					getColor((chartfootnotecolor==null?null:chartfootnotecolor)));
			}

			PieCh.setChartGridDesc(gridline, gridstyle, gridcolor);

			if (chartorient != null && 
				getOrientation(chartorient) == HORIZONTAL)
				PieCh.setOrientation(HORIZONTAL);

			if (legendposition!=null && legendposition.trim().length()>0)
			{
				PieCh.setLegendPosition(getLegendPosition(legendposition));
			}

			if (chartxcolumn == null || crosstab.findColumn(chartxcolumn) == 0)
				chartxcolumn = crosstab.getColumnName(1);
			crosstab.quicksort(chartxcolumn);
			if (chartycolumn != null)
			{
				ycolumn = TextUtils.toStringArray(TextUtils.getWords(chartycolumn,"|"));
				ChartColumn chartcolumn=new ChartColumn(ycolumn, c_types,c_subtypes, c_subtypes2);
				ycolumn = chartcolumn.getColumns();
				c_types = chartcolumn.getTypes();
				c_subtypes = chartcolumn.getSubTypes();
				c_subtypes2= chartcolumn.getSubTypes2();
			}
			else
			{
				if (crosstab.getColumnCount()>1)
				{
					ycolumn = new String[crosstab.getColumnCount() - 1];
					for(i = 2; i <= crosstab.getColumnCount() ; i ++)
						ycolumn [i - 2] = crosstab.getColumnName(i);
				}
			}
			PieCh.setYMaxValue(chartymaxval, dfont, chartylabel);
			switch(axis_type)
			{
				case BAR:
				case LINE:
				case STACKBAR:
				case AREA:
					
					PieCh.setData(crosstab,chartxcolumn,ycolumn,
						getPropertyArray(ycolumn.length,1,c_types),
						getPropertyArray(ycolumn.length,2,c_subtypes),
						getPropertyArray(ycolumn.length,3,c_subtypes2),dfont,colorlist);
					PieCh.setY2Axis(y2_columns, charty2maxval, dfont);
					break;
				case STOCK:
					PieCh.setStockData(chartxcolumn,
						crosstab,
						(stock_open==null?crosstab.getColumnName(2):stock_open),
						(stock_high==null?crosstab.getColumnName(3):stock_high),
						(stock_low==null?crosstab.getColumnName(4):stock_low),
						(stock_close==null?crosstab.getColumnName(5):stock_close));
					break;
				case STOCK_OHLC:
					PieCh.setStockLine(
						chartxcolumn,
						crosstab,
						(stock_open==null?crosstab.getColumnName(2):stock_open),
						(stock_high==null?crosstab.getColumnName(3):stock_high),
						(stock_low==null?crosstab.getColumnName(4):stock_low),
						(stock_close==null?crosstab.getColumnName(5):stock_close));
					break;
				case STOCK_HLC:
					PieCh.setStockLine(chartxcolumn,
						crosstab,
						(stock_high==null?crosstab.getColumnName(2):stock_high),
						(stock_low==null?crosstab.getColumnName(3):stock_low),
						(stock_close==null?crosstab.getColumnName(4):stock_close));
					break;
				case WebChart.DOT:
					PieCh.setData(crosstab,chartxcolumn,ycolumn,axis_type,
						getChartSubType2(
						(chartsubtype2==null?null:chartsubtype2)),
						getChartSubType(axis_type,
						(chartsubtype==null?null:chartsubtype)),dfont,colorlist);
					break;					
				default:
					PieCh.setData(crosstab,chartxcolumn,ycolumn,axis_type,dfont,colorlist);
			}

			if (subchartcolumn != null && crosstab.findColumn(subchartcolumn) > 0)
			{
				try {
					if (subchartheight != null)
						sub_height = Double.valueOf(subchartheight).doubleValue();
				} catch (java.lang.NumberFormatException nfe) 
				{
					sub_height=0.5;
				}
				int sub_type = WebChart.BAR;
				if (subcharttype != null)
				{
	 				if ( subcharttype.equalsIgnoreCase("BAR"))
	 					sub_type = WebChart.BAR;
	 				else if (subcharttype.equalsIgnoreCase("LINE"))
	 					sub_type = WebChart.LINE;
 					else if (subcharttype.equalsIgnoreCase("DOT"))
 						sub_type = WebChart.DOT;
	 				else if (subcharttype.equalsIgnoreCase("AREA"))
	 					sub_type = WebChart.AREA;
				}

				java.awt.Color random_color = getChartSeriesColor(ycolumn.length, colorlist);
				PieCh.setSubChart(crosstab.getColumnLabel(subchartcolumn),crosstab,chartxcolumn,subchartcolumn,
					sub_height,sub_type,
					getChartSubType(sub_type,
						(subchartsubtype==null?null:subchartsubtype)),
					getChartSubType2(
						(subchartsubtype2==null?null:subchartsubtype2)),
					random_color,dfont);
				PieCh.setY2MaxValue(crosstab.getColumnLabel(subchartcolumn), subchartymaxval,dfont);
			}
				
	 		PieCh.saveGifFile(out, false);
	 	}
		out.flush();
  	}

	public static class PieChart extends oracle.charts.piechart.PieChart
	{
		public void setTitle(String title)
		{
			getTitle().setText(title);
			getTitle().setFont(WebChart.SIMHEI18);
		}
		public void setTitle(String title,java.awt.Font sfont)
		{
			getTitle().setText(title);
			if (sfont != null)
				getTitle().setFont(sfont);
		}
		public void setTitle(String title,java.awt.Font sfont,java.awt.Color scolor)
		{
			getTitle().setText(title);
			if (sfont != null)
				getTitle().setFont(sfont);
			if (scolor != null)
				getTitle().setForeground(scolor);
		}
		public void setSubtitle(String subtitle)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getSubtitle().setText(lines);
				getSubtitle().setFont(WebChart.SIMSUN12);
			}
		}
		public void setSubtitle(String subtitle,java.awt.Font sfont)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getSubtitle().setText(lines);
				if (sfont != null)
					getSubtitle().setFont(sfont);
			}
		}
		public void setSubtitle(String subtitle,java.awt.Font sfont,java.awt.Color scolor)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getSubtitle().setText(lines);
				if (sfont != null)
					getSubtitle().setFont(sfont);
				if (scolor != null)
					getSubtitle().setForeground(scolor);
			}
		}
		public void setFootnote(String subtitle)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getFootnote().setText(lines);
				getFootnote().setFont(WebChart.SIMSUN12);
			}
		}
		public void setFootnote(String subtitle,java.awt.Font sfont)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getFootnote().setText(lines);
				if (sfont != null)
					getFootnote().setFont(sfont);
			}
		}
		public void setFootnote(String subtitle,java.awt.Font sfont,java.awt.Color scolor)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getFootnote().setText(lines);
				if (sfont != null)
					getFootnote().setFont(sfont);
				if (scolor != null)
					getFootnote().setForeground(scolor);
			}
		}
		public void setLabelStyle(boolean disp_pct,int point_digit)
		{
			try {
				if (point_digit > 0)
				{
					if (disp_pct)
					{
						java.text.NumberFormat nf = java.text.NumberFormat.getPercentInstance();
						nf.setMaximumFractionDigits(point_digit);
						setDeriveLabelPct(nf);
					}
					else
					{
						java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
						nf.setMaximumFractionDigits(point_digit);
						setDeriveLabelVal(nf);
					}
				}
				else
				{
					if (disp_pct)
						setDeriveLabelPct();
					else
						setDeriveLabelVal();
				}
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setPieStyle(int style)
		{
			try {
				oracle.charts.types.PieStyleDesc piestyle = new oracle.charts.types.PieStyleDesc();
				piestyle.setStyleType(style);
				setChartAttributes(piestyle);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}

		public void setChartGridDesc(String gridline, String gridstyle, String gridcolor)
		{

		}

		private oracle.charts.types.PieSliceDesc[] getPieData(DBRowCache data,String d_col,String v_col, java.awt.Color colorlist[])
		{
			int row,col_d,col_v;
			double d_val;
			oracle.charts.types.PieSliceDesc chart_pies	[] = {};
			col_d = data.findColumn(d_col);
			col_v = data.findColumn(v_col);
			if ( data.getRowCount()==0 ||
				col_d==0 || col_v==0)
				return chart_pies;
			chart_pies = new oracle.charts.types.PieSliceDesc[data.getRowCount()];
			for(row = 1;row <= data.getRowCount();row ++)
			{
				if (data.getItem(row,col_d) == null)
					continue;
				try {
					if (data.getItem(row,col_v) != null)
						d_val = Double.valueOf(data.getItem(row,col_v).toString()).doubleValue();
					else
						d_val = 0.0;
				}
				 catch (java.lang.NumberFormatException nfe)
				{
					d_val = 0.0;
				}
				chart_pies[row-1] = new oracle.charts.types.PieSliceDesc(
					data.getItem(row,col_d).toString(),d_val);
				chart_pies[row-1].setBackground(getChartSeriesColor(row-1,colorlist));
				/*
				if (row - 1 >= COLORLIST.length)
					chart_pies[row-1].setBackground(new java.awt.Color(
						(int)(255 * Math.random()),(int)(255 * Math.random()),(int)(255 * Math.random())));
				else
					chart_pies[row-1].setBackground(COLORLIST[row - 1]);
				*/
			}
			return chart_pies;
		}

		public void setData(DBRowCache data,String d_col,String v_col, java.awt.Color colorlist[])
		{
			try {
				setSeries(getPieData(data,d_col,v_col,colorlist));
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}

		public void setData(DBRowCache data, java.awt.Color colorlist[])
		{
			try {
				setSeries(getPieData(data,data.getColumnName(1),data.getColumnName(2), colorlist));
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}

		public void setLegendPosition( int pos)
		{
			try {
				setLegendAlignment(pos);
			} catch ( oracle.charts.types.ChartException chart_ex){}
		}

		private java.awt.image.BufferedImage getBufferedImage(boolean imgcss)
		{
		       BufferedImage bi = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_BGR);
		       Graphics2D g2 = null;
		       g2 = bi.createGraphics();
		       try {
				if (imgcss) setEnableImageMapSS(new oracle.charts.types.ImageMapDesc());
				drawBuffer(g2);
				/*
			        g2.setFont(SIMSUN10);
			        g2.setColor(java.awt.Color.GRAY);
			        g2.drawString("AnySQL.net",getWidth() - 70,getHeight() - 4);
				*/
		       }
			catch(oracle.charts.types.ChartException ch_ex) {}
		       return bi;
		}

		public String saveGifFile(boolean imgcss,int keep_time) throws java.io.IOException
		{
			java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
			WebChart.saveGifFile(getBufferedImage(imgcss),out);
			out.close();
			return ImageCache.putContent(out.toByteArray(), keep_time + 10);
		}

		public void saveGifFile(java.io.OutputStream out, boolean imgcss) throws java.io.IOException
		{
			WebChart.saveGifFile(getBufferedImage(imgcss),out);
		}

		private String getImageMapXML(String mapname,DBRowCache data,
			String col,String tcols,String target,VariableTable vt)
		{
			int row;
			StringBuffer map_body = new StringBuffer();
			String lines[];
			Object arg;
			java.util.Vector words = new java.util.Vector();
			map_body.append("\t\t<image_map name=\""+mapname+"\">\n");
			try {
				lines = WebChart.getLines(String.valueOf(getImageMapSS()));
				if (lines.length>2)
				{
					for (row = 2;row<lines.length;row ++)
					{
						map_body.append("\t\t\t<mapitem col=\""+tcols+"\" row=\""+(row-1)+"\">\n");
						words = TextUtils.getWords(lines[row]," ");
						if (words.size()>0)
						{
							map_body.append("\t\t\t\t<title><![CDATA[");
							arg = data.getItem(row - 1,col);
							if (arg != null)
								map_body.append(arg.toString());
							else
								map_body.append("NULL");
							map_body.append("=");
							arg = data.getItem(row - 1,tcols);
							if (arg != null)
								map_body.append(arg.toString());
							else
								map_body.append("NULL");
							map_body.append("]]></title>\n");
							map_body.append("\t\t\t\t<shape><![CDATA[");
							map_body.append(words.elementAt(0).toString());
							map_body.append("]]></shape>\n");
							map_body.append("\t\t\t\t<coords><![CDATA[");
						        map_body.append(TextUtils.getWords(words.elementAt(1).toString(),"\t").elementAt(0).toString());
							map_body.append("]]></coords>\n");
							if (data.getColumnMemo(tcols) == null)
							{
								map_body.append("\t\t\t\t<href><![CDATA[#myanchor]]></href>\n");
							}
							else
							{
								map_body.append("\t\t\t\t<href><![CDATA["+data.parseString(data.getColumnMemo(tcols),vt,row - 1,1)+"]]></href>\n");
							}
							if (target != null && target.trim().length() > 0)
							{
								map_body.append("\t\t\t\t<target><![CDATA[");
								map_body.append(target);
								map_body.append("]]></target>\n");
							}
						}
						map_body.append("\t\t\t</mapitem>\n");
					}
				}
			} catch(oracle.charts.types.ChartException ch_ex) {}
			map_body.append("\t\t</image_map>\n");
			return map_body.toString();
		}

	}


    public static String getURLString(String url)
    {
	char hex_arr[] = {
		'0','1','2','3','4','5','6','7',
		'8','9','a','b','c','d','e','f'};

	if (url == null || url.length() == 0) return url;

        byte content[] = url.getBytes();

        StringBuffer result = new StringBuffer(content.length * 3);

        for (int i = 0; i < content.length; i++)
	{
		if ((content[i] >= '0' && content[i] <= '9') ||
		    (content[i] >= 'A' && content[i] <= 'Z') ||
		    (content[i] >= 'a' && content[i] <= 'z'))
		    result.append((char) content[i]);
		else
		{
			result.append('%');
			result.append((char)hex_arr[((content[i]+256)%256)/16]);
			result.append((char)hex_arr[((content[i]+256)%256)%16]);
		}
        }
        return (result.toString());
    }

	public static class AxisChart extends oracle.charts.axischart.AxisChart
	{
		public void setTitle(String title)
		{
			getTitle().setText(title);
			getTitle().setFont(WebChart.SIMHEI18);
		}
		public void setTitle(String title,java.awt.Font sfont)
		{
			getTitle().setText(title);
			if (sfont != null)
				getTitle().setFont(sfont);
		}
		public void setTitle(String title,java.awt.Font sfont,java.awt.Color scolor)
		{
			getTitle().setText(title);
			if (sfont != null)
				getTitle().setFont(sfont);
			if (scolor != null)
				getTitle().setForeground(scolor);
		}
		public void setSubtitle(String subtitle)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getSubtitle().setText(lines);
				getSubtitle().setFont(WebChart.SIMSUN12);
			}
		}
		public void setSubtitle(String subtitle,java.awt.Font sfont)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getSubtitle().setText(lines);
				if (sfont != null)
					getSubtitle().setFont(sfont);
			}
		}
		public void setSubtitle(String subtitle,java.awt.Font sfont,java.awt.Color scolor)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getSubtitle().setText(lines);
				if (sfont != null)
					getSubtitle().setFont(sfont);
				if (scolor != null)
					getSubtitle().setForeground(scolor);
			}
		}

		public void setChartGridDesc(String gridline, String gridstyle, String gridcolor)
		{
			try {
			oracle.charts.types.GridDesc xgriddesc = getGridDesc(AxisChart.XAXIS);
			oracle.charts.types.GridDesc ygriddesc = getGridDesc(AxisChart.YAXIS);

			int line = 3;
			if (gridline != null)	line = getAxisGridType(gridline);
			if (line == 0)
			{
			    xgriddesc.setDrawGridOff();
			    ygriddesc.setDrawGridOff();
			}
			else if (line == 1)
			{
			    ygriddesc.setDrawGridOff();
			}
			else if (line == 2)
			{
			    xgriddesc.setDrawGridOff();
			}
			if (gridstyle != null)
			{
			    xgriddesc.setGridStyle(getChartSubType(LINE,gridstyle));
			    ygriddesc.setGridStyle(getChartSubType(LINE,gridstyle));
			}
			if (gridcolor != null)
			{
			    xgriddesc.setColor(getColor(gridcolor));
			    ygriddesc.setColor(getColor(gridcolor));
			}
			setChartAttributes(AxisChart.XAXIS, xgriddesc);
			setChartAttributes(AxisChart.YAXIS, ygriddesc);
		} catch (oracle.charts.types.ChartException ce) {}
		}

		public void setFootnote(String subtitle)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getFootnote().setText(lines);
				getFootnote().setFont(WebChart.SIMSUN12);
			}
		}
		public void setFootnote(String subtitle,java.awt.Font sfont)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getFootnote().setText(lines);
				if (sfont != null)
					getFootnote().setFont(sfont);
			}
		}
		public void setFootnote(String subtitle,java.awt.Font sfont,java.awt.Color scolor)
		{
			String lines[] = WebChart.getLines(subtitle);
			if (lines.length>0)
			{
				getFootnote().setText(lines);
				if (sfont != null)
					getFootnote().setFont(sfont);
				if (scolor != null)
					getFootnote().setForeground(scolor);
			}
		}

		private static String[] getStringArray(DBRowCache data,String col)
		{
			String col_val[] = {};
			if (data == null) return col_val;
			int row,col_index = data.findColumn(col);
			if (data.getRowCount()==0 || col_index == 0)
				return col_val;
			col_val = new String[data.getRowCount()];
			for(row = 1;row<=data.getRowCount();row++)
				col_val[row - 1] = data.getItem(row,col_index).toString();
			return col_val;
		}

		private static java.util.Date[] getDateArray(DBRowCache data,String col)
		{
			java.util.Date col_val[] = {};
			if (data == null) return col_val;
			int row,col_index = data.findColumn(col);
			if (data.getRowCount()==0 || col_index == 0)
				return col_val;
			col_val = new java.util.Date[data.getRowCount()];
			for(row = 1;row<=data.getRowCount();row++)
				col_val[row - 1] = (java.util.Date)(data.getItem(row,col_index));
			return col_val;
		}

		private static double[] getDoubleArray(DBRowCache data,String col)
		{
			double col_val[] = {};
			if (data == null) return col_val;
			int row,col_index = data.findColumn(col);
			if (data.getRowCount()==0 || col_index == 0)
				return col_val;
			col_val = new double[data.getRowCount()];
			for(row = 1;row<=data.getRowCount();row++)
			{
				if (data.getItem(row,col_index) != null)
				{
					try {
						col_val[row - 1] = Double.valueOf(data.getItem(row,col_index).toString()).doubleValue();
					}
					 catch (java.lang.NumberFormatException nfe)
					{
						col_val[row - 1] = 0.0;
					}
				}
				else
					col_val[row - 1] = 0.0;
			}
			return col_val;
		}

		public void setY2Axis(String y2_cols[], String y2maxval, java.awt.Font font)
                {
			setY2MaxValue(null, y2maxval, font);
			for(int i=0; i< y2_cols.length; i++)
                        {
				try {
					setYSeriesAxis2(y2_cols[i]);
				} catch (oracle.charts.types.ChartException ex) {}
                        }
                }

		public void setYMaxValue(String ymaxval, java.awt.Font yfont, String chartylabel)
                {
                	double ymin=0, ymax=0, yinc=0;
                	String cols[] = {};
                	try {
                	    oracle.charts.types.NumAxisDesc yAxisD = new oracle.charts.types.NumAxisDesc();
                	    if (chartylabel != null && chartylabel.equalsIgnoreCase("OFF"))
                	       yAxisD.setDrawLabelOff();
			    if (yfont != null)
				 yAxisD.setFont(yfont);
			    else
 				 yAxisD.setFont(SIMSUN12);
                	    if (ymaxval != null)
                	    {
                		cols = TextUtils.toStringArray(TextUtils.getFields(ymaxval,"|"));
                		if (cols.length > 2)
                		{
                			try {
						if (cols[2] != null)
						{
                				    yinc = Double.valueOf(cols[2]).doubleValue();
                				    yAxisD.setLabelIncrement(yinc);
						}
                			} catch (java.lang.NumberFormatException nfe) {};
                		}
                		if (cols.length > 1)
                		{
                			try {
						if (cols[1] != null)
						{
                				   ymax = Double.valueOf(cols[1]).doubleValue();
                				   yAxisD.setExtentMax(ymax);
						}
                			} catch (java.lang.NumberFormatException nfe) {};
                		}                		
                		if (cols.length > 0)
                		{
                			try {
						if (cols[0] != null)
						{
                				    ymin = Double.valueOf(cols[0]).doubleValue();
                				    yAxisD.setExtentMin(ymin);
						}
                			} catch (java.lang.NumberFormatException nfe) {};
                		}                		
                	    }
               		    setChartAttributes(yAxisD);
                	}  catch ( oracle.charts.types.ChartException chart_ex){}
                }

		public void setY2MaxValue(String y2col, String ymaxval, java.awt.Font font)
                {
                	double ymin=0, ymax=0, yinc=0;
                	String cols[] = {};
                	try {
                	    oracle.charts.types.NumAxisDesc yAxisD = new oracle.charts.types.NumAxisDesc();
			    if (font != null)
				 yAxisD.setFont(font);
			    else
				 yAxisD.setFont(SIMSUN12);
                	    if (ymaxval != null)
                	    {
                		cols = TextUtils.toStringArray(TextUtils.getFields(ymaxval,"|"));
                		if (cols.length > 2)
                		{
                			try {
						if (cols[2] != null)
						{
                				    yinc = Double.valueOf(cols[2]).doubleValue();
                				    yAxisD.setLabelIncrement(yinc);
						}
                			} catch (java.lang.NumberFormatException nfe) {};
                		}
                		if (cols.length > 1)
                		{
                			try {
						if (cols[1] != null)
						{
                				   ymax = Double.valueOf(cols[1]).doubleValue();
                				   yAxisD.setExtentMax(ymax);
						}
                			} catch (java.lang.NumberFormatException nfe) {};
                		}                		
                		if (cols.length > 0)
                		{
                			try {
						if (cols[0] != null)
						{
                				    ymin = Double.valueOf(cols[0]).doubleValue();
                				    yAxisD.setExtentMin(ymin);
						}
                			} catch (java.lang.NumberFormatException nfe) {};
                		}                		
                	    }
                	    /* yAxisD.setSecondAxis();  */
			    if (y2col != null)
            		        setChartAttributes(y2col, yAxisD);
			    else
			        setChartAttributes(yAxisD);
                	}  catch ( oracle.charts.types.ChartException chart_ex){}
                }

		public void setData(DBRowCache data,int type,java.awt.Font dfont,java.awt.Color colorlist[])
		{
			if (type ==WebChart.BAR || type == WebChart.STACKBAR)
			{
				setData(data,type,WebChart.BAR_3D,0,dfont,colorlist);
			}
			else if (type ==WebChart.LINE || type == WebChart.AREA)
			{
				setData(data,type,WebChart.LINE_DASHED,WebChart.MARKER_CIRCLE,dfont,colorlist);
			}
			else
				setData(data,type,WebChart.MARKER_CIRCLE,WebChart.MARKER_CIRCLE,dfont,colorlist);
		}

		public void setData(DBRowCache data,int type,int substyle,java.awt.Font dfont,java.awt.Color colorlist[])
		{
			setData(data,type,substyle,WebChart.MARKER_CIRCLE,dfont,colorlist);
		}

		public void setData(DBRowCache data,int type,int substyle,int substyle2,java.awt.Font dfont, java.awt.Color colorlist[])
		{
			int i=2;
			java.awt.Color random_color;
			setXData(data,data.getColumnName(1));
			for(i=2;i<=data.getColumnCount();i++)
			{
				random_color = getChartSeriesColor(i-2,colorlist);
				setYData(data.getColumnLabel(i),
					data,data.getColumnName(i),type,random_color);
				if (type ==WebChart.BAR || type == WebChart.STACKBAR)
				{
					setBarStyle(data.getColumnLabel(i),substyle,random_color,dfont);
				}
				else if (type ==WebChart.LINE )
				{
					setLineStyle(data.getColumnLabel(i),substyle,random_color,
						substyle2,random_color);
				}
				else if ( type == WebChart.AREA)
				{
					setAreaStyle(data.getColumnLabel(i),substyle,random_color,
						substyle2,random_color);
				}
				else if (type == WebChart.DOT)
				{
					setPointStyle(data.getColumnLabel(i),substyle,random_color);
				}
			}
		}

		public void setData(DBRowCache data,String xcolumn,String ycolumn[],int type,java.awt.Font dfont,java.awt.Color colorlist[])
		{
			if (type ==WebChart.BAR || type == WebChart.STACKBAR)
			{
				setData(data,xcolumn,ycolumn,type,WebChart.BAR_3D,0,dfont,colorlist);
			}
			else if (type ==WebChart.LINE || type == WebChart.AREA)
			{
				setData(data,xcolumn,ycolumn,type,WebChart.LINE_DASHED,WebChart.MARKER_CIRCLE,dfont,colorlist);
			}
			else
				setData(data,xcolumn,ycolumn,type,WebChart.MARKER_CIRCLE,WebChart.MARKER_CIRCLE,dfont,colorlist);
		}

		public void setData(DBRowCache data,String xcolumn,String ycolumn[],int type[],java.awt.Font dfont,java.awt.Color colorlist[])
		{
                        int substyle[] = new int [ycolumn.length];
                        int substyle2[] = new int [ycolumn.length];
                        for (int i=0; i< ycolumn.length; i++)
                        {
                            substyle[i] = WebChart.MARKER_CIRCLE;
                            substyle2[i] = WebChart.MARKER_CIRCLE;
                        } 
			setData(data,xcolumn,ycolumn,type,substyle,substyle2,dfont,colorlist);
		}

		public void setData(DBRowCache data,String xcolumn,String ycolumn[],int type,int substyle,java.awt.Font dfont,java.awt.Color colorlist[])
		{
			setData(data,xcolumn,ycolumn,type,substyle,WebChart.MARKER_CIRCLE,dfont,colorlist);
		}

		public void setData(DBRowCache data,String xcolumn,String ycolumn[],int type[],int substyle[],java.awt.Font dfont,java.awt.Color colorlist[])
		{
                        int substyle2[] = new int [ycolumn.length];
                        for (int i=0; i< ycolumn.length; i++)
                        {
                            substyle2[i] = WebChart.MARKER_CIRCLE;
                        } 
			setData(data,xcolumn,ycolumn,type,substyle,substyle2,dfont,colorlist);
		}

		public void setData(DBRowCache data,String xcolumn,String ycolumn[],int type,int substyle,int substyle2,java.awt.Font dfont,java.awt.Color colorlist[])
		{
			int i=2;
			java.awt.Color random_color;
			setXData(data,xcolumn);
			int xtype = data.getColumnType(xcolumn);
			for(i=0;i<ycolumn.length;i++)
			{
				random_color = getChartSeriesColor(i,colorlist);
				if (xtype == java.sql.Types.DATE || xtype == java.sql.Types.TIME || xtype == java.sql.Types.TIMESTAMP)
				{
					setSparseYData(data.getColumnLabel(ycolumn[i]),data,xcolumn,ycolumn[i],type,random_color);
				}
				else
				{
					setYData(data.getColumnLabel(ycolumn[i]),data,ycolumn[i],type,random_color);
				}
				if (type ==WebChart.BAR || type == WebChart.STACKBAR)
				{
					setBarStyle(data.getColumnLabel(ycolumn[i]),substyle,random_color,dfont);
				}
				else if (type ==WebChart.LINE )
				{
					setLineStyle(data.getColumnLabel(ycolumn[i]),substyle,random_color,
						substyle2,random_color);
				}
				else if ( type == WebChart.AREA)
				{
					setAreaStyle(data.getColumnLabel(ycolumn[i]),substyle,random_color,
						substyle2,random_color);
				}
				else if (type == WebChart.DOT)
				{
					setPointStyle(data.getColumnLabel(ycolumn[i]),substyle,random_color);
				}
			}
		}

		public void setData(DBRowCache data,String xcolumn,String ycolumn[],int type[],int substyle[],int substyle2[],java.awt.Font dfont,java.awt.Color colorlist[])
		{
			int i=2;
			java.awt.Color random_color;
			setXData(data,xcolumn);
			int xtype = data.getColumnType(xcolumn);
			for(i=0;i<ycolumn.length;i++)
			{
				random_color = getChartSeriesColor(i, colorlist);
				if (xtype == java.sql.Types.DATE || xtype == java.sql.Types.TIME || xtype == java.sql.Types.TIMESTAMP)
				{
					setSparseYData(data.getColumnLabel(ycolumn[i]),data,xcolumn,ycolumn[i],type[i],random_color);
				}
				else
				{
					setYData(data.getColumnLabel(ycolumn[i]),data,ycolumn[i],type[i],random_color);
				}
				if (type[i] ==WebChart.BAR || type[i] == WebChart.STACKBAR)
				{
					setBarStyle(data.getColumnLabel(ycolumn[i]),substyle[i],random_color,dfont);
				}
				else if (type[i] ==WebChart.LINE )
				{
					setLineStyle(data.getColumnLabel(ycolumn[i]),substyle[i],random_color,
						substyle2[i],random_color);
				}
				else if ( type[i] == WebChart.AREA)
				{
					setAreaStyle(data.getColumnLabel(ycolumn[i]),substyle[i],random_color,
						substyle2[i],random_color);
				}
				else if (type[i] == WebChart.DOT)
				{
					setPointStyle(data.getColumnLabel(ycolumn[i]),substyle[i],random_color);
				}
			}
		}

		public void setXData(DBRowCache data,String col)
		{
			int col_index = data.findColumn(col);
			if (col_index == 0) return;
			int type = data.getColumnType(col_index);
			try {
				if (type == java.sql.Types.DATE ||
					type == java.sql.Types.TIME ||
					type == java.sql.Types.TIMESTAMP)
					setXSeries(getDateArray(data,col));
				else
					setXSeries(getStringArray(data,col));
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}

		public void setYData(String label,DBRowCache data,String col,
			int type,java.awt.Color color)
		{
			int col_index = data.findColumn(col);
			if (col_index == 0) return;
			try {
				setYSeries(label,getDoubleArray(data,col));
				setSeriesGraphType(label, type);
				setSeriesColor(label,color);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}

		public void setSparseYData(String label,DBRowCache data,String xcol, String ycol, int type,java.awt.Color color)
		{
			int xcol_index = data.findColumn(xcol);
			int ycol_index = data.findColumn(ycol);
			int rows = 0;
			if (xcol_index == 0 || ycol_index == 0) return;
			java.util.Date x_arr[] = {};
			double y_arr[] = {};
			
			for(int i=1; i<=data.getRowCount(); i++)
			{
				if (data.getItem(i,ycol_index) != null) rows ++;
			}			
			if (rows == 0) return;
			x_arr = new java.util.Date[rows];
			y_arr = new double[rows];
			rows = 0;
			for(int i = 1; i <=data.getRowCount(); i++)
			{
				if (data.getItem(i, ycol_index) != null)
				{
				    x_arr[rows] = (java.util.Date)(data.getItem(i,xcol_index));
				    try {
					  y_arr[rows] = Double.valueOf(data.getItem(i,ycol_index).toString()).doubleValue();
				    }
				    catch (java.lang.NumberFormatException nfe)
				    {
					 y_arr[rows] = 0.0;
				    }	
				    rows ++;			    
				}
			}
			try {
				setYSeriesSparse(label,x_arr,y_arr);
				setSeriesGraphType(label, type);
				setSeriesColor(label,color);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}

		public void setSparseYData(String label,DBRowCache data,String xcol, String ycol)
		{
			int xcol_index = data.findColumn(xcol);
			int ycol_index = data.findColumn(ycol);
			int rows = 0;
			if (xcol_index == 0 || ycol_index == 0) return;
			java.util.Date x_arr[] = {};
			double y_arr[] = {};
			
			for(int i=1; i<=data.getRowCount(); i++)
			{
				if (data.getItem(i,ycol_index) != null) rows ++;
			}			
			if (rows == 0) return;
			x_arr = new java.util.Date[rows];
			y_arr = new double[rows];
			rows = 0;
			for(int i = 1; i <=data.getRowCount(); i++)
			{
				if (data.getItem(i, ycol_index) != null)
				{
				    x_arr[rows] = (java.util.Date)(data.getItem(i,xcol_index));
				    try {
					  y_arr[rows] = Double.valueOf(data.getItem(i,ycol_index).toString()).doubleValue();
				    }
				    catch (java.lang.NumberFormatException nfe)
				    {
					 y_arr[rows] = 0.0;
				    }	
				    rows ++;			    
				}
			}
			try {
				setYSeriesSparse(label,x_arr,y_arr);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}

		public void setYData(String label,DBRowCache data,String col,java.util.Date start_date,
			int type,java.awt.Color color)
		{
			int col_index = data.findColumn(col);
			if (col_index == 0) return;
			try {
				setYSeries(label,getDoubleArray(data,col),start_date);
				setSeriesGraphType(label, type);
				setSeriesColor(label,color);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setYData(String label,DBRowCache data,String col,int start_pos,
			int type,java.awt.Color color)
		{
			int col_index = data.findColumn(col);
			if (col_index == 0) return;
			try {
				setYSeries(label,getDoubleArray(data,col),start_pos);
				setSeriesGraphType(label, type);
				setSeriesColor(label,color);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setSeriesLabels(java.lang.String series,DBRowCache data,String lcol)
		{
			try {
				setSeriesPointLabels(series,getStringArray(data,lcol));
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setSeriesLabels(java.lang.String series)
		{
			try {
				setSeriesPointLabels(series);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setOrientation(int orient)
		{
			try {
				setChartOrientation(orient);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setBarStyle(String gLabel,int b_style,java.awt.Color b_color,java.awt.Font font)
		{
			try {
				oracle.charts.types.BarDesc markerDescSquare = new oracle.charts.types.BarDesc();
				//markerDescSquare.setDwellLabelFont(font);
				if (font != null)
				{
					markerDescSquare.setPointLabelFont(font);
				}
				markerDescSquare.setBarStyle(b_style);
				markerDescSquare.setBarColor(b_color);
				setSeriesGraphic(gLabel, markerDescSquare);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setPointStyle(String gLabel,int m_style,java.awt.Color m_color)
		{
			try {
				oracle.charts.types.LineDesc markerDescSquare = new oracle.charts.types.LineDesc();
				markerDescSquare.setMarkerType(m_style);
				markerDescSquare.setMarkerColor(m_color);
				setSeriesColor(gLabel, m_color);
				setSeriesGraphic(gLabel, markerDescSquare);
				setSeriesGraphType(gLabel, WebChart.DOT);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setLineStyle(String gLabel,int m_style,java.awt.Color m_color)
		{
			try {
				oracle.charts.types.LineDesc markerDescSquare = new oracle.charts.types.LineDesc();
				markerDescSquare.setLineWidth(1);
				markerDescSquare.setMarkerType(m_style);
				markerDescSquare.setMarkerColor(m_color);
				setSeriesGraphic(gLabel, markerDescSquare);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setLineStyle(String gLabel,int l_style,java.awt.Color l_color,int m_style,java.awt.Color m_color)
		{
			try {
				oracle.charts.types.LineDesc markerDescSquare = new oracle.charts.types.LineDesc();
				markerDescSquare.setMarkerType(m_style);
				markerDescSquare.setMarkerColor(m_color);
				markerDescSquare.setLineWidth(1);
				markerDescSquare.setLineColor(l_color);
				markerDescSquare.setLineStyle(l_style);
				setSeriesGraphic(gLabel, markerDescSquare);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setAreaStyle(String gLabel,int l_style,java.awt.Color l_color,int m_style,java.awt.Color m_color)
		{
			try {
				setLineStyle(gLabel,l_style,l_color,m_style,m_color);
				oracle.charts.types.AreaDesc markerDescSquare = new oracle.charts.types.AreaDesc();
				markerDescSquare.setLineWidth(1);
				markerDescSquare.setLineColor(l_color);
				markerDescSquare.setAreaColor(l_color);
				markerDescSquare.setAreaTransparency(0.5f);
				setSeriesGraphic(gLabel, markerDescSquare);
			}
			 catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setStockStyle(String gLabel,int width,java.awt.Color ucolor,java.awt.Color dcolor)
		{
			try {
				oracle.charts.types.CandlestickDesc desc= new oracle.charts.types.CandlestickDesc();
				desc.setRealBodyWidth(width);
				desc.setRealBodyColorUp(ucolor);
				desc.setRealBodyColorDown(dcolor);
				setSeriesGraphic(gLabel,desc);
			}
			catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setStockLineStyle(String gLabel,int width,int length,java.awt.Color ucolor)
		{
			try {
				oracle.charts.types.HiLoCloseDesc desc= new oracle.charts.types.HiLoCloseDesc();
				desc.setMarkerWidth(width);
				desc.setMarkerLength(length);
				desc.setMarkerColor(ucolor);
				setSeriesGraphic(gLabel,desc);
			}
			catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setStockLine(String glabel,DBRowCache data,String gHigh,String gLow,String gClose)
		{
			try {
				setXData(data,glabel);
				setHiLoCloseSeries(glabel,
					getDoubleArray(data,gHigh),
					getDoubleArray(data,gLow),
					getDoubleArray(data,gClose));
			} catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setStockLine(String glabel,DBRowCache data,String gOpen,String gHigh,String gLow,String gClose)
		{
			try {
				setXData(data,glabel);
				setOpenHiLoCloseSeries(glabel,
					getDoubleArray(data,gOpen),
					getDoubleArray(data,gHigh),
					getDoubleArray(data,gLow),
					getDoubleArray(data,gClose));
				setStockLineStyle(glabel,2,getWidth()*3/10/data.getRowCount(),java.awt.Color.red);
			} catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setStockData(String glabel,DBRowCache data,String gOpen,String gHigh,String gLow,String gClose)
		{
			try {
				setXData(data,glabel);
				setCandlestickSeries(glabel,
					getDoubleArray(data,gOpen),
					getDoubleArray(data,gHigh),
					getDoubleArray(data,gLow),
					getDoubleArray(data,gClose));
				setStockStyle(glabel,getWidth()*3/5/data.getRowCount(),java.awt.Color.red,java.awt.Color.green);
			} catch ( oracle.charts.types.ChartException chart_ex){}
		}
		public void setSubChart(String gLabel,DBRowCache data,String xcol, String gCol,
			double height,int type,int substyle,int substyle2,java.awt.Color color,java.awt.Font font)
		{
			try {
				super.setSubChart(gLabel);
				setSparseYData(gLabel,data, xcol, gCol);
				setSubChartHeightFraction(gLabel, height);
				setSeriesGraphType(gLabel, type);
				setSeriesColor(gLabel,color);

				if (type ==WebChart.BAR || type == WebChart.STACKBAR)
				{
					setBarStyle(gLabel,substyle,color,font);
				}
				else if (type ==WebChart.LINE )
				{
					setLineStyle(gLabel,substyle,color,substyle2,color);
				}
				else if (type ==WebChart.AREA )
				{
					setAreaStyle(gLabel,substyle,color,substyle2,color);
				}
				else if (type == WebChart.DOT)
				{
					setPointStyle(gLabel,substyle,color);
				}
				if (data.getRowCount() < MAX_IMAGE_CSS_ITEMS)
					setEnableSubChartImageMapSS(gLabel,new oracle.charts.types.ImageMapDesc()); 
			} catch ( oracle.charts.types.ChartException chart_ex){}

		}
		public void setLegendPosition( int pos)
		{
			try {
				setLegendAlignment(pos);
			} catch ( oracle.charts.types.ChartException chart_ex){}
		}

		private java.awt.image.BufferedImage getBufferedImage(boolean imgcss)
		{
		       BufferedImage bi = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_BGR);
		       Graphics2D g2 = null;
		       g2 = bi.createGraphics();
		       try {
				if (imgcss) setEnableImageMapSS(new oracle.charts.types.ImageMapDesc());
				drawBuffer(g2);
				/*
			        g2.setFont(SIMSUN10);
			        g2.setColor(java.awt.Color.GRAY);
			        g2.drawString("AnySQL.net",getWidth() - 70,getHeight() - 4);
				*/
		       }
			catch(oracle.charts.types.ChartException ch_ex) {}
		       return bi;
		}

		public String saveGifFile(boolean imgcss, int keep_time) throws java.io.IOException
		{
			java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
			WebChart.saveGifFile(getBufferedImage(imgcss),out);
			out.close();
			return ImageCache.putContent(out.toByteArray(), keep_time + 10);
		}

		public void saveGifFile(java.io.OutputStream out, boolean imgcss) throws java.io.IOException
		{
			WebChart.saveGifFile(getBufferedImage(imgcss),out);
		}

		private String getImageMapXML(String mapname,DBRowCache data,
			String col,String tcols[],String target,VariableTable vt)
		{
			int row,series;
			StringBuffer map_body = new StringBuffer();
			String lines[];
			Object arg;
			java.util.Vector words = new java.util.Vector();
			map_body.append("\t\t<image_map name=\""+mapname+"\">\n");
			try {
				lines = WebChart.getImageMapLines(String.valueOf(getImageMapSS()));
				if (lines.length>0)
				{
					series = (lines.length) / data.getRowCount();
					for (row = 0;row<lines.length;row ++)
					{
						map_body.append("\t\t\t<mapitem col=\""+tcols[(row)/data.getRowCount()]+
							"\" row=\""+((row)%data.getRowCount()+1)+"\">\n");
						words = TextUtils.getWords(lines[row]," ");
						if (words.size()>2)
						{
							map_body.append("\t\t\t\t<title><![CDATA[");
							if ((row)/data.getRowCount() < tcols.length)
							{
								arg = data.getItem(((row)%data.getRowCount()+1),col);
								if (arg != null)
									map_body.append(arg.toString());
								else
									map_body.append("NULL");
								map_body.append("=");								
								arg = data.getItem(((row)%data.getRowCount()+1),
									tcols[(row)/data.getRowCount()]);
								if (arg != null)
									map_body.append(arg.toString());
								else
									map_body.append("NULL");
							}
							map_body.append("]]></title>\n");
							map_body.append("\t\t\t\t<shape><![CDATA[");
							map_body.append(words.elementAt(0).toString());
							map_body.append("]]></shape>\n");
							map_body.append("\t\t\t\t<coords><![CDATA[");
						        map_body.append(TextUtils.getWords(words.elementAt(1).toString(),"\t").elementAt(0).toString());
							map_body.append("]]></coords>\n");
							if (data.getColumnMemo(tcols[(row)/data.getRowCount()])==null)
							{
								map_body.append("\t\t\t\t<href><![CDATA[#myanchor]]></href>\n");
							}
							else
							{
								map_body.append("\t\t\t\t<href><![CDATA["+data.parseString(data.getColumnMemo(tcols[(row)/data.getRowCount()]),vt,((row)%data.getRowCount()+1),1)+"]]></href>\n");
							}
							if (target != null && target.trim().length() > 0)
							{
								map_body.append("\t\t\t\t<target><![CDATA[");
								map_body.append(target);
								map_body.append("]]></target>\n");
							}
						}
						map_body.append("\t\t\t</mapitem>\n");
					}
				}
			} catch(oracle.charts.types.ChartException ch_ex) {}
			map_body.append("\t\t</image_map>\n");
			return map_body.toString();
		}

	}

}
