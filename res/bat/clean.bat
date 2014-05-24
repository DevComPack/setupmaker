@ECHO OFF
echo Command: reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\BOC 1.0.3" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\BOC 1.0.3" /f
echo Command: java -jar uninstaller.jar
java -jar uninstaller.jar
echo finished.