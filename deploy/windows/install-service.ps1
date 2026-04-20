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

function Get-PrimaryHttpUrl {
    param([string]$RawUrls)

    if ([string]::IsNullOrWhiteSpace($RawUrls)) {
        return $null
    }

    $urls = $RawUrls.Split(';', [System.StringSplitOptions]::RemoveEmptyEntries)
    foreach ($urlText in $urls) {
        $trimmed = $urlText.Trim()
        if ([string]::IsNullOrWhiteSpace($trimmed)) {
            continue
        }

        $parsedUrl = $null
        if ([System.Uri]::TryCreate($trimmed, [System.UriKind]::Absolute, [ref]$parsedUrl)) {
            if ($parsedUrl.Scheme -eq 'http' -or $parsedUrl.Scheme -eq 'https') {
                return $parsedUrl
            }
        }
    }

    return $null
}

function Test-LocalEndpointAfterStart {
    param([string]$RawUrls)

    $primaryUrl = Get-PrimaryHttpUrl -RawUrls $RawUrls
    if (-not $primaryUrl) {
        Write-Warning "No se pudo determinar una URL HTTP/HTTPS válida desde -Urls: $RawUrls"
        return
    }

    $probeUrl = "{0}://127.0.0.1:{1}/login.html" -f $primaryUrl.Scheme, $primaryUrl.Port

    try {
        $response = Invoke-WebRequest -Uri $probeUrl -UseBasicParsing -TimeoutSec 8
        if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 400) {
            Write-Host "Sonda local exitosa en $probeUrl (HTTP $($response.StatusCode))." -ForegroundColor Green
            return
        }

        Write-Warning "La sonda local respondió con estado inesperado en $probeUrl (HTTP $($response.StatusCode))."
    }
    catch {
        Write-Warning "No se pudo validar la sonda local en $probeUrl. Error: $($_.Exception.Message)"
        Write-Host "Ejecuta diagnóstico: .\\deploy\\windows\\diagnose-connectivity.ps1 -ServiceName '$ServiceName' -Urls '$RawUrls'" -ForegroundColor Yellow
    }
}

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

Start-Service -Name $ServiceName -ErrorAction Stop
(Get-Service -Name $ServiceName).WaitForStatus("Running", [TimeSpan]::FromSeconds(30))
Test-LocalEndpointAfterStart -RawUrls $Urls

Write-Host "Servicio '$ServiceName' instalado y ejecutándose." -ForegroundColor Green
