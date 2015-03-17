package com.dcp.sm.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;


public class Iso3Validator implements Validator
{

    @Override
    public boolean isValid(String s)
    {
        if (s.equals(""))//Empty string
            return true;
        
        if (s.length() != 3) {// should be 3 characters
            Out.print(LOG_LEVEL.DEBUG, "ISO3 format incorrect: " + s);
            return false;
        }
        
        return true;
    }

}
