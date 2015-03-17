package com.dcp.sm.logic.factory;


public class TypeFactory
{
    // Log level type
    public enum LOG_LEVEL {
        DEBUG(0, "DEBUG"),
        INFO(1, "INFO"),
        WARN(2, "WARNING"),
        ERR(3, "ERROR");
        
        private int _level;
        private String _tag;
        private LOG_LEVEL(int level, String tag)
        {
            _level = level;
            _tag = tag;
        }
        @Override
        public String toString()
        {
            return this._tag;
        }
        
        public int value()
        {
            return this._level;
        }
    }
    
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
    
    // Pack IzPack Conditions (condition id on IzPack spec)
    public enum CONDITION {
        ARCH32("arch32"),
        ARCH64("arch64");
        
        private String _value;
        private CONDITION(String value)
        {
            _value = value;
        }
        @Override
        public String toString()
        {
            return this._value;
        }
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
    // Scan folder import type enumeration
    public static enum SCAN_FOLDER {
        DEFAULT("pack"),
        PACK_FOLDER("pack"),//Default
        GROUP_FOLDER("group");
        
        private String _value;
        private SCAN_FOLDER(String value)
        {
            _value = value;
        }
        @Override
        public String toString()
        {
            return this._value;
        }
    }
    
    // Build mode enumeration
    public static enum BUILD_MODE {
        DEFAULT("IzPack"),
        IZPACK_BUILD("IzPack"),//Default
        NUGET_BUILD("NuGet");
        
        private String _value;
        private BUILD_MODE(String value)
        {
            _value = value;
        }
        @Override
        public String toString()
        {
            return this._value;
        }
    }
}
