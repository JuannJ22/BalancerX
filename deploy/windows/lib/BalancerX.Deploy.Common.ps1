Set-StrictMode -Version Latest

function Test-IsRunningAsAdministrator {
    $currentIdentity = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($currentIdentity)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

function Assert-RunningAsAdministrator {
    param([string]$OperationName = "operación de despliegue")

    if (Test-IsRunningAsAdministrator) {
        return
    }

    throw "Se requieren privilegios de administrador para $OperationName. Ejecuta PowerShell como Administrador y reintenta."
}

function Invoke-NativeCommandOrThrow {
    param(
        [Parameter(Mandatory = $true)]
        [scriptblock]$Command,

        [Parameter(Mandatory = $true)]
        [string]$FailureMessage
    )

    & $Command | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "$FailureMessage (exit code: $LASTEXITCODE)."
    }
}

function Get-HttpPortsFromUrls {
    param([string]$RawUrls)

    if ([string]::IsNullOrWhiteSpace($RawUrls)) {
        return @()
    }

    $ports = New-Object System.Collections.Generic.HashSet[int]
    $urls = $RawUrls.Split(';', [System.StringSplitOptions]::RemoveEmptyEntries)

    foreach ($urlText in $urls) {
        $trimmed = $urlText.Trim()
        if ([string]::IsNullOrWhiteSpace($trimmed)) {
            continue
        }

        $parsedUrl = $null
        if ([System.Uri]::TryCreate($trimmed, [System.UriKind]::Absolute, [ref]$parsedUrl)) {
            if ($parsedUrl.Scheme -eq 'http' -or $parsedUrl.Scheme -eq 'https') {
                $ports.Add($parsedUrl.Port) | Out-Null
            }
        }
    }

    return @($ports)
}

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

function Ensure-FirewallRulesForUrls {
    param(
        [string]$ServiceNameForRule,
        [string]$RawUrls
    )

    $ports = Get-HttpPortsFromUrls -RawUrls $RawUrls
    foreach ($port in $ports) {
        $ruleName = "$ServiceNameForRule TCP $port"
        $existingRule = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue
        if (-not $existingRule) {
            try {
                New-NetFirewallRule -DisplayName $ruleName `
                    -Direction Inbound `
                    -Profile Any `
                    -Action Allow `
                    -Protocol TCP `
                    -LocalPort $port `
                    -ErrorAction Stop | Out-Null

                Write-Host "Regla de firewall creada: $ruleName" -ForegroundColor Yellow
            }
            catch {
                throw "No se pudo crear la regla de firewall '$ruleName'. Error: $($_.Exception.Message)"
            }
        }
        else {
            Write-Host "Regla de firewall ya existente: $ruleName" -ForegroundColor DarkYellow
        }
    }
}

function Test-LocalEndpointAfterStart {
    param(
        [string]$RawUrls,
        [string]$ServiceNameForHint = "BalancerX.Api"
    )

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
        Write-Host "Ejecuta diagnóstico: .\\deploy\\windows\\diagnose-connectivity.ps1 -ServiceName '$ServiceNameForHint' -Urls '$RawUrls'" -ForegroundColor Yellow
    }
}

function Show-ServiceStartDiagnostics {
    param(
        [string]$TargetServiceName,
        [string]$ExecutablePath,
        [string]$RawUrls
    )

    Write-Host "--- Diagnóstico de arranque del servicio '$TargetServiceName' ---" -ForegroundColor Yellow

    try {
        $serviceCim = Get-CimInstance -ClassName Win32_Service -Filter "Name='$TargetServiceName'"
        if ($serviceCim) {
            Write-Host "Estado SCM: $($serviceCim.State) | StartName: $($serviceCim.StartName) | ExitCode: $($serviceCim.ExitCode) | ServiceSpecificExitCode: $($serviceCim.ServiceSpecificExitCode)"
        }
    }
    catch {
        Write-Warning "No se pudo leer Win32_Service para '$TargetServiceName'. Error: $($_.Exception.Message)"
    }

    if (Test-Path $ExecutablePath) {
        try {
            $acl = Get-Acl -Path $ExecutablePath
            Write-Host "ACL de ejecutable ($ExecutablePath):"
            $acl.Access |
                Select-Object IdentityReference, FileSystemRights, AccessControlType |
                Format-Table -AutoSize |
                Out-String |
                Write-Host
        }
        catch {
            Write-Warning "No se pudo inspeccionar ACL del ejecutable. Error: $($_.Exception.Message)"
        }
    }

    Write-Host "Pistas rápidas:" -ForegroundColor Yellow
    Write-Host "  1) Si StartName no es LocalSystem, valida derecho 'Iniciar sesión como servicio' para la cuenta." -ForegroundColor Yellow
    Write-Host "  2) Asegura permisos de lectura/ejecución sobre C:\apps\balancerx y releases para la cuenta del servicio." -ForegroundColor Yellow
    Write-Host "  3) Revisa eventos del sistema: Get-WinEvent -LogName System -MaxEvents 40 | ? ProviderName -eq 'Service Control Manager'" -ForegroundColor Yellow
    Write-Host "  4) Ejecuta diagnóstico de red: .\deploy\windows\diagnose-connectivity.ps1 -ServiceName '$TargetServiceName' -Urls '$RawUrls'" -ForegroundColor Yellow
}
