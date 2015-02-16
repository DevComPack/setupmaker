package dcp.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;

import dcp.logic.factory.TypeFactory.LOG_LEVEL;
import dcp.main.log.Out;


public class NameValidator implements Validator
{

    @Override
    public boolean isValid(String s)
    {
        boolean correct = true;
        
        if (s.equals(""))//Empty string
            return false;
        
        //Contains special characters
        else if (s.contains("/") || s.contains("\\") || s.contains("%") || s.contains("$") )
            correct = false;
        else if (s.contains("'") || s.contains("\"") || s.contains(",") || s.contains("&") )
            correct = false;
        else if (s.contains(":") || s.contains(";") || s.contains("?") || s.contains("!") )
            correct = false;
        else if (s.contains("(") || s.contains(")") || s.contains("[") || s.contains("]") )
            correct = false;
        else if (s.contains(">") || s.contains("<") || s.contains("@") || s.contains("²") )
            correct = false;
        else if (s.contains("{") || s.contains("}") || s.contains("°") || s.contains("=") )
            correct = false;
        else if (s.contains("+") || s.contains("*") || s.contains("#") || s.contains("£") )
            correct = false;
        else if (s.contains("¤") || s.contains("µ") || s.contains("^") || s.contains("|") )
            correct = false;
        else if (s.contains("~") || s.contains("`") )
            correct = false;
        
        if (!correct) Out.print(LOG_LEVEL.WARN, "Name format incorrect: " + s);
        return correct;
    }

}
