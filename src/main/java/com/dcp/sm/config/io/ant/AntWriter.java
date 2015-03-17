package com.dcp.sm.config.io.ant;

import java.io.File;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;


public class AntWriter extends StaxMateWriter
{
    protected String build_file;
    
    //<project name="DevComPack-izpack" default="izpack-standalone" basedir=".">
    public AntWriter(String xml_file, String name, String default_target) throws XMLStreamException
    {
        super(xml_file);
        build_file = super.xml_file;
        root = out.setRoot("project");//<project 
        root.addAttribute("name", name);
        root.addAttribute("default", default_target);
        root.addAttribute("basedir", new File(".").getAbsolutePath());
    }
    
    //<property name="dist"  location="${basedir}/target"/>
    public void addProperty(String name, String location) throws XMLStreamException {
        SMOutputElement property = root.addElement("property");
        property.addAttribute("name", name);
        property.addAttribute("location", location);
    }
    
    /*taskdef name="IZPACK" classpath="${basedir}/lib/izpack/standalone-compiler.jar"
             classname="com.izforge.izpack.ant.IzPackTask"/>*/
    public void addTask(String name, String classpath, String classname) throws XMLStreamException {
        SMOutputElement taskdef = root.addElement("taskdef");
        taskdef.addAttribute("name", name);
        taskdef.addAttribute("classpath", classpath);
        taskdef.addAttribute("classname", classname);
    }
    
    /*<target name="izpack-standalone" description="izpack compile" />*/
    public SMOutputElement addTarget(String name, String description) throws XMLStreamException {
        SMOutputElement target = root.addElement("target");
        target.addAttribute("name", name);
        target.addAttribute("description", description);
        return target;
    }
    
    //<echo message="Finished!"/>
    protected SMOutputElement echo(SMOutputElement element, String message) throws XMLStreamException {
        SMOutputElement ECHO = element.addElement("echo");
        ECHO.addAttribute("message", message);
        return ECHO;
    }
    
}
