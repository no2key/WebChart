package com.lfx.db;
public class SQLConvert
{
   public static final SQLQuery parseSQL(String sqlfrom)
   {
   	VariableTable vt = new VariableTable();
   	return parseSQL(sqlfrom, vt);
   }
   
   public static final SQLQuery parseSQL(String sqlfrom,VariableTable vt)
   {
	    boolean in_quote_now = false;
	    StringBuffer result_buf = new StringBuffer();
	    StringBuffer temp_buf   = new StringBuffer();
	    StringBuffer word_buf   = new StringBuffer();

	    java.util.Vector<String> paramlist=new java.util.Vector<String>();
	    java.util.Vector<String> paramtype=new java.util.Vector<String>();

	    Object val;
	    int pos;

	    if (sqlfrom != null)
	    {
	      for(int i=0;i<sqlfrom.length();)
	      {
		if (in_quote_now)
		{
		   if (sqlfrom.charAt(i) != '\'')
		   {
			result_buf.append(sqlfrom.charAt(i));
			i ++;
			continue;
		   }
		   else
		   {
			result_buf.append(sqlfrom.charAt(i));
			if (i < sqlfrom.length() - 1)
			{
			    if (sqlfrom.charAt(i+1) == '\'')
			    {
				result_buf.append(sqlfrom.charAt(i+1));
				i++;
			    }
			    else
			    {
				in_quote_now = false;
			    }
			}
			i ++;
		   }
		}
		else if (sqlfrom.charAt(i) == '\'')
		{
		   result_buf.append(sqlfrom.charAt(i));
		   in_quote_now = true;
		   i ++;
		   continue;
		}
	        else if (sqlfrom.charAt(i) == ':')
        	{
	          i++;
	          if (i<sqlfrom.length())
        	  {
	            if (sqlfrom.charAt(i) == '{')
        	    {
	              i++;
        	      temp_buf.delete(0, temp_buf.length());
	              while(i<sqlfrom.length() && sqlfrom.charAt(i) != '}')
        	      {
	                temp_buf.append(sqlfrom.charAt(i));
        	        i++;
	              }
	              if (i<sqlfrom.length()) i++;
		      result_buf.append('?');

		      {
			word_buf.delete(0, word_buf.length());
			pos = i;
			/* Skip space char */
			while(pos < sqlfrom.length() &&
		              Character.isWhitespace(sqlfrom.charAt(pos))) pos ++;

			/* Make sure the first character is character or under line */
			if (pos < sqlfrom.length() && (Character.isLetter(sqlfrom.charAt(pos)) || sqlfrom.charAt(pos) == '_'))
			{
			    word_buf.append(sqlfrom.charAt(pos));
			    pos ++;
			    while(pos<sqlfrom.length() && 
		                     (Character.isLetter(sqlfrom.charAt(pos)) || 
                		      Character.isDigit(sqlfrom.charAt(pos)) || 
		                      sqlfrom.charAt(i) == '_'))
		            {
	        		  word_buf.append(sqlfrom.charAt(pos));
			          pos ++;
			    }	    
			}
			if (word_buf.length() > 0)
			{
			    if (word_buf.toString().equalsIgnoreCase("OUT"))
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("OUT");
				i = pos;
			    }
			    else if (word_buf.toString().equalsIgnoreCase("INOUT"))
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("INOUT");
				i = pos;
			    }
			    else
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("IN");				
			    }
			}
			else
			{
			    if (pos < sqlfrom.length() && (sqlfrom.charAt(pos) == '=' || sqlfrom.charAt(pos) == ':'))
			    {
			    	paramlist.add(temp_buf.toString());
			    	paramtype.add("OUT");				
			    }
			    else
			    {
			    	paramlist.add(temp_buf.toString());
			    	paramtype.add("IN");				
			    }
			}
		      }
        	    }
	            else if (Character.isLetter(sqlfrom.charAt(i)) || sqlfrom.charAt(i) == '_')
        	    {
	            	temp_buf.delete(0, temp_buf.length());
        	        temp_buf.append(sqlfrom.charAt(i));
	                i++;
	                while(i<sqlfrom.length() && (Character.isLetter(sqlfrom.charAt(i)) 
				|| Character.isDigit(sqlfrom.charAt(i)) || sqlfrom.charAt(i) == '_'))
        	        {
	                   temp_buf.append(sqlfrom.charAt(i));
	                   i++;
	                }
			if (i<sqlfrom.length() && sqlfrom.charAt(i) == '.' ) i++;
			result_buf.append('?');

		      {
			word_buf.delete(0, word_buf.length());
			pos = i;
			/* Skip space char */
			while(pos < sqlfrom.length() &&
		              Character.isWhitespace(sqlfrom.charAt(pos))) pos ++;

			/* Make sure the first character is character or under line */
			if (pos < sqlfrom.length() && (Character.isLetter(sqlfrom.charAt(pos)) || sqlfrom.charAt(pos) == '_'))
			{
			    word_buf.append(sqlfrom.charAt(pos));
			    pos ++;
			    while(pos<sqlfrom.length() && 
		                     (Character.isLetter(sqlfrom.charAt(pos)) || 
                		      Character.isDigit(sqlfrom.charAt(pos)) || 
		                      sqlfrom.charAt(i) == '_'))
		            {
	        		  word_buf.append(sqlfrom.charAt(pos));
			          pos ++;
			    }	    
			}
			if (word_buf.length() > 0)
			{
			    if (word_buf.toString().equalsIgnoreCase("OUT"))
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("OUT");
				i = pos;
			    }
			    else if (word_buf.toString().equalsIgnoreCase("INOUT"))
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("INOUT");
				i = pos;
			    }
			    else
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("IN");				
			    }
			}
			else
			{
			    if (pos < sqlfrom.length() && (sqlfrom.charAt(pos) == '=' || sqlfrom.charAt(pos) == ':'))
			    {
			    	paramlist.add(temp_buf.toString());
			    	paramtype.add("OUT");				
			    }
			    else
			    {
			    	paramlist.add(temp_buf.toString());
			    	paramtype.add("IN");				
			    }
			}
		      }
            	    }
	            else
        	    {
        	    	result_buf.append(':');
		 	if (sqlfrom.charAt(i) > 0)
	        	    result_buf.append(sqlfrom.charAt(i));        	    	
            	 	i++;
            	    }
          	  }
	        }
	        else if (sqlfrom.charAt(i) == '&')
        	{
	          i++;
	          if (i<sqlfrom.length())
        	  {
	            if (sqlfrom.charAt(i) == '{')
        	    {
	              i++;
        	      temp_buf.delete(0, temp_buf.length());
	              while(i<sqlfrom.length() && sqlfrom.charAt(i) != '}')
        	      {
	                temp_buf.append(sqlfrom.charAt(i));
        	        i++;
	              }
	              if (i<sqlfrom.length()) i++;
	              if (temp_buf.length() > 0)
	              {
	                 if (vt.getString(temp_buf.toString()) != null)
	                 {
		      	    result_buf.append(vt.getString(temp_buf.toString()));
		         }
		      }
        	    }
	            else if (Character.isLetter(sqlfrom.charAt(i)) || sqlfrom.charAt(i) == '_')
        	    {
	            	temp_buf.delete(0, temp_buf.length());
        	        temp_buf.append(sqlfrom.charAt(i));
	                i++;
	                while(i<sqlfrom.length() && (Character.isLetter(sqlfrom.charAt(i))
				 || Character.isDigit(sqlfrom.charAt(i)) || sqlfrom.charAt(i) == '_'))
        	        {
	                   temp_buf.append(sqlfrom.charAt(i));
	                   i++;
	                }
			if (i<sqlfrom.length() && sqlfrom.charAt(i) == '.' ) i++;
	                if (temp_buf.length() > 0)
	                {
	                  if (vt.getString(temp_buf.toString()) != null)
	                  {
		      	     result_buf.append(vt.getString(temp_buf.toString()));
		          }
		        }
            	  }
	          else
        	  {
        	  	result_buf.append('&');
		 	if (sqlfrom.charAt(i) > 0)
	        	    result_buf.append(sqlfrom.charAt(i));        	    	
            		i++;
            	  }
          	}
	      }
	      else
	      {
		 if (sqlfrom.charAt(i) > 0)
	        	 result_buf.append(sqlfrom.charAt(i));
        	 i++;
	      }
      	    }
    	  }
	  return new SQLQuery(sqlfrom,result_buf.toString(),
			toArray(paramlist),toArray(paramtype));
     }

   public static final SQLQuery parseCall(String sqlfrom)
   {
   	VariableTable vt = new VariableTable();
   	return parseCall(sqlfrom, vt);
   }
   
   public static final SQLQuery parseCall(String sqlfrom,VariableTable vt)
   {
	    boolean in_quote_now = false;
	    StringBuffer result_buf = new StringBuffer();
	    StringBuffer temp_buf   = new StringBuffer();
	    StringBuffer word_buf   = new StringBuffer();

	    java.util.Vector<String> paramlist=new java.util.Vector<String>();
	    java.util.Vector<String> paramtype=new java.util.Vector<String>();

	    Object val;
	    int pos;

	    if (sqlfrom != null)
	    {
	      for(int i=0;i<sqlfrom.length();)
	      {
		if (in_quote_now)
		{
		   if (sqlfrom.charAt(i) != '\'')
		   {
			result_buf.append(sqlfrom.charAt(i));
			i ++;
			continue;
		   }
		   else
		   {
			result_buf.append(sqlfrom.charAt(i));
			if (i < sqlfrom.length() - 1)
			{
			    if (sqlfrom.charAt(i+1) == '\'')
			    {
				result_buf.append(sqlfrom.charAt(i+1));
				i++;
			    }
			    else
			    {
				in_quote_now = false;
			    }
			}
			i ++;
		   }
		}
		else if (sqlfrom.charAt(i) == '\'')
		{
		   result_buf.append(sqlfrom.charAt(i));
		   in_quote_now = true;
		   i ++;
		   continue;
		}
	        else if (sqlfrom.charAt(i) == ':')
        	{
	          i++;
	          if (i<sqlfrom.length())
        	  {
	            if (sqlfrom.charAt(i) == '{')
        	    {
	              i++;
        	      temp_buf.delete(0, temp_buf.length());
	              while(i<sqlfrom.length() && sqlfrom.charAt(i) != '}')
        	      {
	                temp_buf.append(sqlfrom.charAt(i));
        	        i++;
	              }
	              if (i<sqlfrom.length()) i++;
		      result_buf.append('?');

		      {
			word_buf.delete(0, word_buf.length());
			pos = i;
			/* Skip space char */
			while(pos < sqlfrom.length() &&
		              Character.isSpaceChar(sqlfrom.charAt(pos))) pos ++;

			/* Make sure the first character is character or under line */
			if (pos < sqlfrom.length() && (Character.isLetter(sqlfrom.charAt(pos)) || sqlfrom.charAt(pos) == '_'))
			{
			    word_buf.append(sqlfrom.charAt(pos));
			    pos ++;
			    while(pos<sqlfrom.length() && 
		                     (Character.isLetter(sqlfrom.charAt(pos)) || 
                		      Character.isDigit(sqlfrom.charAt(pos)) || 
		                      sqlfrom.charAt(i) == '_'))
		            {
	        		  word_buf.append(sqlfrom.charAt(pos));
			          pos ++;
			    }	    
			}
			if (word_buf.length() > 0)
			{
			    if (word_buf.toString().equalsIgnoreCase("OUT"))
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("OUT");
				i = pos;
			    }
			    else if (word_buf.toString().equalsIgnoreCase("INOUT"))
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("INOUT");
				i = pos;
			    }
			    else
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("IN");				
			    }
			}
			else
			{
			    if (pos < sqlfrom.length() && (sqlfrom.charAt(pos) == '=' || sqlfrom.charAt(pos) == ':'))
			    {
			    	paramlist.add(temp_buf.toString());
			    	paramtype.add("OUT");				
			    }
			    else
			    {
			    	paramlist.add(temp_buf.toString());
			    	paramtype.add("IN");				
			    }
			}
		      }
        	    }
	            else if (Character.isLetter(sqlfrom.charAt(i)) || sqlfrom.charAt(i) == '_')
        	    {
	            	temp_buf.delete(0, temp_buf.length());
        	        temp_buf.append(sqlfrom.charAt(i));
	                i++;
	                while(i<sqlfrom.length() && (Character.isLetter(sqlfrom.charAt(i)) 
				|| Character.isDigit(sqlfrom.charAt(i)) || sqlfrom.charAt(i) == '_'))
        	        {
	                   temp_buf.append(sqlfrom.charAt(i));
	                   i++;
	                }
			if (i<sqlfrom.length() && sqlfrom.charAt(i) == '.' ) i++;
			result_buf.append('?');

		      {
			word_buf.delete(0, word_buf.length());
			pos = i;
			/* Skip space char */
			while(pos < sqlfrom.length() &&
		              Character.isSpaceChar(sqlfrom.charAt(pos))) pos ++;

			/* Make sure the first character is character or under line */
			if (pos < sqlfrom.length() && (Character.isLetter(sqlfrom.charAt(pos)) || sqlfrom.charAt(pos) == '_'))
			{
			    word_buf.append(sqlfrom.charAt(pos));
			    pos ++;
			    while(pos<sqlfrom.length() && 
		                     (Character.isLetter(sqlfrom.charAt(pos)) || 
                		      Character.isDigit(sqlfrom.charAt(pos)) || 
		                      sqlfrom.charAt(i) == '_'))
		            {
	        		  word_buf.append(sqlfrom.charAt(pos));
			          pos ++;
			    }	    
			}
			if (word_buf.length() > 0)
			{
			    if (word_buf.toString().equalsIgnoreCase("OUT"))
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("OUT");
				i = pos;
			    }
			    else if (word_buf.toString().equalsIgnoreCase("INOUT"))
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("INOUT");
				i = pos;
			    }
			    else
			    {
				paramlist.add(temp_buf.toString());
				paramtype.add("IN");				
			    }
			}
			else
			{
			    if (pos < sqlfrom.length() && (sqlfrom.charAt(pos) == '=' || sqlfrom.charAt(pos) == ':'))
			    {
			    	paramlist.add(temp_buf.toString());
			    	paramtype.add("OUT");				
			    }
			    else
			    {
			    	paramlist.add(temp_buf.toString());
			    	paramtype.add("IN");				
			    }
			}
		      }
            	    }
	            else
        	    {
        	    	result_buf.append(':');
		 	if (sqlfrom.charAt(i) > 0)
	        	    result_buf.append(sqlfrom.charAt(i));        	    	
            	 	i++;
            	    }
          	  }
	        }
	        else if (sqlfrom.charAt(i) == '&')
        	{
	          i++;
	          if (i<sqlfrom.length())
        	  {
	            if (sqlfrom.charAt(i) == '{')
        	    {
	              i++;
        	      temp_buf.delete(0, temp_buf.length());
	              while(i<sqlfrom.length() && sqlfrom.charAt(i) != '}')
        	      {
	                temp_buf.append(sqlfrom.charAt(i));
        	        i++;
	              }
	              if (i<sqlfrom.length()) i++;
	              if (temp_buf.length() > 0)
	              {
	                 if (vt.getString(temp_buf.toString()) != null)
	                 {
		      	    result_buf.append(vt.getString(temp_buf.toString()));
		         }
		      }
        	    }
	            else if (Character.isLetter(sqlfrom.charAt(i)) || sqlfrom.charAt(i) == '_')
        	    {
	            	temp_buf.delete(0, temp_buf.length());
        	        temp_buf.append(sqlfrom.charAt(i));
	                i++;
	                while(i<sqlfrom.length() && (Character.isLetter(sqlfrom.charAt(i))
				 || Character.isDigit(sqlfrom.charAt(i)) || sqlfrom.charAt(i) == '_'))
        	        {
	                   temp_buf.append(sqlfrom.charAt(i));
	                   i++;
	                }
			if (i<sqlfrom.length() && sqlfrom.charAt(i) == '.' ) i++;
	                if (temp_buf.length() > 0)
	                {
	                  if (vt.getString(temp_buf.toString()) != null)
	                  {
		      	     result_buf.append(vt.getString(temp_buf.toString()));
		          }
		        }
            	  }
	          else
        	  {
        	  	result_buf.append('&');
		 	if (sqlfrom.charAt(i) > 0)
	        	    result_buf.append(sqlfrom.charAt(i));        	    	
            		i++;
            	  }
          	}
	      }
	      else
	      {
		 if (sqlfrom.charAt(i) > 0)
	        	 result_buf.append(sqlfrom.charAt(i));
        	 i++;
	      }
      	    }
    	  }
    	  pos = 0;
    	  while(pos < result_buf.length() && Character.isSpaceChar(result_buf.charAt(pos)))
    	        pos ++;
    	  if (pos < result_buf.length() && result_buf.charAt(pos) != '?')
    	  {
    	  	result_buf.insert(0,"call ");
    	  }
	  return new SQLQuery(sqlfrom,result_buf.toString(),
			toArray(paramlist),toArray(paramtype));
     }

     private static final String[] toArray(java.util.Vector<String> v)
     {
		String r_arr[] = {};
		if (v == null) return null;
		if (v.size()==0) return r_arr;
		/*
		int i;
		r_arr = new String[v.size()];
		for(i=0;i<v.size();i++)
		{
			r_arr[i] = v.elementAt(i).toString();
		}
		return r_arr;
		*/
		return v.toArray(new String[v.size()]);
    }     
}