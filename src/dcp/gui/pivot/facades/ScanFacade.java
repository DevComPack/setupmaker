package dcp.gui.pivot.facades;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.wtk.content.TreeNode;

import dcp.logic.factory.TypeFactory.SCAN_FOLDER;
import dcp.logic.factory.TypeFactory.SCAN_MODE;
import dcp.logic.model.Group;
import dcp.logic.model.Pack;


public class ScanFacade
{
    // Configuration
    private SCAN_MODE mode = SCAN_MODE.DEFAULT;// scan mode
    private SCAN_FOLDER folder = SCAN_FOLDER.DEFAULT;// folder import type
    
    // Model
    private List<Pack> packs;//List of scanned packs (Master)
    private List<Group> groups;//List of scanned directories (Master)
    private List<TreeNode> treeData;//Tree view data

    
    public ScanFacade()
    {
        treeData = new ArrayList<TreeNode>();
        setPacks(new ArrayList<Pack>());
        setGroups(new ArrayList<Group>());
    }
    
    //// METHODS
    
    // Configuration ----------------------------------------------------
    public SCAN_MODE getScanMode()
    {
        return mode;
    }
    public void setScanMode(SCAN_MODE mode)
    {
        this.mode = mode;
    }

    public SCAN_FOLDER getFolderScan()
    {
        return folder;
    }
    public void setFolderScan(SCAN_FOLDER folder)
    {
        this.folder = folder;
    }

    // Model ---------------------------------------------------
    public List<TreeNode> getTreeData()
    {
        return treeData;
    }
    
    // Pack
    public List<Pack> getPacks()
    {
        return packs;
    }
    public void setPacks(List<Pack> packs)
    {
        this.packs = null;
        this.packs = packs;
    }
    
    // Group
    public List<Group> getGroups()
    {
        return groups;
    }
    public void setGroups(List<Group> groups)
    {
        this.groups = null;
        this.groups = groups;
    }
    
    /**
     * Clear data from display + models
     */
    public void clear()
    {
        treeData.clear();
        packs.clear();
        groups.clear();
    }

}
