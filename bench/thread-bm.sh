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
	echo "writing output to log$$-$i.dat"
	for threads in 1 2 3 4 5 6 7 8; do
		echo -n "$threads "
		java $JAVA_OPTS -jar ../build/Chunky.jar -benchmark -threads $threads -tile-width 16 | \
			grep 'Benchmark completed' | cut -d ' ' -f 5
	done | tee log$$-$i.dat
done

