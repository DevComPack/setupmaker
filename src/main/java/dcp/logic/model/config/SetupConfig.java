package dcp.logic.model.config;

import java.io.Serializable;

import dcp.logic.factory.TypeFactory.SCAN_FOLDER;
import dcp.logic.factory.TypeFactory.SCAN_MODE;


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
    public String appName="";//Application name
    public String appVersion="";//Application version
    public int appWidth=800, appHeight=600 ;//Application dimensions
    public boolean resizable = false;//If window is resizable
    // Author
    public String authorName="";//Author Name
    public String authorEmail="";//Author Email
    public String appURL="";//Application URL
    // Resources
    public String readmePath="";//Readme Path
    public String licensePath="";//License Path
    public String logoPath="";//Logo Path
    public String sideLogoPath="";//Side Logo Path
    // Langpacks
    public boolean lp_eng = true;
    public boolean lp_fra = false;
    public boolean lp_deu = false;
    public boolean lp_spa = false;
    public boolean lp_cus = false;
    public String lp_iso = "", lp_path = "";
    // Scan
    public String srcPath = "";//Packs source path
    public SCAN_MODE scanMode = SCAN_MODE.SIMPLE_SCAN;// Scan mode (v1.2)
    public SCAN_FOLDER scanFolder = SCAN_FOLDER.PACK_FOLDER;// Scan folders as packs (v1.2)
    public boolean scanFolderTarget = false;// folder target mode (v1.2)
    // Configuration
    public String installPath = "";//Default install directory path
    public boolean forcePath = false;//Whether to force an install path or not
    public boolean registryCheck = false;//Activate registry check for installed version
    public boolean scriptGen = false;//Activate script generation at end of Setup
    // Shortcuts
    public boolean shortcuts = true;//Activate shortcuts creation
    public boolean folderShortcut = false;//Make a shortcut for global install path
    public boolean shToStartMenu = true;//Install shortcuts to start menu
    public boolean shToDesktop = false;//Install shortcuts to desktop
    
}
