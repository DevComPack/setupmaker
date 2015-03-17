package com.dcp.sm.config.io.ant;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;


public class RunAntWriter extends AntWriter
{

    public RunAntWriter(String xml_file) throws XMLStreamException
    {
        super(xml_file, "DevComPack-run", "run");
    }
    
    /*
    <target name="run" description="launches izpack package" >
        <echo message="Launch package"/>
        <java jar="${dist}/package.jar" fork="true" failonerror="true" >
          <classpath>
            <pathelement location="${dist}/package.jar"/>
            <pathelement path="${java.class.path}"/>
          </classpath>
        </java>
        <echo message="Finished!"/>
    </target>
     */
    public void writeRunTarget(String jar_file) throws XMLStreamException {
        SMOutputElement target = addTarget("run", "launches izpack package");
        echo(target, "Launch package");
        SMOutputElement java_task = target.addElement("java");
        java_task.addAttribute("jar", jar_file);
        java_task.addAttribute("fork", "true");
        java_task.addAttribute("failonerror", "true");
        
        SMOutputElement classpath = java_task.addElement("classpath");
        SMOutputElement pathelement = classpath.addElement("pathelement");
        pathelement.addAttribute("location", jar_file);
        pathelement = classpath.addElement("pathelement");
        pathelement.addAttribute("path", "${java.class.path}");
        
        echo(target, "Finished!");
    }

}
