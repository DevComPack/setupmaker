package dcp.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import dcp.config.io.xml.StaxMateWriter;


public class ConditionWriter extends StaxMateWriter
{
    private SMOutputElement condRoot;

    public ConditionWriter(String xml_file)
    {
        super(xml_file);
        root = out.setRoot("conditions");//<xfragment>
        condRoot = root;
    }
    
    public ConditionWriter(SMOutputElement root) throws XMLStreamException {
        super(root);
        header();
        condRoot = root;
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
    public boolean addCondition(String className, String field) throws XMLStreamException {
        SMOutputElement condition = condRoot.addElement("condition");//<condition
        condition.addAttribute("type", "java");
        condition.addAttribute("id", "platform32");
        
        SMOutputElement xJava = condition.addElement("java");//<java>
        xJava.addElementWithCharacters(null, "class", className);//<class></class>
        xJava.addElementWithCharacters(null, "field", field);//<field></field>
        
        SMOutputElement xReturnvalue = condition.addElementWithCharacters(null, "returnvalue", "true");//<returnvalue>
        xReturnvalue.addAttribute("type", "boolean");
        
        
        //</conditions>
        
        return true;
    }

}
