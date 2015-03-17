package com.dcp.sm.main.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;


public class StreamDisplay
{
    private static boolean noError = true;//If the error stream returned an error or not (true->not)
    private static int timeout = 1000;//Stream waiting timeout (1s)
    private static boolean wait = true;//Waits for a stream (or not)
    
    /**
     * Sets the display to wait or not for a stream result
     * Should be called before and after the function
     * to recover its state to the default one (true)
     */ 
    public static void setWait(boolean wait)
    {
        StreamDisplay.wait = wait;
    }
    
    /*
     * Sets the timeout waiting period
     * the WAIT function must be activated
     */
    public static void setTimeout(int TIMEOUT)
    {
        if (TIMEOUT<100) TIMEOUT=100;
        StreamDisplay.timeout = TIMEOUT;
    }
    
    //Display the generated output of a stream
    @SuppressWarnings({"unused", "static-access"})
    private static boolean mono_display (LOG_LEVEL level, InputStream STDOUT, InputStream STDERR) throws IOException, InterruptedException {
        String line = null;
        noError = true;
        
        //Output stream declaration
        InputStreamReader isr = new InputStreamReader(STDOUT);
        BufferedReader br = new BufferedReader(isr);
        
        //Error stream declaration
        InputStreamReader isrErr = new InputStreamReader(STDERR);
        BufferedReader brErr = new BufferedReader(isrErr);
        
        //Waiting for an output
        //Break after TIMEOUT miliseconds
        int i=0;
        while (!br.ready() && !brErr.ready()) {
            Thread.currentThread().sleep(100);
            i++;
            if (i>=StreamDisplay.timeout/100 && !StreamDisplay.wait) break;
        }
        if (i>=StreamDisplay.timeout/100 && !StreamDisplay.wait) return true;//Terminated without output
        
        //Output stream display
        if (br.ready())
        while ( (line = br.readLine()) != null)
            Out.print(level, line);
        
        //Error stream display
        if (brErr.ready()) {
            noError = false;
            while ( (line = brErr.readLine()) != null) {
                Out.print(level, "ERR> "+line);
            }
        }
        
        return noError;
    }
    
    private static boolean stereo_display(final LOG_LEVEL level, InputStream STDOUT, InputStream STDERR) throws IOException, InterruptedException {
        noError = true;
        
        //Output stream declaration
        InputStreamReader isr = new InputStreamReader(STDOUT);
        final BufferedReader br = new BufferedReader(isr);
        
        //Error stream declaration
        InputStreamReader isrErr = new InputStreamReader(STDERR);
        final BufferedReader brErr = new BufferedReader(isrErr);
        
        final Thread th_out = new Thread() {
            @Override
            public void run() {
                String line = null;
                try
                {
                    while(!br.ready());
                    if (br.ready())
                        while ( (line = br.readLine()) != null)
                            Out.print(level, line);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            
        };
        
        Thread th_err = new Thread() {
            @Override
            public void run() {
                String line = null;
                try
                {
                    while(!brErr.ready() && th_out.isAlive());
                    if (brErr.ready()) {
                        noError = false;
                        while ( (line = brErr.readLine()) != null) {
                            Out.print(level, "ERR> "+line);
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        
        th_out.start();
        th_err.start();
        
        while(th_err.isAlive());
        
        return noError;
    }
    
    public static boolean show(final LOG_LEVEL level, InputStream STDOUT, InputStream STDERR) throws IOException, InterruptedException {
        return stereo_display(level, STDOUT, STDERR);
    }
}
