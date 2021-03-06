; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "WarnMarket"
#define MyAppVersion "1.0"
#define MyAppPublisher "Warn Market, Inc."
#define MyAppURL "https://www.example.com/"
#define MyAppExeName "warnmarket.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application. Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{5FB79E77-5C1A-4BAD-AF5C-9CC2E0C363B6}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
DisableProgramGroupPage=yes
; Uncomment the following line to run in non administrative install mode (install for current user only.)
;PrivilegesRequired=lowest
OutputDir=C:\Users\josem\Desktop
OutputBaseFilename=warnmarketSetup
SetupIconFile=D:\Estudios\FP\Segundo curso\ProyectoFinal\ProyectoIntegrado\instalador app\Archivos\icono\warnmarketicon.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "D:\Estudios\FP\Segundo curso\ProyectoFinal\ProyectoIntegrado\instalador app\Archivos\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\Estudios\FP\Segundo curso\ProyectoFinal\ProyectoIntegrado\instalador app\Archivos\dist\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "D:\Estudios\FP\Segundo curso\ProyectoFinal\ProyectoIntegrado\instalador app\Script\complementos\*"; DestDir: "{tmp}"; Flags: ignoreversion deleteafterinstall;

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{tmp}\java15.exe"; Parameters: "/s";  WorkingDir: {tmp}; StatusMsg: Instalando Java 15; Flags: runhidden;
Filename: "msiexec.exe" ;Parameters: "/i""{tmp}\mysql-5.5.41-winx64.msi"" /qn  INSTALLDIR=""C:\Program Files\mysql""";WorkingDir:{tmp}; StatusMsg: "Instalando Motor de Base de Datos"; Description: "Instalar Motor de Base de Datos"; Flags: runhidden; 
Filename: "C:\Program Files\mysql\bin\mysqld.exe"; Parameters: --install; WorkingDir: "C:\Program Files\mysql\bin"; StatusMsg: Instalando Servicio MySQL; Description: Instalando  MySQL Server ; Flags: runhidden;
Filename: "net.exe"; Parameters: start mysql; StatusMsg: Iniciando Servicio MySQL; Description: Iniciar Servicio MySQL; Flags: runhidden;

Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

