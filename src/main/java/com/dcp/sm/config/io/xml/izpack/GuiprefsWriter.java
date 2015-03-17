package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;





import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;
import com.dcp.sm.logic.model.config.SetupConfig;


public class GuiprefsWriter extends StaxMateWriter
{
    private SMOutputElement guiRoot;
    
    public GuiprefsWriter(String xml_file)
    {
        super(xml_file);
        root = out.setRoot("xfragment");//<xfragment>
        guiRoot = root;
    }
    public GuiprefsWriter(SMOutputElement root) throws XMLStreamException
    {
        super(root);
        header();
        guiRoot = root.addElement("guiprefs");//<guiprefs>
    }
    
    public void header() throws XMLStreamException
    {
        addComment("The gui preferences indication.");
    }
    
    //Setup GUI Preferences
    public void setPrefs(SetupConfig setupConfig) throws XMLStreamException
    {
        guiRoot.addAttribute("width", String.valueOf(setupConfig.getAppWidth()));
        guiRoot.addAttribute("height", String.valueOf(setupConfig.getAppHeight()));
        if (setupConfig.isResizable())
            guiRoot.addAttribute("resizable", "yes");
        else
            guiRoot.addAttribute("resizable", "no");
        
        /*
        <laf name="looks">
            <os family="windows" />
            <param name="variant" value="windows" />
        </laf>
        <laf name="metouia">
            <os family="unix" />
        </laf>
         */
        SMOutputElement laf = guiRoot.addElement("laf");
        laf.addAttribute("name", "looks");
        laf.addElement("os").addAttribute("family", "windows");
        SMOutputElement param = laf.addElement("param");
        param.addAttribute("name", "variant");
        param.addAttribute("value", "windows");
        
        laf = guiRoot.addElement("laf");
        laf.addAttribute("name", "metouia");
        laf.addElement("os").addAttribute("family", "unix");
    }
    
    //<modifier key="langDisplayType" value="native"/>
    public void addModifier(String key, String value) throws XMLStreamException {
        SMOutputElement modifier = guiRoot.addElement("modifier");
        modifier.addAttribute("key", key);
        modifier.addAttribute("value", value);
    }
    
}
