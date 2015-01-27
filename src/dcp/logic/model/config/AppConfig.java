package dcp.logic.model.config;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;

import dcp.logic.factory.TypeFactory.BUILD_MODE;
import dcp.logic.factory.TypeFactory.SCAN_MODE;
import dcp.logic.model.config.build.IzpackConfig;
import dcp.logic.model.config.build.NugetConfig;

/**
 * DevComPack Application configuration data
 * @author SSAIDELI
 *
 */
public class AppConfig implements Serializable
{
    /**
     * class written to workspace file: conf.dcp
     */
    private static final long serialVersionUID = 1194986597451209924L;

    // Constants
    private final static int MAX_RECENT_DIRECTORIES = 5;
    // Flag
    transient private boolean modified = false;
    public boolean isModified() { return modified; }
    public void setModified(boolean modified) { this.modified = modified; }
    // Assistant flag
    private boolean help = true;
    // Attributes
    private String appName;
    private String appVersion;
    // Data
    private LinkedList<File> recentDirs = new LinkedList<File>();
    // Workspace
    private float scanHorSplitPaneRatio = 0.25f;
    private float setVerSplitPaneRatio = 0.6f;
    private float setHorSplitPaneRatio = 0.7f;
    // Modes
    private SCAN_MODE scanMode = SCAN_MODE.SIMPLE_SCAN;
    private BUILD_MODE buildMode = BUILD_MODE.IZPACK_BUILD;
    // Default Configurations
    private SetupConfig defaultSetupConfig;
    private IzpackConfig defaultIzpackConf;
    private NugetConfig defaultNugetConf;
    

    public AppConfig(String app_name, String app_version)
    {
        this.appName = app_name;
        this.appVersion = app_version;
        this.defaultSetupConfig = new SetupConfig("Package", "1.0.0");
        this.defaultIzpackConf = new IzpackConfig();
        this.defaultNugetConf = new NugetConfig();
    }
    
    // Attributes
    public String getAppName() { return appName; }
    public void setAppName(String app_name) { this.appName = app_name; setModified(true); }
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String app_version) { this.appVersion = app_version; setModified(true); }
    
    // Data
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
        setModified(true);
        return recentDirs.remove(directory);
    }
    
    // Workspace methods
    public float getScanHorSplitPaneRatio() { return scanHorSplitPaneRatio; } 
    public void setScanHorSplitPaneRatio(float scanHorSplitPaneRatio) { this.scanHorSplitPaneRatio = scanHorSplitPaneRatio; setModified(true); }
    
    public float getSetVerSplitPaneRatio() { return setVerSplitPaneRatio; } 
    public void setSetVerSplitPaneRatio(float setVerSplitPaneRatio) { this.setVerSplitPaneRatio = setVerSplitPaneRatio; setModified(true); }

    public float getSetHorSplitPaneRatio() { return setHorSplitPaneRatio; }
    public void setSetHorSplitPaneRatio(float setHorSplitPaneRatio) { this.setHorSplitPaneRatio = setHorSplitPaneRatio; setModified(true); }
    
    // Helpers methods
    public boolean isHelp() { return help; }
    public void setHelp(boolean help) { this.help = help; setModified(true); }
    
    // Default configurations methods
    public SCAN_MODE getScanMode() { return this.scanMode; }
    public void setScanMode(SCAN_MODE scanMode) { this.scanMode = scanMode; setModified(true); }
    public BUILD_MODE getBuildMode() { return buildMode; }
    public void setBuildMode(BUILD_MODE buildMode) { this.buildMode = buildMode;  setModified(true); }
    
    // Default Configurations methods
    public IzpackConfig getDefaultIzpackConfig() { return defaultIzpackConf; }
    public void setDefaultIzpackConfig(IzpackConfig izpackConf)
    {
        this.defaultIzpackConf = izpackConf;
        setModified(true);
    }
    
    public NugetConfig getDefaultNugetConfig() { return defaultNugetConf; }
    public void setDefaultNugetConfig(NugetConfig nugetConf)
    {
        this.defaultNugetConf = nugetConf;
        setModified(true);
    }
    
    public SetupConfig getDefaultSetupConfig() { return defaultSetupConfig; }
    public void setDefaultSetupConfig(SetupConfig defaultSetupConfig)
    {
        this.defaultSetupConfig = defaultSetupConfig;
        setModified(true);
    }
    
    
}
