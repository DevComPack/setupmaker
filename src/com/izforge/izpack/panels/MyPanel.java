package com.izforge.izpack.panels;
 
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
 
public class MyPanel extends IzPanel {
 
  private static final long serialVersionUID = -2686831922771063920L;
 
  public MyPanel(InstallerFrame parent, InstallData idata) {
    super(parent, idata);
  }
 
  @Override
  public void panelActivate() {
    System.out.println("Hello, IzPack");
  }
}