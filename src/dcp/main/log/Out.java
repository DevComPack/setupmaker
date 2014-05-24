package dcp.main.log;

import java.io.PrintStream;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Component;


public class Out
{
    //Log
    private static List<String> log = new ArrayList<String>();//Global Log
    public static List<String> getLog() { return log; }
    private static String[] compileLogTags = {  "IZPACK",
                                                "JAR", "STAX",
                                                "PIVOT_BUILD",
                                                "INFO", "ERROR",
                                                "REG", "IO", "SFTP"
                                              };//Tags to display on log
    
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
        print("INFO", "");
    }
    
    //Prints a string to the outputstream
    public static void print(String text, PrintStream outStream) {
        outStream.println(text);
    }
    
    /*
     * Tags = [IZPACK|JAR|PIVOT_XXX|MAIN|MODEL|REG|IO|STAX|INFO|ERROR]
     */
    public static void print(String TAG, String TXT) {
        print("["+TAG.toUpperCase()+"] "+TXT, System.out);
        log.add(TXT);
        final String log_TXT = TXT;
        
        for(String S:compileLogTags) {//Compile logs to display
            if (S.equalsIgnoreCase(TAG)) {
                log(log_TXT);
                break;
            }
        }
    }
    
    public static void log(final String MSG) {
        ApplicationContext.queueCallback(new Runnable() {//Enqueue GUI display repaint
            @Override public void run()
            {
                compileLog.add(MSG);
                if (getLogger() != null) Out.logger.repaint();//Component update
            }
        });
    }
    
}
