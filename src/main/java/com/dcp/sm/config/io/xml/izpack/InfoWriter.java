package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;






import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;
import com.dcp.sm.logic.model.config.SetupConfig;
import com.dcp.sm.logic.model.config.build.IzpackConfig;


public class InfoWriter extends StaxMateWriter
{
    private SMOutputElement infoRoot;
    
    public InfoWriter(String xml_file)
    {
        super(xml_file);
        root = out.setRoot("xfragment");//<xfragment>
        infoRoot = root;
    }
    
    
    public InfoWriter(SMOutputElement root) throws XMLStreamException
    {
        super(root);
        header();
        infoRoot = root;
    }
    
    public void header() throws XMLStreamException
    {
        addComment("The info section. The meaning of the tags should be natural ...");
    }
    
    //Setup File Info
    public void setInfo(SetupConfig setupConfig, IzpackConfig izpackConfig) throws XMLStreamException
    {
        SMOutputElement x_info = infoRoot.addElement("info");//<info>
        
        x_info.addElementWithCharacters(null, "appname", setupConfig.getAppName());//<appname></>
        x_info.addElementWithCharacters(null, "appversion", setupConfig.getAppVersion());//<appversion></>
        
        SMOutputElement authors = x_info.addElement("authors");//<authors>
        if (!setupConfig.getAuthorName().equals("") || !setupConfig.getAuthorEmail().equals("")) {

            String[] authorsNames = setupConfig.getAuthorName().split(",");
            String[] authorsEmails = setupConfig.getAuthorEmail().split(",");

            for(int i = 0; i < authorsNames.length; i++) {
                SMOutputElement author = authors.addElement("author");//<author>
                author.addAttribute("name", authorsNames[i].trim());//name = ""
                if (i < authorsEmails.length) author.addAttribute("email", authorsEmails[i].trim());//email = ""
                else author.addAttribute("email", "");//email = ""
            }
        }

        if (!setupConfig.getAppURL().equals(""))
            x_info.addElementWithCharacters(null, "url", setupConfig.getAppURL());//<appurl></>
        
        if (izpackConfig.isWebSetup())
            x_info.addElementWithCharacters(null, "webdir", izpackConfig.getWebUrl());//<webdir></>
        
        //root.addElement("pack200");//<pack200 />
        
        //<run-privileged condition="izpack.windowsinstall.vista|izpack.windowsinstall.7"/>
        SMOutputElement run_privileged = x_info.addElement("run-privileged");
        run_privileged.addAttribute("condition", "izpack.windowsinstall.vista|izpack.windowsinstall.7");
        
        //<summarylogfilepath>$INSTALL_PATH/Uninstaller/installSummary.htm</summarylogfilepath>
        x_info.addElementWithCharacters(null, "summarylogfilepath",
                                        "$INSTALL_PATH/Uninstaller/installSummary.htm");
    }
    
}
