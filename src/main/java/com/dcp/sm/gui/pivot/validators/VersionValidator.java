package com.dcp.sm.gui.pivot.validators;

import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.validation.Validator;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;


public class VersionValidator implements Validator
{
    private Component component;
    private String tooltipText = "Version";
    private boolean required;
    
    public VersionValidator(Component component, boolean required)
    {
        assert component != null;
        this.component = component;
        this.required = required;
    }
    
    @Override
    public boolean isValid(String str)
    {
        if (str.length() == 0) return !required;
        
        if (str.length() > 20 || !str.matches("[0-9]+([.][0-9]+)*")) {
            component.setTooltipText("[Version format incorrect x[.x]*]");
            Out.print(LOG_LEVEL.DEBUG, "Pack version format incorrect: " + str);
            
            return false;
        }
        
        component.setTooltipText(tooltipText);
        return true;
    }

}
