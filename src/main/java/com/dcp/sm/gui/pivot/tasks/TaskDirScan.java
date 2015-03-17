package com.dcp.sm.gui.pivot.tasks;


import java.io.File;
import java.io.FilenameFilter;

import org.apache.pivot.collections.List;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.wtk.content.TreeBranch;
import org.apache.pivot.wtk.content.TreeNode;

import com.dcp.sm.config.io.IOFactory;
import com.dcp.sm.gui.pivot.frames.ScanFrame;
import com.dcp.sm.logic.factory.CastFactory;
import com.dcp.sm.logic.factory.TypeFactory.SCAN_MODE;
import com.dcp.sm.logic.model.Group;
import com.dcp.sm.logic.model.Pack;


public class TaskDirScan extends Task<Integer>
{
    //Constants
    private final static int INFINITE_DEPTH = 20;//Maximum recursive scan depth if depth is '-'
    //Attributes
    private ScanFrame scanFrame;
    private String path;
    private List<TreeNode> treeData;
    private String depthValue;
    //Data
    private List<Group> groups;
    private List<Pack> packs;
    //Filter
    private boolean folderFilter;
    private FilenameFilter filter;
    
    
    public TaskDirScan(ScanFrame source, String path, List<TreeNode> treeData, FilenameFilter filter,
                       String depthValue, boolean folderFilter)
    {
        this.scanFrame = source;
        // point to data models lists
        this.groups = scanFrame.facade.getGroups();
        this.packs = scanFrame.facade.getPacks();
        
        this.path = path;
        this.treeData = treeData;
        this.filter= filter;
        
        this.depthValue = depthValue;
        this.folderFilter = folderFilter;
    }
    
    
    /**
     * Recursive Scan Function + Groups create
     * Scan DIR to treeBranch, directories to PARENT
     */
    private void recursivScan(File directory, TreeBranch treeBranch, Group PARENT, int DEPTH) {
        for(File f:directory.listFiles(filter)) {
            if (f.isDirectory()) {//Directory
                if (DEPTH>0) {
                    Group G = new Group(f.getName(),PARENT);
                    groups.add(G);//Group model add
                    TreeBranch T = new TreeBranch(IOFactory.imgFolder, f.getName());
                    recursivScan(f, T, G, DEPTH-1);//@-
                    treeBranch.add(T);//Tree Branch node add
                }
                else if (folderFilter) {//Get folders as packs
                    Pack P = CastFactory.fileToPack(f);
                    P.setPriority(packs.getLength());//Priority initialize
                    P.setGroup(PARENT);
                    packs.add(P);
                    treeBranch.add(new TreeNode(P.getIcon(), P.getName()));//Tree node add
                }
            }
            else {//File
                Pack P = CastFactory.fileToPack(f);
                P.setPriority(packs.getLength());//Priority initialize
                P.setGroup(PARENT);//Setting Parent
                packs.add(P);
                treeBranch.add(new TreeNode(P.getIcon(), P.getName()));//Tree node add
            }
        }
    }
    
    /**
     * Simple Scan Functions
     * @param directory to scan
     * @param folder folder filter is selected
     */
    private void simpleScan(File directory) {
        for(File f : directory.listFiles(filter)) {
            if (f.isDirectory()) {//Directory
                if (folderFilter) {//Directory filter cb selected (not active)
                    Pack P = CastFactory.fileToPack(f);
                    P.setPriority(packs.getLength());//Priority initialize
                    packs.add(P);
                    treeData.add(new TreeNode(P.getIcon(), P.getName()));
                }
            }
            else if (f.isFile()) {//File
                Pack P = CastFactory.fileToPack(f);
                P.setPriority(packs.getLength());//Priority initialize
                packs.add(P);
                treeData.add(new TreeNode(P.getIcon(), P.getName()));
            }
        }
    }
    
    @Override
    public Integer execute()
    {
        scanFrame.facade.clear();// Clear data
        
        if (!path.equals("")) {//If a path is entered
            File dir = new File(path);
            
            if (dir.exists()) {
                scanFrame.setModified(true);//Flag Modified
                
                TreeBranch treeBranch = new TreeBranch(dir.getName());
                SCAN_MODE scanMode = ScanFrame.getSingleton().facade.getScanMode();
                
                if (scanMode == SCAN_MODE.SIMPLE_SCAN) {//Simple Scan
                    simpleScan(dir);
                }
                else if (scanMode == SCAN_MODE.RECURSIVE_SCAN) {//Recursive Scan
                    String DEPTH = depthValue;
                    if (DEPTH.equals("-"))//Infinite depth
                        recursivScan(dir, treeBranch, null, INFINITE_DEPTH);
                    else//Limited depth [1-5]
                        recursivScan(dir, treeBranch, null, Integer.parseInt(DEPTH));
                }
                //Populate the treeData with nodes of the resulting directory TreeBranch
                for(int i=0; i<treeBranch.getLength(); i++)
                    treeData.add(treeBranch.get(i));
                
            }
            else {//Path doesn't exist
                return 2;
            }
        }
        else {//Path value empty
            return 1;
        }
        
        return 0;//Success
    }

}
