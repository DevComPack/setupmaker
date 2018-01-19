package com.dcp.sm;

import java.io.File;

import org.apache.pivot.wtk.DesktopApplicationContext;

import com.dcp.sm.gui.pivot.Master;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;

/**
 * DCP Setup Maker Application launch Class
 * @author Said El Imam Said
 */
public class App 
{
    public static void main( String[] args )
    {
    	if (args.length > 0) // Command Line Function
        {
            Out.setLogger(null);
            Out.print(LOG_LEVEL.INFO, "Command Line compiling enabled");
            Out.print(LOG_LEVEL.INFO, "Loading application...");
            new Master();
            
            for (String s: args) {
                if (new File(s).exists() && s.endsWith(".dcp")) {
                    Out.print(LOG_LEVEL.INFO, "Processing dcp file: " + s);
                    System.out.println();
                    
                    Master.facade.process(s);// compile save file with izpack
                }
                else {
                    Out.print(LOG_LEVEL.ERR, "Filepath doesn't exist or file is incorrect! Please give a valid path to a dcp save file.");
                }
            }
        }
        else // GUI Application
        {
            // Apache Pivot bugfix for Java VM versions containing '_'
            String javaVersionFix = System.getProperty("java.runtime.version").split("_")[0];
            System.setProperty("java.vm.version", javaVersionFix);
            Out.print(LOG_LEVEL.INFO, "Fixing Java version to v" + System.getProperty("java.vm.version"));

            DesktopApplicationContext.main(Master.class, args);
        }
    }
}
