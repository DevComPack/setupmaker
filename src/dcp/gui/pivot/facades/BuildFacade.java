package dcp.gui.pivot.facades;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import dcp.logic.model.config.build.IzpackConfig;
import dcp.main.log.Out;


public class BuildFacade
{
    private IzpackConfig izpackConf; // IzPack build configuration
    
    /**
     * Build Tab Facade Constructor
     * @param mode: Default Build mode
     */
    public BuildFacade()
    {
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
        Out.print("DEBUG", "Open desktop folder at " + folder.getAbsolutePath());
        return true;
    }

    // Getters/Setters
    public IzpackConfig getIzpackConfig() { return izpackConf; }
    public void setIzpackConfig(IzpackConfig izpackConf) { this.izpackConf = izpackConf; }

}
