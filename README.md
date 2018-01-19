DCP Setup Maker
---
[![Build Status](https://travis-ci.org/DevComPack/setupmaker.svg)](https://travis-ci.org/DevComPack/setupmaker)  
[![Download DCP Setup Maker](https://img.shields.io/sourceforge/dt/devcompack.svg)](https://sourceforge.net/projects/devcompack/files/latest/download)
  
Powerful cross-platform install builder  

Great and powerful application to generate stable and multi-platform java installers.  

Watch a demo of DCP Setup Maker's installer being made in 2 minutes: https://www.youtube.com/watch?v=dmF5Kyla7Hg

Its intuitive UI and exceptional ease of use makes it very easy to build complex installers with large amounts of files.
The whole process is done step by step through a wizard-like interface, filling in fields and setting up files to finally build your package.

[![Download DCP Setup Maker](https://a.fsdn.com/con/app/sf-download-button)](https://sourceforge.net/projects/devcompack/files/latest/download)

*Required: Java 1.6+ for built packages*  
*Powered by Java, IzPack, Nuget, Launch4j, Apache Pivot, Apache Ant and Stax.*  


GET INVOLVED
---
All contributors are very welcome to help make this software a perfect open-source setup maker! All questions and requests to make some edits in the program will be replied to ASAP.

If you want to make a pull request, you just have to clone the master branch on your computer, create a branch for your modifications and push it with a pull request.

Questions can be asked in the [Google Group](https://groups.google.com/forum/#!forum/dcp-setup-makers).


What you will need on your machine
-
- Apache Maven
- Apache Ant: *add bcel jar library to the lib folder*
- Java JDK 8+

Run the app
-
One you made modifications, you can run the DCP app from you IDE via the class *com.dcp.sm.App* as a Java application.

Automated tasks
-
+ build.xml:
This ant build file contains tasks to compile and generate the DCP binary for you to test, the binary will be created on the code root path.
You can launch *dist* for a Java distribution and *dist.win* for a special Windows binary.
The *clean* target can clean all of te generated files from your folder once you finished testing.

+ release.xml:
This ant build file contain tasks to create the final release packages.
The two properties *doc.dir* and *launch4j.dir* should be updated according to your own local paths of [gh-pages/doc](https://github.com/DevComPack/setupmaker/tree/gh-pages/doc) and [launch4j install](https://sourceforge.net/projects/launch4j/) respectively.

Dev on Windows
-
For Windows developers, you already have the *cmd/* folder which contains a batch script file for every step of the release process, going from 0-clean to 5-documentation.


LICENSE
---
Apache License

Version 2.0, January 2004

http://www.apache.org/licenses/

CHANGELOG
---
*1.3.0*
- Java 8 incompatibilities for Apache Pivot fixed with the new 2.0.5 release
see full changelog here (http://mirrors.ircam.fr/pub/apache//pivot/RELEASE-NOTES-2.0.5.html)
- Source code / automated tasks / documentation updated for new contributors to get involved

*1.2.1*  
- Windows special distribution with executable binary for Java version check (1.7+)  
- Mac OS/Linux distribution size reduced with Windows features disabled  
- Project source code migrated to Maven structure  
* Bug fixes:  
Window close event validation  
Unix filenames without extensions couldn't be selected in filebrowser  
Author Email autofill include null value  
Load project with relative paths in Tweak tab  
Zip files content list for shortcut (TFile > ZipFile)  
Tweak app name was filling in empty install path  

*1.2.0*
+ New features:  
Open package target folder on Build  
Check packs properties for errors  
Pack target architecture(x32/x64) property (request by Danish42)  
Properties restored on new scan for same packs (request by Hozku)  
Easy access for recent saved/loaded projects (up to 3)  
Scan child folders directly from file view context menu  
Copy/Paste pack data between packs (context menu)  
Folder target path set to packs from scan tab  
Text fields validators register error message on component tooltip text  
- Enhancements:  
GUI update  
Treat folders as groups moved to Scan tab (instead of Import button)  
Logging organized into 4 levels (DEBUG, INFO, WARN, ERR)  
NuGet Builder option only available from Windows systems  
Model data structure update on cascade when loading projects of older version (starting from version 1.0)  
Group rename disabled when same name  
Context menus for Packs and Groups views with icons  
Executables temporary target directory updated for use  
Scan file-browser root folder update on path change  
Master GUI buttons grouped inside one menu Bar  
Undo enabled for every edit to reload new project  
Tweak options organized into different tab levels  
Tweak pack shortcut option always enabled for setting  
* Bug fixes:  
Empty Group creation was possible  
Group treeview node delete button returned exception when none selected  
Panels display flag wasn\'t initialized  
Build Mode was set double times on app start  
Faster application start  
Tweak application version validator wasn\'t working  
Tooltip on empty tableView field is now hidden  
Multi selected packs validation was still active on deletion  
Exceptions on some buttons when empty data  
Advanced shortcut button enable bug  

*1.1.1*
+ New features:  
Nuget/Chocolatey process compiler  
Conditions support for IzPack  
- Enhancements:  
Code structure update  
Validators debugging warnings  
Multi lines clipboard copy of selected log lines  
Back to factory setup data on undo with no modifications  
Merge of dcp libs to one library  
Add scan horizontal split panel to Workspace auto-saved data  
Build mode and default build configurations added to workspace data  
Build logs copy to clipboard from context menu  
Apache Commons IO for file copy operations  
CommandLine build includes app name and version in package file  
* Bug fixes:  
Default empty target path was based on app data instead of setup data  
Workspace configuration was always saved even when not modified  
Save file extension correction on empty path  
Pack install name validation was displaying a wrong debug message  
SFTP data load generated exception on empty data  
Workspace data was saved with edit flag on  
Frames Singletons were not used as intended  

*1.1.0*
* Bug Fixes:  
Set tab listbox circular mode disabled  
Packs tooltip text correction on empty area  
Scan Packs TableView selection enabled  
Expand/Collapse buttons fix for Java 8  
Fixed Data initialization and load at startup  
Tab Singletons initialize with instantiation  
Scan on enabled selection didn't update packs list  
+ New features:  
Pack version property  
New compilation system based on multiple compilers  
Command Line compilation of dcp files  
- Enhancements:  
Apache Pivot update to 2.0.4  
Split panel on Scan tab's side bar  
Resizable RegExp filter input size  
Version extract from pack file name  


*1.0.3*
* Bug Fixes:  
Fixed for Java 8  
Code structure update  
Simple scan didn't update on changed selection  
Filters now are enabled when selected  
Version increment format check + focus  
+ Enhancements:  
GUI update  
Assistant update  
Recent scanned directories display with parent + full path on tooltip  
Improved filtering system  
Custom filters show up in UI  
Second custom filter replaced with regexp filtering  
Packs tooltip displays full path to pack on disk  
Automated version number parts selection  
Version Increment applies to selected part or last part by default  


*1.0.2*
- Bug fixes
- Custom user defined langpack
- Hidden pack option
- Pack Shortcut advanced options for folders and archives
- Suggestions for folder/archive inner files' paths shortcuts
- Packs source path included into save/load
- Path Validator for scanned folder path
- Scanned folder path can now be saved as default configuration
- Changing/Correcting pack paths from Scan folder path if path error
- Editing invalidated source path doesn't scan the folder
- Added DCP version number to save-file for future versions load fix
- Multiple comma separated authors can be added (emails included)
- Helper assistant text updated
- Added Tera-Bytes to split size units
- Packs panel disabled if all packs are required
- Shortcuts panel disabled if no shortcut to install
- Log line selection copies content to clipboard
- Packs can be deleted from Set tab
- Increment version number button


*1.0.1*
- Bug fixes
- Fixed for Mac OS X based systems
- Apache Pivot library updated from 2.0.2 to 2.0.3
- Tweak option for automated script generation at end of setup
- Input validation for file paths
- Process panel fixed for non-windows os
- Added support for more executable file types (reg, bat, sh, jar)
- Dependency implemented for executable packs
- Packs can have two types of dependency (Group or single Pack)
- Two Custom filters added to scan for advanced users
- Platform OS option for Packs
- Selected option by default for Packs
- HTTP Web Setup builder
- SFTP connection + packs file transfer
- Directories filter enabled for Recursive Scan
- Group rename function button added
