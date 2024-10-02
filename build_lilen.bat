@echo off
cls

::set JAR_FILE=DataVisualizer.jar

::set JAVA=C:\Program Files\Java\jdk1.8.0_102
::set CYGWIN=C:\Programok\cygwin64

@call config.bat
@set PATH=%CYGWIN%\bin\;%PATH%

:: updating version info
%CYGWIN%\bin\bash.exe -i -c "./version.sh"

:: updating source list
del /f /Q javafiles
%CYGWIN%\bin\bash.exe -i -c "find src -iname '*.java' ! -name DataVisualizerLayoutFile_test.java >javafiles"
mkdir bin
del /f /q /s *.class
"%JAVA%\bin\javac" -Werror -d bin -cp lib/RXTXcomm/RXTXcomm.jar @javafiles
if %ERRORLEVEL%==0 goto link_step
echo ERROR!
goto end_pause

:link_step
cd bin
del /f /Q classfiles
%CYGWIN%\bin\bash.exe -i -c "find . -iname '*.class' >classfiles"

del /f /Q %JAR_FILE%
"%JAVA%\bin\jar" cmf ../manifest.mf %JAR_FILE% @classfiles
if %ERRORLEVEL%==0 goto copy_step
echo ERROR!
cd ..
goto end_pause

:copy_step
::del /f /q PATH\%JAR_FILE%
::copy %JAR_FILE% PATH
cd ..
goto end

:end_pause
::pause

:end
