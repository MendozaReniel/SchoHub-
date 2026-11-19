@echo off
setlocal
set "ROOT=%~dp0"
cd /d "%ROOT%"
set "JAVA_HOME=%ROOT%.tools\jdk-17.0.21+9"
set "JAR=%ROOT%target\schohub-backend-1.0.0.jar"

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo Bundled JDK not found at "%JAVA_HOME%"
  echo Edit JAVA_HOME in this script, or install JDK 17.
  exit /b 1
)

if not exist "%JAR%" (
  echo Prebuilt jar not found at "%JAR%"
  echo Build it once with run-backend.cmd, or: mvn -DskipTests package
  exit /b 1
)

echo Starting SchoHub from the prebuilt jar - no Maven, no internet needed.
echo Open http://localhost:8080 in your browser. Press Ctrl+C to stop.
"%JAVA_HOME%\bin\java.exe" -jar "%JAR%" %*
endlocal
