package com.lfx.perf;
import com.lfx.db.DBRowCache;

public abstract class LFXPerfData
{
    public abstract void getNextValue(String dbname);
    public abstract DBRowCache getPerfData();
}