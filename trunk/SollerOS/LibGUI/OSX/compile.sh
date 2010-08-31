gcc -c libgui.c -o libgui.o
ar rcs libgui.a libgui.o
rm libgui.o
cd ..
./compile-osx.sh