#!/bin/sh

echo "version=`git describe`" > src/main/res/Version.properties
cd win
cmd //c release.bat
