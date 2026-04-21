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

Assert-RunningAsAdministrator -OperationName "instalar y arrancar servicios de Windows"

$binaryPath = '"{0}"' -f $PublishedExePath

$serviceExists = Get-Service -Name $ServiceName -ErrorAction SilentlyContinue
if ($serviceExists) {
    throw "El servicio '$ServiceName' ya existe. Elimínalo o usa otro nombre."
}

Invoke-NativeCommandOrThrow `
    -Command { sc.exe create $ServiceName binPath= $binaryPath start= auto } `
    -FailureMessage "No se pudo crear el servicio '$ServiceName'"

Invoke-NativeCommandOrThrow `
    -Command { sc.exe failure $ServiceName reset= 86400 actions= restart/5000/restart/5000/restart/10000 } `
    -FailureMessage "No se pudieron configurar las políticas de recuperación del servicio '$ServiceName'"

Invoke-NativeCommandOrThrow `
    -Command { reg add "HKLM\SYSTEM\CurrentControlSet\Services\$ServiceName" /v Description /t REG_SZ /d "BalancerX API Service" /f } `
    -FailureMessage "No se pudo escribir la descripción del servicio '$ServiceName' en el registro"

Invoke-NativeCommandOrThrow `
    -Command { reg add "HKLM\SYSTEM\CurrentControlSet\Services\$ServiceName" /v DelayedAutoStart /t REG_DWORD /d 1 /f } `
    -FailureMessage "No se pudo habilitar DelayedAutoStart para el servicio '$ServiceName'"

if ($ServiceUser -ne "") {
    if ($ServicePassword -eq "") {
        throw "Si defines ServiceUser debes definir ServicePassword."
    }

    Invoke-NativeCommandOrThrow `
        -Command { sc.exe config $ServiceName obj= $ServiceUser password= $ServicePassword } `
        -FailureMessage "No se pudo configurar la cuenta '$ServiceUser' para el servicio '$ServiceName'"
}

Invoke-NativeCommandOrThrow `
    -Command { reg add "HKLM\SYSTEM\CurrentControlSet\Services\$ServiceName" /v "Environment" /t REG_MULTI_SZ /d "ASPNETCORE_ENVIRONMENT=$Environment\0ASPNETCORE_URLS=$Urls" /f } `
    -FailureMessage "No se pudieron guardar las variables de entorno del servicio '$ServiceName'"

Ensure-FirewallRulesForUrls -ServiceNameForRule $ServiceName -RawUrls $Urls

try {
    Start-Service -Name $ServiceName -ErrorAction Stop
    (Get-Service -Name $ServiceName).WaitForStatus("Running", [TimeSpan]::FromSeconds(30))
}
catch {
    Show-ServiceStartDiagnostics -TargetServiceName $ServiceName -ExecutablePath $PublishedExePath -RawUrls $Urls
    throw
}

Test-LocalEndpointAfterStart -RawUrls $Urls

Write-Host "Servicio '$ServiceName' instalado y ejecutándose." -ForegroundColor Green
