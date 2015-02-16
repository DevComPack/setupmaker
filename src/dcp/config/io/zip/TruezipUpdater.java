package dcp.config.io.zip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;



import dcp.logic.factory.TypeFactory.LOG_LEVEL;
import dcp.main.log.Out;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileOutputStream;
import de.schlichtherle.truezip.file.TFileReader;
import de.schlichtherle.truezip.file.TFileWriter;


public class TruezipUpdater
{
    
    private String zipfile;

    public TruezipUpdater(String ZIP_FILE)
    {
        super();
        this.zipfile = ZIP_FILE;
    }
    
    public void setZipFile(String ZIP_FILE) {
        zipfile = ZIP_FILE;
    }
    
    /*
     * Test TrueZIP class methods
     */
    public static void main(String[] args)
    {
        try
        {
            TruezipUpdater tz = new TruezipUpdater("target/package.jar");
            tz.addBinFile("res/native/ShellLink.dll", "native");
            tz.addBinFile("res/native/ShellLink_x64.dll", "native");
            Out.print(LOG_LEVEL.INFO, "Command executed!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void addFile(String FILE_TO_ADD, String DEST_DIR) throws IOException {
        String[] dirs = {FILE_TO_ADD};
        if (FILE_TO_ADD.contains("/")) dirs = FILE_TO_ADD.split("/");
        else if (FILE_TO_ADD.contains("\\")) dirs = FILE_TO_ADD.split("\\\\");
        String DEST_FILE = dirs[dirs.length-1];
        
        File entry = new TFile(this.zipfile + "/"+ DEST_DIR +"/" + DEST_FILE);
        Writer writer = new TFileWriter(entry);
        Reader reader = new FileReader(FILE_TO_ADD);
        int c;
        
        try {
            while ( (c=reader.read()) != -1)
                writer.write(c);
        }
        finally {
            writer.close();
            reader.close();
        }
    }
    
    public boolean addBinFile(String FILE_TO_ADD, String DEST_DIR) throws IOException {
        if (new File(FILE_TO_ADD).exists()) {
            String[] dirs = {FILE_TO_ADD};
            if (FILE_TO_ADD.contains("/")) dirs = FILE_TO_ADD.split("/");
            else if (FILE_TO_ADD.contains("\\")) dirs = FILE_TO_ADD.split("\\\\");
            String DEST_FILE = dirs[dirs.length-1];
            
            Out.print(LOG_LEVEL.DEBUG, "Copy "+FILE_TO_ADD+" to "+this.zipfile + "/"+ DEST_DIR +"/" + DEST_FILE);
            
            File entry = new TFile(this.zipfile + "/"+ DEST_DIR +"/" + DEST_FILE);
            TFileOutputStream writer = new TFileOutputStream(entry);
            FileInputStream reader = new FileInputStream(FILE_TO_ADD);
            //Writer writer = new TFileWriter(entry);
            //Reader reader = new FileReader(FILE_TO_ADD);
            int n;
            byte[] b = new byte[1024]; 
            
            try {
                while ( (n=reader.read(b)) != -1)
                    writer.write(b, 0, n);
            }
            finally {
                writer.close();
                reader.close();
            }
            //Out.print("JAR", "File copied.");
            return true;
        }
        else {
            Out.print(LOG_LEVEL.ERR, "File '"+ FILE_TO_ADD +"' not found!");
            return false;
        }
    }
    
    public void read() throws IOException {
        File entry = new TFile(this.zipfile + "/dir/HelloWorld.txt");
        Reader reader = new TFileReader(entry);
        BufferedReader br = new BufferedReader(reader);
        
        try {
            Out.print(LOG_LEVEL.DEBUG, br.readLine());
        }
        finally {
            reader.close();
        }
    }
    
    public void list() {
        TFile archive = new TFile(this.zipfile);
        for (String member : archive.list())
          Out.print(LOG_LEVEL.DEBUG, member);
    }
    
    public void update() throws FileNotFoundException {
        TFile entry = new TFile(this.zipfile);
        entry.setLastModified(System.currentTimeMillis()); // sub-optimal!
    }

}
