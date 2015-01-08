package dcp.gui.pivot.frames;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
import org.apache.pivot.wtk.ListViewSelectionListener;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Prompt;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.RadioButton;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.Spinner;
import org.apache.pivot.wtk.SpinnerSelectionListener;
import org.apache.pivot.wtk.TaskAdapter;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Button.State;
import org.apache.pivot.wtk.TextInputContentListener;
import org.apache.pivot.wtk.validation.Validator;

import dcp.config.io.IOFactory;
import dcp.gui.pivot.Master;
import dcp.gui.pivot.actions.BrowseAction;
import dcp.gui.pivot.tasks.TaskIzpackCompile;
import dcp.gui.pivot.tasks.TaskIzpackDebug;
import dcp.gui.pivot.tasks.TaskIzpackRun;
import dcp.gui.pivot.tasks.TaskNugetCompile;
import dcp.logic.factory.CastFactory;
import dcp.logic.factory.TypeFactory.BUILD_MODE;
import dcp.main.log.Out;


public class BuildFrame extends FillPane implements Bindable
{
    // Singleton reference
    private static BuildFrame singleton;
    public static BuildFrame getSingleton() { if (singleton != null) return singleton; else return new BuildFrame(); }
    //------DATA
    // Flags
    private static boolean modified = false;//True if tab processed data
    public static void setModified(boolean VALUE) { modified = VALUE; }
    public static boolean isModified() { return modified; }
    // Browse Area
    @BXML private FileBrowserSheet fileBrowserSheet;//File Browser
    @BXML private PushButton btBrowse;//Browse button
    @BXML private PushButton btOpen;//Open folder button
    @BXML private TextInput inTargetPath;//Path Text Input
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
    private String feedUrl;// default feed url
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
    
    public BuildFrame() {
        if (singleton == null) singleton = this;
        
        // Open target folder in explorer
        AOpenFolder = new Action() {
            @Override public void perform(Component c)
            {
                String target = inTargetPath.getText();
                
                if (target.length() > 0) {
                    Desktop desktop = Desktop.getDesktop();
                    File dirToOpen = null;
                    try {
                        dirToOpen = new File(target);
                        if (dirToOpen.isFile()) // Get parent folder of file
                            dirToOpen = dirToOpen.getParentFile();
                        desktop.open(dirToOpen); // Open folder
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        Out.print("ERROR", "File Not Found: " + dirToOpen.getAbsolutePath());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
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
    
    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
        try {
            fileBrowserSheet.setRootDirectory(new File(".").getCanonicalFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //Data Binding
        switch(Master.appConfig.getBuildMode()) {
        case IZPACK_BUILD:
            lbBuild.setSelectedItem("IzPack");
            break;
        case NUGET_BUILD:
            lbBuild.setSelectedItem("NuGet");
            break;
        case DEFAULT: default: break;
        }
        feedUrl = Master.appConfig.getNugFeedUrl();
        stepNbr = Master.appConfig.getNugStepNbr();
        setNugStepNbr(stepNbr);
        lbBuild.setSelectedItem("IzPack");// 'IzPack' build type by default
        logger.setListData(dcp.main.log.Out.getCompileLog());//Bind compile log tags to List view logger
        try { init(Master.setupConfig.getAppName(), Master.setupConfig.getAppVersion()); }
        catch (IOException e) {
            e.printStackTrace();
        }
        displayRefresh();
        
        //Action Binding
        btBrowse.setAction(new BrowseAction(fileBrowserSheet));
        btOpen.setAction(AOpenFolder);
        
        //Target file chosen from File Chooser event
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
                    switch ((String) lbBuild.getSelectedItem()) // Save default workspace
                    {
                        case "IzPack":
                            Master.appConfig.setBuildMode(BUILD_MODE.IZPACK_BUILD);
                            break;
                        case "NuGet":
                            Master.appConfig.setBuildMode(BUILD_MODE.NUGET_BUILD);
                            break;
                        default: break;
                    }
                    init(Master.setupConfig.getAppName(), Master.setupConfig.getAppVersion());
                    displayRefresh();
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
                feedUrl = TI.getText();
                Master.appConfig.setNugFeedUrl(feedUrl); // Save default workspace
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
                
                Master.appConfig.setNugStepNbr(stepNbr); // Save default workspace
            }
        });
        
        //Split option activate checkbox listener
        cbSplit.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                if (bt.isSelected()) {//Activated Split
                    inSize.setEnabled(true);
                    sizeSpinner.setEnabled(true);
                    setSplit(Integer.parseInt(inSize.getText()) *
                            (((String) sizeSpinner.getSelectedItem()).equals("TB")?1024*1024:
                                ((String) sizeSpinner.getSelectedItem()).equals("GB")?1024:1) );//Enable Packaging
                    cbWeb.setSelected(false);
                }
                else {//Desactivated Split
                    sizeSpinner.setEnabled(false);
                    inSize.setEnabled(false);
                    setSplit(0);
                }
            }
        });
        
        //Web setup otion checkbox listener
        cbWeb.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                if (bt.isSelected()) {//Activated Web setup
                    inWebDir.setEnabled(true);
                    btWebConfig.setEnabled(true);
                    Master.setupConfig.setWeb(true);
                    cbSplit.setSelected(false);
                }
                else {//Desactivated Web Setup
                    inWebDir.setEnabled(false);
                    btWebConfig.setEnabled(false);
                    sftpDialog.disable();
                    Master.setupConfig.setWeb(false);
                }
            }
        });
        
        //Set inSize text input filter for numbers only
        inSize.setValidator(new Validator() {
            @Override public boolean isValid(String size)
            {
                try {
                    if (size.equals("")) return true;//If empty
                    Integer.parseInt(size);
                }
                catch(NumberFormatException e) { return false; }//If not number
                return true;//Validated
            }
        });
        
        //Textinput constraints content listener
        inSize.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput textInput)
            {
                if (textInput.getText().equals(""))//Put 0 if no value
                    textInput.setText("0");
                /*else if (textInput.getText().startsWith("0")) {//delete first 0
                    textInput.setText(textInput.getText().substring(1));
                }*/
                setSplit(Integer.parseInt(textInput.getText()) *
                        (((String) sizeSpinner.getSelectedItem()).equals("GB")?1024:1) );//Enable Packaging
            }
        });
        //Update Packaging option size
        sizeSpinner.getSpinnerSelectionListeners().add(new SpinnerSelectionListener.Adapter() {
            @Override public void selectedItemChanged(Spinner spinner, Object obj)
            {
                setSplit(Integer.parseInt(inSize.getText()) *
                        (((String) spinner.getSelectedItem()).equals("GB")?1024:1) );//Enable Packaging
            }
        });
        
        //Web URL directory update
        inWebDir.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput ti)
            {
                if (ti.isEnabled()) {
                    if (ti.getText().length() == 0) Master.setupConfig.setWeb(false);
                    else {
                        Master.setupConfig.setWeb(true);
                        Master.setupConfig.setWebDir(ti.getText());
                    }
                }
            }
        });
        
        //SFTP Configuration Dialog
        btWebConfig.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                sftpDialog.open(BuildFrame.this.getDisplay(), BuildFrame.this.getWindow());
            }
        });
        
        //Compile Task Launch from button btCompile
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
                        Out.print("BUILD", "Finished.");
                        waiter.setActive(false);//Waiter hide
                    }
                };

                dcp.main.log.Out.clearCompileLog();//Clear Saved Log
				String buildType = lbBuild.getSelectedItem().toString();
                if (buildType.equals("IzPack")) { // IzPack compile task
	                String targetPath = CastFactory.pathValidate(inTargetPath.getText(),Master.setupConfig.getAppName(),"jar");
	                TaskIzpackCompile compileTask = new TaskIzpackCompile(targetPath, Master.setupConfig, sftpDialog.getWebConfig());
	                compileTask.setLogger(logger);//Setting log display on logger
	                compileTask.execute(new TaskAdapter<Boolean>(tlCompile));//Compile
				}
                else if (buildType.equals("NuGet")) { // NuGet compile task
                    TaskNugetCompile compileTask = new TaskNugetCompile(inTargetPath.getText(), feedUrl, stepNbr);
                    compileTask.setLogger(logger);//Setting log display on logger
                    compileTask.execute(new TaskAdapter<Boolean>(tlCompile));// Compile
                }
            }
        });
        
        //Launch the install button
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
                
                dcp.main.log.Out.clearCompileLog();//Clear Saved log
                new TaskIzpackRun(CastFactory.pathValidate(inTargetPath.getText(),Master.setupConfig.getAppName(),"jar"), logger).execute(new TaskAdapter<Boolean>(tlRun));
                Out.print("BUILD", "Launch generated package..");
                Out.newLine();
            }
        });
        
        //Debug with trace mode the install button
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
                
                dcp.main.log.Out.clearCompileLog();//Clear Saved log
                new TaskIzpackDebug(CastFactory.pathValidate(inTargetPath.getText(),Master.setupConfig.getAppName(),"jar"),
                        logger).execute(new TaskAdapter<Boolean>(tlDebug));
                Out.print("BUILD", "Launch generated package with TRACE mode enabled..");
                Out.newLine();
            }
        });
        
        //Logger list view automatic scroll to last element
        logger.getListViewItemListeners().add(new ListViewItemListener.Adapter() {
            @Override
            public void itemInserted(ListView arg0, int arg1)
            {
                logger.setSelectedIndex(logger.getListData().getLength()-1);
            }
        });
        logger.getListViewSelectionListeners().add(new ListViewSelectionListener.Adapter() {
            @Override public void selectedRangesChanged(ListView lv, Sequence<Span> s) // save selection to clipboard
            {
                if (!waiter.isActive() && logger.isFocused()) { // if not compiling/running && logger selected
                    @SuppressWarnings("unchecked")
                    Sequence<String> sel = (Sequence<String>) lv.getSelectedItems(); // list of selected strings
                    String selCb = ""; // full selection data string
                    for (int i = 0; i < sel.getLength(); i++) { // concat selection
                        //selCb += sel.get(i) + "\n";
                        selCb = String.format("%s%s%n", selCb, sel.get(i)); 
                    }
                    
                    System.out.println("Copied to Clipboard: "+ selCb);
                    StringSelection selection = new StringSelection(selCb);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
                }
            }
        });
    }
    
    /**
     * Refresh workflow based on build-type (IzPack/NuGet)
     */
    private void displayRefresh() {
        btCompile.setEnabled(true);
        switch (lbBuild.getSelectedItem().toString()) {
        case "IzPack":
            accBuildOpt.setSelectedIndex(0);
            accIzpack.setEnabled(true);
            accNuget.setEnabled(false);
            btLaunch.setEnabled(true);
            btDebug.setEnabled(true);
            break;
        case "NuGet":
            accBuildOpt.setSelectedIndex(1);
            accIzpack.setEnabled(false);
            accNuget.setEnabled(true);
            btLaunch.setEnabled(false);
            btDebug.setEnabled(false);
            break;
        }
    }

    /**
     * Enable/Set Packaging option
     * @param SIZE_IN_MB
     */
    private void setSplit(int SIZE_IN_MB)
    {
        if (SIZE_IN_MB == 0) Master.setupConfig.setSplit(false);
        else {
            Master.setupConfig.setSplit(true);
            Master.setupConfig.setSplitSize(SIZE_IN_MB);
        }
    }
    
    /**
     * Build tab data/display initialize
     * @throws IOException 
     */
    public void init(String AppName, String AppVersion) throws IOException
    {
        String filename = AppName+"-"+AppVersion+".jar";
        filename = filename.replaceAll(" ", "");
        
        //File export set
        switch ((String) lbBuild.getSelectedItem())
        {
        case "IzPack": // IzPack
            fileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_AS);//FileBrowser Mode to File selection
            
            if (new File(IOFactory.targetPath).exists()) {// If 'target' folder exists
                inTargetPath.setText(new File(IOFactory.targetPath, filename).getCanonicalPath());
            }
            else {
                inTargetPath.setText(new File(filename).getCanonicalPath());
            }
            fileBrowserSheet.setSelectedFile(new File(inTargetPath.getText()).getCanonicalFile());
            Out.print("DEBUG", "Export file set to: " + inTargetPath.getText());
            break;
        case "NuGet": // NuGet
            fileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_TO);//FileBrowser Mode to Folder selection
            inFeedSource.setText(feedUrl);// default source for debugging
            
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
            Out.print("DEBUG", "Export folder set to: " + inTargetPath.getText());
            break;
        default: break;
        }
        
    }
    
}
