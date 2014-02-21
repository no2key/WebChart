package com.lfx.db;
public final class DateOperator
{
	private final static int Month_Days[]={31,28,31,30,31,30,31,31,30,31,30,31};

	private final static int getMonthDays(int year,int month)
	{
		if((year%400==0) || (year%4==0 && year%100!=0))
		{
			if (month==2)
				return Month_Days[month-1]+1;
		}
		return Month_Days[month-1];
	}

	public final static  String getDay()
	{
		String today = "";
		java.text.SimpleDateFormat sdft = new java.text.SimpleDateFormat();
		sdft.applyPattern("yyyyMMdd");
		today = sdft.format(new java.util.Date());
		sdft = null;
		return today;
	}

	public final static String firstDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;
		rtnval = String.valueOf(yy);
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;
		rtnval = rtnval + "01";
		return rtnval;
	}

	public final static String firstQuaterDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;
		rtnval = String.valueOf(yy);
		mm = (mm-1)/3*3+1;
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;

		rtnval = rtnval + "01";
		return rtnval;
	}

	public final static String lastQuaterDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;
		rtnval = String.valueOf(yy);
		mm = ((mm-1)/3+1)*3;
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;

		rtnval = rtnval + getMonthDays(yy,mm);
		return rtnval;
	}

	public final static String firstYearDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;
		rtnval = String.valueOf(yy);
		rtnval = rtnval + "0101";
		return rtnval;
	}

	public final static String lastYearDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;
		rtnval = String.valueOf(yy);
		rtnval = rtnval + "12"+getMonthDays(yy,12);
		return rtnval;
	}


	public final static String lastDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;
		rtnval = String.valueOf(yy);
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;
		rtnval = rtnval + getMonthDays(yy,mm);
		return rtnval;
	}

	public final static String nextDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;

		if (dd == getMonthDays(yy,mm))
		{
			dd = 1;
			mm = mm+1;
		}
		else
			dd = dd+1;
		if (mm>12)
		{
			mm = 1;
			yy = yy + 1;
		}
		rtnval = String.valueOf(yy);
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;
		if (dd < 10)
			rtnval = rtnval + "0" + dd;
		else
			rtnval = rtnval + dd;
		return rtnval;
	}

	public final static String prevDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;

		if (dd == 1)
		{
			mm = mm - 1;
			if (mm == 0)
			{
				mm = 12;
				yy = yy -1;
			}
			dd = getMonthDays(yy,mm); 
		}
		else
			dd = dd-1;

		rtnval = String.valueOf(yy);
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;
		if (dd < 10)
			rtnval = rtnval + "0" + dd;
		else
			rtnval = rtnval + dd;
		return rtnval;
	}
	
	public final static String addDays (String currday,int days)
	{
		int i;
		int loopcount=Math.abs(days);
		String resultval = currday;
		boolean isnext = days>0?true:false;
		for(i=0;i<loopcount;i++)
		{
			if (isnext)
				resultval = nextDay(resultval);	
			else
				resultval = prevDay(resultval);
		}
		return resultval;
	}

	public final static String nextMonth(String currday)
	{
		boolean isMonthEnd=false;
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;

		if (dd == getMonthDays(yy,mm))
		{
			isMonthEnd = true;
		}
		else
		{
			isMonthEnd = false;
		}

		mm = mm + 1;
		if (mm>12)
		{
			mm = 1;
			yy = yy + 1;
		}
		if (isMonthEnd || dd > getMonthDays(yy,mm))
			dd = getMonthDays(yy,mm);
		rtnval = String.valueOf(yy);
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;
		if (dd < 10)
			rtnval = rtnval + "0" + dd;
		else
			rtnval = rtnval + dd;
		return rtnval;
	}

	public final static String prevMonth(String currday)
	{
		boolean isMonthEnd=false;
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;

		if (dd == getMonthDays(yy,mm))
		{
			isMonthEnd = true;
		}
		else
		{
			isMonthEnd = false;
		}

		mm = mm - 1;
		if (mm == 0)
		{
			mm = 12;
			yy = yy - 1;
		}
		if (isMonthEnd || dd > getMonthDays(yy,mm))
			dd = getMonthDays(yy,mm);
		rtnval = String.valueOf(yy);
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;
		if (dd < 10)
			rtnval = rtnval + "0" + dd;
		else
			rtnval = rtnval + dd;
		return rtnval;
	}

	public final static String addMonths (String currday,int days)
	{
		int i;
		int loopcount=Math.abs(days);
		String resultval = currday;
		boolean isnext = days>0?true:false;
		for(i=0;i<loopcount;i++)
		{
			if (isnext)
				resultval = nextMonth(resultval);	
			else
				resultval = prevMonth(resultval);
		}
		return resultval;
	}

	public final static String firstHarfYearDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;
		rtnval = String.valueOf(yy);
		if (mm > 6) 
			mm = 7;
		else
			mm = 1;
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;

		rtnval = rtnval + "01";
		return rtnval;
	}

	public final static String lastHarfYearDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;
		rtnval = String.valueOf(yy);
		if (mm > 6) 
			mm = 12;
		else
			mm = 6;
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;

		rtnval = rtnval + getMonthDays(yy,mm);
		return rtnval;
	}

	public final static String firstTenDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;
		rtnval = String.valueOf(yy);
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;
		if (dd < 11)
			dd = 1;
		else if (dd < 21)
			dd = 11;
		else
			dd = 21;
		if (dd < 10)
			rtnval = rtnval + "0" + dd;
		else
			rtnval = rtnval + dd;
		return rtnval;
	}

	public final static String lastTenDay(String currday)
	{
		long val;
		int yy,mm,dd;
		String rtnval="";
		if (currday==null) return currday;
		if (currday.length()!=8) return currday;
		if (currday.startsWith("-") ||
			currday.startsWith("+")) return currday;
		try {
			val = Long.valueOf(currday).longValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};
		try {
			yy = Integer.valueOf(currday.substring(0,4)).intValue();
			mm = Integer.valueOf(currday.substring(4,6)).intValue();
			dd = Integer.valueOf(currday.substring(6,8)).intValue();
		}
		 catch(java.lang.NumberFormatException nfe)
		{
			return currday;
		};

		if (mm > 12 || mm < 1) return currday;
		if (dd > getMonthDays(yy,mm) || dd < 1) return currday;
		rtnval = String.valueOf(yy);
		if (mm < 10)
			rtnval = rtnval + "0" + mm;
		else
			rtnval = rtnval + mm;
		if (dd < 11)
			dd = 10;
		else if (dd < 21)
			dd = 20;
		else
			dd = getMonthDays(yy,mm);
		if (dd < 10)
			rtnval = rtnval + "0" + dd;
		else
			rtnval = rtnval + dd;
		return rtnval;
	}

	public final static String[] getDays(String p_from,String p_to)
	{
		int i;
		String days[] = {};
		java.util.Vector result = new java.util.Vector();
		if (DateOperator.nextDay(p_from).equals(p_from)) return days;
		if (DateOperator.nextDay(p_to).equals(p_to)) return days;
		String temp_time=p_from;
		i = temp_time.compareTo(p_to);
		if (i>0) return days;
		while(i <= 0)
		{
			result.addElement(temp_time);
			temp_time = DateOperator.nextDay(temp_time);
			i = temp_time.compareTo(p_to);
		}
		days = new String[result.size()];
		for(i=0;i<result.size();i++)
		{
			days[i]=result.elementAt(i).toString();
		}
		return days;
	}

	public final static String[] getTenDays(String p_from,String p_to)
	{
		int i;
		String days[] = getDays(p_from,p_to);
		java.util.Vector result = new java.util.Vector();
		String temp="xxxxxxxx";
		for (i=0;i<days.length;i++)
		{
			if (!firstTenDay(days[i]).equals(temp))
			{
				temp = firstTenDay(days[i]);
				result.addElement(temp);
			}
		}
		days = new String[result.size()];
		for(i=0;i<result.size();i++)
		{
			days[i]=result.elementAt(i).toString();
		}
		return days;
	}

	public final static String[] getMonthDays(String p_from,String p_to)
	{
		int i;
		String days[] = getTenDays(p_from,p_to);
		java.util.Vector result = new java.util.Vector();
		String temp="xxxxxxxx";
		for (i=0;i<days.length;i++)
		{
			if (!firstDay(days[i]).equals(temp))
			{
				temp = firstDay(days[i]);
				result.addElement(temp);
			}
		}

		days = new String[result.size()];
		for(i=0;i<result.size();i++)
		{
			days[i]=result.elementAt(i).toString();
		}
		return days;
	}

	public final static String[] getQuaterDays(String p_from,String p_to)
	{
		int i;
		String days[] = getMonthDays(p_from,p_to);
		java.util.Vector result = new java.util.Vector();
		String temp="xxxxxxxx";
		for (i=0;i<days.length;i++)
		{
			if (!firstQuaterDay(days[i]).equals(temp))
			{
				temp = firstQuaterDay(days[i]);
				result.addElement(temp);
			}
		}

		days = new String[result.size()];
		for(i=0;i<result.size();i++)
		{
			days[i]=result.elementAt(i).toString();
		}
		return days;
	}

	public final static String[] getHarfYearDays(String p_from,String p_to)
	{
		int i;
		String days[] = getQuaterDays(p_from,p_to);
		java.util.Vector result = new java.util.Vector();
		String temp="xxxxxxxx";
		for (i=0;i<days.length;i++)
		{
			if (!firstHarfYearDay(days[i]).equals(temp))
			{
				temp = firstHarfYearDay(days[i]);
				result.addElement(temp);
			}
		}

		days = new String[result.size()];
		for(i=0;i<result.size();i++)
		{
			days[i]=result.elementAt(i).toString();
		}
		return days;
	}

	public final static String[] getYearDays(String p_from,String p_to)
	{
		int i;
		String days[] = getHarfYearDays(p_from,p_to);
		java.util.Vector result = new java.util.Vector();
		String temp="xxxxxxxx";
		for (i=0;i<days.length;i++)
		{
			if (!firstYearDay(days[i]).equals(temp))
			{
				temp = firstYearDay(days[i]);
				result.addElement(temp);
			}
		}

		days = new String[result.size()];
		for(i=0;i<result.size();i++)
		{
			days[i]=result.elementAt(i).toString();
		}
		return days;
	}

}