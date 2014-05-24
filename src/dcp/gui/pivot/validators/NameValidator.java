package dcp.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;


public class NameValidator implements Validator
{

    @Override
    public boolean isValid(String s)
    {
        if (s.equals(""))//Empty string
            return false;
        
        //Contains special characters
        if (s.contains("/") || s.contains("\\") || s.contains("%") || s.contains("$") )
            return false;
        if (s.contains("'") || s.contains("\"") || s.contains(",") || s.contains("&") )
            return false;
        if (s.contains(":") || s.contains(";") || s.contains("?") || s.contains("!") )
            return false;
        if (s.contains("(") || s.contains(")") || s.contains("[") || s.contains("]") )
            return false;
        if (s.contains(">") || s.contains("<") || s.contains("@") || s.contains("²") )
            return false;
        if (s.contains("{") || s.contains("}") || s.contains("°") || s.contains("=") )
            return false;
        if (s.contains("+") || s.contains("*") || s.contains("#") || s.contains("£") )
            return false;
        if (s.contains("¤") || s.contains("µ") || s.contains("^") || s.contains("|") )
            return false;
        if (s.contains("~") || s.contains("`") )
            return false;
        
        return true;
    }

}
