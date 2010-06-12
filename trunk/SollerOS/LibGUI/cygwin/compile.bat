set PATH=%PATH%;\cygwin\bin
gcc-4 -lX11 -c libgui.c -o libgui.o
ar rcs libgui.a libgui.o
rm libgui.o