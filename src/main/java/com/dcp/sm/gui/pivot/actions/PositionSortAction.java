package com.dcp.sm.gui.pivot.actions;


import org.apache.pivot.collections.List;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.RadioButton;
import org.apache.pivot.wtk.SortDirection;
import org.apache.pivot.wtk.TableView;

import com.dcp.sm.logic.factory.PackFactory;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.logic.model.Pack;
import com.dcp.sm.main.log.Out;


public class PositionSortAction extends Action
{
    private List<Pack> packs;
    private TableView tableView;//Sort area
    //Display components to refresh
    private PushButton btTop;//to 1st pos
    private PushButton btUp;//move up
    private PushButton btDown;//move down
    private PushButton btBottom;//to last pos
    private RadioButton rbCustom;//Custom sort choice
    
    public PositionSortAction(TableView TABLEVIEW, RadioButton CUSTOM,
            PushButton TOP, PushButton UP, PushButton DOWN, PushButton BOTTOM)
    {
        packs = PackFactory.getPacks();//Get packs data
        tableView = TABLEVIEW;
        rbCustom = CUSTOM;
        btTop = TOP;
        btUp = UP;
        btDown = DOWN;
        btBottom = BOTTOM;
    }
    
    public void setTableView(TableView TABLEVIEW) {
        tableView = TABLEVIEW;
    }

    @Override
    public void perform(Component source)
    {
        int oldIndex = tableView.getSelectedIndex();
        Pack P = null;
        if (oldIndex >= 0) P = packs.get(oldIndex);
        
        if (P!=null) {//If selected row
            int newIndex = -1;
            PushButton bt = (PushButton) source;
            
            if (bt.equals(btTop)) {//Top
                newIndex = 0;
                for(int i=0; i<P.getPriority(); i++)
                    packs.get(i).setPriority(i+1);
            }
            else if (bt.equals(btUp)) {//Up
                if (oldIndex > 0)
                    newIndex = oldIndex - 1;
                else newIndex = 0;
                packs.get(newIndex).setPriority(oldIndex);
            }
            else if (bt.equals(btDown)) {//Down
                if (oldIndex < packs.getLength()-1)
                    newIndex = oldIndex + 1;
                else newIndex = packs.getLength()-1;
                packs.get(newIndex).setPriority(oldIndex);
            }
            else if (bt.equals(btBottom)) {//Bottom
                newIndex = packs.getLength()-1;
                for(int i=packs.getLength()-1; i>P.getPriority(); i--)
                    packs.get(i).setPriority(i-1);
            }
            
            //Swap pack position with newIndex
            if (newIndex != oldIndex) {
                P.setPriority(newIndex);
                //Sort table by priority
                tableView.setSort("priority", SortDirection.ASCENDING);//Priority always sorted Ascending
                rbCustom.setSelected(true);//Select Custom radio button
                Out.print(LOG_LEVEL.DEBUG, "Pack: " + P.getName() + "- priority (" + (oldIndex) + ">" + (newIndex) + ")");
                tableView.setSelectedIndex(newIndex);
                tableView.requestFocus();//Focus on table view selected row
            }
        }
    }

}
