@echo off
java -jar Driver_Test.jar -A
pause
echo Deleting test output files...
cd test_resources\files\outputs
del /Q *
echo Test output files deleted.
cd ..
cd ..
cd ..