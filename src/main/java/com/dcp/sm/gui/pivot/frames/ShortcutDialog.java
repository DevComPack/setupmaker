package com.dcp.sm.gui.pivot.frames;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Stack;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonStateListener;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.RadioButton;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.TextInputContentListener;
import org.apache.pivot.wtk.Button.State;

import com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE;
import com.dcp.sm.logic.model.Pack;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.zip.ZipEntry;
import de.schlichtherle.truezip.zip.ZipFile;


public class ShortcutDialog extends Dialog implements Bindable
{
    //-----DATA
    private Pack pack;
    
    //RadioButtons
    @BXML private RadioButton rbFolder;
    @BXML private RadioButton rbFile;
    @BXML private TextInput inFilePath;
    
    public ShortcutDialog() {//Constructor
            
    }
    
    private void search(TFile entry, Stack<File> files) throws IOException {
        if (entry.isDirectory()) {
            files.push(entry);
            for (TFile member : entry.listFiles())
                search(member, files);
        } else if (entry.isFile()) {
            files.push(entry);
        } // else is special file or non-existent
    }
    private void search(ZipFile zipFile, Stack<File> files) throws IOException, ClassNotFoundException {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            files.push(new File(entry.getName()));
        }
    }
    private void search(File entry, Stack<File> files) throws IOException {
        if (entry.isDirectory()) {
            files.push(entry);
            for (File member : entry.listFiles())
                search(member, files);
        } else if (entry.isFile()) {
            files.push(entry);
        } // else is special file or non-existent
    }
    
    @Override
    public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2)
    {
        rbFile.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                inFilePath.setEnabled(bt.isSelected());
                if (!bt.isSelected()) {
                    inFilePath.setText("");
                }
            }
        });
        
        inFilePath.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            private String cache = "";
            private Stack<File> paths = new Stack<File>();
            
            @Override public void textChanged(TextInput TI)
            {
                pack.setShortcutPath(TI.getText());
            }
            //Make inner files suggestions
            @Override public void textInserted(TextInput textInput, int index, int count)
            {
                if (pack.getSize() < 50*1024*1024) {// only if file size less than 50MB
                    String text = textInput.getText().toLowerCase();
                    boolean found = false;
                    String suggestion = "";
                    
                    if (!cache.equals(pack.getName())) {
                        paths.clear();
                        try
                        {
                            if (pack.getFileType() == FILE_TYPE.Folder)
                                search(new File(pack.getPath()), paths);
                            else if (pack.getFileType() == FILE_TYPE.Archive)
                                search(new ZipFile(pack.getPath()), paths);
                            cache = pack.getName();
                        }
                        catch (IOException e) { e.printStackTrace(); }
                        catch (ClassNotFoundException e) { e.printStackTrace(); }
                    }
                    
                    for(File f:paths) {// Suggestions from Group paths
                        String path = f.getPath();
                        if (path.startsWith(pack.getPath()))
                            path = f.getAbsolutePath().substring(pack.getPath().length()).replace('\\', '/');
                        else if (!path.startsWith("/"))
                            path = "/" + path;
                        if (path.toLowerCase().startsWith(text)) {
                            found = true;
                            suggestion = path;
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
            }
        });
    }
    
    public void initData(Pack pack) {
        this.pack = pack;
        inFilePath.setText(pack.getShortcutPath());
        
        if (pack.isShortcut()) {
            if (pack.getShortcutPath().length() == 0) {// no shortcut path
                rbFile.setSelected(false);
                rbFolder.setSelected(true);
            }
            else {// shortcut path available
                rbFolder.setSelected(false);
                rbFile.setSelected(true);
            }
        }
    }

}
