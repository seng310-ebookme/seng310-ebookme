[Files]
Source: {#DistDir}\*; DestDir: {app}; Flags: recursesubdirs; Components: base
Source: {#JreInstaller}; DestDir: {tmp}; DestName: java_setup.exe; Flags: deleteafterinstall; Components: java

[Components]
Name: base; Description: Program files; Flags: fixed; Types: custom compact full
Name: filters; Description: Import Filters; Types: custom full
Name: emulator; Description: Microemulator; Types: custom full
Name: samples; Description: Sample e-books; Types: custom full
Name: java; Description: Java Runtime Environment 6; Types: full

[Run]
Filename: {tmp}\java_setup.exe; StatusMsg: Installing Java Runtime Environment; Components: java

[Setup]
AppName={#MyAppName}
AppId={#MyAppId}
AppVerName={#MyAppName} {#MyAppVersion}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
LicenseFile=doc\license.txt
OutputBaseFilename={#MyAppName}_setup_{#MyAppVersion}_wjre
VersionInfoVersion={#MyAppVersionWin}
VersionInfoCompany=Josef Cacek
VersionInfoDescription=EBookME creates e-books for mobile devices (with Java ME support)
AppPublisher=Josef Cacek
AppSupportURL=http://ebookme.sourceforge.net/
AppVersion={#MyAppVersion}

[Icons]
Name: {group}\EBookME {#MyAppVersion}; Filename: {app}\EBookME.exe; Components: base; WorkingDir: {app}
Name: {group}\Microemulator; Filename: {app}\emulator.exe; Components: emulator; WorkingDir: {app}
Name: {group}\EBookME Guide; Filename: {app}\docs\EBookME.pdf; Components: 
Name: {group}\Holy Bible (english sample); Filename: {app}\emulator.exe; Components: samples; Parameters: samples\bible.jad; WorkingDir: {app}; IconIndex: 0
Name: {group}\Skolak Kaja Marik (czech sample); Filename: {app}\emulator.exe; Components: samples; Parameters: samples\kaja.jad; WorkingDir: {app}; IconIndex: 0
Name: {group}\Uninstall {#MyAppName}; Filename: {uninstallexe}; Components: 

[UninstallDelete]
Name: {%USERPROFILE}\.microemulator; Type: filesandordirs

[Code]
//********** Check if application is already installed
function MyAppInstalled: Boolean;
begin
  Result := RegKeyExists(HKEY_LOCAL_MACHINE,
	'SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\{#MyAppID}_is1');
end;

//********** If app already installed, uninstall it before setup.
function InitializeSetup(): Boolean;
var
  uninstaller: String;
  oldVersion: String;
  ErrorCode: Integer;
begin
  if not MyAppInstalled then begin
    Result := True;
    Exit;
  end;
  RegQueryStringValue(HKEY_LOCAL_MACHINE,
	'SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\{#MyAppID}_is1',
	'DisplayName', oldVersion);
  if (MsgBox(oldVersion + ' is already installed, it has to be uninstalled before installation. Continue?',
	  mbConfirmation, MB_YESNO) = IDNO) then begin
	Result := False;
	Exit;
  end;

  RegQueryStringValue(HKEY_LOCAL_MACHINE,
	'SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\{#MyAppID}_is1',
	'QuietUninstallString', uninstaller);
  Exec('>', uninstaller, '', SW_SHOW, ewWaitUntilTerminated, ErrorCode);
  if (ErrorCode <> 0) then begin
	MsgBox('Failed to uninstall previous version. . Please run {#MyAppName} uninstaller manually from Start menu or Control Panel and then run installer again.',
	 mbError, MB_OK );
	Result := False;
	Exit;
  end;

  Result := True;
end;
