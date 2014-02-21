package com.lfx.db;
public final class StringTable
{
   private DBRowCache stringtable = new SimpleDBRowCache();

   public StringTable()
   {
	stringtable.addColumn("NAME",java.sql.Types.VARCHAR);
	stringtable.addColumn("VALUE",java.sql.Types.VARCHAR);
   }

   public final void loadURL(String url)
   {
	try {
		java.net.URL urlfile = getClass().getResource(url);
		if (urlfile == null) return;
		load(new java.io.BufferedReader(
			new java.io.InputStreamReader(urlfile.openStream())));
	} catch (java.io.IOException e)	{}
   }

   public final void loadFile(String url)
   {
	try {
		java.io.BufferedReader file = new java.io.BufferedReader(
			new java.io.FileReader(url));
		load(file);
	} catch (java.io.IOException e) {}
   }

   private final void load(java.io.BufferedReader in)
   {
	int rows=0,pos=0;
	String temp="";
	try {
		if (in==null) return;
		stringtable.deleteAllRow();
		while((temp=in.readLine())!=null)
		{
			pos=0;
			temp = temp.trim();
			if (temp.length()==0) continue;
			if (temp.substring(0,1).equals("#")) continue;
			pos = temp.indexOf("=");
			if (pos<=0)
				 continue;
			rows = stringtable.insertRow(rows+1);
			stringtable.setItem(rows,1,temp.substring(0,pos).trim().toUpperCase());
			if (pos == (temp.length() - 1))
				stringtable.setItem(rows,2,null);
			else
				stringtable.setItem(rows,2,temp.substring(pos+1));
		}
	} catch (java.io.IOException e)
	{
	}
	try {
		if (in != null)
			in.close();
	} catch (java.io.IOException ioe){}
   }

   public final boolean exists(String stname)
   {
	return (stringtable.find(1,stname)>0);
   }

   public final int getInt(String stname,int idef)
   {
	String temp = getString(stname);
	if (temp == null)
		return idef;
	try {
		return Integer.valueOf(temp).intValue();
	}
	 catch (NumberFormatException nfe) {}
	return idef;
   }

   public final long getLong(String stname,long ldef)
   {
	String temp = getString(stname);
	if (temp == null)
		return ldef;
	try {
		return Long.valueOf(temp).longValue();
	}
	 catch (NumberFormatException nfe) {}
	return ldef;
   }

   public final float getFloat(String stname,float fdef)
   {
	String temp = getString(stname);
	if (temp == null)
		return fdef;
	try {
		return Float.valueOf(temp).floatValue();
	}
	 catch (NumberFormatException nfe) {}
	return fdef;
   }

   public final double getDouble(String stname,double ddef)
   {
	String temp = getString(stname);
	if (temp == null)
		return ddef;
	try {
		return Double.valueOf(temp).doubleValue();
	}
	 catch (NumberFormatException nfe) {}
	return ddef;
   }

   public final boolean getBoolean(String stname,boolean bdef)
   {
	String temp = getString(stname);
	if (temp == null)
		return bdef;
	return Boolean.valueOf(temp).booleanValue();
   }

   public final String getString(String stname)
   {
        int pos=0;
	if ((pos=stringtable.find(1,stname.toUpperCase()))>0)
	{
		return (String)(stringtable.getItem(pos,2));
	}
	return null;
   }

   public final String getString(String stname,String defval)
   {
        int pos=0;
	if ((pos=stringtable.find(1,stname))>0)
	{
		return (String)(stringtable.getItem(pos,2));
	}
	return defval;
   }


   public final String[] getStrings(String stname)
   {
	int i; 
	String s_result[]={};
	int s_rows[] = stringtable.filter(1,stname.toUpperCase());
	if (s_rows == null || s_rows.length == 0) return s_result;
	s_result = new String[s_rows.length];
	for(i=0;i<s_result.length;i++)
	{
		s_result[i]=(String)(stringtable.getItem(s_rows[i],2));
	}
	return s_result;
   }
 }
