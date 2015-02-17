package dcp.gui.pivot;

import java.io.File;

import org.apache.pivot.wtk.DesktopApplicationContext;

import dcp.logic.factory.TypeFactory.LOG_LEVEL;
import dcp.main.log.Out;


public class Main
{
    public Main()
    {
    }


    /**
     * Launch the Pivot application [ GUI|Command mode ]
     * @param args
     */
    public static void main(String[] args)
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
            DesktopApplicationContext.main(Master.class, args);
    }

}
