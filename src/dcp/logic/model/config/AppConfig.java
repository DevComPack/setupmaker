package dcp.logic.model.config;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;

/**
 * DevComPack Application configuration data
 * @author SSAIDELI
 *
 */
public class AppConfig implements Serializable
{
    /**
     * class written to save file: conf.dcp
     */
    private static final long serialVersionUID = 1194986597451209924L;

    //Enum
    public static enum SCAN_MODE {//Scan mode enumeration
        DEFAULT,
        SIMPLE_SCAN,//Default
        RECURSIVE_SCAN
    }
    //Constants
    private final static int MAX_RECENT_DIRECTORIES = 5;
    //Flag
    private boolean modified = false;
    public boolean isModified() { return modified; }
    public void setModified(boolean modified) { this.modified = modified; }
    //Attributes
    private String appName;
    private String appVersion;
    //Data
    private LinkedList<File> recentDirs = new LinkedList<File>();
    //Workspace
    private float setVerSplitPaneRatio = 0.6f;
    private float setHorSplitPaneRatio = 0.7f;
    //Tutorial helpers
    private boolean help = true;
    //Scan Modes
    private SCAN_MODE scanMode = SCAN_MODE.SIMPLE_SCAN;
    //Default configurations for setup
    private SetupConfig defaultSetupConfig;
    

    public AppConfig(String app_name, String app_version)
    {
        this.setAppName(app_name);
        this.setAppVersion(app_version);
        this.setDefaultSetupConfig(new SetupConfig("Package", "1.0.0"));
    }
    
    //Attributes
    public String getAppName() { return appName; }
    public void setAppName(String app_name) { this.appName = app_name; setModified(true); }
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String app_version) { this.appVersion = app_version; setModified(true); }
    
    //Data
    public List<File> getRecentDirs() {
        if (recentDirs.size() > 0) {
            List<File> list = new ArrayList<File>();
            Iterator<File> it = recentDirs.descendingIterator();
            while(it.hasNext()) {
                list.add(it.next());
            }
            return list;
        }
        else return null;
    }
    public void addRecentDir(File directory) {
        assert !directory.getAbsolutePath().equals("");
        if (recentDirs.size() == 0 || !directory.equals(recentDirs.getLast())) {
            if (recentDirs.contains(directory)) {
                recentDirs.remove(directory);
            }
            else {
                if (recentDirs.size() == MAX_RECENT_DIRECTORIES)
                    recentDirs.removeFirst();
            }
            recentDirs.addLast(directory);
            setModified(true);
        }
    }
    public boolean removeRecentDir(File directory) {
        assert !directory.getName().equals("");
        return recentDirs.remove(directory);
    }
    
    //Workspace
    public float getSetVerSplitPaneRatio() { return setVerSplitPaneRatio; } 
    public void setSetVerSplitPaneRatio(float setVerSplitPaneRatio) { this.setVerSplitPaneRatio = setVerSplitPaneRatio; setModified(true); }

    public float getSetHorSplitPaneRatio() { return setHorSplitPaneRatio; }
    public void setSetHorSplitPaneRatio(float setHorSplitPaneRatio) { this.setHorSplitPaneRatio = setHorSplitPaneRatio; setModified(true); }
    
    //Helpers functions
    public boolean isHelp() { return help; }
    public void setHelp(boolean help) { this.help = help; setModified(true); }
    
    //Default configurations functions
    public SCAN_MODE getScanMode() { return this.scanMode; }
    public void setScanMode(SCAN_MODE scanMode) { this.scanMode = scanMode; setModified(true); }
    public SetupConfig getDefaultSetupConfig() { return defaultSetupConfig; }
    public void setDefaultSetupConfig(SetupConfig defaultSetupConfig)
    {
        this.defaultSetupConfig = defaultSetupConfig;
        setModified(true);
    }
    
}
