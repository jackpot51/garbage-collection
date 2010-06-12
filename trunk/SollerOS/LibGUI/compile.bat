set PATH=%PATH%;\cygwin\bin
set scc=\cygwin\SollerOS\cross\bin\i586-pc-solleros-gcc
%scc% -s asteroids.c -L SollerOS -lgui -lm -o SollerOS/asteroids.elf
%scc% -s gravity.c -L SollerOS -lgui -o SollerOS/gravity.elf
%scc% -s raytrace.c -L SollerOS -lgui -o SollerOS/raytrace.elf
%scc% -s screensaver.c -L SollerOS -lgui -o SollerOS/screensaver.elf
gcc-4 -s asteroids.c -L cygwin -lgui -lX11 -lm -o cygwin/asteroids.exe
gcc-4 -s gravity.c -L cygwin -lgui -lX11 -o cygwin/gravity.exe
gcc-4 -s raytrace.c -L cygwin -lgui -lX11 -o cygwin/raytrace.exe
gcc-4 -s screensaver.c -L cygwin -lgui -lX11 -o cygwin/screensaver.exe