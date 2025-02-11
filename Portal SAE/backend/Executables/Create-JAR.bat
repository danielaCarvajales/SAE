@echo off
echo Limpiando y empaquetando el proyecto...
cd ..
mvn clean package

if %ERRORLEVEL% NEQ 0 (
    echo Error: La construcción con Maven falló.
    exit /b %ERRORLEVEL%
)

echo ¡Proyecto construido correctamente!
pause
