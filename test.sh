#!/bin/sh

#JAVA_OPTS=-XX:+UnlockDiagnosticVMOptions \
#    -XX:+PrintAssembly \
#    -XX:CompileCommand=print,Octree.intersect
#JAVA_OPTS=-XX:+PrintCompilation

if ant clean && ant -Ddebug=true jar; then
    java $JAVA_OPTS -jar build/Chunky.jar
fi

