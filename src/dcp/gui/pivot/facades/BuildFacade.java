package dcp.gui.pivot.facades;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import dcp.gui.pivot.Master;
import dcp.main.log.Out;


public class BuildFacade
{
    /*
    private IzpackConfig izpackConf; // IzPack build configuration
    private NugetConfig nugetConf; // NuGet build configuration
    */
    
    /**
     * Build Tab Facade Constructor
     */
    public BuildFacade()
    {
    }
    /*
    public IzpackConfig getIzpackConfig() { return izpackConf; }
    public NugetConfig getNugetConfig() { return nugetConf; }
    */
    
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

    /// ************************************************************************************************ IZPACK

    public void setIzSplit(boolean enable)
    {
        Master.izpackConf.setSplit(enable);
    }
    public void setIzWebSetup(boolean enable)
    {
        Master.izpackConf.setWebSetup(enable);
    }
    
    /// ************************************************************************************************ NUGET

    public int getNugStepNbr()
    {
        return Master.nugetConf.getStepNbr();
    }
    public void setNugStepNbr(int stepNbr)
    {
        Master.nugetConf.setStepNbr(stepNbr);
    }

    public String getNugFeedUrl()
    {
        return Master.nugetConf.getFeedUrl();
    }
    public void setNugFeedUrl(String feed)
    {
        Master.nugetConf.setFeedUrl(feed);
    }

}
