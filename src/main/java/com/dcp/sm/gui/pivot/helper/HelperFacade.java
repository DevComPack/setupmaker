package com.dcp.sm.gui.pivot.helper;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.CardPane;
import org.apache.pivot.wtk.CardPaneListener;
import org.apache.pivot.wtk.LinkButton;
import org.apache.pivot.wtk.Meter;
import org.apache.pivot.wtk.Sheet;


public class HelperFacade extends Sheet implements Bindable
{
    private @BXML CardPane cardPane;
    private @BXML Meter meter;
    private @BXML LinkButton btPrevious;
    private @BXML LinkButton btNext;
    
    /**
     * Sets the initial page of helper to display
     * @param index
     */
    public void setIndex(int index) {
        cardPane.setSelectedIndex(index);
    }

    @Override
    public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2)
    {
        cardPane.getCardPaneListeners().add(new CardPaneListener.Adapter() {
            @Override
            public void selectedIndexChanged(CardPane cardPane, int previousSelectedIndex) {
                updateLinkButtonState();
            }
        });
        
        btPrevious.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                cardPane.setSelectedIndex(cardPane.getSelectedIndex() - 1);
            }
        });
        btNext.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                cardPane.setSelectedIndex(cardPane.getSelectedIndex() + 1);
            }
        });
    }
    
    private void updateLinkButtonState() {
        int selectedIndex = cardPane.getSelectedIndex();
        btPrevious.setEnabled(selectedIndex > 0);
        btNext.setEnabled(selectedIndex < cardPane.getLength() - 1);
        meter.setPercentage((selectedIndex+1) * Double.parseDouble(meter.getStyles().get("gridFrequency").toString()));
    }

}
