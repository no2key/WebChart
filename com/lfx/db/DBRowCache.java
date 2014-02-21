package com.lfx.db;
public interface DBRowCache
{
	public abstract long getLoadTime();
	public abstract void setLoadTime(long loadtime);

	public abstract int getColumnCount();
	public abstract int getRowCount();

	public abstract int  getErrorCode();
	public abstract String getErrorMessage();
	public abstract void setErrorCode(int errcode);
	public abstract void setErrorMessage(String errmsg);
	
	public abstract void addColumn(String col,int type);
	public abstract void removeAllColumn();

	public abstract DBRowCache rotate();

	public abstract int findColumn(String col);
	public abstract String getColumnName(int col);
	public abstract String getColumnLabel(int col);
	public abstract String getColumnLabel(String col);
	public abstract void setColumnLabel(int col,String label);
	public abstract void setColumnLabel(String col,String label);
	public abstract String getColumnSuperLabel(int col);
	public abstract String getColumnSuperLabel(String col);
	public abstract void setColumnSuperLabel(int col,String label);
	public abstract void setColumnSuperLabel(String col,String label);
	public abstract void setColumnType(int col,int type);
	public abstract void setColumnType(String col,int type);
	public abstract void setColumnSize(int col,int len);
	public abstract void setColumnSize(String col,int len);
	public abstract void setColumnMemo(int col,String memo);
	public abstract void setColumnMemo(String col,String memo);
	public abstract void setColumnTooltip(int col,String tooltip);
	public abstract void setColumnTooltip(String col,String tooltip);
	public abstract void setColumnFormater(int col,String fmter);
	public abstract void setColumnFormater(String col,String fmter);
	public abstract void setHeaderFormater(int col,String fmter);
	public abstract void setHeaderFormater(String col,String fmter);
	public abstract int getColumnType(int col);
	public abstract int getColumnType(String col);
	public abstract int getColumnSize(int col);
	public abstract int getColumnSize(String col);
	public abstract String getColumnMemo(int col);
	public abstract String getColumnMemo(String col);
	public abstract String getColumnTooltip(int col);
	public abstract String getColumnTooltip(String col);
	public abstract String getColumnFormater(int col);
	public abstract String getColumnFormater(String col);
	public abstract String getHeaderFormater(int col);
	public abstract String getHeaderFormater(String col);
	public abstract void copyColumns(DBRowCache data);
	public abstract void setPageSize(int pg);
	public abstract int  getPageSize();

        public abstract void setStringProperty(String prop, String value);
        public abstract String getStringProperty(String prop);

	public abstract void setColumnVisible(int col,boolean bvis);
	public abstract void setColumnVisible(String col,boolean bvis);
	public abstract boolean getColumnVisible(int col);
	public abstract boolean getColumnVisible(String col);
	
	public abstract void setItem(int row,int col,Object val);
	public abstract void setItem(int row,String col,Object val);
	public abstract Object getItem(int row,int col);
	public abstract Object getItem(int row,String col);

	public abstract double min(String field);
	public abstract double min(String field[]);
	public abstract double max(String field);
	public abstract double max(String field[]);
	public abstract double avg(String field);
	public abstract double sum(String field);
	public abstract int count(String field);
	public abstract int count();
        public abstract double moveaverage(int currow, String field);


	public abstract double min(int currow, String field, int grpcol[]);
	public abstract double min(int currow, String field[], int grpcol[]);
	public abstract double max(int currow, String field, int grpcol[]);
	public abstract double max(int currow, String field[], int grpcol[]);
	public abstract double avg(int currow, String field, int grpcol[]);
	public abstract double sum(int currow, String field, int grpcol[]);
	public abstract int count(int currow, String field, int grpcol[]);
	public abstract int count(int currow, int grpcol[]);
        public abstract double moveaverage(int currow, String field, int grpcol[]);

	public abstract Object[] getRow(int row);
	public abstract int insertRow(int row);
	public abstract int insertRow(int row,Object record[]);
	public abstract int appendRow();
	public abstract int appendRow(Object record[]);
	public abstract void appendRow(DBRowCache data);
	public abstract void deleteRow(int row);
	public abstract void deleteAllRow();


	public abstract int  read(java.io.BufferedReader ib,int rows) throws java.io.IOException;
	public abstract int  read(java.io.BufferedReader ib,String seperator,int rows) throws java.io.IOException;
	public abstract int  read(java.io.BufferedReader ib,String seperator,String rec_end,int rows) throws java.io.IOException;

	public abstract void  write(java.io.PrintStream ps);
	public abstract void  write(java.io.PrintStream ps,int row);
	public abstract void  write(java.io.PrintStream ps,String seperator);
	public abstract void  write(java.io.PrintStream ps,String seperator,String rec_end);
	public abstract void  write(java.io.PrintStream ps,String seperator,int row);
	public abstract void  write(java.io.PrintStream ps,String seperator,String rec_end,int row);

        public abstract void  writeForm(java.io.Writer ps) throws java.io.IOException;
        public abstract void  writeForm(java.io.Writer ps, int row) throws java.io.IOException;
        public abstract void  writeForm(java.io.PrintStream ps) throws java.io.IOException;
        public abstract void  writeForm(java.io.PrintStream ps, int row) throws java.io.IOException;

	public abstract void  write(java.io.Writer ps) throws java.io.IOException;
	public abstract void  write(java.io.Writer ps,int row) throws java.io.IOException;
	public abstract void  write(java.io.Writer ps,String seperator) throws java.io.IOException;
	public abstract void  write(java.io.Writer ps,String seperator,String rec_end) throws java.io.IOException;
	public abstract void  write(java.io.Writer ps,String seperator,int row) throws java.io.IOException;
	public abstract void  write(java.io.Writer ps,String seperator,String rec_end,int row) throws java.io.IOException;

	public abstract void  write(java.io.RandomAccessFile ps) throws java.io.IOException;
	public abstract void  write(java.io.RandomAccessFile ps,String seperator) throws java.io.IOException;
	public abstract void  write(java.io.RandomAccessFile ps,String seperator,String rec_end) throws java.io.IOException;

	public abstract String  getString(int row);
	public abstract String  getString(String seperator,int row);
        public abstract String  getFullText();

	public abstract int find(int col,Object val);
	public abstract int find(int col,Object val,int start);
	public abstract int find(int col[],Object val[]);
	public abstract int find(int col[],Object val[],int start);
	public abstract int count(int col,Object val);
	public abstract int[] filter(int col,Object val);
	public abstract int[] filter(int col[], Object val[]);
	public abstract Object[] distinct(int col);
	public abstract Object[] distinct(String col);

	public abstract void joinData(DBRowCache data2, String joincols);
	public abstract void joinData(DBRowCache data2, String joincols[]);
	
	public abstract DBRowCache groupData(String gcols[], String vcols[]);

	public abstract void addCrossTab(DBRowCache data,int r_col,int c_col,int v_col);
	public abstract void addCrossTab(DBRowCache data,String r_col,String c_col,String v_col);
	public abstract void addCrossTab(DBRowCache data,int r_col,int c_col[],int v_col[]);
	public abstract void addCrossTab(DBRowCache data,String r_col,String c_col[],String v_col[]);
	public abstract void addCrossTab(DBRowCache data,int r_col[],int c_col,int v_col);
	public abstract void addCrossTab(DBRowCache data,String r_col[],String c_col,String v_col);
	public abstract void addCrossTab(DBRowCache data,int r_col[],int c_col[],int v_col[]);
	public abstract void addCrossTab(DBRowCache data,String r_col[],String c_col[],String v_col[]);

	public abstract DBRowCache getCrossTab(int r_col,int c_col,int v_col);
	public abstract DBRowCache getCrossTab(String r_col,String c_col,String v_col);
	public abstract DBRowCache getCrossTab(int r_col,int c_col[],int v_col[]);
	public abstract DBRowCache getCrossTab(String r_col,String c_col[],String v_col[]);
	public abstract DBRowCache getCrossTab(int r_col[],int c_col[],int v_col[]);
	public abstract DBRowCache getCrossTab(String r_col[],String c_col[],String v_col[]);

	public abstract void  writeXMLBody(java.io.Writer out,VariableTable vt) throws java.io.IOException;
	public abstract void  writeXMLBody(java.io.Writer out,int grpcount,VariableTable vt) throws java.io.IOException;
	public abstract void  writeXMLBody(java.io.Writer out,String tag,int grpcount,VariableTable vt) throws java.io.IOException;
	public abstract void  writeXMLBody(java.io.Writer out,String tag,int grpcount,String collen[],VariableTable vt) throws java.io.IOException;
	public abstract void  writeXMLBody(java.io.Writer out,String tag,String attr,int grpcount, VariableTable vt) throws java.io.IOException;
	public abstract void  writeXMLBody(java.io.Writer out,String tag,String attr,int grpcount, String collen[], VariableTable vt) throws java.io.IOException;
	public abstract void  writeXMLBody(java.io.Writer out,String tag,String attr,int grpcount, String mergecols[], String collen[], VariableTable vt) throws java.io.IOException;
	public abstract void  writeXMLBody(java.io.Writer out,String tag,String attr,int grpcount, String mergecols[], String collen[], VariableTable vt, boolean editmode) throws java.io.IOException;

	public abstract int getWidth(boolean percent);
        public abstract void  addGroovyExpression(String col, String expr);
	public abstract double getExprValue(int row,String col);
	public abstract double getExprValue(int row,String col, int grpcols[]);
	public abstract void  addExpression(String col, String expr);
	public abstract void  addExpression(String col, String expr, String grpcols[]);
	public abstract void  expressFilter(String expr);

        public abstract void quicksort(int col,boolean asc);
        public abstract void quicksort(String col,boolean asc);
        public abstract void quicksort(int col);
        public abstract void quicksort(String col);	
        public abstract void quicksort(int col[],boolean asc);
        public abstract void quicksort(String col[],boolean asc);
        public abstract void quicksort(int col[]);
        public abstract void quicksort(String col[]);	        

        public abstract String parseString(String sfile, VariableTable vt, int row, int level);
        public abstract String parseString(String sfile, char sep, VariableTable vt, int row, int level);

	public abstract String EncodeHTML(String from);
	public abstract String EncodeXML(String from);

	public abstract void  writeJasonFormat(java.io.Writer ps) throws java.io.IOException;
}