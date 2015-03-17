package com.dcp.sm.config.compile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

import com.dcp.sm.config.io.IOFactory;
import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.main.log.Out;

/**
 * 
 * @author SSAIDELI
 *
 */
public class NugetProcessCompiler
{
    private String nuget = "\"./res/utils/nuget.exe\"";
    private String target_dir = "";
    
    // Authentication information for nuget ProGet feed access
    private final static String apikey = IOFactory.apikey;
    
    /**
     * Change the target file
     * @param TARGET_DIR
     */
    public void setTarget(String TARGET_DIR) {
        target_dir = TARGET_DIR;
    }
    /**
     * Get target install file
     * @return Target directory path
     */
    public String getTarget() {
        return target_dir;
    }
    
    /**
     * execute command using nuget binary file
     * @param command: nuget.exe command to execute
     */
    private Process exec(String command)
    {
        try
        {
            Process process = Runtime.getRuntime().exec(command, null, new File(getTarget()));
            
            Scanner in = new Scanner(process.getInputStream());
            Scanner err = new Scanner(process.getErrorStream());
            
            Thread.sleep(500);
            while(in.hasNextLine() || err.hasNextLine()) {
                if (in.hasNextLine())
                    Out.print(LOG_LEVEL.INFO, in.nextLine());
                if (err.hasNextLine())
                    Out.print(LOG_LEVEL.ERR, err.nextLine());
                Thread.sleep(500);
            }
            process.waitFor();
            
            in.close();
            err.close();
            
            return process;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Delete all files under a target directory
     * @param path: folder containing spec files
     * @throws IOException
     */
    public void cleanDir(String path) throws IOException {
        Path directory = Paths.get(path);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }
    
    /**
     * make a package from a spec folder
     * @param specFile: nuspec file
     * @return Process
     * @throws IOException
     */
    public Process pack(File specFile) throws IOException {
        String command = nuget + " pack \""
                + specFile.getAbsolutePath()
                + "\" -NoPackageAnalysis";
        Out.print(LOG_LEVEL.DEBUG, "$"+command);
        
        
        return exec(command);
    }
    
    /**
     * Push a package to a feed repository
     * @param pkg: nupkg file to push
     * @param feedUrl: source feed url
     * @return Process
     * @throws IOException
     */
    public Process push(File pkg, String feedUrl) throws IOException {
        String command = nuget + " push \""
                + pkg.getAbsolutePath()
                + "\" -Source " + feedUrl
                + " -ApiKey "+ apikey;
        Out.print(LOG_LEVEL.DEBUG, "$"+command);
        
        
        return exec(command);
    }
    
    
    
    public static void main(String[] args) {

        try
        {
            //ProcessBuilder ps = new ProcessBuilder("where", "chocolatey");
            //ProcessBuilder ps = new ProcessBuilder("chocolatey.bat", "pack");
            Process process = Runtime.getRuntime().exec("nuget pack oracle/oracle.nuspec -NoPackageAnalysis", null, new File("target/nuget"));
            //Process process = ps.start();
            
            Scanner in = new Scanner(process.getInputStream());
            
            while(in.hasNextLine()) {
                System.out.println(in.nextLine());
            }
            process.destroy();
            System.out.println(process.exitValue());
            
            in.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
