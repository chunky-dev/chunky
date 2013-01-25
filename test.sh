#!/bin/sh

#JAVA_OPTS=-XX:+UnlockDiagnosticVMOptions \
#    -XX:+PrintAssembly \
#    -XX:CompileCommand=print,Octree.intersect
#JAVA_OPTS=-XX:+PrintCompilation

if [ ! -d test ]; then
	mkdir test
fi

if ant clean && ant -Ddebug=true jar; then
    (cd test; java $JAVA_OPTS -jar ../build/Chunky.jar)
fi

