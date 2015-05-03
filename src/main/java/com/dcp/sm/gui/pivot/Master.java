package com.dcp.sm.gui.pivot;

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
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.FileBrowserSheetListener;
import org.apache.pivot.wtk.Keyboard;
import org.apache.pivot.wtk.Keyboard.Modifier;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.Menu;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Prompt;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TabPane;
import org.apache.pivot.wtk.TabPaneSelectionListener;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.Keyboard.KeyLocation;

import com.dcp.sm.config.io.IOFactory;
import com.dcp.sm.config.io.PropertiesFactory;
import com.dcp.sm.gui.pivot.helper.HelperFacade;
import com.dcp.sm.logic.factory.GroupFactory;
import com.dcp.sm.logic.factory.PackFactory;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;

/**
 * Main GUI Window Class
 * @author Said El Imam Said
 */
public class Master extends Window implements Application, Bindable
{
    // Constant Application Values
    public final static String AppName = "DCP Setup Maker";
    public static String AppVersion;
    static {
        PropertiesFactory props;
        try
        {
            props = new PropertiesFactory("app.properties");
            AppVersion = props.getProperty("version");
        }
        catch (IOException e) { e.printStackTrace(); }
    }
    
    // Class Data
    private static Window window;// Main application window
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
    // Menu
    @BXML private Menu.Item btSave;
    @BXML private Menu.Item btSaveAs;
    @BXML private Menu.Item btLoad;
    @BXML private Menu.Item btUndo;
    @BXML private Menu.Item btDefault;
    @BXML private Menu.Item btHelp;
    //@BXML private PushButton btInfo;
    
    //Actions
    private Action ASave;//Save project on file
    private Action ALoad;//Load project from file
    
    /**
     * Update title with save file and edit flag '*'
     */
    private void titleUpdate() {
        if (!Master.this.getTitle().contains("-")) {// No save file defined
            if (Master.this.getTitle().contains("*"))// remove * flag from title
                Master.this.setTitle(Master.this.getTitle().substring(0, Master.this.getTitle().length()-1));
            if (IOFactory.saveFile.length() > 0)
                Master.this.setTitle(Master.this.getTitle().concat(" - " + IOFactory.saveFile ));
        }
        else {// Already saved on a file
            if (IOFactory.saveFile.length() > 0) // Save file set
                Master.this.setTitle(Master.this.getTitle().
                        substring(0, Master.this.getTitle().indexOf('-') + 2).
                        concat(IOFactory.saveFile));
            else // Save file unset
                Master.this.setTitle(Master.this.getTitle().
                        substring(0, Master.this.getTitle().indexOf('-') - 1));
        }
    }
    
    public Master() {
        if (facade == null)
        {
            IOFactory.init();// Factory memory data initialize
            facade = new Facade(AppName, AppVersion);
        }
        else {
            facade.setWindow(this); // bind window application to facade

            //Save action
            ASave = new Action() {// Project Save Action
                @Override public void perform(Component source) {
                    if (facade.save(IOFactory.saveFile)) {
                        // Enable Save button
                        if (!btSave.isEnabled()) {
                            btSave.setEnabled(true);
                            //btDefault.setEnabled(false);
                        }
                        setUndo(true);
                    }
                } };
            //Load Action
            ALoad = new Action() {// Project Load Action
                @Override
                public void perform(Component c)
                {
                    if (facade.load(IOFactory.saveFile)) {
                        facade.tabsInit();
                        // Go to Set tab
                        tabPane.setSelectedIndex(0);
                        for(int i=1; i<tabPane.getTabs().getLength(); i++)
                            tabPane.getTabs().get(i).setEnabled(false);
                        tabPane.getTabs().get(0).setEnabled(true);
                        //if (tabPane.getSelectedIndex() == 1)// If already on Set tab
                        //    ScanFrame.setLoaded(false);// Disable scan loaded flag*
                        btBack.setEnabled(false);
                        btNext.setEnabled(true);
                        // Enable Save button
                        if (!btSave.isEnabled()) {
                            btSave.setEnabled(true);
                        }
                        setUndo(true);
                    }
                }
            };
        }
    }
    @Override public void resume() throws Exception { }
    @Override public void suspend() throws Exception { }

    @Override public void startup(Display display, Map<String, String> properties) throws Exception// App start
    {
        Out.print(LOG_LEVEL.DEBUG, "Data loaded to memory");
        
        Locale.setDefault(Locale.ENGLISH);// Set default UI language to English
        BXMLSerializer bxmlSerializer = new BXMLSerializer();
        window = (Window) bxmlSerializer.readObject(getClass().getResource("master.bxml"));
        window.open(display);

        // Helper launch if first time
        if (facade.appConfig.isHelp()) {
            helper.open(window);
            facade.appConfig.setHelp(false);
        }
        
        Out.print(LOG_LEVEL.DEBUG, "Window open");
    }

    @Override public boolean shutdown(boolean optional) throws Exception//Window close
    {
        if (window != null) {
            window.close();
            if (facade.appConfig.isModified()) { // Save configuration if modified
                facade.confSave();
                Out.print(LOG_LEVEL.INFO, "Workspace saved");
            }
            Out.print(LOG_LEVEL.DEBUG, "BYE");
        }
        return false;
    }
    
    /**
     * Load a project file in the GUI
     * @param file to load (dcp file)
     */
    public void loadProject(String file)
    {
        IOFactory.setSaveFile(file);
        ALoad.perform(null);
        Prompt.prompt(MessageType.INFO, "Data loaded from saved project.", getWindow());
    }
    
    /**
     * enable/disable undo
     * @param enable: true|false
     */
    public void setUndo(boolean enable)
    {
        btUndo.setEnabled(enable);
        titleUpdate();
    }

    @Override public void initialize(Map<String, Object> args, URL url, Resources res)
    {
        facade.framesInit(this);// Tabs singletons init
        facade.tabsInit();// Tabs display init
        
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
        btSaveAs.setAction(new Action() {
            @Override public void perform(Component source)
            {
                saveFileBrowserSheet.open(getWindow());
            }
        });
        btLoad.setAction(new Action() {
            @Override public void perform(Component source)
            {
                loadFileBrowserSheet.open(getWindow());
            }
        });
        
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
                            loadProject(s);
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
                if (Master.this.getTitle().contains("-") && Master.this.getTitle().contains("*")) { // Modified save file
                    ALoad.perform(bt);
                    Out.print(LOG_LEVEL.INFO, "Back to save file");
                }
                else { // back to default factory setup
                    facade.factoryReset();
                    titleUpdate();
                    bt.setEnabled(false);
                    Out.print(LOG_LEVEL.INFO, "Back to factory setup configuration");
                }
            }
        });
        btDefault.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                facade.saveDefault();
                Out.print(LOG_LEVEL.INFO, "Default Workspace data saved.");
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

}
