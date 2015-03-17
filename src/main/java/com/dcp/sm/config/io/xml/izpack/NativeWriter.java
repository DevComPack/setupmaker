package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;


public class NativeWriter extends StaxMateWriter
{

    public NativeWriter(SMOutputElement root) throws XMLStreamException
    {
        super(root);
        header();
    }
    
    public void header() throws XMLStreamException {
        addComment("The Natives section. We specify here our dll files and libraries.");
    }
    
    /*
    <native type="izpack" name="ShellLink.dll">
        <os family="windows"/>
    </native>
     */
    public void addNative(String type, String name) throws XMLStreamException {
        SMOutputElement x_native = root.addElement("native");
        x_native.addAttribute("type", type);
        x_native.addAttribute("name", name);
        SMOutputElement x_os = x_native.addElement("os");
        x_os.addAttribute("family", "windows");
    }
    
    /*
    <jar src="lib/dcp/dcp-executable.jar" stage="both"/>
     */
    public void addJar(String src) throws XMLStreamException {
        SMOutputElement x_jar = root.addElement("jar");
        x_jar.addAttribute("src", src);
        x_jar.addAttribute("stage", "both");
    }

}
