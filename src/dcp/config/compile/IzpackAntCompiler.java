package dcp.config.compile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import dcp.config.io.IOFactory;
import dcp.gui.pivot.Master;


public class IzpackAntCompiler {
    
    private String target_file = "";
    
    /**
     * Change the target file
     * @param TARGET_FILE
     */
    public void setTarget(String TARGET_FILE) {
        target_file = TARGET_FILE;
    }
    /**
     * Get target install file
     * @return
     */
    public String getTarget() {
        return target_file;
    }

    /**
     * Compile IzPack using Ant Task defined target
     * @return int
     */
    private int antCompile() {
        AntCompiler comp = new AntCompiler(IOFactory.xmlIzpackAntBuild, "izpack");
        
        comp.runTarget("izpack-standalone");
        return 0;
    }
    
    /**
     * Public compile function, uses one compile method from above
     * @return int
     * @throws IOException
     * @throws InterruptedException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
	public int compile() throws IOException, InterruptedException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
	    return antCompile();
	}
    
    /**
     * Run IzPack generated package using Ant Task defined target with Trace mode enabled
     * @return int
     */
    private int antDebug() {
        AntCompiler comp = new AntCompiler(IOFactory.xmlDebugAntBuild, Master.setupConfig.getAppName());
        
        comp.runTarget("debug");
        return 0;
    }
	
    /**
     * Public debug function, uses one compile method from above
     * @throws IOException
     * @throws InterruptedException
     */
    public int debug() throws IOException, InterruptedException {
        return antDebug();
	}
    
    /**
     * Run IzPack generated package using Ant Task defined target
     * @return int
     */
    private int antRun() {
        AntCompiler comp = new AntCompiler(IOFactory.xmlRunAntBuild, Master.setupConfig.getAppName());
        
        comp.runTarget("run");
        return 0;
    }
    
    /**
     * Public run function, uses one run method from above
     * @throws IOException
     * @throws InterruptedException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public int run() throws IOException, InterruptedException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return antRun();
    }
    
}
