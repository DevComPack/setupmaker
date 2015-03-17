package com.dcp.sm.logic.factory;

import java.io.File;
import java.io.IOException;
import java.util.ListIterator;








import org.apache.pivot.wtk.content.TreeBranch;
import org.apache.pivot.wtk.content.TreeNode;
import org.apache.pivot.wtk.media.Image;

import com.dcp.sm.config.io.IOFactory;
import com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.logic.factory.TypeFactory.SCAN_FOLDER;
import com.dcp.sm.logic.factory.TypeFactory.SCAN_MODE;
import com.dcp.sm.config.io.OSValidator;
import com.dcp.sm.logic.model.Group;
import com.dcp.sm.logic.model.Pack;
import com.dcp.sm.logic.model.config.SetupConfig;
import com.dcp.sm.main.log.Out;

/**
 * Casts class between
 * Pivot components, models and java types
 */
public class CastFactory
{
    //Constants
    private final static String PATH_DELIMITER = "/";
    
    
    /**
     * File to Pack cast
     * @param FILE
     * @return Pack
     */
    public static Pack fileToPack(File FILE) {
        String NAME = FILE.getName();//File name
        Image IMG = null;//Icon
        IMG = nameToImage(NAME, FILE.isDirectory());
        Pack P = new Pack(NAME, IMG);//Pack (NAME + IMG)
        P.setFileType(IOFactory.getFileType(FILE));
        P.setSize(FILE.length());//Size
        
        try {//File Path
            P.setPath(FILE.getCanonicalPath());//PATH
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return P;
    }
    
    /**
     * Pivot Image from Pack name
     * @param NAME
     * @param folder
     * @return Image
     */
    public static Image nameToImage(String NAME, boolean folder)
    {
        Image IMG = null;//Icon
        if (folder) {//Folder
            IMG = IOFactory.imgFolder;
        }
        else {//File icon
            if (IOFactory.isFileType(NAME, FILE_TYPE.Archive))
                IMG = IOFactory.imgZip;
            else if (IOFactory.isFileType(NAME, FILE_TYPE.Executable))
                IMG = IOFactory.imgExe;
            else if (IOFactory.isFileType(NAME, FILE_TYPE.Setup))
                IMG = IOFactory.imgSetup;
            else if (IOFactory.isFileType(NAME, FILE_TYPE.Document))
                IMG = IOFactory.imgDoc;
            else if (IOFactory.isFileType(NAME, FILE_TYPE.Image))
                IMG = IOFactory.imgImg;
            else if (IOFactory.isFileType(NAME, FILE_TYPE.Web))
                IMG = IOFactory.imgWeb;
            else if (IOFactory.isFileType(NAME, FILE_TYPE.Sound))
                IMG = IOFactory.imgSound;
            else if (IOFactory.isFileType(NAME, FILE_TYPE.Video))
                IMG = IOFactory.imgVid;
            else if (IOFactory.isFileType(NAME, FILE_TYPE.Custom))
                IMG = IOFactory.imgCust;
            else
                IMG = IOFactory.imgFile;//Simple File
        }
        return IMG;
    }

    /**
     * String array path to single string delimited by '/'
     * @param PATH
     * @return String Path
     */
    public static String pathToString(String[] PATH) {
        String tmpPath = "";
        for(String s:PATH)//Strings concat
            tmpPath = tmpPath + s + PATH_DELIMITER;
        return tmpPath;
    }
    
    /**
     * Tree view Node to String array path from Tree view root
     * @param NODE
     * @return path array of parent groups
     */
    public static String[] nodeToPath(TreeNode NODE) {
        java.util.List<String> backList = new java.util.ArrayList<String>();
        //Return back in hierarchy
        TreeNode Ntmp = NODE;
        do {
            backList.add(Ntmp.getText());
            Ntmp = Ntmp.getParent();
        } while(Ntmp != null);
        //Copy the hierarchy from top to bottom
        String[] PATH = new String[backList.size()];
        int i = 0;
        ListIterator<String> it = backList.listIterator(backList.size());
        while(it.hasPrevious()) {
            PATH[i++] = it.previous();
        }
        
        return PATH;
    }
    
    /**
     * Add Group's children to tree branch recursively
     * @param group
     * @return group's new branch instance
     */
    public static TreeBranch groupToSingleBranch(Group group) {
        TreeBranch TB = new TreeBranch(IOFactory.imgFolder, group.getName());
        TB.setUserData(group);//Save model data
        return TB;
    }
    private static void recursivAdd(Group group, TreeBranch TARGET_TB) {
        for(Group G:group.getChildren()) {
            TreeBranch TB = groupToSingleBranch(G);
            TARGET_TB.add(TB);
            recursivAdd(G, TB);//-@
        }
    }
    /**
     * Group model to new TreeBranch tree view component
     * @param group
     * @return group's new branch instance with childs
     */
    public static TreeBranch groupToBranch(Group group) {
        TreeBranch TB = groupToSingleBranch(group);
        recursivAdd(group, TB);//Add its childs
        return TB;
    }
    
    /**
     * Pack model to Tree view node
     * @param P
     * @return pack's new node instance
     */
    public static TreeNode packToNode(Pack P) {
        //TreeNode node = new TreeNode(P.getIcon(), P.getInstallName());
        TreeNode node = new TreeNode(P.getIcon(), P.getName());
        node.setUserData(P);//Save model data
        return node;
    }
    
    
    /**
     * Validate path
     * @param target_file
     * @param default_name
     * @param extension
     * @return validated path
     */
    public static String pathValidate(String target_file, String default_name, String extension) {
        //Target file output validating
        if (target_file.equals(""))//If no path entered
            target_file = new File("").getAbsolutePath() + "/"+default_name + "."+extension;//in working directory
        
        else {//Path entered
            
            //if relative path entered
            if ( (OSValidator.isWindows() && !target_file.substring(1).startsWith(":")) ||//Windows starts with '?:'
                    (OSValidator.isUnix() && !target_file.startsWith("/")) )//Unix starts with '/'
                target_file = new File("").getAbsolutePath() + "/" + target_file;
            //if no extension .jar given in path, add it to last name
            if (!(target_file.endsWith("."+extension.toLowerCase()) ||
                  target_file.endsWith("."+extension.toUpperCase()))) {
                
                if (target_file.endsWith("/") || target_file.endsWith("\\"))//if ends with directory
                    target_file = target_file + default_name + "."+extension;
                else//If ends with file name
                    target_file = target_file + "."+extension;
                
            }
            //If parent path doesn't exist
            if (!new File(new File(target_file).getParent()).exists()) {
                new File(new File(target_file).getParent()).mkdir();//Making the directory
                if (!new File(new File(target_file).getParent()).exists())//If error in creating dir
                    target_file = new File("").getAbsolutePath() + "/" + default_name;//Working dir
            }
        }
        
        return target_file;
    }
    
    /**
     * Update model data of old Pack
     * @param P: Pack to update
     * @param DCP_VERSION: old DCP version of pack
     */
    public static void packModelUpdate(Pack P, String DCP_VERSION) {
        switch (DCP_VERSION) {// no break for first cases to update properties on cascade
        case "1.0":
            P.setInstallVersion(Pack.getVersionFromName(P.getName()));
        case "1.1":
            P.setArch(0);
            
        Out.print(LOG_LEVEL.DEBUG, "Pack data model updated from "+ DCP_VERSION +".x to current DCP version");
        break;
        }
    }
    
    /**
     * Update setup data of old SetupConfigs
     * @param setupConfig: SetupConfig to update
     * @param DCP_VERSION: old DCP version of pack
     */
    public static void setupModelUpdate(SetupConfig setupConfig, String DCP_VERSION) {
        switch (DCP_VERSION) {// no break for first cases to update properties on cascade
        case "1.0":
        case "1.1":
            setupConfig.setScanMode(SCAN_MODE.SIMPLE_SCAN);
            setupConfig.setScanFolder(SCAN_FOLDER.PACK_FOLDER);
            
        Out.print(LOG_LEVEL.DEBUG, "Setup data model updated from "+ DCP_VERSION +".x to current DCP version");
        break;
        }
    }
    
}
