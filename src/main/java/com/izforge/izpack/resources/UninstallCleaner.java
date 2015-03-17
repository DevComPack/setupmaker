package com.izforge.izpack.resources;

import java.io.File;

import com.izforge.izpack.util.AbstractUIProcessHandler;


public class UninstallCleaner
{
    public boolean run(AbstractUIProcessHandler handler, String[] args)
    {
        handler.logOutput("Cleaning process tmp directory", false);
        
        String path = args[0];//"$INSTALL_PATH/$EXE_DIR"
        File dir = new File(path);
        if (dir.exists()) {
            handler.logOutput(">"+dir.getAbsolutePath(), false);
            for (File f:dir.listFiles())
                f.delete();
            dir.delete();
        }
        
        return true;
    }

}
