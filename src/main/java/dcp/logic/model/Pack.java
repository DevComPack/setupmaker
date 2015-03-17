package dcp.logic.model;

import java.io.Serializable;

import dcp.logic.factory.TypeFactory.FILE_TYPE;
import dcp.logic.factory.TypeFactory.INSTALL_TYPE;
import dcp.logic.factory.TypeFactory.PLATFORM;


public class Pack implements Serializable
{
    /**
     * Write Pack data to save file
     */
    private static final long serialVersionUID = -8775694196832301518L;
    
    //Relations
    public Group group = null;//Pack member of this group
    public String installGroups = "";//Install Groups *
    public Group dependsOnGroup = null;//Group dependency
    public Pack dependsOnPack = null;//Pack dependency
    //Attributes
    public String name = "";//File Name
    public FILE_TYPE fileType = FILE_TYPE.File;//File type
    public double size = 0;//File Size in bytes
    public String path = "";//File Absolute Path
    public INSTALL_TYPE installType = INSTALL_TYPE.COPY;//Install Type
    public PLATFORM installOs = PLATFORM.ALL;//Install OS
    public int arch = 0;// Platform Architecture (32/64 bits|0 = all)
    public String installPath = "";//Install Path, after the $INSTALL_PATH defined
    public String shortcutPath = "";//Relative path for shortcut
    //Info
    public int priority = 0;//Pack install priority
    public String installName = "";//Pack Install Name
    public String installVersion = "1.0.0";//Pack Install Version
    public String description = "";//Pack description
    //Booleans
    public boolean silent_install = false;//If Setup install is passive
    public boolean required = false;//Is required
    public boolean selected = true;//Is selected
    public boolean hidden = false;//Is hidden
    public boolean override = true;//If pack will override existing pack
    public boolean shortcut = false;//If pack will have a shortcut created for it
    //Icon
    public String icon = "";//Icon file path
    
}
