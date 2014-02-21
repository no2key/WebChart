package com.lfx.db;
//import java.lang.*;

public class FieldTokenizer implements java.util.Enumeration
{
    private int currentPosition;
    private int newPosition;
    private int maxPosition;
    private String str;
    private String delimiters;
    private boolean retDelims;
    private boolean delimsChanged;

    private char maxDelimChar;

    public FieldTokenizer(String str, String delim, boolean returnDelims)
    {
	currentPosition = 0;
	newPosition = -1;
	delimsChanged = false;
	this.str = str;
	maxPosition = str.length();
	delimiters = delim;
	retDelims = returnDelims;
    }

    public FieldTokenizer(String str, String delim)
    {
	this(str, delim, false);
    }

    public FieldTokenizer(String str)
    {
	this(str, " \t\n\r\f", false);
    }

    private boolean matchDelims(int position)
    {
        if (delimiters == null)
            throw new NullPointerException();
        boolean matched=true;
    	if (position >= maxPosition)
    	{
    		return false;
	}
	if (position+delimiters.length() > maxPosition) return false;
    	for (int i=0;i<delimiters.length();i++)
    	{
    		if (str.charAt(position+i) != delimiters.charAt(i))
    		{
    			matched = false;
    			break;
    		}
    	}
    	return matched;
    }
    
    private int skipDelimiters(int startPos)
    {
        if (delimiters == null)
            throw new NullPointerException();
        int position = startPos;
	while (!retDelims && position < maxPosition) {
	    if (matchDelims(position))
	    {
		position = position + delimiters.length();
	    	break;
	    }
	    position++;
	}
        return position ;
    }

    private int scanToken(int startPos) {
        int position = startPos;
        while (position < maxPosition)
	{
	    if (matchDelims(position))
	    {
	    	//position = position + delimiters.length();
	    	break;
	    }
            position++;
	}
	if (retDelims && (startPos == position))
	{
	    if (matchDelims(position))
	    	position = position + delimiters.length();
        }
        return position;
    }

    public boolean hasMoreTokens()
    {
	newPosition = skipDelimiters(currentPosition);
	return (newPosition < maxPosition);
    }

    public String nextToken()
    {
	currentPosition = (newPosition >= 0 && !delimsChanged) ?  
	    newPosition : skipDelimiters(currentPosition);

	delimsChanged = false;
	newPosition = -1;

	if (currentPosition >= maxPosition)
	    throw new java.util.NoSuchElementException();
	int start = currentPosition;
	currentPosition = scanToken(currentPosition);
	return str.substring(start, currentPosition);
    }

    public String nextToken(String delim)
    {
	delimiters = delim;

	delimsChanged = true;

	return nextToken();
    }

    public boolean hasMoreElements()
    {
	return hasMoreTokens();
    }

    public Object nextElement()
    {
	return nextToken();
    }

    public int countTokens()
    {
	int count = 0;
	int currpos = currentPosition;
	while (currpos < maxPosition) {
            currpos = skipDelimiters(currpos);
	    if (currpos >= maxPosition)
		break;
            currpos = scanToken(currpos);
	    count++;
	}
	return count;
    }
}
