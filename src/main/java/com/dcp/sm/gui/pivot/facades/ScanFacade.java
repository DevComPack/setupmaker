package com.dcp.sm.gui.pivot.facades;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.collections.Sequence.Tree.Path;
import org.apache.pivot.wtk.content.TreeNode;

import com.dcp.sm.gui.pivot.Master;
import com.dcp.sm.logic.factory.TypeFactory.SCAN_FOLDER;
import com.dcp.sm.logic.factory.TypeFactory.SCAN_MODE;
import com.dcp.sm.logic.model.Group;
import com.dcp.sm.logic.model.Pack;


public class ScanFacade
{
    // Configuration
    private SCAN_MODE mode = SCAN_MODE.DEFAULT;// scan mode
    
    // Model
    private List<Pack> packs;//List of scanned packs
    private List<Group> groups;//List of scanned directories
    private List<TreeNode> treeData;//Tree view data

    
    public ScanFacade()
    {
        treeData = new ArrayList<TreeNode>();
        packs = new ArrayList<Pack>();
        groups = new ArrayList<Group>();
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
        Master.facade.setupConfig.setScanMode(mode);
    }

    public SCAN_FOLDER getFolderScan()
    {
        return Master.facade.setupConfig.getScanFolder();
    }
    public void setFolderScan(SCAN_FOLDER folder)
    {
        Master.facade.setupConfig.setScanFolder(folder);
    }

    public boolean getFolderTarget()
    {
        return Master.facade.setupConfig.getScanTarget();
    }
    public void setFolderTarget(boolean folderTarget)
    {
        Master.facade.setupConfig.setScanTarget(folderTarget);
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
    
    // Group
    public List<Group> getGroups()
    {
        return groups;
    }
    
    /**
     * Get only packs checked in treeview
     * @param sequence of checked paths in treeview
     * @return list of checked packs
     */
    public List<Pack> getCheckedPacks(Sequence<Path> list) {
        List<Pack> selected = new ArrayList<Pack>();

        if (list.getLength() > 0) {
            Path p;
            for(int i = 0; i < list.getLength(); i++) {
                p = list.get(i);
                TreeNode node = treeData.get(p.get(0));
                for(Pack P:packs) {
                    if (node.getParent() == null && P.getName().equalsIgnoreCase(node.getText()) ) {
                        selected.add(P);
                        break;
                    }
                }
            }
        }
        return selected;
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
