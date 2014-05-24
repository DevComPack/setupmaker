package dcp.gui.pivot.validators;

import org.apache.pivot.wtk.validation.Validator;


public class URLValidator implements Validator
{
    
    @Override
    public boolean isValid(String url)
    {
        if ("http://".startsWith(url) || "https://".startsWith(url))
            return true;
        if (url.equals("") ||
                url.startsWith("http://") || url.startsWith("https://"))
            return true;
        return false;
    }

}
