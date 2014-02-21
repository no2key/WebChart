package com.lfx.db;
public final class TextUtils extends Object
{
        public static final long hashCode(String key)
	{
	      long hash = 0xAAAAAAAA;

	      if (key != null && key.length() > 0)
	      {
	        for(int i = 0; i < key.length(); i++)
	        {
        	  if ((i & 1) == 0)
	          {
        	    hash ^= ((hash << 7) ^ key.charAt(i) * (hash >> 3));
	          }
        	  else
	          {
        	    hash ^= (~((hash << 11) + key.charAt(i) ^ (hash >> 5)));
	          }
	        }
	      }
	      return hash & 0x7fffffff;
	}

        public static final long hashCode(String key,  long seed)
	{
		return hashCode(key) % seed;
	}

	public static final java.util.Vector getFields(String line)
	{
		return getFields(line,',','\"');
	}
	public static final java.util.Vector<String> getFields(String line,String seperator)
	{
		if (seperator != null && seperator.length() == 1)
			return getFields(line,seperator.charAt(0),'\"');
		else
			return getFields(line,seperator,"\"");
	}
	public static final java.util.Vector<String> getFields(String line,String seperator,String quote)
	{
		java.util.Vector<String> result = new java.util.Vector<String>();
		if (line == null) return result;
		boolean in_quote=false;
		FieldTokenizer st = new FieldTokenizer(line,seperator,true);
		String word = "";
		boolean prewords=false;
		while(st.hasMoreTokens())
		{
			String temp = st.nextToken();
			if (in_quote)
			{
				word = word+temp;
			}
			else
			{
				if (temp.equals(seperator))
				{
					if(!prewords)
					{
						result.addElement(null);
					}
					prewords=false;
					continue;
				}
				word = temp;
			}
			if(word.startsWith(quote))
				in_quote=true;
			if(in_quote &&(!word.endsWith(quote) || word.length()==quote.length())) continue;
			if(word.endsWith(quote))
				in_quote=false;
			if(word.length()>quote.length() && 
				word.startsWith(quote) &&
				word.endsWith(quote))
			{
				word = word.substring(quote.length());
				if(word.length()>=quote.length())
					word = word.substring(0,word.length()-quote.length());
			}
			if (!in_quote)
			{
				result.addElement(word);
				prewords=true;
			}
			word = "";
		}
		if(!prewords)
			result.addElement(null);	
		return result;
	}
	public static final java.util.Vector<String> getWords(String line)
	{
		return getWords(line,' ','\"');
	}
	public static final java.util.Vector<String> getWords(String line,String seperator)
	{
		if (seperator != null && seperator.length() == 1)
			return getWords(line,seperator.charAt(0),'\"');
		else
			return getWords(line,seperator,"\"");
	}
	public static final java.util.Vector<String> getWords(String line,String seperator,String quote)
	{
		java.util.Vector<String> result = new java.util.Vector<String>();
		if (line == null) return result;
		boolean in_quote=false;
		FieldTokenizer st = new FieldTokenizer(line,seperator,true);
		String word = "";
		while(st.hasMoreTokens())
		{
			String temp = st.nextToken();
			if (in_quote)
			{
				word = word+temp;
			}
			else
			{
				if (temp.equals(seperator) || temp.equals("\n")) continue;
				word = temp;
			}
			if(word.startsWith(quote))
				in_quote=true;
			if(in_quote && (!(word.endsWith(quote) || word.endsWith(quote+"\n")) 
					|| word.length()==quote.length()))
				continue;
			if(word.endsWith(quote) || word.endsWith(quote+"\n") )
				in_quote=false;
			if(word.length()>quote.length() && 
				word.startsWith(quote) &&
				(word.endsWith(quote) || word.endsWith(quote+"\n")))
			{
				word = word.substring(quote.length());
				if(word.length()>=quote.length() && word.endsWith(quote))
				{
					word = word.substring(0,word.length()-quote.length());
				}
				else if (word.length()>=quote.length() && word.endsWith(quote+"\n"))
				{
					word = word.substring(0,word.length()-quote.length()-1);
				}
			}
			if (!in_quote)
			{
				if (word.endsWith("\n"))
					word = word.substring(0,word.length()-1);
				result.addElement(word.trim());			
			}
			word = "";
		}
		if (word.length()>0)
		{
			if (word.endsWith("\n"))
				word = word.substring(0,word.length()-1);
			result.addElement(word.trim());
		}
		return result;
	}

        public static final java.util.Vector<String> getWords(String line,char seperator)
        {
        	return getWords(line,seperator,'\"');
        }

	public static final java.util.Vector<String> getWords(String line, char seperator, char quote)
	{
		java.util.Vector<String> result = new java.util.Vector<String>();
		if (line == null) return result;
		
		int i=0;
		boolean in_quote=false;
		StringBuffer tempbuf = new StringBuffer();
		int pos = 0;
		
		while(pos < line.length())
		{
        	    if (in_quote)
        	    {
        	    	if (line.charAt(pos) == quote)
        	    	{
        	    	    if (pos + 1 < line.length() && line.charAt(pos + 1) == quote)
        	    	    {
        	    	    	pos ++;
        	    	    	tempbuf.append(line.charAt(pos));
        	    	    	pos++;	
        	    	    }
        	    	    else
        	    	    {
				/*
        	    	        if (tempbuf.length() > 0)
        	    	        {
        	    	            result.addElement(tempbuf.toString());
        	    	        }
        	    	        tempbuf.setLength(0);
				*/
        	    	        in_quote=false;
        	    	        pos ++;
        	    	    }
        	    	}
        	    	else
        	    	{
        	    	   tempbuf.append(line.charAt(pos));
        	    	   pos ++;
        	    	}
        	    }	
        	    else 
        	    {
        	    	if (line.charAt(pos) == quote)
        	    	{
        	    	    in_quote = true;
        	    	    pos ++;
        	    	}
        	    	else if (line.charAt(pos) == seperator || (seperator == ' ' && Character.isWhitespace(line.charAt(pos))))
        	    	{
        	    	    if (tempbuf.length() > 0)
        	    	    {
        	    	        for(i=0; i< tempbuf.length(); i++)
        	    	        {
        	    	           if (!Character.isWhitespace(tempbuf.charAt(i))) break;
        	    	        }
        	    	        if (i>0) tempbuf.delete(0,i);
        	    	        for(i=tempbuf.length() - 1; i>= 0; i--)
        	    	        {
        	    	           if (!Character.isWhitespace(tempbuf.charAt(i))) break;
        	    	        }
        	    	        tempbuf.setLength(i+1);
        	    	        if (tempbuf.length() > 0)
        	    	            result.addElement(tempbuf.toString());
        	    	    }
        	    	    tempbuf.setLength(0);
        	    	    pos ++;        	    	    	
        	    	}
        	    	else
        	    	{
        	    	   tempbuf.append(line.charAt(pos));
        	    	   pos ++;        	    		
        	    	}
        	    }	        	    	
		}
		if (tempbuf.length()>0)
		{
		    if (!in_quote)
		    {
        	        for(i=0; i< tempbuf.length(); i++)
        	        {
        	           if (!Character.isWhitespace(tempbuf.charAt(i))) break;
        	        }
        	        if (i>0) tempbuf.delete(0,i);
        	        for(i=tempbuf.length() - 1; i>= 0; i--)
        	        {
        	           if (!Character.isWhitespace(tempbuf.charAt(i))) break;
        	        }
        	        tempbuf.setLength(i+1);
        	    }
        	    if (in_quote || tempbuf.length() > 0)
        	        result.addElement(tempbuf.toString());
		}
		return result;
	}

        public static final java.util.Vector<String> getFields(String line,char seperator)
        {
        	return getFields(line,seperator,'\"');
        }

	public static final java.util.Vector<String> getFields(String line, char seperator, char quote)
	{
		java.util.Vector<String> result = new java.util.Vector<String>();
		if (line == null) return result;
		
		boolean in_quote=false;
		StringBuffer tempbuf = new StringBuffer();
		int pos = 0;
		
		while(pos < line.length())
		{
        	    if (in_quote)
        	    {
        	    	if (line.charAt(pos) == quote)
        	    	{
        	    	    if (pos + 1 < line.length() && line.charAt(pos + 1) == quote)
        	    	    {
        	    	    	pos ++;
        	    	    	tempbuf.append(line.charAt(pos));
        	    	    	pos++;	
        	    	    }
        	    	    else
        	    	    {
				/*
       	    	                result.addElement(tempbuf.toString());
        	    	        tempbuf.setLength(0);
				*/
        	    	        in_quote=false;
        	    	        pos ++;
        	    	    }
        	    	}
        	    	else
        	    	{
        	    	   tempbuf.append(line.charAt(pos));
        	    	   pos ++;
        	    	}
        	    }	
        	    else 
        	    {
        	    	if (line.charAt(pos) == quote)
        	    	{
        	    	    in_quote = true;
        	    	    pos ++;
        	    	}
        	    	else if (line.charAt(pos) == seperator)
        	    	{
        	    	    if (tempbuf.length() > 0)
        	    	        result.addElement(tempbuf.toString());
			    else
			        result.addElement(null);
        	    	    tempbuf.setLength(0);
        	    	    pos ++;        	    	    	
        	    	}
        	    	else
        	    	{
        	    	   tempbuf.append(line.charAt(pos));
        	    	   pos ++;        	    		
        	    	}
        	    }	        	    	
		}
		if (in_quote || tempbuf.length()>0)
		    result.addElement(tempbuf.toString());
		else
		    result.addElement(null);	
		return result;
	}	
		
	public static final java.util.Properties getProperties(String line)
	{
		return getProperties(line,false);
	}

	public static final java.util.Properties getProperties(String line,boolean casesensitive)
	{
		return getProperties(getWords(line," "),casesensitive);
	}

	public static final java.util.Properties getProperties(String prop[],boolean casesensitive)
	{
		java.util.Properties result = new java.util.Properties();
		if (prop == null) return result;
		if (prop.length==0) return result;
		int i;
		java.util.Vector props = new java.util.Vector();
		for(i=0;i<prop.length;i++)
			props.addElement(prop[i]);
		return getProperties(props,casesensitive);
	}
	public static final java.util.Properties getProperties(java.util.Vector prop,boolean casesensitive)
	{
		prop.addElement("");
		java.util.Properties result = new java.util.Properties();
		int i;
		if (prop==null || prop.size()==0) return result;
		for(i=prop.size();i>0;i--)
		{
			if (prop.elementAt(i-1) == null)
				prop.removeElementAt(i-1);
		}
		if (prop==null || prop.size()==0) return result;
		String property="",value="",temp="";
		for(i=0;i<prop.size();i++)
		{
			temp = prop.elementAt(i).toString();
			if (property!="")
			{
				if (temp.startsWith("-") || i == prop.size()-1)
				{
					if (casesensitive)
						result.setProperty(property,value.trim());
					else
						result.setProperty(property.toUpperCase(),value.trim());
					if (temp.startsWith("-") && temp.trim().length()>1)
						property=temp.trim().substring(1);
					value="";
					continue;
				}
				else 
					value = value + " " + temp;
			}
			else
			{
				if (temp.startsWith("-") || i == prop.size()-1)
				{
					if (temp.startsWith("-") && temp.trim().length()>1)
						property=temp.trim().substring(1);
					value="";
				}
				else
					value = value + " " + temp;
			}
		}
		return result;
	}

	public static final java.util.Vector<String> getLines(String cmd)
	{
		String line=null;
		java.util.Vector<String> result=new java.util.Vector<String>();
		java.io.StringReader sr = new java.io.StringReader(cmd);
		java.io.BufferedReader br = new java.io.BufferedReader(sr);
		try {
			while((line=br.readLine())!=null) result.addElement(line);
			br.close();
		} catch(java.io.IOException ioe) {}
		return result;
	}

	public static final String[] toStringArray(java.util.Vector<String> src)
	{
		String lines[] = {};
		if (src == null || src.size()==0) return lines;
		/*
		int i;
		lines = new String[src.size()];
		for (i=0;i<src.size();i++)
		{
			if (src.elementAt(i) != null)
  			    lines[i] = src.elementAt(i).toString();
			else
			    lines[i] = null;
		}
		return lines;
		*/
		lines = src.toArray(new String[src.size()]);
		return lines;
	}

        public static final String toHtmlLines(String from)
	{
	     if (from == null) return null;
	     char fromchar[] = from.toCharArray();
	     StringBuffer tobuf = new StringBuffer();
	     for(int i=0;i<fromchar.length;i++)
	     {
		  if (fromchar[i] == '\\')
		  {
			if (i<fromchar.length - 1)
			{
			    i++;
			    if (fromchar[i] == 'n' || fromchar[i] == 'N')
			    {
				tobuf.append("<br />");
			    }
			    else
			    {
				tobuf.append(fromchar[i]);
			    }
			}
		  }
		  else
		  {
			tobuf.append(fromchar[i]);
		  }
	     }
	     return tobuf.toString();
	}
        public static final String toNativeLines(String from)
	{
	     if (from == null) return null;
	     char fromchar[] = from.toCharArray();
	     StringBuffer tobuf = new StringBuffer();
	     for(int i=0;i<fromchar.length;i++)
	     {
		  if (fromchar[i] == '\\')
		  {
			if (i<fromchar.length - 1)
			{
			    i++;
			    if (fromchar[i] == 'n' || fromchar[i] == 'N')
			    {
				tobuf.append("\r\n");
			    }
			    else
			    {
				tobuf.append(fromchar[i]);
			    }
			}
		  }
		  else
		  {
			tobuf.append(fromchar[i]);
		  }
	     }
	     return tobuf.toString();
	}
}