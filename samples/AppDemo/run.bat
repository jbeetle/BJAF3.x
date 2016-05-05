@echo off
set java_home=D:\Tools\jdk1.6.0_30
set jvm_option=-Xms512m -Xmx1024m -Xmn300m -XX:MaxPermSize=64m -XX:MaxDirectMemorySize=256m
SETLOCAL
set need_lib=.
set app_lib=bin
for %%c in (lib/*.jar) do call :appendClasspath lib\%%c
set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

%java_home%\bin\java %jvm_option% -cp %app_lib%%need_lib% demo.XXXApp.XXXMain %CMD_LINE_ARGS%
echo %ERRORLEVEL%
ENDLOCAL

goto :EOF

:appendClasspath
set need_lib=%need_lib%;%1
goto :EOF
