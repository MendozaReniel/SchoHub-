@echo off
setlocal
set "ROOT=%~dp0"
set "JAVA_HOME=%ROOT%.tools\jdk-17.0.21+9"
set "MAVEN_HOME=%ROOT%.tools\apache-maven-3.9.9"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo Local JDK not found at %JAVA_HOME%
  echo Install JDK 17 or edit JAVA_HOME in this script.
  exit /b 1
)

call "%MAVEN_HOME%\bin\mvn.cmd" -f "%ROOT%pom.xml" spring-boot:run %*
endlocal
