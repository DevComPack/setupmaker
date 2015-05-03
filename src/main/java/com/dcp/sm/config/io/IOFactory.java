package com.dcp.sm.config.io;

import java.io.File;
import java.io.IOException;

import org.apache.pivot.util.Filter;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.media.Image;
import org.json.simple.parser.ParseException;

import com.dcp.sm.config.io.json.JsonSimpleReader;
import com.dcp.sm.logic.factory.TypeFactory.FILE_TYPE;

/**
 * Input/Output Factory
 * @author SSAIDELI
 */
public class IOFactory
{
    //ISO3 language codes
    public static String[] iso3Codes;
    //File type extensions
    public static String[] archiveExt;// = {"zip", "rar", "jar", "iso"};
    public static String[] exeExt;// = {"exe", "jar", "cmd", "sh", "bat"};
    public static String[] setupExt;// = {"msi"};
    public static String[] docExt;// = {"txt", "pdf", "doc", "docx", "xml", "rtf", "odt", "xls", "xlsx", "csv"};
    public static String[] imgExt;// = {"bmp", "jpg", "jpeg", "gif", "png", "ico"};
    public static String[] webExt;// = {"html", "htm", "php", "js", "asp", "aspx", "css"};
    public static String[] soundExt;// = {"wav", "mp3", "ogg", "wma"};
    public static String[] vidExt;// = {"mp4", "mpg", "mpeg", "wmv", "avi", "mkv"};
    public static String[] custExt;//User defined custom extensions
    
    /**
     * Returns file type of a file
     * @param f: file
     * @return FILE_TYPE
     */
    public static FILE_TYPE getFileType(File f) {
        if (f.isDirectory()) return FILE_TYPE.Folder;
        
        String NAME = f.getName();
        if (IOFactory.isFileType(NAME, FILE_TYPE.Archive))//Archive
            return FILE_TYPE.Archive;
        else if (IOFactory.isFileType(NAME, FILE_TYPE.Executable))//Executable
            return FILE_TYPE.Executable;
        else if (IOFactory.isFileType(NAME, FILE_TYPE.Setup))//Setup file
            return FILE_TYPE.Setup;
        else if (IOFactory.isFileType(NAME, FILE_TYPE.Document))//Text Document file
            return FILE_TYPE.Document;
        else if (IOFactory.isFileType(NAME, FILE_TYPE.Image))//Image file
            return FILE_TYPE.Image;
        else if (IOFactory.isFileType(NAME, FILE_TYPE.Web))//Web file
            return FILE_TYPE.Web;
        else if (IOFactory.isFileType(NAME, FILE_TYPE.Sound))//Sound file
            return FILE_TYPE.Sound;
        else if (IOFactory.isFileType(NAME, FILE_TYPE.Video))//Video file
            return FILE_TYPE.Video;
        else if (IOFactory.isFileType(NAME, FILE_TYPE.Custom))//Custom extension
            return FILE_TYPE.Custom;
        else//Simple File
            return FILE_TYPE.File;
    }
    
    /**
     * Tests if a file's extension is of a type
     * (folders and files without extension are automatically valid)
     * @param file_name
     * @param file_type
     * @return boolean: success
     */
    public static boolean isFileType(String file_name, FILE_TYPE file_type) {
        // Accept all (unix) files without extensions
        if (!file_name.contains("."))
            return true;
        
        String[] exts = null;
        switch (file_type) {
        case Archive:
            exts = archiveExt;
            break;
        case Executable:
            exts = exeExt;
            break;
        case Setup:
            exts = setupExt;
            break;
        case Document:
            exts = docExt;
            break;
        case Image:
            exts = imgExt;
            break;
        case Web:
            exts = webExt;
            break;
        case Sound:
            exts = soundExt;
            break;
        case Video:
            exts = vidExt;
            break;
        case Custom:
            exts = custExt;
            break;
        case File:
            return true;
        default:
            break;
        }
        assert exts != null;
        
        for(String S : exts)
            if (file_name.toLowerCase().endsWith("."+S)) {
                return true;
            }
        return false;
    }
    
    
    // Cached icons
    private static final String iconsPath = "/com/dcp/sm/gui/icons/";
    public static Image imgFolder;//Folder icon
    public static Image imgFile;//File icon
    public static Image imgZip;//Archive icon
    public static Image imgExe;//Executable icon
    public static Image imgSetup;//Setup icon
    public static Image imgDoc;//Text file icon
    public static Image imgImg;//Image file icon
    public static Image imgWeb;//Web file icon
    public static Image imgSound;//Audio file icon
    public static Image imgVid;//Video file icon
    public static Image imgCust;//Custom filter file icon
    public static Image imgHistory;//History file icon
    public static Image imgClose;//Close file icon
    // Menu icons
    public static Image imgAdd;//Add menu icon
    public static Image imgRight;//Right arrow menu icon
    public static Image imgEdit;//Edit menu icon
    public static Image imgDelete;//Delete menu icon
    public static Image imgImport;//Import menu icon
    public static Image imgCopy;//Copy menu icon
    public static Image imgPaste;//Paste menu icon
    
    //Filters
    public final static Filter<File> imgFilter = new Filter<File>() {
        @Override public boolean include(File file)
        {
            if (file.isDirectory()) return false;
            else if (file.isFile() && IOFactory.isFileType(file.getName(), FILE_TYPE.Image) )
                return false;
            return true;
        }
    };
    public final static Filter<File> docFilter = new Filter<File>() {
        @Override public boolean include(File file)
        {
            if (file.isDirectory()) return false;
            else if (file.isFile() && (IOFactory.isFileType(file.getName(), FILE_TYPE.Document)
                                        || IOFactory.isFileType(file.getName(), FILE_TYPE.Web)))
                return false;
            return true;
        }
    };
    
    //JSON Files
    public static final String jsonSettings = "settings.json";
    //Directories Paths
    public static String resPath;// = "res";
    public static String xmlPath;// = resPath+"/xml";
    public static String langpackPath;// = resPath+"/langpacks";
    public static String batPath;// = resPath+"/bat";
    public static String antPath;// = resPath+"/ant";
    public static String psPath;// = resPath+"ps/";
    public static String libPath;// = "lib/dcp";
    public static String targetPath;// = "target";
    public static String savePath;// = "saves";
    public static String exeTargetDir;// = "~tmp/";
    //File Paths
    public static String confFile;// = "conf.dcp";
    public static String defDirFile;// = resPath+"/default-dir.txt";
    public static String xmlIzpackInstall;// = "install.xml";
    public final static String dcpFileExt = "dcp";
    //Options
    public static boolean izpackWrite;// = true; 
    public static boolean izpackCompile;// = true;
    public static String apikey;// = Admin:Admin
    
    //Jar libraries
    //public static String jarExecutable;// = libPath+"/dcp-executable.jar";
    //public static String jarListeners;// = libPath+"/dcp-listeners.jar";
    public static String jarResources;// = libPath+"/dcp-resources.jar";
    //Ant build files
    public static String xmlIzpackAntBuild;// = antPath+"/izpack-target.xml";
    public static String xmlRunAntBuild;// = antPath+"/run-target.xml";
    public static String xmlDebugAntBuild;// = antPath+"/debug-target.xml";
    //Bat files
    public static String batClean;// = batPath+"/clean.bat";
    //izpack spec files
    public static String xmlProcessPanelSpec;// = xmlPath+"/ProcessPanelSpec.xml";
    public static String xmlShortcutPanelSpec;// = xmlPath+"/ShortcutPanelSpec.xml";
    public static String xmlRegistrySpec;// = xmlPath+"/RegistrySpec.xml";
    // chocolatey files
    public static String psChocolateyInstall;// = psPath+"/ChocolateyInstall.ps1"
    public static String psChocolateyUninstall;// = psPath+"/ChocolateyUninstall.ps1"
    
    public static String saveFile = "";//"save.dcp";
    public static void setSaveFile(String canonicalPath) {
        if (canonicalPath.length() > 0 && !canonicalPath.endsWith("."+IOFactory.dcpFileExt)) {//add file extension if not given
            canonicalPath = canonicalPath.concat("."+IOFactory.dcpFileExt);
        }
        saveFile = canonicalPath;
    }
    
    /**
     * Load settings from JSON file: settings.json
     * @throws ParseException 
     * @throws IOException 
     */
    private static boolean loadSettings(String json_file) throws IOException, ParseException {
        JsonSimpleReader json_ext = new JsonSimpleReader(json_file, "extensions");
        archiveExt = json_ext.readStringArray("archiveExt");
        exeExt = json_ext.readStringArray("exeExt");
        setupExt = json_ext.readStringArray("setupExt");
        docExt = json_ext.readStringArray("docExt");
        imgExt = json_ext.readStringArray("imgExt");
        webExt = json_ext.readStringArray("webExt");
        soundExt = json_ext.readStringArray("soundExt");
        vidExt = json_ext.readStringArray("vidExt");
        custExt = json_ext.readStringArray("custExt");
        json_ext.close();

        JsonSimpleReader json_paths = new JsonSimpleReader(json_file, "paths");
        savePath = json_paths.readString("savePath");
        resPath = json_paths.readString("resPath");
        xmlPath = json_paths.readString("xmlPath");
        langpackPath = json_paths.readString("langpackPath");
        batPath = json_paths.readString("batPath");
        antPath = json_paths.readString("antPath");
        psPath = json_paths.readString("psPath");
        libPath = json_paths.readString("libPath");
        targetPath = json_paths.readString("targetPath");
        exeTargetDir = json_paths.readString("exeTargetDir");
        json_paths.close();

        JsonSimpleReader json_files = new JsonSimpleReader(json_file, "files");
        confFile = json_files.readString("confFile");
        defDirFile = json_files.readString("defDirFile");
        xmlIzpackInstall = json_files.readString("xmlIzpackInstall");
        json_files.close();

        JsonSimpleReader json_options = new JsonSimpleReader(json_file, "options");
        izpackWrite = (json_options.readString("izpackWrite").equalsIgnoreCase("yes") ||
                json_options.readString("izpackWrite").equalsIgnoreCase("true"));
        izpackCompile = (json_options.readString("izpackCompile").equalsIgnoreCase("yes") ||
                            json_options.readString("izpackCompile").equalsIgnoreCase("true"));
        apikey = json_options.readString("apikey");
        json_options.close();
        
        JsonSimpleReader json_codes = new JsonSimpleReader(json_file, "codes");
        iso3Codes = json_codes.readStringArray("iso3");
        json_codes.close();
        
        return true;
    }
    
    /**
     * Data load
     */
    public static void init() {
        try {
            
            //Import file extensions from settings.json
            loadSettings(IOFactory.jsonSettings);
            //jarExecutable = libPath+"/dcp-executable.jar";
            //jarListeners = libPath+"/dcp-listeners.jar";
            jarResources = libPath+"/dcp-resources.jar";
            xmlIzpackAntBuild = antPath+"/izpack-target.xml";
            xmlRunAntBuild = antPath+"/run-target.xml";
            xmlDebugAntBuild = antPath+"/debug-target.xml";
            batClean = batPath+"/clean.bat";
            xmlProcessPanelSpec = xmlPath+"/ProcessPanelSpec.xml";
            xmlShortcutPanelSpec = xmlPath+"/ShortcutPanelSpec.xml";
            xmlRegistrySpec = xmlPath+"/RegistrySpec.xml";
            psChocolateyInstall = psPath+"/ChocolateyInstall.ps1";
            psChocolateyUninstall = psPath+"/ChocolateyUninstall.ps1";
            
            //Images Resources
            imgFolder = Image.load(IOFactory.class.getClass().getResource(iconsPath + "folder.png"));
            imgFile = Image.load(IOFactory.class.getClass().getResource(iconsPath + "file.png"));
            imgZip = Image.load(IOFactory.class.getClass().getResource(iconsPath + "archive.png"));
            imgExe = Image.load(IOFactory.class.getClass().getResource(iconsPath + "executable.png"));
            imgSetup = Image.load(IOFactory.class.getClass().getResource(iconsPath + "setup.png"));
            imgDoc = Image.load(IOFactory.class.getClass().getResource(iconsPath + "document.png"));
            imgImg = Image.load(IOFactory.class.getClass().getResource(iconsPath + "image.png"));
            imgWeb = Image.load(IOFactory.class.getClass().getResource(iconsPath + "webfile.png"));
            imgSound = Image.load(IOFactory.class.getClass().getResource(iconsPath + "sound.png"));
            imgVid = Image.load(IOFactory.class.getClass().getResource(iconsPath + "video.png"));
            imgCust = Image.load(IOFactory.class.getClass().getResource(iconsPath + "filter.png"));
            imgHistory = Image.load(IOFactory.class.getClass().getResource(iconsPath + "history.png"));
            imgClose = Image.load(IOFactory.class.getClass().getResource(iconsPath + "close.png"));
            imgAdd = Image.load(IOFactory.class.getClass().getResource(iconsPath + "add.png"));
            
            imgRight = Image.load(IOFactory.class.getClass().getResource(iconsPath + "right.png"));
            imgEdit = Image.load(IOFactory.class.getClass().getResource(iconsPath + "edit.png"));
            imgDelete = Image.load(IOFactory.class.getClass().getResource(iconsPath + "delete.png"));
            imgImport = Image.load(IOFactory.class.getClass().getResource(iconsPath + "import.png"));
            imgCopy = Image.load(IOFactory.class.getClass().getResource(iconsPath + "copy.png"));
            imgPaste = Image.load(IOFactory.class.getClass().getResource(iconsPath + "paste.png"));
            
            //Make needed directories
            String[] dirs = new String[] {resPath, antPath, batPath, xmlPath, targetPath, savePath};
            for(String dir:dirs) {
                File f = new File(dir);
                if (!f.exists())
                    f.mkdir();
            }
            
        } catch (TaskExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
}
