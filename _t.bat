@cls
@set START=start "%CD%"

@call config.bat

@set PATH=lib\RXTXcomm;%PATH%

%START% "%JAVA%\bin\java" -jar bin\%JAR_FILE% %*
