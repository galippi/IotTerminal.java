@cls
@set START=start "%CD%"

@call config.bat
%START% "%JAVA%\bin\java" -jar bin\%JAR_FILE% %*
