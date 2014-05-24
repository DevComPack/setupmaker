package dcp.config.io.xml;

import java.io.File;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * StaxMate parser test class
 */
public class StaxMateParser
{
    /**
     * Write example
     */
    public static void writeXML() {
        WstxOutputFactory factory = new WstxOutputFactory();
        factory.setProperty(WstxOutputFactory.P_AUTOMATIC_EMPTY_ELEMENTS, true);
        // 1: need output factory
        SMOutputFactory outf = new SMOutputFactory(factory);
        SMOutputDocument doc;
        try
        {
            doc = outf.createOutputDocument(new File("src/config/xml/parse/test.xml"));
            // (optional) 3: enable indentation (note spaces after backslash!)
            doc.setIndentation("\n\t\t\t\t\t", 1, 1);
            // 4. comment regarding generation time
            doc.addComment(" generated: " + new java.util.Date().toString());
            SMOutputElement empl = doc.addElement("employee");
            empl.addAttribute(/*namespace*/ null, "id", 123);
            SMOutputElement name = empl.addElement("name");
            name.addElement("first").addCharacters("Tatu");
            name.addElement("last").addCharacters("Saloranta");
            // 10. close the document to close elements, flush output
            doc.closeRoot();
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Read example
     */
    public static void readXML() {
        WstxInputFactory factory = new WstxInputFactory();
        // 1: need input factory
        SMInputFactory inf = new SMInputFactory(factory);
        // 2: and root cursor that reads XML document from File:
        SMHierarchicCursor rootC;
        try
        {
            rootC = inf.rootElementCursor(new File("src/config/xml/parse/test.xml"));
            rootC.advance(); // note: 2.0 only method; can also call ".getNext()"
            int employeeId = rootC.getAttrIntValue(0);
                System.out.println("Employee id: " + employeeId);
            SMHierarchicCursor nameC = (SMHierarchicCursor) rootC.childElementCursor("name").advance();
            SMHierarchicCursor leafC = (SMHierarchicCursor) nameC.childElementCursor().advance();
            String first = leafC.collectDescendantText(false);
                System.out.println("Employee first name: " + first);
            leafC.advance();
            String last = leafC.collectDescendantText(false);
                System.out.println("Employee last name: " + last);
            rootC.getStreamReader().closeCompletely();
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Test function
     */
    public static void main(String[] args)
    {
        writeXML();//Write xml file
        readXML();//Read xml file
    }

}
