[Setup]
AppName=AgentVideoLAN
AppVersion=25.6.1
DefaultDirName={autopf}\AgentVideoLAN
DefaultGroupName=AgentVideoLAN
OutputBaseFilename=AgentVideoLAN
Compression=lzma
SolidCompression=yes
DisableStartupPrompt=true
DisableWelcomePage=true
DisableFinishedPage=true
DisableDirPage=true
DisableProgramGroupPage=true
Uninstallable=true

[Files]
Source: "C:\Program Files\VideoLAN\VLC\*"; DestDir: "{app}"; Flags: recursesubdirs ignoreversion

[Tasks]
Name: "desktopicon"; Description: "Tạo icon ngoài Desktop"; GroupDescription: "Tùy chọn bổ sung"

[Icons]
Name: "{group}\VLC media player"; Filename: "{app}\vlc.exe"
Name: "{commondesktop}\VLC media player"; Filename: "{app}\vlc.exe"; Tasks: desktopicon

[Run]
Filename: "{app}\vlc.exe"; Description: "Mở VLC"; Flags: nowait postinstall skipifsilent
