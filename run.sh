#!/bin/sh

if [ ! -d test ]; then
	mkdir test
fi

if ant -Ddebug=true jar; then
    (cd test; java -jar ../build/Chunky.jar)
fi

