package dcp.logic.factory;


public class TypeFactory
{
    // Pack install type
    public enum INSTALL_TYPE {
        DEFAULT,
        COPY,
        EXTRACT,
        EXECUTE
    }
    
    // Pack install os
    public enum PLATFORM {
        ALL,
        WINDOWS,
        LINUX,
        MAC
    }
    
    // File Type
    public static enum FILE_TYPE {
        File,
        Folder,
        Archive,
        Executable,
        Setup,
        Document,
        Image,
        Web,
        Sound,
        Video,
        Custom
    }
    
    // Scan mode enumeration
    public static enum SCAN_MODE {
        DEFAULT,
        SIMPLE_SCAN,//Default
        RECURSIVE_SCAN
    }
    
    // Build mode enumeration
    public static enum BUILD_MODE {
        DEFAULT,
        IZPACK_BUILD,//Default
        NUGET_BUILD
    }
}
