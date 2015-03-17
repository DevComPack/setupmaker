package com.dcp.sm.gui.pivot.tasks;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;


public class TaskWait extends Task<Integer>
{
    @Override
    public Integer execute() throws TaskExecutionException
    {
        try
        {
            wait(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

}
