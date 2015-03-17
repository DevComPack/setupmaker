package com.dcp.sm.gui.pivot.frames;

import java.net.URL;


import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.RadioButton;
import org.apache.pivot.wtk.SortDirection;
import org.apache.pivot.wtk.TableView;

import com.dcp.sm.logic.factory.PackFactory;
import com.dcp.sm.logic.model.Pack;


public class SortDialog extends Dialog implements Bindable
{
    //-----DATA
    //Packs
    private List<Pack> packs = PackFactory.getPacks();//References getPacks()
    public void updatePriorities() {//Recalculate priorities
        for(int i=0; i<packs.getLength(); i++)
            packs.get(i).setPriority(i);
    }
    
    //Display
    @BXML private TableView tableView;
    //Push Buttons (position)
    @BXML private PushButton btTop;//to 1st pos
    @BXML private PushButton btUp;//move up
    @BXML private PushButton btDown;//move down
    @BXML private PushButton btBottom;//to last pos
    //Radio Buttons (sort by)
    @BXML private RadioButton rbName;
    @BXML private RadioButton rbSize;
    @BXML private RadioButton rbFileType;
    @BXML private RadioButton rbInstallType;
    @BXML private RadioButton rbGroup;
    @BXML private RadioButton rbRequired;
    @BXML private RadioButton rbCustom;
    //Actions
    private Action APositioning;//Positioning buttons Action
    private Action ASortBy;//Sort By Action
    //Info
    private String sortby = "";
    private boolean DESC = false;
    
    public SortDialog() {//Constructor
        APositioning = new Action() {//Move packs in priority
            @Override public void perform(Component source)
            {
                int oldIndex = tableView.getSelectedIndex();
                Pack P = null;
                if (oldIndex >= 0) P = packs.get(oldIndex);
                
                if (P!=null) {//If selected row
                    int newIndex = -1;
                    PushButton bt = (PushButton) source;
                    
                    if (bt.equals(btTop)) {//Top
                        newIndex = 0;
                        //Swap priorities
                        for(int i=0; i<P.getPriority()-1; i++)
                            packs.get(i).setPriority(i+1);
                    }
                    else if (bt.equals(btUp)) {//Up
                        if (oldIndex > 0)
                            newIndex = oldIndex - 1;
                        else newIndex = 0;
                        //Swap priorities
                        packs.get(newIndex).setPriority(oldIndex);
                    }
                    else if (bt.equals(btDown)) {//Down
                        if (oldIndex < packs.getLength()-1)
                            newIndex = oldIndex + 1;
                        else newIndex = packs.getLength()-1;
                        //Swap priorities
                        packs.get(newIndex).setPriority(oldIndex);
                    }
                    else if (bt.equals(btBottom)) {//Bottom
                        newIndex = packs.getLength()-1;
                        //Swap priorities
                        for(int i=packs.getLength()-1; i>P.getPriority()-1; i--)
                            packs.get(i).setPriority(i-1);
                    }
                    
                    //Swap pack position with newIndex
                    if (newIndex != oldIndex) {
                        P.setPriority(newIndex);
                        sort("priority");//Sort table by priority
                        //Out.print("PIVOT_SORT", "Pack: " + P.getName() + "- priority (" + (oldIndex) + ">" + (newIndex) + ")");
                        tableView.setSelectedIndex(newIndex);
                        tableView.requestFocus();//Focus on table view selected row
                    }
                }
            }
        };
        
        ASortBy = new Action() {//Sort By radio buttons press event
            @Override public void perform(Component arg0)
            {
                if (rbName.isSelected()) {//Name
                    sort("name");
                }
                else if (rbSize.isSelected()) {//Size
                    sort("size");
                }
                else if (rbFileType.isSelected()) {//File Type
                    sort("fileType");
                }
                else if (rbInstallType.isSelected()) {//Install Type
                    sort("installType");
                }
                else if (rbGroup.isSelected()) {//Group
                    sort("group");
                }
                else if (rbRequired.isSelected()) {//Required
                    sort("required");
                }
                else if (rbCustom.isSelected()) {//Custom
                    sort("priority");
                }
            }
        };
    }
    
    @Override
    public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2)
    {
        tableView.setTableData(packs);
        
        //Action binding
        btTop.setAction(APositioning);
        btUp.setAction(APositioning);
        btDown.setAction(APositioning);
        btBottom.setAction(APositioning);
        
        rbName.setAction(ASortBy);
        rbSize.setAction(ASortBy);
        rbFileType.setAction(ASortBy);
        rbInstallType.setAction(ASortBy);
        rbGroup.setAction(ASortBy);
        rbRequired.setAction(ASortBy);
        rbCustom.setAction(ASortBy);
       
    }
    
    /*
     * Sort function for radio buttons
     * Custom mode managed by 'priority'
     */
    private void sort(String BY) {
        if (!sortby.equals(BY)) DESC = false;//First Ascending
        else DESC = !DESC;//Flip sort
        if (BY.toLowerCase().equals("priority")) {
            tableView.setSort(BY, SortDirection.ASCENDING);//Priority always sorted Ascending
            rbCustom.setSelected(true);//Select Custom radio button
        }
        else {
            tableView.setSort(BY, (DESC)?SortDirection.DESCENDING:SortDirection.ASCENDING);//Sort
            updatePriorities();
        }
        sortby = BY;
    }

}
