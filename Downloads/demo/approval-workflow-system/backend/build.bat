@echo off
echo Building the Spring Boot Application...

cd src\main\java
javac -d ..\..\..\..\target\classes com\example\approval\ApprovalWorkflowApplication.java
cd ..\..\..\

echo Copying resources...
mkdir target\classes\resources
xcopy /E /I src\main\resources target\classes\resources

echo Build completed! 