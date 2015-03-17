package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;
import com.dcp.sm.logic.model.Pack;
import com.dcp.sm.logic.factory.TypeFactory.INSTALL_TYPE;

public class ShortcutWriter extends StaxMateWriter
{
    private boolean startmenu;
    private boolean desktop;

    public ShortcutWriter(String xml_file, boolean startmenu, boolean desktop) throws XMLStreamException
    {
        super(xml_file);
        root = out.setRoot("shortcuts");//<shortcuts>
        /*  <skipIfNotSupported />
            <defaultCurrentUser/>
            <programGroup defaultName="$APP_NAME" location="startMenu" />
        */
        root.addElement("skipIfNotSupported");
        root.addElement("defaultCurrentUser");
        SMOutputElement programGroup = root.addElement("programGroup");
        programGroup.addAttribute("defaultName", "$APP_NAME");
        programGroup.addAttribute("location", "startMenu");
        
        this.startmenu = startmenu;
        this.desktop = desktop;
    }
    

    /* <shortcut  name="$APP_NAME $APP_VER"
                  target="$INSTALL_PATH"
                  commandLine=""
                  workingDirectory="$INSTALL_PATH"
                  description="Shortcut to package folder"
                  initialState="normal"
                  programGroup="yes"
                  desktop="yes"
                  applications="no"
                  startMenu="no"
                  startup="yes"
                  iconFile="%SystemRoot%\system32\SHELL32.dll"
                  iconIndex="31">
            <createForPack name="core" />
        </shortcut>
     */
    public void addShortcut(String name, String target_path, String file_name, String command_line, boolean desktop, boolean startup) throws XMLStreamException {
        SMOutputElement shortcut = root.addElement("shortcut");
        shortcut.addAttribute("name", name);
        shortcut.addAttribute("target", target_path+file_name);
        shortcut.addAttribute("commandLine", command_line);
        shortcut.addAttribute("workingDirectory", target_path);
        shortcut.addAttribute("description", "");
        shortcut.addAttribute("initialState", "normal");
        shortcut.addAttribute("programGroup", (startmenu)?"yes":"no");
        shortcut.addAttribute("desktop", (desktop)?"yes":"no");
        shortcut.addAttribute("applications", "no");
        shortcut.addAttribute("startMenu", "no");
        shortcut.addAttribute("startup", (startup)?"yes":"no");
        
        if (name.equals("Uninstall")) {//Icon for uninstall shortcut
            shortcut.addAttribute("iconFile", "%SystemRoot%\\system32\\SHELL32.dll");
            shortcut.addAttribute("iconIndex", "31");
        }
        
        shortcut.addElement("createForPack").addAttribute("name", "core");//core default system pack
    }
    
    /**
     * Add shortcut from Pack data
     * @param pack
     * @param startup
     * @throws XMLStreamException
     */
    public void addPackShortcut(Pack pack, boolean startup) throws XMLStreamException {
        SMOutputElement shortcut = root.addElement("shortcut");
        shortcut.addAttribute("name", pack.getInstallName());

        String target;
        if (pack.getInstallType() == INSTALL_TYPE.EXTRACT)
            target = pack.getInstallPath();//Unzip
        else target = ((pack.getInstallPath().length()>0)?pack.getInstallPath() + "/":"") + pack.getName();//Copy
        shortcut.addAttribute("target", "$INSTALL_PATH"+(target.length()>0?"/"+target:"")+pack.getShortcutPath());

        shortcut.addAttribute("commandLine", "");
        shortcut.addAttribute("workingDirectory", "$INSTALL_PATH/"+pack.getInstallPath());
        shortcut.addAttribute("description", "");
        shortcut.addAttribute("initialState", "normal");
        shortcut.addAttribute("programGroup", (startmenu)?"yes":"no");
        shortcut.addAttribute("desktop", (desktop)?"yes":"no");
        shortcut.addAttribute("applications", "no");
        shortcut.addAttribute("startMenu", "no");
        shortcut.addAttribute("startup", (startup)?"yes":"no");

        if (pack != null)//Create for pack
            shortcut.addElement("createForPack").addAttribute("name", pack.getInstallName());
    }
    
}
