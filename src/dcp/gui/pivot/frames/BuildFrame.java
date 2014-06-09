package dcp.gui.pivot.frames;

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
import org.apache.pivot.wtk.ActivityIndicator;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.ButtonStateListener;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.FileBrowserSheetListener;
import org.apache.pivot.wtk.FillPane;
import org.apache.pivot.wtk.ImageView;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.ListButton;
import org.apache.pivot.wtk.ListView;
import org.apache.pivot.wtk.ListViewItemListener;
import org.apache.pivot.wtk.ListViewSelectionListener;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Prompt;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.Spinner;
import org.apache.pivot.wtk.SpinnerSelectionListener;
import org.apache.pivot.wtk.TaskAdapter;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Button.State;
import org.apache.pivot.wtk.TextInputContentListener;
import org.apache.pivot.wtk.validation.Validator;

import dcp.config.compile.IzpackAntCompiler;
import dcp.config.io.IOFactory;
import dcp.gui.pivot.Master;
import dcp.gui.pivot.actions.BrowseAction;
import dcp.gui.pivot.tasks.TaskCompile;
import dcp.gui.pivot.tasks.TaskDebug;
import dcp.gui.pivot.tasks.TaskRun;
import dcp.logic.factory.CastFactory;
import dcp.main.log.Out;


public class BuildFrame extends FillPane implements Bindable
{
    //Singleton reference
    private static BuildFrame singleton;
    public static BuildFrame getSingleton() { return singleton; }
    //------DATA
    //Constants
    private final static String IZPACK_HOME = "compiler/izpack";//System.getenv("IZPACK_HOME");//Izpack home directory path
    //private final static String IZPACK_FILE = IOFactory.xmlIzpackInstall;//Izpack xml file to compile
    //Flags
    private static boolean modified = false;//True if tab processed data
    public static void setModified(boolean VALUE) { modified = VALUE; }
    public static boolean isModified() { return modified; }
    //Browse Area
    @BXML private ListButton lbBuild;// Build type list button
    @BXML private FileBrowserSheet fileBrowserSheet;//File Browser
    @BXML private PushButton btBrowse;//Browse button
    @BXML private TextInput outPath;//Path Text Input
    //Split Area
    @BXML private Checkbox cbSplit;//Split option enable/disable
    @BXML private TextInput inSize;//Split size
    @BXML private Spinner sizeSpinner;//Split size unit [MB|GB|GB]
    //Web Area
    @BXML private Checkbox cbWeb;//Web Setup option enabled/disable
    @BXML private TextInput inWebDir;//Web HTTP server URL
    @BXML private SftpDialog sftpDialog;//Web SFTP Configuration dialog
    @BXML private PushButton btWebConfig;//Web SFTP Configuration dialog button
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
    
    //Compiler
    private IzpackAntCompiler compiler;//IzPack Compiler Class
    
    public BuildFrame() {
        if (singleton == null) singleton = this;
        //IzPack Compiler instantiate
        compiler = new IzpackAntCompiler();
    }
    
    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
        //Set default directory selection in File Browser (target/package.jar)
        fileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_AS);//FileBrowser Mode to File selection
        try {
            fileBrowserSheet.setRootDirectory(new File(".").getCanonicalFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //Data Binding
        lbBuild.setSelectedItem("IzPack");// 'IzPack' build type by default
        logger.setListData(dcp.main.log.Out.getCompileLog());//Bind compile log tags to List view logger
        
        //Action Binding
        btBrowse.setAction(new BrowseAction(fileBrowserSheet));
        
        //Target file chosen from File Chooser event
        fileBrowserSheet.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                try {//Set the Input Text value to the selected file path
                    outPath.setText(fileBrowserSheet.getSelectedFile().getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                compiler.setTarget(outPath.getText());//Change compiler's target file
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
            @Override public void buttonPressed(Button button) {
                successIcon.setVisible(false);
                failIcon.setVisible(false);
                successText.setVisible(false);
                failText.setVisible(false);
                waiter.setActive(true);//Waiter show
                btCompile.setEnabled(false);
                btLaunch.setEnabled(false);
                btDebug.setEnabled(false);
                
                TaskListener<Boolean> tlCompile = new TaskListener<Boolean>() {//Finished compile
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
                        btCompile.setEnabled(true);
                        btLaunch.setEnabled(true);
                        if (IZPACK_HOME != null) btDebug.setEnabled(true);
                        waiter.setActive(false);//Waiter hide
                    }   
                };

                String target_file = outPath.getText();
                System.out.println("*"+target_file);
                System.out.println("*"+compiler.getTarget());
                compiler.setTarget(CastFactory.pathValidate(target_file,Master.setupConfig.getAppName(),"jar"));//Set the target package file
                System.out.println("*"+compiler.getTarget());
                dcp.main.log.Out.clearCompileLog();//Clear Saved Log
                //Compile Task launch
                TaskCompile compileTask = new TaskCompile(compiler, Master.setupConfig, sftpDialog.getWebConfig());
                compileTask.setLogger(logger);//Setting log display on logger
                compileTask.execute(new TaskAdapter<Boolean>(tlCompile));//Compile
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
                    @Override public void executeFailed(Task<Boolean> arg0) {
                        taskExecuted(arg0);
                    }
                    @Override public void taskExecuted(Task<Boolean> arg0) {
                        btCompile.setEnabled(true);
                        btLaunch.setEnabled(true);
                        btDebug.setEnabled(true);
                        waiter.setActive(false);//Waiter stop
                    } };
                
                compiler.setTarget(CastFactory.pathValidate(outPath.getText(),Master.setupConfig.getAppName(),"jar"));//Set the target package file
                dcp.main.log.Out.clearCompileLog();//Clear Saved log
                new TaskRun(compiler, logger).execute(new TaskAdapter<Boolean>(tlRun));
                Out.print("PIVOT_BUILD", "Launch generated package..");
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
                    @Override public void executeFailed(Task<Boolean> arg0) {
                        taskExecuted(arg0);
                    }
                    @Override public void taskExecuted(Task<Boolean> arg0) {
                        btCompile.setEnabled(true);
                        btLaunch.setEnabled(true);
                        btDebug.setEnabled(true);
                        waiter.setActive(false);//Waiter stop
                    } };
                
                compiler.setTarget(CastFactory.pathValidate(outPath.getText(),Master.setupConfig.getAppName(),"jar"));//Set the target package file
                dcp.main.log.Out.clearCompileLog();//Clear Saved log
                new TaskDebug(compiler, logger).execute(new TaskAdapter<Boolean>(tlDebug));
                Out.print("PIVOT_BUILD", "Launch generated package with TRACE mode enabled..");
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
            @Override public void selectedRangesChanged(ListView lv, Sequence<Span> s)// save selection to clipboard
            {
                if (!waiter.isActive() && logger.isFocused()) {// if not compiling/running && logger selected
                    System.out.println("Copied to Clipboard: "+(String) lv.getSelectedItem());
                    StringSelection selection = new StringSelection((String) lv.getSelectedItem());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
                }
            }
        });
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
     * Build tab export path initialize
     */
    public void init(String AppName, String AppVersion)
    {
        String filename = AppName+"-"+AppVersion+".jar";
        filename = filename.replaceAll(" ", "");
        
        //File export set
        if (new File(IOFactory.targetPath).exists()) {//If 'target' folder exists
            outPath.setText(new File(IOFactory.targetPath+"/"+filename).getAbsolutePath());
        }
        else {
            outPath.setText(new File(filename).getAbsolutePath());
        }
        fileBrowserSheet.setSelectedFile(new File(outPath.getText()).getAbsoluteFile());
        Out.print("DEBUG", "Export file set to: " + outPath.getText());
        
        //Data load from saved project
        if (Master.setupConfig.isSplit()) {
            inSize.setText(String.valueOf(Master.setupConfig.getSplitSize()/(1024*1024)));
            sizeSpinner.setSelectedIndex(0);
            cbSplit.setSelected(true);
        }
        else if (Master.setupConfig.isWeb()) {
            inWebDir.setText(Master.setupConfig.getWebDir());
            cbWeb.setSelected(true);
        }
    }
    
}
