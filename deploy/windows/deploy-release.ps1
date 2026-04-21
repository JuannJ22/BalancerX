param(
    [string]$ServiceName = "BalancerX.Api",
    [string]$ProjectPath = ".\src\BalancerX.Api\BalancerX.Api.csproj",
    [string]$BasePath = "C:\apps\balancerx",
    [string]$Environment = "Production",
    [string]$Urls = "http://0.0.0.0:5000",
    [string]$Configuration = "Release",
    [string]$Runtime = "win-x64"
)

$ErrorActionPreference = "Stop"
$commonScriptPath = Join-Path $PSScriptRoot "lib\BalancerX.Deploy.Common.ps1"
. $commonScriptPath

if (!(Test-Path $ProjectPath)) {
    throw "No existe el proyecto en ProjectPath: $ProjectPath"
}

Assert-RunningAsAdministrator -OperationName "actualizar servicios de Windows"

$dotnetCmd = (Get-Command dotnet -ErrorAction Stop).Source
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$releasesPath = Join-Path $BasePath "releases"
$releasePath = Join-Path $releasesPath $timestamp
$currentPath = Join-Path $BasePath "current"
$logsPath = Join-Path $BasePath "logs"

New-Item -ItemType Directory -Force -Path $releasesPath | Out-Null
New-Item -ItemType Directory -Force -Path $logsPath | Out-Null

Write-Host "[1/6] Publicando release en $releasePath" -ForegroundColor Cyan
& $dotnetCmd publish $ProjectPath -c $Configuration -r $Runtime --self-contained true -o $releasePath

$service = Get-Service -Name $ServiceName -ErrorAction SilentlyContinue
if (-not $service) {
    throw "No existe el servicio '$ServiceName'. Instálalo primero con deploy/windows/install-service.ps1"
}

Write-Host "[2/6] Deteniendo servicio $ServiceName" -ForegroundColor Cyan
if ($service.Status -ne "Stopped") {
    Stop-Service -Name $ServiceName -Force
    $service.WaitForStatus("Stopped", [TimeSpan]::FromSeconds(30))
}

Write-Host "[3/6] Actualizando enlace current" -ForegroundColor Cyan
if (Test-Path $currentPath) {
    Remove-Item -Path $currentPath -Recurse -Force -ErrorAction Stop
}
New-Item -ItemType Junction -Path $currentPath -Target $releasePath | Out-Null

$serviceExePath = Join-Path $currentPath "BalancerX.Api.exe"
if (!(Test-Path $serviceExePath)) {
    throw "No se encontró BalancerX.Api.exe en $serviceExePath"
}

$binaryPath = '"{0}"' -f $serviceExePath
Write-Host "[4/6] Actualizando binPath del servicio" -ForegroundColor Cyan
Invoke-NativeCommandOrThrow `
    -Command { sc.exe config $ServiceName binPath= $binaryPath } `
    -FailureMessage "No se pudo actualizar binPath del servicio '$ServiceName'"

Write-Host "[5/6] Actualizando variables de entorno del servicio" -ForegroundColor Cyan
Invoke-NativeCommandOrThrow `
    -Command { reg add "HKLM\SYSTEM\CurrentControlSet\Services\$ServiceName" /v "Environment" /t REG_MULTI_SZ /d "ASPNETCORE_ENVIRONMENT=$Environment\0ASPNETCORE_URLS=$Urls" /f } `
    -FailureMessage "No se pudieron actualizar las variables de entorno del servicio '$ServiceName'"

Ensure-FirewallRulesForUrls -ServiceNameForRule $ServiceName -RawUrls $Urls

Write-Host "[6/6] Iniciando servicio $ServiceName" -ForegroundColor Cyan
try {
    Start-Service -Name $ServiceName -ErrorAction Stop
    (Get-Service -Name $ServiceName).WaitForStatus("Running", [TimeSpan]::FromSeconds(30))
}
catch {
    Show-ServiceStartDiagnostics -TargetServiceName $ServiceName -ExecutablePath $serviceExePath -RawUrls $Urls
    throw "No se pudo iniciar el servicio '$ServiceName' después del despliegue. Revisa el diagnóstico anterior."
}

Test-LocalEndpointAfterStart -RawUrls $Urls -ServiceNameForHint $ServiceName

Write-Host "Despliegue completado. Release activo: $releasePath" -ForegroundColor Green
