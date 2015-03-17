package com.dcp.sm.config.io.ant;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;


public class CompileAntWriter extends AntWriter
{
    private String taskName;

    public CompileAntWriter(String xml_file) throws XMLStreamException
    {
        super(xml_file, "DevComPack-izpack", "izpack-standalone");
    }
    
    //<taskdef name="IZPACK" classpath="${basedir}/lib/izpack/standalone-compiler.jar" classname="com.izforge.izpack.ant.IzPackTask"/>
    public void writeStandaloneTask(String name) throws XMLStreamException {
        taskName = name;
        addTask(name, "${basedir}/lib/izpack/standalone-compiler.jar", "com.izforge.izpack.ant.IzPackTask");
    }

    
    /*
    <target name="izpack-standalone" description="izpack compile" >
        <!-- We call IzPack -->
        <echo message="Makes the installer using IzPack standalone compiler"/>
        <IZPACK input="${basedir}/install.xml"
                output="${dist}/package.jar"
                installerType="standard"
                basedir="${basedir}"/>
        <echo message="Finished!"/>
    </target>
     */
    public void writeIzpackTarget(String install_file, String target_file, boolean web) throws XMLStreamException {
        SMOutputElement target = addTarget("izpack-standalone", "izpack compile");
        SMOutputElement izpack = target.addElement(taskName);
        izpack.addAttribute("input", "${basedir}/"+install_file);
        izpack.addAttribute("output", target_file);
        izpack.addAttribute("installerType", (web==true)?"web":"standard");
        izpack.addAttribute("basedir", "${basedir}");
        echo(target, "Finished!");
    }

}
