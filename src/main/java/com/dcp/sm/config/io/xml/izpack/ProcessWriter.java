package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;
import com.dcp.sm.logic.model.Pack;


public class ProcessWriter extends StaxMateWriter
{
    /*
        <processing version="1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <logfiledir>$INSTALL_PATH</logfiledir> 
     */
    public ProcessWriter(String xml_file) throws XMLStreamException
    {
        super(xml_file);
        root = out.setRoot("processing");//<processing
        root.addAttribute("version", "1.0");//version="1.0"
        root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");//xsi namespace
        
        root.addElementWithCharacters(null, "logfiledir", "$INSTALL_PATH");//<logfiledir></>
    }
    
    /*
    <onFail previous="true" next="false" />
    <onSuccess previous="false" next="true" />
     */
    @Override
    public void close() {
        try
        {
            SMOutputElement onFail = root.addElement("onFail");//<onFail
            onFail.addAttribute("previous", "true");
            onFail.addAttribute("next", "false");
            SMOutputElement onSuccess = root.addElement("onSuccess");//<onSuccess
            onSuccess.addAttribute("previous", "false");
            onSuccess.addAttribute("next", "true");
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
        super.close();
    }
    
    /*
    <job name="Putty">
        <executeForPack name="Putty"/>
        <os family="windows" />
        <executefile name="$INSTALL_PATH/tmp/putty.exe" />
    </job>
     */
    /**
     * Add process job for an executable pack
     * @param pack
     * @throws XMLStreamException
     */
    public void addPackJob(Pack pack) throws XMLStreamException {
        SMOutputElement job = root.addElement("job");//<job
        job.addAttribute("name", pack.getName());//name=""
        
        SMOutputElement executeForPack = job.addElement("executeForPack");//<executeForPack
        executeForPack.addAttribute("name", pack.getInstallName());//name=""
        
        SMOutputElement os;
        switch (pack.getInstallOs()) {
        case WINDOWS:
            os = job.addElement("os");//<os
            os.addAttribute("family", "windows");//family="windows"
            break;
        case LINUX:
            os = job.addElement("os");//<os
            os.addAttribute("family", "unix");//family="unix"
            break;
        case MAC:
            os = job.addElement("os");//<os
            os.addAttribute("family", "mac");//family="mac"
            break;
        default://All platforms
            break;
        }
        
        SMOutputElement executefile = job.addElement("executefile");//<executefile
        executefile.addAttribute("name", "$INSTALL_PATH/" + pack.getInstallPath() + "/" + pack.getName() );//name=""
    }
    
    /*
    <job name="Putty">
        <executeForPack name="Putty"/>
        <os family="windows" />
        <executefile name="$INSTALL_PATH/tmp/putty.exe">
            <env>DEP=$INSTALL_PATH/tmp</env>
        </executefile>
    </job>
     */
    /**
     * Add process job for a batch or shell file
     * @param pack
     * @param env
     * @throws XMLStreamException
     */
    public void addScriptJob(Pack pack, String env) throws XMLStreamException {
        SMOutputElement job = root.addElement("job");//<job
        job.addAttribute("name", pack.getName()+".script");//name=""
        
        SMOutputElement executeForPack = job.addElement("executeForPack");//<executeForPack
        executeForPack.addAttribute("name", pack.getInstallName());//name=""
        
        SMOutputElement os;
        switch (pack.getInstallOs()) {
        case WINDOWS:
            os = job.addElement("os");//<os
            os.addAttribute("family", "windows");//family="windows"
            break;
        case LINUX:
            os = job.addElement("os");//<os
            os.addAttribute("family", "unix");//family="unix"
            break;
        case MAC:
            os = job.addElement("os");//<os
            os.addAttribute("family", "mac");//family="mac"
            break;
        default://All platforms
            break;
        }
        
        SMOutputElement executefile = job.addElement("executefile");//<executefile
        executefile.addAttribute("name", "$INSTALL_PATH/" + pack.getInstallPath() + "/" + pack.getBaseName()+".bat" );//name=""
        executefile.addElementWithCharacters(null, "env", "INSTALL_PATH=$INSTALL_PATH\\");
        executefile.addElementWithCharacters(null, "env", env);
    }
    
    /*
     <job name="test">
        <executeclass name="main.resources.Executable">
            <arg>argument_titre_1</arg>
            <arg>argument_description_2</arg>
        </executeclass>
     </job>
     */
    /**
     * Add process job for a java executable class
     * @param pack
     * @param className
     * @param args
     * @throws XMLStreamException
     */
    public void addClassJob(Pack pack, String className, String[] args) throws XMLStreamException {
        SMOutputElement job = root.addElement("job");//<job
        job.addAttribute("name", pack.getName()+".class");//name=""
        
        SMOutputElement executeForPack = job.addElement("executeForPack");//<executeForPack
        executeForPack.addAttribute("name", pack.getInstallName());//name=""
        
        SMOutputElement os;
        switch (pack.getInstallOs()) {
        case WINDOWS:
            os = job.addElement("os");//<os
            os.addAttribute("family", "windows");//family="windows"
            break;
        case LINUX:
            os = job.addElement("os");//<os
            os.addAttribute("family", "unix");//family="unix"
            break;
        case MAC:
            os = job.addElement("os");//<os
            os.addAttribute("family", "mac");//family="mac"
            break;
        default://All platforms
            break;
        }
        
        SMOutputElement executeclass = job.addElement("executeclass");//<executeclass
        executeclass.addAttribute("name", className);//name=""
        
        for(String s:args)
            executeclass.addElementWithCharacters(null, "arg", s);//<arg></>
    }
    
    /**
     * Final clean job to remove jobs temporary files
     * @throws XMLStreamException
     * @param tmpDir: directory to clean
     */
    public void addCleanJob(String tmpDir) throws XMLStreamException
    {
        SMOutputElement job = root.addElement("job");//<job
        job.addAttribute("name", "installExeClean"+".dcp");//name=""
        
        SMOutputElement executeclass = job.addElement("executeclass");//<executeclass
        executeclass.addAttribute("name", "com.izforge.izpack.resources.InstallCleaner");//name=""
        
        executeclass.addElementWithCharacters(null, "arg", tmpDir);//<arg></>
    }
}
