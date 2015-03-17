package com.dcp.sm.config.io.ps.chocolatey;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.dcp.sm.config.io.ps.PSWriter;
import com.dcp.sm.logic.model.Pack;

/**
 * 
 * @author SSAIDELI
 *
 */
public class InstallWriter extends PSWriter
{
    Map<String, String> values; // map collection containing template strings and their translate values
    
    
    public InstallWriter(String template_path) throws IOException
    {
        super(template_path);
        values = new TreeMap<String, String>();
    }
    
    /**
     * Fill values for template
     * @param target_dir
     * @param pack
     */
    public void fillFrom(String target_dir, Pack pack) {
        values.put("INSTALLPATH", new File(target_dir, pack.getInstallPath()).toString());
        values.put("INSTALLNAME", pack.getInstallName());
    }
    
    /**
     * write from template with modifications from values map
     * @param target_path: file to write to
     * @throws IOException
     */
    public void writeTo(File target_file) throws IOException {
        setTargetFile(target_file);
        writeFromTemplate(values);
    }
    public void writeTo(String target_path) throws IOException {
        writeTo(new File(target_path));
    }
    
}
