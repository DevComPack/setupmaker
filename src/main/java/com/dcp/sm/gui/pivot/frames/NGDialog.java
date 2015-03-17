package com.dcp.sm.gui.pivot.frames;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.DialogCloseListener;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Window;

/**
 * New Group Dialog
 * @author ssaideli
 *
 */
public class NGDialog extends Dialog implements Bindable
{
    private boolean validated = false;
    //Components
    @BXML private TextInput inGroupName;//Text input for new group name
    @BXML private Checkbox cbHierarchy;//Hierarchy Group creation
    @BXML private Label labParentGroup;//Group Name label
    @BXML private PushButton btGroupAdd;//Dialog Group Name confirm
    //Actions
    private Action AGroupAdd;//Confirm the group name add from dialog
    
    public NGDialog()
    {
        AGroupAdd = new Action() {
            @Override public void perform(Component source)
            {
                if (inGroupName.getText().length() > 0)
                    validated = true;
                NGDialog.this.close();
            } };
    }
    
    @Override public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2)
    {
        //Actions binding
        btGroupAdd.setAction(AGroupAdd);
        btGroupAdd.setButtonDataKey("ENTER");
    }
    
    @Override public void open(Display display, Window owner, DialogCloseListener dialogCloseListenerArgument)
    {
        inGroupName.setText("");
        validated=false;
        super.open(display, owner, dialogCloseListenerArgument);
    }
    
    public void setHierarchy(boolean ENABLE, String PATH)//Set if hierarchy is possible (checkbox)
    {
        cbHierarchy.setSelected(ENABLE);
        cbHierarchy.setVisible(ENABLE);
        cbHierarchy.setEnabled(ENABLE);
        labParentGroup.setVisible(ENABLE);
        labParentGroup.setText(PATH);
    }
    public String getHierarchy()//If hierarchy checkbox selected (return parent name)
    {
        if (cbHierarchy.isEnabled() && cbHierarchy.isSelected())
            return labParentGroup.getText();
        else
            return null;
    }
    
    public String getText()
    {
        return inGroupName.getText();
    }
    
    public boolean isValidated() {
        return validated;
    }
    
}
