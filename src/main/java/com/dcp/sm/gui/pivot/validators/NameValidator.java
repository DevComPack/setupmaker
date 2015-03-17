package com.dcp.sm.gui.pivot.validators;

import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.validation.Validator;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;


public class NameValidator implements Validator
{
    private Component component;
    private String tooltipText = "Name";
    private boolean required;
    private boolean id;
    
    public NameValidator(Component component, boolean required, boolean id)
    {
        assert component != null;
        this.component = component;
        this.required = required;
        this.id = id;
    }

    @Override
    public boolean isValid(String str)
    {
        if (str.length() == 0) return !this.required;
        
        if (!(str.matches("[a-zA-Z._\\-0-9]+")
                || (!id && str.matches("[a-zA-Z._\\-0-9 ]+")) )) {
            Out.print(LOG_LEVEL.DEBUG, "Pack name incorrect: " + str);
            if (str.contains(" "))
                component.setTooltipText("[No space allowed]");
            else component.setTooltipText("[Name contains invalid characters]");
            
            return false;
        }
        
        component.setTooltipText(tooltipText);
        return true;
    }

}
