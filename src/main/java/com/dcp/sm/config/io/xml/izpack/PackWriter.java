package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.IOFactory;
import com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE;
import com.dcp.sm.config.io.xml.StaxMateWriter;
import com.dcp.sm.logic.factory.PackFactory;
import com.dcp.sm.logic.factory.TypeFactory.CONDITION;
import com.dcp.sm.logic.model.Group;
import com.dcp.sm.logic.model.Pack;
import com.dcp.sm.logic.factory.TypeFactory.INSTALL_TYPE;


public class PackWriter extends StaxMateWriter
{
    private SMOutputElement packRoot;
    
    public PackWriter(String xml_file)
    {
        super(xml_file);
        root = out.setRoot("xfragment");//<xfragment>
        packRoot = root;
    }
    
    public PackWriter(SMOutputElement root) throws XMLStreamException {
        super(root);
        header();
        packRoot = root.addElement("packs");
    }
    
    @Override
    public void include(String href) throws XMLStreamException
    {
        packRoot.addElement("xi:include").addAttribute("href", href);
    }
    
    public void header() throws XMLStreamException
    {
        addComment("The packs section. We specify here our packs.");
    }
    
    //Write GROUP Data
    public boolean addGroup(Group GROUP) throws XMLStreamException {
        SMOutputElement pack = packRoot.addElement("pack");//<pack 
        pack.addAttribute("name", GROUP.getName());//name="Group_Name"
        pack.addAttribute("required", "no");//required="no"
        if (GROUP.getParent() != null)
            pack.addAttribute("parent", GROUP.getParent().getName());//parent="Parent_name"
        if (!GROUP.getInstallGroups().equals(""))
            pack.addAttribute("installGroups", GROUP.getInstallGroups());//installGroups="tools,dev"
        
        pack.addElementWithCharacters(null, "description", GROUP.getDescription());//<description></description>
        
        return true;
    }

    //Write PACK Data
    /*
     <pack name="core" required="yes">
        <description>Uninstall files</description>
        <file src="res/bat/clean.bat" targetdir="$INSTALL_PATH/Uninstaller" override="true" />
        <!-- <file src="res/utils/Elevate.exe" targetdir="$INSTALL_PATH/Uninstaller" override="true" /> -->
     </pack>
     */
    public boolean addCorePack(boolean verCheck) throws XMLStreamException {
        SMOutputElement pack = packRoot.addElement("pack");//<pack 
        pack.addAttribute("name", "core");//name="core"
        pack.addAttribute("required", "yes");//required="yes">

        pack.addElementWithCharacters(null, "description", "Uninstall files");//<description></description>
        
        if (verCheck) {//If Registry check is enabled
            SMOutputElement clean_bat = pack.addElement("file");//<file 
            clean_bat.addAttribute("src", IOFactory.batPath+"/clean.bat");//src="res/bat/clean.bat"
            clean_bat.addAttribute("targetdir", "$INSTALL_PATH/Uninstaller");//targetDir="$INSTALL_PATH/Uninstaller"
            clean_bat.addAttribute("override", "true");//override="true"

            SMOutputElement elevate_exe = pack.addElement("file");//<file 
            elevate_exe.addAttribute("src", IOFactory.resPath+"/utils/Elevate.exe");//src="res/utils/Elevate.exe"
            elevate_exe.addAttribute("targetdir", "$INSTALL_PATH/Uninstaller");//targetDir="$INSTALL_PATH/Uninstaller"
            elevate_exe.addAttribute("override", "true");//override="true"
        }
        
        return true;
    }
    
    //Write PACK Data
    public boolean addPack(Pack pack) throws XMLStreamException {
        SMOutputElement xmlPack = packRoot.addElement("pack");//<pack 
        xmlPack.addAttribute("name", pack.getInstallName());//name="Pack_Name"
        xmlPack.addAttribute("required", (pack.isRequired())?"yes":"no");//required="yes/no"
        xmlPack.addAttribute("preselected", (pack.isSelected())?"yes":"no");//preselected="yes/no"
        xmlPack.addAttribute("hidden", (pack.isHidden())?"true":"false");//hidden="true/false"
        switch(pack.getInstallOs()) {//os="windows/unix/mac"
        case WINDOWS:
            xmlPack.addAttribute("os", "windows");
            break;
        case LINUX:
            xmlPack.addAttribute("os", "unix");
            break;
        case MAC:
            xmlPack.addAttribute("os", "mac");
            break;
        default:
            break;
        }
        
        if (pack.getArch() > 0)
            xmlPack.addAttribute("condition", (pack.getArch() == 64)? CONDITION.ARCH64.toString() : CONDITION.ARCH32.toString());
        
        if (pack.getGroup() != null) {
            xmlPack.addAttribute("parent", pack.getGroupName());//parent="Group_name"
        }
        
        if (!pack.getInstallGroups().equals(""))
            xmlPack.addAttribute("installGroups", pack.getInstallGroups());//installGroups="tools,dev"
        
        xmlPack.addElementWithCharacters(null, "description", pack.getDescription());//<description></description>
        
        if (pack.getFileType() ==  FILE_TYPE.Folder && pack.getInstallType() == INSTALL_TYPE.EXTRACT) {//Folder unzip
            SMOutputElement pack_file = xmlPack.addElement("fileset");//<fileset 
            pack_file.addAttribute("dir", pack.getPath());//dir="path/to/file"
            pack_file.addAttribute("targetdir", "$INSTALL_PATH/"+pack.getInstallPath());//targetDir="$INSTALL_PATH/Group"
            if (pack.isOverride())//override="asktrue"
                pack_file.addAttribute("override", "true");
            else pack_file.addAttribute("override", "false");
        }
        else {//Normal
            SMOutputElement pack_file = xmlPack.addElement("file");//<file 
            pack_file.addAttribute("src", pack.getPath());//src="path/to/file"
            pack_file.addAttribute("targetdir", "$INSTALL_PATH/"+pack.getInstallPath());//targetDir="$INSTALL_PATH/Group"
            if (pack.isOverride())//override="asktrue"
                pack_file.addAttribute("override", "true");
            else pack_file.addAttribute("override", "false");
            if (pack.getFileType() ==  FILE_TYPE.Archive && pack.getInstallType() == INSTALL_TYPE.EXTRACT)//unpack="true"
                pack_file.addAttribute("unpack", "true");
        }
        
        if (pack.getGroupDependency() != null) {// Group Dependencies
            Group G = pack.getGroupDependency();
            SMOutputElement group_dep = xmlPack.addElement("depends");//<depends 
            group_dep.addAttribute("packname", G.getName());//packName="Dependency_Group"
            for(Pack P:PackFactory.getPacks()) {
                if (P.getGroupName().equals(G.getName()) && P.getGroupPath().equals(G.getPath())) {
                    SMOutputElement pack_dep = xmlPack.addElement("depends");//<depends 
                    pack_dep.addAttribute("packname", P.getInstallName());//packName="Dependency_Pack"
                    if (pack.getInstallType().equals(INSTALL_TYPE.EXECUTE)) {///Copy dependent files to tmp directory if executable
                        SMOutputElement pack_file = xmlPack.addElement("file");//<file 
                        pack_file.addAttribute("src", P.getPath());//src="path/to/file"
                        pack_file.addAttribute("targetdir", "$INSTALL_PATH/"+pack.getInstallPath());//targetDir="$INSTALL_PATH/tmp/Group"
                        pack_file.addAttribute("override", "true");//override="asktrue"
                    }
                }
            }
        }
        else if(pack.getPackDependency() != null) {// Pack Dependency
            Pack P = pack.getPackDependency();
            SMOutputElement pack_dep = xmlPack.addElement("depends");//<depends 
            pack_dep.addAttribute("packname", P.getInstallName());//packName="Dependency_Pack"
            if (pack.getInstallType().equals(INSTALL_TYPE.EXECUTE)) {///Copy dependent file to tmp directory if executable
                SMOutputElement pack_file = xmlPack.addElement("file");//<file 
                pack_file.addAttribute("src", P.getPath());//src="path/to/file"
                pack_file.addAttribute("targetdir", "$INSTALL_PATH/"+pack.getInstallPath());//targetDir="$INSTALL_PATH/tmp/Group"
                pack_file.addAttribute("override", "true");//override="asktrue"
            }
        }
        
        return true;
    }
    
}
