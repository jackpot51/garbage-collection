#!/bin/bash
olddir=$PWD
os=SollerOS
echo "Compiling for $os."
cd ../..
bin=bin/$os
lib=lib/$os
mkdir -p $bin
scc="/SollerOS/cross/bin/i586-pc-solleros-gcc"
$scc -c ${lib}/libgui.c -o ${bin}/libgui.o || exit 1
ar rcs ${bin}/libgui.a ${bin}/libgui.o || exit 2
rm ${bin}/libgui.o

scc="/SollerOS/cross/bin/i586-pc-solleros-gcc -L. -lgui"
cd src
src=`ls *.c`
cd ..
for i in $src
do
	prog=`echo $i | sed 's/\(.*\)\..*/\1/'`
	echo "Compiling $prog"
	gcc src/$i -o ${bin}/$prog $flags -lm || exit 3
done
cd $olddir
echo "Finished."


