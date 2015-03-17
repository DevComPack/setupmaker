package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;


public class VariableWriter extends StaxMateWriter
{
    private SMOutputElement varRoot;

    public VariableWriter(String xml_file) throws XMLStreamException
    {
        super(xml_file);
        root = out.setRoot("xfragment");//<xfragment>
        root = root.addElement("variables");//<variables>
        varRoot = root;
    }
    
    public VariableWriter(SMOutputElement root) throws XMLStreamException {
        super(root);
        header();
        varRoot = root.addElement("variables");
    }
    
    public void header() throws XMLStreamException
    {
        addComment("The Variables section.");
    }
    
    /*
    <variables>
        <variable name="InstallerFrame.logfilePath" value="$INSTALL_PATH/Uninstaller/install.log" />
        <variable name="DesktopShortcutCheckboxEnabled" value="true" />
    </variables>
     */
    public void addVariable(String name, String value) throws XMLStreamException {
        SMOutputElement variable = varRoot.addElement("variable");
        variable.addAttribute("name", name);
        variable.addAttribute("value", value);
    }
    
}
