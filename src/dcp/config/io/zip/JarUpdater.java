package dcp.config.io.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import dcp.logic.factory.TypeFactory.LOG_LEVEL;
import dcp.main.log.Out;
import dcp.main.log.StreamDisplay;




public class JarUpdater {
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
    
        //zipAddFiles() Test
        /*
        File[] contents = {new File("res/native/ShellLink.dll"),
                           new File("res/native/ShellLink_x64.dll")};
        
        File jarFile = new File("target/package.jar");
    
        try {
            zipAddFiles(jarFile, contents);
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/
        
        //cmdAddFiles() Test
        
        String[] native_files = {"native/ShellLink.dll", "native/ShellLink_x64.dll"};
        try
        {
            cmdAddFiles("target/package.jar", "res", native_files);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //jarAddFiles() Test
        //JarUpdater.jarAddFiles("target/package.jar");
    
    }

    
    //Adds files to a jar zip archive (overrides it)
    public static void zipAddFiles(File zipFile, File[] files) throws IOException {
        
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();
    
        boolean renameOk=zipFile.renameTo(tempFile);
        if (!renameOk)
        {
            throw new RuntimeException("could not rename the file "+zipFile.getAbsolutePath()+" to "+tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[1024];
    
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
    
        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean notInFiles = true;
            
            for (File f : files) {
                if (f.getName().equals(name)) {
                    notInFiles = false;
                    break;
                }
            }
            if (notInFiles) {
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(name));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        }
        
        // Close the streams        
        zin.close();
        
        // Compress the files
        for (int i = 0; i < files.length; i++) {
            InputStream in = new FileInputStream(files[i]);
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(files[i].getName()));
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
        }
        
        // Complete the ZIP file
        out.close();
        tempFile.delete();
    }

    
    //Adds files to a jar archive from an extern cmd process
    public static int cmdAddFiles(String jarFile, String baseDir, String[] files) throws IOException, InterruptedException {
        
        String cmd = "jar uf "+ jarFile;
        
        for (String s:files) {
            cmd = cmd.concat(" -C "+ baseDir + " " + s);
        }
        
        Out.print(LOG_LEVEL.DEBUG, "- JAR command: "+cmd);
        
        Process process_update = Runtime.getRuntime().exec(cmd);
        
        //StreamDisplay.setWait(false);//finishes the process after 1s without stream
        if (!StreamDisplay.show(LOG_LEVEL.INFO, process_update.getInputStream(), process_update.getErrorStream()))
            Out.print(LOG_LEVEL.ERR, "Command terminated with errors!");
        else
            Out.print(LOG_LEVEL.INFO, "Command executed.");
        
        //StreamDisplay.setWait(true);
        return process_update.waitFor();
    }
    
    //Adds files to a jar archive (overrides it)
    public static void jarAddFiles(String TARGET) throws FileNotFoundException, IOException {
        File jarfile = new File(TARGET);
        if (jarfile.exists()) {
            if (!jarfile.canWrite()) {
                Out.print(LOG_LEVEL.WARN, "Fichier JAR protégé en écriture!");
                jarfile.setWritable(true);
                Out.print(LOG_LEVEL.INFO, "Permissions d'écriture affectées au fichier.");
            }
            else {
                Out.print(LOG_LEVEL.DEBUG, "Fichier JAR accessible en écriture");
                JarOutputStream out = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(jarfile)));
                out.putNextEntry(new JarEntry("res/native/ShellLink.dll"));
                out.closeEntry();
                Out.print(LOG_LEVEL.DEBUG, "Fichier JAR modifié.");
                out.finish();
                out.close();
            }
        }
        else {
            Out.print(LOG_LEVEL.ERR, "Fichier JAR non existant!");
        }
    }
}
