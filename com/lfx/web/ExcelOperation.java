package com.lfx.web;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.*;
import com.lfx.db.*;

public class ExcelOperation
{

	public static String   getVariableTableValue(VariableTable vt, String varname, String index, boolean bupper)
	{
		if (vt == null) return null;
		String val = vt.getString("WEBCHART."+varname+"_"+index);
		if (val == null && bupper)
			val = vt.getString("WEBCHART."+varname);
		return val;
	}

	public static String   getVariableTableValue(VariableTable vt, String varname)
	{
		if (vt == null) return null;
		String val = vt.getString("WEBCHART."+varname);
		return val;		
	}	

	public static boolean  existVariableTableValue(VariableTable vt, String varname)
	{
		if (vt == null) return false;
		return vt.exists("WEBCHART."+varname);
	}	

	public static boolean  existVariableTableValue(VariableTable vt, String varname, String index)
	{
		if (vt == null) return false;
		return vt.exists("WEBCHART."+varname+"_"+index);
	}
	
	public final static void writeExcel(VariableTable vt,String file,
		SimpleDBRowCache data,String title,String note,String footnote,int grpcolcount) throws java.io.IOException
	{
		String arr[]={};
		writeExcel(vt,file,data,title,note,footnote,arr,arr,grpcolcount);	
	}
	public final static void writeExcel(VariableTable vt,String file,
		SimpleDBRowCache data,String title,String note,String footnote,String header[],String footer[],int grpcolcount) throws java.io.IOException
	{
		HSSFWorkbook wb = new HSSFWorkbook();
		writeExcel(vt,wb,data,title,note,footnote,header,footer,grpcolcount);
		java.io.FileOutputStream of = new java.io.FileOutputStream(file);
		wb.write(of);
		of.close();		
	}
	public final static void writeExcel(VariableTable vt,java.io.OutputStream of,
		SimpleDBRowCache data,String title,String note,String footnote,int grpcolcount) throws java.io.IOException
	{
		String arr[]={};
		writeExcel(vt,of,data,title,note,footnote,arr,arr,grpcolcount);	
	}
	public final static void writeExcel(VariableTable vt,java.io.OutputStream of,
		SimpleDBRowCache data,String title,String note,String footnote,String header[],String footer[],int grpcolcount) throws java.io.IOException
	{
		HSSFWorkbook wb = new HSSFWorkbook();
		writeExcel(vt,wb,data,title,note,footnote,header,footer,grpcolcount);
		wb.write(of);
	}

	public final static void writeExcel(VariableTable vt,HSSFWorkbook excel,SimpleDBRowCache data,
		String title,String note,String footnote,int grpcolcount)
	{
		String arr[]={};
		writeExcel(vt,excel,data,title,note,footnote,arr,arr,grpcolcount);	
	}

	public final static void writeExcel(VariableTable vt,HSSFWorkbook excel,SimpleDBRowCache data,
		String title,String note,String footnote, String header[],String footer[],int grpcolcount)
	{
		String arr[]={};
		writeExcel(vt,excel,data,title,note,footnote,arr,arr,grpcolcount, arr, 500);	
	}

	public final static void writeExcel(VariableTable vt,HSSFWorkbook excel,SimpleDBRowCache data,
		String title,String note,String footnote,String headersetting[],String footersetting[],int grpcolcount, String mergecols[], int xlswidth)
	{
		int row,col,totallen=0,rownum=0, duplabelcnt = 0;
		Object row_data[];
		totallen=data.getWidth(false);

		int mgcols[] = new int[1];
		int _mergecols[] = new int[data.getColumnCount()];
		boolean is_merge_column = false;
		float _pdfwidth[] = new float[data.getColumnCount()];

		HSSFSheet s = excel.createSheet();
		// excel.setSheetName(0,title);

		HSSFRow   r = null;
		HSSFCell  c = null;
		HSSFRow   r2 = null;
		HSSFCell  c2 = null;

		HSSFCellStyle cs = excel.createCellStyle();
		cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		HSSFCellStyle cs_header = excel.createCellStyle();
		cs_header.setWrapText(true);
		cs_header.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cs_header.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cs_header.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cs_header.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cs_header.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cs_header.setBorderRight(HSSFCellStyle.BORDER_THIN);

		HSSFCellStyle cs_note = excel.createCellStyle();
		cs_note.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cs_note.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		HSSFCellStyle cs_normal = excel.createCellStyle();
		cs_normal.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		HSSFFont font_title = excel.createFont();
		font_title.setFontHeightInPoints((short)18);
		font_title.setFontName("SimHei");

		//HSSFFont font_note = excel.createFont();
		//font_note.setFontHeightInPoints((short)12);
		//font_note.setFontName("SimHei");

		cs.setFont(font_title);
		//cs_note.setFont(font_note);

		r = s.createRow((short)rownum);
		r.setHeight((short)(600));
		rownum++;
		c=r.createCell((short)0);
		c.setCellStyle(cs);
		c.setCellValue(title);

		CellRangeAddress region = new CellRangeAddress(rownum - 1, rownum - 1, 0, data.getColumnCount()-1);	
		s.addMergedRegion(region);

		HSSFCellStyle col_cs[] = new HSSFCellStyle[data.getColumnCount()];

		r = s.createRow((short)rownum);
		r.setHeight((short)(350));
		rownum++;
		c=r.createCell((short)0);
		c.setCellStyle(cs_note);

		region = new CellRangeAddress(rownum - 1, rownum - 1, 0, data.getColumnCount()-1);	
		s.addMergedRegion(region);
		c.setCellValue(note);

		if (mergecols != null)
		{
		    for(int i=0;i<mergecols.length;i++)
		    {
			_mergecols[i] = data.findColumn(mergecols[i]);
		    }
		}

		if (headersetting!=null && headersetting.length>0)
		{
			for(int i=0;i<headersetting.length;i++)
			{
				if (headersetting[i]==null || headersetting[i].trim().length()==0)
					continue;
				if (headersetting[i].trim().equalsIgnoreCase("NEWLINE"))
				{
					r = s.createRow((short)rownum);
					r.setHeight((short)(320));
					rownum++;		
				}
				else
				{
					String temp = headersetting[i].trim();
					int pos = temp.indexOf("=");
					if (pos > 0 && pos < temp.length()-1)
					{
						String s_pos = temp.substring(0,pos);
						String s_val = temp.substring(pos+1);
						int c_pos = getint(s_pos,0);
						if (c_pos > 0)
						{
							c=r.createCell((short)(c_pos-1));
							c.setCellStyle(cs_normal);
							c.setCellValue(s_val);		
						}
					}
				}
			}
		}

		r = s.createRow((short)rownum);
		r.setHeight((short)(500));
		rownum++;
		r2 = s.createRow((short)rownum);
		r2.setHeight((short)(500));

		for(row=1;row<=data.getColumnCount();row++)
		{
			data.setColumnVisible(row, true);
			col_cs[row - 1] = excel.createCellStyle();
			col_cs[row - 1].setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			col_cs[row - 1].setBorderTop(HSSFCellStyle.BORDER_THIN);
			col_cs[row - 1].setBorderBottom(HSSFCellStyle.BORDER_THIN);
			col_cs[row - 1].setBorderLeft(HSSFCellStyle.BORDER_THIN);
			col_cs[row - 1].setBorderRight(HSSFCellStyle.BORDER_THIN);
			col_cs[row - 1].setAlignment(HSSFCellStyle.ALIGN_CENTER);

			if (data.getColumnType(row) == java.sql.Types.LONGVARCHAR ||
				data.getColumnType(row) == java.sql.Types.VARCHAR ||
				data.getColumnType(row) == java.sql.Types.BLOB ||
				data.getColumnType(row) == java.sql.Types.CLOB ||
				data.getColumnType(row) == java.sql.Types.CHAR )
			{
				if (data.getColumnSize(row) < 20)
					col_cs[row - 1].setAlignment(HSSFCellStyle.ALIGN_CENTER);
				else
					col_cs[row - 1].setAlignment(HSSFCellStyle.ALIGN_LEFT);
			}
			else if (data.getColumnType(row) == java.sql.Types.DATE ||
				data.getColumnType(row) == java.sql.Types.TIME ||
				data.getColumnType(row) == java.sql.Types.TIMESTAMP )
			{
				col_cs[row - 1].setAlignment(HSSFCellStyle.ALIGN_CENTER);
			}
			else
			{
				col_cs[row - 1].setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			}

                        duplabelcnt = 0;
			for(int i=row+1;i<=data.getColumnCount();i++)
			{
			    if (data.getColumnSuperLabel(row).equals(data.getColumnSuperLabel(i))) duplabelcnt ++;
			}

			/*
			c=r.createCell((short)(row - 1));
			c.setCellType(HSSFCell.CELL_TYPE_STRING);
			c.setCellValue(TextUtils.toNativeLines(vt.parseString(data.getColumnLabel(row))));
			c.setCellStyle(cs_header);
			s.setColumnWidth((short) (row - 1), (short) (xlswidth * 80 * data.getColumnSize(row)/totallen));
			*/

			if (duplabelcnt == 0)
			{
			    if (row > 1 && data.getColumnSuperLabel(row).equals(data.getColumnSuperLabel(row - 1)))
		            {
				c=r2.createCell((short)(row - 1));
				c.setCellType(HSSFCell.CELL_TYPE_STRING);
				c.setCellValue(TextUtils.toNativeLines(vt.parseString(data.getColumnLabel(row))));
				c.setCellStyle(cs_header);
				s.setColumnWidth((short) (row - 1), (short) (xlswidth * 80 * data.getColumnSize(row)/totallen));
                            }
                            else if (data.getColumnSuperLabel(row).equals(data.getColumnLabel(row)))
		            {
				c=r.createCell((short)(row - 1));
				c.setCellType(HSSFCell.CELL_TYPE_STRING);
				c.setCellValue(TextUtils.toNativeLines(vt.parseString(data.getColumnLabel(row))));
				c.setCellStyle(cs_header);
				s.setColumnWidth((short) (row - 1), (short) (xlswidth * 80 * data.getColumnSize(row)/totallen));

				c=r2.createCell((short)(row - 1));
				c.setCellType(HSSFCell.CELL_TYPE_STRING);
				c.setCellStyle(cs_header);

				region = new CellRangeAddress(rownum - 1, rownum, (row-1), (row-1));	
				s.addMergedRegion(region);
                            }
			    else
		            {
				c=r.createCell((short)(row - 1));
				c.setCellType(HSSFCell.CELL_TYPE_STRING);
				c.setCellValue(TextUtils.toNativeLines(vt.parseString(data.getColumnSuperLabel(row))));
				c.setCellStyle(cs_header);
				// s.setColumnWidth((short) (row - 1), (short) (xlswidth * 80 * data.getColumnSize(row)/totallen));


				c=r2.createCell((short)(row - 1));
				c.setCellType(HSSFCell.CELL_TYPE_STRING);
				c.setCellValue(TextUtils.toNativeLines(vt.parseString(data.getColumnLabel(row))));
				c.setCellStyle(cs_header);
				s.setColumnWidth((short) (row - 1), (short) (xlswidth * 80 * data.getColumnSize(row)/totallen));
                            }			
			}
			else
			{
			    if (row > 1 && data.getColumnSuperLabel(row).equals(data.getColumnSuperLabel(row - 1)))
		            {
				c=r2.createCell((short)(row - 1));
				c.setCellType(HSSFCell.CELL_TYPE_STRING);
				c.setCellValue(TextUtils.toNativeLines(vt.parseString(data.getColumnLabel(row))));
				c.setCellStyle(cs_header);
				s.setColumnWidth((short) (row - 1), (short) (xlswidth * 80 * data.getColumnSize(row)/totallen));
                            }
			    else
			    {
				c=r.createCell((short)(row - 1));
				c.setCellType(HSSFCell.CELL_TYPE_STRING);
				c.setCellValue(TextUtils.toNativeLines(vt.parseString(data.getColumnSuperLabel(row))));
				c.setCellStyle(cs_header);
				// s.setColumnWidth((short) (row - 1), (short) (xlswidth * 80 * data.getColumnSize(row)/totallen));

				for(int i=(row-1)+1; i<=(row-1 + duplabelcnt);i++)
				{
					c=r.createCell((short)i);
					c.setCellType(HSSFCell.CELL_TYPE_STRING);
					c.setCellStyle(cs_header);
				}

				region = new CellRangeAddress(rownum - 1, rownum - 1, (row-1), (row-1 + duplabelcnt));	
				s.addMergedRegion(region);


				c=r2.createCell((short)(row - 1));
				c.setCellType(HSSFCell.CELL_TYPE_STRING);
				c.setCellValue(TextUtils.toNativeLines(vt.parseString(data.getColumnLabel(row))));
				c.setCellStyle(cs_header);
				s.setColumnWidth((short) (row - 1), (short) (xlswidth * 80 * data.getColumnSize(row)/totallen));
                            }
			}

			/*
			region = new CellRangeAddress(rownum - 1, rownum-1+grprows[col-1]-1, (col-1), (col-1));	
			s.addMergedRegion(region);
			*/
		}
		rownum ++;

		boolean newgrp[] = new boolean[data.getColumnCount()];
		int 	grprows[] = new int[data.getColumnCount()];

		for(row=1;row<=data.getRowCount();row++)
		{
			r = s.createRow((short)rownum);
			r.setHeight((short)(320));
			rownum++;

			row_data = (Object [])(data.getRow(row));

			for(col=1;col<=data.getColumnCount();col++)
			{
				c=r.createCell((short)(col - 1));
				c.setCellStyle(col_cs[col - 1]);
				if (col <= grpcolcount)
				{
					newgrp[col - 1] = data.rowEquals(row,row-1,col);

					if (!newgrp[col - 1])
					{
						int col_id[] = new int[col];
						for(int i=0;i<col;i++)
							col_id[i]=i+1;
						grprows[col - 1] = data.countgroup(col_id,row);
					}
					
					if (newgrp[col - 1])
					{
						//out.write("grp=\"0\" ");
					}
					else
					{ 
						if (grprows[col-1]>1)
						{
							region = new CellRangeAddress(rownum - 1, rownum-1+grprows[col-1]-1, (col-1), (col-1));	
							s.addMergedRegion(region);
						}
						
						if(row_data[col - 1] != null)
						{
							if (row_data[col - 1] instanceof Number)
							{
								c.setCellValue(((Number)row_data[col - 1]).doubleValue());
								c.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							}
							else
							{
								c.setCellValue(row_data[col - 1].toString());
								c.setCellType(HSSFCell.CELL_TYPE_STRING);
							}
						}
					}
				}
				else
				{
				    is_merge_column = false;
				    if (mergecols != null && mergecols.length > 0)
				    {
					for(int i=0;i<mergecols.length;i++)
					{
						if (_mergecols[i] == col)
						{
							is_merge_column = true;
							mgcols[0] = col; 
							newgrp[col - 1] = data.rowEquals(row,row-1,mgcols);

							if (!newgrp[col - 1])
								grprows[col - 1] = data.countgroup(mgcols,row);
					
							if (newgrp[col - 1])
							{
								//out.write("grp=\"0\" ");
							}
							else 
							{
								if (grprows[col-1]>1)
								{
									region = new CellRangeAddress(rownum - 1, rownum-1+grprows[col-1]-1, (col-1), (col-1));	
									s.addMergedRegion(region);
								}
								if(row_data[col - 1] != null)
								{
									if (row_data[col - 1] instanceof Number)
									{
										c.setCellValue(((Number)row_data[col - 1]).doubleValue());
										c.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									}
									else
									{
										c.setCellValue(row_data[col - 1].toString());
										c.setCellType(HSSFCell.CELL_TYPE_STRING);	
									}
								}
							}
						}
					}
				    }
				    if (!is_merge_column)
				    {
					if(row_data[col - 1] != null)
					{
						if (row_data[col - 1] instanceof Number)
						{
							c.setCellValue(((Number)row_data[col - 1]).doubleValue());
							c.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						}
						else
						{
							c.setCellValue(row_data[col - 1].toString());
							c.setCellType(HSSFCell.CELL_TYPE_STRING);
						}
					}
				    }
				}
			}
		}

		if (footersetting!=null && footersetting.length>0)
		{
			for(int i=0;i<footersetting.length;i++)
			{
				if (footersetting[i]==null || footersetting[i].trim().length()==0)
					continue;
				if (footersetting[i].trim().equalsIgnoreCase("NEWLINE"))
				{
					r = s.createRow((short)rownum);
					r.setHeight((short)(320));
					rownum++;		
				}
				else
				{
					String temp = footersetting[i].trim();
					int pos = temp.indexOf("=");
					if (pos > 0 && pos < temp.length()-1)
					{
						String s_pos = temp.substring(0,pos);
						String s_val = temp.substring(pos+1);
						int c_pos = getint(s_pos,0);
						if (c_pos > 0)
						{
							c=r.createCell((short)(c_pos-1));
							c.setCellStyle(cs_normal);
							c.setCellValue(s_val);		
						}
					}
				}
			}
		}
		r = s.createRow((short)rownum);
		r.setHeight((short)(320));
		rownum++;		
		r = s.createRow((short)rownum);
		r.setHeight((short)(350));
		rownum++;
		c=r.createCell((short)0);
		c.setCellStyle(cs_note);
		region = new CellRangeAddress(rownum - 1, rownum-1, 0, data.getColumnCount()-1);	
		s.addMergedRegion(region);
		c.setCellValue(footnote);
	}
	public static void generateExcel(java.io.OutputStream out,VariableTable vt) 
		throws java.io.IOException
    	{
		int i=0,sql_count=0;
		DBRowCache crosstab = null, temprows=null;
    		String chartquery = null;
		String charttype  = null;
		String iscrosstab = null;
		String dbname = null;
		String dbrule = null;
		String express = null;
		String foreach = null;
		String xlswidth = "500";
		String ignmarkdown = null;
		String ignsqlerror = null;
		java.util.Vector foreachlist = new java.util.Vector();
		java.util.Vector querylist = new java.util.Vector();

		DBPooledConnection db	= null;

		if(existVariableTableValue(vt,"QUERY") && getVariableTableValue(vt,"QUERY") != null)
                {
			querylist = TextUtils.getWords(getVariableTableValue(vt,"QUERY"),",");
                }
                else
                {
  		    for(i=0;i<100;i++)
		    {
			if (existVariableTableValue(vt,"QUERY",String.valueOf(i+1))) 
			{
				chartquery = getVariableTableValue(vt,"QUERY",String.valueOf(i+1),false);
				if (chartquery != null && chartquery.length() > 0)
				{
					querylist.addElement(String.valueOf(i+1));
				}			
			}
		    }
		}

		HSSFWorkbook wb = new HSSFWorkbook();
		if (querylist.size()>0)
		{
			crosstab = DBOperation.getDBRowCache();
			for(i=0;i<querylist.size();i++)
			{
			    crosstab = DBOperation.getDBRowCache();
			    iscrosstab = getVariableTableValue(vt, "CROSSTAB", querylist.elementAt(i).toString(), true);
			    chartquery = getVariableTableValue(vt, "QUERY", querylist.elementAt(i).toString(),false);
			    charttype = getVariableTableValue(vt, "TYPE", querylist.elementAt(i).toString(),false);
			    dbname = getVariableTableValue(vt, "DBNAME", querylist.elementAt(i).toString(),true);
			    dbrule = getVariableTableValue(vt, "DBID", querylist.elementAt(i).toString(),true);
			    ignsqlerror = getVariableTableValue(vt, "IGNORE_SQLERROR", querylist.elementAt(i).toString(),true);
			    ignmarkdown = getVariableTableValue(vt, "IGNORE_MARKDOWN", querylist.elementAt(i).toString(),true);

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
				    crosstab = null;
				    crosstab = DBOperation.getDBRowCache();
				    for(int dsloop=0; dsloop < 2; dsloop ++)
				    {
				      try {
					if (dbname != null && (dbname.startsWith("url::") || dbname.startsWith("URL::")))
					{
					   try {
				                 db = new DBPooledConnection(DBOperation.getConnection(dbname.substring(5)));
					   } catch (java.lang.ClassNotFoundException cnfe) { throw new java.io.IOException(cnfe.getMessage()); };
					}
					else
				           db = DBLogicalManager.getPoolConnection(vt.parseString(dbname), dbrule);

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
					    	if (expwords.size() >= 3)
					    	{
					    		colname = expwords.elementAt(0).toString();
					    		expstr  = expwords.elementAt(1).toString();
					    		expcols = TextUtils.toStringArray(TextUtils.getWords(expwords.elementAt(2).toString(),","));
							if (expwords.size() > 3)
								colarrs = TextUtils.toStringArray(TextUtils.getWords(expwords.elementAt(3).toString(),","));
					    		if (expcols.length < 2)
					    		{
					    		    crosstab.addExpression(colname, expstr, expcols[0], colarrs);
					    		}
					    		else if (expcols.length < 3)
					    		{
					    		    crosstab.addExpression(colname, expstr, expcols[0],expcols[1], colarrs);
					    		}
					    		else if (expcols.length < 4)
					    		{
					    		    crosstab.addExpression(colname, expstr, expcols[0],expcols[1],expcols[2], colarrs);
					    		}					    		
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
					    {
					    	expwords = TextUtils.getWords(exparr[tmpk],"|");
					    	if (expwords.size() >= 2)
					    	{
					    		expstr = expwords.elementAt(0).toString();
					    		expcols = TextUtils.toStringArray(TextUtils.getWords(expwords.elementAt(1).toString(),","));
					    		if (expcols.length < 2)
					    		{
					    		    crosstab.expressFilter(expstr, expcols[0]);
					    		}
					    		else if (expcols.length < 3)
					    		{
					    		    crosstab.expressFilter(expstr, expcols[0], expcols[1]);
					    		}
					    		else if (expcols.length < 4)
					    		{
					    		    crosstab.expressFilter(expstr, expcols[0], expcols[1], expcols[2]);
					    		}					    		
						}
					    }
					}					
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
					if (db != null) db.close();
				      }
				    }
				}
				db = null;

				String chartlabel = getVariableTableValue(vt,"LABEL",querylist.elementAt(i).toString(),true);
				java.util.Vector label = TextUtils.getFields(chartlabel,"|");
				for(int j=0;j<label.size() && j<crosstab.getColumnCount();j++)
				{
					if (label.elementAt(j) != null)
						crosstab.setColumnLabel(j+1,label.elementAt(j).toString());
				}
				String chartsuperlabel = getVariableTableValue(vt,"SUPER",querylist.elementAt(i).toString(),true);
				java.util.Vector superlabel = TextUtils.getFields(chartsuperlabel,"|");
				for(int j=0;j<superlabel.size() && j<crosstab.getColumnCount();j++)
				{
					if (superlabel.elementAt(j) != null)
						crosstab.setColumnSuperLabel(j+1,superlabel.elementAt(j).toString());
				}
				if (crosstab.getRowCount()==1)
				{
					for(int j=1;j<=crosstab.getColumnCount();j++)
					{
						vt.add("QUERY_"+querylist.elementAt(i).toString()+"."+crosstab.getColumnName(j),
							crosstab.getColumnType(j));
						vt.setValue("QUERY_"+querylist.elementAt(i).toString()+"."+crosstab.getColumnName(j),
							crosstab.getItem(1,j));
					}
				}
		                String sortcolumns = getVariableTableValue(vt,"SORT",querylist.elementAt(i).toString(),true);
				if (sortcolumns != null)
				{
					crosstab.quicksort(TextUtils.toStringArray(TextUtils.getWords(sortcolumns,",")));
				}				
				if (charttype == null || !charttype.equals("-"))
				{
					if (temprows != null)
					{
						temprows.appendRow(crosstab);
						crosstab = temprows;
					}				
					String isexcel = getVariableTableValue(vt,"EXCEL",querylist.elementAt(i).toString(),true);
					if (isexcel == null)
						isexcel = "YES";
					String title = getVariableTableValue(vt,"XLSTITLE",querylist.elementAt(i).toString(),true);
					String note = getVariableTableValue(vt,"XLSSUBTITLE",querylist.elementAt(i).toString(),true);
					String footnote = getVariableTableValue(vt,"XLSFOOTNOTE",querylist.elementAt(i).toString(),true);
					String grp = getVariableTableValue(vt,"GROUP",querylist.elementAt(i).toString(),true);
					String header = getVariableTableValue(vt,"XLSHEADER",querylist.elementAt(i).toString(),true);
					String footer = getVariableTableValue(vt,"XLSFOOTER",querylist.elementAt(i).toString(),true);
					String mergecols = getVariableTableValue(vt,"MERGE",querylist.elementAt(i).toString(),true);
					xlswidth = getVariableTableValue(vt, "XLSWIDTH", querylist.elementAt(i).toString(),true);


					if (title == null) title = "";
					if (note == null) note = "";
					if (footnote == null) footnote = "";
					if (header == null) header = "";
					if (footer == null) footer = "";
					if (mergecols == null) mergecols = "";

					String headersetting[] = TextUtils.toStringArray(TextUtils.getLines(header));
					String footersetting[] = TextUtils.toStringArray(TextUtils.getLines(footer));
					String mergecolslist[] = TextUtils.toStringArray(TextUtils.getLines(mergecols));

					for(int j=0;j<headersetting.length;j++)
						headersetting[j]=vt.parseString(headersetting[j]);
					for(int j=0;j<footersetting.length;j++)
						footersetting[j]=vt.parseString(footersetting[j]);
					title = vt.parseString(title);
					note = vt.parseString(note);
					footnote=vt.parseString(footnote);
					if (!("NO".equalsIgnoreCase(isexcel.trim())))
						writeExcel(vt,wb,(SimpleDBRowCache)crosstab,title,note,footnote,
							headersetting,footersetting,getint(grp,2), mergecolslist, getint(xlswidth,500));
				}
				else
				{
					if (temprows==null || temprows.getColumnCount() != crosstab.getColumnCount())
					{
						temprows = DBOperation.getDBRowCache();
						temprows.copyColumns(crosstab);
					}
					temprows.appendRow(crosstab);
				}
			    }
			}
		}
		wb.write(out);
		out.flush();
		out.close();
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
}