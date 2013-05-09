#!/bin/bash

set -e

interrupted()
{
    echo
    exit $?
}

trap interrupted SIGINT

JAVA_OPTS="-Xmx12g -Xms512m"
JAR="build/chunky.jar"
LIB="chunky/lib"
CLASSPATH="$JAR:$LIB/log4j-1.2.17.jar:$LIB/commons-math3-3.2.jar"
BLOCKS=( 17:0 17:1 17:2 17:3 162:0 162:1 37 38:0 38:1 38:2 38:3 38:4 38:5 38:6 38:7 38:8 )

if ant -Ddebug=true dist; then
    if [ ! -e "$JAR" ]; then
        echo "Could not find $JAR!"
        exit 1
    fi
    if [ ! -d "test" ]; then
        mkdir test
    fi

    for ID in "${BLOCKS[@]}"; do
        echo Block $ID
        java $JAVA_OPTS -cp $CLASSPATH se.llbit.chunky.main.BlockTestRenderer \
            $ID -o "test/block$ID.png" > /dev/null
    done
fi
