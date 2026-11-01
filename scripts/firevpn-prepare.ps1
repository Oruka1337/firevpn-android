# FireVPN Android: prebuilt parts that are not stored in git (Windows PowerShell).
# The Xray core (libv2ray.aar) comes from 2dust/AndroidLibXrayLite, the tunnel libraries and geo data files
# come from the official v2rayNG release APK of the same version. Every download is checked by SHA-256.
# Usage: powershell -ExecutionPolicy Bypass -File scripts\firevpn-prepare.ps1
$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'
[Net.ServicePointManager]::SecurityProtocol = 'Tls12'

$root = Split-Path $PSScriptRoot -Parent
$app = Join-Path $root 'V2rayNG\app'
$tmp = Join-Path $root 'build-prebuilt'
New-Item -ItemType Directory -Force $tmp, (Join-Path $app 'libs') | Out-Null

function Get-Checked($url, $file, $sha256) {
    Invoke-WebRequest $url -OutFile $file
    $h = (Get-FileHash $file -Algorithm SHA256).Hash.ToLower()
    if ($h -ne $sha256) { throw "checksum mismatch for $url" }
}

# Xray core, the tag that the AndroidLibXrayLite submodule points to in v2rayNG 2.3.15
Get-Checked 'https://github.com/2dust/AndroidLibXrayLite/releases/download/v26.10.31/libv2ray.aar' `
    (Join-Path $app 'libs\libv2ray.aar') '2efdf731b1ed624eb1cb0d5a4d0aa95c0672e89f9702226d1ba2dc90308630b6'

# official v2rayNG 2.3.15 APK: hev-socks5-tunnel libraries (built for package com/v2ray/ang) and geo data
$apk = Join-Path $tmp 'v2rayNG_2.3.15_universal.apk'
Get-Checked 'https://github.com/2dust/v2rayNG/releases/download/2.3.15/v2rayNG_2.3.15_universal.apk' `
    $apk '561df11add49967981e4393db30344fea6ee76b72fca052db9a8114fb0ba9d7d'

Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [IO.Compression.ZipFile]::OpenRead($apk)
try {
    foreach ($e in $zip.Entries) {
        if ($e.FullName -match '^lib/([^/]+)/(libhev-socks5-tunnel\.so|libhevsockstun\.so)$') {
            $dir = Join-Path $app "libs\$($Matches[1])"
            New-Item -ItemType Directory -Force $dir | Out-Null
            [IO.Compression.ZipFileExtensions]::ExtractToFile($e, (Join-Path $dir $Matches[2]), $true)
        } elseif ($e.FullName -match '^assets/([^/]+\.dat)$') {
            [IO.Compression.ZipFileExtensions]::ExtractToFile($e, (Join-Path $app "src\main\assets\$($Matches[1])"), $true)
        }
    }
} finally { $zip.Dispose() }
Remove-Item $tmp -Recurse -Force
'prebuilt parts ready'
