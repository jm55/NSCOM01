@echo off
cls
echo Compile
echo ============================================

echo Compiling server_udp...
javac server_udp.java -Xlint

echo Compiling client_udp...
javac client_udp.java -Xlint

echo Compile complete!
pause
cls