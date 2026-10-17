@echo off
chcp 65001 >nul
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
  echo ERRO: JDK nao localizado. Instale um JDK 8 ou mais novo, e configure o PATH.
  echo O VS Code sozinho nao executa projetos Java.
  pause
  exit /b 1
)
if not exist bin mkdir bin
echo Compilando o projeto...
javac -encoding UTF-8 -d bin src\*.java
if errorlevel 1 (
  echo Houve um erro de compilacao.
  pause
  exit /b 1
)
echo.
java -cp bin Main
pause
