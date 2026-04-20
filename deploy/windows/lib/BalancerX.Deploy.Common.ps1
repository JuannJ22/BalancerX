Set-StrictMode -Version Latest

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
            New-NetFirewallRule -DisplayName $ruleName `
                -Direction Inbound `
                -Profile Any `
                -Action Allow `
                -Protocol TCP `
                -LocalPort $port | Out-Null
            Write-Host "Regla de firewall creada: $ruleName" -ForegroundColor Yellow
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
