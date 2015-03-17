package com.dcp.sm.gui.pivot.tasks;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.XMLStreamException;

import org.apache.pivot.collections.List;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.wtk.Component;
import org.codehaus.staxmate.out.SMOutputElement;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import com.dcp.sm.config.compile.IzpackAntCompiler;
import com.dcp.sm.config.io.BatWriter;
import com.dcp.sm.config.io.IOFactory;
import com.dcp.sm.config.io.OSValidator;
import com.dcp.sm.config.io.TextWriter;
import com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE;
import com.dcp.sm.config.io.ant.CompileAntWriter;
import com.dcp.sm.config.io.ant.DebugAntWriter;
import com.dcp.sm.config.io.ant.RunAntWriter;
import com.dcp.sm.config.io.xml.izpack.ConditionWriter;
import com.dcp.sm.config.io.xml.izpack.GuiprefsWriter;
import com.dcp.sm.config.io.xml.izpack.InfoWriter;
import com.dcp.sm.config.io.xml.izpack.IzpackWriter;
import com.dcp.sm.config.io.xml.izpack.ListenerWriter;
import com.dcp.sm.config.io.xml.izpack.LocaleWriter;
import com.dcp.sm.config.io.xml.izpack.NativeWriter;
import com.dcp.sm.config.io.xml.izpack.PackWriter;
import com.dcp.sm.config.io.xml.izpack.PackagingWriter;
import com.dcp.sm.config.io.xml.izpack.PanelWriter;
import com.dcp.sm.config.io.xml.izpack.ProcessWriter;
import com.dcp.sm.config.io.xml.izpack.ResourceWriter;
import com.dcp.sm.config.io.xml.izpack.ShortcutWriter;
import com.dcp.sm.config.io.xml.izpack.VariableWriter;
import com.dcp.sm.config.io.zip.TrueZipCastFactory;
import com.dcp.sm.logic.factory.GroupFactory;
import com.dcp.sm.logic.factory.PackFactory;
import com.dcp.sm.logic.factory.TypeFactory;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.logic.model.Group;
import com.dcp.sm.logic.model.Pack;
import com.dcp.sm.logic.factory.TypeFactory.INSTALL_TYPE;
import com.dcp.sm.logic.model.config.SetupConfig;
import com.dcp.sm.logic.model.config.build.IzpackConfig;
import com.dcp.sm.logic.model.config.build.WebConfig;
import com.dcp.sm.main.log.Out;
import com.dcp.sm.web.sftp.JschFactory;


public class TaskIzpackCompile extends Task<Boolean>
{
    // Compiler Configuration
    private IzpackAntCompiler compiler = new IzpackAntCompiler();//IzPack Compiler Class
    private SetupConfig setupConfig;// Setup configuration
    private IzpackConfig izpackConf;// IzPack configuration
    
    // Models Data
    private List<Pack> packs = PackFactory.getPacks();
    private List<Group> groups = GroupFactory.getGroups();

    // XML Root Element
    private SMOutputElement root;
    
    // Panels display flags
    private boolean isPackPanel = true;// Disable pack panel display if all packs required
    private boolean isProcessPanel = false;// If has an executable with an execute intall mode
    private boolean isInstallGroup = false;// Has install groups affected to one or more packs
    private boolean isArchCondition = false;// Has conditions affected to one or more packs
    
    public TaskIzpackCompile(String targetPath, SetupConfig setupConfig, IzpackConfig izpackConf) {
        this.compiler.setTarget(targetPath);
        this.setupConfig = setupConfig;
        this.izpackConf = izpackConf;
    }
    
    // Set log component to display compile stream
    public void setLogger(Component LOGGER) {
        Out.setLogger(LOGGER);
    }
    
    @Override
    public Boolean execute()
    {
        try {
            // Izpack work files prepare/generate
            if (IOFactory.izpackWrite) {
                if (!workflow()) return false;
                Out.newLine();
            }
            
            // IZPACK COMPILE
            if (IOFactory.izpackCompile) {//IzPack compilation enabled
                Out.print(LOG_LEVEL.INFO, "Compiling package to " + compiler.getTarget());
                if (compiler.compile() == 0) {
                    Out.print(LOG_LEVEL.INFO, "Compilation success.");
                    TrueZipCastFactory.clearArchives();
                    
                    if (izpackConf.getWebConfig() != null && izpackConf.getWebConfig().isEnabled()) {//Send pack files through SFTP
                        Out.newLine();
                        Out.print(LOG_LEVEL.INFO, "Web Setup upload process for packs begins..");
                        return webUpload();
                    }
                    return true;
                }
                else {
                    Out.print(LOG_LEVEL.INFO, "Compilation error!");
                    TrueZipCastFactory.clearArchives();
                    return false;
                }
            }
            else {// Stop after xml data writing
                Out.print(LOG_LEVEL.INFO, "Data written on " + IOFactory.xmlIzpackInstall);
                return true;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
        return abort;
    }

    private boolean workflow() throws XMLStreamException, IOException
    {
        // Ant tasks/targets define
        antTargetsDefine();
        
        // Setup config and Data analyze + corrections
        configAnalyze(setupConfig);
        if (!dataAnalyze())
            return false;
        
        // Setting default install directory (res/default-dir.txt)
        TextWriter.writeInstallPath(setupConfig.getInstallPath());
        if (setupConfig.isRegistryCheck()) {// write clean bat file commands
            BatWriter BW = new BatWriter(IOFactory.batClean);
            BW.writeClean(setupConfig.getAppName(), setupConfig.getAppVersion());
        }
        
        IzpackWriter IzW = new IzpackWriter(IOFactory.xmlIzpackInstall);
        root = IzW.getRoot();

            if (!writeInfo())// Writing Application info
                return false;
            
            if (!writeGuiPrefs())// Writing Gui Preferences
                return false;
            
            if (!writeLocales())// Writing langpacks (eng & fra & deu & spa)
                return false;
            
            if (!writeVariables())// Writing variables
                return false;

            if (!writePackaging())// Writing packaging options
                return false;
            
            if (!writePanels())// Writing Panels
                return false;
            
            if (!writeListeners())// Writing listeners
                return false;
            
            if (!writeResources())// Writing resources (images/specs)
                return false;
            
            if (!writeNatives())// Writing dll files + libraries
                return false;
            //jar / natives
            
            if (!writeConditions())// Writing conditions
                return false;
            
            // Packs/Groups
            if (!writePacks(IOFactory.xmlProcessPanelSpec))// Writing Packs data to xml file (res/xml/PacksPanelSpec.xml)
                return false;// If writing error
        
        IzW.close();
        
        
        if (setupConfig.isShortcuts()) {// Writing shortcuts spec
            if (!writeShortcuts(IOFactory.xmlShortcutPanelSpec))
                return false;
        }
        return true;
    }

    private boolean writeGuiPrefs() throws XMLStreamException
    {
        GuiprefsWriter GW = new GuiprefsWriter(root);
        Out.print(LOG_LEVEL.INFO, "Writing GUI Preferences");
        GW.setPrefs(setupConfig);// Write Gui prefs
        // Modifiers
        GW.addModifier("langDisplayType", "native");
        GW.addModifier("allXGap", "0");
        GW.addModifier("allYGap", "0");
        GW.addModifier("labelGap", "2");
        GW.addModifier("useButtonIcons", "yes");
        GW.addModifier("useHeadingPanel", "yes");
        GW.addModifier("useHeadingForSummary", "yes");
        GW.addModifier("headingLineCount", "1");
        GW.addModifier("headingFontSize", "1.5");
        GW.addModifier("headingBackgroundColor", "0x00ffffff");
        GW.addModifier("headingPanelCounter", "text");
        
        return true;
    }

    private void antTargetsDefine() throws XMLStreamException
    {
        CompileAntWriter CW = new CompileAntWriter(IOFactory.xmlIzpackAntBuild);
        CW.writeStandaloneTask("IZPACK");
        CW.writeIzpackTarget(IOFactory.xmlIzpackInstall, compiler.getTarget(), izpackConf.isWebSetup());
        CW.close();
        
        RunAntWriter RW = new RunAntWriter(IOFactory.xmlRunAntBuild);
        RW.writeRunTarget(compiler.getTarget());
        RW.close();
        
        DebugAntWriter DW = new DebugAntWriter(IOFactory.xmlDebugAntBuild);
        DW.writeDebugTarget(compiler.getTarget());
        DW.close();
    }
    
    private void configAnalyze(SetupConfig setupConfig)
    {
        if (!setupConfig.getAppURL().equals("")) {// If URL defined
            if (!(setupConfig.getAppURL().startsWith("http://") || setupConfig.getAppURL().startsWith("https://"))) {
                setupConfig.setAppURL("http://"+setupConfig.getAppURL());
            }
        }
        if (setupConfig.getSrcPath().endsWith("/")) {
            setupConfig.setSrcPath(setupConfig.getSrcPath().substring(0, setupConfig.getSrcPath().length()-1));
        }
    }

    private boolean dataAnalyze() throws IOException
    {
        Out.print(LOG_LEVEL.INFO, "Data parsing...");
        Out.print(LOG_LEVEL.INFO, setupConfig.getAppName() + " " + setupConfig.getAppVersion());
        Out.print(LOG_LEVEL.INFO, "#packs " + packs.getLength());
        Out.print(LOG_LEVEL.INFO, "#groups " + groups.getLength());
        Out.newLine();
        
        boolean notRequired = false;// false if all packs are required
        boolean shortcut = false;// false if all pack don't have shortcut enabled
        
        // Panel Options Init
        isPackPanel = true;
        isProcessPanel = false;
        isInstallGroup = false;
        isArchCondition = false;
        
        // Packs' data analyze/correct
        for (Pack P:PackFactory.getPacks()) {
            if (!notRequired && !(P.isRequired() || P.isHidden()))
                notRequired = true;// one pack is not required
            if (!shortcut && P.isShortcut())
                shortcut = true;// one pack have shortcut enabled

            // Replace blanks in install name with '_'
            if (P.getInstallName().contains(" ")) {
                P.setInstallName(P.getInstallName().replace(' ', '_'));
            }
            // Pack install groups correction if contains ';' instead of ','
            if (P.getInstallGroups().contains(";")) {
                P.setInstallGroups(P.getInstallGroups().replace(';', ','));
            }
            
            // Delete , or ; at end of install groups string
            if (P.getInstallGroups().trim().endsWith(",")) {
                P.setInstallGroups(P.getInstallGroups().trim().substring(0, P.getInstallGroups().trim().length()-1));
            }
            
            // Flags
            if (!isProcessPanel && P.getInstallType() == INSTALL_TYPE.EXECUTE) {
                isProcessPanel = true;// Activate Process panel if pack(s) to execute
            }
            if (!isInstallGroup && !P.getInstallGroups().equals("")) {
                isInstallGroup = true;// Activate install groups panel display
            }
            if (!isArchCondition && P.getArch() > 0) {
                isArchCondition = true;// Activate conditions if pack(s) related to arch
            }
            
            if (!new File(P.getPath()).exists()) {// packs' source file path correction
                String newPath = setupConfig.getSrcPath()+"/"+P.getGroupPath()+P.getName();
                Out.print(LOG_LEVEL.INFO, "Changing '"+P.getName()+"' source path to: "+newPath);
                File newFile = new File(newPath);
                if (newFile.exists()) P.updatePack(newFile);
                else Out.print(LOG_LEVEL.ERR,"File not found: "+newFile.getAbsolutePath());
            }
            
            if (P.getInstallType() == INSTALL_TYPE.EXECUTE) {
                P.setInstallPath(IOFactory.exeTargetDir);// Set default exe copy path
            }
            else if (P.getInstallType() == INSTALL_TYPE.EXTRACT) {
                if (P.getName().toLowerCase().endsWith(".rar")) {// convert rar file to zip
                    Out.print(LOG_LEVEL.ERR, "Error reading RAR file. Please use ZIP archives instead.");
                    return false;
                    /*Out.print(LOG_LEVEL.INFO, "Converting rar file " + P.getName() + " to zip");
                    P.updatePack(TrueZipCastFactory.rarToZip(new File(P.getPath())));*/
                }
            }
            
            // Pack Install Path correction to standard ("folder/subfolder")
            if (!P.getInstallPath().equals("")) {
                P.setInstallPath(P.getInstallPath().replace('\\', '/'));
                if (P.getInstallPath().startsWith("/") && OSValidator.isWindows()) {
                    P.setInstallPath(P.getInstallPath().substring(1));
                }
                if (P.getInstallPath().endsWith("/")) {
                    P.setInstallPath(P.getInstallPath().substring(0, P.getInstallPath().length()-1));
                }
            }
        }
        
        if (!notRequired) {// if all packs are required
            isPackPanel = false;// disable packs panel display
            Out.print(LOG_LEVEL.INFO, "Packs panel disabled");
        }
        if (!shortcut) {// if all packs don't have shortcut enabled
            setupConfig.setShortcuts(false);// disable shortcut panel
            Out.print(LOG_LEVEL.INFO, "Shortcuts panel disabled");
        }
        if (setupConfig.isForcePath())
            Out.print(LOG_LEVEL.INFO, "Install path panel disabled");
        if (izpackConf.isSplit())
            Out.print(LOG_LEVEL.INFO, "Packaging option enabled");
        
        if (isInstallGroup)
            Out.print(LOG_LEVEL.INFO, "Install Group panel enabled");
        
        Out.newLine();
        return true;
    }

    private boolean writeInfo() throws XMLStreamException
    {
        InfoWriter IW = new InfoWriter(root);
        Out.print(LOG_LEVEL.INFO, "Writing Setup Info");
        IW.setInfo(setupConfig, izpackConf);// Write Setup info
        
        return true;
    }

    private boolean writeLocales() throws XMLStreamException
    {
        LocaleWriter LW = new LocaleWriter(root);
        
        boolean done = false;
        if (setupConfig.isCustomLang()) { LW.addLangpack(setupConfig.getCustomLangISO()); done = true; }
        if (setupConfig.isFrench()) { LW.addLangpack("fra"); done = true; }
        if (setupConfig.isGerman()) { LW.addLangpack("deu"); done = true; }
        if (setupConfig.isSpanish()) { LW.addLangpack("spa"); done = true; }
        if (setupConfig.isEnglish() || !done) {
            LW.addLangpack("eng");
            if (!done) Out.print(LOG_LEVEL.INFO, "Setting English as a default langpack");
        }
        
        return true;
    }

    private boolean writeVariables() throws XMLStreamException
    {
        VariableWriter VW = new VariableWriter(root);
        Out.print(LOG_LEVEL.INFO, "Writing Variables");
        
        VW.addVariable("InstallerFrame.logfilePath", "$INSTALL_PATH/Uninstaller/install.log");
        VW.addVariable("DesktopShortcutCheckboxEnabled", "true");
        if (isProcessPanel)
            VW.addVariable("EXE_DIR", IOFactory.exeTargetDir);
        
        return true;
    }

    private boolean writePackaging() throws XMLStreamException
    {
        // Packaging activate
        if (izpackConf.isSplit()) {
            Out.print(LOG_LEVEL.INFO, "Setting packaging option");
            PackagingWriter PkgW = new PackagingWriter(root);
            PkgW.setPackaging(izpackConf.getSplitSize());
        }
        Out.newLine();
        return true;
    }
    
    private boolean writePanels() throws XMLStreamException {
        PanelWriter PW = new PanelWriter(root);
        Out.print(LOG_LEVEL.INFO, "Writing Panels");
        
        if (setupConfig.isRegistryCheck())//<panel />
            PW.writePanel("CheckedHelloPanel");
        else PW.writePanel("HelloPanel");

        if (!setupConfig.getReadmePath().equals(""))//<panel classname="HTMLInfoPanel"/>
            PW.writePanel("HTMLInfoPanel");

        if (!setupConfig.getLicensePath().equals(""))//<panel classname="HTMLLicencePanel"/>
            PW.writePanel("HTMLLicencePanel");
        
        if (isInstallGroup)//<panel />
            PW.writePanel("InstallationGroupPanel");
        
        if (isPackPanel)//<panel />
            PW.writePanel("TreePacksPanel");
        
        if (setupConfig.isForcePath())//<panel />
            PW.writePanel("DefaultTargetPanel");
        else
            PW.writePanel("TargetPanel");
        
        PW.writePanel("SummaryPanel");//<panel />
        PW.writePanel("InstallPanel");//<panel />
        
        // If there's packs to execute
        if (isProcessPanel)//<panel />
            PW.writePanel("ProcessPanel");
        
        if (setupConfig.isShortcuts() || setupConfig.isFolderShortcut())//<panel />
            PW.writePanel("ShortcutPanel");
        
        if (setupConfig.isScriptGen())//<panel />
            PW.writePanel("FinishPanel");
        else PW.writePanel("SimpleFinishPanel");
        
        return true;
    }

    private boolean writeListeners() throws XMLStreamException
    {
        ListenerWriter LW = new ListenerWriter(root);
        Out.print(LOG_LEVEL.INFO, "Writing Listeners");
        
        LW.addListener("SummaryLoggerInstallerListener", "", false);
        if (setupConfig.isRegistryCheck())
            LW.addListener("RegistryInstallerListener", "RegistryUninstallerListener", true);
        
        return true;
    }
    
    private boolean writeResources() throws XMLStreamException
    {
        ResourceWriter RW = new ResourceWriter(root);
        Out.print(LOG_LEVEL.INFO, "Writing Resources");
        
        // Langpacks
        boolean done = false;
        if (setupConfig.isCustomLang()) {
            RW.addResource("packsLang.xml_"+setupConfig.getCustomLangISO(), setupConfig.getCustomLangPath());
            RW.addResource("CustomLangpack.xml_"+setupConfig.getCustomLangISO(), setupConfig.getCustomLangPath());
            done = true;
        }
        if (setupConfig.isFrench()) {
            RW.addResource("packsLang.xml_fra", IOFactory.langpackPath+"/fra.xml");
            RW.addResource("CustomLangpack.xml_fra", IOFactory.langpackPath+"/fra.xml");
            done = true;
        }
        if (setupConfig.isGerman()) {
            RW.addResource("packsLang.xml_deu", IOFactory.langpackPath+"/deu.xml");
            RW.addResource("CustomLangpack.xml_deu", IOFactory.langpackPath+"/deu.xml");
            done = true;
        }
        if (setupConfig.isSpanish()) {
            RW.addResource("packsLang.xml_spa", IOFactory.langpackPath+"/spa.xml");
            RW.addResource("CustomLangpack.xml_spa", IOFactory.langpackPath+"/spa.xml");
            done = true;
        }
        if (setupConfig.isEnglish() || !done) {
            RW.addResource("packsLang.xml", IOFactory.langpackPath+"/eng.xml");
            RW.addResource("CustomLangpack.xml_eng", IOFactory.langpackPath+"/eng.xml");
        }
        
        if (!setupConfig.getReadmePath().equals(""))// Readme file
            RW.addResource("HTMLInfoPanel.info", setupConfig.getReadmePath());
        if (!setupConfig.getLicensePath().equals(""))// License file
            RW.addResource("HTMLLicencePanel.licence", setupConfig.getLicensePath());
        
        // Default directory
        RW.addResource("TargetPanel.dir.windows", IOFactory.defDirFile);
        // Panels Specs
        if (setupConfig.isShortcuts())//ShortcutPanelSpec
            RW.addResource("shortcutSpec.xml", IOFactory.xmlShortcutPanelSpec);
        if (setupConfig.isRegistryCheck())//RegistrySpec
            RW.addResource("RegistrySpec.xml", IOFactory.xmlRegistrySpec);
        if (isProcessPanel)//ProcessPanelSpec
            RW.addResource("ProcessPanel.Spec.xml", IOFactory.xmlProcessPanelSpec);
        // Pictures
        if (!setupConfig.getLogoPath().equals("")) {
            RW.addResource("installer.langsel.img", setupConfig.getLogoPath());
            RW.addResource("Heading.image", setupConfig.getLogoPath());
        }
        if (!setupConfig.getSideLogoPath().equals(""))
            RW.addResource("Installer.image", setupConfig.getSideLogoPath());
        
        return true;
    }

    private boolean writeNatives() throws XMLStreamException
    {
        NativeWriter NW = new NativeWriter(root);
        // Jars
        NW.addJar(IOFactory.jarResources);
        /*if (setupConfig.isProcess()) {
            //NW.addJar(IOFactory.jarExecutable);
            //NW.addJar(IOFactory.jarListeners);
        }*/
        // Natives
        if (setupConfig.isShortcuts()) {
            NW.addNative("izpack", "ShellLink.dll");
            NW.addNative("izpack", "ShellLink_x64.dll");
        }
        if (setupConfig.isRegistryCheck()) {
            NW.addNative("3rdparty", "COIOSHelper.dll");
            NW.addNative("3rdparty", "COIOSHelper_x64.dll");
        }
        
        return true;
    }

    private boolean writeConditions() throws XMLStreamException
    {
        Out.print(LOG_LEVEL.INFO, "Declaring pack conditions");
        Out.newLine();
        
        ConditionWriter CondW = new ConditionWriter(root);
        if (isArchCondition) {
            if (CondW.addCondition(TypeFactory.CONDITION.ARCH32.toString(), "com.izforge.izpack.resources.SystemCheck", "x32"))
            if (CondW.addCondition(TypeFactory.CONDITION.ARCH64.toString(), "com.izforge.izpack.resources.SystemCheck", "x64"))
                return true;
        }
        else return true;
        
        return false;
    }
    
    private boolean writePacks(String process_spec) throws XMLStreamException, IOException
    {
        Out.print(LOG_LEVEL.INFO, "Data-xml writing...");
        PackWriter PW = new PackWriter(root);// Packs writing: PacksPanelSpec.xml
        
        // Core pack: Include registry clean files if registry check enabled
        PW.addCorePack(setupConfig.isRegistryCheck());
        
        // Groups Writing
        for(Group G:groups) {
            Out.print(LOG_LEVEL.INFO, "Writing group " + G.getName());
            if (!PW.addGroup(G)) {// If error writing
                Out.print(LOG_LEVEL.INFO, "Writing error for group " + G.getName() + "!");
                return false;
            }
        }
        // Packs writing
        for(Pack P:packs) {
            Out.print(LOG_LEVEL.INFO, "Writing pack " + P.getInstallName());
            if (!PW.addPack(P)) {// If error writing
                Out.print(LOG_LEVEL.INFO, "Writing error for pack " + P.getInstallName() + "!");
                return false;
            }
        }
        
        // Jobs Writing
        if (isProcessPanel) {// ProcessPanelSpec.xml executables run
            ProcessWriter ProcessW = new ProcessWriter(process_spec);
            
            for (Pack P:packs) {
                if (P.getInstallType() == INSTALL_TYPE.EXECUTE) {
                    Out.print(LOG_LEVEL.INFO, "Writing process pack job for " + P.getInstallName() + " to " + ProcessW.getTargetFile());
                    if (P.getFileType() == FILE_TYPE.Setup) {// Setups batch writing
                        ProcessW.addClassJob(P,"com.izforge.izpack.resources.SetupExecute",
                                                new String[] {P.getName(), P.isSilentInstall()?"true":"false", "$INSTALL_PATH/$EXE_DIR"});
                    }
                    else if (P.getFileType() == FILE_TYPE.Executable) {// Executable process writing
                        if (P.getName().endsWith(".jar")) {// Jar executable
                            ProcessW.addClassJob(P,"com.izforge.izpack.resources.JarExecute",
                                    new String[] {P.getName(), "$INSTALL_PATH/$EXE_DIR"});
                        }
                        else if (P.getName().endsWith(".bat") || P.getName().endsWith(".sh")) {// Bat-Sh script
                            ProcessW.addScriptJob(P, "DEP=$INSTALL_PATH\\"+IOFactory.exeTargetDir);
                        }
                        else if (P.getName().endsWith(".reg")) {// Reg script
                            ProcessW.addClassJob(P,"com.izforge.izpack.resources.RegExecute",
                                    new String[] {P.getName(), "$INSTALL_PATH/$EXE_DIR"});
                        }
                        else
                            ProcessW.addPackJob(P);
                    }
                }
            }
            
            // Passing the tmp exe dir to cleaner class to be deleted
            ProcessW.addCleanJob("$INSTALL_PATH/$EXE_DIR");
            
            ProcessW.close();
        }
        else if (new File(IOFactory.xmlProcessPanelSpec).exists()) {// Suprimer fichier spec process
            new File(IOFactory.xmlProcessPanelSpec).delete();
        }
        
        return true;
    }

    private boolean writeShortcuts(String xml_file) throws XMLStreamException
    {
        ShortcutWriter SW = new ShortcutWriter(xml_file,
                                                setupConfig.isShToStartMenu(),
                                                setupConfig.isShToDesktop());
        Out.print(LOG_LEVEL.INFO, "Writing Shortcuts to " + SW.getTargetFile());

        if (setupConfig.isFolderShortcut())// Folder shortcut
            SW.addShortcut("$APP_NAME $APP_VER", "$INSTALL_PATH", "", "", setupConfig.isShToDesktop(), false);

        if (setupConfig.isRegistryCheck())// Uninstaller
            SW.addShortcut("Uninstall", "$INSTALL_PATH/Uninstaller", "/clean.bat",
                    "", false, false);
        else
            SW.addShortcut("Uninstall", "$INSTALL_PATH/Uninstaller", "/uninstaller.jar",
                    "", false, false);

        if (setupConfig.isShortcuts())// If shortcuts install enabled
        for(Pack P:packs) {
            if (P.isShortcut() && P.getInstallType() != INSTALL_TYPE.EXECUTE)
                SW.addPackShortcut(P, false);
        }

        SW.close();
        return true;
    }
    
    /**
     * Sends Pack files through SFTP
     * @return true if success
     */
    private boolean webUpload()
    {
        JschFactory sftp = null;
        WebConfig webConfig = izpackConf.getWebConfig();
        
        try {
            sftp = new JschFactory(webConfig.getHost(), webConfig.getUser(), webConfig.getPass(), webConfig.getRemoteDir());
            Out.print(LOG_LEVEL.INFO, "SFTP Connection initiated");
            String package_name = compiler.getTarget().substring(0, compiler.getTarget().lastIndexOf("."));
            Out.print(LOG_LEVEL.INFO, "Begin upload:");
            File file = new File(package_name+".pack-core.jar");
            Out.print(LOG_LEVEL.INFO, "Uploading file "+file.getName());
            sftp.put(file, webConfig.getPath());
            while(!sftp.isReady()) Thread.sleep(100);
            file.delete();
            for(Pack P:packs) {
                file = new File(package_name+".pack-"+P.getInstallName()+".jar");
                Out.print(LOG_LEVEL.INFO, "Uploading file "+file.getName());
                sftp.put(file, webConfig.getPath());
                while(!sftp.isReady())
                    Thread.sleep(200);
                file.delete();
            }
            for(Group G:groups) {
                file = new File(package_name+".pack-"+G.getName()+".jar");
                Out.print(LOG_LEVEL.INFO, "Begin uploading file "+file.getName());
                sftp.put(file, webConfig.getPath());
                while(!sftp.isReady())
                    Thread.sleep(200);
                file.delete();
            }
        }
        catch (JSchException e) {
            e.printStackTrace();
            Out.print(LOG_LEVEL.ERR, "JSch Error!");
            return false;
        }
        catch (SftpException e) {
            e.printStackTrace();
            Out.print(LOG_LEVEL.ERR, "SFTP connection Error!");
            return false;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Out.print(LOG_LEVEL.ERR, "SFTP connection interrupted!");
            return false;
        }
        finally {
            sftp.disconnect();
        }
        
        Out.print(LOG_LEVEL.INFO, "All packs uploaded to "+webConfig.getPath());
        return true;
    }

}
