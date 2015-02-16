package dcp.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;

import dcp.config.io.IOFactory;
import dcp.logic.factory.TypeFactory.LOG_LEVEL;
import dcp.main.log.Out;


public class PathValidator implements Validator
{

    @Override
    public boolean isValid(String s)
    {
        if (!IOFactory.pathValidate(s)) {
            Out.print(LOG_LEVEL.WARN, "Path error: " + s);
            return false;
        }
        return true;
    }

}
