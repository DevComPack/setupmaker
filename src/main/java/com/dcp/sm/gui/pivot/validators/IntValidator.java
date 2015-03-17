package com.dcp.sm.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;


public class IntValidator implements Validator
{

    @Override
    public boolean isValid(String str)
    {
        if (str.length() == 0)// Empty string
            return false;
        
        try { Integer.parseInt(str); }
        catch(NumberFormatException e) {
            return false;
        }
        
        return true;
    }

}
