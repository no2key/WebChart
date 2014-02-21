package com.lfx.web;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.DecimalFormatSymbols;

public class WebChartNumberFormat extends DecimalFormat
{
    public WebChartNumberFormat()
    {
       super();
    }
    public WebChartNumberFormat(String pattern)
    {
       super(pattern);
    }
    public WebChartNumberFormat(String pattern, DecimalFormatSymbols symbols)
    {
       super(pattern, symbols);
    }
    public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) 
    {
	StringBuffer buf = null;
	if (number >= 10000000)
	{
	    buf = super.format(number/1000000, result, fieldPosition);
	    buf.append('m');
	}
	else if (number >= 10000)
	{
	    buf = super.format(number/1000, result, fieldPosition);
	    buf.append('k');
	}
	else if (number <= -10000000)
	{
	    buf = super.format(number/1000000, result, fieldPosition);
	    buf.append('m');
	}
	else if (number <= -10000)
	{
	    buf = super.format(number/1000, result, fieldPosition);
	    buf.append('k');
	}
	else
	{
	    buf = super.format(number, result, fieldPosition);
	}
	return buf;
    }
    public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) 
    {
	StringBuffer buf = null;
	if (number >= 10000000)
	{
	    buf = super.format(number/1000000, result, fieldPosition);
	    buf.append('m');
	}
	else if (number >= 10000)
	{
	    buf = super.format(number/1000, result, fieldPosition);
	    buf.append('k');
	}
	else if (number <= -10000000)
	{
	    buf = super.format(number/1000000, result, fieldPosition);
	    buf.append('m');
	}
	else if (number <= -10000)
	{
	    buf = super.format(number/1000, result, fieldPosition);
	    buf.append('k');
	}
	else
	{
	    buf = super.format(number, result, fieldPosition);
	}
	return buf;
    }
}