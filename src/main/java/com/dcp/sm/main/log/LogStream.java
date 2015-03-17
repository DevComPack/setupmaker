package com.dcp.sm.main.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;


public class LogStream extends PrintStream
{

    public LogStream(File log_file) throws FileNotFoundException
    {
        super(log_file);
    }
    
    
    public LogStream(String s) throws FileNotFoundException
    {
        super(s);
    }

}
