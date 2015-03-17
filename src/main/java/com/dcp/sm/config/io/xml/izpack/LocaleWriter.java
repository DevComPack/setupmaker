package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;


public class LocaleWriter extends StaxMateWriter
{
    private SMOutputElement localeRoot;
    
    /*
    <locale>
        <langpack iso3="fra"/>
        <langpack iso3="eng"/>
    </locale>
     */
    public LocaleWriter(String xml_file)
    {
        super(xml_file);
        root = out.setRoot("locale");
        localeRoot = root;
    }
    
    public LocaleWriter(SMOutputElement root) throws XMLStreamException {
        super(root);
        header();
        localeRoot = root.addElement("locale");
    }
    
    public void header() throws XMLStreamException
    {
        addComment("The locale section. Asks here to include the English and French langpacks.");
    }
    
    public void addLangpack(String iso3) throws XMLStreamException {
        localeRoot.addElement("langpack").addAttribute("iso3", iso3);
    }

}
