package com.dcp.sm.gui.pivot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.TaskAdapter;
import org.apache.pivot.wtk.Window;

import com.dcp.sm.config.io.IOFactory;
import com.dcp.sm.gui.pivot.frames.BuildFrame;
import com.dcp.sm.gui.pivot.frames.ScanFrame;
import com.dcp.sm.gui.pivot.frames.SetFrame;
import com.dcp.sm.gui.pivot.frames.TweakFrame;
import com.dcp.sm.gui.pivot.tasks.TaskIzpackCompile;
import com.dcp.sm.logic.factory.CastFactory;
import com.dcp.sm.logic.factory.GroupFactory;
import com.dcp.sm.logic.factory.PackFactory;
import com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.logic.model.Group;
import com.dcp.sm.logic.model.Pack;
import com.dcp.sm.logic.model.config.AppConfig;
import com.dcp.sm.logic.model.config.SetupConfig;
import com.dcp.sm.logic.model.config.build.IzpackConfig;
import com.dcp.sm.logic.model.config.build.NugetConfig;
import com.dcp.sm.main.log.Out;


public class Facade
{
    private Window master;
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
    
    // Model data (filled on load)
    private List<Pack> packs;//List of loaded packs
    private List<Group> groups;//List of loaded directories

    public Facade(String name, String version)
    {
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
                Out.print(LOG_LEVEL.WARN, "Configuration file conf.dcp contains data of an old version: " + appConfig.getAppVersion());
            setupConfig = new SetupConfig(appConfig.getDefaultSetupConfig());
            izpackConf = new IzpackConfig(appConfig.getDefaultIzpackConfig());
            nugetConf = new NugetConfig(appConfig.getDefaultNugetConfig());
        }
    }
    
    /**
     * initialize window pointer to application
     * @param application
     */
    public void setWindow(Window application)
    {
        this.master = application;
    }
    
    /**
     * Initialize frames from instantiated singletons
     */
    public void framesInit(Master master)
    {
        scanFrame = ScanFrame.getSingleton();
        scanFrame.setMaster(master);
        
        setFrame = SetFrame.getSingleton();
        setFrame.setMaster(master);
        
        tweakFrame = TweakFrame.getSingleton();
        tweakFrame.setMaster(master);
        
        buildFrame = BuildFrame.getSingleton();
        buildFrame.setMaster(master);
    }

    /**
     * Bind data to GUI
     * @param load: init Scan and Set for load mode
     */
    public void tabsInit()
    {
        try
        {
            // ScanFrame binding
            scanFrame.init(appConfig, setupConfig);
            // SetFrame binding
            setFrame.init(groups, packs);
            // SetupConfig binding
            tweakFrame.init(setupConfig);
            // AppConfig binding
            buildFrame.init(appConfig, setupConfig);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Process a save file given from commandline
     * Compile it using IzPack
     * @param saveFile: dcp file
     */
    public void process(String saveFile)
    {
        if (!load(saveFile)) // load error
            Out.print(LOG_LEVEL.ERR, "Error loading the file! Please load it from the GUI and correct if there are some errors then reload it.");
        else { // load success
            Out.print(LOG_LEVEL.INFO, "File data loaded successfully.");
            System.out.println();
            
            // Fill model factories from loaded data
            GroupFactory.clear();// groups
            for(Group g:groups)
                GroupFactory.addGroup(g);
            PackFactory.clear();// packs
            for(Pack p:packs)
                PackFactory.addPack(p);
            
            final TaskListener<Boolean> tlCompile = new TaskListener<Boolean>() {// Finished compilation
                @Override public void executeFailed(Task<Boolean> t) {// Failed
                    System.out.println();
                    Out.print(LOG_LEVEL.ERR, "Compiled with errors!");
                }
                @Override public void taskExecuted(Task<Boolean> t) {// Success
                    if (t.getResult() == true) {// If no errors
                        System.out.println();
                        Out.print(LOG_LEVEL.INFO, "Finished compiling.");
                    } else executeFailed(t);// Compile Errors
                }
            };
            
            // IzPack Compile Task launch
            String filename = setupConfig.getAppName().replaceAll(" ", "") + "-" + setupConfig.getAppVersion() + ".jar";
            Out.print(LOG_LEVEL.INFO, "Compiling file " + filename);
            TaskIzpackCompile compileTask = new TaskIzpackCompile(new File(filename).getAbsolutePath(), setupConfig, izpackConf);
            compileTask.execute(new TaskAdapter<Boolean>(tlCompile));//Compile
        }
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
                if (scanFrame.isModified()) {//If Scanned directory
                    scanFrame.setModified(false);
                    setFrame.update();//Data export from Scan to Set tab
                    if (!master.getTitle().contains("*"))
                        master.setTitle(master.getTitle().concat("*"));//modified flag in Title
                }
                break;
            case 2://Tweak Tab
                if (setFrame.isModified()) {
                    setFrame.setModified(false);
                    tweakFrame.update();//Enable/Disable packs shortcut option
                    if (!master.getTitle().contains("*"))
                        master.setTitle(master.getTitle().concat("*"));//modified flag in Title
                }
                break;
            case 3://Build Tab
                if (tweakFrame.isModified()) {
                    tweakFrame.setModified(false);
                    buildFrame.update();//Generate target file export path filename
                    if (!master.getTitle().contains("*"))
                        master.setTitle(master.getTitle().concat("*"));//modified flag in Title
                }
                break;
            default:
                break;
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
            Out.print(LOG_LEVEL.INFO, appConfig.getAppName() + " " + appConfig.getAppVersion() +
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
            Out.print(LOG_LEVEL.INFO, setupConfig.getAppName() + " " + setupConfig.getAppVersion() +
                      " data saved to " + f.getAbsolutePath());
            appConfig.addRecentProject(f);
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
            if (!f.exists()) {// If save file not exists
                return false;// error
            }
            else {// File exists
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream is = new ObjectInputStream(in);
                
                String version = (String) is.readObject();
                Out.print(LOG_LEVEL.DEBUG, "DCP File version: "+version);
                
                Object obj = is.readObject();
                try {
                setupConfig = (SetupConfig) obj;
                }
                catch(ClassCastException e) {
                	setupConfig = new SetupConfig((dcp.logic.model.config.SetupConfig) obj);
                }
                Out.print(LOG_LEVEL.DEBUG, setupConfig.getAppName() + " " + setupConfig.getAppVersion());
                
                // File compatibility fix
                CastFactory.setupModelUpdate(setupConfig, version.substring(0, 3));// cast Setup model from old versions

                groups = new ArrayList<Group>();
                //GroupFactory.clear();
                int nGroups = is.readInt();
                Group G;
                for(int i = 0; i<nGroups; i++) {
                	obj = is.readObject();
                	try { G = (Group) obj; }
                	catch(ClassCastException e) {
                		G = new Group((dcp.logic.model.Group) obj);
                	}
                    groups.add(G);
                }
                if (nGroups > 0) Out.print(LOG_LEVEL.DEBUG, GroupFactory.getCount() + " group(s) loaded");

                packs = new ArrayList<Pack>();
                //PackFactory.clear();
                int nPacks = is.readInt();
                Pack P;
                for(int i = 0; i<nPacks; i++) {
                	obj = is.readObject();
                	try { P = (Pack) obj; }
                	catch(ClassCastException e) {
                		P = new Pack((dcp.logic.model.Pack) obj);
                	}
                    
                    // File compatibility fix
                    CastFactory.packModelUpdate(P, version.substring(0, 3));// cast Pack model from old versions
                    
                    P.setIcon(CastFactory.nameToImage(P.getName(), P.getFileType() == FILE_TYPE.Folder));
                    //PackFactory.addPack(P);
                    packs.add(P);
                }
                if (nPacks > 0) Out.print(LOG_LEVEL.DEBUG, PackFactory.getCount() + " pack(s) loaded");
                
                is.close();
                in.close();
                Out.print(LOG_LEVEL.INFO, "Data loaded from file " + IOFactory.saveFile);
                appConfig.addRecentProject(f);
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
        appConfig.setBuildMode(buildFrame.facade.getBuildMode());
    }

    /**
     * Back to factory default data
     */
    public void factoryReset()
    {
        setupConfig = new SetupConfig(appConfig.getDefaultSetupConfig());
        if (packs != null) packs.clear();
        if (groups != null) groups.clear();
        tabsInit();
        IOFactory.setSaveFile("");
    }

}
