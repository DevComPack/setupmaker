package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;


public class ResourceWriter extends StaxMateWriter
{
    private SMOutputElement resRoot;
    
    public ResourceWriter(String xml_file) throws XMLStreamException
    {
        super(xml_file);
        root = out.setRoot("xfragment");//<xfragment>
        root = root.addElement("resources");//<resources>
        resRoot = root;
    }
    
    public ResourceWriter(SMOutputElement root) throws XMLStreamException {
        super(root);
        header();
        resRoot = root.addElement("resources");
    }
    
    public void header() throws XMLStreamException
    {
        addComment("The resources section. The ids must be these ones if you want to use the LicencePanel and/or the InfoPanel.");
    }
    
    //<res id="installer.langsel.img" src="res/img/logo.jpg"/>
    public void addResource(String id, String src) throws XMLStreamException {
        SMOutputElement x_res = resRoot.addElement("res");//<res
        x_res.addAttribute("id", id);//id=""
        x_res.addAttribute("src", src);//src=""
    }
}
