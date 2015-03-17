package com.dcp.sm.config.io.xml.nuget;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;
import com.dcp.sm.logic.factory.PackFactory;
import com.dcp.sm.logic.model.Group;
import com.dcp.sm.logic.model.Pack;
import com.dcp.sm.logic.model.config.SetupConfig;

/*
<package xmlns="http://schemas.microsoft.com/packaging/2010/07/nuspec.xsd">
    <metadata>
        <id>maven</id>
        <version>2.1.0</version>
        <title>Maven</title>
        <authors>SSAIDELI</authors>
        <owners>SSAIDELI</owners>
        <requireLicenseAcceptance>false</requireLicenseAcceptance>
        <description>Apache Maven 2.1.0</description>
        <tags>Boxstarter</tags>
        <dependencies>
            <dependency id="jQuery" version="2.1.1" />
        </dependencies>
    </metadata>
</package>
 */
/**
 * 
 * @author SSAIDELI
 *
 */
public class SpecWriter extends StaxMateWriter
{
    
    /*
     * <package xmlns="http://schemas.microsoft.com/packaging/2010/07/nuspec.xsd">
     */
    public SpecWriter(String xml_file) throws XMLStreamException
    {
        super(xml_file);
        root = out.setRoot("package");
        root.addAttribute("xmlns", "http://schemas.microsoft.com/packaging/2010/07/nuspec.xsd");
    }
    
    /**
     * Spec file metadata for setup package
     * @param setupConfig
     * @throws XMLStreamException
     */
    public void writeMetadata(SetupConfig setupConfig) throws XMLStreamException
    {
        SMOutputElement metadata = root.addElement("metadata");
        metadata.addElementWithCharacters(null, "id", setupConfig.getAppName().replaceAll(" ", ""));
        metadata.addElementWithCharacters(null, "version", setupConfig.getAppVersion());
        metadata.addElementWithCharacters(null, "title", setupConfig.getAppName());
        metadata.addElementWithCharacters(null, "authors", setupConfig.getAuthorName());
        metadata.addElementWithCharacters(null, "owners", "Capgemini, SNCF");
        metadata.addElementWithCharacters(null, "requireLicenseAcceptance", "false");
        metadata.addElementWithCharacters(null, "description", setupConfig.getAppName() + " " + setupConfig.getAppVersion());
        metadata.addElementWithCharacters(null, "tags", setupConfig.getAppName());
    }
    
    private void writePackDependencyMetadata(Pack pack, SMOutputElement element) throws XMLStreamException {
        if (!pack.isHidden()) { // write only if Pack isn't hidden
            SMOutputElement dependency = element.addElement("dependency");
            dependency.addAttribute("id", pack.getInstallName());
            dependency.addAttribute("version", pack.getInstallVersion());
        }
    }
    
    /**
     * Spec file metadata for single pack
     * @param pack
     * @throws XMLStreamException
     */
    public void writeMetadata(Pack pack) throws XMLStreamException
    {
        SMOutputElement metadata = root.addElement("metadata");
        metadata.addElementWithCharacters(null, "id", pack.getInstallName().replaceAll(" ", ""));
        metadata.addElementWithCharacters(null, "version", pack.getInstallVersion());
        metadata.addElementWithCharacters(null, "title", pack.getInstallName());
        metadata.addElementWithCharacters(null, "authors", System.getenv("USERNAME"));
        metadata.addElementWithCharacters(null, "owners", System.getenv("USERNAME"));
        metadata.addElementWithCharacters(null, "requireLicenseAcceptance", "false");
        metadata.addElementWithCharacters(null, "description",
                pack.getInstallName()
                + " " + pack.getInstallVersion()
                + " - " + pack.getDescription());
        metadata.addElementWithCharacters(null, "tags", pack.getInstallName());
        
        // Dependencies
        SMOutputElement dependencies = metadata.addElement("dependencies");
        
        // Pack Dependency
        Pack depPack = pack.getPackDependency();
        Group depGroup = pack.getGroupDependency();
        if (depPack != null) {
            writePackDependencyMetadata(depPack, dependencies);
        }
        // Group Dependency (group of packs)
        else if (depGroup != null) {
            for(Pack p:PackFactory.getPacks()) {
                Group pGroup = p.getGroup();
                if (pGroup == null) continue;
                if (pGroup == depGroup || pGroup.hasParent(depGroup)) {
                    writePackDependencyMetadata(p, dependencies);
                }
            }
        }
    }

}
