package com.dcp.sm.config.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;



public class BatWriter
{
    private File file;
    public String getTargetFile() throws IOException { return file.getCanonicalPath(); }
    
    
    public BatWriter(String FILE_PATH) throws IOException
    {
        file = new File(FILE_PATH);
        
        if (!file.exists()) file.createNewFile();
    }
    
    //Write setup install batch commands
    //cmd: msiexec /package "path/to/setupFile.msi" /passive /norestart
    public void writeSetup(String pack_name, boolean silent_install) throws IOException {
        Out.print(LOG_LEVEL.INFO, "Writing setup command to: " + file.getAbsolutePath());
        
        String tmpDir = IOFactory.exeTargetDir.replace('/', '\\');
        String command = "msiexec /package \"%INSTALL_PATH%" + tmpDir + pack_name + "\"" + ((silent_install==true)?" /passive":"") + " /norestart";
        
        FileWriter writer = new FileWriter(file);
        writer.write("@ECHO OFF\n");
        writer.write("echo Execute command: " + command + "\n");
        writer.write(command+"\n");
        writer.write("echo " + pack_name + " setup finished.");
        
        writer.close();
    }

    //Write setup install batch commands
    //cmd1: elevate reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$APP_NAME $APP_VER" /f
    //cmd2: java -jar uninstaller.jar
    public void writeClean(String APP_NAME, String APP_VER) throws IOException {
        Out.print(LOG_LEVEL.INFO, "Writing clean command to: " + file.getAbsolutePath());
        
        String com1 = "reg delete \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"+APP_NAME + " " + APP_VER+"\" /f";
        String com2 = "java -jar uninstaller.jar";
        
        FileWriter writer = new FileWriter(file);
        writer.write("@ECHO OFF\n");
        writer.write("echo Command: " + com1 + "\n");
        writer.write(com1+"\n");
        writer.write("echo Command: " + com2 + "\n");
        writer.write(com2+"\n");
        writer.write("echo finished.");
        
        writer.close();
    }
    
    //Delete generated bat file
    public void fileDelete() {
        file.delete();
    }
}
