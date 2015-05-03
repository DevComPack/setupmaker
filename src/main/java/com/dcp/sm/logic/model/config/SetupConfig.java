package com.dcp.sm.logic.model.config;

import java.io.Serializable;
import java.util.Map;

import com.dcp.sm.config.io.OSValidator;
import com.dcp.sm.logic.factory.TypeFactory.SCAN_FOLDER;
import com.dcp.sm.logic.factory.TypeFactory.SCAN_MODE;

/**
 * IzPack exported setup info configuration
 */
public class SetupConfig implements Serializable
{
    /**
     * Generated serial for file save
     */
    private static final long serialVersionUID = 4289372637132381636L;
    
    // Application
    private String appName="";//Application name
    private String appVersion="";//Application version
    private int appWidth=800, appHeight=600 ;//Application dimensions
    private boolean resizable = false;//If window is resizable
    // Author
    private String authorName="";//Author Name
    private String authorEmail="";//Author Email
    private String appURL="";//Application URL
    // Resources
    private String readmePath="";//Readme Path
    private String licensePath="";//License Path
    private String logoPath="";//Logo Path
    private String sideLogoPath="";//Side Logo Path
    // Langpacks
    private boolean lp_eng = true;
    private boolean lp_fra = false;
    private boolean lp_deu = false;
    private boolean lp_spa = false;
    private boolean lp_cus = false;
    private String lp_iso = "", lp_path = "";
    // Scan
    private String srcPath = "";//Packs source path
    private SCAN_MODE scanMode = SCAN_MODE.SIMPLE_SCAN;// Scan mode (v1.2)
    private SCAN_FOLDER scanFolder = SCAN_FOLDER.PACK_FOLDER;// Scan folders as packs (v1.2)
    private boolean scanFolderTarget = false;// folder target mode (v1.2)
    // Configuration
    private String installPath = "";//Default install directory path
    private boolean forcePath = false;//Whether to force an install path or not
    private boolean registryCheck = false;//Activate registry check for installed version
    private boolean scriptGen = false;//Activate script generation at end of Setup
    // Shortcuts
    private boolean shortcuts = true;//Activate shortcuts creation
    private boolean folderShortcut = false;//Make a shortcut for global install path
    private boolean shToStartMenu = true;//Install shortcuts to start menu
    private boolean shToDesktop = false;//Install shortcuts to desktop

    
    public SetupConfig(String APPNAME, String APPVERSION)
    {
        appName = APPNAME;
        appVersion = APPVERSION;
        Map<String, String> sysEnv = System.getenv();
        if (OSValidator.isWindows()) {// Windows
            authorName = sysEnv.get("USERNAME");// From env var 'USERNAME'
            String USERDNSDOMAIN = sysEnv.get("USERDNSDOMAIN");
            authorEmail = authorName + (USERDNSDOMAIN != null?"@"+USERDNSDOMAIN:"");// From env var 'USERDNSDOMAIN'
            installPath = sysEnv.get("ProgramFiles") + "\\" + appName;// From env var 'ProgramFiles'
        }
        else if (OSValidator.isUnix() || OSValidator.isMac()) {// Unix & Mac
            authorName = sysEnv.get("USER");// From env var 'USER'
            if (authorName == null) {
                authorName = sysEnv.get("HOME");// From env var 'HOME'
                if (authorName != null && authorName.contains("/")) authorName = authorName.substring(authorName.lastIndexOf("/")+1);
            }
            installPath = sysEnv.get("HOME") + "/" + appName;// From env var 'HOME'
        }
        /*else {// Other OS
            authorName = "";
            installPath = "";
        }*/
        authorName = authorName.toUpperCase();
    }
    public SetupConfig(SetupConfig setupConfig)
    {
        appName = setupConfig.appName;
        appVersion = setupConfig.appVersion;
        appWidth = setupConfig.appWidth;
        appHeight = setupConfig.appHeight;
        resizable = setupConfig.resizable;
        
        authorName = setupConfig.authorName;
        authorEmail = setupConfig.authorEmail;
        appURL = setupConfig.appURL;
        
        readmePath = setupConfig.readmePath;
        licensePath = setupConfig.licensePath;
        logoPath = setupConfig.logoPath;
        sideLogoPath = setupConfig.sideLogoPath;
        
        lp_eng = setupConfig.lp_eng;
        lp_fra = setupConfig.lp_fra;
        lp_deu = setupConfig.lp_deu;
        lp_spa = setupConfig.lp_spa;
        lp_cus = setupConfig.lp_cus;
        
        srcPath = setupConfig.srcPath;
        scanMode = setupConfig.scanMode;// 1.2
        scanFolder = setupConfig.scanFolder;// 1.2
        scanFolderTarget = setupConfig.scanFolderTarget;// 1.2
        
        installPath = setupConfig.installPath;
        forcePath = setupConfig.forcePath;
        registryCheck = setupConfig.registryCheck;
        scriptGen = setupConfig.scriptGen;
        
        shortcuts = setupConfig.shortcuts;
        folderShortcut = setupConfig.folderShortcut;
        shToStartMenu = setupConfig.shToStartMenu;
        shToDesktop = setupConfig.shToDesktop;
    }
    
    /**
     * Cast setupConfig model from Non-Maven data (<v1.2.1)
     * @param setupConfig
     */
    public SetupConfig(dcp.logic.model.config.SetupConfig setupConfig)
    {
        appName = setupConfig.appName;
        appVersion = setupConfig.appVersion;
        appWidth = setupConfig.appWidth;
        appHeight = setupConfig.appHeight;
        resizable = setupConfig.resizable;
        
        authorName = setupConfig.authorName;
        authorEmail = setupConfig.authorEmail;
        appURL = setupConfig.appURL;
        
        readmePath = setupConfig.readmePath;
        licensePath = setupConfig.licensePath;
        logoPath = setupConfig.logoPath;
        sideLogoPath = setupConfig.sideLogoPath;
        
        lp_eng = setupConfig.lp_eng;
        lp_fra = setupConfig.lp_fra;
        lp_deu = setupConfig.lp_deu;
        lp_spa = setupConfig.lp_spa;
        lp_cus = setupConfig.lp_cus;
        
        srcPath = setupConfig.srcPath;
        if (setupConfig.scanMode != null)
        	scanMode = setupConfig.scanMode.cast();// 1.2.1 cast
        if (setupConfig.scanFolder != null)
        	scanFolder = setupConfig.scanFolder.cast();// 1.2.1 cast
        scanFolderTarget = setupConfig.scanFolderTarget;
        
        installPath = setupConfig.installPath;
        forcePath = setupConfig.forcePath;
        registryCheck = setupConfig.registryCheck;
        scriptGen = setupConfig.scriptGen;
        
        shortcuts = setupConfig.shortcuts;
        folderShortcut = setupConfig.folderShortcut;
        shToStartMenu = setupConfig.shToStartMenu;
        shToDesktop = setupConfig.shToDesktop;
	}
	//Application
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
    public int getAppWidth() { return appWidth; }
    public void setAppWidth(int appWidth) { this.appWidth = appWidth; }
    public int getAppHeight() { return appHeight; }
    public void setAppHeight(int appHeight) { this.appHeight = appHeight; }
    public boolean isResizable() { return resizable; }
    public void setResizable(boolean resizable) { this.resizable = resizable; }
    
    //Author
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorEmail() { return authorEmail; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }
    public String getAppURL() { return appURL; }
    public void setAppURL(String URL) { appURL = URL; }
    
    //Resources
    public void setReadmePath(String path) { readmePath = path; }
    public String getReadmePath() { return readmePath; }
    public void setLicensePath(String path) { licensePath = path; }
    public String getLicensePath() { return licensePath; }
    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
    public String getSideLogoPath() { return sideLogoPath; }
    public void setSideLogoPath(String sideLogoPath) { this.sideLogoPath = sideLogoPath; }
    
    //Langpacks
    public void setEnglish(boolean ENG) { lp_eng = ENG; }
    public boolean isEnglish() { return lp_eng; }
    public void setFrench(boolean FRA) { lp_fra = FRA; }
    public boolean isFrench() { return lp_fra; }
    public void setGerman(boolean DEU) { lp_deu = DEU; }
    public boolean isGerman() { return lp_deu; }
    public void setSpanish(boolean SPA) { lp_spa = SPA; }
    public boolean isSpanish() { return lp_spa; }
    public void setCustomLang(boolean CUS) { lp_cus = CUS; }
    public boolean isCustomLang() { return lp_cus; }
    public void setCustomLangISO(String ISO) { lp_iso = ISO; }
    public String getCustomLangISO() { return lp_iso; }
    public void setCustomLangPath(String PATH) { lp_path = PATH; }
    public String getCustomLangPath() { return lp_path; }
    
    //Scan
    public SCAN_MODE getScanMode() { return this.scanMode; }
    public void setScanMode(SCAN_MODE scanMode) { this.scanMode = scanMode; }
    public SCAN_FOLDER getScanFolder() { return this.scanFolder; }
    public void setScanFolder(SCAN_FOLDER scanFolder) { this.scanFolder = scanFolder; }
    public boolean getScanTarget() { return this.scanFolderTarget; }
    public void setScanTarget(boolean scanTarget) { this.scanFolderTarget = scanTarget; }
    
    //Configuration
    public String getSrcPath() { return srcPath; }
    public void setSrcPath(String srcPath) { this.srcPath = srcPath; }
    public String getInstallPath() { return installPath; }
    public void setInstallPath(String installPath) { this.installPath = installPath; }
    public boolean isForcePath() { return forcePath; }
    public void setForcePath(boolean forcePath) { this.forcePath = forcePath; }
    public boolean isRegistryCheck() { return registryCheck; }
    public void setRegistryCheck(boolean registryCheck) { this.registryCheck = registryCheck; }
    public boolean isScriptGen() { return scriptGen; }
    public void setScriptGen(boolean scriptGen) { this.scriptGen = scriptGen; }
    
    //Shortcuts
    public boolean isShortcuts() { return shortcuts; }
    public void setShortcuts(boolean shortcuts) { this.shortcuts = shortcuts; }
    public boolean isFolderShortcut() { return folderShortcut; }
    public void setFolderShortcut(boolean folderShortcut) { this.folderShortcut = folderShortcut; }
    public boolean isShToStartMenu() { return shToStartMenu; }
    public void setShToStartMenu(boolean shToStartMenu) { this.shToStartMenu = shToStartMenu; }
    public boolean isShToDesktop() { return shToDesktop; }
    public void setShToDesktop(boolean shToDesktop) { this.shToDesktop = shToDesktop; }
    
}
