@echo off
java -jar Driver_Test.jar -A
pause
echo Deleting test output files...
cd D:\Users\Miguel Escalona\Github\NSCOM01\TFTP\Java\.executables\test_resources\files\outputs
del /Q *
echo Test output files deleted.
cd D:\Users\Miguel Escalona\Github\NSCOM01\TFTP\Java\.executables