param(
    [Parameter(Mandatory = $true)]
    [string]$ServiceName,

    [Parameter(Mandatory = $true)]
    [string]$PublishedExePath,

    [string]$Environment = "Production",
    [string]$Urls = "http://0.0.0.0:5000",
    [string]$ServiceUser = "",
    [string]$ServicePassword = ""
)

$ErrorActionPreference = "Stop"
$commonScriptPath = Join-Path $PSScriptRoot "lib\BalancerX.Deploy.Common.ps1"
. $commonScriptPath

if (!(Test-Path $PublishedExePath)) {
    throw "No existe PublishedExePath: $PublishedExePath"
}

$binaryPath = '"{0}"' -f $PublishedExePath

$serviceExists = Get-Service -Name $ServiceName -ErrorAction SilentlyContinue
if ($serviceExists) {
    throw "El servicio '$ServiceName' ya existe. Elimínalo o usa otro nombre."
}

sc.exe create $ServiceName binPath= $binaryPath start= auto | Out-Null

sc.exe failure $ServiceName reset= 86400 actions= restart/5000/restart/5000/restart/10000 | Out-Null

reg add "HKLM\SYSTEM\CurrentControlSet\Services\$ServiceName" /v Description /t REG_SZ /d "BalancerX API Service" /f | Out-Null
reg add "HKLM\SYSTEM\CurrentControlSet\Services\$ServiceName" /v DelayedAutoStart /t REG_DWORD /d 1 /f | Out-Null

if ($ServiceUser -ne "") {
    if ($ServicePassword -eq "") {
        throw "Si defines ServiceUser debes definir ServicePassword."
    }

    sc.exe config $ServiceName obj= $ServiceUser password= $ServicePassword | Out-Null
}

reg add "HKLM\SYSTEM\CurrentControlSet\Services\$ServiceName" /v "Environment" /t REG_MULTI_SZ /d "ASPNETCORE_ENVIRONMENT=$Environment\0ASPNETCORE_URLS=$Urls" /f | Out-Null
Ensure-FirewallRulesForUrls -ServiceNameForRule $ServiceName -RawUrls $Urls

Start-Service -Name $ServiceName
Test-LocalEndpointAfterStart -RawUrls $Urls -ServiceNameForHint $ServiceName

Write-Host "Servicio '$ServiceName' instalado y ejecutándose." -ForegroundColor Green
