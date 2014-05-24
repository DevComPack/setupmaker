package dcp.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;

import dcp.config.io.IOFactory;


public class PathValidator implements Validator
{

    @Override
    public boolean isValid(String text)
    {
        return IOFactory.pathValidate(text);
    }

}
