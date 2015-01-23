package dcp.gui.pivot.facades;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import dcp.logic.factory.TypeFactory.BUILD_MODE;


public class BuildFacade
{
    private BUILD_MODE mode;
    
    /**
     * Build Tab Facade Constructor
     * @param mode: Default Build mode
     */
    public BuildFacade(BUILD_MODE mode)
    {
        this.setBuildMode(mode);
    }
    
    /**
     * Open target folder in system explorer
     * @param path to folder to open
     * @return success
     * @throws IOException 
     */
    public boolean openFolder(String path) throws IOException
    {
        assert path.length() > 0;

        File folder = new File(path);
        if (!folder.exists()) return false;
        if (folder.isFile()) // Get parent folder of file
            folder = folder.getParentFile();
        
        Desktop desktop = Desktop.getDesktop();
        desktop.open(folder); // Open folder
        return true;
    }

    // Getters/Setters
    public BUILD_MODE getBuildMode() { return mode; }
    public void setBuildMode(BUILD_MODE mode) { this.mode = mode; }

}
