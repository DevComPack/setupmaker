package dcp.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;

import dcp.logic.factory.TypeFactory.LOG_LEVEL;
import dcp.main.log.Out;


public class VersionValidator implements Validator
{
    @Override
    public boolean isValid(String s)
    {
        if (s.length() > 0) {
            if (s.length() > 20 || !s.matches("[0-9]+([.][0-9]+)*")) {
                Out.print(LOG_LEVEL.WARN, "Pack version format incorrect: " + s);
                return false;
            }
        }
        return true;
    }

}
