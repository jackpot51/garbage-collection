set scc=\cygwin\SollerOS\cross\bin\i586-pc-solleros-gcc
%scc% -c libgui.c -o libgui.o
ar rcs libgui.a libgui.o
rm libgui.o