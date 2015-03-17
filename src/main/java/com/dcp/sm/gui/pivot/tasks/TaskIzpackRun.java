package com.dcp.sm.gui.pivot.tasks;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;



import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.Component;

import com.dcp.sm.config.compile.IzpackAntCompiler;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;


public class TaskIzpackRun extends Task<Boolean>
{
    IzpackAntCompiler compiler = new IzpackAntCompiler();//IzPack Compiler Class
    String appName;
    
    public TaskIzpackRun(String TargetPath, Component LOGGER, String appName)
    {
        compiler.setTarget(TargetPath);;
        Out.setLogger(LOGGER);
        this.appName = appName;
    }
    
    @Override
    public Boolean execute() throws TaskExecutionException
    {
        try
        {
            if (compiler.run(appName) != 0)
                Out.print(LOG_LEVEL.ERR, "Install aborted!");
            
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
