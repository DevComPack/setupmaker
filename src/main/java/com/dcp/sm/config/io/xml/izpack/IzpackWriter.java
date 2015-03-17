package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import com.dcp.sm.config.io.xml.StaxMateWriter;


public class IzpackWriter extends StaxMateWriter
{
    
    /*
    <installation version="1.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:xi="http://www.w3.org/2001/XInclude">
     */
    public IzpackWriter(String xml_file) throws XMLStreamException
    {
        super(xml_file);
        root = out.setRoot("installation");
        root.addAttribute("version", "1.0");
        root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.addAttribute("xmlns:xi", "http://www.w3.org/2001/XInclude");
    }

}
