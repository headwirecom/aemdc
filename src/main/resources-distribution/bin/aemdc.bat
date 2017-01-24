@REM
@REM ----------------------------------------------------------------------------
@REM AEMDC Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM GIT_HOME  - location of a Git home dir
@REM
@REM Optional ENV vars
@REM AEMDC_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM AEMDC_BATCH_PAUSE - set to 'on' to wait for a key stroke before ending
@REM AEMDC_OPTS - parameters passed to the Java VM when running aemdc
@REM     e.g. to debug aemdc itself, use
@REM set AEMDC_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case AEMDC_BATCH_ECHO is 'on'
@echo off
@REM enable echoing my setting AEMDC_BATCH_ECHO to 'on'
@if "%AEMDC_BATCH_ECHO%" == "on"  echo %AEMDC_BATCH_ECHO%

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set HOME=%HOMEDRIVE%%HOMEPATH%)

@REM Execute a user defined script before this one
if exist "%HOME%\aemdcrc_pre.bat" call "%HOME%\aemdcrc_pre.bat"

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set AEMDC_HOME=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set AEMDC_HOME=%~dp0\..

:repoSetup

@REM ----------------------------------------------------------------------------
@REM --- Clone Placeholders example project from GitHub : START -----------------
@REM ----------------------------------------------------------------------------

IF exist "aemdc-config.properties" GOTO parseConfigFile
GOTO gitCloneEnd

:parseConfigFile
FOR /F "eol=# delims== tokens=1,*" %%a IN (aemdc-config.properties) DO (
    IF NOT "%%a"=="" IF NOT "%%b"=="" SET aemdc.%%a=%%b
    IF "%%a"=="SOURCE_FOLDER" GOTO checkPlaceHolderRepo
)
GOTO gitCloneEnd

:checkPlaceHolderRepo
::ECHO PlaceHoldersRepo=%aemdc.SOURCE_FOLDER%
IF exist "%aemdc.SOURCE_FOLDER%" GOTO gitCloneEnd

:: IF NOT exist "%aemdc.SOURCE_FOLDER%"
ECHO Placeholders folder "%aemdc.SOURCE_FOLDER%" doesn't exist.
SET /P gitClone="Would you like to clone an example placeholders project https://github.com/headwirecom/aemdc-files.git to "%aemdc.SOURCE_FOLDER%"? (y/n)"
IF "y"=="%gitClone%" GOTO gitCloneCommando
GOTO gitCloneEnd
    
:gitCloneCommando
if "%GITCMD%"=="" set GITCMD=git
%GITCMD% clone https://github.com/headwirecom/aemdc-files.git "%aemdc.SOURCE_FOLDER%"
SET /P gitCloneFinished="Press any key to continue..."

:gitCloneEnd
@REM ----------------------------------------------------------------------------
@REM --- Clone Placeholders example project from GitHub : END -------------------
@REM ----------------------------------------------------------------------------

if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=%AEMDC_HOME%\lib

:: set CLASSPATH="%REPO%"\aemdc-0.10.1-SNAPSHOT-jar-with-dependencies.jar
set CLASSPATH=""
set MAINJARPATH="%REPO%"\aemdc-0.10.1-SNAPSHOT-jar-with-dependencies.jar
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

:: %JAVACMD% %AEMDC_OPTS% -Xms500m -Xmx500m -XX:PermSize=128m -XX:-UseGCOverheadLimit -classpath %CLASSPATH_PREFIX%;%CLASSPATH% -Dapp.name="aemdc" -jar %MAINJARPATH% %CMD_LINE_ARGS%
%JAVACMD% %AEMDC_OPTS% -classpath %CLASSPATH_PREFIX%;%CLASSPATH% -Dapp.name="aemdc" -jar %MAINJARPATH% %CMD_LINE_ARGS%

if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=%ERRORLEVEL%

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@REM If error code is set to 1 then the endlocal was done already in :error.
if %ERROR_CODE% EQU 0 @endlocal


:postExec
if exist "%HOME%\aemdcrc_post.bat" call "%HOME%\aemdcrc_post.bat"
@REM pause the batch file if AEMDC_BATCH_PAUSE is set to 'on'
if "%AEMDC_BATCH_PAUSE%" == "on" pause


if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%
