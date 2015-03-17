package com.dcp.sm.config.io.ant;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;


public class DebugAntWriter extends AntWriter
{

    public DebugAntWriter(String xml_file) throws XMLStreamException
    {
        super(xml_file, "DevComPack-debug", "debug");
    }
    
    /*
    <target name="debug" description="launches izpack package with trace mode enabled" >
        <echo message="Debug package"/>
        <exec executable="java" failonerror="false">
            <arg line="-DTRACE=TRUE"/>
            <arg line="-jar ${dist}/package.jar"/>
        </exec>
        <echo message="Finished!"/>
    </target>
     */
    public void writeDebugTarget(String jar_file) throws XMLStreamException {
        SMOutputElement target = addTarget("debug", "launches izpack package with trace mode enabled");
        echo(target, "Debug package");
        SMOutputElement exec_task = target.addElement("exec");
        exec_task.addAttribute("executable", "java");
        exec_task.addAttribute("failonerror", "false");
        
        SMOutputElement arg = exec_task.addElement("arg");
        arg.addAttribute("line", "-DTRACE=TRUE");
        arg = exec_task.addElement("arg");
        arg.addAttribute("line", "-jar \""+jar_file+"\"");
        
        echo(target, "Finished!");
    }
    
}
