package com.dcp.sm.config.compile;

import java.io.File;


import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.dcp.sm.main.log.Out;


public class AntCompiler
{
    private String build_file_path;
    private Project project;
    
    public AntCompiler(String build_file, final String log_tag)
    {
        this.build_file_path = build_file;
        
        project = new Project();
        
        File buildFile = new File(this.build_file_path);
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", helper);
        project.init();
        
        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        project.addBuildListener(consoleLogger);
        
        //Log using Out class
        project.addBuildListener(new BuildListener() {
            
            @Override
            public void taskStarted(BuildEvent ev)
            { }
            
            @Override
            public void taskFinished(BuildEvent ev)
            { }
            
            @Override
            public void targetStarted(BuildEvent ev)
            { }
            
            @Override
            public void targetFinished(BuildEvent ev)
            { }
            
            @Override
            public void messageLogged(BuildEvent ev)
            {
                if (ev.getPriority() == Project.MSG_INFO ||
                    //ev.getPriority() == Project.MSG_VERBOSE ||
                    ev.getPriority() == Project.MSG_WARN ||
                    ev.getPriority() == Project.MSG_ERR)
                    Out.log(log_tag.toLowerCase()+"> "+ev.getMessage());
                else
                    Out.print("[DEBUG] "+ev.getMessage(), System.out);
            }
            
            @Override
            public void buildStarted(BuildEvent ev)
            { }
            
            @Override
            public void buildFinished(BuildEvent ev)
            { }
        });
        
        helper.parse(project, buildFile);
    }
    
    public void runDefaultTarget() {
        project.executeTarget(project.getDefaultTarget());
    }
    
    public void runTarget(String target_name) {
        project.executeTarget(target_name);
    }
    

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        AntCompiler comp = new AntCompiler("ant-build.xml", "Izpack");
        //comp.runDefaultTarget();
        comp.runTarget("izpack-standalone");
    }

}
