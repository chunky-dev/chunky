#!/bin/bash

interrupted()
{
    exit $?
}

trap interrupted SIGINT

JAVA_OPTS=-Xmx4g -Xms512m

if [ ! -d test ]; then
	mkdir test
fi

if ant clean && ant jar; then
	for tilewidth in 1 2 4 8 16 32 64 128 256; do
		echo -n "$tilewidth "
    	(cd test; java $JAVA_OPTS -jar ../build/Chunky.jar -benchmark -threads 8 -tile-width $tilewidth) | grep 'Benchmark completed' | cut -d ' ' -f 5
	done
fi


