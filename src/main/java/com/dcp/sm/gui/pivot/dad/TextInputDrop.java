package com.dcp.sm.gui.pivot.dad;

import java.io.IOException;

import org.apache.pivot.wtk.DropTarget;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.DropAction;
import org.apache.pivot.wtk.Manifest;
import org.apache.pivot.wtk.TextInput;


public class TextInputDrop implements DropTarget
{
    @Override
    public DropAction userDropActionChange(Component component, Manifest dragContent,
            int supportedDropActions, int x, int y, DropAction dropActions)
    {
        return (dragContent.containsText() ? DropAction.COPY : null);
    }
    
    @Override
    public DropAction drop(Component component, Manifest dragContent,
            int supportedDropActions, int x, int y, DropAction dropActions)
    {
        DropAction dropAction = null;

        if (dragContent.containsText()) {
            try {
                ((TextInput) component).setText(dragContent.getText());
                dropAction = DropAction.COPY;
            } catch(IOException exception) {
                System.err.println(exception);
            }
        }

        dragExit(component);

        return dropAction;
    }
    
    @Override
    public DropAction dragMove(Component component, Manifest dragContent,
            int supportedDropActions, int x, int y, DropAction dropActions)
    {
        return (dragContent.containsText() ? DropAction.COPY : null);
    }
    
    @Override public void dragExit(Component component) { }
    
    @Override
    public DropAction dragEnter(Component component, Manifest dragContent,
            int supportedDropActions, DropAction userDropAction)
    {
        DropAction dropAction = null;

        if (dragContent.containsText()
            && DropAction.COPY.isSelected(supportedDropActions)) {
            dropAction = DropAction.COPY;
        }

        return dropAction;
    }
}
