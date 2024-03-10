#!/bin/sh
cd $(dirname $0)
mkdir -p build/bin
javac src/BoardFrame.java -d build/bin -cp src:lib/flatlaf-3.4.jar
cp -r src/img build/bin/
cd build/bin
jar -xf ../../lib/flatlaf-3.4.jar
rm -rf module-info.class
rm -rf META-INF
cd ../..
jar --create --file build/LogicBlocks.jar --main-class BoardFrame -C build/bin .
