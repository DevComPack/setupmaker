package com.dcp.sm.logic.model.config.build;

import java.io.Serializable;


public class IzpackConfig implements Serializable
{
    /**
     * class written to workspace file: conf.dcp
     * from AppConfig class
     */
    private static final long serialVersionUID = 1550797914797285496L;

    // Packaging Split option
    private boolean split = false;//Split option
    private int splitSize = 0;//Split size
    // Web Area
    private boolean webSetup = false;//Web Setup option
    private String webUrl = "";//Web HTTP server URL
    private WebConfig webConfig;// SFTP Web config

    public IzpackConfig()
    {
        this.webConfig = new WebConfig();
    }
    public IzpackConfig(IzpackConfig izpackConf)
    {
        this.split = izpackConf.split;
        this.splitSize = izpackConf.splitSize;
        this.webSetup = izpackConf.webSetup;
        this.webUrl = izpackConf.webUrl;
        this.webConfig = new WebConfig(izpackConf.webConfig);
    }
    
    public boolean isSplit() { return split; }
    public void setSplit(boolean split) { this.split = split; }

    public int getSplitSize() { return splitSize; }
    public void setSplitSize(int splitSizeInMB) { this.splitSize = splitSizeInMB * (1024*1024); }

    public boolean isWebSetup() { return webSetup; }
    public void setWebSetup(boolean webSetup) { this.webSetup = webSetup; }

    public String getWebUrl() { return webUrl; }
    public void setWebUrl(String webUrl) { this.webUrl = webUrl; }

    public WebConfig getWebConfig() { return webConfig; }
    public void setWebConfig(WebConfig webConfig) { this.webConfig = webConfig; }

}
