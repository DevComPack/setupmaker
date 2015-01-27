package dcp.gui.pivot.facades;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import dcp.gui.pivot.Master;
import dcp.logic.factory.TypeFactory.BUILD_MODE;
import dcp.main.log.Out;


public class BuildFacade
{
    private BUILD_MODE mode = BUILD_MODE.DEFAULT;
    
    /**
     * Build Tab Facade Constructor
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
    

    public BUILD_MODE getBuildMode()
    {
        return mode;
    }
    public void setBuildMode(BUILD_MODE mode)
    {
        this.mode = mode;
    }

    /// ************************************************************************************************ IZPACK

    public void setIzSplit(boolean enable)
    {
        Master.facade.izpackConf.setSplit(enable);
    }
    public void setIzWebSetup(boolean enable)
    {
        Master.facade.izpackConf.setWebSetup(enable);
    }
    
    /**
     * Enable/Set Packaging option
     * @param SIZE_IN_MB
     */
    public void setIzSplitSize(int size, String unit)
    {
        int SIZE_IN_MB = size * (unit.equals("TB")?1024*1024 : (unit.equals("GB")?1024 : 1));
        
        if (SIZE_IN_MB == 0) Master.facade.setupConfig.setSplit(false);
        else {
            Master.facade.setupConfig.setSplit(true);
            Master.facade.setupConfig.setSplitSize(SIZE_IN_MB);
        }
    }
    
    /// ************************************************************************************************ NUGET

    public int getNugStepNbr()
    {
        return Master.facade.nugetConf.getStepNbr();
    }
    public void setNugStepNbr(int stepNbr)
    {
        Master.facade.nugetConf.setStepNbr(stepNbr);
    }

    public String getNugFeedUrl()
    {
        return Master.facade.nugetConf.getFeedUrl();
    }
    public void setNugFeedUrl(String feed)
    {
        Master.facade.nugetConf.setFeedUrl(feed);
    }

}
