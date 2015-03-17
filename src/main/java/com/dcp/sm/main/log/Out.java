package com.dcp.sm.main.log;

import java.io.PrintStream;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Component;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;


public class Out
{
    //Log
    private static List<String> log = new ArrayList<String>();//Global Log
    public static List<String> getLog() { return log; }
    private static int displayLevel = 1;//logs to display must be >= than this
    
    private static List<String> compileLog = new ArrayList<String>();//Izpack compile Log
    public static List<String> getCompileLog() { return compileLog; }
    
    //Pivot GUI Component Repaint for log update
    private static Component logger = null;
    public static Component getLogger() { return logger; }
    public static void setLogger(Component logger) { Out.logger = logger; }
    
    //Clear the stream cache
    public static void clearCompileLog() {
        compileLog.clear();
    }
    
    //Add a new empty line
    public static void newLine() {
        print(LOG_LEVEL.INFO, "");
    }
    
    /**
     * Prints a string to the output stream
     * @param text: log text
     * @param outStream: output stream
     */
    public static void print(String text, PrintStream outStream) {
        outStream.println(text);
    }
    
    /**
     * print text with a tag
     * format: [TAG] TXT
     * @param TAG: tag name
     * @param TXT: text to log
     */
    public static void print(LOG_LEVEL level, String msg) {
        print("["+level.toString()+"] "+msg, System.out);
        log.add(msg);
        final String log_TXT = msg;
        
        if (level.value() >= displayLevel)// display if >= than threshold
            log(log_TXT);
    }
    
    public static void log(final String msg) {
        ApplicationContext.queueCallback(new Runnable() {//Enqueue GUI display repaint
            @Override public void run()
            {
                compileLog.add(msg);
                if (getLogger() != null) getLogger().repaint();//Component update
            }
        });
    }
    
}
