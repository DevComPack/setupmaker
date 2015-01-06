package dcp.gui.pivot.tasks;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.Component;

import dcp.config.compile.IzpackAntCompiler;
import dcp.main.log.Out;


public class TaskIzpackRun extends Task<Boolean>
{
    IzpackAntCompiler compiler = new IzpackAntCompiler();//IzPack Compiler Class
    
    public TaskIzpackRun(String TargetPath, Component LOGGER)
    {
        compiler.setTarget(TargetPath);;
        Out.setLogger(LOGGER);
    }
    
    @Override
    public Boolean execute() throws TaskExecutionException
    {
        try
        {
            if (compiler.run() != 0)
                Out.print("IZPACK", "Install aborted!");
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
        return abort;
    }

}
