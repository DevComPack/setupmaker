package com.dcp.sm.logic.model.config.build;

import java.io.Serializable;


public class NugetConfig implements Serializable
{
    /**
     * class written to workspace file: conf.dcp
     * from AppConfig class
     */
    private static final long serialVersionUID = 829663743614405550L;
    
    // Nuget options
    private String feedUrl = "http://";// default feed url
    private int stepNbr = 3;// step number (default 3)

    public NugetConfig()
    {
    }
    public NugetConfig(NugetConfig nugetConf)
    {
        this.feedUrl = nugetConf.feedUrl;
        this.stepNbr = nugetConf.stepNbr;
    }
    
    // Nuget options methods
    public String getFeedUrl() { return feedUrl; }
    public void setFeedUrl(String feedUrl) { this.feedUrl = feedUrl; }
    public int getStepNbr() { return stepNbr; }
    public void setStepNbr(int stepNbr) { this.stepNbr = stepNbr; }

}
