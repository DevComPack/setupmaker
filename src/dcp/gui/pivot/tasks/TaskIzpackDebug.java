package dcp.gui.pivot.tasks;

import java.io.IOException;


import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.Component;

import dcp.config.compile.IzpackAntCompiler;
import dcp.main.log.Out;


public class TaskIzpackDebug extends Task<Boolean>
{
    private IzpackAntCompiler compiler = new IzpackAntCompiler();//IzPack Compiler Class
    private String appName;
    
    public TaskIzpackDebug(String TargetPath, Component LOGGER, String appName) {
        compiler.setTarget(TargetPath);
        Out.setLogger(LOGGER);
        this.appName = appName;
    }

    @Override
    public Boolean execute() throws TaskExecutionException
    {
        try
        {
            if (compiler.debug(appName) == 0) {
                Out.print("IZPACK", "Install success.");
            }
            else Out.print("IZPACK", "Install aborted!");
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return abort;
    }

}
