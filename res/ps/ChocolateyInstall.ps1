function Expand-ZIPFile($file, $destination)
{
    $shell = new-object -com shell.application
    $zip = $shell.NameSpace($file)
    foreach($item in $zip.items())
    {
        $shell.Namespace($destination).copyhere($item)
    }
}

try {
    "+ Begin install of ___INSTALLNAME___"
    $scriptPath = split-path -parent $MyInvocation.MyCommand.Definition
    $copyFolder = Join-Path $scriptPath '..\copy'
    $extrFolder = Join-Path $scriptPath '..\extr'
    $execFolder = Join-Path $scriptPath '..\exec'
    $target = "___INSTALLPATH___"
    
    if (! (Test-Path "$target") ) {
        "- Creating install target folder: $target"
        mkdir "$target"
    }
    
    if (Test-Path "$copyFolder") {
        "- Copying files to $target"
        Copy-Item "$copyFolder\*" "$target" -Force -Recurse
    }
    
    if (Test-Path "$extrFolder") {
        "- Extracting files to $target"
        $archives = Get-ChildItem "$extrFolder" -Recurse |
            Where-Object { $_.Extension -eq ".zip" -or $_.Extension -eq ".7z" }

        foreach($file in $archives) {
            $filename = $file.FullName.ToString()
            "Extracting archive $file"
            Expand-ZIPFile -File "$filename" -Destination "$target"
        }
    }
    
    if (Test-Path "$execFolder") {
        "- Executing files"
        $files = Get-ChildItem "$execFolder" -Recurse

        foreach($file in $files) {
            $filename = $file.FullName.ToString()
            $extension = $file.Extension.ToString()

            if ($extension -eq ".exe" -or $extension -eq ".bat" -or $extension -eq ".cmd") {
                "Registering executable $filename";
                & "$filename"
            }
            elseif ($extension -eq ".jar") {
                "Launching jar $filename";
                java -jar "$filename"
            }
            elseif ($extension -eq ".msi") {
                "Executing MSI setup $filename in silent mode";
                msiexec /i "$filename" /quiet
            }
            elseif ($extension -eq ".reg") {
                "Importing registry data from $filename";
                reg import "$filename"
            }
        }

    }

    "+ Finished install of ___INSTALLNAME___"
    
    Write-ChocolateySuccess '___INSTALLNAME___'
} catch {
  Write-ChocolateyFailure '___INSTALLNAME___' $($_.Exception.Message)
  throw
}