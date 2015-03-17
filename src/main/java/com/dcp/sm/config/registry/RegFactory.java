package com.dcp.sm.config.registry;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;





public class RegFactory
{
    //private List<String> subkeys = new ArrayList<String>();
    private List<String> programs = new ArrayList<String>();
    
    public RegFactory() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        super();
        listPrograms();//Gets the list of all installed programs in the list "programs"
    }

    public String winVersion() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        String value = WinRegistry.readString (
                WinRegistry.HKEY_LOCAL_MACHINE,                             //HKEY
               "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion",           //Key
               "ProductName");                                              //ValueName
        
        return value;
    }

    public void javaVersion() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        String value;
        String version = null;

        for (String key:programs) {
            value = WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,
                        "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"+key,
                        "DisplayName");
            
            if (value != null) {
                // search if java installed
                if (value.toLowerCase().contains("java")) {
                System.out.print(key+"/"+value+"; ");
                version = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,
                        "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"+key,
                        "DisplayVersion");
                Out.print(LOG_LEVEL.DEBUG, "Java Version: " + version);// display installed version
                }
            }
        }
    }
    
    public String getJavaPath() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        String value;
        String path = null;
        
        String date;
        Date InstallDate = null;
        Date newInstallDate = null;
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        
        Out.print(LOG_LEVEL.INFO, "Registry scan...");
        for (String key:programs) {
            value = WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,
                        "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"+key,
                        "DisplayName");
            
            if (value != null) {
                // search if java installed
                if (value.toLowerCase().contains("java ") || value.toLowerCase().contains("java(tm) ")) {
                    String contact = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,
                            "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"+key,
                            "Contact");
                    
                    if (contact != null &&
                            (contact.equalsIgnoreCase("http://java.com") || contact.equalsIgnoreCase("http://java.sun.com") )) {
                        try
                        {
                            date = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,
                                    "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"+key,
                                    "InstallDate");
                            newInstallDate = df.parse(date);
                            
                            if ((InstallDate == null || InstallDate.before(newInstallDate))
                                    && value.contains("Development Kit")) {
                                path =  WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,
                                        "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"+key,
                                        "InstallLocation");
                                
                                InstallDate = newInstallDate;
                                Out.print(LOG_LEVEL.DEBUG, "["+new SimpleDateFormat("dd/MM/yyyy").format(newInstallDate)+"] " + value + " > " + path);
                            }
                        } catch (ParseException e) { e.printStackTrace(); }
                    }   
                }
            }
        }
        
        return path;
    }

    private void listPrograms() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        programs = WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE,
                                                "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall");
    }
    
}
