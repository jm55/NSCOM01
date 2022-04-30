@echo off
cls
echo Compile
echo ============================================

echo Compiling server_udp...
javac server_udp.java -verbose
pause

echo Compiling client_udp...
javac client_udp.java -verbose
pause

echo Compile complete!
pause