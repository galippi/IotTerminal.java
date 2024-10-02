@PATH=%CD%\lib\RXTXcomm;%PATH%
::@set JAVA=C:\Program Files\Java\jre1.8.0_261
@call config.bat
"%JAVA%\bin\java" -jar bin\%JAR_FILE%
