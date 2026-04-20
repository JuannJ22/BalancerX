param(
    [string]$ServiceName = "BalancerX.Api",
    [string]$ProjectPath = ".\src\BalancerX.Api\BalancerX.Api.csproj",
    [string]$BasePath = "C:\apps\balancerx",
    [string]$Environment = "Production",
    [string]$Urls = "http://0.0.0.0:5000",
    [string]$Configuration = "Release",
    [string]$Runtime = "win-x64",
    [string]$ServiceUser = "",
    [string]$ServicePassword = ""
)

$ErrorActionPreference = "Stop"

function Publish-InitialRelease {
    param(
        [string]$ProjectFilePath,
        [string]$RootBasePath,
        [string]$BuildConfiguration,
        [string]$BuildRuntime
    )

    if (!(Test-Path $ProjectFilePath)) {
        throw "No existe el proyecto en ProjectPath: $ProjectFilePath"
    }

    $dotnetCmd = (Get-Command dotnet -ErrorAction Stop).Source
    $timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
    $releasesPath = Join-Path $RootBasePath "releases"
    $releasePath = Join-Path $releasesPath $timestamp
    $currentPath = Join-Path $RootBasePath "current"
    $logsPath = Join-Path $RootBasePath "logs"

    New-Item -ItemType Directory -Force -Path $releasesPath | Out-Null
    New-Item -ItemType Directory -Force -Path $logsPath | Out-Null

    Write-Host "[Bootstrap 1/4] Publicando release inicial en $releasePath" -ForegroundColor Cyan
    & $dotnetCmd publish $ProjectFilePath -c $BuildConfiguration -r $BuildRuntime --self-contained true -o $releasePath | Out-Null

    Write-Host "[Bootstrap 2/4] Actualizando enlace current" -ForegroundColor Cyan
    if (Test-Path $currentPath) {
        Remove-Item -Path $currentPath -Force
    }
    New-Item -ItemType Junction -Path $currentPath -Target $releasePath | Out-Null

    $exePath = Join-Path $currentPath "BalancerX.Api.exe"
    if (!(Test-Path $exePath)) {
        throw "No se encontró BalancerX.Api.exe en $exePath"
    }

    Write-Host "[Bootstrap 3/4] Release inicial publicado y current actualizado." -ForegroundColor Cyan
    return $exePath
}

$installScriptPath = Join-Path $PSScriptRoot "install-service.ps1"
$deployScriptPath = Join-Path $PSScriptRoot "deploy-release.ps1"
$diagnoseScriptPath = Join-Path $PSScriptRoot "diagnose-connectivity.ps1"

if (!(Test-Path $installScriptPath)) { throw "No se encontró script: $installScriptPath" }
if (!(Test-Path $deployScriptPath)) { throw "No se encontró script: $deployScriptPath" }
if (!(Test-Path $diagnoseScriptPath)) { throw "No se encontró script: $diagnoseScriptPath" }

$existingService = Get-Service -Name $ServiceName -ErrorAction SilentlyContinue

if ($existingService) {
    Write-Host "Servicio existente detectado: $ServiceName. Ejecutando flujo de actualización (deploy-release)." -ForegroundColor Green
    & $deployScriptPath `
        -ServiceName $ServiceName `
        -ProjectPath $ProjectPath `
        -BasePath $BasePath `
        -Environment $Environment `
        -Urls $Urls `
        -Configuration $Configuration `
        -Runtime $Runtime
}
else {
    Write-Host "Servicio no existe. Ejecutando flujo completo de inicialización (publish + install-service)." -ForegroundColor Green
    $publishedExePath = Publish-InitialRelease `
        -ProjectFilePath $ProjectPath `
        -RootBasePath $BasePath `
        -BuildConfiguration $Configuration `
        -BuildRuntime $Runtime

    if ([string]::IsNullOrWhiteSpace($ServiceUser)) {
        & $installScriptPath `
            -ServiceName $ServiceName `
            -PublishedExePath $publishedExePath `
            -Environment $Environment `
            -Urls $Urls
    }
    else {
        & $installScriptPath `
            -ServiceName $ServiceName `
            -PublishedExePath $publishedExePath `
            -Environment $Environment `
            -Urls $Urls `
            -ServiceUser $ServiceUser `
            -ServicePassword $ServicePassword
    }
}

Write-Host "Ejecutando diagnóstico final de conectividad..." -ForegroundColor Cyan
& $diagnoseScriptPath -ServiceName $ServiceName -Urls $Urls

Write-Host "Flujo bootstrap-and-run finalizado correctamente." -ForegroundColor Green
