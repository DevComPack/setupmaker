package com.izforge.izpack.resources;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.izforge.izpack.resources.JarExecute;
import com.izforge.izpack.util.AbstractUIProcessHandler;

/**
 * Runs Jar executables
 * using process commands
 * @author Jack RED
 * @param args pack_name, tmpDir
 *
 */
public class JarExecute
{
    
    public boolean run(AbstractUIProcessHandler handler, String[] args)
    {
        String pack_name = args[0];
        handler.logOutput("Executing jar " + pack_name, false);
        
        String tmpDir = args[1].replace('/', '\\');
        String command = "java -jar \"" + tmpDir + pack_name + "\"";
        handler.logOutput("Command: >"+command, false);
        
        try
        {
            Process pSetup = Runtime.getRuntime().exec(command);
            //stream(handler, pSetup.getInputStream(), pSetup.getErrorStream());
            pSetup.waitFor();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        return true;
    }
    
    @SuppressWarnings("unused")
    private boolean stream(final AbstractUIProcessHandler handler, InputStream STDOUT, InputStream STDERR) throws IOException, InterruptedException
    {
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
                            handler.logOutput(line, false);
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
                        while ( (line = brErr.readLine()) != null) {
                            handler.logOutput("ERROR> " + line, false);
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        
        //Threads start
        th_out.start();
        th_err.start();
        
        while(th_err.isAlive());
        
        return true;
    }

}
