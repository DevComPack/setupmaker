try {
    "+ Begin uninstall of ___INSTALLNAME___"
    $target = "___INSTALLPATH___"
    
    if (Test-Path "$target") {
        "+ Deleting install folder: $target"
	    Remove-Item "$target" -Force -Recurse
    }

    "+ Finished uninstall of ___INSTALLNAME___"
    
    Write-ChocolateySuccess '___INSTALLNAME___'
} catch {
  Write-ChocolateyFailure '___INSTALLNAME___' $($_.Exception.Message)
  throw
}