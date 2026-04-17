param(
    [string]$ServiceName = "BalancerX.Api",
    [string]$ProjectPath = ".\src\BalancerX.Api\BalancerX.Api.csproj",
    [string]$BasePath = "C:\apps\balancerx",
    [string]$Environment = "Production",
    [string]$Urls = "http://127.0.0.1:5000",
    [string]$Configuration = "Release"
)

$ErrorActionPreference = "Stop"

if (!(Test-Path $ProjectPath)) {
    throw "No existe el proyecto en ProjectPath: $ProjectPath"
}

$dotnetCmd = (Get-Command dotnet -ErrorAction Stop).Source
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$releasesPath = Join-Path $BasePath "releases"
$releasePath = Join-Path $releasesPath $timestamp
$currentPath = Join-Path $BasePath "current"
$logsPath = Join-Path $BasePath "logs"

New-Item -ItemType Directory -Force -Path $releasesPath | Out-Null
New-Item -ItemType Directory -Force -Path $logsPath | Out-Null

Write-Host "[1/6] Publicando release en $releasePath" -ForegroundColor Cyan
& $dotnetCmd publish $ProjectPath -c $Configuration -o $releasePath

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
    Remove-Item -Path $currentPath -Force
}
New-Item -ItemType Junction -Path $currentPath -Target $releasePath | Out-Null

$serviceDllPath = Join-Path $currentPath "BalancerX.Api.dll"
if (!(Test-Path $serviceDllPath)) {
    throw "No se encontró BalancerX.Api.dll en $serviceDllPath"
}

$binaryPath = '"{0}" "{1}"' -f $dotnetCmd, $serviceDllPath
Write-Host "[4/6] Actualizando binPath del servicio" -ForegroundColor Cyan
sc.exe config $ServiceName binPath= $binaryPath | Out-Null

Write-Host "[5/6] Actualizando variables de entorno del servicio" -ForegroundColor Cyan
reg add "HKLM\SYSTEM\CurrentControlSet\Services\$ServiceName" /v "Environment" /t REG_MULTI_SZ /d "ASPNETCORE_ENVIRONMENT=$Environment\0ASPNETCORE_URLS=$Urls" /f | Out-Null

Write-Host "[6/6] Iniciando servicio $ServiceName" -ForegroundColor Cyan
Start-Service -Name $ServiceName
(Get-Service -Name $ServiceName).WaitForStatus("Running", [TimeSpan]::FromSeconds(30))

Write-Host "Despliegue completado. Release activo: $releasePath" -ForegroundColor Green
