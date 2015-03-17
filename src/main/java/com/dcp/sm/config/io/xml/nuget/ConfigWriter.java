package com.dcp.sm.config.io.xml.nuget;

import javax.xml.stream.XMLStreamException;

import org.apache.pivot.collections.List;
import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;
import com.dcp.sm.logic.model.Pack;

/*
    <packages>
      <package id="apackage" />
      <package id="anotherPackage" version="1.1" />
      <package id="chocolateytestpackage" version="0.1" source="somelocation" />
    </packages>
 */
public class ConfigWriter extends StaxMateWriter
{
    private String source;
    
    /*
     * <package xmlns="http://schemas.microsoft.com/packaging/2010/07/nuspec.xsd">
     */
    public ConfigWriter(String xml_file, String feedUrl) throws XMLStreamException
    {
        super(xml_file);
        this.source = feedUrl;
        root = out.setRoot("packages");
    }
    
    /**
     * write packs into packages config file
     * @param packs: list of all packs to include for install
     * @throws XMLStreamException
     */
    public void writePackages(List<Pack> packs) throws XMLStreamException
    {
        for (Pack p:packs) {
            if (p.isHidden()) continue; // don't include hidden packs into config file
            SMOutputElement xmlPackage = root.addElement("package");
            xmlPackage.addAttribute("id", p.getInstallName());
            xmlPackage.addAttribute("version", p.getInstallVersion());
            xmlPackage.addAttribute("source", this.source);
        }
    }

}
