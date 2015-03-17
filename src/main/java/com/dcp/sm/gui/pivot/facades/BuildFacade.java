package com.dcp.sm.gui.pivot.facades;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;

import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.TaskAdapter;

import com.dcp.sm.gui.pivot.Master;
import com.dcp.sm.gui.pivot.tasks.TaskIzpackCompile;
import com.dcp.sm.gui.pivot.tasks.TaskNugetCompile;
import com.dcp.sm.logic.factory.TypeFactory.BUILD_MODE;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;


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
        if (!folder.exists() || folder.isFile()) // Get parent folder of file
            folder = folder.getParentFile();
        if (!folder.exists()) return false;
        
        Desktop desktop = Desktop.getDesktop();
        desktop.open(folder); // Open folder
        Out.print(LOG_LEVEL.DEBUG, "Open desktop folder at " + folder.getAbsolutePath());
        
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
    
    public void copyToClipboard(Sequence<String> data)
    {
        if (data.getLength() > 0) {
            String selCb = ""; // full selection data string
            for (int i = 0; i < data.getLength(); i++) { // concat selection with line ends
                selCb = String.format("%s%s%n", selCb, data.get(i));//selCb += sel.get(i) + "\n"; 
            }
            
            Out.print(LOG_LEVEL.DEBUG, "Copied to Clipboard: "+ selCb);
            StringSelection selection = new StringSelection(selCb);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }
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
        
        if (SIZE_IN_MB == 0) Master.facade.izpackConf.setSplit(false);
        else {
            Master.facade.izpackConf.setSplit(true);
            Master.facade.izpackConf.setSplitSize(SIZE_IN_MB);
        }
    }
    /**
     * Set Websetup path
     * @param url: web directory
     */
    public void setIzWebUrl(String url)
    {
        Master.facade.izpackConf.setWebUrl(url);
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

    /// ************************************************************************************************ BUILD
    
    public void build(String target, TaskListener<Boolean> tlCompile)
    {
        if (mode.equals(BUILD_MODE.IZPACK_BUILD)) { // IzPack compile task
            TaskIzpackCompile compileTask = new TaskIzpackCompile(target, Master.facade.setupConfig, Master.facade.izpackConf);
            compileTask.setLogger(Out.getLogger());//Setting log display on logger
            compileTask.execute(new TaskAdapter<Boolean>(tlCompile));//Compile
        }
        else if (mode.equals(BUILD_MODE.NUGET_BUILD)) { // NuGet compile task
            TaskNugetCompile compileTask = new TaskNugetCompile(target, getNugFeedUrl(), getNugStepNbr());
            compileTask.setLogger(Out.getLogger());//Setting log display on logger
            compileTask.execute(new TaskAdapter<Boolean>(tlCompile));// Compile
        }
    }

}
