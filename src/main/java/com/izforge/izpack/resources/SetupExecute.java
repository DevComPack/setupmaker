package com.izforge.izpack.resources;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.izforge.izpack.resources.SetupExecute;
import com.izforge.izpack.util.AbstractUIProcessHandler;

/**
 * Executes MSI setup files
 * using process commands
 * on Windows DOS
 * @author Jack RED
 * @param args pack_name, silent_install, tmpDir
 *
 */
public class SetupExecute
{
    
    public boolean run(AbstractUIProcessHandler handler, String[] args)
    {
        String pack_name = args[0];
        boolean silent_install = args[1].equals("true");

        handler.logOutput("Executing setup file " + pack_name, false);
        
        String tmpDir = args[2].replace('/', '\\');
        String command = "msiexec /package \"" + tmpDir + pack_name + "\"" + ((silent_install==true)?" /passive":"") + " /norestart";
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
