package com.dcp.sm.config.io.zip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.schlichtherle.truezip.file.TFile;


public class TrueZipCastFactory
{
    private static List<TFile> archives = new ArrayList<TFile>();
    
    /**
     * convert rar archive to zip
     * @param rar_file
     * @return
     * @throws IOException
     */
    /*public static File rarToZip(File rar_file) throws IOException {
        TFile zip_file = new TFile(rar_file.getAbsolutePath().replace(".rar", ".zip"));
        new TFile(rar_file).cp_rp(zip_file);
        archives.add(zip_file);
        return zip_file;
    }*/
    
    public static void clearArchives() {
        for(TFile f:archives) {
            f.deleteOnExit();
        }
    }

}
