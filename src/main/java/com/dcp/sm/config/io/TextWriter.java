package com.dcp.sm.config.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;



public class TextWriter
{
    //Setting default install directory (in res/default-dir.txt)
    public static void writeInstallPath(String path) throws IOException {
        Out.print(LOG_LEVEL.INFO, "Setting default output directory: " + path);
        
        File defaultDirFile = new File("res/default-dir.txt");
        FileWriter writer = new FileWriter(defaultDirFile);
        
        //if (!defaultDirFile.getParentFile().exists()) defaultDirFile.mkdir();
        writer.write(path);
        
        writer.close();
    }
}
