package dcp.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;


public class IntValidator implements Validator
{

    @Override
    public boolean isValid(String s)
    {
        if (s.equals(""))//Empty string
            return false;
        
        try { Integer.parseInt(s); }
        catch(NumberFormatException e) {
            return false;
        }
        
        return true;
    }

}
