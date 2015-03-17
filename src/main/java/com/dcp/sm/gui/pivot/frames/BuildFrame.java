package com.dcp.sm.gui.pivot.frames;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.Accordion;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.ActivityIndicator;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonGroup;
import org.apache.pivot.wtk.ButtonGroupListener;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.ButtonStateListener;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.FileBrowserSheetListener;
import org.apache.pivot.wtk.FillPane;
import org.apache.pivot.wtk.ImageView;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.ListButton;
import org.apache.pivot.wtk.ListButtonSelectionListener;
import org.apache.pivot.wtk.ListView;
import org.apache.pivot.wtk.ListViewItemListener;
import org.apache.pivot.wtk.Menu;
import org.apache.pivot.wtk.MenuHandler;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Prompt;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.RadioButton;
import org.apache.pivot.wtk.Spinner;
import org.apache.pivot.wtk.SpinnerSelectionListener;
import org.apache.pivot.wtk.TaskAdapter;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Button.State;
import org.apache.pivot.wtk.TextInputContentListener;
import org.apache.pivot.wtk.content.ButtonData;
import org.apache.pivot.wtk.validation.Validator;

import com.dcp.sm.config.io.IOFactory;
import com.dcp.sm.gui.pivot.Master;
import com.dcp.sm.gui.pivot.actions.BrowseAction;
import com.dcp.sm.gui.pivot.facades.BuildFacade;
import com.dcp.sm.gui.pivot.tasks.TaskIzpackDebug;
import com.dcp.sm.gui.pivot.tasks.TaskIzpackRun;
import com.dcp.sm.logic.factory.CastFactory;
import com.dcp.sm.logic.factory.TypeFactory.BUILD_MODE;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.logic.model.config.AppConfig;
import com.dcp.sm.logic.model.config.SetupConfig;
import com.dcp.sm.main.log.Out;


public class BuildFrame extends FillPane implements Bindable
{
    // Singleton reference
    private static BuildFrame singleton;
    public static BuildFrame getSingleton() { assert (singleton != null); return singleton; }
    public BuildFacade facade;
    //Configuration
    private SetupConfig setupConfig = Master.facade.setupConfig;
    // Master class
    private Master master;
    public void setMaster(Master master) { this.master = master; }
    // Edit Flag
    private boolean modified = false;// True if tab changed data
    public void setModified(boolean VALUE) { assert master != null; modified = VALUE; master.setUndo(true); }
    public boolean isModified() { return modified; }
    
    // Browse Area
    @BXML private FileBrowserSheet fileBrowserSheet;// File Browser
    @BXML private PushButton btBrowse;// Browse button
    @BXML private PushButton btOpen;// Open folder button
    @BXML private TextInput inTargetPath;// Path Text Input
    private Action AOpenFolder;
    // Build
    @BXML private ListButton lbBuild;// Build type (IzPack/NuGet)
    @BXML private Accordion accBuildOpt;// Build options Accordion
    @BXML private FillPane accIzpack;// Izpack accordion element
    @BXML private FillPane accNuget;// Nuget accordion element
    // Build options (IzPack)
        //Split Area
    @BXML private Checkbox cbSplit;//Split option enable/disable
    @BXML private TextInput inSize;//Split size
    @BXML private Spinner sizeSpinner;//Split size unit [MB|GB|GB]
        //Web Area
    @BXML private Checkbox cbWeb;//Web Setup option enabled/disable
    @BXML private TextInput inWebDir;//Web HTTP server URL
    @BXML private SftpDialog sftpDialog;//Web SFTP Configuration dialog
    @BXML private PushButton btWebConfig;//Web SFTP Configuration dialog button
    // Build options (NuGet)
    @BXML private TextInput inFeedSource;// Nuget Source Feed url
    @BXML private ButtonGroup buildSteps;// Build steps radio buttons
        //Build Steps
    @BXML private RadioButton rbConfig;// step 1
    @BXML private RadioButton rbSpec;// 2
    @BXML private RadioButton rbPack;// 3
    @BXML private RadioButton rbPush;// 4
        // NuGet data
    private int stepNbr;// step number (default 3)
    //Log Area
    @BXML private ListView logger;//List View for Log display
    //Compile Area
    @BXML private PushButton btCompile;//IzPack Compile button
    @BXML private PushButton btLaunch;//Package launch button
    @BXML private PushButton btDebug;//Install Debug button
    //Log Result
    @BXML private ImageView successIcon;//Result Success Icon image
    @BXML private Label successText;//Success Log Result Display
    @BXML private ImageView failIcon;//Result Fail Icon image
    @BXML private Label failText;//Fail Log Result Display
    @BXML private ActivityIndicator waiter;//Activity Indicator to wait for compile
    // Menus
    private MenuHandler menuHandler;
    
    public BuildFrame() {
        assert (singleton == null);
        singleton = this;
        
        // logger copy context menu
        menuHandler = new MenuHandler.Adapter() {
            @Override
            public boolean configureContextMenu(Component component, Menu menu, int x, int y)
            {
                Menu.Section menuSection = new Menu.Section();
                menu.getSections().add(menuSection);

                Menu.Item copy = new Menu.Item(new ButtonData(IOFactory.imgImport, "copy to clipboard"));
                copy.setAction(new Action() {
                    @SuppressWarnings("unchecked")
                    @Override public void perform(Component source) {
                        facade.copyToClipboard((Sequence<String>) logger.getSelectedItems()); // list of selected strings
                    }
                });

                menuSection.add(copy);
                return false;
            }
        };
        
        // Open target folder in explorer
        AOpenFolder = new Action() {
            @Override public void perform(Component c)
            {
                String target = inTargetPath.getText();
                
                if (target.length() > 0) {
                    try
                    {
                        BuildFrame.this.facade.openFolder(target);
                    }
                    catch (IOException e)
                    {
                        Out.print(LOG_LEVEL.ERR, "File Not Found: " + target);
                        e.printStackTrace();
                    }
                }
            }
        };
    }
    
    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources)
    {
        facade = new BuildFacade();
        
        // Data Binding
        try
        {
            fileBrowserSheet.setRootDirectory(new File(".").getCanonicalFile());
            logger.setListData(com.dcp.sm.main.log.Out.getCompileLog());// Bind compile log tags to List view logger
            logger.setMenuHandler(menuHandler);
            //init(); // build mode workspace
            //displayRefresh(); // build buttons workspace
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        // Action Binding
        btBrowse.setAction(new BrowseAction(fileBrowserSheet));
        btOpen.setAction(AOpenFolder);
        
        // Target file chosen from File Chooser event
        fileBrowserSheet.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                try {//Set the Input Text value to the selected file path
                    inTargetPath.setText(fileBrowserSheet.getSelectedFile().getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        // Build type option change
        lbBuild.getListButtonSelectionListeners().add(new ListButtonSelectionListener.Adapter() {
            @Override public void selectedItemChanged(ListButton lb, Object previousSelectedItem)
            {
                try
                {
                    // Save default workspace
                    String build = lbBuild.getSelectedItem().toString();
                    if (BUILD_MODE.IZPACK_BUILD.toString().equals(build)) { // IzPack
                        //Master.appConfig.setBuildMode(BUILD_MODE.IZPACK_BUILD);
                        setBuildMode(BUILD_MODE.IZPACK_BUILD);
                    }
                    else if (BUILD_MODE.NUGET_BUILD.toString().equals(build)) { // NuGet
                        //Master.appConfig.setBuildMode(BUILD_MODE.NUGET_BUILD);
                        setBuildMode(BUILD_MODE.NUGET_BUILD);
                    }
                    Out.print(LOG_LEVEL.DEBUG, "Build Mode set to " + build);
                    //displayRefresh();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        
        // Nuget Source text input listener
        inFeedSource.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                facade.setNugFeedUrl(TI.getText()); // Save default workspace
            }
        });
        
        // Build step changed
        buildSteps.getButtonGroupListeners().add(new ButtonGroupListener.Adapter() {
            @Override public void selectionChanged(ButtonGroup btGrp, Button bt)
            {
                if (rbConfig.isSelected())
                    stepNbr = 1;
                else if (rbSpec.isSelected())
                    stepNbr = 2;
                else if (rbPack.isSelected())
                    stepNbr = 3;
                else// if (rbPush.isSelected())
                    stepNbr = 4;
                
                facade.setNugStepNbr(stepNbr); // Save default workspace
            }
        });
        
        // Split option activate checkbox listener
        cbSplit.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                if (bt.isSelected()) setSplit();
                else unsetSplit();
            }
        });
        
        // Web setup otion checkbox listener
        cbWeb.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                if (bt.isSelected()) setWeb();
                else unsetWeb();
            }
        });
        
        // Set inSize text input filter for numbers only
        inSize.setValidator(new Validator() {
            @Override public boolean isValid(String size)
            {
                try {
                    if (size.equals("")) return true;// If empty
                    Integer.parseInt(size);
                }
                catch(NumberFormatException e) { return false; }// If not number
                return true;// Validated
            }
        });
        
        // TextInput constraints content listener
        inSize.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput textInput)
            {
                if (textInput.getText().equals(""))// Put 0 if no value
                    textInput.setText("0");
                // Enable Packaging
                facade.setIzSplitSize(Integer.parseInt(textInput.getText()), sizeSpinner.getSelectedItem().toString());
            }
        });
        // Update Packaging option size
        sizeSpinner.getSpinnerSelectionListeners().add(new SpinnerSelectionListener.Adapter() {
            @Override public void selectedItemChanged(Spinner spinner, Object obj)
            {
                facade.setIzSplitSize(Integer.parseInt(inSize.getText()), spinner.getSelectedItem().toString());
            }
        });
        
        // Web URL directory update
        inWebDir.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput ti)
            {
                if (ti.isEnabled()) {
                    if (ti.getText().length() == 0) facade.setIzWebSetup(false);
                    else {
                        facade.setIzWebSetup(true);
                        facade.setIzWebUrl(ti.getText());
                    }
                }
            }
        });
        
        // SFTP Configuration Dialog
        btWebConfig.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                sftpDialog.open(BuildFrame.this.getDisplay(), BuildFrame.this.getWindow());
            }
        });
        
        // Compile Task Launch from button btCompile
        btCompile.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt) {
                successIcon.setVisible(false);
                failIcon.setVisible(false);
                successText.setVisible(false);
                failText.setVisible(false);
                waiter.setActive(true);//Waiter show
                btCompile.setEnabled(false);
                btLaunch.setEnabled(false);
                btDebug.setEnabled(false);
                
                final TaskListener<Boolean> tlCompile = new TaskListener<Boolean>() {//Finished compilation
                    @Override public void executeFailed(Task<Boolean> t) {//Failed
                        taskFinished();
                        failIcon.setVisible(true);
                        failText.setVisible(true);
                        Prompt.prompt(MessageType.INFO, "Compiled with errors!", getWindow());
                    }
                    @Override public void taskExecuted(Task<Boolean> t) {//Success
                        if (t.getResult() == true) {//If no errors
                            taskFinished();
                            successIcon.setVisible(true);
                            successText.setVisible(true);
                            Prompt.prompt(MessageType.INFO, "Finished compiling.", getWindow());
                        } else executeFailed(t);//Compile Errors
                    }
                    private void taskFinished() {//Display Refresh
                        displayRefresh();
                        Out.newLine();
                        Out.print(LOG_LEVEL.INFO, "Finished.");
                        waiter.setActive(false);//Waiter hide
                    }
                };

                com.dcp.sm.main.log.Out.clearCompileLog();//Clear Saved Log
                String targetPath;
                if (facade.getBuildMode().equals(BUILD_MODE.IZPACK_BUILD))
                    targetPath = CastFactory.pathValidate(inTargetPath.getText(), setupConfig.getAppName(),"jar");
                else targetPath = inTargetPath.getText();
                facade.build(targetPath, tlCompile);
            }
        });
        
        // Launch the install button
        btLaunch.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button button) {
                successIcon.setVisible(false);
                failIcon.setVisible(false);
                successText.setVisible(false);
                failText.setVisible(false);
                waiter.setActive(true);//Waiter start
                btCompile.setEnabled(false);
                btLaunch.setEnabled(false);
                btDebug.setEnabled(false);
                
                TaskListener<Boolean> tlRun = new TaskListener<Boolean>() {//Finished Debug
                    @Override public void executeFailed(Task<Boolean> t) {
                        taskExecuted(t);
                    }
                    @Override public void taskExecuted(Task<Boolean> t) {
                        displayRefresh();
                        waiter.setActive(false);//Waiter stop
                    } };
                
                com.dcp.sm.main.log.Out.clearCompileLog();//Clear Saved log
                new TaskIzpackRun(CastFactory.pathValidate(inTargetPath.getText(), setupConfig.getAppName(),"jar"), logger, setupConfig.getAppName())
                    .execute(new TaskAdapter<Boolean>(tlRun));
                Out.print(LOG_LEVEL.INFO, "Launch generated package..");
                Out.newLine();
            }
        });
        
        // Debug with trace mode the install button
        btDebug.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button button) {
                successIcon.setVisible(false);
                failIcon.setVisible(false);
                successText.setVisible(false);
                failText.setVisible(false);
                waiter.setActive(true);//Waiter start
                btCompile.setEnabled(false);
                btLaunch.setEnabled(false);
                btDebug.setEnabled(false);
                
                TaskListener<Boolean> tlDebug = new TaskListener<Boolean>() {//Finished Debug
                    @Override public void executeFailed(Task<Boolean> t) {
                        taskExecuted(t);
                    }
                    @Override public void taskExecuted(Task<Boolean> t) {
                        displayRefresh();
                        waiter.setActive(false);//Waiter stop
                    } };
                
                com.dcp.sm.main.log.Out.clearCompileLog();//Clear Saved log
                new TaskIzpackDebug(CastFactory.pathValidate(inTargetPath.getText(), setupConfig.getAppName(),"jar"), logger, setupConfig.getAppName())
                    .execute(new TaskAdapter<Boolean>(tlDebug));
                Out.print(LOG_LEVEL.INFO, "Launch generated package with TRACE mode enabled..");
                Out.newLine();
            }
        });
        
        // Logger list view automatic scroll to last element
        logger.getListViewItemListeners().add(new ListViewItemListener.Adapter() {
            @Override
            public void itemInserted(ListView arg0, int arg1)
            {
                logger.setSelectedIndex(logger.getListData().getLength()-1);
            }
        });
    }
    
    // Display refresh for Nuget step number
    private void setNugStepNbr(int stepNbr) {
        if (stepNbr == 1)
            rbConfig.setSelected(true);
        else if (stepNbr == 2)
            rbSpec.setSelected(true);
        else if (stepNbr == 3)
            rbPack.setSelected(true);
        else// if (stepNbr = 4)
            rbPush.setSelected(true);
    }
    
    // Refresh workflow based on build-type (IzPack/NuGet)
    private void displayRefresh() {
        btCompile.setEnabled(true);
        if (facade.getBuildMode().equals(BUILD_MODE.IZPACK_BUILD)) {
            //accBuildOpt.setSelectedIndex(0);
            //accIzpack.setEnabled(true);
            //accNuget.setEnabled(false);
            btLaunch.setEnabled(true);
            btDebug.setEnabled(true);
        }
        else if (facade.getBuildMode().equals(BUILD_MODE.NUGET_BUILD)) {
            //accBuildOpt.setSelectedIndex(1);
            //accIzpack.setEnabled(false);
            //accNuget.setEnabled(true);
            btLaunch.setEnabled(false);
            btDebug.setEnabled(false);
        }
    }
    
    /**
     * Change Build tab workspace for new Build Mode
     * @param mode to change to
     * @throws IOException 
     */
    private void setBuildMode(BUILD_MODE mode) throws IOException
    {
        assert mode != BUILD_MODE.DEFAULT;
        facade.setBuildMode(mode);
        
        String filename = setupConfig.getAppName() + "-" + setupConfig.getAppVersion() + ".jar";
        filename = filename.replaceAll(" ", "");
        
        switch (mode)
        {
        case IZPACK_BUILD: // IzPack
            accBuildOpt.setSelectedIndex(0);
            accIzpack.setEnabled(true);
            accNuget.setEnabled(false);
            
            fileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_AS);//FileBrowser Mode to File selection
            
            if (new File(IOFactory.targetPath).exists()) {// If 'target' folder exists
                inTargetPath.setText(new File(IOFactory.targetPath, filename).getCanonicalPath());
            }
            else {
                inTargetPath.setText(new File(filename).getCanonicalPath());
            }
            fileBrowserSheet.setSelectedFile(new File(inTargetPath.getText()).getCanonicalFile());
            Out.print(LOG_LEVEL.DEBUG, "Export path set to file: " + inTargetPath.getText());
            break;
        case NUGET_BUILD: // NuGet
            accBuildOpt.setSelectedIndex(1);
            accIzpack.setEnabled(false);
            accNuget.setEnabled(true);
            
            fileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_TO);//FileBrowser Mode to Folder selection
            
            if (inTargetPath.getText().length() > 0) {
                File target = new File(inTargetPath.getText());
                if (!target.isDirectory())
                    inTargetPath.setText(target.getParent());
            }
            else if (new File(IOFactory.targetPath).exists()) {// If 'target' folder exists
                inTargetPath.setText(IOFactory.targetPath);
            }
            else {
                inTargetPath.setText(new File(".").getCanonicalPath());
            }
            Out.print(LOG_LEVEL.DEBUG, "Export path set to folder: " + inTargetPath.getText());
            break;
        default: break;
        }
    }

    /**
     * Enable/Set IzPack option
     */
    private void setSplit()
    {
        facade.setIzSplit(true);
        facade.setIzSplitSize(Integer.parseInt(inSize.getText()), sizeSpinner.getSelectedItem().toString());//Enable Packaging
        inSize.setEnabled(true);
        sizeSpinner.setEnabled(true);
        cbWeb.setSelected(false);
        unsetWeb();
    }
    private void setWeb()
    {
        facade.setIzWebSetup(true);
        inWebDir.setEnabled(true);
        btWebConfig.setEnabled(true);
        facade.setIzWebSetup(true);
        facade.setIzWebUrl(inWebDir.getText());
        cbSplit.setSelected(false);
        unsetSplit();
    }
    private void unsetSplit()
    {
        facade.setIzSplit(false);
        sizeSpinner.setEnabled(false);
        inSize.setEnabled(false);
    }
    private void unsetWeb()
    {
        facade.setIzWebSetup(false);
        inWebDir.setEnabled(false);
        btWebConfig.setEnabled(false);
        sftpDialog.disable();
        facade.setIzWebSetup(false);
    }
    
    /**
     * Build tab data/display initialize
     * @throws IOException 
     */
    public void init(AppConfig appConfig, SetupConfig setupConfig) throws IOException
    {
        lbBuild.setSelectedItem(appConfig.getBuildMode().toString());
        //setBuildMode(appConfig.getBuildMode());
        
        // IzPack bindings
        cbSplit.setSelected(appConfig.getDefaultIzpackConfig().isSplit());
        cbWeb.setSelected(appConfig.getDefaultIzpackConfig().isWebSetup());
        if (cbSplit.isSelected()) setSplit();
        else if (cbWeb.isSelected()) setWeb();
        inWebDir.setText(appConfig.getDefaultIzpackConfig().getWebUrl());
        
        // NuGet bindings
        stepNbr = facade.getNugStepNbr();
        setNugStepNbr(stepNbr);
        inFeedSource.setText(facade.getNugFeedUrl());// default source for debugging
        
        //this.appConfig = appConfig;
        this.setupConfig = setupConfig;
    }
    
    // Export path set
    public void update() throws IOException
    {
        Out.print(LOG_LEVEL.DEBUG, "Build tab update");
        String filename = setupConfig.getAppName() + "-" + setupConfig.getAppVersion() + ".jar";
        filename = filename.replaceAll(" ", "");
        
        String bm = lbBuild.getSelectedItem().toString();
        if (bm.equals(BUILD_MODE.IZPACK_BUILD.toString()))
        {
            if (new File(IOFactory.targetPath).exists()) {// If 'target' folder exists
                inTargetPath.setText(new File(IOFactory.targetPath, filename).getCanonicalPath());
            }
            else {
                inTargetPath.setText(new File(filename).getCanonicalPath());
            }
        }
        else if (bm.equals(BUILD_MODE.NUGET_BUILD.toString()))
        {
            if (inTargetPath.getText().length() > 0) {
                File target = new File(inTargetPath.getText());
                if (!target.isDirectory())
                    inTargetPath.setText(target.getParent());
            }
            else if (new File(IOFactory.targetPath).exists()) {// If 'target' folder exists
                inTargetPath.setText(IOFactory.targetPath);
            }
            else {
                inTargetPath.setText(new File(".").getCanonicalPath());
            }
        }
    }
    
}
