package com.izforge.izpack.resources;

import java.io.File;

import com.izforge.izpack.util.AbstractUIProcessHandler;


public class InstallCleaner
{
    private void dir_delete(File dir) {
        for (File f:dir.listFiles())
            if (f.isDirectory()) dir_delete(f);
            else f.delete();
        dir.delete();
    }
    
    public boolean run(AbstractUIProcessHandler handler, String[] args)
    {
        handler.logOutput("Cleaning process tmp directory", false);
        
        String path = args[0];//"$INSTALL_PATH/$EXE_DIR"
        File dir = new File(path);
        if (dir.exists()) {
            dir_delete(dir);
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        (new InstallCleaner()).run(null, null);
    }
    
}
