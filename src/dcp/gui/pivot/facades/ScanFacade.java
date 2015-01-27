package dcp.gui.pivot.facades;

import dcp.logic.factory.TypeFactory.SCAN_MODE;


public class ScanFacade
{
    // DATA
    private SCAN_MODE mode = SCAN_MODE.DEFAULT;

    public ScanFacade()
    {
    }
    
    public SCAN_MODE getScanMode()
    {
        return mode;
    }
    public void setScanMode(SCAN_MODE mode)
    {
        this.mode = mode;
    }

}
