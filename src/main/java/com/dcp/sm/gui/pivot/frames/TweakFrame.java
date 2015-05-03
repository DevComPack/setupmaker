package com.dcp.sm.gui.pivot.frames;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.ButtonStateListener;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseButtonListener;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.FileBrowserSheetListener;
import org.apache.pivot.wtk.FillPane;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.TextInputContentListener;
import org.apache.pivot.wtk.Button.State;

import com.dcp.sm.config.io.IOFactory;
import com.dcp.sm.gui.pivot.Master;
import com.dcp.sm.gui.pivot.actions.BrowseAction;
import com.dcp.sm.gui.pivot.validators.IntValidator;
import com.dcp.sm.gui.pivot.validators.Iso3Validator;
import com.dcp.sm.gui.pivot.validators.NameValidator;
import com.dcp.sm.gui.pivot.validators.PathValidator;
import com.dcp.sm.gui.pivot.validators.VersionValidator;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.logic.model.config.SetupConfig;
import com.dcp.sm.main.log.Out;


public class TweakFrame extends FillPane implements Bindable
{
    // Singleton reference
    private static TweakFrame singleton;
    public static TweakFrame getSingleton() { assert (singleton != null); return singleton; }
    // Configuration
    private SetupConfig setupConfig = Master.facade.setupConfig;
    // Master class
    private Master master;
    public void setMaster(Master master) { this.master = master; }
    // Edit Flag
    private boolean modified = false;// True if tab changed data
    public void setModified(boolean VALUE) { assert master != null; modified = VALUE; master.setUndo(true); }
    public boolean isModified() { return modified; }
    
    // ---Components
    // Application
    @BXML private TextInput inAppName;
    @BXML private TextInput inAppVersion;
    @BXML private PushButton btIncr;
    @BXML private TextInput inAppWidth;
    @BXML private TextInput inAppHeight;
    @BXML private Checkbox cbResizable;// If Window will be resizable
    // Author
    @BXML private TextInput inAuthorName;
    @BXML private TextInput inAuthorEmail;
    @BXML private TextInput inAppURL;
    // Install Path
    @BXML private FileBrowserSheet fileBrowserSheet;// File Browser
    @BXML private Checkbox cbForce;// Force a target path for the setup
    @BXML private PushButton btBrowse;// Browse button
    @BXML private TextInput installPath;// Default Install Path Text Input
    // Resources
    @BXML private TextInput inReadme;// Readme
    @BXML private TextInput inLicense;// License
    @BXML private FileBrowserSheet fBSReadme;// Readme File Browser
    @BXML private FileBrowserSheet fBSLicense;// License File Browser
    @BXML private PushButton btBrowseReadme;// Browse button
    @BXML private PushButton btBrowseLicense;// Browse button
    @BXML private TextInput inLogo;
    @BXML private FileBrowserSheet fBS1;// Logo File Browser
    @BXML private PushButton btBrowseLogo;// Browse button
    @BXML private TextInput inSideLogo;
    @BXML private FileBrowserSheet fBS2;// Side Logo File Browser
    @BXML private PushButton btBrowseSideLogo;// Browse button
    // Shortcuts
    @BXML private Checkbox cbShortcuts;// Shortcuts enable/disable option
    @BXML private Checkbox cbFolderSh;// Shortcut to global install path
    @BXML private Checkbox cbShToStartMenu;// Add shortcuts to start menu
    @BXML private Checkbox cbShToDesktop;// Add shortcuts to desktop
    // Advanced
    @BXML private Checkbox cbRegistryCheck;// Registry version check option
    @BXML private Checkbox cbScriptGen;// Script generation option
    @BXML private Checkbox cbLpEnglish;// English langpack
    @BXML private Checkbox cbLpFrench;// French langpack
    @BXML private Checkbox cbLpGerman;// German langpack
    @BXML private Checkbox cbLpSpanish;// Spanish langpack
    
    @BXML private BoxPane boxCustomLP;// Custom langpack elements
    @BXML private Checkbox cbLpCustom;// Custom langpack checkbox
    @BXML private TextInput inCustomISO3;// ISO3 code input
    @BXML private TextInput inCustomLangpack;// xml langpack file input
    @BXML private FileBrowserSheet fBSLangpack;// Custom Langpack File Browser
    @BXML private PushButton btBrowseCustomLangpack;// Browse button
    
    
    public TweakFrame()
    {
        assert (singleton == null);
        singleton = this;
    }

    @Override
    public void initialize(Map<String, Object> args, URL url, Resources res)
    {
        // init(setupConfig);
        
        // Validators
        inAppName.setValidator(new NameValidator(inAppName, true, false));// Name Validator
        inAppVersion.setValidator(new VersionValidator(inAppVersion, true));// Version Validator
        inAppWidth.setValidator(new IntValidator());// Int Validators
        inAppHeight.setValidator(new IntValidator());
        // inAppURL.setValidator(new URLValidator());// URL Validator
        inReadme.setValidator(new PathValidator(inReadme, false));// Path Validators
        inLicense.setValidator(new PathValidator(inLicense, false));
        inLogo.setValidator(new PathValidator(inLogo, false));
        inSideLogo.setValidator(new PathValidator(inSideLogo, false));
        inCustomLangpack.setValidator(new PathValidator(inCustomLangpack, false));
        inCustomISO3.setValidator(new Iso3Validator());// ISO3 Language Code Validator
        
        // Browsers Set
        fileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_TO);
        fBSReadme.setMode(FileBrowserSheet.Mode.OPEN);
        fBSLicense.setMode(FileBrowserSheet.Mode.OPEN);
        fBSLangpack.setMode(FileBrowserSheet.Mode.OPEN);
        fBS1.setMode(FileBrowserSheet.Mode.OPEN);
        fBS2.setMode(FileBrowserSheet.Mode.OPEN);
        try// Set working directory as root directory for file browser
        {
            fBSReadme.setRootDirectory(new File(".").getCanonicalFile());
            fBSLicense.setRootDirectory(new File(".").getCanonicalFile());
            fBSLangpack.setRootDirectory(new File(".").getCanonicalFile());
            fBS1.setRootDirectory(new File(".").getCanonicalFile());
            fBS2.setRootDirectory(new File(".").getCanonicalFile());
        }
        catch (IOException e1) { e1.printStackTrace(); }
        
        // Images filter
        fBSReadme.setDisabledFileFilter(IOFactory.docFilter);
        fBSLicense.setDisabledFileFilter(IOFactory.docFilter);
        fBSLangpack.setDisabledFileFilter(IOFactory.docFilter);
        fBS1.setDisabledFileFilter(IOFactory.imgFilter);
        fBS2.setDisabledFileFilter(IOFactory.imgFilter);
        
        // Action binding
        btBrowse.setAction(new BrowseAction(fileBrowserSheet));
        btBrowseReadme.setAction(new BrowseAction(fBSReadme, inReadme.getText()));
        btBrowseLicense.setAction(new BrowseAction(fBSLicense, inLicense.getText()));
        btBrowseLogo.setAction(new BrowseAction(fBS1,  inLogo.getText()));
        btBrowseSideLogo.setAction(new BrowseAction(fBS2, inSideLogo.getText()));
        btBrowseCustomLangpack.setAction(new BrowseAction(fBSLangpack, "."));
        
        // File/Folder select in File Browser Event
        fileBrowserSheet.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                try {
                    String s = fileBrowserSheet.getSelectedFile().getCanonicalPath();
                    installPath.setText(s + "\\" + setupConfig.getAppName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        fBSReadme.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                // If path changed
                if (!TweakFrame.this.inReadme.getText().equals(fileBrowserSheet.getSelectedFile().getAbsolutePath())) {
                    Out.print(LOG_LEVEL.DEBUG, "Changed readme file to: "+fileBrowserSheet.getSelectedFile().getAbsolutePath());
                    TweakFrame.this.inReadme.setText(fileBrowserSheet.getSelectedFile().getAbsolutePath());
                    // Setting same root directory for License file browser
                    fBSLicense.setRootDirectory(new File(TweakFrame.this.inReadme.getText()).getParentFile());
                }
            }
        });
        fBSLicense.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                // If path changed
                if (!TweakFrame.this.inLicense.getText().equals(fileBrowserSheet.getSelectedFile().getAbsolutePath())) {
                    Out.print(LOG_LEVEL.DEBUG, "Changed license file to: "+fileBrowserSheet.getSelectedFile().getAbsolutePath());
                    TweakFrame.this.inLicense.setText(fileBrowserSheet.getSelectedFile().getAbsolutePath());
                    // Setting same root directory for Logo file browser
                    fBS1.setRootDirectory(new File(TweakFrame.this.inLicense.getText()).getParentFile());
                }
            }
        });
        fBS1.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                // If path changed
                if (!TweakFrame.this.inLogo.getText().equals(fileBrowserSheet.getSelectedFile().getAbsolutePath())) {
                    Out.print(LOG_LEVEL.DEBUG, "Changed logo to: "+fileBrowserSheet.getSelectedFile().getAbsolutePath());
                    TweakFrame.this.inLogo.setText(fileBrowserSheet.getSelectedFile().getAbsolutePath());
                    // Setting same root directory for Side logo file browser
                    fBS2.setRootDirectory(new File(TweakFrame.this.inLogo.getText()).getParentFile());
                }
            }
        });
        fBS2.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                // If path changed
                if (!TweakFrame.this.inSideLogo.getText().equals(fileBrowserSheet.getSelectedFile().getAbsolutePath())) {
                    Out.print(LOG_LEVEL.DEBUG, "Changed side logo to: "+fileBrowserSheet.getSelectedFile().getAbsolutePath());
                    TweakFrame.this.inSideLogo.setText(fileBrowserSheet.getSelectedFile().getAbsolutePath());
                }
            }
        });
        fBSLangpack.getFileBrowserSheetListeners().add(new FileBrowserSheetListener.Adapter() {
            @Override public void selectedFilesChanged(FileBrowserSheet fileBrowserSheet, Sequence<File> previousSelectedFiles)
            {
                String filePath = fileBrowserSheet.getSelectedFile().getAbsolutePath(),
                        fileName = fileBrowserSheet.getSelectedFile().getName();
                // If path changed
                if (!TweakFrame.this.inCustomLangpack.getText().equals(filePath)) {
                    Out.print(LOG_LEVEL.DEBUG, "Changed langpack to: "+filePath);
                    TweakFrame.this.inCustomLangpack.setText(filePath);
                    // get ISO3 code if filename equals 3 chars (without file extension)
                    if (fileName.substring(0, fileName.lastIndexOf('.')).length() == 3)
                        inCustomISO3.setText(fileName.substring(0, fileName.lastIndexOf('.')));
                }
            }
        });
        
        // Application
        inAppName.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                if (!TI.getText().contains("\\") && !TI.getText().contains("/")) {
                    String s = installPath.getText();
                    if (s.length() > 0 && s.contains(setupConfig.getAppName())) {
                        s = s.substring(0, s.lastIndexOf(setupConfig.getAppName())) + TI.getText();
                        installPath.setText(s);
                    }
                    setupConfig.setAppName(TI.getText());
                    setModified(true);// Modified flag*
                }
            }
        });
        inAppVersion.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setAppVersion(TI.getText());
                setModified(true);// Modified flag*
            }
        });
        inAppVersion.getComponentMouseButtonListeners().add(new ComponentMouseButtonListener.Adapter() {
            @Override public boolean mouseClick(Component component, org.apache.pivot.wtk.Mouse.Button bt, int x, int y, int count)
            {
                if (bt == org.apache.pivot.wtk.Mouse.Button.LEFT){
                    int insertPoint = inAppVersion.getInsertionPoint(x);
                    String chars = inAppVersion.getText();
                    int start=0, end=0;
                    boolean finish = false;
                    for(int i=0; i < chars.length(); i++) {
                        if (!finish && i == insertPoint) {
                            finish = true;
                        }
                        if (chars.charAt(i) == '.') {
                            if (!finish)
                                start = i+1;
                            else {
                                end = i;
                                break;
                            }
                        }
                    }
                    if (end == 0)
                        end = chars.length();
                    inAppVersion.setSelection(start, end-start);
                }
                return super.mouseClick(component, bt, x, y, count);
            }
        });
        btIncr.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                if (inAppVersion.getText().matches("[.0-9]+")) {
                    inAppVersion.requestFocus();
                    int number;
                    // Increment selected value
                    if (inAppVersion.getSelectionLength() > 0 && !inAppVersion.getSelectedText().contains(".")) {
                        number = Integer.parseInt(inAppVersion.getSelectedText());
                        int selStart = inAppVersion.getSelectionStart(), selLength = inAppVersion.getSelectionLength();
                        inAppVersion.setText(inAppVersion.getText().substring(0, selStart)
                                + String.valueOf(++number)
                                + inAppVersion.getText().substring(selStart+selLength,
                                        inAppVersion.getCharacterCount()) );
                        inAppVersion.setSelection(selStart, selLength);
                    }
                    // Increment last version value
                    else {
                        String[] version = inAppVersion.getText().split("[.]");
                        if (version.length > 1) number = Integer.parseInt(version[version.length-1]);
                        else number = Integer.parseInt(inAppVersion.getText());
                        inAppVersion.setText(inAppVersion.getText().substring(0, inAppVersion.getText().lastIndexOf(".")+1) +
                                String.valueOf(++number) );
                        if (version.length > 1) inAppVersion.setSelection(inAppVersion.getText().lastIndexOf(".")+1, String.valueOf(number).length());
                        else inAppVersion.setSelection(0, String.valueOf(number).length());
                    }
                }
            }
        });
        
        inAppWidth.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                try { Integer.parseInt(TI.getText()); }
                catch(NumberFormatException e) { return; }
                setupConfig.setAppWidth(Integer.parseInt(TI.getText()));
                setModified(true);// Modified flag*
            }
        });
        inAppHeight.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                try { Integer.parseInt(TI.getText()); }
                catch(NumberFormatException e) { return; }
                setupConfig.setAppHeight(Integer.parseInt(TI.getText()));
                setModified(true);// Modified flag*
            }
        });
        cbResizable.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                setupConfig.setResizable(bt.isSelected());
                setModified(true);// Modified flag*
            }
        });
        
        // Author
        inAuthorName.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setAuthorName(TI.getText());
                setModified(true);// Modified flag*
            }
        });
        inAuthorEmail.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setAuthorEmail(TI.getText());
                setModified(true);// Modified flag*
            }
        });
        inAppURL.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setAppURL(TI.getText());
                setModified(true);// Modified flag*
            }
        });
        
        // Install Path
        installPath.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setInstallPath(TI.getText());// Set the default install path
                File f = new File(TI.getText()).getAbsoluteFile();
                if (f.exists())// Map the file browser with the path entered
                    fileBrowserSheet.setRootDirectory(f.isDirectory()?f:f.getParentFile());
                setModified(true);// Modified flag*
                if (TI.getText().length() > 0) {
                    if (!cbForce.isEnabled()) {
                        cbForce.setEnabled(true);
                        cbForce.setSelected(true);
                    }
                }
                else if (TI.getText().length() == 0) {
                    cbForce.setEnabled(false);
                    cbForce.setSelected(false);
                }
            }
        });
        cbForce.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                setupConfig.setForcePath(bt.isSelected());
                setModified(true);// Modified flag*
            }
        });
        
        // Resources
        inReadme.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setReadmePath(TI.getText());
                File f = new File(TI.getText()).getAbsoluteFile();
                if (f.exists())// Map the file browser with the path entered
                    fBSReadme.setRootDirectory(f.isDirectory()?f:f.getParentFile());
                setModified(true);// Modified flag*
            }
        });
        inLicense.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setLicensePath(TI.getText());
                File f = new File(TI.getText()).getAbsoluteFile();
                if (f.exists())// Map the file browser with the path entered
                    fBSLicense.setRootDirectory(f.isDirectory()?f:f.getParentFile());
                setModified(true);// Modified flag*
            }
        });
        inLogo.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setLogoPath(TI.getText());
                File f = new File(TI.getText()).getAbsoluteFile();
                if (f.exists())// Map the file browser with the path entered
                    fBS1.setRootDirectory(f.isDirectory()?f:f.getParentFile());
                setModified(true);// Modified flag*
            }
        });
        inSideLogo.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setSideLogoPath(TI.getText());
                File f = new File(TI.getText()).getAbsoluteFile();
                if (f.exists())// Map the file browser with the path entered
                    fBS2.setRootDirectory(f.isDirectory()?f:f.getParentFile());
                setModified(true);// Modified flag*
            }
        });
        
        // Shortcuts
        cbShortcuts.getButtonPressListeners().add(new ButtonPressListener() {// Shortcuts enable for packs
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setShortcuts(bt.isSelected());
                setModified(true);// Modified flag*
            }
        });
        cbFolderSh.getButtonPressListeners().add(new ButtonPressListener() {// Shortcut enable for folder
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setFolderShortcut(bt.isSelected());
                setModified(true);// Modified flag*
            }
        });
        cbShToStartMenu.getButtonPressListeners().add(new ButtonPressListener() {// Shortcuts to start menu
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setShToStartMenu(bt.isSelected());
                setModified(true);// Modified flag*
            }
        });
        cbShToDesktop.getButtonPressListeners().add(new ButtonPressListener() {// Shortcuts to desktop
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setShToDesktop(bt.isSelected());
                setModified(true);// Modified flag*
            }
        });
        
        // Advanced
        cbRegistryCheck.getButtonPressListeners().add(new ButtonPressListener() {// Registry Check
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setRegistryCheck(bt.isSelected());
                setModified(true);// Modified flag*
            }
        });
        cbScriptGen.getButtonPressListeners().add(new ButtonPressListener() {// Script Generation
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setScriptGen(bt.isSelected());
                setModified(true);// Modified flag*
            }
        });
        cbLpEnglish.getButtonPressListeners().add(new ButtonPressListener() {// English Langpack
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setEnglish(bt.isSelected());
                if (!cbLpEnglish.isSelected() && !cbLpFrench.isSelected() && !cbLpCustom.isSelected() &&
                        !cbLpGerman.isSelected() && !cbLpSpanish.isSelected())
                    cbLpEnglish.setSelected(true);// Select English when none is selected
                setModified(true);// Modified flag*
            }
        });
        cbLpFrench.getButtonPressListeners().add(new ButtonPressListener() {// French Langpack
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setFrench(bt.isSelected());
                if (!cbLpEnglish.isSelected() && !cbLpFrench.isSelected() && !cbLpCustom.isSelected() &&
                        !cbLpGerman.isSelected() && !cbLpSpanish.isSelected())
                    cbLpEnglish.setSelected(true);// Select English when none is selected
                setModified(true);// Modified flag*
            }
        });
        cbLpGerman.getButtonPressListeners().add(new ButtonPressListener() {// German Langpack
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setGerman(bt.isSelected());
                if (!cbLpEnglish.isSelected() && !cbLpFrench.isSelected() && !cbLpCustom.isSelected() &&
                        !cbLpGerman.isSelected() && !cbLpSpanish.isSelected())
                    cbLpEnglish.setSelected(true);// Select English when none is selected
                setModified(true);// Modified flag*
            }
        });
        cbLpSpanish.getButtonPressListeners().add(new ButtonPressListener() {// Spanish Langpack
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setSpanish(bt.isSelected());
                if (!cbLpEnglish.isSelected() && !cbLpFrench.isSelected() && !cbLpCustom.isSelected() &&
                        !cbLpGerman.isSelected() && !cbLpSpanish.isSelected())
                    cbLpEnglish.setSelected(true);// Select English when none is selected
                setModified(true);// Modified flag*
            }
        });
        cbLpCustom.getButtonPressListeners().add(new ButtonPressListener() {// Custom Langpack
            @Override public void buttonPressed(Button bt)
            {
                setupConfig.setCustomLang(bt.isSelected());
                inCustomISO3.setEnabled(bt.isSelected());
                inCustomLangpack.setEnabled(bt.isSelected());
                boxCustomLP.setEnabled(bt.isSelected());
                if (!cbLpEnglish.isSelected() && !cbLpFrench.isSelected() && !cbLpCustom.isSelected() &&
                        !cbLpGerman.isSelected() && !cbLpSpanish.isSelected())
                    cbLpEnglish.setSelected(true);// Select English when none is selected
                setModified(true);// Modified flag*
            }
        });
        inCustomISO3.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setCustomLangISO(TI.getText());
                setModified(true);// Modified flag*
            }
            // Make iso3 codes suggestions
            @Override public void textInserted(TextInput textInput, int index, int count)
            {
                String text = textInput.getText().toLowerCase();
                boolean found = false;
                String suggestion = "";
                
                for(String iso:IOFactory.iso3Codes) {// Suggestions from Group paths
                    if (iso.toLowerCase().startsWith(text)) {
                        found = true;
                        suggestion = iso;
                        break;
                    }
                }
                
                if (found) {
                    int selectionStart = text.length();
                    int selectionLength = suggestion.length() - selectionStart;
                    
                    textInput.insertText(suggestion.subSequence(text.length(), suggestion.length()), selectionStart);
                    textInput.setSelection(selectionStart, selectionLength);
                }
            }
        });
        inCustomLangpack.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                setupConfig.setCustomLangPath(TI.getText());
                File f = new File(TI.getText()).getAbsoluteFile();
                if (f.exists())// Map the file browser with the path entered
                    fBSLangpack.setRootDirectory(f.isDirectory()?f:f.getParentFile());
                setModified(true);// Modified flag*
            }
        });
    }

    /**
     * Bind components with data
     * @param setupConfig
     */
    public void init(SetupConfig setupConfig)
    {
        // Application
        inAppName.setText(setupConfig.getAppName());
        inAppVersion.setText(setupConfig.getAppVersion());
        inAppWidth.setText(String.valueOf(setupConfig.getAppWidth()));
        inAppHeight.setText(String.valueOf(setupConfig.getAppHeight()));
        cbResizable.setSelected(setupConfig.isResizable());
        // Author
        inAuthorName.setText(setupConfig.getAuthorName());
        inAuthorEmail.setText(setupConfig.getAuthorEmail());
        inAppURL.setText(setupConfig.getAppURL());
        // Install Path
        installPath.setText(setupConfig.getInstallPath());
        cbForce.setSelected(setupConfig.isForcePath());
        // Resources
        inReadme.setText(setupConfig.getReadmePath());
        inLicense.setText(setupConfig.getLicensePath());
        inLogo.setText(setupConfig.getLogoPath());
        inSideLogo.setText(setupConfig.getSideLogoPath());
        // Shortcuts
        cbShortcuts.setSelected(setupConfig.isShortcuts());
        cbFolderSh.setSelected(setupConfig.isFolderShortcut());
        cbShToStartMenu.setSelected(setupConfig.isShToStartMenu());
        cbShToDesktop.setSelected(setupConfig.isShToDesktop());
        // Advanced
        cbRegistryCheck.setSelected(setupConfig.isRegistryCheck());
        cbScriptGen.setSelected(setupConfig.isScriptGen());
        // Langpacks
        cbLpEnglish.setSelected(setupConfig.isEnglish());
        cbLpFrench.setSelected(setupConfig.isFrench());
        cbLpGerman.setSelected(setupConfig.isGerman());
        cbLpSpanish.setSelected(setupConfig.isSpanish());
        cbLpCustom.setSelected(setupConfig.isCustomLang());
        inCustomISO3.setText(setupConfig.getCustomLangISO());
        inCustomLangpack.setText(setupConfig.getCustomLangPath());
        
        this.setupConfig = setupConfig;
        setModified(false);
    }
    
    /**
     * Enable shortcuts for packs if a pack is configured for a shortcut
     */
    public void update()
    {
        Out.print(LOG_LEVEL.DEBUG, "Tweak tab update");
        /*boolean found = false;
        for(Pack p:PackFactory.getPacks())
            if (p.isShortcut() && p.getInstallType() != INSTALL_TYPE.EXECUTE) {
                found = true;
                break;
            }
        
        if (cbShortcuts.isEnabled() != found) {
            cbShortcuts.setEnabled(found);
            cbShortcuts.setSelected(found);
            if (found == true) Out.print(LOG_LEVEL.DEBUG, "Shortcuts option enabled for Packs");
        }*/
        
        setModified(true);
    }

}
