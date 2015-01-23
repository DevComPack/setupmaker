package dcp.gui.pivot.facades;

import dcp.logic.factory.TypeFactory.BUILD_MODE;


public class BuildFacade
{
    private BUILD_MODE mode;
    
    /**
     * Build Tab Facade Constructor
     * @param mode: Default Build mode
     */
    public BuildFacade(BUILD_MODE mode)
    {
        this.setBuildMode(mode);
    }

    // Getters/Setters
    public BUILD_MODE getBuildMode() { return mode; }
    public void setBuildMode(BUILD_MODE mode) { this.mode = mode; }

}
