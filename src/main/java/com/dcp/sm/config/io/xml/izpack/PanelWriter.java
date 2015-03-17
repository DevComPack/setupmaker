package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;


public class PanelWriter extends StaxMateWriter
{
    private SMOutputElement panelRoot;
    
    public PanelWriter(String xml_file) throws XMLStreamException
    {
        super(xml_file);
        root = out.setRoot("xfragment");//<xfragment>
        root = root.addElement("panels");//<panels>
        panelRoot = root;
    }
    
    public PanelWriter(SMOutputElement root) throws XMLStreamException {
        super(root);
        header();
        panelRoot = root.addElement("panels");
    }
    
    public void header() throws XMLStreamException
    {
        addComment("The panels section. We indicate here which panels we want to use. The order will be respected.");
    }
    
    public SMOutputElement writePanel(String className) throws XMLStreamException {
        SMOutputElement panel = panelRoot.addElement("panel");//<panel 
        panel.addAttribute("classname", className);//classname="InstallPanel"
        return panel;
    }
}
