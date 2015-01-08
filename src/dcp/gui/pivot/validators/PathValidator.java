package dcp.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;

import dcp.config.io.IOFactory;
import dcp.main.log.Out;


public class PathValidator implements Validator
{

    @Override
    public boolean isValid(String s)
    {
        if (!IOFactory.pathValidate(s)) {
            Out.print("WARNING", "Path error: " + s);
            return false;
        }
        return true;
    }

}
