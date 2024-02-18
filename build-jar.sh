#!/bin/sh
cd $(dirname $0)
mkdir -p build/bin
javac src/BoardFrame.java -d build/bin -cp src
copy -r src/img build/img
jar --create --file build/LogicBlocks.jar --main-class BoardFrame -C build/bin .
