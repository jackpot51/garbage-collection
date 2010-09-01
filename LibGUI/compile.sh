#!/bin/bash
os=`uname -s`
if [[ $os =~ CYGWIN ]]
then
os=`uname -o`
fi
echo "Compiling for $os."
bin=bin/$os
lib=lib/$os
mkdir -p $bin
gcc -c ${lib}/libgui.c -o ${bin}/libgui.o || exit 1
ar rcs ${bin}/libgui.a ${bin}/libgui.o || exit 2
rm ${bin}/libgui.o

flags="-L${bin} -lgui -lX11 -s -msse3"
cd src
src=`ls *.c`
cd ..
for i in $src
do
	prog=`echo $i | sed 's/\(.*\)\..*/\1/'`
	echo "Compiling $prog"
	gcc src/$i -o ${bin}/$prog $flags -lm || exit 3
done

echo "Finished."