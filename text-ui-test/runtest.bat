@echo off
setlocal enableextensions
pushd %~dp0

cd ..
call gradlew clean shadowJar

cd build\libs
for /f "tokens=*" %%a in (
    'dir /b *.jar'
) do (
    set jarloc=%%a
)

java -jar %jarloc% < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL.TXT

cd ..\..\text-ui-test

rem Resolve today's date in yyyy-MM-dd format
for /f "tokens=1-3 delims=-" %%a in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd"') do (
    set TODAY=%%a-%%b-%%c
)

rem Replace {TODAY} placeholder with today's date in a temp expected file
powershell -NoProfile -Command ^
    "(Get-Content EXPECTED.TXT) -replace '\{TODAY\}', '%TODAY%' | Set-Content EXPECTED-RESOLVED.TXT"

FC ACTUAL.TXT EXPECTED-RESOLVED.TXT >NUL && ECHO Test passed! || ECHO Test failed!