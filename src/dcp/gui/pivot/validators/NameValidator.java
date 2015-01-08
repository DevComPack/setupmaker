package dcp.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;

import dcp.main.log.Out;


public class NameValidator implements Validator
{

    @Override
    public boolean isValid(String s)
    {
        boolean correct = true;
        
        if (s.equals(""))//Empty string
            correct = false;
        
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
        
        if (!correct) Out.print("WARNING", "Name format incorrect: " + s);
        return correct;
    }

}
