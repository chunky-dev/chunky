#!/bin/bash

if [ $# -lt "1" ]; then
    echo "Usage: $0 <BLOCK ID[:METADATA]>"
    exit 1
fi

JAVA_OPTS="-Xmx12g -Xms512m"

if ant -Ddebug=true build; then
	if [ ! -d test ]; then
		mkdir test
	fi

	cd test
    java $JAVA_OPTS -cp ../bin:../lib/j99.jar:../lib/JOCL-0.1.7.jar:../lib/log4j-1.2.17.jar se.llbit.chunky.main.BlockTestRenderer $1
fi

