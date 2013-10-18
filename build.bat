@echo off
set MOD_SRC_DIR=%cd%
echo Starting build
echo Starting build on %date%, %time% > "%MOD_SRC_DIR%\build.log"
if "%MCP_HOME%" == "" goto mcpNotSet
echo MCP_HOME is %MCP_HOME%
echo MCP_HOME is %MCP_HOME% >>"%MOD_SRC_DIR%\build.log"

echo Copying sources to MCP
echo Copying sources to MCP 1>>"%MOD_SRC_DIR%\build.log"
rem 2>&1 means redirect stderr(2) to stdout(1)
xcopy src\hunternif /s /y "%MCP_HOME%\src\minecraft\hunternif\" 1>>"%MOD_SRC_DIR%\build.log" 2>&1

cd /d "%MCP_HOME%"
echo Recompiling...
runtime\bin\python\python_mcp runtime\recompile.py %* 1>>"%MOD_SRC_DIR%\build.log" 2>&1
echo Reobfuscating...
runtime\bin\python\python_mcp runtime\reobfuscate.py --srgnames %* 1>>"%MOD_SRC_DIR%\build.log" 2>&1

echo Removing sources from MCP
echo Removing sources from MCP 1>>"%MOD_SRC_DIR%\build.log"
rmdir /s /q "%MCP_HOME%\src\minecraft\hunternif" 1>>"%MOD_SRC_DIR%\build.log" 2>&1


echo Copying resource files to "reobf"
echo Copying resource files to "reobf" 1>>"%MOD_SRC_DIR%\build.log"
xcopy "%MOD_SRC_DIR%\src\assets" /s /y "%MCP_HOME%\reobf\minecraft\assets\" 1>>"%MOD_SRC_DIR%\build.log" 2>&1
xcopy "%MOD_SRC_DIR%\src\mcmod.info" /y "%MCP_HOME%\reobf\minecraft\" 1>>"%MOD_SRC_DIR%\build.log" 2>&1
xcopy "%MOD_SRC_DIR%\src\*.png" /y "%MCP_HOME%\reobf\minecraft\" 1>>"%MOD_SRC_DIR%\build.log" 2>&1

echo Creating archive
echo Creating archive 1>>"%MOD_SRC_DIR%\build.log"
cscript "%MOD_SRC_DIR%\zip.vbs" "%MCP_HOME%\reobf\minecraft" "%MOD_SRC_DIR%\mod.zip" 1>>"%MOD_SRC_DIR%\build.log" 2>&1
echo Archive created
echo Archive created 1>>"%MOD_SRC_DIR%\build.log"
goto end

:mcpNotSet
echo MCP_HOME is not set
goto end


:end
cd /d %MOD_SRC_DIR%
echo Build finished. 1>>"%MOD_SRC_DIR%\build.log"
echo Build finished. Saved log to "%MOD_SRC_DIR%\build.log"
pause