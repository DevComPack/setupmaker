package com.dcp.sm.gui.pivot.frames;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.regex.PatternSyntaxException;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.collections.Sequence.Tree.Path;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.ButtonStateListener;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseListener;
import org.apache.pivot.wtk.ComponentStateListener;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.FileBrowserSheetListener;
import org.apache.pivot.wtk.FillPane;
import org.apache.pivot.wtk.LinkButton;
import org.apache.pivot.wtk.Menu;
import org.apache.pivot.wtk.MenuHandler;
import org.apache.pivot.wtk.Orientation;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.RadioButton;
import org.apache.pivot.wtk.Spinner;
import org.apache.pivot.wtk.SpinnerSelectionListener;
import org.apache.pivot.wtk.SplitPane;
import org.apache.pivot.wtk.SplitPaneListener;
import org.apache.pivot.wtk.TablePane;
import org.apache.pivot.wtk.TextInputContentListener;
import org.apache.pivot.wtk.TreeViewNodeStateListener;
import org.apache.pivot.wtk.TablePane.Row;
import org.apache.pivot.wtk.TreeView.NodeCheckState;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.TreeView;
import org.apache.pivot.wtk.Button.State;
import org.apache.pivot.wtk.content.ButtonData;
import org.apache.pivot.wtk.content.TreeNode;
import org.apache.pivot.wtk.effects.FadeTransition;
import org.apache.pivot.wtk.effects.Transition;
import org.apache.pivot.wtk.effects.TransitionListener;

import com.dcp.sm.config.io.IOFactory;
import com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.logic.factory.TypeFactory.SCAN_FOLDER;
import com.dcp.sm.logic.factory.TypeFactory.SCAN_MODE;
import com.dcp.sm.gui.pivot.Master;
import com.dcp.sm.gui.pivot.actions.BrowseAction;
import com.dcp.sm.gui.pivot.facades.ScanFacade;
import com.dcp.sm.gui.pivot.tasks.TaskDirScan;
import com.dcp.sm.gui.pivot.transitions.AppearTransition;
import com.dcp.sm.gui.pivot.validators.PathValidator;
import com.dcp.sm.logic.model.Group;
import com.dcp.sm.logic.model.Pack;
import com.dcp.sm.logic.model.config.AppConfig;
import com.dcp.sm.logic.model.config.SetupConfig;
import com.dcp.sm.main.log.Out;


public class ScanFrame extends FillPane implements Bindable
{
    // Singleton reference
    private static ScanFrame singleton;
    public static ScanFrame getSingleton() { assert (singleton != null); return singleton; }
    public ScanFacade facade;
    // Configuration
    private AppConfig appConfig = Master.facade.appConfig;
    private SetupConfig setupConfig = Master.facade.setupConfig;
    // Master class
    private Master master;
    public void setMaster(Master master) { this.master = master; }
    // Edit Flag
    private boolean modified = false;// True if tab changed data
    public void setModified(boolean VALUE) { assert master != null; modified = VALUE; master.setUndo(true); }
    public boolean isModified() { return modified; }
    
    public List<Pack> getPacks() {// Read-only Packs data (selected)
        if (treeView.getCheckmarksEnabled())// If select mode and not loaded
            return facade.getCheckedPacks(treeView.getCheckedPaths());
        return facade.getPacks();
    }
    public List<Group> getGroups() {
        return facade.getGroups();
    }
    
    // Browse Area
    @BXML private FileBrowserSheet fileBrowserSheet;// File Browser
    @BXML private TextInput inPath;// Path Text Input
    public String getScanDir() { return inPath.getText(); }// Return scanned directory path
    @BXML private PushButton btRefresh;// Refresh button
    @BXML private PushButton btBrowse;// Browse button
    // Recent Directories options
    @BXML private SplitPane hSplitPane;
    @BXML private TablePane recent_dirs;
    @BXML private TablePane recent_projects;
    // Scan Mode options
    @BXML private RadioButton btRadSimple;// Simple Scan Mode Radio Button
    @BXML private Checkbox btSelect;// Activate/Desactivate Node Check State Select mode
    @BXML private PushButton btSelectAll;// Select All button
    @BXML private PushButton btSelectNone;// Select None button
    @BXML private RadioButton btRadRecursiv;// Recursive Scan Mode Radio Button (default)
    @BXML private BoxPane depthPane;// Depth Label+Spinner Box Pane
    @BXML private Spinner depthSpinner;// Spinner depth value
    @BXML private PushButton btCollapse;// Collapse button
    @BXML private PushButton btExpand;// Expand button
    // Filter options
    @BXML private Checkbox cbZip;// Archives
    @BXML private Checkbox cbSetup;// Setups
    @BXML private Checkbox cbExe;// Executables
    @BXML private Checkbox cbDir;// Directories
    @BXML private Checkbox cbImg;// Images
    @BXML private Checkbox cbVid;// Videos
    @BXML private Checkbox cbSound;// Sounds
    @BXML private Checkbox cbDoc;// Documents
    @BXML private Checkbox cbCustTxt;// Custom set filter
    @BXML private Checkbox cbCustExpr;// Custom Expression filter button
    @BXML private TextInput inCustExpr;// Custom REGEXP filter input
    // Advanced options
    @BXML private Checkbox cbFolderScan;// Treat folders as groups in Set tab
    @BXML private Checkbox cbFolderTarget;// Treat folders as install paths for Packs in Set tab
    // Displays
    @BXML private static TreeView treeView;// Tree View for scanned directory
    // Filter
    private FilenameFilter filter;
    // Actions
    private Action ADirScan;// Scan the given directory
    // Menu Handlers
    private MenuHandler MHTreeView;
    //========================
    
    public ScanFrame() {// Constructor
        assert (singleton == null);
        singleton = this;
        
        // folder tree view context menu
        MHTreeView = new MenuHandler.Adapter() {
            @Override public boolean configureContextMenu(Component component, Menu menu, int x, int y)
            {
                final TreeNode node = (TreeNode) treeView.getSelectedNode();
                
                if (node != null) {
                    Menu.Section menuSection = new Menu.Section();
                    menu.getSections().add(menuSection);
    
                    Menu.Item itemOpen = new Menu.Item(new ButtonData(IOFactory.imgFolder, "scan"));
                    
                    final File newDir = new File(inPath.getText(), node.getText());
                    if (newDir.exists() && newDir.isDirectory()) {
                        itemOpen.getButtonPressListeners().add(new ButtonPressListener() {// scan selected folder
                            @Override public void buttonPressed(Button bt) {
                                Out.print(LOG_LEVEL.DEBUG, "Scan folder set to child folder: " + node.getText());
                                inPath.setText(newDir.getAbsolutePath());
                                ADirScan.perform(bt);
                            }
                        });
                    }
                    else itemOpen.setEnabled(false);
    
                    menuSection.add(itemOpen);
                }
                return false;
            }
        };
        
        filter = new FilenameFilter() {// Checkbox packs filters
            @Override public boolean accept(File dir, String name)
            {
                try {
                    if (inCustExpr.getText().length() > 0 && name.matches(inCustExpr.getText()))
                        return !cbCustExpr.isSelected();
                    if (IOFactory.isFileType(name, FILE_TYPE.Archive))
                        return !cbZip.isSelected();
                    if (IOFactory.isFileType(name, FILE_TYPE.Setup))
                        return !cbSetup.isSelected();
                    if (IOFactory.isFileType(name, FILE_TYPE.Executable))
                        return !cbExe.isSelected();
                    if (IOFactory.isFileType(name, FILE_TYPE.Image))
                        return !cbImg.isSelected();
                    if (IOFactory.isFileType(name, FILE_TYPE.Video))
                        return !cbVid.isSelected();
                    if (IOFactory.isFileType(name, FILE_TYPE.Sound))
                        return !cbSound.isSelected();
                    if (IOFactory.isFileType(name, FILE_TYPE.Document))
                        return !cbDoc.isSelected();
                    if (IOFactory.isFileType(name, FILE_TYPE.Custom))
                        return !cbCustTxt.isSelected();
                    return true;
                }
                catch (PatternSyntaxException e) {
                    cbCustExpr.setSelected(false);
                    return true;
                }
            }
        };
        
        ADirScan = new Action() {// Directory Scan from Filters/mode Action
            @Override public void perform(Component source) {
                if (btSelect.isSelected()) btSelect.press(); // * bugfix: scan on enabled selection doesn't update packs list
                
                TaskDirScan scan = new TaskDirScan(singleton, inPath.getText(), facade.getTreeData(), filter,
                        (String) depthSpinner.getSelectedItem(), !cbDir.isSelected());
 
                int res = scan.execute();
                if (res == 0) {
                    Out.print(LOG_LEVEL.DEBUG, "Scanned directory: " + inPath.getText());
                    // Save directory to app config recent dirs
                    appConfig.addRecentDir(new File(inPath.getText()));// add scanned dir to recent dirs
                    recentFieldFill(recent_dirs, appConfig.getRecentDirs());// refresh recent dirs list display
                    if (depthSpinner.getSelectedIndex() < 5)
                        treeView.expandAll();
                    else treeView.collapseAll();
                    inPath.requestFocus();
                    setModified(true);// Modified Flag (*)
                }
                else if (res == 2) {// Error: Path doesn't exist
                    Out.print(LOG_LEVEL.DEBUG, "Path error: " + inPath.getText());
                    Alert.alert("This path doesn't exist! Please correct it.", ScanFrame.this.getWindow());
                }
                
            } };
    }

    //========================
    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources)
    {
        facade = new ScanFacade();
        recentFieldFill(recent_dirs, appConfig.getRecentDirs());
        recentFieldFill(recent_projects, appConfig.getRecentProjects());
        hSplitPane.setSplitRatio(appConfig.getScanHorSplitPaneRatio());
        
        // Custom filters fill from settings.json file to UI
        if (IOFactory.custExt.length > 0) {
            final int maxExts = 3;// number of extensions to show in UI
            String customFilters = "(";
            for (int i = 0; i < maxExts && i < IOFactory.custExt.length; i++)
                customFilters += IOFactory.custExt[i] + " ";
            customFilters = "Custom " + customFilters.trim().replaceAll(" ", ", ")
                    + (IOFactory.custExt.length > maxExts ? "..)" : ")");
            Out.print(LOG_LEVEL.DEBUG, "Loaded Filter: "+customFilters);
            cbCustTxt.setButtonData(new ButtonData(customFilters));
        }
        
        // Select button transitions
        final AppearTransition selectExpTrans = new AppearTransition(btSelect, 150, 20);
        final FadeTransition selectCollTrans = new FadeTransition(btSelect, 100, 20);
        // Depth transitions
        final AppearTransition depthAppTrans = new AppearTransition(depthPane, 150, 20);
        final FadeTransition depthFadeTrans = new FadeTransition(depthPane, 100, 20);
        
        // Validators
        inPath.setValidator(new PathValidator(inPath, false));
        
        // Data Binding
        treeView.setMenuHandler(MHTreeView);
        treeView.setTreeData(facade.getTreeData());// Bind Tree view to data
        treeView.getTreeViewNodeStateListeners().add(new TreeViewNodeStateListener() {
            @Override public void nodeCheckStateChanged(TreeView tv, Path arg1, NodeCheckState arg2)
            {
                setModified(true);
            }
        });
        
        // Action Binding
        btRefresh.setAction(ADirScan);
        btRefresh.setButtonDataKey("ENTER");
        btBrowse.setAction(new BrowseAction(fileBrowserSheet));
        
        // Set default directory selection in File Browser (packs/)
        try {// Set working directory as root directory for file browser
            fileBrowserSheet.setRootDirectory(new File(".").getCanonicalFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_TO);// FileBrowser Mode to Directory selection
        
        // Directory select in File Browser Event
        fileBrowserSheet.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override
            public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                // If path changed
                if (!ScanFrame.this.inPath.getText().equals(fileBrowserSheet.getSelectedFile().getAbsolutePath())) {
                    boolean error = false;
                    if (!ScanFrame.this.inPath.getValidator().isValid(inPath.getText()) && ScanFrame.this.inPath.getText().length() > 0) {
                        error = true;
                    }
                    ScanFrame.this.inPath.setText(fileBrowserSheet.getSelectedFile().getAbsolutePath());
                    ADirScan.perform(fileBrowserSheet);// Scan Action
                    if (error == true) {
                        Out.print(LOG_LEVEL.INFO, "Updated folder path for packs to: "+ScanFrame.this.inPath.getText());
                        //setModified(false);
                    }
                }
            }
        });
        
        // update workspace scan horizontal splitpane ratio
        hSplitPane.getSplitPaneListeners().add(new SplitPaneListener.Adapter() {
            @Override public void splitRatioChanged(SplitPane sp, float ratio)
            {
                appConfig.setScanHorSplitPaneRatio(sp.getSplitRatio());
            }
        });
        
        // Update setup source path from inPath
        inPath.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                if (TI.isValid())// path exists
                    setupConfig.setSrcPath(TI.getText());
            }
        });
        // Set root directory from written path to file browser
        inPath.getComponentStateListeners().add(new ComponentStateListener.Adapter() {
            @Override public void focusedChanged(Component component, Component obverseComponent)
            {
                if (!component.isFocused()) {
                    File root = new File(inPath.getText());
                    if (root.exists() && root.isDirectory())
                        fileBrowserSheet.setRootDirectory(root);
                }
            }
        });
        
        //Simple scan activate radio button listener
        btRadSimple.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                if (facade.getScanMode() == SCAN_MODE.RECURSIVE_SCAN) {
                    setScanMode(SCAN_MODE.SIMPLE_SCAN);
                    btSelect.setVisible(true);
                    btSelect.setSelected(false);
                    selectExpTrans.start();//Select button expand transition
                    depthFadeTrans.start(new TransitionListener() {//Depth Fade transition
                        @Override public void transitionCompleted(Transition tr)
                        {
                            depthPane.setVisible(false);
                            btCollapse.setEnabled(false);
                            btExpand.setEnabled(false);
                            cbFolderScan.setEnabled(false);
                            cbFolderTarget.setEnabled(false);
                        }
                    });
                    ADirScan.perform(bt);
                }
            }
        });
        //Recursive scan activate radio button listener
        btRadRecursiv.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                if (facade.getScanMode() == SCAN_MODE.SIMPLE_SCAN) {
                    setScanMode(SCAN_MODE.RECURSIVE_SCAN);
                    btCollapse.setEnabled(true);
                    btExpand.setEnabled(true);
                    selectCollTrans.start(new TransitionListener() {//Select button collapse transition
                        @Override public void transitionCompleted(Transition arg0)
                        {
                            btSelect.setVisible(false);
                            btSelectAll.setEnabled(false);
                            btSelectNone.setEnabled(false);
                            cbFolderScan.setEnabled(true);
                            cbFolderTarget.setEnabled(true);
                            // enable Group folder options by default
                            cbFolderScan.setSelected(true);
                            setFolderScan(SCAN_FOLDER.GROUP_FOLDER);
                            cbFolderTarget.setSelected(true);
                            setFolderTarget(true);
                        }
                    });
                    depthPane.setVisible(true);
                    depthAppTrans.start();//Depth appear transition
                    ADirScan.perform(bt);
                }
            }
        });
        
        btExpand.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                treeView.expandAll();
            }
        });
        btCollapse.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                treeView.collapseAll();
            }
        });
        
        //Activate Checkmarks for tree view button listener
        btSelect.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                if (bt.isSelected()) {//Enable Selection
                    treeView.setCheckmarksEnabled(true);
                    btSelectAll.setEnabled(true);
                    btSelectNone.setEnabled(true);
                    selectAll();
                }
                else {//Disable Selection
                    treeView.setCheckmarksEnabled(false);
                    btSelectAll.setEnabled(false);
                    btSelectNone.setEnabled(false);
                    setModified(true);// (*)
                }
            }
        });
        //Select All button
        btSelectAll.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt) { selectAll(); }
        });
        //Select None button
        btSelectNone.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt) { selectNone(); }
        });
        
        //Spinner depth value change listener (do scan)
        depthSpinner.getSpinnerSelectionListeners().add(new SpinnerSelectionListener.Adapter() {
            @Override public void selectedIndexChanged(Spinner spinner, int previousSelectedIndex)
            {
                ADirScan.perform(spinner);//Directory scan
            }
        });
        
        //Checkbox Filters Event Listeners
        cbZip.getButtonPressListeners().add(new ButtonPressListener() {//Archive
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print(LOG_LEVEL.DEBUG,"Applied filter: Archive");
            }
        });
        cbSetup.getButtonPressListeners().add(new ButtonPressListener() {//Setup
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print(LOG_LEVEL.DEBUG,"Applied filter: Setup");
            }
        });
        cbExe.getButtonPressListeners().add(new ButtonPressListener() {//Executable
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print(LOG_LEVEL.DEBUG,"Applied filter: Executable");
            }
        });
        cbDir.getButtonPressListeners().add(new ButtonPressListener() {//Directory
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print(LOG_LEVEL.DEBUG,"Applied filter: Directory");
            }
        });
        cbImg.getButtonPressListeners().add(new ButtonPressListener() {//Images
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print(LOG_LEVEL.DEBUG,"Applied filter: Image");
            }
        });
        cbVid.getButtonPressListeners().add(new ButtonPressListener() {//Videos
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print(LOG_LEVEL.DEBUG,"Applied filter: Video");
            }
        });
        cbSound.getButtonPressListeners().add(new ButtonPressListener() {//Sounds
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print(LOG_LEVEL.DEBUG,"Applied filter: Sound");
            }
        });
        cbDoc.getButtonPressListeners().add(new ButtonPressListener() {//Documents
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print(LOG_LEVEL.DEBUG,"Applied filter: Document");
            }
        });
        cbCustTxt.getButtonPressListeners().add(new ButtonPressListener() {// Custom filter SETTINGS
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print(LOG_LEVEL.DEBUG,"Applied custom filter");
            }
        });
        cbCustExpr.getButtonPressListeners().add(new ButtonPressListener() {//Custom filter REGEXP
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print(LOG_LEVEL.DEBUG,"Applied regular expression filter");
            }
        });
        inCustExpr.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput textInput)
            {
                if (cbCustExpr.isSelected())
                    ADirScan.perform(textInput);//Action launch
            }
        });
        
        cbFolderScan.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                if (bt.isSelected())
                    setFolderScan(SCAN_FOLDER.GROUP_FOLDER);
                else setFolderScan(SCAN_FOLDER.PACK_FOLDER);
            }
        });
        cbFolderTarget.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                if (bt.isSelected())
                    setFolderTarget(true);
                else setFolderTarget(false);
            }
        });
        
    }
    
    //========================
    
    /**
     * Checkmarks Selection function for packs tree view (simple scan/select mode)
     */
    private void selectAll() {
        if (treeView.getCheckmarksEnabled())
        for(int i=0; i<facade.getTreeData().getLength(); i++)//Select all
            treeView.setNodeChecked(new Path(i), true);
    }
    private void selectNone() {
        if (treeView.getCheckmarksEnabled())
        for(int i=0; i<facade.getTreeData().getLength(); i++)//Select all
            treeView.setNodeChecked(new Path(i), false);
    }
    
    /**
     * Changes the Mode of Scan [SIMPLE|RECURSIVE]
     * @param new_mode
     */
    private void setScanMode(SCAN_MODE new_mode)
    {
        assert (new_mode == SCAN_MODE.RECURSIVE_SCAN || new_mode == SCAN_MODE.SIMPLE_SCAN);
        SCAN_MODE mode = facade.getScanMode();
        if (mode != new_mode) {
            //Directory Filter Checkbox value change
            if (new_mode == SCAN_MODE.RECURSIVE_SCAN) {// Recursive Scan
                treeView.setCheckmarksEnabled(false);// Check state disable
            }

            if (mode == SCAN_MODE.DEFAULT)
                Out.print(LOG_LEVEL.DEBUG, "Scan Mode set to " + new_mode);
            else
                Out.print(LOG_LEVEL.DEBUG, "Scan Mode changed from " + mode + " to " + new_mode);
            
            facade.setScanMode(new_mode);
            setModified(true);
        }
    }
    
    /**
     * Changes the folder scan mode [PACK|GROUP]
     * @param new_mode
     */
    private void setFolderScan(SCAN_FOLDER new_mode)
    {
        SCAN_FOLDER mode = facade.getFolderScan();
        if (mode != new_mode) {
            if (mode == SCAN_FOLDER.DEFAULT)
                Out.print(LOG_LEVEL.DEBUG, "Folder scan mode set to: " + new_mode);
            else
                Out.print(LOG_LEVEL.DEBUG, "Folder scan mode changed from " + mode + " to " + new_mode);
            
            facade.setFolderScan(new_mode);
            setModified(true);
        }
    }
    /**
     * Changes the folder target mode [true|false]
     * @param enable
     */
    private void setFolderTarget(boolean enable)
    {
        if (facade.getFolderTarget() != enable) {
            if (enable == true)
                Out.print(LOG_LEVEL.DEBUG, "Folder target mode set");
            else
                Out.print(LOG_LEVEL.DEBUG, "Folder target mode unset");
            
            facade.setFolderTarget(enable);
            setModified(true);
        }
    }
    
    /**
     * Fill recent paths field with list of Files
     * @param field: gui component to fill
     * @param files: list of paths to put
     */
    public void recentFieldFill(final TablePane field, final List<File> files) {
        if (files != null)
        try
        {
            if (field.getRows().getLength() > 0)// Data clear
                field.getRows().remove(0, field.getRows().getLength());
            
            //Analyze for wrong paths to remove
            int n = files.getLength();
            for(int i = 0; i < n; i++) {
                if (!files.get(i).exists()) {
                    files.remove(files.get(i));
                    i--; n--;
                }
            }
            
            for(final File file:files)
            {
                //Remove button
                ButtonData remBtData = new ButtonData(IOFactory.imgClose, "");
                final Button btRemove = new PushButton(remBtData);
                btRemove.setStyles("{toolbar:'true', backgroundColor:'WHITE', borderColor:'WHITE'}");
                btRemove.setTooltipText("Remove");
                btRemove.setVisible(false);

                final BoxPane removeBoxPane = new BoxPane();//BoxPane
                removeBoxPane.setOrientation(Orientation.HORIZONTAL);
                removeBoxPane.setStyles("{verticalAlignment:'center', horizontalAlignment:'right'}");
                removeBoxPane.add(btRemove);
                
                //Directory Link button
                ButtonData btData = new ButtonData(IOFactory.imgHistory,
                        file.getParentFile().getName() + "/" + file.getName());
                final LinkButton btLink = new LinkButton(btData);//LinkButton
                btLink.setStyles("{color:'#0198E1'}");
                btLink.setTooltipText(file.getAbsolutePath());

                final BoxPane linkBoxPane = new BoxPane();//BoxPane
                linkBoxPane.setOrientation(Orientation.HORIZONTAL);
                linkBoxPane.setStyles("{verticalAlignment:'center', horizontalAlignment:'left'}");
                linkBoxPane.add(btLink);
                
                //Row
                final FillPane fillPane = new FillPane();//FillPane
                fillPane.setOrientation(Orientation.HORIZONTAL);
                fillPane.add(linkBoxPane);
                
                final Row row = new Row();//TablePane.Row
                row.setHeight("16");
                row.add(fillPane);
                row.add(removeBoxPane);
                
                field.getRows().add(row);
                
                //Remove button display/hide event
                removeBoxPane.getComponentMouseListeners().add(new ComponentMouseListener.Adapter() {
                    @Override public void mouseOver(Component component)
                    {
                        btRemove.setVisible(true);
                    }
                    @Override public void mouseOut(Component component)
                    {
                        btRemove.setVisible(false);
                    }
                });
                
                //Remove button press event
                btRemove.getButtonPressListeners().add(new ButtonPressListener() {
                    @Override public void buttonPressed(Button bt)
                    {
                        field.getRows().remove(row);
                        Out.print(LOG_LEVEL.INFO, file.getName()+" entry removed");
                        if (field.equals(recent_dirs))
                            appConfig.removeRecentDir(file);
                        else if (field.equals(recent_projects))
                            appConfig.removeRecentProject(file);
                    }
                });
                
                //Recent directory link press event
                btLink.getButtonPressListeners().add(new ButtonPressListener() {
                    @Override public void buttonPressed(Button bt)
                    {
                        if (field.equals(recent_dirs)) { // Scan folder
                            inPath.setText(file.getAbsolutePath());
                            fileBrowserSheet.setRootDirectory(file.getParentFile());
                            fileBrowserSheet.setSelectedFile(file);
                            ADirScan.perform(bt);
                        }
                        else if (field.equals(recent_projects)) { // Load project
                            master.loadProject(file.getAbsolutePath());
                        }
                    }
                });
            }
            
            field.repaint();
        }
        catch (SerializationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Init options from default/loaded setup configuration
     * @param appConfig: application configuration
     * @param setupConfig: default or loaded setup configuration data
     */
    public void init(AppConfig appConfig, SetupConfig setupConfig) {
        inPath.setText(setupConfig.getSrcPath());
        facade.clear();
        
        this.appConfig = appConfig;
        this.setupConfig = setupConfig;
        
        SCAN_FOLDER scanFolder = setupConfig.getScanFolder();
        if (scanFolder != facade.getFolderScan()) {
            cbFolderScan.setSelected(scanFolder == SCAN_FOLDER.GROUP_FOLDER ? true : false);
            setFolderScan(scanFolder);
        }
        if (setupConfig.getScanTarget() != facade.getFolderTarget()) {
            cbFolderTarget.setSelected(setupConfig.getScanTarget());
            setFolderTarget(setupConfig.getScanTarget());
        }
        
        SCAN_MODE scanMode = setupConfig.getScanMode();
        if (scanMode != facade.getScanMode()) {
            // display update
            if (scanMode == SCAN_MODE.RECURSIVE_SCAN) {
                btRadRecursiv.setSelected(true);
                btCollapse.setEnabled(true);
                btExpand.setEnabled(true);
                btSelect.setVisible(false);
                btSelectAll.setEnabled(false);
                btSelectNone.setEnabled(false);
                depthPane.setVisible(true);
                cbFolderScan.setEnabled(true);
                cbFolderTarget.setEnabled(true);
            }
            else {
                btRadSimple.setSelected(true);
                btSelect.setVisible(true);
                btSelect.setSelected(false);
                depthPane.setVisible(false);
                btCollapse.setEnabled(false);
                btExpand.setEnabled(false);
                cbFolderScan.setEnabled(false);
                cbFolderTarget.setEnabled(false);
            }
            setScanMode(scanMode);// scans folder too
        }
        
        ADirScan.perform(null);
        setModified(false);// disable update on next step
    }
    
}
