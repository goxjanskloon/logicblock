@echo off
cd /d %~dp0
mkdir build
mkdir build\bin
javac src\BoardFrame.java -d build\bin -cp src;lib\flatlaf-3.4.jar
xcopy src\img build\bin\img\ /s/y
cd build/bin
jar -xf ../../lib/flatlaf-3.4.jar
del module-info.class /q
rd META-INF /s/q
cd ../..
jar --create --file build\LogicBlocks.jar --main-class BoardFrame -C build\bin .
