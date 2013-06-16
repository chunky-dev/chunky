#!/bin/bash

# Run the benchmark with a number of different tile sizes to evaluate
# optimum tile size. Note that the optimum tile size depends on the actual
# canvas size and this benchmark only uses 400 by 400 canvas size.

interrupted()
{
	exit $?
}

trap interrupted SIGINT

JAVA_OPTS="-Xmx4g -Xms512m"

if ! (ant -f ../build.xml clean jar); then
	echo "failed to build chunky!"
	exit 1
fi

for ((i=1;i<=10;++i)); do
	echo "benchmark run $i/10"
	echo "writing output to log$$-1.dat"
	for tilewidth in 1 2 4 8 16 32 64 128 256; do
		echo -n "$tilewidth "
		java $JAVA_OPTS -jar ../build/Chunky.jar -benchmark -threads 8 -tile-width $tilewidth | \
			grep 'Benchmark completed' | cut -d ' ' -f 5
	done | tee log$$-$i.dat
done

