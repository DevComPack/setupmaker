package dcp.gui.pivot.frames;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.regex.PatternSyntaxException;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.ArrayList;
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
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.FileBrowserSheetListener;
import org.apache.pivot.wtk.FillPane;
import org.apache.pivot.wtk.LinkButton;
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

import dcp.config.io.IOFactory;
import dcp.logic.factory.TypeFactory.FILE_TYPE;
import dcp.logic.factory.TypeFactory.SCAN_MODE;
import dcp.gui.pivot.Master;
import dcp.gui.pivot.actions.BrowseAction;
import dcp.gui.pivot.tasks.TaskDirScan;
import dcp.gui.pivot.transitions.AppearTransition;
import dcp.gui.pivot.validators.PathValidator;
import dcp.logic.model.Group;
import dcp.logic.model.Pack;
import dcp.main.log.Out;


public class ScanFrame extends FillPane implements Bindable
{
    //Singleton reference
    private static ScanFrame singleton;
    public static ScanFrame getSingleton() { assert (singleton != null); return singleton; }
    //Flags
    private boolean modified = false;//True if tab changed data
    public void setModified(boolean VALUE) { modified = VALUE; }
    public boolean isModified() { return modified; }
    
    private static boolean loaded = false;//True if tab loaded data
    public static void setLoaded(boolean VALUE) { loaded = VALUE; }
    public static boolean isLoaded() { return loaded; }
    //Packs
    private static List<Pack> packs = new ArrayList<Pack>();//List of scanned packs
    public void setPacks(List<Pack> packs) {
        ScanFrame.packs = null;
        ScanFrame.packs = packs;
        ScanFrame.setLoaded(true);//Loaded flag
    }
    public static List<Pack> getPacks() {//Read-only Packs data (selected)
        if (treeView.getCheckmarksEnabled() && !loaded)//If select mode and not loaded
            return getCheckedPacks();
        return packs;
    }
    //Groups
    private static List<Group> groups = new ArrayList<Group>();//List of scanned directories
    public static void setGroups(List<Group> groups) {
        ScanFrame.groups = null;
        ScanFrame.groups = groups;
        ScanFrame.setLoaded(true);//Loaded flag
    }
    public static List<Group> getGroups() {//Read-only Groups data
        return groups;
    }
    //Scan Modes
    private static SCAN_MODE scanMode = SCAN_MODE.DEFAULT;
    public static SCAN_MODE getScanMode() { return scanMode; }//Returns selected Scan Mode
    
    //Browse Area
    @BXML private FileBrowserSheet fileBrowserSheet;//File Browser
    @BXML private TextInput inPath;//Path Text Input
    public String getScanDir() { return inPath.getText(); }//Return scanned directory path
    @BXML private PushButton btRefresh;//Refresh button
    @BXML private PushButton btBrowse;//Browse button
    //Recent Directories options
    @BXML private SplitPane hSplitPane;
    @BXML private TablePane recent_dirs;
    //Scan Mode options
    @BXML private RadioButton btRadSimple;//Simple Scan Mode Radio Button
    @BXML private Checkbox btSelect;//Activate/Desactivate Node Check State Select mode
    @BXML private PushButton btSelectAll;//Select All button
    @BXML private PushButton btSelectNone;//Select None button
    @BXML private RadioButton btRadRecursiv;//Recursive Scan Mode Radio Button (default)
    @BXML private BoxPane depthPane;//Depth Label+Spinner Box Pane
    @BXML private Spinner depthSpinner;//Spinner depth value
    @BXML private PushButton btCollapse;//Collapse button
    @BXML private PushButton btExpand;//Expand button
    //Filter options
    @BXML private Checkbox cbZip;//Archives
    @BXML private Checkbox cbSetup;//Setups
    @BXML private Checkbox cbExe;//Executables
    @BXML private Checkbox cbDir;//Directories
    @BXML private Checkbox cbImg;//Images
    @BXML private Checkbox cbVid;//Videos
    @BXML private Checkbox cbSound;//Sounds
    @BXML private Checkbox cbDoc;//Documents
    @BXML private Checkbox cbCustTxt;//Custom set filter
    @BXML private Checkbox cbCustExpr;//Custom Expression filter button
    @BXML private TextInput inCustExpr;//Custom REGEXP filter input
    //Displays
    @BXML private static TreeView treeView;//Tree View for scanned directory
    private static List<TreeNode> treeData = new ArrayList<TreeNode>();//Tree view data
    //Filter
    private FilenameFilter filter;
    //Actions
    private Action ADirScan;//Scan the given directory
    //========================
    
    public ScanFrame() {//Constructor
        assert (singleton == null);
        singleton = this;
        
        filter = new FilenameFilter() {//Checkbox packs filters
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
        
        ADirScan = new Action() {//Directory Scan from Filters/mode Action
            @Override public void perform(Component source) {
                if (btSelect.isSelected()) btSelect.press(); // * bugfix: scan on enabled selection doesn't update packs list
                
                TaskDirScan scan = new TaskDirScan(singleton, inPath.getText(), treeData, filter,
                        (String) depthSpinner.getSelectedItem(), !cbDir.isSelected());
 
                int res = scan.execute();
                if (res == 0) {
                    Out.print("SCAN", "Scanned directory: " + inPath.getText());
                    //Save directory to app config recent dirs
                    Master.appConfig.addRecentDir(new File(inPath.getText()));
                    recentDirsFill(Master.appConfig.getRecentDirs());
                    if (depthSpinner.getSelectedIndex() < 5)
                        treeView.expandAll();
                    else treeView.collapseAll();
                    setModified(true);//Modified Flag (*)
                }
                else if (res == 2) {//Error: Path doesn't exist
                    Out.print("SCAN", "Path error: " + inPath.getText());
                    Alert.alert("This path doesn't exist!", ScanFrame.this.getWindow());
                }
                
            } };
    }

    //========================
    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources)
    {
        recentDirsFill(Master.appConfig.getRecentDirs());
        hSplitPane.setSplitRatio(Master.appConfig.getScanHorSplitPaneRatio());
        
        // Custom filters fill from settings.json file to UI
        if (IOFactory.custExt.length > 0) {
            final int maxExts = 3;// number of extensions to show in UI
            String customFilters = "(";
            for (int i = 0; i < maxExts && i < IOFactory.custExt.length; i++)
                customFilters += IOFactory.custExt[i] + " ";
            customFilters = "Custom " + customFilters.trim().replaceAll(" ", ", ")
                    + (IOFactory.custExt.length > maxExts ? "..)" : ")");
            Out.print("FACTORY", "Loaded Filter: "+customFilters);
            cbCustTxt.setButtonData(new ButtonData(customFilters));
        }
        
        //Select button transitions
        final AppearTransition selectExpTrans = new AppearTransition(btSelect, 150, 20);
        final FadeTransition selectCollTrans = new FadeTransition(btSelect, 100, 20);
        //Depth transitions
        final AppearTransition depthAppTrans = new AppearTransition(depthPane, 150, 20);
        final FadeTransition depthFadeTrans = new FadeTransition(depthPane, 100, 20);
        
        //Validators
        inPath.setValidator(new PathValidator());
        
        //Data Binding
        treeView.setTreeData(treeData);//Bind Tree view to data
        treeView.getTreeViewNodeStateListeners().add(new TreeViewNodeStateListener() {
            @Override public void nodeCheckStateChanged(TreeView tv, Path arg1, NodeCheckState arg2)
            {
                setModified(true);
            }
        });
        
        //Action Binding
        btRefresh.setAction(ADirScan);
        btRefresh.setButtonDataKey("ENTER");
        btBrowse.setAction(new BrowseAction(fileBrowserSheet));
        
        //Set default directory selection in File Browser (packs/)
        try {//Set working directory as root directory for file browser
            fileBrowserSheet.setRootDirectory(new File(".").getCanonicalFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_TO);//FileBrowser Mode to Directory selection
        
        //Directory select in File Browser Event
        fileBrowserSheet.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override
            public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                //If path changed
                if (!ScanFrame.this.inPath.getText().equals(fileBrowserSheet.getSelectedFile().getAbsolutePath())) {
                    if (ScanFrame.this.inPath.getValidator().isValid(inPath.getText()) || ScanFrame.this.inPath.getText().length() == 0) {
                        ScanFrame.this.inPath.setText(fileBrowserSheet.getSelectedFile().getAbsolutePath());
                        ADirScan.perform(fileBrowserSheet);//Action launch
                    }
                    else {
                        ScanFrame.this.inPath.setText(fileBrowserSheet.getSelectedFile().getAbsolutePath());
                        Out.print("INFO", "Updated folder path for packs to: "+ScanFrame.this.inPath.getText());
                    }
                }
            }
        });
        
        // update workspace scan horizontal splitpane ratio
        hSplitPane.getSplitPaneListeners().add(new SplitPaneListener.Adapter() {
            @Override public void splitRatioChanged(SplitPane sp, float ratio)
            {
                Master.appConfig.setScanHorSplitPaneRatio(sp.getSplitRatio());
            }
        });
        
        // update setup source path from inPath
        inPath.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                Master.setupConfig.setSrcPath(TI.getText());
            }
        });
        
        //Simple scan activate radio button listener
        btRadSimple.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                if (getScanMode() == SCAN_MODE.RECURSIVE_SCAN) {
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
                        }
                    });
                }
            }
        });
        
        //Recursive scan activate radio button listener
        btRadRecursiv.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                if (getScanMode() == SCAN_MODE.SIMPLE_SCAN) {
                    setScanMode(SCAN_MODE.RECURSIVE_SCAN);
                    btCollapse.setEnabled(true);
                    btExpand.setEnabled(true);
                    selectCollTrans.start(new TransitionListener() {//Select button collapse transition
                        @Override public void transitionCompleted(Transition arg0)
                        {
                            btSelect.setVisible(false);
                            btSelectAll.setEnabled(false);
                            btSelectNone.setEnabled(false);
                        }
                    });
                    depthPane.setVisible(true);
                    depthAppTrans.start();//Depth appear transition
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
                Out.print("SCAN","Applied filter: Archive");
            }
        });
        cbSetup.getButtonPressListeners().add(new ButtonPressListener() {//Setup
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print("SCAN","Applied filter: Setup");
            }
        });
        cbExe.getButtonPressListeners().add(new ButtonPressListener() {//Executable
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print("SCAN","Applied filter: Executable");
            }
        });
        cbDir.getButtonPressListeners().add(new ButtonPressListener() {//Directory
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print("SCAN","Applied filter: Directory");
            }
        });
        cbImg.getButtonPressListeners().add(new ButtonPressListener() {//Images
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print("SCAN","Applied filter: Image");
            }
        });
        cbVid.getButtonPressListeners().add(new ButtonPressListener() {//Videos
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print("SCAN","Applied filter: Video");
            }
        });
        cbSound.getButtonPressListeners().add(new ButtonPressListener() {//Sounds
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print("SCAN","Applied filter: Sound");
            }
        });
        cbDoc.getButtonPressListeners().add(new ButtonPressListener() {//Documents
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print("SCAN","Applied filter: Document");
            }
        });
        cbCustTxt.getButtonPressListeners().add(new ButtonPressListener() {// Custom filter SETTINGS
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print("SCAN","Applied custom filter");
            }
        });
        cbCustExpr.getButtonPressListeners().add(new ButtonPressListener() {//Custom filter REGEXP
            @Override public void buttonPressed(Button button) {
                ADirScan.perform(button);//Action launch
                Out.print("SCAN","Applied regular expression filter");
            }
        });
        inCustExpr.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput textInput)
            {
                if (cbCustExpr.isSelected())
                    ADirScan.perform(textInput);//Action launch
            }
        });
        
        //Default Scan Mode loaded
        if (Master.appConfig.getScanMode() == SCAN_MODE.RECURSIVE_SCAN) {
            setScanMode(SCAN_MODE.RECURSIVE_SCAN);
            btRadRecursiv.setSelected(true);
            btCollapse.setEnabled(true);
            btExpand.setEnabled(true);
            btSelect.setVisible(false);
            btSelectAll.setEnabled(false);
            btSelectNone.setEnabled(false);
            depthPane.setVisible(true);
        }
        else {
            setScanMode(SCAN_MODE.SIMPLE_SCAN);
            btRadSimple.setSelected(true);
            btSelect.setVisible(true);
            btSelect.setSelected(false);
            depthPane.setVisible(false);
            btCollapse.setEnabled(false);
            btExpand.setEnabled(false);
        }
    }
    
    //========================
    
    /**
     * Checkmarks Selection function for packs tree view (simple scan/select mode)
     */
    private void selectAll() {
        if (treeView.getCheckmarksEnabled())
        for(int i=0; i<treeData.getLength(); i++)//Select all
            treeView.setNodeChecked(new Path(i), true);
    }
    private void selectNone() {
        if (treeView.getCheckmarksEnabled())
        for(int i=0; i<treeData.getLength(); i++)//Select all
            treeView.setNodeChecked(new Path(i), false);
    }
    private static List<Pack> getCheckedPacks() {
        List<Pack> selected = new ArrayList<Pack>();
        Sequence<Path> list = treeView.getCheckedPaths();
        if (list.getLength() > 0) {
            Path p;
            for(int i = 0; i < list.getLength(); i++) {
                p = list.get(i);
                TreeNode node = treeData.get(p.get(0));
                for(Pack P:packs) {
                    if (node.getParent() == null && P.getName().equalsIgnoreCase(node.getText()) ) {
                        selected.add(P);
                        break;
                    }
                }
            }
        }
        return selected;
    }
    
    /**
     * Changes the Mode of Scan [SIMPLE|RECURSIVE]
     * @param MODE
     */
    private void setScanMode(SCAN_MODE MODE)
    {
        if (scanMode != MODE &&
            (MODE == SCAN_MODE.RECURSIVE_SCAN || MODE == SCAN_MODE.SIMPLE_SCAN) ) {
            
            //Directory Filter Checkbox value change
            if (MODE == SCAN_MODE.RECURSIVE_SCAN) {//Recursive Scan
                treeView.setCheckmarksEnabled(false);//Checkstate disable
            }

            if (scanMode == SCAN_MODE.DEFAULT) {
                scanMode = MODE;
                Out.print("SCAN", "Scan Mode set to " + MODE);
            }
            else {
                Out.print("SCAN", "Scan Mode changed from " + scanMode + " to " + MODE);
                scanMode = MODE;
                ADirScan.perform(this);//Refresh the Packs View
            }
        }
    }
    
    /**
     * Fill recent directories field with list of Files
     * @param directories
     */
    public void recentDirsFill(final List<File> directories) {
        if (directories != null)
        try
        {
            if (recent_dirs.getRows().getLength() > 0)//Data clear
                recent_dirs.getRows().remove(0, recent_dirs.getRows().getLength());
            
            //Analyze for wrong paths to remove
            int n = directories.getLength();
            for(int i = 0; i < n; i++) {
                if (!directories.get(i).exists()) {
                    directories.remove(directories.get(i));
                    i--; n--;
                }
            }
            
            for(final File dir:directories)
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
                        dir.getParentFile().getName() + "/" + dir.getName());
                final LinkButton btLink = new LinkButton(btData);//LinkButton
                btLink.setStyles("{color:'#0198E1'}");
                btLink.setTooltipText(dir.getAbsolutePath());

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
                
                recent_dirs.getRows().add(row);
                
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
                        recent_dirs.getRows().remove(row);
                        Out.print("SCAN", dir.getName()+" entry removed");
                        Master.appConfig.removeRecentDir(dir);
                    }
                });
                
                //Recent directory link press event
                btLink.getButtonPressListeners().add(new ButtonPressListener() {
                    @Override public void buttonPressed(Button bt)
                    {
                        inPath.setText(dir.getAbsolutePath());
                        fileBrowserSheet.setRootDirectory(dir.getParentFile());
                        fileBrowserSheet.setSelectedFile(dir);
                        ADirScan.perform(bt);
                    }
                });
            }
            
            recent_dirs.repaint();
        }
        catch (SerializationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * clear scanned directory path
     */
    public void init(String srcPath) {
        inPath.setText(srcPath);
        treeData.clear();
        groups.clear();
        packs.clear();
    }
    
}
