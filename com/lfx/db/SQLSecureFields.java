package com.lfx.db;

public final class SQLSecureFields
{
    private static long fields_loadtm = System.currentTimeMillis();
    private static boolean fields_loaded = false;
    private static java.util.HashMap fields_list = new java.util.HashMap();

    public final static void loadFieldsFromFile(String url)
    {
	 if (!fields_loaded || System.currentTimeMillis() - fields_loadtm > 300000)
	 {
		try {
			java.io.BufferedReader file = new java.io.BufferedReader(
				new java.io.InputStreamReader
					(new java.io.FileInputStream(url)));
			loadFieldsFromFile(file);
		} catch (java.io.IOException e) {}
	 }
    }

    public final static void loadFieldsFromFile(java.io.BufferedReader in)
    {
	String line="";
	if (!fields_loaded || System.currentTimeMillis() - fields_loadtm > 300000)
	{
	   try {
		if (in==null) return;
		while((line=in.readLine())!=null)
		{
			line=line.trim().toUpperCase();
			if (line.length()==0) continue;
			if (line.substring(0,1).equals("#")) continue;
			String words[] = TextUtils.toStringArray(TextUtils.getWords(line,"."));
			if (words.length == 2)
			{
				fields_list.put(words[0],"True");
				fields_list.put(line,"True");
			}
		}
		fields_loaded = true;
		fields_loadtm = System.currentTimeMillis();
	   } catch (java.io.IOException e)
	   {
	   }
	   try {
		if (in != null) in.close();
	   } catch (java.io.IOException ioe){}
	}
    }

    public final static int isSecureFields(String fields)
    {
	if (fields == null) return 0;
	String upfname = fields.trim().toUpperCase();
	if (fields_list.containsKey(upfname)) return 1;
	String words[] = TextUtils.toStringArray(TextUtils.getWords(upfname,"."));
	if (words.length == 2)
	{
	    if ("*".equals(words[1]))
	    {
		if (fields_list.containsKey(words[0])) return 2;
	    }
	    if (fields_list.containsKey("TABLE."+words[0])) return 3;
	}
	return 0;
    }
}