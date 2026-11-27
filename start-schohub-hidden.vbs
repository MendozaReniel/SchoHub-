' Starts SchoHub in the background with no visible window.
' Used by the Startup shortcut (shell:startup) so http://localhost:8080 is ready
' right after Windows login. Output goes to logs\autostart.log.
' Stop it again with stop-schohub.cmd.

Set fso = CreateObject("Scripting.FileSystemObject")
Set shell = CreateObject("WScript.Shell")

projectDir = fso.GetParentFolderName(WScript.ScriptFullName)
logDir = projectDir & "\logs"

If Not fso.FolderExists(logDir) Then
    fso.CreateFolder(logDir)
End If

shell.CurrentDirectory = projectDir

quote = Chr(34)
command = "cmd /c " & quote & quote & projectDir & "\start-offline.cmd" & quote & _
          " >> " & quote & logDir & "\autostart.log" & quote & " 2>&1" & quote

' 0 = hidden window, False = do not wait for it to finish
shell.Run command, 0, False
