; NSIS script for Chunky
; Copyright (c) 2013, Jesper Öqvist <jesper@llbit.se>

Name "Chunky"
OutFile "Chunky.exe"

InstallDir $PROGRAMFILES\Chunky

; Registry key to save install directory
InstallDirRegKey HKLM "Software\Chunky" "Install_Dir"

; request admin privileges
RequestExecutionLevel admin

Page components
Page directory
Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

Section "Chunky (required)"

	SectionIn RO

	; Set destination directory
	SetOutPath $INSTDIR
	
	File ..\build\Chunky.jar
	File ..\README.txt
	File ..\ChangeLog.txt
	
	; Write install dir to registry
	WriteRegStr HKLM "Software\Chunky" "Install_Dir" "$INSTDIR"
	
	; Write Windows uninstall keys
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Chunky" "DisplayName" "Chunky"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Chunky" "UninstallString" '"$INSTDIR\uninstall.exe"'
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Chunky" "NoModify" 1
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Chunky" "NoRepair" 1
	WriteUninstaller "Uninstall.exe"

SectionEnd

Section "Start Menu Shortcuts"

	CreateDirectory "$SMPROGRAMS\Chunky"
	CreateShortCut "$SMPROGRAMS\Chunky\Chunky.lnk" "$INSTDIR\Chunky.jar"
	CreateShortCut "$SMPROGRAMS\Chunky\Uninstall.lnk" "$INSTDIR\Uninstall.exe"

SectionEnd

; Uninstaller

Section "Uninstall"
  
  ; Delete reg keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Chunky"
  DeleteRegKey HKLM SOFTWARE\Chunky

  ; Delete installed files
  Delete $INSTDIR\example2.nsi
  Delete $INSTDIR\Uninstall.exe

  ; Delete shortcuts
  Delete "$SMPROGRAMS\Chunky\*.*"

  ; Remove directories used
  RMDir "$SMPROGRAMS\Example2"
  RMDir "$INSTDIR"

SectionEnd
