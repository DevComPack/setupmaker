package dcp.logic.model.config.build;

import java.io.Serializable;


public class WebConfig implements Serializable
{
    private static final long serialVersionUID = 1439240538763028358L;

    private boolean enabled = false;
    
    private String host;
    private String user;
    private String pass;
    private String remoteDir;
    private String path;

    public boolean isEnabled()
    {
        return enabled;
    }
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public String getHost()
    {
        return host;
    }
    public void setHost(String host)
    {
        this.host = host;
    }
    public String getUser()
    {
        return user;
    }
    public void setUser(String user)
    {
        this.user = user;
    }
    public String getPass()
    {
        return pass;
    }
    public void setPass(String pass)
    {
        this.pass = pass;
    }
    public String getRemoteDir()
    {
        return remoteDir;
    }
    public void setRemoteDir(String remoteDir)
    {
        this.remoteDir = remoteDir;
    }
    public String getPath()
    {
        return path;
    }
    public void setPath(String path)
    {
        this.path = path;
    }
    
}
