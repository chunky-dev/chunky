#!/bin/bash

interrupted()
{
	echo
    exit $?
}

trap interrupted SIGINT

JAVA_OPTS="-Xmx12g -Xms512m"

if ant -Ddebug=true build; then
	if [ ! -d test ]; then
		mkdir test
	fi

	cd test

	for (( ID=1; ID <= 158; ID++ )); do
		
		echo Block $ID
    	java $JAVA_OPTS -cp ../bin:../lib/j99.jar:../lib/JOCL-0.1.7.jar:../lib/log4j-1.2.17.jar \
			se.llbit.chunky.main.BlockTestRenderer $ID -o block$ID.png > /dev/null
	done
fi
