set PATH=%PATH%;\cygwin\bin
set scc=\cygwin\SollerOS\cross\bin\i586-pc-solleros-gcc
%scc% -s gravity.c -L SollerOS -lgui -o SollerOS/gravity.elf
%scc% -s raytrace.c -L SollerOS -lgui -o SollerOS/raytrace.elf
%scc% -s screensaver.c -L SollerOS -lgui -o SollerOS/screensaver.elf
gcc-4 -s gravity.c -L X11 -lgui -lX11 -o X11/gravity.exe
gcc-4 -s raytrace.c -L X11 -lgui -lX11 -o X11/raytrace.exe
gcc-4 -s screensaver.c -L X11 -lgui -lX11 -o X11/screensaver.exe