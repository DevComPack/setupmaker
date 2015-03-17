package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;


public class ListenerWriter extends StaxMateWriter
{
    private SMOutputElement listenerRoot;
    
    public ListenerWriter(String xml_file)
    {
        super(xml_file);
        root = out.setRoot("listeners");//<listeners>
        listenerRoot = root;
    }
    
    public ListenerWriter(SMOutputElement root) throws XMLStreamException {
        super(root);
        header();
        listenerRoot = root.addElement("listeners");
    }
    
    public void header() throws XMLStreamException
    {
        addComment("The listeners section for CustomActions");
    }

    /*
    <listener installer="RegistryInstallerListener" uninstaller="RegistryUninstallerListener">
        <os family="windows"/>
    </listener>
     */
    public void addListener(String installer, String uninstaller, boolean windows) throws XMLStreamException {
        SMOutputElement listener = listenerRoot.addElement("listener");
        listener.addAttribute("installer", installer);
        if (!uninstaller.equals(""))
            listener.addAttribute("uninstaller", uninstaller);
        if (windows) listener.addElement("os").addAttribute("family", "windows");
    }
    
}
