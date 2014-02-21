package com.lfx.db;
import java.sql.Types;
public final class SQLTypes
{
	public static final int  getTypeID(String type)
	{
		if (type == null) return Types.VARCHAR;
		String s_type = type.trim().toUpperCase();
		if (s_type.equals("CHAR"))
			return Types.CHAR;
		else if (s_type.equals("VARCHAR"))
			return Types.VARCHAR;
		else if (s_type.equals("LONGVARCHAR"))
			return Types.LONGVARCHAR;
		else if (s_type.equals("BINARY"))
			return Types.BINARY;
		else if (s_type.equals("VARBINARY"))
			return Types.VARBINARY;
		else if (s_type.equals("LONGVARBINARY"))
			return Types.LONGVARBINARY;
		else if (s_type.equals("NUMERIC"))
			return Types.NUMERIC;
		else if (s_type.equals("DECIMAL"))
			return Types.DECIMAL;
		else if (s_type.equals("BIT"))
			return Types.BIT;
		else if (s_type.equals("TINYINT"))
			return Types.TINYINT;
		else if (s_type.equals("SMALLINT"))
			return Types.SMALLINT;
		else if (s_type.equals("INTEGER"))
			return Types.INTEGER;
		else if (s_type.equals("BIGINT"))
			return Types.BIGINT;
		else if (s_type.equals("REAL"))
			return Types.REAL;
		else if (s_type.equals("FLOAT"))
			return Types.FLOAT;
		else if (s_type.equals("DOUBLE"))
			return Types.DOUBLE;
		else if (s_type.equals("DATE"))
			return Types.DATE;
		else if (s_type.equals("TIME"))
			return Types.TIME;
		else if (s_type.equals("TIMESTAMP"))
			return Types.TIMESTAMP;
		else if (s_type.equals("REF"))
			return Types.REF;
		else
			return Types.VARCHAR;
	}

	public static final String  getTypeName(int type)
	{
		switch(type)
		{
			case Types.CHAR:
				return "CHAR";
			case Types.VARCHAR:
				return "VARCHAR";
			case Types.LONGVARCHAR:
				return "LONGVARCHAR";
			case Types.BINARY:
				return "BINARY";
			case Types.VARBINARY:
				return "VARBINARY";
			case Types.LONGVARBINARY:
				return "LONGVARBINARY";
			case Types.NUMERIC:
				return "NUMERIC";
			case Types.DECIMAL:
				return "DECIMAL";
			case Types.BIT:
				return "BIT";
			case Types.TINYINT:
				return "TINYINT";
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.BIGINT:
				return "BIGINT";
			case Types.REAL:
				return "REAL";
			case Types.FLOAT:
				return "FLOAT";
			case Types.DOUBLE:
				return "DOUBLE";
			case Types.DATE:
				return "DATE";
			case Types.TIME:
				return "TIME";
			case Types.TIMESTAMP:
				return "TIMESTAMP";
			case Types.CLOB:
				return "CLOB";
			case Types.BLOB:
				return "BLOB";
			case Types.REF:
				return "REF";
		}
		return "VARCHAR";
	}

	public static final Class  getTypeClass(int type)
	{
		switch(type)
		{
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				return String.class;
			case Types.NUMERIC:
			case Types.DECIMAL:
				return java.math.BigDecimal.class;
			case Types.BIT:
				return Boolean.class;
			case Types.TINYINT:
				return Byte.class;
			case Types.SMALLINT:
			case Types.INTEGER:
				return Integer.class;
			case Types.BIGINT:
				return Long.class;
			case Types.REAL:
				return Float.class;
			case Types.FLOAT:
			case Types.DOUBLE:
				return Double.class;
			case Types.DATE:
				return java.sql.Date.class;
			case Types.TIME:
				return java.sql.Time.class;
			case Types.TIMESTAMP:
				return java.sql.Timestamp.class;
		}
		return Object.class;
	}
	public static final Object getValue(int type,Object oldval) throws NumberFormatException
	{
		if (oldval == null)
			return null;
		switch(type)
		{
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				return oldval.toString();
			case Types.NUMERIC:
			case Types.DECIMAL:
				return new java.math.BigDecimal(oldval.toString().trim());
				//return Double.valueOf(oldval.toString());
			case Types.BIT:
				return Boolean.valueOf(oldval.toString());
			case Types.TINYINT:
				if (oldval instanceof Number)
				    return Byte.valueOf(((Number)oldval).byteValue());
				else
				    return Byte.valueOf(oldval.toString().trim());
			case Types.SMALLINT:
			case Types.INTEGER:
				if (oldval instanceof Number)
				    return Integer.valueOf(((Number)oldval).intValue());
				else
				    return Integer.valueOf(oldval.toString().trim());
			case Types.BIGINT:
				if (oldval instanceof Number)
				    return Long.valueOf(((Number)oldval).longValue());
				else
				    return Long.valueOf(oldval.toString().trim());
			case Types.REAL:
				if (oldval instanceof Number)
				    return Float.valueOf(((Number)oldval).floatValue());
				else
				    return Float.valueOf(oldval.toString().trim());
			case Types.FLOAT:
			case Types.DOUBLE:
				if (oldval instanceof Number)
				    return Double.valueOf(((Number)oldval).doubleValue());
				else
				    return Double.valueOf(oldval.toString().trim());
			case Types.DATE:
				return java.sql.Date.valueOf(oldval.toString().trim());
			case Types.TIME:
				return java.sql.Time.valueOf(oldval.toString().trim());
			case Types.TIMESTAMP:
				return java.sql.Timestamp.valueOf(oldval.toString().trim());
		}
		return oldval;
	}
}