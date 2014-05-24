package com.izforge.izpack.resources;


import com.izforge.izpack.resources.Executable;
import com.izforge.izpack.util.AbstractUIProcessHandler;


public class Executable //implements Processable
{
    
    public boolean run(AbstractUIProcessHandler handler, String[] args)
    {
        /*
        int choice = handler.askQuestion(args[0], args[1], AbstractUIProcessHandler.CHOICES_YES_NO);
        */
        handler.logOutput("Ceci est une classe executable!", false);
        handler.logOutput("Arguments:", false);
        
        for(String arg:args)
            handler.logOutput(arg + "; ", false);
        
        return true;
    }

}
