package com.dcp.sm.config.io.ps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;


public class PSWriter
{
    private final static String TEMPVALDEL = "___";// Template Values Delimiters
    
    private File target_file; // powershell file to write to
    private File template_file; // template file to edit (copy from with modifications)
    
    protected PSWriter(String template_path) throws IOException
    {
        this.template_file = new File(template_path);
    }
    
    /**
     * Rewrites content from template replacing values
     * @param values
     * @throws IOException
     */
    protected void writeFromTemplate(Map<String, String> values) throws IOException {
        assert this.target_file != null;
        
        if (!this.target_file.exists())
            this.target_file.createNewFile();
        
        Scanner in = new Scanner(new FileInputStream(template_file));
        BufferedWriter out = new BufferedWriter(new FileWriter(target_file));
        
        String line;
        while(in.hasNextLine()) {
            line = in.nextLine();
            
            for(Map.Entry<String, String> entry:values.entrySet())
                line = line.replace(TEMPVALDEL+entry.getKey()+TEMPVALDEL, entry.getValue());
            
            out.write(line);
            out.newLine();
        }
        
        in.close();
        out.close();
    }
    

    // Getters & Setters
    protected File getTargetFile() { return target_file; }
    protected void setTargetFile(File target_file) { this.target_file = target_file; }
    protected File getTemplateFile() { return template_file; }
    protected void setTemplateFile(File template) { this.template_file = template; }

}
