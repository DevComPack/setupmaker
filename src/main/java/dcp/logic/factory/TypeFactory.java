package dcp.logic.factory;


public class TypeFactory
{
    // Pack install type
    public enum INSTALL_TYPE {
        DEFAULT,
        COPY,
        EXTRACT,
        EXECUTE;
        
        public com.dcp.sm.logic.factory.TypeFactory.INSTALL_TYPE cast()
        {
        	switch(this) {
        	default:
        	case DEFAULT:
        		return com.dcp.sm.logic.factory.TypeFactory.INSTALL_TYPE.DEFAULT;
        	case COPY:
        		return com.dcp.sm.logic.factory.TypeFactory.INSTALL_TYPE.COPY;
        	case EXTRACT:
        		return com.dcp.sm.logic.factory.TypeFactory.INSTALL_TYPE.EXTRACT;
        	case EXECUTE:
        		return com.dcp.sm.logic.factory.TypeFactory.INSTALL_TYPE.EXECUTE;
        	}
        }
    }
    
    // Pack install os
    public enum PLATFORM {
        ALL,
        WINDOWS,
        LINUX,
        MAC;
        
        public com.dcp.sm.logic.factory.TypeFactory.PLATFORM cast()
        {
        	switch(this) {
        	default:
        	case ALL:
        		return com.dcp.sm.logic.factory.TypeFactory.PLATFORM.ALL;
        	case WINDOWS:
        		return com.dcp.sm.logic.factory.TypeFactory.PLATFORM.WINDOWS;
        	case LINUX:
        		return com.dcp.sm.logic.factory.TypeFactory.PLATFORM.LINUX;
        	case MAC:
        		return com.dcp.sm.logic.factory.TypeFactory.PLATFORM.MAC;
        	}
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
        Custom;
        
        public com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE cast()
        {
        	switch(this) {
        	case File:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.File;
        	case Folder:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.Folder;
        	case Archive:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.Archive;
        	case Executable:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.Executable;
        	case Setup:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.Setup;
        	case Document:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.Document;
        	case Image:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.Image;
        	case Web:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.Web;
        	case Sound:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.Sound;
        	case Video:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.Video;
        	case Custom:
        	default:
        		return com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE.Custom;
        	}
        }
    }
    
    // Scan mode enumeration
    public static enum SCAN_MODE {
        DEFAULT,
        SIMPLE_SCAN,//Default
        RECURSIVE_SCAN;
        
        public com.dcp.sm.logic.factory.TypeFactory.SCAN_MODE cast()
        {
        	switch(this) {
        	case DEFAULT:
        	case SIMPLE_SCAN:
        	default:
        		return com.dcp.sm.logic.factory.TypeFactory.SCAN_MODE.SIMPLE_SCAN;
        	case RECURSIVE_SCAN:
        		return com.dcp.sm.logic.factory.TypeFactory.SCAN_MODE.RECURSIVE_SCAN;
        	}
        }
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
        
        public com.dcp.sm.logic.factory.TypeFactory.SCAN_FOLDER cast()
        {
        	switch(this) {
        	case DEFAULT:
        	case PACK_FOLDER:
        	default:
        		return com.dcp.sm.logic.factory.TypeFactory.SCAN_FOLDER.PACK_FOLDER;
        	case GROUP_FOLDER:
        		return com.dcp.sm.logic.factory.TypeFactory.SCAN_FOLDER.GROUP_FOLDER;
        	}
        }
    }
    
}
