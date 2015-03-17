package com.dcp.sm.config.io.xml;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;


public abstract class StaxMateWriter
{
    protected StaxMateFactory out;
    protected SMOutputElement root;
    public SMOutputElement getRoot() { return root; }
    
    protected String xml_file;
    public String getTargetFile() { return xml_file; }
    
    public StaxMateWriter(String xml_file)
    {
        this.xml_file = xml_file;
        if (xml_file != null && !xml_file.equals(""))
            out = new StaxMateFactory(xml_file);
    }
    
    public StaxMateWriter(SMOutputElement root)
    {
        this.xml_file = "";
        this.root = root;
    }
    
    public void close() { out.close(); }
    
    public void addComment(String comment) throws XMLStreamException {
        root.addComment(comment);
    }
    
    //<xi:include href="res/xml/PacksPanelSpec.xml" />
    public void include(String href) throws XMLStreamException {
        root.addElement("xi:include").addAttribute("href", href);
    }
}
