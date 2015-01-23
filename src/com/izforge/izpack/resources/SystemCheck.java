package com.izforge.izpack.resources;


public class SystemCheck
{
    public static boolean x32 = is32bits();
    public static boolean x64 = is64bits();
    
    private static boolean is32bits() {
        return System.getProperty("sun.arch.data.model").equals("32");
    }
    private static boolean is64bits() {
        return System.getProperty("sun.arch.data.model").equals("64");
    }

    public static void main(String[] args)
    {
        System.out.println("OS Architecture : " + System.getProperty("os.arch"));
        System.out.println("OS Name : " + System.getProperty("os.name"));
        System.out.println("OS Version : " + System.getProperty("os.version"));
        System.out.println("Data Model : " + System.getProperty("sun.arch.data.model"));
        
        // WINDOWS 64
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

        String realArch = arch.endsWith("64")
                          || wow64Arch != null && wow64Arch.endsWith("64")
                              ? "64" : "32";
        System.out.println(realArch);
    }

}
