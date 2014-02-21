package com.lfx.db;

public class StaticResourceItem
{
    public final static int JS         = 1;
    public final static int CSS        = 2;
    public final static int GIF        = 3;
    public final static int XSL	       = 4;
 
    private String _name = null;
    private int _type = 0;
    private String _textcontent = null;
    private byte[] _bytecontent = null;

    public StaticResourceItem (String name, int type, String textcontent)
    {
	_name = name;
        _type = type;
        _textcontent = textcontent;
    }
    public StaticResourceItem (String name, int type, byte bytecontent[])
    {
	_name = name;
        _type = type;
        _bytecontent = bytecontent;	
    }
    public String getName()
    {
        return _name;
    }
    public int getType()
    {
        return _type;
    }
    public String getTextContent()
    {
        return _textcontent;
    }
    public byte[] getByteContent()
    {
        return _bytecontent;
    }
}