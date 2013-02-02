; NSIS script for Chunky
; Copyright (c) 2013, Jesper Öqvist <jesper@llbit.se>

; Use Modern UI
!include "MUI2.nsh"

Name "Chunky"
OutFile "Chunky.exe"

InstallDir $PROGRAMFILES\Chunky

; Registry key to save install directory
InstallDirRegKey HKLM "Software\Chunky" "Install_Dir"

; request admin privileges
RequestExecutionLevel admin

; Warn on abort
!define MUI_ABORTWARNING

; Pages
!insertmacro MUI_PAGE_LICENSE ../license/LICENSE.txt
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

; Language
!insertmacro MUI_LANGUAGE "English"

Section "Chunky (required)" SecChunky

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

Section "Start Menu Shortcuts" SecSM

	CreateDirectory "$SMPROGRAMS\Chunky"
	CreateShortCut "$SMPROGRAMS\Chunky\Chunky.lnk" "$INSTDIR\Chunky.jar"
	CreateShortCut "$SMPROGRAMS\Chunky\Uninstall.lnk" "$INSTDIR\Uninstall.exe"

SectionEnd

;Descriptions

	;Language strings
	LangString DESC_SecChunky ${LANG_ENGLISH} "Installs Chunky"
	LangString DESC_SecSM ${LANG_ENGLISH} "Adds shortcuts to your start menu"

	;Assign language strings to sections
	!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
		!insertmacro MUI_DESCRIPTION_TEXT ${SecChunky} $(DESC_SecChunky)
		!insertmacro MUI_DESCRIPTION_TEXT ${SecSM} $(DESC_SecSM)
	!insertmacro MUI_FUNCTION_DESCRIPTION_END

; Uninstaller

Section "Uninstall"
  
  ; Delete reg keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Chunky"
  DeleteRegKey HKLM SOFTWARE\Chunky

  ; Delete installed files
  Delete $INSTDIR\Chunky.jar
  Delete $INSTDIR\README.txt
  Delete $INSTDIR\LICENSE.txt
  Delete $INSTDIR\Uninstall.exe

  ; Delete shortcuts
  Delete "$SMPROGRAMS\Chunky\*.*"

  ; Remove directories used
  RMDir "$SMPROGRAMS\Chunky"
  RMDir "$INSTDIR"

SectionEnd
