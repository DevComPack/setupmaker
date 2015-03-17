package com.dcp.sm.gui.pivot.frames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.ButtonStateListener;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.TextInputContentListener;
import org.apache.pivot.wtk.Button.State;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.logic.model.config.build.WebConfig;
import com.dcp.sm.main.log.Out;

/**
 * SFTP Dialog
 * @author ssaideli
 *
 */
public class SftpDialog extends Dialog implements Bindable
{
    @BXML private Checkbox cbEnable;
    @BXML private Border formArea;
    
    @BXML private TextInput inHost;
    @BXML private TextInput inUser;
    @BXML private TextInput inPass;
    @BXML private TextInput inRemDir;
    @BXML private TextInput inPath;
    
    @BXML private PushButton btSave, btLoad;
    
    private WebConfig webConfig;
    
    public SftpDialog()
    {
        webConfig = new WebConfig();
    }
    
    public WebConfig getWebConfig() { return webConfig; }
    
    public void disable() { if (cbEnable.isSelected()) cbEnable.setSelected(false); }
    
    @Override public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2)
    {
        cbEnable.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                formArea.setEnabled(bt.isSelected());
                webConfig.setEnabled(bt.isSelected());
            }
        });
        
        inHost.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput ti)
            {
                webConfig.setHost(ti.getText());
            }
        });
        inUser.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput ti)
            {
                webConfig.setUser(ti.getText());
            }
        });
        inPass.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput ti)
            {
                webConfig.setPass(ti.getText());
            }
        });
        inRemDir.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput ti)
            {
                webConfig.setRemoteDir(ti.getText());
            }
        });
        inPath.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput ti)
            {
                webConfig.setPath(ti.getText());
            }
        });
        
        //Save buttons
        btSave.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                save(webConfig);
            }
        });
        btLoad.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                WebConfig tmp = load();
                if (tmp != null) {
                    webConfig = tmp;
                    webConfig.setEnabled(cbEnable.isSelected());
                    if (webConfig.getHost() != null) inHost.setText(webConfig.getHost());
                    if (webConfig.getUser() != null) inUser.setText(webConfig.getUser());
                    if (webConfig.getPass() != null) inPass.setText(webConfig.getPass());
                    if (webConfig.getRemoteDir() != null) inRemDir.setText(webConfig.getRemoteDir());
                    if (webConfig.getPath() != null) inPath.setText(webConfig.getPath());
                }
            }
        });
    }
    
    /**
     * Save SFTP Data
     * @param webConfig
     */
    private void save(WebConfig webConfig) {
        try
        {
            File f = new File("web.dcp");
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(out);
            
            os.writeObject(webConfig);
            
            os.close();
            out.close();
            Out.print(LOG_LEVEL.INFO, "SFTP Data saved");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Load SFTP Data
     */
    private WebConfig load() {
        try
        {
            File f = new File("web.dcp");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream is = new ObjectInputStream(in);
                
                WebConfig webConfig = (WebConfig) is.readObject();
                
                is.close();
                in.close();
                Out.print(LOG_LEVEL.INFO, "SFTP Data loaded");
                
                return webConfig;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
