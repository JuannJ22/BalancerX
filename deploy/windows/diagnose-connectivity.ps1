param(
    [string]$ServiceName = "BalancerX.Api",
    [string]$Urls = "http://0.0.0.0:5000"
)

$ErrorActionPreference = "Stop"
$commonScriptPath = Join-Path $PSScriptRoot "lib\BalancerX.Deploy.Common.ps1"
. $commonScriptPath

function Show-Section {
    param([string]$Title)
    Write-Host ""
    Write-Host "=== $Title ===" -ForegroundColor Cyan
}

Write-Host "Diagnóstico de conectividad BalancerX" -ForegroundColor Green
Write-Host "Servicio: $ServiceName"
Write-Host "URLs: $Urls"

Show-Section "Estado del servicio"
$service = Get-Service -Name $ServiceName -ErrorAction SilentlyContinue
if (-not $service) {
    Write-Host "Servicio no encontrado: $ServiceName" -ForegroundColor Red
}
else {
    $service | Format-Table Name, Status, StartType -AutoSize
}

Show-Section "Variables de entorno del servicio"
$regPath = "HKLM:\SYSTEM\CurrentControlSet\Services\$ServiceName"
if (Test-Path $regPath) {
    try {
        $envValue = (Get-ItemProperty -Path $regPath -Name Environment -ErrorAction Stop).Environment
        Write-Host ($envValue -join "`n")
    }
    catch {
        Write-Host "No se encontró valor 'Environment' en el servicio." -ForegroundColor Yellow
    }
}

$ports = Get-HttpPortsFromUrls -RawUrls $Urls
if ($ports.Count -eq 0) {
    Show-Section "Puertos"
    Write-Host "No se pudieron extraer puertos desde -Urls." -ForegroundColor Yellow
    exit 0
}

foreach ($port in $ports) {
    Show-Section "Puerto $port en escucha"
    netstat -ano | findstr ":$port"

    Show-Section "Sonda local en 127.0.0.1:$port"
    try {
        $response = Invoke-WebRequest -Uri "http://127.0.0.1:$port/login.html" -UseBasicParsing -TimeoutSec 8
        Write-Host "HTTP $($response.StatusCode) en /login.html" -ForegroundColor Green
    }
    catch {
        Write-Host "Fallo sonda local: $($_.Exception.Message)" -ForegroundColor Red
    }

    Show-Section "Regla de firewall esperada"
    $ruleName = "$ServiceName TCP $port"
    $rule = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue
    if ($rule) {
        $rule | Format-Table DisplayName, Enabled, Direction, Action -AutoSize
    }
    else {
        Write-Host "No existe regla de firewall: $ruleName" -ForegroundColor Yellow
    }
}

Show-Section "Próximo paso recomendado"
Write-Host "1) Si la sonda local falla, revisar logs de aplicación y conexión a SQL."
Write-Host "2) Si la sonda local pasa y desde red externa falla, revisar firewall de Windows/red."
Write-Host "3) Validar que el navegador abra: http://<IP_DEL_SERVIDOR>:<PUERTO>/login.html"
