@echo off
setlocal EnableExtensions
title SchoHub - Edit Login Database
color 0F

rem ==================================================================
rem  SchoHub login database editor
rem  Works on the same H2 file the website uses: data\schohub.mv.db
rem  Safe to run while the site is running (the app opens the DB in
rem  AUTO_SERVER mode, so a second connection is allowed).
rem ==================================================================

set "ROOT=%~dp0"
set "DBFILE=%ROOT%data\schohub.mv.db"
set "DBURL=jdbc:h2:file:%ROOT%data/schohub;AUTO_SERVER=TRUE"

rem BCrypt hash of the demo password "Password123". The website only
rem stores BCrypt hashes, so a plain-text password cannot be typed
rem straight into the table. Resetting writes this hash instead.
set "HASH=$2a$10$LhWDhrCHb7dDEWHteD1Wxeab4IyNBYqpqQZfmgiuEuir.kcvv.01K"

if not exist "%DBFILE%" (
  echo.
  echo   [x] Database not found:
  echo       %DBFILE%
  echo.
  echo   Start the website once so the database is created, then run this again.
  echo.
  pause
  exit /b 1
)

rem ---------------------------------------------------------------- Java
set "JAVA=%ROOT%.tools\jdk-17.0.21+9\bin\java.exe"
if not exist "%JAVA%" set "JAVA=java"
"%JAVA%" -version >nul 2>&1
if errorlevel 1 (
  echo.
  echo   [x] Java was not found.
  echo       Install JDK 17, or keep the .tools folder in this project.
  echo.
  pause
  exit /b 1
)

call :findh2
if not defined H2JAR (
  echo.
  echo   [x] The H2 database driver h2-*.jar was not found.
  echo.
  echo   Fix it in one of these ways:
  echo     - Build the project once with run-backend.cmd so Maven downloads h2
  echo     - or copy an h2-x.x.x.jar into:  %ROOT%data\h2.jar
  echo.
  pause
  exit /b 1
)

:menu
cls
echo.
echo   ============================================================
echo    SchoHub - Login Database Editor
echo   ============================================================
echo    Database : data\schohub.mv.db   user "sa", no password
echo    Driver   : %H2JAR%
echo.
echo    [1]  List every account on the website
echo    [2]  Show one account in detail
echo    [3]  Reset a password to "Password123"
echo    [4]  Change an email address
echo    [5]  Change a username
echo    [6]  Add a new login account
echo    [7]  Delete a login account
echo    [8]  Sign out every user (clear all sessions)
echo    [9]  Back up the database file
echo    [10] Open the H2 web console in a browser
echo    [11] Export every account to a CSV file
echo    [0]  Exit
echo.
set "OPT="
set /p "OPT=   Choose a number and press Enter: "
echo.
if "%OPT%"=="1"  goto list
if "%OPT%"=="2"  goto detail
if "%OPT%"=="3"  goto resetpw
if "%OPT%"=="4"  goto changeemail
if "%OPT%"=="5"  goto changeuser
if "%OPT%"=="6"  goto addaccount
if "%OPT%"=="7"  goto deleteaccount
if "%OPT%"=="8"  goto clearsessions
if "%OPT%"=="9"  goto backup
if "%OPT%"=="10" goto console
if "%OPT%"=="11" goto exportcsv
if "%OPT%"=="0"  exit /b 0
echo   [x] "%OPT%" is not one of the numbers above.
ping -n 3 127.0.0.1 >nul
goto menu

rem ================================================================ 1
:list
echo   --- Every account on the website ---------------------------
set "SQL=SELECT EMAIL, USERNAME, FULL_NAME, CITY, BARANGAY, EDUCATION_LEVEL, APPLICATION_PATH, CREATED_AT FROM STUDENTS ORDER BY CREATED_AT; SELECT COUNT(*) AS TOTAL_ACCOUNTS FROM STUDENTS;"
call :runsql
goto pauseback

rem ================================================================ 2
:detail
set "TARGET="
set /p "TARGET=   Email of the account: "
if "%TARGET%"=="" goto menu
call :validate "%TARGET%"
if not "%VALID%"=="1" goto badinput
set "SQL=SELECT EMAIL, USERNAME, FULL_NAME, BIRTHDAY, SEX, CITY, BARANGAY, EDUCATION_LEVEL, SCHOOL, PROGRAM, GWA_VALUE, GWA_SCALE, APPLICATION_PATH, ACTIVE_SCHOLARSHIP_CODE, LOWEST_SUBJECT_GRADE, ANNUAL_FAMILY_INCOME, CREATED_AT FROM STUDENTS WHERE EMAIL='%TARGET%';"
call :runsql
goto pauseback

rem ================================================================ 3
:resetpw
echo   The new password will be:  Password123
echo.
set "TARGET="
set /p "TARGET=   Email of the account: "
if "%TARGET%"=="" goto menu
call :validate "%TARGET%"
if not "%VALID%"=="1" goto badinput
set "SQL=UPDATE STUDENTS SET PASSWORD_HASH='%HASH%' WHERE EMAIL='%TARGET%'; SELECT EMAIL, USERNAME FROM STUDENTS WHERE EMAIL='%TARGET%';"
call :runsql
echo.
echo   If the account is listed above, it can now sign in with Password123.
goto pauseback

rem ================================================================ 4
:changeemail
set "OLD="
set /p "OLD=   Current email: "
if "%OLD%"=="" goto menu
call :validate "%OLD%"
if not "%VALID%"=="1" goto badinput
set "NEW="
set /p "NEW=   New email    : "
if "%NEW%"=="" goto menu
call :validate "%NEW%"
if not "%VALID%"=="1" goto badinput
set "SQL=UPDATE STUDENTS SET EMAIL='%NEW%' WHERE EMAIL='%OLD%'; SELECT EMAIL, USERNAME FROM STUDENTS WHERE EMAIL='%NEW%';"
call :runsql
goto pauseback

rem ================================================================ 5
:changeuser
set "TARGET="
set /p "TARGET=   Email of the account: "
if "%TARGET%"=="" goto menu
call :validate "%TARGET%"
if not "%VALID%"=="1" goto badinput
set "NEW="
set /p "NEW=   New username : "
if "%NEW%"=="" goto menu
call :validate "%NEW%"
if not "%VALID%"=="1" goto badinput
set "SQL=UPDATE STUDENTS SET USERNAME='%NEW%' WHERE EMAIL='%TARGET%'; SELECT EMAIL, USERNAME FROM STUDENTS WHERE EMAIL='%TARGET%';"
call :runsql
goto pauseback

rem ================================================================ 6
:addaccount
echo   The new account will start with the password:  Password123
echo   (the student can complete the rest of the profile on the site)
echo.
set "NEWEMAIL="
set /p "NEWEMAIL=   Email    : "
if "%NEWEMAIL%"=="" goto menu
call :validate "%NEWEMAIL%"
if not "%VALID%"=="1" goto badinput
set "NEWUSER="
set /p "NEWUSER=   Username : "
if "%NEWUSER%"=="" goto menu
call :validate "%NEWUSER%"
if not "%VALID%"=="1" goto badinput
set "SQL=INSERT INTO STUDENTS (ID, EMAIL, USERNAME, PASSWORD_HASH, CREATED_AT) VALUES (RANDOM_UUID(), '%NEWEMAIL%', '%NEWUSER%', '%HASH%', CURRENT_TIMESTAMP); SELECT EMAIL, USERNAME FROM STUDENTS WHERE EMAIL='%NEWEMAIL%';"
call :runsql
goto pauseback

rem ================================================================ 7
:deleteaccount
echo   This removes the account, its sessions and its saved documents.
echo   It cannot be undone.
echo.
set "TARGET="
set /p "TARGET=   Email of the account: "
if "%TARGET%"=="" goto menu
call :validate "%TARGET%"
if not "%VALID%"=="1" goto badinput
set "CONFIRM="
set /p "CONFIRM=   Type DELETE to confirm: "
if /i not "%CONFIRM%"=="DELETE" goto cancel
set "SQL=DELETE FROM PUBLIC.STUDENT_RENEWAL_DOCUMENTS WHERE STUDENT_ID=(SELECT ID FROM STUDENTS WHERE EMAIL='%TARGET%'); DELETE FROM PUBLIC.SESSIONS WHERE STUDENT_ID=(SELECT ID FROM STUDENTS WHERE EMAIL='%TARGET%'); DELETE FROM STUDENTS WHERE EMAIL='%TARGET%'; SELECT COUNT(*) AS REMAINING FROM STUDENTS WHERE EMAIL='%TARGET%';"
call :runsql
goto pauseback

rem ================================================================ 8
:clearsessions
set "SQL=SELECT COUNT(*) AS OPEN_SESSIONS FROM PUBLIC.SESSIONS; DELETE FROM PUBLIC.SESSIONS;"
call :runsql
echo.
echo   Every student has to sign in again.
goto pauseback

rem ================================================================ 9
:backup
for /f "delims=" %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd-HHmmss"') do set "STAMP=%%i"
if not exist "%ROOT%data\backups" mkdir "%ROOT%data\backups"
set "BKP=%ROOT%data\backups\schohub-%STAMP%.zip"
set "SQL=BACKUP TO '%BKP%';"
call :runsql
if exist "%BKP%" (
  echo   Backup saved: data\backups\schohub-%STAMP%.zip
) else (
  echo   [x] Backup failed. See the message above.
)
goto pauseback

rem ================================================================ 10
:console
echo   Opening the H2 web console...
echo.
echo   If the page asks for the connection details, use:
echo     JDBC URL : jdbc:h2:file:%ROOT%data/schohub
echo     User Name: sa
echo     Password : (leave it empty)
echo.
echo   The console only loads while the website is running on port 8080.
start "" "http://localhost:8080/h2-console/"
goto pauseback

rem ================================================================ 11
:exportcsv
for /f "delims=" %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd-HHmmss"') do set "STAMP=%%i"
set "CSV=%ROOT%data\accounts-%STAMP%.csv"
set "SQL=CALL CSVWRITE('%CSV%', 'SELECT * FROM STUDENTS ORDER BY CREATED_AT');"
call :runsql
if exist "%CSV%" (
  echo   Every account was written to: data\accounts-%STAMP%.csv
  echo   Open it with Excel or Notepad.
) else (
  echo   [x] Export failed. See the message above.
)
goto pauseback

rem ================================================================ helpers
:runsql
"%JAVA%" -cp "%H2JAR%" org.h2.tools.Shell -url "%DBURL%" -user sa -sql "%SQL%"
if errorlevel 1 echo   [x] The change failed. Check the message above.
exit /b

:validate
rem Allows letters, digits and . _ - @ + only (safe inside a SQL string).
set "VALID=0"
setlocal EnableDelayedExpansion
set "V=%~1"
echo(!V!| findstr /r "^[A-Za-z0-9._@+-]*$" >nul && set "VALID=1"
endlocal & set "VALID=%VALID%"
exit /b

:findh2
set "H2JAR="
if exist "%ROOT%data\h2.jar" set "H2JAR=%ROOT%data\h2.jar"
if defined H2JAR exit /b
for /f "delims=" %%f in ('dir /b /s "%USERPROFILE%\.m2\repository\com\h2database\h2\h2-*.jar" 2^>nul') do set "H2JAR=%%f"
if defined H2JAR exit /b
if not exist "%ROOT%target\schohub-backend-1.0.0.jar" exit /b
powershell -NoProfile -ExecutionPolicy Bypass -Command "$src='%ROOT%target\schohub-backend-1.0.0.jar'; $out=Join-Path $env:TEMP 'schohub-tools'; New-Item -ItemType Directory -Force -Path $out | Out-Null; Add-Type -AssemblyName System.IO.Compression.FileSystem; $zip=[System.IO.Compression.ZipFile]::OpenRead($src); $e=$zip.Entries | Where-Object { $_.FullName -like 'BOOT-INF/lib/h2-*.jar' } | Select-Object -First 1; if ($e) { [System.IO.Compression.ZipFileExtensions]::ExtractToFile($e, (Join-Path $out 'h2.jar'), $true) }; $zip.Dispose()" >nul 2>&1
if exist "%TEMP%\schohub-tools\h2.jar" set "H2JAR=%TEMP%\schohub-tools\h2.jar"
exit /b

:badinput
echo   [x] Use only letters, digits and the characters . _ - @ +
echo       Quotes, spaces, commas and %% are not allowed here.
ping -n 3 127.0.0.1 >nul
goto menu

:cancel
echo   Cancelled.
ping -n 3 127.0.0.1 >nul
goto menu

:pauseback
echo.
pause
goto menu
