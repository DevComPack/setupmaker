package com.dcp.sm.config.io.xml.izpack;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.out.SMOutputElement;

import com.dcp.sm.config.io.xml.StaxMateWriter;


public class PackagingWriter extends StaxMateWriter
{
    private SMOutputElement packagRoot;

    public PackagingWriter(String xml_file)
    {
        super(xml_file);
        root = out.setRoot("xfragment");//<xfragment>
        packagRoot = root;
    }
    
    public PackagingWriter(SMOutputElement root) throws XMLStreamException {
        super(root);
        header();
        packagRoot = root;
    }
    
    public void header() throws XMLStreamException
    {
        addComment("The Packaging section. We indicate the packaging element to span the installer over multiple volumes.");
    }
    
    /**
     * Setup Split Packaging Data
     * @param SIZE files max size
     * @return boolean
     * @throws XMLStreamException
     */
    public boolean setPackaging(int SIZE) throws XMLStreamException {
        if (SIZE != 0) {
            SMOutputElement packaging = packagRoot.addElement("packaging");//<packaging>
            SMOutputElement packager = packaging.addElement("packager");//<packager
            packager.addAttribute("class", "com.izforge.izpack.compiler.MultiVolumePackager");//class=".." >
            SMOutputElement options = packager.addElement("options");//<options
            options.addAttribute("volumesize", String.valueOf(SIZE));//volumesize="681574400"
            options.addAttribute("firstvolumefreespace", String.valueOf(SIZE/4));//firstvolumefreespace="157286400" />
            //</packager>
            SMOutputElement unpacker = packaging.addElement("unpacker");//<unpacker
            unpacker.addAttribute("class", "com.izforge.izpack.installer.MultiVolumeUnpacker");//class=".."
            //</packaging>
        }
        return true;
    }

}
