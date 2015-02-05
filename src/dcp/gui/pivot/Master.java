package dcp.gui.pivot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.Filter;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentKeyListener;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.FileBrowserSheetListener;
import org.apache.pivot.wtk.Keyboard;
import org.apache.pivot.wtk.Keyboard.Modifier;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Prompt;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TabPane;
import org.apache.pivot.wtk.TabPaneSelectionListener;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.Keyboard.KeyLocation;

import dcp.config.io.IOFactory;
import dcp.gui.pivot.actions.BrowseAction;
import dcp.gui.pivot.frames.ScanFrame;
import dcp.gui.pivot.helper.HelperFacade;
import dcp.logic.factory.GroupFactory;
import dcp.logic.factory.PackFactory;
import dcp.main.log.Out;


public class Master extends Window implements Application, Bindable
{
    // Constant Application Values
    public final static String AppName = "DCP Setup Maker";
    public final static String AppVersion = "1.1.1";
    
    // Class Data
    private Window window;// Main application window
    public static Facade facade;// Application data facade
    
    // Helpers
    @BXML static public HelperFacade helper;
    // Display
    @BXML private Label info;
    @BXML private TabPane tabPane;
    @BXML private Label statusBarStep;
    @BXML private Label statusBarNPacks;
    @BXML private Label statusBarNGroups;
    // Browsers
    @BXML private FileBrowserSheet saveFileBrowserSheet;//File Browser
    @BXML private FileBrowserSheet loadFileBrowserSheet;//File Browser
    // Buttons
    @BXML private PushButton btBack;
    @BXML private PushButton btNext;
    @BXML private PushButton btSaveAs;
    @BXML private PushButton btSave;
    @BXML private PushButton btLoad;
    @BXML private PushButton btUndo;
    @BXML private PushButton btDefault;
    @BXML private PushButton btHelp;
    //@BXML private PushButton btInfo;
    
    //Actions
    private Action ASave;//Save project on file
    private Action ALoad;//Load project from file
    
    /**
     * Update title with save file and edit flag '*'
     */
    private void titleUpdate() {
        if (!Master.this.getTitle().contains("-")) {//No save file defined
            if (Master.this.getTitle().contains("*"))//remove * flag from title
                Master.this.setTitle(Master.this.getTitle().substring(0, Master.this.getTitle().length()-1));
            Master.this.setTitle(Master.this.getTitle().concat(" - "+IOFactory.saveFile ));
        }
        else {//Already saved on a file
            if (IOFactory.saveFile.length() > 0) // Save file set
                Master.this.setTitle(Master.this.getTitle().
                        substring(0, Master.this.getTitle().indexOf('-')+2).
                        concat(IOFactory.saveFile));
            else // Save file unset
                Master.this.setTitle(Master.this.getTitle().
                        substring(0, Master.this.getTitle().indexOf('-')-1));
        }
    }
    
    public Master() {
        IOFactory.init();// Factory memory data initialize
        facade = new Facade(this, AppName, AppVersion);
        
        //Save action
        ASave = new Action() {// Project Save Action
            @Override public void perform(Component source) {
                if (facade.save(IOFactory.saveFile)) {
                    // Enable Save button
                    if (!btSave.isEnabled()) {
                        btSave.setEnabled(true);
                        //btDefault.setEnabled(false);
                    }
                    btUndo.setEnabled(true);
                    titleUpdate();
                }
            } };
        //Load Action
        ALoad = new Action() {// Project Load Action
            @Override
            public void perform(Component arg0)
            {
                if (facade.load(IOFactory.saveFile)) {
                    facade.tabsInit(true);
                    // Go to Set tab
                    tabPane.setSelectedIndex(1);
                    for(int i=0; i<tabPane.getTabs().getLength(); i++)
                        tabPane.getTabs().get(i).setEnabled(false);
                    tabPane.getTabs().get(1).setEnabled(true);
                    if (tabPane.getSelectedIndex() == 1)// If already on Set tab
                        ScanFrame.setLoaded(false);// Disable scan loaded flag*
                    btBack.setEnabled(true);
                    btNext.setEnabled(true);
                    // Enable Save button
                    if (!btSave.isEnabled()) {
                        btSave.setEnabled(true);
                    }
                    btUndo.setEnabled(true);
                    titleUpdate();
                }
            }
        };

        Out.print("FACTORY", "Data loaded to memory");
    }

    @Override public void startup(Display display, Map<String, String> properties) throws Exception// App start
    {
        Out.print("PIVOT", "Window open");
        Locale.setDefault(Locale.ENGLISH);// Set default UI language to English
        BXMLSerializer bxmlSerializer = new BXMLSerializer();
        window = (Window) bxmlSerializer.readObject(getClass().getResource("master.bxml"));
        window.open(display);
        
        // Helper launch if first time
        if (facade.appConfig.isHelp()) {
            helper.open(window);
            facade.appConfig.setHelp(false);
        }
    }

    @Override public boolean shutdown(boolean optional) throws Exception//Window close
    {
        if (window != null) {
            window.close();
            if (facade.appConfig.isModified()) { // Save configuration if modified
                facade.confSave();
                Out.print("INFO", "Workspace saved");
            }
            Out.print("PIVOT", "BYE");
        }
        return false;
    }

    @Override public void resume() throws Exception { }
    @Override public void suspend() throws Exception { }

    @Override public void initialize(Map<String, Object> args, URL url, Resources res)
    {
        facade.framesInit();// Tabs singletons init
        facade.tabsInit(false);// Tabs display init
        
        //Shortcuts definition
        this.getComponentKeyListeners().add(new ComponentKeyListener.Adapter() {
            @Override
            public boolean keyPressed(Component component, int keyCode, KeyLocation keyLocation)
            {
                switch(keyCode) {
                case Keyboard.KeyCode.F1://[F1] - Help
                    if (btHelp.isEnabled()) btHelp.press();
                    return true;
                default: break;
                }
                
                if (Keyboard.isPressed(Modifier.CTRL)) {//CTRL + [keycode]
                    switch(keyCode) {
                    case Keyboard.KeyCode.O://[ctrl+O] - load project
                        if (btLoad.isEnabled()) btLoad.press();
                        return true;
                    case Keyboard.KeyCode.S://[ctrl+S] - save project
                        if (Keyboard.isPressed(Modifier.SHIFT) || !btSave.isEnabled())
                            btSaveAs.press();//Save As
                        else btSave.press();//Save
                        return true;
                    case Keyboard.KeyCode.Z://[ctrl+Z] - undo all
                        if (btUndo.isEnabled()) btUndo.press();
                        return true;
                    case Keyboard.KeyCode.LEFT://[ctrl+LEFT] - previous step
                        if (btBack.isEnabled()) btBack.press();
                        return true;
                    case Keyboard.KeyCode.RIGHT://[ctrl+RIGHT] - next step
                        if (btNext.isEnabled()) btNext.press();
                        return true;
                    default:
                        break;
                    }   
                }
                else if (Keyboard.isPressed(Modifier.ALT)) {//ALT + [keycode]
                    switch(keyCode) {
                    case Keyboard.KeyCode.F4://[alt+F4] - quit
                        break;
                    default:
                        break;
                    }
                }
                
                return false;
            }
        });
        
        //Version display
        //info.setText(AppName + " " + AppVersion + " ");
        info.setText(AppVersion + " ");
        
        //Actions binding
        btSaveAs.setAction(new BrowseAction(saveFileBrowserSheet));
        btLoad.setAction(new BrowseAction(loadFileBrowserSheet));
        
        //Set default directory selection in File Browser (packs/)
        try {//Set working directory as root directory for file browser
            File saveDir = new File(IOFactory.savePath); 
            if (!saveDir.exists())
                saveDir = new File(".");
            saveFileBrowserSheet.setRootDirectory(saveDir.getCanonicalFile());
            loadFileBrowserSheet.setRootDirectory(saveDir.getCanonicalFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveFileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_AS);//FileBrowser Mode to File save
        loadFileBrowserSheet.setMode(FileBrowserSheet.Mode.OPEN);//FileBrowser Mode to File select
        
        loadFileBrowserSheet.setDisabledFileFilter(new Filter<File>() {//Filter for dcp files only
            @Override
            public boolean include(File file)
            {
                if (file.isDirectory() || file.getName().endsWith("."+IOFactory.dcpFileExt))
                    return false;
                return true;
            }
        });
        
        //File select in File Browser Event
        saveFileBrowserSheet.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override
            public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                //If save file changed
                if (fileBrowserSheet.getSelectedFile() != null) {
                    try {
                        String s = fileBrowserSheet.getSelectedFile().getCanonicalPath();
                        if (!s.equals(IOFactory.saveFile)) {
                            IOFactory.setSaveFile(s);
                            ASave.perform(fileBrowserSheet);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        loadFileBrowserSheet.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override
            public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                //If save file changed
                if (fileBrowserSheet.getSelectedFile() != null) {
                    try {
                        String s = fileBrowserSheet.getSelectedFile().getCanonicalPath();
                        if (!s.equals(IOFactory.saveFile)) {
                            IOFactory.setSaveFile(s);
                            ALoad.perform(fileBrowserSheet);
                            Prompt.prompt(MessageType.INFO, "Data loaded from saved project.", getWindow());
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        //Buttons listeners
        btBack.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                if (!btNext.isEnabled()) btNext.setEnabled(true);
                
                if (tabPane.getSelectedIndex()>0) {
                    tabPane.getTabs().get(tabPane.getSelectedIndex()).setEnabled(false);
                    tabPane.setSelectedIndex(tabPane.getSelectedIndex()-1);
                    tabPane.getTabs().get(tabPane.getSelectedIndex()).setEnabled(true);
                }
                
                if (tabPane.getSelectedIndex() == 0)
                    bt.setEnabled(false);
            }
        });
        btNext.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                if (!btBack.isEnabled()) btBack.setEnabled(true);
                
                if (tabPane.getSelectedIndex()<tabPane.getTabs().getLength()-1) {
                    tabPane.getTabs().get(tabPane.getSelectedIndex()).setEnabled(false);
                    tabPane.setSelectedIndex(tabPane.getSelectedIndex()+1);
                    tabPane.getTabs().get(tabPane.getSelectedIndex()).setEnabled(true);
                }
                
                if (tabPane.getSelectedIndex() == tabPane.getTabs().getLength()-1)
                    bt.setEnabled(false);
            }
        });
        btSave.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                ASave.perform(bt);
                Prompt.prompt(MessageType.INFO, "Project data saved.", getWindow());
            }
        });
        btUndo.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                if (Master.this.getTitle().contains("-") && Master.this.getTitle().contains("*")) // Modified save file
                    ALoad.perform(bt);
                else { // back to default factory setup
                    facade.factoryReset();
                    titleUpdate();
                    bt.setEnabled(false);
                    Out.print("INFO", "Back to factory setup configuration");
                }
            }
        });
        btDefault.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                facade.saveDefault();
                Out.print("INFO", "Default Workspace data saved.");
                Prompt.prompt(MessageType.INFO, "Workspace data saved as default.", getWindow());
            }
        });
        btHelp.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                helper.setIndex(tabPane.getSelectedIndex());
                helper.open(Master.this, null);
            }
        });
        
        //Tabs selection listener
        tabPane.getTabPaneSelectionListeners().add(new TabPaneSelectionListener.Adapter() {
            @Override public void selectedIndexChanged(TabPane tabPane, int previousSelectedIndex)
            {
                try {
                    facade.setOpenTab(tabPane.getSelectedIndex());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                
                updateStatusBar();
            }
        });
        
        updateStatusBar();
    }
    
    /**
     * Updates status bar info
     * step nbr, nbr of packs, nbr of groups
     */
    public void updateStatusBar()
    {
        statusBarStep.setText(String.valueOf(tabPane.getSelectedIndex()+1) + "/" +
                                String.valueOf(tabPane.getTabs().getLength()) + ",");
        statusBarNPacks.setText(String.valueOf(PackFactory.getPacks().getLength()));
        statusBarNGroups.setText(String.valueOf(GroupFactory.getGroups().getLength()));
    }

    
    /**
     * Launch the Pivot application [ GUI/Command mode ]
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length > 0) // Command Line Function
        {
            Out.setLogger(null);
            Out.print("INFO", "Command Line compiling enabled");
            Out.print("INFO", "Loading application...");
            new Master();
            
            for (String s: args) {
                if (new File(s).exists() && s.endsWith(".dcp")) {
                    Out.print("INFO", "Processing dcp file: " + s);
                    System.out.println();
                    
                    Master.facade.process(s);// compile save file with izpack
                }
                else {
                    Out.print("ERROR", "Filepath doesn't exist or file is incorrect! Please give a valid path to a dcp save file.");
                }
            }
        }
        else // GUI Application
            DesktopApplicationContext.main(Master.class, args);
    }

}
