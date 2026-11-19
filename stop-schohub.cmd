@echo off
setlocal
title Stop SchoHub
echo Stopping SchoHub (port 8080)...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$c = Get-NetTCPConnection -State Listen -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -First 1;" ^
  "if (-not $c) { Write-Host 'SchoHub is not running.'; exit 0 }" ^
  "$target = $c.OwningProcess;" ^
  "$p = Get-CimInstance Win32_Process -Filter ('ProcessId=' + $target);" ^
  "if ($p -and $p.CommandLine -like '*schohub-backend*') { Stop-Process -Id $target -Force; Write-Host ('SchoHub stopped (PID ' + $target + ').') }" ^
  "else { Write-Host 'Port 8080 is used by another program, so nothing was stopped.' }"
echo.
ping -n 3 127.0.0.1 >nul
endlocal
