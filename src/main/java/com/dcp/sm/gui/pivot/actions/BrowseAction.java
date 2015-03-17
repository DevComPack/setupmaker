package com.dcp.sm.gui.pivot.actions;

import java.io.File;

import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.FileBrowserSheet;

public class BrowseAction extends Action
{

    private FileBrowserSheet fileBrowserSheet = null;

    private File rootDirectory = null;

    public BrowseAction(FileBrowserSheet fileBrowserSheet)
    {
        this.fileBrowserSheet = fileBrowserSheet;
        this.fileBrowserSheet.setRootDirectory(new File("").getAbsoluteFile());
    }

    public BrowseAction(FileBrowserSheet fileBrowserSheet, String rootDirectory)
    {
        this.fileBrowserSheet = fileBrowserSheet;
        this.rootDirectory = new File(rootDirectory);
    }

    @Override
    public void perform(Component component)
    {
        if (rootDirectory != null && rootDirectory.exists())
        {
            if (rootDirectory.isDirectory())
                fileBrowserSheet.setRootDirectory(rootDirectory.getAbsoluteFile());
            else
            {
                fileBrowserSheet.setRootDirectory(rootDirectory.getParentFile().getAbsoluteFile());
                fileBrowserSheet.setSelectedFile(rootDirectory.getAbsoluteFile());
            }
        }
        fileBrowserSheet.open(component.getWindow());
    }

}
