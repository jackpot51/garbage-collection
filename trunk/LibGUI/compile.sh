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

flags="-L${bin} -lgui -lX11 -msse3 -I."
if [ $os == Darwin ]
then
#OSX does not have libX11 in the lib path
flags="$flags -L/usr/X11/lib"
else
#It also does not support the -s flag
flags="$flags -s"
fi 
for i in `ls src`
do
	if [ -n `ls src/$i/*.c` ]
	then
		echo "Compiling $i"
		gcc src/$i/$i.c -o ${bin}/$i $flags -lm || exit 3
	fi
done

echo "Finished."