package com.dcp.sm.gui.pivot.frames;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.TextInput;

/**
 * Rename Group Dialog
 * @author ssaideli
 *
 */
public class RGDialog extends Dialog implements Bindable
{
    //Components
    @BXML private TextInput inGroupName;//Text input for new group name
    
    @Override public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2)
    {
    }
    
    public String getText()
    {
        return inGroupName.getText();
    }
    
    public void setText(String name)
    {
        inGroupName.setText(name);
    }
    
}
