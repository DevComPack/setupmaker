package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;


public class ConditionWriter extends StaxMateWriter
{
    private SMOutputElement condRoot;

    public ConditionWriter(String xml_file) throws XMLStreamException
    {
        super(xml_file);
        root = out.setRoot("xfragment");//<xfragment>
        root = root.addElement("conditions");//<conditions>
        condRoot = root;
    }
    
    public ConditionWriter(SMOutputElement root) throws XMLStreamException {
        super(root);
        header();
        condRoot = root.addElement("conditions");
    }
    
    public void header() throws XMLStreamException
    {
        addComment("The Conditions section. We declare conditions used by packs.");
    }
    
    /**
     * Setup Conditions Data (32/64 bits platform)
     * @return boolean
     * @throws XMLStreamException
     *  <conditions>
           <condition type="java" id="installonwindows">
                 <java>
                         <class>com.izforge.izpack.util.OsVersion</class>
                         <field>IS_WINDOWS</field>
                 </java>
                 <returnvalue type="boolean">true</returnvalue>
           </condition>
     *  </conditions>
     */
    public boolean addCondition(String id, String className, String field) throws XMLStreamException {
        SMOutputElement condition = condRoot.addElement("condition");//<condition
        condition.addAttribute("type", "java");
        condition.addAttribute("id", id);
        
        SMOutputElement xJava = condition.addElement("java");//<java>
        xJava.addElementWithCharacters(null, "class", className);//<class></class>
        xJava.addElementWithCharacters(null, "field", field);//<field></field>
        
        SMOutputElement xReturnvalue = condition.addElement("returnvalue");//<returnvalue>
        xReturnvalue.addAttribute("type", "boolean");
        xReturnvalue.addCharacters("true");
        
        //</conditions>
        
        return true;
    }

}
