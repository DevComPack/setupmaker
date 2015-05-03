package com.dcp.sm.logic.model;

import java.io.File;
import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.content.TreeNode;
import org.apache.pivot.wtk.media.Image;

import com.dcp.sm.logic.factory.GroupFactory;
import com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE;
import com.dcp.sm.logic.factory.TypeFactory.INSTALL_TYPE;
import com.dcp.sm.logic.factory.TypeFactory.PLATFORM;

/**
 * Pack Model
 * @author Said
 *
 */
public class Pack implements Serializable
{
    /**
     * Write Pack data to save file
     */
    private static final long serialVersionUID = -8775694196832301518L;
    
    //Relations
    private Group group = null;//Pack member of this group
    private String installGroups = "";//Install Groups *
    private Group dependsOnGroup = null;//Group dependency
    private Pack dependsOnPack = null;//Pack dependency
    //Attributes
    private String name = "";//File Name
    private FILE_TYPE fileType = FILE_TYPE.File;//File type
    private double size = 0;//File Size in bytes
    private String path = "";//File Absolute Path
    private INSTALL_TYPE installType = INSTALL_TYPE.COPY;//Install Type
    private PLATFORM installOs = PLATFORM.ALL;//Install OS
    private int arch = 0;// Platform Architecture (32/64 bits|0 = all)
    private String installPath = "";//Install Path, after the $INSTALL_PATH defined
    private String shortcutPath = "";//Relative path for shortcut
    //Info
    private int priority = 0;//Pack install priority
    private String installName = "";//Pack Install Name
    private String installVersion = "1.0.0";//Pack Install Version
    private String description = "";//Pack description
    //Booleans
    private boolean silent_install = false;//If Setup install is passive
    private boolean required = false;//Is required
    private boolean selected = true;//Is selected
    private boolean hidden = false;//Is hidden
    private boolean override = true;//If pack will override existing pack
    private boolean shortcut = false;//If pack will have a shortcut created for it
    //Icon
    private String icon = "";//Icon file path
    private transient Image imgIcon = null;//Icon Image
    
    //Functions
    public String getBaseName() {//Name without extension
        if (name.contains("."))
            return name.substring(0,name.lastIndexOf("."));
        return name;
    }
    // extract version number from file name<
    public static String getVersionFromName(String name) {
        try {
            if (Pattern.compile(".*[0-9]+([._\\-a-zA-Z][0-9]+)+.*").matcher(name).find()) {
                String[] borders = name.split("[0-9]+([._\\-a-zA-Z][0-9]+)+[a-zA-Z]?");
                if (borders.length > 0)
                    return name.substring(borders[0].length(), name.length() - (borders.length > 1?borders[1].length():0));
                else
                    return name;
            }
            return "1.0.0";
        }
        catch(IllegalStateException e) {
            System.out.println(e);
            return "1.0.0";
        }
    }
    
    //Constructors
    public Pack(String NAME, Image ICON) {
        this.name = NAME;
        this.description = name;
        this.installName = name;
        this.installVersion = getVersionFromName(name);
        setIcon(ICON);
    }
    public Pack(Pack pack) {//Copy another Pack data
        name = pack.name;
        priority = pack.priority;
        fileType = pack.fileType;
        installName = pack.installName;
        installVersion = pack.installVersion; // 1.1
        size = pack.size;
        path = pack.path;
        group = pack.group;
        installType = pack.installType;
        if (pack.installOs != null) installOs = pack.installOs;
        arch = pack.arch; // 1.2
        installPath = pack.installPath;
        shortcutPath = pack.shortcutPath;
        installGroups = pack.installGroups;
        description = pack.description;
        if (pack.dependsOnGroup!=null) dependsOnGroup = pack.dependsOnGroup;
        if (pack.dependsOnPack!=null) dependsOnPack = pack.dependsOnPack;
        required = pack.required;
        selected = pack.selected;
        hidden = pack.hidden;
        override = pack.override;
        shortcut = pack.shortcut;
        silent_install = pack.silent_install;
        icon = pack.icon;
        imgIcon = pack.imgIcon;
    }
    
    /**
     * Cast pack model from Non-Maven data (<v1.2.1)
     * @param obj
     */
    public Pack(dcp.logic.model.Pack pack) {
        name = pack.name;
        priority = pack.priority;
        fileType = pack.fileType.cast();
        installName = pack.installName;
        installVersion = pack.installVersion; // 1.1
        size = pack.size;
        path = pack.path;
        if (pack.group != null) group = new Group(pack.group);
        installType = pack.installType.cast();
        if (pack.installOs != null) installOs = pack.installOs.cast();
        arch = pack.arch; // 1.2
        installPath = pack.installPath;
        shortcutPath = pack.shortcutPath;
        installGroups = pack.installGroups;
        description = pack.description;
        if (pack.dependsOnGroup != null) dependsOnGroup = GroupFactory.get(new Group(pack.dependsOnGroup).getPath());
        if (pack.dependsOnPack != null) dependsOnPack = new Pack(pack.dependsOnPack);
        required = pack.required;
        selected = pack.selected;
        hidden = pack.hidden;
        override = pack.override;
        shortcut = pack.shortcut;
        silent_install = pack.silent_install;
        icon = pack.icon;
        setIcon(imgIcon);
	}
    
    
	//Overrides
    @Override public boolean equals(Object obj)//Packs compare
    {
        if (obj==null) return false;
        if (obj instanceof Pack) {//With a Pack model
            Pack P = (Pack) obj;
            return this.name.toLowerCase().equals(P.getName().toLowerCase())//Name
                    && this.path.toLowerCase().equals(P.getPath().toLowerCase());//Path
        }
        else if (obj instanceof TreeNode) {//With a TreeView Node
            TreeNode node = (TreeNode) obj;
            return this.name.toLowerCase().equals(node.getText().toLowerCase())//Name
                    && this.group.equals(node.getParent());//Group
        }
        else return super.equals(obj);
    }
    @Override
    public String toString()
    {
        return this.name;
    }
    
    /**
     * Update pack name and path from new file
     * @param new_file
     */
    public void updatePack(File new_file) {
        name = new_file.getName();
        path = new_file.getAbsolutePath();
    }
    
    //Relation functions
    public Group getGroup() { return group; }
    public String getGroupName() {
        if (group != null)
            return group.getName();
        return "";
    }
    public String getGroupPath() {
        if (group != null)
            return group.getPath();
        return "";
    }
    public void setGroup(Group group) { this.group = group; }
    
    public String getInstallGroups() { return installGroups; }
    public void setInstallGroups(String installGroups) { this.installGroups = installGroups; }
    
    public Group getGroupDependency() { return dependsOnGroup; }
    public void setGroupDependency(Group group) { this.dependsOnGroup = group; this.dependsOnPack = null; }
    public Pack getPackDependency() { return dependsOnPack; }
    public void setPackDependency(Pack pack) { this.dependsOnPack = pack; this.dependsOnGroup = null; }
    
    
    //Getters & Setters
    public String getName() { return name; }
    
    public double getSize() { return size; }
    public void setSize(double size) { this.size = size; }

    public String getIconPath() { return icon; }
    public Image getIcon() { return imgIcon; }
    public void setIcon(String ICON)
    {
        this.icon = ICON;
        try {
            setIcon( Image.load(getClass().getResource(icon)) );
        } catch (TaskExecutionException e) {
            e.printStackTrace();
        }
    }
    public void setIcon(Image ICON) { imgIcon = ICON; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getInstallPath() { return installPath; }
    public void setInstallPath(String installPath) { this.installPath = installPath; }
    
    public String getShortcutPath() { return shortcutPath; }
    public void setShortcutPath(String shortcutPath) {
        if (shortcutPath.length() > 0 && !(shortcutPath.substring(0, 1).equals("/") || shortcutPath.substring(0, 1).equals("\\")))
            shortcutPath = "/" + shortcutPath;
        this.shortcutPath = shortcutPath;
    }
    
    
    //Info functions
    public String getInstallName() { return installName; }
    public void setInstallName(String installName) { this.installName = installName; }

    public String getInstallVersion() { return installVersion; }
    public void setInstallVersion(String installVersion) { this.installVersion = installVersion; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public INSTALL_TYPE getInstallType() { return installType; }
    public void setInstallType(INSTALL_TYPE IT) { installType = IT; }

    public PLATFORM getInstallOs() { return installOs; }
    public void setInstallOs(PLATFORM OS) { installOs = OS; }
    
    public int getArch() { return arch; }
    public void setArch(int arch) {
        assert arch == 0 || arch == 32 || arch == 64;
        this.arch = arch;
    }
    
    public int getPriority() { return priority+1; }
    public void setPriority(int priority) { this.priority = priority; }
    
    
    //Boolean functions
    public FILE_TYPE getFileType() { return fileType; }
    public void setFileType(FILE_TYPE fileType) { this.fileType = fileType; }
    public boolean isSilentInstall() { return this.silent_install; }
    public void setSilentInstall(boolean silent_install) { this.silent_install = silent_install; }
    
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) {
        this.required = required;
        if (required == true && this.getPackDependency() != null) {
            this.getPackDependency().setRequired(true);
        }
    }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected == true && this.getPackDependency() != null) {
            this.getPackDependency().setSelected(true);
        }
    }
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
    public boolean isOverride() { return override; }
    public void setOverride(boolean override) { this.override = override; }
    public boolean isShortcut() { return shortcut; }
    public void setShortcut(boolean shortcut) { this.shortcut = shortcut; }
    
}
