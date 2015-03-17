package com.dcp.sm.gui.pivot.validators;

import java.io.File;

import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.validation.Validator;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;


public class PathValidator implements Validator
{
    private Component component;
    private String tooltipText;
    private boolean required;

    public PathValidator(Component component, boolean required)
    {
        assert component != null;
        this.component = component;
        this.tooltipText = component.getTooltipText();
        this.required = required;
    }
    
    /**
     * Returns if a path string exists or empty string
     * @param path: to validate
     * @param required: if path must be defined
     * @return int: error code
     * 0 - no error
     * 1 - required path not defined
     * 2 - path doesn't exist on disk
     */
    private static int pathValidate(String path, boolean required) {
        assert path != null;
        if (path.length() == 0) return (required == true)?1:0;
        File f = new File(path);
        if (f.exists())
            return 0;
        return 2;
    }
    
    @Override
    public boolean isValid(String s)
    {
        int err = pathValidate(s, required);
        if (err != 0) {
            switch(err)
            {
            case 1:
                component.setTooltipText("Path is required");
                break;
            case 2:
                component.setTooltipText("Path doesn't exist on this filesystem!");
                break;
            }
            Out.print(LOG_LEVEL.DEBUG, "Path error: " + s);
            return false;
        }
        component.setTooltipText(this.tooltipText);
        return true;
    }

}
