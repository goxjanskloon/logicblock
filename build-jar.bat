@echo off
cd /d %~dp0
mkdir build
mkdir build\bin
javac src\BoardFrame.java -d build\bin -cp src
xcopy src\img build\bin\img\ /s/y
jar --create --file build\LogicBlocks.jar --main-class BoardFrame -C build\bin .
