package dcp.gui.pivot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.wtk.Window;

import dcp.config.io.IOFactory;
import dcp.gui.pivot.frames.BuildFrame;
import dcp.gui.pivot.frames.ScanFrame;
import dcp.gui.pivot.frames.SetFrame;
import dcp.gui.pivot.frames.TweakFrame;
import dcp.logic.factory.CastFactory;
import dcp.logic.factory.GroupFactory;
import dcp.logic.factory.PackFactory;
import dcp.logic.factory.TypeFactory.FILE_TYPE;
import dcp.logic.model.Group;
import dcp.logic.model.Pack;
import dcp.logic.model.config.AppConfig;
import dcp.logic.model.config.SetupConfig;
import dcp.logic.model.config.build.IzpackConfig;
import dcp.logic.model.config.build.NugetConfig;
import dcp.main.log.Out;


public class Facade
{
    private Window application;
    
    // Data (loaded from saved file)
    public List<Pack> packs;
    public List<Group> groups;
    // UI Tabs
    private ScanFrame scanFrame;
    private SetFrame setFrame;
    private TweakFrame tweakFrame;
    private BuildFrame buildFrame;
    
    // Configurations
    public AppConfig appConfig;// App configuration file
    public SetupConfig setupConfig;// Setup configuration file
    public IzpackConfig izpackConf; // IzPack build configuration
    public NugetConfig nugetConf; // NuGet build configuration

    public Facade(Window application, String name, String version)
    {
        this.application = application;
        appConfig = confLoad();// Load configuration file
        if (appConfig == null)
        {
            appConfig = new AppConfig(name, version);// Init config if not exists
            setupConfig = new SetupConfig("Package", "1.0.0");
            izpackConf = new IzpackConfig();
            nugetConf = new NugetConfig();
        }
        else
        {
            //appConfig.setAppName(AppName); appConfig.setAppVersion(AppVersion);
            if (!appConfig.getAppVersion().equals(version)) // Warning if conf.dcp file is old 
                Out.print("WARNING", "Configuration file conf.dcp contains data of an old version: " + appConfig.getAppVersion());
            setupConfig = new SetupConfig(appConfig.getDefaultSetupConfig());
            izpackConf = new IzpackConfig(appConfig.getDefaultIzpackConfig());
            nugetConf = new NugetConfig(appConfig.getDefaultNugetConfig());
        }
    }
    
    /**
     * Initialize frames from instantiated singletons
     */
    public void init()
    {
        scanFrame = ScanFrame.getSingleton();
        setFrame = SetFrame.getSingleton();
        tweakFrame = TweakFrame.getSingleton();
        buildFrame = BuildFrame.getSingleton();
    }
    
    /**
     * Change active tab
     * @param n: tab number
     * @throws IOException 
     */
    public void setOpenTab(int id) throws IOException
    {
        switch(id) {
            case 0://Scan Tab
                break;
            case 1://Set Tab
                if (scanFrame.isModified() && !ScanFrame.isLoaded()) {//If Scanned directory
                    scanFrame.setModified(false);
                    setFrame.scanInit();//Data export to Set tab
                    if (!application.getTitle().contains("*"))
                        application.setTitle(application.getTitle().concat("*"));//modified flag in Title
                }
                else ScanFrame.setLoaded(false);//Disable scan loaded flag*
                break;
            case 2://Tweak Tab
                if (setFrame.isModified()) {
                    setFrame.setModified(false);//Modified flag*
                    tweakFrame.update();
                    tweakFrame.setModified(true);
                    if (!application.getTitle().contains("*"))
                        application.setTitle(application.getTitle().concat("*"));//modified flag in Title
                }
                break;
            case 3://Build Tab
                if (tweakFrame.isModified()) {
                    tweakFrame.setModified(false);//Modified flag*
                    buildFrame.update();
                    if (!application.getTitle().contains("*"))
                        application.setTitle(application.getTitle().concat("*"));//modified flag in Title
                }
                break;
            default:
                break;
        }
    }

    /**
     * Bind data to GUI
     */
    public void fullInit()
    {
        try
        {
            //Clear scanned path
            scanFrame.init(setupConfig.getSrcPath());
            //Groups binding
            ScanFrame.setGroups(groups);//loaded flag enabled*
            //Packs binding
            scanFrame.setPacks(packs);//loaded flag enabled*
            //Tab data initialize
            setFrame.loadInit();
            //SetupConfig binding
            tweakFrame.init(setupConfig);
            //AppConfig binding
            buildFrame.init();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    

    /**
     * Save current application configuration
     */
    public void confSave() {
        try
        {
            File f = new File(IOFactory.confFile);
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(out);
            
            os.writeObject(appConfig);
            
            os.close();
            out.close();
            Out.print("INFO", appConfig.getAppName() + " " + appConfig.getAppVersion() +
                      " configuration saved to " + IOFactory.confFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Load application configuration from file
     */
    public AppConfig confLoad() {
        try
        {
            File f = new File(IOFactory.confFile);
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream is = new ObjectInputStream(in);
                
                AppConfig appConfig = (AppConfig) is.readObject();
                
                is.close();
                in.close();
                
                return appConfig;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Save data to file
     * [SETUP_CONFIG][GROUPS][PACKS]
     */
    public boolean save(String saveFile) {
        try
        {
            File f = new File(saveFile);
            if (!f.exists()) f.createNewFile();//create file if not exists
            FileOutputStream out = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(out);
            
            os.writeObject(Master.AppVersion);
            
            os.writeObject(setupConfig);
            
            os.writeInt(GroupFactory.getGroups().getLength());
            for(Group g:GroupFactory.getGroups())
                os.writeObject(g);
            
            os.writeInt(PackFactory.getPacks().getLength());
            for(Pack p:PackFactory.getPacks())
                os.writeObject(p);
            
            os.close();
            out.close();
            Out.print("INFO", setupConfig.getAppName() + " " + setupConfig.getAppVersion() +
                      " data saved to " + f.getAbsolutePath());
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Load data from file
     */
    public boolean load(String saveFile) {
        try
        {
            File f = new File(saveFile);
            if (!f.exists()) {//If save file not exists
                return false;//error
            }
            else {//File exists
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream is = new ObjectInputStream(in);
                
                String version = (String) is.readObject();
                Out.print("DEBUG", "DCP File version: "+version);
                
                setupConfig = (SetupConfig) is.readObject();
                Out.print("DEBUG", setupConfig.getAppName() + " " + setupConfig.getAppVersion());
                
                groups = new ArrayList<Group>();
                int nGroups = is.readInt();
                for(int i = 0; i<nGroups; i++) {
                    Group G = (Group) is.readObject();
                    groups.add(G);
                }
                if (nGroups > 0) Out.print("DEBUG", groups.getLength() + " group(s) loaded");
                
                packs = new ArrayList<Pack>();
                int nPacks = is.readInt();
                for(int i = 0; i<nPacks; i++) {
                    Pack P = (Pack) is.readObject();
                    
                    // File compatibility fix
                    if (version.startsWith("1.0"))// cast Pack model from 1.0.x version (Chocolatey feature)
                        CastFactory.packModelUpdate(P, "1.0");
                    
                    P.setIcon(CastFactory.nameToImage(P.getName(), P.getFileType() == FILE_TYPE.Folder));
                    packs.add(P);
                }
                if (nPacks > 0) Out.print("DEBUG", packs.getLength() + " pack(s) loaded");
                
                is.close();
                in.close();
                Out.print("INFO", "Data loaded from file "+IOFactory.saveFile);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Save default data to workspace class
     * (sets modified flag to true)
     */
    public void saveDefault()
    {
        appConfig.setDefaultSetupConfig(new SetupConfig(setupConfig));
        appConfig.setDefaultIzpackConfig(new IzpackConfig(izpackConf));
        appConfig.setDefaultNugetConfig(new NugetConfig(nugetConf));
        appConfig.setScanMode(scanFrame.facade.getScanMode());
        appConfig.setBuildMode(buildFrame.facade.getBuildMode());
    }

    /**
     * Back to factory default data
     */
    public void factoryReset()
    {
        setupConfig = new SetupConfig(appConfig.getDefaultSetupConfig());
        packs = new ArrayList<Pack>();
        groups = new ArrayList<Group>();
        fullInit();
        IOFactory.setSaveFile("");
    }

}
