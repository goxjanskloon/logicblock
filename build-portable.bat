@echo off
cd /d %~dp0
mkdir build\portable
copy build\LogicBlocks.jar build\portable\
jlink --no-header-files --no-man-pages --add-modules java.base,java.desktop,java.logging --output build\portable\jre
copy src\run.bat.src build\portable\run.bat
