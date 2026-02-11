$ErrorActionPreference = 'Stop'

Set-Location -Path $PSScriptRoot

if (-not (Test-Path out)) {
    New-Item -ItemType Directory out | Out-Null
}

$cp = 'out;lib/sqlite-jdbc-3.46.0.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar'

Get-ChildItem -Recurse -Path src/main/java -Filter *.java |
    ForEach-Object { $_.FullName -replace '\\', '/' } |
    Set-Content sources.txt

try {
    & javac -d out -cp $cp @sources.txt
    if ($LASTEXITCODE -ne 0) {
        throw 'Compilation failed.'
    }

    & java -cp $cp app.Main
    exit $LASTEXITCODE
}
finally {
    Remove-Item sources.txt -ErrorAction SilentlyContinue
}
